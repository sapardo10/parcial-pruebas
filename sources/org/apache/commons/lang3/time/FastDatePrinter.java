package org.apache.commons.lang3.time;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.text.FieldPosition;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class FastDatePrinter implements DatePrinter, Serializable {
    public static final int FULL = 0;
    public static final int LONG = 1;
    private static final int MAX_DIGITS = 10;
    public static final int MEDIUM = 2;
    public static final int SHORT = 3;
    private static final ConcurrentMap<TimeZoneDisplayKey, String> cTimeZoneDisplayCache = new ConcurrentHashMap(7);
    private static final long serialVersionUID = 1;
    private final Locale mLocale;
    private transient int mMaxLengthEstimate;
    private final String mPattern;
    private transient Rule[] mRules;
    private final TimeZone mTimeZone;

    private interface Rule {
        void appendTo(Appendable appendable, Calendar calendar) throws IOException;

        int estimateLength();
    }

    private static class TimeZoneDisplayKey {
        private final Locale mLocale;
        private final int mStyle;
        private final TimeZone mTimeZone;

        TimeZoneDisplayKey(TimeZone timeZone, boolean daylight, int style, Locale locale) {
            this.mTimeZone = timeZone;
            if (daylight) {
                this.mStyle = Integer.MIN_VALUE | style;
            } else {
                this.mStyle = style;
            }
            this.mLocale = locale;
        }

        public int hashCode() {
            return (((this.mStyle * 31) + this.mLocale.hashCode()) * 31) + this.mTimeZone.hashCode();
        }

        public boolean equals(Object obj) {
            boolean z = true;
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof TimeZoneDisplayKey)) {
                return false;
            }
            TimeZoneDisplayKey other = (TimeZoneDisplayKey) obj;
            if (!this.mTimeZone.equals(other.mTimeZone) || this.mStyle != other.mStyle || !this.mLocale.equals(other.mLocale)) {
                z = false;
            }
            return z;
        }
    }

    private static class CharacterLiteral implements Rule {
        private final char mValue;

        CharacterLiteral(char value) {
            this.mValue = value;
        }

        public int estimateLength() {
            return 1;
        }

        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            buffer.append(this.mValue);
        }
    }

    private static class Iso8601_Rule implements Rule {
        static final Iso8601_Rule ISO8601_HOURS = new Iso8601_Rule(3);
        static final Iso8601_Rule ISO8601_HOURS_COLON_MINUTES = new Iso8601_Rule(6);
        static final Iso8601_Rule ISO8601_HOURS_MINUTES = new Iso8601_Rule(5);
        final int length;

        static Iso8601_Rule getRule(int tokenLen) {
            switch (tokenLen) {
                case 1:
                    return ISO8601_HOURS;
                case 2:
                    return ISO8601_HOURS_MINUTES;
                case 3:
                    return ISO8601_HOURS_COLON_MINUTES;
                default:
                    throw new IllegalArgumentException("invalid number of X");
            }
        }

        Iso8601_Rule(int length) {
            this.length = length;
        }

        public int estimateLength() {
            return this.length;
        }

        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            int offset = calendar.get(15) + calendar.get(16);
            if (offset == 0) {
                buffer.append("Z");
                return;
            }
            if (offset < 0) {
                buffer.append('-');
                offset = -offset;
            } else {
                buffer.append('+');
            }
            int hours = offset / 3600000;
            FastDatePrinter.appendDigits(buffer, hours);
            int i = this.length;
            if (i >= 5) {
                if (i == 6) {
                    buffer.append(':');
                }
                FastDatePrinter.appendDigits(buffer, (offset / 60000) - (hours * 60));
            }
        }
    }

    private interface NumberRule extends Rule {
        void appendTo(Appendable appendable, int i) throws IOException;
    }

    private static class StringLiteral implements Rule {
        private final String mValue;

        StringLiteral(String value) {
            this.mValue = value;
        }

        public int estimateLength() {
            return this.mValue.length();
        }

        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            buffer.append(this.mValue);
        }
    }

    private static class TextField implements Rule {
        private final int mField;
        private final String[] mValues;

        TextField(int field, String[] values) {
            this.mField = field;
            this.mValues = values;
        }

        public int estimateLength() {
            int max = 0;
            int i = this.mValues.length;
            while (true) {
                i--;
                if (i < 0) {
                    return max;
                }
                int len = this.mValues[i].length();
                if (len > max) {
                    max = len;
                }
            }
        }

        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            buffer.append(this.mValues[calendar.get(this.mField)]);
        }
    }

    private static class TimeZoneNameRule implements Rule {
        private final String mDaylight;
        private final Locale mLocale;
        private final String mStandard;
        private final int mStyle;

        TimeZoneNameRule(TimeZone timeZone, Locale locale, int style) {
            this.mLocale = locale;
            this.mStyle = style;
            this.mStandard = FastDatePrinter.getTimeZoneDisplay(timeZone, false, style, locale);
            this.mDaylight = FastDatePrinter.getTimeZoneDisplay(timeZone, true, style, locale);
        }

        public int estimateLength() {
            return Math.max(this.mStandard.length(), this.mDaylight.length());
        }

        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            TimeZone zone = calendar.getTimeZone();
            if (calendar.get(16) != 0) {
                buffer.append(FastDatePrinter.getTimeZoneDisplay(zone, true, this.mStyle, this.mLocale));
            } else {
                buffer.append(FastDatePrinter.getTimeZoneDisplay(zone, false, this.mStyle, this.mLocale));
            }
        }
    }

    private static class TimeZoneNumberRule implements Rule {
        static final TimeZoneNumberRule INSTANCE_COLON = new TimeZoneNumberRule(true);
        static final TimeZoneNumberRule INSTANCE_NO_COLON = new TimeZoneNumberRule(false);
        final boolean mColon;

        TimeZoneNumberRule(boolean colon) {
            this.mColon = colon;
        }

        public int estimateLength() {
            return 5;
        }

        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            int offset = calendar.get(15) + calendar.get(16);
            if (offset < 0) {
                buffer.append('-');
                offset = -offset;
            } else {
                buffer.append('+');
            }
            int hours = offset / 3600000;
            FastDatePrinter.appendDigits(buffer, hours);
            if (this.mColon) {
                buffer.append(':');
            }
            FastDatePrinter.appendDigits(buffer, (offset / 60000) - (hours * 60));
        }
    }

    private static class DayInWeekField implements NumberRule {
        private final NumberRule mRule;

        DayInWeekField(NumberRule rule) {
            this.mRule = rule;
        }

        public int estimateLength() {
            return this.mRule.estimateLength();
        }

        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            int i = 7;
            int value = calendar.get(7);
            NumberRule numberRule = this.mRule;
            if (value != 1) {
                i = value - 1;
            }
            numberRule.appendTo(buffer, i);
        }

        public void appendTo(Appendable buffer, int value) throws IOException {
            this.mRule.appendTo(buffer, value);
        }
    }

    private static class PaddedNumberField implements NumberRule {
        private final int mField;
        private final int mSize;

        PaddedNumberField(int field, int size) {
            if (size >= 3) {
                this.mField = field;
                this.mSize = size;
                return;
            }
            throw new IllegalArgumentException();
        }

        public int estimateLength() {
            return this.mSize;
        }

        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            appendTo(buffer, calendar.get(this.mField));
        }

        public final void appendTo(Appendable buffer, int value) throws IOException {
            FastDatePrinter.appendFullDigits(buffer, value, this.mSize);
        }
    }

    private static class TwelveHourField implements NumberRule {
        private final NumberRule mRule;

        TwelveHourField(NumberRule rule) {
            this.mRule = rule;
        }

        public int estimateLength() {
            return this.mRule.estimateLength();
        }

        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            int value = calendar.get(10);
            if (value == 0) {
                value = calendar.getLeastMaximum(10) + 1;
            }
            this.mRule.appendTo(buffer, value);
        }

        public void appendTo(Appendable buffer, int value) throws IOException {
            this.mRule.appendTo(buffer, value);
        }
    }

    private static class TwentyFourHourField implements NumberRule {
        private final NumberRule mRule;

        TwentyFourHourField(NumberRule rule) {
            this.mRule = rule;
        }

        public int estimateLength() {
            return this.mRule.estimateLength();
        }

        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            int value = calendar.get(11);
            if (value == 0) {
                value = calendar.getMaximum(11) + 1;
            }
            this.mRule.appendTo(buffer, value);
        }

        public void appendTo(Appendable buffer, int value) throws IOException {
            this.mRule.appendTo(buffer, value);
        }
    }

    private static class TwoDigitMonthField implements NumberRule {
        static final TwoDigitMonthField INSTANCE = new TwoDigitMonthField();

        TwoDigitMonthField() {
        }

        public int estimateLength() {
            return 2;
        }

        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            appendTo(buffer, calendar.get(2) + 1);
        }

        public final void appendTo(Appendable buffer, int value) throws IOException {
            FastDatePrinter.appendDigits(buffer, value);
        }
    }

    private static class TwoDigitNumberField implements NumberRule {
        private final int mField;

        TwoDigitNumberField(int field) {
            this.mField = field;
        }

        public int estimateLength() {
            return 2;
        }

        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            appendTo(buffer, calendar.get(this.mField));
        }

        public final void appendTo(Appendable buffer, int value) throws IOException {
            if (value < 100) {
                FastDatePrinter.appendDigits(buffer, value);
            } else {
                FastDatePrinter.appendFullDigits(buffer, value, 2);
            }
        }
    }

    private static class TwoDigitYearField implements NumberRule {
        static final TwoDigitYearField INSTANCE = new TwoDigitYearField();

        TwoDigitYearField() {
        }

        public int estimateLength() {
            return 2;
        }

        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            appendTo(buffer, calendar.get(1) % 100);
        }

        public final void appendTo(Appendable buffer, int value) throws IOException {
            FastDatePrinter.appendDigits(buffer, value);
        }
    }

    private static class UnpaddedMonthField implements NumberRule {
        static final UnpaddedMonthField INSTANCE = new UnpaddedMonthField();

        UnpaddedMonthField() {
        }

        public int estimateLength() {
            return 2;
        }

        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            appendTo(buffer, calendar.get(2) + 1);
        }

        public final void appendTo(Appendable buffer, int value) throws IOException {
            if (value < 10) {
                buffer.append((char) (value + 48));
            } else {
                FastDatePrinter.appendDigits(buffer, value);
            }
        }
    }

    private static class UnpaddedNumberField implements NumberRule {
        private final int mField;

        UnpaddedNumberField(int field) {
            this.mField = field;
        }

        public int estimateLength() {
            return 4;
        }

        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            appendTo(buffer, calendar.get(this.mField));
        }

        public final void appendTo(Appendable buffer, int value) throws IOException {
            if (value < 10) {
                buffer.append((char) (value + 48));
            } else if (value < 100) {
                FastDatePrinter.appendDigits(buffer, value);
            } else {
                FastDatePrinter.appendFullDigits(buffer, value, 1);
            }
        }
    }

    private static class WeekYear implements NumberRule {
        private final NumberRule mRule;

        WeekYear(NumberRule rule) {
            this.mRule = rule;
        }

        public int estimateLength() {
            return this.mRule.estimateLength();
        }

        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            this.mRule.appendTo(buffer, calendar.getWeekYear());
        }

        public void appendTo(Appendable buffer, int value) throws IOException {
            this.mRule.appendTo(buffer, value);
        }
    }

    protected FastDatePrinter(String pattern, TimeZone timeZone, Locale locale) {
        this.mPattern = pattern;
        this.mTimeZone = timeZone;
        this.mLocale = locale;
        init();
    }

    private void init() {
        List<Rule> rulesList = parsePattern();
        this.mRules = (Rule[]) rulesList.toArray(new Rule[rulesList.size()]);
        int len = 0;
        int i = this.mRules.length;
        while (true) {
            i--;
            if (i >= 0) {
                len += this.mRules[i].estimateLength();
            } else {
                this.mMaxLengthEstimate = len;
                return;
            }
        }
    }

    protected List<Rule> parsePattern() {
        String[] strArr;
        String[] strArr2;
        DateFormatSymbols symbols = new DateFormatSymbols(this.mLocale);
        List<Rule> rules = new ArrayList();
        String[] ERAs = symbols.getEras();
        String[] months = symbols.getMonths();
        String[] shortMonths = symbols.getShortMonths();
        String[] weekdays = symbols.getWeekdays();
        String[] shortWeekdays = symbols.getShortWeekdays();
        String[] AmPmStrings = symbols.getAmPmStrings();
        int length = this.mPattern.length();
        int[] indexRef = new int[1];
        int i = 0;
        while (i < length) {
            indexRef[0] = i;
            String token = parseToken(r0.mPattern, indexRef);
            i = indexRef[0];
            int tokenLen = token.length();
            if (tokenLen == 0) {
                DateFormatSymbols dateFormatSymbols = symbols;
                strArr = months;
                strArr2 = weekdays;
                return rules;
            }
            char c = token.charAt(0);
            switch (c) {
                case 'D':
                    dateFormatSymbols = symbols;
                    strArr = months;
                    strArr2 = weekdays;
                    symbols = selectNumberRule(6, tokenLen);
                    break;
                case 'E':
                    dateFormatSymbols = symbols;
                    strArr = months;
                    strArr2 = weekdays;
                    symbols = new TextField(7, tokenLen < 4 ? shortWeekdays : strArr2);
                    break;
                case 'F':
                    dateFormatSymbols = symbols;
                    strArr = months;
                    strArr2 = weekdays;
                    symbols = selectNumberRule(8, tokenLen);
                    break;
                case 'G':
                    dateFormatSymbols = symbols;
                    strArr = months;
                    strArr2 = weekdays;
                    symbols = new TextField(0, ERAs);
                    break;
                case 'H':
                    dateFormatSymbols = symbols;
                    strArr = months;
                    strArr2 = weekdays;
                    symbols = selectNumberRule(11, tokenLen);
                    break;
                default:
                    switch (c) {
                        case 'W':
                            dateFormatSymbols = symbols;
                            strArr = months;
                            strArr2 = weekdays;
                            symbols = selectNumberRule(4, tokenLen);
                            break;
                        case 'X':
                            dateFormatSymbols = symbols;
                            strArr = months;
                            strArr2 = weekdays;
                            symbols = Iso8601_Rule.getRule(tokenLen);
                            break;
                        case 'Y':
                            dateFormatSymbols = symbols;
                            strArr = months;
                            strArr2 = weekdays;
                            if (tokenLen != 2) {
                                symbols = selectNumberRule(1, tokenLen >= 4 ? tokenLen : 4);
                            } else {
                                symbols = TwoDigitYearField.INSTANCE;
                            }
                            if (c != 'Y') {
                                symbols = new WeekYear((NumberRule) symbols);
                                break;
                            }
                            break;
                        case 'Z':
                            dateFormatSymbols = symbols;
                            strArr = months;
                            strArr2 = weekdays;
                            if (tokenLen != 1) {
                                if (tokenLen != 2) {
                                    symbols = TimeZoneNumberRule.INSTANCE_COLON;
                                    break;
                                }
                                symbols = Iso8601_Rule.ISO8601_HOURS_COLON_MINUTES;
                                break;
                            }
                            symbols = TimeZoneNumberRule.INSTANCE_NO_COLON;
                            break;
                        default:
                            switch (c) {
                                case 'y':
                                    break;
                                case 'z':
                                    dateFormatSymbols = symbols;
                                    strArr2 = weekdays;
                                    if (tokenLen < 4) {
                                        strArr = months;
                                        symbols = new TimeZoneNameRule(r0.mTimeZone, r0.mLocale, 0);
                                        break;
                                    }
                                    strArr = months;
                                    symbols = new TimeZoneNameRule(r0.mTimeZone, r0.mLocale, 1);
                                    continue;
                                default:
                                    switch (c) {
                                        case '\'':
                                            dateFormatSymbols = symbols;
                                            strArr2 = weekdays;
                                            weekdays = token.substring(1);
                                            if (weekdays.length() != 1) {
                                                symbols = new StringLiteral(weekdays);
                                                strArr = months;
                                                break;
                                            }
                                            symbols = new CharacterLiteral(weekdays.charAt(0));
                                            strArr = months;
                                            break;
                                        case 'K':
                                            dateFormatSymbols = symbols;
                                            strArr2 = weekdays;
                                            symbols = selectNumberRule(10, tokenLen);
                                            strArr = months;
                                            break;
                                        case 'M':
                                            dateFormatSymbols = symbols;
                                            strArr2 = weekdays;
                                            if (tokenLen < 4) {
                                                if (tokenLen != 3) {
                                                    if (tokenLen != 2) {
                                                        symbols = UnpaddedMonthField.INSTANCE;
                                                        strArr = months;
                                                        break;
                                                    }
                                                    symbols = TwoDigitMonthField.INSTANCE;
                                                    strArr = months;
                                                    break;
                                                }
                                                symbols = new TextField(2, shortMonths);
                                                strArr = months;
                                                break;
                                            }
                                            symbols = new TextField(2, months);
                                            strArr = months;
                                            break;
                                        case 'S':
                                            dateFormatSymbols = symbols;
                                            strArr2 = weekdays;
                                            symbols = selectNumberRule(14, tokenLen);
                                            strArr = months;
                                            break;
                                        case 'a':
                                            dateFormatSymbols = symbols;
                                            strArr2 = weekdays;
                                            symbols = new TextField(9, AmPmStrings);
                                            strArr = months;
                                            break;
                                        case 'd':
                                            dateFormatSymbols = symbols;
                                            strArr2 = weekdays;
                                            symbols = selectNumberRule(5, tokenLen);
                                            strArr = months;
                                            break;
                                        case 'h':
                                            dateFormatSymbols = symbols;
                                            strArr2 = weekdays;
                                            symbols = new TwelveHourField(selectNumberRule(10, tokenLen));
                                            strArr = months;
                                            break;
                                        case 'k':
                                            dateFormatSymbols = symbols;
                                            strArr2 = weekdays;
                                            symbols = new TwentyFourHourField(selectNumberRule(11, tokenLen));
                                            strArr = months;
                                            break;
                                        case 'm':
                                            dateFormatSymbols = symbols;
                                            strArr2 = weekdays;
                                            symbols = selectNumberRule(12, tokenLen);
                                            strArr = months;
                                            break;
                                        case 's':
                                            dateFormatSymbols = symbols;
                                            strArr2 = weekdays;
                                            symbols = selectNumberRule(13, tokenLen);
                                            strArr = months;
                                            break;
                                        case 'u':
                                            dateFormatSymbols = symbols;
                                            strArr2 = weekdays;
                                            symbols = new DayInWeekField(selectNumberRule(7, tokenLen));
                                            strArr = months;
                                            break;
                                        case 'w':
                                            dateFormatSymbols = symbols;
                                            strArr2 = weekdays;
                                            symbols = selectNumberRule(3, tokenLen);
                                            strArr = months;
                                            continue;
                                        default:
                                            symbols = new StringBuilder();
                                            symbols.append("Illegal pattern component: ");
                                            symbols.append(token);
                                            throw new IllegalArgumentException(symbols.toString());
                                    }
                            }
                            dateFormatSymbols = symbols;
                            strArr = months;
                            strArr2 = weekdays;
                            if (tokenLen != 2) {
                                symbols = TwoDigitYearField.INSTANCE;
                            } else {
                                if (tokenLen >= 4) {
                                }
                                symbols = selectNumberRule(1, tokenLen >= 4 ? tokenLen : 4);
                            }
                            if (c != 'Y') {
                                break;
                            }
                            symbols = new WeekYear((NumberRule) symbols);
                            break;
                    }
            }
            rules.add(symbols);
            i++;
            months = strArr;
            symbols = dateFormatSymbols;
            weekdays = strArr2;
        }
        strArr = months;
        strArr2 = weekdays;
        return rules;
    }

    protected String parseToken(String pattern, int[] indexRef) {
        StringBuilder buf = new StringBuilder();
        int i = indexRef[0];
        int length = pattern.length();
        char c = pattern.charAt(i);
        if (c >= 'A') {
            if (c > 'Z') {
            }
            buf.append(c);
            while (i + 1 < length) {
                if (pattern.charAt(i + 1) == c) {
                    break;
                }
                buf.append(c);
                i++;
            }
            indexRef[0] = i;
            return buf.toString();
        }
        if (c < 'a' || c > 'z') {
            buf.append('\'');
            boolean inLiteral = false;
            while (i < length) {
                c = pattern.charAt(i);
                if (c != '\'') {
                    if (!inLiteral) {
                        if (c >= 'A') {
                            if (c <= 'Z') {
                                i--;
                                break;
                            }
                        }
                        if (c >= 'a' && c <= 'z') {
                            i--;
                            break;
                        }
                    }
                    buf.append(c);
                } else if (i + 1 >= length || pattern.charAt(i + 1) != '\'') {
                    inLiteral = !inLiteral;
                } else {
                    i++;
                    buf.append(c);
                }
                i++;
            }
            indexRef[0] = i;
            return buf.toString();
        }
        buf.append(c);
        while (i + 1 < length) {
            if (pattern.charAt(i + 1) == c) {
                break;
            }
            buf.append(c);
            i++;
        }
        indexRef[0] = i;
        return buf.toString();
    }

    protected NumberRule selectNumberRule(int field, int padding) {
        switch (padding) {
            case 1:
                return new UnpaddedNumberField(field);
            case 2:
                return new TwoDigitNumberField(field);
            default:
                return new PaddedNumberField(field, padding);
        }
    }

    @Deprecated
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        if (obj instanceof Date) {
            return format((Date) obj, toAppendTo);
        }
        if (obj instanceof Calendar) {
            return format((Calendar) obj, toAppendTo);
        }
        if (obj instanceof Long) {
            return format(((Long) obj).longValue(), toAppendTo);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unknown class: ");
        stringBuilder.append(obj == null ? "<null>" : obj.getClass().getName());
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    String format(Object obj) {
        if (obj instanceof Date) {
            return format((Date) obj);
        }
        if (obj instanceof Calendar) {
            return format((Calendar) obj);
        }
        if (obj instanceof Long) {
            return format(((Long) obj).longValue());
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unknown class: ");
        stringBuilder.append(obj == null ? "<null>" : obj.getClass().getName());
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public String format(long millis) {
        Calendar c = newCalendar();
        c.setTimeInMillis(millis);
        return applyRulesToString(c);
    }

    private String applyRulesToString(Calendar c) {
        return ((StringBuilder) applyRules(c, new StringBuilder(this.mMaxLengthEstimate))).toString();
    }

    private Calendar newCalendar() {
        return Calendar.getInstance(this.mTimeZone, this.mLocale);
    }

    public String format(Date date) {
        Calendar c = newCalendar();
        c.setTime(date);
        return applyRulesToString(c);
    }

    public String format(Calendar calendar) {
        return ((StringBuilder) format(calendar, new StringBuilder(this.mMaxLengthEstimate))).toString();
    }

    public StringBuffer format(long millis, StringBuffer buf) {
        Calendar c = newCalendar();
        c.setTimeInMillis(millis);
        return (StringBuffer) applyRules(c, (Appendable) buf);
    }

    public StringBuffer format(Date date, StringBuffer buf) {
        Calendar c = newCalendar();
        c.setTime(date);
        return (StringBuffer) applyRules(c, (Appendable) buf);
    }

    public StringBuffer format(Calendar calendar, StringBuffer buf) {
        return format(calendar.getTime(), buf);
    }

    public <B extends Appendable> B format(long millis, B buf) {
        Calendar c = newCalendar();
        c.setTimeInMillis(millis);
        return applyRules(c, (Appendable) buf);
    }

    public <B extends Appendable> B format(Date date, B buf) {
        Calendar c = newCalendar();
        c.setTime(date);
        return applyRules(c, (Appendable) buf);
    }

    public <B extends Appendable> B format(Calendar calendar, B buf) {
        if (!calendar.getTimeZone().equals(this.mTimeZone)) {
            calendar = (Calendar) calendar.clone();
            calendar.setTimeZone(this.mTimeZone);
        }
        return applyRules(calendar, (Appendable) buf);
    }

    @Deprecated
    protected StringBuffer applyRules(Calendar calendar, StringBuffer buf) {
        return (StringBuffer) applyRules(calendar, (Appendable) buf);
    }

    private <B extends Appendable> B applyRules(Calendar calendar, B buf) {
        try {
            for (Rule rule : this.mRules) {
                rule.appendTo(buf, calendar);
            }
        } catch (IOException ioe) {
            ExceptionUtils.rethrow(ioe);
        }
        return buf;
    }

    public String getPattern() {
        return this.mPattern;
    }

    public TimeZone getTimeZone() {
        return this.mTimeZone;
    }

    public Locale getLocale() {
        return this.mLocale;
    }

    public int getMaxLengthEstimate() {
        return this.mMaxLengthEstimate;
    }

    public boolean equals(Object obj) {
        boolean z = false;
        if (!(obj instanceof FastDatePrinter)) {
            return false;
        }
        FastDatePrinter other = (FastDatePrinter) obj;
        if (this.mPattern.equals(other.mPattern) && this.mTimeZone.equals(other.mTimeZone) && this.mLocale.equals(other.mLocale)) {
            z = true;
        }
        return z;
    }

    public int hashCode() {
        return this.mPattern.hashCode() + ((this.mTimeZone.hashCode() + (this.mLocale.hashCode() * 13)) * 13);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("FastDatePrinter[");
        stringBuilder.append(this.mPattern);
        stringBuilder.append(",");
        stringBuilder.append(this.mLocale);
        stringBuilder.append(",");
        stringBuilder.append(this.mTimeZone.getID());
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        init();
    }

    private static void appendDigits(Appendable buffer, int value) throws IOException {
        buffer.append((char) ((value / 10) + 48));
        buffer.append((char) ((value % 10) + 48));
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void appendFullDigits(java.lang.Appendable r5, int r6, int r7) throws java.io.IOException {
        /*
        r0 = 10;
        r1 = 48;
        r2 = 10000; // 0x2710 float:1.4013E-41 double:4.9407E-320;
        if (r6 >= r2) goto L_0x005f;
    L_0x0008:
        r2 = 4;
        r3 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r4 = 100;
        if (r6 >= r3) goto L_0x001c;
    L_0x000f:
        r2 = r2 + -1;
        if (r6 >= r4) goto L_0x001b;
    L_0x0013:
        r2 = r2 + -1;
        if (r6 >= r0) goto L_0x001a;
    L_0x0017:
        r2 = r2 + -1;
        goto L_0x001d;
    L_0x001a:
        goto L_0x001d;
    L_0x001b:
        goto L_0x001d;
    L_0x001d:
        r3 = r7 - r2;
    L_0x001f:
        if (r3 <= 0) goto L_0x0027;
    L_0x0021:
        r5.append(r1);
        r3 = r3 + -1;
        goto L_0x001f;
    L_0x0027:
        switch(r2) {
            case 1: goto L_0x0057;
            case 2: goto L_0x0046;
            case 3: goto L_0x0035;
            case 4: goto L_0x002b;
            default: goto L_0x002a;
        };
    L_0x002a:
        goto L_0x005e;
    L_0x002b:
        r3 = r6 / 1000;
        r3 = r3 + r1;
        r3 = (char) r3;
        r5.append(r3);
        r6 = r6 % 1000;
        goto L_0x0036;
    L_0x0036:
        if (r6 < r4) goto L_0x0042;
    L_0x0038:
        r3 = r6 / 100;
        r3 = r3 + r1;
        r3 = (char) r3;
        r5.append(r3);
        r6 = r6 % 100;
        goto L_0x0047;
    L_0x0042:
        r5.append(r1);
        goto L_0x0047;
    L_0x0047:
        if (r6 < r0) goto L_0x0053;
    L_0x0049:
        r0 = r6 / 10;
        r0 = r0 + r1;
        r0 = (char) r0;
        r5.append(r0);
        r6 = r6 % 10;
        goto L_0x0058;
    L_0x0053:
        r5.append(r1);
        goto L_0x0058;
    L_0x0058:
        r0 = r6 + 48;
        r0 = (char) r0;
        r5.append(r0);
    L_0x005e:
        goto L_0x0085;
    L_0x005f:
        r0 = new char[r0];
        r2 = 0;
    L_0x0062:
        if (r6 == 0) goto L_0x0070;
    L_0x0064:
        r3 = r2 + 1;
        r4 = r6 % 10;
        r4 = r4 + r1;
        r4 = (char) r4;
        r0[r2] = r4;
        r6 = r6 / 10;
        r2 = r3;
        goto L_0x0062;
    L_0x0071:
        if (r2 >= r7) goto L_0x0079;
    L_0x0073:
        r5.append(r1);
        r7 = r7 + -1;
        goto L_0x0071;
    L_0x007a:
        r2 = r2 + -1;
        if (r2 < 0) goto L_0x0084;
    L_0x007e:
        r1 = r0[r2];
        r5.append(r1);
        goto L_0x007a;
    L_0x0085:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.time.FastDatePrinter.appendFullDigits(java.lang.Appendable, int, int):void");
    }

    static String getTimeZoneDisplay(TimeZone tz, boolean daylight, int style, Locale locale) {
        TimeZoneDisplayKey key = new TimeZoneDisplayKey(tz, daylight, style, locale);
        String value = (String) cTimeZoneDisplayCache.get(key);
        if (value != null) {
            return value;
        }
        value = tz.getDisplayName(daylight, style, locale);
        String prior = (String) cTimeZoneDisplayCache.putIfAbsent(key, value);
        if (prior != null) {
            return prior;
        }
        return value;
    }
}
