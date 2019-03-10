package com.google.android.exoplayer2.ui;

import android.graphics.Bitmap;

/* compiled from: lambda */
/* renamed from: com.google.android.exoplayer2.ui.-$$Lambda$PlayerNotificationManager$BitmapCallback$ai-lvTgLEQ8d7uyKftaUKVPjkgA */
public final /* synthetic */ class C0644x1be680ef implements Runnable {
    private final /* synthetic */ PlayerNotificationManager$BitmapCallback f$0;
    private final /* synthetic */ Bitmap f$1;

    public /* synthetic */ C0644x1be680ef(PlayerNotificationManager$BitmapCallback playerNotificationManager$BitmapCallback, Bitmap bitmap) {
        this.f$0 = playerNotificationManager$BitmapCallback;
        this.f$1 = bitmap;
    }

    public final void run() {
        PlayerNotificationManager$BitmapCallback.lambda$onBitmap$0(this.f$0, this.f$1);
    }
}
