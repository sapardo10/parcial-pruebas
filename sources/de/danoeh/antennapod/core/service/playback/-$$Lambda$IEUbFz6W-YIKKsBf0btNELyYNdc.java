package de.danoeh.antennapod.core.service.playback;

import de.danoeh.antennapod.core.service.playback.PlaybackServiceTaskManager.PSTMCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$IEUbFz6W-YIKKsBf0btNELyYNdc implements Runnable {
    private final /* synthetic */ PSTMCallback f$0;

    public /* synthetic */ -$$Lambda$IEUbFz6W-YIKKsBf0btNELyYNdc(PSTMCallback pSTMCallback) {
        this.f$0 = pSTMCallback;
    }

    public final void run() {
        this.f$0.onWidgetUpdaterTick();
    }
}
