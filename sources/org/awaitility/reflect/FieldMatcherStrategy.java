package org.awaitility.reflect;

import java.lang.reflect.Field;
import org.awaitility.reflect.exception.FieldNotFoundException;

abstract class FieldMatcherStrategy {
    public abstract boolean matches(Field field);

    public abstract void notFound(Class<?> cls, boolean z) throws FieldNotFoundException;

    FieldMatcherStrategy() {
    }
}
