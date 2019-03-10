package retrofit2;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

final class Utils$GenericArrayTypeImpl implements GenericArrayType {
    private final Type componentType;

    Utils$GenericArrayTypeImpl(Type componentType) {
        this.componentType = componentType;
    }

    public Type getGenericComponentType() {
        return this.componentType;
    }

    public boolean equals(Object o) {
        if (o instanceof GenericArrayType) {
            if (Utils.equals(this, (GenericArrayType) o)) {
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
        stringBuilder.append(Utils.typeToString(this.componentType));
        stringBuilder.append("[]");
        return stringBuilder.toString();
    }
}
