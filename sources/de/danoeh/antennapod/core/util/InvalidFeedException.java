package de.danoeh.antennapod.core.util;

public class InvalidFeedException extends Exception {
    public InvalidFeedException(String detailMessage) {
        super(detailMessage);
    }

    public InvalidFeedException(Throwable throwable) {
        super(throwable);
    }

    public InvalidFeedException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
