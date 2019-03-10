package de.danoeh.antennapod.core.service.download;

import java.util.concurrent.ThreadFactory;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DownloadService$s9x7FQ3YeZtzrwlH5OZ5glsDOKA implements ThreadFactory {
    public static final /* synthetic */ -$$Lambda$DownloadService$s9x7FQ3YeZtzrwlH5OZ5glsDOKA INSTANCE = new -$$Lambda$DownloadService$s9x7FQ3YeZtzrwlH5OZ5glsDOKA();

    private /* synthetic */ -$$Lambda$DownloadService$s9x7FQ3YeZtzrwlH5OZ5glsDOKA() {
    }

    public final Thread newThread(Runnable runnable) {
        return DownloadService.lambda$onCreate$2(runnable);
    }
}
