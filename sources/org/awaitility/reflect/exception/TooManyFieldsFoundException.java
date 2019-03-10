package org.awaitility.reflect.exception;

public class TooManyFieldsFoundException extends RuntimeException {
    private static final long serialVersionUID = 1564231184610341053L;

    public TooManyFieldsFoundException(String message) {
        super(message);
    }
}
