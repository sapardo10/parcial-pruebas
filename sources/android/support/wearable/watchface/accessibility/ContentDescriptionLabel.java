package android.support.wearable.watchface.accessibility;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.wearable.complications.ComplicationData;
import android.support.wearable.complications.ComplicationText;
import android.support.wearable.complications.ComplicationTextTemplate;
import android.support.wearable.complications.TimeDependentText;
import java.util.Objects;

@SuppressLint({"NewApi"})
public class ContentDescriptionLabel implements Parcelable {
    public static final Creator<ContentDescriptionLabel> CREATOR = new C04771();
    private static final String KEY_BOUNDS = "KEY_BOUNDS";
    private static final String KEY_TAP_ACTION = "KEY_TAP_ACTION";
    private static final String KEY_TEXT = "KEY_TEXT";
    private final Rect bounds;
    private PendingIntent tapAction;
    private final TimeDependentText text;

    /* renamed from: android.support.wearable.watchface.accessibility.ContentDescriptionLabel$1 */
    class C04771 implements Creator<ContentDescriptionLabel> {
        C04771() {
        }

        public ContentDescriptionLabel createFromParcel(Parcel in) {
            return new ContentDescriptionLabel(in);
        }

        public ContentDescriptionLabel[] newArray(int size) {
            return new ContentDescriptionLabel[size];
        }
    }

    public ContentDescriptionLabel(@NonNull Rect bounds, @NonNull ComplicationTextTemplate text) {
        this(bounds, (TimeDependentText) text);
    }

    public ContentDescriptionLabel(@NonNull Rect bounds, @NonNull ComplicationText text) {
        this(bounds, (TimeDependentText) text);
    }

    public ContentDescriptionLabel(@NonNull Context context, @NonNull Rect bounds, @NonNull ComplicationData data) {
        this(bounds, AccessibilityUtils.generateContentDescription(context, data));
    }

    private ContentDescriptionLabel(@NonNull Rect bounds, @NonNull TimeDependentText text) {
        this.bounds = bounds;
        this.text = text;
    }

    protected ContentDescriptionLabel(Parcel in) {
        Bundle bundle = in.readBundle(getClass().getClassLoader());
        this.text = (TimeDependentText) bundle.getParcelable(KEY_TEXT);
        this.bounds = (Rect) bundle.getParcelable(KEY_BOUNDS);
        this.tapAction = (PendingIntent) bundle.getParcelable(KEY_TAP_ACTION);
    }

    public Rect getBounds() {
        return this.bounds;
    }

    public TimeDependentText getText() {
        return this.text;
    }

    public PendingIntent getTapAction() {
        return this.tapAction;
    }

    public void setTapAction(PendingIntent tapAction) {
        this.tapAction = tapAction;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_TEXT, this.text);
        bundle.putParcelable(KEY_BOUNDS, this.bounds);
        bundle.putParcelable(KEY_TAP_ACTION, this.tapAction);
        dest.writeBundle(bundle);
    }

    public String toString() {
        String valueOf = String.valueOf(this.text);
        String valueOf2 = String.valueOf(this.bounds);
        String valueOf3 = String.valueOf(this.tapAction);
        StringBuilder stringBuilder = new StringBuilder(((String.valueOf(valueOf).length() + 51) + String.valueOf(valueOf2).length()) + String.valueOf(valueOf3).length());
        stringBuilder.append("ContentDescriptionLabel{text=");
        stringBuilder.append(valueOf);
        stringBuilder.append(", bounds=");
        stringBuilder.append(valueOf2);
        stringBuilder.append(", tapAction=");
        stringBuilder.append(valueOf3);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    public boolean equals(Object o) {
        boolean z = true;
        if (this == o) {
            return true;
        }
        if (o != null) {
            if (getClass() == o.getClass()) {
                ContentDescriptionLabel that = (ContentDescriptionLabel) o;
                if (Objects.equals(this.text, that.text)) {
                    if (Objects.equals(this.bounds, that.bounds)) {
                        if (Objects.equals(this.tapAction, that.tapAction)) {
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
        return Objects.hash(new Object[]{this.text, this.bounds, this.tapAction});
    }
}
