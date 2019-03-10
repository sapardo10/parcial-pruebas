package de.danoeh.antennapod.core.util;

import de.danoeh.antennapod.core.BuildConfig;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DuckType {
    private final Object objectToCoerce;

    private class CoercedProxy implements InvocationHandler {
        static final /* synthetic */ boolean $assertionsDisabled = false;

        static {
            Class cls = DuckType.class;
        }

        private CoercedProxy() {
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return DuckType.this.findMethodBySignature(method).invoke(DuckType.this.objectToCoerce, args);
        }
    }

    private DuckType(Object objectToCoerce) {
        this.objectToCoerce = objectToCoerce;
    }

    public static DuckType coerce(Object object) {
        return new DuckType(object);
    }

    public <T> T to(Class iface) {
        if (BuildConfig.DEBUG) {
            if (!iface.isInterface()) {
                throw new AssertionError("cannot coerce object to a class, must be an interface");
            }
        }
        if (isA(iface)) {
            return iface.cast(this.objectToCoerce);
        }
        if (quacksLikeA(iface)) {
            return generateProxy(iface);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Could not coerce object of type ");
        stringBuilder.append(this.objectToCoerce.getClass());
        stringBuilder.append(" to ");
        stringBuilder.append(iface);
        throw new ClassCastException(stringBuilder.toString());
    }

    private boolean isA(Class iface) {
        return this.objectToCoerce.getClass().isInstance(iface);
    }

    private boolean quacksLikeA(Class iface) {
        for (Method method : iface.getMethods()) {
            if (findMethodBySignature(method) == null) {
                return false;
            }
        }
        return true;
    }

    private <T> T generateProxy(Class iface) {
        return Proxy.newProxyInstance(iface.getClassLoader(), new Class[]{iface}, new CoercedProxy());
    }

    private Method findMethodBySignature(Method method) {
        try {
            return this.objectToCoerce.getClass().getMethod(method.getName(), method.getParameterTypes());
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}
