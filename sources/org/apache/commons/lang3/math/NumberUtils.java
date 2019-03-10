package org.apache.commons.lang3.math;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class NumberUtils {
    public static final Byte BYTE_MINUS_ONE = Byte.valueOf((byte) -1);
    public static final Byte BYTE_ONE = Byte.valueOf((byte) 1);
    public static final Byte BYTE_ZERO = Byte.valueOf((byte) 0);
    public static final Double DOUBLE_MINUS_ONE = Double.valueOf(-1.0d);
    public static final Double DOUBLE_ONE = Double.valueOf(1.0d);
    public static final Double DOUBLE_ZERO = Double.valueOf(0.0d);
    public static final Float FLOAT_MINUS_ONE = Float.valueOf(-1.0f);
    public static final Float FLOAT_ONE = Float.valueOf(1.0f);
    public static final Float FLOAT_ZERO = Float.valueOf(0.0f);
    public static final Integer INTEGER_MINUS_ONE = Integer.valueOf(-1);
    public static final Integer INTEGER_ONE = Integer.valueOf(1);
    public static final Integer INTEGER_ZERO = Integer.valueOf(0);
    public static final Long LONG_MINUS_ONE = Long.valueOf(-1);
    public static final Long LONG_ONE = Long.valueOf(1);
    public static final Long LONG_ZERO = Long.valueOf(0);
    public static final Short SHORT_MINUS_ONE = Short.valueOf((short) -1);
    public static final Short SHORT_ONE = Short.valueOf((short) 1);
    public static final Short SHORT_ZERO = Short.valueOf((short) 0);

    public static java.lang.Number createNumber(java.lang.String r17) throws java.lang.NumberFormatException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:177:0x028b in {3, 10, 11, 12, 19, 20, 21, 27, 32, 34, 36, 38, 45, 47, 48, 49, 53, 55, 56, 57, 64, 65, 70, 71, 84, 85, 91, 93, 96, 99, 101, 108, 109, 110, 112, 113, 120, 121, 122, 123, 126, 127, 129, 134, 135, 140, 144, 147, 152, 158, 161, 166, 169, 170, 171, 172, 174, 176} preds:[]
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
        r1 = r17;
        if (r1 != 0) goto L_0x0006;
    L_0x0004:
        r0 = 0;
        return r0;
    L_0x0006:
        r0 = org.apache.commons.lang3.StringUtils.isBlank(r17);
        if (r0 != 0) goto L_0x0283;
    L_0x000c:
        r0 = 6;
        r0 = new java.lang.String[r0];
        r2 = "0x";
        r3 = 0;
        r0[r3] = r2;
        r2 = "0X";
        r4 = 1;
        r0[r4] = r2;
        r2 = 2;
        r5 = "-0x";
        r0[r2] = r5;
        r2 = 3;
        r5 = "-0X";
        r0[r2] = r5;
        r2 = 4;
        r5 = "#";
        r0[r2] = r5;
        r2 = 5;
        r5 = "-#";
        r0[r2] = r5;
        r2 = r0;
        r0 = 0;
        r5 = r2;
        r6 = r5.length;
        r7 = 0;
    L_0x0032:
        if (r7 >= r6) goto L_0x0047;
    L_0x0034:
        r8 = r5[r7];
        r9 = r1.startsWith(r8);
        if (r9 == 0) goto L_0x0043;
    L_0x003c:
        r9 = r8.length();
        r0 = r0 + r9;
        r5 = r0;
        goto L_0x0048;
        r7 = r7 + 1;
        goto L_0x0032;
    L_0x0047:
        r5 = r0;
    L_0x0048:
        if (r5 <= 0) goto L_0x008b;
    L_0x004a:
        r0 = 0;
        r3 = r5;
    L_0x004c:
        r4 = r17.length();
        if (r3 >= r4) goto L_0x0060;
    L_0x0052:
        r0 = r1.charAt(r3);
        r4 = 48;
        if (r0 != r4) goto L_0x005f;
    L_0x005a:
        r5 = r5 + 1;
        r3 = r3 + 1;
        goto L_0x004c;
    L_0x005f:
        goto L_0x0061;
    L_0x0061:
        r3 = r17.length();
        r3 = r3 - r5;
        r4 = 16;
        if (r3 > r4) goto L_0x0085;
    L_0x006a:
        r6 = 55;
        if (r3 != r4) goto L_0x0071;
    L_0x006e:
        if (r0 <= r6) goto L_0x0071;
    L_0x0070:
        goto L_0x0085;
    L_0x0071:
        r4 = 8;
        if (r3 > r4) goto L_0x007f;
    L_0x0075:
        if (r3 != r4) goto L_0x007a;
    L_0x0077:
        if (r0 <= r6) goto L_0x007a;
    L_0x0079:
        goto L_0x007f;
    L_0x007a:
        r4 = createInteger(r17);
        return r4;
        r4 = createLong(r17);
        return r4;
        r4 = createBigInteger(r17);
        return r4;
    L_0x008b:
        r0 = r17.length();
        r0 = r0 - r4;
        r6 = r1.charAt(r0);
        r0 = 46;
        r7 = r1.indexOf(r0);
        r8 = 101; // 0x65 float:1.42E-43 double:5.0E-322;
        r8 = r1.indexOf(r8);
        r9 = 69;
        r9 = r1.indexOf(r9);
        r8 = r8 + r9;
        r8 = r8 + r4;
        r9 = -1;
        if (r7 <= r9) goto L_0x00df;
    L_0x00ab:
        if (r8 <= r9) goto L_0x00d4;
    L_0x00ad:
        if (r8 < r7) goto L_0x00bc;
    L_0x00af:
        r10 = r17.length();
        if (r8 > r10) goto L_0x00bc;
    L_0x00b5:
        r10 = r7 + 1;
        r10 = r1.substring(r10, r8);
        goto L_0x00da;
        r0 = new java.lang.NumberFormatException;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r1);
        r4 = " is not a valid number.";
        r3.append(r4);
        r3 = r3.toString();
        r0.<init>(r3);
        throw r0;
    L_0x00d4:
        r10 = r7 + 1;
        r10 = r1.substring(r10);
    L_0x00da:
        r11 = getMantissa(r1, r7);
        goto L_0x010a;
    L_0x00df:
        if (r8 <= r9) goto L_0x0104;
    L_0x00e1:
        r10 = r17.length();
        if (r8 > r10) goto L_0x00ed;
    L_0x00e7:
        r10 = getMantissa(r1, r8);
        r11 = r10;
        goto L_0x0109;
    L_0x00ed:
        r0 = new java.lang.NumberFormatException;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r1);
        r4 = " is not a valid number.";
        r3.append(r4);
        r3 = r3.toString();
        r0.<init>(r3);
        throw r0;
    L_0x0104:
        r10 = getMantissa(r17);
        r11 = r10;
    L_0x0109:
        r10 = 0;
    L_0x010a:
        r12 = java.lang.Character.isDigit(r6);
        r13 = 0;
        r14 = 0;
        if (r12 != 0) goto L_0x01f2;
    L_0x0113:
        if (r6 == r0) goto L_0x01f2;
    L_0x0115:
        if (r8 <= r9) goto L_0x012b;
    L_0x0117:
        r0 = r17.length();
        r0 = r0 - r4;
        if (r8 >= r0) goto L_0x012b;
    L_0x011e:
        r0 = r8 + 1;
        r9 = r17.length();
        r9 = r9 - r4;
        r0 = r1.substring(r0, r9);
        r9 = r0;
        goto L_0x012e;
        r0 = 0;
        r9 = r0;
    L_0x012e:
        r0 = r17.length();
        r0 = r0 - r4;
        r12 = r1.substring(r3, r0);
        r0 = isAllZeros(r11);
        if (r0 == 0) goto L_0x0145;
    L_0x013d:
        r0 = isAllZeros(r9);
        if (r0 == 0) goto L_0x0145;
    L_0x0143:
        r0 = 1;
        goto L_0x0146;
    L_0x0145:
        r0 = 0;
    L_0x0146:
        r16 = r0;
        r0 = 68;
        if (r6 == r0) goto L_0x01bb;
    L_0x014c:
        r0 = 70;
        if (r6 == r0) goto L_0x01a2;
    L_0x0150:
        r0 = 76;
        if (r6 == r0) goto L_0x0163;
    L_0x0154:
        r0 = 100;
        if (r6 == r0) goto L_0x01bb;
    L_0x0158:
        r0 = 102; // 0x66 float:1.43E-43 double:5.04E-322;
        if (r6 == r0) goto L_0x01a2;
    L_0x015c:
        r0 = 108; // 0x6c float:1.51E-43 double:5.34E-322;
        if (r6 != r0) goto L_0x0161;
    L_0x0160:
        goto L_0x0163;
    L_0x0161:
        goto L_0x01db;
    L_0x0163:
        if (r10 != 0) goto L_0x018a;
    L_0x0165:
        if (r9 != 0) goto L_0x018a;
    L_0x0167:
        r0 = r12.charAt(r3);
        r3 = 45;
        if (r0 != r3) goto L_0x0179;
    L_0x016f:
        r0 = r12.substring(r4);
        r0 = isDigits(r0);
        if (r0 != 0) goto L_0x017f;
    L_0x0179:
        r0 = isDigits(r12);
        if (r0 == 0) goto L_0x018a;
    L_0x017f:
        r0 = createLong(r12);	 Catch:{ NumberFormatException -> 0x0184 }
        return r0;
    L_0x0184:
        r0 = move-exception;
        r0 = createBigInteger(r12);
        return r0;
        r0 = new java.lang.NumberFormatException;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r1);
        r4 = " is not a valid number.";
        r3.append(r4);
        r3 = r3.toString();
        r0.<init>(r3);
        throw r0;
    L_0x01a2:
        r0 = createFloat(r17);	 Catch:{ NumberFormatException -> 0x01b9 }
        r3 = r0.isInfinite();	 Catch:{ NumberFormatException -> 0x01b9 }
        if (r3 != 0) goto L_0x01b7;	 Catch:{ NumberFormatException -> 0x01b9 }
    L_0x01ac:
        r3 = r0.floatValue();	 Catch:{ NumberFormatException -> 0x01b9 }
        r3 = (r3 > r13 ? 1 : (r3 == r13 ? 0 : -1));
        if (r3 != 0) goto L_0x01b6;
    L_0x01b4:
        if (r16 == 0) goto L_0x01b7;
    L_0x01b6:
        return r0;
        goto L_0x01bc;
    L_0x01b9:
        r0 = move-exception;
        goto L_0x01bc;
    L_0x01bc:
        r0 = createDouble(r17);	 Catch:{ NumberFormatException -> 0x01d4 }
        r3 = r0.isInfinite();	 Catch:{ NumberFormatException -> 0x01d4 }
        if (r3 != 0) goto L_0x01d2;	 Catch:{ NumberFormatException -> 0x01d4 }
    L_0x01c6:
        r3 = r0.floatValue();	 Catch:{ NumberFormatException -> 0x01d4 }
        r3 = (double) r3;
        r13 = (r3 > r14 ? 1 : (r3 == r14 ? 0 : -1));
        if (r13 != 0) goto L_0x01d1;
    L_0x01cf:
        if (r16 == 0) goto L_0x01d2;
    L_0x01d1:
        return r0;
        goto L_0x01d5;
    L_0x01d4:
        r0 = move-exception;
    L_0x01d5:
        r0 = createBigDecimal(r12);	 Catch:{ NumberFormatException -> 0x01da }
        return r0;
    L_0x01da:
        r0 = move-exception;
    L_0x01db:
        r0 = new java.lang.NumberFormatException;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r1);
        r4 = " is not a valid number.";
        r3.append(r4);
        r3 = r3.toString();
        r0.<init>(r3);
        throw r0;
        if (r8 <= r9) goto L_0x0208;
    L_0x01f5:
        r0 = r17.length();
        r0 = r0 - r4;
        if (r8 >= r0) goto L_0x0208;
    L_0x01fc:
        r0 = r8 + 1;
        r9 = r17.length();
        r0 = r1.substring(r0, r9);
        r9 = r0;
        goto L_0x020b;
        r0 = 0;
        r9 = r0;
    L_0x020b:
        if (r10 != 0) goto L_0x0220;
    L_0x020d:
        if (r9 != 0) goto L_0x0220;
    L_0x020f:
        r0 = createInteger(r17);	 Catch:{ NumberFormatException -> 0x0214 }
        return r0;
    L_0x0214:
        r0 = move-exception;
        r0 = createLong(r17);	 Catch:{ NumberFormatException -> 0x021a }
        return r0;
    L_0x021a:
        r0 = move-exception;
        r0 = createBigInteger(r17);
        return r0;
        r0 = isAllZeros(r11);
        if (r0 == 0) goto L_0x022f;
    L_0x0227:
        r0 = isAllZeros(r9);
        if (r0 == 0) goto L_0x022f;
    L_0x022d:
        r3 = 1;
    L_0x022f:
        r0 = createFloat(r17);	 Catch:{ NumberFormatException -> 0x027d }
        r4 = createDouble(r17);	 Catch:{ NumberFormatException -> 0x027d }
        r12 = r0.isInfinite();	 Catch:{ NumberFormatException -> 0x027d }
        if (r12 != 0) goto L_0x0256;	 Catch:{ NumberFormatException -> 0x027d }
    L_0x023d:
        r12 = r0.floatValue();	 Catch:{ NumberFormatException -> 0x027d }
        r12 = (r12 > r13 ? 1 : (r12 == r13 ? 0 : -1));	 Catch:{ NumberFormatException -> 0x027d }
        if (r12 != 0) goto L_0x0247;	 Catch:{ NumberFormatException -> 0x027d }
    L_0x0245:
        if (r3 == 0) goto L_0x0256;	 Catch:{ NumberFormatException -> 0x027d }
    L_0x0247:
        r12 = r0.toString();	 Catch:{ NumberFormatException -> 0x027d }
        r13 = r4.toString();	 Catch:{ NumberFormatException -> 0x027d }
        r12 = r12.equals(r13);	 Catch:{ NumberFormatException -> 0x027d }
        if (r12 == 0) goto L_0x0256;	 Catch:{ NumberFormatException -> 0x027d }
    L_0x0255:
        return r0;	 Catch:{ NumberFormatException -> 0x027d }
        r12 = r4.isInfinite();	 Catch:{ NumberFormatException -> 0x027d }
        if (r12 != 0) goto L_0x027b;	 Catch:{ NumberFormatException -> 0x027d }
    L_0x025d:
        r12 = r4.doubleValue();	 Catch:{ NumberFormatException -> 0x027d }
        r16 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1));	 Catch:{ NumberFormatException -> 0x027d }
        if (r16 != 0) goto L_0x0267;	 Catch:{ NumberFormatException -> 0x027d }
    L_0x0265:
        if (r3 == 0) goto L_0x027b;	 Catch:{ NumberFormatException -> 0x027d }
    L_0x0267:
        r12 = createBigDecimal(r17);	 Catch:{ NumberFormatException -> 0x027d }
        r13 = r4.doubleValue();	 Catch:{ NumberFormatException -> 0x027d }
        r13 = java.math.BigDecimal.valueOf(r13);	 Catch:{ NumberFormatException -> 0x027d }
        r13 = r12.compareTo(r13);	 Catch:{ NumberFormatException -> 0x027d }
        if (r13 != 0) goto L_0x027a;
    L_0x0279:
        return r4;
    L_0x027a:
        return r12;
        goto L_0x027e;
    L_0x027d:
        r0 = move-exception;
    L_0x027e:
        r0 = createBigDecimal(r17);
        return r0;
    L_0x0283:
        r0 = new java.lang.NumberFormatException;
        r2 = "A blank string is not a valid number";
        r0.<init>(r2);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.math.NumberUtils.createNumber(java.lang.String):java.lang.Number");
    }

    public static int toInt(String str) {
        return toInt(str, 0);
    }

    public static int toInt(String str, int defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static long toLong(String str) {
        return toLong(str, 0);
    }

    public static long toLong(String str, long defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static float toFloat(String str) {
        return toFloat(str, 0.0f);
    }

    public static float toFloat(String str, float defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static double toDouble(String str) {
        return toDouble(str, 0.0d);
    }

    public static double toDouble(String str, double defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static byte toByte(String str) {
        return toByte(str, (byte) 0);
    }

    public static byte toByte(String str, byte defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Byte.parseByte(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static short toShort(String str) {
        return toShort(str, (short) 0);
    }

    public static short toShort(String str, short defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Short.parseShort(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static String getMantissa(String str) {
        return getMantissa(str, str.length());
    }

    private static String getMantissa(String str, int stopPos) {
        boolean hasSign;
        char firstChar = str.charAt(0);
        if (firstChar != '-') {
            if (firstChar != '+') {
                hasSign = false;
                return hasSign ? str.substring(1, stopPos) : str.substring(0, stopPos);
            }
        }
        hasSign = true;
        if (hasSign) {
        }
    }

    private static boolean isAllZeros(String str) {
        boolean z = true;
        if (str == null) {
            return true;
        }
        for (int i = str.length() - 1; i >= 0; i--) {
            if (str.charAt(i) != '0') {
                return false;
            }
        }
        if (str.length() <= 0) {
            z = false;
        }
        return z;
    }

    public static Float createFloat(String str) {
        if (str == null) {
            return null;
        }
        return Float.valueOf(str);
    }

    public static Double createDouble(String str) {
        if (str == null) {
            return null;
        }
        return Double.valueOf(str);
    }

    public static Integer createInteger(String str) {
        if (str == null) {
            return null;
        }
        return Integer.decode(str);
    }

    public static Long createLong(String str) {
        if (str == null) {
            return null;
        }
        return Long.decode(str);
    }

    public static BigInteger createBigInteger(String str) {
        if (str == null) {
            return null;
        }
        BigInteger value;
        int pos = 0;
        int radix = 10;
        boolean negate = false;
        if (str.startsWith("-")) {
            negate = true;
            pos = 1;
        }
        if (!str.startsWith("0x", pos)) {
            if (!str.startsWith("0X", pos)) {
                if (str.startsWith("#", pos)) {
                    radix = 16;
                    pos++;
                } else if (str.startsWith("0", pos) && str.length() > pos + 1) {
                    radix = 8;
                    pos++;
                }
                value = new BigInteger(str.substring(pos), radix);
                return negate ? value.negate() : value;
            }
        }
        radix = 16;
        pos += 2;
        value = new BigInteger(str.substring(pos), radix);
        if (negate) {
        }
        return negate ? value.negate() : value;
    }

    public static BigDecimal createBigDecimal(String str) {
        if (str == null) {
            return null;
        }
        if (StringUtils.isBlank(str)) {
            throw new NumberFormatException("A blank string is not a valid number");
        } else if (!str.trim().startsWith("--")) {
            return new BigDecimal(str);
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(str);
            stringBuilder.append(" is not a valid number.");
            throw new NumberFormatException(stringBuilder.toString());
        }
    }

    public static long min(long... array) {
        validateArray(array);
        long min = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
            }
        }
        return min;
    }

    public static int min(int... array) {
        validateArray(array);
        int min = array[0];
        for (int j = 1; j < array.length; j++) {
            if (array[j] < min) {
                min = array[j];
            }
        }
        return min;
    }

    public static short min(short... array) {
        validateArray(array);
        short min = array[(short) 0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
            }
        }
        return min;
    }

    public static byte min(byte... array) {
        validateArray(array);
        byte min = array[(byte) 0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
            }
        }
        return min;
    }

    public static double min(double... array) {
        validateArray(array);
        double min = array[0.0d];
        for (int i = 1; i < array.length; i++) {
            if (Double.isNaN(array[i])) {
                return Double.NaN;
            }
            if (array[i] < min) {
                min = array[i];
            }
        }
        return min;
    }

    public static float min(float... array) {
        validateArray(array);
        float min = array[0];
        for (int i = 1; i < array.length; i++) {
            if (Float.isNaN(array[i])) {
                return Float.NaN;
            }
            if (array[i] < min) {
                min = array[i];
            }
        }
        return min;
    }

    public static long max(long... array) {
        validateArray(array);
        long max = array[0];
        for (int j = 1; j < array.length; j++) {
            if (array[j] > max) {
                max = array[j];
            }
        }
        return max;
    }

    public static int max(int... array) {
        validateArray(array);
        int max = array[0];
        for (int j = 1; j < array.length; j++) {
            if (array[j] > max) {
                max = array[j];
            }
        }
        return max;
    }

    public static short max(short... array) {
        validateArray(array);
        short max = array[(short) 0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }

    public static byte max(byte... array) {
        validateArray(array);
        byte max = array[(byte) 0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }

    public static double max(double... array) {
        validateArray(array);
        double max = array[0.0d];
        for (int j = 1; j < array.length; j++) {
            if (Double.isNaN(array[j])) {
                return Double.NaN;
            }
            if (array[j] > max) {
                max = array[j];
            }
        }
        return max;
    }

    public static float max(float... array) {
        validateArray(array);
        float max = array[0];
        for (int j = 1; j < array.length; j++) {
            if (Float.isNaN(array[j])) {
                return Float.NaN;
            }
            if (array[j] > max) {
                max = array[j];
            }
        }
        return max;
    }

    private static void validateArray(Object array) {
        boolean z = true;
        Validate.isTrue(array != null, "The Array must not be null", new Object[0]);
        if (Array.getLength(array) == 0) {
            z = false;
        }
        Validate.isTrue(z, "Array cannot be empty.", new Object[0]);
    }

    public static long min(long a, long b, long c) {
        if (b < a) {
            a = b;
        }
        if (c < a) {
            return c;
        }
        return a;
    }

    public static int min(int a, int b, int c) {
        if (b < a) {
            a = b;
        }
        if (c < a) {
            return c;
        }
        return a;
    }

    public static short min(short a, short b, short c) {
        if (b < a) {
            a = b;
        }
        if (c < a) {
            return c;
        }
        return a;
    }

    public static byte min(byte a, byte b, byte c) {
        if (b < a) {
            a = b;
        }
        if (c < a) {
            return c;
        }
        return a;
    }

    public static double min(double a, double b, double c) {
        return Math.min(Math.min(a, b), c);
    }

    public static float min(float a, float b, float c) {
        return Math.min(Math.min(a, b), c);
    }

    public static long max(long a, long b, long c) {
        if (b > a) {
            a = b;
        }
        if (c > a) {
            return c;
        }
        return a;
    }

    public static int max(int a, int b, int c) {
        if (b > a) {
            a = b;
        }
        if (c > a) {
            return c;
        }
        return a;
    }

    public static short max(short a, short b, short c) {
        if (b > a) {
            a = b;
        }
        if (c > a) {
            return c;
        }
        return a;
    }

    public static byte max(byte a, byte b, byte c) {
        if (b > a) {
            a = b;
        }
        if (c > a) {
            return c;
        }
        return a;
    }

    public static double max(double a, double b, double c) {
        return Math.max(Math.max(a, b), c);
    }

    public static float max(float a, float b, float c) {
        return Math.max(Math.max(a, b), c);
    }

    public static boolean isDigits(String str) {
        return StringUtils.isNumeric(str);
    }

    @Deprecated
    public static boolean isNumber(String str) {
        return isCreatable(str);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isCreatable(java.lang.String r17) {
        /*
        r0 = org.apache.commons.lang3.StringUtils.isEmpty(r17);
        r1 = 0;
        if (r0 == 0) goto L_0x0008;
    L_0x0007:
        return r1;
    L_0x0008:
        r0 = r17.toCharArray();
        r2 = r0.length;
        r3 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 0;
        r7 = r0[r1];
        r8 = 45;
        r9 = 43;
        r10 = 1;
        if (r7 == r8) goto L_0x0021;
    L_0x001a:
        r7 = r0[r1];
        if (r7 != r9) goto L_0x001f;
    L_0x001e:
        goto L_0x0021;
    L_0x001f:
        r7 = 0;
        goto L_0x0022;
    L_0x0021:
        r7 = 1;
    L_0x0022:
        if (r7 != r10) goto L_0x002a;
    L_0x0024:
        r11 = r0[r1];
        if (r11 != r9) goto L_0x002a;
    L_0x0028:
        r11 = 1;
        goto L_0x002b;
    L_0x002a:
        r11 = 0;
    L_0x002b:
        r12 = r7 + 1;
        r13 = 70;
        r14 = 102; // 0x66 float:1.43E-43 double:5.04E-322;
        r15 = 57;
        r8 = 48;
        if (r2 <= r12) goto L_0x009e;
    L_0x0037:
        r12 = r0[r7];
        if (r12 != r8) goto L_0x009e;
    L_0x003b:
        r12 = r7 + 1;
        r12 = r0[r12];
        r9 = 120; // 0x78 float:1.68E-43 double:5.93E-322;
        if (r12 == r9) goto L_0x006d;
    L_0x0043:
        r9 = r7 + 1;
        r9 = r0[r9];
        r12 = 88;
        if (r9 != r12) goto L_0x004c;
    L_0x004b:
        goto L_0x006d;
    L_0x004c:
        r9 = r7 + 1;
        r9 = r0[r9];
        r9 = java.lang.Character.isDigit(r9);
        if (r9 == 0) goto L_0x006c;
    L_0x0056:
        r9 = r7 + 1;
    L_0x0058:
        r12 = r0.length;
        if (r9 >= r12) goto L_0x006b;
    L_0x005b:
        r12 = r0[r9];
        if (r12 < r8) goto L_0x0069;
    L_0x005f:
        r12 = r0[r9];
        r13 = 55;
        if (r12 <= r13) goto L_0x0066;
    L_0x0065:
        goto L_0x0069;
    L_0x0066:
        r9 = r9 + 1;
        goto L_0x0058;
        return r1;
    L_0x006b:
        return r10;
    L_0x006c:
        goto L_0x009f;
        r9 = r7 + 2;
        if (r9 != r2) goto L_0x0073;
    L_0x0072:
        return r1;
    L_0x0074:
        r12 = r0.length;
        if (r9 >= r12) goto L_0x009d;
    L_0x0077:
        r12 = r0[r9];
        if (r12 < r8) goto L_0x0081;
    L_0x007b:
        r12 = r0[r9];
        if (r12 <= r15) goto L_0x0080;
    L_0x007f:
        goto L_0x0081;
    L_0x0080:
        goto L_0x0096;
    L_0x0081:
        r12 = r0[r9];
        r15 = 97;
        if (r12 < r15) goto L_0x008b;
    L_0x0087:
        r12 = r0[r9];
        if (r12 <= r14) goto L_0x0080;
    L_0x008b:
        r12 = r0[r9];
        r15 = 65;
        if (r12 < r15) goto L_0x009b;
    L_0x0091:
        r12 = r0[r9];
        if (r12 <= r13) goto L_0x0080;
    L_0x0095:
        goto L_0x009b;
    L_0x0096:
        r9 = r9 + 1;
        r15 = 57;
        goto L_0x0074;
        return r1;
    L_0x009d:
        return r10;
    L_0x009f:
        r2 = r2 + -1;
        r9 = r7;
    L_0x00a2:
        r12 = 69;
        r15 = 101; // 0x65 float:1.42E-43 double:5.0E-322;
        r13 = 46;
        if (r9 < r2) goto L_0x0123;
    L_0x00aa:
        r14 = r2 + 1;
        if (r9 >= r14) goto L_0x00b8;
    L_0x00ae:
        if (r5 == 0) goto L_0x00b8;
    L_0x00b0:
        if (r6 != 0) goto L_0x00b8;
    L_0x00b2:
        r14 = 102; // 0x66 float:1.43E-43 double:5.04E-322;
        r16 = 70;
        goto L_0x0125;
    L_0x00b8:
        r14 = r0.length;
        if (r9 >= r14) goto L_0x011c;
    L_0x00bb:
        r14 = r0[r9];
        if (r14 < r8) goto L_0x00d0;
    L_0x00bf:
        r8 = r0[r9];
        r14 = 57;
        if (r8 > r14) goto L_0x00d0;
    L_0x00c5:
        r8 = org.apache.commons.lang3.SystemUtils.IS_JAVA_1_6;
        if (r8 == 0) goto L_0x00ce;
    L_0x00c9:
        if (r11 == 0) goto L_0x00ce;
    L_0x00cb:
        if (r4 != 0) goto L_0x00ce;
    L_0x00cd:
        return r1;
        return r10;
        r8 = r0[r9];
        if (r8 == r15) goto L_0x011a;
    L_0x00d5:
        r8 = r0[r9];
        if (r8 != r12) goto L_0x00da;
    L_0x00d9:
        goto L_0x011a;
    L_0x00da:
        r8 = r0[r9];
        if (r8 != r13) goto L_0x00e6;
    L_0x00de:
        if (r4 != 0) goto L_0x00e4;
    L_0x00e0:
        if (r3 == 0) goto L_0x00e3;
    L_0x00e2:
        goto L_0x00e4;
    L_0x00e3:
        return r6;
        return r1;
    L_0x00e6:
        if (r5 != 0) goto L_0x0101;
    L_0x00e8:
        r8 = r0[r9];
        r12 = 100;
        if (r8 == r12) goto L_0x0100;
    L_0x00ee:
        r8 = r0[r9];
        r12 = 68;
        if (r8 == r12) goto L_0x0100;
    L_0x00f4:
        r8 = r0[r9];
        r14 = 102; // 0x66 float:1.43E-43 double:5.04E-322;
        if (r8 == r14) goto L_0x0100;
    L_0x00fa:
        r8 = r0[r9];
        r12 = 70;
        if (r8 != r12) goto L_0x0101;
    L_0x0100:
        return r6;
        r8 = r0[r9];
        r12 = 108; // 0x6c float:1.51E-43 double:5.34E-322;
        if (r8 == r12) goto L_0x0110;
    L_0x0108:
        r8 = r0[r9];
        r12 = 76;
        if (r8 != r12) goto L_0x010f;
    L_0x010e:
        goto L_0x0110;
    L_0x010f:
        return r1;
        if (r6 == 0) goto L_0x0119;
    L_0x0113:
        if (r3 != 0) goto L_0x0119;
    L_0x0115:
        if (r4 != 0) goto L_0x0119;
    L_0x0117:
        r1 = 1;
    L_0x0119:
        return r1;
        return r1;
    L_0x011c:
        if (r5 != 0) goto L_0x0122;
    L_0x011e:
        if (r6 == 0) goto L_0x0122;
    L_0x0120:
        r1 = 1;
    L_0x0122:
        return r1;
    L_0x0123:
        r16 = 70;
    L_0x0125:
        r10 = r0[r9];
        if (r10 < r8) goto L_0x0136;
    L_0x0129:
        r10 = r0[r9];
        r8 = 57;
        if (r10 > r8) goto L_0x0138;
    L_0x012f:
        r6 = 1;
        r5 = 0;
        r12 = 43;
        r13 = 45;
        goto L_0x0178;
    L_0x0136:
        r8 = 57;
    L_0x0138:
        r10 = r0[r9];
        if (r10 != r13) goto L_0x0149;
    L_0x013c:
        if (r4 != 0) goto L_0x0147;
    L_0x013e:
        if (r3 == 0) goto L_0x0141;
    L_0x0140:
        goto L_0x0147;
    L_0x0141:
        r4 = 1;
        r12 = 43;
        r13 = 45;
        goto L_0x0178;
        return r1;
    L_0x0149:
        r10 = r0[r9];
        if (r10 == r15) goto L_0x016c;
    L_0x014d:
        r10 = r0[r9];
        if (r10 != r12) goto L_0x0156;
    L_0x0151:
        r12 = 43;
        r13 = 45;
        goto L_0x0170;
    L_0x0156:
        r10 = r0[r9];
        r12 = 43;
        if (r10 == r12) goto L_0x0164;
    L_0x015c:
        r10 = r0[r9];
        r13 = 45;
        if (r10 != r13) goto L_0x0163;
    L_0x0162:
        goto L_0x0166;
    L_0x0163:
        return r1;
    L_0x0164:
        r13 = 45;
    L_0x0166:
        if (r5 != 0) goto L_0x0169;
    L_0x0168:
        return r1;
    L_0x0169:
        r5 = 0;
        r6 = 0;
        goto L_0x0178;
    L_0x016c:
        r12 = 43;
        r13 = 45;
    L_0x0170:
        if (r3 == 0) goto L_0x0173;
    L_0x0172:
        return r1;
    L_0x0173:
        if (r6 != 0) goto L_0x0176;
    L_0x0175:
        return r1;
    L_0x0176:
        r3 = 1;
        r5 = 1;
    L_0x0178:
        r9 = r9 + 1;
        r8 = 48;
        r10 = 1;
        r13 = 70;
        goto L_0x00a2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.math.NumberUtils.isCreatable(java.lang.String):boolean");
    }

    public static boolean isParsable(String str) {
        if (StringUtils.isEmpty(str) || str.charAt(str.length() - 1) == '.') {
            return false;
        }
        if (str.charAt(0) != '-') {
            return withDecimalsParsing(str, 0);
        }
        if (str.length() == 1) {
            return false;
        }
        return withDecimalsParsing(str, 1);
    }

    private static boolean withDecimalsParsing(String str, int beginIdx) {
        int decimalPoints = 0;
        int i = beginIdx;
        while (i < str.length()) {
            boolean isDecimalPoint = str.charAt(i) == '.';
            if (isDecimalPoint) {
                decimalPoints++;
            }
            if (decimalPoints > 1) {
                return false;
            }
            if (!isDecimalPoint && !Character.isDigit(str.charAt(i))) {
                return false;
            }
            i++;
        }
        return true;
    }

    public static int compare(int x, int y) {
        if (x == y) {
            return 0;
        }
        return x < y ? -1 : 1;
    }

    public static int compare(long x, long y) {
        if (x == y) {
            return 0;
        }
        return x < y ? -1 : 1;
    }

    public static int compare(short x, short y) {
        if (x == y) {
            return 0;
        }
        return x < y ? -1 : 1;
    }

    public static int compare(byte x, byte y) {
        return x - y;
    }
}
