package com.google.android.exoplayer2.audio;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import java.util.Arrays;

@TargetApi(21)
public final class AudioCapabilities {
    public static final AudioCapabilities DEFAULT_AUDIO_CAPABILITIES = new AudioCapabilities(new int[]{2}, 8);
    private static final int DEFAULT_MAX_CHANNEL_COUNT = 8;
    private final int maxChannelCount;
    private final int[] supportedEncodings;

    public static AudioCapabilities getCapabilities(Context context) {
        return getCapabilities(context.registerReceiver(null, new IntentFilter("android.media.action.HDMI_AUDIO_PLUG")));
    }

    @SuppressLint({"InlinedApi"})
    static AudioCapabilities getCapabilities(@Nullable Intent intent) {
        if (intent != null) {
            if (intent.getIntExtra("android.media.extra.AUDIO_PLUG_STATE", 0) != 0) {
                return new AudioCapabilities(intent.getIntArrayExtra("android.media.extra.ENCODINGS"), intent.getIntExtra("android.media.extra.MAX_CHANNEL_COUNT", 8));
            }
        }
        return DEFAULT_AUDIO_CAPABILITIES;
    }

    public AudioCapabilities(@Nullable int[] supportedEncodings, int maxChannelCount) {
        if (supportedEncodings != null) {
            this.supportedEncodings = Arrays.copyOf(supportedEncodings, supportedEncodings.length);
            Arrays.sort(this.supportedEncodings);
        } else {
            this.supportedEncodings = new int[0];
        }
        this.maxChannelCount = maxChannelCount;
    }

    public boolean supportsEncoding(int encoding) {
        return Arrays.binarySearch(this.supportedEncodings, encoding) >= 0;
    }

    public int getMaxChannelCount() {
        return this.maxChannelCount;
    }

    public boolean equals(@Nullable Object other) {
        boolean z = true;
        if (this == other) {
            return true;
        }
        if (!(other instanceof AudioCapabilities)) {
            return false;
        }
        AudioCapabilities audioCapabilities = (AudioCapabilities) other;
        if (!Arrays.equals(this.supportedEncodings, audioCapabilities.supportedEncodings) || this.maxChannelCount != audioCapabilities.maxChannelCount) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        return this.maxChannelCount + (Arrays.hashCode(this.supportedEncodings) * 31);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("AudioCapabilities[maxChannelCount=");
        stringBuilder.append(this.maxChannelCount);
        stringBuilder.append(", supportedEncodings=");
        stringBuilder.append(Arrays.toString(this.supportedEncodings));
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
