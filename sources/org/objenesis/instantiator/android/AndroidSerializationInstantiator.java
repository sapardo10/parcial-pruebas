package org.objenesis.instantiator.android;

import java.io.ObjectStreamClass;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.instantiator.annotations.Instantiator;
import org.objenesis.instantiator.annotations.Typology;

@Instantiator(Typology.SERIALIZATION)
public class AndroidSerializationInstantiator<T> implements ObjectInstantiator<T> {
    private final Method newInstanceMethod = getNewInstanceMethod();
    private final ObjectStreamClass objectStreamClass;
    private final Class<T> type;

    public AndroidSerializationInstantiator(Class<T> type) {
        this.type = type;
        try {
            try {
                this.objectStreamClass = (ObjectStreamClass) ObjectStreamClass.class.getMethod("lookupAny", new Class[]{Class.class}).invoke(null, new Object[]{type});
            } catch (IllegalAccessException e) {
                throw new ObjenesisException(e);
            } catch (InvocationTargetException e2) {
                throw new ObjenesisException(e2);
            }
        } catch (NoSuchMethodException e3) {
            throw new ObjenesisException(e3);
        }
    }

    public T newInstance() {
        try {
            return this.type.cast(this.newInstanceMethod.invoke(this.objectStreamClass, new Object[]{this.type}));
        } catch (IllegalAccessException e) {
            throw new ObjenesisException(e);
        } catch (IllegalArgumentException e2) {
            throw new ObjenesisException(e2);
        } catch (InvocationTargetException e3) {
            throw new ObjenesisException(e3);
        }
    }

    private static Method getNewInstanceMethod() {
        try {
            Method newInstanceMethod = ObjectStreamClass.class.getDeclaredMethod("newInstance", new Class[]{Class.class});
            newInstanceMethod.setAccessible(true);
            return newInstanceMethod;
        } catch (RuntimeException e) {
            throw new ObjenesisException(e);
        } catch (NoSuchMethodException e2) {
            throw new ObjenesisException(e2);
        }
    }
}
