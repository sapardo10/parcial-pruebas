package de.danoeh.antennapod.core.util.playback;

import java.util.concurrent.ThreadFactory;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PlaybackController$FNxrlRgG33XPy65Ml4ebHKM8RzM implements ThreadFactory {
    public static final /* synthetic */ -$$Lambda$PlaybackController$FNxrlRgG33XPy65Ml4ebHKM8RzM INSTANCE = new -$$Lambda$PlaybackController$FNxrlRgG33XPy65Ml4ebHKM8RzM();

    private /* synthetic */ -$$Lambda$PlaybackController$FNxrlRgG33XPy65Ml4ebHKM8RzM() {
    }

    public final Thread newThread(Runnable runnable) {
        return PlaybackController.lambda$new$0(runnable);
    }
}
