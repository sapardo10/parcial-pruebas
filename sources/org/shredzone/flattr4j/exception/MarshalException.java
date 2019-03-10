package org.shredzone.flattr4j.exception;

public class MarshalException extends RuntimeException {
    private static final long serialVersionUID = 961055160464831870L;

    public MarshalException(String msg) {
        super(msg);
    }

    public MarshalException(Throwable cause) {
        super(cause);
    }

    public MarshalException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
