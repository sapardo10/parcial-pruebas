package retrofit2;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

final class Utils$WildcardTypeImpl implements WildcardType {
    private final Type lowerBound;
    private final Type upperBound;

    Utils$WildcardTypeImpl(Type[] upperBounds, Type[] lowerBounds) {
        if (lowerBounds.length > 1) {
            throw new IllegalArgumentException();
        } else if (upperBounds.length != 1) {
            throw new IllegalArgumentException();
        } else if (lowerBounds.length == 1) {
            if (lowerBounds[0] != null) {
                Utils.checkNotPrimitive(lowerBounds[0]);
                if (upperBounds[0] == Object.class) {
                    this.lowerBound = lowerBounds[0];
                    this.upperBound = Object.class;
                    return;
                }
                throw new IllegalArgumentException();
            }
            throw new NullPointerException();
        } else if (upperBounds[0] != null) {
            Utils.checkNotPrimitive(upperBounds[0]);
            this.lowerBound = null;
            this.upperBound = upperBounds[0];
        } else {
            throw new NullPointerException();
        }
    }

    public Type[] getUpperBounds() {
        return new Type[]{this.upperBound};
    }

    public Type[] getLowerBounds() {
        if (this.lowerBound == null) {
            return Utils.EMPTY_TYPE_ARRAY;
        }
        return new Type[]{this.lowerBound};
    }

    public boolean equals(Object other) {
        return (other instanceof WildcardType) && Utils.equals(this, (WildcardType) other);
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
            stringBuilder.append(Utils.typeToString(this.lowerBound));
            return stringBuilder.toString();
        } else if (this.upperBound == Object.class) {
            return "?";
        } else {
            stringBuilder = new StringBuilder();
            stringBuilder.append("? extends ");
            stringBuilder.append(Utils.typeToString(this.upperBound));
            return stringBuilder.toString();
        }
    }
}
