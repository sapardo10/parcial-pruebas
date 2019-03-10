package org.shredzone.flattr4j.exception;

public class FlattrException extends Exception {
    private static final long serialVersionUID = 3095863989605383892L;

    public FlattrException(String msg) {
        super(msg);
    }

    public FlattrException(Throwable cause) {
        super(cause);
    }

    public FlattrException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
