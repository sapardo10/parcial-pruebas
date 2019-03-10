package org.awaitility.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.concurrent.Callable;
import org.awaitility.reflect.WhiteboxImpl;
import org.awaitility.reflect.exception.FieldNotFoundException;

public class FieldSupplierBuilder {
    private Class<? extends Annotation> expectedAnnotation;
    private String expectedFieldName;
    private Class<?> expectedFieldType;
    private final Object object;

    public class AnnotationFieldSupplier<T> implements Callable<T> {
        public Callable<T> andAnnotatedWith(Class<? extends Annotation> annotationType) {
            FieldSupplierBuilder.this.assertNotNullParameter(annotationType, "annotationType");
            if (WhiteboxImpl.getFieldAnnotatedWith(FieldSupplierBuilder.this.object, annotationType).getName().equals(FieldSupplierBuilder.this.expectedFieldName)) {
                FieldSupplierBuilder.this.expectedAnnotation = annotationType;
                return this;
            }
            throw new FieldNotFoundException(String.format("Couldn't find a field with name %s annotated with %s in %s.", new Object[]{FieldSupplierBuilder.this.expectedFieldName, annotationType.getClass().getName(), WhiteboxImpl.getType(FieldSupplierBuilder.this.object).getName()}));
        }

        public T call() throws Exception {
            return WhiteboxImpl.getByNameAndType(FieldSupplierBuilder.this.object, FieldSupplierBuilder.this.expectedFieldName, FieldSupplierBuilder.this.expectedFieldType);
        }
    }

    public class NameAndAnnotationFieldSupplier<T> implements Callable<T> {
        public AnnotationFieldSupplier<T> andWithName(String fieldName) {
            FieldSupplierBuilder.this.assertNotNullParameter(fieldName, "fieldName");
            FieldSupplierBuilder.this.expectedFieldName = fieldName;
            return new AnnotationFieldSupplier();
        }

        public NameFieldSupplier<T> andAnnotatedWith(Class<? extends Annotation> annotationType) {
            FieldSupplierBuilder.this.assertNotNullParameter(annotationType, "annotationType");
            FieldSupplierBuilder.this.expectedAnnotation = annotationType;
            return new NameFieldSupplier();
        }

        public T call() throws Exception {
            return WhiteboxImpl.getInternalState(FieldSupplierBuilder.this.object, FieldSupplierBuilder.this.expectedFieldType);
        }
    }

    public class NameAndTypeFieldSupplier<T> implements Callable<T> {
        public AnnotationFieldSupplier<T> andWithName(String fieldName) {
            FieldSupplierBuilder.this.assertNotNullParameter(fieldName, "fieldName");
            FieldSupplierBuilder.this.expectedFieldName = fieldName;
            return new AnnotationFieldSupplier();
        }

        public <S> NameAndAnnotationFieldSupplier<S> andOfType(Class<S> type) {
            FieldSupplierBuilder.this.assertNotNullParameter(type, "Expected field type");
            FieldSupplierBuilder.this.expectedFieldType = type;
            return new NameAndAnnotationFieldSupplier();
        }

        public T call() throws Exception {
            return WhiteboxImpl.getFieldAnnotatedWith(FieldSupplierBuilder.this.object, FieldSupplierBuilder.this.expectedAnnotation).get(FieldSupplierBuilder.this.object);
        }
    }

    public class NameFieldSupplier<T> implements Callable<T> {
        private final Field foundField;

        public NameFieldSupplier() {
            this.foundField = WhiteboxImpl.getFieldAnnotatedWith(FieldSupplierBuilder.this.object, FieldSupplierBuilder.this.expectedAnnotation);
            if (!this.foundField.getType().isAssignableFrom(FieldSupplierBuilder.this.expectedFieldType)) {
                throw new FieldNotFoundException(String.format("Couldn't find a field of type %s annotated with %s in %s.", new Object[]{this$0.expectedFieldType.getClass().getName(), this$0.expectedAnnotation.getClass().getName(), WhiteboxImpl.getType(this$0.object).getName()}));
            }
        }

        public Callable<T> andWithName(final String fieldName) {
            FieldSupplierBuilder.this.assertNotNullParameter(fieldName, "fieldName");
            return new Callable<T>() {
                public T call() throws Exception {
                    return WhiteboxImpl.getByNameAndType(FieldSupplierBuilder.this.object, fieldName, FieldSupplierBuilder.this.expectedFieldType);
                }
            };
        }

        public T call() throws Exception {
            Field field = this.foundField;
            if (field == null) {
                return WhiteboxImpl.getInternalState(FieldSupplierBuilder.this.object, FieldSupplierBuilder.this.expectedFieldType);
            }
            return field.get(FieldSupplierBuilder.this.object);
        }
    }

    public FieldSupplierBuilder(Object object) {
        assertNotNullParameter(object, "Object passed to fieldIn");
        this.object = object;
    }

    public <T> NameAndAnnotationFieldSupplier<T> ofType(Class<T> fieldType) {
        this.expectedFieldType = fieldType;
        return new NameAndAnnotationFieldSupplier();
    }

    private void assertNotNullParameter(Object parameterValue, String name) {
        if (parameterValue == null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(name);
            stringBuilder.append(" cannot be null");
            throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    Object getObject() {
        return this.object;
    }

    String getExpectedFieldName() {
        return this.expectedFieldName;
    }

    Class<?> getExpectedFieldType() {
        return this.expectedFieldType;
    }

    Class<? extends Annotation> getExpectedAnnotation() {
        return this.expectedAnnotation;
    }
}
