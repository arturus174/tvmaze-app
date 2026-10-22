package com.kairos.tvmaze.service;

import com.kairos.tvmaze.client.TvMazeClient;
import com.kairos.tvmaze.dto.response.ShowDetailResponse;
import com.kairos.tvmaze.dto.response.ShowSearchResponse;
import com.kairos.tvmaze.model.domain.TvMazeSearchResult;
import com.kairos.tvmaze.model.domain.TvMazeShow;
import com.kairos.tvmaze.model.entity.Comment;
import com.kairos.tvmaze.model.entity.ShowCache;
import com.kairos.tvmaze.repository.CommentRepository;
import com.kairos.tvmaze.repository.ShowCacheRepository;
import com.kairos.tvmaze.service.mapper.ShowMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Clase de pruebas unitarias para ShowService.
 */
@ExtendWith(MockitoExtension.class)
class ShowServiceTest {

    /**
     * Mock del cliente HTTP de TV Maze.
     */
    @Mock
    private TvMazeClient tvMazeClient;

    /**
     * Simula operaciones de MongoDB para el caché.
     */
    @Mock
    private ShowCacheRepository showCacheRepository;

    /**
     * Simula operaciones de MongoDB para los comentarios.
     */
    @Mock
    private CommentRepository commentRepository;

    /**
     * Instancia real del mapper 
     */
    private ShowMapper showMapper;

    @InjectMocks
    private ShowService showService;


    @BeforeEach
    void setUp() {
        showMapper = new ShowMapper();
        // Re-inicializar el servicio con el mapper real
        showService = new ShowService(tvMazeClient, showMapper, showCacheRepository, commentRepository);
    }

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

        // Crear comentarios de prueba
        Comment comment1 = Comment.builder()
                .showId(1)
                .comment("Amazing series!")
                .rating(5)
                .createdAt(LocalDateTime.now())
                .build();

        Comment comment2 = Comment.builder()
                .showId(2)
                .comment("Good but slow start")
                .rating(4)
                .createdAt(LocalDateTime.now())
                .build();

        // Configurar los mocks
        when(tvMazeClient.searchShows(query)).thenReturn(Mono.just(searchResults));
        when(commentRepository.findByShowIdOrderByCreatedAtDesc(1)).thenReturn(List.of(comment1));
        when(commentRepository.findByShowIdOrderByCreatedAtDesc(2)).thenReturn(List.of(comment2));

        // Act: Ejecutar el método bajo prueba
        List<ShowSearchResponse> results = showService.searchShows(query);

        // Assert: Validar los resultados
        assertNotNull(results);
        assertEquals(2, results.size());

        // Validar primer show
        assertEquals("Breaking Bad", results.get(0).getName());
        assertEquals(1, results.get(0).getComments().size());
        assertEquals("Amazing series!", results.get(0).getComments().get(0).getComment());

        // Validar segundo show
        assertEquals("Better Call Saul", results.get(1).getName());
        assertEquals(1, results.get(1).getComments().size());
        assertEquals("Good but slow start", results.get(1).getComments().get(0).getComment());

        // Validar interacciones con los mocks
        verify(tvMazeClient, times(1)).searchShows(query);
        verify(commentRepository, times(1)).findByShowIdOrderByCreatedAtDesc(1);
        verify(commentRepository, times(1)).findByShowIdOrderByCreatedAtDesc(2);
    }

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

        // Validar que NO se consultaron comentarios (optimización)
        verify(commentRepository, never()).findByShowIdOrderByCreatedAtDesc(anyInt());
    }

    @Test
    @DisplayName("Obtener show existente en caché debe retornar desde MongoDB sin llamar API")
    void getShowById_whenExistsInCache_shouldReturnFromCacheWithoutCallingApi() {
        // Arrange
        Integer showId = 1;

        ShowCache cachedShow = ShowCache.builder()
                .showId(showId)
                .name("Breaking Bad")
                .summary("A chemistry teacher")
                .language("English")
                .genres(List.of("Drama"))
                .status("Ended")
                .runtime(60)
                .premiered("2008-01-20")
                .imageUrl("https://example.com/image.jpg")
                .channelName("AMC")
                .channelCountry("United States")
                .cachedAt(LocalDateTime.now().minusDays(1))
                .build();

        Comment comment = Comment.builder()
                .showId(showId)
                .comment("Masterpiece")
                .rating(5)
                .createdAt(LocalDateTime.now())
                .build();

        when(showCacheRepository.findByShowId(showId)).thenReturn(Optional.of(cachedShow));
        when(commentRepository.findByShowIdOrderByCreatedAtDesc(showId)).thenReturn(List.of(comment));

        // Act
        Optional<ShowDetailResponse> result = showService.getShowById(showId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Breaking Bad", result.get().getName());
        assertEquals(1, result.get().getComments().size());
        assertEquals("Masterpiece", result.get().getComments().get(0).getComment());

        // Validar que NO se llamó a la API externa
        verify(tvMazeClient, never()).getShowById(anyInt());
        verify(showCacheRepository, times(1)).findByShowId(showId);
        verify(showCacheRepository, never()).save(any());
    }


    @Test
    @DisplayName("Obtener show no existente en caché debe llamar API y guardar en MongoDB")
    void getShowById_whenNotInCache_shouldCallApiAndSaveToCache() {
        // Arrange
        Integer showId = 1;

        TvMazeShow showFromApi = TvMazeShow.builder()
                .id(showId)
                .name("Breaking Bad")
                .url("https://www.tvmaze.com/shows/1/breaking-bad")
                .summary("A chemistry teacher")
                .language("English")
                .genres(List.of("Drama"))
                .status("Ended")
                .runtime(60)
                .premiered("2008-01-20")
                .build();

        when(showCacheRepository.findByShowId(showId)).thenReturn(Optional.empty());
        when(tvMazeClient.getShowById(showId)).thenReturn(Mono.just(showFromApi));
        when(commentRepository.findByShowIdOrderByCreatedAtDesc(showId)).thenReturn(Collections.emptyList());
        when(showCacheRepository.save(any(ShowCache.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<ShowDetailResponse> result = showService.getShowById(showId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Breaking Bad", result.get().getName());
        assertTrue(result.get().getComments().isEmpty());

        // Validar que se llamó a la API y se guardó en caché
        verify(tvMazeClient, times(1)).getShowById(showId);
        verify(showCacheRepository, times(1)).save(any(ShowCache.class));
    }

    @Test
    @DisplayName("Obtener show inexistente en TV Maze debe retornar Optional.empty()")
    void getShowById_whenNotFoundInApi_shouldReturnEmpty() {
        // Arrange
        Integer showId = 999999;

        when(showCacheRepository.findByShowId(showId)).thenReturn(Optional.empty());
        when(tvMazeClient.getShowById(showId)).thenReturn(Mono.empty());

        // Act
        Optional<ShowDetailResponse> result = showService.getShowById(showId);

        // Assert
        assertTrue(result.isEmpty());

        // Validar que NO se guardó en caché
        verify(showCacheRepository, never()).save(any());
    }
}

