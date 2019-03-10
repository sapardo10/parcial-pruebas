package de.danoeh.antennapod.activity;

import de.danoeh.antennapod.core.service.download.DownloadStatus;
import io.reactivex.functions.Consumer;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$OnlineFeedViewActivity$5ytEz3yQ6VCuzWZ_KDZqgWSjAtk implements Consumer {
    private final /* synthetic */ OnlineFeedViewActivity f$0;

    public /* synthetic */ -$$Lambda$OnlineFeedViewActivity$5ytEz3yQ6VCuzWZ_KDZqgWSjAtk(OnlineFeedViewActivity onlineFeedViewActivity) {
        this.f$0 = onlineFeedViewActivity;
    }

    public final void accept(Object obj) {
        this.f$0.checkDownloadResult((DownloadStatus) obj);
    }
}
