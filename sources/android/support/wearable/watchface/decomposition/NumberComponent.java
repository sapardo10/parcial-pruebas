package android.support.wearable.watchface.decomposition;

import android.annotation.SuppressLint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.wearable.watchface.decomposition.WatchFaceDecomposition.DrawnComponent;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class NumberComponent implements Parcelable, DrawnComponent {
    public static final Creator<NumberComponent> CREATOR = new C04811();
    private static final String FIELD_COMPONENT_ID = "component_id";
    private static final String FIELD_FONT_COMPONENT_ID = "font_component_id";
    private static final String FIELD_HIGHEST_VALUE = "highest_value";
    private static final String FIELD_LOWEST_VALUE = "lowest_value";
    private static final String FIELD_MIN_DIGITS_SHOWN = "leading_zeroes";
    private static final String FIELD_MS_PER_INCREMENT = "ms_per_increment";
    private static final String FIELD_POSITION = "position";
    private static final String FIELD_TIME_OFFSET_MS = "time_offset_ms";
    private static final String FIELD_Z_ORDER = "zOrder";
    private final Bundle fields;

    /* renamed from: android.support.wearable.watchface.decomposition.NumberComponent$1 */
    class C04811 implements Creator<NumberComponent> {
        C04811() {
        }

        public NumberComponent createFromParcel(Parcel source) {
            return new NumberComponent(source);
        }

        public NumberComponent[] newArray(int size) {
            return new NumberComponent[size];
        }
    }

    public static class Builder {
        public static final int HOURS_12 = 3;
        public static final int HOURS_24 = 4;
        public static final int MINUTES = 2;
        public static final int SECONDS = 1;
        private final Bundle fields = new Bundle();

        public Builder(int preset) {
            switch (preset) {
                case 1:
                    setMsPerIncrement(TimeUnit.SECONDS.toMillis(1));
                    setLowestValue(0);
                    setHighestValue(59);
                    return;
                case 2:
                    setMsPerIncrement(TimeUnit.MINUTES.toMillis(1));
                    setLowestValue(0);
                    setHighestValue(59);
                    return;
                case 3:
                    setMsPerIncrement(TimeUnit.HOURS.toMillis(1));
                    setLowestValue(1);
                    setHighestValue(12);
                    return;
                case 4:
                    setMsPerIncrement(TimeUnit.HOURS.toMillis(1));
                    setLowestValue(0);
                    setHighestValue(23);
                    return;
                default:
                    throw new IllegalArgumentException("preset type not recognised");
            }
        }

        public Builder setComponentId(int componentId) {
            this.fields.putInt(NumberComponent.FIELD_COMPONENT_ID, componentId);
            return this;
        }

        public Builder setMsPerIncrement(long timeUnit) {
            this.fields.putLong(NumberComponent.FIELD_MS_PER_INCREMENT, timeUnit);
            return this;
        }

        public Builder setLowestValue(long lowestValue) {
            this.fields.putLong(NumberComponent.FIELD_LOWEST_VALUE, lowestValue);
            return this;
        }

        public Builder setHighestValue(long highestValue) {
            this.fields.putLong(NumberComponent.FIELD_HIGHEST_VALUE, highestValue);
            return this;
        }

        public Builder setTimeOffsetMs(long offset) {
            this.fields.putLong(NumberComponent.FIELD_TIME_OFFSET_MS, offset);
            return this;
        }

        public Builder setMinDigitsShown(int minDigitsShown) {
            this.fields.putInt(NumberComponent.FIELD_MIN_DIGITS_SHOWN, minDigitsShown);
            return this;
        }

        public Builder setFontComponentId(int fontComponentId) {
            this.fields.putInt(NumberComponent.FIELD_FONT_COMPONENT_ID, fontComponentId);
            return this;
        }

        public Builder setZOrder(int zOrder) {
            this.fields.putInt(NumberComponent.FIELD_Z_ORDER, zOrder);
            return this;
        }

        @SuppressLint({"NewApi"})
        public Builder setPosition(PointF position) {
            this.fields.putParcelable("position", position);
            return this;
        }

        public NumberComponent build() {
            if (!this.fields.containsKey(NumberComponent.FIELD_COMPONENT_ID)) {
                throw new IllegalStateException("Component id must be provided");
            } else if (!this.fields.containsKey(NumberComponent.FIELD_MS_PER_INCREMENT)) {
                throw new IllegalStateException("Ms per increment must be specified");
            } else if (this.fields.containsKey(NumberComponent.FIELD_HIGHEST_VALUE)) {
                if (!this.fields.containsKey(NumberComponent.FIELD_MIN_DIGITS_SHOWN)) {
                    Bundle bundle = this.fields;
                    bundle.putInt(NumberComponent.FIELD_MIN_DIGITS_SHOWN, ((int) Math.log10((double) bundle.getLong(NumberComponent.FIELD_HIGHEST_VALUE))) + 1);
                }
                return new NumberComponent(this.fields);
            } else {
                throw new IllegalStateException("Highest value must be specified");
            }
        }
    }

    private NumberComponent(Bundle fields) {
        this.fields = fields;
    }

    private NumberComponent(Parcel in) {
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

    public long getMsPerIncrement() {
        return this.fields.getLong(FIELD_MS_PER_INCREMENT);
    }

    public long getLowestValue() {
        return this.fields.getLong(FIELD_LOWEST_VALUE);
    }

    public long getHighestValue() {
        return this.fields.getLong(FIELD_HIGHEST_VALUE);
    }

    public long getTimeOffsetMs() {
        return this.fields.getLong(FIELD_TIME_OFFSET_MS);
    }

    public int getMinDigitsShown() {
        return this.fields.getInt(FIELD_MIN_DIGITS_SHOWN);
    }

    public int getFontComponentId() {
        return this.fields.getInt(FIELD_FONT_COMPONENT_ID);
    }

    public int getZOrder() {
        return this.fields.getInt(FIELD_Z_ORDER);
    }

    public PointF getPosition() {
        PointF position = (PointF) this.fields.getParcelable("position");
        if (position == null) {
            return null;
        }
        return new PointF(position.x, position.y);
    }

    public String getDisplayStringForTime(long timeMillis) {
        timeMillis += (long) TimeZone.getDefault().getOffset(timeMillis);
        long lowest = getLowestValue();
        long displayNum = ((((getTimeOffsetMs() + timeMillis) / getMsPerIncrement()) - lowest) % ((getHighestValue() - lowest) + 1)) + lowest;
        int minDigits = getMinDigitsShown();
        if (minDigits <= 0) {
            return Long.toString(displayNum);
        }
        Locale locale = Locale.US;
        StringBuilder stringBuilder = new StringBuilder(14);
        stringBuilder.append("%0");
        stringBuilder.append(minDigits);
        stringBuilder.append("d");
        return String.format(locale, stringBuilder.toString(), new Object[]{Long.valueOf(displayNum)});
    }
}
