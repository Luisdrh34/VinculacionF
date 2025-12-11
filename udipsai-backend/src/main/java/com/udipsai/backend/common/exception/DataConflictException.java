package com.udipsai.backend.common.exception;

/**
 * ExcepciÃ³n lanzada cuando hay un conflicto de datos (duplicados, constraints, etc)
 */
public class DataConflictException extends RuntimeException {
    public DataConflictException(String message) {
        super(message);
    }
}
