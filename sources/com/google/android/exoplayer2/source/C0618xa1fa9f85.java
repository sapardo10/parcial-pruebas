package com.google.android.exoplayer2.source;

/* compiled from: lambda */
/* renamed from: com.google.android.exoplayer2.source.-$$Lambda$MediaSourceEventListener$EventDispatcher$WQKVpIh5ilpOizOGmbnyUThugMU */
public final /* synthetic */ class C0618xa1fa9f85 implements Runnable {
    private final /* synthetic */ MediaSourceEventListener$EventDispatcher f$0;
    private final /* synthetic */ MediaSourceEventListener f$1;
    private final /* synthetic */ MediaSourceEventListener$LoadEventInfo f$2;
    private final /* synthetic */ MediaSourceEventListener$MediaLoadData f$3;

    public /* synthetic */ C0618xa1fa9f85(MediaSourceEventListener$EventDispatcher mediaSourceEventListener$EventDispatcher, MediaSourceEventListener mediaSourceEventListener, MediaSourceEventListener$LoadEventInfo mediaSourceEventListener$LoadEventInfo, MediaSourceEventListener$MediaLoadData mediaSourceEventListener$MediaLoadData) {
        this.f$0 = mediaSourceEventListener$EventDispatcher;
        this.f$1 = mediaSourceEventListener;
        this.f$2 = mediaSourceEventListener$LoadEventInfo;
        this.f$3 = mediaSourceEventListener$MediaLoadData;
    }

    public final void run() {
        this.f$1.onLoadStarted(this.f$0.windowIndex, this.f$0.mediaPeriodId, this.f$2, this.f$3);
    }
}
