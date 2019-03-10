package com.google.android.exoplayer2.video;

import com.google.android.exoplayer2.decoder.DecoderCounters;

/* compiled from: lambda */
/* renamed from: com.google.android.exoplayer2.video.-$$Lambda$VideoRendererEventListener$EventDispatcher$qTQ-0WnG_WelRJ9iR8L0OaiS0Go */
public final /* synthetic */ class C0662x62435c5 implements Runnable {
    private final /* synthetic */ VideoRendererEventListener$EventDispatcher f$0;
    private final /* synthetic */ DecoderCounters f$1;

    public /* synthetic */ C0662x62435c5(VideoRendererEventListener$EventDispatcher videoRendererEventListener$EventDispatcher, DecoderCounters decoderCounters) {
        this.f$0 = videoRendererEventListener$EventDispatcher;
        this.f$1 = decoderCounters;
    }

    public final void run() {
        VideoRendererEventListener$EventDispatcher.lambda$disabled$6(this.f$0, this.f$1);
    }
}
