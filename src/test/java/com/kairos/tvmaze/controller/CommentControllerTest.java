package com.kairos.tvmaze.controller;

import com.kairos.tvmaze.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.tvmaze.dto.request.CommentRequest;
import com.kairos.tvmaze.dto.response.StatusResponse;
import com.kairos.tvmaze.service.CommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para CommentController usando MockMvc.
 */
@WebMvcTest(CommentController.class)
@Import(GlobalExceptionHandler.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    /**
     * Prueba la creación exitosa de un comentario.
     *
     * Validaciones:
     * - HTTP 201 Created.
     * - El cuerpo JSON contiene el status, mensaje y commentId.
     */
    @Test
    @DisplayName("POST /api/comments con datos válidos debe retornar HTTP 201")
    void createComment_withValidData_shouldReturn201() throws Exception {
        // Arrange
        CommentRequest request = CommentRequest.builder()
                .showId(1)
                .comment("Excellent series!")
                .rating(5)
                .build();

        StatusResponse response = StatusResponse.builder()
                .status(201)
                .message("Comentario creado exitosamente")
                .commentId("abc123")
                .build();

        when(commentService.createComment(any(CommentRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Comentario creado exitosamente"))
                .andExpect(jsonPath("$.commentId").value("abc123"));
    }

    /**
     * Prueba la validación de rating fuera de rango (0-5).
     *
     * Validaciones:
     * - HTTP 400 Bad Request.
     * - El cuerpo JSON contiene el mensaje de error de validación.
     */
    @Test
    @DisplayName("POST /api/comments con rating > 5 debe retornar HTTP 400")
    void createComment_withInvalidRating_shouldReturn400() throws Exception {
        CommentRequest request = CommentRequest.builder()
                .showId(1)
                .comment("Test")
                .rating(10) // Inválido: > 5
                .build();

        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error de validación en los datos de entrada"));
    }

    /**
     * Prueba la validación de comentario vacío.
     *
     * Validaciones:
     * - HTTP 400 Bad Request.
     */
    @Test
    @DisplayName("POST /api/comments con comment vacío debe retornar HTTP 400")
    void createComment_withEmptyComment_shouldReturn400() throws Exception {
        CommentRequest request = CommentRequest.builder()
                .showId(1)
                .comment("") // Inválido: vacío
                .rating(5)
                .build();

        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Prueba la validación de showId nulo.
     *
     * Validaciones:
     * - HTTP 400 Bad Request.
     */
    @Test
    @DisplayName("POST /api/comments sin showId debe retornar HTTP 400")
    void createComment_withoutShowId_shouldReturn400() throws Exception {
        CommentRequest request = CommentRequest.builder()
                .comment("Test")
                .rating(5)
                .build();

        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
