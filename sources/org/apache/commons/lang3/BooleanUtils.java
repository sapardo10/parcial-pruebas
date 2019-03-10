package org.apache.commons.lang3;

import org.apache.commons.lang3.math.NumberUtils;

public class BooleanUtils {
    public static boolean and(boolean... r5) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:16:0x0026 in {8, 9, 11, 13, 15} preds:[]
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
        if (r5 == 0) goto L_0x001e;
    L_0x0002:
        r0 = r5.length;
        if (r0 == 0) goto L_0x0016;
    L_0x0005:
        r0 = r5;
        r1 = r0.length;
        r2 = 0;
    L_0x0008:
        if (r2 >= r1) goto L_0x0014;
    L_0x000a:
        r3 = r0[r2];
        if (r3 != 0) goto L_0x0010;
    L_0x000e:
        r4 = 0;
        return r4;
        r2 = r2 + 1;
        goto L_0x0008;
    L_0x0014:
        r0 = 1;
        return r0;
    L_0x0016:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "Array is empty";
        r0.<init>(r1);
        throw r0;
    L_0x001e:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "The Array must not be null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.BooleanUtils.and(boolean[]):boolean");
    }

    public static boolean or(boolean... r5) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:16:0x0026 in {8, 9, 11, 13, 15} preds:[]
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
        if (r5 == 0) goto L_0x001e;
    L_0x0002:
        r0 = r5.length;
        if (r0 == 0) goto L_0x0016;
    L_0x0005:
        r0 = r5;
        r1 = r0.length;
        r2 = 0;
    L_0x0008:
        if (r2 >= r1) goto L_0x0014;
    L_0x000a:
        r3 = r0[r2];
        if (r3 == 0) goto L_0x0010;
    L_0x000e:
        r4 = 1;
        return r4;
        r2 = r2 + 1;
        goto L_0x0008;
    L_0x0014:
        r0 = 0;
        return r0;
    L_0x0016:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "Array is empty";
        r0.<init>(r1);
        throw r0;
    L_0x001e:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "The Array must not be null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.BooleanUtils.or(boolean[]):boolean");
    }

    public static boolean xor(boolean... r5) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:11:0x0022 in {5, 6, 8, 10} preds:[]
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
        if (r5 == 0) goto L_0x001a;
    L_0x0002:
        r0 = r5.length;
        if (r0 == 0) goto L_0x0012;
    L_0x0005:
        r0 = 0;
        r1 = r5;
        r2 = r1.length;
        r3 = 0;
    L_0x0009:
        if (r3 >= r2) goto L_0x0011;
    L_0x000b:
        r4 = r1[r3];
        r0 = r0 ^ r4;
        r3 = r3 + 1;
        goto L_0x0009;
    L_0x0011:
        return r0;
    L_0x0012:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "Array is empty";
        r0.<init>(r1);
        throw r0;
    L_0x001a:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "The Array must not be null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.BooleanUtils.xor(boolean[]):boolean");
    }

    public static Boolean negate(Boolean bool) {
        if (bool == null) {
            return null;
        }
        return bool.booleanValue() ? Boolean.FALSE : Boolean.TRUE;
    }

    public static boolean isTrue(Boolean bool) {
        return Boolean.TRUE.equals(bool);
    }

    public static boolean isNotTrue(Boolean bool) {
        return isTrue(bool) ^ 1;
    }

    public static boolean isFalse(Boolean bool) {
        return Boolean.FALSE.equals(bool);
    }

    public static boolean isNotFalse(Boolean bool) {
        return isFalse(bool) ^ 1;
    }

    public static boolean toBoolean(Boolean bool) {
        return bool != null && bool.booleanValue();
    }

    public static boolean toBooleanDefaultIfNull(Boolean bool, boolean valueIfNull) {
        if (bool == null) {
            return valueIfNull;
        }
        return bool.booleanValue();
    }

    public static boolean toBoolean(int value) {
        return value != 0;
    }

    public static Boolean toBooleanObject(int value) {
        return value == 0 ? Boolean.FALSE : Boolean.TRUE;
    }

    public static Boolean toBooleanObject(Integer value) {
        if (value == null) {
            return null;
        }
        return value.intValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
    }

    public static boolean toBoolean(int value, int trueValue, int falseValue) {
        if (value == trueValue) {
            return true;
        }
        if (value == falseValue) {
            return false;
        }
        throw new IllegalArgumentException("The Integer did not match either specified value");
    }

    public static boolean toBoolean(Integer value, Integer trueValue, Integer falseValue) {
        if (value == null) {
            if (trueValue == null) {
                return true;
            }
            if (falseValue == null) {
                return false;
            }
        } else if (value.equals(trueValue)) {
            return true;
        } else {
            if (value.equals(falseValue)) {
                return false;
            }
        }
        throw new IllegalArgumentException("The Integer did not match either specified value");
    }

    public static Boolean toBooleanObject(int value, int trueValue, int falseValue, int nullValue) {
        if (value == trueValue) {
            return Boolean.TRUE;
        }
        if (value == falseValue) {
            return Boolean.FALSE;
        }
        if (value == nullValue) {
            return null;
        }
        throw new IllegalArgumentException("The Integer did not match any specified value");
    }

    public static Boolean toBooleanObject(Integer value, Integer trueValue, Integer falseValue, Integer nullValue) {
        if (value == null) {
            if (trueValue == null) {
                return Boolean.TRUE;
            }
            if (falseValue == null) {
                return Boolean.FALSE;
            }
            if (nullValue == null) {
                return null;
            }
        } else if (value.equals(trueValue)) {
            return Boolean.TRUE;
        } else {
            if (value.equals(falseValue)) {
                return Boolean.FALSE;
            }
            if (value.equals(nullValue)) {
                return null;
            }
        }
        throw new IllegalArgumentException("The Integer did not match any specified value");
    }

    public static int toInteger(boolean bool) {
        return bool;
    }

    public static Integer toIntegerObject(boolean bool) {
        return bool ? NumberUtils.INTEGER_ONE : NumberUtils.INTEGER_ZERO;
    }

    public static Integer toIntegerObject(Boolean bool) {
        if (bool == null) {
            return null;
        }
        return bool.booleanValue() ? NumberUtils.INTEGER_ONE : NumberUtils.INTEGER_ZERO;
    }

    public static int toInteger(boolean bool, int trueValue, int falseValue) {
        return bool ? trueValue : falseValue;
    }

    public static int toInteger(Boolean bool, int trueValue, int falseValue, int nullValue) {
        if (bool == null) {
            return nullValue;
        }
        return bool.booleanValue() ? trueValue : falseValue;
    }

    public static Integer toIntegerObject(boolean bool, Integer trueValue, Integer falseValue) {
        return bool ? trueValue : falseValue;
    }

    public static Integer toIntegerObject(Boolean bool, Integer trueValue, Integer falseValue, Integer nullValue) {
        if (bool == null) {
            return nullValue;
        }
        return bool.booleanValue() ? trueValue : falseValue;
    }

    public static Boolean toBooleanObject(String str) {
        String str2 = str;
        if (str2 == "true") {
            return Boolean.TRUE;
        }
        if (str2 == null) {
            return null;
        }
        char ch0;
        char ch1;
        char ch2;
        char ch22;
        char ch3;
        switch (str.length()) {
            case 1:
                ch0 = str2.charAt(0);
                if (!(ch0 == 'y' || ch0 == 'Y' || ch0 == 't')) {
                    if (ch0 != 'T') {
                        if (!(ch0 == 'n' || ch0 == 'N' || ch0 == 'f')) {
                            if (ch0 != 'F') {
                                break;
                            }
                        }
                        return Boolean.FALSE;
                    }
                }
                return Boolean.TRUE;
            case 2:
                ch0 = str2.charAt(0);
                ch1 = str2.charAt(1);
                if (ch0 != 'o') {
                    if (ch0 == 'O') {
                    }
                    if (ch0 != 'n') {
                        if (ch0 == 'N') {
                        }
                        break;
                    }
                    if (ch1 == 'o' || ch1 == 'O') {
                        return Boolean.FALSE;
                    }
                }
                if (ch1 != 'n') {
                    if (ch1 == 'N') {
                    }
                    if (ch0 != 'n') {
                        if (ch0 == 'N') {
                        }
                    }
                    return Boolean.FALSE;
                }
                return Boolean.TRUE;
            case 3:
                ch0 = str2.charAt(0);
                ch1 = str2.charAt(1);
                ch2 = str2.charAt(2);
                if (ch0 != 'y') {
                    if (ch0 == 'Y') {
                    }
                    if (ch0 != 'o') {
                        if (ch0 == 'O') {
                        }
                        break;
                    }
                    if ((ch1 == 'f' || ch1 == 'F') && (ch2 == 'f' || ch2 == 'F')) {
                        return Boolean.FALSE;
                    }
                }
                if (ch1 == 'e' || ch1 == 'E') {
                    if (ch2 != 's') {
                        if (ch2 == 'S') {
                        }
                    }
                    return Boolean.TRUE;
                }
                if (ch0 != 'o') {
                    if (ch0 == 'O') {
                    }
                }
                return Boolean.FALSE;
            case 4:
                ch0 = str2.charAt(0);
                ch1 = str2.charAt(1);
                ch22 = str2.charAt(2);
                ch3 = str2.charAt(3);
                if (ch0 != 't') {
                    if (ch0 == 'T') {
                    }
                    break;
                }
                if ((ch1 == 'r' || ch1 == 'R') && ((ch22 == 'u' || ch22 == 'U') && (ch3 == 'e' || ch3 == 'E'))) {
                    return Boolean.TRUE;
                }
            case 5:
                ch0 = str2.charAt(0);
                ch1 = str2.charAt(1);
                ch2 = str2.charAt(2);
                ch3 = str2.charAt(3);
                ch22 = str2.charAt('\u0004');
                if (ch0 != 'f') {
                    if (ch0 == 'F') {
                    }
                    break;
                }
                if ((ch1 == 'a' || ch1 == 'A') && ((ch2 == 'l' || ch2 == 'L') && ((ch3 == 's' || ch3 == 'S') && (ch22 == 'e' || ch22 == 'E')))) {
                    return Boolean.FALSE;
                }
            default:
                break;
        }
        return null;
    }

    public static Boolean toBooleanObject(String str, String trueString, String falseString, String nullString) {
        if (str == null) {
            if (trueString == null) {
                return Boolean.TRUE;
            }
            if (falseString == null) {
                return Boolean.FALSE;
            }
            if (nullString == null) {
                return null;
            }
        } else if (str.equals(trueString)) {
            return Boolean.TRUE;
        } else {
            if (str.equals(falseString)) {
                return Boolean.FALSE;
            }
            if (str.equals(nullString)) {
                return null;
            }
        }
        throw new IllegalArgumentException("The String did not match any specified value");
    }

    public static boolean toBoolean(String str) {
        return toBooleanObject(str) == Boolean.TRUE;
    }

    public static boolean toBoolean(String str, String trueString, String falseString) {
        if (str == trueString) {
            return true;
        }
        if (str == falseString) {
            return false;
        }
        if (str != null) {
            if (str.equals(trueString)) {
                return true;
            }
            if (str.equals(falseString)) {
                return false;
            }
        }
        throw new IllegalArgumentException("The String did not match either specified value");
    }

    public static String toStringTrueFalse(Boolean bool) {
        return toString(bool, "true", "false", null);
    }

    public static String toStringOnOff(Boolean bool) {
        return toString(bool, "on", "off", null);
    }

    public static String toStringYesNo(Boolean bool) {
        return toString(bool, "yes", "no", null);
    }

    public static String toString(Boolean bool, String trueString, String falseString, String nullString) {
        if (bool == null) {
            return nullString;
        }
        return bool.booleanValue() ? trueString : falseString;
    }

    public static String toStringTrueFalse(boolean bool) {
        return toString(bool, "true", "false");
    }

    public static String toStringOnOff(boolean bool) {
        return toString(bool, "on", "off");
    }

    public static String toStringYesNo(boolean bool) {
        return toString(bool, "yes", "no");
    }

    public static String toString(boolean bool, String trueString, String falseString) {
        return bool ? trueString : falseString;
    }

    public static Boolean and(Boolean... array) {
        if (array == null) {
            throw new IllegalArgumentException("The Array must not be null");
        } else if (array.length != 0) {
            try {
                return and(ArrayUtils.toPrimitive(array)) ? Boolean.TRUE : Boolean.FALSE;
            } catch (NullPointerException e) {
                throw new IllegalArgumentException("The array must not contain any null elements");
            }
        } else {
            throw new IllegalArgumentException("Array is empty");
        }
    }

    public static Boolean or(Boolean... array) {
        if (array == null) {
            throw new IllegalArgumentException("The Array must not be null");
        } else if (array.length != 0) {
            try {
                return or(ArrayUtils.toPrimitive(array)) ? Boolean.TRUE : Boolean.FALSE;
            } catch (NullPointerException e) {
                throw new IllegalArgumentException("The array must not contain any null elements");
            }
        } else {
            throw new IllegalArgumentException("Array is empty");
        }
    }

    public static Boolean xor(Boolean... array) {
        if (array == null) {
            throw new IllegalArgumentException("The Array must not be null");
        } else if (array.length != 0) {
            try {
                return xor(ArrayUtils.toPrimitive(array)) ? Boolean.TRUE : Boolean.FALSE;
            } catch (NullPointerException e) {
                throw new IllegalArgumentException("The array must not contain any null elements");
            }
        } else {
            throw new IllegalArgumentException("Array is empty");
        }
    }

    public static int compare(boolean x, boolean y) {
        if (x == y) {
            return 0;
        }
        return x ? 1 : -1;
    }
}
