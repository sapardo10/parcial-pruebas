package com.google.android.exoplayer2.audio;

import android.support.annotation.Nullable;

public final class AuxEffectInfo {
    public static final int NO_AUX_EFFECT_ID = 0;
    public final int effectId;
    public final float sendLevel;

    public AuxEffectInfo(int effectId, float sendLevel) {
        this.effectId = effectId;
        this.sendLevel = sendLevel;
    }

    public boolean equals(@Nullable Object o) {
        boolean z = true;
        if (this == o) {
            return true;
        }
        if (o != null) {
            if (getClass() == o.getClass()) {
                AuxEffectInfo auxEffectInfo = (AuxEffectInfo) o;
                if (this.effectId == auxEffectInfo.effectId) {
                    if (Float.compare(auxEffectInfo.sendLevel, this.sendLevel) == 0) {
                        return z;
                    }
                }
                z = false;
                return z;
            }
        }
        return false;
    }

    public int hashCode() {
        return (((17 * 31) + this.effectId) * 31) + Float.floatToIntBits(this.sendLevel);
    }
}
