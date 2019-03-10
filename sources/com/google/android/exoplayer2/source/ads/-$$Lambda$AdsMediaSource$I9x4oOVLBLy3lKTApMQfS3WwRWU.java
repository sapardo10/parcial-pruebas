package com.google.android.exoplayer2.source.ads;

import com.google.android.exoplayer2.ExoPlayer;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AdsMediaSource$I9x4oOVLBLy3lKTApMQfS3WwRWU implements Runnable {
    private final /* synthetic */ AdsMediaSource f$0;
    private final /* synthetic */ ExoPlayer f$1;
    private final /* synthetic */ ComponentListener f$2;

    public /* synthetic */ -$$Lambda$AdsMediaSource$I9x4oOVLBLy3lKTApMQfS3WwRWU(AdsMediaSource adsMediaSource, ExoPlayer exoPlayer, ComponentListener componentListener) {
        this.f$0 = adsMediaSource;
        this.f$1 = exoPlayer;
        this.f$2 = componentListener;
    }

    public final void run() {
        this.f$0.adsLoader.attachPlayer(this.f$1, this.f$2, this.f$0.adUiViewGroup);
    }
}
