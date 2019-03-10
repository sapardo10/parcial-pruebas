package de.danoeh.antennapod.core.service.playback;

import android.util.Log;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocalPSMP$qifhFeKxO81U0t20oXLXFmR2hyc implements RejectedExecutionHandler {
    public static final /* synthetic */ -$$Lambda$LocalPSMP$qifhFeKxO81U0t20oXLXFmR2hyc INSTANCE = new -$$Lambda$LocalPSMP$qifhFeKxO81U0t20oXLXFmR2hyc();

    private /* synthetic */ -$$Lambda$LocalPSMP$qifhFeKxO81U0t20oXLXFmR2hyc() {
    }

    public final void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
        Log.d(LocalPSMP.TAG, "Rejected execution of runnable");
    }
}
