package com.google.android.exoplayer2;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.util.Assertions;

public final class SeekParameters {
    public static final SeekParameters CLOSEST_SYNC = new SeekParameters(Long.MAX_VALUE, Long.MAX_VALUE);
    public static final SeekParameters DEFAULT = EXACT;
    public static final SeekParameters EXACT = new SeekParameters(0, 0);
    public static final SeekParameters NEXT_SYNC = new SeekParameters(0, Long.MAX_VALUE);
    public static final SeekParameters PREVIOUS_SYNC = new SeekParameters(Long.MAX_VALUE, 0);
    public final long toleranceAfterUs;
    public final long toleranceBeforeUs;

    public SeekParameters(long toleranceBeforeUs, long toleranceAfterUs) {
        boolean z = true;
        Assertions.checkArgument(toleranceBeforeUs >= 0);
        if (toleranceAfterUs < 0) {
            z = false;
        }
        Assertions.checkArgument(z);
        this.toleranceBeforeUs = toleranceBeforeUs;
        this.toleranceAfterUs = toleranceAfterUs;
    }

    public boolean equals(@Nullable Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                SeekParameters other = (SeekParameters) obj;
                if (this.toleranceBeforeUs != other.toleranceBeforeUs || this.toleranceAfterUs != other.toleranceAfterUs) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }

    public int hashCode() {
        return (((int) this.toleranceBeforeUs) * 31) + ((int) this.toleranceAfterUs);
    }
}
