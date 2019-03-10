package com.google.android.exoplayer2.video;

import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.Surface;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.util.Assertions;

public final class VideoRendererEventListener$EventDispatcher {
    @Nullable
    private final Handler handler;
    @Nullable
    private final VideoRendererEventListener listener;

    public VideoRendererEventListener$EventDispatcher(@Nullable Handler handler, @Nullable VideoRendererEventListener listener) {
        this.handler = listener != null ? (Handler) Assertions.checkNotNull(handler) : null;
        this.listener = listener;
    }

    public void enabled(DecoderCounters decoderCounters) {
        if (this.listener != null) {
            this.handler.post(new C0661xeb53b9bf(this, decoderCounters));
        }
    }

    public void decoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {
        if (this.listener != null) {
            this.handler.post(new C0660xed443dd9(this, decoderName, initializedTimestampMs, initializationDurationMs));
        }
    }

    public void inputFormatChanged(Format format) {
        if (this.listener != null) {
            this.handler.post(new C0657xd380fd15(this, format));
        }
    }

    public void droppedFrames(int droppedFrameCount, long elapsedMs) {
        if (this.listener != null) {
            this.handler.post(new C0663x89d7f10d(this, droppedFrameCount, elapsedMs));
        }
    }

    public void videoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        if (this.listener != null) {
            this.handler.post(new C0659x3b67d6b6(this, width, height, unappliedRotationDegrees, pixelWidthHeightRatio));
        }
    }

    public void renderedFirstFrame(@Nullable Surface surface) {
        if (this.listener != null) {
            this.handler.post(new C0658x785b3d18(this, surface));
        }
    }

    public void disabled(DecoderCounters counters) {
        if (this.listener != null) {
            this.handler.post(new C0662x62435c5(this, counters));
        }
    }

    public static /* synthetic */ void lambda$disabled$6(VideoRendererEventListener$EventDispatcher videoRendererEventListener$EventDispatcher, DecoderCounters counters) {
        counters.ensureUpdated();
        videoRendererEventListener$EventDispatcher.listener.onVideoDisabled(counters);
    }
}
