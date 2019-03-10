package com.squareup.moshi.internal;

import com.squareup.moshi.JsonQualifier;
import com.squareup.moshi.Types;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.Nullable;

public final class Util {
    public static final Type[] EMPTY_TYPE_ARRAY = new Type[0];
    public static final Set<Annotation> NO_ANNOTATIONS = Collections.emptySet();

    public static final class ParameterizedTypeImpl implements ParameterizedType {
        @Nullable
        private final Type ownerType;
        private final Type rawType;
        public final Type[] typeArguments;

        public ParameterizedTypeImpl(@Nullable Type ownerType, Type rawType, Type... typeArguments) {
            if (rawType instanceof Class) {
                Class<?> enclosingClass = ((Class) rawType).getEnclosingClass();
                StringBuilder stringBuilder;
                if (ownerType != null) {
                    if (enclosingClass == null || Types.getRawType(ownerType) != enclosingClass) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("unexpected owner type for ");
                        stringBuilder.append(rawType);
                        stringBuilder.append(": ");
                        stringBuilder.append(ownerType);
                        throw new IllegalArgumentException(stringBuilder.toString());
                    }
                } else if (enclosingClass != null) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("unexpected owner type for ");
                    stringBuilder.append(rawType);
                    stringBuilder.append(": null");
                    throw new IllegalArgumentException(stringBuilder.toString());
                }
            }
            this.ownerType = ownerType == null ? null : Util.canonicalize(ownerType);
            this.rawType = Util.canonicalize(rawType);
            this.typeArguments = (Type[]) typeArguments.clone();
            int t = 0;
            while (true) {
                Type[] typeArr = this.typeArguments;
                if (t >= typeArr.length) {
                    return;
                }
                if (typeArr[t] != null) {
                    Util.checkNotPrimitive(typeArr[t]);
                    typeArr = this.typeArguments;
                    typeArr[t] = Util.canonicalize(typeArr[t]);
                    t++;
                } else {
                    throw new NullPointerException();
                }
            }
        }

        public Type[] getActualTypeArguments() {
            return (Type[]) this.typeArguments.clone();
        }

        public Type getRawType() {
            return this.rawType;
        }

        @Nullable
        public Type getOwnerType() {
            return this.ownerType;
        }

        public boolean equals(Object other) {
            if (other instanceof ParameterizedType) {
                if (Types.equals(this, (ParameterizedType) other)) {
                    return true;
                }
            }
            return false;
        }

        public int hashCode() {
            return (Arrays.hashCode(this.typeArguments) ^ this.rawType.hashCode()) ^ Util.hashCodeOrZero(this.ownerType);
        }

        public String toString() {
            StringBuilder result = new StringBuilder((this.typeArguments.length + 1) * 30);
            result.append(Util.typeToString(this.rawType));
            if (this.typeArguments.length == 0) {
                return result.toString();
            }
            result.append("<");
            result.append(Util.typeToString(this.typeArguments[0]));
            for (int i = 1; i < this.typeArguments.length; i++) {
                result.append(", ");
                result.append(Util.typeToString(this.typeArguments[i]));
            }
            result.append(">");
            return result.toString();
        }
    }

    public static final class WildcardTypeImpl implements WildcardType {
        @Nullable
        private final Type lowerBound;
        private final Type upperBound;

        public WildcardTypeImpl(Type[] upperBounds, Type[] lowerBounds) {
            if (lowerBounds.length > 1) {
                throw new IllegalArgumentException();
            } else if (upperBounds.length != 1) {
                throw new IllegalArgumentException();
            } else if (lowerBounds.length == 1) {
                if (lowerBounds[0] != null) {
                    Util.checkNotPrimitive(lowerBounds[0]);
                    if (upperBounds[0] == Object.class) {
                        this.lowerBound = Util.canonicalize(lowerBounds[0]);
                        this.upperBound = Object.class;
                        return;
                    }
                    throw new IllegalArgumentException();
                }
                throw new NullPointerException();
            } else if (upperBounds[0] != null) {
                Util.checkNotPrimitive(upperBounds[0]);
                this.lowerBound = null;
                this.upperBound = Util.canonicalize(upperBounds[0]);
            } else {
                throw new NullPointerException();
            }
        }

        public Type[] getUpperBounds() {
            return new Type[]{this.upperBound};
        }

        public Type[] getLowerBounds() {
            if (this.lowerBound == null) {
                return Util.EMPTY_TYPE_ARRAY;
            }
            return new Type[]{this.lowerBound};
        }

        public boolean equals(Object other) {
            if (other instanceof WildcardType) {
                if (Types.equals(this, (WildcardType) other)) {
                    return true;
                }
            }
            return false;
        }

        public int hashCode() {
            Type type = this.lowerBound;
            return (type != null ? type.hashCode() + 31 : 1) ^ (this.upperBound.hashCode() + 31);
        }

        public String toString() {
            StringBuilder stringBuilder;
            if (this.lowerBound != null) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("? super ");
                stringBuilder.append(Util.typeToString(this.lowerBound));
                return stringBuilder.toString();
            } else if (this.upperBound == Object.class) {
                return "?";
            } else {
                stringBuilder = new StringBuilder();
                stringBuilder.append("? extends ");
                stringBuilder.append(Util.typeToString(this.upperBound));
                return stringBuilder.toString();
            }
        }
    }

    static int indexOf(java.lang.Object[] r2, java.lang.Object r3) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:9:0x0016 in {5, 6, 8} preds:[]
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
        r0 = 0;
    L_0x0001:
        r1 = r2.length;
        if (r0 >= r1) goto L_0x0010;
    L_0x0004:
        r1 = r2[r0];
        r1 = r3.equals(r1);
        if (r1 == 0) goto L_0x000d;
    L_0x000c:
        return r0;
    L_0x000d:
        r0 = r0 + 1;
        goto L_0x0001;
    L_0x0010:
        r0 = new java.util.NoSuchElementException;
        r0.<init>();
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.squareup.moshi.internal.Util.indexOf(java.lang.Object[], java.lang.Object):int");
    }

    public static java.lang.reflect.Type resolve(java.lang.reflect.Type r9, java.lang.Class<?> r10, java.lang.reflect.Type r11) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:66:0x00cc in {4, 5, 12, 13, 14, 19, 20, 21, 26, 27, 33, 34, 35, 36, 37, 39, 40, 41, 49, 50, 59, 60, 61, 62, 63, 65} preds:[]
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
    L_0x0000:
        r0 = r11 instanceof java.lang.reflect.TypeVariable;
        if (r0 == 0) goto L_0x000f;
    L_0x0004:
        r0 = r11;
        r0 = (java.lang.reflect.TypeVariable) r0;
        r11 = resolveTypeVariable(r9, r10, r0);
        if (r11 != r0) goto L_0x000e;
    L_0x000d:
        return r11;
    L_0x000e:
        goto L_0x0000;
    L_0x000f:
        r0 = r11 instanceof java.lang.Class;
        if (r0 == 0) goto L_0x0030;
    L_0x0013:
        r0 = r11;
        r0 = (java.lang.Class) r0;
        r0 = r0.isArray();
        if (r0 == 0) goto L_0x0030;
    L_0x001c:
        r0 = r11;
        r0 = (java.lang.Class) r0;
        r1 = r0.getComponentType();
        r2 = resolve(r9, r10, r1);
        if (r1 != r2) goto L_0x002b;
    L_0x0029:
        r3 = r0;
        goto L_0x002f;
    L_0x002b:
        r3 = com.squareup.moshi.Types.arrayOf(r2);
    L_0x002f:
        return r3;
        r0 = r11 instanceof java.lang.reflect.GenericArrayType;
        if (r0 == 0) goto L_0x0049;
    L_0x0035:
        r0 = r11;
        r0 = (java.lang.reflect.GenericArrayType) r0;
        r1 = r0.getGenericComponentType();
        r2 = resolve(r9, r10, r1);
        if (r1 != r2) goto L_0x0044;
    L_0x0042:
        r3 = r0;
        goto L_0x0048;
    L_0x0044:
        r3 = com.squareup.moshi.Types.arrayOf(r2);
    L_0x0048:
        return r3;
    L_0x0049:
        r0 = r11 instanceof java.lang.reflect.ParameterizedType;
        r1 = 1;
        r2 = 0;
        if (r0 == 0) goto L_0x0091;
    L_0x004f:
        r0 = r11;
        r0 = (java.lang.reflect.ParameterizedType) r0;
        r3 = r0.getOwnerType();
        r4 = resolve(r9, r10, r3);
        if (r4 == r3) goto L_0x005d;
    L_0x005c:
        goto L_0x005e;
    L_0x005d:
        r1 = 0;
    L_0x005e:
        r2 = r0.getActualTypeArguments();
        r5 = 0;
        r6 = r2.length;
    L_0x0064:
        if (r5 >= r6) goto L_0x0083;
    L_0x0066:
        r7 = r2[r5];
        r7 = resolve(r9, r10, r7);
        r8 = r2[r5];
        if (r7 == r8) goto L_0x007f;
    L_0x0070:
        if (r1 != 0) goto L_0x007b;
    L_0x0072:
        r8 = r2.clone();
        r2 = r8;
        r2 = (java.lang.reflect.Type[]) r2;
        r1 = 1;
        goto L_0x007c;
    L_0x007c:
        r2[r5] = r7;
        goto L_0x0080;
    L_0x0080:
        r5 = r5 + 1;
        goto L_0x0064;
    L_0x0083:
        if (r1 == 0) goto L_0x008f;
    L_0x0085:
        r5 = new com.squareup.moshi.internal.Util$ParameterizedTypeImpl;
        r6 = r0.getRawType();
        r5.<init>(r4, r6, r2);
        goto L_0x0090;
    L_0x008f:
        r5 = r0;
    L_0x0090:
        return r5;
    L_0x0091:
        r0 = r11 instanceof java.lang.reflect.WildcardType;
        if (r0 == 0) goto L_0x00c9;
    L_0x0095:
        r0 = r11;
        r0 = (java.lang.reflect.WildcardType) r0;
        r3 = r0.getLowerBounds();
        r4 = r0.getUpperBounds();
        r5 = r3.length;
        if (r5 != r1) goto L_0x00b4;
    L_0x00a3:
        r1 = r3[r2];
        r1 = resolve(r9, r10, r1);
        r2 = r3[r2];
        if (r1 == r2) goto L_0x00b2;
    L_0x00ad:
        r2 = com.squareup.moshi.Types.supertypeOf(r1);
        return r2;
        goto L_0x00c7;
    L_0x00b4:
        r5 = r4.length;
        if (r5 != r1) goto L_0x00c7;
    L_0x00b7:
        r1 = r4[r2];
        r1 = resolve(r9, r10, r1);	 Catch:{ Throwable -> 0x00ca }
        r2 = r4[r2];
        if (r1 == r2) goto L_0x00c6;
    L_0x00c1:
        r2 = com.squareup.moshi.Types.subtypeOf(r1);
        return r2;
    L_0x00c6:
        goto L_0x00c8;
    L_0x00c8:
        return r0;
    L_0x00c9:
        return r11;
    L_0x00ca:
        r9 = move-exception;
        throw r9;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.squareup.moshi.internal.Util.resolve(java.lang.reflect.Type, java.lang.Class, java.lang.reflect.Type):java.lang.reflect.Type");
    }

    private Util() {
    }

    public static boolean typesMatch(Type pattern, Type candidate) {
        return Types.equals(pattern, candidate);
    }

    public static Set<? extends Annotation> jsonAnnotations(AnnotatedElement annotatedElement) {
        return jsonAnnotations(annotatedElement.getAnnotations());
    }

    public static Set<? extends Annotation> jsonAnnotations(Annotation[] annotations) {
        Set<Annotation> result = null;
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().isAnnotationPresent(JsonQualifier.class)) {
                if (result == null) {
                    result = new LinkedHashSet();
                }
                result.add(annotation);
            }
        }
        return result != null ? Collections.unmodifiableSet(result) : NO_ANNOTATIONS;
    }

    public static boolean isAnnotationPresent(Set<? extends Annotation> annotations, Class<? extends Annotation> annotationClass) {
        if (annotations.isEmpty()) {
            return false;
        }
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == annotationClass) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasNullable(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().getSimpleName().equals("Nullable")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPlatformType(Class<?> rawType) {
        String name = rawType.getName();
        if (!name.startsWith("android.")) {
            if (!name.startsWith("androidx.")) {
                if (!name.startsWith("java.")) {
                    if (!name.startsWith("javax.")) {
                        if (!name.startsWith("kotlin.")) {
                            if (!name.startsWith("scala.")) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public static RuntimeException rethrowCause(InvocationTargetException e) {
        Throwable cause = e.getTargetException();
        if (cause instanceof RuntimeException) {
            throw ((RuntimeException) cause);
        } else if (cause instanceof Error) {
            throw ((Error) cause);
        } else {
            throw new RuntimeException(cause);
        }
    }

    public static Type canonicalize(Type type) {
        if (type instanceof Class) {
            Class<?> c = (Class) type;
            return c.isArray() ? new Util$GenericArrayTypeImpl(canonicalize(c.getComponentType())) : c;
        } else if (type instanceof ParameterizedType) {
            if (type instanceof ParameterizedTypeImpl) {
                return type;
            }
            ParameterizedType p = (ParameterizedType) type;
            return new ParameterizedTypeImpl(p.getOwnerType(), p.getRawType(), p.getActualTypeArguments());
        } else if (type instanceof GenericArrayType) {
            if (type instanceof Util$GenericArrayTypeImpl) {
                return type;
            }
            return new Util$GenericArrayTypeImpl(((GenericArrayType) type).getGenericComponentType());
        } else if (!(type instanceof WildcardType) || (type instanceof WildcardTypeImpl)) {
            return type;
        } else {
            WildcardType w = (WildcardType) type;
            return new WildcardTypeImpl(w.getUpperBounds(), w.getLowerBounds());
        }
    }

    static Type resolveTypeVariable(Type context, Class<?> contextRawType, TypeVariable<?> unknown) {
        Class<?> declaredByRaw = declaringClassOf(unknown);
        if (declaredByRaw == null) {
            return unknown;
        }
        Type declaredBy = getGenericSupertype(context, contextRawType, declaredByRaw);
        if (!(declaredBy instanceof ParameterizedType)) {
            return unknown;
        }
        return ((ParameterizedType) declaredBy).getActualTypeArguments()[indexOf(declaredByRaw.getTypeParameters(), unknown)];
    }

    public static Type getGenericSupertype(Type context, Class<?> rawType, Class<?> toResolve) {
        if (toResolve == rawType) {
            return context;
        }
        if (toResolve.isInterface()) {
            Class<?>[] interfaces = rawType.getInterfaces();
            int length = interfaces.length;
            for (int i = 0; i < length; i++) {
                if (interfaces[i] == toResolve) {
                    return rawType.getGenericInterfaces()[i];
                }
                if (toResolve.isAssignableFrom(interfaces[i])) {
                    return getGenericSupertype(rawType.getGenericInterfaces()[i], interfaces[i], toResolve);
                }
            }
        }
        if (!rawType.isInterface()) {
            while (rawType != Object.class) {
                Class<?> rawSupertype = rawType.getSuperclass();
                if (rawSupertype == toResolve) {
                    return rawType.getGenericSuperclass();
                }
                if (toResolve.isAssignableFrom(rawSupertype)) {
                    return getGenericSupertype(rawType.getGenericSuperclass(), rawSupertype, toResolve);
                }
                rawType = rawSupertype;
            }
        }
        return toResolve;
    }

    static int hashCodeOrZero(@Nullable Object o) {
        return o != null ? o.hashCode() : 0;
    }

    static String typeToString(Type type) {
        return type instanceof Class ? ((Class) type).getName() : type.toString();
    }

    @Nullable
    static Class<?> declaringClassOf(TypeVariable<?> typeVariable) {
        GenericDeclaration genericDeclaration = typeVariable.getGenericDeclaration();
        return genericDeclaration instanceof Class ? (Class) genericDeclaration : null;
    }

    static void checkNotPrimitive(Type type) {
        if (type instanceof Class) {
            if (((Class) type).isPrimitive()) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unexpected primitive ");
                stringBuilder.append(type);
                stringBuilder.append(". Use the boxed type.");
                throw new IllegalArgumentException(stringBuilder.toString());
            }
        }
    }

    public static String typeAnnotatedWithAnnotations(Type type, Set<? extends Annotation> annotations) {
        String str;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(type);
        if (annotations.isEmpty()) {
            str = " (with no annotations)";
        } else {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(" annotated ");
            stringBuilder2.append(annotations);
            str = stringBuilder2.toString();
        }
        stringBuilder.append(str);
        return stringBuilder.toString();
    }
}
