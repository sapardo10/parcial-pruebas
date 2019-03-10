package de.danoeh.antennapod.core.util.playback;

import android.util.Log;
import io.reactivex.functions.Consumer;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PlaybackController$J-lLfT_PXhC28QLbvjDr3Dvq8G8 implements Consumer {
    public static final /* synthetic */ -$$Lambda$PlaybackController$J-lLfT_PXhC28QLbvjDr3Dvq8G8 INSTANCE = new -$$Lambda$PlaybackController$J-lLfT_PXhC28QLbvjDr3Dvq8G8();

    private /* synthetic */ -$$Lambda$PlaybackController$J-lLfT_PXhC28QLbvjDr3Dvq8G8() {
    }

    public final void accept(Object obj) {
        Log.e(PlaybackController.TAG, Log.getStackTraceString((Throwable) obj));
    }
}
