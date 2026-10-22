package com.kairos.tvmaze.config;

import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

/**
 * Clase de configuración que define el bean de WebClient.
 * WebClient es el cliente HTTP no bloqueante de Spring WebFlux,
 * ideal para consumir APIs externas de forma eficiente y reactiva.
 *
 * Se configura con la URL base de TV Maze (leída desde application.yml)
 * y timeouts razonables para evitar que una petición lenta bloquee
 * indefinidamente los hilos de la aplicación.
 */
@Configuration
public class WebClientConfig {

    /**
     * URL base de la API de TV Maze, inyectada desde application.yml.
     * Ejemplo: https://api.tvmaze.com
     */
    @Value("${tvmaze.api.base-url}")
    private String baseUrl;

    /**
     * Crea y configura el bean de WebClient utilizado para consumir
     * la API externa de TV Maze.
     *
     * Configuraciones aplicadas:
     * - Base URL: evita repetir el dominio en cada petición.
     * - Timeout de conexión: 5 segundos.
     * - Timeout de respuesta: 10 segundos.
     *
     * @return instancia de WebClient configurada y lista para usar.
     */
    @Bean
    public WebClient tvmazeWebClient() {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(10))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5_000);

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}

