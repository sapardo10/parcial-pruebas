package android.support.wearable.complications;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@TargetApi(24)
public class ComplicationText implements Parcelable, TimeDependentText {
    public static final Creator<ComplicationText> CREATOR = new C04061();
    public static final int DIFFERENCE_STYLE_SHORT_DUAL_UNIT = 3;
    public static final int DIFFERENCE_STYLE_SHORT_SINGLE_UNIT = 2;
    public static final int DIFFERENCE_STYLE_SHORT_WORDS_SINGLE_UNIT = 5;
    public static final int DIFFERENCE_STYLE_STOPWATCH = 1;
    public static final int DIFFERENCE_STYLE_WORDS_SINGLE_UNIT = 4;
    public static final int FORMAT_STYLE_DEFAULT = 1;
    public static final int FORMAT_STYLE_LOWER_CASE = 3;
    public static final int FORMAT_STYLE_UPPER_CASE = 2;
    private static final String KEY_DIFFERENCE_MINIMUM_UNIT = "minimum_unit";
    private static final String KEY_DIFFERENCE_PERIOD_END = "difference_period_end";
    private static final String KEY_DIFFERENCE_PERIOD_START = "difference_period_start";
    private static final String KEY_DIFFERENCE_SHOW_NOW_TEXT = "show_now_text";
    private static final String KEY_DIFFERENCE_STYLE = "difference_style";
    private static final String KEY_FORMAT_FORMAT_STRING = "format_format_string";
    private static final String KEY_FORMAT_STYLE = "format_style";
    private static final String KEY_FORMAT_TIME_ZONE = "format_time_zone";
    private static final String KEY_SURROUNDING_STRING = "surrounding_string";
    private CharSequence mDependentTextCache;
    private long mDependentTextCacheTime;
    private final CharSequence mSurroundingText;
    private final CharSequence[] mTemplateValues;
    private final TimeDependentText mTimeDependentText;

    /* renamed from: android.support.wearable.complications.ComplicationText$1 */
    class C04061 implements Creator<ComplicationText> {
        C04061() {
        }

        public ComplicationText createFromParcel(Parcel in) {
            return new ComplicationText(in);
        }

        public ComplicationText[] newArray(int size) {
            return new ComplicationText[size];
        }
    }

    public static final class TimeDifferenceBuilder {
        private static final long NO_PERIOD_END = Long.MAX_VALUE;
        private static final long NO_PERIOD_START = 0;
        private TimeUnit mMinimumUnit;
        private long mReferencePeriodEnd = Long.MAX_VALUE;
        private long mReferencePeriodStart = 0;
        private Boolean mShowNowText;
        private int mStyle = 3;
        private CharSequence mSurroundingText;

        public TimeDifferenceBuilder setReferencePeriodStart(long refPeriodStart) {
            if (refPeriodStart >= 0) {
                this.mReferencePeriodStart = refPeriodStart;
                return this;
            }
            throw new IllegalArgumentException("Reference period start cannot be negative");
        }

        public TimeDifferenceBuilder setReferencePeriodEnd(long refPeriodEnd) {
            if (refPeriodEnd >= 0) {
                this.mReferencePeriodEnd = refPeriodEnd;
                return this;
            }
            throw new IllegalArgumentException("Reference period end cannot be negative");
        }

        public TimeDifferenceBuilder setStyle(int style) {
            this.mStyle = style;
            return this;
        }

        public TimeDifferenceBuilder setSurroundingText(CharSequence surroundingText) {
            this.mSurroundingText = surroundingText;
            return this;
        }

        public TimeDifferenceBuilder setShowNowText(boolean showNowText) {
            this.mShowNowText = Boolean.valueOf(showNowText);
            return this;
        }

        public TimeDifferenceBuilder setMinimumUnit(@Nullable TimeUnit minimumUnit) {
            this.mMinimumUnit = minimumUnit;
            return this;
        }

        public ComplicationText build() {
            if (this.mReferencePeriodEnd >= this.mReferencePeriodStart) {
                Boolean bool = this.mShowNowText;
                return new ComplicationText(this.mSurroundingText, new TimeDifferenceText(this.mReferencePeriodStart, this.mReferencePeriodEnd, this.mStyle, bool == null ? getDefaultShowNowTextForStyle(this.mStyle) : bool.booleanValue(), this.mMinimumUnit));
            }
            throw new IllegalStateException("Reference period end must not be before start.");
        }

        private static boolean getDefaultShowNowTextForStyle(int style) {
            if (style != 1) {
                return true;
            }
            return false;
        }
    }

    public static final class TimeFormatBuilder {
        private String mFormat;
        private int mStyle = 1;
        private CharSequence mSurroundingText;
        private TimeZone mTimeZone;

        public TimeFormatBuilder setFormat(String format) {
            this.mFormat = format;
            return this;
        }

        public TimeFormatBuilder setStyle(int style) {
            this.mStyle = style;
            return this;
        }

        public TimeFormatBuilder setSurroundingText(CharSequence surroundingText) {
            this.mSurroundingText = surroundingText;
            return this;
        }

        public TimeFormatBuilder setTimeZone(TimeZone timeZone) {
            this.mTimeZone = timeZone;
            return this;
        }

        public ComplicationText build() {
            String str = this.mFormat;
            if (str != null) {
                return new ComplicationText(this.mSurroundingText, new TimeFormatText(str, this.mStyle, this.mTimeZone));
            }
            throw new IllegalStateException("Format must be specified.");
        }
    }

    private ComplicationText(CharSequence surroundingText, TimeDependentText timeDependentText) {
        this.mTemplateValues = new CharSequence[]{"", "^2", "^3", "^4", "^5", "^6", "^7", "^8", "^9"};
        this.mSurroundingText = surroundingText;
        this.mTimeDependentText = timeDependentText;
        checkFields();
    }

    private ComplicationText(Parcel in) {
        this.mTemplateValues = new CharSequence[]{"", "^2", "^3", "^4", "^5", "^6", "^7", "^8", "^9"};
        Bundle bundle = in.readBundle(getClass().getClassLoader());
        this.mSurroundingText = bundle.getCharSequence(KEY_SURROUNDING_STRING);
        if (bundle.containsKey(KEY_DIFFERENCE_STYLE)) {
            if (bundle.containsKey(KEY_DIFFERENCE_PERIOD_START)) {
                if (bundle.containsKey(KEY_DIFFERENCE_PERIOD_END)) {
                    this.mTimeDependentText = new TimeDifferenceText(bundle.getLong(KEY_DIFFERENCE_PERIOD_START), bundle.getLong(KEY_DIFFERENCE_PERIOD_END), bundle.getInt(KEY_DIFFERENCE_STYLE), bundle.getBoolean(KEY_DIFFERENCE_SHOW_NOW_TEXT, true), timeUnitFromName(bundle.getString(KEY_DIFFERENCE_MINIMUM_UNIT)));
                    checkFields();
                }
            }
        }
        if (bundle.containsKey(KEY_FORMAT_FORMAT_STRING)) {
            if (bundle.containsKey(KEY_FORMAT_STYLE)) {
                TimeZone timeZone = null;
                if (bundle.containsKey(KEY_FORMAT_TIME_ZONE)) {
                    timeZone = TimeZone.getTimeZone(bundle.getString(KEY_FORMAT_TIME_ZONE));
                }
                this.mTimeDependentText = new TimeFormatText(bundle.getString(KEY_FORMAT_FORMAT_STRING), bundle.getInt(KEY_FORMAT_STYLE), timeZone);
                checkFields();
            }
        }
        this.mTimeDependentText = null;
        checkFields();
    }

    private static TimeUnit timeUnitFromName(@Nullable String name) {
        TimeUnit timeUnit = null;
        if (name == null) {
            return null;
        }
        try {
            timeUnit = TimeUnit.valueOf(name);
            return timeUnit;
        } catch (IllegalArgumentException e) {
            return timeUnit;
        }
    }

    private void checkFields() {
        if (this.mSurroundingText == null) {
            if (this.mTimeDependentText == null) {
                throw new IllegalStateException("One of mSurroundingText and mTimeDependentText must be non-null");
            }
        }
    }

    public void writeToParcel(Parcel out, int flags) {
        Bundle bundle = new Bundle();
        bundle.putCharSequence(KEY_SURROUNDING_STRING, this.mSurroundingText);
        TimeFormatText timeFormatText = this.mTimeDependentText;
        if (timeFormatText instanceof TimeDifferenceText) {
            TimeDifferenceText timeDiffText = (TimeDifferenceText) timeFormatText;
            bundle.putLong(KEY_DIFFERENCE_PERIOD_START, timeDiffText.getReferencePeriodStart());
            bundle.putLong(KEY_DIFFERENCE_PERIOD_END, timeDiffText.getReferencePeriodEnd());
            bundle.putInt(KEY_DIFFERENCE_STYLE, timeDiffText.getStyle());
            bundle.putBoolean(KEY_DIFFERENCE_SHOW_NOW_TEXT, timeDiffText.shouldShowNowText());
            if (timeDiffText.getMinimumUnit() != null) {
                bundle.putString(KEY_DIFFERENCE_MINIMUM_UNIT, timeDiffText.getMinimumUnit().name());
            }
        } else if (timeFormatText instanceof TimeFormatText) {
            timeFormatText = timeFormatText;
            bundle.putString(KEY_FORMAT_FORMAT_STRING, timeFormatText.getFormatString());
            bundle.putInt(KEY_FORMAT_STYLE, timeFormatText.getStyle());
            TimeZone timeZone = timeFormatText.getTimeZone();
            if (timeZone != null) {
                bundle.putString(KEY_FORMAT_TIME_ZONE, timeZone.getID());
            }
            out.writeBundle(bundle);
        }
        out.writeBundle(bundle);
    }

    public CharSequence getText(Context context, long dateTimeMillis) {
        TimeDependentText timeDependentText = this.mTimeDependentText;
        if (timeDependentText == null) {
            return this.mSurroundingText;
        }
        CharSequence timeDependentPart;
        CharSequence charSequence;
        if (this.mDependentTextCache != null) {
            if (timeDependentText.returnsSameText(this.mDependentTextCacheTime, dateTimeMillis)) {
                timeDependentPart = this.mDependentTextCache;
                charSequence = this.mSurroundingText;
                if (charSequence == null) {
                    return timeDependentPart;
                }
                CharSequence[] charSequenceArr = this.mTemplateValues;
                charSequenceArr[0] = timeDependentPart;
                return TextUtils.expandTemplate(charSequence, charSequenceArr);
            }
        }
        timeDependentPart = this.mTimeDependentText.getText(context, dateTimeMillis);
        this.mDependentTextCacheTime = dateTimeMillis;
        this.mDependentTextCache = timeDependentPart;
        charSequence = this.mSurroundingText;
        if (charSequence == null) {
            return timeDependentPart;
        }
        CharSequence[] charSequenceArr2 = this.mTemplateValues;
        charSequenceArr2[0] = timeDependentPart;
        return TextUtils.expandTemplate(charSequence, charSequenceArr2);
    }

    public boolean returnsSameText(long firstDateTimeMillis, long secondDateTimeMillis) {
        TimeDependentText timeDependentText = this.mTimeDependentText;
        if (timeDependentText == null) {
            return true;
        }
        return timeDependentText.returnsSameText(firstDateTimeMillis, secondDateTimeMillis);
    }

    public boolean isAlwaysEmpty() {
        return this.mTimeDependentText == null && TextUtils.isEmpty(this.mSurroundingText);
    }

    boolean isTimeDependent() {
        return this.mTimeDependentText != null;
    }

    public int describeContents() {
        return 0;
    }

    public static ComplicationText plainText(CharSequence text) {
        return new ComplicationText(text, null);
    }

    public static CharSequence getText(Context context, ComplicationText complicationText, long dateTimeMillis) {
        if (complicationText == null) {
            return null;
        }
        return complicationText.getText(context, dateTimeMillis);
    }
}
