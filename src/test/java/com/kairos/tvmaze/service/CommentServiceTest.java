package com.kairos.tvmaze.service;

import com.kairos.tvmaze.dto.request.CommentRequest;
import com.kairos.tvmaze.dto.response.StatusResponse;
import com.kairos.tvmaze.model.entity.Comment;
import com.kairos.tvmaze.repository.CommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas de integración para CommentService.
 */
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    @DisplayName("C- Endpoint comments: Crear comentario válido debe persistir y retornar status 201")
    void createComment_withValidRequest_shouldPersistAndReturnStatus201() {
        // Arrange
        CommentRequest request = CommentRequest.builder()
                .showId(1)
                .comment("Excelente serie")
                .rating(5)
                .build();

        Comment savedComment = Comment.builder()
                .id("abc123")
                .showId(1)
                .comment("Excelente serie")
                .rating(5)
                .build();

        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        // Act
        StatusResponse response = commentService.createComment(request);

        // Assert
        assertNotNull(response);
        assertEquals(201, response.getStatus());
        assertEquals("Comentario creado exitosamente", response.getMessage());
        assertEquals("abc123", response.getCommentId());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }
}