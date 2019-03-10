package org.awaitility.reflect;

import java.lang.reflect.Field;
import org.apache.commons.lang3.StringUtils;
import org.awaitility.reflect.exception.FieldNotFoundException;

class FieldNameAndTypeMatcherStrategy extends FieldMatcherStrategy {
    private final String fieldName;
    private final Class<?> fieldType;

    public FieldNameAndTypeMatcherStrategy(String fieldName, Class<?> type) {
        if (fieldName == null || fieldName.equals("") || fieldName.startsWith(StringUtils.SPACE)) {
            throw new IllegalArgumentException("field name cannot be null.");
        } else if (type != null) {
            this.fieldName = fieldName;
            this.fieldType = type;
        } else {
            throw new IllegalArgumentException("type cannot be null.");
        }
    }

    public boolean matches(Field field) {
        return this.fieldName.equals(field.getName()) && this.fieldType.equals(field.getType());
    }

    public void notFound(Class<?> type, boolean isInstanceField) throws FieldNotFoundException {
        Object[] objArr = new Object[4];
        objArr[0] = isInstanceField ? "instance" : "static";
        objArr[1] = this.fieldName;
        objArr[2] = this.fieldType.getName();
        objArr[3] = type.getName();
        throw new FieldNotFoundException(String.format("No %s field with name \"%s\" and type \"%s\" could be found in the class hierarchy of %s.", objArr));
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("fieldName ");
        stringBuilder.append(this.fieldName);
        stringBuilder.append(", fieldType = ");
        stringBuilder.append(this.fieldType.getName());
        return stringBuilder.toString();
    }
}
