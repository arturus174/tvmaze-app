package com.kairos.tvmaze.service;

import com.kairos.tvmaze.client.TvMazeClient;
import com.kairos.tvmaze.dto.response.ShowDetailResponse;
import com.kairos.tvmaze.dto.response.ShowSearchResponse;
import com.kairos.tvmaze.model.entity.Comment;
import com.kairos.tvmaze.model.entity.ShowCache;
import com.kairos.tvmaze.repository.CommentRepository;
import com.kairos.tvmaze.repository.ShowCacheRepository;
import com.kairos.tvmaze.model.domain.TvMazeSearchResult;
import com.kairos.tvmaze.model.domain.TvMazeShow;
import com.kairos.tvmaze.service.mapper.ShowMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Clase de pruebas unitarias para ShowService.
 *
 * Utiliza simular las dependencias externas:
 * - TvMazeClient: evita llamadas reales a la API de TV Maze.
 */
@ExtendWith(MockitoExtension.class)
class ShowServiceTest {

    @Mock
    private TvMazeClient tvMazeClient;

    // se agrega para simular prueba del cache.
    @Mock
    private ShowCacheRepository showCacheRepository;

    // Se agrega para prueba de los comentarios.
    @Mock
    private CommentRepository commentRepository;

    /**
     * sin dependencias externas.
     * Se inyecta manualmente en el método @BeforeEach.
     */
    private ShowMapper showMapper;

    @InjectMocks
    private ShowService showService;


    @BeforeEach
    void setUp() {
        showMapper = new ShowMapper();
        // Re-inicializar el servicio con el mapper real
        showService = new ShowService(tvMazeClient, showMapper, showCacheRepository, commentRepository  );
    }

    /**
     * Prueba el caso de búsqueda exitosa con resultados.
     *
     * Escenario:
     * El cliente HTTP devuelve 2 shows.
     *
     * Validaciones:
     * - El tamaño de la lista de resultados es correcto.
     */
    @Test
    @DisplayName("Buscar shows con query válido debe retornar resultados con comentarios")
    void searchShows_withValidQuery_shouldReturnResultsWithComments() {
        // Arrange: Preparar los datos de prueba
        String query = "breaking";

        // Crear shows de prueba (simulando respuesta de TV Maze)
        TvMazeShow show1 = TvMazeShow.builder()
                .id(1)
                .name("Breaking Bad")
                .summary("A chemistry teacher turned meth manufacturer")
                .genres(List.of("Drama", "Crime"))
                .build();

        TvMazeShow show2 = TvMazeShow.builder()
                .id(2)
                .name("Better Call Saul")
                .summary("A lawyer's journey")
                .genres(List.of("Drama"))
                .build();

        List<TvMazeSearchResult> searchResults = List.of(
                TvMazeSearchResult.builder().score(0.9).show(show1).build(),
                TvMazeSearchResult.builder().score(0.8).show(show2).build()
        );


        // Configurar los mocks
        when(tvMazeClient.searchShows(query)).thenReturn(Mono.just(searchResults));

        // Act: Ejecutar el método bajo prueba
        List<ShowSearchResponse> results = showService.searchShows(query);

        // Assert: Validar los resultados
        assertNotNull(results);
        assertEquals(2, results.size());

        // Validar primer show
        assertEquals("Breaking Bad", results.get(0).getName());


        // Validar segundo show
        assertEquals("Better Call Saul", results.get(1).getName());


        // Validar interacciones con los mocks
        verify(tvMazeClient, times(1)).searchShows(query);

    }

    /**
     * Prueba el caso de búsqueda sin resultados.
     *
     * Escenario:
     * El cliente HTTP devuelve una lista vacía.
     *
     * Validaciones:
     * - La lista de resultados está vacía.
     */
    @Test
    @DisplayName("Buscar shows sin resultados debe retornar lista vacía")
    void searchShows_withNoResults_shouldReturnEmptyList() {
        // Arrange
        String query = "nonexistent";
        when(tvMazeClient.searchShows(query)).thenReturn(Mono.just(Collections.emptyList()));

        // Act
        List<ShowSearchResponse> results = showService.searchShows(query);

        // Assert
        assertNotNull(results);
        assertTrue(results.isEmpty());

    }

    @Test
    @DisplayName("B- Endpoint show: Debe retornar el show completo cuando la API lo encuentra")
    void getShowById_whenFound_shouldReturnDetailResponse() {
        // Arrange
        Integer showId = 1;
        TvMazeShow apiShow = TvMazeShow.builder()
                .id(showId)
                .name("Breaking Bad")
                .language("English")
                .genres(List.of("Drama"))
                .build();

        when(tvMazeClient.getShowById(showId)).thenReturn(Mono.just(apiShow));

        // Act
        Optional<ShowDetailResponse> result = showService.getShowById(showId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Breaking Bad", result.get().getName());
        assertEquals("English", result.get().getLanguage());
        verify(tvMazeClient, times(1)).getShowById(showId);
    }

    @Test
    @DisplayName("B- Endpoint show: Debe retornar Optional.empty() cuando la API no encuentra el show")
    void getShowById_whenNotFound_shouldReturnEmpty() {
        // Arrange
        Integer showId = 999;
        when(tvMazeClient.getShowById(showId)).thenReturn(Mono.empty());

        // Act
        Optional<ShowDetailResponse> result = showService.getShowById(showId);

        // Assert
        assertTrue(result.isEmpty());
        verify(tvMazeClient, times(1)).getShowById(showId);
    }

    @Test
    @DisplayName("B- Endpoint show (Caché): Debe retornar desde MongoDB sin llamar a la API si ya existe")
    void getShowById_whenExistsInCache_shouldReturnFromCacheWithoutApiCall() {
        // Arrange
        Integer showId = 1;
        ShowCache cachedShow = ShowCache.builder()
                .showId(showId)
                .name("Breaking Bad")
                .language("English")
                .cachedAt(LocalDateTime.now().minusDays(1))
                .build();

        when(showCacheRepository.findByShowId(showId)).thenReturn(Optional.of(cachedShow));

        // Act
        Optional<ShowDetailResponse> result = showService.getShowById(showId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Breaking Bad", result.get().getName());
        
        // Validaciones CRÍTICAS:
        verify(showCacheRepository, times(1)).findByShowId(showId);
        verify(tvMazeClient, never()).getShowById(anyInt()); // ¡No debe llamar a la API!
        verify(showCacheRepository, never()).save(any());    // ¡No debe guardar de nuevo!
    }

    @Test
    @DisplayName("B- Endpoint show (Caché): Debe llamar a la API y guardar en Mongo si no existe en caché")
    void getShowById_whenNotInCache_shouldCallApiAndSaveToMongo() {
        // Arrange
        Integer showId = 99;
        TvMazeShow apiShow = TvMazeShow.builder()
                .id(showId)
                .name("New Show")
                .language("Spanish")
                .build();

        when(showCacheRepository.findByShowId(showId)).thenReturn(Optional.empty());
        when(tvMazeClient.getShowById(showId)).thenReturn(Mono.just(apiShow));
        when(showCacheRepository.save(any(ShowCache.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<ShowDetailResponse> result = showService.getShowById(showId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("New Show", result.get().getName());
        
        // Validaciones CRÍTICAS:
        verify(showCacheRepository, times(1)).findByShowId(showId);
        verify(tvMazeClient, times(1)).getShowById(showId); // ¡Sí debe llamar a la API!
        verify(showCacheRepository, times(1)).save(any(ShowCache.class)); // ¡Debe guardar en Mongo!
    }

    /*
     * Prueba de agregar comentarios a la BD
     */
    @Test
    @DisplayName("A- searchShows: Debe enriquecer cada show con sus comentarios de MongoDB")
    void searchShows_withResults_shouldEnrichWithComments() {
        // Arrange
        String query = "breaking";

        TvMazeShow show1 = TvMazeShow.builder()
                .id(1)
                .name("Breaking Bad")
                .summary("A chemistry teacher")
                .genres(List.of("Drama"))
                .build();

        List<TvMazeSearchResult> searchResults = List.of(
                TvMazeSearchResult.builder().score(0.9).show(show1).build()
        );

        Comment comment1 = Comment.builder()
                .showId(1)
                .comment("Amazing series!")
                .rating(5)
                .createdAt(LocalDateTime.now())
                .build();

        when(tvMazeClient.searchShows(query)).thenReturn(Mono.just(searchResults));
        when(commentRepository.findByShowIdOrderByCreatedAtDesc(1)).thenReturn(List.of(comment1));

        // Act
        List<ShowSearchResponse> results = showService.searchShows(query);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Breaking Bad", results.get(0).getName());
        
        // Validación clave: los comentarios fueron agregados
        assertEquals(1, results.get(0).getComments().size());
        assertEquals("Amazing series!", results.get(0).getComments().get(0).getComment());
        assertEquals(5, results.get(0).getComments().get(0).getRating());

        verify(tvMazeClient, times(1)).searchShows(query);
        verify(commentRepository, times(1)).findByShowIdOrderByCreatedAtDesc(1);
    }

    @Test
    @DisplayName("A- searchShows: Si un show no tiene comentarios, debe retornar arreglo vacío de comments")
    void searchShows_withShowWithoutComments_shouldReturnEmptyCommentsList() {
        // Arrange
        String query = "test";
        TvMazeShow show = TvMazeShow.builder().id(2).name("Test Show").build();
        List<TvMazeSearchResult> searchResults = List.of(
                TvMazeSearchResult.builder().score(0.8).show(show).build()
        );

        when(tvMazeClient.searchShows(query)).thenReturn(Mono.just(searchResults));
        when(commentRepository.findByShowIdOrderByCreatedAtDesc(2)).thenReturn(Collections.emptyList());

        // Act
        List<ShowSearchResponse> results = showService.searchShows(query);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertNotNull(results.get(0).getComments());
        assertTrue(results.get(0).getComments().isEmpty());
    }

    @Test
    @DisplayName("A- searchShows: Sin resultados de API debe retornar lista vacía sin consultar comentarios")
    void searchShows_withNoApiResults_shouldReturnEmptyList() {
        // Arrange
        when(tvMazeClient.searchShows("xyz")).thenReturn(Mono.just(Collections.emptyList()));

        // Act
        List<ShowSearchResponse> results = showService.searchShows("xyz");

        // Assert
        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(commentRepository, never()).findByShowIdOrderByCreatedAtDesc(anyInt());
    }

}

