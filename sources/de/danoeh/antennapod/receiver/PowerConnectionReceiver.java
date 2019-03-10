package de.danoeh.antennapod.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import de.danoeh.antennapod.core.ClientConfig;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.storage.DBTasks;
import de.danoeh.antennapod.core.storage.DownloadRequester;

public class PowerConnectionReceiver extends BroadcastReceiver {
    private static final String TAG = "PowerConnectionReceiver";

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("charging intent: ");
        stringBuilder.append(action);
        Log.d(str, stringBuilder.toString());
        ClientConfig.initialize(context);
        if ("android.intent.action.ACTION_POWER_CONNECTED".equals(action)) {
            Log.d(TAG, "charging, starting auto-download");
            DBTasks.autodownloadUndownloadedItems(context);
        } else if (UserPreferences.isEnableAutodownloadOnBattery()) {
            Log.d(TAG, "not charging anymore, but the user allows auto-download when on battery so we'll keep going");
        } else {
            Log.d(TAG, "not charging anymore, canceling auto-download");
            DownloadRequester.getInstance().cancelAllDownloads(context);
        }
    }
}
