package com.google.android.exoplayer2.ui;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.app.NotificationCompat.Builder;
import com.google.android.exoplayer2.offline.DownloadManager.TaskState;

public final class DownloadNotificationUtil {
    @StringRes
    private static final int NULL_STRING_ID = 0;

    private DownloadNotificationUtil() {
    }

    public static Notification buildProgressNotification(Context context, @DrawableRes int smallIcon, String channelId, @Nullable PendingIntent contentIntent, @Nullable String message, TaskState[] taskStates) {
        int titleStringId;
        Builder notificationBuilder;
        boolean indeterminate;
        TaskState[] taskStateArr = taskStates;
        boolean haveDownloadTasks = false;
        boolean haveRemoveTasks = false;
        int length = taskStateArr.length;
        boolean haveDownloadedBytes = false;
        boolean allDownloadPercentagesUnknown = true;
        int downloadTaskCount = 0;
        float totalPercentage = 0.0f;
        int totalPercentage2 = 0;
        while (true) {
            int i = 1;
            if (totalPercentage2 >= length) {
                break;
            }
            TaskState taskState = taskStateArr[totalPercentage2];
            if (taskState.state == 1 || taskState.state == 2) {
                if (taskState.action.isRemoveAction) {
                    haveRemoveTasks = true;
                } else {
                    haveDownloadTasks = true;
                    if (taskState.downloadPercentage != -1.0f) {
                        allDownloadPercentagesUnknown = false;
                        totalPercentage += taskState.downloadPercentage;
                    }
                    if (taskState.downloadedBytes <= 0) {
                        i = 0;
                    }
                    haveDownloadedBytes |= i;
                    downloadTaskCount++;
                }
            }
            totalPercentage2++;
        }
        if (haveDownloadTasks) {
            totalPercentage2 = C0649R.string.exo_download_downloading;
        } else if (haveRemoveTasks) {
            totalPercentage2 = C0649R.string.exo_download_removing;
        } else {
            titleStringId = 0;
            notificationBuilder = newNotificationBuilder(context, smallIcon, channelId, contentIntent, message, titleStringId);
            length = 0;
            indeterminate = true;
            if (haveDownloadTasks) {
                length = (int) (totalPercentage / ((float) downloadTaskCount));
                boolean z = allDownloadPercentagesUnknown && haveDownloadedBytes;
                indeterminate = z;
            }
            notificationBuilder.setProgress(100, length, indeterminate);
            notificationBuilder.setOngoing(true);
            notificationBuilder.setShowWhen(false);
            return notificationBuilder.build();
        }
        titleStringId = totalPercentage2;
        notificationBuilder = newNotificationBuilder(context, smallIcon, channelId, contentIntent, message, titleStringId);
        length = 0;
        indeterminate = true;
        if (haveDownloadTasks) {
            length = (int) (totalPercentage / ((float) downloadTaskCount));
            if (!allDownloadPercentagesUnknown) {
            }
            indeterminate = z;
        }
        notificationBuilder.setProgress(100, length, indeterminate);
        notificationBuilder.setOngoing(true);
        notificationBuilder.setShowWhen(false);
        return notificationBuilder.build();
    }

    public static Notification buildDownloadCompletedNotification(Context context, @DrawableRes int smallIcon, String channelId, @Nullable PendingIntent contentIntent, @Nullable String message) {
        return newNotificationBuilder(context, smallIcon, channelId, contentIntent, message, C0649R.string.exo_download_completed).build();
    }

    public static Notification buildDownloadFailedNotification(Context context, @DrawableRes int smallIcon, String channelId, @Nullable PendingIntent contentIntent, @Nullable String message) {
        return newNotificationBuilder(context, smallIcon, channelId, contentIntent, message, C0649R.string.exo_download_failed).build();
    }

    private static Builder newNotificationBuilder(Context context, @DrawableRes int smallIcon, String channelId, @Nullable PendingIntent contentIntent, @Nullable String message, @StringRes int titleStringId) {
        Builder notificationBuilder = new Builder(context, channelId).setSmallIcon(smallIcon);
        if (titleStringId != 0) {
            notificationBuilder.setContentTitle(context.getResources().getString(titleStringId));
        }
        if (contentIntent != null) {
            notificationBuilder.setContentIntent(contentIntent);
        }
        if (message != null) {
            notificationBuilder.setStyle(new BigTextStyle().bigText(message));
        }
        return notificationBuilder;
    }
}
