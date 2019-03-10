package de.danoeh.antennapod.core.service.download;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DownloadService$-Ouk4A5B9WbWuMaWU-6JBHD_7nQ implements Runnable {
    private final /* synthetic */ DownloadService f$0;

    public /* synthetic */ -$$Lambda$DownloadService$-Ouk4A5B9WbWuMaWU-6JBHD_7nQ(DownloadService downloadService) {
        this.f$0 = downloadService;
    }

    public final void run() {
        this.f$0.queryDownloads();
    }
}
