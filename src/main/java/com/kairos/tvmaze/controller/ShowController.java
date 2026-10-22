package com.kairos.tvmaze.controller;

import com.kairos.tvmaze.dto.response.ShowSearchResponse;
import com.kairos.tvmaze.service.ShowService;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;


/**
 * Controlador REST que expone los endpoints relacionados con shows.
 *
 * Responsabilidades:
 * - Endpoint Search A (GET /api/search/shows): búsqueda de shows por texto.
 *
 * Se utiliza @Validated a nivel de clase para habilitar la validación
 * de parámetros de método (NotBlank, NotNull, etc.).
 */
@Slf4j
@RestController
@RequestMapping("/api")
@Validated
public class ShowController {

    private final ShowService showService;

    public ShowController(ShowService showService) {
        this.showService = showService;
    }

    /**
     * Endpoint A: Búsqueda de shows por texto.
     *
     * @param q texto de búsqueda (nombre del show, actor, etc.).
     * @return ResponseEntity con lista de ShowSearchResponse (HTTP 200).
     *         Si no hay resultados, retorna lista vacía.
     */
    @GetMapping("/search/shows")
    public ResponseEntity<List<ShowSearchResponse>> searchShows(
            @RequestParam(name = "q")
            @NotBlank(message = "El parámetro de búsqueda 'q' es obligatorio y no puede estar vacío")
            String q) {

        log.info("Recibida petición de búsqueda de shows con query: '{}'", q);

        List<ShowSearchResponse> results = showService.searchShows(q);

        log.info("Respondiendo con {} resultados para query '{}'", results.size(), q);

        return ResponseEntity.ok(results);
    }

}

