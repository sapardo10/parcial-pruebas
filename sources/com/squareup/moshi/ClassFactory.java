package com.squareup.moshi;

import com.squareup.moshi.internal.Util;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

abstract class ClassFactory<T> {

    /* renamed from: com.squareup.moshi.ClassFactory$1 */
    class C09891 extends ClassFactory<T> {
        final /* synthetic */ Constructor val$constructor;
        final /* synthetic */ Class val$rawType;

        C09891(Constructor constructor, Class cls) {
            this.val$constructor = constructor;
            this.val$rawType = cls;
        }

        public T newInstance() throws IllegalAccessException, InvocationTargetException, InstantiationException {
            return this.val$constructor.newInstance(null);
        }

        public String toString() {
            return this.val$rawType.getName();
        }
    }

    /* renamed from: com.squareup.moshi.ClassFactory$2 */
    class C09902 extends ClassFactory<T> {
        final /* synthetic */ Method val$allocateInstance;
        final /* synthetic */ Class val$rawType;
        final /* synthetic */ Object val$unsafe;

        C09902(Method method, Object obj, Class cls) {
            this.val$allocateInstance = method;
            this.val$unsafe = obj;
            this.val$rawType = cls;
        }

        public T newInstance() throws InvocationTargetException, IllegalAccessException {
            return this.val$allocateInstance.invoke(this.val$unsafe, new Object[]{this.val$rawType});
        }

        public String toString() {
            return this.val$rawType.getName();
        }
    }

    /* renamed from: com.squareup.moshi.ClassFactory$3 */
    class C09913 extends ClassFactory<T> {
        final /* synthetic */ int val$constructorId;
        final /* synthetic */ Method val$newInstance;
        final /* synthetic */ Class val$rawType;

        C09913(Method method, Class cls, int i) {
            this.val$newInstance = method;
            this.val$rawType = cls;
            this.val$constructorId = i;
        }

        public T newInstance() throws InvocationTargetException, IllegalAccessException {
            return this.val$newInstance.invoke(null, new Object[]{this.val$rawType, Integer.valueOf(this.val$constructorId)});
        }

        public String toString() {
            return this.val$rawType.getName();
        }
    }

    /* renamed from: com.squareup.moshi.ClassFactory$4 */
    class C09924 extends ClassFactory<T> {
        final /* synthetic */ Method val$newInstance;
        final /* synthetic */ Class val$rawType;

        C09924(Method method, Class cls) {
            this.val$newInstance = method;
            this.val$rawType = cls;
        }

        public T newInstance() throws InvocationTargetException, IllegalAccessException {
            return this.val$newInstance.invoke(null, new Object[]{this.val$rawType, Object.class});
        }

        public String toString() {
            return this.val$rawType.getName();
        }
    }

    abstract T newInstance() throws InvocationTargetException, IllegalAccessException, InstantiationException;

    ClassFactory() {
    }

    public static <T> ClassFactory<T> get(Class<?> rawType) {
        try {
            Constructor<?> constructor = rawType.getDeclaredConstructor(new Class[0]);
            constructor.setAccessible(true);
            return new C09891(constructor, rawType);
        } catch (NoSuchMethodException e) {
            try {
                Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
                Field f = unsafeClass.getDeclaredField("theUnsafe");
                f.setAccessible(true);
                return new C09902(unsafeClass.getMethod("allocateInstance", new Class[]{Class.class}), f.get(null), rawType);
            } catch (IllegalAccessException e2) {
                throw new AssertionError();
            } catch (ClassNotFoundException e3) {
                try {
                    Method getConstructorId = ObjectStreamClass.class.getDeclaredMethod("getConstructorId", new Class[]{Class.class});
                    getConstructorId.setAccessible(true);
                    int constructorId = ((Integer) getConstructorId.invoke(null, new Object[]{Object.class})).intValue();
                    Method newInstance = ObjectStreamClass.class.getDeclaredMethod("newInstance", new Class[]{Class.class, Integer.TYPE});
                    newInstance.setAccessible(true);
                    return new C09913(newInstance, rawType, constructorId);
                } catch (IllegalAccessException e4) {
                    throw new AssertionError();
                } catch (InvocationTargetException e5) {
                    throw Util.rethrowCause(e5);
                } catch (NoSuchMethodException e6) {
                    try {
                        Method newInstance2 = ObjectInputStream.class.getDeclaredMethod("newInstance", new Class[]{Class.class, Class.class});
                        newInstance2.setAccessible(true);
                        return new C09924(newInstance2, rawType);
                    } catch (Exception e7) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("cannot construct instances of ");
                        stringBuilder.append(rawType.getName());
                        throw new IllegalArgumentException(stringBuilder.toString());
                    }
                }
            }
        }
    }
}
