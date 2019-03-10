package com.google.android.exoplayer2.source;

import com.google.android.exoplayer2.source.MediaSource.MediaPeriodId;

/* compiled from: lambda */
/* renamed from: com.google.android.exoplayer2.source.-$$Lambda$MediaSourceEventListener$EventDispatcher$PV8wmqGm7vRMJNlt--V3zhXfxiE */
public final /* synthetic */ class C0617x9b820e1e implements Runnable {
    private final /* synthetic */ MediaSourceEventListener$EventDispatcher f$0;
    private final /* synthetic */ MediaSourceEventListener f$1;
    private final /* synthetic */ MediaPeriodId f$2;

    public /* synthetic */ C0617x9b820e1e(MediaSourceEventListener$EventDispatcher mediaSourceEventListener$EventDispatcher, MediaSourceEventListener mediaSourceEventListener, MediaPeriodId mediaPeriodId) {
        this.f$0 = mediaSourceEventListener$EventDispatcher;
        this.f$1 = mediaSourceEventListener;
        this.f$2 = mediaPeriodId;
    }

    public final void run() {
        this.f$1.onReadingStarted(this.f$0.windowIndex, this.f$2);
    }
}
