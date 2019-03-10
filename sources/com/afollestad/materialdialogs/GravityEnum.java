package com.afollestad.materialdialogs;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.support.v4.view.GravityCompat;

public enum GravityEnum {
    START,
    CENTER,
    END;
    
    private static final boolean HAS_RTL = false;

    @SuppressLint({"RtlHardcoded"})
    public int getGravityInt() {
        switch (this) {
            case START:
                return HAS_RTL ? 8388611 : 3;
            case CENTER:
                return 1;
            case END:
                return HAS_RTL ? GravityCompat.END : 5;
            default:
                throw new IllegalStateException("Invalid gravity constant");
        }
    }

    @TargetApi(17)
    public int getTextAlignment() {
        switch (this) {
            case CENTER:
                return 4;
            case END:
                return 6;
            default:
                return 5;
        }
    }
}
