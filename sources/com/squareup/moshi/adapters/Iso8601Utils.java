package com.squareup.moshi.adapters;

import com.squareup.moshi.JsonDataException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

final class Iso8601Utils {
    static final String GMT_ID = "GMT";
    static final TimeZone TIMEZONE_Z = TimeZone.getTimeZone("GMT");

    private static int parseInt(java.lang.String r6, int r7, int r8) throws java.lang.NumberFormatException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:22:0x0072 in {8, 10, 11, 15, 17, 19, 21} preds:[]
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
        if (r7 < 0) goto L_0x006b;
    L_0x0002:
        r0 = r6.length();
        if (r8 > r0) goto L_0x006b;
    L_0x0008:
        if (r7 > r8) goto L_0x006b;
    L_0x000a:
        r0 = r7;
        r1 = 0;
        r2 = 10;
        if (r0 >= r8) goto L_0x003a;
    L_0x0010:
        r3 = r0 + 1;
        r0 = r6.charAt(r0);
        r0 = java.lang.Character.digit(r0, r2);
        if (r0 < 0) goto L_0x001f;
    L_0x001c:
        r1 = -r0;
        r0 = r3;
        goto L_0x003b;
    L_0x001f:
        r2 = new java.lang.NumberFormatException;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "Invalid number: ";
        r4.append(r5);
        r5 = r6.substring(r7, r8);
        r4.append(r5);
        r4 = r4.toString();
        r2.<init>(r4);
        throw r2;
    L_0x003b:
        if (r0 >= r8) goto L_0x0069;
    L_0x003d:
        r3 = r0 + 1;
        r0 = r6.charAt(r0);
        r0 = java.lang.Character.digit(r0, r2);
        if (r0 < 0) goto L_0x004e;
    L_0x0049:
        r1 = r1 * 10;
        r1 = r1 - r0;
        r0 = r3;
        goto L_0x003b;
    L_0x004e:
        r2 = new java.lang.NumberFormatException;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "Invalid number: ";
        r4.append(r5);
        r5 = r6.substring(r7, r8);
        r4.append(r5);
        r4 = r4.toString();
        r2.<init>(r4);
        throw r2;
    L_0x0069:
        r2 = -r1;
        return r2;
        r0 = new java.lang.NumberFormatException;
        r0.<init>(r6);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.squareup.moshi.adapters.Iso8601Utils.parseInt(java.lang.String, int, int):int");
    }

    Iso8601Utils() {
    }

    public static String format(Date date) {
        Calendar calendar = new GregorianCalendar(TIMEZONE_Z, Locale.US);
        calendar.setTime(date);
        StringBuilder formatted = new StringBuilder("yyyy-MM-ddThh:mm:ss.sssZ".length());
        padInt(formatted, calendar.get(1), "yyyy".length());
        formatted.append('-');
        padInt(formatted, calendar.get(2) + 1, "MM".length());
        formatted.append('-');
        padInt(formatted, calendar.get(5), "dd".length());
        formatted.append('T');
        padInt(formatted, calendar.get(11), "hh".length());
        formatted.append(':');
        padInt(formatted, calendar.get(12), "mm".length());
        formatted.append(':');
        padInt(formatted, calendar.get(13), "ss".length());
        formatted.append('.');
        padInt(formatted, calendar.get(14), "sss".length());
        formatted.append('Z');
        return formatted.toString();
    }

    public static Date parse(String date) {
        String str = date;
        int month = 0 + 4;
        StringBuilder stringBuilder;
        try {
            int year = parseInt(str, 0, month);
            if (checkOffset(str, month, '-')) {
                month++;
            }
            int day = month + 2;
            month = parseInt(str, month, day);
            if (checkOffset(str, day, '-')) {
                day++;
            }
            int offset = day + 2;
            day = parseInt(str, day, offset);
            int hour = 0;
            int minutes = 0;
            int seconds = 0;
            int milliseconds = 0;
            boolean hasT = checkOffset(str, offset, true);
            if (!hasT && date.length() <= offset) {
                return new GregorianCalendar(year, month - 1, day).getTime();
            }
            int day2;
            if (hasT) {
                offset++;
                int offset2 = offset + 2;
                hour = parseInt(str, offset, offset2);
                if (checkOffset(str, offset2, ':')) {
                    offset2++;
                }
                int offset3 = offset2 + 2;
                minutes = parseInt(str, offset2, offset3);
                if (checkOffset(str, offset3, ':')) {
                    offset = offset3 + 1;
                } else {
                    offset = offset3;
                }
                if (date.length() > offset) {
                    char c = str.charAt(offset);
                    char c2;
                    if (c == 'Z' || c == '+' || c == '-') {
                        day2 = day;
                        c2 = c;
                    } else {
                        offset3 = offset + 2;
                        offset = parseInt(str, offset, offset3);
                        if (offset <= 59 || offset >= 63) {
                            seconds = offset;
                        } else {
                            seconds = 59;
                        }
                        if (checkOffset(str, offset3, '.')) {
                            offset3++;
                            offset = indexOfNonDigit(str, offset3 + 1);
                            int parseEndOffset = Math.min(offset, offset3 + 3);
                            int fraction = parseInt(str, offset3, parseEndOffset);
                            day2 = day;
                            double pow = Math.pow(10.0d, (double) (3 - (parseEndOffset - offset3)));
                            double d = (double) fraction;
                            Double.isNaN(d);
                            milliseconds = (int) (pow * d);
                            int i = offset;
                        } else {
                            day2 = day;
                            c2 = c;
                            offset = offset3;
                        }
                    }
                } else {
                    day2 = day;
                }
            } else {
                day2 = day;
            }
            if (date.length() > offset) {
                TimeZone timezone;
                char timezoneIndicator = str.charAt(offset);
                char c3;
                if (timezoneIndicator == 'Z') {
                    timezone = TIMEZONE_Z;
                    c3 = timezoneIndicator;
                } else {
                    if (timezoneIndicator != '+') {
                        if (timezoneIndicator != '-') {
                            StringBuilder stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("Invalid time zone indicator '");
                            stringBuilder2.append(timezoneIndicator);
                            stringBuilder2.append("'");
                            throw new IndexOutOfBoundsException(stringBuilder2.toString());
                        }
                    }
                    String timezoneOffset = str.substring(offset);
                    String str2;
                    if ("+0000".equals(timezoneOffset)) {
                        str2 = timezoneOffset;
                    } else if ("+00:00".equals(timezoneOffset)) {
                        c3 = timezoneIndicator;
                        str2 = timezoneOffset;
                    } else {
                        String timezoneId = new StringBuilder();
                        timezoneId.append("GMT");
                        timezoneId.append(timezoneOffset);
                        timezoneId = timezoneId.toString();
                        TimeZone timezone2 = TimeZone.getTimeZone(timezoneId);
                        String act = timezone2.getID();
                        if (act.equals(timezoneId)) {
                            str2 = timezoneOffset;
                        } else if (act.replace(":", "").equals(timezoneId)) {
                            c3 = timezoneIndicator;
                        } else {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("Mismatching time zone indicator: ");
                            stringBuilder.append(timezoneId);
                            stringBuilder.append(" given, resolves to ");
                            stringBuilder.append(timezone2.getID());
                            throw new IndexOutOfBoundsException(stringBuilder.toString());
                        }
                        timezone = timezone2;
                    }
                    timezone = TIMEZONE_Z;
                }
                Calendar calendar = new GregorianCalendar(timezone);
                calendar.setLenient(false);
                calendar.set(1, year);
                calendar.set(2, month - 1);
                calendar.set(5, day2);
                calendar.set(11, hour);
                calendar.set(12, minutes);
                calendar.set(13, seconds);
                calendar.set(14, milliseconds);
                return calendar.getTime();
            }
            throw new IllegalArgumentException("No time zone indicator");
        } catch (RuntimeException e) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("Not an RFC 3339 date: ");
            stringBuilder.append(str);
            throw new JsonDataException(stringBuilder.toString(), e);
        }
    }

    private static boolean checkOffset(String value, int offset, char expected) {
        return offset < value.length() && value.charAt(offset) == expected;
    }

    private static void padInt(StringBuilder buffer, int value, int length) {
        String strValue = Integer.toString(value);
        for (int i = length - strValue.length(); i > 0; i--) {
            buffer.append('0');
        }
        buffer.append(strValue);
    }

    private static int indexOfNonDigit(String string, int offset) {
        int i = offset;
        while (i < string.length()) {
            char c = string.charAt(i);
            if (c >= '0') {
                if (c <= '9') {
                    i++;
                }
            }
            return i;
        }
        return string.length();
    }
}
