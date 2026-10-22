package com.kairos.tvmaze.service;

import com.kairos.tvmaze.dto.request.CommentRequest;
import com.kairos.tvmaze.dto.response.StatusResponse;
import com.kairos.tvmaze.model.entity.Comment;
import com.kairos.tvmaze.repository.CommentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Servicio que gestiona la lógica de negocio para los comentarios.
 */
@Slf4j
@Service
public class CommentService {

    private final CommentRepository commentRepository;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param commentRepository repositorio de comentarios en MongoDB.
     */
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    /**
     * Servicio que gestiona la lógica de negocio para los comentarios.
     *
     * @param request DTO con los datos del comentario (showId, comment, rating).
     * @return StatusResponse con el código HTTP, mensaje e ID del comentario creado.
     */
    public StatusResponse createComment(CommentRequest request) {
        log.info("Creando comentario para show ID: {}", request.getShowId());

        // Convertir DTO a entidad
        Comment comment = Comment.builder()
                .showId(request.getShowId())
                .comment(request.getComment())
                .rating(request.getRating())
                .createdAt(LocalDateTime.now())
                .build();

        // Guardar en MongoDB
        Comment savedComment = commentRepository.save(comment);

        log.info("Comentario creado exitosamente. ID: {}", savedComment.getId());

        // Respuesta de status
        return StatusResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("Comentario creado exitosamente")
                .commentId(savedComment.getId())
                .build();
    }
}
