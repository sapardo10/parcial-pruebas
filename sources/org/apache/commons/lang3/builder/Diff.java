package org.apache.commons.lang3.builder;

import java.lang.reflect.Type;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.apache.commons.lang3.tuple.Pair;

public abstract class Diff<T> extends Pair<T, T> {
    private static final long serialVersionUID = 1;
    private final String fieldName;
    private final Type type = ((Type) ObjectUtils.defaultIfNull(TypeUtils.getTypeArguments(getClass(), Diff.class).get(Diff.class.getTypeParameters()[0]), Object.class));

    protected Diff(String fieldName) {
        this.fieldName = fieldName;
    }

    public final Type getType() {
        return this.type;
    }

    public final String getFieldName() {
        return this.fieldName;
    }

    public final String toString() {
        return String.format("[%s: %s, %s]", new Object[]{this.fieldName, getLeft(), getRight()});
    }

    public final T setValue(T t) {
        throw new UnsupportedOperationException("Cannot alter Diff object.");
    }
}
