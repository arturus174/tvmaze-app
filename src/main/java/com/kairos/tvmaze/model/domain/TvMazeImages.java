package com.kairos.tvmaze.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Contenedor de las imágenes oficiales del show.
 * TV Maze devuelve varias resoluciones; usamos la "original".
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TvMazeImages {

    /** Imagen en resolución media. */
    private TvMazeImage medium;

    /** Imagen en resolución original (la de mayor calidad). */
    private TvMazeImage original;
}
