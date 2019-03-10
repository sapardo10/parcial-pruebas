package android.support.wearable.input;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RotateDrawable;
import android.os.Bundle;
import android.provider.Settings.System;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.wearable.C0395R;
import android.support.wearable.internal.SharedLibraryVersion;
import android.view.WindowManager;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.google.android.wearable.input.WearableInputDevice;

@TargetApi(23)
public final class WearableButtons {
    @VisibleForTesting
    static final int LOC_BOTTOM_CENTER = 107;
    @VisibleForTesting
    static final int LOC_BOTTOM_LEFT = 106;
    @VisibleForTesting
    static final int LOC_BOTTOM_RIGHT = 108;
    @VisibleForTesting
    static final int LOC_EAST = 0;
    @VisibleForTesting
    static final int LOC_ENE = 1;
    @VisibleForTesting
    static final int LOC_ESE = 15;
    @VisibleForTesting
    static final int LOC_LEFT_BOTTOM = 105;
    @VisibleForTesting
    static final int LOC_LEFT_CENTER = 104;
    @VisibleForTesting
    static final int LOC_LEFT_TOP = 103;
    @VisibleForTesting
    static final int LOC_NE = 2;
    @VisibleForTesting
    static final int LOC_NNE = 3;
    @VisibleForTesting
    static final int LOC_NNW = 5;
    @VisibleForTesting
    static final int LOC_NORTH = 4;
    @VisibleForTesting
    static final int LOC_NW = 6;
    @VisibleForTesting
    static final int LOC_RIGHT_BOTTOM = 109;
    @VisibleForTesting
    static final int LOC_RIGHT_CENTER = 110;
    @VisibleForTesting
    static final int LOC_RIGHT_TOP = 111;
    private static final int LOC_ROUND_COUNT = 16;
    @VisibleForTesting
    static final int LOC_SE = 14;
    @VisibleForTesting
    static final int LOC_SOUTH = 12;
    @VisibleForTesting
    static final int LOC_SSE = 13;
    @VisibleForTesting
    static final int LOC_SSW = 11;
    @VisibleForTesting
    static final int LOC_SW = 10;
    @VisibleForTesting
    static final int LOC_TOP_CENTER = 101;
    @VisibleForTesting
    static final int LOC_TOP_LEFT = 102;
    @VisibleForTesting
    static final int LOC_TOP_RIGHT = 100;
    @VisibleForTesting
    static final int LOC_UNKNOWN = -1;
    @VisibleForTesting
    static final int LOC_WEST = 8;
    @VisibleForTesting
    static final int LOC_WNW = 7;
    @VisibleForTesting
    static final int LOC_WSW = 9;
    private static final String X_KEY_ROTATED = "x_key_rotated";
    private static final String Y_KEY_ROTATED = "y_key_rotated";
    private static volatile int sButtonCount = -1;

    public static final class ButtonInfo {
        private final int keycode;
        private final int locationZone;
        /* renamed from: x */
        private final float f5x;
        /* renamed from: y */
        private final float f6y;

        public int getKeycode() {
            return this.keycode;
        }

        public float getX() {
            return this.f5x;
        }

        public float getY() {
            return this.f6y;
        }

        @VisibleForTesting
        ButtonInfo(int keycode, float x, float y, int locationZone) {
            this.keycode = keycode;
            this.f5x = x;
            this.f6y = y;
            this.locationZone = locationZone;
        }
    }

    private WearableButtons() {
        throw new RuntimeException("WearableButtons should not be instantiated");
    }

    @Nullable
    public static final ButtonInfo getButtonInfo(Context context, int keycode) {
        if (!isApiAvailable()) {
            return null;
        }
        Bundle bundle = WearableInputDevice.getButtonInfo(context, keycode);
        if (bundle.containsKey("x_key")) {
            if (bundle.containsKey("y_key")) {
                float screenLocationX = bundle.getFloat("x_key");
                float screenLocationY = bundle.getFloat("y_key");
                WindowManager wm = (WindowManager) context.getSystemService("window");
                Point screenSize = new Point();
                wm.getDefaultDisplay().getSize(screenSize);
                if (isLeftyModeEnabled(context)) {
                    float screenRotatedX = ((float) screenSize.x) - screenLocationX;
                    float screenRotatedY = ((float) screenSize.y) - screenLocationY;
                    if (bundle.containsKey(X_KEY_ROTATED) && bundle.containsKey(Y_KEY_ROTATED)) {
                        screenRotatedX = bundle.getFloat(X_KEY_ROTATED);
                        screenRotatedY = bundle.getFloat(Y_KEY_ROTATED);
                    }
                    screenLocationX = screenRotatedX;
                    screenLocationY = screenRotatedY;
                }
                return new ButtonInfo(keycode, screenLocationX, screenLocationY, getLocationZone(context.getResources().getConfiguration().isScreenRound(), screenSize, screenLocationX, screenLocationY));
            }
        }
        return null;
    }

    public static int getButtonCount(Context context) {
        if (!isApiAvailable()) {
            return -1;
        }
        int gottenValue = sButtonCount;
        if (gottenValue == -1) {
            synchronized (WearableButtons.class) {
                int length = WearableInputDevice.getAvailableButtonKeyCodes(context).length;
                sButtonCount = length;
                gottenValue = length;
            }
        }
        return gottenValue;
    }

    @Nullable
    public static final Drawable getButtonIcon(Context context, int keycode) {
        ButtonInfo info = getButtonInfo(context, keycode);
        if (info == null) {
            return null;
        }
        return getButtonIconFromLocationZone(context, info.locationZone);
    }

    @VisibleForTesting
    static final RotateDrawable getButtonIconFromLocationZone(Context context, int locationZone) {
        int id;
        int degrees;
        switch (locationZone) {
            case 0:
                id = C0395R.drawable.ic_cc_settings_button_e;
                degrees = 0;
                break;
            case 1:
            case 2:
            case 3:
                id = C0395R.drawable.ic_cc_settings_button_e;
                degrees = -45;
                break;
            case 4:
                id = C0395R.drawable.ic_cc_settings_button_e;
                degrees = -90;
                break;
            case 5:
            case 6:
            case 7:
                id = C0395R.drawable.ic_cc_settings_button_e;
                degrees = -135;
                break;
            case 8:
                id = C0395R.drawable.ic_cc_settings_button_e;
                degrees = 180;
                break;
            case 9:
            case 10:
            case 11:
                id = C0395R.drawable.ic_cc_settings_button_e;
                degrees = TsExtractor.TS_STREAM_TYPE_E_AC3;
                break;
            case 12:
                id = C0395R.drawable.ic_cc_settings_button_e;
                degrees = 90;
                break;
            case 13:
            case 14:
            case 15:
                id = C0395R.drawable.ic_cc_settings_button_e;
                degrees = 45;
                break;
            default:
                switch (locationZone) {
                    case 100:
                        id = C0395R.drawable.ic_cc_settings_button_bottom;
                        degrees = -90;
                        break;
                    case 101:
                        id = C0395R.drawable.ic_cc_settings_button_center;
                        degrees = -90;
                        break;
                    case 102:
                        id = C0395R.drawable.ic_cc_settings_button_top;
                        degrees = -90;
                        break;
                    case 103:
                        id = C0395R.drawable.ic_cc_settings_button_bottom;
                        degrees = 180;
                        break;
                    case 104:
                        id = C0395R.drawable.ic_cc_settings_button_center;
                        degrees = 180;
                        break;
                    case 105:
                        id = C0395R.drawable.ic_cc_settings_button_top;
                        degrees = 180;
                        break;
                    case 106:
                        id = C0395R.drawable.ic_cc_settings_button_bottom;
                        degrees = 90;
                        break;
                    case 107:
                        id = C0395R.drawable.ic_cc_settings_button_center;
                        degrees = 90;
                        break;
                    case 108:
                        id = C0395R.drawable.ic_cc_settings_button_top;
                        degrees = 90;
                        break;
                    case 109:
                        id = C0395R.drawable.ic_cc_settings_button_bottom;
                        degrees = 0;
                        break;
                    case 110:
                        id = C0395R.drawable.ic_cc_settings_button_center;
                        degrees = 0;
                        break;
                    case 111:
                        id = C0395R.drawable.ic_cc_settings_button_top;
                        degrees = 0;
                        break;
                    default:
                        throw new IllegalArgumentException("Unexpected location zone");
                }
        }
        RotateDrawable rotateIcon = new RotateDrawable();
        rotateIcon.setDrawable(context.getDrawable(id));
        rotateIcon.setFromDegrees((float) degrees);
        rotateIcon.setToDegrees((float) degrees);
        rotateIcon.setLevel(1);
        return rotateIcon;
    }

    public static final CharSequence getButtonLabel(Context context, int keycode) {
        int[] buttonsInQuadrantCount = new int[4];
        int[] buttonCodes = WearableInputDevice.getAvailableButtonKeyCodes(context);
        for (int key : buttonCodes) {
            ButtonInfo info = getButtonInfo(context, key);
            if (info != null) {
                int quadrantIndex = getQuadrantIndex(info.locationZone);
                if (quadrantIndex != -1) {
                    buttonsInQuadrantCount[quadrantIndex] = buttonsInQuadrantCount[quadrantIndex] + 1;
                }
            }
        }
        ButtonInfo info2 = getButtonInfo(context, keycode);
        int key2 = info2 != null ? getQuadrantIndex(info2.locationZone) : -1;
        if (info2 == null) {
            return null;
        }
        return context.getString(getFriendlyLocationZoneStringId(info2.locationZone, key2 == -1 ? 0 : buttonsInQuadrantCount[key2]));
    }

    private static int getQuadrantIndex(int locationZone) {
        switch (locationZone) {
            case 1:
            case 2:
            case 3:
                return 0;
            case 5:
            case 6:
            case 7:
                return 1;
            case 9:
            case 10:
            case 11:
                return 2;
            case 13:
            case 14:
            case 15:
                return 3;
            default:
                return -1;
        }
    }

    @VisibleForTesting
    static int getFriendlyLocationZoneStringId(int locationZone, int buttonsInQuadrantCount) {
        if (buttonsInQuadrantCount == 2) {
            switch (locationZone) {
                case 1:
                    return C0395R.string.buttons_round_top_right_lower;
                case 2:
                case 3:
                    return C0395R.string.buttons_round_top_right_upper;
                case 5:
                case 6:
                    return C0395R.string.buttons_round_top_left_upper;
                case 7:
                    return C0395R.string.buttons_round_top_left_lower;
                case 9:
                case 10:
                    return C0395R.string.buttons_round_bottom_right_upper;
                case 11:
                    return C0395R.string.buttons_round_bottom_right_lower;
                case 13:
                    return C0395R.string.buttons_round_bottom_left_lower;
                case 14:
                case 15:
                    return C0395R.string.buttons_round_bottom_left_upper;
                default:
                    break;
            }
        }
        switch (locationZone) {
            case 0:
                return C0395R.string.buttons_round_center_right;
            case 1:
            case 2:
            case 3:
                return C0395R.string.buttons_round_top_right;
            case 4:
                return C0395R.string.buttons_round_top_center;
            case 5:
            case 6:
            case 7:
                return C0395R.string.buttons_round_top_left;
            case 8:
                return C0395R.string.buttons_round_center_left;
            case 9:
            case 10:
            case 11:
                return C0395R.string.buttons_round_bottom_left;
            case 12:
                return C0395R.string.buttons_round_bottom_center;
            case 13:
            case 14:
            case 15:
                return C0395R.string.buttons_round_bottom_right;
            default:
                switch (locationZone) {
                    case 100:
                        return C0395R.string.buttons_rect_top_right;
                    case 101:
                        return C0395R.string.buttons_rect_top_center;
                    case 102:
                        return C0395R.string.buttons_rect_top_left;
                    case 103:
                        return C0395R.string.buttons_rect_left_top;
                    case 104:
                        return C0395R.string.buttons_rect_left_center;
                    case 105:
                        return C0395R.string.buttons_rect_left_bottom;
                    case 106:
                        return C0395R.string.buttons_rect_bottom_left;
                    case 107:
                        return C0395R.string.buttons_rect_bottom_center;
                    case 108:
                        return C0395R.string.buttons_rect_bottom_right;
                    case 109:
                        return C0395R.string.buttons_rect_right_bottom;
                    case 110:
                        return C0395R.string.buttons_rect_right_center;
                    case 111:
                        return C0395R.string.buttons_rect_right_top;
                    default:
                        throw new IllegalArgumentException("Unexpected location zone");
                }
        }
    }

    @VisibleForTesting
    static int getLocationZone(boolean isRound, Point screenSize, float screenLocationX, float screenLocationY) {
        if (screenLocationX != Float.MAX_VALUE) {
            if (screenLocationY != Float.MAX_VALUE) {
                int locationZoneRound;
                if (isRound) {
                    locationZoneRound = getLocationZoneRound(screenSize, screenLocationX, screenLocationY);
                } else {
                    locationZoneRound = getLocationZoneRectangular(screenSize, screenLocationX, screenLocationY);
                }
                return locationZoneRound;
            }
        }
        return -1;
    }

    private static int getLocationZoneRound(Point screenSize, float screenLocationX, float screenLocationY) {
        double angle = Math.atan2((double) (((float) (screenSize.y / 2)) - screenLocationY), (double) (screenLocationX - ((float) (screenSize.x / 2))));
        if (angle < 0.0d) {
            angle += 6.283185307179586d;
        }
        return Math.round((float) (angle / 0.39269908169872414d)) % 16;
    }

    private static int getLocationZoneRectangular(Point screenSize, float screenLocationX, float screenLocationY) {
        float deltaFromLeft = screenLocationX;
        float deltaFromRight = ((float) screenSize.x) - screenLocationX;
        float deltaFromTop = screenLocationY;
        float minDelta = Math.min(deltaFromLeft, Math.min(deltaFromRight, Math.min(deltaFromTop, ((float) screenSize.y) - screenLocationY)));
        if (minDelta == deltaFromLeft) {
            switch (whichThird((float) screenSize.y, screenLocationY)) {
                case 0:
                    return 103;
                case 1:
                    return 104;
                default:
                    return 105;
            }
        } else if (minDelta == deltaFromRight) {
            switch (whichThird((float) screenSize.y, screenLocationY)) {
                case 0:
                    return 111;
                case 1:
                    return 110;
                default:
                    return 109;
            }
        } else if (minDelta == deltaFromTop) {
            switch (whichThird((float) screenSize.x, screenLocationX)) {
                case 0:
                    return 102;
                case 1:
                    return 101;
                default:
                    return 100;
            }
        } else {
            switch (whichThird((float) screenSize.x, screenLocationX)) {
                case 0:
                    return 106;
                case 1:
                    return 107;
                default:
                    return 108;
            }
        }
    }

    private static int whichThird(float screenLength, float screenLocation) {
        if (screenLocation <= screenLength / 3.0f) {
            return 0;
        }
        if (screenLocation <= (2.0f * screenLength) / 3.0f) {
            return 1;
        }
        return 2;
    }

    private static boolean isApiAvailable() {
        return SharedLibraryVersion.version() >= 1;
    }

    private static boolean isLeftyModeEnabled(Context context) {
        return System.getInt(context.getContentResolver(), "user_rotation", 0) == 2;
    }
}
