package android.support.wearable.complications;

import android.annotation.TargetApi;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

@TargetApi(24)
public class ComplicationProviderInfo implements Parcelable {
    public static final Creator<ComplicationProviderInfo> CREATOR = new C04011();
    private static final String KEY_APP_NAME = "app_name";
    private static final String KEY_PROVIDER_ICON = "provider_icon";
    private static final String KEY_PROVIDER_NAME = "provider_name";
    private static final String KEY_PROVIDER_TYPE = "complication_type";
    public final String appName;
    public final int complicationType;
    public final Icon providerIcon;
    public final String providerName;

    /* renamed from: android.support.wearable.complications.ComplicationProviderInfo$1 */
    class C04011 implements Creator<ComplicationProviderInfo> {
        C04011() {
        }

        public ComplicationProviderInfo createFromParcel(Parcel source) {
            return new ComplicationProviderInfo(source);
        }

        public ComplicationProviderInfo[] newArray(int size) {
            return new ComplicationProviderInfo[size];
        }
    }

    public ComplicationProviderInfo(String appName, String providerName, Icon providerIcon, int complicationType) {
        this.appName = appName;
        this.providerName = providerName;
        this.providerIcon = providerIcon;
        this.complicationType = complicationType;
    }

    public ComplicationProviderInfo(Parcel in) {
        Bundle bundle = in.readBundle(getClass().getClassLoader());
        this.appName = bundle.getString(KEY_APP_NAME);
        this.providerName = bundle.getString(KEY_PROVIDER_NAME);
        this.providerIcon = (Icon) bundle.getParcelable(KEY_PROVIDER_ICON);
        this.complicationType = bundle.getInt(KEY_PROVIDER_TYPE);
    }

    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_APP_NAME, this.appName);
        bundle.putString(KEY_PROVIDER_NAME, this.providerName);
        bundle.putParcelable(KEY_PROVIDER_ICON, this.providerIcon);
        bundle.putInt(KEY_PROVIDER_TYPE, this.complicationType);
        dest.writeBundle(bundle);
    }

    public int describeContents() {
        return 0;
    }

    public String toString() {
        String str = this.appName;
        String str2 = this.providerName;
        String valueOf = String.valueOf(this.providerIcon);
        int i = this.complicationType;
        StringBuilder stringBuilder = new StringBuilder(((String.valueOf(str).length() + 98) + String.valueOf(str2).length()) + String.valueOf(valueOf).length());
        stringBuilder.append("ComplicationProviderInfo{appName='");
        stringBuilder.append(str);
        stringBuilder.append('\'');
        stringBuilder.append(", providerName='");
        stringBuilder.append(str2);
        stringBuilder.append('\'');
        stringBuilder.append(", providerIcon=");
        stringBuilder.append(valueOf);
        stringBuilder.append(", complicationType=");
        stringBuilder.append(i);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
