package org.apache.commons.lang3.builder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.Validate;

public class HashCodeBuilder implements Builder<Integer> {
    private static final int DEFAULT_INITIAL_VALUE = 17;
    private static final int DEFAULT_MULTIPLIER_VALUE = 37;
    private static final ThreadLocal<Set<IDKey>> REGISTRY = new ThreadLocal();
    private final int iConstant;
    private int iTotal;

    private static void reflectionAppend(java.lang.Object r8, java.lang.Class<?> r9, org.apache.commons.lang3.builder.HashCodeBuilder r10, boolean r11, java.lang.String[] r12) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:31:0x006c in {2, 12, 19, 23, 24, 25, 27, 30} preds:[]
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
        r0 = isRegistered(r8);
        if (r0 == 0) goto L_0x0007;
    L_0x0006:
        return;
    L_0x0007:
        register(r8);	 Catch:{ all -> 0x0067 }
        r0 = r9.getDeclaredFields();	 Catch:{ all -> 0x0067 }
        r1 = 1;	 Catch:{ all -> 0x0067 }
        java.lang.reflect.AccessibleObject.setAccessible(r0, r1);	 Catch:{ all -> 0x0067 }
        r1 = r0;	 Catch:{ all -> 0x0067 }
        r2 = r1.length;	 Catch:{ all -> 0x0067 }
        r3 = 0;	 Catch:{ all -> 0x0067 }
    L_0x0015:
        if (r3 >= r2) goto L_0x0062;	 Catch:{ all -> 0x0067 }
    L_0x0017:
        r4 = r1[r3];	 Catch:{ all -> 0x0067 }
        r5 = r4.getName();	 Catch:{ all -> 0x0067 }
        r5 = org.apache.commons.lang3.ArrayUtils.contains(r12, r5);	 Catch:{ all -> 0x0067 }
        if (r5 != 0) goto L_0x005e;	 Catch:{ all -> 0x0067 }
    L_0x0023:
        r5 = r4.getName();	 Catch:{ all -> 0x0067 }
        r6 = "$";	 Catch:{ all -> 0x0067 }
        r5 = r5.contains(r6);	 Catch:{ all -> 0x0067 }
        if (r5 != 0) goto L_0x005e;	 Catch:{ all -> 0x0067 }
    L_0x002f:
        if (r11 != 0) goto L_0x003b;	 Catch:{ all -> 0x0067 }
    L_0x0031:
        r5 = r4.getModifiers();	 Catch:{ all -> 0x0067 }
        r5 = java.lang.reflect.Modifier.isTransient(r5);	 Catch:{ all -> 0x0067 }
        if (r5 != 0) goto L_0x005e;	 Catch:{ all -> 0x0067 }
    L_0x003b:
        r5 = r4.getModifiers();	 Catch:{ all -> 0x0067 }
        r5 = java.lang.reflect.Modifier.isStatic(r5);	 Catch:{ all -> 0x0067 }
        if (r5 != 0) goto L_0x005e;	 Catch:{ all -> 0x0067 }
    L_0x0045:
        r5 = org.apache.commons.lang3.builder.HashCodeExclude.class;	 Catch:{ all -> 0x0067 }
        r5 = r4.isAnnotationPresent(r5);	 Catch:{ all -> 0x0067 }
        if (r5 != 0) goto L_0x005e;
    L_0x004d:
        r5 = r4.get(r8);	 Catch:{ IllegalAccessException -> 0x0055 }
        r10.append(r5);	 Catch:{ IllegalAccessException -> 0x0055 }
        goto L_0x005f;
    L_0x0055:
        r5 = move-exception;
        r6 = new java.lang.InternalError;	 Catch:{ all -> 0x0067 }
        r7 = "Unexpected IllegalAccessException";	 Catch:{ all -> 0x0067 }
        r6.<init>(r7);	 Catch:{ all -> 0x0067 }
        throw r6;	 Catch:{ all -> 0x0067 }
    L_0x005f:
        r3 = r3 + 1;
        goto L_0x0015;
    L_0x0062:
        unregister(r8);
        return;
    L_0x0067:
        r0 = move-exception;
        unregister(r8);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.builder.HashCodeBuilder.reflectionAppend(java.lang.Object, java.lang.Class, org.apache.commons.lang3.builder.HashCodeBuilder, boolean, java.lang.String[]):void");
    }

    static Set<IDKey> getRegistry() {
        return (Set) REGISTRY.get();
    }

    static boolean isRegistered(Object value) {
        Set<IDKey> registry = getRegistry();
        return registry != null && registry.contains(new IDKey(value));
    }

    public static int reflectionHashCode(int initialNonZeroOddNumber, int multiplierNonZeroOddNumber, Object object) {
        return reflectionHashCode(initialNonZeroOddNumber, multiplierNonZeroOddNumber, object, false, null, new String[0]);
    }

    public static int reflectionHashCode(int initialNonZeroOddNumber, int multiplierNonZeroOddNumber, Object object, boolean testTransients) {
        return reflectionHashCode(initialNonZeroOddNumber, multiplierNonZeroOddNumber, object, testTransients, null, new String[0]);
    }

    public static <T> int reflectionHashCode(int initialNonZeroOddNumber, int multiplierNonZeroOddNumber, T object, boolean testTransients, Class<? super T> reflectUpToClass, String... excludeFields) {
        Validate.isTrue(object != null, "The object to build a hash code for must not be null", new Object[0]);
        HashCodeBuilder builder = new HashCodeBuilder(initialNonZeroOddNumber, multiplierNonZeroOddNumber);
        Class<?> clazz = object.getClass();
        reflectionAppend(object, clazz, builder, testTransients, excludeFields);
        while (clazz.getSuperclass() != null && clazz != reflectUpToClass) {
            clazz = clazz.getSuperclass();
            reflectionAppend(object, clazz, builder, testTransients, excludeFields);
        }
        return builder.toHashCode();
    }

    public static int reflectionHashCode(Object object, boolean testTransients) {
        return reflectionHashCode(17, 37, object, testTransients, null, new String[0]);
    }

    public static int reflectionHashCode(Object object, Collection<String> excludeFields) {
        return reflectionHashCode(object, ReflectionToStringBuilder.toNoNullStringArray((Collection) excludeFields));
    }

    public static int reflectionHashCode(Object object, String... excludeFields) {
        return reflectionHashCode(17, 37, object, false, null, excludeFields);
    }

    private static void register(Object value) {
        Set<IDKey> registry = getRegistry();
        if (registry == null) {
            registry = new HashSet();
            REGISTRY.set(registry);
        }
        registry.add(new IDKey(value));
    }

    private static void unregister(Object value) {
        Set<IDKey> registry = getRegistry();
        if (registry != null) {
            registry.remove(new IDKey(value));
            if (registry.isEmpty()) {
                REGISTRY.remove();
            }
        }
    }

    public HashCodeBuilder() {
        this.iTotal = 0;
        this.iConstant = 37;
        this.iTotal = 17;
    }

    public HashCodeBuilder(int initialOddNumber, int multiplierOddNumber) {
        this.iTotal = 0;
        boolean z = true;
        Validate.isTrue(initialOddNumber % 2 != 0, "HashCodeBuilder requires an odd initial value", new Object[0]);
        if (multiplierOddNumber % 2 == 0) {
            z = false;
        }
        Validate.isTrue(z, "HashCodeBuilder requires an odd multiplier", new Object[0]);
        this.iConstant = multiplierOddNumber;
        this.iTotal = initialOddNumber;
    }

    public HashCodeBuilder append(boolean value) {
        this.iTotal = (this.iTotal * this.iConstant) + (value ^ 1);
        return this;
    }

    public HashCodeBuilder append(boolean[] array) {
        if (array == null) {
            this.iTotal *= this.iConstant;
        } else {
            for (boolean element : array) {
                append(element);
            }
        }
        return this;
    }

    public HashCodeBuilder append(byte value) {
        this.iTotal = (this.iTotal * this.iConstant) + value;
        return this;
    }

    public HashCodeBuilder append(byte[] array) {
        if (array == null) {
            this.iTotal *= this.iConstant;
        } else {
            for (byte element : array) {
                append(element);
            }
        }
        return this;
    }

    public HashCodeBuilder append(char value) {
        this.iTotal = (this.iTotal * this.iConstant) + value;
        return this;
    }

    public HashCodeBuilder append(char[] array) {
        if (array == null) {
            this.iTotal *= this.iConstant;
        } else {
            for (char element : array) {
                append(element);
            }
        }
        return this;
    }

    public HashCodeBuilder append(double value) {
        return append(Double.doubleToLongBits(value));
    }

    public HashCodeBuilder append(double[] array) {
        if (array == null) {
            this.iTotal *= this.iConstant;
        } else {
            for (double element : array) {
                append(element);
            }
        }
        return this;
    }

    public HashCodeBuilder append(float value) {
        this.iTotal = (this.iTotal * this.iConstant) + Float.floatToIntBits(value);
        return this;
    }

    public HashCodeBuilder append(float[] array) {
        if (array == null) {
            this.iTotal *= this.iConstant;
        } else {
            for (float element : array) {
                append(element);
            }
        }
        return this;
    }

    public HashCodeBuilder append(int value) {
        this.iTotal = (this.iTotal * this.iConstant) + value;
        return this;
    }

    public HashCodeBuilder append(int[] array) {
        if (array == null) {
            this.iTotal *= this.iConstant;
        } else {
            for (int element : array) {
                append(element);
            }
        }
        return this;
    }

    public HashCodeBuilder append(long value) {
        this.iTotal = (this.iTotal * this.iConstant) + ((int) ((value >> 32) ^ value));
        return this;
    }

    public HashCodeBuilder append(long[] array) {
        if (array == null) {
            this.iTotal *= this.iConstant;
        } else {
            for (long element : array) {
                append(element);
            }
        }
        return this;
    }

    public HashCodeBuilder append(Object object) {
        if (object == null) {
            this.iTotal *= this.iConstant;
        } else if (object.getClass().isArray()) {
            appendArray(object);
        } else {
            this.iTotal = (this.iTotal * this.iConstant) + object.hashCode();
        }
        return this;
    }

    private void appendArray(Object object) {
        if (object instanceof long[]) {
            append((long[]) object);
        } else if (object instanceof int[]) {
            append((int[]) object);
        } else if (object instanceof short[]) {
            append((short[]) object);
        } else if (object instanceof char[]) {
            append((char[]) object);
        } else if (object instanceof byte[]) {
            append((byte[]) object);
        } else if (object instanceof double[]) {
            append((double[]) object);
        } else if (object instanceof float[]) {
            append((float[]) object);
        } else if (object instanceof boolean[]) {
            append((boolean[]) object);
        } else {
            append((Object[]) object);
        }
    }

    public HashCodeBuilder append(Object[] array) {
        if (array == null) {
            this.iTotal *= this.iConstant;
        } else {
            for (Object element : array) {
                append(element);
            }
        }
        return this;
    }

    public HashCodeBuilder append(short value) {
        this.iTotal = (this.iTotal * this.iConstant) + value;
        return this;
    }

    public HashCodeBuilder append(short[] array) {
        if (array == null) {
            this.iTotal *= this.iConstant;
        } else {
            for (short element : array) {
                append(element);
            }
        }
        return this;
    }

    public HashCodeBuilder appendSuper(int superHashCode) {
        this.iTotal = (this.iTotal * this.iConstant) + superHashCode;
        return this;
    }

    public int toHashCode() {
        return this.iTotal;
    }

    public Integer build() {
        return Integer.valueOf(toHashCode());
    }

    public int hashCode() {
        return toHashCode();
    }
}
