package com.google.android.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.util.Util;

public final class TextInformationFrame extends Id3Frame {
    public static final Creator<TextInformationFrame> CREATOR = new C06001();
    @Nullable
    public final String description;
    public final String value;

    /* renamed from: com.google.android.exoplayer2.metadata.id3.TextInformationFrame$1 */
    static class C06001 implements Creator<TextInformationFrame> {
        C06001() {
        }

        public TextInformationFrame createFromParcel(Parcel in) {
            return new TextInformationFrame(in);
        }

        public TextInformationFrame[] newArray(int size) {
            return new TextInformationFrame[size];
        }
    }

    public TextInformationFrame(String id, @Nullable String description, String value) {
        super(id);
        this.description = description;
        this.value = value;
    }

    TextInformationFrame(Parcel in) {
        super((String) Util.castNonNull(in.readString()));
        this.description = in.readString();
        this.value = (String) Util.castNonNull(in.readString());
    }

    public boolean equals(@Nullable Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                TextInformationFrame other = (TextInformationFrame) obj;
                if (this.id.equals(other.id) && Util.areEqual(this.description, other.description)) {
                    if (Util.areEqual(this.value, other.value)) {
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
        int hashCode = ((17 * 31) + this.id.hashCode()) * 31;
        String str = this.description;
        int i = 0;
        int hashCode2 = (hashCode + (str != null ? str.hashCode() : 0)) * 31;
        str = this.value;
        if (str != null) {
            i = str.hashCode();
        }
        return hashCode2 + i;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.id);
        stringBuilder.append(": value=");
        stringBuilder.append(this.value);
        return stringBuilder.toString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.description);
        dest.writeString(this.value);
    }
}
