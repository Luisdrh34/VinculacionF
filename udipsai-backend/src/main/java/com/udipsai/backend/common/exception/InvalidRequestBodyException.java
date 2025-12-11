package com.udipsai.backend.common.exception;

/**
 * ExcepciÃ³n lanzada cuando el cuerpo de la peticiÃ³n es invÃ¡lido
 */
public class InvalidRequestBodyException extends RuntimeException {
    public InvalidRequestBodyException(String message) {
        super(message);
    }
}
