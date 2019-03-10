package org.apache.commons.lang3.time;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class DurationFormatUtils {
    /* renamed from: H */
    static final Object f72H = "H";
    public static final String ISO_EXTENDED_FORMAT_PATTERN = "'P'yyyy'Y'M'M'd'DT'H'H'm'M's.SSS'S'";
    /* renamed from: M */
    static final Object f73M = "M";
    /* renamed from: S */
    static final Object f74S = "S";
    /* renamed from: d */
    static final Object f75d = "d";
    /* renamed from: m */
    static final Object f76m = "m";
    /* renamed from: s */
    static final Object f77s = "s";
    /* renamed from: y */
    static final Object f78y = "y";

    static class Token {
        private int count;
        private final Object value;

        static boolean containsTokenWithValue(Token[] tokens, Object value) {
            for (Token token : tokens) {
                if (token.getValue() == value) {
                    return true;
                }
            }
            return false;
        }

        Token(Object value) {
            this.value = value;
            this.count = 1;
        }

        Token(Object value, int count) {
            this.value = value;
            this.count = count;
        }

        void increment() {
            this.count++;
        }

        int getCount() {
            return this.count;
        }

        Object getValue() {
            return this.value;
        }

        public boolean equals(Object obj2) {
            boolean z = false;
            if (!(obj2 instanceof Token)) {
                return false;
            }
            Token tok2 = (Token) obj2;
            if (this.value.getClass() != tok2.value.getClass() || this.count != tok2.count) {
                return false;
            }
            Object obj = this.value;
            if (obj instanceof StringBuilder) {
                return obj.toString().equals(tok2.value.toString());
            }
            if (obj instanceof Number) {
                return obj.equals(tok2.value);
            }
            if (obj == tok2.value) {
                z = true;
            }
            return z;
        }

        public int hashCode() {
            return this.value.hashCode();
        }

        public String toString() {
            return StringUtils.repeat(this.value.toString(), this.count);
        }
    }

    static org.apache.commons.lang3.time.DurationFormatUtils.Token[] lexx(java.lang.String r8) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:51:0x00cb in {6, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 35, 36, 41, 42, 43, 44, 45, 48, 50} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r0 = new java.util.ArrayList;
        r1 = r8.length();
        r0.<init>(r1);
        r1 = 0;
        r2 = 0;
        r3 = 0;
        r4 = 0;
    L_0x000d:
        r5 = r8.length();
        if (r4 >= r5) goto L_0x00a5;
    L_0x0013:
        r5 = r8.charAt(r4);
        r6 = 39;
        if (r1 == 0) goto L_0x0022;
    L_0x001b:
        if (r5 == r6) goto L_0x0022;
    L_0x001d:
        r2.append(r5);
        goto L_0x00a1;
        r7 = 0;
        if (r5 == r6) goto L_0x006d;
    L_0x0026:
        r6 = 72;
        if (r5 == r6) goto L_0x006a;
    L_0x002a:
        r6 = 77;
        if (r5 == r6) goto L_0x0067;
    L_0x002e:
        r6 = 83;
        if (r5 == r6) goto L_0x0064;
    L_0x0032:
        r6 = 100;
        if (r5 == r6) goto L_0x0061;
    L_0x0036:
        r6 = 109; // 0x6d float:1.53E-43 double:5.4E-322;
        if (r5 == r6) goto L_0x005e;
    L_0x003a:
        r6 = 115; // 0x73 float:1.61E-43 double:5.7E-322;
        if (r5 == r6) goto L_0x005b;
    L_0x003e:
        r6 = 121; // 0x79 float:1.7E-43 double:6.0E-322;
        if (r5 == r6) goto L_0x0058;
    L_0x0042:
        if (r2 != 0) goto L_0x0053;
    L_0x0044:
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r2 = r6;
        r6 = new org.apache.commons.lang3.time.DurationFormatUtils$Token;
        r6.<init>(r2);
        r0.add(r6);
        goto L_0x0054;
    L_0x0054:
        r2.append(r5);
        goto L_0x0082;
    L_0x0058:
        r7 = f78y;
        goto L_0x0082;
    L_0x005b:
        r7 = f77s;
        goto L_0x0082;
    L_0x005e:
        r7 = f76m;
        goto L_0x0082;
    L_0x0061:
        r7 = f75d;
        goto L_0x0082;
    L_0x0064:
        r7 = f74S;
        goto L_0x0082;
    L_0x0067:
        r7 = f73M;
        goto L_0x0082;
    L_0x006a:
        r7 = f72H;
        goto L_0x0082;
    L_0x006d:
        if (r1 == 0) goto L_0x0072;
    L_0x006f:
        r2 = 0;
        r1 = 0;
        goto L_0x0082;
    L_0x0072:
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r2 = r6;
        r6 = new org.apache.commons.lang3.time.DurationFormatUtils$Token;
        r6.<init>(r2);
        r0.add(r6);
        r1 = 1;
    L_0x0082:
        if (r7 == 0) goto L_0x00a0;
    L_0x0084:
        if (r3 == 0) goto L_0x0094;
    L_0x0086:
        r6 = r3.getValue();
        r6 = r6.equals(r7);
        if (r6 == 0) goto L_0x0094;
    L_0x0090:
        r3.increment();
        goto L_0x009e;
        r6 = new org.apache.commons.lang3.time.DurationFormatUtils$Token;
        r6.<init>(r7);
        r0.add(r6);
        r3 = r6;
    L_0x009e:
        r2 = 0;
        goto L_0x00a1;
    L_0x00a1:
        r4 = r4 + 1;
        goto L_0x000d;
    L_0x00a5:
        if (r1 != 0) goto L_0x00b4;
    L_0x00a7:
        r4 = r0.size();
        r4 = new org.apache.commons.lang3.time.DurationFormatUtils.Token[r4];
        r4 = r0.toArray(r4);
        r4 = (org.apache.commons.lang3.time.DurationFormatUtils.Token[]) r4;
        return r4;
    L_0x00b4:
        r4 = new java.lang.IllegalArgumentException;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "Unmatched quote in format: ";
        r5.append(r6);
        r5.append(r8);
        r5 = r5.toString();
        r4.<init>(r5);
        throw r4;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.time.DurationFormatUtils.lexx(java.lang.String):org.apache.commons.lang3.time.DurationFormatUtils$Token[]");
    }

    public static String formatDurationHMS(long durationMillis) {
        return formatDuration(durationMillis, "HH:mm:ss.SSS");
    }

    public static String formatDurationISO(long durationMillis) {
        return formatDuration(durationMillis, ISO_EXTENDED_FORMAT_PATTERN, false);
    }

    public static String formatDuration(long durationMillis, String format) {
        return formatDuration(durationMillis, format, true);
    }

    public static String formatDuration(long durationMillis, String format, boolean padWithZeros) {
        long seconds;
        long milliseconds;
        Validate.inclusiveBetween(0, Long.MAX_VALUE, durationMillis, "durationMillis must not be negative");
        Token[] tokens = lexx(format);
        long days = 0;
        long hours = 0;
        long minutes = 0;
        long milliseconds2 = durationMillis;
        if (Token.containsTokenWithValue(tokens, f75d)) {
            days = milliseconds2 / DateUtils.MILLIS_PER_DAY;
            milliseconds2 -= DateUtils.MILLIS_PER_DAY * days;
        }
        if (Token.containsTokenWithValue(tokens, f72H)) {
            hours = milliseconds2 / DateUtils.MILLIS_PER_HOUR;
            milliseconds2 -= DateUtils.MILLIS_PER_HOUR * hours;
        }
        if (Token.containsTokenWithValue(tokens, f76m)) {
            minutes = milliseconds2 / 60000;
            milliseconds2 -= 60000 * minutes;
        }
        if (Token.containsTokenWithValue(tokens, f77s)) {
            long seconds2 = milliseconds2 / 1000;
            seconds = seconds2;
            milliseconds = milliseconds2 - (1000 * seconds2);
        } else {
            seconds = 0;
            milliseconds = milliseconds2;
        }
        return format(tokens, 0, 0, days, hours, minutes, seconds, milliseconds, padWithZeros);
    }

    public static String formatDurationWords(long durationMillis, boolean suppressLeadingZeroElements, boolean suppressTrailingZeroElements) {
        StringBuilder stringBuilder;
        String tmp;
        String duration = formatDuration(durationMillis, "d' days 'H' hours 'm' minutes 's' seconds'");
        if (suppressLeadingZeroElements) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(StringUtils.SPACE);
            stringBuilder.append(duration);
            duration = stringBuilder.toString();
            tmp = StringUtils.replaceOnce(duration, " 0 days", "");
            if (tmp.length() != duration.length()) {
                duration = tmp;
                tmp = StringUtils.replaceOnce(duration, " 0 hours", "");
                if (tmp.length() != duration.length()) {
                    tmp = StringUtils.replaceOnce(tmp, " 0 minutes", "");
                    duration = tmp;
                    if (tmp.length() != duration.length()) {
                        duration = StringUtils.replaceOnce(tmp, " 0 seconds", "");
                    }
                }
            }
            if (duration.length() != 0) {
                duration = duration.substring(1);
            }
        }
        if (suppressTrailingZeroElements) {
            tmp = StringUtils.replaceOnce(duration, " 0 seconds", "");
            if (tmp.length() != duration.length()) {
                duration = tmp;
                tmp = StringUtils.replaceOnce(duration, " 0 minutes", "");
                if (tmp.length() != duration.length()) {
                    duration = tmp;
                    tmp = StringUtils.replaceOnce(duration, " 0 hours", "");
                    if (tmp.length() != duration.length()) {
                        duration = StringUtils.replaceOnce(tmp, " 0 days", "");
                    }
                }
            }
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append(StringUtils.SPACE);
        stringBuilder.append(duration);
        return StringUtils.replaceOnce(StringUtils.replaceOnce(StringUtils.replaceOnce(StringUtils.replaceOnce(stringBuilder.toString(), " 1 seconds", " 1 second"), " 1 minutes", " 1 minute"), " 1 hours", " 1 hour"), " 1 days", " 1 day").trim();
    }

    public static String formatPeriodISO(long startMillis, long endMillis) {
        return formatPeriod(startMillis, endMillis, ISO_EXTENDED_FORMAT_PATTERN, false, TimeZone.getDefault());
    }

    public static String formatPeriod(long startMillis, long endMillis, String format) {
        return formatPeriod(startMillis, endMillis, format, true, TimeZone.getDefault());
    }

    public static String formatPeriod(long startMillis, long endMillis, String format, boolean padWithZeros, TimeZone timezone) {
        int months;
        long j = startMillis;
        long j2 = endMillis;
        Validate.isTrue(j <= j2, "startMillis must not be greater than endMillis", new Object[0]);
        Token[] tokens = lexx(format);
        Calendar start = Calendar.getInstance(timezone);
        start.setTime(new Date(j));
        Calendar end = Calendar.getInstance(timezone);
        end.setTime(new Date(j2));
        int milliseconds = end.get(14) - start.get(14);
        int seconds = end.get(13) - start.get(13);
        int minutes = end.get(12) - start.get(12);
        int hours = end.get(11) - start.get(11);
        int days = end.get(5) - start.get(5);
        int i = 2;
        int months2 = end.get(2) - start.get(2);
        int years = end.get(1) - start.get(1);
        while (milliseconds < 0) {
            milliseconds += 1000;
            seconds--;
        }
        while (seconds < 0) {
            seconds += 60;
            minutes--;
        }
        while (minutes < 0) {
            minutes += 60;
            hours--;
        }
        while (hours < 0) {
            hours += 24;
            days--;
        }
        if (Token.containsTokenWithValue(tokens, f73M)) {
            while (days < 0) {
                days += start.getActualMaximum(5);
                months2--;
                start.add(2, 1);
            }
            while (months2 < 0) {
                months2 += 12;
                years--;
            }
            if (Token.containsTokenWithValue(tokens, f78y) || years == 0) {
                months = months2;
                months2 = years;
            } else {
                while (years != 0) {
                    months2 += years * 12;
                    years = 0;
                }
                months = months2;
                months2 = years;
            }
        } else {
            if (!Token.containsTokenWithValue(tokens, f78y)) {
                int days2;
                months = 1;
                int target = end.get(1);
                if (months2 < 0) {
                    days2 = days;
                    days = target - 1;
                } else {
                    days2 = days;
                    days = target;
                }
                while (start.get(months) != days) {
                    days2 += start.getActualMaximum(6) - start.get(6);
                    if ((start instanceof GregorianCalendar) && start.get(r14) == 1 && start.get(5) == 29) {
                        days2++;
                    }
                    start.add(1, 1);
                    days2 += start.get(6);
                    months = 1;
                    i = 2;
                }
                years = 0;
                days = days2;
            }
            while (start.get(2) != end.get(2)) {
                days += start.getActualMaximum(5);
                start.add(2, 1);
            }
            months2 = 0;
            while (days < 0) {
                days += start.getActualMaximum(5);
                months2--;
                start.add(2, 1);
            }
            months = months2;
            months2 = years;
        }
        if (!Token.containsTokenWithValue(tokens, f75d)) {
            hours += days * 24;
            days = 0;
        }
        if (Token.containsTokenWithValue(tokens, f72H)) {
            i = hours;
        } else {
            minutes += hours * 60;
            i = 0;
        }
        if (!Token.containsTokenWithValue(tokens, f76m)) {
            seconds += minutes * 60;
            minutes = 0;
        }
        if (Token.containsTokenWithValue(tokens, f77s)) {
            hours = seconds;
        } else {
            milliseconds += seconds * 1000;
            hours = 0;
        }
        j2 = (long) months;
        long j3 = (long) minutes;
        long j4 = j2;
        return format(tokens, (long) months2, j4, (long) days, (long) i, j3, (long) hours, (long) milliseconds, padWithZeros);
    }

    static String format(Token[] tokens, long years, long months, long days, long hours, long minutes, long seconds, long milliseconds, boolean padWithZeros) {
        long j;
        long j2;
        boolean z;
        Token[] arr$;
        int len$;
        long j3 = milliseconds;
        boolean z2 = padWithZeros;
        StringBuilder buffer = new StringBuilder();
        boolean lastOutputSeconds = false;
        Token[] arr$2 = tokens;
        int len$2 = arr$2.length;
        int i$ = 0;
        while (i$ < len$2) {
            Token token = arr$2[i$];
            Object value = token.getValue();
            int count = token.getCount();
            if (value instanceof StringBuilder) {
                buffer.append(value.toString());
                j = years;
                j2 = months;
                z = lastOutputSeconds;
                arr$ = arr$2;
                len$ = len$2;
                arr$2 = seconds;
            } else {
                if (value.equals(f78y)) {
                    buffer.append(paddedValue(years, z2, count));
                    lastOutputSeconds = false;
                    j2 = months;
                    arr$ = arr$2;
                    len$ = len$2;
                    arr$2 = seconds;
                } else {
                    j = years;
                    if (value.equals(f73M)) {
                        buffer.append(paddedValue(months, z2, count));
                        lastOutputSeconds = false;
                        arr$ = arr$2;
                        len$ = len$2;
                        arr$2 = seconds;
                    } else {
                        j2 = months;
                        long j4;
                        if (value.equals(f75d)) {
                            arr$ = arr$2;
                            len$ = len$2;
                            buffer.append(paddedValue(days, z2, count));
                            lastOutputSeconds = false;
                            j4 = seconds;
                        } else {
                            arr$ = arr$2;
                            len$ = len$2;
                            Token token2 = token;
                            arr$2 = days;
                            if (value.equals(f72H)) {
                                buffer.append(paddedValue(hours, z2, count));
                                lastOutputSeconds = false;
                                j4 = seconds;
                            } else {
                                j4 = hours;
                                if (value.equals(f76m)) {
                                    buffer.append(paddedValue(minutes, z2, count));
                                    lastOutputSeconds = false;
                                    j4 = seconds;
                                } else {
                                    j4 = minutes;
                                    if (value.equals(f77s)) {
                                        buffer.append(paddedValue(seconds, z2, count));
                                        lastOutputSeconds = true;
                                    } else {
                                        j4 = seconds;
                                        if (value.equals(f74S)) {
                                            if (lastOutputSeconds) {
                                                int width = 3;
                                                if (z2) {
                                                    width = Math.max(3, count);
                                                }
                                                buffer.append(paddedValue(j3, true, width));
                                            } else {
                                                buffer.append(paddedValue(j3, z2, count));
                                            }
                                            lastOutputSeconds = false;
                                        } else {
                                            z = lastOutputSeconds;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                i$++;
                arr$2 = arr$;
                len$2 = len$;
            }
            lastOutputSeconds = z;
            i$++;
            arr$2 = arr$;
            len$2 = len$;
        }
        j = years;
        j2 = months;
        z = lastOutputSeconds;
        arr$ = arr$2;
        len$ = len$2;
        arr$2 = seconds;
        return buffer.toString();
    }

    private static String paddedValue(long value, boolean padWithZeros, int count) {
        String longString = Long.toString(value);
        return padWithZeros ? StringUtils.leftPad(longString, count, '0') : longString;
    }
}
