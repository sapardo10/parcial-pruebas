package com.google.android.exoplayer2.source;

import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource.SourceInfoRefreshListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$CompositeMediaSource$ahAPO18YbnzL6kKRAWdp4FR_Vco implements SourceInfoRefreshListener {
    private final /* synthetic */ CompositeMediaSource f$0;
    private final /* synthetic */ Object f$1;

    public /* synthetic */ -$$Lambda$CompositeMediaSource$ahAPO18YbnzL6kKRAWdp4FR_Vco(CompositeMediaSource compositeMediaSource, Object obj) {
        this.f$0 = compositeMediaSource;
        this.f$1 = obj;
    }

    public final void onSourceInfoRefreshed(MediaSource mediaSource, Timeline timeline, Object obj) {
        this.f$0.onChildSourceInfoRefreshed(this.f$1, mediaSource, timeline, obj);
    }
}
