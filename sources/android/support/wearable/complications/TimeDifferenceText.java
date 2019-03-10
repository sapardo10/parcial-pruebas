package android.support.wearable.complications;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import android.support.wearable.C0395R;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@TargetApi(24)
public class TimeDifferenceText implements TimeDependentText {
    public static final Creator<TimeDifferenceText> CREATOR = new C04111();
    private static final int MINIMUM_UNIT_PARCELED_IS_NULL = -1;
    private static final int ONLY_SHOW_DAYS_THRESHOLD = 10;
    private static final int SHORT_CHARACTER_LIMIT = 7;
    @Nullable
    private final TimeUnit mMinimumUnit;
    private final long mReferencePeriodEnd;
    private final long mReferencePeriodStart;
    private final boolean mShowNowText;
    private final int mStyle;

    /* renamed from: android.support.wearable.complications.TimeDifferenceText$1 */
    class C04111 implements Creator<TimeDifferenceText> {
        C04111() {
        }

        public TimeDifferenceText createFromParcel(Parcel source) {
            return new TimeDifferenceText(source);
        }

        public TimeDifferenceText[] newArray(int size) {
            return new TimeDifferenceText[size];
        }
    }

    /* renamed from: android.support.wearable.complications.TimeDifferenceText$2 */
    static /* synthetic */ class C04122 {
        static final /* synthetic */ int[] $SwitchMap$java$util$concurrent$TimeUnit = new int[TimeUnit.values().length];

        static {
            try {
                $SwitchMap$java$util$concurrent$TimeUnit[TimeUnit.MILLISECONDS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$java$util$concurrent$TimeUnit[TimeUnit.SECONDS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$java$util$concurrent$TimeUnit[TimeUnit.MINUTES.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$java$util$concurrent$TimeUnit[TimeUnit.HOURS.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$java$util$concurrent$TimeUnit[TimeUnit.DAYS.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    public TimeDifferenceText(long referencePeriodStart, long referencePeriodEnd, int style, boolean showNowText, @Nullable TimeUnit minimumUnit) {
        this.mReferencePeriodStart = referencePeriodStart;
        this.mReferencePeriodEnd = referencePeriodEnd;
        this.mStyle = style;
        this.mShowNowText = showNowText;
        this.mMinimumUnit = minimumUnit;
    }

    public CharSequence getText(Context context, long dateTimeMillis) {
        Resources res = context.getResources();
        long timeDifference = getTimeDifference(dateTimeMillis);
        if (timeDifference == 0 && this.mShowNowText) {
            return res.getString(C0395R.string.time_difference_now);
        }
        switch (this.mStyle) {
            case 1:
                return buildStopwatchText(timeDifference, res);
            case 2:
                return buildShortSingleUnitText(timeDifference, res);
            case 3:
                return shortDualUnlessTooLong(timeDifference, res);
            case 4:
                return buildWordsSingleUnitText(timeDifference, res);
            case 5:
                return wordsSingleUnlessTooLong(timeDifference, res);
            default:
                return buildShortSingleUnitText(timeDifference, res);
        }
    }

    public boolean returnsSameText(long firstDateTimeMillis, long secondDateTimeMillis) {
        long precision = getPrecision();
        return divRoundingUp(getTimeDifference(firstDateTimeMillis), precision) == divRoundingUp(getTimeDifference(secondDateTimeMillis), precision);
    }

    public long getPrecision() {
        long defaultPrecision = this.mStyle != 1 ? TimeUnit.MINUTES.toMillis(1) : TimeUnit.SECONDS.toMillis(1);
        TimeUnit timeUnit = this.mMinimumUnit;
        if (timeUnit == null) {
            return defaultPrecision;
        }
        return Math.max(defaultPrecision, timeUnit.toMillis(1));
    }

    long getReferencePeriodStart() {
        return this.mReferencePeriodStart;
    }

    long getReferencePeriodEnd() {
        return this.mReferencePeriodEnd;
    }

    int getStyle() {
        return this.mStyle;
    }

    boolean shouldShowNowText() {
        return this.mShowNowText;
    }

    @Nullable
    TimeUnit getMinimumUnit() {
        return this.mMinimumUnit;
    }

    private long getTimeDifference(long dateTimeMillis) {
        long j = this.mReferencePeriodStart;
        if (dateTimeMillis < j) {
            return j - dateTimeMillis;
        }
        j = this.mReferencePeriodEnd;
        if (dateTimeMillis > j) {
            return dateTimeMillis - j;
        }
        return 0;
    }

    private String buildShortSingleUnitText(long time, Resources res) {
        long timeRoundedToHours = roundUpToUnit(time, TimeUnit.HOURS);
        if (!isGreaterOrEqual(this.mMinimumUnit, TimeUnit.DAYS)) {
            if (days(timeRoundedToHours) <= 0) {
                long timeRoundedToMins = roundUpToUnit(time, TimeUnit.MINUTES);
                if (!isGreaterOrEqual(this.mMinimumUnit, TimeUnit.HOURS)) {
                    if (hours(timeRoundedToMins) <= 0) {
                        return buildShortMinsText(minutes(timeRoundedToMins), res);
                    }
                }
                return buildShortHoursText(hours(timeRoundedToHours), res);
            }
        }
        return buildShortDaysText(days(roundUpToUnit(time, TimeUnit.DAYS)), res);
    }

    private String buildShortDualUnitText(long time, Resources res) {
        long timeRoundedToHours = roundUpToUnit(time, TimeUnit.HOURS);
        if (!isGreaterOrEqual(this.mMinimumUnit, TimeUnit.DAYS)) {
            if (days(timeRoundedToHours) < 10) {
                long timeRoundedToMins = roundUpToUnit(time, TimeUnit.MINUTES);
                int hoursRoundedToHours;
                if (days(timeRoundedToMins) > 0) {
                    hoursRoundedToHours = hours(timeRoundedToHours);
                    if (hoursRoundedToHours > 0) {
                        return buildShortDaysHoursText(days(timeRoundedToHours), hoursRoundedToHours, res);
                    }
                    return buildShortDaysText(days(timeRoundedToHours), res);
                } else if (isGreaterOrEqual(this.mMinimumUnit, TimeUnit.HOURS)) {
                    return buildShortHoursText(hours(timeRoundedToHours), res);
                } else {
                    hoursRoundedToHours = hours(timeRoundedToMins);
                    int minutesRoundedToMins = minutes(timeRoundedToMins);
                    if (hoursRoundedToHours <= 0) {
                        return buildShortMinsText(minutes(timeRoundedToMins), res);
                    }
                    if (minutesRoundedToMins > 0) {
                        return buildShortHoursMinsText(hoursRoundedToHours, minutesRoundedToMins, res);
                    }
                    return buildShortHoursText(hoursRoundedToHours, res);
                }
            }
        }
        return buildShortDaysText(days(roundUpToUnit(time, TimeUnit.DAYS)), res);
    }

    private String shortDualUnlessTooLong(long time, Resources res) {
        String shortDual = buildShortDualUnitText(time, res);
        if (shortDual.length() <= 7) {
            return shortDual;
        }
        return buildShortSingleUnitText(time, res);
    }

    private String buildStopwatchText(long time, Resources res) {
        if (isGreaterOrEqual(this.mMinimumUnit, TimeUnit.DAYS)) {
            return buildShortDaysText(days(roundUpToUnit(time, TimeUnit.DAYS)), res);
        }
        long timeRoundedToMins = roundUpToUnit(time, TimeUnit.MINUTES);
        if (!isGreaterOrEqual(this.mMinimumUnit, TimeUnit.HOURS)) {
            if (days(timeRoundedToMins) <= 0) {
                long timeRoundedToSecs = roundUpToUnit(time, TimeUnit.SECONDS);
                if (!isGreaterOrEqual(this.mMinimumUnit, TimeUnit.MINUTES)) {
                    if (hours(timeRoundedToSecs) <= 0) {
                        return String.format(Locale.US, "%02d:%02d", new Object[]{Integer.valueOf(minutes(timeRoundedToSecs)), Integer.valueOf(seconds(timeRoundedToSecs))});
                    }
                }
                return String.format(Locale.US, "%d:%02d", new Object[]{Integer.valueOf(hours(timeRoundedToMins)), Integer.valueOf(minutes(timeRoundedToMins))});
            }
        }
        return buildShortDualUnitText(time, res);
    }

    private String buildWordsSingleUnitText(long time, Resources res) {
        int mins;
        long timeRoundedToHours = roundUpToUnit(time, TimeUnit.HOURS);
        if (!isGreaterOrEqual(this.mMinimumUnit, TimeUnit.DAYS)) {
            if (days(timeRoundedToHours) <= 0) {
                long timeRoundedToMins = roundUpToUnit(time, TimeUnit.MINUTES);
                if (!isGreaterOrEqual(this.mMinimumUnit, TimeUnit.HOURS)) {
                    if (hours(timeRoundedToMins) <= 0) {
                        mins = minutes(timeRoundedToMins);
                        return res.getQuantityString(C0395R.plurals.time_difference_words_minutes, mins, new Object[]{Integer.valueOf(mins)});
                    }
                }
                mins = hours(timeRoundedToHours);
                return res.getQuantityString(C0395R.plurals.time_difference_words_hours, mins, new Object[]{Integer.valueOf(mins)});
            }
        }
        mins = days(roundUpToUnit(time, TimeUnit.DAYS));
        return res.getQuantityString(C0395R.plurals.time_difference_words_days, mins, new Object[]{Integer.valueOf(mins)});
    }

    private String wordsSingleUnlessTooLong(long time, Resources res) {
        String wordsSingle = buildWordsSingleUnitText(time, res);
        if (wordsSingle.length() <= 7) {
            return wordsSingle;
        }
        return buildShortSingleUnitText(time, res);
    }

    private static String buildShortDaysText(int days, Resources res) {
        return res.getQuantityString(C0395R.plurals.time_difference_short_days, days, new Object[]{Integer.valueOf(days)});
    }

    private static String buildShortHoursText(int hours, Resources res) {
        return res.getQuantityString(C0395R.plurals.time_difference_short_hours, hours, new Object[]{Integer.valueOf(hours)});
    }

    private static String buildShortMinsText(int mins, Resources res) {
        return res.getQuantityString(C0395R.plurals.time_difference_short_minutes, mins, new Object[]{Integer.valueOf(mins)});
    }

    private static String buildShortDaysHoursText(int days, int hours, Resources res) {
        return res.getString(C0395R.string.time_difference_short_days_and_hours, new Object[]{buildShortDaysText(days, res), buildShortHoursText(hours, res)});
    }

    private static String buildShortHoursMinsText(int hours, int mins, Resources res) {
        return res.getString(C0395R.string.time_difference_short_hours_and_minutes, new Object[]{buildShortHoursText(hours, res), buildShortMinsText(mins, res)});
    }

    private static long roundUpToUnit(long durationMillis, TimeUnit unit) {
        long unitInMillis = unit.toMillis(1);
        return divRoundingUp(durationMillis, unitInMillis) * unitInMillis;
    }

    private static long divRoundingUp(long num, long divisor) {
        return (num / divisor) + ((long) (num % divisor == 0 ? 0 : 1));
    }

    private static int modToUnit(long durationMillis, TimeUnit unit) {
        return (int) ((durationMillis / unit.toMillis(1)) % ((long) getUnitMaximum(unit)));
    }

    private static int days(long durationMillis) {
        return modToUnit(durationMillis, TimeUnit.DAYS);
    }

    private static int hours(long durationMillis) {
        return modToUnit(durationMillis, TimeUnit.HOURS);
    }

    private static int minutes(long durationMillis) {
        return modToUnit(durationMillis, TimeUnit.MINUTES);
    }

    private static int seconds(long durationMillis) {
        return modToUnit(durationMillis, TimeUnit.SECONDS);
    }

    private static boolean isGreaterOrEqual(@Nullable TimeUnit unit1, TimeUnit unit2) {
        boolean z = false;
        if (unit1 == null) {
            return false;
        }
        if (unit1.toMillis(1) >= unit2.toMillis(1)) {
            z = true;
        }
        return z;
    }

    private static int getUnitMaximum(TimeUnit unit) {
        switch (C04122.$SwitchMap$java$util$concurrent$TimeUnit[unit.ordinal()]) {
            case 1:
                return 1000;
            case 2:
                return 60;
            case 3:
                return 60;
            case 4:
                return 24;
            case 5:
                return Integer.MAX_VALUE;
            default:
                String valueOf = String.valueOf(unit);
                StringBuilder stringBuilder = new StringBuilder(String.valueOf(valueOf).length() + 20);
                stringBuilder.append("Unit not supported: ");
                stringBuilder.append(valueOf);
                throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mReferencePeriodStart);
        dest.writeLong(this.mReferencePeriodEnd);
        dest.writeInt(this.mStyle);
        dest.writeByte(this.mShowNowText);
        TimeUnit timeUnit = this.mMinimumUnit;
        dest.writeInt(timeUnit == null ? -1 : timeUnit.ordinal());
    }

    protected TimeDifferenceText(Parcel in) {
        TimeUnit timeUnit;
        this.mReferencePeriodStart = in.readLong();
        this.mReferencePeriodEnd = in.readLong();
        this.mStyle = in.readInt();
        this.mShowNowText = in.readByte() != (byte) 0;
        int tmpMMinimumUnit = in.readInt();
        if (tmpMMinimumUnit == -1) {
            timeUnit = null;
        } else {
            timeUnit = TimeUnit.values()[tmpMMinimumUnit];
        }
        this.mMinimumUnit = timeUnit;
    }
}
