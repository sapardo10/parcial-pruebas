package org.apache.commons.lang3.builder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.tuple.Pair;

public class EqualsBuilder implements Builder<Boolean> {
    private static final ThreadLocal<Set<Pair<IDKey, IDKey>>> REGISTRY = new ThreadLocal();
    private String[] excludeFields = null;
    private boolean isEquals = true;
    private Class<?> reflectUpToClass = null;
    private boolean testRecursive = false;
    private boolean testTransients = false;

    private void reflectionAppend(java.lang.Object r7, java.lang.Object r8, java.lang.Class<?> r9) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:35:0x0077 in {2, 16, 23, 27, 28, 29, 31, 34} preds:[]
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
        r6 = this;
        r0 = isRegistered(r7, r8);
        if (r0 == 0) goto L_0x0007;
    L_0x0006:
        return;
    L_0x0007:
        register(r7, r8);	 Catch:{ all -> 0x0072 }
        r0 = r9.getDeclaredFields();	 Catch:{ all -> 0x0072 }
        r1 = 1;	 Catch:{ all -> 0x0072 }
        java.lang.reflect.AccessibleObject.setAccessible(r0, r1);	 Catch:{ all -> 0x0072 }
        r1 = 0;	 Catch:{ all -> 0x0072 }
    L_0x0013:
        r2 = r0.length;	 Catch:{ all -> 0x0072 }
        if (r1 >= r2) goto L_0x006d;	 Catch:{ all -> 0x0072 }
    L_0x0016:
        r2 = r6.isEquals;	 Catch:{ all -> 0x0072 }
        if (r2 == 0) goto L_0x006d;	 Catch:{ all -> 0x0072 }
    L_0x001a:
        r2 = r0[r1];	 Catch:{ all -> 0x0072 }
        r3 = r6.excludeFields;	 Catch:{ all -> 0x0072 }
        r4 = r2.getName();	 Catch:{ all -> 0x0072 }
        r3 = org.apache.commons.lang3.ArrayUtils.contains(r3, r4);	 Catch:{ all -> 0x0072 }
        if (r3 != 0) goto L_0x0069;	 Catch:{ all -> 0x0072 }
    L_0x0028:
        r3 = r2.getName();	 Catch:{ all -> 0x0072 }
        r4 = "$";	 Catch:{ all -> 0x0072 }
        r3 = r3.contains(r4);	 Catch:{ all -> 0x0072 }
        if (r3 != 0) goto L_0x0069;	 Catch:{ all -> 0x0072 }
    L_0x0034:
        r3 = r6.testTransients;	 Catch:{ all -> 0x0072 }
        if (r3 != 0) goto L_0x0042;	 Catch:{ all -> 0x0072 }
    L_0x0038:
        r3 = r2.getModifiers();	 Catch:{ all -> 0x0072 }
        r3 = java.lang.reflect.Modifier.isTransient(r3);	 Catch:{ all -> 0x0072 }
        if (r3 != 0) goto L_0x0069;	 Catch:{ all -> 0x0072 }
    L_0x0042:
        r3 = r2.getModifiers();	 Catch:{ all -> 0x0072 }
        r3 = java.lang.reflect.Modifier.isStatic(r3);	 Catch:{ all -> 0x0072 }
        if (r3 != 0) goto L_0x0069;	 Catch:{ all -> 0x0072 }
    L_0x004c:
        r3 = org.apache.commons.lang3.builder.EqualsExclude.class;	 Catch:{ all -> 0x0072 }
        r3 = r2.isAnnotationPresent(r3);	 Catch:{ all -> 0x0072 }
        if (r3 != 0) goto L_0x0069;
    L_0x0054:
        r3 = r2.get(r7);	 Catch:{ IllegalAccessException -> 0x0060 }
        r4 = r2.get(r8);	 Catch:{ IllegalAccessException -> 0x0060 }
        r6.append(r3, r4);	 Catch:{ IllegalAccessException -> 0x0060 }
        goto L_0x006a;
    L_0x0060:
        r3 = move-exception;
        r4 = new java.lang.InternalError;	 Catch:{ all -> 0x0072 }
        r5 = "Unexpected IllegalAccessException";	 Catch:{ all -> 0x0072 }
        r4.<init>(r5);	 Catch:{ all -> 0x0072 }
        throw r4;	 Catch:{ all -> 0x0072 }
    L_0x006a:
        r1 = r1 + 1;
        goto L_0x0013;
    L_0x006d:
        unregister(r7, r8);
        return;
    L_0x0072:
        r0 = move-exception;
        unregister(r7, r8);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.builder.EqualsBuilder.reflectionAppend(java.lang.Object, java.lang.Object, java.lang.Class):void");
    }

    static Set<Pair<IDKey, IDKey>> getRegistry() {
        return (Set) REGISTRY.get();
    }

    static Pair<IDKey, IDKey> getRegisterPair(Object lhs, Object rhs) {
        return Pair.of(new IDKey(lhs), new IDKey(rhs));
    }

    static boolean isRegistered(Object lhs, Object rhs) {
        Set<Pair<IDKey, IDKey>> registry = getRegistry();
        Pair<IDKey, IDKey> pair = getRegisterPair(lhs, rhs);
        return registry != null && (registry.contains(pair) || registry.contains(Pair.of(pair.getRight(), pair.getLeft())));
    }

    private static void register(Object lhs, Object rhs) {
        Set<Pair<IDKey, IDKey>> registry = getRegistry();
        if (registry == null) {
            registry = new HashSet();
            REGISTRY.set(registry);
        }
        registry.add(getRegisterPair(lhs, rhs));
    }

    private static void unregister(Object lhs, Object rhs) {
        Set<Pair<IDKey, IDKey>> registry = getRegistry();
        if (registry != null) {
            registry.remove(getRegisterPair(lhs, rhs));
            if (registry.isEmpty()) {
                REGISTRY.remove();
            }
        }
    }

    public EqualsBuilder setTestTransients(boolean testTransients) {
        this.testTransients = testTransients;
        return this;
    }

    public EqualsBuilder setTestRecursive(boolean testRecursive) {
        this.testRecursive = testRecursive;
        return this;
    }

    public EqualsBuilder setReflectUpToClass(Class<?> reflectUpToClass) {
        this.reflectUpToClass = reflectUpToClass;
        return this;
    }

    public EqualsBuilder setExcludeFields(String... excludeFields) {
        this.excludeFields = excludeFields;
        return this;
    }

    public static boolean reflectionEquals(Object lhs, Object rhs, Collection<String> excludeFields) {
        return reflectionEquals(lhs, rhs, ReflectionToStringBuilder.toNoNullStringArray((Collection) excludeFields));
    }

    public static boolean reflectionEquals(Object lhs, Object rhs, String... excludeFields) {
        return reflectionEquals(lhs, rhs, false, null, excludeFields);
    }

    public static boolean reflectionEquals(Object lhs, Object rhs, boolean testTransients) {
        return reflectionEquals(lhs, rhs, testTransients, null, new String[0]);
    }

    public static boolean reflectionEquals(Object lhs, Object rhs, boolean testTransients, Class<?> reflectUpToClass, String... excludeFields) {
        return reflectionEquals(lhs, rhs, testTransients, reflectUpToClass, false, excludeFields);
    }

    public static boolean reflectionEquals(Object lhs, Object rhs, boolean testTransients, Class<?> reflectUpToClass, boolean testRecursive, String... excludeFields) {
        if (lhs == rhs) {
            return true;
        }
        if (lhs != null) {
            if (rhs != null) {
                return new EqualsBuilder().setExcludeFields(excludeFields).setReflectUpToClass(reflectUpToClass).setTestTransients(testTransients).setTestRecursive(testRecursive).reflectionAppend(lhs, rhs).isEquals();
            }
        }
        return false;
    }

    public EqualsBuilder reflectionAppend(Object lhs, Object rhs) {
        if (!this.isEquals || lhs == rhs) {
            return this;
        }
        if (lhs != null) {
            if (rhs != null) {
                Class<?> testClass;
                Class<?> lhsClass = lhs.getClass();
                Class<?> rhsClass = rhs.getClass();
                if (lhsClass.isInstance(rhs)) {
                    testClass = lhsClass;
                    if (!rhsClass.isInstance(lhs)) {
                        testClass = rhsClass;
                    }
                } else if (rhsClass.isInstance(lhs)) {
                    testClass = rhsClass;
                    if (!lhsClass.isInstance(rhs)) {
                        testClass = lhsClass;
                    }
                } else {
                    this.isEquals = false;
                    return this;
                }
                try {
                    if (testClass.isArray()) {
                        append(lhs, rhs);
                    } else {
                        reflectionAppend(lhs, rhs, testClass);
                        while (testClass.getSuperclass() != null && testClass != this.reflectUpToClass) {
                            testClass = testClass.getSuperclass();
                            reflectionAppend(lhs, rhs, testClass);
                        }
                    }
                    return this;
                } catch (IllegalArgumentException e) {
                    this.isEquals = false;
                    return this;
                }
            }
        }
        this.isEquals = false;
        return this;
    }

    public EqualsBuilder appendSuper(boolean superEquals) {
        if (!this.isEquals) {
            return this;
        }
        this.isEquals = superEquals;
        return this;
    }

    public EqualsBuilder append(Object lhs, Object rhs) {
        if (!this.isEquals || lhs == rhs) {
            return this;
        }
        if (lhs != null) {
            if (rhs != null) {
                Class<?> lhsClass = lhs.getClass();
                if (lhsClass.isArray()) {
                    appendArray(lhs, rhs);
                } else if (!this.testRecursive || ClassUtils.isPrimitiveOrWrapper(lhsClass)) {
                    this.isEquals = lhs.equals(rhs);
                } else {
                    reflectionAppend(lhs, rhs);
                }
                return this;
            }
        }
        setEquals(false);
        return this;
    }

    private void appendArray(Object lhs, Object rhs) {
        if (lhs.getClass() != rhs.getClass()) {
            setEquals(false);
        } else if (lhs instanceof long[]) {
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
            append((Object[]) lhs, (Object[]) rhs);
        }
    }

    public EqualsBuilder append(long lhs, long rhs) {
        if (!this.isEquals) {
            return this;
        }
        this.isEquals = lhs == rhs;
        return this;
    }

    public EqualsBuilder append(int lhs, int rhs) {
        if (!this.isEquals) {
            return this;
        }
        this.isEquals = lhs == rhs;
        return this;
    }

    public EqualsBuilder append(short lhs, short rhs) {
        if (!this.isEquals) {
            return this;
        }
        this.isEquals = lhs == rhs;
        return this;
    }

    public EqualsBuilder append(char lhs, char rhs) {
        if (!this.isEquals) {
            return this;
        }
        this.isEquals = lhs == rhs;
        return this;
    }

    public EqualsBuilder append(byte lhs, byte rhs) {
        if (!this.isEquals) {
            return this;
        }
        this.isEquals = lhs == rhs;
        return this;
    }

    public EqualsBuilder append(double lhs, double rhs) {
        if (this.isEquals) {
            return append(Double.doubleToLongBits(lhs), Double.doubleToLongBits(rhs));
        }
        return this;
    }

    public EqualsBuilder append(float lhs, float rhs) {
        if (this.isEquals) {
            return append(Float.floatToIntBits(lhs), Float.floatToIntBits(rhs));
        }
        return this;
    }

    public EqualsBuilder append(boolean lhs, boolean rhs) {
        if (!this.isEquals) {
            return this;
        }
        this.isEquals = lhs == rhs;
        return this;
    }

    public EqualsBuilder append(Object[] lhs, Object[] rhs) {
        if (!this.isEquals || lhs == rhs) {
            return this;
        }
        if (lhs != null) {
            if (rhs != null) {
                if (lhs.length != rhs.length) {
                    setEquals(false);
                    return this;
                }
                for (int i = 0; i < lhs.length && this.isEquals; i++) {
                    append(lhs[i], rhs[i]);
                }
                return this;
            }
        }
        setEquals(false);
        return this;
    }

    public EqualsBuilder append(long[] lhs, long[] rhs) {
        if (!this.isEquals || lhs == rhs) {
            return this;
        }
        if (lhs != null) {
            if (rhs != null) {
                if (lhs.length != rhs.length) {
                    setEquals(false);
                    return this;
                }
                for (int i = 0; i < lhs.length && this.isEquals; i++) {
                    append(lhs[i], rhs[i]);
                }
                return this;
            }
        }
        setEquals(false);
        return this;
    }

    public EqualsBuilder append(int[] lhs, int[] rhs) {
        if (!this.isEquals || lhs == rhs) {
            return this;
        }
        if (lhs != null) {
            if (rhs != null) {
                if (lhs.length != rhs.length) {
                    setEquals(false);
                    return this;
                }
                for (int i = 0; i < lhs.length && this.isEquals; i++) {
                    append(lhs[i], rhs[i]);
                }
                return this;
            }
        }
        setEquals(false);
        return this;
    }

    public EqualsBuilder append(short[] lhs, short[] rhs) {
        if (!this.isEquals || lhs == rhs) {
            return this;
        }
        if (lhs != null) {
            if (rhs != null) {
                if (lhs.length != rhs.length) {
                    setEquals(false);
                    return this;
                }
                for (int i = 0; i < lhs.length && this.isEquals; i++) {
                    append(lhs[i], rhs[i]);
                }
                return this;
            }
        }
        setEquals(false);
        return this;
    }

    public EqualsBuilder append(char[] lhs, char[] rhs) {
        if (!this.isEquals || lhs == rhs) {
            return this;
        }
        if (lhs != null) {
            if (rhs != null) {
                if (lhs.length != rhs.length) {
                    setEquals(false);
                    return this;
                }
                for (int i = 0; i < lhs.length && this.isEquals; i++) {
                    append(lhs[i], rhs[i]);
                }
                return this;
            }
        }
        setEquals(false);
        return this;
    }

    public EqualsBuilder append(byte[] lhs, byte[] rhs) {
        if (!this.isEquals || lhs == rhs) {
            return this;
        }
        if (lhs != null) {
            if (rhs != null) {
                if (lhs.length != rhs.length) {
                    setEquals(false);
                    return this;
                }
                for (int i = 0; i < lhs.length && this.isEquals; i++) {
                    append(lhs[i], rhs[i]);
                }
                return this;
            }
        }
        setEquals(false);
        return this;
    }

    public EqualsBuilder append(double[] lhs, double[] rhs) {
        if (!this.isEquals || lhs == rhs) {
            return this;
        }
        if (lhs != null) {
            if (rhs != null) {
                if (lhs.length != rhs.length) {
                    setEquals(false);
                    return this;
                }
                for (int i = 0; i < lhs.length && this.isEquals; i++) {
                    append(lhs[i], rhs[i]);
                }
                return this;
            }
        }
        setEquals(false);
        return this;
    }

    public EqualsBuilder append(float[] lhs, float[] rhs) {
        if (!this.isEquals || lhs == rhs) {
            return this;
        }
        if (lhs != null) {
            if (rhs != null) {
                if (lhs.length != rhs.length) {
                    setEquals(false);
                    return this;
                }
                for (int i = 0; i < lhs.length && this.isEquals; i++) {
                    append(lhs[i], rhs[i]);
                }
                return this;
            }
        }
        setEquals(false);
        return this;
    }

    public EqualsBuilder append(boolean[] lhs, boolean[] rhs) {
        if (!this.isEquals || lhs == rhs) {
            return this;
        }
        if (lhs != null) {
            if (rhs != null) {
                if (lhs.length != rhs.length) {
                    setEquals(false);
                    return this;
                }
                for (int i = 0; i < lhs.length && this.isEquals; i++) {
                    append(lhs[i], rhs[i]);
                }
                return this;
            }
        }
        setEquals(false);
        return this;
    }

    public boolean isEquals() {
        return this.isEquals;
    }

    public Boolean build() {
        return Boolean.valueOf(isEquals());
    }

    protected void setEquals(boolean isEquals) {
        this.isEquals = isEquals;
    }

    public void reset() {
        this.isEquals = true;
    }
}
