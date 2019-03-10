package com.google.android.exoplayer2.ui;

import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.Player;

public interface PlayerNotificationManager$MediaDescriptionAdapter {
    @Nullable
    PendingIntent createCurrentContentIntent(Player player);

    @Nullable
    String getCurrentContentText(Player player);

    String getCurrentContentTitle(Player player);

    @Nullable
    Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager$BitmapCallback playerNotificationManager$BitmapCallback);
}
