package com.kairos.tvmaze.service;

import com.kairos.tvmaze.client.TvMazeClient;
import com.kairos.tvmaze.dto.response.CommentResponse;
import com.kairos.tvmaze.dto.response.ShowDetailResponse;
import com.kairos.tvmaze.dto.response.ShowSearchResponse;
import com.kairos.tvmaze.model.domain.TvMazeSearchResult;
import com.kairos.tvmaze.model.domain.TvMazeShow;
import com.kairos.tvmaze.model.entity.Comment;
import com.kairos.tvmaze.model.entity.ShowCache;
import com.kairos.tvmaze.repository.CommentRepository;
import com.kairos.tvmaze.repository.ShowCacheRepository;
import com.kairos.tvmaze.service.mapper.ShowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio que orquesta la lógica de negocio para los endpoints de shows.
 *
 * Responsabilidades:
 * - Endpoint search A : consumir API externa.
 * - Endpoint show B : Se valida caché en Mongo antes de 
 *   obtienerlo de la API externa.
 */
@Slf4j
@Service
public class ShowService {

    private final TvMazeClient tvMazeClient;
    private final ShowMapper showMapper;
    private final ShowCacheRepository showCacheRepository;
    private final CommentRepository commentRepository;


    public ShowService(TvMazeClient tvMazeClient,
                       ShowMapper showMapper,
                       ShowCacheRepository showCacheRepository,
                       CommentRepository commentRepository) {
        this.tvMazeClient = tvMazeClient;
        this.showMapper = showMapper;
        this.showCacheRepository = showCacheRepository;
        this.commentRepository = commentRepository;
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
                .map(result -> {
                    TvMazeShow show = result.getShow();
                    ShowSearchResponse response = showMapper.toSearchResponse(show);

                    // Consultar comentarios para este show específico
                    List<Comment> comments = commentRepository.findByShowIdOrderByCreatedAtDesc(show.getId());
                    response.setComments(mapCommentsToResponse(comments));

                    return response;
                })
                .collect(Collectors.toList());
    }

    /**
     * Punto B (Básico y cache): Obtiene el show por ID consumiendo la API externa.
     * Si existe, lo retorna. Si no, consume la API, guarda en Mongo y luego retorna.
     * 
     * @param showId ID del show en TV Maze.
     * @return Optional con el ShowDetailResponse si existe, vacío si no.
     */
    public Optional<ShowDetailResponse> getShowById(Integer showId) {
        log.info("Solicitando detalle del show con ID: {}", showId);

        // 1. Validar si ya existe en el caché de MongoDB
        Optional<ShowCache> cachedShow = showCacheRepository.findByShowId(showId);
        
        if (cachedShow.isPresent()) {
            log.info("Show ID {} encontrado en caché de MongoDB. Evitando llamada a API externa.", showId);
            return Optional.of(showMapper.fromCacheToDetailResponse(cachedShow.get()));
        }

        // 2. Si no existe, consumir la API externa
        log.info("Show ID {} no está en caché. Consumiendo API de TV Maze...", showId);
        TvMazeShow show = tvMazeClient.getShowById(showId).block();

        if (show == null) {
            log.warn("Show ID {} no encontrado en la API de TV Maze.", showId);
            return Optional.empty();
        }

        // 3. Guardar el resultado en MongoDB antes de retornar
        log.info("Guardando show ID {} en caché de MongoDB.", showId);
        ShowCache cacheEntity = showMapper.toCacheEntity(show);
        showCacheRepository.save(cacheEntity);

        // 4. Retornar la respuesta mapeada desde el objeto de la API
        return Optional.of(showMapper.toDetailResponse(show));
        
    }

    /**
     * Método auxiliar para convertir entidades Comment a DTOs CommentResponse.
     */
    private List<CommentResponse> mapCommentsToResponse(List<Comment> comments) {
        if (comments == null || comments.isEmpty()) {
            return Collections.emptyList();
        }

        return comments.stream()
                .map(comment -> CommentResponse.builder()
                        .comment(comment.getComment())
                        .rating(comment.getRating())
                        .build())
                .collect(Collectors.toList());
    }


    
}

