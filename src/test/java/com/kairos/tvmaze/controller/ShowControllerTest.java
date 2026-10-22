package com.kairos.tvmaze.controller;

import com.kairos.tvmaze.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.tvmaze.dto.response.CommentResponse;
import com.kairos.tvmaze.dto.response.ShowDetailResponse;
import com.kairos.tvmaze.dto.response.ShowSearchResponse;
import com.kairos.tvmaze.service.ShowService;
import com.kairos.tvmaze.dto.response.CommentResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para ShowController usando MockMvc.
 *
 * MockMvc simula peticiones HTTP reales sin arrancar un servidor completo,
 * permitiendo probar el flujo completo: HTTP → Controller → Service (mockeado).
 *
 * @WebMvcTest carga solo la capa web (controladores), no el contexto completo.
 */
@WebMvcTest(ShowController.class)
@Import(GlobalExceptionHandler.class)
class ShowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Mock del servicio de shows.
     * Se mockea para aislar las pruebas del controlador de la lógica de negocio.
     */
    @MockBean
    private ShowService showService;

    /**
     * Prueba el endpoint GET /api/search/shows?q=breaking con resultados.
     *
     * Validaciones:
     * - HTTP 200 OK.
     */
    @Test
    @DisplayName("GET /api/search/shows con query válido debe retornar HTTP 200")
    void searchShows_withValidQuery_shouldReturn200() throws Exception {
        // Arrange
        ShowSearchResponse show = ShowSearchResponse.builder()
                .id(1)
                .name("Breaking Bad")
                .channel("AMC")
                .summary("A chemistry teacher")
                .genres(List.of("Drama"))
                .build();

        when(showService.searchShows("breaking")).thenReturn(List.of(show));

        // Act & Assert
        mockMvc.perform(get("/api/search/shows")
                        .param("q", "breaking"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Breaking Bad"))
                .andExpect(jsonPath("$[0].channel").value("AMC"));
    }

    /**
     * Prueba el endpoint GET /api/search/shows sin el parámetro "q".
     *
     * Validaciones:
     * - HTTP 400 Bad Request.
     */
    @Test
    @DisplayName("GET /api/search/shows sin parámetro 'q' debe retornar HTTP 400")
    void searchShows_withoutQueryParam_shouldReturn400() throws Exception {
        mockMvc.perform(get("/api/search/shows"))
                .andExpect(status().isBadRequest());
    }

        @Test
    @DisplayName("B- GET /api/shows/{id} con ID existente debe retornar HTTP 200")
    void getShowById_withExistingShow_shouldReturn200() throws Exception {
        // Arrange
        ShowDetailResponse show = ShowDetailResponse.builder()
                .id(1)
                .name("Breaking Bad")
                .language("English")
                .build();

        when(showService.getShowById(1)).thenReturn(Optional.of(show));

        // Act & Assert
        mockMvc.perform(get("/api/shows/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Breaking Bad"))
                .andExpect(jsonPath("$.language").value("English"));
    }

    @Test
    @DisplayName("B- GET /api/shows/{id} con ID inexistente debe retornar HTTP 404")
    void getShowById_withNonExistentShow_shouldReturn404() throws Exception {
        // Arrange
        when(showService.getShowById(999)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/shows/999"))
                .andExpect(status().isNotFound());
    }
    
    /*
     * Se agregan los Test para los comentarios
     */

    @Test
    @DisplayName("A- GET /api/search/shows con query válido debe retornar HTTP 200 con arreglo de comments")
    void searchShows_withValidQuery_shouldReturn200WithComments() throws Exception {
        // Arrange
        CommentResponse comment = CommentResponse.builder()
                .comment("Great show!")
                .rating(5)
                .build();

        ShowSearchResponse show = ShowSearchResponse.builder()
                .id(1)
                .name("Breaking Bad")
                .channel("AMC")
                .summary("A chemistry teacher")
                .genres(List.of("Drama"))
                .comments(List.of(comment))
                .build();

        when(showService.searchShows("breaking")).thenReturn(List.of(show));

        // Act & Assert
        mockMvc.perform(get("/api/search/shows")
                        .param("q", "breaking"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Breaking Bad"))
                .andExpect(jsonPath("$[0].channel").value("AMC"))
                // Validaciones clave del Commit 5:
                .andExpect(jsonPath("$[0].comments").isArray())
                .andExpect(jsonPath("$[0].comments[0].comment").value("Great show!"))
                .andExpect(jsonPath("$[0].comments[0].rating").value(5));
    }

    @Test
    @DisplayName("A- GET /api/search/shows sin comentarios debe retornar arreglo comments vacío")
    void searchShows_withoutComments_shouldReturnEmptyCommentsArray() throws Exception {
        // Arrange
        ShowSearchResponse show = ShowSearchResponse.builder()
                .id(2)
                .name("Test Show")
                .channel("HBO")
                .summary("Test summary")
                .genres(List.of("Drama"))
                .comments(Collections.emptyList())
                .build();

        when(showService.searchShows("test")).thenReturn(List.of(show));

        // Act & Assert
        mockMvc.perform(get("/api/search/shows")
                        .param("q", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].comments").isArray())
                .andExpect(jsonPath("$[0].comments").isEmpty());
    }

    //Test comentarios B
        @Test
    @DisplayName("B- GET /api/shows/{id} debe retornar 200 con el arreglo comments")
    void getShowById_withComments_shouldReturn200WithCommentsArray() throws Exception {
        // Arrange: detalle enriquecido tal como lo entrega el servicio.
        ShowDetailResponse show = ShowDetailResponse.builder()
                .id(1)
                .name("Breaking Bad")
                .language("English")
                .channelName("AMC")
                .comments(List.of(
                        CommentResponse.builder().comment("Increíble serie").rating(5).build(),
                        CommentResponse.builder().comment("Buen final").rating(4).build()
                ))
                .build();

        when(showService.getShowById(1)).thenReturn(Optional.of(show));

        // Act & Assert: valida la estructura JSON exacta requerida por el PDF.
        mockMvc.perform(get("/api/shows/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Breaking Bad"))
                .andExpect(jsonPath("$.comments").isArray())
                .andExpect(jsonPath("$.comments.length()").value(2))
                .andExpect(jsonPath("$.comments[0].comment").value("Increíble serie"))
                .andExpect(jsonPath("$.comments[0].rating").value(5))
                .andExpect(jsonPath("$.comments[1].comment").value("Buen final"))
                .andExpect(jsonPath("$.comments[1].rating").value(4));
    }

    @Test
    @DisplayName("B- GET /api/shows/{id} sin comentarios debe retornar comments como arreglo vacío")
    void getShowById_withoutComments_shouldReturnEmptyCommentsArray() throws Exception {
        // Arrange
        ShowDetailResponse show = ShowDetailResponse.builder()
                .id(2)
                .name("Test Show")
                .comments(Collections.emptyList())
                .build();

        when(showService.getShowById(2)).thenReturn(Optional.of(show));

        // Act & Assert
        mockMvc.perform(get("/api/shows/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comments").isArray())
                .andExpect(jsonPath("$.comments").isEmpty());
    }

    @Test
    @DisplayName("B- GET /api/shows/{id} inexistente debe retornar 404")
    void getShowById_whenNotFound_shouldReturn404() throws Exception {
        // Arrange
        when(showService.getShowById(999)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/shows/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("B- GET /api/shows/{id} con ID no numérico debe retornar 400")
    void getShowById_withInvalidIdType_shouldReturn400() throws Exception {
        // Act & Assert: conversión de tipo fallida, no debe llegar al servicio.
        mockMvc.perform(get("/api/shows/abc"))
                .andExpect(status().isBadRequest());
    }
}
