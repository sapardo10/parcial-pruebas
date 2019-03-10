package com.google.android.exoplayer2.audio;

import android.os.Handler;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.util.Assertions;

public final class AudioRendererEventListener$EventDispatcher {
    @Nullable
    private final Handler handler;
    @Nullable
    private final AudioRendererEventListener listener;

    public AudioRendererEventListener$EventDispatcher(@Nullable Handler handler, @Nullable AudioRendererEventListener listener) {
        this.handler = listener != null ? (Handler) Assertions.checkNotNull(handler) : null;
        this.listener = listener;
    }

    public void enabled(DecoderCounters decoderCounters) {
        if (this.listener != null) {
            this.handler.post(new C0563x1953d11f(this, decoderCounters));
        }
    }

    public void decoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {
        if (this.listener != null) {
            this.handler.post(new C0562x951d9860(this, decoderName, initializedTimestampMs, initializationDurationMs));
        }
    }

    public void inputFormatChanged(Format format) {
        if (this.listener != null) {
            this.handler.post(new C0561x2218d907(this, format));
        }
    }

    public void audioTrackUnderrun(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {
        if (this.listener != null) {
            this.handler.post(new C0566x106a4654(this, bufferSize, bufferSizeMs, elapsedSinceLastFeedMs));
        }
    }

    public void disabled(DecoderCounters counters) {
        if (this.listener != null) {
            this.handler.post(new C0565xbffc7ff8(this, counters));
        }
    }

    public static /* synthetic */ void lambda$disabled$4(AudioRendererEventListener$EventDispatcher audioRendererEventListener$EventDispatcher, DecoderCounters counters) {
        counters.ensureUpdated();
        audioRendererEventListener$EventDispatcher.listener.onAudioDisabled(counters);
    }

    public void audioSessionId(int audioSessionId) {
        if (this.listener != null) {
            this.handler.post(new C0564x4f60bcd7(this, audioSessionId));
        }
    }
}
