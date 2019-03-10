package de.danoeh.antennapod.core.util;

import android.content.Context;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.commons.lang3.time.TimeZones;

public class DateUtils {
    private static final String TAG = "DateUtils";
    private static final TimeZone defaultTimezone = TimeZone.getTimeZone(TimeZones.GMT_ID);

    public static java.util.Date parse(java.lang.String r12) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:44:0x0207 in {8, 13, 14, 19, 20, 21, 22, 31, 32, 34, 35, 39, 41, 43} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        if (r12 == 0) goto L_0x01ff;
    L_0x0002:
        r0 = r12.trim();
        r1 = 47;
        r2 = 45;
        r0 = r0.replace(r1, r2);
        r1 = "( ){2,}+";
        r2 = " ";
        r0 = r0.replaceAll(r1, r2);
        r1 = "CEST$";
        r2 = "+02:00";
        r0 = r0.replaceAll(r1, r2);
        r1 = "CET$";
        r2 = "+01:00";
        r0 = r0.replaceAll(r1, r2);
        r1 = "\\bSept\\b";
        r2 = "Sep";
        r0 = r0.replaceAll(r1, r2);
        r1 = ".";
        r1 = r0.contains(r1);
        r2 = 4;
        r3 = 1;
        r4 = 0;
        if (r1 == 0) goto L_0x00d0;
    L_0x0039:
        r1 = 46;
        r1 = r0.indexOf(r1);
        r5 = r1 + 1;
    L_0x0041:
        r6 = r0.length();
        if (r5 >= r6) goto L_0x0054;
    L_0x0047:
        r6 = r0.charAt(r5);
        r6 = java.lang.Character.isDigit(r6);
        if (r6 == 0) goto L_0x0054;
    L_0x0051:
        r5 = r5 + 1;
        goto L_0x0041;
        r6 = r5 - r1;
        if (r6 <= r2) goto L_0x0081;
    L_0x0059:
        r6 = r0.length();
        r6 = r6 - r3;
        if (r5 >= r6) goto L_0x007a;
    L_0x0060:
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r7 = r1 + 4;
        r7 = r0.substring(r4, r7);
        r6.append(r7);
        r7 = r0.substring(r5);
        r6.append(r7);
        r0 = r6.toString();
        goto L_0x00d1;
    L_0x007a:
        r6 = r1 + 4;
        r0 = r0.substring(r4, r6);
        goto L_0x00d1;
    L_0x0081:
        r6 = r5 - r1;
        if (r6 >= r2) goto L_0x00cf;
    L_0x0085:
        r6 = r0.length();
        r6 = r6 - r3;
        if (r5 >= r6) goto L_0x00b1;
    L_0x008c:
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r7 = r0.substring(r4, r5);
        r6.append(r7);
        r7 = "0";
        r8 = r5 - r1;
        r8 = 4 - r8;
        r7 = org.apache.commons.lang3.StringUtils.repeat(r7, r8);
        r6.append(r7);
        r7 = r0.substring(r5);
        r6.append(r7);
        r0 = r6.toString();
        goto L_0x00d1;
    L_0x00b1:
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r7 = r0.substring(r4, r5);
        r6.append(r7);
        r7 = "0";
        r8 = r5 - r1;
        r8 = 4 - r8;
        r7 = org.apache.commons.lang3.StringUtils.repeat(r7, r8);
        r6.append(r7);
        r0 = r6.toString();
        goto L_0x00d1;
    L_0x00cf:
        goto L_0x00d1;
    L_0x00d1:
        r1 = 29;
        r1 = new java.lang.String[r1];
        r5 = "dd MMM yy HH:mm:ss Z";
        r1[r4] = r5;
        r5 = "dd MMM yy HH:mm Z";
        r1[r3] = r5;
        r5 = 2;
        r6 = "EEE, dd MMM yyyy HH:mm:ss Z";
        r1[r5] = r6;
        r5 = 3;
        r6 = "EEE, dd MMM yyyy HH:mm:ss";
        r1[r5] = r6;
        r5 = "EEE, dd MMMM yyyy HH:mm:ss Z";
        r1[r2] = r5;
        r2 = 5;
        r5 = "EEE, dd MMMM yyyy HH:mm:ss";
        r1[r2] = r5;
        r2 = 6;
        r5 = "EEEE, dd MMM yyyy HH:mm:ss Z";
        r1[r2] = r5;
        r2 = 7;
        r5 = "EEEE, dd MMM yy HH:mm:ss Z";
        r1[r2] = r5;
        r2 = 8;
        r5 = "EEEE, dd MMM yyyy HH:mm:ss";
        r1[r2] = r5;
        r2 = 9;
        r5 = "EEEE, dd MMM yy HH:mm:ss";
        r1[r2] = r5;
        r2 = 10;
        r5 = "EEE MMM d HH:mm:ss yyyy";
        r1[r2] = r5;
        r2 = 11;
        r5 = "EEE, dd MMM yyyy HH:mm Z";
        r1[r2] = r5;
        r2 = 12;
        r5 = "EEE, dd MMM yyyy HH:mm";
        r1[r2] = r5;
        r2 = 13;
        r5 = "EEE, dd MMMM yyyy HH:mm Z";
        r1[r2] = r5;
        r2 = 14;
        r5 = "EEE, dd MMMM yyyy HH:mm";
        r1[r2] = r5;
        r2 = 15;
        r5 = "EEEE, dd MMM yyyy HH:mm Z";
        r1[r2] = r5;
        r2 = 16;
        r5 = "EEEE, dd MMM yy HH:mm Z";
        r1[r2] = r5;
        r2 = 17;
        r5 = "EEEE, dd MMM yyyy HH:mm";
        r1[r2] = r5;
        r2 = 18;
        r5 = "EEEE, dd MMM yy HH:mm";
        r1[r2] = r5;
        r2 = 19;
        r5 = "EEE MMM d HH:mm yyyy";
        r1[r2] = r5;
        r2 = 20;
        r5 = "yyyy-MM-dd'T'HH:mm:ss";
        r1[r2] = r5;
        r2 = 21;
        r5 = "yyyy-MM-dd'T'HH:mm:ss.SSS Z";
        r1[r2] = r5;
        r2 = 22;
        r5 = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        r1[r2] = r5;
        r2 = 23;
        r5 = "yyyy-MM-dd'T'HH:mm:ssZ";
        r1[r2] = r5;
        r2 = 24;
        r5 = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        r1[r2] = r5;
        r2 = 25;
        r5 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
        r1[r2] = r5;
        r2 = 26;
        r5 = "yyyy-MM-ddZ";
        r1[r2] = r5;
        r2 = 27;
        r5 = "yyyy-MM-dd";
        r1[r2] = r5;
        r2 = 28;
        r5 = "EEE d MMM yyyy HH:mm:ss 'GMT'Z (z)";
        r1[r2] = r5;
        r2 = new java.text.SimpleDateFormat;
        r5 = "";
        r6 = java.util.Locale.US;
        r2.<init>(r5, r6);
        r2.setLenient(r4);
        r5 = defaultTimezone;
        r2.setTimeZone(r5);
        r5 = new java.text.ParsePosition;
        r5.<init>(r4);
        r6 = r1.length;
        r7 = 0;
    L_0x0198:
        if (r7 >= r6) goto L_0x01c2;
    L_0x019a:
        r8 = r1[r7];
        r2.applyPattern(r8);
        r5.setIndex(r4);
        r9 = r2.parse(r0, r5);	 Catch:{ Exception -> 0x01b5 }
        if (r9 == 0) goto L_0x01b3;	 Catch:{ Exception -> 0x01b5 }
    L_0x01a8:
        r10 = r5.getIndex();	 Catch:{ Exception -> 0x01b5 }
        r11 = r0.length();	 Catch:{ Exception -> 0x01b5 }
        if (r10 != r11) goto L_0x01b3;
    L_0x01b2:
        return r9;
        goto L_0x01bf;
    L_0x01b5:
        r9 = move-exception;
        r10 = "DateUtils";
        r11 = android.util.Log.getStackTraceString(r9);
        android.util.Log.e(r10, r11);
    L_0x01bf:
        r7 = r7 + 1;
        goto L_0x0198;
    L_0x01c2:
        r4 = "^\\w+, .*$";
        r4 = r0.matches(r4);
        if (r4 == 0) goto L_0x01da;
    L_0x01ca:
        r4 = 44;
        r4 = r0.indexOf(r4);
        r4 = r4 + r3;
        r3 = r0.substring(r4);
        r3 = parse(r3);
        return r3;
    L_0x01da:
        r3 = "DateUtils";
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r6 = "Could not parse date string \"";
        r4.append(r6);
        r4.append(r12);
        r6 = "\" [";
        r4.append(r6);
        r4.append(r0);
        r6 = "]";
        r4.append(r6);
        r4 = r4.toString();
        android.util.Log.d(r3, r4);
        r3 = 0;
        return r3;
    L_0x01ff:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "Date must not be null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.core.util.DateUtils.parse(java.lang.String):java.util.Date");
    }

    private DateUtils() {
    }

    public static long parseTimeString(String time) {
        String[] parts = time.split(":");
        long result = 0;
        int idx = 0;
        if (parts.length == 3) {
            result = 0 + (((long) Integer.parseInt(parts[0])) * org.apache.commons.lang3.time.DateUtils.MILLIS_PER_HOUR);
            idx = 0 + 1;
        }
        if (parts.length >= 2) {
            return (result + (((long) Integer.parseInt(parts[idx])) * 60000)) + ((long) (Float.parseFloat(parts[idx + 1]) * 1000.0f));
        }
        return result;
    }

    public static String formatRFC822Date(Date date) {
        return new SimpleDateFormat("dd MMM yy HH:mm:ss Z", Locale.US).format(date);
    }

    public static String formatRFC3339Local(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US).format(date);
    }

    public static String formatRFC3339UTC(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        format.setTimeZone(defaultTimezone);
        return format.format(date);
    }

    public static String formatAbbrev(Context context, Date date) {
        if (date == null) {
            return "";
        }
        GregorianCalendar cal = new GregorianCalendar();
        cal.add(1, -1);
        cal.add(5, 10);
        int format = 524288;
        if (date.after(cal.getTime())) {
            format = 524288 | 8;
        }
        return android.text.format.DateUtils.formatDateTime(context, date.getTime(), format);
    }
}
