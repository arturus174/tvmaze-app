package com.kairos.tvmaze.client;

import com.kairos.tvmaze.model.domain.TvMazeSearchResult;
import com.kairos.tvmaze.model.domain.TvMazeShow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Cliente HTTP encargado de consumir la API pública de TV Maze.
 * Centraliza todas las llamadas externas para facilitar su mantenimiento,
 * manejo de errores y pruebas unitarias (mockeable con Mockito).
 *
 * Utiliza WebClient (no bloqueante) para realizar peticiones GET
 * a los endpoints de búsqueda y detalle de shows.
 */
@Slf4j
@Component
public class TvMazeClient {

    /** Cliente HTTP inyectado desde WebClientConfig. */
    private final WebClient webClient;

    /**
     * Constructor con inyección de dependencias.
     * Spring inyecta automáticamente el bean de WebClient configurado.
     *
     * @param webClient instancia de WebClient configurada con la base URL de TV Maze.
     */
    public TvMazeClient(WebClient tvmazeWebClient) {
        this.webClient = tvmazeWebClient;
    }

    /**
     * Consume el endpoint de búsqueda de TV Maze: /search/shows?q={query}.
     * Devuelve una lista de resultados con su score de relevancia.
     *
     * Flujo:
     * 1. Realiza GET a /search/shows pasando el query como parámetro.
     * 2. Deserializa la respuesta a una lista de TvMazeSearchResult.
     * 3. Maneja errores HTTP (4xx, 5xx) y de conexión de forma centralizada.
     *
     * @param query texto de búsqueda (nombre del show, actor, etc.).
     * @return Mono con la lista de resultados (puede estar vacía si no hay coincidencias).
     */
    public Mono<List<TvMazeSearchResult>> searchShows(String query) {
        log.info("Consultando API de TV Maze - búsqueda de shows con query: '{}'", query);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/shows")
                        .queryParam("q", query)
                        .build())
                .retrieve()
                .bodyToFlux(TvMazeSearchResult.class)
                .collectList()
                .doOnSuccess(results -> log.info("Búsqueda completada. Resultados encontrados: {}",
                        results != null ? results.size() : 0))
                .doOnError(error -> log.error("Error al buscar shows con query '{}': {}",
                        query, error.getMessage()))
                .onErrorResume(WebClientResponseException.class, error -> {
                    log.warn("TV Maze respondió con status {}: {}",
                            error.getStatusCode(), error.getResponseBodyAsString());
                    return Mono.just(List.of());
                })
                .onErrorResume(Exception.class, error -> {
                    log.error("Error de conexión con TV Maze: {}", error.getMessage());
                    return Mono.just(List.of());
                });
    }

    /**
     * Consume el endpoint de detalle de TV Maze: /shows/{showId}.
     * Devuelve la información completa de un show específico.
     *
     * Flujo:
     * 1. Realiza GET a /shows/{showId}.
     * 2. Deserializa la respuesta a un objeto TvMazeShow.
     * 3. Si el show no existe (404), devuelve Mono.empty().
     *
     * @param showId ID del show en TV Maze.
     * @return Mono con el show completo, o Mono.empty() si no se encuentra.
     */
    public Mono<TvMazeShow> getShowById(Integer showId) {
        log.info("Consultando API de TV Maze - detalle del show con ID: {}", showId);

        return webClient.get()
                .uri("/shows/{showId}", showId)
                .retrieve()
                .bodyToMono(TvMazeShow.class)
                .doOnSuccess(show -> log.info("Show obtenido correctamente. Nombre: {}",
                        show != null ? show.getName() : "null"))
                .doOnError(error -> log.error("Error al obtener show con ID {}: {}",
                        showId, error.getMessage()))
                .onErrorResume(WebClientResponseException.NotFound.class, error -> {
                    log.warn("Show con ID {} no encontrado en TV Maze", showId);
                    return Mono.empty();
                })
                .onErrorResume(WebClientResponseException.class, error -> {
                    log.error("TV Maze respondió con error {} para show ID {}",
                            error.getStatusCode(), showId);
                    return Mono.empty();
                })
                .onErrorResume(Exception.class, error -> {
                    log.error("Error de conexión al consultar show ID {}: {}",
                            showId, error.getMessage());
                    return Mono.empty();
                });
    }
}

