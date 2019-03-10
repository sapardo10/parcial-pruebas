package de.danoeh.antennapod.core.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;

public class IntentUtils {
    private IntentUtils() {
    }

    public static boolean isCallable(Context context, Intent intent) {
        for (ResolveInfo info : context.getPackageManager().queryIntentActivities(intent, 65536)) {
            if (info.activityInfo.exported) {
                return true;
            }
        }
        return false;
    }

    public static void sendLocalBroadcast(Context context, String action) {
        context.sendBroadcast(new Intent(action).setPackage(context.getPackageName()));
    }
}
