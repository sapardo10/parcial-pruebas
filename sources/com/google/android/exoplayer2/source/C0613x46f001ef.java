package com.google.android.exoplayer2.source;

import com.google.android.exoplayer2.source.MediaSource.MediaPeriodId;

/* compiled from: lambda */
/* renamed from: com.google.android.exoplayer2.source.-$$Lambda$MediaSourceEventListener$EventDispatcher$BtPa14lQQTv1oUeMy_9QaCysWHY */
public final /* synthetic */ class C0613x46f001ef implements Runnable {
    private final /* synthetic */ MediaSourceEventListener$EventDispatcher f$0;
    private final /* synthetic */ MediaSourceEventListener f$1;
    private final /* synthetic */ MediaPeriodId f$2;
    private final /* synthetic */ MediaSourceEventListener$MediaLoadData f$3;

    public /* synthetic */ C0613x46f001ef(MediaSourceEventListener$EventDispatcher mediaSourceEventListener$EventDispatcher, MediaSourceEventListener mediaSourceEventListener, MediaPeriodId mediaPeriodId, MediaSourceEventListener$MediaLoadData mediaSourceEventListener$MediaLoadData) {
        this.f$0 = mediaSourceEventListener$EventDispatcher;
        this.f$1 = mediaSourceEventListener;
        this.f$2 = mediaPeriodId;
        this.f$3 = mediaSourceEventListener$MediaLoadData;
    }

    public final void run() {
        this.f$1.onUpstreamDiscarded(this.f$0.windowIndex, this.f$2, this.f$3);
    }
}
