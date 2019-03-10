package org.awaitility.reflect;

import java.lang.reflect.Field;
import org.awaitility.reflect.exception.FieldNotFoundException;

class FieldTypeMatcherStrategy extends FieldMatcherStrategy {
    final Class<?> expectedFieldType;

    public FieldTypeMatcherStrategy(Class<?> fieldType) {
        if (fieldType != null) {
            this.expectedFieldType = fieldType;
            return;
        }
        throw new IllegalArgumentException("field type cannot be null.");
    }

    public boolean matches(Field field) {
        return this.expectedFieldType.equals(field.getType());
    }

    public void notFound(Class<?> type, boolean isInstanceField) throws FieldNotFoundException {
        Object[] objArr = new Object[3];
        objArr[0] = isInstanceField ? "instance" : "static";
        objArr[1] = this.expectedFieldType.getName();
        objArr[2] = type.getName();
        throw new FieldNotFoundException(String.format("No %s field of type \"%s\" could be found in the class hierarchy of %s.", objArr));
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("type ");
        stringBuilder.append(this.expectedFieldType.getName());
        return stringBuilder.toString();
    }
}
