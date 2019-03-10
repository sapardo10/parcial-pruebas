package org.apache.commons.lang3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.lang3.mutable.MutableObject;

public class ClassUtils {
    public static final String INNER_CLASS_SEPARATOR = String.valueOf('$');
    public static final char INNER_CLASS_SEPARATOR_CHAR = '$';
    public static final String PACKAGE_SEPARATOR = String.valueOf('.');
    public static final char PACKAGE_SEPARATOR_CHAR = '.';
    private static final Map<String, String> abbreviationMap;
    private static final Map<String, Class<?>> namePrimitiveMap = new HashMap();
    private static final Map<Class<?>, Class<?>> primitiveWrapperMap = new HashMap();
    private static final Map<String, String> reverseAbbreviationMap;
    private static final Map<Class<?>, Class<?>> wrapperPrimitiveMap = new HashMap();

    public enum Interfaces {
        INCLUDE,
        EXCLUDE
    }

    public static java.lang.String getAbbreviatedName(java.lang.String r10, int r11) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:20:0x0055 in {3, 8, 9, 11, 13, 14, 15, 17, 19} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        if (r11 <= 0) goto L_0x004d;
    L_0x0002:
        if (r10 != 0) goto L_0x0007;
    L_0x0004:
        r0 = "";
        return r0;
    L_0x0007:
        r0 = r11;
        r1 = 46;
        r2 = org.apache.commons.lang3.StringUtils.countMatches(r10, r1);
        r3 = r2 + 1;
        r3 = new java.lang.String[r3];
        r4 = r10.length();
        r5 = 1;
        r4 = r4 - r5;
        r6 = r2;
    L_0x0019:
        if (r6 < 0) goto L_0x0048;
    L_0x001b:
        r7 = r10.lastIndexOf(r1, r4);
        r8 = r7 + 1;
        r9 = r4 + 1;
        r8 = r10.substring(r8, r9);
        r9 = r8.length();
        r0 = r0 - r9;
        if (r6 <= 0) goto L_0x0031;
    L_0x002e:
        r0 = r0 + -1;
        goto L_0x0032;
    L_0x0032:
        if (r6 != r2) goto L_0x0037;
    L_0x0034:
        r3[r6] = r8;
        goto L_0x0043;
    L_0x0037:
        if (r0 <= 0) goto L_0x003c;
    L_0x0039:
        r3[r6] = r8;
        goto L_0x0043;
    L_0x003c:
        r9 = 0;
        r9 = r8.substring(r9, r5);
        r3[r6] = r9;
    L_0x0043:
        r4 = r7 + -1;
        r6 = r6 + -1;
        goto L_0x0019;
    L_0x0048:
        r1 = org.apache.commons.lang3.StringUtils.join(r3, r1);
        return r1;
    L_0x004d:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "len must be > 0";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.ClassUtils.getAbbreviatedName(java.lang.String, int):java.lang.String");
    }

    public static java.lang.reflect.Method getPublicMethod(java.lang.Class<?> r6, java.lang.String r7, java.lang.Class<?>... r8) throws java.lang.SecurityException, java.lang.NoSuchMethodException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:19:0x007d in {2, 8, 13, 14, 16, 18} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r0 = r6.getMethod(r7, r8);
        r1 = r0.getDeclaringClass();
        r1 = r1.getModifiers();
        r1 = java.lang.reflect.Modifier.isPublic(r1);
        if (r1 == 0) goto L_0x0013;
    L_0x0012:
        return r0;
    L_0x0013:
        r1 = new java.util.ArrayList;
        r1.<init>();
        r2 = getAllInterfaces(r6);
        r1.addAll(r2);
        r2 = getAllSuperclasses(r6);
        r1.addAll(r2);
        r2 = r1.iterator();
    L_0x002a:
        r3 = r2.hasNext();
        if (r3 == 0) goto L_0x0059;
    L_0x0030:
        r3 = r2.next();
        r3 = (java.lang.Class) r3;
        r4 = r3.getModifiers();
        r4 = java.lang.reflect.Modifier.isPublic(r4);
        if (r4 != 0) goto L_0x0041;
    L_0x0040:
        goto L_0x002a;
    L_0x0041:
        r4 = r3.getMethod(r7, r8);	 Catch:{ NoSuchMethodException -> 0x0057 }
        r5 = r4.getDeclaringClass();
        r5 = r5.getModifiers();
        r5 = java.lang.reflect.Modifier.isPublic(r5);
        if (r5 == 0) goto L_0x0055;
    L_0x0054:
        return r4;
        goto L_0x002a;
    L_0x0057:
        r4 = move-exception;
        goto L_0x002a;
        r2 = new java.lang.NoSuchMethodException;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "Can't find a public method for ";
        r3.append(r4);
        r3.append(r7);
        r4 = " ";
        r3.append(r4);
        r4 = org.apache.commons.lang3.ArrayUtils.toString(r8);
        r3.append(r4);
        r3 = r3.toString();
        r2.<init>(r3);
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.ClassUtils.getPublicMethod(java.lang.Class, java.lang.String, java.lang.Class[]):java.lang.reflect.Method");
    }

    static {
        namePrimitiveMap.put("boolean", Boolean.TYPE);
        namePrimitiveMap.put("byte", Byte.TYPE);
        namePrimitiveMap.put("char", Character.TYPE);
        namePrimitiveMap.put("short", Short.TYPE);
        namePrimitiveMap.put("int", Integer.TYPE);
        namePrimitiveMap.put("long", Long.TYPE);
        namePrimitiveMap.put("double", Double.TYPE);
        namePrimitiveMap.put("float", Float.TYPE);
        namePrimitiveMap.put("void", Void.TYPE);
        primitiveWrapperMap.put(Boolean.TYPE, Boolean.class);
        primitiveWrapperMap.put(Byte.TYPE, Byte.class);
        primitiveWrapperMap.put(Character.TYPE, Character.class);
        primitiveWrapperMap.put(Short.TYPE, Short.class);
        primitiveWrapperMap.put(Integer.TYPE, Integer.class);
        primitiveWrapperMap.put(Long.TYPE, Long.class);
        primitiveWrapperMap.put(Double.TYPE, Double.class);
        primitiveWrapperMap.put(Float.TYPE, Float.class);
        primitiveWrapperMap.put(Void.TYPE, Void.TYPE);
        for (Entry<Class<?>, Class<?>> entry : primitiveWrapperMap.entrySet()) {
            Class<?> primitiveClass = (Class) entry.getKey();
            Class<?> wrapperClass = (Class) entry.getValue();
            if (!primitiveClass.equals(wrapperClass)) {
                wrapperPrimitiveMap.put(wrapperClass, primitiveClass);
            }
        }
        Map<String, String> m = new HashMap();
        m.put("int", "I");
        m.put("boolean", "Z");
        m.put("float", "F");
        m.put("long", "J");
        m.put("short", "S");
        m.put("byte", "B");
        m.put("double", "D");
        m.put("char", "C");
        Map<String, String> r = new HashMap();
        for (Entry<String, String> e : m.entrySet()) {
            r.put(e.getValue(), e.getKey());
        }
        abbreviationMap = Collections.unmodifiableMap(m);
        reverseAbbreviationMap = Collections.unmodifiableMap(r);
    }

    public static String getShortClassName(Object object, String valueIfNull) {
        if (object == null) {
            return valueIfNull;
        }
        return getShortClassName(object.getClass());
    }

    public static String getShortClassName(Class<?> cls) {
        if (cls == null) {
            return "";
        }
        return getShortClassName(cls.getName());
    }

    public static String getShortClassName(String className) {
        if (StringUtils.isEmpty(className)) {
            return "";
        }
        StringBuilder arrayPrefix = new StringBuilder();
        int innerIdx = 0;
        if (className.startsWith("[")) {
            while (className.charAt(0) == '[') {
                className = className.substring(1);
                arrayPrefix.append("[]");
            }
            if (className.charAt(0) == 'L' && className.charAt(className.length() - 1) == ';') {
                className = className.substring(1, className.length() - 1);
            }
            if (reverseAbbreviationMap.containsKey(className)) {
                className = (String) reverseAbbreviationMap.get(className);
            }
        }
        int lastDotIdx = className.lastIndexOf(46);
        if (lastDotIdx != -1) {
            innerIdx = lastDotIdx + 1;
        }
        innerIdx = className.indexOf(36, innerIdx);
        String out = className.substring(lastDotIdx + 1);
        if (innerIdx != -1) {
            out = out.replace('$', '.');
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(out);
        stringBuilder.append(arrayPrefix);
        return stringBuilder.toString();
    }

    public static String getSimpleName(Class<?> cls) {
        return getSimpleName((Class) cls, "");
    }

    public static String getSimpleName(Class<?> cls, String valueIfNull) {
        return cls == null ? valueIfNull : cls.getSimpleName();
    }

    public static String getSimpleName(Object object) {
        return getSimpleName(object, "");
    }

    public static String getSimpleName(Object object, String valueIfNull) {
        return object == null ? valueIfNull : object.getClass().getSimpleName();
    }

    public static String getName(Class<?> cls) {
        return getName((Class) cls, "");
    }

    public static String getName(Class<?> cls, String valueIfNull) {
        return cls == null ? valueIfNull : cls.getName();
    }

    public static String getName(Object object) {
        return getName(object, "");
    }

    public static String getName(Object object, String valueIfNull) {
        return object == null ? valueIfNull : object.getClass().getName();
    }

    public static String getPackageName(Object object, String valueIfNull) {
        if (object == null) {
            return valueIfNull;
        }
        return getPackageName(object.getClass());
    }

    public static String getPackageName(Class<?> cls) {
        if (cls == null) {
            return "";
        }
        return getPackageName(cls.getName());
    }

    public static String getPackageName(String className) {
        if (StringUtils.isEmpty(className)) {
            return "";
        }
        while (className.charAt(0) == '[') {
            className = className.substring(1);
        }
        if (className.charAt(0) == 'L' && className.charAt(className.length() - 1) == ';') {
            className = className.substring(1);
        }
        int i = className.lastIndexOf(46);
        if (i == -1) {
            return "";
        }
        return className.substring(0, i);
    }

    public static String getAbbreviatedName(Class<?> cls, int len) {
        if (cls == null) {
            return "";
        }
        return getAbbreviatedName(cls.getName(), len);
    }

    public static List<Class<?>> getAllSuperclasses(Class<?> cls) {
        if (cls == null) {
            return null;
        }
        List<Class<?>> classes = new ArrayList();
        for (Class<?> superclass = cls.getSuperclass(); superclass != null; superclass = superclass.getSuperclass()) {
            classes.add(superclass);
        }
        return classes;
    }

    public static List<Class<?>> getAllInterfaces(Class<?> cls) {
        if (cls == null) {
            return null;
        }
        LinkedHashSet<Class<?>> interfacesFound = new LinkedHashSet();
        getAllInterfaces(cls, interfacesFound);
        return new ArrayList(interfacesFound);
    }

    private static void getAllInterfaces(Class<?> cls, HashSet<Class<?>> interfacesFound) {
        while (cls != null) {
            for (Class<?> i : cls.getInterfaces()) {
                if (interfacesFound.add(i)) {
                    getAllInterfaces(i, interfacesFound);
                }
            }
            cls = cls.getSuperclass();
        }
    }

    public static List<Class<?>> convertClassNamesToClasses(List<String> classNames) {
        if (classNames == null) {
            return null;
        }
        List<Class<?>> classes = new ArrayList(classNames.size());
        for (String className : classNames) {
            try {
                classes.add(Class.forName(className));
            } catch (Exception e) {
                classes.add(null);
            }
        }
        return classes;
    }

    public static List<String> convertClassesToClassNames(List<Class<?>> classes) {
        if (classes == null) {
            return null;
        }
        List<String> classNames = new ArrayList(classes.size());
        for (Class<?> cls : classes) {
            if (cls == null) {
                classNames.add(null);
            } else {
                classNames.add(cls.getName());
            }
        }
        return classNames;
    }

    public static boolean isAssignable(Class<?>[] classArray, Class<?>... toClassArray) {
        return isAssignable((Class[]) classArray, (Class[]) toClassArray, SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_5));
    }

    public static boolean isAssignable(Class<?>[] classArray, Class<?>[] toClassArray, boolean autoboxing) {
        if (!ArrayUtils.isSameLength((Object[]) classArray, (Object[]) toClassArray)) {
            return false;
        }
        if (classArray == null) {
            classArray = ArrayUtils.EMPTY_CLASS_ARRAY;
        }
        if (toClassArray == null) {
            toClassArray = ArrayUtils.EMPTY_CLASS_ARRAY;
        }
        for (int i = 0; i < classArray.length; i++) {
            if (!isAssignable(classArray[i], toClassArray[i], autoboxing)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isPrimitiveOrWrapper(Class<?> type) {
        boolean z = false;
        if (type == null) {
            return false;
        }
        if (!type.isPrimitive()) {
            if (!isPrimitiveWrapper(type)) {
                return z;
            }
        }
        z = true;
        return z;
    }

    public static boolean isPrimitiveWrapper(Class<?> type) {
        return wrapperPrimitiveMap.containsKey(type);
    }

    public static boolean isAssignable(Class<?> cls, Class<?> toClass) {
        return isAssignable((Class) cls, (Class) toClass, SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_5));
    }

    public static boolean isAssignable(Class<?> cls, Class<?> toClass, boolean autoboxing) {
        boolean z = false;
        if (toClass == null) {
            return false;
        }
        if (cls == null) {
            return toClass.isPrimitive() ^ true;
        }
        if (autoboxing) {
            if (cls.isPrimitive() && !toClass.isPrimitive()) {
                cls = primitiveToWrapper(cls);
                if (cls == null) {
                    return false;
                }
            }
            if (toClass.isPrimitive() && !cls.isPrimitive()) {
                cls = wrapperToPrimitive(cls);
                if (cls == null) {
                    return false;
                }
            }
        }
        if (cls.equals(toClass)) {
            return true;
        }
        if (!cls.isPrimitive()) {
            return toClass.isAssignableFrom(cls);
        }
        if (!toClass.isPrimitive()) {
            return false;
        }
        if (Integer.TYPE.equals(cls)) {
            if (!(Long.TYPE.equals(toClass) || Float.TYPE.equals(toClass))) {
                if (!Double.TYPE.equals(toClass)) {
                    return z;
                }
            }
            z = true;
            return z;
        } else if (Long.TYPE.equals(cls)) {
            if (!Float.TYPE.equals(toClass)) {
                if (!Double.TYPE.equals(toClass)) {
                    return z;
                }
            }
            z = true;
            return z;
        } else if (Boolean.TYPE.equals(cls) || Double.TYPE.equals(cls)) {
            return false;
        } else {
            if (Float.TYPE.equals(cls)) {
                return Double.TYPE.equals(toClass);
            }
            if (Character.TYPE.equals(cls)) {
                if (!(Integer.TYPE.equals(toClass) || Long.TYPE.equals(toClass) || Float.TYPE.equals(toClass))) {
                    if (!Double.TYPE.equals(toClass)) {
                        return z;
                    }
                }
                z = true;
                return z;
            } else if (Short.TYPE.equals(cls)) {
                if (!(Integer.TYPE.equals(toClass) || Long.TYPE.equals(toClass) || Float.TYPE.equals(toClass))) {
                    if (!Double.TYPE.equals(toClass)) {
                        return z;
                    }
                }
                z = true;
                return z;
            } else if (!Byte.TYPE.equals(cls)) {
                return false;
            } else {
                if (!(Short.TYPE.equals(toClass) || Integer.TYPE.equals(toClass) || Long.TYPE.equals(toClass) || Float.TYPE.equals(toClass))) {
                    if (!Double.TYPE.equals(toClass)) {
                        return z;
                    }
                }
                z = true;
                return z;
            }
        }
    }

    public static Class<?> primitiveToWrapper(Class<?> cls) {
        Class<?> convertedClass = cls;
        if (cls == null || !cls.isPrimitive()) {
            return convertedClass;
        }
        return (Class) primitiveWrapperMap.get(cls);
    }

    public static Class<?>[] primitivesToWrappers(Class<?>... classes) {
        if (classes == null) {
            return null;
        }
        if (classes.length == 0) {
            return classes;
        }
        Class<?>[] convertedClasses = new Class[classes.length];
        for (int i = 0; i < classes.length; i++) {
            convertedClasses[i] = primitiveToWrapper(classes[i]);
        }
        return convertedClasses;
    }

    public static Class<?> wrapperToPrimitive(Class<?> cls) {
        return (Class) wrapperPrimitiveMap.get(cls);
    }

    public static Class<?>[] wrappersToPrimitives(Class<?>... classes) {
        if (classes == null) {
            return null;
        }
        if (classes.length == 0) {
            return classes;
        }
        Class<?>[] convertedClasses = new Class[classes.length];
        for (int i = 0; i < classes.length; i++) {
            convertedClasses[i] = wrapperToPrimitive(classes[i]);
        }
        return convertedClasses;
    }

    public static boolean isInnerClass(Class<?> cls) {
        return (cls == null || cls.getEnclosingClass() == null) ? false : true;
    }

    public static Class<?> getClass(ClassLoader classLoader, String className, boolean initialize) throws ClassNotFoundException {
        try {
            Class<?> clazz;
            if (namePrimitiveMap.containsKey(className)) {
                clazz = (Class) namePrimitiveMap.get(className);
            } else {
                clazz = Class.forName(toCanonicalName(className), initialize, classLoader);
            }
            return clazz;
        } catch (ClassNotFoundException ex) {
            int lastDotIndex = className.lastIndexOf(46);
            if (lastDotIndex != -1) {
                try {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(className.substring(0, lastDotIndex));
                    stringBuilder.append('$');
                    stringBuilder.append(className.substring(lastDotIndex + 1));
                    return getClass(classLoader, stringBuilder.toString(), initialize);
                } catch (ClassNotFoundException e) {
                    throw ex;
                }
            }
            throw ex;
        }
    }

    public static Class<?> getClass(ClassLoader classLoader, String className) throws ClassNotFoundException {
        return getClass(classLoader, className, true);
    }

    public static Class<?> getClass(String className) throws ClassNotFoundException {
        return getClass(className, true);
    }

    public static Class<?> getClass(String className, boolean initialize) throws ClassNotFoundException {
        ClassLoader contextCL = Thread.currentThread().getContextClassLoader();
        return getClass(contextCL == null ? ClassUtils.class.getClassLoader() : contextCL, className, initialize);
    }

    private static String toCanonicalName(String className) {
        className = StringUtils.deleteWhitespace(className);
        Validate.notNull(className, "className must not be null.", new Object[0]);
        if (!className.endsWith("[]")) {
            return className;
        }
        StringBuilder classNameBuffer = new StringBuilder();
        while (className.endsWith("[]")) {
            className = className.substring(0, className.length() - 2);
            classNameBuffer.append("[");
        }
        String abbreviation = (String) abbreviationMap.get(className);
        if (abbreviation != null) {
            classNameBuffer.append(abbreviation);
        } else {
            classNameBuffer.append("L");
            classNameBuffer.append(className);
            classNameBuffer.append(";");
        }
        return classNameBuffer.toString();
    }

    public static Class<?>[] toClass(Object... array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return ArrayUtils.EMPTY_CLASS_ARRAY;
        }
        Class<?>[] classes = new Class[array.length];
        for (int i = 0; i < array.length; i++) {
            classes[i] = array[i] == null ? null : array[i].getClass();
        }
        return classes;
    }

    public static String getShortCanonicalName(Object object, String valueIfNull) {
        if (object == null) {
            return valueIfNull;
        }
        return getShortCanonicalName(object.getClass().getName());
    }

    public static String getCanonicalName(Class<?> cls) {
        return getCanonicalName((Class) cls, "");
    }

    public static String getCanonicalName(Class<?> cls, String valueIfNull) {
        if (cls == null) {
            return valueIfNull;
        }
        String canonicalName = cls.getCanonicalName();
        return canonicalName == null ? valueIfNull : canonicalName;
    }

    public static String getCanonicalName(Object object) {
        return getCanonicalName(object, "");
    }

    public static String getCanonicalName(Object object, String valueIfNull) {
        if (object == null) {
            return valueIfNull;
        }
        String canonicalName = object.getClass().getCanonicalName();
        return canonicalName == null ? valueIfNull : canonicalName;
    }

    public static String getShortCanonicalName(Class<?> cls) {
        if (cls == null) {
            return "";
        }
        return getShortCanonicalName(cls.getName());
    }

    public static String getShortCanonicalName(String canonicalName) {
        return getShortClassName(getCanonicalName(canonicalName));
    }

    public static String getPackageCanonicalName(Object object, String valueIfNull) {
        if (object == null) {
            return valueIfNull;
        }
        return getPackageCanonicalName(object.getClass().getName());
    }

    public static String getPackageCanonicalName(Class<?> cls) {
        if (cls == null) {
            return "";
        }
        return getPackageCanonicalName(cls.getName());
    }

    public static String getPackageCanonicalName(String canonicalName) {
        return getPackageName(getCanonicalName(canonicalName));
    }

    private static String getCanonicalName(String className) {
        className = StringUtils.deleteWhitespace(className);
        if (className == null) {
            return null;
        }
        int dim = 0;
        while (className.startsWith("[")) {
            dim++;
            className = className.substring(1);
        }
        if (dim < 1) {
            return className;
        }
        if (className.startsWith("L")) {
            className = className.substring(1, className.endsWith(";") ? className.length() - 1 : className.length());
        } else if (className.length() > 0) {
            className = (String) reverseAbbreviationMap.get(className.substring(0, 1));
        }
        StringBuilder canonicalClassNameBuffer = new StringBuilder(className);
        for (int i = 0; i < dim; i++) {
            canonicalClassNameBuffer.append("[]");
        }
        return canonicalClassNameBuffer.toString();
    }

    public static Iterable<Class<?>> hierarchy(Class<?> type) {
        return hierarchy(type, Interfaces.EXCLUDE);
    }

    public static Iterable<Class<?>> hierarchy(final Class<?> type, Interfaces interfacesBehavior) {
        final Iterable<Class<?>> classes = new Iterable<Class<?>>() {
            public Iterator<Class<?>> iterator() {
                final MutableObject<Class<?>> next = new MutableObject(type);
                return new Iterator<Class<?>>() {
                    public boolean hasNext() {
                        return next.getValue() != null;
                    }

                    public Class<?> next() {
                        Class<?> result = (Class) next.getValue();
                        next.setValue(result.getSuperclass());
                        return result;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
        if (interfacesBehavior != Interfaces.INCLUDE) {
            return classes;
        }
        return new Iterable<Class<?>>() {
            public Iterator<Class<?>> iterator() {
                final Set<Class<?>> seenInterfaces = new HashSet();
                final Iterator<Class<?>> wrapped = classes.iterator();
                return new Iterator<Class<?>>() {
                    Iterator<Class<?>> interfaces = Collections.emptySet().iterator();

                    public boolean hasNext() {
                        if (!this.interfaces.hasNext()) {
                            if (!wrapped.hasNext()) {
                                return false;
                            }
                        }
                        return true;
                    }

                    public Class<?> next() {
                        if (this.interfaces.hasNext()) {
                            Class<?> nextInterface = (Class) this.interfaces.next();
                            seenInterfaces.add(nextInterface);
                            return nextInterface;
                        }
                        nextInterface = (Class) wrapped.next();
                        Set<Class<?>> currentInterfaces = new LinkedHashSet();
                        walkInterfaces(currentInterfaces, nextInterface);
                        this.interfaces = currentInterfaces.iterator();
                        return nextInterface;
                    }

                    private void walkInterfaces(Set<Class<?>> addTo, Class<?> c) {
                        for (Class<?> iface : c.getInterfaces()) {
                            if (!seenInterfaces.contains(iface)) {
                                addTo.add(iface);
                            }
                            walkInterfaces(addTo, iface);
                        }
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }
}
