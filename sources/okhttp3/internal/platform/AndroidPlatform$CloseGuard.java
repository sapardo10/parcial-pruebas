package okhttp3.internal.platform;

import java.lang.reflect.Method;
import org.apache.commons.lang3.concurrent.AbstractCircuitBreaker;

final class AndroidPlatform$CloseGuard {
    private final Method getMethod;
    private final Method openMethod;
    private final Method warnIfOpenMethod;

    AndroidPlatform$CloseGuard(Method getMethod, Method openMethod, Method warnIfOpenMethod) {
        this.getMethod = getMethod;
        this.openMethod = openMethod;
        this.warnIfOpenMethod = warnIfOpenMethod;
    }

    Object createAndOpen(String closer) {
        Object closeGuardInstance = this.getMethod;
        if (closeGuardInstance == null) {
            return null;
        }
        try {
            closeGuardInstance = closeGuardInstance.invoke(null, new Object[0]);
            this.openMethod.invoke(closeGuardInstance, new Object[]{closer});
            return closeGuardInstance;
        } catch (Exception e) {
        }
    }

    boolean warnIfOpen(Object closeGuardInstance) {
        if (closeGuardInstance == null) {
            return false;
        }
        try {
            this.warnIfOpenMethod.invoke(closeGuardInstance, new Object[0]);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    static AndroidPlatform$CloseGuard get() {
        Method getMethod;
        Method openMethod;
        Method warnIfOpenMethod;
        try {
            Class<?> closeGuardClass = Class.forName("dalvik.system.CloseGuard");
            getMethod = closeGuardClass.getMethod("get", new Class[0]);
            openMethod = closeGuardClass.getMethod(AbstractCircuitBreaker.PROPERTY_NAME, new Class[]{String.class});
            warnIfOpenMethod = closeGuardClass.getMethod("warnIfOpen", new Class[0]);
        } catch (Exception e) {
            getMethod = null;
            openMethod = null;
            warnIfOpenMethod = null;
        }
        return new AndroidPlatform$CloseGuard(getMethod, openMethod, warnIfOpenMethod);
    }
}
