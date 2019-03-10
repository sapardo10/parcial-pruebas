package org.apache.commons.lang3.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import kotlin.text.Typography;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.Builder;

public class TypeUtils {
    public static final WildcardType WILDCARD_ALL = wildcardType().withUpperBounds(Object.class).build();

    private static final class GenericArrayTypeImpl implements GenericArrayType {
        private final Type componentType;

        private GenericArrayTypeImpl(Type componentType) {
            this.componentType = componentType;
        }

        public Type getGenericComponentType() {
            return this.componentType;
        }

        public String toString() {
            return TypeUtils.toString((Type) this);
        }

        public boolean equals(Object obj) {
            if (obj != this) {
                if (!(obj instanceof GenericArrayType) || !TypeUtils.equals((GenericArrayType) this, (Type) (GenericArrayType) obj)) {
                    return false;
                }
            }
            return true;
        }

        public int hashCode() {
            return 1072 | this.componentType.hashCode();
        }
    }

    private static final class ParameterizedTypeImpl implements ParameterizedType {
        private final Class<?> raw;
        private final Type[] typeArguments;
        private final Type useOwner;

        private ParameterizedTypeImpl(Class<?> raw, Type useOwner, Type[] typeArguments) {
            this.raw = raw;
            this.useOwner = useOwner;
            this.typeArguments = (Type[]) typeArguments.clone();
        }

        public Type getRawType() {
            return this.raw;
        }

        public Type getOwnerType() {
            return this.useOwner;
        }

        public Type[] getActualTypeArguments() {
            return (Type[]) this.typeArguments.clone();
        }

        public String toString() {
            return TypeUtils.toString((Type) this);
        }

        public boolean equals(Object obj) {
            if (obj != this) {
                if (!(obj instanceof ParameterizedType) || !TypeUtils.equals((ParameterizedType) this, (Type) (ParameterizedType) obj)) {
                    return false;
                }
            }
            return true;
        }

        public int hashCode() {
            return ((((1136 | this.raw.hashCode()) << 4) | Objects.hashCode(this.useOwner)) << 8) | Arrays.hashCode(this.typeArguments);
        }
    }

    private static final class WildcardTypeImpl implements WildcardType {
        private static final Type[] EMPTY_BOUNDS = new Type[0];
        private final Type[] lowerBounds;
        private final Type[] upperBounds;

        private WildcardTypeImpl(Type[] upperBounds, Type[] lowerBounds) {
            this.upperBounds = (Type[]) ObjectUtils.defaultIfNull(upperBounds, EMPTY_BOUNDS);
            this.lowerBounds = (Type[]) ObjectUtils.defaultIfNull(lowerBounds, EMPTY_BOUNDS);
        }

        public Type[] getUpperBounds() {
            return (Type[]) this.upperBounds.clone();
        }

        public Type[] getLowerBounds() {
            return (Type[]) this.lowerBounds.clone();
        }

        public String toString() {
            return TypeUtils.toString((Type) this);
        }

        public boolean equals(Object obj) {
            if (obj != this) {
                if (!(obj instanceof WildcardType) || !TypeUtils.equals((WildcardType) this, (Type) (WildcardType) obj)) {
                    return false;
                }
            }
            return true;
        }

        public int hashCode() {
            return ((18688 | Arrays.hashCode(this.upperBounds)) << 8) | Arrays.hashCode(this.lowerBounds);
        }
    }

    public static class WildcardTypeBuilder implements Builder<WildcardType> {
        private Type[] lowerBounds;
        private Type[] upperBounds;

        private WildcardTypeBuilder() {
        }

        public WildcardTypeBuilder withUpperBounds(Type... bounds) {
            this.upperBounds = bounds;
            return this;
        }

        public WildcardTypeBuilder withLowerBounds(Type... bounds) {
            this.lowerBounds = bounds;
            return this;
        }

        public WildcardType build() {
            return new WildcardTypeImpl(this.upperBounds, this.lowerBounds);
        }
    }

    private static java.util.Map<java.lang.reflect.TypeVariable<?>, java.lang.reflect.Type> getTypeArguments(java.lang.reflect.Type r6, java.lang.Class<?> r7, java.util.Map<java.lang.reflect.TypeVariable<?>, java.lang.reflect.Type> r8) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:38:0x008e in {3, 7, 12, 13, 15, 23, 24, 25, 33, 34, 35, 37} preds:[]
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
        r0 = r6 instanceof java.lang.Class;
        if (r0 == 0) goto L_0x000c;
    L_0x0004:
        r0 = r6;
        r0 = (java.lang.Class) r0;
        r0 = getTypeArguments(r0, r7, r8);
        return r0;
    L_0x000c:
        r0 = r6 instanceof java.lang.reflect.ParameterizedType;
        if (r0 == 0) goto L_0x0018;
    L_0x0010:
        r0 = r6;
        r0 = (java.lang.reflect.ParameterizedType) r0;
        r0 = getTypeArguments(r0, r7, r8);
        return r0;
    L_0x0018:
        r0 = r6 instanceof java.lang.reflect.GenericArrayType;
        if (r0 == 0) goto L_0x0034;
    L_0x001c:
        r0 = r6;
        r0 = (java.lang.reflect.GenericArrayType) r0;
        r0 = r0.getGenericComponentType();
        r1 = r7.isArray();
        if (r1 == 0) goto L_0x002e;
    L_0x0029:
        r1 = r7.getComponentType();
        goto L_0x002f;
    L_0x002e:
        r1 = r7;
    L_0x002f:
        r0 = getTypeArguments(r0, r1, r8);
        return r0;
    L_0x0034:
        r0 = r6 instanceof java.lang.reflect.WildcardType;
        r1 = 0;
        if (r0 == 0) goto L_0x0056;
    L_0x0039:
        r0 = r6;
        r0 = (java.lang.reflect.WildcardType) r0;
        r0 = getImplicitUpperBounds(r0);
        r2 = r0.length;
        r3 = 0;
    L_0x0042:
        if (r3 >= r2) goto L_0x0055;
    L_0x0044:
        r4 = r0[r3];
        r5 = isAssignable(r4, r7);
        if (r5 == 0) goto L_0x0051;
    L_0x004c:
        r1 = getTypeArguments(r4, r7, r8);
        return r1;
        r3 = r3 + 1;
        goto L_0x0042;
    L_0x0055:
        return r1;
    L_0x0056:
        r0 = r6 instanceof java.lang.reflect.TypeVariable;
        if (r0 == 0) goto L_0x0077;
    L_0x005a:
        r0 = r6;
        r0 = (java.lang.reflect.TypeVariable) r0;
        r0 = getImplicitBounds(r0);
        r2 = r0.length;
        r3 = 0;
    L_0x0063:
        if (r3 >= r2) goto L_0x0076;
    L_0x0065:
        r4 = r0[r3];
        r5 = isAssignable(r4, r7);
        if (r5 == 0) goto L_0x0072;
    L_0x006d:
        r1 = getTypeArguments(r4, r7, r8);
        return r1;
        r3 = r3 + 1;
        goto L_0x0063;
    L_0x0076:
        return r1;
    L_0x0077:
        r0 = new java.lang.IllegalStateException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "found an unhandled type: ";
        r1.append(r2);
        r1.append(r6);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.reflect.TypeUtils.getTypeArguments(java.lang.reflect.Type, java.lang.Class, java.util.Map):java.util.Map<java.lang.reflect.TypeVariable<?>, java.lang.reflect.Type>");
    }

    private static boolean isAssignable(java.lang.reflect.Type r7, java.lang.Class<?> r8) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:46:0x0096 in {5, 6, 7, 9, 12, 16, 20, 27, 28, 29, 38, 39, 40, 43, 45} preds:[]
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
        r0 = 1;
        r1 = 0;
        if (r7 != 0) goto L_0x0010;
    L_0x0004:
        if (r8 == 0) goto L_0x000f;
    L_0x0006:
        r2 = r8.isPrimitive();
        if (r2 != 0) goto L_0x000d;
    L_0x000c:
        goto L_0x000f;
    L_0x000d:
        r0 = 0;
    L_0x000f:
        return r0;
    L_0x0010:
        if (r8 != 0) goto L_0x0013;
    L_0x0012:
        return r1;
    L_0x0013:
        r2 = r8.equals(r7);
        if (r2 == 0) goto L_0x001a;
    L_0x0019:
        return r0;
    L_0x001a:
        r2 = r7 instanceof java.lang.Class;
        if (r2 == 0) goto L_0x0026;
    L_0x001e:
        r0 = r7;
        r0 = (java.lang.Class) r0;
        r0 = org.apache.commons.lang3.ClassUtils.isAssignable(r0, r8);
        return r0;
    L_0x0026:
        r2 = r7 instanceof java.lang.reflect.ParameterizedType;
        if (r2 == 0) goto L_0x0036;
    L_0x002a:
        r0 = r7;
        r0 = (java.lang.reflect.ParameterizedType) r0;
        r0 = getRawType(r0);
        r0 = isAssignable(r0, r8);
        return r0;
    L_0x0036:
        r2 = r7 instanceof java.lang.reflect.TypeVariable;
        if (r2 == 0) goto L_0x0053;
    L_0x003a:
        r2 = r7;
        r2 = (java.lang.reflect.TypeVariable) r2;
        r2 = r2.getBounds();
        r3 = r2.length;
        r4 = 0;
    L_0x0043:
        if (r4 >= r3) goto L_0x0052;
    L_0x0045:
        r5 = r2[r4];
        r6 = isAssignable(r5, r8);
        if (r6 == 0) goto L_0x004e;
    L_0x004d:
        return r0;
        r4 = r4 + 1;
        goto L_0x0043;
    L_0x0052:
        return r1;
    L_0x0053:
        r2 = r7 instanceof java.lang.reflect.GenericArrayType;
        if (r2 == 0) goto L_0x007a;
    L_0x0057:
        r2 = java.lang.Object.class;
        r2 = r8.equals(r2);
        if (r2 != 0) goto L_0x0079;
    L_0x005f:
        r2 = r8.isArray();
        if (r2 == 0) goto L_0x0077;
    L_0x0065:
        r2 = r7;
        r2 = (java.lang.reflect.GenericArrayType) r2;
        r2 = r2.getGenericComponentType();
        r3 = r8.getComponentType();
        r2 = isAssignable(r2, r3);
        if (r2 == 0) goto L_0x0077;
    L_0x0076:
        goto L_0x0079;
    L_0x0077:
        r0 = 0;
    L_0x0079:
        return r0;
    L_0x007a:
        r0 = r7 instanceof java.lang.reflect.WildcardType;
        if (r0 == 0) goto L_0x007f;
    L_0x007e:
        return r1;
    L_0x007f:
        r0 = new java.lang.IllegalStateException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "found an unhandled type: ";
        r1.append(r2);
        r1.append(r7);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.reflect.TypeUtils.isAssignable(java.lang.reflect.Type, java.lang.Class):boolean");
    }

    private static boolean isAssignable(java.lang.reflect.Type r8, java.lang.reflect.GenericArrayType r9, java.util.Map<java.lang.reflect.TypeVariable<?>, java.lang.reflect.Type> r10) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:45:0x0093 in {2, 5, 8, 15, 16, 17, 21, 28, 29, 30, 37, 38, 39, 42, 44} preds:[]
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
        r0 = 1;
        if (r8 != 0) goto L_0x0004;
    L_0x0003:
        return r0;
    L_0x0004:
        r1 = 0;
        if (r9 != 0) goto L_0x0008;
    L_0x0007:
        return r1;
    L_0x0008:
        r2 = r9.equals(r8);
        if (r2 == 0) goto L_0x000f;
    L_0x000e:
        return r0;
    L_0x000f:
        r2 = r9.getGenericComponentType();
        r3 = r8 instanceof java.lang.Class;
        if (r3 == 0) goto L_0x002d;
    L_0x0017:
        r3 = r8;
        r3 = (java.lang.Class) r3;
        r4 = r3.isArray();
        if (r4 == 0) goto L_0x002b;
    L_0x0020:
        r4 = r3.getComponentType();
        r4 = isAssignable(r4, r2, r10);
        if (r4 == 0) goto L_0x002b;
    L_0x002a:
        goto L_0x002c;
    L_0x002b:
        r0 = 0;
    L_0x002c:
        return r0;
    L_0x002d:
        r3 = r8 instanceof java.lang.reflect.GenericArrayType;
        if (r3 == 0) goto L_0x003d;
    L_0x0031:
        r0 = r8;
        r0 = (java.lang.reflect.GenericArrayType) r0;
        r0 = r0.getGenericComponentType();
        r0 = isAssignable(r0, r2, r10);
        return r0;
    L_0x003d:
        r3 = r8 instanceof java.lang.reflect.WildcardType;
        if (r3 == 0) goto L_0x005a;
    L_0x0041:
        r3 = r8;
        r3 = (java.lang.reflect.WildcardType) r3;
        r3 = getImplicitUpperBounds(r3);
        r4 = r3.length;
        r5 = 0;
    L_0x004a:
        if (r5 >= r4) goto L_0x0059;
    L_0x004c:
        r6 = r3[r5];
        r7 = isAssignable(r6, r9);
        if (r7 == 0) goto L_0x0055;
    L_0x0054:
        return r0;
        r5 = r5 + 1;
        goto L_0x004a;
    L_0x0059:
        return r1;
    L_0x005a:
        r3 = r8 instanceof java.lang.reflect.TypeVariable;
        if (r3 == 0) goto L_0x0077;
    L_0x005e:
        r3 = r8;
        r3 = (java.lang.reflect.TypeVariable) r3;
        r3 = getImplicitBounds(r3);
        r4 = r3.length;
        r5 = 0;
    L_0x0067:
        if (r5 >= r4) goto L_0x0076;
    L_0x0069:
        r6 = r3[r5];
        r7 = isAssignable(r6, r9);
        if (r7 == 0) goto L_0x0072;
    L_0x0071:
        return r0;
        r5 = r5 + 1;
        goto L_0x0067;
    L_0x0076:
        return r1;
    L_0x0077:
        r0 = r8 instanceof java.lang.reflect.ParameterizedType;
        if (r0 == 0) goto L_0x007c;
    L_0x007b:
        return r1;
    L_0x007c:
        r0 = new java.lang.IllegalStateException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r3 = "found an unhandled type: ";
        r1.append(r3);
        r1.append(r8);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.reflect.TypeUtils.isAssignable(java.lang.reflect.Type, java.lang.reflect.GenericArrayType, java.util.Map):boolean");
    }

    public static boolean isAssignable(Type type, Type toType) {
        return isAssignable(type, toType, null);
    }

    private static boolean isAssignable(Type type, Type toType, Map<TypeVariable<?>, Type> typeVarAssigns) {
        if (toType != null) {
            if (!(toType instanceof Class)) {
                if (toType instanceof ParameterizedType) {
                    return isAssignable(type, (ParameterizedType) toType, (Map) typeVarAssigns);
                }
                if (toType instanceof GenericArrayType) {
                    return isAssignable(type, (GenericArrayType) toType, (Map) typeVarAssigns);
                }
                if (toType instanceof WildcardType) {
                    return isAssignable(type, (WildcardType) toType, (Map) typeVarAssigns);
                }
                if (toType instanceof TypeVariable) {
                    return isAssignable(type, (TypeVariable) toType, (Map) typeVarAssigns);
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("found an unhandled type: ");
                stringBuilder.append(toType);
                throw new IllegalStateException(stringBuilder.toString());
            }
        }
        return isAssignable(type, (Class) toType);
    }

    private static boolean isAssignable(Type type, ParameterizedType toParameterizedType, Map<TypeVariable<?>, Type> typeVarAssigns) {
        if (type == null) {
            return true;
        }
        if (toParameterizedType == null) {
            return false;
        }
        if (toParameterizedType.equals(type)) {
            return true;
        }
        Class toClass = getRawType(toParameterizedType);
        Map<TypeVariable<?>, Type> fromTypeVarAssigns = getTypeArguments(type, toClass, (Map) null);
        if (fromTypeVarAssigns == null) {
            return false;
        }
        if (fromTypeVarAssigns.isEmpty()) {
            return true;
        }
        Map<TypeVariable<?>, Type> toTypeVarAssigns = getTypeArguments(toParameterizedType, toClass, (Map) typeVarAssigns);
        for (TypeVariable<?> var : toTypeVarAssigns.keySet()) {
            Type toTypeArg = unrollVariableAssignments(var, toTypeVarAssigns);
            Type fromTypeArg = unrollVariableAssignments(var, fromTypeVarAssigns);
            if (toTypeArg != null || !(fromTypeArg instanceof Class)) {
                if (fromTypeArg != null && !toTypeArg.equals(fromTypeArg) && (!(toTypeArg instanceof WildcardType) || !isAssignable(fromTypeArg, toTypeArg, (Map) typeVarAssigns))) {
                    return false;
                }
            }
        }
        return true;
    }

    private static Type unrollVariableAssignments(TypeVariable<?> var, Map<TypeVariable<?>, Type> typeVarAssigns) {
        Type result;
        while (true) {
            result = (Type) typeVarAssigns.get(var);
            if ((result instanceof TypeVariable) && !result.equals(var)) {
                var = (TypeVariable) result;
            }
        }
        return result;
    }

    private static boolean isAssignable(Type type, WildcardType toWildcardType, Map<TypeVariable<?>, Type> typeVarAssigns) {
        Type type2 = type;
        WildcardType wildcardType = toWildcardType;
        Map map = typeVarAssigns;
        if (type2 == null) {
            return true;
        }
        if (wildcardType == null) {
            return false;
        }
        if (wildcardType.equals(type2)) {
            return true;
        }
        Type[] toUpperBounds = getImplicitUpperBounds(toWildcardType);
        Type[] toLowerBounds = getImplicitLowerBounds(toWildcardType);
        if (type2 instanceof WildcardType) {
            WildcardType wildcardType2 = (WildcardType) type2;
            Type[] upperBounds = getImplicitUpperBounds(wildcardType2);
            Type[] lowerBounds = getImplicitLowerBounds(wildcardType2);
            for (Type toBound : toUpperBounds) {
                Type toBound2 = substituteTypeVariables(toBound2, map);
                for (Type bound : upperBounds) {
                    if (!isAssignable(bound, toBound2, map)) {
                        return false;
                    }
                }
            }
            for (Type toBound3 : toLowerBounds) {
                Type toBound32 = substituteTypeVariables(toBound32, map);
                for (Type bound2 : lowerBounds) {
                    if (!isAssignable(toBound32, bound2, map)) {
                        return false;
                    }
                }
            }
            return true;
        }
        for (Type toBound4 : toUpperBounds) {
            if (!isAssignable(type2, substituteTypeVariables(toBound4, map), map)) {
                return false;
            }
        }
        for (Type toBound42 : toLowerBounds) {
            if (!isAssignable(substituteTypeVariables(toBound42, map), type2, map)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isAssignable(Type type, TypeVariable<?> toTypeVariable, Map<TypeVariable<?>, Type> typeVarAssigns) {
        if (type == null) {
            return true;
        }
        if (toTypeVariable == null) {
            return false;
        }
        if (toTypeVariable.equals(type)) {
            return true;
        }
        if (type instanceof TypeVariable) {
            for (Type bound : getImplicitBounds((TypeVariable) type)) {
                if (isAssignable(bound, (TypeVariable) toTypeVariable, (Map) typeVarAssigns)) {
                    return true;
                }
            }
        }
        if (!((type instanceof Class) || (type instanceof ParameterizedType) || (type instanceof GenericArrayType))) {
            if (!(type instanceof WildcardType)) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("found an unhandled type: ");
                stringBuilder.append(type);
                throw new IllegalStateException(stringBuilder.toString());
            }
        }
        return false;
    }

    private static Type substituteTypeVariables(Type type, Map<TypeVariable<?>, Type> typeVarAssigns) {
        if (!(type instanceof TypeVariable) || typeVarAssigns == null) {
            return type;
        }
        Type replacementType = (Type) typeVarAssigns.get(type);
        if (replacementType != null) {
            return replacementType;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("missing assignment type for type variable ");
        stringBuilder.append(type);
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public static Map<TypeVariable<?>, Type> getTypeArguments(ParameterizedType type) {
        return getTypeArguments(type, getRawType(type), null);
    }

    public static Map<TypeVariable<?>, Type> getTypeArguments(Type type, Class<?> toClass) {
        return getTypeArguments(type, (Class) toClass, null);
    }

    private static Map<TypeVariable<?>, Type> getTypeArguments(ParameterizedType parameterizedType, Class<?> toClass, Map<TypeVariable<?>, Type> subtypeVarAssigns) {
        Type cls = getRawType(parameterizedType);
        if (!isAssignable(cls, (Class) toClass)) {
            return null;
        }
        Map typeVarAssigns;
        Type ownerType = parameterizedType.getOwnerType();
        if (ownerType instanceof ParameterizedType) {
            ParameterizedType typeVarAssigns2 = (ParameterizedType) ownerType;
            typeVarAssigns = getTypeArguments(typeVarAssigns2, getRawType(typeVarAssigns2), (Map) subtypeVarAssigns);
        } else {
            typeVarAssigns = subtypeVarAssigns == null ? new HashMap() : new HashMap(subtypeVarAssigns);
        }
        Type[] typeArgs = parameterizedType.getActualTypeArguments();
        TypeVariable<?>[] typeParams = cls.getTypeParameters();
        for (int i = 0; i < typeParams.length; i++) {
            Type typeArg = typeArgs[i];
            typeVarAssigns.put(typeParams[i], typeVarAssigns.containsKey(typeArg) ? (Type) typeVarAssigns.get(typeArg) : typeArg);
        }
        if (toClass.equals(cls)) {
            return typeVarAssigns;
        }
        return getTypeArguments(getClosestParentType(cls, toClass), (Class) toClass, typeVarAssigns);
    }

    private static Map<TypeVariable<?>, Type> getTypeArguments(Class<?> cls, Class<?> toClass, Map<TypeVariable<?>, Type> subtypeVarAssigns) {
        if (!isAssignable((Type) cls, (Class) toClass)) {
            return null;
        }
        if (cls.isPrimitive()) {
            if (toClass.isPrimitive()) {
                return new HashMap();
            }
            cls = ClassUtils.primitiveToWrapper(cls);
        }
        Map typeVarAssigns = subtypeVarAssigns == null ? new HashMap() : new HashMap(subtypeVarAssigns);
        if (toClass.equals(cls)) {
            return typeVarAssigns;
        }
        return getTypeArguments(getClosestParentType(cls, toClass), (Class) toClass, typeVarAssigns);
    }

    public static Map<TypeVariable<?>, Type> determineTypeArguments(Class<?> cls, ParameterizedType superType) {
        Validate.notNull(cls, "cls is null", new Object[0]);
        Validate.notNull(superType, "superType is null", new Object[0]);
        Class superClass = getRawType(superType);
        if (!isAssignable((Type) cls, superClass)) {
            return null;
        }
        if (cls.equals(superClass)) {
            return getTypeArguments(superType, superClass, null);
        }
        Type midType = getClosestParentType(cls, superClass);
        if (midType instanceof Class) {
            return determineTypeArguments((Class) midType, superType);
        }
        ParameterizedType midParameterizedType = (ParameterizedType) midType;
        Map<TypeVariable<?>, Type> typeVarAssigns = determineTypeArguments(getRawType(midParameterizedType), superType);
        mapTypeVariablesToArguments(cls, midParameterizedType, typeVarAssigns);
        return typeVarAssigns;
    }

    private static <T> void mapTypeVariablesToArguments(Class<T> cls, ParameterizedType parameterizedType, Map<TypeVariable<?>, Type> typeVarAssigns) {
        Type ownerType = parameterizedType.getOwnerType();
        if (ownerType instanceof ParameterizedType) {
            mapTypeVariablesToArguments(cls, (ParameterizedType) ownerType, typeVarAssigns);
        }
        Type[] typeArgs = parameterizedType.getActualTypeArguments();
        TypeVariable<?>[] typeVars = getRawType(parameterizedType).getTypeParameters();
        List<TypeVariable<Class<T>>> typeVarList = Arrays.asList(cls.getTypeParameters());
        for (int i = 0; i < typeArgs.length; i++) {
            TypeVariable<?> typeVar = typeVars[i];
            Type typeArg = typeArgs[i];
            if (typeVarList.contains(typeArg) && typeVarAssigns.containsKey(typeVar)) {
                typeVarAssigns.put((TypeVariable) typeArg, typeVarAssigns.get(typeVar));
            }
        }
    }

    private static Type getClosestParentType(Class<?> cls, Class<?> superClass) {
        if (superClass.isInterface()) {
            Type genericInterface = null;
            for (Type midType : cls.getGenericInterfaces()) {
                Type midClass;
                if (midType instanceof ParameterizedType) {
                    midClass = getRawType((ParameterizedType) midType);
                } else if (midType instanceof Class) {
                    midClass = (Class) midType;
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Unexpected generic interface type found: ");
                    stringBuilder.append(midType);
                    throw new IllegalStateException(stringBuilder.toString());
                }
                if (isAssignable(midClass, (Class) superClass) && isAssignable(genericInterface, midClass)) {
                    genericInterface = midType;
                }
            }
            if (genericInterface != null) {
                return genericInterface;
            }
        }
        return cls.getGenericSuperclass();
    }

    public static boolean isInstance(Object value, Type type) {
        boolean z = false;
        if (type == null) {
            return false;
        }
        if (value == null) {
            if (type instanceof Class) {
                if (((Class) type).isPrimitive()) {
                }
            }
            z = true;
        } else {
            z = isAssignable(value.getClass(), type, null);
        }
        return z;
    }

    public static Type[] normalizeUpperBounds(Type[] bounds) {
        Validate.notNull(bounds, "null value specified for bounds array", new Object[0]);
        if (bounds.length < 2) {
            return bounds;
        }
        Set<Type> types = new HashSet(bounds.length);
        for (Type type1 : bounds) {
            boolean subtypeFound = false;
            for (Type type2 : bounds) {
                if (type1 != type2 && isAssignable(type2, type1, null)) {
                    subtypeFound = true;
                    break;
                }
            }
            if (!subtypeFound) {
                types.add(type1);
            }
        }
        return (Type[]) types.toArray(new Type[types.size()]);
    }

    public static Type[] getImplicitBounds(TypeVariable<?> typeVariable) {
        Validate.notNull(typeVariable, "typeVariable is null", new Object[0]);
        Type[] bounds = typeVariable.getBounds();
        if (bounds.length != 0) {
            return normalizeUpperBounds(bounds);
        }
        return new Type[]{Object.class};
    }

    public static Type[] getImplicitUpperBounds(WildcardType wildcardType) {
        Validate.notNull(wildcardType, "wildcardType is null", new Object[0]);
        Type[] bounds = wildcardType.getUpperBounds();
        if (bounds.length != 0) {
            return normalizeUpperBounds(bounds);
        }
        return new Type[]{Object.class};
    }

    public static Type[] getImplicitLowerBounds(WildcardType wildcardType) {
        Validate.notNull(wildcardType, "wildcardType is null", new Object[0]);
        Type[] bounds = wildcardType.getLowerBounds();
        if (bounds.length != 0) {
            return bounds;
        }
        return new Type[]{null};
    }

    public static boolean typesSatisfyVariables(Map<TypeVariable<?>, Type> typeVarAssigns) {
        Validate.notNull(typeVarAssigns, "typeVarAssigns is null", new Object[0]);
        for (Entry<TypeVariable<?>, Type> entry : typeVarAssigns.entrySet()) {
            Type type = (Type) entry.getValue();
            for (Type bound : getImplicitBounds((TypeVariable) entry.getKey())) {
                if (!isAssignable(type, substituteTypeVariables(bound, typeVarAssigns), (Map) typeVarAssigns)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static Class<?> getRawType(ParameterizedType parameterizedType) {
        Type rawType = parameterizedType.getRawType();
        if (rawType instanceof Class) {
            return (Class) rawType;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Wait... What!? Type of rawType: ");
        stringBuilder.append(rawType);
        throw new IllegalStateException(stringBuilder.toString());
    }

    public static Class<?> getRawType(Type type, Type assigningType) {
        if (type instanceof Class) {
            return (Class) type;
        }
        if (type instanceof ParameterizedType) {
            return getRawType((ParameterizedType) type);
        }
        if (type instanceof TypeVariable) {
            if (assigningType == null) {
                return null;
            }
            Object genericDeclaration = ((TypeVariable) type).getGenericDeclaration();
            if (!(genericDeclaration instanceof Class)) {
                return null;
            }
            Map<TypeVariable<?>, Type> typeVarAssigns = getTypeArguments(assigningType, (Class) genericDeclaration);
            if (typeVarAssigns == null) {
                return null;
            }
            Type typeArgument = (Type) typeVarAssigns.get(type);
            if (typeArgument == null) {
                return null;
            }
            return getRawType(typeArgument, assigningType);
        } else if (type instanceof GenericArrayType) {
            return Array.newInstance(getRawType(((GenericArrayType) type).getGenericComponentType(), assigningType), 0).getClass();
        } else {
            if (type instanceof WildcardType) {
                return null;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("unknown type: ");
            stringBuilder.append(type);
            throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    public static boolean isArrayType(Type type) {
        if (!(type instanceof GenericArrayType)) {
            if (!(type instanceof Class) || !((Class) type).isArray()) {
                return false;
            }
        }
        return true;
    }

    public static Type getArrayComponentType(Type type) {
        Type type2 = null;
        if (type instanceof Class) {
            Class<?> clazz = (Class) type;
            if (clazz.isArray()) {
                type2 = clazz.getComponentType();
            }
            return type2;
        } else if (type instanceof GenericArrayType) {
            return ((GenericArrayType) type).getGenericComponentType();
        } else {
            return null;
        }
    }

    public static Type unrollVariables(Map<TypeVariable<?>, Type> typeArguments, Type type) {
        if (typeArguments == null) {
            typeArguments = Collections.emptyMap();
        }
        if (containsTypeVariables(type)) {
            if (type instanceof TypeVariable) {
                return unrollVariables(typeArguments, (Type) typeArguments.get(type));
            }
            if (type instanceof ParameterizedType) {
                Map<TypeVariable<?>, Type> parameterizedTypeArguments;
                ParameterizedType p = (ParameterizedType) type;
                if (p.getOwnerType() == null) {
                    parameterizedTypeArguments = typeArguments;
                } else {
                    parameterizedTypeArguments = new HashMap(typeArguments);
                    parameterizedTypeArguments.putAll(getTypeArguments(p));
                }
                Type[] args = p.getActualTypeArguments();
                for (int i = 0; i < args.length; i++) {
                    Type unrolled = unrollVariables(parameterizedTypeArguments, args[i]);
                    if (unrolled != null) {
                        args[i] = unrolled;
                    }
                }
                return parameterizeWithOwner(p.getOwnerType(), (Class) p.getRawType(), args);
            } else if (type instanceof WildcardType) {
                WildcardType wild = (WildcardType) type;
                return wildcardType().withUpperBounds(unrollBounds(typeArguments, wild.getUpperBounds())).withLowerBounds(unrollBounds(typeArguments, wild.getLowerBounds())).build();
            }
        }
        return type;
    }

    private static Type[] unrollBounds(Map<TypeVariable<?>, Type> typeArguments, Type[] bounds) {
        Type[] result = bounds;
        int i = 0;
        while (i < result.length) {
            Type unrolled = unrollVariables(typeArguments, result[i]);
            if (unrolled == null) {
                result = (Type[]) ArrayUtils.remove((Object[]) result, i);
                i--;
            } else {
                result[i] = unrolled;
            }
            i++;
        }
        return result;
    }

    public static boolean containsTypeVariables(Type type) {
        boolean z = true;
        if (type instanceof TypeVariable) {
            return true;
        }
        if (type instanceof Class) {
            if (((Class) type).getTypeParameters().length <= 0) {
                z = false;
            }
            return z;
        } else if (type instanceof ParameterizedType) {
            for (Type arg : ((ParameterizedType) type).getActualTypeArguments()) {
                if (containsTypeVariables(arg)) {
                    return true;
                }
            }
            return false;
        } else if (!(type instanceof WildcardType)) {
            return false;
        } else {
            WildcardType wild = (WildcardType) type;
            if (!containsTypeVariables(getImplicitLowerBounds(wild)[0])) {
                if (!containsTypeVariables(getImplicitUpperBounds(wild)[0])) {
                    z = false;
                }
            }
            return z;
        }
    }

    public static final ParameterizedType parameterize(Class<?> raw, Type... typeArguments) {
        return parameterizeWithOwner(null, (Class) raw, typeArguments);
    }

    public static final ParameterizedType parameterize(Class<?> raw, Map<TypeVariable<?>, Type> typeArgMappings) {
        Validate.notNull(raw, "raw class is null", new Object[0]);
        Validate.notNull(typeArgMappings, "typeArgMappings is null", new Object[0]);
        return parameterizeWithOwner(null, (Class) raw, extractTypeArgumentsFrom(typeArgMappings, raw.getTypeParameters()));
    }

    public static final ParameterizedType parameterizeWithOwner(Type owner, Class<?> raw, Type... typeArguments) {
        Type useOwner;
        Validate.notNull(raw, "raw class is null", new Object[0]);
        if (raw.getEnclosingClass() == null) {
            Validate.isTrue(owner == null, "no owner allowed for top-level %s", raw);
            useOwner = null;
        } else if (owner == null) {
            useOwner = raw.getEnclosingClass();
        } else {
            Validate.isTrue(isAssignable(owner, raw.getEnclosingClass()), "%s is invalid owner type for parameterized %s", owner, raw);
            useOwner = owner;
        }
        Validate.noNullElements((Object[]) typeArguments, "null type argument at index %s", new Object[0]);
        Validate.isTrue(raw.getTypeParameters().length == typeArguments.length, "invalid number of type parameters specified: expected %d, got %d", Integer.valueOf(raw.getTypeParameters().length), Integer.valueOf(typeArguments.length));
        return new ParameterizedTypeImpl(raw, useOwner, typeArguments);
    }

    public static final ParameterizedType parameterizeWithOwner(Type owner, Class<?> raw, Map<TypeVariable<?>, Type> typeArgMappings) {
        Validate.notNull(raw, "raw class is null", new Object[0]);
        Validate.notNull(typeArgMappings, "typeArgMappings is null", new Object[0]);
        return parameterizeWithOwner(owner, (Class) raw, extractTypeArgumentsFrom(typeArgMappings, raw.getTypeParameters()));
    }

    private static Type[] extractTypeArgumentsFrom(Map<TypeVariable<?>, Type> mappings, TypeVariable<?>[] variables) {
        Type[] result = new Type[variables.length];
        int index = 0;
        TypeVariable<?>[] arr$ = variables;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            Type var = arr$[i$];
            Validate.isTrue(mappings.containsKey(var), "missing argument mapping for %s", toString(var));
            int index2 = index + 1;
            result[index] = (Type) mappings.get(var);
            i$++;
            index = index2;
        }
        return result;
    }

    public static WildcardTypeBuilder wildcardType() {
        return new WildcardTypeBuilder();
    }

    public static GenericArrayType genericArrayType(Type componentType) {
        return new GenericArrayTypeImpl((Type) Validate.notNull(componentType, "componentType is null", new Object[0]));
    }

    public static boolean equals(Type t1, Type t2) {
        if (Objects.equals(t1, t2)) {
            return true;
        }
        if (t1 instanceof ParameterizedType) {
            return equals((ParameterizedType) t1, t2);
        }
        if (t1 instanceof GenericArrayType) {
            return equals((GenericArrayType) t1, t2);
        }
        if (t1 instanceof WildcardType) {
            return equals((WildcardType) t1, t2);
        }
        return false;
    }

    private static boolean equals(ParameterizedType p, Type t) {
        if (t instanceof ParameterizedType) {
            ParameterizedType other = (ParameterizedType) t;
            if (equals(p.getRawType(), other.getRawType()) && equals(p.getOwnerType(), other.getOwnerType())) {
                return equals(p.getActualTypeArguments(), other.getActualTypeArguments());
            }
        }
        return false;
    }

    private static boolean equals(GenericArrayType a, Type t) {
        return (t instanceof GenericArrayType) && equals(a.getGenericComponentType(), ((GenericArrayType) t).getGenericComponentType());
    }

    private static boolean equals(WildcardType w, Type t) {
        boolean z = false;
        if (!(t instanceof WildcardType)) {
            return false;
        }
        WildcardType other = (WildcardType) t;
        if (equals(getImplicitLowerBounds(w), getImplicitLowerBounds(other)) && equals(getImplicitUpperBounds(w), getImplicitUpperBounds(other))) {
            z = true;
        }
        return z;
    }

    private static boolean equals(Type[] t1, Type[] t2) {
        if (t1.length != t2.length) {
            return false;
        }
        for (int i = 0; i < t1.length; i++) {
            if (!equals(t1[i], t2[i])) {
                return false;
            }
        }
        return true;
    }

    public static String toString(Type type) {
        Validate.notNull(type);
        if (type instanceof Class) {
            return classToString((Class) type);
        }
        if (type instanceof ParameterizedType) {
            return parameterizedTypeToString((ParameterizedType) type);
        }
        if (type instanceof WildcardType) {
            return wildcardTypeToString((WildcardType) type);
        }
        if (type instanceof TypeVariable) {
            return typeVariableToString((TypeVariable) type);
        }
        if (type instanceof GenericArrayType) {
            return genericArrayTypeToString((GenericArrayType) type);
        }
        throw new IllegalArgumentException(ObjectUtils.identityToString(type));
    }

    public static String toLongString(TypeVariable<?> var) {
        Validate.notNull(var, "var is null", new Object[0]);
        StringBuilder buf = new StringBuilder();
        GenericDeclaration d = var.getGenericDeclaration();
        if (d instanceof Class) {
            Class<?> c = (Class) d;
            while (c.getEnclosingClass() != null) {
                buf.insert(0, c.getSimpleName()).insert(0, '.');
                c = c.getEnclosingClass();
            }
            buf.insert(0, c.getName());
        } else if (d instanceof Type) {
            buf.append(toString((Type) d));
        } else {
            buf.append(d);
        }
        buf.append(':');
        buf.append(typeVariableToString(var));
        return buf.toString();
    }

    public static <T> Typed<T> wrap(final Type type) {
        return new Typed<T>() {
            public Type getType() {
                return type;
            }
        };
    }

    public static <T> Typed<T> wrap(Class<T> type) {
        return wrap((Type) type);
    }

    private static String classToString(Class<?> c) {
        if (c.isArray()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(toString(c.getComponentType()));
            stringBuilder.append("[]");
            return stringBuilder.toString();
        }
        stringBuilder = new StringBuilder();
        if (c.getEnclosingClass() != null) {
            stringBuilder.append(classToString(c.getEnclosingClass()));
            stringBuilder.append('.');
            stringBuilder.append(c.getSimpleName());
        } else {
            stringBuilder.append(c.getName());
        }
        if (c.getTypeParameters().length > 0) {
            stringBuilder.append(Typography.less);
            appendAllTo(stringBuilder, ", ", c.getTypeParameters());
            stringBuilder.append(Typography.greater);
        }
        return stringBuilder.toString();
    }

    private static String typeVariableToString(TypeVariable<?> v) {
        StringBuilder buf = new StringBuilder(v.getName());
        Type[] bounds = v.getBounds();
        if (bounds.length > 0 && (bounds.length != 1 || !Object.class.equals(bounds[0]))) {
            buf.append(" extends ");
            appendAllTo(buf, " & ", v.getBounds());
        }
        return buf.toString();
    }

    private static String parameterizedTypeToString(ParameterizedType p) {
        StringBuilder buf = new StringBuilder();
        Type useOwner = p.getOwnerType();
        Class<?> raw = (Class) p.getRawType();
        if (useOwner == null) {
            buf.append(raw.getName());
        } else {
            if (useOwner instanceof Class) {
                buf.append(((Class) useOwner).getName());
            } else {
                buf.append(useOwner.toString());
            }
            buf.append('.');
            buf.append(raw.getSimpleName());
        }
        int[] recursiveTypeIndexes = findRecursiveTypes(p);
        if (recursiveTypeIndexes.length > 0) {
            appendRecursiveTypes(buf, recursiveTypeIndexes, p.getActualTypeArguments());
        } else {
            buf.append(Typography.less);
            appendAllTo(buf, ", ", p.getActualTypeArguments()).append(Typography.greater);
        }
        return buf.toString();
    }

    private static void appendRecursiveTypes(StringBuilder buf, int[] recursiveTypeIndexes, Type[] argumentTypes) {
        for (int i = 0; i < recursiveTypeIndexes.length; i++) {
            buf.append(Typography.less);
            appendAllTo(buf, ", ", argumentTypes[i].toString()).append(Typography.greater);
        }
        Type[] argumentsFiltered = (Type[]) ArrayUtils.removeAll((Object[]) argumentTypes, recursiveTypeIndexes);
        if (argumentsFiltered.length > 0) {
            buf.append(Typography.less);
            appendAllTo(buf, ", ", argumentsFiltered).append(Typography.greater);
        }
    }

    private static int[] findRecursiveTypes(ParameterizedType p) {
        Type[] filteredArgumentTypes = (Type[]) Arrays.copyOf(p.getActualTypeArguments(), p.getActualTypeArguments().length);
        int[] indexesToRemove = new int[null];
        for (int i = 0; i < filteredArgumentTypes.length; i++) {
            if (filteredArgumentTypes[i] instanceof TypeVariable) {
                if (containsVariableTypeSameParametrizedTypeBound((TypeVariable) filteredArgumentTypes[i], p)) {
                    indexesToRemove = ArrayUtils.add(indexesToRemove, i);
                }
            }
        }
        return indexesToRemove;
    }

    private static boolean containsVariableTypeSameParametrizedTypeBound(TypeVariable<?> typeVariable, ParameterizedType p) {
        return ArrayUtils.contains(typeVariable.getBounds(), (Object) p);
    }

    private static String wildcardTypeToString(WildcardType w) {
        StringBuilder buf = new StringBuilder().append('?');
        Type[] lowerBounds = w.getLowerBounds();
        Type[] upperBounds = w.getUpperBounds();
        if (lowerBounds.length <= 1) {
            if (lowerBounds.length != 1 || lowerBounds[0] == null) {
                if (upperBounds.length <= 1) {
                    if (upperBounds.length != 1 || Object.class.equals(upperBounds[0])) {
                        return buf.toString();
                    }
                }
                buf.append(" extends ");
                appendAllTo(buf, " & ", upperBounds);
                return buf.toString();
            }
        }
        buf.append(" super ");
        appendAllTo(buf, " & ", lowerBounds);
        return buf.toString();
    }

    private static String genericArrayTypeToString(GenericArrayType g) {
        return String.format("%s[]", new Object[]{toString(g.getGenericComponentType())});
    }

    private static <T> StringBuilder appendAllTo(StringBuilder buf, String sep, T... types) {
        Validate.notEmpty(Validate.noNullElements((Object[]) types));
        if (types.length > 0) {
            buf.append(toString(types[0]));
            for (int i = 1; i < types.length; i++) {
                buf.append(sep);
                buf.append(toString(types[i]));
            }
        }
        return buf;
    }

    private static <T> String toString(T object) {
        return object instanceof Type ? toString((Type) object) : object.toString();
    }
}
