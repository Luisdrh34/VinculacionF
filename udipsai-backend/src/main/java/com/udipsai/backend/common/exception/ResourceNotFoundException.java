package com.udipsai.backend.common.exception;

/**
 * ExcepciÃ³n lanzada cuando un recurso no es encontrado
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
