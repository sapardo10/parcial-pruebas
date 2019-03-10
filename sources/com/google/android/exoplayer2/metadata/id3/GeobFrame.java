package com.google.android.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.util.Util;
import java.util.Arrays;

public final class GeobFrame extends Id3Frame {
    public static final Creator<GeobFrame> CREATOR = new C05961();
    public static final String ID = "GEOB";
    public final byte[] data;
    public final String description;
    public final String filename;
    public final String mimeType;

    /* renamed from: com.google.android.exoplayer2.metadata.id3.GeobFrame$1 */
    static class C05961 implements Creator<GeobFrame> {
        C05961() {
        }

        public GeobFrame createFromParcel(Parcel in) {
            return new GeobFrame(in);
        }

        public GeobFrame[] newArray(int size) {
            return new GeobFrame[size];
        }
    }

    public GeobFrame(String mimeType, String filename, String description, byte[] data) {
        super(ID);
        this.mimeType = mimeType;
        this.filename = filename;
        this.description = description;
        this.data = data;
    }

    GeobFrame(Parcel in) {
        super(ID);
        this.mimeType = (String) Util.castNonNull(in.readString());
        this.filename = (String) Util.castNonNull(in.readString());
        this.description = (String) Util.castNonNull(in.readString());
        this.data = (byte[]) Util.castNonNull(in.createByteArray());
    }

    public boolean equals(@Nullable Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                GeobFrame other = (GeobFrame) obj;
                if (Util.areEqual(this.mimeType, other.mimeType) && Util.areEqual(this.filename, other.filename)) {
                    if (Util.areEqual(this.description, other.description) && Arrays.equals(this.data, other.data)) {
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
        int result = 17 * 31;
        String str = this.mimeType;
        int i = 0;
        int hashCode = (result + (str != null ? str.hashCode() : 0)) * 31;
        str = this.filename;
        result = (hashCode + (str != null ? str.hashCode() : 0)) * 31;
        str = this.description;
        if (str != null) {
            i = str.hashCode();
        }
        return ((result + i) * 31) + Arrays.hashCode(this.data);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.id);
        stringBuilder.append(": mimeType=");
        stringBuilder.append(this.mimeType);
        stringBuilder.append(", filename=");
        stringBuilder.append(this.filename);
        stringBuilder.append(", description=");
        stringBuilder.append(this.description);
        return stringBuilder.toString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mimeType);
        dest.writeString(this.filename);
        dest.writeString(this.description);
        dest.writeByteArray(this.data);
    }
}
