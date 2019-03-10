package android.support.wearable.watchface.decomposition;

import android.annotation.SuppressLint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import android.support.wearable.watchface.decomposition.WatchFaceDecomposition.DrawnComponent;

public class ImageComponent implements Parcelable, DrawnComponent {
    public static final Creator<ImageComponent> CREATOR = new C04801();
    private static final String FIELD_BOUNDS = "bounds";
    private static final String FIELD_COMPONENT_ID = "component_id";
    private static final String FIELD_DEGREES_PER_DAY = "degreesPerDay";
    private static final String FIELD_IMAGE = "image";
    private static final String FIELD_OFFSET_DEGREES = "offsetDegrees";
    private static final String FIELD_PIVOT = "pivot";
    private static final String FIELD_TIME_STEP_MS = "timeStepMs";
    private static final String FIELD_Z_ORDER = "zOrder";
    private final Bundle fields;

    /* renamed from: android.support.wearable.watchface.decomposition.ImageComponent$1 */
    class C04801 implements Creator<ImageComponent> {
        C04801() {
        }

        public ImageComponent createFromParcel(Parcel source) {
            return new ImageComponent(source);
        }

        public ImageComponent[] newArray(int size) {
            return new ImageComponent[size];
        }
    }

    public static class Builder {
        private final Bundle fields = new Bundle();

        public Builder setComponentId(int componentId) {
            this.fields.putInt(ImageComponent.FIELD_COMPONENT_ID, componentId);
            return this;
        }

        @SuppressLint({"NewApi"})
        public Builder setImage(Icon image) {
            this.fields.putParcelable("image", image);
            return this;
        }

        public Builder setBounds(RectF bounds) {
            this.fields.putParcelable(ImageComponent.FIELD_BOUNDS, new RectF(bounds));
            return this;
        }

        public Builder setZOrder(int zOrder) {
            this.fields.putInt(ImageComponent.FIELD_Z_ORDER, zOrder);
            return this;
        }

        public Builder setDegreesPerDay(float degreesPerDay) {
            this.fields.putFloat(ImageComponent.FIELD_DEGREES_PER_DAY, degreesPerDay);
            return this;
        }

        @SuppressLint({"NewApi"})
        public Builder setPivot(PointF pivot) {
            this.fields.putParcelable(ImageComponent.FIELD_PIVOT, new PointF(pivot.x, pivot.y));
            return this;
        }

        public Builder setOffsetDegrees(float offset) {
            this.fields.putFloat(ImageComponent.FIELD_OFFSET_DEGREES, offset);
            return this;
        }

        public Builder setTimeStepMs(long timeStep) {
            this.fields.putLong(ImageComponent.FIELD_TIME_STEP_MS, timeStep);
            return this;
        }

        public ImageComponent build() {
            if (!this.fields.containsKey(ImageComponent.FIELD_COMPONENT_ID)) {
                throw new IllegalStateException("Component id must be provided");
            } else if (this.fields.getParcelable("image") != null) {
                if (!this.fields.containsKey(ImageComponent.FIELD_BOUNDS)) {
                    this.fields.putParcelable(ImageComponent.FIELD_BOUNDS, new RectF(0.0f, 0.0f, 1.0f, 1.0f));
                }
                return new ImageComponent(this.fields);
            } else {
                throw new IllegalStateException("Image must be provided");
            }
        }
    }

    private ImageComponent(Bundle fields) {
        this.fields = fields;
    }

    private ImageComponent(Parcel in) {
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

    public RectF getBounds() {
        return new RectF((RectF) this.fields.getParcelable(FIELD_BOUNDS));
    }

    public int getZOrder() {
        return this.fields.getInt(FIELD_Z_ORDER);
    }

    public float getDegreesPerDay() {
        return this.fields.getFloat(FIELD_DEGREES_PER_DAY);
    }

    @Nullable
    public PointF getPivot() {
        PointF pivot = (PointF) this.fields.getParcelable(FIELD_PIVOT);
        if (pivot == null) {
            return null;
        }
        return new PointF(pivot.x, pivot.y);
    }

    public float getOffsetDegrees() {
        return this.fields.getFloat(FIELD_OFFSET_DEGREES);
    }

    public long getTimeStepMs() {
        return this.fields.getLong(FIELD_TIME_STEP_MS);
    }
}
