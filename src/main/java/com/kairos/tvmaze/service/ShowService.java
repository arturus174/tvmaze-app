package com.kairos.tvmaze.service;

import com.kairos.tvmaze.client.TvMazeClient;
import com.kairos.tvmaze.dto.response.ShowSearchResponse;
import com.kairos.tvmaze.model.domain.TvMazeSearchResult;
import com.kairos.tvmaze.service.mapper.ShowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio que orquesta la lógica de negocio para los endpoints de shows.
 *
 * Responsabilidades:
 * - Endpoint search A : consumir API externa.
 */
@Slf4j
@Service
public class ShowService {

    private final TvMazeClient tvMazeClient;
    private final ShowMapper showMapper;


    public ShowService(TvMazeClient tvMazeClient,
                       ShowMapper showMapper) {
        this.tvMazeClient = tvMazeClient;
        this.showMapper = showMapper;
    }

    /**
     * Busca shows en TV Maze consumiendo la api 
     * @param query texto de búsqueda (nombre del show, actor, etc.).
     * @return lista de shows con sus comentarios (puede estar vacía).
     */
    public List<ShowSearchResponse> searchShows(String query) {
        log.info("Iniciando búsqueda de shows con query: '{}'", query);

        // Paso 1: Consumir API externa de TV Maze
        List<TvMazeSearchResult> results = tvMazeClient.searchShows(query).block();

        if (results == null || results.isEmpty()) {
            log.info("No se encontraron resultados para el query: '{}'", query);
            return Collections.emptyList();
        }

        log.info("Se encontraron {} resultados.", results.size());

        return results.stream()
                .filter(result -> result.getShow() != null)
                .map(result -> showMapper.toSearchResponse(result.getShow()))
                .collect(Collectors.toList());
    }

    
}

