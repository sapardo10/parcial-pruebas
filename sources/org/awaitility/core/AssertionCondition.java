package org.awaitility.core;

import java.lang.reflect.Method;
import org.apache.commons.lang3.StringUtils;
import org.awaitility.Duration;

public class AssertionCondition implements Condition<Void> {
    private final ConditionAwaiter conditionAwaiter;
    private final ConditionEvaluationHandler<Object> conditionEvaluationHandler;
    private String lastExceptionMessage;

    public AssertionCondition(final ThrowingRunnable supplier, final ConditionSettings settings) {
        if (supplier != null) {
            this.conditionEvaluationHandler = new ConditionEvaluationHandler(null, settings);
            final ThrowingRunnable throwingRunnable = supplier;
            final ConditionSettings conditionSettings = settings;
            this.conditionAwaiter = new ConditionAwaiter(new ConditionEvaluator() {
                public ConditionEvaluationResult eval(Duration pollInterval) throws Exception {
                    try {
                        supplier.run();
                        AssertionCondition.this.conditionEvaluationHandler.handleConditionResultMatch(AssertionCondition.this.getMatchMessage(supplier, settings.getAlias()), null, pollInterval);
                        return new ConditionEvaluationResult(true);
                    } catch (AssertionError e) {
                        AssertionCondition.this.lastExceptionMessage = e.getMessage();
                        ConditionEvaluationHandler access$100 = AssertionCondition.this.conditionEvaluationHandler;
                        AssertionCondition assertionCondition = AssertionCondition.this;
                        access$100.handleConditionResultMismatch(assertionCondition.getMismatchMessage(supplier, assertionCondition.lastExceptionMessage, settings.getAlias(), true), null, pollInterval);
                        return new ConditionEvaluationResult(false, null, e);
                    } catch (Throwable throwable) {
                        return (ConditionEvaluationResult) CheckedExceptionRethrower.safeRethrow(throwable);
                    }
                }
            }, settings) {
                protected String getTimeoutMessage() {
                    AssertionCondition assertionCondition = AssertionCondition.this;
                    return assertionCondition.getMismatchMessage(throwingRunnable, assertionCondition.lastExceptionMessage, conditionSettings.getAlias(), false);
                }
            };
            return;
        }
        throw new IllegalArgumentException("You must specify a supplier (was null).");
    }

    private String getMatchMessage(ThrowingRunnable supplier, String conditionAlias) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(generateDescriptionPrefix(supplier, conditionAlias, true));
        stringBuilder.append(" reached its end value");
        return stringBuilder.toString();
    }

    private String getMismatchMessage(ThrowingRunnable supplier, String exceptionMessage, String conditionAlias, boolean includeAliasIfDefined) {
        if (exceptionMessage != null && exceptionMessage.endsWith(".")) {
            exceptionMessage = exceptionMessage.substring(0, exceptionMessage.length() - 1);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(generateDescriptionPrefix(supplier, conditionAlias, includeAliasIfDefined));
        stringBuilder.append(StringUtils.SPACE);
        stringBuilder.append(exceptionMessage);
        return stringBuilder.toString();
    }

    private String generateDescriptionPrefix(ThrowingRunnable supplier, String conditionAlias, boolean includeAliasIfDefined) {
        String methodDescription = generateMethodDescription(supplier);
        boolean hasAlias = conditionAlias != null;
        String prefix;
        if (LambdaErrorMessageGenerator.isLambdaClass(supplier.getClass())) {
            if (hasAlias && includeAliasIfDefined) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Assertion condition with alias ");
                stringBuilder.append(conditionAlias);
                stringBuilder.append(" defined as a ");
                prefix = stringBuilder.toString();
            } else {
                prefix = "Assertion condition defined as a ";
            }
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(prefix);
            stringBuilder2.append(LambdaErrorMessageGenerator.generateLambdaErrorMessagePrefix(supplier.getClass(), false));
            stringBuilder2.append(methodDescription);
            return stringBuilder2.toString();
        }
        StringBuilder stringBuilder3 = new StringBuilder();
        stringBuilder3.append("Assertion condition");
        if (hasAlias) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(" with alias ");
            stringBuilder.append(conditionAlias);
            prefix = stringBuilder.toString();
        } else {
            prefix = "";
        }
        stringBuilder3.append(prefix);
        stringBuilder3.append(methodDescription);
        return stringBuilder3.toString();
    }

    private String generateMethodDescription(ThrowingRunnable supplier) {
        String methodDescription = "";
        Method enclosingMethod = null;
        try {
            enclosingMethod = supplier.getClass().getEnclosingMethod();
        } catch (Error e) {
        }
        if (enclosingMethod == null) {
            return methodDescription;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" defined in ");
        stringBuilder.append(enclosingMethod.toString());
        return stringBuilder.toString();
    }

    public Void await() {
        this.conditionAwaiter.await(this.conditionEvaluationHandler);
        return null;
    }
}
