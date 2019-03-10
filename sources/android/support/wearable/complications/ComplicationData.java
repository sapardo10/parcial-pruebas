package android.support.wearable.complications;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.graphics.drawable.Icon;
import android.os.BadParcelableException;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;

@TargetApi(24)
public class ComplicationData implements Parcelable {
    public static final Creator<ComplicationData> CREATOR = new C04001();
    private static final String FIELD_CONTENT_DESCRIPTION = "CONTENT_DESCRIPTION";
    private static final String FIELD_END_TIME = "END_TIME";
    private static final String FIELD_ICON = "ICON";
    private static final String FIELD_ICON_BURN_IN_PROTECTION = "ICON_BURN_IN_PROTECTION";
    private static final String FIELD_IMAGE_CONTENT_DESCRIPTION = "IMAGE_CONTENT_DESCRIPTION";
    private static final String FIELD_IMAGE_STYLE = "IMAGE_STYLE";
    private static final String FIELD_LARGE_IMAGE = "LARGE_IMAGE";
    private static final String FIELD_LONG_TEXT = "LONG_TEXT";
    private static final String FIELD_LONG_TITLE = "LONG_TITLE";
    private static final String FIELD_MAX_VALUE = "MAX_VALUE";
    private static final String FIELD_MIN_VALUE = "MIN_VALUE";
    private static final String FIELD_SHORT_TEXT = "SHORT_TEXT";
    private static final String FIELD_SHORT_TITLE = "SHORT_TITLE";
    private static final String FIELD_SMALL_IMAGE = "SMALL_IMAGE";
    private static final String FIELD_SMALL_IMAGE_BURN_IN_PROTECTION = "SMALL_IMAGE_BURN_IN_PROTECTION";
    private static final String FIELD_START_TIME = "START_TIME";
    private static final String FIELD_TAP_ACTION = "TAP_ACTION";
    private static final String FIELD_VALUE = "VALUE";
    public static final int IMAGE_STYLE_ICON = 2;
    public static final int IMAGE_STYLE_PHOTO = 1;
    private static final String[][] OPTIONAL_FIELDS;
    private static final String[][] REQUIRED_FIELDS;
    private static final String TAG = "ComplicationData";
    public static final int TYPE_EMPTY = 2;
    public static final int TYPE_ICON = 6;
    public static final int TYPE_LARGE_IMAGE = 8;
    public static final int TYPE_LONG_TEXT = 4;
    public static final int TYPE_NOT_CONFIGURED = 1;
    public static final int TYPE_NO_DATA = 10;
    public static final int TYPE_NO_PERMISSION = 9;
    public static final int TYPE_RANGED_VALUE = 5;
    public static final int TYPE_SHORT_TEXT = 3;
    public static final int TYPE_SMALL_IMAGE = 7;
    private final Bundle mFields;
    private final int mType;

    /* renamed from: android.support.wearable.complications.ComplicationData$1 */
    class C04001 implements Creator<ComplicationData> {
        C04001() {
        }

        public ComplicationData createFromParcel(Parcel source) {
            return new ComplicationData(source);
        }

        public ComplicationData[] newArray(int size) {
            return new ComplicationData[size];
        }
    }

    public static final class Builder {
        private final Bundle mFields;
        private final int mType;

        public Builder(ComplicationData data) {
            this.mType = data.getType();
            this.mFields = (Bundle) data.mFields.clone();
        }

        public Builder(int type) {
            this.mType = type;
            this.mFields = new Bundle();
            if (type != 7) {
                if (type != 4) {
                    return;
                }
            }
            setImageStyle(1);
        }

        public Builder setStartTime(long startTime) {
            this.mFields.putLong(ComplicationData.FIELD_START_TIME, startTime);
            return this;
        }

        public Builder setEndTime(long endTime) {
            this.mFields.putLong(ComplicationData.FIELD_END_TIME, endTime);
            return this;
        }

        public Builder setValue(float value) {
            putFloatField(ComplicationData.FIELD_VALUE, value);
            return this;
        }

        public Builder setMinValue(float minValue) {
            putFloatField(ComplicationData.FIELD_MIN_VALUE, minValue);
            return this;
        }

        public Builder setMaxValue(float maxValue) {
            putFloatField(ComplicationData.FIELD_MAX_VALUE, maxValue);
            return this;
        }

        public Builder setLongTitle(ComplicationText longTitle) {
            putFieldIfNotNull(ComplicationData.FIELD_LONG_TITLE, longTitle);
            return this;
        }

        public Builder setLongText(ComplicationText longText) {
            putFieldIfNotNull(ComplicationData.FIELD_LONG_TEXT, longText);
            return this;
        }

        public Builder setShortTitle(ComplicationText shortTitle) {
            putFieldIfNotNull(ComplicationData.FIELD_SHORT_TITLE, shortTitle);
            return this;
        }

        public Builder setShortText(ComplicationText shortText) {
            putFieldIfNotNull(ComplicationData.FIELD_SHORT_TEXT, shortText);
            return this;
        }

        public Builder setIcon(Icon icon) {
            putFieldIfNotNull(ComplicationData.FIELD_ICON, icon);
            return this;
        }

        public Builder setBurnInProtectionIcon(Icon icon) {
            putFieldIfNotNull(ComplicationData.FIELD_ICON_BURN_IN_PROTECTION, icon);
            return this;
        }

        public Builder setSmallImage(Icon smallImage) {
            putFieldIfNotNull(ComplicationData.FIELD_SMALL_IMAGE, smallImage);
            return this;
        }

        public Builder setBurnInProtectionSmallImage(Icon smallImage) {
            putFieldIfNotNull(ComplicationData.FIELD_SMALL_IMAGE_BURN_IN_PROTECTION, smallImage);
            return this;
        }

        public Builder setImageStyle(int imageStyle) {
            putIntField(ComplicationData.FIELD_IMAGE_STYLE, imageStyle);
            return this;
        }

        public Builder setLargeImage(Icon largeImage) {
            putFieldIfNotNull(ComplicationData.FIELD_LARGE_IMAGE, largeImage);
            return this;
        }

        public Builder setTapAction(PendingIntent pendingIntent) {
            putFieldIfNotNull(ComplicationData.FIELD_TAP_ACTION, pendingIntent);
            return this;
        }

        @Deprecated
        public Builder setContentDescription(ComplicationText description) {
            putFieldIfNotNull(ComplicationData.FIELD_CONTENT_DESCRIPTION, description);
            return this;
        }

        public Builder setImageContentDescription(ComplicationText description) {
            putFieldIfNotNull(ComplicationData.FIELD_IMAGE_CONTENT_DESCRIPTION, description);
            return this;
        }

        public ComplicationData build() {
            String[] strArr = ComplicationData.REQUIRED_FIELDS[this.mType];
            int length = strArr.length;
            int i = 0;
            while (i < length) {
                String requiredField = strArr[i];
                if (this.mFields.containsKey(requiredField)) {
                    if (this.mFields.containsKey(ComplicationData.FIELD_ICON_BURN_IN_PROTECTION)) {
                        if (!this.mFields.containsKey(ComplicationData.FIELD_ICON)) {
                            throw new IllegalStateException("Field ICON must be provided when field ICON_BURN_IN_PROTECTION is provided.");
                        }
                    }
                    if (this.mFields.containsKey(ComplicationData.FIELD_SMALL_IMAGE_BURN_IN_PROTECTION)) {
                        if (!this.mFields.containsKey(ComplicationData.FIELD_SMALL_IMAGE)) {
                            throw new IllegalStateException("Field SMALL_IMAGE must be provided when field SMALL_IMAGE_BURN_IN_PROTECTION is provided.");
                        }
                    }
                    i++;
                } else {
                    length = this.mType;
                    StringBuilder stringBuilder = new StringBuilder(String.valueOf(requiredField).length() + 39);
                    stringBuilder.append("Field ");
                    stringBuilder.append(requiredField);
                    stringBuilder.append(" is required for type ");
                    stringBuilder.append(length);
                    throw new IllegalStateException(stringBuilder.toString());
                }
            }
            return new ComplicationData();
        }

        private void putIntField(String field, int value) {
            ComplicationData.checkFieldValidForTypeThrowingException(field, this.mType);
            this.mFields.putInt(field, value);
        }

        private void putFloatField(String field, float value) {
            ComplicationData.checkFieldValidForTypeThrowingException(field, this.mType);
            this.mFields.putFloat(field, value);
        }

        private void putFieldIfNotNull(String field, Object obj) {
            ComplicationData.checkFieldValidForTypeThrowingException(field, this.mType);
            if (obj == null) {
                this.mFields.remove(field);
                return;
            }
            if (obj instanceof String) {
                this.mFields.putString(field, (String) obj);
            } else if (obj instanceof Parcelable) {
                this.mFields.putParcelable(field, (Parcelable) obj);
            } else {
                String valueOf = String.valueOf(obj.getClass());
                StringBuilder stringBuilder = new StringBuilder(String.valueOf(valueOf).length() + 24);
                stringBuilder.append("Unexpected object type: ");
                stringBuilder.append(valueOf);
                throw new IllegalArgumentException(stringBuilder.toString());
            }
        }
    }

    static {
        r1 = new String[11][];
        r1[3] = new String[]{FIELD_SHORT_TEXT};
        r1[4] = new String[]{FIELD_LONG_TEXT};
        r1[5] = new String[]{FIELD_VALUE, FIELD_MIN_VALUE, FIELD_MAX_VALUE};
        r1[6] = new String[]{FIELD_ICON};
        r1[7] = new String[]{FIELD_SMALL_IMAGE, FIELD_IMAGE_STYLE};
        r1[8] = new String[]{FIELD_LARGE_IMAGE};
        r1[9] = new String[0];
        r1[10] = new String[0];
        REQUIRED_FIELDS = r1;
        r0 = new String[11][];
        r0[3] = new String[]{FIELD_SHORT_TITLE, FIELD_ICON, FIELD_ICON_BURN_IN_PROTECTION, FIELD_TAP_ACTION, FIELD_CONTENT_DESCRIPTION, FIELD_IMAGE_CONTENT_DESCRIPTION};
        r0[4] = new String[]{FIELD_LONG_TITLE, FIELD_ICON, FIELD_ICON_BURN_IN_PROTECTION, FIELD_SMALL_IMAGE, FIELD_SMALL_IMAGE_BURN_IN_PROTECTION, FIELD_IMAGE_STYLE, FIELD_TAP_ACTION, FIELD_CONTENT_DESCRIPTION, FIELD_IMAGE_CONTENT_DESCRIPTION};
        r0[5] = new String[]{FIELD_SHORT_TEXT, FIELD_SHORT_TITLE, FIELD_ICON, FIELD_ICON_BURN_IN_PROTECTION, FIELD_TAP_ACTION, FIELD_CONTENT_DESCRIPTION, FIELD_IMAGE_CONTENT_DESCRIPTION};
        r0[6] = new String[]{FIELD_TAP_ACTION, FIELD_ICON_BURN_IN_PROTECTION, FIELD_CONTENT_DESCRIPTION, FIELD_IMAGE_CONTENT_DESCRIPTION};
        r0[7] = new String[]{FIELD_TAP_ACTION, FIELD_SMALL_IMAGE_BURN_IN_PROTECTION, FIELD_CONTENT_DESCRIPTION, FIELD_IMAGE_CONTENT_DESCRIPTION};
        r0[8] = new String[]{FIELD_TAP_ACTION, FIELD_CONTENT_DESCRIPTION, FIELD_IMAGE_CONTENT_DESCRIPTION};
        r0[9] = new String[]{FIELD_SHORT_TEXT, FIELD_SHORT_TITLE, FIELD_ICON, FIELD_ICON_BURN_IN_PROTECTION, FIELD_CONTENT_DESCRIPTION, FIELD_IMAGE_CONTENT_DESCRIPTION};
        r0[10] = new String[0];
        OPTIONAL_FIELDS = r0;
    }

    private ComplicationData(Builder builder) {
        this.mType = builder.mType;
        this.mFields = builder.mFields;
    }

    private ComplicationData(Parcel in) {
        this.mType = in.readInt();
        this.mFields = in.readBundle(getClass().getClassLoader());
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mType);
        dest.writeBundle(this.mFields);
    }

    public int getType() {
        return this.mType;
    }

    public boolean isActive(long dateTimeMillis) {
        if (dateTimeMillis >= this.mFields.getLong(FIELD_START_TIME, 0)) {
            if (dateTimeMillis <= this.mFields.getLong(FIELD_END_TIME, Long.MAX_VALUE)) {
                return true;
            }
        }
        return false;
    }

    public float getValue() {
        checkFieldValidForTypeWithoutThrowingException(FIELD_VALUE, this.mType);
        return this.mFields.getFloat(FIELD_VALUE);
    }

    public float getMinValue() {
        checkFieldValidForTypeWithoutThrowingException(FIELD_MIN_VALUE, this.mType);
        return this.mFields.getFloat(FIELD_MIN_VALUE);
    }

    public float getMaxValue() {
        checkFieldValidForTypeWithoutThrowingException(FIELD_MAX_VALUE, this.mType);
        return this.mFields.getFloat(FIELD_MAX_VALUE);
    }

    public ComplicationText getShortTitle() {
        checkFieldValidForTypeWithoutThrowingException(FIELD_SHORT_TITLE, this.mType);
        return (ComplicationText) getParcelableField(FIELD_SHORT_TITLE);
    }

    public ComplicationText getShortText() {
        checkFieldValidForTypeWithoutThrowingException(FIELD_SHORT_TEXT, this.mType);
        return (ComplicationText) getParcelableField(FIELD_SHORT_TEXT);
    }

    public ComplicationText getLongTitle() {
        checkFieldValidForTypeWithoutThrowingException(FIELD_LONG_TITLE, this.mType);
        return (ComplicationText) getParcelableField(FIELD_LONG_TITLE);
    }

    public ComplicationText getLongText() {
        checkFieldValidForTypeWithoutThrowingException(FIELD_LONG_TEXT, this.mType);
        return (ComplicationText) getParcelableField(FIELD_LONG_TEXT);
    }

    public Icon getIcon() {
        checkFieldValidForTypeWithoutThrowingException(FIELD_ICON, this.mType);
        return (Icon) getParcelableField(FIELD_ICON);
    }

    public Icon getBurnInProtectionIcon() {
        checkFieldValidForTypeWithoutThrowingException(FIELD_ICON_BURN_IN_PROTECTION, this.mType);
        return (Icon) getParcelableField(FIELD_ICON_BURN_IN_PROTECTION);
    }

    public Icon getSmallImage() {
        checkFieldValidForTypeWithoutThrowingException(FIELD_SMALL_IMAGE, this.mType);
        return (Icon) getParcelableField(FIELD_SMALL_IMAGE);
    }

    public Icon getBurnInProtectionSmallImage() {
        checkFieldValidForTypeWithoutThrowingException(FIELD_SMALL_IMAGE_BURN_IN_PROTECTION, this.mType);
        return (Icon) getParcelableField(FIELD_SMALL_IMAGE_BURN_IN_PROTECTION);
    }

    public int getImageStyle() {
        checkFieldValidForTypeWithoutThrowingException(FIELD_IMAGE_STYLE, this.mType);
        return this.mFields.getInt(FIELD_IMAGE_STYLE);
    }

    public Icon getLargeImage() {
        checkFieldValidForTypeWithoutThrowingException(FIELD_LARGE_IMAGE, this.mType);
        return (Icon) getParcelableField(FIELD_LARGE_IMAGE);
    }

    public PendingIntent getTapAction() {
        checkFieldValidForTypeWithoutThrowingException(FIELD_TAP_ACTION, this.mType);
        return (PendingIntent) getParcelableField(FIELD_TAP_ACTION);
    }

    public ComplicationText getContentDescription() {
        checkFieldValidForTypeWithoutThrowingException(FIELD_CONTENT_DESCRIPTION, this.mType);
        return (ComplicationText) getParcelableField(FIELD_CONTENT_DESCRIPTION);
    }

    public ComplicationText getImageContentDescription() {
        checkFieldValidForTypeWithoutThrowingException(FIELD_IMAGE_CONTENT_DESCRIPTION, this.mType);
        return (ComplicationText) getParcelableField(FIELD_IMAGE_CONTENT_DESCRIPTION);
    }

    public boolean isTimeDependent() {
        if (!isTimeDependentField(FIELD_SHORT_TEXT)) {
            if (!isTimeDependentField(FIELD_SHORT_TITLE)) {
                if (!isTimeDependentField(FIELD_LONG_TEXT)) {
                    if (!isTimeDependentField(FIELD_LONG_TITLE)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean isTimeDependentField(String field) {
        ComplicationText text = (ComplicationText) getParcelableField(field);
        return text != null && text.isTimeDependent();
    }

    private static boolean isFieldValidForType(String field, int type) {
        for (String requiredField : REQUIRED_FIELDS[type]) {
            if (requiredField.equals(field)) {
                return true;
            }
        }
        for (String requiredField2 : OPTIONAL_FIELDS[type]) {
            if (requiredField2.equals(field)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isTypeSupported(int type) {
        return 1 <= type && type <= REQUIRED_FIELDS.length;
    }

    private static void checkFieldValidForTypeWithoutThrowingException(String field, int type) {
        if (isTypeSupported(type)) {
            if (!isFieldValidForType(field, type)) {
                if (Log.isLoggable(TAG, 3)) {
                    String str = TAG;
                    StringBuilder stringBuilder = new StringBuilder(String.valueOf(field).length() + 44);
                    stringBuilder.append("Field ");
                    stringBuilder.append(field);
                    stringBuilder.append(" is not supported for type ");
                    stringBuilder.append(type);
                    Log.d(str, stringBuilder.toString());
                }
            }
            return;
        }
        str = TAG;
        stringBuilder = new StringBuilder(38);
        stringBuilder.append("Type ");
        stringBuilder.append(type);
        stringBuilder.append(" can not be recognized");
        Log.w(str, stringBuilder.toString());
    }

    private static void checkFieldValidForTypeThrowingException(String field, int type) {
        StringBuilder stringBuilder;
        if (!isTypeSupported(type)) {
            stringBuilder = new StringBuilder(38);
            stringBuilder.append("Type ");
            stringBuilder.append(type);
            stringBuilder.append(" can not be recognized");
            throw new IllegalStateException(stringBuilder.toString());
        } else if (!isFieldValidForType(field, type)) {
            stringBuilder = new StringBuilder(String.valueOf(field).length() + 44);
            stringBuilder.append("Field ");
            stringBuilder.append(field);
            stringBuilder.append(" is not supported for type ");
            stringBuilder.append(type);
            throw new IllegalStateException(stringBuilder.toString());
        }
    }

    private <T extends Parcelable> T getParcelableField(String field) {
        try {
            return this.mFields.getParcelable(field);
        } catch (BadParcelableException e) {
            Log.w(TAG, "Could not unparcel ComplicationData. Provider apps must exclude wearable support complication classes from proguard.", e);
            return null;
        }
    }

    public String toString() {
        int i = this.mType;
        String valueOf = String.valueOf(this.mFields);
        StringBuilder stringBuilder = new StringBuilder(String.valueOf(valueOf).length() + 45);
        stringBuilder.append("ComplicationData{mType=");
        stringBuilder.append(i);
        stringBuilder.append(", mFields=");
        stringBuilder.append(valueOf);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
