package de.danoeh.antennapod.core.util.playback;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.SurfaceHolder;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import de.danoeh.antennapod.core.C0734R;
import de.danoeh.antennapod.core.feed.Chapter;
import de.danoeh.antennapod.core.feed.FeedMedia;
import de.danoeh.antennapod.core.feed.MediaType;
import de.danoeh.antennapod.core.preferences.PlaybackPreferences;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.service.playback.PlaybackService;
import de.danoeh.antennapod.core.service.playback.PlaybackService.LocalBinder;
import de.danoeh.antennapod.core.service.playback.PlaybackServiceMediaPlayer.PSMPInfo;
import de.danoeh.antennapod.core.service.playback.PlayerStatus;
import de.danoeh.antennapod.core.storage.DBTasks;
import de.danoeh.antennapod.core.util.Converter;
import de.danoeh.antennapod.core.util.playback.Playable.PlayableUtils;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.antennapod.audio.MediaPlayer;

public abstract class PlaybackController {
    private static final int INVALID_TIME = -1;
    private static final int SCHED_EX_POOLSIZE = 1;
    private static final String TAG = "PlaybackController";
    private final Activity activity;
    private boolean initialized = false;
    private final ServiceConnection mConnection = new C07621();
    private Playable media;
    private boolean mediaInfoLoaded = false;
    private Disposable mediaLoader;
    private final BroadcastReceiver notificationReceiver = new C07643();
    private PlaybackService playbackService;
    private MediaPositionObserver positionObserver;
    private ScheduledFuture<?> positionObserverFuture;
    private final boolean reinitOnPause;
    private boolean released = false;
    private final ScheduledThreadPoolExecutor schedExecutor;
    private Disposable serviceBinder;
    private final BroadcastReceiver shutdownReceiver = new C07654();
    private PlayerStatus status = PlayerStatus.STOPPED;
    private final BroadcastReceiver statusUpdate = new C07632();

    /* renamed from: de.danoeh.antennapod.core.util.playback.PlaybackController$1 */
    class C07621 implements ServiceConnection {
        C07621() {
        }

        public void onServiceConnected(ComponentName className, IBinder service) {
            if (service instanceof LocalBinder) {
                PlaybackController.this.playbackService = ((LocalBinder) service).getService();
                if (PlaybackController.this.released) {
                    Log.i(PlaybackController.TAG, "Connection to playback service has been established, but controller has already been released");
                    return;
                }
                PlaybackController.this.queryService();
                Log.d(PlaybackController.TAG, "Connection to Service established");
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            PlaybackController.this.playbackService = null;
            Log.d(PlaybackController.TAG, "Disconnected from Service");
        }
    }

    /* renamed from: de.danoeh.antennapod.core.util.playback.PlaybackController$2 */
    class C07632 extends BroadcastReceiver {
        C07632() {
        }

        public void onReceive(Context context, Intent intent) {
            Log.d(PlaybackController.TAG, "Received statusUpdate Intent.");
            if (PlaybackController.this.isConnectedToPlaybackService()) {
                PSMPInfo info = PlaybackController.this.playbackService.getPSMPInfo();
                PlaybackController.this.status = info.playerStatus;
                PlaybackController.this.media = info.playable;
                PlaybackController.this.handleStatus();
                return;
            }
            Log.w(PlaybackController.TAG, "Couldn't receive status update: playbackService was null");
            PlaybackController.this.bindToService();
        }
    }

    /* renamed from: de.danoeh.antennapod.core.util.playback.PlaybackController$3 */
    class C07643 extends BroadcastReceiver {
        C07643() {
        }

        public void onReceive(Context context, Intent intent) {
            if (PlaybackController.this.isConnectedToPlaybackService()) {
                int type = intent.getIntExtra(PlaybackService.EXTRA_NOTIFICATION_TYPE, -1);
                int code = intent.getIntExtra(PlaybackService.EXTRA_NOTIFICATION_CODE, -1);
                if (code != -1) {
                    if (type != -1) {
                        if (type != 0) {
                            switch (type) {
                                case 2:
                                    PlaybackController.this.onBufferUpdate(((float) code) / 100.0f);
                                    break;
                                case 3:
                                    PlaybackController.this.cancelPositionObserver();
                                    PlaybackController.this.mediaInfoLoaded = false;
                                    PlaybackController.this.queryService();
                                    PlaybackController.this.onReloadNotification(intent.getIntExtra(PlaybackService.EXTRA_NOTIFICATION_CODE, -1));
                                    break;
                                case 4:
                                    PlaybackController.this.onSleepTimerUpdate();
                                    break;
                                case 5:
                                    PlaybackController.this.onBufferStart();
                                    break;
                                case 6:
                                    PlaybackController.this.onBufferEnd();
                                    break;
                                case 7:
                                    PlaybackController.this.onPlaybackEnd();
                                    break;
                                case 8:
                                    PlaybackController.this.onPlaybackSpeedChange();
                                    break;
                                case 9:
                                    PlaybackController.this.onSetSpeedAbilityChanged();
                                    break;
                                case 10:
                                    PlaybackController.this.postStatusMsg(code, true);
                                    break;
                                default:
                                    break;
                            }
                        }
                        PlaybackController.this.handleError(code);
                        return;
                    }
                }
                Log.d(PlaybackController.TAG, "Bad arguments. Won't handle intent");
                return;
            }
            PlaybackController.this.bindToService();
        }
    }

    /* renamed from: de.danoeh.antennapod.core.util.playback.PlaybackController$4 */
    class C07654 extends BroadcastReceiver {
        C07654() {
        }

        public void onReceive(Context context, Intent intent) {
            if (!PlaybackController.this.isConnectedToPlaybackService()) {
                return;
            }
            if (TextUtils.equals(intent.getAction(), PlaybackService.ACTION_SHUTDOWN_PLAYBACK_SERVICE)) {
                PlaybackController.this.release();
                PlaybackController.this.onShutdownNotification();
            }
        }
    }

    public class MediaPositionObserver implements Runnable {
        static final int WAITING_INTERVALL = 1000;

        public void run() {
            if (PlaybackController.this.playbackService != null && PlaybackController.this.playbackService.getStatus() == PlayerStatus.PLAYING) {
                PlaybackController.this.activity.runOnUiThread(new -$$Lambda$wq-FOi-2ySG-7Qedk62-EKlrTkk(PlaybackController.this));
            }
        }
    }

    public PlaybackController(@NonNull Activity activity, boolean reinitOnPause) {
        this.activity = activity;
        this.reinitOnPause = reinitOnPause;
        this.schedExecutor = new ScheduledThreadPoolExecutor(1, -$$Lambda$PlaybackController$FNxrlRgG33XPy65Ml4ebHKM8RzM.INSTANCE, -$$Lambda$PlaybackController$Te0XlQ0sFS7I2AsxFCw--Ouj-iw.INSTANCE);
    }

    static /* synthetic */ Thread lambda$new$0(Runnable r) {
        Thread t = new Thread(r);
        t.setPriority(1);
        return t;
    }

    public void init() {
        if (PlaybackService.isRunning) {
            initServiceRunning();
        } else {
            initServiceNotRunning();
        }
    }

    private synchronized void initServiceRunning() {
        if (!this.initialized) {
            this.initialized = true;
            this.activity.registerReceiver(this.statusUpdate, new IntentFilter(PlaybackService.ACTION_PLAYER_STATUS_CHANGED));
            this.activity.registerReceiver(this.notificationReceiver, new IntentFilter(PlaybackService.ACTION_PLAYER_NOTIFICATION));
            this.activity.registerReceiver(this.shutdownReceiver, new IntentFilter(PlaybackService.ACTION_SHUTDOWN_PLAYBACK_SERVICE));
            if (this.released) {
                throw new IllegalStateException("Can't call init() after release() has been called");
            }
            bindToService();
            checkMediaInfoLoaded();
        }
    }

    public void release() {
        Log.d(TAG, "Releasing PlaybackController");
        try {
            this.activity.unregisterReceiver(this.statusUpdate);
        } catch (IllegalArgumentException e) {
        }
        try {
            this.activity.unregisterReceiver(this.notificationReceiver);
        } catch (IllegalArgumentException e2) {
        }
        Disposable disposable = this.serviceBinder;
        if (disposable != null) {
            disposable.dispose();
        }
        try {
            this.activity.unbindService(this.mConnection);
        } catch (IllegalArgumentException e3) {
        }
        try {
            this.activity.unregisterReceiver(this.shutdownReceiver);
        } catch (IllegalArgumentException e4) {
        }
        cancelPositionObserver();
        this.schedExecutor.shutdownNow();
        this.media = null;
        this.released = true;
    }

    public void pause() {
        this.mediaInfoLoaded = false;
    }

    private void bindToService() {
        Log.d(TAG, "Trying to connect to service");
        Disposable disposable = this.serviceBinder;
        if (disposable != null) {
            disposable.dispose();
        }
        this.serviceBinder = Observable.fromCallable(new -$$Lambda$PlaybackController$klxtvU3dTYxoq0rVP768CzvXKWs()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new -$$Lambda$PlaybackController$mVedNjQa_jfAOsEChkqET20mhtk(), -$$Lambda$PlaybackController$J-lLfT_PXhC28QLbvjDr3Dvq8G8.INSTANCE);
    }

    public static /* synthetic */ void lambda$bindToService$2(PlaybackController playbackController, Intent intent) throws Exception {
        boolean bound = false;
        if (PlaybackService.started) {
            Log.d(TAG, "PlaybackService is running, trying to connect without start command.");
            Context context = playbackController.activity;
            bound = context.bindService(new Intent(context, PlaybackService.class), playbackController.mConnection, 0);
        } else if (intent != null) {
            Log.d(TAG, "Calling start service");
            ContextCompat.startForegroundService(playbackController.activity, intent);
            bound = playbackController.activity.bindService(intent, playbackController.mConnection, 0);
        } else {
            playbackController.status = PlayerStatus.STOPPED;
            playbackController.setupGUI();
            playbackController.handleStatus();
        }
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Result for service binding: ");
        stringBuilder.append(bound);
        Log.d(str, stringBuilder.toString());
    }

    @Nullable
    private Intent getPlayLastPlayedMediaIntent() {
        Log.d(TAG, "Trying to restore last played media");
        Playable media = PlayableUtils.createInstanceFromPreferences(this.activity);
        if (media == null) {
            Log.d(TAG, "No last played media found");
            return null;
        }
        boolean fileExists = media.localFileAvailable();
        boolean lastIsStream = PlaybackPreferences.getCurrentEpisodeIsStream();
        if (!fileExists && !lastIsStream && (media instanceof FeedMedia)) {
            DBTasks.notifyMissingFeedMediaFile(this.activity, (FeedMedia) media);
        }
        boolean z = false;
        PlaybackServiceStarter startWhenPrepared = new PlaybackServiceStarter(this.activity, media).startWhenPrepared(false);
        if (!lastIsStream) {
            if (fileExists) {
                return startWhenPrepared.shouldStream(z).getIntent();
            }
        }
        z = true;
        return startWhenPrepared.shouldStream(z).getIntent();
    }

    private void setupPositionObserver() {
        ScheduledFuture scheduledFuture = this.positionObserverFuture;
        if (scheduledFuture != null) {
            if (!scheduledFuture.isCancelled()) {
                if (!this.positionObserverFuture.isDone()) {
                    return;
                }
            }
        }
        Log.d(TAG, "Setting up position observer");
        this.positionObserver = new MediaPositionObserver();
        this.positionObserverFuture = this.schedExecutor.scheduleWithFixedDelay(this.positionObserver, 1000, 1000, TimeUnit.MILLISECONDS);
    }

    private void cancelPositionObserver() {
        boolean result = this.positionObserverFuture;
        if (result) {
            result = result.cancel(true);
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("PositionObserver cancelled. Result: ");
            stringBuilder.append(result);
            Log.d(str, stringBuilder.toString());
        }
    }

    public void setupGUI() {
    }

    public void onPositionObserverUpdate() {
    }

    public void onPlaybackSpeedChange() {
    }

    public void onSetSpeedAbilityChanged() {
    }

    public void onShutdownNotification() {
    }

    public void onReloadNotification(int code) {
    }

    public void onBufferStart() {
    }

    public void onBufferEnd() {
    }

    public void onBufferUpdate(float progress) {
    }

    public void onSleepTimerUpdate() {
    }

    public void handleError(int code) {
    }

    public void onPlaybackEnd() {
    }

    public void repeatHandleStatus() {
        if (this.status != null && this.playbackService != null) {
            handleStatus();
        }
    }

    private void handleStatus() {
        int playResource;
        int pauseResource;
        String str;
        StringBuilder stringBuilder;
        PlaybackService playbackService;
        CharSequence playText = this.activity.getString(C0734R.string.play_label);
        CharSequence pauseText = this.activity.getString(C0734R.string.pause_label);
        if (PlaybackService.getCurrentMediaType() != MediaType.AUDIO) {
            if (!PlaybackService.isCasting()) {
                playResource = C0734R.drawable.ic_av_play_circle_outline_80dp;
                pauseResource = C0734R.drawable.ic_av_pause_circle_outline_80dp;
                str = TAG;
                stringBuilder = new StringBuilder();
                stringBuilder.append("status: ");
                stringBuilder.append(this.status.toString());
                Log.d(str, stringBuilder.toString());
                switch (this.status) {
                    case ERROR:
                        postStatusMsg(C0734R.string.player_error_msg, false);
                        handleError(1);
                        return;
                    case PAUSED:
                        clearStatusMsg();
                        checkMediaInfoLoaded();
                        cancelPositionObserver();
                        onPositionObserverUpdate();
                        updatePlayButtonAppearance(playResource, playText);
                        if (PlaybackService.isCasting()) {
                            return;
                        }
                        if (PlaybackService.getCurrentMediaType() == MediaType.VIDEO) {
                            setScreenOn(false);
                            return;
                        }
                        return;
                    case PLAYING:
                        clearStatusMsg();
                        checkMediaInfoLoaded();
                        if (!PlaybackService.isCasting()) {
                            if (PlaybackService.getCurrentMediaType() == MediaType.VIDEO) {
                                onAwaitingVideoSurface();
                                setScreenOn(true);
                            }
                        }
                        setupPositionObserver();
                        updatePlayButtonAppearance(pauseResource, pauseText);
                        return;
                    case PREPARING:
                        postStatusMsg(C0734R.string.player_preparing_msg, false);
                        checkMediaInfoLoaded();
                        playbackService = this.playbackService;
                        if (playbackService == null) {
                            return;
                        }
                        if (playbackService.isStartWhenPrepared()) {
                            updatePlayButtonAppearance(playResource, playText);
                            return;
                        } else {
                            updatePlayButtonAppearance(pauseResource, pauseText);
                            return;
                        }
                    case STOPPED:
                        postStatusMsg(C0734R.string.player_stopped_msg, false);
                        return;
                    case PREPARED:
                        checkMediaInfoLoaded();
                        postStatusMsg(C0734R.string.player_ready_msg, false);
                        updatePlayButtonAppearance(playResource, playText);
                        onPositionObserverUpdate();
                        return;
                    case SEEKING:
                        onPositionObserverUpdate();
                        postStatusMsg(C0734R.string.player_seeking_msg, false);
                        return;
                    case INITIALIZED:
                        checkMediaInfoLoaded();
                        clearStatusMsg();
                        updatePlayButtonAppearance(playResource, playText);
                        return;
                    default:
                        return;
                }
            }
        }
        TypedArray res = this.activity.obtainStyledAttributes(new int[]{C0734R.attr.av_play_big, C0734R.attr.av_pause_big});
        pauseResource = res.getResourceId(0, C0734R.drawable.ic_play_arrow_grey600_36dp);
        int pauseResource2 = res.getResourceId(1, C0734R.drawable.ic_pause_grey600_36dp);
        res.recycle();
        playResource = pauseResource;
        pauseResource = pauseResource2;
        str = TAG;
        stringBuilder = new StringBuilder();
        stringBuilder.append("status: ");
        stringBuilder.append(this.status.toString());
        Log.d(str, stringBuilder.toString());
        switch (this.status) {
            case ERROR:
                postStatusMsg(C0734R.string.player_error_msg, false);
                handleError(1);
                return;
            case PAUSED:
                clearStatusMsg();
                checkMediaInfoLoaded();
                cancelPositionObserver();
                onPositionObserverUpdate();
                updatePlayButtonAppearance(playResource, playText);
                if (PlaybackService.isCasting()) {
                    if (PlaybackService.getCurrentMediaType() == MediaType.VIDEO) {
                        setScreenOn(false);
                        return;
                    }
                    return;
                }
                return;
            case PLAYING:
                clearStatusMsg();
                checkMediaInfoLoaded();
                if (!PlaybackService.isCasting()) {
                    if (PlaybackService.getCurrentMediaType() == MediaType.VIDEO) {
                        onAwaitingVideoSurface();
                        setScreenOn(true);
                    }
                }
                setupPositionObserver();
                updatePlayButtonAppearance(pauseResource, pauseText);
                return;
            case PREPARING:
                postStatusMsg(C0734R.string.player_preparing_msg, false);
                checkMediaInfoLoaded();
                playbackService = this.playbackService;
                if (playbackService == null) {
                    if (playbackService.isStartWhenPrepared()) {
                        updatePlayButtonAppearance(playResource, playText);
                        return;
                    } else {
                        updatePlayButtonAppearance(pauseResource, pauseText);
                        return;
                    }
                }
                return;
            case STOPPED:
                postStatusMsg(C0734R.string.player_stopped_msg, false);
                return;
            case PREPARED:
                checkMediaInfoLoaded();
                postStatusMsg(C0734R.string.player_ready_msg, false);
                updatePlayButtonAppearance(playResource, playText);
                onPositionObserverUpdate();
                return;
            case SEEKING:
                onPositionObserverUpdate();
                postStatusMsg(C0734R.string.player_seeking_msg, false);
                return;
            case INITIALIZED:
                checkMediaInfoLoaded();
                clearStatusMsg();
                updatePlayButtonAppearance(playResource, playText);
                return;
            default:
                return;
        }
    }

    private void checkMediaInfoLoaded() {
        boolean z;
        if (!this.mediaInfoLoaded) {
            if (!loadMediaInfo()) {
                z = false;
                this.mediaInfoLoaded = z;
            }
        }
        z = true;
        this.mediaInfoLoaded = z;
    }

    private void updatePlayButtonAppearance(int resource, CharSequence contentDescription) {
        ImageButton butPlay = getPlayButton();
        if (butPlay != null) {
            butPlay.setImageResource(resource);
            butPlay.setContentDescription(contentDescription);
        }
    }

    public ImageButton getPlayButton() {
        return null;
    }

    public void postStatusMsg(int msg, boolean showToast) {
    }

    public void clearStatusMsg() {
    }

    public boolean loadMediaInfo() {
        return false;
    }

    public void onAwaitingVideoSurface() {
    }

    private void queryService() {
        Log.d(TAG, "Querying service info");
        PSMPInfo info = this.playbackService;
        if (info != null) {
            info = info.getPSMPInfo();
            this.status = info.playerStatus;
            this.media = info.playable;
            onServiceQueried();
            setupGUI();
            handleStatus();
            this.mediaInfoLoaded = false;
            return;
        }
        Log.e(TAG, "queryService() was called without an existing connection to playbackservice");
    }

    public void onServiceQueried() {
    }

    public float onSeekBarProgressChanged(SeekBar seekBar, int progress, boolean fromUser, TextView txtvPosition) {
        if (!fromUser || this.playbackService == null || this.media == null) {
            return 0.0f;
        }
        float prog = ((float) progress) / ((float) seekBar.getMax());
        txtvPosition.setText(Converter.getDurationStringLong((int) (((float) this.media.getDuration()) * prog)));
        return prog;
    }

    public void onSeekBarStartTrackingTouch(SeekBar seekBar) {
        cancelPositionObserver();
    }

    public void onSeekBarStopTrackingTouch(SeekBar seekBar, float prog) {
        PlaybackService playbackService = this.playbackService;
        if (playbackService != null) {
            Playable playable = this.media;
            if (playable != null) {
                playbackService.seekTo((int) (((float) playable.getDuration()) * prog));
                setupPositionObserver();
            }
        }
    }

    protected void setScreenOn(boolean enable) {
    }

    public void playPause() {
        if (this.playbackService == null) {
            new PlaybackServiceStarter(this.activity, this.media).startWhenPrepared(true).streamIfLastWasStream().start();
            Log.w(TAG, "Play/Pause button was pressed, but playbackservice was null!");
            return;
        }
        int i = C07665.f22xd30cb86a[this.status.ordinal()];
        if (i != 6) {
            if (i != 8) {
                switch (i) {
                    case 2:
                        break;
                    case 3:
                        this.playbackService.pause(true, this.reinitOnPause);
                        break;
                    case 4:
                        PlaybackService playbackService = this.playbackService;
                        playbackService.setStartWhenPrepared(true ^ playbackService.isStartWhenPrepared());
                        if (this.reinitOnPause) {
                            if (!this.playbackService.isStartWhenPrepared()) {
                                this.playbackService.reinit();
                                break;
                            }
                            break;
                        }
                        break;
                    default:
                        break;
                }
            }
            this.playbackService.setStartWhenPrepared(true);
            this.playbackService.prepare();
        }
        this.playbackService.resume();
    }

    public boolean serviceAvailable() {
        return this.playbackService != null;
    }

    public int getPosition() {
        PlaybackService playbackService = this.playbackService;
        if (playbackService != null) {
            return playbackService.getCurrentPosition();
        }
        Playable playable = this.media;
        if (playable != null) {
            return playable.getPosition();
        }
        return -1;
    }

    public int getDuration() {
        PlaybackService playbackService = this.playbackService;
        if (playbackService != null) {
            return playbackService.getDuration();
        }
        Playable playable = this.media;
        if (playable != null) {
            return playable.getDuration();
        }
        return -1;
    }

    public Playable getMedia() {
        if (this.media == null) {
            this.media = PlayableUtils.createInstanceFromPreferences(this.activity);
        }
        return this.media;
    }

    public boolean sleepTimerActive() {
        PlaybackService playbackService = this.playbackService;
        return playbackService != null && playbackService.sleepTimerActive();
    }

    public boolean sleepTimerNotActive() {
        PlaybackService playbackService = this.playbackService;
        return (playbackService == null || playbackService.sleepTimerActive()) ? false : true;
    }

    public void disableSleepTimer() {
        PlaybackService playbackService = this.playbackService;
        if (playbackService != null) {
            playbackService.disableSleepTimer();
        }
    }

    public long getSleepTimerTimeLeft() {
        PlaybackService playbackService = this.playbackService;
        if (playbackService != null) {
            return playbackService.getSleepTimerTimeLeft();
        }
        return -1;
    }

    public void setSleepTimer(long time, boolean shakeToReset, boolean vibrate) {
        PlaybackService playbackService = this.playbackService;
        if (playbackService != null) {
            playbackService.setSleepTimer(time, shakeToReset, vibrate);
        }
    }

    public void seekToChapter(Chapter chapter) {
        PlaybackService playbackService = this.playbackService;
        if (playbackService != null) {
            playbackService.seekToChapter(chapter);
        }
    }

    public void seekTo(int time) {
        PlaybackService playbackService = this.playbackService;
        if (playbackService != null) {
            playbackService.seekTo(time);
        }
    }

    public void setVideoSurface(SurfaceHolder holder) {
        PlaybackService playbackService = this.playbackService;
        if (playbackService != null) {
            playbackService.setVideoSurface(holder);
        }
    }

    public PlayerStatus getStatus() {
        return this.status;
    }

    public boolean canSetPlaybackSpeed() {
        if (!MediaPlayer.isPrestoLibraryInstalled(this.activity.getApplicationContext())) {
            if (!UserPreferences.useSonic() && VERSION.SDK_INT < 23) {
                PlaybackService playbackService = this.playbackService;
                if (playbackService != null) {
                    if (playbackService.canSetSpeed()) {
                    }
                }
                return false;
            }
        }
        return true;
    }

    public void setPlaybackSpeed(float speed) {
        PlaybackService playbackService = this.playbackService;
        if (playbackService != null) {
            playbackService.setSpeed(speed);
        }
    }

    public void setSkipSilence(boolean skipSilence) {
        PlaybackService playbackService = this.playbackService;
        if (playbackService != null) {
            playbackService.skipSilence(skipSilence);
        }
    }

    public void setVolume(float leftVolume, float rightVolume) {
        PlaybackService playbackService = this.playbackService;
        if (playbackService != null) {
            playbackService.setVolume(leftVolume, rightVolume);
        }
    }

    public float getCurrentPlaybackSpeedMultiplier() {
        if (canSetPlaybackSpeed()) {
            return this.playbackService.getCurrentPlaybackSpeed();
        }
        return -1.0f;
    }

    public boolean canDownmix() {
        PlaybackService playbackService = this.playbackService;
        return playbackService != null && playbackService.canDownmix();
    }

    public void setDownmix(boolean enable) {
        PlaybackService playbackService = this.playbackService;
        if (playbackService != null) {
            playbackService.setDownmix(enable);
        }
    }

    public boolean isPlayingVideoLocally() {
        boolean z = false;
        if (PlaybackService.isCasting()) {
            return false;
        }
        if (this.playbackService != null) {
            if (PlaybackService.getCurrentMediaType() == MediaType.VIDEO) {
                z = true;
            }
            return z;
        }
        if (getMedia() != null && getMedia().getMediaType() == MediaType.VIDEO) {
            z = true;
        }
        return z;
    }

    public Pair<Integer, Integer> getVideoSize() {
        PlaybackService playbackService = this.playbackService;
        if (playbackService != null) {
            return playbackService.getVideoSize();
        }
        return null;
    }

    private boolean isConnectedToPlaybackService() {
        return this.playbackService != null;
    }

    public void notifyVideoSurfaceAbandoned() {
        PlaybackService playbackService = this.playbackService;
        if (playbackService != null) {
            playbackService.notifyVideoSurfaceAbandoned();
        }
    }

    public void reinitServiceIfPaused() {
        PlaybackService playbackService = this.playbackService;
        if (playbackService == null) {
            return;
        }
        if (!playbackService.isStreaming()) {
            return;
        }
        if (!PlaybackService.isCasting()) {
            if (this.playbackService.getStatus() != PlayerStatus.PAUSED) {
                if (this.playbackService.getStatus() != PlayerStatus.PREPARING) {
                    return;
                }
                if (this.playbackService.isStartWhenPrepared()) {
                    return;
                }
            }
            this.playbackService.reinit();
        }
    }

    private void initServiceNotRunning() {
        this.mediaLoader = Maybe.create(new -$$Lambda$PlaybackController$L8zj4uoBiiJK6R-QY62x9IyOQq0()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new -$$Lambda$PlaybackController$unPf-KZ3N6z90Zv3wDvGi-e8ZiQ(), -$$Lambda$PlaybackController$JppEz3DGgroAG42bm6UGOm5d-VM.INSTANCE);
    }

    public static /* synthetic */ void lambda$initServiceNotRunning$4(PlaybackController playbackController, MaybeEmitter emitter) throws Exception {
        Playable media = playbackController.getMedia();
        if (media != null) {
            emitter.onSuccess(media);
        } else {
            emitter.onComplete();
        }
    }

    public static /* synthetic */ void lambda$initServiceNotRunning$5(PlaybackController playbackController, Playable media) throws Exception {
        if (media.getMediaType() == MediaType.AUDIO) {
            TypedArray res = playbackController.activity.obtainStyledAttributes(new int[]{C0734R.attr.av_play_big});
            playbackController.getPlayButton().setImageResource(res.getResourceId(0, C0734R.drawable.ic_play_arrow_grey600_36dp));
            res.recycle();
            return;
        }
        playbackController.getPlayButton().setImageResource(C0734R.drawable.ic_av_play_circle_outline_80dp);
    }
}
