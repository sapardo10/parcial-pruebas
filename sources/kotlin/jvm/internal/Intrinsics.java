package kotlin.jvm.internal;

import java.util.Arrays;
import java.util.List;
import kotlin.KotlinNullPointerException;
import kotlin.SinceKotlin;
import kotlin.UninitializedPropertyAccessException;
import org.apache.commons.io.IOUtils;

public class Intrinsics {
    private Intrinsics() {
    }

    public static String stringPlus(String self, Object other) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(self);
        stringBuilder.append(other);
        return stringBuilder.toString();
    }

    public static void checkNotNull(Object object) {
        if (object == null) {
            throwNpe();
        }
    }

    public static void checkNotNull(Object object, String message) {
        if (object == null) {
            throwNpe(message);
        }
    }

    public static void throwNpe() {
        throw ((KotlinNullPointerException) sanitizeStackTrace(new KotlinNullPointerException()));
    }

    public static void throwNpe(String message) {
        throw ((KotlinNullPointerException) sanitizeStackTrace(new KotlinNullPointerException(message)));
    }

    public static void throwUninitializedProperty(String message) {
        throw ((UninitializedPropertyAccessException) sanitizeStackTrace(new UninitializedPropertyAccessException(message)));
    }

    public static void throwUninitializedPropertyAccessException(String propertyName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("lateinit property ");
        stringBuilder.append(propertyName);
        stringBuilder.append(" has not been initialized");
        throwUninitializedProperty(stringBuilder.toString());
    }

    public static void throwAssert() {
        throw ((AssertionError) sanitizeStackTrace(new AssertionError()));
    }

    public static void throwAssert(String message) {
        throw ((AssertionError) sanitizeStackTrace(new AssertionError(message)));
    }

    public static void throwIllegalArgument() {
        throw ((IllegalArgumentException) sanitizeStackTrace(new IllegalArgumentException()));
    }

    public static void throwIllegalArgument(String message) {
        throw ((IllegalArgumentException) sanitizeStackTrace(new IllegalArgumentException(message)));
    }

    public static void throwIllegalState() {
        throw ((IllegalStateException) sanitizeStackTrace(new IllegalStateException()));
    }

    public static void throwIllegalState(String message) {
        throw ((IllegalStateException) sanitizeStackTrace(new IllegalStateException(message)));
    }

    public static void checkExpressionValueIsNotNull(Object value, String expression) {
        if (value == null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(expression);
            stringBuilder.append(" must not be null");
            throw ((IllegalStateException) sanitizeStackTrace(new IllegalStateException(stringBuilder.toString())));
        }
    }

    public static void checkNotNullExpressionValue(Object value, String message) {
        if (value == null) {
            throw ((IllegalStateException) sanitizeStackTrace(new IllegalStateException(message)));
        }
    }

    public static void checkReturnedValueIsNotNull(Object value, String className, String methodName) {
        if (value == null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Method specified as non-null returned null: ");
            stringBuilder.append(className);
            stringBuilder.append(".");
            stringBuilder.append(methodName);
            throw ((IllegalStateException) sanitizeStackTrace(new IllegalStateException(stringBuilder.toString())));
        }
    }

    public static void checkReturnedValueIsNotNull(Object value, String message) {
        if (value == null) {
            throw ((IllegalStateException) sanitizeStackTrace(new IllegalStateException(message)));
        }
    }

    public static void checkFieldIsNotNull(Object value, String className, String fieldName) {
        if (value == null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Field specified as non-null is null: ");
            stringBuilder.append(className);
            stringBuilder.append(".");
            stringBuilder.append(fieldName);
            throw ((IllegalStateException) sanitizeStackTrace(new IllegalStateException(stringBuilder.toString())));
        }
    }

    public static void checkFieldIsNotNull(Object value, String message) {
        if (value == null) {
            throw ((IllegalStateException) sanitizeStackTrace(new IllegalStateException(message)));
        }
    }

    public static void checkParameterIsNotNull(Object value, String paramName) {
        if (value == null) {
            throwParameterIsNullException(paramName);
        }
    }

    public static void checkNotNullParameter(Object value, String message) {
        if (value == null) {
            throw ((IllegalArgumentException) sanitizeStackTrace(new IllegalArgumentException(message)));
        }
    }

    private static void throwParameterIsNullException(String paramName) {
        StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
        String className = caller.getClassName();
        String methodName = caller.getMethodName();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Parameter specified as non-null is null: method ");
        stringBuilder.append(className);
        stringBuilder.append(".");
        stringBuilder.append(methodName);
        stringBuilder.append(", parameter ");
        stringBuilder.append(paramName);
        throw ((IllegalArgumentException) sanitizeStackTrace(new IllegalArgumentException(stringBuilder.toString())));
    }

    public static int compare(long thisVal, long anotherVal) {
        if (thisVal < anotherVal) {
            return -1;
        }
        return thisVal == anotherVal ? 0 : 1;
    }

    public static int compare(int thisVal, int anotherVal) {
        if (thisVal < anotherVal) {
            return -1;
        }
        return thisVal == anotherVal ? 0 : 1;
    }

    public static boolean areEqual(Object first, Object second) {
        if (first == null) {
            return second == null;
        } else {
            return first.equals(second);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @kotlin.SinceKotlin(version = "1.1")
    public static boolean areEqual(java.lang.Double r7, java.lang.Double r8) {
        /*
        r0 = 1;
        r1 = 0;
        if (r7 != 0) goto L_0x0007;
    L_0x0004:
        if (r8 != 0) goto L_0x0016;
    L_0x0006:
        goto L_0x0017;
    L_0x0007:
        if (r8 == 0) goto L_0x0016;
    L_0x0009:
        r2 = r7.doubleValue();
        r4 = r8.doubleValue();
        r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r6 != 0) goto L_0x0016;
    L_0x0015:
        goto L_0x0006;
    L_0x0016:
        r0 = 0;
    L_0x0017:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.jvm.internal.Intrinsics.areEqual(java.lang.Double, java.lang.Double):boolean");
    }

    @SinceKotlin(version = "1.1")
    public static boolean areEqual(Double first, double second) {
        return first != null && first.doubleValue() == second;
    }

    @SinceKotlin(version = "1.1")
    public static boolean areEqual(double first, Double second) {
        return second != null && first == second.doubleValue();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @kotlin.SinceKotlin(version = "1.1")
    public static boolean areEqual(java.lang.Float r4, java.lang.Float r5) {
        /*
        r0 = 1;
        r1 = 0;
        if (r4 != 0) goto L_0x0007;
    L_0x0004:
        if (r5 != 0) goto L_0x0016;
    L_0x0006:
        goto L_0x0017;
    L_0x0007:
        if (r5 == 0) goto L_0x0016;
    L_0x0009:
        r2 = r4.floatValue();
        r3 = r5.floatValue();
        r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1));
        if (r2 != 0) goto L_0x0016;
    L_0x0015:
        goto L_0x0006;
    L_0x0016:
        r0 = 0;
    L_0x0017:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.jvm.internal.Intrinsics.areEqual(java.lang.Float, java.lang.Float):boolean");
    }

    @SinceKotlin(version = "1.1")
    public static boolean areEqual(Float first, float second) {
        return first != null && first.floatValue() == second;
    }

    @SinceKotlin(version = "1.1")
    public static boolean areEqual(float first, Float second) {
        return second != null && first == second.floatValue();
    }

    public static void throwUndefinedForReified() {
        throwUndefinedForReified("This function has a reified type parameter and thus can only be inlined at compilation time, not called directly.");
    }

    public static void throwUndefinedForReified(String message) {
        throw new UnsupportedOperationException(message);
    }

    public static void reifiedOperationMarker(int id, String typeParameterIdentifier) {
        throwUndefinedForReified();
    }

    public static void reifiedOperationMarker(int id, String typeParameterIdentifier, String message) {
        throwUndefinedForReified(message);
    }

    public static void needClassReification() {
        throwUndefinedForReified();
    }

    public static void needClassReification(String message) {
        throwUndefinedForReified(message);
    }

    public static void checkHasClass(String internalName) throws ClassNotFoundException {
        String fqName = internalName.replace(IOUtils.DIR_SEPARATOR_UNIX, '.');
        try {
            Class.forName(fqName);
        } catch (ClassNotFoundException e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Class ");
            stringBuilder.append(fqName);
            stringBuilder.append(" is not found. Please update the Kotlin runtime to the latest version");
            throw ((ClassNotFoundException) sanitizeStackTrace(new ClassNotFoundException(stringBuilder.toString(), e)));
        }
    }

    public static void checkHasClass(String internalName, String requiredVersion) throws ClassNotFoundException {
        String fqName = internalName.replace(IOUtils.DIR_SEPARATOR_UNIX, '.');
        try {
            Class.forName(fqName);
        } catch (ClassNotFoundException e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Class ");
            stringBuilder.append(fqName);
            stringBuilder.append(" is not found: this code requires the Kotlin runtime of version at least ");
            stringBuilder.append(requiredVersion);
            throw ((ClassNotFoundException) sanitizeStackTrace(new ClassNotFoundException(stringBuilder.toString(), e)));
        }
    }

    private static <T extends Throwable> T sanitizeStackTrace(T throwable) {
        return sanitizeStackTrace(throwable, Intrinsics.class.getName());
    }

    static <T extends Throwable> T sanitizeStackTrace(T throwable, String classNameToDrop) {
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        int size = stackTrace.length;
        int lastIntrinsic = -1;
        for (int i = 0; i < size; i++) {
            if (classNameToDrop.equals(stackTrace[i].getClassName())) {
                lastIntrinsic = i;
            }
        }
        List<StackTraceElement> list = Arrays.asList(stackTrace).subList(lastIntrinsic + 1, size);
        throwable.setStackTrace((StackTraceElement[]) list.toArray(new StackTraceElement[list.size()]));
        return throwable;
    }
}
