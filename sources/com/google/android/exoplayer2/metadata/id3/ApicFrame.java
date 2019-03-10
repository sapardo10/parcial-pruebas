package com.google.android.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.util.Util;
import java.util.Arrays;

public final class ApicFrame extends Id3Frame {
    public static final Creator<ApicFrame> CREATOR = new C05911();
    public static final String ID = "APIC";
    @Nullable
    public final String description;
    public final String mimeType;
    public final byte[] pictureData;
    public final int pictureType;

    /* renamed from: com.google.android.exoplayer2.metadata.id3.ApicFrame$1 */
    static class C05911 implements Creator<ApicFrame> {
        C05911() {
        }

        public ApicFrame createFromParcel(Parcel in) {
            return new ApicFrame(in);
        }

        public ApicFrame[] newArray(int size) {
            return new ApicFrame[size];
        }
    }

    public ApicFrame(String mimeType, @Nullable String description, int pictureType, byte[] pictureData) {
        super(ID);
        this.mimeType = mimeType;
        this.description = description;
        this.pictureType = pictureType;
        this.pictureData = pictureData;
    }

    ApicFrame(Parcel in) {
        super(ID);
        this.mimeType = (String) Util.castNonNull(in.readString());
        this.description = (String) Util.castNonNull(in.readString());
        this.pictureType = in.readInt();
        this.pictureData = (byte[]) Util.castNonNull(in.createByteArray());
    }

    public boolean equals(@Nullable Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                ApicFrame other = (ApicFrame) obj;
                if (this.pictureType == other.pictureType && Util.areEqual(this.mimeType, other.mimeType)) {
                    if (Util.areEqual(this.description, other.description)) {
                        if (Arrays.equals(this.pictureData, other.pictureData)) {
                            return z;
                        }
                    }
                }
                z = false;
                return z;
            }
        }
        return false;
    }

    public int hashCode() {
        int i = ((17 * 31) + this.pictureType) * 31;
        String str = this.mimeType;
        int i2 = 0;
        int hashCode = (i + (str != null ? str.hashCode() : 0)) * 31;
        str = this.description;
        if (str != null) {
            i2 = str.hashCode();
        }
        return ((hashCode + i2) * 31) + Arrays.hashCode(this.pictureData);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.id);
        stringBuilder.append(": mimeType=");
        stringBuilder.append(this.mimeType);
        stringBuilder.append(", description=");
        stringBuilder.append(this.description);
        return stringBuilder.toString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mimeType);
        dest.writeString(this.description);
        dest.writeInt(this.pictureType);
        dest.writeByteArray(this.pictureData);
    }
}
