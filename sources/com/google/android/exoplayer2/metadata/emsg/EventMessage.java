package com.google.android.exoplayer2.metadata.emsg;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.metadata.Metadata.Entry;
import com.google.android.exoplayer2.util.Util;
import java.util.Arrays;

public final class EventMessage implements Entry {
    public static final Creator<EventMessage> CREATOR = new C05901();
    public final long durationMs;
    private int hashCode;
    public final long id;
    public final byte[] messageData;
    public final long presentationTimeUs;
    public final String schemeIdUri;
    public final String value;

    /* renamed from: com.google.android.exoplayer2.metadata.emsg.EventMessage$1 */
    static class C05901 implements Creator<EventMessage> {
        C05901() {
        }

        public EventMessage createFromParcel(Parcel in) {
            return new EventMessage(in);
        }

        public EventMessage[] newArray(int size) {
            return new EventMessage[size];
        }
    }

    public EventMessage(String schemeIdUri, String value, long durationMs, long id, byte[] messageData, long presentationTimeUs) {
        this.schemeIdUri = schemeIdUri;
        this.value = value;
        this.durationMs = durationMs;
        this.id = id;
        this.messageData = messageData;
        this.presentationTimeUs = presentationTimeUs;
    }

    EventMessage(Parcel in) {
        this.schemeIdUri = (String) Util.castNonNull(in.readString());
        this.value = (String) Util.castNonNull(in.readString());
        this.presentationTimeUs = in.readLong();
        this.durationMs = in.readLong();
        this.id = in.readLong();
        this.messageData = (byte[]) Util.castNonNull(in.createByteArray());
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            int result = 17 * 31;
            String str = this.schemeIdUri;
            int i = 0;
            int hashCode = (result + (str != null ? str.hashCode() : 0)) * 31;
            str = this.value;
            if (str != null) {
                i = str.hashCode();
            }
            result = (hashCode + i) * 31;
            long j = this.presentationTimeUs;
            hashCode = (result + ((int) (j ^ (j >>> 32)))) * 31;
            j = this.durationMs;
            result = (hashCode + ((int) (j ^ (j >>> 32)))) * 31;
            j = this.id;
            this.hashCode = ((result + ((int) (j ^ (j >>> 32)))) * 31) + Arrays.hashCode(this.messageData);
        }
        return this.hashCode;
    }

    public boolean equals(@Nullable Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                EventMessage other = (EventMessage) obj;
                if (this.presentationTimeUs == other.presentationTimeUs && this.durationMs == other.durationMs && this.id == other.id) {
                    if (Util.areEqual(this.schemeIdUri, other.schemeIdUri)) {
                        if (Util.areEqual(this.value, other.value) && Arrays.equals(this.messageData, other.messageData)) {
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

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("EMSG: scheme=");
        stringBuilder.append(this.schemeIdUri);
        stringBuilder.append(", id=");
        stringBuilder.append(this.id);
        stringBuilder.append(", value=");
        stringBuilder.append(this.value);
        return stringBuilder.toString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.schemeIdUri);
        dest.writeString(this.value);
        dest.writeLong(this.presentationTimeUs);
        dest.writeLong(this.durationMs);
        dest.writeLong(this.id);
        dest.writeByteArray(this.messageData);
    }
}
