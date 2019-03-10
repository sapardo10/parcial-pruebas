package com.google.android.exoplayer2.audio;

public interface AudioListener {

    public final /* synthetic */ class -CC {
        public static void $default$onAudioSessionId(AudioListener -this, int audioSessionId) {
        }

        public static void $default$onAudioAttributesChanged(AudioListener -this, AudioAttributes audioAttributes) {
        }

        public static void $default$onVolumeChanged(AudioListener -this, float volume) {
        }
    }

    void onAudioAttributesChanged(AudioAttributes audioAttributes);

    void onAudioSessionId(int i);

    void onVolumeChanged(float f);
}
