package de.danoeh.antennapod.core.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import de.danoeh.antennapod.core.ClientConfig;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.util.FeedUpdateUtils;

public class FeedUpdateReceiver extends BroadcastReceiver {
    private static final String TAG = "FeedUpdateReceiver";

    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received intent");
        ClientConfig.initialize(context);
        FeedUpdateUtils.startAutoUpdate(context, null);
        UserPreferences.restartUpdateAlarm(false);
    }
}
