package org.shredzone.flattr4j.exception;

public class NotFoundException extends FlattrServiceException {
    private static final long serialVersionUID = -1178705084339857902L;

    public NotFoundException(String code, String msg) {
        super(code, msg);
    }
}
