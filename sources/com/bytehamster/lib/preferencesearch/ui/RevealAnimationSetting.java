package com.bytehamster.lib.preferencesearch.ui;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class RevealAnimationSetting implements Parcelable {
    public static final Creator<RevealAnimationSetting> CREATOR = new C05541();
    private int centerX;
    private int centerY;
    private int colorAccent;
    private int height;
    private int width;

    /* renamed from: com.bytehamster.lib.preferencesearch.ui.RevealAnimationSetting$1 */
    static class C05541 implements Creator<RevealAnimationSetting> {
        C05541() {
        }

        public RevealAnimationSetting createFromParcel(Parcel in) {
            return new RevealAnimationSetting(in);
        }

        public RevealAnimationSetting[] newArray(int size) {
            return new RevealAnimationSetting[size];
        }
    }

    public RevealAnimationSetting(int centerX, int centerY, int width, int height, int colorAccent) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.width = width;
        this.height = height;
        this.colorAccent = colorAccent;
    }

    private RevealAnimationSetting(Parcel in) {
        this.centerX = in.readInt();
        this.centerY = in.readInt();
        this.width = in.readInt();
        this.height = in.readInt();
        this.colorAccent = in.readInt();
    }

    public int getCenterX() {
        return this.centerX;
    }

    public int getCenterY() {
        return this.centerY;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getColorAccent() {
        return this.colorAccent;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.centerX);
        dest.writeInt(this.centerY);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeInt(this.colorAccent);
    }
}
