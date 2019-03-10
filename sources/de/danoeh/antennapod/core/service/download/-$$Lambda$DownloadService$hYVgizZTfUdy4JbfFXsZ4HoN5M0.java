package de.danoeh.antennapod.core.service.download;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DownloadService$hYVgizZTfUdy4JbfFXsZ4HoN5M0 implements Runnable {
    private final /* synthetic */ DownloadService f$0;
    private final /* synthetic */ Downloader f$1;

    public /* synthetic */ -$$Lambda$DownloadService$hYVgizZTfUdy4JbfFXsZ4HoN5M0(DownloadService downloadService, Downloader downloader) {
        this.f$0 = downloadService;
        this.f$1 = downloader;
    }

    public final void run() {
        DownloadService.lambda$removeDownload$4(this.f$0, this.f$1);
    }
}
