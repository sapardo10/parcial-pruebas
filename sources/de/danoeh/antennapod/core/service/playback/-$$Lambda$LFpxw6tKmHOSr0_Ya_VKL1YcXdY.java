package de.danoeh.antennapod.core.service.playback;

import de.danoeh.antennapod.core.service.playback.PlaybackServiceTaskManager.PSTMCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LFpxw6tKmHOSr0_Ya_VKL1YcXdY implements Runnable {
    private final /* synthetic */ PSTMCallback f$0;

    public /* synthetic */ -$$Lambda$LFpxw6tKmHOSr0_Ya_VKL1YcXdY(PSTMCallback pSTMCallback) {
        this.f$0 = pSTMCallback;
    }

    public final void run() {
        this.f$0.positionSaverTick();
    }
}
