package de.danoeh.antennapod.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import de.danoeh.antennapod.core.ClientConfig;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.storage.DownloadRequestException;
import de.danoeh.antennapod.core.storage.DownloadRequester;
import de.danoeh.antennapod.debug.R;
import java.util.Arrays;

public class SPAReceiver extends BroadcastReceiver {
    public static final String ACTION_SP_APPS_QUERY_FEEDS = "de.danoeh.antennapdsp.intent.SP_APPS_QUERY_FEEDS";
    private static final String ACTION_SP_APPS_QUERY_FEEDS_REPSONSE = "de.danoeh.antennapdsp.intent.SP_APPS_QUERY_FEEDS_RESPONSE";
    private static final String ACTION_SP_APPS_QUERY_FEEDS_REPSONSE_FEEDS_EXTRA = "feeds";
    private static final String TAG = "SPAReceiver";

    public void onReceive(Context context, Intent intent) {
        if (TextUtils.equals(intent.getAction(), ACTION_SP_APPS_QUERY_FEEDS_REPSONSE)) {
            Log.d(TAG, "Received SP_APPS_QUERY_RESPONSE");
            if (intent.hasExtra(ACTION_SP_APPS_QUERY_FEEDS_REPSONSE_FEEDS_EXTRA)) {
                String[] feedUrls = intent.getStringArrayExtra(ACTION_SP_APPS_QUERY_FEEDS_REPSONSE_FEEDS_EXTRA);
                if (feedUrls == null) {
                    Log.e(TAG, "Received invalid SP_APPS_QUERY_REPSONSE: extra was null");
                    return;
                }
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Received feeds list: ");
                stringBuilder.append(Arrays.toString(feedUrls));
                Log.d(str, stringBuilder.toString());
                ClientConfig.initialize(context);
                for (String url : feedUrls) {
                    try {
                        DownloadRequester.getInstance().downloadFeed(context, new Feed(url, null));
                    } catch (DownloadRequestException e) {
                        String str2 = TAG;
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("Error while trying to add feed ");
                        stringBuilder2.append(url);
                        Log.e(str2, stringBuilder2.toString());
                        e.printStackTrace();
                    }
                }
                Toast.makeText(context, R.string.sp_apps_importing_feeds_msg, 1).show();
                return;
            }
            Log.e(TAG, "Received invalid SP_APPS_QUERY_RESPONSE: Contains no extra");
        }
    }
}
