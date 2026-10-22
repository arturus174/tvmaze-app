package com.kairos.tvmaze.model.entity;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

/**
 * Entidad que representa un comentario y calificación de un usuario
 * sobre un show específico en MongoDB.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "comments")
public class Comment {

    @Id
    private String id;

    /**
     * ID del show al que pertenece el comentario.
     * Indexado para optimizar futuras consultas por show.
     */
    @NotNull(message = "El showId es obligatorio")
    @Indexed
    private Integer showId;

    /** Texto del comentario dejado por el usuario. */
    @NotBlank(message = "El comentario no puede estar vacío")
    private String comment;

    /** Calificación numérica del 0 al 5 (ambos inclusive). */
    @NotNull(message = "El rating es obligatorio")
    @Min(value = 0, message = "El rating mínimo es 0")
    @Max(value = 5, message = "El rating máximo es 5")
    private Integer rating;

    /** Fecha y hora en que se creó el comentario. */
    private LocalDateTime createdAt;
}
