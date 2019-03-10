package android.support.wearable.watchface.accessibility;

import android.content.Context;
import android.support.wearable.C0395R;
import android.support.wearable.complications.ComplicationData;
import android.support.wearable.complications.ComplicationText;
import android.support.wearable.complications.ComplicationText.TimeFormatBuilder;
import android.support.wearable.complications.ComplicationTextTemplate.Builder;
import android.support.wearable.complications.TimeDependentText;
import android.text.format.DateFormat;

public class AccessibilityUtils {
    public static ComplicationText makeTimeAsComplicationText(Context context) {
        String format;
        if (DateFormat.is24HourFormat(context)) {
            format = "HH:mm";
        } else {
            format = "h:mm a";
        }
        return new TimeFormatBuilder().setFormat(format).build();
    }

    public static TimeDependentText generateContentDescription(Context context, ComplicationData data) {
        ComplicationText text;
        ComplicationText title;
        ComplicationText typeSpecificText;
        ComplicationText desc = data.getContentDescription();
        if (desc != null) {
            if (!desc.isAlwaysEmpty()) {
                return desc;
            }
        }
        ComplicationText imageDescription = data.getImageContentDescription();
        if (data.getType() == 4) {
            text = data.getLongText();
            title = data.getLongTitle();
        } else {
            text = data.getShortText();
            title = data.getShortTitle();
        }
        Builder builder = new Builder();
        boolean isBuilderEmpty = true;
        boolean hasTextOrTitle = false;
        if (imageDescription != null && !imageDescription.isAlwaysEmpty()) {
            builder.addComplicationText(imageDescription);
            isBuilderEmpty = false;
        }
        if (text != null && !text.isAlwaysEmpty()) {
            builder.addComplicationText(text);
            isBuilderEmpty = false;
            hasTextOrTitle = true;
        }
        if (title != null && !title.isAlwaysEmpty()) {
            builder.addComplicationText(title);
            isBuilderEmpty = false;
            hasTextOrTitle = true;
        }
        int type = data.getType();
        if (type != 5) {
            switch (type) {
                case 9:
                    typeSpecificText = ComplicationText.plainText(context.getString(C0395R.string.a11y_no_permission));
                    break;
                case 10:
                    typeSpecificText = ComplicationText.plainText(context.getString(C0395R.string.a11y_no_data));
                    break;
                default:
                    typeSpecificText = null;
                    break;
            }
        } else if (hasTextOrTitle) {
            typeSpecificText = null;
        } else {
            float value = data.getValue();
            float max = data.getMaxValue();
            typeSpecificText = ComplicationText.plainText(context.getString(C0395R.string.a11y_template_range, new Object[]{Float.valueOf(value), Float.valueOf(max)}));
        }
        if (typeSpecificText == null && isBuilderEmpty) {
            return ComplicationText.plainText("");
        }
        if (typeSpecificText != null) {
            if (isBuilderEmpty) {
                return typeSpecificText;
            }
            builder.addComplicationText(typeSpecificText);
        }
        return builder.build();
    }
}
