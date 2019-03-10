package de.danoeh.antennapod.core.util.playback;

import android.util.Log;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PlaybackController$Te0XlQ0sFS7I2AsxFCw--Ouj-iw implements RejectedExecutionHandler {
    public static final /* synthetic */ -$$Lambda$PlaybackController$Te0XlQ0sFS7I2AsxFCw--Ouj-iw INSTANCE = new -$$Lambda$PlaybackController$Te0XlQ0sFS7I2AsxFCw--Ouj-iw();

    private /* synthetic */ -$$Lambda$PlaybackController$Te0XlQ0sFS7I2AsxFCw--Ouj-iw() {
    }

    public final void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
        Log.w(PlaybackController.TAG, "Rejected execution of runnable in schedExecutor");
    }
}
