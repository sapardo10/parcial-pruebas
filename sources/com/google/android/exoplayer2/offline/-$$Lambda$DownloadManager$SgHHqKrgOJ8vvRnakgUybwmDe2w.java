package com.google.android.exoplayer2.offline;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DownloadManager$SgHHqKrgOJ8vvRnakgUybwmDe2w implements Runnable {
    private final /* synthetic */ DownloadManager f$0;
    private final /* synthetic */ DownloadAction[] f$1;

    public /* synthetic */ -$$Lambda$DownloadManager$SgHHqKrgOJ8vvRnakgUybwmDe2w(DownloadManager downloadManager, DownloadAction[] downloadActionArr) {
        this.f$0 = downloadManager;
        this.f$1 = downloadActionArr;
    }

    public final void run() {
        DownloadManager.lambda$saveActions$2(this.f$0, this.f$1);
    }
}
