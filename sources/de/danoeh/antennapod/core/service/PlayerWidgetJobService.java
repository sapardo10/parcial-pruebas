package de.danoeh.antennapod.core.service;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.SafeJobIntentService;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.RemoteViews;
import de.danoeh.antennapod.core.C0734R;
import de.danoeh.antennapod.core.receiver.MediaButtonReceiver;
import de.danoeh.antennapod.core.receiver.PlayerWidget;
import de.danoeh.antennapod.core.service.playback.PlaybackService;
import de.danoeh.antennapod.core.service.playback.PlaybackService.LocalBinder;
import de.danoeh.antennapod.core.service.playback.PlayerStatus;
import de.danoeh.antennapod.core.util.Converter;
import de.danoeh.antennapod.core.util.playback.Playable;
import de.danoeh.antennapod.core.util.playback.Playable.PlayableUtils;

public class PlayerWidgetJobService extends SafeJobIntentService {
    private static final int JOB_ID = -17001;
    private static final String TAG = "PlayerWidgetJobService";
    private final ServiceConnection mConnection = new C07441();
    private PlaybackService playbackService;
    private final Object waitForService = new Object();

    /* renamed from: de.danoeh.antennapod.core.service.PlayerWidgetJobService$1 */
    class C07441 implements ServiceConnection {
        C07441() {
        }

        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(PlayerWidgetJobService.TAG, "Connection to service established");
            if (service instanceof LocalBinder) {
                synchronized (PlayerWidgetJobService.this.waitForService) {
                    PlayerWidgetJobService.this.playbackService = ((LocalBinder) service).getService();
                    PlayerWidgetJobService.this.waitForService.notifyAll();
                }
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            PlayerWidgetJobService.this.playbackService = null;
            Log.d(PlayerWidgetJobService.TAG, "Disconnected from service");
        }
    }

    protected void onHandleWork(@android.support.annotation.NonNull android.content.Intent r5) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:36:0x004e in {2, 15, 19, 20, 21, 27, 29, 30, 31, 35} preds:[]
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
        r4 = this;
        r0 = r4.getApplicationContext();
        r0 = de.danoeh.antennapod.core.receiver.PlayerWidget.isEnabled(r0);
        if (r0 != 0) goto L_0x000b;
    L_0x000a:
        return;
    L_0x000b:
        r0 = r4.waitForService;
        monitor-enter(r0);
        r1 = de.danoeh.antennapod.core.service.playback.PlaybackService.isRunning;	 Catch:{ all -> 0x004b }
        if (r1 == 0) goto L_0x0031;	 Catch:{ all -> 0x004b }
    L_0x0012:
        r1 = r4.playbackService;	 Catch:{ all -> 0x004b }
        if (r1 != 0) goto L_0x0031;	 Catch:{ all -> 0x004b }
    L_0x0016:
        r1 = new android.content.Intent;	 Catch:{ all -> 0x004b }
        r2 = de.danoeh.antennapod.core.service.playback.PlaybackService.class;	 Catch:{ all -> 0x004b }
        r1.<init>(r4, r2);	 Catch:{ all -> 0x004b }
        r2 = r4.mConnection;	 Catch:{ all -> 0x004b }
        r3 = 0;	 Catch:{ all -> 0x004b }
        r4.bindService(r1, r2, r3);	 Catch:{ all -> 0x004b }
    L_0x0023:
        r1 = r4.playbackService;	 Catch:{ all -> 0x004b }
        if (r1 != 0) goto L_0x0030;
    L_0x0027:
        r1 = r4.waitForService;	 Catch:{ InterruptedException -> 0x002d }
        r1.wait();	 Catch:{ InterruptedException -> 0x002d }
        goto L_0x0023;
    L_0x002d:
        r1 = move-exception;
        monitor-exit(r0);	 Catch:{ all -> 0x004b }
        return;	 Catch:{ all -> 0x004b }
    L_0x0030:
        goto L_0x0032;	 Catch:{ all -> 0x004b }
    L_0x0032:
        monitor-exit(r0);	 Catch:{ all -> 0x004b }
        r4.updateViews();
        r0 = r4.playbackService;
        if (r0 == 0) goto L_0x0049;
    L_0x003a:
        r0 = r4.mConnection;	 Catch:{ IllegalArgumentException -> 0x0040 }
        r4.unbindService(r0);	 Catch:{ IllegalArgumentException -> 0x0040 }
        goto L_0x004a;
    L_0x0040:
        r0 = move-exception;
        r1 = "PlayerWidgetJobService";
        r2 = "IllegalArgumentException when trying to unbind service";
        android.util.Log.w(r1, r2);
        goto L_0x004a;
    L_0x004a:
        return;
    L_0x004b:
        r1 = move-exception;
        monitor-exit(r0);	 Catch:{ all -> 0x004b }
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.core.service.PlayerWidgetJobService.onHandleWork(android.content.Intent):void");
    }

    public static void updateWidget(Context context) {
        JobIntentService.enqueueWork(context, PlayerWidgetJobService.class, (int) JOB_ID, new Intent(context, PlayerWidgetJobService.class));
    }

    private void updateViews() {
        PlayerStatus status;
        ComponentName playerWidget = new ComponentName(this, PlayerWidget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        RemoteViews views = new RemoteViews(getPackageName(), C0734R.layout.player_widget);
        PendingIntent startMediaplayer = PendingIntent.getActivity(this, 0, PlaybackService.getPlayerActivityIntent(this), 0);
        PendingIntent startAppPending = PendingIntent.getActivity(this, 0, PlaybackService.getPlayerActivityIntent(this), 134217728);
        boolean nothingPlaying = false;
        Playable media = this.playbackService;
        if (media != null) {
            media = media.getPlayable();
            status = this.playbackService.getStatus();
        } else {
            media = PlayableUtils.createInstanceFromPreferences(getApplicationContext());
            status = PlayerStatus.STOPPED;
        }
        if (media != null) {
            String progressString;
            views.setOnClickPendingIntent(C0734R.id.layout_left, startMediaplayer);
            views.setTextViewText(C0734R.id.txtvTitle, media.getEpisodeTitle());
            PlaybackService playbackService = this.playbackService;
            if (playbackService != null) {
                progressString = getProgressString(playbackService.getCurrentPosition(), this.playbackService.getDuration());
            } else {
                progressString = getProgressString(media.getPosition(), media.getDuration());
            }
            if (progressString != null) {
                views.setViewVisibility(C0734R.id.txtvProgress, 0);
                views.setTextViewText(C0734R.id.txtvProgress, progressString);
            }
            if (status == PlayerStatus.PLAYING) {
                views.setImageViewResource(C0734R.id.butPlay, C0734R.drawable.ic_pause_white_24dp);
                if (VERSION.SDK_INT >= 15) {
                    views.setContentDescription(C0734R.id.butPlay, getString(C0734R.string.pause_label));
                }
            } else {
                views.setImageViewResource(C0734R.id.butPlay, C0734R.drawable.ic_play_arrow_white_24dp);
                if (VERSION.SDK_INT >= 15) {
                    views.setContentDescription(C0734R.id.butPlay, getString(C0734R.string.play_label));
                }
            }
            views.setOnClickPendingIntent(C0734R.id.butPlay, createMediaButtonIntent());
        } else {
            nothingPlaying = true;
        }
        if (nothingPlaying) {
            views.setOnClickPendingIntent(C0734R.id.layout_left, startAppPending);
            views.setOnClickPendingIntent(C0734R.id.butPlay, startAppPending);
            views.setViewVisibility(C0734R.id.txtvProgress, 4);
            views.setTextViewText(C0734R.id.txtvTitle, getString(C0734R.string.no_media_playing_label));
            views.setImageViewResource(C0734R.id.butPlay, C0734R.drawable.ic_play_arrow_white_24dp);
        }
        manager.updateAppWidget(playerWidget, views);
    }

    private PendingIntent createMediaButtonIntent() {
        KeyEvent event = new KeyEvent(0, 85);
        Intent startingIntent = new Intent(getBaseContext(), MediaButtonReceiver.class);
        startingIntent.setAction(MediaButtonReceiver.NOTIFY_BUTTON_RECEIVER);
        startingIntent.putExtra("android.intent.extra.KEY_EVENT", event);
        return PendingIntent.getBroadcast(this, 0, startingIntent, 0);
    }

    private String getProgressString(int position, int duration) {
        if (position <= 0 || duration <= 0) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Converter.getDurationStringLong(position));
        stringBuilder.append(" / ");
        stringBuilder.append(Converter.getDurationStringLong(duration));
        return stringBuilder.toString();
    }
}
