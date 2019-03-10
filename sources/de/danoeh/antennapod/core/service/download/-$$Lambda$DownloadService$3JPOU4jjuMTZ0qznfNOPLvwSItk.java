package de.danoeh.antennapod.core.service.download;

import android.util.Log;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DownloadService$3JPOU4jjuMTZ0qznfNOPLvwSItk implements RejectedExecutionHandler {
    public static final /* synthetic */ -$$Lambda$DownloadService$3JPOU4jjuMTZ0qznfNOPLvwSItk INSTANCE = new -$$Lambda$DownloadService$3JPOU4jjuMTZ0qznfNOPLvwSItk();

    private /* synthetic */ -$$Lambda$DownloadService$3JPOU4jjuMTZ0qznfNOPLvwSItk() {
    }

    public final void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
        Log.w(DownloadService.TAG, "SchedEx rejected submission of new task");
    }
}
