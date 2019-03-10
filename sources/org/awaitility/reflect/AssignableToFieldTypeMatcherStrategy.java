package org.awaitility.reflect;

import java.lang.reflect.Field;

class AssignableToFieldTypeMatcherStrategy extends FieldTypeMatcherStrategy {
    public AssignableToFieldTypeMatcherStrategy(Class<?> fieldType) {
        super(fieldType);
    }

    public boolean matches(Field field) {
        return this.expectedFieldType.isAssignableFrom(field.getType());
    }
}
