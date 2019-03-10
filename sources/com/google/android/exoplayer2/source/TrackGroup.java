package com.google.android.exoplayer2.source;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.util.Assertions;
import java.util.Arrays;

public final class TrackGroup implements Parcelable {
    public static final Creator<TrackGroup> CREATOR = new C06241();
    private final Format[] formats;
    private int hashCode;
    public final int length;

    /* renamed from: com.google.android.exoplayer2.source.TrackGroup$1 */
    static class C06241 implements Creator<TrackGroup> {
        C06241() {
        }

        public TrackGroup createFromParcel(Parcel in) {
            return new TrackGroup(in);
        }

        public TrackGroup[] newArray(int size) {
            return new TrackGroup[size];
        }
    }

    public TrackGroup(Format... formats) {
        Assertions.checkState(formats.length > 0);
        this.formats = formats;
        this.length = formats.length;
    }

    TrackGroup(Parcel in) {
        this.length = in.readInt();
        this.formats = new Format[this.length];
        for (int i = 0; i < this.length; i++) {
            this.formats[i] = (Format) in.readParcelable(Format.class.getClassLoader());
        }
    }

    public Format getFormat(int index) {
        return this.formats[index];
    }

    public int indexOf(Format format) {
        int i = 0;
        while (true) {
            Format[] formatArr = this.formats;
            if (i >= formatArr.length) {
                return -1;
            }
            if (format == formatArr[i]) {
                return i;
            }
            i++;
        }
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = (17 * 31) + Arrays.hashCode(this.formats);
        }
        return this.hashCode;
    }

    public boolean equals(@Nullable Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                TrackGroup other = (TrackGroup) obj;
                if (this.length != other.length || !Arrays.equals(this.formats, other.formats)) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.length);
        for (int i = 0; i < this.length; i++) {
            dest.writeParcelable(this.formats[i], 0);
        }
    }
}
