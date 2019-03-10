package com.google.android.exoplayer2.ui;

import android.graphics.Bitmap;

public final class PlayerNotificationManager$BitmapCallback {
    private final int notificationTag;
    final /* synthetic */ PlayerNotificationManager this$0;

    private PlayerNotificationManager$BitmapCallback(PlayerNotificationManager this$0, int notificationTag) {
        this.this$0 = this$0;
        this.notificationTag = notificationTag;
    }

    public void onBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            PlayerNotificationManager.access$000(this.this$0).post(new C0644x1be680ef(this, bitmap));
        }
    }

    public static /* synthetic */ void lambda$onBitmap$0(PlayerNotificationManager$BitmapCallback playerNotificationManager$BitmapCallback, Bitmap bitmap) {
        if (PlayerNotificationManager.access$100(playerNotificationManager$BitmapCallback.this$0) == null) {
            return;
        }
        if (playerNotificationManager$BitmapCallback.notificationTag != PlayerNotificationManager.access$200(playerNotificationManager$BitmapCallback.this$0)) {
            return;
        }
        if (PlayerNotificationManager.access$300(playerNotificationManager$BitmapCallback.this$0)) {
            PlayerNotificationManager.access$400(playerNotificationManager$BitmapCallback.this$0, bitmap);
        }
    }
}
