package de.danoeh.antennapod.core.storage;

import de.danoeh.antennapod.core.service.download.DownloadStatus;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DBWriter$ySZMqYxWfPDgDDGW83kKqpzMNOc implements Runnable {
    private final /* synthetic */ DownloadStatus f$0;

    public /* synthetic */ -$$Lambda$DBWriter$ySZMqYxWfPDgDDGW83kKqpzMNOc(DownloadStatus downloadStatus) {
        this.f$0 = downloadStatus;
    }

    public final void run() {
        DBWriter.lambda$addDownloadStatus$6(this.f$0);
    }
}
