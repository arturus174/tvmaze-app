package com.kairos.tvmaze.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa un canal web/plataforma de streaming (ej: Netflix, Hulu).
 * Estructura similar a TvMazeNetwork pero para medios digitales.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TvMazeWebChannel {

    /** ID interno del canal web en TV Maze. */
    private Integer id;

    /** Nombre del canal web (ej: "Netflix"). */
    private String name;

    /** País de origen del canal web. */
    private TvMazeCountry country;
}
