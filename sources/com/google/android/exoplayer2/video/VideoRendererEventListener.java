package com.google.android.exoplayer2.video;

import android.support.annotation.Nullable;
import android.view.Surface;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.decoder.DecoderCounters;

public interface VideoRendererEventListener {

    public final /* synthetic */ class -CC {
        public static void $default$onVideoEnabled(VideoRendererEventListener -this, DecoderCounters counters) {
        }

        public static void $default$onVideoDecoderInitialized(VideoRendererEventListener -this, String decoderName, long initializedTimestampMs, long initializationDurationMs) {
        }

        public static void $default$onVideoInputFormatChanged(VideoRendererEventListener -this, Format format) {
        }

        public static void $default$onDroppedFrames(VideoRendererEventListener -this, int count, long elapsedMs) {
        }

        public static void $default$onVideoSizeChanged(VideoRendererEventListener -this, int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        }

        public static void $default$onRenderedFirstFrame(@Nullable VideoRendererEventListener -this, Surface surface) {
        }

        public static void $default$onVideoDisabled(VideoRendererEventListener -this, DecoderCounters counters) {
        }
    }

    void onDroppedFrames(int i, long j);

    void onRenderedFirstFrame(@Nullable Surface surface);

    void onVideoDecoderInitialized(String str, long j, long j2);

    void onVideoDisabled(DecoderCounters decoderCounters);

    void onVideoEnabled(DecoderCounters decoderCounters);

    void onVideoInputFormatChanged(Format format);

    void onVideoSizeChanged(int i, int i2, int i3, float f);
}
