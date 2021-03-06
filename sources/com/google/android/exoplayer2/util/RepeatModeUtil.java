package com.google.android.exoplayer2.util;

public final class RepeatModeUtil {
    public static final int REPEAT_TOGGLE_MODE_ALL = 2;
    public static final int REPEAT_TOGGLE_MODE_NONE = 0;
    public static final int REPEAT_TOGGLE_MODE_ONE = 1;

    private RepeatModeUtil() {
    }

    public static int getNextRepeatMode(int currentMode, int enabledModes) {
        for (int offset = 1; offset <= 2; offset++) {
            int proposedMode = (currentMode + offset) % 3;
            if (isRepeatModeEnabled(proposedMode, enabledModes)) {
                return proposedMode;
            }
        }
        return currentMode;
    }

    public static boolean isRepeatModeEnabled(int repeatMode, int enabledModes) {
        boolean z = false;
        switch (repeatMode) {
            case 0:
                return true;
            case 1:
                if ((enabledModes & 1) != 0) {
                    z = true;
                }
                return z;
            case 2:
                if ((enabledModes & 2) != 0) {
                    z = true;
                }
                return z;
            default:
                return false;
        }
    }
}
