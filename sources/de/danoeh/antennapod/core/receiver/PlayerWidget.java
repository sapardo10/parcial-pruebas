package de.danoeh.antennapod.core.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import de.danoeh.antennapod.core.service.PlayerWidgetJobService;
import java.util.Arrays;

public class PlayerWidget extends AppWidgetProvider {
    private static final String KEY_ENABLED = "WidgetEnabled";
    private static final String PREFS_NAME = "PlayerWidgetPrefs";
    private static final String TAG = "PlayerWidget";

    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        super.onReceive(context, intent);
        PlayerWidgetJobService.updateWidget(context);
    }

    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.d(TAG, "Widget enabled");
        setEnabled(context, true);
        PlayerWidgetJobService.updateWidget(context);
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onUpdate() called with: context = [");
        stringBuilder.append(context);
        stringBuilder.append("], appWidgetManager = [");
        stringBuilder.append(appWidgetManager);
        stringBuilder.append("], appWidgetIds = [");
        stringBuilder.append(Arrays.toString(appWidgetIds));
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
        PlayerWidgetJobService.updateWidget(context);
    }

    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.d(TAG, "Widget disabled");
        setEnabled(context, false);
    }

    public static boolean isEnabled(Context context) {
        return context.getSharedPreferences(PREFS_NAME, 0).getBoolean(KEY_ENABLED, false);
    }

    private void setEnabled(Context context, boolean enabled) {
        context.getSharedPreferences(PREFS_NAME, 0).edit().putBoolean(KEY_ENABLED, enabled).apply();
    }
}
