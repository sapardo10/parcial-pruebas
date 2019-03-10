package org.antennapod.audio;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractAudioPlayer {
    private static final String MPI_TAG = "AbstractMediaPlayer";
    protected final ReentrantLock lockMuteOnPreparedCount = new ReentrantLock();
    protected final ReentrantLock lockMuteOnSeekCount = new ReentrantLock();
    protected final Context mContext;
    protected int muteOnPreparedCount = 0;
    protected int muteOnSeekCount = 0;
    protected final MediaPlayer owningMediaPlayer;

    public abstract boolean canDownmix();

    public abstract boolean canSetPitch();

    public abstract boolean canSetSpeed();

    public abstract int getAudioSessionId();

    public abstract float getCurrentPitchStepsAdjustment();

    public abstract int getCurrentPosition();

    public abstract float getCurrentSpeedMultiplier();

    public abstract int getDuration();

    public abstract float getMaxSpeedMultiplier();

    public abstract float getMinSpeedMultiplier();

    public abstract boolean isLooping();

    public abstract boolean isPlaying();

    public abstract void pause();

    public abstract void prepare() throws IllegalStateException, IOException;

    public abstract void prepareAsync();

    public abstract void release();

    public abstract void reset();

    public abstract void seekTo(int i) throws IllegalStateException;

    public abstract void setAudioStreamType(int i);

    public abstract void setDataSource(Context context, Uri uri) throws IllegalArgumentException, IllegalStateException, IOException;

    public abstract void setDataSource(String str) throws IllegalArgumentException, IllegalStateException, IOException;

    public abstract void setDownmix(boolean z);

    public abstract void setEnableSpeedAdjustment(boolean z);

    public abstract void setLooping(boolean z);

    public abstract void setPitchStepsAdjustment(float f);

    public abstract void setPlaybackPitch(float f);

    public abstract void setPlaybackSpeed(float f);

    public abstract void setVolume(float f, float f2);

    public abstract void setWakeMode(Context context, int i);

    public abstract void start();

    public abstract void stop();

    public AbstractAudioPlayer(MediaPlayer owningMediaPlayer, Context context) {
        this.owningMediaPlayer = owningMediaPlayer;
        this.mContext = context;
    }

    public void muteNextOnPrepare() {
        this.lockMuteOnPreparedCount.lock();
        Log.d(MPI_TAG, "muteNextOnPrepare()");
        try {
            this.muteOnPreparedCount++;
        } finally {
            this.lockMuteOnPreparedCount.unlock();
        }
    }

    public void muteNextSeek() {
        this.lockMuteOnSeekCount.lock();
        Log.d(MPI_TAG, "muteNextOnSeek()");
        try {
            this.muteOnSeekCount++;
        } finally {
            this.lockMuteOnSeekCount.unlock();
        }
    }
}
