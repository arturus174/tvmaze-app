package com.kairos.tvmaze.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa un comentario individual dentro del arreglo
 * de comentarios de un show.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {

    /** Texto del comentario. */
    private String comment;

    /** Calificación otorgada (0-5). */
    private Integer rating;
}