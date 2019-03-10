package de.danoeh.antennapod.core.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import de.danoeh.antennapod.core.ClientConfig;
import de.danoeh.antennapod.core.preferences.UserPreferences;

public class AlarmUpdateReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmUpdateReceiver";

    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received intent");
        if (TextUtils.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED")) {
            Log.d(TAG, "Resetting update alarm after reboot");
        } else if (TextUtils.equals(intent.getAction(), "android.intent.action.PACKAGE_REPLACED")) {
            Log.d(TAG, "Resetting update alarm after app upgrade");
        }
        ClientConfig.initialize(context);
        UserPreferences.restartUpdateAlarm(false);
    }
}
