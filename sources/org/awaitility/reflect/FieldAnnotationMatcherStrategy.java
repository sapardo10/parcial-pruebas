package org.awaitility.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import org.awaitility.reflect.exception.FieldNotFoundException;

public class FieldAnnotationMatcherStrategy extends FieldMatcherStrategy {
    final Class<? extends Annotation> annotation;

    public FieldAnnotationMatcherStrategy(Class<? extends Annotation> annotation) {
        if (annotation != null) {
            this.annotation = annotation;
            return;
        }
        throw new IllegalArgumentException("You must specify an annotation.");
    }

    public boolean matches(Field field) {
        return field.isAnnotationPresent(this.annotation);
    }

    public void notFound(Class<?> type, boolean isInstanceField) throws FieldNotFoundException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("No field with annotation of type \"");
        stringBuilder.append(this.annotation.getName());
        stringBuilder.append("\" could be found in the class hierarchy of ");
        stringBuilder.append(type.getName());
        stringBuilder.append(".");
        throw new FieldNotFoundException(stringBuilder.toString());
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("annotation ");
        stringBuilder.append(this.annotation.getName());
        return stringBuilder.toString();
    }
}
