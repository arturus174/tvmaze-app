package com.kairos.tvmaze.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Modelo que representa la estructura JSON de un show
 * devuelto por la API pública de TV Maze.
 * Se usa para deserializar las respuestas HTTP mediante Jackson.
 *
 * Nota: @JsonIgnoreProperties(ignoreUnknown = true) permite ignorar
 * campos que TV Maze pueda agregar en el futuro sin romper la app.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TvMazeShow {

    /** ID único del show en TV Maze. */
    private Integer id;

    /** URL oficial del show en TV Maze. */
    private String url;

    /** Nombre del show. */
    private String name;

    /** Tipo de medio (ej: "Scripted"). */
    private String type;

    /** Idioma original del show. */
    private String language;

    /** Lista de géneros (ej: ["Drama", "Science-Fiction"]). */
    private List<String> genres;

    /** Estado del show ("Running", "Ended", etc.). */
    private String status;

    /** Duración en minutos por episodio. */
    private Integer runtime;

    /** Fecha de estreno (YYYY-MM-DD). */
    private String premiered;

    /** Resumen del show en formato HTML. */
    private String summary;

    /** Objeto con las imágenes oficiales del show. */
    private TvMazeImages image;

    /** Red de televisión donde se emite (puede ser null). */
    private TvMazeNetwork network;

    /** Canal web donde se emite (puede ser null). */
    @JsonProperty("webChannel")
    private TvMazeWebChannel webChannel;
}
