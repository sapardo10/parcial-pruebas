package com.google.android.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.util.Util;
import java.util.Arrays;

public final class PrivFrame extends Id3Frame {
    public static final Creator<PrivFrame> CREATOR = new C05991();
    public static final String ID = "PRIV";
    public final String owner;
    public final byte[] privateData;

    /* renamed from: com.google.android.exoplayer2.metadata.id3.PrivFrame$1 */
    static class C05991 implements Creator<PrivFrame> {
        C05991() {
        }

        public PrivFrame createFromParcel(Parcel in) {
            return new PrivFrame(in);
        }

        public PrivFrame[] newArray(int size) {
            return new PrivFrame[size];
        }
    }

    public PrivFrame(String owner, byte[] privateData) {
        super(ID);
        this.owner = owner;
        this.privateData = privateData;
    }

    PrivFrame(Parcel in) {
        super(ID);
        this.owner = (String) Util.castNonNull(in.readString());
        this.privateData = (byte[]) Util.castNonNull(in.createByteArray());
    }

    public boolean equals(@Nullable Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                PrivFrame other = (PrivFrame) obj;
                if (!Util.areEqual(this.owner, other.owner) || !Arrays.equals(this.privateData, other.privateData)) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }

    public int hashCode() {
        int result = 17 * 31;
        String str = this.owner;
        return ((result + (str != null ? str.hashCode() : 0)) * 31) + Arrays.hashCode(this.privateData);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.id);
        stringBuilder.append(": owner=");
        stringBuilder.append(this.owner);
        return stringBuilder.toString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.owner);
        dest.writeByteArray(this.privateData);
    }
}
