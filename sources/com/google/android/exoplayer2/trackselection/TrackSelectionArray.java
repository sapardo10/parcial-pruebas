package com.google.android.exoplayer2.trackselection;

import android.support.annotation.Nullable;
import java.util.Arrays;

public final class TrackSelectionArray {
    private int hashCode;
    public final int length;
    private final TrackSelection[] trackSelections;

    public TrackSelectionArray(TrackSelection... trackSelections) {
        this.trackSelections = trackSelections;
        this.length = trackSelections.length;
    }

    @Nullable
    public TrackSelection get(int index) {
        return this.trackSelections[index];
    }

    public TrackSelection[] getAll() {
        return (TrackSelection[]) this.trackSelections.clone();
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = (17 * 31) + Arrays.hashCode(this.trackSelections);
        }
        return this.hashCode;
    }

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                return Arrays.equals(this.trackSelections, ((TrackSelectionArray) obj).trackSelections);
            }
        }
        return false;
    }
}
