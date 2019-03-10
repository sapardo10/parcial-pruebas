package android.support.wearable.watchface.decomposition;

import android.annotation.SuppressLint;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class FontComponent implements Parcelable, Component {
    public static final Creator<FontComponent> CREATOR = new C04791();
    private static final String FIELD_COMPONENT_ID = "component_id";
    private static final String FIELD_DIGIT_COUNT = "digit_count";
    private static final String FIELD_IMAGE = "image";
    private final Bundle fields;

    /* renamed from: android.support.wearable.watchface.decomposition.FontComponent$1 */
    class C04791 implements Creator<FontComponent> {
        C04791() {
        }

        public FontComponent createFromParcel(Parcel source) {
            return new FontComponent(source);
        }

        public FontComponent[] newArray(int size) {
            return new FontComponent[size];
        }
    }

    public static class Builder {
        private final Bundle fields = new Bundle();

        public Builder setComponentId(int componentId) {
            this.fields.putInt(FontComponent.FIELD_COMPONENT_ID, componentId);
            return this;
        }

        @SuppressLint({"NewApi"})
        public Builder setImage(Icon image) {
            this.fields.putParcelable("image", image);
            return this;
        }

        public Builder setDigitCount(int digitCount) {
            this.fields.putInt(FontComponent.FIELD_DIGIT_COUNT, digitCount);
            return this;
        }

        public FontComponent build() {
            if (!this.fields.containsKey(FontComponent.FIELD_COMPONENT_ID)) {
                throw new IllegalStateException("Component id must be provided");
            } else if (this.fields.getParcelable("image") != null) {
                return new FontComponent(this.fields);
            } else {
                throw new IllegalStateException("Image must be provided");
            }
        }
    }

    private FontComponent(Bundle fields) {
        this.fields = fields;
    }

    private FontComponent(Parcel in) {
        this.fields = in.readBundle(getClass().getClassLoader());
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBundle(this.fields);
    }

    public int getComponentId() {
        return this.fields.getInt(FIELD_COMPONENT_ID);
    }

    public Icon getImage() {
        return (Icon) this.fields.getParcelable("image");
    }

    public int getDigitCount() {
        return this.fields.getInt(FIELD_DIGIT_COUNT);
    }
}
