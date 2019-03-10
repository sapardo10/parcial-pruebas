package org.apache.commons.lang3.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.Validate;

public class ConstructorUtils {
    public static <T> T invokeConstructor(Class<T> cls, Object... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        args = ArrayUtils.nullToEmpty(args);
        return invokeConstructor(cls, args, ClassUtils.toClass(args));
    }

    public static <T> T invokeConstructor(Class<T> cls, Object[] args, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        args = ArrayUtils.nullToEmpty(args);
        Constructor<T> ctor = getMatchingAccessibleConstructor(cls, ArrayUtils.nullToEmpty((Class[]) parameterTypes));
        if (ctor != null) {
            if (ctor.isVarArgs()) {
                args = MethodUtils.getVarArgs(args, ctor.getParameterTypes());
            }
            return ctor.newInstance(args);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("No such accessible constructor on object: ");
        stringBuilder.append(cls.getName());
        throw new NoSuchMethodException(stringBuilder.toString());
    }

    public static <T> T invokeExactConstructor(Class<T> cls, Object... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        args = ArrayUtils.nullToEmpty(args);
        return invokeExactConstructor(cls, args, ClassUtils.toClass(args));
    }

    public static <T> T invokeExactConstructor(Class<T> cls, Object[] args, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        args = ArrayUtils.nullToEmpty(args);
        Constructor<T> ctor = getAccessibleConstructor(cls, ArrayUtils.nullToEmpty((Class[]) parameterTypes));
        if (ctor != null) {
            return ctor.newInstance(args);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("No such accessible constructor on object: ");
        stringBuilder.append(cls.getName());
        throw new NoSuchMethodException(stringBuilder.toString());
    }

    public static <T> Constructor<T> getAccessibleConstructor(Class<T> cls, Class<?>... parameterTypes) {
        Validate.notNull(cls, "class cannot be null", new Object[0]);
        try {
            return getAccessibleConstructor(cls.getConstructor(parameterTypes));
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static <T> Constructor<T> getAccessibleConstructor(Constructor<T> ctor) {
        Validate.notNull(ctor, "constructor cannot be null", new Object[0]);
        return (MemberUtils.isAccessible(ctor) && isAccessible(ctor.getDeclaringClass())) ? ctor : null;
    }

    public static <T> Constructor<T> getMatchingAccessibleConstructor(Class<T> cls, Class<?>... parameterTypes) {
        Constructor<T> ctor;
        Validate.notNull(cls, "class cannot be null", new Object[0]);
        try {
            ctor = cls.getConstructor(parameterTypes);
            MemberUtils.setAccessibleWorkaround(ctor);
            return ctor;
        } catch (NoSuchMethodException e) {
            ctor = null;
            for (Constructor<?> ctor2 : cls.getConstructors()) {
                Constructor<?> ctor22;
                if (MemberUtils.isMatchingConstructor(ctor22, parameterTypes)) {
                    ctor22 = getAccessibleConstructor(ctor22);
                    if (ctor22 != null) {
                        MemberUtils.setAccessibleWorkaround(ctor22);
                        if (ctor != null) {
                            if (MemberUtils.compareConstructorFit(ctor22, ctor, parameterTypes) < 0) {
                            }
                        }
                        ctor = ctor22;
                    }
                }
            }
            return ctor;
        }
    }

    private static boolean isAccessible(Class<?> type) {
        for (Class<?> cls = type; cls != null; cls = cls.getEnclosingClass()) {
            if (!Modifier.isPublic(cls.getModifiers())) {
                return false;
            }
        }
        return true;
    }
}
