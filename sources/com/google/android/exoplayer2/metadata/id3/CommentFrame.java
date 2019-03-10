package com.google.android.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.util.Util;

public final class CommentFrame extends Id3Frame {
    public static final Creator<CommentFrame> CREATOR = new C05951();
    public static final String ID = "COMM";
    public final String description;
    public final String language;
    public final String text;

    /* renamed from: com.google.android.exoplayer2.metadata.id3.CommentFrame$1 */
    static class C05951 implements Creator<CommentFrame> {
        C05951() {
        }

        public CommentFrame createFromParcel(Parcel in) {
            return new CommentFrame(in);
        }

        public CommentFrame[] newArray(int size) {
            return new CommentFrame[size];
        }
    }

    public CommentFrame(String language, String description, String text) {
        super(ID);
        this.language = language;
        this.description = description;
        this.text = text;
    }

    CommentFrame(Parcel in) {
        super(ID);
        this.language = (String) Util.castNonNull(in.readString());
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
                CommentFrame other = (CommentFrame) obj;
                if (Util.areEqual(this.description, other.description) && Util.areEqual(this.language, other.language)) {
                    if (Util.areEqual(this.text, other.text)) {
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
        String str = this.language;
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
        stringBuilder.append(": language=");
        stringBuilder.append(this.language);
        stringBuilder.append(", description=");
        stringBuilder.append(this.description);
        return stringBuilder.toString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.language);
        dest.writeString(this.text);
    }
}
