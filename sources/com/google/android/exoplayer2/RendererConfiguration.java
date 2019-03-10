package com.google.android.exoplayer2;

import android.support.annotation.Nullable;

public final class RendererConfiguration {
    public static final RendererConfiguration DEFAULT = new RendererConfiguration(0);
    public final int tunnelingAudioSessionId;

    public RendererConfiguration(int tunnelingAudioSessionId) {
        this.tunnelingAudioSessionId = tunnelingAudioSessionId;
    }

    public boolean equals(@Nullable Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                if (this.tunnelingAudioSessionId != ((RendererConfiguration) obj).tunnelingAudioSessionId) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }

    public int hashCode() {
        return this.tunnelingAudioSessionId;
    }
}
