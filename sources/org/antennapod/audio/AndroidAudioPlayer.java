package org.antennapod.audio;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Build.VERSION;
import android.util.Log;
import java.io.IOException;
import org.antennapod.audio.MediaPlayer.State;

public class AndroidAudioPlayer extends AbstractAudioPlayer {
    private static final String AMP_TAG = "AndroidMediaPlayer";
    MediaPlayer mp;
    private final OnBufferingUpdateListener onBufferingUpdateListener;
    private final OnCompletionListener onCompletionListener;
    private final OnErrorListener onErrorListener;
    private final OnInfoListener onInfoListener;
    private final OnPreparedListener onPreparedListener;
    private final OnSeekCompleteListener onSeekCompleteListener;

    /* renamed from: org.antennapod.audio.AndroidAudioPlayer$1 */
    class C11331 implements OnBufferingUpdateListener {
        C11331() {
        }

        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            if (AndroidAudioPlayer.this.owningMediaPlayer != null) {
                AndroidAudioPlayer.this.owningMediaPlayer.lock.lock();
                try {
                    if (AndroidAudioPlayer.this.owningMediaPlayer.onBufferingUpdateListener != null && AndroidAudioPlayer.this.owningMediaPlayer.mpi == AndroidAudioPlayer.this) {
                        AndroidAudioPlayer.this.owningMediaPlayer.onBufferingUpdateListener.onBufferingUpdate(AndroidAudioPlayer.this.owningMediaPlayer, percent);
                    }
                    AndroidAudioPlayer.this.owningMediaPlayer.lock.unlock();
                } catch (Throwable th) {
                    AndroidAudioPlayer.this.owningMediaPlayer.lock.unlock();
                }
            }
        }
    }

    /* renamed from: org.antennapod.audio.AndroidAudioPlayer$2 */
    class C11342 implements OnCompletionListener {
        C11342() {
        }

        public void onCompletion(MediaPlayer mp) {
            Log.d(AndroidAudioPlayer.AMP_TAG, "onCompletionListener being called");
            if (AndroidAudioPlayer.this.owningMediaPlayer != null) {
                AndroidAudioPlayer.this.owningMediaPlayer.lock.lock();
                try {
                    if (AndroidAudioPlayer.this.owningMediaPlayer.onCompletionListener != null) {
                        AndroidAudioPlayer.this.owningMediaPlayer.onCompletionListener.onCompletion(AndroidAudioPlayer.this.owningMediaPlayer);
                    }
                    AndroidAudioPlayer.this.owningMediaPlayer.lock.unlock();
                } catch (Throwable th) {
                    AndroidAudioPlayer.this.owningMediaPlayer.lock.unlock();
                }
            }
        }
    }

    /* renamed from: org.antennapod.audio.AndroidAudioPlayer$3 */
    class C11353 implements OnErrorListener {
        C11353() {
        }

        public boolean onError(MediaPlayer mp, int what, int extra) {
            if (AndroidAudioPlayer.this.owningMediaPlayer != null) {
                AndroidAudioPlayer.this.owningMediaPlayer.lock.lock();
                try {
                    if (AndroidAudioPlayer.this.owningMediaPlayer.onErrorListener != null) {
                        boolean onError = AndroidAudioPlayer.this.owningMediaPlayer.onErrorListener.onError(AndroidAudioPlayer.this.owningMediaPlayer, what, extra);
                        return onError;
                    }
                    AndroidAudioPlayer.this.owningMediaPlayer.lock.unlock();
                } finally {
                    AndroidAudioPlayer.this.owningMediaPlayer.lock.unlock();
                }
            }
            return false;
        }
    }

    /* renamed from: org.antennapod.audio.AndroidAudioPlayer$4 */
    class C11364 implements OnInfoListener {
        C11364() {
        }

        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            if (AndroidAudioPlayer.this.owningMediaPlayer != null) {
                AndroidAudioPlayer.this.owningMediaPlayer.lock.lock();
                try {
                    if (AndroidAudioPlayer.this.owningMediaPlayer.onInfoListener == null || AndroidAudioPlayer.this.owningMediaPlayer.mpi != AndroidAudioPlayer.this) {
                        AndroidAudioPlayer.this.owningMediaPlayer.lock.unlock();
                    } else {
                        boolean onInfo = AndroidAudioPlayer.this.owningMediaPlayer.onInfoListener.onInfo(AndroidAudioPlayer.this.owningMediaPlayer, what, extra);
                        return onInfo;
                    }
                } finally {
                    AndroidAudioPlayer.this.owningMediaPlayer.lock.unlock();
                }
            }
            return false;
        }
    }

    /* renamed from: org.antennapod.audio.AndroidAudioPlayer$5 */
    class C11375 implements OnPreparedListener {
        C11375() {
        }

        public void onPrepared(MediaPlayer mp) {
            Log.d(AndroidAudioPlayer.AMP_TAG, "Calling onPreparedListener.onPrepared()");
            if (AndroidAudioPlayer.this.owningMediaPlayer != null) {
                AndroidAudioPlayer.this.lockMuteOnPreparedCount.lock();
                try {
                    if (AndroidAudioPlayer.this.muteOnPreparedCount > 0) {
                        AndroidAudioPlayer androidAudioPlayer = AndroidAudioPlayer.this;
                        androidAudioPlayer.muteOnPreparedCount--;
                    } else {
                        AndroidAudioPlayer.this.muteOnPreparedCount = 0;
                        Log.d(AndroidAudioPlayer.AMP_TAG, "Invoking AndroidMediaPlayer.this.owningMediaPlayer.onPreparedListener.onPrepared");
                        AndroidAudioPlayer.this.owningMediaPlayer.onPreparedListener.onPrepared(AndroidAudioPlayer.this.owningMediaPlayer);
                    }
                    AndroidAudioPlayer.this.lockMuteOnPreparedCount.unlock();
                    if (AndroidAudioPlayer.this.owningMediaPlayer.mpi != AndroidAudioPlayer.this) {
                        Log.d(AndroidAudioPlayer.AMP_TAG, "owningMediaPlayer has changed implementation");
                    }
                } catch (Throwable th) {
                    AndroidAudioPlayer.this.lockMuteOnPreparedCount.unlock();
                }
            }
        }
    }

    /* renamed from: org.antennapod.audio.AndroidAudioPlayer$6 */
    class C11386 implements OnSeekCompleteListener {
        C11386() {
        }

        public void onSeekComplete(MediaPlayer mp) {
            if (AndroidAudioPlayer.this.owningMediaPlayer != null) {
                AndroidAudioPlayer.this.owningMediaPlayer.lock.lock();
                try {
                    AndroidAudioPlayer.this.lockMuteOnSeekCount.lock();
                    if (AndroidAudioPlayer.this.muteOnSeekCount > 0) {
                        AndroidAudioPlayer androidAudioPlayer = AndroidAudioPlayer.this;
                        androidAudioPlayer.muteOnSeekCount--;
                    } else {
                        AndroidAudioPlayer.this.muteOnSeekCount = 0;
                        if (AndroidAudioPlayer.this.owningMediaPlayer.onSeekCompleteListener != null) {
                            AndroidAudioPlayer.this.owningMediaPlayer.onSeekCompleteListener.onSeekComplete(AndroidAudioPlayer.this.owningMediaPlayer);
                        }
                    }
                    AndroidAudioPlayer.this.lockMuteOnSeekCount.unlock();
                    AndroidAudioPlayer.this.owningMediaPlayer.lock.unlock();
                } catch (Throwable th) {
                    AndroidAudioPlayer.this.owningMediaPlayer.lock.unlock();
                }
            }
        }
    }

    public void reset() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:13:0x002b in {2, 9, 10, 12} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r3 = this;
        r0 = r3.owningMediaPlayer;
        r0 = r0.lock;
        r0.lock();
        r0 = r3.mp;	 Catch:{ IllegalStateException -> 0x0016 }
        r0.reset();	 Catch:{ IllegalStateException -> 0x0016 }
    L_0x000c:
        r0 = r3.owningMediaPlayer;
        r0 = r0.lock;
        r0.unlock();
        goto L_0x0022;
    L_0x0014:
        r0 = move-exception;
        goto L_0x0023;
    L_0x0016:
        r0 = move-exception;
        r1 = "AndroidMediaPlayer";	 Catch:{ all -> 0x0014 }
        r2 = android.util.Log.getStackTraceString(r0);	 Catch:{ all -> 0x0014 }
        android.util.Log.e(r1, r2);	 Catch:{ all -> 0x0014 }
        goto L_0x000c;
    L_0x0022:
        return;
    L_0x0023:
        r1 = r3.owningMediaPlayer;
        r1 = r1.lock;
        r1.unlock();
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.antennapod.audio.AndroidAudioPlayer.reset():void");
    }

    public AndroidAudioPlayer(MediaPlayer owningMediaPlayer, Context context) {
        super(owningMediaPlayer, context);
        this.mp = null;
        this.onBufferingUpdateListener = new C11331();
        this.onCompletionListener = new C11342();
        this.onErrorListener = new C11353();
        this.onInfoListener = new C11364();
        this.onPreparedListener = new C11375();
        this.onSeekCompleteListener = new C11386();
        this.mp = new MediaPlayer();
        MediaPlayer mediaPlayer = this.mp;
        if (mediaPlayer != null) {
            mediaPlayer.setOnBufferingUpdateListener(this.onBufferingUpdateListener);
            this.mp.setOnCompletionListener(this.onCompletionListener);
            this.mp.setOnErrorListener(this.onErrorListener);
            this.mp.setOnInfoListener(this.onInfoListener);
            Log.d(AMP_TAG, "Setting prepared listener to this.onPreparedListener");
            this.mp.setOnPreparedListener(this.onPreparedListener);
            this.mp.setOnSeekCompleteListener(this.onSeekCompleteListener);
            return;
        }
        throw new IllegalStateException("Did not instantiate MediaPlayer successfully");
    }

    public int getAudioSessionId() {
        return this.mp.getAudioSessionId();
    }

    public boolean canSetPitch() {
        return VERSION.SDK_INT >= 23;
    }

    public boolean canSetSpeed() {
        return VERSION.SDK_INT >= 23;
    }

    public float getCurrentPitchStepsAdjustment() {
        return 0.0f;
    }

    public boolean canDownmix() {
        return false;
    }

    public int getCurrentPosition() {
        int i;
        this.owningMediaPlayer.lock.lock();
        int currentPosition;
        try {
            currentPosition = this.mp.getCurrentPosition();
            return currentPosition;
        } catch (IllegalStateException e) {
            currentPosition = e;
            i = -1;
            return i;
        } finally {
            i = this.owningMediaPlayer.lock;
            i.unlock();
        }
    }

    public float getCurrentSpeedMultiplier() {
        return 1.0f;
    }

    public int getDuration() {
        int i;
        this.owningMediaPlayer.lock.lock();
        int duration;
        try {
            duration = this.mp.getDuration();
            return duration;
        } catch (IllegalStateException e) {
            duration = e;
            i = -1;
            return i;
        } finally {
            i = this.owningMediaPlayer.lock;
            i.unlock();
        }
    }

    public float getMaxSpeedMultiplier() {
        return 1.0f;
    }

    public float getMinSpeedMultiplier() {
        return 1.0f;
    }

    public boolean isLooping() {
        boolean z;
        this.owningMediaPlayer.lock.lock();
        boolean isLooping;
        try {
            isLooping = this.mp.isLooping();
            return isLooping;
        } catch (IllegalStateException e) {
            isLooping = e;
            z = false;
            return z;
        } finally {
            z = this.owningMediaPlayer.lock;
            z.unlock();
        }
    }

    public boolean isPlaying() {
        boolean z;
        this.owningMediaPlayer.lock.lock();
        boolean isPlaying;
        try {
            isPlaying = this.mp.isPlaying();
            return isPlaying;
        } catch (IllegalStateException e) {
            isPlaying = e;
            z = false;
            return z;
        } finally {
            z = this.owningMediaPlayer.lock;
            z.unlock();
        }
    }

    public void pause() {
        this.owningMediaPlayer.lock.lock();
        try {
            this.mp.pause();
        } finally {
            this.owningMediaPlayer.lock.unlock();
        }
    }

    public void prepare() throws IllegalStateException, IOException {
        this.owningMediaPlayer.lock.lock();
        Log.d(AMP_TAG, "prepare()");
        try {
            this.mp.prepare();
            Log.d(AMP_TAG, "Finish prepare()");
        } finally {
            this.owningMediaPlayer.lock.unlock();
        }
    }

    public void prepareAsync() {
        this.mp.prepareAsync();
    }

    public void release() {
        this.owningMediaPlayer.lock.lock();
        try {
            if (this.mp != null) {
                Log.d(AMP_TAG, "mp.release()");
                this.mp.release();
            }
        } catch (IllegalStateException e) {
        } catch (Throwable th) {
            this.owningMediaPlayer.lock.unlock();
        }
        this.owningMediaPlayer.lock.unlock();
    }

    public void seekTo(int msec) throws IllegalStateException {
        this.owningMediaPlayer.lock.lock();
        try {
            this.mp.setOnSeekCompleteListener(this.onSeekCompleteListener);
            this.mp.seekTo(msec);
        } finally {
            this.owningMediaPlayer.lock.unlock();
        }
    }

    public void setAudioStreamType(int streamtype) {
        this.owningMediaPlayer.lock.lock();
        try {
            this.mp.setAudioStreamType(streamtype);
        } finally {
            this.owningMediaPlayer.lock.unlock();
        }
    }

    public void setDataSource(Context context, Uri uri) throws IllegalArgumentException, IllegalStateException, IOException {
        this.owningMediaPlayer.lock.lock();
        try {
            String str = AMP_TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("setDataSource(context, ");
            stringBuilder.append(uri.toString());
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
            this.mp.setDataSource(context, uri);
        } finally {
            this.owningMediaPlayer.lock.unlock();
        }
    }

    public void setDataSource(String path) throws IllegalArgumentException, IllegalStateException, IOException {
        this.owningMediaPlayer.lock.lock();
        try {
            String str = AMP_TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("setDataSource(");
            stringBuilder.append(path);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
            this.mp.setDataSource(path);
        } finally {
            this.owningMediaPlayer.lock.unlock();
        }
    }

    public void setEnableSpeedAdjustment(boolean enableSpeedAdjustment) {
    }

    public void setLooping(boolean loop) {
        this.owningMediaPlayer.lock.lock();
        try {
            this.mp.setLooping(loop);
        } finally {
            this.owningMediaPlayer.lock.unlock();
        }
    }

    public void setPitchStepsAdjustment(float pitchSteps) {
        if (VERSION.SDK_INT >= 23) {
            PlaybackParams params = this.mp.getPlaybackParams();
            params.setPitch(params.getPitch() + pitchSteps);
            this.mp.setPlaybackParams(params);
        }
    }

    public void setPlaybackPitch(float f) {
        String str = AMP_TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("setPlaybackPitch(");
        stringBuilder.append(f);
        stringBuilder.append(")");
        Log.d(str, stringBuilder.toString());
        if (VERSION.SDK_INT >= 23) {
            PlaybackParams params = this.mp.getPlaybackParams();
            params.setPitch(f);
            this.mp.setPlaybackParams(params);
        }
    }

    public void setPlaybackSpeed(float f) {
        String str = AMP_TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("setPlaybackSpeed(");
        stringBuilder.append(f);
        stringBuilder.append(")");
        Log.d(str, stringBuilder.toString());
        if (VERSION.SDK_INT >= 23) {
            PlaybackParams params = this.mp.getPlaybackParams();
            params.setSpeed(f);
            boolean isPaused = this.owningMediaPlayer.state == State.PAUSED;
            this.mp.setPlaybackParams(params);
            if (isPaused) {
                this.mp.pause();
            }
        }
    }

    public void setDownmix(boolean enable) {
    }

    public void setVolume(float leftVolume, float rightVolume) {
        this.owningMediaPlayer.lock.lock();
        try {
            this.mp.setVolume(leftVolume, rightVolume);
        } finally {
            this.owningMediaPlayer.lock.unlock();
        }
    }

    public void setWakeMode(Context context, int mode) {
        this.owningMediaPlayer.lock.lock();
        if (mode != 0) {
            try {
                this.mp.setWakeMode(context, mode);
            } catch (Throwable th) {
                this.owningMediaPlayer.lock.unlock();
            }
        }
        this.owningMediaPlayer.lock.unlock();
    }

    public void start() {
        this.owningMediaPlayer.lock.lock();
        try {
            this.mp.start();
        } finally {
            this.owningMediaPlayer.lock.unlock();
        }
    }

    public void stop() {
        this.owningMediaPlayer.lock.lock();
        try {
            this.mp.stop();
        } finally {
            this.owningMediaPlayer.lock.unlock();
        }
    }
}
