package com.kairos.tvmaze.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Envoltorio que representa cada elemento del arreglo devuelto
 * por el endpoint /search/shows de TV Maze.
 * Contiene un "score" de relevancia y el objeto "show" completo.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TvMazeSearchResult {

    /** Puntaje de relevancia de la coincidencia (0.0 a 1.0). */
    private Double score;

    /** Objeto show completo asociado al resultado. */
    private TvMazeShow show;
}
