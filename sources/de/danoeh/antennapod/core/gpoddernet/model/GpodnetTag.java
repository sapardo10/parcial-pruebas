package de.danoeh.antennapod.core.gpoddernet.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;

public class GpodnetTag implements Parcelable {
    public static final Creator<GpodnetTag> CREATOR = new C07421();
    private final String tag;
    private final String title;
    private final int usage;

    /* renamed from: de.danoeh.antennapod.core.gpoddernet.model.GpodnetTag$1 */
    static class C07421 implements Creator<GpodnetTag> {
        C07421() {
        }

        public GpodnetTag createFromParcel(Parcel in) {
            return new GpodnetTag(in);
        }

        public GpodnetTag[] newArray(int size) {
            return new GpodnetTag[size];
        }
    }

    public GpodnetTag(@NonNull String title, @NonNull String tag, int usage) {
        this.title = title;
        this.tag = tag;
        this.usage = usage;
    }

    private GpodnetTag(Parcel in) {
        this.title = in.readString();
        this.tag = in.readString();
        this.usage = in.readInt();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("GpodnetTag [title=");
        stringBuilder.append(this.title);
        stringBuilder.append(", tag=");
        stringBuilder.append(this.tag);
        stringBuilder.append(", usage=");
        stringBuilder.append(this.usage);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public String getTitle() {
        return this.title;
    }

    public String getTag() {
        return this.tag;
    }

    public int getUsage() {
        return this.usage;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.tag);
        dest.writeInt(this.usage);
    }
}
