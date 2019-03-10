package android.support.wearable.complications;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@TargetApi(24)
public class TimeFormatText implements TimeDependentText {
    public static final Creator<TimeFormatText> CREATOR = new C04131();
    private static final long[] DATE_TIME_FORMAT_PRECISION = new long[]{TimeUnit.SECONDS.toMillis(1), TimeUnit.MINUTES.toMillis(1), TimeUnit.HOURS.toMillis(1), TimeUnit.DAYS.toMillis(1)};
    private static final String[][] DATE_TIME_FORMAT_SYMBOLS;
    private final Date mDate;
    private final SimpleDateFormat mDateFormat;
    private final int mStyle;
    private long mTimePrecision;
    private final TimeZone mTimeZone;

    /* renamed from: android.support.wearable.complications.TimeFormatText$1 */
    class C04131 implements Creator<TimeFormatText> {
        C04131() {
        }

        public TimeFormatText createFromParcel(Parcel source) {
            return new TimeFormatText(source);
        }

        public TimeFormatText[] newArray(int size) {
            return new TimeFormatText[size];
        }
    }

    static {
        r1 = new String[4][];
        r1[0] = new String[]{"S", "s"};
        r1[1] = new String[]{"m"};
        r1[2] = new String[]{"H", "K", "h", "k"};
        r1[3] = new String[]{"D", "E", "F", "c", "d", "W", "w", "M", "y"};
        DATE_TIME_FORMAT_SYMBOLS = r1;
    }

    public TimeFormatText(String format, int style, TimeZone timeZone) {
        this.mDateFormat = new SimpleDateFormat(format);
        this.mStyle = style;
        this.mTimePrecision = -1;
        if (timeZone != null) {
            this.mDateFormat.setTimeZone(timeZone);
            this.mTimeZone = timeZone;
        } else {
            this.mTimeZone = this.mDateFormat.getTimeZone();
        }
        this.mDate = new Date();
    }

    @SuppressLint({"DefaultLocale"})
    public CharSequence getText(Context context, long dateTimeMillis) {
        String formattedDate = this.mDateFormat.format(new Date(dateTimeMillis));
        switch (this.mStyle) {
            case 2:
                return formattedDate.toUpperCase();
            case 3:
                return formattedDate.toLowerCase();
            default:
                return formattedDate;
        }
    }

    public boolean returnsSameText(long firstDateTimeMillis, long secondDateTimeMillis) {
        long precision = getPrecision();
        return (firstDateTimeMillis + getOffset(firstDateTimeMillis)) / precision == (secondDateTimeMillis + getOffset(secondDateTimeMillis)) / precision;
    }

    public long getPrecision() {
        if (this.mTimePrecision == -1) {
            String format = getDateFormatWithoutText(this.mDateFormat.toPattern());
            for (int i = 0; i < DATE_TIME_FORMAT_SYMBOLS.length && this.mTimePrecision == -1; i++) {
                int j = 0;
                while (true) {
                    String[][] strArr = DATE_TIME_FORMAT_SYMBOLS;
                    if (j >= strArr[i].length) {
                        break;
                    } else if (format.contains(strArr[i][j])) {
                        break;
                    } else {
                        j++;
                    }
                }
                this.mTimePrecision = DATE_TIME_FORMAT_PRECISION[i];
            }
        }
        return this.mTimePrecision;
    }

    public String getFormatString() {
        return this.mDateFormat.toPattern();
    }

    public int getStyle() {
        return this.mStyle;
    }

    public TimeZone getTimeZone() {
        return this.mTimeZone;
    }

    private long getOffset(long date) {
        this.mDate.setTime(date);
        if (this.mTimeZone.inDaylightTime(this.mDate)) {
            return ((long) this.mTimeZone.getRawOffset()) + ((long) this.mTimeZone.getDSTSavings());
        }
        return (long) this.mTimeZone.getRawOffset();
    }

    private String getDateFormatWithoutText(String format) {
        String result = "";
        boolean isTextPart = false;
        int index = 0;
        while (index < format.length()) {
            boolean z = true;
            if (format.charAt(index) != '\'') {
                if (!isTextPart) {
                    String valueOf = String.valueOf(result);
                    char charAt = format.charAt(index);
                    StringBuilder stringBuilder = new StringBuilder(String.valueOf(valueOf).length() + 1);
                    stringBuilder.append(valueOf);
                    stringBuilder.append(charAt);
                    result = stringBuilder.toString();
                }
                index++;
            } else if (index + 1 >= format.length() || format.charAt(index + 1) != '\'') {
                index++;
                if (isTextPart) {
                    z = false;
                }
                isTextPart = z;
            } else {
                index += 2;
            }
        }
        return result;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.mDateFormat);
        dest.writeInt(this.mStyle);
        dest.writeSerializable(this.mTimeZone);
    }

    protected TimeFormatText(Parcel in) {
        this.mDateFormat = (SimpleDateFormat) in.readSerializable();
        this.mStyle = in.readInt();
        this.mTimeZone = (TimeZone) in.readSerializable();
        this.mTimePrecision = -1;
        this.mDate = new Date();
    }
}
