package de.danoeh.antennapod.core.service.download;

import java.util.concurrent.ThreadFactory;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DownloadService$tz7yEQIu3iJxCJX3QuhbO0OuPGk implements ThreadFactory {
    public static final /* synthetic */ -$$Lambda$DownloadService$tz7yEQIu3iJxCJX3QuhbO0OuPGk INSTANCE = new -$$Lambda$DownloadService$tz7yEQIu3iJxCJX3QuhbO0OuPGk();

    private /* synthetic */ -$$Lambda$DownloadService$tz7yEQIu3iJxCJX3QuhbO0OuPGk() {
    }

    public final Thread newThread(Runnable runnable) {
        return DownloadService.lambda$onCreate$0(runnable);
    }
}
