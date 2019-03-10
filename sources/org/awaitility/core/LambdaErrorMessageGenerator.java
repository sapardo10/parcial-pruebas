package org.awaitility.core;

import java.lang.reflect.Method;

class LambdaErrorMessageGenerator {
    private static final String LAMBDA_CLASS_NAME = "$$Lambda$";
    private static final String LAMBDA_METHOD_NAME = "$Lambda";

    LambdaErrorMessageGenerator() {
    }

    static boolean isLambdaClass(Class<?> cls) {
        return cls.getSimpleName().contains(LAMBDA_CLASS_NAME);
    }

    static String generateLambdaErrorMessagePrefix(Class<?> lambdaClass, boolean firstLetterLowerCaseAndEndWithColon) {
        String name = lambdaClass.getName();
        return addLambdaDetailsIfFound(lambdaClass, name.substring(0, name.indexOf(LAMBDA_CLASS_NAME)), firstLetterLowerCaseAndEndWithColon);
    }

    private static String addLambdaDetailsIfFound(Class<?> supplierClass, String nameWithoutLambda, boolean firstLetterUpperCaseAndEndWithColon) {
        String nameToReturn = nameWithoutLambda;
        Method lambdaMethod = null;
        for (Method declaredMethod : supplierClass.getDeclaredMethods()) {
            if (declaredMethod.getName().contains(LAMBDA_METHOD_NAME)) {
                lambdaMethod = declaredMethod;
                break;
            }
        }
        if (lambdaMethod == null) {
            return nameToReturn;
        }
        Class<?>[] lambdaParams = lambdaMethod.getParameterTypes();
        if (lambdaParams.length <= 0) {
            return nameToReturn;
        }
        StringBuilder nameToReturnBuilder = new StringBuilder(firstLetterUpperCaseAndEndWithColon ? "L" : "l");
        nameToReturnBuilder.append("ambda expression in ");
        nameToReturnBuilder = nameToReturnBuilder.append(nameToReturn);
        if (!nameWithoutLambda.equals(lambdaParams[0].getName())) {
            nameToReturnBuilder.append(" that uses ");
            for (int i = 0; i < lambdaParams.length; i++) {
                Class<?> lambdaParam = lambdaParams[i];
                nameToReturnBuilder.append(lambdaParam.getName());
                if (i + 1 != lambdaParams.length) {
                    nameToReturnBuilder.append(", ");
                    nameToReturnBuilder.append(lambdaParam.getName());
                } else if (firstLetterUpperCaseAndEndWithColon) {
                    nameToReturnBuilder.append(':');
                }
            }
        } else if (firstLetterUpperCaseAndEndWithColon) {
            nameToReturnBuilder.append(':');
        }
        return nameToReturnBuilder.toString();
    }
}
