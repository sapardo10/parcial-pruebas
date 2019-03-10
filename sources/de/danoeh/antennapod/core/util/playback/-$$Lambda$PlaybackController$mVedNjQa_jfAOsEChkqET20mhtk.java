package de.danoeh.antennapod.core.util.playback;

import android.content.Intent;
import io.reactivex.functions.Consumer;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PlaybackController$mVedNjQa_jfAOsEChkqET20mhtk implements Consumer {
    private final /* synthetic */ PlaybackController f$0;

    public /* synthetic */ -$$Lambda$PlaybackController$mVedNjQa_jfAOsEChkqET20mhtk(PlaybackController playbackController) {
        this.f$0 = playbackController;
    }

    public final void accept(Object obj) {
        PlaybackController.lambda$bindToService$2(this.f$0, (Intent) obj);
    }
}
