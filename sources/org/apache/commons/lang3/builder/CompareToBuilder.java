package org.apache.commons.lang3.builder;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Comparator;
import org.apache.commons.lang3.ArrayUtils;

public class CompareToBuilder implements Builder<Integer> {
    private int comparison = 0;

    public static int reflectionCompare(java.lang.Object r11, java.lang.Object r12, boolean r13, java.lang.Class<?> r14, java.lang.String... r15) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:18:0x0048 in {2, 11, 13, 15, 17} preds:[]
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
        if (r11 != r12) goto L_0x0004;
    L_0x0002:
        r0 = 0;
        return r0;
    L_0x0004:
        if (r11 == 0) goto L_0x0041;
    L_0x0006:
        if (r12 == 0) goto L_0x0041;
    L_0x0008:
        r0 = r11.getClass();
        r1 = r0.isInstance(r12);
        if (r1 == 0) goto L_0x003b;
    L_0x0012:
        r4 = new org.apache.commons.lang3.builder.CompareToBuilder;
        r4.<init>();
        r1 = r11;
        r2 = r12;
        r3 = r0;
        r5 = r13;
        r6 = r15;
        reflectionAppend(r1, r2, r3, r4, r5, r6);
    L_0x001f:
        r1 = r0.getSuperclass();
        if (r1 == 0) goto L_0x0035;
    L_0x0025:
        if (r0 == r14) goto L_0x0035;
    L_0x0027:
        r0 = r0.getSuperclass();
        r5 = r11;
        r6 = r12;
        r7 = r0;
        r8 = r4;
        r9 = r13;
        r10 = r15;
        reflectionAppend(r5, r6, r7, r8, r9, r10);
        goto L_0x001f;
        r1 = r4.toComparison();
        return r1;
    L_0x003b:
        r1 = new java.lang.ClassCastException;
        r1.<init>();
        throw r1;
        r0 = new java.lang.NullPointerException;
        r0.<init>();
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.builder.CompareToBuilder.reflectionCompare(java.lang.Object, java.lang.Object, boolean, java.lang.Class, java.lang.String[]):int");
    }

    public static int reflectionCompare(Object lhs, Object rhs) {
        return reflectionCompare(lhs, rhs, false, null, new String[0]);
    }

    public static int reflectionCompare(Object lhs, Object rhs, boolean compareTransients) {
        return reflectionCompare(lhs, rhs, compareTransients, null, new String[0]);
    }

    public static int reflectionCompare(Object lhs, Object rhs, Collection<String> excludeFields) {
        return reflectionCompare(lhs, rhs, ReflectionToStringBuilder.toNoNullStringArray((Collection) excludeFields));
    }

    public static int reflectionCompare(Object lhs, Object rhs, String... excludeFields) {
        return reflectionCompare(lhs, rhs, false, null, excludeFields);
    }

    private static void reflectionAppend(Object lhs, Object rhs, Class<?> clazz, CompareToBuilder builder, boolean useTransients, String[] excludeFields) {
        Field[] fields = clazz.getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);
        for (int i = 0; i < fields.length && builder.comparison == 0; i++) {
            Field f = fields[i];
            if (!ArrayUtils.contains((Object[]) excludeFields, f.getName()) && !f.getName().contains("$") && ((useTransients || !Modifier.isTransient(f.getModifiers())) && !Modifier.isStatic(f.getModifiers()))) {
                try {
                    builder.append(f.get(lhs), f.get(rhs));
                } catch (IllegalAccessException e) {
                    throw new InternalError("Unexpected IllegalAccessException");
                }
            }
        }
    }

    public CompareToBuilder appendSuper(int superCompareTo) {
        if (this.comparison != 0) {
            return this;
        }
        this.comparison = superCompareTo;
        return this;
    }

    public CompareToBuilder append(Object lhs, Object rhs) {
        return append(lhs, rhs, null);
    }

    public CompareToBuilder append(Object lhs, Object rhs, Comparator<?> comparator) {
        if (this.comparison != 0 || lhs == rhs) {
            return this;
        }
        if (lhs == null) {
            this.comparison = -1;
            return this;
        } else if (rhs == null) {
            this.comparison = 1;
            return this;
        } else {
            if (lhs.getClass().isArray()) {
                appendArray(lhs, rhs, comparator);
            } else if (comparator == null) {
                this.comparison = ((Comparable) lhs).compareTo(rhs);
            } else {
                this.comparison = comparator.compare(lhs, rhs);
            }
            return this;
        }
    }

    private void appendArray(Object lhs, Object rhs, Comparator<?> comparator) {
        if (lhs instanceof long[]) {
            append((long[]) lhs, (long[]) rhs);
        } else if (lhs instanceof int[]) {
            append((int[]) lhs, (int[]) rhs);
        } else if (lhs instanceof short[]) {
            append((short[]) lhs, (short[]) rhs);
        } else if (lhs instanceof char[]) {
            append((char[]) lhs, (char[]) rhs);
        } else if (lhs instanceof byte[]) {
            append((byte[]) lhs, (byte[]) rhs);
        } else if (lhs instanceof double[]) {
            append((double[]) lhs, (double[]) rhs);
        } else if (lhs instanceof float[]) {
            append((float[]) lhs, (float[]) rhs);
        } else if (lhs instanceof boolean[]) {
            append((boolean[]) lhs, (boolean[]) rhs);
        } else {
            append((Object[]) lhs, (Object[]) rhs, (Comparator) comparator);
        }
    }

    public CompareToBuilder append(long lhs, long rhs) {
        if (this.comparison != 0) {
            return this;
        }
        int i = lhs < rhs ? -1 : lhs > rhs ? 1 : 0;
        this.comparison = i;
        return this;
    }

    public CompareToBuilder append(int lhs, int rhs) {
        if (this.comparison != 0) {
            return this;
        }
        int i = lhs < rhs ? -1 : lhs > rhs ? 1 : 0;
        this.comparison = i;
        return this;
    }

    public CompareToBuilder append(short lhs, short rhs) {
        if (this.comparison != 0) {
            return this;
        }
        int i = lhs < rhs ? -1 : lhs > rhs ? 1 : 0;
        this.comparison = i;
        return this;
    }

    public CompareToBuilder append(char lhs, char rhs) {
        if (this.comparison != 0) {
            return this;
        }
        int i = lhs < rhs ? -1 : lhs > rhs ? 1 : 0;
        this.comparison = i;
        return this;
    }

    public CompareToBuilder append(byte lhs, byte rhs) {
        if (this.comparison != 0) {
            return this;
        }
        int i = lhs < rhs ? -1 : lhs > rhs ? 1 : 0;
        this.comparison = i;
        return this;
    }

    public CompareToBuilder append(double lhs, double rhs) {
        if (this.comparison != 0) {
            return this;
        }
        this.comparison = Double.compare(lhs, rhs);
        return this;
    }

    public CompareToBuilder append(float lhs, float rhs) {
        if (this.comparison != 0) {
            return this;
        }
        this.comparison = Float.compare(lhs, rhs);
        return this;
    }

    public CompareToBuilder append(boolean lhs, boolean rhs) {
        if (this.comparison != 0 || lhs == rhs) {
            return this;
        }
        if (lhs) {
            this.comparison = 1;
        } else {
            this.comparison = -1;
        }
        return this;
    }

    public CompareToBuilder append(Object[] lhs, Object[] rhs) {
        return append(lhs, rhs, null);
    }

    public CompareToBuilder append(Object[] lhs, Object[] rhs, Comparator<?> comparator) {
        if (this.comparison != 0 || lhs == rhs) {
            return this;
        }
        int i = -1;
        if (lhs == null) {
            this.comparison = -1;
            return this;
        } else if (rhs == null) {
            this.comparison = 1;
            return this;
        } else if (lhs.length != rhs.length) {
            if (lhs.length >= rhs.length) {
                i = 1;
            }
            this.comparison = i;
            return this;
        } else {
            for (i = 0; i < lhs.length && this.comparison == 0; i++) {
                append(lhs[i], rhs[i], (Comparator) comparator);
            }
            return this;
        }
    }

    public CompareToBuilder append(long[] lhs, long[] rhs) {
        if (this.comparison != 0 || lhs == rhs) {
            return this;
        }
        int i = -1;
        if (lhs == null) {
            this.comparison = -1;
            return this;
        } else if (rhs == null) {
            this.comparison = 1;
            return this;
        } else if (lhs.length != rhs.length) {
            if (lhs.length >= rhs.length) {
                i = 1;
            }
            this.comparison = i;
            return this;
        } else {
            for (i = 0; i < lhs.length && this.comparison == 0; i++) {
                append(lhs[i], rhs[i]);
            }
            return this;
        }
    }

    public CompareToBuilder append(int[] lhs, int[] rhs) {
        if (this.comparison != 0 || lhs == rhs) {
            return this;
        }
        int i = -1;
        if (lhs == null) {
            this.comparison = -1;
            return this;
        } else if (rhs == null) {
            this.comparison = 1;
            return this;
        } else if (lhs.length != rhs.length) {
            if (lhs.length >= rhs.length) {
                i = 1;
            }
            this.comparison = i;
            return this;
        } else {
            for (i = 0; i < lhs.length && this.comparison == 0; i++) {
                append(lhs[i], rhs[i]);
            }
            return this;
        }
    }

    public CompareToBuilder append(short[] lhs, short[] rhs) {
        if (this.comparison != 0 || lhs == rhs) {
            return this;
        }
        int i = -1;
        if (lhs == null) {
            this.comparison = -1;
            return this;
        } else if (rhs == null) {
            this.comparison = 1;
            return this;
        } else if (lhs.length != rhs.length) {
            if (lhs.length >= rhs.length) {
                i = 1;
            }
            this.comparison = i;
            return this;
        } else {
            for (i = 0; i < lhs.length && this.comparison == 0; i++) {
                append(lhs[i], rhs[i]);
            }
            return this;
        }
    }

    public CompareToBuilder append(char[] lhs, char[] rhs) {
        if (this.comparison != 0 || lhs == rhs) {
            return this;
        }
        int i = -1;
        if (lhs == null) {
            this.comparison = -1;
            return this;
        } else if (rhs == null) {
            this.comparison = 1;
            return this;
        } else if (lhs.length != rhs.length) {
            if (lhs.length >= rhs.length) {
                i = 1;
            }
            this.comparison = i;
            return this;
        } else {
            for (i = 0; i < lhs.length && this.comparison == 0; i++) {
                append(lhs[i], rhs[i]);
            }
            return this;
        }
    }

    public CompareToBuilder append(byte[] lhs, byte[] rhs) {
        if (this.comparison != 0 || lhs == rhs) {
            return this;
        }
        int i = -1;
        if (lhs == null) {
            this.comparison = -1;
            return this;
        } else if (rhs == null) {
            this.comparison = 1;
            return this;
        } else if (lhs.length != rhs.length) {
            if (lhs.length >= rhs.length) {
                i = 1;
            }
            this.comparison = i;
            return this;
        } else {
            for (i = 0; i < lhs.length && this.comparison == 0; i++) {
                append(lhs[i], rhs[i]);
            }
            return this;
        }
    }

    public CompareToBuilder append(double[] lhs, double[] rhs) {
        if (this.comparison != 0 || lhs == rhs) {
            return this;
        }
        int i = -1;
        if (lhs == null) {
            this.comparison = -1;
            return this;
        } else if (rhs == null) {
            this.comparison = 1;
            return this;
        } else if (lhs.length != rhs.length) {
            if (lhs.length >= rhs.length) {
                i = 1;
            }
            this.comparison = i;
            return this;
        } else {
            for (i = 0; i < lhs.length && this.comparison == 0; i++) {
                append(lhs[i], rhs[i]);
            }
            return this;
        }
    }

    public CompareToBuilder append(float[] lhs, float[] rhs) {
        if (this.comparison != 0 || lhs == rhs) {
            return this;
        }
        int i = -1;
        if (lhs == null) {
            this.comparison = -1;
            return this;
        } else if (rhs == null) {
            this.comparison = 1;
            return this;
        } else if (lhs.length != rhs.length) {
            if (lhs.length >= rhs.length) {
                i = 1;
            }
            this.comparison = i;
            return this;
        } else {
            for (i = 0; i < lhs.length && this.comparison == 0; i++) {
                append(lhs[i], rhs[i]);
            }
            return this;
        }
    }

    public CompareToBuilder append(boolean[] lhs, boolean[] rhs) {
        if (this.comparison != 0 || lhs == rhs) {
            return this;
        }
        int i = -1;
        if (lhs == null) {
            this.comparison = -1;
            return this;
        } else if (rhs == null) {
            this.comparison = 1;
            return this;
        } else if (lhs.length != rhs.length) {
            if (lhs.length >= rhs.length) {
                i = 1;
            }
            this.comparison = i;
            return this;
        } else {
            for (i = 0; i < lhs.length && this.comparison == 0; i++) {
                append(lhs[i], rhs[i]);
            }
            return this;
        }
    }

    public int toComparison() {
        return this.comparison;
    }

    public Integer build() {
        return Integer.valueOf(toComparison());
    }
}
