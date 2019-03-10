package de.danoeh.antennapod.core.util;

import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.app.NotificationCompat;

public class PowerUtils {
    private static final String TAG = "PowerUtils";

    private PowerUtils() {
    }

    public static boolean deviceCharging(Context context) {
        int status = context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED")).getIntExtra(NotificationCompat.CATEGORY_STATUS, -1);
        if (status != 2) {
            if (status != 5) {
                return false;
            }
        }
        return true;
    }
}
