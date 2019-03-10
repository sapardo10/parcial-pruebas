package com.squareup.moshi;

import com.squareup.moshi.internal.Util;
import com.squareup.moshi.internal.Util$GenericArrayTypeImpl;
import com.squareup.moshi.internal.Util.ParameterizedTypeImpl;
import com.squareup.moshi.internal.Util.WildcardTypeImpl;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

@CheckReturnValue
public final class Types {
    public static java.util.Set<? extends java.lang.annotation.Annotation> getFieldJsonQualifierAnnotations(java.lang.Class<?> r8, java.lang.String r9) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:13:0x0055 in {5, 6, 7, 9, 12} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r0 = r8.getDeclaredField(r9);	 Catch:{ NoSuchFieldException -> 0x0031 }
        r1 = 1;	 Catch:{ NoSuchFieldException -> 0x0031 }
        r0.setAccessible(r1);	 Catch:{ NoSuchFieldException -> 0x0031 }
        r1 = r0.getDeclaredAnnotations();	 Catch:{ NoSuchFieldException -> 0x0031 }
        r2 = new java.util.LinkedHashSet;	 Catch:{ NoSuchFieldException -> 0x0031 }
        r3 = r1.length;	 Catch:{ NoSuchFieldException -> 0x0031 }
        r2.<init>(r3);	 Catch:{ NoSuchFieldException -> 0x0031 }
        r3 = r1.length;	 Catch:{ NoSuchFieldException -> 0x0031 }
        r4 = 0;	 Catch:{ NoSuchFieldException -> 0x0031 }
    L_0x0014:
        if (r4 >= r3) goto L_0x002c;	 Catch:{ NoSuchFieldException -> 0x0031 }
    L_0x0016:
        r5 = r1[r4];	 Catch:{ NoSuchFieldException -> 0x0031 }
        r6 = r5.annotationType();	 Catch:{ NoSuchFieldException -> 0x0031 }
        r7 = com.squareup.moshi.JsonQualifier.class;	 Catch:{ NoSuchFieldException -> 0x0031 }
        r6 = r6.isAnnotationPresent(r7);	 Catch:{ NoSuchFieldException -> 0x0031 }
        if (r6 == 0) goto L_0x0028;	 Catch:{ NoSuchFieldException -> 0x0031 }
    L_0x0024:
        r2.add(r5);	 Catch:{ NoSuchFieldException -> 0x0031 }
        goto L_0x0029;	 Catch:{ NoSuchFieldException -> 0x0031 }
    L_0x0029:
        r4 = r4 + 1;	 Catch:{ NoSuchFieldException -> 0x0031 }
        goto L_0x0014;	 Catch:{ NoSuchFieldException -> 0x0031 }
    L_0x002c:
        r3 = java.util.Collections.unmodifiableSet(r2);	 Catch:{ NoSuchFieldException -> 0x0031 }
        return r3;
    L_0x0031:
        r0 = move-exception;
        r1 = new java.lang.IllegalArgumentException;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "Could not access field ";
        r2.append(r3);
        r2.append(r9);
        r3 = " on class ";
        r2.append(r3);
        r3 = r8.getCanonicalName();
        r2.append(r3);
        r2 = r2.toString();
        r1.<init>(r2, r0);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.squareup.moshi.Types.getFieldJsonQualifierAnnotations(java.lang.Class, java.lang.String):java.util.Set<? extends java.lang.annotation.Annotation>");
    }

    @javax.annotation.Nullable
    public static java.util.Set<? extends java.lang.annotation.Annotation> nextAnnotations(java.util.Set<? extends java.lang.annotation.Annotation> r4, java.lang.Class<? extends java.lang.annotation.Annotation> r5) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:16:0x0051 in {4, 11, 12, 13, 15} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r0 = com.squareup.moshi.JsonQualifier.class;
        r0 = r5.isAnnotationPresent(r0);
        if (r0 == 0) goto L_0x003a;
    L_0x0008:
        r0 = r4.isEmpty();
        r1 = 0;
        if (r0 == 0) goto L_0x0010;
    L_0x000f:
        return r1;
    L_0x0010:
        r0 = r4.iterator();
    L_0x0014:
        r2 = r0.hasNext();
        if (r2 == 0) goto L_0x0039;
    L_0x001a:
        r2 = r0.next();
        r2 = (java.lang.annotation.Annotation) r2;
        r3 = r2.annotationType();
        r3 = r5.equals(r3);
        if (r3 == 0) goto L_0x0037;
    L_0x002a:
        r0 = new java.util.LinkedHashSet;
        r0.<init>(r4);
        r0.remove(r2);
        r1 = java.util.Collections.unmodifiableSet(r0);
        return r1;
        goto L_0x0014;
    L_0x0039:
        return r1;
    L_0x003a:
        r0 = new java.lang.IllegalArgumentException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r1.append(r5);
        r2 = " is not a JsonQualifier.";
        r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.squareup.moshi.Types.nextAnnotations(java.util.Set, java.lang.Class):java.util.Set<? extends java.lang.annotation.Annotation>");
    }

    private Types() {
    }

    public static ParameterizedType newParameterizedType(Type rawType, Type... typeArguments) {
        return new ParameterizedTypeImpl(null, rawType, typeArguments);
    }

    public static ParameterizedType newParameterizedTypeWithOwner(Type ownerType, Type rawType, Type... typeArguments) {
        return new ParameterizedTypeImpl(ownerType, rawType, typeArguments);
    }

    public static GenericArrayType arrayOf(Type componentType) {
        return new Util$GenericArrayTypeImpl(componentType);
    }

    public static WildcardType subtypeOf(Type bound) {
        return new WildcardTypeImpl(new Type[]{bound}, Util.EMPTY_TYPE_ARRAY);
    }

    public static WildcardType supertypeOf(Type bound) {
        return new WildcardTypeImpl(new Type[]{Object.class}, new Type[]{bound});
    }

    public static Class<?> getRawType(Type type) {
        if (type instanceof Class) {
            return (Class) type;
        }
        if (type instanceof ParameterizedType) {
            return (Class) ((ParameterizedType) type).getRawType();
        }
        if (type instanceof GenericArrayType) {
            return Array.newInstance(getRawType(((GenericArrayType) type).getGenericComponentType()), 0).getClass();
        }
        if (type instanceof TypeVariable) {
            return Object.class;
        }
        if (type instanceof WildcardType) {
            return getRawType(((WildcardType) type).getUpperBounds()[0]);
        }
        String className = type == null ? "null" : type.getClass().getName();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Expected a Class, ParameterizedType, or GenericArrayType, but <");
        stringBuilder.append(type);
        stringBuilder.append("> is of type ");
        stringBuilder.append(className);
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public static Type collectionElementType(Type context, Class<?> contextRawType) {
        Type collectionType = getSupertype(context, contextRawType, Collection.class);
        if (collectionType instanceof WildcardType) {
            collectionType = ((WildcardType) collectionType).getUpperBounds()[0];
        }
        if (collectionType instanceof ParameterizedType) {
            return ((ParameterizedType) collectionType).getActualTypeArguments()[0];
        }
        return Object.class;
    }

    public static boolean equals(@Nullable Type a, @Nullable Type b) {
        boolean z = true;
        if (a == b) {
            return true;
        }
        if (a instanceof Class) {
            if (b instanceof GenericArrayType) {
                return equals(((Class) a).getComponentType(), ((GenericArrayType) b).getGenericComponentType());
            }
            return a.equals(b);
        } else if (a instanceof ParameterizedType) {
            if (!(b instanceof ParameterizedType)) {
                return false;
            }
            Type[] aTypeArguments;
            Type[] bTypeArguments;
            ParameterizedType pa = (ParameterizedType) a;
            ParameterizedType pb = (ParameterizedType) b;
            if (pa instanceof ParameterizedTypeImpl) {
                aTypeArguments = ((ParameterizedTypeImpl) pa).typeArguments;
            } else {
                aTypeArguments = pa.getActualTypeArguments();
            }
            if (pb instanceof ParameterizedTypeImpl) {
                bTypeArguments = ((ParameterizedTypeImpl) pb).typeArguments;
            } else {
                bTypeArguments = pb.getActualTypeArguments();
            }
            if (equals(pa.getOwnerType(), pb.getOwnerType())) {
                if (pa.getRawType().equals(pb.getRawType())) {
                    if (Arrays.equals(aTypeArguments, bTypeArguments)) {
                        return z;
                    }
                }
            }
            z = false;
            return z;
        } else if (a instanceof GenericArrayType) {
            if (b instanceof Class) {
                return equals(((Class) b).getComponentType(), ((GenericArrayType) a).getGenericComponentType());
            }
            if (!(b instanceof GenericArrayType)) {
                return false;
            }
            return equals(((GenericArrayType) a).getGenericComponentType(), ((GenericArrayType) b).getGenericComponentType());
        } else if (a instanceof WildcardType) {
            if (!(b instanceof WildcardType)) {
                return false;
            }
            WildcardType wa = (WildcardType) a;
            WildcardType wb = (WildcardType) b;
            if (Arrays.equals(wa.getUpperBounds(), wb.getUpperBounds())) {
                if (Arrays.equals(wa.getLowerBounds(), wb.getLowerBounds())) {
                    return z;
                }
            }
            z = false;
            return z;
        } else if (!(a instanceof TypeVariable) || !(b instanceof TypeVariable)) {
            return false;
        } else {
            TypeVariable<?> va = (TypeVariable) a;
            TypeVariable<?> vb = (TypeVariable) b;
            if (va.getGenericDeclaration() == vb.getGenericDeclaration()) {
                if (va.getName().equals(vb.getName())) {
                    return z;
                }
            }
            z = false;
            return z;
        }
    }

    static <T extends Annotation> T createJsonQualifierImplementation(Class<T> annotationType) {
        StringBuilder stringBuilder;
        if (!annotationType.isAnnotation()) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(annotationType);
            stringBuilder.append(" must be an annotation.");
            throw new IllegalArgumentException(stringBuilder.toString());
        } else if (!annotationType.isAnnotationPresent(JsonQualifier.class)) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(annotationType);
            stringBuilder.append(" must have @JsonQualifier.");
            throw new IllegalArgumentException(stringBuilder.toString());
        } else if (annotationType.getDeclaredMethods().length == 0) {
            return (Annotation) Proxy.newProxyInstance(annotationType.getClassLoader(), new Class[]{annotationType}, new Types$1(annotationType));
        } else {
            stringBuilder = new StringBuilder();
            stringBuilder.append(annotationType);
            stringBuilder.append(" must not declare methods.");
            throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    static Type[] mapKeyAndValueTypes(Type context, Class<?> contextRawType) {
        if (context == Properties.class) {
            return new Type[]{String.class, String.class};
        }
        Type mapType = getSupertype(context, contextRawType, Map.class);
        if (mapType instanceof ParameterizedType) {
            return ((ParameterizedType) mapType).getActualTypeArguments();
        }
        return new Type[]{Object.class, Object.class};
    }

    static Type getSupertype(Type context, Class<?> contextRawType, Class<?> supertype) {
        if (supertype.isAssignableFrom(contextRawType)) {
            return Util.resolve(context, contextRawType, Util.getGenericSupertype(context, contextRawType, supertype));
        }
        throw new IllegalArgumentException();
    }

    static Type getGenericSuperclass(Type type) {
        Class<?> rawType = getRawType(type);
        return Util.resolve(type, rawType, rawType.getGenericSuperclass());
    }

    static Type arrayComponentType(Type type) {
        if (type instanceof GenericArrayType) {
            return ((GenericArrayType) type).getGenericComponentType();
        }
        if (type instanceof Class) {
            return ((Class) type).getComponentType();
        }
        return null;
    }

    static boolean isAllowedPlatformType(Type type) {
        if (!(type == Boolean.class || type == Byte.class || type == Character.class || type == Double.class || type == Float.class || type == Integer.class || type == Long.class || type == Short.class || type == String.class)) {
            if (type != Object.class) {
                return false;
            }
        }
        return true;
    }
}
