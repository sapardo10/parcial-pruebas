package okhttp3.internal.platform;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class OptionalMethod<T> {
    private final String methodName;
    private final Class[] methodParams;
    private final Class<?> returnType;

    OptionalMethod(Class<?> returnType, String methodName, Class... methodParams) {
        this.returnType = returnType;
        this.methodName = methodName;
        this.methodParams = methodParams;
    }

    public boolean isSupported(T target) {
        return getMethod(target.getClass()) != null;
    }

    public Object invokeOptional(T target, Object... args) throws InvocationTargetException {
        Method m = getMethod(target.getClass());
        Object obj = null;
        if (m == null) {
            return null;
        }
        try {
            obj = m.invoke(target, args);
            return obj;
        } catch (IllegalAccessException e) {
            return obj;
        }
    }

    public Object invokeOptionalWithoutCheckedException(T target, Object... args) {
        try {
            return invokeOptional(target, args);
        } catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            if (targetException instanceof RuntimeException) {
                throw ((RuntimeException) targetException);
            }
            AssertionError error = new AssertionError("Unexpected exception");
            error.initCause(targetException);
            throw error;
        }
    }

    public Object invoke(T target, Object... args) throws InvocationTargetException {
        Method m = getMethod(target.getClass());
        if (m != null) {
            try {
                return m.invoke(target, args);
            } catch (IllegalAccessException e) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unexpectedly could not call: ");
                stringBuilder.append(m);
                AssertionError error = new AssertionError(stringBuilder.toString());
                error.initCause(e);
                throw error;
            }
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("Method ");
        stringBuilder2.append(this.methodName);
        stringBuilder2.append(" not supported for object ");
        stringBuilder2.append(target);
        throw new AssertionError(stringBuilder2.toString());
    }

    public Object invokeWithoutCheckedException(T target, Object... args) {
        try {
            return invoke(target, args);
        } catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            if (targetException instanceof RuntimeException) {
                throw ((RuntimeException) targetException);
            }
            AssertionError error = new AssertionError("Unexpected exception");
            error.initCause(targetException);
            throw error;
        }
    }

    private Method getMethod(Class<?> clazz) {
        String str = this.methodName;
        if (str == null) {
            return null;
        }
        Method method = getPublicMethod(clazz, str, this.methodParams);
        if (method != null) {
            Class cls = this.returnType;
            if (cls != null) {
                if (cls.isAssignableFrom(method.getReturnType())) {
                    return method;
                }
                return null;
            }
        }
        return method;
    }

    private static Method getPublicMethod(Class<?> clazz, String methodName, Class[] parameterTypes) {
        Method method = null;
        try {
            method = clazz.getMethod(methodName, parameterTypes);
            if ((method.getModifiers() & 1) == 0) {
                method = null;
            }
        } catch (NoSuchMethodException e) {
        }
        return method;
    }
}
