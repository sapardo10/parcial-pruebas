package com.google.android.exoplayer2.metadata;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public final class Metadata implements Parcelable {
    public static final Creator<Metadata> CREATOR = new C05891();
    private final Entry[] entries;

    /* renamed from: com.google.android.exoplayer2.metadata.Metadata$1 */
    static class C05891 implements Creator<Metadata> {
        C05891() {
        }

        public Metadata createFromParcel(Parcel in) {
            return new Metadata(in);
        }

        public Metadata[] newArray(int size) {
            return new Metadata[0];
        }
    }

    public interface Entry extends Parcelable {
    }

    public Metadata(Entry... entries) {
        this.entries = entries == null ? new Entry[0] : entries;
    }

    public Metadata(List<? extends Entry> entries) {
        if (entries != null) {
            this.entries = new Entry[entries.size()];
            entries.toArray(this.entries);
            return;
        }
        this.entries = new Entry[0];
    }

    Metadata(Parcel in) {
        this.entries = new Entry[in.readInt()];
        int i = 0;
        while (true) {
            Entry[] entryArr = this.entries;
            if (i < entryArr.length) {
                entryArr[i] = (Entry) in.readParcelable(Entry.class.getClassLoader());
                i++;
            } else {
                return;
            }
        }
    }

    public int length() {
        return this.entries.length;
    }

    public Entry get(int index) {
        return this.entries[index];
    }

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                return Arrays.equals(this.entries, ((Metadata) obj).entries);
            }
        }
        return false;
    }

    public int hashCode() {
        return Arrays.hashCode(this.entries);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.entries.length);
        for (Entry entry : this.entries) {
            dest.writeParcelable(entry, 0);
        }
    }
}
