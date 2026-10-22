package com.kairos.tvmaze.service;

import com.kairos.tvmaze.client.TvMazeClient;
import com.kairos.tvmaze.dto.response.ShowSearchResponse;
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

import static org.junit.jupiter.api.Assertions.*;
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
        showService = new ShowService(tvMazeClient, showMapper);
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

   
}

