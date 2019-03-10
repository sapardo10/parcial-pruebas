package com.google.android.exoplayer2.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline.Window;

class PlayerNotificationManager$NotificationBroadcastReceiver extends BroadcastReceiver {
    final /* synthetic */ PlayerNotificationManager this$0;
    private final Window window = new Window();

    public PlayerNotificationManager$NotificationBroadcastReceiver(PlayerNotificationManager playerNotificationManager) {
        this.this$0 = playerNotificationManager;
    }

    public void onReceive(Context context, Intent intent) {
        Player player = PlayerNotificationManager.access$100(this.this$0);
        if (player != null) {
            if (PlayerNotificationManager.access$300(this.this$0)) {
                if (intent.getIntExtra(PlayerNotificationManager.EXTRA_INSTANCE_ID, PlayerNotificationManager.access$1000(this.this$0)) == PlayerNotificationManager.access$1000(this.this$0)) {
                    String action = intent.getAction();
                    if (!PlayerNotificationManager.ACTION_PLAY.equals(action)) {
                        if (!PlayerNotificationManager.ACTION_PAUSE.equals(action)) {
                            if (!PlayerNotificationManager.ACTION_FAST_FORWARD.equals(action)) {
                                if (!PlayerNotificationManager.ACTION_REWIND.equals(action)) {
                                    int nextWindowIndex;
                                    if (PlayerNotificationManager.ACTION_NEXT.equals(action)) {
                                        nextWindowIndex = player.getNextWindowIndex();
                                        if (nextWindowIndex != -1) {
                                            PlayerNotificationManager.access$1100(this.this$0).dispatchSeekTo(player, nextWindowIndex, C0555C.TIME_UNSET);
                                        }
                                    } else if (PlayerNotificationManager.ACTION_PREVIOUS.equals(action)) {
                                        player.getCurrentTimeline().getWindow(player.getCurrentWindowIndex(), this.window);
                                        nextWindowIndex = player.getPreviousWindowIndex();
                                        if (nextWindowIndex != -1) {
                                            if (player.getCurrentPosition() > 3000) {
                                                if (!this.window.isDynamic || this.window.isSeekable) {
                                                }
                                            }
                                            PlayerNotificationManager.access$1100(this.this$0).dispatchSeekTo(player, nextWindowIndex, C0555C.TIME_UNSET);
                                        }
                                        PlayerNotificationManager.access$1100(this.this$0).dispatchSeekTo(player, player.getCurrentWindowIndex(), C0555C.TIME_UNSET);
                                    } else if (PlayerNotificationManager.ACTION_STOP.equals(action)) {
                                        PlayerNotificationManager.access$1100(this.this$0).dispatchStop(player, true);
                                        PlayerNotificationManager.access$1400(this.this$0);
                                    } else if (PlayerNotificationManager.access$1500(this.this$0) != null && PlayerNotificationManager.access$1600(this.this$0).containsKey(action)) {
                                        PlayerNotificationManager.access$1500(this.this$0).onCustomAction(player, action, intent);
                                    }
                                }
                            }
                            PlayerNotificationManager.access$1100(this.this$0).dispatchSeekTo(player, player.getCurrentWindowIndex(), player.getCurrentPosition() + (PlayerNotificationManager.ACTION_FAST_FORWARD.equals(action) ? PlayerNotificationManager.access$1200(this.this$0) : -PlayerNotificationManager.access$1300(this.this$0)));
                        }
                    }
                    PlayerNotificationManager.access$1100(this.this$0).dispatchSetPlayWhenReady(player, PlayerNotificationManager.ACTION_PLAY.equals(action));
                }
            }
        }
    }
}
