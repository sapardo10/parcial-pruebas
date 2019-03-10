package de.danoeh.antennapod.fragment;

import android.util.Log;
import io.reactivex.functions.Consumer;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DownloadLogFragment$Ug6I8M85V5BKLhKUtLTIJhPjPzY implements Consumer {
    public static final /* synthetic */ -$$Lambda$DownloadLogFragment$Ug6I8M85V5BKLhKUtLTIJhPjPzY INSTANCE = new -$$Lambda$DownloadLogFragment$Ug6I8M85V5BKLhKUtLTIJhPjPzY();

    private /* synthetic */ -$$Lambda$DownloadLogFragment$Ug6I8M85V5BKLhKUtLTIJhPjPzY() {
    }

    public final void accept(Object obj) {
        Log.e(DownloadLogFragment.TAG, Log.getStackTraceString((Throwable) obj));
    }
}
