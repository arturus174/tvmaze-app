package com.kairos.tvmaze;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal que arranca la aplicación Spring Boot.
 * Actúa como el punto de entrada del microservicio middleware que
 * orquesta las peticiones hacia la API pública de TV Maze y
 * gestiona el caché/comentarios en MongoDB Atlas.
 */
@SpringBootApplication
public class TvMazeMiddlewareApplication {

    /**
     * Método main que inicializa el contexto de Spring y arranca el servidor embebido (Tomcat).
     *
     * @param args Argumentos de línea de comandos (si los hubiera).
     */
    public static void main(String[] args) {
        SpringApplication.run(TvMazeMiddlewareApplication.class, args);
    }
}
