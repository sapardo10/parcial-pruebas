package com.google.android.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.util.Util;

public final class UrlLinkFrame extends Id3Frame {
    public static final Creator<UrlLinkFrame> CREATOR = new C06011();
    @Nullable
    public final String description;
    public final String url;

    /* renamed from: com.google.android.exoplayer2.metadata.id3.UrlLinkFrame$1 */
    static class C06011 implements Creator<UrlLinkFrame> {
        C06011() {
        }

        public UrlLinkFrame createFromParcel(Parcel in) {
            return new UrlLinkFrame(in);
        }

        public UrlLinkFrame[] newArray(int size) {
            return new UrlLinkFrame[size];
        }
    }

    public UrlLinkFrame(String id, @Nullable String description, String url) {
        super(id);
        this.description = description;
        this.url = url;
    }

    UrlLinkFrame(Parcel in) {
        super((String) Util.castNonNull(in.readString()));
        this.description = in.readString();
        this.url = (String) Util.castNonNull(in.readString());
    }

    public boolean equals(@Nullable Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                UrlLinkFrame other = (UrlLinkFrame) obj;
                if (this.id.equals(other.id) && Util.areEqual(this.description, other.description)) {
                    if (Util.areEqual(this.url, other.url)) {
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
        str = this.url;
        if (str != null) {
            i = str.hashCode();
        }
        return hashCode2 + i;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.id);
        stringBuilder.append(": url=");
        stringBuilder.append(this.url);
        return stringBuilder.toString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.description);
        dest.writeString(this.url);
    }
}
