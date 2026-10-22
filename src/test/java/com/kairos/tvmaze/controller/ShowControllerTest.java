package com.kairos.tvmaze.controller;

import com.kairos.tvmaze.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.tvmaze.dto.response.ShowDetailResponse;
import com.kairos.tvmaze.dto.response.ShowSearchResponse;
import com.kairos.tvmaze.service.ShowService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

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

}
