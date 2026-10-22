package com.kairos.tvmaze.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa una red de televisión tradicional (ej: NBC, HBO).
 * Contiene el nombre y el país de origen.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TvMazeNetwork {

    /** ID interno de la red en TV Maze. */
    private Integer id;

    /** Nombre de la red (ej: "NBC"). */
    private String name;

    /** País de origen de la red. */
    private TvMazeCountry country;
}
