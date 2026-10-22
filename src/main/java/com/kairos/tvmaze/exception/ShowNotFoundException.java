package com.kairos.tvmaze.exception;

/**
 * Excepción lanzada cuando TV Maze no encuentra un show por ID.
 * Se traduce a HTTP 404 en el GlobalExceptionHandler.
 */
public class ShowNotFoundException extends RuntimeException {
    public ShowNotFoundException(Long showId) {
        super("Show noencontrado por id: " + showId);
    }
}

