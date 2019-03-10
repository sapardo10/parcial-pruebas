package de.danoeh.antennapod.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import android.util.Log;
import de.danoeh.antennapod.BuildConfig;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import org.antennapod.audio.MediaPlayer;

class UpdateManager {
    private static final String KEY_VERSION_CODE = "version_code";
    private static final String PREF_NAME = "app_version";
    private static final String TAG = UpdateManager.class.getSimpleName();
    private static Context context;
    private static int currentVersionCode;
    private static SharedPreferences prefs;

    private UpdateManager() {
    }

    public static void init(Context context) {
        context = context;
        prefs = context.getSharedPreferences(PREF_NAME, 0);
        try {
            currentVersionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
            int oldVersionCode = getStoredVersionCode();
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("old: ");
            stringBuilder.append(oldVersionCode);
            stringBuilder.append(", current: ");
            stringBuilder.append(currentVersionCode);
            Log.d(str, stringBuilder.toString());
            int i = currentVersionCode;
            if (oldVersionCode < i) {
                onUpgrade(oldVersionCode, i);
                setCurrentVersionCode();
            }
        } catch (NameNotFoundException e) {
            String str2 = TAG;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Failed to obtain package info for package name: ");
            stringBuilder2.append(context.getPackageName());
            Log.e(str2, stringBuilder2.toString(), e);
            currentVersionCode = 0;
        }
    }

    private static int getStoredVersionCode() {
        return prefs.getInt(KEY_VERSION_CODE, -1);
    }

    private static void setCurrentVersionCode() {
        prefs.edit().putInt(KEY_VERSION_CODE, currentVersionCode).apply();
    }

    private static void onUpgrade(int oldVersionCode, int newVersionCode) {
        if (oldVersionCode < 1050004) {
            if (MediaPlayer.isPrestoLibraryInstalled(context) && VERSION.SDK_INT >= 16) {
                UserPreferences.enableSonic();
            }
        }
        if (oldVersionCode < BuildConfig.VERSION_CODE) {
            int oldValueInDays = UserPreferences.getEpisodeCleanupValue();
            if (oldValueInDays > 0) {
                UserPreferences.setEpisodeCleanupValue(oldValueInDays * 24);
            }
        }
    }
}
