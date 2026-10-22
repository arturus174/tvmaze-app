package com.kairos.tvmaze.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para el endpoint de creación de comentarios.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {

    /**
     * ID del show al que se asocia el comentario.
     * Debe ser un valor positivo.
     */
    @NotNull(message = "El campo showId es obligatorio")
    private Integer showId;

    /**
     * Texto del comentario.
     * No puede estar vacío y tiene un límite de 1000 caracteres.
     */
    @NotBlank(message = "El comentario no puede estar vacío")
    @Size(max = 1000, message = "El comentario no puede exceder los 1000 caracteres")
    private String comment;

    /**
     * Calificación numérica del 0 al 5.
     */
    @NotNull(message = "El rating es obligatorio")
    @Min(value = 0, message = "El rating mínimo permitido es 0")
    @Max(value = 5, message = "El rating máximo permitido es 5")
    private Integer rating;
}

