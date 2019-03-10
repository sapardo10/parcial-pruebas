package de.danoeh.antennapod.core;

import android.app.PendingIntent;
import android.content.Context;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.service.download.DownloadRequest;

public interface DownloadServiceCallbacks {
    PendingIntent getAuthentificationNotificationContentIntent(Context context, DownloadRequest downloadRequest);

    PendingIntent getNotificationContentIntent(Context context);

    PendingIntent getReportNotificationContentIntent(Context context);

    void onFeedParsed(Context context, Feed feed);

    boolean shouldCreateReport();
}
