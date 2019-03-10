package org.shredzone.flattr4j.exception;

public class RateLimitExceededException extends FlattrServiceException {
    private static final long serialVersionUID = 2052032965034468567L;

    public RateLimitExceededException(String code, String msg) {
        super(code, msg);
    }
}
