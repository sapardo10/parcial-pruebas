package com.google.android.exoplayer2.ui;

import android.app.Notification;

public interface PlayerNotificationManager$NotificationListener {
    void onNotificationCancelled(int i);

    void onNotificationStarted(int i, Notification notification);
}
