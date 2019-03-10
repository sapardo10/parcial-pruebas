package de.danoeh.antennapod.core.service.playback;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.media.MediaBrowserCompat.MediaItem;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaBrowserServiceCompat.BrowserRoot;
import android.support.v4.media.MediaBrowserServiceCompat.Result;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.MediaSessionCompat.Callback;
import android.support.v4.media.session.MediaSessionCompat.QueueItem;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import de.danoeh.antennapod.core.C0734R;
import de.danoeh.antennapod.core.ClientConfig;
import de.danoeh.antennapod.core.event.MessageEvent;
import de.danoeh.antennapod.core.event.ServiceEvent;
import de.danoeh.antennapod.core.event.ServiceEvent.Action;
import de.danoeh.antennapod.core.feed.Chapter;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.feed.FeedMedia;
import de.danoeh.antennapod.core.feed.MediaType;
import de.danoeh.antennapod.core.feed.SearchResult;
import de.danoeh.antennapod.core.glide.ApGlideSettings;
import de.danoeh.antennapod.core.preferences.PlaybackPreferences;
import de.danoeh.antennapod.core.preferences.SleepTimerPreferences;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.receiver.MediaButtonReceiver;
import de.danoeh.antennapod.core.service.PlayerWidgetJobService;
import de.danoeh.antennapod.core.service.playback.PlaybackServiceMediaPlayer.PSMPCallback;
import de.danoeh.antennapod.core.service.playback.PlaybackServiceMediaPlayer.PSMPInfo;
import de.danoeh.antennapod.core.service.playback.PlaybackServiceTaskManager.PSTMCallback;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.storage.DBTasks;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.core.storage.FeedSearcher;
import de.danoeh.antennapod.core.storage.PodDBAdapter;
import de.danoeh.antennapod.core.util.IntentUtils;
import de.danoeh.antennapod.core.util.QueueAccess;
import de.danoeh.antennapod.core.util.gui.NotificationUtils;
import de.danoeh.antennapod.core.util.playback.ExternalMedia;
import de.danoeh.antennapod.core.util.playback.Playable;
import de.danoeh.antennapod.core.util.playback.Playable.PlayableException;
import de.danoeh.antennapod.core.util.playback.Playable.PlayableUtils;
import de.greenrobot.event.EventBus;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class PlaybackService extends MediaBrowserServiceCompat {
    public static final String ACTION_PAUSE_PLAY_CURRENT_EPISODE = "action.de.danoeh.antennapod.core.service.pausePlayCurrentEpisode";
    public static final String ACTION_PLAYER_NOTIFICATION = "action.de.danoeh.antennapod.core.service.playerNotification";
    public static final String ACTION_PLAYER_STATUS_CHANGED = "action.de.danoeh.antennapod.core.service.playerStatusChanged";
    public static final String ACTION_RESUME_PLAY_CURRENT_EPISODE = "action.de.danoeh.antennapod.core.service.resumePlayCurrentEpisode";
    public static final String ACTION_SHUTDOWN_PLAYBACK_SERVICE = "action.de.danoeh.antennapod.core.service.actionShutdownPlaybackService";
    public static final String ACTION_SKIP_CURRENT_EPISODE = "action.de.danoeh.antennapod.core.service.skipCurrentEpisode";
    private static final String AVRCP_ACTION_META_CHANGED = "com.android.music.metachanged";
    private static final String AVRCP_ACTION_PLAYER_STATUS_CHANGED = "com.android.music.playstatechanged";
    private static final String CUSTOM_ACTION_FAST_FORWARD = "action.de.danoeh.antennapod.core.service.fastForward";
    private static final String CUSTOM_ACTION_REWIND = "action.de.danoeh.antennapod.core.service.rewind";
    private static final String EXTRA_CAST_DISCONNECT = "extra.de.danoeh.antennapod.core.service.castDisconnect";
    public static final int EXTRA_CODE_AUDIO = 1;
    public static final int EXTRA_CODE_CAST = 3;
    public static final int EXTRA_CODE_VIDEO = 2;
    public static final String EXTRA_NEW_PLAYER_STATUS = "extra.de.danoeh.antennapod.service.playerStatusChanged.newStatus";
    public static final String EXTRA_NOTIFICATION_CODE = "extra.de.danoeh.antennapod.core.service.notificationCode";
    public static final String EXTRA_NOTIFICATION_TYPE = "extra.de.danoeh.antennapod.core.service.notificationType";
    public static final String EXTRA_PLAYABLE = "PlaybackService.PlayableExtra";
    public static final String EXTRA_PREPARE_IMMEDIATELY = "extra.de.danoeh.antennapod.core.service.prepareImmediately";
    public static final String EXTRA_SHOULD_STREAM = "extra.de.danoeh.antennapod.core.service.shouldStream";
    public static final String EXTRA_START_WHEN_PREPARED = "extra.de.danoeh.antennapod.core.service.startWhenPrepared";
    public static final int INVALID_TIME = -1;
    private static final int NOTIFICATION_ID = 1;
    public static final int NOTIFICATION_TYPE_BUFFER_END = 6;
    public static final int NOTIFICATION_TYPE_BUFFER_START = 5;
    public static final int NOTIFICATION_TYPE_BUFFER_UPDATE = 2;
    public static final int NOTIFICATION_TYPE_ERROR = 0;
    public static final int NOTIFICATION_TYPE_INFO = 1;
    public static final int NOTIFICATION_TYPE_PLAYBACK_END = 7;
    public static final int NOTIFICATION_TYPE_PLAYBACK_SPEED_CHANGE = 8;
    public static final int NOTIFICATION_TYPE_RELOAD = 3;
    public static final int NOTIFICATION_TYPE_SET_SPEED_ABILITY_CHANGED = 9;
    public static final int NOTIFICATION_TYPE_SHOW_TOAST = 10;
    public static final int NOTIFICATION_TYPE_SLEEPTIMER_UPDATE = 4;
    private static final String TAG = "PlaybackService";
    private static volatile MediaType currentMediaType = MediaType.UNKNOWN;
    private static volatile boolean isCasting = false;
    public static boolean isRunning = false;
    public static boolean started = false;
    private static boolean transientPause = false;
    private final BroadcastReceiver audioBecomingNoisy = new C07557();
    private final BroadcastReceiver autoStateUpdated = new C07524();
    private final BroadcastReceiver bluetoothStateUpdated = new C07546();
    private PlaybackServiceFlavorHelper flavorHelper;
    private final FlavorHelperCallback flavorHelperCallback = new FlavorHelperCallback() {
        public PSMPCallback getMediaPlayerCallback() {
            return PlaybackService.this.mediaPlayerCallback;
        }

        public void setMediaPlayer(PlaybackServiceMediaPlayer mediaPlayer) {
            PlaybackService.this.mediaPlayer = mediaPlayer;
        }

        public PlaybackServiceMediaPlayer getMediaPlayer() {
            return PlaybackService.this.mediaPlayer;
        }

        public void setIsCasting(boolean isCasting) {
            PlaybackService.isCasting = isCasting;
        }

        public void sendNotificationBroadcast(int type, int code) {
            PlaybackService.this.sendNotificationBroadcast(type, code);
        }

        public void saveCurrentPosition(boolean fromMediaPlayer, Playable playable, int position) {
            PlaybackService.this.saveCurrentPosition(fromMediaPlayer, playable, position);
        }

        public void setupNotification(boolean connected, PSMPInfo info) {
            if (connected) {
                PlaybackService.this.setupNotification(info);
                return;
            }
            PlayerStatus status = info.playerStatus;
            if (status != PlayerStatus.PLAYING && status != PlayerStatus.SEEKING && status != PlayerStatus.PREPARING) {
                if (UserPreferences.isPersistNotify()) {
                }
                if (!UserPreferences.isPersistNotify()) {
                    PlaybackService.this.stopForeground(true);
                }
            }
            if (VERSION.SDK_INT >= 16) {
                PlaybackService.this.setupNotification(info);
                return;
            }
            if (!UserPreferences.isPersistNotify()) {
                PlaybackService.this.stopForeground(true);
            }
        }

        public MediaSessionCompat getMediaSession() {
            return PlaybackService.this.mediaSession;
        }

        public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
            return PlaybackService.this.registerReceiver(receiver, filter);
        }

        public void unregisterReceiver(BroadcastReceiver receiver) {
            PlaybackService.this.unregisterReceiver(receiver);
        }
    };
    private final BroadcastReceiver headsetDisconnected = new C07535();
    private final IBinder mBinder = new LocalBinder();
    private PlaybackServiceMediaPlayer mediaPlayer;
    private final PSMPCallback mediaPlayerCallback = new C10292();
    private MediaSessionCompat mediaSession;
    private Thread mediaSessionSetupThread;
    private Thread notificationSetupThread;
    private final BroadcastReceiver pausePlayCurrentEpisodeReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(), PlaybackService.ACTION_PAUSE_PLAY_CURRENT_EPISODE)) {
                Log.d(PlaybackService.TAG, "Received PAUSE_PLAY_CURRENT_EPISODE intent");
                PlaybackService.this.mediaPlayer.pause(false, false);
            }
        }
    };
    private final BroadcastReceiver pauseResumeCurrentEpisodeReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(), PlaybackService.ACTION_RESUME_PLAY_CURRENT_EPISODE)) {
                Log.d(PlaybackService.TAG, "Received RESUME_PLAY_CURRENT_EPISODE intent");
                PlaybackService.this.mediaPlayer.resume();
            }
        }
    };
    private final OnSharedPreferenceChangeListener prefListener = new -$$Lambda$PlaybackService$dlRsv4jogcQmYXeP_hk3FKrHUHs();
    private final Callback sessionCallback = new Callback() {
        private static final String TAG = "MediaSessionCompat";

        public void onPlay() {
            Log.d(TAG, "onPlay()");
            PlayerStatus status = PlaybackService.this.getStatus();
            if (status != PlayerStatus.PAUSED) {
                if (status != PlayerStatus.PREPARED) {
                    if (status == PlayerStatus.INITIALIZED) {
                        PlaybackService.this.setStartWhenPrepared(true);
                        PlaybackService.this.prepare();
                        return;
                    }
                    return;
                }
            }
            PlaybackService.this.resume();
        }

        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("onPlayFromMediaId: mediaId: ");
            stringBuilder.append(mediaId);
            stringBuilder.append(" extras: ");
            stringBuilder.append(extras.toString());
            Log.d(str, stringBuilder.toString());
            FeedMedia p = DBReader.getFeedMedia(Long.parseLong(mediaId));
            if (p != null) {
                PlaybackService.this.mediaPlayer.playMediaObject(p, p.localFileAvailable() ^ true, true, true);
            }
        }

        public void onPlayFromSearch(String query, Bundle extras) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("onPlayFromSearch  query=");
            stringBuilder.append(query);
            stringBuilder.append(" extras=");
            stringBuilder.append(extras.toString());
            Log.d(str, stringBuilder.toString());
            for (SearchResult result : FeedSearcher.performSearch(PlaybackService.this.getBaseContext(), query, 0)) {
                try {
                    FeedMedia p = ((FeedItem) result.getComponent()).getMedia();
                    PlaybackService.this.mediaPlayer.playMediaObject(p, !p.localFileAvailable(), true, true);
                    return;
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());
                    e.printStackTrace();
                }
            }
            onPlay();
        }

        public void onPause() {
            Log.d(TAG, "onPause()");
            if (PlaybackService.this.getStatus() == PlayerStatus.PLAYING) {
                PlaybackService.this.pause(UserPreferences.isPersistNotify() ^ true, true);
            }
        }

        public void onStop() {
            Log.d(TAG, "onStop()");
            PlaybackService.this.mediaPlayer.stopPlayback(true);
        }

        public void onSkipToPrevious() {
            Log.d(TAG, "onSkipToPrevious()");
            PlaybackService.this.seekDelta((-UserPreferences.getRewindSecs()) * 1000);
        }

        public void onRewind() {
            Log.d(TAG, "onRewind()");
            PlaybackService.this.seekDelta((-UserPreferences.getRewindSecs()) * 1000);
        }

        public void onFastForward() {
            Log.d(TAG, "onFastForward()");
            PlaybackService.this.seekDelta(UserPreferences.getFastForwardSecs() * 1000);
        }

        public void onSkipToNext() {
            Log.d(TAG, "onSkipToNext()");
            if (UserPreferences.shouldHardwareButtonSkip()) {
                PlaybackService.this.mediaPlayer.skip();
            } else {
                PlaybackService.this.seekDelta(UserPreferences.getFastForwardSecs() * 1000);
            }
        }

        public void onSeekTo(long pos) {
            Log.d(TAG, "onSeekTo()");
            PlaybackService.this.seekTo((int) pos);
        }

        public boolean onMediaButtonEvent(Intent mediaButton) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("onMediaButtonEvent(");
            stringBuilder.append(mediaButton);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
            if (mediaButton != null) {
                KeyEvent keyEvent = (KeyEvent) mediaButton.getParcelableExtra("android.intent.extra.KEY_EVENT");
                if (keyEvent != null) {
                    if (keyEvent.getAction() == 0) {
                        if (keyEvent.getRepeatCount() == 0) {
                            return PlaybackService.this.handleKeycode(keyEvent.getKeyCode(), false);
                        }
                    }
                }
            }
            return false;
        }

        public void onCustomAction(String action, Bundle extra) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("onCustomAction(");
            stringBuilder.append(action);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
            if (PlaybackService.CUSTOM_ACTION_FAST_FORWARD.equals(action)) {
                onFastForward();
            } else if (PlaybackService.CUSTOM_ACTION_REWIND.equals(action)) {
                onRewind();
            }
        }
    };
    private final BroadcastReceiver shutdownReceiver = new C07568();
    private final BroadcastReceiver skipCurrentEpisodeReceiver = new C07579();
    private PlaybackServiceTaskManager taskManager;
    private final PSTMCallback taskManagerCallback = new C10281();

    /* renamed from: de.danoeh.antennapod.core.service.playback.PlaybackService$4 */
    class C07524 extends BroadcastReceiver {
        C07524() {
        }

        public void onReceive(Context context, Intent intent) {
            String status = intent.getStringExtra("media_connection_status");
            boolean isConnectedToCar = "media_connected".equals(status);
            String str = PlaybackService.TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Received Auto Connection update: ");
            stringBuilder.append(status);
            Log.d(str, stringBuilder.toString());
            if (isConnectedToCar) {
                PlayerStatus playerStatus = PlaybackService.this.mediaPlayer.getPlayerStatus();
                if (playerStatus != PlayerStatus.PAUSED) {
                    if (playerStatus != PlayerStatus.PREPARED) {
                        if (playerStatus == PlayerStatus.PREPARING) {
                            PlaybackService.this.mediaPlayer.setStartWhenPrepared(true ^ PlaybackService.this.mediaPlayer.isStartWhenPrepared());
                            return;
                        } else if (playerStatus == PlayerStatus.INITIALIZED) {
                            PlaybackService.this.mediaPlayer.setStartWhenPrepared(true);
                            PlaybackService.this.mediaPlayer.prepare();
                            return;
                        } else {
                            return;
                        }
                    }
                }
                PlaybackService.this.mediaPlayer.resume();
                return;
            }
            Log.d(PlaybackService.TAG, "Car was unplugged during playback.");
            PlaybackService.this.pauseIfPauseOnDisconnect();
        }
    }

    /* renamed from: de.danoeh.antennapod.core.service.playback.PlaybackService$5 */
    class C07535 extends BroadcastReceiver {
        private static final int PLUGGED = 1;
        private static final String TAG = "headsetDisconnected";
        private static final int UNPLUGGED = 0;

        C07535() {
        }

        public void onReceive(Context context, Intent intent) {
            if (!isInitialStickyBroadcast()) {
                if (TextUtils.equals(intent.getAction(), "android.intent.action.HEADSET_PLUG")) {
                    int state = intent.getIntExtra("state", -1);
                    if (state != -1) {
                        String str = TAG;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Headset plug event. State is ");
                        stringBuilder.append(state);
                        Log.d(str, stringBuilder.toString());
                        if (state == 0) {
                            Log.d(TAG, "Headset was unplugged during playback.");
                            PlaybackService.this.pauseIfPauseOnDisconnect();
                        } else if (state == 1) {
                            Log.d(TAG, "Headset was plugged in during playback.");
                            PlaybackService.this.unpauseIfPauseOnDisconnect(false);
                        }
                    } else {
                        Log.e(TAG, "Received invalid ACTION_HEADSET_PLUG intent");
                    }
                }
            }
        }
    }

    /* renamed from: de.danoeh.antennapod.core.service.playback.PlaybackService$6 */
    class C07546 extends BroadcastReceiver {
        C07546() {
        }

        public void onReceive(Context context, Intent intent) {
            if (!TextUtils.equals(intent.getAction(), "android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED")) {
                return;
            }
            if (intent.getIntExtra("android.bluetooth.profile.extra.STATE", -1) == 2) {
                Log.d(PlaybackService.TAG, "Received bluetooth connection intent");
                PlaybackService.this.unpauseIfPauseOnDisconnect(true);
            }
        }
    }

    /* renamed from: de.danoeh.antennapod.core.service.playback.PlaybackService$7 */
    class C07557 extends BroadcastReceiver {
        C07557() {
        }

        public void onReceive(Context context, Intent intent) {
            Log.d(PlaybackService.TAG, "Pausing playback because audio is becoming noisy");
            PlaybackService.this.pauseIfPauseOnDisconnect();
        }
    }

    /* renamed from: de.danoeh.antennapod.core.service.playback.PlaybackService$8 */
    class C07568 extends BroadcastReceiver {
        C07568() {
        }

        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(), PlaybackService.ACTION_SHUTDOWN_PLAYBACK_SERVICE)) {
                PlaybackService.this.stopService();
            }
        }
    }

    /* renamed from: de.danoeh.antennapod.core.service.playback.PlaybackService$9 */
    class C07579 extends BroadcastReceiver {
        C07579() {
        }

        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(), PlaybackService.ACTION_SKIP_CURRENT_EPISODE)) {
                Log.d(PlaybackService.TAG, "Received SKIP_CURRENT_EPISODE intent");
                PlaybackService.this.mediaPlayer.skip();
            }
        }
    }

    interface FlavorHelperCallback {
        PlaybackServiceMediaPlayer getMediaPlayer();

        PSMPCallback getMediaPlayerCallback();

        MediaSessionCompat getMediaSession();

        Intent registerReceiver(BroadcastReceiver broadcastReceiver, IntentFilter intentFilter);

        void saveCurrentPosition(boolean z, Playable playable, int i);

        void sendNotificationBroadcast(int i, int i2);

        void setIsCasting(boolean z);

        void setMediaPlayer(PlaybackServiceMediaPlayer playbackServiceMediaPlayer);

        void setupNotification(boolean z, PSMPInfo pSMPInfo);

        void unregisterReceiver(BroadcastReceiver broadcastReceiver);
    }

    public class LocalBinder extends Binder {
        public PlaybackService getService() {
            return PlaybackService.this;
        }
    }

    /* renamed from: de.danoeh.antennapod.core.service.playback.PlaybackService$1 */
    class C10281 implements PSTMCallback {
        C10281() {
        }

        public void positionSaverTick() {
            PlaybackService.this.saveCurrentPosition(true, null, -1);
        }

        public void onSleepTimerAlmostExpired() {
            PlaybackService.this.mediaPlayer.setVolume(UserPreferences.getLeftVolume() * 0.1f, UserPreferences.getRightVolume() * 0.1f);
        }

        public void onSleepTimerExpired() {
            PlaybackService.this.mediaPlayer.pause(true, true);
            PlaybackService.this.mediaPlayer.setVolume(UserPreferences.getLeftVolume(), UserPreferences.getRightVolume());
            PlaybackService.this.sendNotificationBroadcast(4, 0);
        }

        public void onSleepTimerReset() {
            PlaybackService.this.mediaPlayer.setVolume(UserPreferences.getLeftVolume(), UserPreferences.getRightVolume());
        }

        public void onWidgetUpdaterTick() {
            PlayerWidgetJobService.updateWidget(PlaybackService.this.getBaseContext());
        }

        public void onChapterLoaded(Playable media) {
            PlaybackService.this.sendNotificationBroadcast(3, 0);
        }
    }

    /* renamed from: de.danoeh.antennapod.core.service.playback.PlaybackService$2 */
    class C10292 implements PSMPCallback {
        C10292() {
        }

        public void statusChanged(PSMPInfo newInfo) {
            if (PlaybackService.this.mediaPlayer != null) {
                PlaybackService.currentMediaType = PlaybackService.this.mediaPlayer.getCurrentMediaType();
            } else {
                PlaybackService.currentMediaType = MediaType.UNKNOWN;
            }
            PlaybackService.this.updateMediaSession(newInfo.playerStatus);
            switch (newInfo.playerStatus) {
                case INITIALIZED:
                    PlaybackService.this.writePlaybackPreferences();
                    break;
                case PREPARED:
                    PlaybackService.this.taskManager.startChapterLoader(newInfo.playable);
                    break;
                case PAUSED:
                    if ((UserPreferences.isPersistNotify() || PlaybackService.isCasting) && VERSION.SDK_INT >= 16) {
                        PlaybackService.this.setupNotification(newInfo);
                    } else if (!UserPreferences.isPersistNotify() && !PlaybackService.isCasting) {
                        PlaybackService.this.stopForeground(true);
                    }
                    PlaybackService.this.writePlayerStatusPlaybackPreferences();
                    break;
                case STOPPED:
                    break;
                case PLAYING:
                    PlaybackService.this.writePlayerStatusPlaybackPreferences();
                    PlaybackService.this.setupNotification(newInfo);
                    PlaybackService.started = true;
                    if (newInfo.oldPlayerStatus != null && newInfo.oldPlayerStatus != PlayerStatus.SEEKING) {
                        if (SleepTimerPreferences.autoEnable() && !PlaybackService.this.sleepTimerActive()) {
                            PlaybackService.this.setSleepTimer(SleepTimerPreferences.timerMillis(), SleepTimerPreferences.shakeToReset(), SleepTimerPreferences.vibrate());
                            break;
                        }
                        break;
                    }
                    break;
                case ERROR:
                    PlaybackService.this.writePlaybackPreferencesNoMediaPlaying();
                    PlaybackService.this.stopService();
                    break;
                default:
                    break;
            }
            IntentUtils.sendLocalBroadcast(PlaybackService.this.getApplicationContext(), PlaybackService.ACTION_PLAYER_STATUS_CHANGED);
            PlayerWidgetJobService.updateWidget(PlaybackService.this.getBaseContext());
            PlaybackService.this.bluetoothNotifyChange(newInfo, PlaybackService.AVRCP_ACTION_PLAYER_STATUS_CHANGED);
            PlaybackService.this.bluetoothNotifyChange(newInfo, PlaybackService.AVRCP_ACTION_META_CHANGED);
        }

        public void shouldStop() {
            PlaybackService.this.stopService();
        }

        public void playbackSpeedChanged(float s) {
            PlaybackService.this.sendNotificationBroadcast(8, 0);
        }

        public void setSpeedAbilityChanged() {
            PlaybackService.this.sendNotificationBroadcast(9, 0);
        }

        public void onBufferingUpdate(int percent) {
            PlaybackService.this.sendNotificationBroadcast(2, percent);
        }

        public void onMediaChanged(boolean reloadUI) {
            Log.d(PlaybackService.TAG, "reloadUI callback reached");
            if (reloadUI) {
                PlaybackService.this.sendNotificationBroadcast(3, 0);
            }
            PlaybackService playbackService = PlaybackService.this;
            playbackService.updateMediaSessionMetadata(playbackService.getPlayable());
        }

        public boolean onMediaPlayerInfo(int code, @StringRes int resourceId) {
            switch (code) {
                case 701:
                    PlaybackService.this.sendNotificationBroadcast(5, 0);
                    return true;
                case 702:
                    PlaybackService.this.sendNotificationBroadcast(6, 0);
                    return true;
                default:
                    return PlaybackService.this.flavorHelper.onMediaPlayerInfo(PlaybackService.this, code, resourceId);
            }
        }

        public boolean onMediaPlayerError(Object inObj, int what, int extra) {
            String TAG = "PlaybackSvc.onErrorLtsn";
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("An error has occured: ");
            stringBuilder.append(what);
            stringBuilder.append(StringUtils.SPACE);
            stringBuilder.append(extra);
            Log.w("PlaybackSvc.onErrorLtsn", stringBuilder.toString());
            if (PlaybackService.this.mediaPlayer.getPlayerStatus() == PlayerStatus.PLAYING) {
                PlaybackService.this.mediaPlayer.pause(true, false);
            }
            PlaybackService.this.sendNotificationBroadcast(0, what);
            PlaybackService.this.writePlaybackPreferencesNoMediaPlaying();
            PlaybackService.this.stopService();
            return true;
        }

        public void onPostPlayback(@NonNull Playable media, boolean ended, boolean skipped, boolean playingNext) {
            PlaybackService.this.onPostPlayback(media, ended, skipped, playingNext);
        }

        public void onPlaybackStart(@NonNull Playable playable, int position) {
            PlaybackService.this.taskManager.startWidgetUpdater();
            if (position != -1) {
                playable.setPosition(position);
            }
            playable.onPlaybackStart();
            PlaybackService.this.taskManager.startPositionSaver();
        }

        public void onPlaybackPause(Playable playable, int position) {
            boolean z;
            PlaybackService.this.taskManager.cancelPositionSaver();
            PlaybackService playbackService = PlaybackService.this;
            if (position != -1) {
                if (playable != null) {
                    z = false;
                    playbackService.saveCurrentPosition(z, playable, position);
                    PlaybackService.this.taskManager.cancelWidgetUpdater();
                    if (playable != null) {
                        playable.onPlaybackPause(PlaybackService.this.getApplicationContext());
                    }
                }
            }
            z = true;
            playbackService.saveCurrentPosition(z, playable, position);
            PlaybackService.this.taskManager.cancelWidgetUpdater();
            if (playable != null) {
                playable.onPlaybackPause(PlaybackService.this.getApplicationContext());
            }
        }

        public Playable getNextInQueue(Playable currentMedia) {
            return PlaybackService.this.getNextInQueue(currentMedia);
        }

        public void onPlaybackEnded(MediaType mediaType, boolean stopPlaying) {
            PlaybackService.this.onPlaybackEnded(mediaType, stopPlaying);
        }
    }

    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "Received onUnbind event");
        return super.onUnbind(intent);
    }

    public static Intent getPlayerActivityIntent(Context context) {
        if (isRunning) {
            return ClientConfig.playbackServiceCallbacks.getPlayerActivityIntent(context, currentMediaType, isCasting);
        }
        if (PlaybackPreferences.getCurrentEpisodeIsVideo()) {
            return ClientConfig.playbackServiceCallbacks.getPlayerActivityIntent(context, MediaType.VIDEO, isCasting);
        }
        return ClientConfig.playbackServiceCallbacks.getPlayerActivityIntent(context, MediaType.AUDIO, isCasting);
    }

    public static Intent getPlayerActivityIntent(Context context, Playable media) {
        return ClientConfig.playbackServiceCallbacks.getPlayerActivityIntent(context, media.getMediaType(), isCasting);
    }

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created.");
        isRunning = true;
        startForeground(1, createBasicNotification().build());
        registerReceiver(this.autoStateUpdated, new IntentFilter("com.google.android.gms.car.media.STATUS"));
        registerReceiver(this.headsetDisconnected, new IntentFilter("android.intent.action.HEADSET_PLUG"));
        registerReceiver(this.shutdownReceiver, new IntentFilter(ACTION_SHUTDOWN_PLAYBACK_SERVICE));
        registerReceiver(this.bluetoothStateUpdated, new IntentFilter("android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED"));
        registerReceiver(this.audioBecomingNoisy, new IntentFilter("android.media.AUDIO_BECOMING_NOISY"));
        registerReceiver(this.skipCurrentEpisodeReceiver, new IntentFilter(ACTION_SKIP_CURRENT_EPISODE));
        registerReceiver(this.pausePlayCurrentEpisodeReceiver, new IntentFilter(ACTION_PAUSE_PLAY_CURRENT_EPISODE));
        registerReceiver(this.pauseResumeCurrentEpisodeReceiver, new IntentFilter(ACTION_RESUME_PLAY_CURRENT_EPISODE));
        this.taskManager = new PlaybackServiceTaskManager(this, this.taskManagerCallback);
        this.flavorHelper = new PlaybackServiceFlavorHelper(this, this.flavorHelperCallback);
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this.prefListener);
        ComponentName eventReceiver = new ComponentName(getApplicationContext(), MediaButtonReceiver.class);
        Intent mediaButtonIntent = new Intent("android.intent.action.MEDIA_BUTTON");
        mediaButtonIntent.setComponent(eventReceiver);
        this.mediaSession = new MediaSessionCompat(getApplicationContext(), TAG, eventReceiver, PendingIntent.getBroadcast(this, null, mediaButtonIntent, 134217728));
        setSessionToken(this.mediaSession.getSessionToken());
        try {
            this.mediaSession.setCallback(this.sessionCallback);
            this.mediaSession.setFlags(3);
        } catch (NullPointerException npe) {
            Log.e(TAG, "NullPointerException while setting up MediaSession");
            npe.printStackTrace();
        }
        List<QueueItem> queueItems = new ArrayList();
        try {
            for (FeedItem feedItem : this.taskManager.getQueue()) {
                if (feedItem.getMedia() != null) {
                    queueItems.add(new QueueItem(feedItem.getMedia().getMediaItem().getDescription(), feedItem.getId()));
                }
            }
            this.mediaSession.setQueue(queueItems);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.flavorHelper.initializeMediaPlayer(this);
        this.mediaSession.setActive(true);
        EventBus.getDefault().post(new ServiceEvent(Action.SERVICE_STARTED));
    }

    private Builder createBasicNotification() {
        return new Builder(this, NotificationUtils.CHANNEL_ID_PLAYING).setContentTitle(getString(C0734R.string.app_name)).setContentText("Service is running").setOngoing(false).setContentIntent(PendingIntent.getActivity(this, 0, getPlayerActivityIntent(this), 134217728)).setWhen(0).setSmallIcon(ClientConfig.playbackServiceCallbacks.getNotificationIconResource(getApplicationContext())).setPriority(-2);
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service is about to be destroyed");
        stopForeground(true);
        isRunning = false;
        started = false;
        currentMediaType = MediaType.UNKNOWN;
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this.prefListener);
        MediaSessionCompat mediaSessionCompat = this.mediaSession;
        if (mediaSessionCompat != null) {
            mediaSessionCompat.release();
        }
        unregisterReceiver(this.autoStateUpdated);
        unregisterReceiver(this.headsetDisconnected);
        unregisterReceiver(this.shutdownReceiver);
        unregisterReceiver(this.bluetoothStateUpdated);
        unregisterReceiver(this.audioBecomingNoisy);
        unregisterReceiver(this.skipCurrentEpisodeReceiver);
        unregisterReceiver(this.pausePlayCurrentEpisodeReceiver);
        unregisterReceiver(this.pauseResumeCurrentEpisodeReceiver);
        this.flavorHelper.removeCastConsumer();
        this.flavorHelper.unregisterWifiBroadcastReceiver();
        this.mediaPlayer.shutdown();
        this.taskManager.shutdown();
    }

    private void stopService() {
        stopForeground(true);
        stopSelf();
    }

    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, Bundle rootHints) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("OnGetRoot: clientPackageName=");
        stringBuilder.append(clientPackageName);
        stringBuilder.append("; clientUid=");
        stringBuilder.append(clientUid);
        stringBuilder.append(" ; rootHints=");
        stringBuilder.append(rootHints);
        Log.d(str, stringBuilder.toString());
        return new BrowserRoot(getResources().getString(C0734R.string.app_name), null);
    }

    private MediaItem createBrowsableMediaItemForRoot() {
        return new MediaItem(new MediaDescriptionCompat.Builder().setMediaId(getResources().getString(C0734R.string.queue_label)).setTitle(getResources().getString(C0734R.string.queue_label)).build(), 1);
    }

    private MediaItem createBrowsableMediaItemForFeed(Feed feed) {
        MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("FeedId:");
        stringBuilder.append(Long.toString(feed.getId()));
        builder = builder.setMediaId(stringBuilder.toString()).setTitle(feed.getTitle()).setDescription(feed.getDescription()).setSubtitle(feed.getCustomTitle());
        if (feed.getImageLocation() != null) {
            builder.setIconUri(Uri.parse(feed.getImageLocation()));
        }
        if (feed.getLink() != null) {
            builder.setMediaUri(Uri.parse(feed.getLink()));
        }
        return new MediaItem(builder.build(), 1);
    }

    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaItem>> result) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("OnLoadChildren: parentMediaId=");
        stringBuilder.append(parentId);
        Log.d(str, stringBuilder.toString());
        List<MediaItem> mediaItems = new ArrayList();
        if (parentId.equals(getResources().getString(C0734R.string.app_name))) {
            try {
                if (!this.taskManager.getQueue().isEmpty()) {
                    mediaItems.add(createBrowsableMediaItemForRoot());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (Feed feed : DBReader.getFeedList()) {
                mediaItems.add(createBrowsableMediaItemForFeed(feed));
            }
        } else if (parentId.equals(getResources().getString(C0734R.string.queue_label))) {
            try {
                for (FeedItem feedItem : this.taskManager.getQueue()) {
                    mediaItems.add(feedItem.getMedia().getMediaItem());
                }
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }
        } else if (parentId.startsWith("FeedId:")) {
            for (FeedItem feedItem2 : DBReader.getFeedItemList(DBReader.getFeed(Long.valueOf(Long.parseLong(parentId.split(":")[1])).longValue()))) {
                if (feedItem2.getMedia() != null && feedItem2.getMedia().getMediaItem() != null) {
                    mediaItems.add(feedItem2.getMedia().getMediaItem());
                }
            }
        }
        result.sendResult(mediaItems);
    }

    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Received onBind event");
        if (intent.getAction() == null || !TextUtils.equals(intent.getAction(), MediaBrowserServiceCompat.SERVICE_INTERFACE)) {
            return this.mBinder;
        }
        return super.onBind(intent);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "OnStartCommand called");
        int keycode = intent.getIntExtra(MediaButtonReceiver.EXTRA_KEYCODE, -1);
        boolean castDisconnect = intent.getBooleanExtra(EXTRA_CAST_DISCONNECT, false);
        Playable playable = (Playable) intent.getParcelableExtra(EXTRA_PLAYABLE);
        if (keycode == -1 && playable == null && !castDisconnect) {
            Log.e(TAG, "PlaybackService was started with no arguments");
            stopService();
            return 2;
        }
        if ((flags & 1) != 0) {
            Log.d(TAG, "onStartCommand is a redelivered intent, calling stopForeground now.");
            stopForeground(true);
        } else {
            if (keycode != -1) {
                Log.d(TAG, "Received media button event");
                if (!handleKeycode(keycode, true)) {
                    stopService();
                    return 2;
                }
            } else if (!(this.flavorHelper.castDisconnect(castDisconnect) || playable == null)) {
                started = true;
                boolean stream = intent.getBooleanExtra(EXTRA_SHOULD_STREAM, true);
                boolean startWhenPrepared = intent.getBooleanExtra(EXTRA_START_WHEN_PREPARED, false);
                boolean prepareImmediately = intent.getBooleanExtra(EXTRA_PREPARE_IMMEDIATELY, false);
                sendNotificationBroadcast(3, 0);
                this.flavorHelper.castDisconnect(playable instanceof ExternalMedia);
                if (playable instanceof FeedMedia) {
                    playable = DBReader.getFeedMedia(((FeedMedia) playable).getId());
                }
                this.mediaPlayer.playMediaObject(playable, stream, startWhenPrepared, prepareImmediately);
                setupNotification(playable);
            }
            setupNotification(playable);
        }
        return 2;
    }

    private boolean handleKeycode(int keycode, boolean notificationButton) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Handling keycode: ");
        stringBuilder.append(keycode);
        Log.d(str, stringBuilder.toString());
        PSMPInfo info = this.mediaPlayer.getPSMPInfo();
        PlayerStatus status = info.playerStatus;
        if (keycode != 79) {
            switch (keycode) {
                case 85:
                    break;
                case 86:
                    if (status == PlayerStatus.PLAYING) {
                        this.mediaPlayer.pause(true, true);
                        started = false;
                    }
                    stopForeground(true);
                    return true;
                case 87:
                    if (!notificationButton) {
                        if (!UserPreferences.shouldHardwareButtonSkip()) {
                            seekDelta(UserPreferences.getFastForwardSecs() * 1000);
                            return true;
                        }
                    }
                    this.mediaPlayer.skip();
                    return true;
                case 88:
                    if (UserPreferences.shouldHardwarePreviousButtonRestart()) {
                        this.mediaPlayer.seekTo(0);
                    } else {
                        this.mediaPlayer.seekDelta((-UserPreferences.getRewindSecs()) * 1000);
                    }
                    return true;
                case 89:
                    this.mediaPlayer.seekDelta((-UserPreferences.getRewindSecs()) * 1000);
                    return true;
                case 90:
                    this.mediaPlayer.seekDelta(UserPreferences.getFastForwardSecs() * 1000);
                    return true;
                default:
                    switch (keycode) {
                        case 126:
                            if (status != PlayerStatus.PAUSED) {
                                if (status != PlayerStatus.PREPARED) {
                                    if (status == PlayerStatus.INITIALIZED) {
                                        this.mediaPlayer.setStartWhenPrepared(true);
                                        this.mediaPlayer.prepare();
                                    } else if (this.mediaPlayer.getPlayable() == null) {
                                        startPlayingFromPreferences();
                                    }
                                    return true;
                                }
                            }
                            this.mediaPlayer.resume();
                            return true;
                        case 127:
                            if (status == PlayerStatus.PLAYING) {
                                this.mediaPlayer.pause(UserPreferences.isPersistNotify() ^ true, true);
                            }
                            return true;
                        default:
                            String str2 = TAG;
                            StringBuilder stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("Unhandled key code: ");
                            stringBuilder2.append(keycode);
                            Log.d(str2, stringBuilder2.toString());
                            if (info.playable != null && info.playerStatus == PlayerStatus.PLAYING) {
                                Toast.makeText(this, String.format(getResources().getString(C0734R.string.unknown_media_key), new Object[]{Integer.valueOf(keycode)}), 0).show();
                            }
                            return false;
                    }
            }
        }
        if (status == PlayerStatus.PLAYING) {
            this.mediaPlayer.pause(UserPreferences.isPersistNotify() ^ true, true);
        } else {
            if (status != PlayerStatus.PAUSED) {
                if (status != PlayerStatus.PREPARED) {
                    if (status == PlayerStatus.PREPARING) {
                        PlaybackServiceMediaPlayer playbackServiceMediaPlayer = this.mediaPlayer;
                        playbackServiceMediaPlayer.setStartWhenPrepared(playbackServiceMediaPlayer.isStartWhenPrepared() ^ true);
                    } else if (status == PlayerStatus.INITIALIZED) {
                        this.mediaPlayer.setStartWhenPrepared(true);
                        this.mediaPlayer.prepare();
                    } else if (this.mediaPlayer.getPlayable() == null) {
                        startPlayingFromPreferences();
                    }
                }
            }
            this.mediaPlayer.resume();
        }
        return true;
    }

    private void startPlayingFromPreferences() {
        Playable playable = PlayableUtils.createInstanceFromPreferences(getApplicationContext());
        if (playable != null) {
            this.mediaPlayer.playMediaObject(playable, false, true, true);
            started = true;
            updateMediaSessionMetadata(playable);
        }
    }

    public void setVideoSurface(SurfaceHolder sh) {
        Log.d(TAG, "Setting display");
        this.mediaPlayer.setVideoSurface(sh);
    }

    public void notifyVideoSurfaceAbandoned() {
        this.mediaPlayer.pause(true, false);
        this.mediaPlayer.resetVideoSurface();
        setupNotification(getPlayable());
        stopForeground(UserPreferences.isPersistNotify() ^ true);
    }

    private Playable getNextInQueue(Playable currentMedia) {
        Playable playable = null;
        if (!(currentMedia instanceof FeedMedia)) {
            Log.d(TAG, "getNextInQueue(), but playable not an instance of FeedMedia, so not proceeding");
            return null;
        } else if (ClientConfig.playbackServiceCallbacks.useQueue()) {
            Log.d(TAG, "getNextInQueue()");
            FeedMedia media = (FeedMedia) currentMedia;
            try {
                media.loadMetadata();
                FeedItem item = media.getItem();
                if (item == null) {
                    Log.w(TAG, "getNextInQueue() with FeedMedia object whose FeedItem is null");
                    return null;
                }
                try {
                    FeedItem nextItem = DBTasks.getQueueSuccessorOfItem(item.getId(), this.taskManager.getQueue());
                    if (nextItem != null) {
                        playable = nextItem.getMedia();
                    }
                    return playable;
                } catch (InterruptedException e) {
                    Log.e(TAG, "Error handling the queue in order to retrieve the next item", e);
                    return null;
                }
            } catch (PlayableException e2) {
                Log.e(TAG, "Unable to load metadata to get next in queue", e2);
                return null;
            }
        } else {
            Log.d(TAG, "getNextInQueue(), but queue not in use by this app");
            return null;
        }
    }

    private void onPlaybackEnded(MediaType mediaType, boolean stopPlaying) {
        Log.d(TAG, "Playback ended");
        int i = 1;
        if (stopPlaying) {
            this.taskManager.cancelPositionSaver();
            writePlaybackPreferencesNoMediaPlaying();
            if (!isCasting) {
                stopForeground(true);
            }
        }
        if (mediaType == null) {
            sendNotificationBroadcast(7, 0);
            return;
        }
        if (isCasting) {
            i = 3;
        } else if (mediaType == MediaType.VIDEO) {
            i = 2;
        }
        sendNotificationBroadcast(3, i);
    }

    private void onPostPlayback(Playable playable, boolean ended, boolean skipped, boolean playingNext) {
        if (playable == null) {
            Log.e(TAG, "Cannot do post-playback processing: media was null");
            return;
        }
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onPostPlayback(): media=");
        stringBuilder.append(playable.getEpisodeTitle());
        Log.d(str, stringBuilder.toString());
        if (playable instanceof FeedMedia) {
            FeedMedia media = (FeedMedia) playable;
            FeedItem item = media.getItem();
            boolean smartMarkAsPlayed = playingNext && media.hasAlmostEnded();
            if (!ended && smartMarkAsPlayed) {
                Log.d(TAG, "smart mark as played");
            }
            if (!ended) {
                if (!smartMarkAsPlayed) {
                    media.onPlaybackPause(getApplicationContext());
                    if (item != null) {
                        if (!(ended || smartMarkAsPlayed)) {
                            if (skipped) {
                                if (!UserPreferences.shouldSkipKeepEpisode()) {
                                }
                            }
                        }
                        DBWriter.markItemPlayed(item, 1, ended);
                        try {
                            if (QueueAccess.ItemListAccess(this.taskManager.getQueue()).contains(item.getId())) {
                                DBWriter.removeQueueItem(this, item, ended);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (item.getFeed().getPreferences().getCurrentAutoDelete()) {
                            if (item.isTagged(FeedItem.TAG_FAVORITE)) {
                                if (UserPreferences.shouldFavoriteKeepEpisode()) {
                                }
                            }
                            DBWriter.deleteFeedMediaOfItem(this, media.getId());
                            Log.d(TAG, "Episode Deleted");
                        }
                    }
                    if (!(ended || skipped)) {
                        if (!playingNext) {
                            return;
                        }
                    }
                    DBWriter.addItemToPlaybackHistory(media);
                    return;
                }
            }
            media.onPlaybackCompleted(getApplicationContext());
            if (item != null) {
                if (skipped) {
                    if (!UserPreferences.shouldSkipKeepEpisode()) {
                        DBWriter.markItemPlayed(item, 1, ended);
                        if (QueueAccess.ItemListAccess(this.taskManager.getQueue()).contains(item.getId())) {
                            DBWriter.removeQueueItem(this, item, ended);
                        }
                        if (item.getFeed().getPreferences().getCurrentAutoDelete()) {
                            if (item.isTagged(FeedItem.TAG_FAVORITE)) {
                                if (UserPreferences.shouldFavoriteKeepEpisode()) {
                                }
                            }
                            DBWriter.deleteFeedMediaOfItem(this, media.getId());
                            Log.d(TAG, "Episode Deleted");
                        }
                    }
                }
            }
            if (!playingNext) {
                DBWriter.addItemToPlaybackHistory(media);
                return;
            }
            return;
        }
        Log.d(TAG, "Not doing post-playback processing: media not of type FeedMedia");
        if (ended) {
            playable.onPlaybackCompleted(getApplicationContext());
        } else {
            playable.onPlaybackPause(getApplicationContext());
        }
    }

    public void setSleepTimer(long waitingTime, boolean shakeToReset, boolean vibrate) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Setting sleep timer to ");
        stringBuilder.append(Long.toString(waitingTime));
        stringBuilder.append(" milliseconds");
        Log.d(str, stringBuilder.toString());
        this.taskManager.setSleepTimer(waitingTime, shakeToReset, vibrate);
        sendNotificationBroadcast(4, 0);
        EventBus.getDefault().post(new MessageEvent(getString(C0734R.string.sleep_timer_enabled_label), new -$$Lambda$fGp36j-wgbN6mIRD6IFVkCdiAKY()));
    }

    public void disableSleepTimer() {
        this.taskManager.disableSleepTimer();
        sendNotificationBroadcast(4, 0);
        EventBus.getDefault().post(new MessageEvent(getString(C0734R.string.sleep_timer_disabled_label)));
    }

    private void writePlaybackPreferencesNoMediaPlaying() {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        editor.putLong(PlaybackPreferences.PREF_CURRENTLY_PLAYING_MEDIA, -1);
        editor.putLong(PlaybackPreferences.PREF_CURRENTLY_PLAYING_FEED_ID, -1);
        editor.putLong(PlaybackPreferences.PREF_CURRENTLY_PLAYING_FEEDMEDIA_ID, -1);
        editor.putInt(PlaybackPreferences.PREF_CURRENT_PLAYER_STATUS, 3);
        editor.commit();
    }

    private int getCurrentPlayerStatusAsInt(PlayerStatus playerStatus) {
        int i = AnonymousClass14.f21xd30cb86a[playerStatus.ordinal()];
        if (i == 3) {
            return 2;
        }
        if (i != 5) {
            return 3;
        }
        return 1;
    }

    private void writePlaybackPreferences() {
        Log.d(TAG, "Writing playback preferences");
        Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        PSMPInfo info = this.mediaPlayer.getPSMPInfo();
        MediaType mediaType = this.mediaPlayer.getCurrentMediaType();
        boolean stream = this.mediaPlayer.isStreaming();
        int playerStatus = getCurrentPlayerStatusAsInt(info.playerStatus);
        if (info.playable != null) {
            editor.putLong(PlaybackPreferences.PREF_CURRENTLY_PLAYING_MEDIA, (long) info.playable.getPlayableType());
            editor.putBoolean(PlaybackPreferences.PREF_CURRENT_EPISODE_IS_STREAM, stream);
            editor.putBoolean(PlaybackPreferences.PREF_CURRENT_EPISODE_IS_VIDEO, mediaType == MediaType.VIDEO);
            if (info.playable instanceof FeedMedia) {
                FeedMedia fMedia = info.playable;
                editor.putLong(PlaybackPreferences.PREF_CURRENTLY_PLAYING_FEED_ID, fMedia.getItem().getFeed().getId());
                editor.putLong(PlaybackPreferences.PREF_CURRENTLY_PLAYING_FEEDMEDIA_ID, fMedia.getId());
            } else {
                editor.putLong(PlaybackPreferences.PREF_CURRENTLY_PLAYING_FEED_ID, -1);
                editor.putLong(PlaybackPreferences.PREF_CURRENTLY_PLAYING_FEEDMEDIA_ID, -1);
            }
            info.playable.writeToPreferences(editor);
        } else {
            editor.putLong(PlaybackPreferences.PREF_CURRENTLY_PLAYING_MEDIA, -1);
            editor.putLong(PlaybackPreferences.PREF_CURRENTLY_PLAYING_FEED_ID, -1);
            editor.putLong(PlaybackPreferences.PREF_CURRENTLY_PLAYING_FEEDMEDIA_ID, -1);
        }
        editor.putInt(PlaybackPreferences.PREF_CURRENT_PLAYER_STATUS, playerStatus);
        editor.commit();
    }

    private void writePlayerStatusPlaybackPreferences() {
        Log.d(TAG, "Writing player status playback preferences");
        Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        editor.putInt(PlaybackPreferences.PREF_CURRENT_PLAYER_STATUS, getCurrentPlayerStatusAsInt(this.mediaPlayer.getPlayerStatus()));
        editor.commit();
    }

    private void sendNotificationBroadcast(int type, int code) {
        Intent intent = new Intent(ACTION_PLAYER_NOTIFICATION);
        intent.putExtra(EXTRA_NOTIFICATION_TYPE, type);
        intent.putExtra(EXTRA_NOTIFICATION_CODE, code);
        intent.setPackage(getPackageName());
        sendBroadcast(intent);
    }

    private void updateMediaSession(PlayerStatus playerStatus) {
        int state;
        PlaybackStateCompat.Builder sessionState = new PlaybackStateCompat.Builder();
        if (playerStatus != null) {
            switch (playerStatus) {
                case INITIALIZED:
                case INDETERMINATE:
                    state = 0;
                    break;
                case PREPARED:
                case PAUSED:
                    state = 2;
                    break;
                case STOPPED:
                    state = 1;
                    break;
                case PLAYING:
                    state = 3;
                    break;
                case ERROR:
                    state = 7;
                    break;
                case SEEKING:
                    state = 4;
                    break;
                case PREPARING:
                case INITIALIZING:
                    state = 8;
                    break;
                default:
                    state = 0;
                    break;
            }
        }
        state = 0;
        sessionState.setState(state, (long) getCurrentPosition(), getCurrentPlaybackSpeed());
        long capabilities = 616;
        if (useSkipToPreviousForRewindInLockscreen()) {
            capabilities = 616 | 16;
        }
        sessionState.setActions(capabilities);
        this.flavorHelper.sessionStateAddActionForWear(sessionState, CUSTOM_ACTION_REWIND, getString(C0734R.string.rewind_label), 17301542);
        this.flavorHelper.sessionStateAddActionForWear(sessionState, CUSTOM_ACTION_FAST_FORWARD, getString(C0734R.string.fast_forward_label), 17301537);
        this.flavorHelper.mediaSessionSetExtraForWear(this.mediaSession);
        this.mediaSession.setPlaybackState(sessionState.build());
    }

    private static boolean useSkipToPreviousForRewindInLockscreen() {
        return UserPreferences.showRewindOnCompactNotification() && VERSION.SDK_INT < 21;
    }

    private void updateMediaSessionMetadata(Playable p) {
        if (p != null) {
            if (this.mediaSession != null) {
                Thread thread = this.mediaSessionSetupThread;
                if (thread != null) {
                    thread.interrupt();
                }
                this.mediaSessionSetupThread = new Thread(new -$$Lambda$PlaybackService$2UKemjeHs6ROwIYDpxlRRJ9gGNA(this, p));
                this.mediaSessionSetupThread.start();
            }
        }
    }

    public static /* synthetic */ void lambda$updateMediaSessionMetadata$0(PlaybackService playbackService, Playable p) {
        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
        builder.putString("android.media.metadata.ARTIST", p.getFeedTitle());
        builder.putString("android.media.metadata.TITLE", p.getEpisodeTitle());
        builder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, p.getFeedTitle());
        builder.putLong("android.media.metadata.DURATION", (long) p.getDuration());
        builder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, p.getEpisodeTitle());
        builder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, p.getFeedTitle());
        String imageLocation = p.getImageLocation();
        if (!TextUtils.isEmpty(imageLocation)) {
            if (UserPreferences.setLockscreenBackground()) {
                builder.putString(MediaMetadataCompat.METADATA_KEY_ART_URI, imageLocation);
                try {
                    builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ART, (Bitmap) Glide.with((Context) playbackService).asBitmap().load(imageLocation).apply(RequestOptions.diskCacheStrategyOf(ApGlideSettings.AP_DISK_CACHE_STRATEGY)).submit(Integer.MIN_VALUE, Integer.MIN_VALUE).get());
                } catch (Throwable tr) {
                    Log.e(TAG, Log.getStackTraceString(tr));
                }
            } else if (isCasting) {
                builder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, imageLocation);
            }
        }
        if (!Thread.currentThread().isInterrupted() && started) {
            playbackService.mediaSession.setSessionActivity(PendingIntent.getActivity(playbackService, 0, getPlayerActivityIntent(playbackService), 134217728));
            try {
                playbackService.mediaSession.setMetadata(builder.build());
            } catch (OutOfMemoryError e) {
                Log.e(TAG, "Setting media session metadata", e);
                builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ART, null);
                playbackService.mediaSession.setMetadata(builder.build());
            }
        }
    }

    private void setupNotification(PSMPInfo info) {
        setupNotification(info.playable);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized void setupNotification(final de.danoeh.antennapod.core.util.playback.Playable r3) {
        /*
        r2 = this;
        monitor-enter(r2);
        r0 = r2.notificationSetupThread;	 Catch:{ all -> 0x0034 }
        if (r0 == 0) goto L_0x000b;
    L_0x0005:
        r0 = r2.notificationSetupThread;	 Catch:{ all -> 0x0034 }
        r0.interrupt();	 Catch:{ all -> 0x0034 }
        goto L_0x000c;
    L_0x000c:
        if (r3 != 0) goto L_0x0021;
    L_0x000e:
        r0 = "PlaybackService";
        r1 = "setupNotification: playable is null";
        android.util.Log.d(r0, r1);	 Catch:{ all -> 0x0034 }
        r0 = started;	 Catch:{ all -> 0x0034 }
        if (r0 != 0) goto L_0x001e;
    L_0x001a:
        r2.stopService();	 Catch:{ all -> 0x0034 }
        goto L_0x001f;
    L_0x001f:
        monitor-exit(r2);
        return;
    L_0x0021:
        r0 = new de.danoeh.antennapod.core.service.playback.PlaybackService$3;	 Catch:{ all -> 0x0034 }
        r0.<init>(r3);	 Catch:{ all -> 0x0034 }
        r1 = new java.lang.Thread;	 Catch:{ all -> 0x0034 }
        r1.<init>(r0);	 Catch:{ all -> 0x0034 }
        r2.notificationSetupThread = r1;	 Catch:{ all -> 0x0034 }
        r1 = r2.notificationSetupThread;	 Catch:{ all -> 0x0034 }
        r1.start();	 Catch:{ all -> 0x0034 }
        monitor-exit(r2);
        return;
    L_0x0034:
        r3 = move-exception;
        monitor-exit(r2);
        throw r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.core.service.playback.PlaybackService.setupNotification(de.danoeh.antennapod.core.util.playback.Playable):void");
    }

    private PendingIntent getPendingIntentForMediaAction(int keycodeValue, int requestCode) {
        Intent intent = new Intent(this, PlaybackService.class);
        intent.putExtra(MediaButtonReceiver.EXTRA_KEYCODE, keycodeValue);
        return PendingIntent.getService(this, requestCode, intent, 134217728);
    }

    private synchronized void saveCurrentPosition(boolean fromMediaPlayer, Playable playable, int position) {
        int duration;
        if (fromMediaPlayer) {
            position = getCurrentPosition();
            duration = getDuration();
            playable = this.mediaPlayer.getPlayable();
        } else {
            duration = playable.getDuration();
        }
        if (position != -1 && duration != -1 && playable != null) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Saving current position to ");
            stringBuilder.append(position);
            Log.d(str, stringBuilder.toString());
            playable.saveCurrentPosition(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()), position, System.currentTimeMillis());
        }
    }

    public boolean sleepTimerActive() {
        return this.taskManager.isSleepTimerActive();
    }

    public long getSleepTimerTimeLeft() {
        return this.taskManager.getSleepTimerTimeLeft();
    }

    private void bluetoothNotifyChange(PSMPInfo info, String whatChanged) {
        boolean isPlaying = false;
        if (info.playerStatus == PlayerStatus.PLAYING) {
            isPlaying = true;
        }
        if (info.playable != null) {
            Intent i = new Intent(whatChanged);
            i.putExtra("id", 1);
            i.putExtra("artist", "");
            i.putExtra("album", info.playable.getFeedTitle());
            i.putExtra("track", info.playable.getEpisodeTitle());
            i.putExtra(NotificationUtils.CHANNEL_ID_PLAYING, isPlaying);
            List<FeedItem> queue = this.taskManager.getQueueIfLoaded();
            if (queue != null) {
                i.putExtra("ListSize", queue.size());
            }
            i.putExtra("duration", (long) info.playable.getDuration());
            i.putExtra(PodDBAdapter.KEY_POSITION, (long) info.playable.getPosition());
            sendBroadcast(i);
        }
    }

    private void pauseIfPauseOnDisconnect() {
        if (UserPreferences.isPauseOnHeadsetDisconnect() && !isCasting()) {
            if (this.mediaPlayer.getPlayerStatus() == PlayerStatus.PLAYING) {
                transientPause = true;
            }
            this.mediaPlayer.pause(UserPreferences.isPersistNotify() ^ true, true);
        }
    }

    private void unpauseIfPauseOnDisconnect(boolean bluetooth) {
        if (transientPause) {
            transientPause = false;
            if (!bluetooth && UserPreferences.isUnpauseOnHeadsetReconnect()) {
                this.mediaPlayer.resume();
            } else if (bluetooth && UserPreferences.isUnpauseOnBluetoothReconnect()) {
                Vibrator v = (Vibrator) getApplicationContext().getSystemService("vibrator");
                if (v != null) {
                    v.vibrate(500);
                }
                this.mediaPlayer.resume();
            }
        }
    }

    public static MediaType getCurrentMediaType() {
        return currentMediaType;
    }

    public static boolean isCasting() {
        return isCasting;
    }

    public void resume() {
        this.mediaPlayer.resume();
    }

    public void prepare() {
        this.mediaPlayer.prepare();
    }

    public void pause(boolean abandonAudioFocus, boolean reinit) {
        this.mediaPlayer.pause(abandonAudioFocus, reinit);
    }

    public void reinit() {
        this.mediaPlayer.reinit();
    }

    public PSMPInfo getPSMPInfo() {
        return this.mediaPlayer.getPSMPInfo();
    }

    public PlayerStatus getStatus() {
        return this.mediaPlayer.getPlayerStatus();
    }

    public Playable getPlayable() {
        return this.mediaPlayer.getPlayable();
    }

    public boolean canSetSpeed() {
        return this.mediaPlayer.canSetSpeed();
    }

    public void setSpeed(float speed) {
        this.mediaPlayer.setPlaybackParams(speed, UserPreferences.isSkipSilence());
    }

    public void skipSilence(boolean skipSilence) {
        this.mediaPlayer.setPlaybackParams(getCurrentPlaybackSpeed(), skipSilence);
    }

    public void setVolume(float leftVolume, float rightVolume) {
        this.mediaPlayer.setVolume(leftVolume, rightVolume);
    }

    public float getCurrentPlaybackSpeed() {
        PlaybackServiceMediaPlayer playbackServiceMediaPlayer = this.mediaPlayer;
        if (playbackServiceMediaPlayer == null) {
            return 1.0f;
        }
        return playbackServiceMediaPlayer.getPlaybackSpeed();
    }

    public boolean canDownmix() {
        return this.mediaPlayer.canDownmix();
    }

    public void setDownmix(boolean enable) {
        this.mediaPlayer.setDownmix(enable);
    }

    public boolean isStartWhenPrepared() {
        return this.mediaPlayer.isStartWhenPrepared();
    }

    public void setStartWhenPrepared(boolean s) {
        this.mediaPlayer.setStartWhenPrepared(s);
    }

    public void seekTo(int t) {
        this.mediaPlayer.seekTo(t);
    }

    private void seekDelta(int d) {
        this.mediaPlayer.seekDelta(d);
    }

    public void seekToChapter(Chapter c) {
        seekTo((int) c.getStart());
    }

    public int getDuration() {
        PlaybackServiceMediaPlayer playbackServiceMediaPlayer = this.mediaPlayer;
        if (playbackServiceMediaPlayer == null) {
            return -1;
        }
        return playbackServiceMediaPlayer.getDuration();
    }

    public int getCurrentPosition() {
        PlaybackServiceMediaPlayer playbackServiceMediaPlayer = this.mediaPlayer;
        if (playbackServiceMediaPlayer == null) {
            return -1;
        }
        return playbackServiceMediaPlayer.getPosition();
    }

    public boolean isStreaming() {
        return this.mediaPlayer.isStreaming();
    }

    public Pair<Integer, Integer> getVideoSize() {
        return this.mediaPlayer.getVideoSize();
    }

    public static /* synthetic */ void lambda$new$1(PlaybackService playbackService, SharedPreferences sharedPreferences, String key) {
        if (UserPreferences.PREF_LOCKSCREEN_BACKGROUND.equals(key)) {
            playbackService.updateMediaSessionMetadata(playbackService.getPlayable());
        } else {
            playbackService.flavorHelper.onSharedPreference(key);
        }
    }
}
