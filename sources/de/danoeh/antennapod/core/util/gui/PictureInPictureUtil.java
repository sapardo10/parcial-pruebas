package de.danoeh.antennapod.core.util.gui;

import android.app.Activity;
import android.os.Build.VERSION;

public class PictureInPictureUtil {
    private PictureInPictureUtil() {
    }

    public static boolean supportsPictureInPicture(Activity activity) {
        if (VERSION.SDK_INT >= 24) {
            return activity.getPackageManager().hasSystemFeature("android.software.picture_in_picture");
        }
        return false;
    }

    public static boolean isInPictureInPictureMode(Activity activity) {
        if (VERSION.SDK_INT < 24 || !supportsPictureInPicture(activity)) {
            return false;
        }
        return activity.isInPictureInPictureMode();
    }
}
