package com.google.android.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.util.Util;
import java.util.Arrays;

public final class BinaryFrame extends Id3Frame {
    public static final Creator<BinaryFrame> CREATOR = new C05921();
    public final byte[] data;

    /* renamed from: com.google.android.exoplayer2.metadata.id3.BinaryFrame$1 */
    static class C05921 implements Creator<BinaryFrame> {
        C05921() {
        }

        public BinaryFrame createFromParcel(Parcel in) {
            return new BinaryFrame(in);
        }

        public BinaryFrame[] newArray(int size) {
            return new BinaryFrame[size];
        }
    }

    public BinaryFrame(String id, byte[] data) {
        super(id);
        this.data = data;
    }

    BinaryFrame(Parcel in) {
        super((String) Util.castNonNull(in.readString()));
        this.data = (byte[]) Util.castNonNull(in.createByteArray());
    }

    public boolean equals(@Nullable Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                BinaryFrame other = (BinaryFrame) obj;
                if (!this.id.equals(other.id) || !Arrays.equals(this.data, other.data)) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }

    public int hashCode() {
        return (((17 * 31) + this.id.hashCode()) * 31) + Arrays.hashCode(this.data);
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeByteArray(this.data);
    }
}
