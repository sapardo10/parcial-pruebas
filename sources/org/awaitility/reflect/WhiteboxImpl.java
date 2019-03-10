package org.awaitility.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.awaitility.reflect.exception.FieldNotFoundException;
import org.awaitility.reflect.exception.TooManyFieldsFoundException;

public class WhiteboxImpl {
    public static <T> T getInternalState(Object object, String fieldName) {
        try {
            return findFieldInHierarchy(object, fieldName).get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Internal error: Failed to get field in method getInternalState.", e);
        }
    }

    private static Field findFieldInHierarchy(Object object, String fieldName) {
        return findFieldInHierarchy(object, new FieldNameMatcherStrategy(fieldName));
    }

    private static Field findFieldInHierarchy(Object object, FieldMatcherStrategy strategy) {
        assertObjectInGetInternalStateIsNotNull(object);
        return findSingleFieldUsingStrategy(strategy, object, true, getType(object));
    }

    private static void assertObjectInGetInternalStateIsNotNull(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("The object containing the field cannot be null");
        }
    }

    private static Field findSingleFieldUsingStrategy(FieldMatcherStrategy strategy, Object object, boolean checkHierarchy, Class<?> startClass) {
        assertObjectInGetInternalStateIsNotNull(object);
        Field foundField = null;
        Class<?> originalStartClass = startClass;
        while (startClass != null) {
            for (Field field : startClass.getDeclaredFields()) {
                if (strategy.matches(field) && hasFieldProperModifier(object, field)) {
                    if (foundField == null) {
                        foundField = field;
                    } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Two or more fields matching ");
                        stringBuilder.append(strategy);
                        stringBuilder.append(".");
                        throw new TooManyFieldsFoundException(stringBuilder.toString());
                    }
                }
            }
            if (foundField != null) {
                break;
            } else if (!checkHierarchy) {
                break;
            } else {
                startClass = startClass.getSuperclass();
            }
        }
        if (foundField == null) {
            strategy.notFound(originalStartClass, true ^ isClass(object));
            return null;
        }
        foundField.setAccessible(true);
        return foundField;
    }

    private static boolean hasFieldProperModifier(Object object, Field field) {
        if (object instanceof Class) {
            if (Modifier.isStatic(field.getModifiers())) {
                return true;
            }
        }
        if (!(object instanceof Class)) {
            if (Modifier.isStatic(field.getModifiers())) {
            }
            return true;
        }
        return false;
    }

    public static <T> T getInternalState(Object object, Class<T> fieldType) {
        try {
            return findFieldInHierarchy(object, new AssignableToFieldTypeMatcherStrategy(fieldType)).get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Internal error: Failed to get field in method getInternalState.", e);
        }
    }

    public static void throwExceptionIfFieldWasNotFound(Class<?> type, String fieldName, Field field) {
        if (field == null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("No field was found with name '");
            stringBuilder.append(fieldName);
            stringBuilder.append("' in class ");
            stringBuilder.append(type.getName());
            stringBuilder.append(".");
            throw new FieldNotFoundException(stringBuilder.toString());
        }
    }

    public static Class<?> getType(Object object) {
        if (isClass(object)) {
            return (Class) object;
        }
        if (object != null) {
            return object.getClass();
        }
        return null;
    }

    public static Field getFieldAnnotatedWith(Object object, Class<? extends Annotation> annotationType) {
        return findSingleFieldUsingStrategy(new FieldAnnotationMatcherStrategy(annotationType), object, true, getType(object));
    }

    public static boolean isClass(Object argument) {
        return argument instanceof Class;
    }

    public static <T> T getByNameAndType(Object object, String fieldName, Class<T> expectedFieldType) {
        try {
            return findSingleFieldUsingStrategy(new FieldNameAndTypeMatcherStrategy(fieldName, expectedFieldType), object, true, getType(object)).get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Internal error: Failed to get field in method getInternalState.", e);
        }
    }
}
