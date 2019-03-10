package org.awaitility.reflect;

import java.lang.reflect.Field;
import org.apache.commons.lang3.StringUtils;
import org.awaitility.reflect.exception.FieldNotFoundException;

class FieldNameMatcherStrategy extends FieldMatcherStrategy {
    private final String fieldName;

    public FieldNameMatcherStrategy(String fieldName) {
        if (fieldName == null || fieldName.equals("") || fieldName.startsWith(StringUtils.SPACE)) {
            throw new IllegalArgumentException("field name cannot be null.");
        }
        this.fieldName = fieldName;
    }

    public boolean matches(Field field) {
        return this.fieldName.equals(field.getName());
    }

    public void notFound(Class<?> type, boolean isInstanceField) throws FieldNotFoundException {
        Object[] objArr = new Object[3];
        objArr[0] = isInstanceField ? "instance" : "static";
        objArr[1] = this.fieldName;
        objArr[2] = type.getName();
        throw new FieldNotFoundException(String.format("No %s field named \"%s\" could be found in the class hierarchy of %s.", objArr));
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("fieldName ");
        stringBuilder.append(this.fieldName);
        return stringBuilder.toString();
    }
}
