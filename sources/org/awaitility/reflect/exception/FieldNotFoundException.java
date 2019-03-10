package org.awaitility.reflect.exception;

public class FieldNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 5420195402982130931L;

    public FieldNotFoundException(String message) {
        super(message);
    }
}
