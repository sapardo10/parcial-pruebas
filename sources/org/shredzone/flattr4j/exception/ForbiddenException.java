package org.shredzone.flattr4j.exception;

public class ForbiddenException extends FlattrServiceException {
    private static final long serialVersionUID = 1279509373771421366L;

    public ForbiddenException(String code, String msg) {
        super(code, msg);
    }
}
