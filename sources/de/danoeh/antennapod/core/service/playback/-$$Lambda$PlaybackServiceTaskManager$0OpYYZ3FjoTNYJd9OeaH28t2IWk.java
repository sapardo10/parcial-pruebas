package de.danoeh.antennapod.core.service.playback;

import java.util.concurrent.ThreadFactory;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PlaybackServiceTaskManager$0OpYYZ3FjoTNYJd9OeaH28t2IWk implements ThreadFactory {
    public static final /* synthetic */ -$$Lambda$PlaybackServiceTaskManager$0OpYYZ3FjoTNYJd9OeaH28t2IWk INSTANCE = new -$$Lambda$PlaybackServiceTaskManager$0OpYYZ3FjoTNYJd9OeaH28t2IWk();

    private /* synthetic */ -$$Lambda$PlaybackServiceTaskManager$0OpYYZ3FjoTNYJd9OeaH28t2IWk() {
    }

    public final Thread newThread(Runnable runnable) {
        return PlaybackServiceTaskManager.lambda$new$0(runnable);
    }
}
