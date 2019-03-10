package de.danoeh.antennapod.core.service.playback;

import de.danoeh.antennapod.core.util.playback.Playable;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocalPSMP$MFmVXLqMp6QtNlI_0S86qHVdTJQ implements Runnable {
    private final /* synthetic */ LocalPSMP f$0;
    private final /* synthetic */ Playable f$1;

    public /* synthetic */ -$$Lambda$LocalPSMP$MFmVXLqMp6QtNlI_0S86qHVdTJQ(LocalPSMP localPSMP, Playable playable) {
        this.f$0 = localPSMP;
        this.f$1 = playable;
    }

    public final void run() {
        this.f$0.callback.onPostPlayback(this.f$1, false, false, true);
    }
}
