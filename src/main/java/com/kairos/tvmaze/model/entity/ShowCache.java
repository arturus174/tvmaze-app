package com.kairos.tvmaze.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad que representa el caché de un show en MongoDB.
 * Se persiste en la colección "show_cache".
 * Su propósito es evitar llamadas redundantes a la API externa de TV Maze
 * cuando un cliente solicita la información de un show por su ID.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "show_cache")
public class ShowCache {

    /**
     * Identificador único del documento en MongoDB.
     * Para diferencialo del ID show proveniente de TV Maze.
     */
    @Id
    private String id;

    /**
     * ID del show proveniente de TV Maze.
     * Se indexa como único para garantizar que no haya duplicados y optimizar la búsqueda.
     */
    @Indexed(unique = true)
    @Field("show_id")
    private Integer showId;

    /** Nombre oficial del show. */
    private String name;

    /** URL oficial del show en TV Maze. */
    private String url;

    /** Resumen/descripción del show. */
    private String summary;

    /** Idioma original del show. */
    private String language;

    /** Lista de géneros asociados al show (ej: ["Drama", "Thriller"]). */
    private List<String> genres;

    /** Estado del show (ej: "Running", "Ended"). */
    private String status;

    /** Duración promedio de cada episodio en minutos. */
    private Integer runtime;

    /** Fecha de estreno del show (formato YYYY-MM-DD). */
    private String premiered;

    /** URL de la imagen oficial del show (poster). */
    private String imageUrl;

    /** Nombre del canal de televisión o plataforma web donde se emite. */
    private String channelName;

    /** País de origen del canal. */
    private String channelCountry;

    /** Fecha y hora en que se registró en caché. */
    private LocalDateTime cachedAt;
}
