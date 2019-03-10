package com.google.android.exoplayer2.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat.Action;
import com.google.android.exoplayer2.Player;
import java.util.List;
import java.util.Map;

public interface PlayerNotificationManager$CustomActionReceiver {
    Map<String, Action> createCustomActions(Context context, int i);

    List<String> getCustomActions(Player player);

    void onCustomAction(Player player, String str, Intent intent);
}
