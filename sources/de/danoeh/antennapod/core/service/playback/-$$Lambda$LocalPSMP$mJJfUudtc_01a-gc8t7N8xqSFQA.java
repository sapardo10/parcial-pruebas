package de.danoeh.antennapod.core.service.playback;

import de.danoeh.antennapod.core.util.playback.Playable;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocalPSMP$mJJfUudtc_01a-gc8t7N8xqSFQA implements Runnable {
    private final /* synthetic */ LocalPSMP f$0;
    private final /* synthetic */ Playable f$1;
    private final /* synthetic */ boolean f$2;
    private final /* synthetic */ boolean f$3;
    private final /* synthetic */ boolean f$4;

    public /* synthetic */ -$$Lambda$LocalPSMP$mJJfUudtc_01a-gc8t7N8xqSFQA(LocalPSMP localPSMP, Playable playable, boolean z, boolean z2, boolean z3) {
        this.f$0 = localPSMP;
        this.f$1 = playable;
        this.f$2 = z;
        this.f$3 = z2;
        this.f$4 = z3;
    }

    public final void run() {
        this.f$0.callback.onPostPlayback(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
