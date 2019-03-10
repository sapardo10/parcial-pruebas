package org.apache.commons.lang3.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.ClassUtils.Interfaces;
import org.apache.commons.lang3.Validate;

public class MethodUtils {
    public static Object invokeMethod(Object object, String methodName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return invokeMethod(object, methodName, ArrayUtils.EMPTY_OBJECT_ARRAY, null);
    }

    public static Object invokeMethod(Object object, boolean forceAccess, String methodName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return invokeMethod(object, forceAccess, methodName, ArrayUtils.EMPTY_OBJECT_ARRAY, null);
    }

    public static Object invokeMethod(Object object, String methodName, Object... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        args = ArrayUtils.nullToEmpty(args);
        return invokeMethod(object, methodName, args, ClassUtils.toClass(args));
    }

    public static Object invokeMethod(Object object, boolean forceAccess, String methodName, Object... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        args = ArrayUtils.nullToEmpty(args);
        return invokeMethod(object, forceAccess, methodName, args, ClassUtils.toClass(args));
    }

    public static Object invokeMethod(Object object, boolean forceAccess, String methodName, Object[] args, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String messagePrefix;
        Method method;
        parameterTypes = ArrayUtils.nullToEmpty((Class[]) parameterTypes);
        args = ArrayUtils.nullToEmpty(args);
        if (forceAccess) {
            messagePrefix = "No such method: ";
            method = getMatchingMethod(object.getClass(), methodName, parameterTypes);
            if (method != null && !method.isAccessible()) {
                method.setAccessible(true);
            }
        } else {
            messagePrefix = "No such accessible method: ";
            method = getMatchingAccessibleMethod(object.getClass(), methodName, parameterTypes);
        }
        if (method != null) {
            return method.invoke(object, toVarArgs(method, args));
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(messagePrefix);
        stringBuilder.append(methodName);
        stringBuilder.append("() on object: ");
        stringBuilder.append(object.getClass().getName());
        throw new NoSuchMethodException(stringBuilder.toString());
    }

    public static Object invokeMethod(Object object, String methodName, Object[] args, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return invokeMethod(object, false, methodName, args, parameterTypes);
    }

    public static Object invokeExactMethod(Object object, String methodName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return invokeExactMethod(object, methodName, ArrayUtils.EMPTY_OBJECT_ARRAY, null);
    }

    public static Object invokeExactMethod(Object object, String methodName, Object... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        args = ArrayUtils.nullToEmpty(args);
        return invokeExactMethod(object, methodName, args, ClassUtils.toClass(args));
    }

    public static Object invokeExactMethod(Object object, String methodName, Object[] args, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        args = ArrayUtils.nullToEmpty(args);
        Method method = getAccessibleMethod(object.getClass(), methodName, ArrayUtils.nullToEmpty((Class[]) parameterTypes));
        if (method != null) {
            return method.invoke(object, args);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("No such accessible method: ");
        stringBuilder.append(methodName);
        stringBuilder.append("() on object: ");
        stringBuilder.append(object.getClass().getName());
        throw new NoSuchMethodException(stringBuilder.toString());
    }

    public static Object invokeExactStaticMethod(Class<?> cls, String methodName, Object[] args, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        args = ArrayUtils.nullToEmpty(args);
        Method method = getAccessibleMethod(cls, methodName, ArrayUtils.nullToEmpty((Class[]) parameterTypes));
        if (method != null) {
            return method.invoke(null, args);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("No such accessible method: ");
        stringBuilder.append(methodName);
        stringBuilder.append("() on class: ");
        stringBuilder.append(cls.getName());
        throw new NoSuchMethodException(stringBuilder.toString());
    }

    public static Object invokeStaticMethod(Class<?> cls, String methodName, Object... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        args = ArrayUtils.nullToEmpty(args);
        return invokeStaticMethod(cls, methodName, args, ClassUtils.toClass(args));
    }

    public static Object invokeStaticMethod(Class<?> cls, String methodName, Object[] args, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        args = ArrayUtils.nullToEmpty(args);
        Method method = getMatchingAccessibleMethod(cls, methodName, ArrayUtils.nullToEmpty((Class[]) parameterTypes));
        if (method != null) {
            return method.invoke(null, toVarArgs(method, args));
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("No such accessible method: ");
        stringBuilder.append(methodName);
        stringBuilder.append("() on class: ");
        stringBuilder.append(cls.getName());
        throw new NoSuchMethodException(stringBuilder.toString());
    }

    private static Object[] toVarArgs(Method method, Object[] args) {
        if (method.isVarArgs()) {
            return getVarArgs(args, method.getParameterTypes());
        }
        return args;
    }

    static Object[] getVarArgs(Object[] args, Class<?>[] methodParameterTypes) {
        if (args.length == methodParameterTypes.length && args[args.length - 1].getClass().equals(methodParameterTypes[methodParameterTypes.length - 1])) {
            return args;
        }
        Object[] newArgs = new Object[methodParameterTypes.length];
        System.arraycopy(args, 0, newArgs, 0, methodParameterTypes.length - 1);
        Class<?> varArgComponentType = methodParameterTypes[methodParameterTypes.length - 1].getComponentType();
        int varArgLength = (args.length - methodParameterTypes.length) + 1;
        Object varArgsArray = Array.newInstance(ClassUtils.primitiveToWrapper(varArgComponentType), varArgLength);
        System.arraycopy(args, methodParameterTypes.length - 1, varArgsArray, 0, varArgLength);
        if (varArgComponentType.isPrimitive()) {
            varArgsArray = ArrayUtils.toPrimitive(varArgsArray);
        }
        newArgs[methodParameterTypes.length - 1] = varArgsArray;
        return newArgs;
    }

    public static Object invokeExactStaticMethod(Class<?> cls, String methodName, Object... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        args = ArrayUtils.nullToEmpty(args);
        return invokeExactStaticMethod(cls, methodName, args, ClassUtils.toClass(args));
    }

    public static Method getAccessibleMethod(Class<?> cls, String methodName, Class<?>... parameterTypes) {
        try {
            return getAccessibleMethod(cls.getMethod(methodName, parameterTypes));
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static Method getAccessibleMethod(Method method) {
        if (!MemberUtils.isAccessible(method)) {
            return null;
        }
        Class<?> cls = method.getDeclaringClass();
        if (Modifier.isPublic(cls.getModifiers())) {
            return method;
        }
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        method = getAccessibleMethodFromInterfaceNest(cls, methodName, parameterTypes);
        if (method == null) {
            method = getAccessibleMethodFromSuperclass(cls, methodName, parameterTypes);
        }
        return method;
    }

    private static Method getAccessibleMethodFromSuperclass(Class<?> cls, String methodName, Class<?>... parameterTypes) {
        Class<?> parentClass = cls.getSuperclass();
        while (true) {
            Method method = null;
            if (parentClass == null) {
                return null;
            }
            if (Modifier.isPublic(parentClass.getModifiers())) {
                try {
                    method = parentClass.getMethod(methodName, parameterTypes);
                    return method;
                } catch (NoSuchMethodException e) {
                    return method;
                }
            }
            parentClass = parentClass.getSuperclass();
        }
    }

    private static Method getAccessibleMethodFromInterfaceNest(Class<?> cls, String methodName, Class<?>... parameterTypes) {
        while (cls != null) {
            for (Class<?> anInterface : cls.getInterfaces()) {
                if (Modifier.isPublic(anInterface.getModifiers())) {
                    try {
                        return anInterface.getDeclaredMethod(methodName, parameterTypes);
                    } catch (NoSuchMethodException e) {
                        Method method = getAccessibleMethodFromInterfaceNest(anInterface, methodName, parameterTypes);
                        if (method != null) {
                            return method;
                        }
                    }
                }
            }
            cls = cls.getSuperclass();
        }
        return null;
    }

    public static Method getMatchingAccessibleMethod(Class<?> cls, String methodName, Class<?>... parameterTypes) {
        Method method;
        try {
            method = cls.getMethod(methodName, parameterTypes);
            MemberUtils.setAccessibleWorkaround(method);
            return method;
        } catch (NoSuchMethodException e) {
            method = null;
            for (Method method2 : cls.getMethods()) {
                if (method2.getName().equals(methodName) && MemberUtils.isMatchingMethod(method2, parameterTypes)) {
                    Method accessibleMethod = getAccessibleMethod(method2);
                    if (accessibleMethod != null && (method == null || MemberUtils.compareMethodFit(accessibleMethod, method, parameterTypes) < 0)) {
                        method = accessibleMethod;
                    }
                }
            }
            if (method != null) {
                MemberUtils.setAccessibleWorkaround(method);
            }
            if (method != null && method.isVarArgs() && method.getParameterTypes().length > 0 && parameterTypes.length > 0) {
                Class<?>[] methodParameterTypes = method.getParameterTypes();
                String methodParameterComponentTypeName = ClassUtils.primitiveToWrapper(methodParameterTypes[methodParameterTypes.length - 1].getComponentType()).getName();
                String parameterTypeName = parameterTypes[parameterTypes.length - 1].getName();
                String parameterTypeSuperClassName = parameterTypes[parameterTypes.length - 1].getSuperclass().getName();
                if (!methodParameterComponentTypeName.equals(parameterTypeName) && !methodParameterComponentTypeName.equals(parameterTypeSuperClassName)) {
                    return null;
                }
            }
            return method;
        }
    }

    public static Method getMatchingMethod(Class<?> cls, String methodName, Class<?>... parameterTypes) {
        Validate.notNull(cls, "Null class not allowed.", new Object[0]);
        Validate.notEmpty((CharSequence) methodName, "Null or blank methodName not allowed.", new Object[0]);
        Method[] methodArray = cls.getDeclaredMethods();
        for (Class<?> klass : ClassUtils.getAllSuperclasses(cls)) {
            methodArray = (Method[]) ArrayUtils.addAll((Object[]) methodArray, klass.getDeclaredMethods());
        }
        Method inexactMatch = null;
        for (Method method : methodArray) {
            if (methodName.equals(method.getName()) && Objects.deepEquals(parameterTypes, method.getParameterTypes())) {
                return method;
            }
            if (methodName.equals(method.getName()) && ClassUtils.isAssignable((Class[]) parameterTypes, method.getParameterTypes(), true)) {
                if (inexactMatch == null) {
                    inexactMatch = method;
                } else if (distance(parameterTypes, method.getParameterTypes()) < distance(parameterTypes, inexactMatch.getParameterTypes())) {
                    inexactMatch = method;
                }
            }
        }
        return inexactMatch;
    }

    private static int distance(Class<?>[] classArray, Class<?>[] toClassArray) {
        int answer = 0;
        if (!ClassUtils.isAssignable((Class[]) classArray, (Class[]) toClassArray, true)) {
            return -1;
        }
        int offset = 0;
        while (offset < classArray.length) {
            if (!classArray[offset].equals(toClassArray[offset])) {
                if (!ClassUtils.isAssignable(classArray[offset], toClassArray[offset], true) || ClassUtils.isAssignable(classArray[offset], toClassArray[offset], false)) {
                    answer += 2;
                } else {
                    answer++;
                }
            }
            offset++;
        }
        return answer;
    }

    public static Set<Method> getOverrideHierarchy(Method method, Interfaces interfacesBehavior) {
        Validate.notNull(method);
        Set<Method> result = new LinkedHashSet();
        result.add(method);
        Class<?>[] parameterTypes = method.getParameterTypes();
        Class<?> declaringClass = method.getDeclaringClass();
        Iterator<Class<?>> hierarchy = ClassUtils.hierarchy(declaringClass, interfacesBehavior).iterator();
        hierarchy.next();
        while (hierarchy.hasNext()) {
            Method m = getMatchingAccessibleMethod((Class) hierarchy.next(), method.getName(), parameterTypes);
            if (m != null) {
                if (Arrays.equals(m.getParameterTypes(), parameterTypes)) {
                    result.add(m);
                } else {
                    Map<TypeVariable<?>, Type> typeArguments = TypeUtils.getTypeArguments(declaringClass, m.getDeclaringClass());
                    for (int i = 0; i < parameterTypes.length; i++) {
                        if (!TypeUtils.equals(TypeUtils.unrollVariables(typeArguments, method.getGenericParameterTypes()[i]), TypeUtils.unrollVariables(typeArguments, m.getGenericParameterTypes()[i]))) {
                            break;
                        }
                    }
                    result.add(m);
                }
            }
        }
        return result;
    }

    public static Method[] getMethodsWithAnnotation(Class<?> cls, Class<? extends Annotation> annotationCls) {
        return getMethodsWithAnnotation(cls, annotationCls, false, false);
    }

    public static List<Method> getMethodsListWithAnnotation(Class<?> cls, Class<? extends Annotation> annotationCls) {
        return getMethodsListWithAnnotation(cls, annotationCls, false, false);
    }

    public static Method[] getMethodsWithAnnotation(Class<?> cls, Class<? extends Annotation> annotationCls, boolean searchSupers, boolean ignoreAccess) {
        List<Method> annotatedMethodsList = getMethodsListWithAnnotation(cls, annotationCls, searchSupers, ignoreAccess);
        return (Method[]) annotatedMethodsList.toArray(new Method[annotatedMethodsList.size()]);
    }

    public static List<Method> getMethodsListWithAnnotation(Class<?> cls, Class<? extends Annotation> annotationCls, boolean searchSupers, boolean ignoreAccess) {
        boolean z = true;
        Validate.isTrue(cls != null, "The class must not be null", new Object[0]);
        if (annotationCls == null) {
            z = false;
        }
        Validate.isTrue(z, "The annotation class must not be null", new Object[0]);
        List<Class<?>> classes = searchSupers ? getAllSuperclassesAndInterfaces(cls) : new ArrayList();
        classes.add(0, cls);
        List<Method> annotatedMethods = new ArrayList();
        for (Class<?> acls : classes) {
            for (Method method : ignoreAccess ? acls.getDeclaredMethods() : acls.getMethods()) {
                if (method.getAnnotation(annotationCls) != null) {
                    annotatedMethods.add(method);
                }
            }
        }
        return annotatedMethods;
    }

    public static <A extends Annotation> A getAnnotation(Method method, Class<A> annotationCls, boolean searchSupers, boolean ignoreAccess) {
        boolean z = true;
        Validate.isTrue(method != null, "The method must not be null", new Object[0]);
        if (annotationCls == null) {
            z = false;
        }
        Validate.isTrue(z, "The annotation class must not be null", new Object[0]);
        if (!ignoreAccess && !MemberUtils.isAccessible(method)) {
            return null;
        }
        A annotation = method.getAnnotation(annotationCls);
        if (annotation == null && searchSupers) {
            for (Class<?> acls : getAllSuperclassesAndInterfaces(method.getDeclaringClass())) {
                Method equivalentMethod;
                if (ignoreAccess) {
                    try {
                        equivalentMethod = acls.getDeclaredMethod(method.getName(), method.getParameterTypes());
                    } catch (NoSuchMethodException e) {
                    }
                } else {
                    equivalentMethod = acls.getMethod(method.getName(), method.getParameterTypes());
                }
                annotation = equivalentMethod.getAnnotation(annotationCls);
                if (annotation != null) {
                    break;
                }
            }
        }
        return annotation;
    }

    private static List<Class<?>> getAllSuperclassesAndInterfaces(Class<?> cls) {
        if (cls == null) {
            return null;
        }
        List<Class<?>> allSuperClassesAndInterfaces = new ArrayList();
        List<Class<?>> allSuperclasses = ClassUtils.getAllSuperclasses(cls);
        int superClassIndex = 0;
        List<Class<?>> allInterfaces = ClassUtils.getAllInterfaces(cls);
        int interfaceIndex = 0;
        while (true) {
            Class<?> acls;
            int interfaceIndex2;
            if (interfaceIndex >= allInterfaces.size()) {
                if (superClassIndex >= allSuperclasses.size()) {
                    return allSuperClassesAndInterfaces;
                }
            }
            int i;
            if (interfaceIndex >= allInterfaces.size()) {
                i = interfaceIndex;
                acls = (Class) allSuperclasses.get(superClassIndex);
                superClassIndex++;
                interfaceIndex2 = i;
            } else if (superClassIndex >= allSuperclasses.size()) {
                interfaceIndex2 = interfaceIndex + 1;
                acls = (Class) allInterfaces.get(interfaceIndex);
            } else if (interfaceIndex < superClassIndex) {
                interfaceIndex2 = interfaceIndex + 1;
                acls = (Class) allInterfaces.get(interfaceIndex);
            } else if (superClassIndex < interfaceIndex) {
                i = interfaceIndex;
                acls = (Class) allSuperclasses.get(superClassIndex);
                superClassIndex++;
                interfaceIndex2 = i;
            } else {
                interfaceIndex2 = interfaceIndex + 1;
                acls = (Class) allInterfaces.get(interfaceIndex);
            }
            allSuperClassesAndInterfaces.add(acls);
            interfaceIndex = interfaceIndex2;
        }
    }
}
