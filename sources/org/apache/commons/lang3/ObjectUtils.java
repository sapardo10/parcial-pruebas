package org.apache.commons.lang3;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeSet;
import org.apache.commons.lang3.exception.CloneFailedException;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.text.StrBuilder;

public class ObjectUtils {
    public static final Null NULL = new Null();

    public static class Null implements Serializable {
        private static final long serialVersionUID = 7092611880189329093L;

        Null() {
        }

        private Object readResolve() {
            return ObjectUtils.NULL;
        }
    }

    public static <T> T defaultIfNull(T object, T defaultValue) {
        return object != null ? object : defaultValue;
    }

    @SafeVarargs
    public static <T> T firstNonNull(T... values) {
        if (values != null) {
            for (T val : values) {
                if (val != null) {
                    return val;
                }
            }
        }
        return null;
    }

    public static boolean anyNotNull(Object... values) {
        return firstNonNull(values) != null;
    }

    public static boolean allNotNull(Object... values) {
        if (values == null) {
            return false;
        }
        for (Object val : values) {
            if (val == null) {
                return false;
            }
        }
        return true;
    }

    @Deprecated
    public static boolean equals(Object object1, Object object2) {
        if (object1 == object2) {
            return true;
        }
        if (object1 != null) {
            if (object2 != null) {
                return object1.equals(object2);
            }
        }
        return false;
    }

    public static boolean notEqual(Object object1, Object object2) {
        return equals(object1, object2) ^ 1;
    }

    @Deprecated
    public static int hashCode(Object obj) {
        return obj == null ? 0 : obj.hashCode();
    }

    @Deprecated
    public static int hashCodeMulti(Object... objects) {
        int hash = 1;
        if (objects != null) {
            for (Object object : objects) {
                hash = (hash * 31) + hashCode(object);
            }
        }
        return hash;
    }

    public static String identityToString(Object object) {
        if (object == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        identityToString(builder, object);
        return builder.toString();
    }

    public static void identityToString(Appendable appendable, Object object) throws IOException {
        Validate.notNull(object, "Cannot get the toString of a null identity", new Object[0]);
        appendable.append(object.getClass().getName()).append('@').append(Integer.toHexString(System.identityHashCode(object)));
    }

    @Deprecated
    public static void identityToString(StrBuilder builder, Object object) {
        Validate.notNull(object, "Cannot get the toString of a null identity", new Object[0]);
        builder.append(object.getClass().getName()).append('@').append(Integer.toHexString(System.identityHashCode(object)));
    }

    public static void identityToString(StringBuffer buffer, Object object) {
        Validate.notNull(object, "Cannot get the toString of a null identity", new Object[0]);
        buffer.append(object.getClass().getName());
        buffer.append('@');
        buffer.append(Integer.toHexString(System.identityHashCode(object)));
    }

    public static void identityToString(StringBuilder builder, Object object) {
        Validate.notNull(object, "Cannot get the toString of a null identity", new Object[0]);
        builder.append(object.getClass().getName());
        builder.append('@');
        builder.append(Integer.toHexString(System.identityHashCode(object)));
    }

    @Deprecated
    public static String toString(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    @Deprecated
    public static String toString(Object obj, String nullStr) {
        return obj == null ? nullStr : obj.toString();
    }

    @SafeVarargs
    public static <T extends Comparable<? super T>> T min(T... values) {
        T result = null;
        if (values != null) {
            for (T value : values) {
                if (compare(value, result, true) < 0) {
                    result = value;
                }
            }
        }
        return result;
    }

    @SafeVarargs
    public static <T extends Comparable<? super T>> T max(T... values) {
        T result = null;
        if (values != null) {
            for (T value : values) {
                if (compare(value, result, false) > 0) {
                    result = value;
                }
            }
        }
        return result;
    }

    public static <T extends Comparable<? super T>> int compare(T c1, T c2) {
        return compare(c1, c2, false);
    }

    public static <T extends Comparable<? super T>> int compare(T c1, T c2, boolean nullGreater) {
        if (c1 == c2) {
            return 0;
        }
        int i = 1;
        if (c1 == null) {
            if (!nullGreater) {
                i = -1;
            }
            return i;
        } else if (c2 != null) {
            return c1.compareTo(c2);
        } else {
            if (nullGreater) {
                i = -1;
            }
            return i;
        }
    }

    @SafeVarargs
    public static <T extends Comparable<? super T>> T median(T... items) {
        Validate.notEmpty((Object[]) items);
        Validate.noNullElements((Object[]) items);
        TreeSet<T> sort = new TreeSet();
        Collections.addAll(sort, items);
        return (Comparable) sort.toArray()[(sort.size() - 1) / 2];
    }

    @SafeVarargs
    public static <T> T median(Comparator<T> comparator, T... items) {
        Validate.notEmpty((Object[]) items, "null/empty items", new Object[0]);
        Validate.noNullElements((Object[]) items);
        Validate.notNull(comparator, "null comparator", new Object[0]);
        TreeSet<T> sort = new TreeSet(comparator);
        Collections.addAll(sort, items);
        return sort.toArray()[(sort.size() - 1) / 2];
    }

    @SafeVarargs
    public static <T> T mode(T... items) {
        if (!ArrayUtils.isNotEmpty((Object[]) items)) {
            return null;
        }
        HashMap<T, MutableInt> occurrences = new HashMap(items.length);
        for (T t : items) {
            MutableInt count = (MutableInt) occurrences.get(t);
            if (count == null) {
                occurrences.put(t, new MutableInt(1));
            } else {
                count.increment();
            }
        }
        T result = null;
        int len$ = 0;
        for (Entry<T, MutableInt> e : occurrences.entrySet()) {
            int cmp = ((MutableInt) e.getValue()).intValue();
            if (cmp == len$) {
                result = null;
            } else if (cmp > len$) {
                len$ = cmp;
                result = e.getKey();
            }
        }
        return result;
    }

    public static <T> T clone(T obj) {
        StringBuilder stringBuilder;
        if (!(obj instanceof Cloneable)) {
            return null;
        }
        T result;
        if (obj.getClass().isArray()) {
            Class<?> componentType = obj.getClass().getComponentType();
            if (componentType.isPrimitive()) {
                int length = Array.getLength(obj);
                T result2 = Array.newInstance(componentType, length);
                while (true) {
                    int length2 = length - 1;
                    if (length <= 0) {
                        break;
                    }
                    Array.set(result2, length2, Array.get(obj, length2));
                    length = length2;
                }
                result = result2;
            } else {
                result = ((Object[]) obj).clone();
            }
        } else {
            try {
                result = obj.getClass().getMethod("clone", new Class[0]).invoke(obj, new Object[0]);
            } catch (NoSuchMethodException e) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("Cloneable type ");
                stringBuilder.append(obj.getClass().getName());
                stringBuilder.append(" has no clone method");
                throw new CloneFailedException(stringBuilder.toString(), e);
            } catch (IllegalAccessException e2) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("Cannot clone Cloneable type ");
                stringBuilder.append(obj.getClass().getName());
                throw new CloneFailedException(stringBuilder.toString(), e2);
            } catch (InvocationTargetException e3) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("Exception cloning Cloneable type ");
                stringBuilder.append(obj.getClass().getName());
                throw new CloneFailedException(stringBuilder.toString(), e3.getCause());
            }
        }
        return result;
    }

    public static <T> T cloneIfPossible(T obj) {
        T clone = clone(obj);
        return clone == null ? obj : clone;
    }

    public static boolean CONST(boolean v) {
        return v;
    }

    public static byte CONST(byte v) {
        return v;
    }

    public static byte CONST_BYTE(int v) throws IllegalArgumentException {
        if (v >= -128 && v <= 127) {
            return (byte) v;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Supplied value must be a valid byte literal between -128 and 127: [");
        stringBuilder.append(v);
        stringBuilder.append("]");
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public static char CONST(char v) {
        return v;
    }

    public static short CONST(short v) {
        return v;
    }

    public static short CONST_SHORT(int v) throws IllegalArgumentException {
        if (v >= -32768 && v <= 32767) {
            return (short) v;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Supplied value must be a valid byte literal between -32768 and 32767: [");
        stringBuilder.append(v);
        stringBuilder.append("]");
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public static int CONST(int v) {
        return v;
    }

    public static long CONST(long v) {
        return v;
    }

    public static float CONST(float v) {
        return v;
    }

    public static double CONST(double v) {
        return v;
    }

    public static <T> T CONST(T v) {
        return v;
    }
}
