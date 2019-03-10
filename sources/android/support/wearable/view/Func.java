package android.support.wearable.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.view.View;

@TargetApi(20)
@Deprecated
class Func {
    Func() {
    }

    static float clamp(float value, int min, int max) {
        if (value < ((float) min)) {
            return (float) min;
        }
        if (value > ((float) max)) {
            return (float) max;
        }
        return value;
    }

    static int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    static boolean getWindowOverscan(View v) {
        Context ctx = v.getContext();
        if (!(ctx instanceof Activity)) {
            return false;
        }
        return (33554432 & ((Activity) ctx).getWindow().getAttributes().flags) != 0;
    }
}
