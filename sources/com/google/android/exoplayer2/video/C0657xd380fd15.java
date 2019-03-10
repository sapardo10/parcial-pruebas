package com.google.android.exoplayer2.video;

import com.google.android.exoplayer2.Format;

/* compiled from: lambda */
/* renamed from: com.google.android.exoplayer2.video.-$$Lambda$VideoRendererEventListener$EventDispatcher$26y6c6BFFT4OL6bJiMmdsfxDEMQ */
public final /* synthetic */ class C0657xd380fd15 implements Runnable {
    private final /* synthetic */ VideoRendererEventListener$EventDispatcher f$0;
    private final /* synthetic */ Format f$1;

    public /* synthetic */ C0657xd380fd15(VideoRendererEventListener$EventDispatcher videoRendererEventListener$EventDispatcher, Format format) {
        this.f$0 = videoRendererEventListener$EventDispatcher;
        this.f$1 = format;
    }

    public final void run() {
        this.f$0.listener.onVideoInputFormatChanged(this.f$1);
    }
}
