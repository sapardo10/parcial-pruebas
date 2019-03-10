package com.google.android.exoplayer2.video;

import com.google.android.exoplayer2.decoder.DecoderCounters;

/* compiled from: lambda */
/* renamed from: com.google.android.exoplayer2.video.-$$Lambda$VideoRendererEventListener$EventDispatcher$Zf6ofdxzBBJ5SL288lE0HglRj8g */
public final /* synthetic */ class C0661xeb53b9bf implements Runnable {
    private final /* synthetic */ VideoRendererEventListener$EventDispatcher f$0;
    private final /* synthetic */ DecoderCounters f$1;

    public /* synthetic */ C0661xeb53b9bf(VideoRendererEventListener$EventDispatcher videoRendererEventListener$EventDispatcher, DecoderCounters decoderCounters) {
        this.f$0 = videoRendererEventListener$EventDispatcher;
        this.f$1 = decoderCounters;
    }

    public final void run() {
        this.f$0.listener.onVideoEnabled(this.f$1);
    }
}
