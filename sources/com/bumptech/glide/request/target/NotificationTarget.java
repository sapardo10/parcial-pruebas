package com.bumptech.glide.request.target;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.util.Preconditions;

public class NotificationTarget extends SimpleTarget<Bitmap> {
    private final Context context;
    private final Notification notification;
    private final int notificationId;
    private final String notificationTag;
    private final RemoteViews remoteViews;
    private final int viewId;

    public NotificationTarget(Context context, int viewId, RemoteViews remoteViews, Notification notification, int notificationId) {
        this(context, viewId, remoteViews, notification, notificationId, null);
    }

    public NotificationTarget(Context context, int viewId, RemoteViews remoteViews, Notification notification, int notificationId, String notificationTag) {
        this(context, Integer.MIN_VALUE, Integer.MIN_VALUE, viewId, remoteViews, notification, notificationId, notificationTag);
    }

    public NotificationTarget(Context context, int width, int height, int viewId, RemoteViews remoteViews, Notification notification, int notificationId, String notificationTag) {
        super(width, height);
        this.context = (Context) Preconditions.checkNotNull(context, "Context must not be null!");
        this.notification = (Notification) Preconditions.checkNotNull(notification, "Notification object can not be null!");
        this.remoteViews = (RemoteViews) Preconditions.checkNotNull(remoteViews, "RemoteViews object can not be null!");
        this.viewId = viewId;
        this.notificationId = notificationId;
        this.notificationTag = notificationTag;
    }

    private void update() {
        ((NotificationManager) Preconditions.checkNotNull((NotificationManager) this.context.getSystemService("notification"))).notify(this.notificationTag, this.notificationId, this.notification);
    }

    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
        this.remoteViews.setImageViewBitmap(this.viewId, resource);
        update();
    }
}
