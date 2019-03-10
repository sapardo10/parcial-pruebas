package com.google.android.exoplayer2.source;

/* compiled from: lambda */
/* renamed from: com.google.android.exoplayer2.source.-$$Lambda$MediaSourceEventListener$EventDispatcher$ES4FdQzWtupQEe6zuV_1M9-f9xU */
public final /* synthetic */ class C0614x6798e946 implements Runnable {
    private final /* synthetic */ MediaSourceEventListener$EventDispatcher f$0;
    private final /* synthetic */ MediaSourceEventListener f$1;
    private final /* synthetic */ MediaSourceEventListener$MediaLoadData f$2;

    public /* synthetic */ C0614x6798e946(MediaSourceEventListener$EventDispatcher mediaSourceEventListener$EventDispatcher, MediaSourceEventListener mediaSourceEventListener, MediaSourceEventListener$MediaLoadData mediaSourceEventListener$MediaLoadData) {
        this.f$0 = mediaSourceEventListener$EventDispatcher;
        this.f$1 = mediaSourceEventListener;
        this.f$2 = mediaSourceEventListener$MediaLoadData;
    }

    public final void run() {
        this.f$1.onDownstreamFormatChanged(this.f$0.windowIndex, this.f$0.mediaPeriodId, this.f$2);
    }
}
