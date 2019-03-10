package com.google.android.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.util.Util;
import java.util.Arrays;

public final class ChapterTocFrame extends Id3Frame {
    public static final Creator<ChapterTocFrame> CREATOR = new C05941();
    public static final String ID = "CTOC";
    public final String[] children;
    public final String elementId;
    public final boolean isOrdered;
    public final boolean isRoot;
    private final Id3Frame[] subFrames;

    /* renamed from: com.google.android.exoplayer2.metadata.id3.ChapterTocFrame$1 */
    static class C05941 implements Creator<ChapterTocFrame> {
        C05941() {
        }

        public ChapterTocFrame createFromParcel(Parcel in) {
            return new ChapterTocFrame(in);
        }

        public ChapterTocFrame[] newArray(int size) {
            return new ChapterTocFrame[size];
        }
    }

    public ChapterTocFrame(String elementId, boolean isRoot, boolean isOrdered, String[] children, Id3Frame[] subFrames) {
        super(ID);
        this.elementId = elementId;
        this.isRoot = isRoot;
        this.isOrdered = isOrdered;
        this.children = children;
        this.subFrames = subFrames;
    }

    ChapterTocFrame(Parcel in) {
        super(ID);
        this.elementId = (String) Util.castNonNull(in.readString());
        boolean z = false;
        this.isRoot = in.readByte() != (byte) 0;
        if (in.readByte() != (byte) 0) {
            z = true;
        }
        this.isOrdered = z;
        this.children = in.createStringArray();
        int subFrameCount = in.readInt();
        this.subFrames = new Id3Frame[subFrameCount];
        for (int i = 0; i < subFrameCount; i++) {
            this.subFrames[i] = (Id3Frame) in.readParcelable(Id3Frame.class.getClassLoader());
        }
    }

    public int getSubFrameCount() {
        return this.subFrames.length;
    }

    public Id3Frame getSubFrame(int index) {
        return this.subFrames[index];
    }

    public boolean equals(@Nullable Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                ChapterTocFrame other = (ChapterTocFrame) obj;
                if (this.isRoot == other.isRoot && this.isOrdered == other.isOrdered) {
                    if (Util.areEqual(this.elementId, other.elementId)) {
                        if (Arrays.equals(this.children, other.children)) {
                            if (Arrays.equals(this.subFrames, other.subFrames)) {
                                return z;
                            }
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
        int i = ((((17 * 31) + this.isRoot) * 31) + this.isOrdered) * 31;
        String str = this.elementId;
        return i + (str != null ? str.hashCode() : 0);
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.elementId);
        dest.writeByte((byte) this.isRoot);
        dest.writeByte((byte) this.isOrdered);
        dest.writeStringArray(this.children);
        dest.writeInt(this.subFrames.length);
        for (Id3Frame subFrame : this.subFrames) {
            dest.writeParcelable(subFrame, 0);
        }
    }
}
