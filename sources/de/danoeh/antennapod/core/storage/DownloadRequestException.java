package de.danoeh.antennapod.core.storage;

public class DownloadRequestException extends Exception {
    public DownloadRequestException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public DownloadRequestException(String detailMessage) {
        super(detailMessage);
    }

    public DownloadRequestException(Throwable throwable) {
        super(throwable);
    }
}
