package org.shredzone.flattr4j.exception;

public class NoMoneyException extends FlattrServiceException {
    private static final long serialVersionUID = -5034022662480993764L;

    public NoMoneyException(String code, String msg) {
        super(code, msg);
    }
}
