package org.objenesis.strategy;

import java.io.NotSerializableException;
import java.io.Serializable;
import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.instantiator.android.AndroidSerializationInstantiator;
import org.objenesis.instantiator.basic.ObjectInputStreamInstantiator;
import org.objenesis.instantiator.basic.ObjectStreamClassInstantiator;
import org.objenesis.instantiator.gcj.GCJSerializationInstantiator;
import org.objenesis.instantiator.perc.PercSerializationInstantiator;
import org.objenesis.instantiator.sun.SunReflectionFactorySerializationInstantiator;

public class SerializingInstantiatorStrategy extends BaseInstantiatorStrategy {
    public <T> ObjectInstantiator<T> newInstantiatorOf(Class<T> type) {
        if (Serializable.class.isAssignableFrom(type)) {
            if (!PlatformDescription.JVM_NAME.startsWith("Java HotSpot")) {
                if (!PlatformDescription.isThisJVM(PlatformDescription.OPENJDK)) {
                    if (PlatformDescription.JVM_NAME.startsWith(PlatformDescription.DALVIK)) {
                        if (PlatformDescription.isAndroidOpenJDK()) {
                            return new ObjectStreamClassInstantiator(type);
                        }
                        return new AndroidSerializationInstantiator(type);
                    } else if (PlatformDescription.JVM_NAME.startsWith(PlatformDescription.GNU)) {
                        return new GCJSerializationInstantiator(type);
                    } else {
                        if (PlatformDescription.JVM_NAME.startsWith(PlatformDescription.PERC)) {
                            return new PercSerializationInstantiator(type);
                        }
                        return new SunReflectionFactorySerializationInstantiator(type);
                    }
                }
            }
            if (PlatformDescription.isGoogleAppEngine() && PlatformDescription.SPECIFICATION_VERSION.equals("1.7")) {
                return new ObjectInputStreamInstantiator(type);
            }
            return new SunReflectionFactorySerializationInstantiator(type);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(type);
        stringBuilder.append(" not serializable");
        throw new ObjenesisException(new NotSerializableException(stringBuilder.toString()));
    }
}
