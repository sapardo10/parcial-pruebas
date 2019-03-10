package de.danoeh.antennapod.core.service.playback;

import de.danoeh.antennapod.core.util.playback.Playable;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PlaybackServiceTaskManager$c09cOz04xoeyeRCAarj9igH4RA4 implements Runnable {
    private final /* synthetic */ PlaybackServiceTaskManager f$0;
    private final /* synthetic */ Playable f$1;

    public /* synthetic */ -$$Lambda$PlaybackServiceTaskManager$c09cOz04xoeyeRCAarj9igH4RA4(PlaybackServiceTaskManager playbackServiceTaskManager, Playable playable) {
        this.f$0 = playbackServiceTaskManager;
        this.f$1 = playable;
    }

    public final void run() {
        PlaybackServiceTaskManager.lambda$startChapterLoader$1(this.f$0, this.f$1);
    }
}
