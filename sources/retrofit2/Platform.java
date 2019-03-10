package retrofit2;

import android.os.Build.VERSION;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;
import retrofit2.CallAdapter.Factory;

class Platform {
    private static final Platform PLATFORM = findPlatform();

    static class Android extends Platform {
        Android() {
        }

        public Executor defaultCallbackExecutor() {
            return new Platform$Android$MainThreadExecutor();
        }

        Factory defaultCallAdapterFactory(@Nullable Executor callbackExecutor) {
            if (callbackExecutor != null) {
                return new ExecutorCallAdapterFactory(callbackExecutor);
            }
            throw new AssertionError();
        }
    }

    @IgnoreJRERequirement
    static class Java8 extends Platform {
        Java8() {
        }

        boolean isDefaultMethod(Method method) {
            return method.isDefault();
        }

        Object invokeDefaultMethod(Method method, Class<?> declaringClass, Object object, @Nullable Object... args) throws Throwable {
            Constructor<Lookup> constructor = Lookup.class.getDeclaredConstructor(new Class[]{Class.class, Integer.TYPE});
            constructor.setAccessible(true);
            return ((Lookup) constructor.newInstance(new Object[]{declaringClass, Integer.valueOf(-1)})).unreflectSpecial(method, declaringClass).bindTo(object).invokeWithArguments(args);
        }
    }

    Platform() {
    }

    static Platform get() {
        return PLATFORM;
    }

    private static Platform findPlatform() {
        try {
            Class.forName("android.os.Build");
            if (VERSION.SDK_INT != 0) {
                return new Android();
            }
            try {
                Class.forName("java.util.Optional");
                return new Java8();
            } catch (ClassNotFoundException e) {
                return new Platform();
            }
        } catch (ClassNotFoundException e2) {
        }
    }

    @Nullable
    Executor defaultCallbackExecutor() {
        return null;
    }

    Factory defaultCallAdapterFactory(@Nullable Executor callbackExecutor) {
        if (callbackExecutor != null) {
            return new ExecutorCallAdapterFactory(callbackExecutor);
        }
        return DefaultCallAdapterFactory.INSTANCE;
    }

    boolean isDefaultMethod(Method method) {
        return false;
    }

    @Nullable
    Object invokeDefaultMethod(Method method, Class<?> cls, Object object, @Nullable Object... args) throws Throwable {
        throw new UnsupportedOperationException();
    }
}
