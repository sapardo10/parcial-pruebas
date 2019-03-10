package de.danoeh.antennapod.core.util;

import android.content.Context;
import android.util.Log;
import de.danoeh.antennapod.core.storage.DBTasks;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;

public class FeedUpdateUtils {
    private static final String TAG = "FeedUpdateUtils";

    private FeedUpdateUtils() {
    }

    public static void startAutoUpdate(Context context, Runnable callback) {
        try {
            Awaitility.with().pollInterval(1, TimeUnit.SECONDS).await().atMost(10, TimeUnit.SECONDS).until(-$$Lambda$FeedUpdateUtils$0xpPzMlFg84O368tsQzM3OuuB2c.INSTANCE);
            DBTasks.refreshAllFeeds(context, null, callback);
        } catch (ConditionTimeoutException e) {
            Log.d(TAG, "Blocking automatic update: no wifi available / no mobile updates allowed");
        }
    }

    static /* synthetic */ Boolean lambda$startAutoUpdate$0() throws Exception {
        boolean z = NetworkUtils.networkAvailable() && NetworkUtils.isDownloadAllowed();
        return Boolean.valueOf(z);
    }
}
