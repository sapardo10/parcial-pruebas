package org.objenesis.strategy;

import java.io.Serializable;
import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.instantiator.android.Android10Instantiator;
import org.objenesis.instantiator.android.Android17Instantiator;
import org.objenesis.instantiator.android.Android18Instantiator;
import org.objenesis.instantiator.basic.AccessibleInstantiator;
import org.objenesis.instantiator.basic.ObjectInputStreamInstantiator;
import org.objenesis.instantiator.gcj.GCJInstantiator;
import org.objenesis.instantiator.perc.PercInstantiator;
import org.objenesis.instantiator.sun.SunReflectionFactoryInstantiator;
import org.objenesis.instantiator.sun.UnsafeFactoryInstantiator;

public class StdInstantiatorStrategy extends BaseInstantiatorStrategy {
    public <T> ObjectInstantiator<T> newInstantiatorOf(Class<T> type) {
        if (!PlatformDescription.isThisJVM("Java HotSpot")) {
            if (!PlatformDescription.isThisJVM(PlatformDescription.OPENJDK)) {
                if (PlatformDescription.isThisJVM(PlatformDescription.DALVIK)) {
                    if (PlatformDescription.isAndroidOpenJDK()) {
                        return new UnsafeFactoryInstantiator(type);
                    }
                    if (PlatformDescription.ANDROID_VERSION <= 10) {
                        return new Android10Instantiator(type);
                    }
                    if (PlatformDescription.ANDROID_VERSION <= 17) {
                        return new Android17Instantiator(type);
                    }
                    return new Android18Instantiator(type);
                } else if (PlatformDescription.isThisJVM(PlatformDescription.JROCKIT)) {
                    return new SunReflectionFactoryInstantiator(type);
                } else {
                    if (PlatformDescription.isThisJVM(PlatformDescription.GNU)) {
                        return new GCJInstantiator(type);
                    }
                    if (PlatformDescription.isThisJVM(PlatformDescription.PERC)) {
                        return new PercInstantiator(type);
                    }
                    return new UnsafeFactoryInstantiator(type);
                }
            }
        }
        if (!PlatformDescription.isGoogleAppEngine() || !PlatformDescription.SPECIFICATION_VERSION.equals("1.7")) {
            return new SunReflectionFactoryInstantiator(type);
        }
        if (Serializable.class.isAssignableFrom(type)) {
            return new ObjectInputStreamInstantiator(type);
        }
        return new AccessibleInstantiator(type);
    }
}
