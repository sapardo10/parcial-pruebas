package de.danoeh.antennapod.spa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;
import de.danoeh.antennapod.BuildConfig;
import de.danoeh.antennapod.receiver.SPAReceiver;
import org.apache.commons.lang3.Validate;

public class SPAUtil {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final String PREF_HAS_QUERIED_SP_APPS = "prefSPAUtil.hasQueriedSPApps";
    private static final String TAG = "SPAUtil";

    private SPAUtil() {
    }

    public static synchronized boolean sendSPAppsQueryFeedsIntent(Context context) {
        synchronized (SPAUtil.class) {
            Context appContext = context.getApplicationContext();
            if (appContext == null) {
                Log.wtf(TAG, "Unable to get application context");
                return false;
            }
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(appContext);
            if (prefs.getBoolean(PREF_HAS_QUERIED_SP_APPS, false)) {
                return false;
            }
            appContext.sendBroadcast(new Intent(SPAReceiver.ACTION_SP_APPS_QUERY_FEEDS));
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Sending SP_APPS_QUERY_FEEDS intent");
            }
            Editor editor = prefs.edit();
            editor.putBoolean(PREF_HAS_QUERIED_SP_APPS, true);
            editor.commit();
            return true;
        }
    }

    public static void resetSPAPreferences(Context c) {
        if (BuildConfig.DEBUG) {
            Validate.notNull(c);
            Editor editor = PreferenceManager.getDefaultSharedPreferences(c.getApplicationContext()).edit();
            editor.putBoolean(PREF_HAS_QUERIED_SP_APPS, false);
            editor.commit();
        }
    }
}
