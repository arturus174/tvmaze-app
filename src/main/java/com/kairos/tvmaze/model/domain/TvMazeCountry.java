package com.kairos.tvmaze.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa la información del país asociado a un canal
 * (ya sea network o webChannel).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TvMazeCountry {

    /** Nombre del país (ej: "United States"). */
    private String name;

    /** Código ISO del país (ej: "US"). */
    private String code;

    /** Zona horaria del país (ej: "America/New_York"). */
    private String timezone;
}
