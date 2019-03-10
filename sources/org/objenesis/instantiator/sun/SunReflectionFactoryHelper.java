package org.objenesis.instantiator.sun;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.objenesis.ObjenesisException;

class SunReflectionFactoryHelper {
    SunReflectionFactoryHelper() {
    }

    public static <T> Constructor<T> newConstructorForSerialization(Class<T> type, Constructor<?> constructor) {
        Class<?> reflectionFactoryClass = getReflectionFactoryClass();
        Object reflectionFactory = createReflectionFactory(reflectionFactoryClass);
        try {
            return (Constructor) getNewConstructorForSerializationMethod(reflectionFactoryClass).invoke(reflectionFactory, new Object[]{type, constructor});
        } catch (Throwable e) {
            throw new ObjenesisException(e);
        } catch (Throwable e2) {
            throw new ObjenesisException(e2);
        } catch (Throwable e22) {
            throw new ObjenesisException(e22);
        }
    }

    private static Class<?> getReflectionFactoryClass() {
        try {
            return Class.forName("sun.reflect.ReflectionFactory");
        } catch (Throwable e) {
            throw new ObjenesisException(e);
        }
    }

    private static Object createReflectionFactory(Class<?> reflectionFactoryClass) {
        try {
            return reflectionFactoryClass.getDeclaredMethod("getReflectionFactory", new Class[0]).invoke(null, new Object[0]);
        } catch (Throwable e) {
            throw new ObjenesisException(e);
        } catch (Throwable e2) {
            throw new ObjenesisException(e2);
        } catch (Throwable e22) {
            throw new ObjenesisException(e22);
        } catch (Throwable e222) {
            throw new ObjenesisException(e222);
        }
    }

    private static Method getNewConstructorForSerializationMethod(Class<?> reflectionFactoryClass) {
        try {
            return reflectionFactoryClass.getDeclaredMethod("newConstructorForSerialization", new Class[]{Class.class, Constructor.class});
        } catch (Throwable e) {
            throw new ObjenesisException(e);
        }
    }
}
