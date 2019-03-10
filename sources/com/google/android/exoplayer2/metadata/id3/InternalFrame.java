package com.google.android.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.util.Util;

public final class InternalFrame extends Id3Frame {
    public static final Creator<InternalFrame> CREATOR = new C05971();
    public static final String ID = "----";
    public final String description;
    public final String domain;
    public final String text;

    /* renamed from: com.google.android.exoplayer2.metadata.id3.InternalFrame$1 */
    static class C05971 implements Creator<InternalFrame> {
        C05971() {
        }

        public InternalFrame createFromParcel(Parcel in) {
            return new InternalFrame(in);
        }

        public InternalFrame[] newArray(int size) {
            return new InternalFrame[size];
        }
    }

    public InternalFrame(String domain, String description, String text) {
        super(ID);
        this.domain = domain;
        this.description = description;
        this.text = text;
    }

    InternalFrame(Parcel in) {
        super(ID);
        this.domain = (String) Util.castNonNull(in.readString());
        this.description = (String) Util.castNonNull(in.readString());
        this.text = (String) Util.castNonNull(in.readString());
    }

    public boolean equals(@Nullable Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                InternalFrame other = (InternalFrame) obj;
                if (Util.areEqual(this.description, other.description)) {
                    if (Util.areEqual(this.domain, other.domain)) {
                        if (Util.areEqual(this.text, other.text)) {
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
        int result = 17 * 31;
        String str = this.domain;
        int i = 0;
        int hashCode = (result + (str != null ? str.hashCode() : 0)) * 31;
        str = this.description;
        result = (hashCode + (str != null ? str.hashCode() : 0)) * 31;
        str = this.text;
        if (str != null) {
            i = str.hashCode();
        }
        return result + i;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.id);
        stringBuilder.append(": domain=");
        stringBuilder.append(this.domain);
        stringBuilder.append(", description=");
        stringBuilder.append(this.description);
        return stringBuilder.toString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.domain);
        dest.writeString(this.text);
    }
}
