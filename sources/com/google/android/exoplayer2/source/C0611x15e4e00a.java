package com.google.android.exoplayer2.source;

import java.io.IOException;

/* compiled from: lambda */
/* renamed from: com.google.android.exoplayer2.source.-$$Lambda$MediaSourceEventListener$EventDispatcher$0X-TAsNqR4TUW1yA_ZD1_p3oT84 */
public final /* synthetic */ class C0611x15e4e00a implements Runnable {
    private final /* synthetic */ MediaSourceEventListener$EventDispatcher f$0;
    private final /* synthetic */ MediaSourceEventListener f$1;
    private final /* synthetic */ MediaSourceEventListener$LoadEventInfo f$2;
    private final /* synthetic */ MediaSourceEventListener$MediaLoadData f$3;
    private final /* synthetic */ IOException f$4;
    private final /* synthetic */ boolean f$5;

    public /* synthetic */ C0611x15e4e00a(MediaSourceEventListener$EventDispatcher mediaSourceEventListener$EventDispatcher, MediaSourceEventListener mediaSourceEventListener, MediaSourceEventListener$LoadEventInfo mediaSourceEventListener$LoadEventInfo, MediaSourceEventListener$MediaLoadData mediaSourceEventListener$MediaLoadData, IOException iOException, boolean z) {
        this.f$0 = mediaSourceEventListener$EventDispatcher;
        this.f$1 = mediaSourceEventListener;
        this.f$2 = mediaSourceEventListener$LoadEventInfo;
        this.f$3 = mediaSourceEventListener$MediaLoadData;
        this.f$4 = iOException;
        this.f$5 = z;
    }

    public final void run() {
        this.f$1.onLoadError(this.f$0.windowIndex, this.f$0.mediaPeriodId, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
