package de.danoeh.antennapod.core.service.download;

import java.util.List;

/* compiled from: lambda */
/* renamed from: de.danoeh.antennapod.core.service.download.-$$Lambda$DownloadService$FeedSyncThread$X-X1agSBhZETgjS27QIHxF8YMNo */
public final /* synthetic */ class C0745x5923bf26 implements Runnable {
    private final /* synthetic */ FeedSyncThread f$0;
    private final /* synthetic */ List f$1;

    public /* synthetic */ C0745x5923bf26(FeedSyncThread feedSyncThread, List list) {
        this.f$0 = feedSyncThread;
        this.f$1 = list;
    }

    public final void run() {
        FeedSyncThread.lambda$run$0(this.f$0, this.f$1);
    }
}
