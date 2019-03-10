package org.awaitility.core;

public class ConditionTimeoutException extends RuntimeException {
    public ConditionTimeoutException(String message) {
        super(message);
    }

    public ConditionTimeoutException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
