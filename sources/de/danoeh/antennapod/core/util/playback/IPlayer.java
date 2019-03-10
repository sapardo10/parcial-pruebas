package de.danoeh.antennapod.core.util.playback;

import android.content.Context;
import android.view.SurfaceHolder;
import java.io.IOException;

public interface IPlayer {
    boolean canDownmix();

    boolean canSetSpeed();

    int getCurrentPosition();

    float getCurrentSpeedMultiplier();

    int getDuration();

    boolean isPlaying();

    void pause();

    void prepare() throws IllegalStateException, IOException;

    void release();

    void reset();

    void seekTo(int i);

    void setAudioStreamType(int i);

    void setDataSource(String str) throws IllegalStateException, IOException, IllegalArgumentException, SecurityException;

    void setDisplay(SurfaceHolder surfaceHolder);

    void setDownmix(boolean z);

    void setPlaybackParams(float f, boolean z);

    void setVolume(float f, float f2);

    void setWakeMode(Context context, int i);

    void start();

    void stop();
}
