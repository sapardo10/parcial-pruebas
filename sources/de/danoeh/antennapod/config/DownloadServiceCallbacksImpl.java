package de.danoeh.antennapod.config;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import de.danoeh.antennapod.activity.DownloadAuthenticationActivity;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.core.DownloadServiceCallbacks;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.service.download.DownloadRequest;
import de.danoeh.antennapod.fragment.DownloadsFragment;

public class DownloadServiceCallbacksImpl implements DownloadServiceCallbacks {
    public PendingIntent getNotificationContentIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_NAV_TYPE, 0);
        intent.putExtra(MainActivity.EXTRA_FRAGMENT_TAG, DownloadsFragment.TAG);
        Bundle args = new Bundle();
        args.putInt(DownloadsFragment.ARG_SELECTED_TAB, 0);
        intent.putExtra(MainActivity.EXTRA_FRAGMENT_ARGS, args);
        return PendingIntent.getActivity(context, 0, intent, 134217728);
    }

    public PendingIntent getAuthentificationNotificationContentIntent(Context context, DownloadRequest request) {
        Intent activityIntent = new Intent(context.getApplicationContext(), DownloadAuthenticationActivity.class);
        activityIntent.putExtra("request", request);
        activityIntent.putExtra(DownloadAuthenticationActivity.ARG_SEND_TO_DOWNLOAD_REQUESTER_BOOL, true);
        return PendingIntent.getActivity(context.getApplicationContext(), 0, activityIntent, 1073741824);
    }

    public PendingIntent getReportNotificationContentIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_NAV_TYPE, 0);
        intent.putExtra(MainActivity.EXTRA_FRAGMENT_TAG, DownloadsFragment.TAG);
        Bundle args = new Bundle();
        args.putInt(DownloadsFragment.ARG_SELECTED_TAB, 2);
        intent.putExtra(MainActivity.EXTRA_FRAGMENT_ARGS, args);
        return PendingIntent.getActivity(context, 0, intent, 134217728);
    }

    public void onFeedParsed(Context context, Feed feed) {
    }

    public boolean shouldCreateReport() {
        return true;
    }
}
