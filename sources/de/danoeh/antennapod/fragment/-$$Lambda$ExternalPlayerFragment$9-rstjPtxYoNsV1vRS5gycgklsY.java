package de.danoeh.antennapod.fragment;

import de.danoeh.antennapod.core.util.playback.Playable;
import io.reactivex.functions.Consumer;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ExternalPlayerFragment$9-rstjPtxYoNsV1vRS5gycgklsY implements Consumer {
    private final /* synthetic */ ExternalPlayerFragment f$0;

    public /* synthetic */ -$$Lambda$ExternalPlayerFragment$9-rstjPtxYoNsV1vRS5gycgklsY(ExternalPlayerFragment externalPlayerFragment) {
        this.f$0 = externalPlayerFragment;
    }

    public final void accept(Object obj) {
        this.f$0.updateUi((Playable) obj);
    }
}
