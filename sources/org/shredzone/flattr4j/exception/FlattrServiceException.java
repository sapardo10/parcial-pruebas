package org.shredzone.flattr4j.exception;

public class FlattrServiceException extends FlattrException {
    private static final long serialVersionUID = -7058726202855943210L;
    private final String code;

    public FlattrServiceException(String code, String msg) {
        super(msg);
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.code);
        stringBuilder.append(": ");
        stringBuilder.append(super.toString());
        return stringBuilder.toString();
    }
}
