package com.google.android.exoplayer2;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.util.Assertions;

public final class PlaybackParameters {
    public static final PlaybackParameters DEFAULT = new PlaybackParameters(1.0f);
    public final float pitch;
    private final int scaledUsPerMs;
    public final boolean skipSilence;
    public final float speed;

    public PlaybackParameters(float speed) {
        this(speed, 1.0f, false);
    }

    public PlaybackParameters(float speed, float pitch) {
        this(speed, pitch, false);
    }

    public PlaybackParameters(float speed, float pitch, boolean skipSilence) {
        boolean z = true;
        Assertions.checkArgument(speed > 0.0f);
        if (pitch <= 0.0f) {
            z = false;
        }
        Assertions.checkArgument(z);
        this.speed = speed;
        this.pitch = pitch;
        this.skipSilence = skipSilence;
        this.scaledUsPerMs = Math.round(1000.0f * speed);
    }

    public long getMediaTimeUsForPlayoutTimeMs(long timeMs) {
        return ((long) this.scaledUsPerMs) * timeMs;
    }

    public boolean equals(@Nullable Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                PlaybackParameters other = (PlaybackParameters) obj;
                if (this.speed != other.speed || this.pitch != other.pitch || this.skipSilence != other.skipSilence) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }

    public int hashCode() {
        return (((((17 * 31) + Float.floatToRawIntBits(this.speed)) * 31) + Float.floatToRawIntBits(this.pitch)) * 31) + this.skipSilence;
    }
}
