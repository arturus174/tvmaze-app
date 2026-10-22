package com.kairos.tvmaze.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para toda la aplicación.
 *
 * Responsabilidades:
 * - Capturar excepciones de validación (@Valid, @NotBlank, etc.).
 * - Capturar parámetros faltantes o con tipo incorrecto.
 * - Capturar errores genéricos no manejados.
 * - Retornar respuestas JSON consistentes con información útil
 *   para el cliente (timestamp, status, mensaje, errores detallados).
 *
 * Se utiliza @RestControllerAdvice para aplicar este manejador
 * a todos los controladores de la aplicación.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja errores de validación de Jakarta Validation (@Valid).
     * Se dispara cuando un DTO marcado con @Valid tiene campos inválidos.
     *
     * Ejemplo: rating fuera del rango 0-5, comment vacío, etc.
     *
     * @param ex excepción lanzada por Spring al fallar la validación.
     * @return ResponseEntity con HTTP 400 (Bad Request) y detalle de errores.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("Error de validación en la petición: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message("Error de validación en los datos de entrada")
                .errors(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Maneja parámetros de petición faltantes (ej: falta el "q" en /search/shows).
     *
     * @param ex excepción lanzada cuando un @RequestParam obligatorio no se envía.
     * @return ResponseEntity con HTTP 400 (Bad Request).
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParams(MissingServletRequestParameterException ex) {
        log.warn("Parámetro faltante en la petición: '{}'", ex.getParameterName());

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message("El parámetro '" + ex.getParameterName()
                        + "' de tipo " + ex.getParameterType() + " es obligatorio")
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Maneja errores de tipo de dato incorrecto en parámetros de ruta o query.
     * Ejemplo: enviar "abc" donde se espera un Integer para showId.
     *
     * @param ex excepción lanzada cuando no se puede convertir el tipo.
     * @return ResponseEntity con HTTP 400 (Bad Request).
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.warn("Tipo de dato incorrecto para el parámetro '{}': {}",
                ex.getName(), ex.getValue());

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message("El parámetro '" + ex.getName() + "' debe ser de tipo " +
                        ex.getRequiredType().getSimpleName())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Manejador genérico para cualquier excepción no capturada específicamente.
     * Evita que el cliente reciba stack traces o mensajes técnicos crudos.
     *
     * @param ex excepción genérica.
     * @return ResponseEntity con HTTP 500 (Internal Server Error).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Error interno no manejado: {}", ex.getMessage(), ex);

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("Ocurrió un error inesperado. Por favor, intente más tarde.")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * DTO interno para estructurar las respuestas de error.
     * Garantiza consistencia en el formato JSON de todas las respuestas de error.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorResponse {
        private LocalDateTime timestamp;
        private Integer status;
        private String error;
        private String message;
        private Map<String, String> errors;
    }
}

