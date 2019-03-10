package android.support.v7.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.appcompat.C0286R;
import android.support.v7.mediarouter.C0299R;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.View;

final class MediaRouterThemeHelper {
    static final int COLOR_DARK_ON_LIGHT_BACKGROUND = -570425344;
    static final int COLOR_WHITE_ON_DARK_BACKGROUND = -1;
    private static final float MIN_CONTRAST = 3.0f;

    private MediaRouterThemeHelper() {
    }

    static Context createThemedButtonContext(Context context) {
        context = new ContextThemeWrapper(context, getRouterThemeId(context));
        int style = getThemeResource(context, C0299R.attr.mediaRouteTheme);
        if (style != 0) {
            return new ContextThemeWrapper(context, style);
        }
        return context;
    }

    static Context createThemedDialogContext(Context context, int theme, boolean alertDialog) {
        if (theme == 0) {
            theme = getThemeResource(context, !alertDialog ? C0286R.attr.dialogTheme : C0286R.attr.alertDialogTheme);
        }
        context = new ContextThemeWrapper(context, theme);
        if (getThemeResource(context, C0299R.attr.mediaRouteTheme) != 0) {
            return new ContextThemeWrapper(context, getRouterThemeId(context));
        }
        return context;
    }

    static int createThemedDialogStyle(Context context) {
        int theme = getThemeResource(context, C0299R.attr.mediaRouteTheme);
        if (theme == 0) {
            return getRouterThemeId(context);
        }
        return theme;
    }

    static int getThemeResource(Context context, int attr) {
        TypedValue value = new TypedValue();
        return context.getTheme().resolveAttribute(attr, value, true) ? value.resourceId : 0;
    }

    static float getDisabledAlpha(Context context) {
        TypedValue value = new TypedValue();
        return context.getTheme().resolveAttribute(16842803, value, true) ? value.getFloat() : 0.5f;
    }

    static int getControllerColor(Context context, int style) {
        if (ColorUtils.calculateContrast(-1, getThemeColor(context, style, C0286R.attr.colorPrimary)) >= 3.0d) {
            return -1;
        }
        return COLOR_DARK_ON_LIGHT_BACKGROUND;
    }

    static int getButtonTextColor(Context context) {
        int primaryColor = getThemeColor(context, 0, C0286R.attr.colorPrimary);
        if (ColorUtils.calculateContrast(primaryColor, getThemeColor(context, 0, 16842801)) < 3.0d) {
            return getThemeColor(context, 0, C0286R.attr.colorAccent);
        }
        return primaryColor;
    }

    static void setMediaControlsBackgroundColor(Context context, View mainControls, View groupControls, boolean hasGroup) {
        int primaryColor = getThemeColor(context, 0, C0286R.attr.colorPrimary);
        int primaryDarkColor = getThemeColor(context, 0, C0286R.attr.colorPrimaryDark);
        if (hasGroup && getControllerColor(context, 0) == COLOR_DARK_ON_LIGHT_BACKGROUND) {
            primaryDarkColor = primaryColor;
            primaryColor = -1;
        }
        mainControls.setBackgroundColor(primaryColor);
        groupControls.setBackgroundColor(primaryDarkColor);
        mainControls.setTag(Integer.valueOf(primaryColor));
        groupControls.setTag(Integer.valueOf(primaryDarkColor));
    }

    static void setVolumeSliderColor(Context context, MediaRouteVolumeSlider volumeSlider, View backgroundView) {
        int controllerColor = getControllerColor(context, 0);
        if (Color.alpha(controllerColor) != 255) {
            controllerColor = ColorUtils.compositeColors(controllerColor, ((Integer) backgroundView.getTag()).intValue());
        }
        volumeSlider.setColor(controllerColor);
    }

    private static boolean isLightTheme(Context context) {
        TypedValue value = new TypedValue();
        return context.getTheme().resolveAttribute(C0286R.attr.isLightTheme, value, true) && value.data != 0;
    }

    private static int getThemeColor(Context context, int style, int attr) {
        if (style != 0) {
            TypedArray ta = context.obtainStyledAttributes(style, new int[]{attr});
            int color = ta.getColor(0, 0);
            ta.recycle();
            if (color != 0) {
                return color;
            }
        }
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(attr, value, true);
        if (value.resourceId != 0) {
            return context.getResources().getColor(value.resourceId);
        }
        return value.data;
    }

    private static int getRouterThemeId(Context context) {
        if (isLightTheme(context)) {
            if (getControllerColor(context, 0) == COLOR_DARK_ON_LIGHT_BACKGROUND) {
                return C0299R.style.Theme_MediaRouter_Light;
            }
            return C0299R.style.Theme_MediaRouter_Light_DarkControlPanel;
        } else if (getControllerColor(context, 0) == COLOR_DARK_ON_LIGHT_BACKGROUND) {
            return C0299R.style.Theme_MediaRouter_LightControlPanel;
        } else {
            return C0299R.style.Theme_MediaRouter;
        }
    }
}
