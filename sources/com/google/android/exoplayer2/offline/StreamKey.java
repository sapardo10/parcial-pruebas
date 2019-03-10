package com.google.android.exoplayer2.offline;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public final class StreamKey implements Comparable<StreamKey> {
    public final int groupIndex;
    public final int periodIndex;
    public final int trackIndex;

    public StreamKey(int groupIndex, int trackIndex) {
        this(0, groupIndex, trackIndex);
    }

    public StreamKey(int periodIndex, int groupIndex, int trackIndex) {
        this.periodIndex = periodIndex;
        this.groupIndex = groupIndex;
        this.trackIndex = trackIndex;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.periodIndex);
        stringBuilder.append(".");
        stringBuilder.append(this.groupIndex);
        stringBuilder.append(".");
        stringBuilder.append(this.trackIndex);
        return stringBuilder.toString();
    }

    public boolean equals(@Nullable Object o) {
        boolean z = true;
        if (this == o) {
            return true;
        }
        if (o != null) {
            if (getClass() == o.getClass()) {
                StreamKey that = (StreamKey) o;
                if (this.periodIndex != that.periodIndex || this.groupIndex != that.groupIndex || this.trackIndex != that.trackIndex) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }

    public int hashCode() {
        return (((this.periodIndex * 31) + this.groupIndex) * 31) + this.trackIndex;
    }

    public int compareTo(@NonNull StreamKey o) {
        int result = this.periodIndex - o.periodIndex;
        if (result != 0) {
            return result;
        }
        result = this.groupIndex - o.groupIndex;
        if (result == 0) {
            return this.trackIndex - o.trackIndex;
        }
        return result;
    }
}
