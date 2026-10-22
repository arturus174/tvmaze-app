package com.kairos.tvmaze.controller;

import com.kairos.tvmaze.dto.request.CommentRequest;
import com.kairos.tvmaze.dto.response.StatusResponse;
import com.kairos.tvmaze.service.CommentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST que expone el endpoint de creación de comentarios.
 */
@Slf4j
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param commentService servicio que gestiona la creación de comentarios.
     */
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Endpoint C: Guardar calificación y comentario.
     *
     * Ejemplo de llamada:
     *   POST /api/comments
     *   {
     *     "showId": 1,
     *     "comment": "Excelente serie",
     *     "rating": 5
     *   }
     *
     * @param request DTO con los datos del comentario.
     * @return ResponseEntity con StatusResponse (HTTP 201).
     */
    @PostMapping
    public ResponseEntity<StatusResponse> createComment(@Valid @RequestBody CommentRequest request) {
        log.info("Recibida petición de creación de comentario para show ID: {}", request.getShowId());

        StatusResponse response = commentService.createComment(request);

        log.info("Comentario creado exitosamente. ID: {}", response.getCommentId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
