package com.udipsai.backend.common.exception;

/**
 * ExcepciÃ³n lanzada cuando un usuario no tiene autorizaciÃ³n para acceder a un recurso
 */
public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
