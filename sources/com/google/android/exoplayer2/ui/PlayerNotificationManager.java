package com.google.android.exoplayer2.ui;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationCompat.Action;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.media.app.NotificationCompat.MediaStyle;
import android.support.v4.media.session.MediaSessionCompat.Token;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.ControlDispatcher;
import com.google.android.exoplayer2.DefaultControlDispatcher;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Player$EventListener;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.NotificationUtil;
import com.google.android.exoplayer2.util.Util;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;

public class PlayerNotificationManager {
    public static final String ACTION_FAST_FORWARD = "com.google.android.exoplayer.ffwd";
    public static final String ACTION_NEXT = "com.google.android.exoplayer.next";
    public static final String ACTION_PAUSE = "com.google.android.exoplayer.pause";
    public static final String ACTION_PLAY = "com.google.android.exoplayer.play";
    public static final String ACTION_PREVIOUS = "com.google.android.exoplayer.prev";
    public static final String ACTION_REWIND = "com.google.android.exoplayer.rewind";
    public static final String ACTION_STOP = "com.google.android.exoplayer.stop";
    public static final int DEFAULT_FAST_FORWARD_MS = 15000;
    public static final int DEFAULT_REWIND_MS = 5000;
    public static final String EXTRA_INSTANCE_ID = "INSTANCE_ID";
    private static final long MAX_POSITION_FOR_SEEK_TO_PREVIOUS = 3000;
    private static int instanceIdCounter;
    private int badgeIconType;
    private final String channelId;
    private int color;
    private boolean colorized;
    private final Context context;
    private ControlDispatcher controlDispatcher;
    private int currentNotificationTag;
    @Nullable
    private final PlayerNotificationManager$CustomActionReceiver customActionReceiver;
    private final Map<String, Action> customActions;
    private int defaults;
    private long fastForwardMs;
    private final int instanceId;
    private final IntentFilter intentFilter;
    private boolean isNotificationStarted;
    private int lastPlaybackState;
    private final Handler mainHandler;
    private final PlayerNotificationManager$MediaDescriptionAdapter mediaDescriptionAdapter;
    @Nullable
    private Token mediaSessionToken;
    private final PlayerNotificationManager$NotificationBroadcastReceiver notificationBroadcastReceiver;
    private final int notificationId;
    @Nullable
    private PlayerNotificationManager$NotificationListener notificationListener;
    private final NotificationManagerCompat notificationManager;
    private boolean ongoing;
    private final Map<String, Action> playbackActions;
    @Nullable
    private Player player;
    private final Player$EventListener playerListener;
    private int priority;
    private long rewindMs;
    @DrawableRes
    private int smallIconResourceId;
    @Nullable
    private String stopAction;
    @Nullable
    private PendingIntent stopPendingIntent;
    private boolean useChronometer;
    private boolean useNavigationActions;
    private boolean usePlayPauseActions;
    private int visibility;
    private boolean wasPlayWhenReady;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public @interface Priority {
    }

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public @interface Visibility {
    }

    public static PlayerNotificationManager createWithNotificationChannel(Context context, String channelId, @StringRes int channelName, int notificationId, PlayerNotificationManager$MediaDescriptionAdapter mediaDescriptionAdapter) {
        NotificationUtil.createNotificationChannel(context, channelId, channelName, 2);
        return new PlayerNotificationManager(context, channelId, notificationId, mediaDescriptionAdapter);
    }

    public PlayerNotificationManager(Context context, String channelId, int notificationId, PlayerNotificationManager$MediaDescriptionAdapter mediaDescriptionAdapter) {
        this(context, channelId, notificationId, mediaDescriptionAdapter, null);
    }

    public PlayerNotificationManager(Context context, String channelId, int notificationId, PlayerNotificationManager$MediaDescriptionAdapter mediaDescriptionAdapter, @Nullable PlayerNotificationManager$CustomActionReceiver customActionReceiver) {
        Map createCustomActions;
        this.context = context.getApplicationContext();
        this.channelId = channelId;
        this.notificationId = notificationId;
        this.mediaDescriptionAdapter = mediaDescriptionAdapter;
        this.customActionReceiver = customActionReceiver;
        this.controlDispatcher = new DefaultControlDispatcher();
        int i = instanceIdCounter;
        instanceIdCounter = i + 1;
        this.instanceId = i;
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.notificationManager = NotificationManagerCompat.from(context);
        this.playerListener = new PlayerNotificationManager$PlayerListener(this, null);
        this.notificationBroadcastReceiver = new PlayerNotificationManager$NotificationBroadcastReceiver(this);
        this.intentFilter = new IntentFilter();
        this.useNavigationActions = true;
        this.usePlayPauseActions = true;
        this.ongoing = true;
        this.colorized = true;
        this.useChronometer = true;
        this.color = 0;
        this.smallIconResourceId = C0649R.drawable.exo_notification_small_icon;
        this.defaults = 0;
        this.priority = -1;
        this.fastForwardMs = 15000;
        this.rewindMs = DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
        this.stopAction = ACTION_STOP;
        this.badgeIconType = 1;
        this.visibility = 1;
        this.playbackActions = createPlaybackActions(context, this.instanceId);
        for (String action : this.playbackActions.keySet()) {
            this.intentFilter.addAction(action);
        }
        if (customActionReceiver != null) {
            createCustomActions = customActionReceiver.createCustomActions(context, this.instanceId);
        } else {
            createCustomActions = Collections.emptyMap();
        }
        this.customActions = createCustomActions;
        for (String action2 : this.customActions.keySet()) {
            this.intentFilter.addAction(action2);
        }
        this.stopPendingIntent = ((Action) Assertions.checkNotNull(this.playbackActions.get(ACTION_STOP))).actionIntent;
    }

    public final void setPlayer(@Nullable Player player) {
        Player player2;
        boolean z = false;
        Assertions.checkState(Looper.myLooper() == Looper.getMainLooper());
        if (player != null) {
            if (player.getApplicationLooper() != Looper.getMainLooper()) {
                Assertions.checkArgument(z);
                player2 = this.player;
                if (player2 == player) {
                    if (player2 != null) {
                        player2.removeListener(this.playerListener);
                        if (player == null) {
                            stopNotification();
                        }
                    }
                    this.player = player;
                    if (player != null) {
                        this.wasPlayWhenReady = player.getPlayWhenReady();
                        this.lastPlaybackState = player.getPlaybackState();
                        player.addListener(this.playerListener);
                        if (this.lastPlaybackState != 1) {
                            startOrUpdateNotification();
                        }
                    }
                }
            }
        }
        z = true;
        Assertions.checkArgument(z);
        player2 = this.player;
        if (player2 == player) {
            if (player2 != null) {
                player2.removeListener(this.playerListener);
                if (player == null) {
                    stopNotification();
                }
            }
            this.player = player;
            if (player != null) {
                this.wasPlayWhenReady = player.getPlayWhenReady();
                this.lastPlaybackState = player.getPlaybackState();
                player.addListener(this.playerListener);
                if (this.lastPlaybackState != 1) {
                    startOrUpdateNotification();
                }
            }
        }
    }

    public final void setControlDispatcher(ControlDispatcher controlDispatcher) {
        this.controlDispatcher = controlDispatcher != null ? controlDispatcher : new DefaultControlDispatcher();
    }

    public final void setNotificationListener(PlayerNotificationManager$NotificationListener notificationListener) {
        this.notificationListener = notificationListener;
    }

    public final void setFastForwardIncrementMs(long fastForwardMs) {
        if (this.fastForwardMs != fastForwardMs) {
            this.fastForwardMs = fastForwardMs;
            invalidate();
        }
    }

    public final void setRewindIncrementMs(long rewindMs) {
        if (this.rewindMs != rewindMs) {
            this.rewindMs = rewindMs;
            invalidate();
        }
    }

    public final void setUseNavigationActions(boolean useNavigationActions) {
        if (this.useNavigationActions != useNavigationActions) {
            this.useNavigationActions = useNavigationActions;
            invalidate();
        }
    }

    public final void setUsePlayPauseActions(boolean usePlayPauseActions) {
        if (this.usePlayPauseActions != usePlayPauseActions) {
            this.usePlayPauseActions = usePlayPauseActions;
            invalidate();
        }
    }

    public final void setStopAction(@Nullable String stopAction) {
        if (!Util.areEqual(stopAction, this.stopAction)) {
            this.stopAction = stopAction;
            if (ACTION_STOP.equals(stopAction)) {
                this.stopPendingIntent = ((Action) Assertions.checkNotNull(this.playbackActions.get(ACTION_STOP))).actionIntent;
            } else if (stopAction != null) {
                this.stopPendingIntent = ((Action) Assertions.checkNotNull(this.customActions.get(stopAction))).actionIntent;
            } else {
                this.stopPendingIntent = null;
            }
            invalidate();
        }
    }

    public final void setMediaSessionToken(Token token) {
        if (!Util.areEqual(this.mediaSessionToken, token)) {
            this.mediaSessionToken = token;
            invalidate();
        }
    }

    public final void setBadgeIconType(int badgeIconType) {
        if (this.badgeIconType != badgeIconType) {
            switch (badgeIconType) {
                case 0:
                case 1:
                case 2:
                    this.badgeIconType = badgeIconType;
                    invalidate();
                    return;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    public final void setColorized(boolean colorized) {
        if (this.colorized != colorized) {
            this.colorized = colorized;
            invalidate();
        }
    }

    public final void setDefaults(int defaults) {
        if (this.defaults != defaults) {
            this.defaults = defaults;
            invalidate();
        }
    }

    public final void setColor(int color) {
        if (this.color != color) {
            this.color = color;
            invalidate();
        }
    }

    public final void setOngoing(boolean ongoing) {
        if (this.ongoing != ongoing) {
            this.ongoing = ongoing;
            invalidate();
        }
    }

    public final void setPriority(int priority) {
        if (this.priority != priority) {
            switch (priority) {
                case -2:
                case -1:
                case 0:
                case 1:
                case 2:
                    this.priority = priority;
                    invalidate();
                    return;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    public final void setSmallIcon(@DrawableRes int smallIconResourceId) {
        if (this.smallIconResourceId != smallIconResourceId) {
            this.smallIconResourceId = smallIconResourceId;
            invalidate();
        }
    }

    public final void setUseChronometer(boolean useChronometer) {
        if (this.useChronometer != useChronometer) {
            this.useChronometer = useChronometer;
            invalidate();
        }
    }

    public final void setVisibility(int visibility) {
        if (this.visibility != visibility) {
            switch (visibility) {
                case -1:
                case 0:
                case 1:
                    this.visibility = visibility;
                    invalidate();
                    return;
                default:
                    throw new IllegalStateException();
            }
        }
    }

    public void invalidate() {
        if (this.isNotificationStarted && this.player != null) {
            updateNotification(null);
        }
    }

    @RequiresNonNull({"player"})
    private Notification updateNotification(@Nullable Bitmap bitmap) {
        Notification notification = createNotification(this.player, bitmap);
        this.notificationManager.notify(this.notificationId, notification);
        return notification;
    }

    private void startOrUpdateNotification() {
        if (this.player != null) {
            Notification notification = updateNotification(null);
            if (!this.isNotificationStarted) {
                this.isNotificationStarted = true;
                this.context.registerReceiver(this.notificationBroadcastReceiver, this.intentFilter);
                PlayerNotificationManager$NotificationListener playerNotificationManager$NotificationListener = this.notificationListener;
                if (playerNotificationManager$NotificationListener != null) {
                    playerNotificationManager$NotificationListener.onNotificationStarted(this.notificationId, notification);
                }
            }
        }
    }

    private void stopNotification() {
        if (this.isNotificationStarted) {
            this.notificationManager.cancel(this.notificationId);
            this.isNotificationStarted = false;
            this.context.unregisterReceiver(this.notificationBroadcastReceiver);
            PlayerNotificationManager$NotificationListener playerNotificationManager$NotificationListener = this.notificationListener;
            if (playerNotificationManager$NotificationListener != null) {
                playerNotificationManager$NotificationListener.onNotificationCancelled(this.notificationId);
            }
        }
    }

    protected Notification createNotification(Player player, @Nullable Bitmap largeIcon) {
        PlayerNotificationManager$MediaDescriptionAdapter playerNotificationManager$MediaDescriptionAdapter;
        int i;
        PendingIntent contentIntent;
        Builder builder = new Builder(this.context, this.channelId);
        List<String> actionNames = getActions(player);
        for (int i2 = 0; i2 < actionNames.size(); i2++) {
            Action action;
            String actionName = (String) actionNames.get(i2);
            if (this.playbackActions.containsKey(actionName)) {
                action = (Action) this.playbackActions.get(actionName);
            } else {
                action = (Action) this.customActions.get(actionName);
            }
            if (action != null) {
                builder.addAction(action);
            }
        }
        MediaStyle mediaStyle = new MediaStyle();
        Token token = this.mediaSessionToken;
        if (token != null) {
            mediaStyle.setMediaSession(token);
        }
        mediaStyle.setShowActionsInCompactView(getActionIndicesForCompactView(actionNames, player));
        boolean useStopAction = this.stopAction != null;
        mediaStyle.setShowCancelButton(useStopAction);
        if (useStopAction) {
            PendingIntent pendingIntent = this.stopPendingIntent;
            if (pendingIntent != null) {
                builder.setDeleteIntent(pendingIntent);
                mediaStyle.setCancelButtonIntent(this.stopPendingIntent);
                builder.setStyle(mediaStyle);
                builder.setBadgeIconType(this.badgeIconType).setOngoing(this.ongoing).setColor(this.color).setColorized(this.colorized).setSmallIcon(this.smallIconResourceId).setVisibility(this.visibility).setPriority(this.priority).setDefaults(this.defaults);
                if (this.useChronometer) {
                    if (!player.isPlayingAd()) {
                        if (!player.isCurrentWindowDynamic()) {
                            if (player.getPlayWhenReady()) {
                                if (player.getPlaybackState() == 3) {
                                    builder.setWhen(System.currentTimeMillis() - player.getContentPosition()).setShowWhen(true).setUsesChronometer(true);
                                    builder.setContentTitle(this.mediaDescriptionAdapter.getCurrentContentTitle(player));
                                    builder.setContentText(this.mediaDescriptionAdapter.getCurrentContentText(player));
                                    if (largeIcon != null) {
                                        playerNotificationManager$MediaDescriptionAdapter = this.mediaDescriptionAdapter;
                                        i = this.currentNotificationTag + 1;
                                        this.currentNotificationTag = i;
                                        largeIcon = playerNotificationManager$MediaDescriptionAdapter.getCurrentLargeIcon(player, new PlayerNotificationManager$BitmapCallback(this, i, null));
                                    }
                                    if (largeIcon == null) {
                                        builder.setLargeIcon(largeIcon);
                                    }
                                    contentIntent = this.mediaDescriptionAdapter.createCurrentContentIntent(player);
                                    if (contentIntent == null) {
                                        builder.setContentIntent(contentIntent);
                                    }
                                    return builder.build();
                                }
                            }
                        }
                    }
                }
                builder.setShowWhen(false).setUsesChronometer(false);
                builder.setContentTitle(this.mediaDescriptionAdapter.getCurrentContentTitle(player));
                builder.setContentText(this.mediaDescriptionAdapter.getCurrentContentText(player));
                if (largeIcon != null) {
                    playerNotificationManager$MediaDescriptionAdapter = this.mediaDescriptionAdapter;
                    i = this.currentNotificationTag + 1;
                    this.currentNotificationTag = i;
                    largeIcon = playerNotificationManager$MediaDescriptionAdapter.getCurrentLargeIcon(player, new PlayerNotificationManager$BitmapCallback(this, i, null));
                }
                if (largeIcon == null) {
                    builder.setLargeIcon(largeIcon);
                }
                contentIntent = this.mediaDescriptionAdapter.createCurrentContentIntent(player);
                if (contentIntent == null) {
                    builder.setContentIntent(contentIntent);
                }
                return builder.build();
            }
        }
        builder.setStyle(mediaStyle);
        builder.setBadgeIconType(this.badgeIconType).setOngoing(this.ongoing).setColor(this.color).setColorized(this.colorized).setSmallIcon(this.smallIconResourceId).setVisibility(this.visibility).setPriority(this.priority).setDefaults(this.defaults);
        if (this.useChronometer) {
            if (!player.isPlayingAd()) {
                if (!player.isCurrentWindowDynamic()) {
                    if (player.getPlayWhenReady()) {
                        if (player.getPlaybackState() == 3) {
                            builder.setWhen(System.currentTimeMillis() - player.getContentPosition()).setShowWhen(true).setUsesChronometer(true);
                            builder.setContentTitle(this.mediaDescriptionAdapter.getCurrentContentTitle(player));
                            builder.setContentText(this.mediaDescriptionAdapter.getCurrentContentText(player));
                            if (largeIcon != null) {
                                playerNotificationManager$MediaDescriptionAdapter = this.mediaDescriptionAdapter;
                                i = this.currentNotificationTag + 1;
                                this.currentNotificationTag = i;
                                largeIcon = playerNotificationManager$MediaDescriptionAdapter.getCurrentLargeIcon(player, new PlayerNotificationManager$BitmapCallback(this, i, null));
                            }
                            if (largeIcon == null) {
                                builder.setLargeIcon(largeIcon);
                            }
                            contentIntent = this.mediaDescriptionAdapter.createCurrentContentIntent(player);
                            if (contentIntent == null) {
                                builder.setContentIntent(contentIntent);
                            }
                            return builder.build();
                        }
                    }
                }
            }
        }
        builder.setShowWhen(false).setUsesChronometer(false);
        builder.setContentTitle(this.mediaDescriptionAdapter.getCurrentContentTitle(player));
        builder.setContentText(this.mediaDescriptionAdapter.getCurrentContentText(player));
        if (largeIcon != null) {
            playerNotificationManager$MediaDescriptionAdapter = this.mediaDescriptionAdapter;
            i = this.currentNotificationTag + 1;
            this.currentNotificationTag = i;
            largeIcon = playerNotificationManager$MediaDescriptionAdapter.getCurrentLargeIcon(player, new PlayerNotificationManager$BitmapCallback(this, i, null));
        }
        if (largeIcon == null) {
            builder.setLargeIcon(largeIcon);
        }
        contentIntent = this.mediaDescriptionAdapter.createCurrentContentIntent(player);
        if (contentIntent == null) {
            builder.setContentIntent(contentIntent);
        }
        return builder.build();
    }

    protected List<String> getActions(Player player) {
        boolean isPlayingAd = player.isPlayingAd();
        List<String> stringActions = new ArrayList();
        if (!isPlayingAd) {
            if (this.useNavigationActions) {
                stringActions.add(ACTION_PREVIOUS);
            }
            if (this.rewindMs > 0) {
                stringActions.add(ACTION_REWIND);
            }
        }
        if (this.usePlayPauseActions) {
            if (player.getPlayWhenReady()) {
                stringActions.add(ACTION_PAUSE);
            } else {
                stringActions.add(ACTION_PLAY);
            }
        }
        if (!isPlayingAd) {
            if (this.fastForwardMs > 0) {
                stringActions.add(ACTION_FAST_FORWARD);
            }
            if (this.useNavigationActions && player.getNextWindowIndex() != -1) {
                stringActions.add(ACTION_NEXT);
            }
        }
        PlayerNotificationManager$CustomActionReceiver playerNotificationManager$CustomActionReceiver = this.customActionReceiver;
        if (playerNotificationManager$CustomActionReceiver != null) {
            stringActions.addAll(playerNotificationManager$CustomActionReceiver.getCustomActions(player));
        }
        if (ACTION_STOP.equals(this.stopAction)) {
            stringActions.add(this.stopAction);
        }
        return stringActions;
    }

    protected int[] getActionIndicesForCompactView(List<String> actionNames, Player player) {
        int pauseActionIndex = actionNames.indexOf(ACTION_PAUSE);
        int playActionIndex = actionNames.indexOf(ACTION_PLAY);
        if (pauseActionIndex != -1) {
            return new int[]{pauseActionIndex};
        } else if (playActionIndex == -1) {
            return new int[0];
        } else {
            return new int[]{playActionIndex};
        }
    }

    private static Map<String, Action> createPlaybackActions(Context context, int instanceId) {
        Map<String, Action> actions = new HashMap();
        actions.put(ACTION_PLAY, new Action(C0649R.drawable.exo_notification_play, context.getString(C0649R.string.exo_controls_play_description), createBroadcastIntent(ACTION_PLAY, context, instanceId)));
        actions.put(ACTION_PAUSE, new Action(C0649R.drawable.exo_notification_pause, context.getString(C0649R.string.exo_controls_pause_description), createBroadcastIntent(ACTION_PAUSE, context, instanceId)));
        actions.put(ACTION_STOP, new Action(C0649R.drawable.exo_notification_stop, context.getString(C0649R.string.exo_controls_stop_description), createBroadcastIntent(ACTION_STOP, context, instanceId)));
        actions.put(ACTION_REWIND, new Action(C0649R.drawable.exo_notification_rewind, context.getString(C0649R.string.exo_controls_rewind_description), createBroadcastIntent(ACTION_REWIND, context, instanceId)));
        actions.put(ACTION_FAST_FORWARD, new Action(C0649R.drawable.exo_notification_fastforward, context.getString(C0649R.string.exo_controls_fastforward_description), createBroadcastIntent(ACTION_FAST_FORWARD, context, instanceId)));
        actions.put(ACTION_PREVIOUS, new Action(C0649R.drawable.exo_notification_previous, context.getString(C0649R.string.exo_controls_previous_description), createBroadcastIntent(ACTION_PREVIOUS, context, instanceId)));
        actions.put(ACTION_NEXT, new Action(C0649R.drawable.exo_notification_next, context.getString(C0649R.string.exo_controls_next_description), createBroadcastIntent(ACTION_NEXT, context, instanceId)));
        return actions;
    }

    private static PendingIntent createBroadcastIntent(String action, Context context, int instanceId) {
        Intent intent = new Intent(action).setPackage(context.getPackageName());
        intent.putExtra(EXTRA_INSTANCE_ID, instanceId);
        return PendingIntent.getBroadcast(context, instanceId, intent, C0555C.ENCODING_PCM_MU_LAW);
    }
}
