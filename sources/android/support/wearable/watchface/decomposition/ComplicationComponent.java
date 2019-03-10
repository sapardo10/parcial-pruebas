package android.support.wearable.watchface.decomposition;

import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import android.support.wearable.complications.rendering.ComplicationDrawable;
import android.support.wearable.watchface.decomposition.WatchFaceDecomposition.DrawnComponent;
import java.util.Arrays;

public class ComplicationComponent implements Parcelable, DrawnComponent {
    public static final Creator<ComplicationComponent> CREATOR = new C04781();
    private static final String FIELD_BOUNDS = "bounds";
    private static final String FIELD_COMPLICATION_DRAWABLE = "complication_drawable";
    private static final String FIELD_COMPLICATION_TYPES = "complication_types";
    private static final String FIELD_COMPONENT_ID = "component_id";
    private static final String FIELD_WATCH_FACE_COMPLICATION_ID = "wf_complication_id";
    private static final String FIELD_Z_ORDER = "zOrder";
    private final Bundle fields;

    /* renamed from: android.support.wearable.watchface.decomposition.ComplicationComponent$1 */
    class C04781 implements Creator<ComplicationComponent> {
        C04781() {
        }

        public ComplicationComponent createFromParcel(Parcel source) {
            return new ComplicationComponent(source);
        }

        public ComplicationComponent[] newArray(int size) {
            return new ComplicationComponent[size];
        }
    }

    public static class Builder {
        private final Bundle fields = new Bundle();

        public Builder setComponentId(int componentId) {
            this.fields.putInt(ComplicationComponent.FIELD_COMPONENT_ID, componentId);
            return this;
        }

        public Builder setBounds(RectF bounds) {
            this.fields.putParcelable(ComplicationComponent.FIELD_BOUNDS, new RectF(bounds));
            return this;
        }

        public Builder setZOrder(int zOrder) {
            this.fields.putInt(ComplicationComponent.FIELD_Z_ORDER, zOrder);
            return this;
        }

        public Builder setComplicationDrawable(ComplicationDrawable drawable) {
            this.fields.putParcelable(ComplicationComponent.FIELD_COMPLICATION_DRAWABLE, drawable);
            return this;
        }

        public Builder setWatchFaceComplicationId(int wfComplicationId) {
            this.fields.putInt(ComplicationComponent.FIELD_WATCH_FACE_COMPLICATION_ID, wfComplicationId);
            return this;
        }

        public Builder setComplicationTypes(int... types) {
            this.fields.putIntArray(ComplicationComponent.FIELD_COMPLICATION_TYPES, types);
            return this;
        }

        public ComplicationComponent build() {
            if (!this.fields.containsKey(ComplicationComponent.FIELD_COMPONENT_ID)) {
                throw new IllegalStateException("Component id must be provided");
            } else if (this.fields.containsKey(ComplicationComponent.FIELD_WATCH_FACE_COMPLICATION_ID)) {
                if (!this.fields.containsKey(ComplicationComponent.FIELD_COMPLICATION_TYPES)) {
                    this.fields.putIntArray(ComplicationComponent.FIELD_COMPLICATION_TYPES, new int[]{5, 3, 7, 6});
                }
                return new ComplicationComponent(this.fields);
            } else {
                throw new IllegalStateException("Watch face complication id must be provided");
            }
        }
    }

    private ComplicationComponent(Bundle fields) {
        this.fields = fields;
    }

    private ComplicationComponent(Parcel in) {
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

    public RectF getBounds() {
        return new RectF((RectF) this.fields.getParcelable(FIELD_BOUNDS));
    }

    public int getZOrder() {
        return this.fields.getInt(FIELD_Z_ORDER);
    }

    public ComplicationDrawable getComplicationDrawable() {
        return (ComplicationDrawable) this.fields.getParcelable(FIELD_COMPLICATION_DRAWABLE);
    }

    public int getWatchFaceComplicationId() {
        return this.fields.getInt(FIELD_WATCH_FACE_COMPLICATION_ID);
    }

    @Nullable
    public int[] getComplicationTypes() {
        int[] types = this.fields.getIntArray(FIELD_COMPLICATION_TYPES);
        if (types == null) {
            return null;
        }
        return Arrays.copyOf(types, types.length);
    }
}
