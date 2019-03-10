package de.danoeh.antennapod.core.service.download;

import java.util.concurrent.ThreadFactory;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DownloadService$pl2GseLBavFoV_kmkDm43MxRZqM implements ThreadFactory {
    public static final /* synthetic */ -$$Lambda$DownloadService$pl2GseLBavFoV_kmkDm43MxRZqM INSTANCE = new -$$Lambda$DownloadService$pl2GseLBavFoV_kmkDm43MxRZqM();

    private /* synthetic */ -$$Lambda$DownloadService$pl2GseLBavFoV_kmkDm43MxRZqM() {
    }

    public final Thread newThread(Runnable runnable) {
        return DownloadService.lambda$onCreate$1(runnable);
    }
}
