package de.danoeh.antennapod.core.service.download;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DownloadService$vRnSiOcE-0YVy-hsmK2vfPwJcVY implements Runnable {
    private final /* synthetic */ DownloadService f$0;
    private final /* synthetic */ DownloadRequest f$1;

    public /* synthetic */ -$$Lambda$DownloadService$vRnSiOcE-0YVy-hsmK2vfPwJcVY(DownloadService downloadService, DownloadRequest downloadRequest) {
        this.f$0 = downloadService;
        this.f$1 = downloadRequest;
    }

    public final void run() {
        DownloadService.lambda$postAuthenticationNotification$5(this.f$0, this.f$1);
    }
}
