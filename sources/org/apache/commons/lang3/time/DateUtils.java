package org.apache.commons.lang3.time;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.Validate;

public class DateUtils {
    public static final long MILLIS_PER_DAY = 86400000;
    public static final long MILLIS_PER_HOUR = 3600000;
    public static final long MILLIS_PER_MINUTE = 60000;
    public static final long MILLIS_PER_SECOND = 1000;
    public static final int RANGE_MONTH_MONDAY = 6;
    public static final int RANGE_MONTH_SUNDAY = 5;
    public static final int RANGE_WEEK_CENTER = 4;
    public static final int RANGE_WEEK_MONDAY = 2;
    public static final int RANGE_WEEK_RELATIVE = 3;
    public static final int RANGE_WEEK_SUNDAY = 1;
    public static final int SEMI_MONTH = 1001;
    private static final int[][] fields;

    static class DateIterator implements Iterator<Calendar> {
        private final Calendar endFinal;
        private final Calendar spot;

        DateIterator(Calendar startFinal, Calendar endFinal) {
            this.endFinal = endFinal;
            this.spot = startFinal;
            this.spot.add(5, -1);
        }

        public boolean hasNext() {
            return this.spot.before(this.endFinal);
        }

        public Calendar next() {
            if (this.spot.equals(this.endFinal)) {
                throw new NoSuchElementException();
            }
            this.spot.add(5, 1);
            return (Calendar) this.spot.clone();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private enum ModifyType {
        TRUNCATE,
        ROUND,
        CEILING
    }

    public static java.util.Iterator<java.util.Calendar> iterator(java.util.Calendar r10, int r11) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:39:0x00a1 in {4, 7, 8, 11, 12, 13, 14, 15, 17, 18, 20, 21, 23, 24, 26, 27, 30, 34, 36, 38} preds:[]
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
        if (r10 == 0) goto L_0x0099;
    L_0x0002:
        r0 = 0;
        r1 = 0;
        r2 = 1;
        r3 = 7;
        r4 = -1;
        r5 = 1;
        r6 = 5;
        r7 = 7;
        switch(r11) {
            case 1: goto L_0x0042;
            case 2: goto L_0x0042;
            case 3: goto L_0x0042;
            case 4: goto L_0x0042;
            case 5: goto L_0x0029;
            case 6: goto L_0x0029;
            default: goto L_0x000d;
        };
    L_0x000d:
        r4 = new java.lang.IllegalArgumentException;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "The range style ";
        r5.append(r6);
        r5.append(r11);
        r6 = " is not valid.";
        r5.append(r6);
        r5 = r5.toString();
        r4.<init>(r5);
        throw r4;
    L_0x0029:
        r8 = 2;
        r0 = truncate(r10, r8);
        r9 = r0.clone();
        r1 = r9;
        r1 = (java.util.Calendar) r1;
        r1.add(r8, r5);
        r1.add(r6, r4);
        r8 = 6;
        if (r11 != r8) goto L_0x0041;
    L_0x003e:
        r2 = 2;
        r3 = 1;
        goto L_0x0066;
    L_0x0041:
        goto L_0x0066;
    L_0x0042:
        r0 = truncate(r10, r6);
        r1 = truncate(r10, r6);
        switch(r11) {
            case 1: goto L_0x0065;
            case 2: goto L_0x0062;
            case 3: goto L_0x005b;
            case 4: goto L_0x004e;
            default: goto L_0x004d;
        };
    L_0x004d:
        goto L_0x0066;
    L_0x004e:
        r8 = r10.get(r7);
        r2 = r8 + -3;
        r8 = r10.get(r7);
        r3 = r8 + 3;
        goto L_0x0066;
    L_0x005b:
        r2 = r10.get(r7);
        r3 = r2 + -1;
        goto L_0x0066;
    L_0x0062:
        r2 = 2;
        r3 = 1;
        goto L_0x0066;
    L_0x0066:
        if (r2 >= r5) goto L_0x006b;
    L_0x0068:
        r2 = r2 + 7;
        goto L_0x006c;
    L_0x006c:
        if (r2 <= r7) goto L_0x0071;
    L_0x006e:
        r2 = r2 + -7;
        goto L_0x0072;
    L_0x0072:
        if (r3 >= r5) goto L_0x0077;
    L_0x0074:
        r3 = r3 + 7;
        goto L_0x0078;
    L_0x0078:
        if (r3 <= r7) goto L_0x007d;
    L_0x007a:
        r3 = r3 + -7;
        goto L_0x007e;
    L_0x007e:
        r8 = r0.get(r7);
        if (r8 == r2) goto L_0x0088;
    L_0x0084:
        r0.add(r6, r4);
        goto L_0x007e;
    L_0x0089:
        r4 = r1.get(r7);
        if (r4 == r3) goto L_0x0093;
    L_0x008f:
        r1.add(r6, r5);
        goto L_0x0089;
    L_0x0093:
        r4 = new org.apache.commons.lang3.time.DateUtils$DateIterator;
        r4.<init>(r0, r1);
        return r4;
    L_0x0099:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "The date must not be null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.time.DateUtils.iterator(java.util.Calendar, int):java.util.Iterator<java.util.Calendar>");
    }

    private static void modify(java.util.Calendar r23, int r24, org.apache.commons.lang3.time.DateUtils.ModifyType r25) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:111:0x01a9 in {4, 9, 10, 11, 14, 15, 20, 21, 22, 25, 26, 31, 32, 33, 36, 37, 50, 51, 56, 57, 62, 63, 64, 65, 66, 71, 76, 77, 80, 81, 82, 83, 88, 89, 92, 93, 94, 95, 99, 100, 101, 102, 104, 105, 106, 108, 110} preds:[]
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
        r0 = r23;
        r1 = r24;
        r2 = r25;
        r3 = 1;
        r4 = r0.get(r3);
        r5 = 280000000; // 0x10b07600 float:6.960157E-29 double:1.38338381E-315;
        if (r4 > r5) goto L_0x01a1;
    L_0x0010:
        r4 = 14;
        if (r1 != r4) goto L_0x0015;
    L_0x0014:
        return;
    L_0x0015:
        r5 = r23.getTime();
        r6 = r5.getTime();
        r8 = 0;
        r4 = r0.get(r4);
        r9 = org.apache.commons.lang3.time.DateUtils.ModifyType.TRUNCATE;
        if (r9 == r2) goto L_0x002c;
    L_0x0026:
        r9 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        if (r4 >= r9) goto L_0x002b;
    L_0x002a:
        goto L_0x002c;
    L_0x002b:
        goto L_0x002e;
    L_0x002c:
        r9 = (long) r4;
        r6 = r6 - r9;
    L_0x002e:
        r9 = 13;
        if (r1 != r9) goto L_0x0034;
    L_0x0032:
        r8 = 1;
        goto L_0x0035;
    L_0x0035:
        r9 = r0.get(r9);
        r10 = 30;
        if (r8 != 0) goto L_0x004a;
    L_0x003d:
        r11 = org.apache.commons.lang3.time.DateUtils.ModifyType.TRUNCATE;
        if (r11 == r2) goto L_0x0043;
    L_0x0041:
        if (r9 >= r10) goto L_0x004a;
    L_0x0043:
        r11 = (long) r9;
        r13 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r11 = r11 * r13;
        r6 = r6 - r11;
        goto L_0x004b;
    L_0x004b:
        r11 = 12;
        if (r1 != r11) goto L_0x0051;
    L_0x004f:
        r8 = 1;
        goto L_0x0052;
    L_0x0052:
        r12 = r0.get(r11);
        if (r8 != 0) goto L_0x0066;
    L_0x0058:
        r13 = org.apache.commons.lang3.time.DateUtils.ModifyType.TRUNCATE;
        if (r13 == r2) goto L_0x005e;
    L_0x005c:
        if (r12 >= r10) goto L_0x0066;
    L_0x005e:
        r13 = (long) r12;
        r15 = 60000; // 0xea60 float:8.4078E-41 double:2.9644E-319;
        r13 = r13 * r15;
        r6 = r6 - r13;
        goto L_0x0067;
    L_0x0067:
        r13 = r5.getTime();
        r10 = (r13 > r6 ? 1 : (r13 == r6 ? 0 : -1));
        if (r10 == 0) goto L_0x0076;
    L_0x006f:
        r5.setTime(r6);
        r0.setTime(r5);
        goto L_0x0077;
    L_0x0077:
        r10 = 0;
        r13 = fields;
        r14 = r13.length;
        r15 = 0;
    L_0x007c:
        if (r15 >= r14) goto L_0x0181;
    L_0x007e:
        r16 = r13[r15];
        r17 = r16;
        r11 = r17;
        r3 = r11.length;
        r18 = 0;
        r19 = r4;
        r4 = r18;
    L_0x008b:
        r18 = r5;
        r21 = 0;
        if (r4 >= r3) goto L_0x00ef;
    L_0x0091:
        r5 = r11[r4];
        if (r5 != r1) goto L_0x00e6;
    L_0x0095:
        r22 = r3;
        r3 = org.apache.commons.lang3.time.DateUtils.ModifyType.CEILING;
        if (r2 == r3) goto L_0x00a3;
    L_0x009b:
        r3 = org.apache.commons.lang3.time.DateUtils.ModifyType.ROUND;
        if (r2 != r3) goto L_0x00a2;
    L_0x009f:
        if (r10 == 0) goto L_0x00a2;
    L_0x00a1:
        goto L_0x00a3;
    L_0x00a2:
        goto L_0x00e5;
    L_0x00a3:
        r3 = 1001; // 0x3e9 float:1.403E-42 double:4.946E-321;
        if (r1 != r3) goto L_0x00c2;
    L_0x00a7:
        r3 = 5;
        r2 = r0.get(r3);
        r3 = 1;
        if (r2 != r3) goto L_0x00b6;
    L_0x00af:
        r2 = 5;
        r3 = 15;
        r0.add(r2, r3);
        goto L_0x00e5;
    L_0x00b6:
        r2 = 5;
        r3 = -15;
        r0.add(r2, r3);
        r2 = 1;
        r3 = 2;
        r0.add(r3, r2);
        goto L_0x00e5;
    L_0x00c2:
        r2 = 9;
        if (r1 != r2) goto L_0x00df;
    L_0x00c6:
        r2 = 11;
        r3 = r0.get(r2);
        if (r3 != 0) goto L_0x00d4;
    L_0x00ce:
        r3 = 12;
        r0.add(r2, r3);
        goto L_0x00e5;
    L_0x00d4:
        r3 = -12;
        r0.add(r2, r3);
        r2 = 1;
        r3 = 5;
        r0.add(r3, r2);
        goto L_0x00e5;
    L_0x00df:
        r2 = 1;
        r3 = r16[r21];
        r0.add(r3, r2);
    L_0x00e5:
        return;
    L_0x00e6:
        r22 = r3;
        r4 = r4 + 1;
        r5 = r18;
        r2 = r25;
        goto L_0x008b;
    L_0x00ef:
        r22 = r3;
        r2 = 0;
        r3 = 0;
        r4 = 9;
        if (r1 == r4) goto L_0x0122;
    L_0x00f7:
        r4 = 1001; // 0x3e9 float:1.403E-42 double:4.946E-321;
        if (r1 == r4) goto L_0x00ff;
    L_0x00fb:
        r4 = 12;
        r5 = 1;
        goto L_0x0140;
    L_0x00ff:
        r4 = r16[r21];
        r5 = 5;
        if (r4 != r5) goto L_0x011e;
    L_0x0104:
        r4 = r0.get(r5);
        r5 = 1;
        r4 = r4 - r5;
        r2 = 15;
        if (r4 < r2) goto L_0x0112;
    L_0x010e:
        r4 = r4 + -15;
        r2 = r4;
        goto L_0x0113;
    L_0x0112:
        r2 = r4;
    L_0x0113:
        r4 = 7;
        if (r2 <= r4) goto L_0x0118;
    L_0x0116:
        r4 = 1;
        goto L_0x0119;
    L_0x0118:
        r4 = 0;
    L_0x0119:
        r10 = r4;
        r3 = 1;
        r4 = 12;
        goto L_0x0140;
    L_0x011e:
        r5 = 1;
        r4 = 12;
        goto L_0x0140;
    L_0x0122:
        r5 = 1;
        r4 = r16[r21];
        r11 = 11;
        if (r4 != r11) goto L_0x013e;
    L_0x0129:
        r2 = r0.get(r11);
        r4 = 12;
        if (r2 < r4) goto L_0x0134;
    L_0x0131:
        r2 = r2 + -12;
        goto L_0x0135;
    L_0x0135:
        r11 = 6;
        if (r2 < r11) goto L_0x013a;
    L_0x0138:
        r11 = 1;
        goto L_0x013b;
    L_0x013a:
        r11 = 0;
    L_0x013b:
        r10 = r11;
        r3 = 1;
        goto L_0x0140;
    L_0x013e:
        r4 = 12;
    L_0x0140:
        if (r3 != 0) goto L_0x0163;
    L_0x0142:
        r11 = r16[r21];
        r11 = r0.getActualMinimum(r11);
        r4 = r16[r21];
        r4 = r0.getActualMaximum(r4);
        r5 = r16[r21];
        r5 = r0.get(r5);
        r2 = r5 - r11;
        r5 = r4 - r11;
        r20 = 2;
        r5 = r5 / 2;
        if (r2 <= r5) goto L_0x0160;
    L_0x015e:
        r5 = 1;
        goto L_0x0161;
    L_0x0160:
        r5 = 0;
    L_0x0161:
        r10 = r5;
        goto L_0x0164;
    L_0x0164:
        if (r2 == 0) goto L_0x0173;
    L_0x0166:
        r4 = r16[r21];
        r5 = r16[r21];
        r5 = r0.get(r5);
        r5 = r5 - r2;
        r0.set(r4, r5);
        goto L_0x0174;
    L_0x0174:
        r15 = r15 + 1;
        r5 = r18;
        r4 = r19;
        r2 = r25;
        r3 = 1;
        r11 = 12;
        goto L_0x007c;
    L_0x0181:
        r19 = r4;
        r18 = r5;
        r2 = new java.lang.IllegalArgumentException;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "The field ";
        r3.append(r4);
        r3.append(r1);
        r4 = " is not supported";
        r3.append(r4);
        r3 = r3.toString();
        r2.<init>(r3);
        throw r2;
    L_0x01a1:
        r2 = new java.lang.ArithmeticException;
        r3 = "Calendar value too large for accurate calculations";
        r2.<init>(r3);
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.time.DateUtils.modify(java.util.Calendar, int, org.apache.commons.lang3.time.DateUtils$ModifyType):void");
    }

    private static java.util.Date parseDateWithLeniency(java.lang.String r12, java.util.Locale r13, java.lang.String[] r14, boolean r15) throws java.text.ParseException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:23:0x006b in {4, 5, 15, 16, 17, 18, 20, 22} preds:[]
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
        if (r12 == 0) goto L_0x0062;
    L_0x0002:
        if (r14 == 0) goto L_0x0062;
    L_0x0004:
        r0 = java.util.TimeZone.getDefault();
        if (r13 != 0) goto L_0x000f;
    L_0x000a:
        r1 = java.util.Locale.getDefault();
        goto L_0x0010;
    L_0x000f:
        r1 = r13;
    L_0x0010:
        r2 = new java.text.ParsePosition;
        r3 = 0;
        r2.<init>(r3);
        r4 = java.util.Calendar.getInstance(r0, r1);
        r4.setLenient(r15);
        r5 = r14;
        r6 = r5.length;
        r7 = 0;
    L_0x0020:
        if (r7 >= r6) goto L_0x004a;
    L_0x0022:
        r8 = r5[r7];
        r9 = new org.apache.commons.lang3.time.FastDateParser;
        r9.<init>(r8, r0, r1);
        r4.clear();
        r10 = r9.parse(r12, r2, r4);	 Catch:{ IllegalArgumentException -> 0x0043 }
        if (r10 == 0) goto L_0x0041;	 Catch:{ IllegalArgumentException -> 0x0043 }
    L_0x0032:
        r10 = r2.getIndex();	 Catch:{ IllegalArgumentException -> 0x0043 }
        r11 = r12.length();	 Catch:{ IllegalArgumentException -> 0x0043 }
        if (r10 != r11) goto L_0x0041;	 Catch:{ IllegalArgumentException -> 0x0043 }
    L_0x003c:
        r3 = r4.getTime();	 Catch:{ IllegalArgumentException -> 0x0043 }
        return r3;
        goto L_0x0044;
    L_0x0043:
        r10 = move-exception;
    L_0x0044:
        r2.setIndex(r3);
        r7 = r7 + 1;
        goto L_0x0020;
    L_0x004a:
        r3 = new java.text.ParseException;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "Unable to parse the date: ";
        r5.append(r6);
        r5.append(r12);
        r5 = r5.toString();
        r6 = -1;
        r3.<init>(r5, r6);
        throw r3;
        r0 = new java.lang.IllegalArgumentException;
        r1 = "Date and Patterns must not be null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.time.DateUtils.parseDateWithLeniency(java.lang.String, java.util.Locale, java.lang.String[], boolean):java.util.Date");
    }

    static {
        r0 = new int[8][];
        r0[0] = new int[]{14};
        r0[1] = new int[]{13};
        r0[2] = new int[]{12};
        r0[3] = new int[]{11, 10};
        r0[4] = new int[]{5, 5, 9};
        r0[5] = new int[]{2, 1001};
        r0[6] = new int[]{1};
        r0[7] = new int[]{0};
        fields = r0;
    }

    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameDay(cal1, cal2);
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 != null && cal2 != null) {
            return cal1.get(0) == cal2.get(0) && cal1.get(1) == cal2.get(1) && cal1.get(6) == cal2.get(6);
        } else {
            throw new IllegalArgumentException("The date must not be null");
        }
    }

    public static boolean isSameInstant(Date date1, Date date2) {
        if (date1 != null && date2 != null) {
            return date1.getTime() == date2.getTime();
        } else {
            throw new IllegalArgumentException("The date must not be null");
        }
    }

    public static boolean isSameInstant(Calendar cal1, Calendar cal2) {
        if (cal1 != null && cal2 != null) {
            return cal1.getTime().getTime() == cal2.getTime().getTime();
        } else {
            throw new IllegalArgumentException("The date must not be null");
        }
    }

    public static boolean isSameLocalTime(Calendar cal1, Calendar cal2) {
        if (cal1 != null && cal2 != null) {
            return cal1.get(14) == cal2.get(14) && cal1.get(13) == cal2.get(13) && cal1.get(12) == cal2.get(12) && cal1.get(11) == cal2.get(11) && cal1.get(6) == cal2.get(6) && cal1.get(1) == cal2.get(1) && cal1.get(0) == cal2.get(0) && cal1.getClass() == cal2.getClass();
        } else {
            throw new IllegalArgumentException("The date must not be null");
        }
    }

    public static Date parseDate(String str, String... parsePatterns) throws ParseException {
        return parseDate(str, null, parsePatterns);
    }

    public static Date parseDate(String str, Locale locale, String... parsePatterns) throws ParseException {
        return parseDateWithLeniency(str, locale, parsePatterns, true);
    }

    public static Date parseDateStrictly(String str, String... parsePatterns) throws ParseException {
        return parseDateStrictly(str, null, parsePatterns);
    }

    public static Date parseDateStrictly(String str, Locale locale, String... parsePatterns) throws ParseException {
        return parseDateWithLeniency(str, locale, parsePatterns, false);
    }

    public static Date addYears(Date date, int amount) {
        return add(date, 1, amount);
    }

    public static Date addMonths(Date date, int amount) {
        return add(date, 2, amount);
    }

    public static Date addWeeks(Date date, int amount) {
        return add(date, 3, amount);
    }

    public static Date addDays(Date date, int amount) {
        return add(date, 5, amount);
    }

    public static Date addHours(Date date, int amount) {
        return add(date, 11, amount);
    }

    public static Date addMinutes(Date date, int amount) {
        return add(date, 12, amount);
    }

    public static Date addSeconds(Date date, int amount) {
        return add(date, 13, amount);
    }

    public static Date addMilliseconds(Date date, int amount) {
        return add(date, 14, amount);
    }

    private static Date add(Date date, int calendarField, int amount) {
        validateDateNotNull(date);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }

    public static Date setYears(Date date, int amount) {
        return set(date, 1, amount);
    }

    public static Date setMonths(Date date, int amount) {
        return set(date, 2, amount);
    }

    public static Date setDays(Date date, int amount) {
        return set(date, 5, amount);
    }

    public static Date setHours(Date date, int amount) {
        return set(date, 11, amount);
    }

    public static Date setMinutes(Date date, int amount) {
        return set(date, 12, amount);
    }

    public static Date setSeconds(Date date, int amount) {
        return set(date, 13, amount);
    }

    public static Date setMilliseconds(Date date, int amount) {
        return set(date, 14, amount);
    }

    private static Date set(Date date, int calendarField, int amount) {
        validateDateNotNull(date);
        Calendar c = Calendar.getInstance();
        c.setLenient(false);
        c.setTime(date);
        c.set(calendarField, amount);
        return c.getTime();
    }

    public static Calendar toCalendar(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c;
    }

    public static Calendar toCalendar(Date date, TimeZone tz) {
        Calendar c = Calendar.getInstance(tz);
        c.setTime(date);
        return c;
    }

    public static Date round(Date date, int field) {
        validateDateNotNull(date);
        Calendar gval = Calendar.getInstance();
        gval.setTime(date);
        modify(gval, field, ModifyType.ROUND);
        return gval.getTime();
    }

    public static Calendar round(Calendar date, int field) {
        if (date != null) {
            Calendar rounded = (Calendar) date.clone();
            modify(rounded, field, ModifyType.ROUND);
            return rounded;
        }
        throw new IllegalArgumentException("The date must not be null");
    }

    public static Date round(Object date, int field) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        } else if (date instanceof Date) {
            return round((Date) date, field);
        } else {
            if (date instanceof Calendar) {
                return round((Calendar) date, field).getTime();
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Could not round ");
            stringBuilder.append(date);
            throw new ClassCastException(stringBuilder.toString());
        }
    }

    public static Date truncate(Date date, int field) {
        validateDateNotNull(date);
        Calendar gval = Calendar.getInstance();
        gval.setTime(date);
        modify(gval, field, ModifyType.TRUNCATE);
        return gval.getTime();
    }

    public static Calendar truncate(Calendar date, int field) {
        if (date != null) {
            Calendar truncated = (Calendar) date.clone();
            modify(truncated, field, ModifyType.TRUNCATE);
            return truncated;
        }
        throw new IllegalArgumentException("The date must not be null");
    }

    public static Date truncate(Object date, int field) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        } else if (date instanceof Date) {
            return truncate((Date) date, field);
        } else {
            if (date instanceof Calendar) {
                return truncate((Calendar) date, field).getTime();
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Could not truncate ");
            stringBuilder.append(date);
            throw new ClassCastException(stringBuilder.toString());
        }
    }

    public static Date ceiling(Date date, int field) {
        validateDateNotNull(date);
        Calendar gval = Calendar.getInstance();
        gval.setTime(date);
        modify(gval, field, ModifyType.CEILING);
        return gval.getTime();
    }

    public static Calendar ceiling(Calendar date, int field) {
        if (date != null) {
            Calendar ceiled = (Calendar) date.clone();
            modify(ceiled, field, ModifyType.CEILING);
            return ceiled;
        }
        throw new IllegalArgumentException("The date must not be null");
    }

    public static Date ceiling(Object date, int field) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        } else if (date instanceof Date) {
            return ceiling((Date) date, field);
        } else {
            if (date instanceof Calendar) {
                return ceiling((Calendar) date, field).getTime();
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Could not find ceiling of for type: ");
            stringBuilder.append(date.getClass());
            throw new ClassCastException(stringBuilder.toString());
        }
    }

    public static Iterator<Calendar> iterator(Date focus, int rangeStyle) {
        validateDateNotNull(focus);
        Calendar gval = Calendar.getInstance();
        gval.setTime(focus);
        return iterator(gval, rangeStyle);
    }

    public static Iterator<?> iterator(Object focus, int rangeStyle) {
        if (focus == null) {
            throw new IllegalArgumentException("The date must not be null");
        } else if (focus instanceof Date) {
            return iterator((Date) focus, rangeStyle);
        } else {
            if (focus instanceof Calendar) {
                return iterator((Calendar) focus, rangeStyle);
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Could not iterate based on ");
            stringBuilder.append(focus);
            throw new ClassCastException(stringBuilder.toString());
        }
    }

    public static long getFragmentInMilliseconds(Date date, int fragment) {
        return getFragment(date, fragment, TimeUnit.MILLISECONDS);
    }

    public static long getFragmentInSeconds(Date date, int fragment) {
        return getFragment(date, fragment, TimeUnit.SECONDS);
    }

    public static long getFragmentInMinutes(Date date, int fragment) {
        return getFragment(date, fragment, TimeUnit.MINUTES);
    }

    public static long getFragmentInHours(Date date, int fragment) {
        return getFragment(date, fragment, TimeUnit.HOURS);
    }

    public static long getFragmentInDays(Date date, int fragment) {
        return getFragment(date, fragment, TimeUnit.DAYS);
    }

    public static long getFragmentInMilliseconds(Calendar calendar, int fragment) {
        return getFragment(calendar, fragment, TimeUnit.MILLISECONDS);
    }

    public static long getFragmentInSeconds(Calendar calendar, int fragment) {
        return getFragment(calendar, fragment, TimeUnit.SECONDS);
    }

    public static long getFragmentInMinutes(Calendar calendar, int fragment) {
        return getFragment(calendar, fragment, TimeUnit.MINUTES);
    }

    public static long getFragmentInHours(Calendar calendar, int fragment) {
        return getFragment(calendar, fragment, TimeUnit.HOURS);
    }

    public static long getFragmentInDays(Calendar calendar, int fragment) {
        return getFragment(calendar, fragment, TimeUnit.DAYS);
    }

    private static long getFragment(Date date, int fragment, TimeUnit unit) {
        validateDateNotNull(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return getFragment(calendar, fragment, unit);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static long getFragment(java.util.Calendar r6, int r7, java.util.concurrent.TimeUnit r8) {
        /*
        if (r6 == 0) goto L_0x008a;
    L_0x0002:
        r0 = 0;
        r2 = java.util.concurrent.TimeUnit.DAYS;
        if (r8 != r2) goto L_0x000a;
    L_0x0008:
        r2 = 0;
        goto L_0x000b;
    L_0x000a:
        r2 = 1;
    L_0x000b:
        switch(r7) {
            case 1: goto L_0x001e;
            case 2: goto L_0x000f;
            default: goto L_0x000e;
        };
    L_0x000e:
        goto L_0x002d;
    L_0x000f:
        r3 = 5;
        r3 = r6.get(r3);
        r3 = r3 - r2;
        r3 = (long) r3;
        r5 = java.util.concurrent.TimeUnit.DAYS;
        r3 = r8.convert(r3, r5);
        r0 = r0 + r3;
        goto L_0x002d;
    L_0x001e:
        r3 = 6;
        r3 = r6.get(r3);
        r3 = r3 - r2;
        r3 = (long) r3;
        r5 = java.util.concurrent.TimeUnit.DAYS;
        r3 = r8.convert(r3, r5);
        r0 = r0 + r3;
    L_0x002d:
        switch(r7) {
            case 1: goto L_0x0050;
            case 2: goto L_0x0050;
            case 3: goto L_0x0030;
            case 4: goto L_0x0030;
            case 5: goto L_0x0050;
            case 6: goto L_0x0050;
            case 7: goto L_0x0030;
            case 8: goto L_0x0030;
            case 9: goto L_0x0030;
            case 10: goto L_0x0030;
            case 11: goto L_0x004f;
            case 12: goto L_0x004e;
            case 13: goto L_0x004d;
            case 14: goto L_0x004c;
            default: goto L_0x0030;
        };
    L_0x0030:
        r3 = new java.lang.IllegalArgumentException;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "The fragment ";
        r4.append(r5);
        r4.append(r7);
        r5 = " is not supported";
        r4.append(r5);
        r4 = r4.toString();
        r3.<init>(r4);
        throw r3;
    L_0x004c:
        goto L_0x0089;
    L_0x004d:
        goto L_0x007a;
    L_0x004e:
        goto L_0x006c;
    L_0x004f:
        goto L_0x005e;
    L_0x0050:
        r3 = 11;
        r3 = r6.get(r3);
        r3 = (long) r3;
        r5 = java.util.concurrent.TimeUnit.HOURS;
        r3 = r8.convert(r3, r5);
        r0 = r0 + r3;
    L_0x005e:
        r3 = 12;
        r3 = r6.get(r3);
        r3 = (long) r3;
        r5 = java.util.concurrent.TimeUnit.MINUTES;
        r3 = r8.convert(r3, r5);
        r0 = r0 + r3;
    L_0x006c:
        r3 = 13;
        r3 = r6.get(r3);
        r3 = (long) r3;
        r5 = java.util.concurrent.TimeUnit.SECONDS;
        r3 = r8.convert(r3, r5);
        r0 = r0 + r3;
    L_0x007a:
        r3 = 14;
        r3 = r6.get(r3);
        r3 = (long) r3;
        r5 = java.util.concurrent.TimeUnit.MILLISECONDS;
        r3 = r8.convert(r3, r5);
        r0 = r0 + r3;
    L_0x0089:
        return r0;
    L_0x008a:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "The date must not be null";
        r0.<init>(r1);
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.time.DateUtils.getFragment(java.util.Calendar, int, java.util.concurrent.TimeUnit):long");
    }

    public static boolean truncatedEquals(Calendar cal1, Calendar cal2, int field) {
        return truncatedCompareTo(cal1, cal2, field) == 0;
    }

    public static boolean truncatedEquals(Date date1, Date date2, int field) {
        return truncatedCompareTo(date1, date2, field) == 0;
    }

    public static int truncatedCompareTo(Calendar cal1, Calendar cal2, int field) {
        return truncate(cal1, field).compareTo(truncate(cal2, field));
    }

    public static int truncatedCompareTo(Date date1, Date date2, int field) {
        return truncate(date1, field).compareTo(truncate(date2, field));
    }

    private static void validateDateNotNull(Date date) {
        Validate.isTrue(date != null, "The date must not be null", new Object[0]);
    }
}
