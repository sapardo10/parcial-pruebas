package de.danoeh.antennapod.core.util.playback;

import android.content.Context;
import de.danoeh.antennapod.core.C0734R;

public class MediaPlayerError {
    private MediaPlayerError() {
    }

    public static String getErrorString(Context context, int code) {
        int resId;
        if (code != 100) {
            resId = C0734R.string.playback_error_unknown;
        } else {
            resId = C0734R.string.playback_error_server_died;
        }
        return context.getString(resId);
    }
}
