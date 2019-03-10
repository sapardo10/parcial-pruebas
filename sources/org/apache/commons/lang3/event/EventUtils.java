package org.apache.commons.lang3.event;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.reflect.MethodUtils;

public class EventUtils {

    private static class EventBindingInvocationHandler implements InvocationHandler {
        private final Set<String> eventTypes;
        private final String methodName;
        private final Object target;

        EventBindingInvocationHandler(Object target, String methodName, String[] eventTypes) {
            this.target = target;
            this.methodName = methodName;
            this.eventTypes = new HashSet(Arrays.asList(eventTypes));
        }

        public Object invoke(Object proxy, Method method, Object[] parameters) throws Throwable {
            if (!this.eventTypes.isEmpty()) {
                if (!this.eventTypes.contains(method.getName())) {
                    return null;
                }
            }
            if (hasMatchingParametersMethod(method)) {
                return MethodUtils.invokeMethod(this.target, this.methodName, parameters);
            }
            return MethodUtils.invokeMethod(this.target, this.methodName);
        }

        private boolean hasMatchingParametersMethod(Method method) {
            return MethodUtils.getAccessibleMethod(this.target.getClass(), this.methodName, method.getParameterTypes()) != null;
        }
    }

    public static <L> void addEventListener(Object eventSource, Class<L> listenerType, L listener) {
        StringBuilder stringBuilder;
        try {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("add");
            stringBuilder2.append(listenerType.getSimpleName());
            MethodUtils.invokeMethod(eventSource, stringBuilder2.toString(), listener);
        } catch (NoSuchMethodException e) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("Class ");
            stringBuilder.append(eventSource.getClass().getName());
            stringBuilder.append(" does not have a public add");
            stringBuilder.append(listenerType.getSimpleName());
            stringBuilder.append(" method which takes a parameter of type ");
            stringBuilder.append(listenerType.getName());
            stringBuilder.append(".");
            throw new IllegalArgumentException(stringBuilder.toString());
        } catch (IllegalAccessException e2) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("Class ");
            stringBuilder.append(eventSource.getClass().getName());
            stringBuilder.append(" does not have an accessible add");
            stringBuilder.append(listenerType.getSimpleName());
            stringBuilder.append(" method which takes a parameter of type ");
            stringBuilder.append(listenerType.getName());
            stringBuilder.append(".");
            throw new IllegalArgumentException(stringBuilder.toString());
        } catch (InvocationTargetException e3) {
            throw new RuntimeException("Unable to add listener.", e3.getCause());
        }
    }

    public static <L> void bindEventsToMethod(Object target, String methodName, Object eventSource, Class<L> listenerType, String... eventTypes) {
        addEventListener(eventSource, listenerType, listenerType.cast(Proxy.newProxyInstance(target.getClass().getClassLoader(), new Class[]{listenerType}, new EventBindingInvocationHandler(target, methodName, eventTypes))));
    }
}
