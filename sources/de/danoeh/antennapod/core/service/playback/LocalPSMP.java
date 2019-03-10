package de.danoeh.antennapod.core.service.playback;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest.Builder;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Pair;
import android.view.SurfaceHolder;
import com.google.android.exoplayer2.util.MimeTypes;
import de.danoeh.antennapod.core.feed.MediaType;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.service.playback.PlaybackServiceMediaPlayer.PSMPCallback;
import de.danoeh.antennapod.core.util.RewindAfterPauseUtils;
import de.danoeh.antennapod.core.util.playback.AudioPlayer;
import de.danoeh.antennapod.core.util.playback.IPlayer;
import de.danoeh.antennapod.core.util.playback.Playable;
import de.danoeh.antennapod.core.util.playback.VideoPlayer;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import org.antennapod.audio.MediaPlayer.OnBufferingUpdateListener;
import org.antennapod.audio.MediaPlayer.OnCompletionListener;
import org.antennapod.audio.MediaPlayer.OnErrorListener;
import org.antennapod.audio.MediaPlayer.OnInfoListener;
import org.antennapod.audio.MediaPlayer.OnSeekCompleteListener;
import org.antennapod.audio.MediaPlayer.OnSpeedAdjustmentAvailableChangedListener;
import org.apache.commons.lang3.StringUtils;

public class LocalPSMP extends PlaybackServiceMediaPlayer {
    private static final String TAG = "LclPlaybackSvcMPlayer";
    private final OnBufferingUpdateListener audioBufferingUpdateListener = new -$$Lambda$LocalPSMP$XOdlrR7byYY7JsG-u7XKqT3JP9w();
    private final OnCompletionListener audioCompletionListener = new -$$Lambda$LocalPSMP$p_TOTFdKxkrPqgwevRM_wdhMKew();
    private final OnErrorListener audioErrorListener = new -$$Lambda$LocalPSMP$adQikeYyVzVQogfsntepKgfVU7E();
    private final OnAudioFocusChangeListener audioFocusChangeListener = new C07501();
    private final OnInfoListener audioInfoListener = new -$$Lambda$LocalPSMP$dUL1B5dMKdXZmJOkWpvIIZ6vpN0();
    private final AudioManager audioManager;
    private final OnSeekCompleteListener audioSeekCompleteListener = new -$$Lambda$LocalPSMP$pIP2fYSS5cCg2ky6sVLlFFEQ1Fs();
    private final OnSpeedAdjustmentAvailableChangedListener audioSetSpeedAbilityListener = new -$$Lambda$LocalPSMP$xNHo6PbNpLlMjMJXXuLzOUMBPKQ();
    private final ThreadPoolExecutor executor;
    private volatile Playable media;
    private volatile IPlayer mediaPlayer;
    private volatile MediaType mediaType;
    private volatile boolean pausedBecauseOfTransientAudiofocusLoss;
    private final ReentrantLock playerLock;
    private CountDownLatch seekLatch;
    private final AtomicBoolean startWhenPrepared;
    private volatile PlayerStatus statusBeforeSeeking;
    private volatile boolean stream;
    private final MediaPlayer.OnBufferingUpdateListener videoBufferingUpdateListener = new -$$Lambda$LocalPSMP$DwAEXk9voH906TKdO-wCDjiqMGw();
    private final MediaPlayer.OnCompletionListener videoCompletionListener = new -$$Lambda$LocalPSMP$RFboA3aLMXI6EAWVLRMU6Hw75sI();
    private final MediaPlayer.OnErrorListener videoErrorListener = new -$$Lambda$LocalPSMP$PM_fsUTqYy2sbLSWYUXJwsTRIZU();
    private final MediaPlayer.OnInfoListener videoInfoListener = new -$$Lambda$LocalPSMP$cM0P9c4Hh_2VsQXu9JcDL-bDRYk();
    private final MediaPlayer.OnSeekCompleteListener videoSeekCompleteListener = new -$$Lambda$LocalPSMP$ns3KvBN--kkivgkb4iSPDI_Rou0();
    private volatile Pair<Integer, Integer> videoSize;

    /* renamed from: de.danoeh.antennapod.core.service.playback.LocalPSMP$1 */
    class C07501 implements OnAudioFocusChangeListener {
        C07501() {
        }

        public void onAudioFocusChange(int focusChange) {
            LocalPSMP.this.executor.submit(new -$$Lambda$LocalPSMP$1$bWQm-SvHzg-23xcSmeXJeDjoCno(this, focusChange));
        }

        public static /* synthetic */ void lambda$onAudioFocusChange$0(C07501 c07501, int focusChange) {
            LocalPSMP.this.playerLock.lock();
            TelephonyManager tm = (TelephonyManager) LocalPSMP.this.context.getSystemService("phone");
            int callState = tm != null ? tm.getCallState() : 0;
            String str = LocalPSMP.TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Call state:");
            stringBuilder.append(callState);
            Log.i(str, stringBuilder.toString());
            if (focusChange != -1) {
                if (UserPreferences.shouldResumeAfterCall() || callState == 0) {
                    if (focusChange == 1) {
                        Log.d(LocalPSMP.TAG, "Gained audio focus");
                        if (LocalPSMP.this.pausedBecauseOfTransientAudiofocusLoss) {
                            LocalPSMP.this.resume();
                        } else {
                            LocalPSMP.this.setVolumeSync(UserPreferences.getLeftVolume(), UserPreferences.getRightVolume());
                        }
                    } else if (focusChange == -3) {
                        if (LocalPSMP.this.playerStatus == PlayerStatus.PLAYING) {
                            if (UserPreferences.shouldPauseForFocusLoss()) {
                                Log.d(LocalPSMP.TAG, "Lost audio focus temporarily. Could duck, but won't, pausing...");
                                LocalPSMP.this.pause(false, false);
                                LocalPSMP.this.pausedBecauseOfTransientAudiofocusLoss = true;
                            } else {
                                Log.d(LocalPSMP.TAG, "Lost audio focus temporarily. Ducking...");
                                LocalPSMP.this.setVolumeSync(UserPreferences.getLeftVolume() * 0.25f, UserPreferences.getRightVolume() * 0.25f);
                                LocalPSMP.this.pausedBecauseOfTransientAudiofocusLoss = false;
                            }
                        }
                    } else if (focusChange == -2) {
                        if (LocalPSMP.this.playerStatus == PlayerStatus.PLAYING) {
                            Log.d(LocalPSMP.TAG, "Lost audio focus temporarily. Pausing...");
                            LocalPSMP.this.pause(false, false);
                            LocalPSMP.this.pausedBecauseOfTransientAudiofocusLoss = true;
                        }
                    }
                    LocalPSMP.this.playerLock.unlock();
                }
            }
            Log.d(LocalPSMP.TAG, "Lost audio focus");
            LocalPSMP.this.pause(true, false);
            LocalPSMP.this.callback.shouldStop();
            LocalPSMP.this.playerLock.unlock();
        }
    }

    public LocalPSMP(@NonNull Context context, @NonNull PSMPCallback callback) {
        super(context, callback);
        this.audioManager = (AudioManager) context.getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        this.playerLock = new ReentrantLock();
        this.startWhenPrepared = new AtomicBoolean(false);
        this.executor = new ThreadPoolExecutor(1, 1, 5, TimeUnit.MINUTES, new LinkedBlockingDeque(), -$$Lambda$LocalPSMP$qifhFeKxO81U0t20oXLXFmR2hyc.INSTANCE);
        this.mediaPlayer = null;
        this.statusBeforeSeeking = null;
        this.pausedBecauseOfTransientAudiofocusLoss = false;
        this.mediaType = MediaType.UNKNOWN;
        this.videoSize = null;
    }

    public void playMediaObject(@NonNull Playable playable, boolean stream, boolean startWhenPrepared, boolean prepareImmediately) {
        Log.d(TAG, "playMediaObject(...)");
        this.executor.submit(new -$$Lambda$LocalPSMP$5kNdyipM_M6Bo3d8le_FUz75a90(this, playable, stream, startWhenPrepared, prepareImmediately));
    }

    public static /* synthetic */ void lambda$playMediaObject$1(@NonNull LocalPSMP localPSMP, Playable playable, boolean stream, boolean startWhenPrepared, boolean prepareImmediately) {
        localPSMP.playerLock.lock();
        try {
            localPSMP.playMediaObject(playable, false, stream, startWhenPrepared, prepareImmediately);
            localPSMP.playerLock.unlock();
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        } catch (Throwable th) {
            localPSMP.playerLock.unlock();
        }
    }

    private void playMediaObject(@NonNull Playable playable, boolean forceReset, boolean stream, boolean startWhenPrepared, boolean prepareImmediately) {
        if (this.playerLock.isHeldByCurrentThread()) {
            if (this.media != null) {
                if (!forceReset && this.media.getIdentifier().equals(playable.getIdentifier()) && this.playerStatus == PlayerStatus.PLAYING) {
                    Log.d(TAG, "Method call to playMediaObject was ignored: media file already playing.");
                    return;
                }
                if (!(this.playerStatus == PlayerStatus.PAUSED || this.playerStatus == PlayerStatus.PLAYING)) {
                    if (this.playerStatus != PlayerStatus.PREPARED) {
                        if (this.playerStatus == PlayerStatus.PLAYING) {
                            this.callback.onPlaybackPause(this.media, getPosition());
                        }
                        if (!this.media.getIdentifier().equals(playable.getIdentifier())) {
                            this.executor.submit(new -$$Lambda$LocalPSMP$MFmVXLqMp6QtNlI_0S86qHVdTJQ(this, this.media));
                        }
                        setPlayerStatus(PlayerStatus.INDETERMINATE, null);
                    }
                }
                this.mediaPlayer.stop();
                if (this.playerStatus == PlayerStatus.PLAYING) {
                    this.callback.onPlaybackPause(this.media, getPosition());
                }
                if (!this.media.getIdentifier().equals(playable.getIdentifier())) {
                    this.executor.submit(new -$$Lambda$LocalPSMP$MFmVXLqMp6QtNlI_0S86qHVdTJQ(this, this.media));
                }
                setPlayerStatus(PlayerStatus.INDETERMINATE, null);
            }
            this.media = playable;
            this.stream = stream;
            this.mediaType = this.media.getMediaType();
            this.videoSize = null;
            createMediaPlayer();
            this.startWhenPrepared.set(startWhenPrepared);
            setPlayerStatus(PlayerStatus.INITIALIZING, this.media);
            try {
                this.media.loadMetadata();
                this.callback.onMediaChanged(false);
                if (stream) {
                    this.mediaPlayer.setDataSource(this.media.getStreamUrl());
                } else if (new File(this.media.getLocalMediaUrl()).canRead()) {
                    this.mediaPlayer.setDataSource(this.media.getLocalMediaUrl());
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Unable to read local file ");
                    stringBuilder.append(this.media.getLocalMediaUrl());
                    throw new IOException(stringBuilder.toString());
                }
                setPlayerStatus(PlayerStatus.INITIALIZED, this.media);
                if (prepareImmediately) {
                    setPlayerStatus(PlayerStatus.PREPARING, this.media);
                    this.mediaPlayer.prepare();
                    onPrepared(startWhenPrepared);
                }
            } catch (Exception e) {
                e.printStackTrace();
                setPlayerStatus(PlayerStatus.ERROR, null);
            }
            return;
        }
        throw new IllegalStateException("method requires playerLock");
    }

    public void resume() {
        this.executor.submit(new -$$Lambda$LocalPSMP$qJO6fE9RrQzc5jd_fVqGKQZ-MqQ());
    }

    public static /* synthetic */ void lambda$resume$3(LocalPSMP localPSMP) {
        localPSMP.playerLock.lock();
        localPSMP.resumeSync();
        localPSMP.playerLock.unlock();
    }

    private void resumeSync() {
        int focusGained;
        if (this.playerStatus != PlayerStatus.PAUSED) {
            if (this.playerStatus != PlayerStatus.PREPARED) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Call to resume() was ignored because current state of PSMP object is ");
                stringBuilder.append(this.playerStatus);
                Log.d(str, stringBuilder.toString());
                return;
            }
        }
        if (VERSION.SDK_INT >= 26) {
            focusGained = this.audioManager.requestAudioFocus(new Builder(1).setAudioAttributes(new AudioAttributes.Builder().setUsage(1).setContentType(1).build()).setOnAudioFocusChangeListener(this.audioFocusChangeListener).setAcceptsDelayedFocusGain(true).setWillPauseWhenDucked(true).build());
        } else {
            focusGained = this.audioManager.requestAudioFocus(this.audioFocusChangeListener, 3, 1);
        }
        if (focusGained == 1) {
            Log.d(TAG, "Audiofocus successfully requested");
            Log.d(TAG, "Resuming/Starting playback");
            acquireWifiLockIfNecessary();
            float speed = 1.0f;
            try {
                speed = Float.parseFloat(UserPreferences.getPlaybackSpeed());
            } catch (NumberFormatException e) {
                Log.e(TAG, Log.getStackTraceString(e));
                UserPreferences.setPlaybackSpeed(String.valueOf(1.0f));
            }
            setPlaybackParams(speed, UserPreferences.isSkipSilence());
            setVolume(UserPreferences.getLeftVolume(), UserPreferences.getRightVolume());
            if (this.playerStatus == PlayerStatus.PREPARED && this.media.getPosition() > 0) {
                seekToSync(RewindAfterPauseUtils.calculatePositionWithRewind(this.media.getPosition(), this.media.getLastPlayedTime()));
            }
            this.mediaPlayer.start();
            setPlayerStatus(PlayerStatus.PLAYING, this.media);
            this.pausedBecauseOfTransientAudiofocusLoss = false;
        } else {
            Log.e(TAG, "Failed to request audio focus");
        }
    }

    public void pause(boolean abandonFocus, boolean reinit) {
        this.executor.submit(new -$$Lambda$LocalPSMP$IxuN9vZPk4VJU200rf32vd5UgtM(this, abandonFocus, reinit));
    }

    public static /* synthetic */ void lambda$pause$4(LocalPSMP localPSMP, boolean abandonFocus, boolean reinit) {
        localPSMP.playerLock.lock();
        localPSMP.releaseWifiLockIfNecessary();
        if (localPSMP.playerStatus == PlayerStatus.PLAYING) {
            Log.d(TAG, "Pausing playback.");
            localPSMP.mediaPlayer.pause();
            localPSMP.setPlayerStatus(PlayerStatus.PAUSED, localPSMP.media, localPSMP.getPosition());
            if (abandonFocus) {
                if (VERSION.SDK_INT >= 26) {
                    localPSMP.audioManager.abandonAudioFocusRequest(new Builder(1).setOnAudioFocusChangeListener(localPSMP.audioFocusChangeListener).build());
                } else {
                    localPSMP.audioManager.abandonAudioFocus(localPSMP.audioFocusChangeListener);
                }
                localPSMP.pausedBecauseOfTransientAudiofocusLoss = false;
            }
            if (localPSMP.stream && reinit) {
                localPSMP.reinit();
            }
        } else {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Ignoring call to pause: Player is in ");
            stringBuilder.append(localPSMP.playerStatus);
            stringBuilder.append(" state");
            Log.d(str, stringBuilder.toString());
        }
        localPSMP.playerLock.unlock();
    }

    public void prepare() {
        this.executor.submit(new -$$Lambda$LocalPSMP$e2KcqXZ7b0S-kIUaHPZV8_h5hKs());
    }

    public static /* synthetic */ void lambda$prepare$5(LocalPSMP localPSMP) {
        localPSMP.playerLock.lock();
        if (localPSMP.playerStatus == PlayerStatus.INITIALIZED) {
            Log.d(TAG, "Preparing media player");
            localPSMP.setPlayerStatus(PlayerStatus.PREPARING, localPSMP.media);
            try {
                localPSMP.mediaPlayer.prepare();
                localPSMP.onPrepared(localPSMP.startWhenPrepared.get());
            } catch (IOException e) {
                e.printStackTrace();
                localPSMP.setPlayerStatus(PlayerStatus.ERROR, null);
            }
        }
        localPSMP.playerLock.unlock();
    }

    private void onPrepared(boolean startWhenPrepared) {
        this.playerLock.lock();
        if (this.playerStatus == PlayerStatus.PREPARING) {
            Log.d(TAG, "Resource prepared");
            if (this.mediaType == MediaType.VIDEO && (this.mediaPlayer instanceof ExoPlayerWrapper)) {
                ExoPlayerWrapper vp = this.mediaPlayer;
                this.videoSize = new Pair(Integer.valueOf(vp.getVideoWidth()), Integer.valueOf(vp.getVideoHeight()));
            } else if (this.mediaType == MediaType.VIDEO && (this.mediaPlayer instanceof VideoPlayer)) {
                VideoPlayer vp2 = this.mediaPlayer;
                this.videoSize = new Pair(Integer.valueOf(vp2.getVideoWidth()), Integer.valueOf(vp2.getVideoHeight()));
                if (this.media.getPosition() > 0) {
                    seekToSync(this.media.getPosition());
                }
                if (this.media.getDuration() <= 0) {
                    Log.d(TAG, "Setting duration of media");
                    this.media.setDuration(this.mediaPlayer.getDuration());
                }
                setPlayerStatus(PlayerStatus.PREPARED, this.media);
                if (startWhenPrepared) {
                    resumeSync();
                }
                this.playerLock.unlock();
                return;
            }
            if (this.media.getPosition() > 0) {
                seekToSync(this.media.getPosition());
            }
            if (this.media.getDuration() <= 0) {
                Log.d(TAG, "Setting duration of media");
                this.media.setDuration(this.mediaPlayer.getDuration());
            }
            setPlayerStatus(PlayerStatus.PREPARED, this.media);
            if (startWhenPrepared) {
                resumeSync();
            }
            this.playerLock.unlock();
            return;
        }
        this.playerLock.unlock();
        throw new IllegalStateException("Player is not in PREPARING state");
    }

    public void reinit() {
        this.executor.submit(new -$$Lambda$LocalPSMP$lXEIDGB5BJ2qyJ79iHTVu_6bfHY());
    }

    public static /* synthetic */ void lambda$reinit$6(LocalPSMP localPSMP) {
        localPSMP.playerLock.lock();
        Log.d(TAG, "reinit()");
        localPSMP.releaseWifiLockIfNecessary();
        if (localPSMP.media != null) {
            localPSMP.playMediaObject(localPSMP.media, true, localPSMP.stream, localPSMP.startWhenPrepared.get(), false);
        } else if (localPSMP.mediaPlayer != null) {
            localPSMP.mediaPlayer.reset();
        } else {
            Log.d(TAG, "Call to reinit was ignored: media and mediaPlayer were null");
        }
        localPSMP.playerLock.unlock();
    }

    private void seekToSync(int t) {
        if (t < 0) {
            t = 0;
        }
        this.playerLock.lock();
        if (!(this.playerStatus == PlayerStatus.PLAYING || this.playerStatus == PlayerStatus.PAUSED)) {
            if (this.playerStatus != PlayerStatus.PREPARED) {
                if (this.playerStatus == PlayerStatus.INITIALIZED) {
                    this.media.setPosition(t);
                    this.startWhenPrepared.set(false);
                    prepare();
                }
                this.playerLock.unlock();
            }
        }
        CountDownLatch countDownLatch = this.seekLatch;
        if (countDownLatch != null && countDownLatch.getCount() > 0) {
            try {
                this.seekLatch.await(3, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }
        this.seekLatch = new CountDownLatch(1);
        this.statusBeforeSeeking = this.playerStatus;
        setPlayerStatus(PlayerStatus.SEEKING, this.media, getPosition());
        this.mediaPlayer.seekTo(t);
        if (this.statusBeforeSeeking == PlayerStatus.PREPARED) {
            this.media.setPosition(t);
        }
        try {
            this.seekLatch.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e2) {
            Log.e(TAG, Log.getStackTraceString(e2));
        }
        this.playerLock.unlock();
    }

    public void seekTo(int t) {
        this.executor.submit(new -$$Lambda$LocalPSMP$yS0GXDf86RojNPV0IdEaSv2wqlw(this, t));
    }

    public void seekDelta(int d) {
        this.executor.submit(new -$$Lambda$LocalPSMP$yOUUHMWSHNMRnsxbmm0KipyUwr0(this, d));
    }

    public static /* synthetic */ void lambda$seekDelta$8(LocalPSMP localPSMP, int d) {
        localPSMP.playerLock.lock();
        int currentPosition = localPSMP.getPosition();
        if (currentPosition != -1) {
            localPSMP.seekToSync(currentPosition + d);
        } else {
            Log.e(TAG, "getPosition() returned INVALID_TIME in seekDelta");
        }
        localPSMP.playerLock.unlock();
    }

    public int getDuration() {
        if (!this.playerLock.tryLock()) {
            return -1;
        }
        int retVal = -1;
        if (!(this.playerStatus == PlayerStatus.PLAYING || this.playerStatus == PlayerStatus.PAUSED)) {
            if (this.playerStatus != PlayerStatus.PREPARED) {
                if (retVal > 0 && this.media != null && this.media.getDuration() > 0) {
                    retVal = this.media.getDuration();
                }
                this.playerLock.unlock();
                return retVal;
            }
        }
        retVal = this.mediaPlayer.getDuration();
        if (retVal > 0) {
        }
        this.playerLock.unlock();
        return retVal;
    }

    public int getPosition() {
        try {
            if (!this.playerLock.tryLock(50, TimeUnit.MILLISECONDS)) {
                return -1;
            }
            int retVal = -1;
            if (this.playerStatus.isAtLeast(PlayerStatus.PREPARED)) {
                retVal = this.mediaPlayer.getCurrentPosition();
            }
            if (retVal <= 0 && this.media != null && this.media.getPosition() >= 0) {
                retVal = this.media.getPosition();
            }
            this.playerLock.unlock();
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("getPosition() -> ");
            stringBuilder.append(retVal);
            Log.d(str, stringBuilder.toString());
            return retVal;
        } catch (InterruptedException e) {
            return -1;
        }
    }

    public boolean isStartWhenPrepared() {
        return this.startWhenPrepared.get();
    }

    public void setStartWhenPrepared(boolean startWhenPrepared) {
        this.startWhenPrepared.set(startWhenPrepared);
    }

    public boolean canSetSpeed() {
        if (this.mediaPlayer == null || this.media == null || this.media.getMediaType() != MediaType.AUDIO) {
            return false;
        }
        return this.mediaPlayer.canSetSpeed();
    }

    private void setSpeedSyncAndSkipSilence(float speed, boolean skipSilence) {
        this.playerLock.lock();
        if (this.media != null && this.media.getMediaType() == MediaType.AUDIO) {
            if (this.mediaPlayer.canSetSpeed()) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Playback speed was set to ");
                stringBuilder.append(speed);
                Log.d(str, stringBuilder.toString());
                this.callback.playbackSpeedChanged(speed);
            }
            this.mediaPlayer.setPlaybackParams(speed, skipSilence);
        }
        this.playerLock.unlock();
    }

    public void setPlaybackParams(float speed, boolean skipSilence) {
        this.executor.submit(new -$$Lambda$LocalPSMP$pKZpLUVTrUQlCXzC-7SYlI3JJAs(this, speed, skipSilence));
    }

    public float getPlaybackSpeed() {
        if (!this.playerLock.tryLock()) {
            return 1.0f;
        }
        float retVal = 1.0f;
        if (!(this.playerStatus == PlayerStatus.PLAYING || this.playerStatus == PlayerStatus.PAUSED)) {
            if (this.playerStatus != PlayerStatus.PREPARED) {
                this.playerLock.unlock();
                return retVal;
            }
        }
        if (this.mediaPlayer.canSetSpeed()) {
            retVal = this.mediaPlayer.getCurrentSpeedMultiplier();
        }
        this.playerLock.unlock();
        return retVal;
    }

    public void setVolume(float volumeLeft, float volumeRight) {
        this.executor.submit(new -$$Lambda$LocalPSMP$PX3WlUoWRWh48sjL2XnAOp155XE(this, volumeLeft, volumeRight));
    }

    private void setVolumeSync(float volumeLeft, float volumeRight) {
        this.playerLock.lock();
        if (this.media != null && this.media.getMediaType() == MediaType.AUDIO) {
            this.mediaPlayer.setVolume(volumeLeft, volumeRight);
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Media player volume was set to ");
            stringBuilder.append(volumeLeft);
            stringBuilder.append(StringUtils.SPACE);
            stringBuilder.append(volumeRight);
            Log.d(str, stringBuilder.toString());
        }
        this.playerLock.unlock();
    }

    public boolean canDownmix() {
        if (this.mediaPlayer == null || this.media == null || this.media.getMediaType() != MediaType.AUDIO) {
            return false;
        }
        return this.mediaPlayer.canDownmix();
    }

    public void setDownmix(boolean enable) {
        this.playerLock.lock();
        if (this.media != null && this.media.getMediaType() == MediaType.AUDIO) {
            this.mediaPlayer.setDownmix(enable);
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Media player downmix was set to ");
            stringBuilder.append(enable);
            Log.d(str, stringBuilder.toString());
        }
        this.playerLock.unlock();
    }

    public MediaType getCurrentMediaType() {
        return this.mediaType;
    }

    public boolean isStreaming() {
        return this.stream;
    }

    public void shutdown() {
        this.executor.shutdown();
        if (this.mediaPlayer != null) {
            try {
                removeMediaPlayerErrorListener();
                if (this.mediaPlayer.isPlaying()) {
                    this.mediaPlayer.stop();
                }
            } catch (Exception e) {
            }
            this.mediaPlayer.release();
        }
        releaseWifiLockIfNecessary();
    }

    private void removeMediaPlayerErrorListener() {
        if (this.mediaPlayer instanceof VideoPlayer) {
            this.mediaPlayer.setOnErrorListener(-$$Lambda$LocalPSMP$EHf4_KLWaMjtJe9Y3MnWCEeHXO4.INSTANCE);
            return;
        }
        if (this.mediaPlayer instanceof AudioPlayer) {
            this.mediaPlayer.setOnErrorListener(-$$Lambda$LocalPSMP$hqyxXLbCv0p9B_kuKAj3_igtswM.INSTANCE);
        } else if (this.mediaPlayer instanceof ExoPlayerWrapper) {
            this.mediaPlayer.setOnErrorListener(-$$Lambda$LocalPSMP$h_PBfUD_j1fD9qPYb2tyVRJ6Cu8.INSTANCE);
        }
    }

    public void shutdownQuietly() {
        this.executor.submit(new -$$Lambda$IKGjTtcG0oaUN63fKt3Gr4ZUZYY());
        this.executor.shutdown();
    }

    public void setVideoSurface(SurfaceHolder surface) {
        this.executor.submit(new -$$Lambda$LocalPSMP$7EidJb1AwUV46nFwvOb2iY9VPXw(this, surface));
    }

    public static /* synthetic */ void lambda$setVideoSurface$14(LocalPSMP localPSMP, SurfaceHolder surface) {
        localPSMP.playerLock.lock();
        if (localPSMP.mediaPlayer != null) {
            localPSMP.mediaPlayer.setDisplay(surface);
        }
        localPSMP.playerLock.unlock();
    }

    public void resetVideoSurface() {
        this.executor.submit(new -$$Lambda$LocalPSMP$NgophUrABOdK3Lw6Yxjo7AH0e78());
    }

    public static /* synthetic */ void lambda$resetVideoSurface$15(LocalPSMP localPSMP) {
        localPSMP.playerLock.lock();
        if (localPSMP.mediaType == MediaType.VIDEO) {
            Log.d(TAG, "Resetting video surface");
            localPSMP.mediaPlayer.setDisplay(null);
            localPSMP.reinit();
        } else {
            Log.e(TAG, "Resetting video surface for media of Audio type");
        }
        localPSMP.playerLock.unlock();
    }

    public Pair<Integer, Integer> getVideoSize() {
        if (!this.playerLock.tryLock()) {
            return this.videoSize;
        }
        Pair<Integer, Integer> res;
        if (!(this.mediaPlayer == null || this.playerStatus == PlayerStatus.ERROR)) {
            if (this.mediaType == MediaType.VIDEO) {
                if (this.mediaPlayer instanceof ExoPlayerWrapper) {
                    ExoPlayerWrapper vp = this.mediaPlayer;
                    this.videoSize = new Pair(Integer.valueOf(vp.getVideoWidth()), Integer.valueOf(vp.getVideoHeight()));
                    res = this.videoSize;
                } else {
                    VideoPlayer vp2 = this.mediaPlayer;
                    this.videoSize = new Pair(Integer.valueOf(vp2.getVideoWidth()), Integer.valueOf(vp2.getVideoHeight()));
                    res = this.videoSize;
                }
                this.playerLock.unlock();
                return res;
            }
        }
        res = null;
        this.playerLock.unlock();
        return res;
    }

    public Playable getPlayable() {
        return this.media;
    }

    protected void setPlayable(Playable playable) {
        this.media = playable;
    }

    private void createMediaPlayer() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.release();
        }
        if (this.media == null) {
            this.mediaPlayer = null;
            return;
        }
        if (UserPreferences.useExoplayer()) {
            this.mediaPlayer = new ExoPlayerWrapper(this.context);
        } else if (this.media.getMediaType() == MediaType.VIDEO) {
            this.mediaPlayer = new VideoPlayer();
        } else {
            this.mediaPlayer = new AudioPlayer(this.context);
        }
        this.mediaPlayer.setAudioStreamType(3);
        this.mediaPlayer.setWakeMode(this.context, 1);
        setMediaPlayerListeners(this.mediaPlayer);
    }

    protected Future<?> endPlayback(boolean hasEnded, boolean wasSkipped, boolean shouldContinue, boolean toStoppedState) {
        return this.executor.submit(new -$$Lambda$LocalPSMP$Cm8NfY9klxUfHGLdJjXUCI5RD9k(this, shouldContinue, toStoppedState, hasEnded, wasSkipped));
    }

    public static /* synthetic */ void lambda$endPlayback$17(LocalPSMP localPSMP, boolean shouldContinue, boolean toStoppedState, boolean hasEnded, boolean wasSkipped) {
        Playable nextMedia;
        LocalPSMP localPSMP2 = localPSMP;
        localPSMP2.playerLock.lock();
        localPSMP.releaseWifiLockIfNecessary();
        boolean isPlaying = localPSMP2.playerStatus == PlayerStatus.PLAYING;
        if (localPSMP2.playerStatus != PlayerStatus.INDETERMINATE) {
            localPSMP.setPlayerStatus(PlayerStatus.INDETERMINATE, localPSMP2.media);
        }
        if (localPSMP2.media != null) {
            int position = localPSMP.getPosition();
            if (position >= 0) {
                localPSMP2.media.setPosition(position);
            }
        }
        if (localPSMP2.mediaPlayer != null) {
            localPSMP2.mediaPlayer.reset();
        }
        if (VERSION.SDK_INT >= 26) {
            localPSMP2.audioManager.abandonAudioFocusRequest(new Builder(1).setOnAudioFocusChangeListener(localPSMP2.audioFocusChangeListener).build());
        } else {
            localPSMP2.audioManager.abandonAudioFocus(localPSMP2.audioFocusChangeListener);
        }
        Playable currentMedia = localPSMP2.media;
        if (shouldContinue) {
            boolean z;
            boolean playNextEpisode;
            nextMedia = localPSMP2.callback.getNextInQueue(currentMedia);
            if (isPlaying && nextMedia != null) {
                if (UserPreferences.isFollowQueue()) {
                    z = true;
                    playNextEpisode = z;
                    if (playNextEpisode) {
                        Log.d(TAG, "Playback of next episode will start immediately.");
                    } else if (nextMedia != null) {
                        Log.d(TAG, "No more episodes available to play");
                    } else {
                        Log.d(TAG, "Loading next episode, but not playing automatically.");
                    }
                    if (nextMedia != null) {
                        localPSMP2.callback.onPlaybackEnded(nextMedia.getMediaType(), playNextEpisode);
                        localPSMP2.media = null;
                        localPSMP.playMediaObject(nextMedia, false, nextMedia.localFileAvailable() ^ 1, playNextEpisode, playNextEpisode);
                    }
                }
            }
            z = false;
            playNextEpisode = z;
            if (playNextEpisode) {
                Log.d(TAG, "Playback of next episode will start immediately.");
            } else if (nextMedia != null) {
                Log.d(TAG, "Loading next episode, but not playing automatically.");
            } else {
                Log.d(TAG, "No more episodes available to play");
            }
            if (nextMedia != null) {
                if (playNextEpisode) {
                }
                localPSMP2.callback.onPlaybackEnded(nextMedia.getMediaType(), playNextEpisode);
                localPSMP2.media = null;
                localPSMP.playMediaObject(nextMedia, false, nextMedia.localFileAvailable() ^ 1, playNextEpisode, playNextEpisode);
            }
        } else {
            nextMedia = null;
        }
        if (!shouldContinue) {
            if (!toStoppedState) {
                if (isPlaying) {
                    localPSMP2.callback.onPlaybackPause(currentMedia, currentMedia.getPosition());
                    localPSMP2.playerLock.unlock();
                }
                localPSMP2.playerLock.unlock();
            }
        }
        if (nextMedia == null) {
            localPSMP2.callback.onPlaybackEnded(null, true);
            localPSMP.stop();
        }
        localPSMP2.executor.submit(new -$$Lambda$LocalPSMP$mJJfUudtc_01a-gc8t7N8xqSFQA(localPSMP, currentMedia, hasEnded, wasSkipped, nextMedia != null));
        localPSMP2.playerLock.unlock();
    }

    private void stop() {
        this.executor.submit(new -$$Lambda$LocalPSMP$lly0OjdQ8PtiVwRz42iawzzI5YU());
    }

    public static /* synthetic */ void lambda$stop$18(LocalPSMP localPSMP) {
        localPSMP.playerLock.lock();
        localPSMP.releaseWifiLockIfNecessary();
        if (localPSMP.playerStatus == PlayerStatus.INDETERMINATE) {
            localPSMP.setPlayerStatus(PlayerStatus.STOPPED, null);
        } else {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Ignored call to stop: Current player state is: ");
            stringBuilder.append(localPSMP.playerStatus);
            Log.d(str, stringBuilder.toString());
        }
        localPSMP.playerLock.unlock();
    }

    protected boolean shouldLockWifi() {
        return this.stream;
    }

    private IPlayer setMediaPlayerListeners(IPlayer mp) {
        if (mp != null) {
            if (this.media != null) {
                String str;
                StringBuilder stringBuilder;
                if (mp instanceof VideoPlayer) {
                    if (this.media.getMediaType() != MediaType.VIDEO) {
                        str = TAG;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("video player, but media type is ");
                        stringBuilder.append(this.media.getMediaType());
                        Log.w(str, stringBuilder.toString());
                    }
                    VideoPlayer vp = (VideoPlayer) mp;
                    vp.setOnCompletionListener(this.videoCompletionListener);
                    vp.setOnSeekCompleteListener(this.videoSeekCompleteListener);
                    vp.setOnErrorListener(this.videoErrorListener);
                    vp.setOnBufferingUpdateListener(this.videoBufferingUpdateListener);
                    vp.setOnInfoListener(this.videoInfoListener);
                } else if (mp instanceof AudioPlayer) {
                    if (this.media.getMediaType() != MediaType.AUDIO) {
                        str = TAG;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("audio player, but media type is ");
                        stringBuilder.append(this.media.getMediaType());
                        Log.w(str, stringBuilder.toString());
                    }
                    AudioPlayer ap = (AudioPlayer) mp;
                    ap.setOnCompletionListener(this.audioCompletionListener);
                    ap.setOnSeekCompleteListener(this.audioSeekCompleteListener);
                    ap.setOnErrorListener(this.audioErrorListener);
                    ap.setOnBufferingUpdateListener(this.audioBufferingUpdateListener);
                    ap.setOnInfoListener(this.audioInfoListener);
                    ap.setOnSpeedAdjustmentAvailableChangedListener(this.audioSetSpeedAbilityListener);
                } else if (mp instanceof ExoPlayerWrapper) {
                    ExoPlayerWrapper ap2 = (ExoPlayerWrapper) mp;
                    ap2.setOnCompletionListener(this.audioCompletionListener);
                    ap2.setOnSeekCompleteListener(this.audioSeekCompleteListener);
                    ap2.setOnErrorListener(this.audioErrorListener);
                } else {
                    str = TAG;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Unknown media player: ");
                    stringBuilder.append(mp);
                    Log.w(str, stringBuilder.toString());
                }
                return mp;
            }
        }
        return mp;
    }

    private void genericOnCompletion() {
        endPlayback(true, false, true, true);
    }

    private void genericOnBufferingUpdate(int percent) {
        this.callback.onBufferingUpdate(percent);
    }

    private boolean genericInfoListener(int what) {
        return this.callback.onMediaPlayerInfo(what, 0);
    }

    public static /* synthetic */ boolean lambda$new$26(LocalPSMP localPSMP, org.antennapod.audio.MediaPlayer mp, int what, int extra) {
        if (mp == null || !mp.canFallback()) {
            return localPSMP.genericOnError(mp, what, extra);
        }
        mp.fallback();
        return true;
    }

    private boolean genericOnError(Object inObj, int what, int extra) {
        return this.callback.onMediaPlayerError(inObj, what, extra);
    }

    private void genericSeekCompleteListener() {
        new Thread(new -$$Lambda$LocalPSMP$vVhv2vv8zUKfLjCfGJ0T5BV_Ye4()).start();
    }

    public static /* synthetic */ void lambda$genericSeekCompleteListener$29(LocalPSMP localPSMP) {
        Log.d(TAG, "genericSeekCompleteListener");
        CountDownLatch countDownLatch = localPSMP.seekLatch;
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
        localPSMP.playerLock.lock();
        if (localPSMP.playerStatus == PlayerStatus.PLAYING) {
            localPSMP.callback.onPlaybackStart(localPSMP.media, localPSMP.getPosition());
        }
        if (localPSMP.playerStatus == PlayerStatus.SEEKING) {
            localPSMP.setPlayerStatus(localPSMP.statusBeforeSeeking, localPSMP.media, localPSMP.getPosition());
        }
        localPSMP.playerLock.unlock();
    }
}
