package com.kairos.tvmaze.repository;

import com.kairos.tvmaze.model.entity.ShowCache;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio de Spring Data MongoDB para la entidad ShowCache.
 */
@Repository
public interface ShowCacheRepository extends MongoRepository<ShowCache, String> {

    /**
     * Busca un show en el caché por su ID proveniente de TV Maze.
     * si el show ya está en caché, no se consume la API externa.
     *
     * @param showId ID del show en TV Maze.
     * @return Optional con el ShowCache si existe, vacío si no.
     */
    Optional<ShowCache> findByShowId(Integer showId);

    /**
     * Verifica si un show ya existe en el caché por su ID.
     *
     * @param showId ID del show en TV Maze.
     * @return true si ya existe, false en caso contrario.
     */
    boolean existsByShowId(Integer showId);

    /**
     * Elimina un show del caché por su ID de TV Maze.
     *
     * @param showId ID del show en TV Maze.
     */
    void deleteByShowId(Integer showId);
}

