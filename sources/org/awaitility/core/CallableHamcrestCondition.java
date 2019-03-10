package org.awaitility.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import org.awaitility.reflect.WhiteboxImpl;
import org.hamcrest.Matcher;

class CallableHamcrestCondition<T> extends AbstractHamcrestCondition<T> {
    public CallableHamcrestCondition(Callable<T> supplier, Matcher<? super T> matcher, ConditionSettings settings) {
        super(supplier, matcher, settings);
    }

    protected String getCallableDescription(Callable<T> supplier) {
        Class<? extends Callable> supplierClass = supplier.getClass();
        Method enclosingMethod = supplierClass.getEnclosingMethod();
        if (isFieldSupplier(supplierClass)) {
            return generateFieldSupplierErrorMessage(supplier);
        }
        if (supplierClass.isAnonymousClass() && enclosingMethod != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(enclosingMethod.getDeclaringClass().getName());
            stringBuilder.append(".");
            stringBuilder.append(enclosingMethod.getName());
            stringBuilder.append(" Callable");
            return stringBuilder.toString();
        } else if (LambdaErrorMessageGenerator.isLambdaClass(supplierClass)) {
            return LambdaErrorMessageGenerator.generateLambdaErrorMessagePrefix(supplierClass, true);
        } else {
            return supplierClass.getName();
        }
    }

    private boolean isFieldSupplier(Class<?> supplierClass) {
        return supplierClass.isMemberClass() && supplierClass.getEnclosingClass() == FieldSupplierBuilder.class;
    }

    private String generateFieldSupplierErrorMessage(Callable<T> supplier) {
        FieldSupplierBuilder fieldSupplier = (FieldSupplierBuilder) WhiteboxImpl.getInternalState((Object) supplier, "this$0");
        Class<? extends Annotation> expectedAnnotation = fieldSupplier.getExpectedAnnotation();
        String expectedFieldName = fieldSupplier.getExpectedFieldName();
        Class<?> expectedFieldType = fieldSupplier.getExpectedFieldType();
        Object object = fieldSupplier.getObject();
        Class<?> objectClass = object instanceof Class ? (Class) object : object.getClass();
        StringBuilder builder = new StringBuilder();
        if (expectedFieldName == null) {
            builder.append("Field in ");
            builder.append(object.getClass().getName());
            if (expectedAnnotation != null) {
                builder.append(" annotated with ");
                builder.append(expectedAnnotation.getName());
                builder.append(" and");
            }
            builder.append(" of type ");
            builder.append(expectedFieldType);
        } else {
            try {
                Field declaredField = objectClass.getDeclaredField(expectedFieldName);
                builder.append("Field ");
                builder.append(declaredField);
            } catch (Exception e) {
                throw new RuntimeException("Internal error", e);
            }
        }
        return builder.toString();
    }
}
