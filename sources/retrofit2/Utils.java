package retrofit2;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import javax.annotation.Nullable;
import okhttp3.ResponseBody;
import okio.Buffer;

final class Utils {
    static final Type[] EMPTY_TYPE_ARRAY = new Type[0];

    private static final class ParameterizedTypeImpl implements ParameterizedType {
        private final Type ownerType;
        private final Type rawType;
        private final Type[] typeArguments;

        ParameterizedTypeImpl(@Nullable Type ownerType, Type rawType, Type... typeArguments) {
            if (rawType instanceof Class) {
                Object obj = 1;
                Object obj2 = ownerType == null ? 1 : null;
                if (((Class) rawType).getEnclosingClass() != null) {
                    obj = null;
                }
                if (obj2 != obj) {
                    throw new IllegalArgumentException();
                }
            }
            for (Type typeArgument : typeArguments) {
                Utils.checkNotNull(typeArgument, "typeArgument == null");
                Utils.checkNotPrimitive(typeArgument);
            }
            this.ownerType = ownerType;
            this.rawType = rawType;
            this.typeArguments = (Type[]) typeArguments.clone();
        }

        public Type[] getActualTypeArguments() {
            return (Type[]) this.typeArguments.clone();
        }

        public Type getRawType() {
            return this.rawType;
        }

        public Type getOwnerType() {
            return this.ownerType;
        }

        public boolean equals(Object other) {
            return (other instanceof ParameterizedType) && Utils.equals(this, (ParameterizedType) other);
        }

        public int hashCode() {
            int hashCode = Arrays.hashCode(this.typeArguments) ^ this.rawType.hashCode();
            Type type = this.ownerType;
            return hashCode ^ (type != null ? type.hashCode() : 0);
        }

        public String toString() {
            Type[] typeArr = this.typeArguments;
            if (typeArr.length == 0) {
                return Utils.typeToString(this.rawType);
            }
            StringBuilder result = new StringBuilder((typeArr.length + 1) * 30);
            result.append(Utils.typeToString(this.rawType));
            result.append("<");
            result.append(Utils.typeToString(this.typeArguments[0]));
            for (int i = 1; i < this.typeArguments.length; i++) {
                result.append(", ");
                result.append(Utils.typeToString(this.typeArguments[i]));
            }
            result.append(">");
            return result.toString();
        }
    }

    static boolean hasUnresolvableType(@javax.annotation.Nullable java.lang.reflect.Type r8) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:27:0x006a in {2, 9, 10, 11, 15, 18, 21, 23, 24, 26} preds:[]
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
        r0 = r8 instanceof java.lang.Class;
        r1 = 0;
        if (r0 == 0) goto L_0x0006;
    L_0x0005:
        return r1;
    L_0x0006:
        r0 = r8 instanceof java.lang.reflect.ParameterizedType;
        r2 = 1;
        if (r0 == 0) goto L_0x0024;
    L_0x000b:
        r0 = r8;
        r0 = (java.lang.reflect.ParameterizedType) r0;
        r3 = r0.getActualTypeArguments();
        r4 = r3.length;
        r5 = 0;
    L_0x0014:
        if (r5 >= r4) goto L_0x0023;
    L_0x0016:
        r6 = r3[r5];
        r7 = hasUnresolvableType(r6);
        if (r7 == 0) goto L_0x001f;
    L_0x001e:
        return r2;
        r5 = r5 + 1;
        goto L_0x0014;
    L_0x0023:
        return r1;
    L_0x0024:
        r0 = r8 instanceof java.lang.reflect.GenericArrayType;
        if (r0 == 0) goto L_0x0034;
    L_0x0028:
        r0 = r8;
        r0 = (java.lang.reflect.GenericArrayType) r0;
        r0 = r0.getGenericComponentType();
        r0 = hasUnresolvableType(r0);
        return r0;
    L_0x0034:
        r0 = r8 instanceof java.lang.reflect.TypeVariable;
        if (r0 == 0) goto L_0x0039;
    L_0x0038:
        return r2;
    L_0x0039:
        r0 = r8 instanceof java.lang.reflect.WildcardType;
        if (r0 == 0) goto L_0x003e;
    L_0x003d:
        return r2;
    L_0x003e:
        if (r8 != 0) goto L_0x0043;
    L_0x0040:
        r0 = "null";
        goto L_0x004b;
    L_0x0043:
        r0 = r8.getClass();
        r0 = r0.getName();
    L_0x004b:
        r1 = new java.lang.IllegalArgumentException;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "Expected a Class, ParameterizedType, or GenericArrayType, but <";
        r2.append(r3);
        r2.append(r8);
        r3 = "> is of type ";
        r2.append(r3);
        r2.append(r0);
        r2 = r2.toString();
        r1.<init>(r2);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: retrofit2.Utils.hasUnresolvableType(java.lang.reflect.Type):boolean");
    }

    private static int indexOf(java.lang.Object[] r2, java.lang.Object r3) {
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
        throw new UnsupportedOperationException("Method not decompiled: retrofit2.Utils.indexOf(java.lang.Object[], java.lang.Object):int");
    }

    static java.lang.reflect.Type resolve(java.lang.reflect.Type r9, java.lang.Class<?> r10, java.lang.reflect.Type r11) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:66:0x00e1 in {4, 5, 12, 13, 14, 19, 20, 21, 26, 27, 33, 34, 35, 36, 37, 39, 40, 41, 49, 50, 59, 60, 61, 62, 63, 65} preds:[]
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
        if (r0 == 0) goto L_0x0010;
    L_0x0004:
        r0 = r11;
        r0 = (java.lang.reflect.TypeVariable) r0;
        r11 = resolveTypeVariable(r9, r10, r0);
        if (r11 != r0) goto L_0x000e;
    L_0x000d:
        return r11;
        goto L_0x0000;
    L_0x0010:
        r0 = r11 instanceof java.lang.Class;
        if (r0 == 0) goto L_0x0032;
    L_0x0014:
        r0 = r11;
        r0 = (java.lang.Class) r0;
        r0 = r0.isArray();
        if (r0 == 0) goto L_0x0032;
    L_0x001d:
        r0 = r11;
        r0 = (java.lang.Class) r0;
        r1 = r0.getComponentType();
        r2 = resolve(r9, r10, r1);
        if (r1 != r2) goto L_0x002c;
    L_0x002a:
        r3 = r0;
        goto L_0x0031;
    L_0x002c:
        r3 = new retrofit2.Utils$GenericArrayTypeImpl;
        r3.<init>(r2);
    L_0x0031:
        return r3;
        r0 = r11 instanceof java.lang.reflect.GenericArrayType;
        if (r0 == 0) goto L_0x004c;
    L_0x0037:
        r0 = r11;
        r0 = (java.lang.reflect.GenericArrayType) r0;
        r1 = r0.getGenericComponentType();
        r2 = resolve(r9, r10, r1);
        if (r1 != r2) goto L_0x0046;
    L_0x0044:
        r3 = r0;
        goto L_0x004b;
    L_0x0046:
        r3 = new retrofit2.Utils$GenericArrayTypeImpl;
        r3.<init>(r2);
    L_0x004b:
        return r3;
    L_0x004c:
        r0 = r11 instanceof java.lang.reflect.ParameterizedType;
        r1 = 1;
        r2 = 0;
        if (r0 == 0) goto L_0x0094;
    L_0x0052:
        r0 = r11;
        r0 = (java.lang.reflect.ParameterizedType) r0;
        r3 = r0.getOwnerType();
        r4 = resolve(r9, r10, r3);
        if (r4 == r3) goto L_0x0060;
    L_0x005f:
        goto L_0x0061;
    L_0x0060:
        r1 = 0;
    L_0x0061:
        r2 = r0.getActualTypeArguments();
        r5 = 0;
        r6 = r2.length;
    L_0x0067:
        if (r5 >= r6) goto L_0x0086;
    L_0x0069:
        r7 = r2[r5];
        r7 = resolve(r9, r10, r7);
        r8 = r2[r5];
        if (r7 == r8) goto L_0x0082;
    L_0x0073:
        if (r1 != 0) goto L_0x007e;
    L_0x0075:
        r8 = r2.clone();
        r2 = r8;
        r2 = (java.lang.reflect.Type[]) r2;
        r1 = 1;
        goto L_0x007f;
    L_0x007f:
        r2[r5] = r7;
        goto L_0x0083;
    L_0x0083:
        r5 = r5 + 1;
        goto L_0x0067;
    L_0x0086:
        if (r1 == 0) goto L_0x0092;
    L_0x0088:
        r5 = new retrofit2.Utils$ParameterizedTypeImpl;
        r6 = r0.getRawType();
        r5.<init>(r4, r6, r2);
        goto L_0x0093;
    L_0x0092:
        r5 = r0;
    L_0x0093:
        return r5;
    L_0x0094:
        r0 = r11 instanceof java.lang.reflect.WildcardType;
        if (r0 == 0) goto L_0x00de;
    L_0x0098:
        r0 = r11;
        r0 = (java.lang.reflect.WildcardType) r0;
        r3 = r0.getLowerBounds();
        r4 = r0.getUpperBounds();
        r5 = r3.length;
        if (r5 != r1) goto L_0x00c2;
    L_0x00a6:
        r5 = r3[r2];
        r5 = resolve(r9, r10, r5);
        r6 = r3[r2];
        if (r5 == r6) goto L_0x00c0;
    L_0x00b0:
        r6 = new retrofit2.Utils$WildcardTypeImpl;
        r7 = new java.lang.reflect.Type[r1];
        r8 = java.lang.Object.class;
        r7[r2] = r8;
        r1 = new java.lang.reflect.Type[r1];
        r1[r2] = r5;
        r6.<init>(r7, r1);
        return r6;
        goto L_0x00dc;
    L_0x00c2:
        r5 = r4.length;
        if (r5 != r1) goto L_0x00dc;
    L_0x00c5:
        r5 = r4[r2];
        r5 = resolve(r9, r10, r5);	 Catch:{ Throwable -> 0x00df }
        r6 = r4[r2];
        if (r5 == r6) goto L_0x00db;
    L_0x00cf:
        r6 = new retrofit2.Utils$WildcardTypeImpl;
        r1 = new java.lang.reflect.Type[r1];
        r1[r2] = r5;
        r2 = EMPTY_TYPE_ARRAY;
        r6.<init>(r1, r2);
        return r6;
    L_0x00db:
        goto L_0x00dd;
    L_0x00dd:
        return r0;
    L_0x00de:
        return r11;
    L_0x00df:
        r9 = move-exception;
        throw r9;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: retrofit2.Utils.resolve(java.lang.reflect.Type, java.lang.Class, java.lang.reflect.Type):java.lang.reflect.Type");
    }

    private Utils() {
    }

    static Class<?> getRawType(Type type) {
        checkNotNull(type, "type == null");
        if (type instanceof Class) {
            return (Class) type;
        }
        if (type instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) type).getRawType();
            if (rawType instanceof Class) {
                return (Class) rawType;
            }
            throw new IllegalArgumentException();
        } else if (type instanceof GenericArrayType) {
            return Array.newInstance(getRawType(((GenericArrayType) type).getGenericComponentType()), 0).getClass();
        } else {
            if (type instanceof TypeVariable) {
                return Object.class;
            }
            if (type instanceof WildcardType) {
                return getRawType(((WildcardType) type).getUpperBounds()[0]);
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Expected a Class, ParameterizedType, or GenericArrayType, but <");
            stringBuilder.append(type);
            stringBuilder.append("> is of type ");
            stringBuilder.append(type.getClass().getName());
            throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    static boolean equals(Type a, Type b) {
        boolean z = true;
        if (a == b) {
            return true;
        }
        if (a instanceof Class) {
            return a.equals(b);
        }
        if (a instanceof ParameterizedType) {
            if (!(b instanceof ParameterizedType)) {
                return false;
            }
            ParameterizedType pa = (ParameterizedType) a;
            ParameterizedType pb = (ParameterizedType) b;
            Object ownerA = pa.getOwnerType();
            Object ownerB = pb.getOwnerType();
            if (ownerA != ownerB) {
                if (ownerA == null || !ownerA.equals(ownerB)) {
                    z = false;
                    return z;
                }
            }
            if (pa.getRawType().equals(pb.getRawType())) {
                if (Arrays.equals(pa.getActualTypeArguments(), pb.getActualTypeArguments())) {
                    return z;
                }
            }
            z = false;
            return z;
        } else if (a instanceof GenericArrayType) {
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

    static Type getGenericSupertype(Type context, Class<?> rawType, Class<?> toResolve) {
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

    static String typeToString(Type type) {
        return type instanceof Class ? ((Class) type).getName() : type.toString();
    }

    static Type getSupertype(Type context, Class<?> contextRawType, Class<?> supertype) {
        if (supertype.isAssignableFrom(contextRawType)) {
            return resolve(context, contextRawType, getGenericSupertype(context, contextRawType, supertype));
        }
        throw new IllegalArgumentException();
    }

    private static Type resolveTypeVariable(Type context, Class<?> contextRawType, TypeVariable<?> unknown) {
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

    private static Class<?> declaringClassOf(TypeVariable<?> typeVariable) {
        GenericDeclaration genericDeclaration = typeVariable.getGenericDeclaration();
        return genericDeclaration instanceof Class ? (Class) genericDeclaration : null;
    }

    static void checkNotPrimitive(Type type) {
        if (type instanceof Class) {
            if (((Class) type).isPrimitive()) {
                throw new IllegalArgumentException();
            }
        }
    }

    static <T> T checkNotNull(@Nullable T object, String message) {
        if (object != null) {
            return object;
        }
        throw new NullPointerException(message);
    }

    static boolean isAnnotationPresent(Annotation[] annotations, Class<? extends Annotation> cls) {
        for (Annotation annotation : annotations) {
            if (cls.isInstance(annotation)) {
                return true;
            }
        }
        return false;
    }

    static ResponseBody buffer(ResponseBody body) throws IOException {
        Buffer buffer = new Buffer();
        body.source().readAll(buffer);
        return ResponseBody.create(body.contentType(), body.contentLength(), buffer);
    }

    static <T> void validateServiceInterface(Class<T> service) {
        if (!service.isInterface()) {
            throw new IllegalArgumentException("API declarations must be interfaces.");
        } else if (service.getInterfaces().length > 0) {
            throw new IllegalArgumentException("API interfaces must not extend other interfaces.");
        }
    }

    static Type getParameterUpperBound(int index, ParameterizedType type) {
        Type[] types = type.getActualTypeArguments();
        if (index < 0 || index >= types.length) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Index ");
            stringBuilder.append(index);
            stringBuilder.append(" not in range [0,");
            stringBuilder.append(types.length);
            stringBuilder.append(") for ");
            stringBuilder.append(type);
            throw new IllegalArgumentException(stringBuilder.toString());
        }
        Type paramType = types[index];
        if (paramType instanceof WildcardType) {
            return ((WildcardType) paramType).getUpperBounds()[0];
        }
        return paramType;
    }

    static Type getCallResponseType(Type returnType) {
        if (returnType instanceof ParameterizedType) {
            return getParameterUpperBound(0, (ParameterizedType) returnType);
        }
        throw new IllegalArgumentException("Call return type must be parameterized as Call<Foo> or Call<? extends Foo>");
    }

    static void throwIfFatal(Throwable t) {
        if (t instanceof VirtualMachineError) {
            throw ((VirtualMachineError) t);
        } else if (t instanceof ThreadDeath) {
            throw ((ThreadDeath) t);
        } else if (t instanceof LinkageError) {
            throw ((LinkageError) t);
        }
    }
}
