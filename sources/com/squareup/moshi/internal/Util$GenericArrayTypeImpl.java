package com.squareup.moshi.internal;

import com.squareup.moshi.Types;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

public final class Util$GenericArrayTypeImpl implements GenericArrayType {
    private final Type componentType;

    public Util$GenericArrayTypeImpl(Type componentType) {
        this.componentType = Util.canonicalize(componentType);
    }

    public Type getGenericComponentType() {
        return this.componentType;
    }

    public boolean equals(Object o) {
        if (o instanceof GenericArrayType) {
            if (Types.equals(this, (GenericArrayType) o)) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        return this.componentType.hashCode();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Util.typeToString(this.componentType));
        stringBuilder.append("[]");
        return stringBuilder.toString();
    }
}
