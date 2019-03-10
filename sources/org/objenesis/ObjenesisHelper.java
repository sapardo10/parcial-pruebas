package org.objenesis;

import java.io.Serializable;
import org.objenesis.instantiator.ObjectInstantiator;

public final class ObjenesisHelper {
    private static final Objenesis OBJENESIS_SERIALIZER = new ObjenesisSerializer();
    private static final Objenesis OBJENESIS_STD = new ObjenesisStd();

    private ObjenesisHelper() {
    }

    public static <T> T newInstance(Class<T> clazz) {
        return OBJENESIS_STD.newInstance(clazz);
    }

    public static <T extends Serializable> T newSerializableInstance(Class<T> clazz) {
        return (Serializable) OBJENESIS_SERIALIZER.newInstance(clazz);
    }

    public static <T> ObjectInstantiator<T> getInstantiatorOf(Class<T> clazz) {
        return OBJENESIS_STD.getInstantiatorOf(clazz);
    }

    public static <T extends Serializable> ObjectInstantiator<T> getSerializableObjectInstantiatorOf(Class<T> clazz) {
        return OBJENESIS_SERIALIZER.getInstantiatorOf(clazz);
    }
}
