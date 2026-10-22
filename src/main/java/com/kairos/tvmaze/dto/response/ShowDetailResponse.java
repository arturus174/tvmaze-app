package com.kairos.tvmaze.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO que representa la información completa de un show
 * en el endpoint de detalle. Incluye todos los atributos
 * del show más el arreglo de comentarios asociados.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShowDetailResponse {

    /** ID único del show. */
    private Integer id;

    /** Nombre del show. */
    private String name;

    /** URL oficial del show en TV Maze. */
    private String url;

    /** Resumen del show. */
    private String summary;

    /** Idioma original. */
    private String language;

    /** Lista de géneros. */
    private List<String> genres;

    /** Estado del show. */
    private String status;

    /** Duración por episodio en minutos. */
    private Integer runtime;

    /** Fecha de estreno. */
    private String premiered;

    /** URL de la imagen oficial del show. */
    private String imageUrl;

    /** Nombre del canal (network o webChannel). */
    private String channelName;

    /** País del canal. */
    private String channelCountry;

}
