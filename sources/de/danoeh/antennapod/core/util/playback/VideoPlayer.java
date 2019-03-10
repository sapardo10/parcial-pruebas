package de.danoeh.antennapod.core.util.playback;

import android.media.MediaPlayer;
import android.util.Log;

public class VideoPlayer extends MediaPlayer implements IPlayer {
    private static final String TAG = "VideoPlayer";

    public boolean canSetSpeed() {
        return false;
    }

    public boolean canDownmix() {
        return false;
    }

    public float getCurrentSpeedMultiplier() {
        return 1.0f;
    }

    public void setPlaybackParams(float speed, boolean skipSilence) {
    }

    public void setDownmix(boolean b) {
        Log.e(TAG, "Setting downmix unsupported in video player");
        throw new UnsupportedOperationException("Setting downmix unsupported in video player");
    }

    public void setVideoScalingMode(int mode) {
        super.setVideoScalingMode(mode);
    }
}
