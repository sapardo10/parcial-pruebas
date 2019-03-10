package de.danoeh.antennapod.core.util;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.util.Log;
import android.util.TypedValue;
import de.danoeh.antennapod.core.C0734R;
import de.danoeh.antennapod.core.preferences.UserPreferences;

public class ThemeUtils {
    private static final String TAG = "ThemeUtils";

    private ThemeUtils() {
    }

    public static int getSelectionBackgroundColor() {
        int theme = UserPreferences.getTheme();
        if (theme == C0734R.style.Theme_AntennaPod_Dark) {
            return C0734R.color.selection_background_color_dark;
        }
        if (theme == C0734R.style.Theme_AntennaPod_TrueBlack) {
            return C0734R.color.selection_background_color_trueblack;
        }
        if (theme == C0734R.style.Theme_AntennaPod_Light) {
            return C0734R.color.selection_background_color_light;
        }
        Log.e(TAG, "getSelectionBackgroundColor could not match the current theme to any color!");
        return C0734R.color.selection_background_color_light;
    }

    @ColorInt
    public static int getColorFromAttr(Context context, @AttrRes int attr) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attr, typedValue, true);
        return typedValue.data;
    }
}
