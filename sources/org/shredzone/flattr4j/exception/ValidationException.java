package org.shredzone.flattr4j.exception;

public class ValidationException extends FlattrServiceException {
    private static final long serialVersionUID = 7637026385151047748L;

    public ValidationException(String code, String msg) {
        super(code, msg);
    }
}
