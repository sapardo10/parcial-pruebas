package com.google.android.exoplayer2.source.ads;

import com.google.android.exoplayer2.source.ads.AdsMediaSource.AdLoadException;

/* compiled from: lambda */
/* renamed from: com.google.android.exoplayer2.source.ads.-$$Lambda$AdsMediaSource$ComponentListener$M2_fNzk2CLGyu6z6f0MiAf67cQ0 */
public final /* synthetic */ class C0628xb24b6d47 implements Runnable {
    private final /* synthetic */ ComponentListener f$0;
    private final /* synthetic */ AdLoadException f$1;

    public /* synthetic */ C0628xb24b6d47(ComponentListener componentListener, AdLoadException adLoadException) {
        this.f$0 = componentListener;
        this.f$1 = adLoadException;
    }

    public final void run() {
        ComponentListener.lambda$onAdLoadError$3(this.f$0, this.f$1);
    }
}
