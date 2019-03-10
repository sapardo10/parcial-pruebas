package com.google.android.exoplayer2.source.smoothstreaming;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SsMediaSource$tFjHmMdOxDkhvkY7QhPdfdPmbtI implements Runnable {
    private final /* synthetic */ SsMediaSource f$0;

    public /* synthetic */ -$$Lambda$SsMediaSource$tFjHmMdOxDkhvkY7QhPdfdPmbtI(SsMediaSource ssMediaSource) {
        this.f$0 = ssMediaSource;
    }

    public final void run() {
        this.f$0.startLoadingManifest();
    }
}
