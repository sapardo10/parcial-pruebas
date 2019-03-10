package io.reactivex.internal.functions;

import io.reactivex.functions.BiPredicate;

public final class ObjectHelper {
    static final BiPredicate<Object, Object> EQUALS = new BiObjectPredicate();

    static final class BiObjectPredicate implements BiPredicate<Object, Object> {
        BiObjectPredicate() {
        }

        public boolean test(Object o1, Object o2) {
            return ObjectHelper.equals(o1, o2);
        }
    }

    private ObjectHelper() {
        throw new IllegalStateException("No instances!");
    }

    public static <T> T requireNonNull(T object, String message) {
        if (object != null) {
            return object;
        }
        throw new NullPointerException(message);
    }

    public static boolean equals(Object o1, Object o2) {
        if (o1 != o2) {
            if (o1 == null || !o1.equals(o2)) {
                return false;
            }
        }
        return true;
    }

    public static int hashCode(Object o) {
        return o != null ? o.hashCode() : 0;
    }

    public static int compare(int v1, int v2) {
        if (v1 < v2) {
            return -1;
        }
        return v1 > v2 ? 1 : 0;
    }

    public static int compare(long v1, long v2) {
        if (v1 < v2) {
            return -1;
        }
        return v1 > v2 ? 1 : 0;
    }

    public static <T> BiPredicate<T, T> equalsPredicate() {
        return EQUALS;
    }

    public static int verifyPositive(int value, String paramName) {
        if (value > 0) {
            return value;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(paramName);
        stringBuilder.append(" > 0 required but it was ");
        stringBuilder.append(value);
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public static long verifyPositive(long value, String paramName) {
        if (value > 0) {
            return value;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(paramName);
        stringBuilder.append(" > 0 required but it was ");
        stringBuilder.append(value);
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    @Deprecated
    public static long requireNonNull(long value, String message) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Null check on a primitive: ");
        stringBuilder.append(message);
        throw new InternalError(stringBuilder.toString());
    }
}
