package de.danoeh.antennapod.activity;

import de.danoeh.antennapod.core.service.download.DownloadRequest;
import java.util.concurrent.Callable;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$OnlineFeedViewActivity$ctXKxnY_nxnkAzy9sK3azvlhqZ8 implements Callable {
    private final /* synthetic */ OnlineFeedViewActivity f$0;
    private final /* synthetic */ DownloadRequest f$1;

    public /* synthetic */ -$$Lambda$OnlineFeedViewActivity$ctXKxnY_nxnkAzy9sK3azvlhqZ8(OnlineFeedViewActivity onlineFeedViewActivity, DownloadRequest downloadRequest) {
        this.f$0 = onlineFeedViewActivity;
        this.f$1 = downloadRequest;
    }

    public final Object call() {
        return OnlineFeedViewActivity.lambda$startFeedDownload$1(this.f$0, this.f$1);
    }
}
