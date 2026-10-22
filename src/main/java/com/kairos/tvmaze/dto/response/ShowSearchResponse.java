package com.kairos.tvmaze.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO que representa un show en la respuesta del endpoint de búsqueda.
 * Contiene los atributos requeridos por el PDF: id, name, channel,
 * summary, genres y el arreglo de comentarios asociados.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShowSearchResponse {

    /** ID único del show en TV Maze. */
    private Integer id;

    /** Nombre del show. */
    private String name;

    /**
     * Nombre del canal (network o webChannel).
     * Se prioriza network.name; si es null, se usa webChannel.name.
     */
    private String channel;

    /** Resumen del show (puede contener HTML). */
    private String summary;

    /** Lista de géneros asociados al show. */
    private List<String> genres;

    /**
     * Arreglo de comentarios guardados para este show.
     */
    private List<CommentResponse> comments;
}
