package com.google.android.exoplayer2.audio;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.decoder.DecoderCounters;

public interface AudioRendererEventListener {

    public final /* synthetic */ class -CC {
        public static void $default$onAudioEnabled(AudioRendererEventListener -this, DecoderCounters counters) {
        }

        public static void $default$onAudioSessionId(AudioRendererEventListener -this, int audioSessionId) {
        }

        public static void $default$onAudioDecoderInitialized(AudioRendererEventListener -this, String decoderName, long initializedTimestampMs, long initializationDurationMs) {
        }

        public static void $default$onAudioInputFormatChanged(AudioRendererEventListener -this, Format format) {
        }

        public static void $default$onAudioSinkUnderrun(AudioRendererEventListener -this, int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {
        }

        public static void $default$onAudioDisabled(AudioRendererEventListener -this, DecoderCounters counters) {
        }
    }

    void onAudioDecoderInitialized(String str, long j, long j2);

    void onAudioDisabled(DecoderCounters decoderCounters);

    void onAudioEnabled(DecoderCounters decoderCounters);

    void onAudioInputFormatChanged(Format format);

    void onAudioSessionId(int i);

    void onAudioSinkUnderrun(int i, long j, long j2);
}
