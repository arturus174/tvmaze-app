package com.kairos.tvmaze.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para el endpoint de creación de comentarios.
 * Devuelve el status de la petición según lo requerido.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusResponse {

    /** Código HTTP o código interno de estado. */
    private Integer status;

    /** Mensaje descriptivo del resultado de la operación. */
    private String message;

    /** ID del comentario creado. */
    private String commentId;
}

