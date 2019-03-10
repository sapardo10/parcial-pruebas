package org.awaitility.core;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import org.awaitility.Duration;
import org.awaitility.spi.Timeout;

class CallableCondition implements Condition<Void> {
    private final ConditionAwaiter conditionAwaiter;
    private final ConditionEvaluationHandler<Object> conditionEvaluationHandler;

    private static class ConditionEvaluationWrapper implements ConditionEvaluator {
        private final ConditionEvaluationHandler<Object> conditionEvaluationHandler;
        private final Callable<Boolean> matcher;
        private final ConditionSettings settings;

        ConditionEvaluationWrapper(Callable<Boolean> matcher, ConditionSettings settings, ConditionEvaluationHandler<Object> conditionEvaluationHandler) {
            this.matcher = matcher;
            this.settings = settings;
            this.conditionEvaluationHandler = conditionEvaluationHandler;
        }

        public ConditionEvaluationResult eval(Duration pollInterval) throws Exception {
            boolean conditionFulfilled = ((Boolean) this.matcher.call()).booleanValue();
            if (conditionFulfilled) {
                this.conditionEvaluationHandler.handleConditionResultMatch(getMatchMessage(this.matcher, this.settings.getAlias()), Boolean.valueOf(true), pollInterval);
            } else {
                this.conditionEvaluationHandler.handleConditionResultMismatch(getMismatchMessage(this.matcher, this.settings.getAlias()), Boolean.valueOf(false), pollInterval);
            }
            return new ConditionEvaluationResult(conditionFulfilled);
        }

        private String getMatchMessage(Callable<Boolean> matcher, String conditionAlias) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(generateDescriptionPrefix(matcher, conditionAlias));
            stringBuilder.append(" returned true");
            return stringBuilder.toString();
        }

        private String getMismatchMessage(Callable<Boolean> matcher, String conditionAlias) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(generateDescriptionPrefix(matcher, conditionAlias));
            stringBuilder.append(" returned false");
            return stringBuilder.toString();
        }

        private String generateDescriptionPrefix(Callable<Boolean> matcher, String conditionAlias) {
            String methodDescription = generateMethodDescription(matcher);
            boolean hasAlias = conditionAlias != null;
            String prefix;
            if (LambdaErrorMessageGenerator.isLambdaClass(matcher.getClass())) {
                if (hasAlias) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Condition with alias ");
                    stringBuilder.append(conditionAlias);
                    stringBuilder.append(" defined as a ");
                    prefix = stringBuilder.toString();
                } else {
                    prefix = "Condition defined as a ";
                }
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(prefix);
                stringBuilder2.append(LambdaErrorMessageGenerator.generateLambdaErrorMessagePrefix(matcher.getClass(), false));
                stringBuilder2.append(methodDescription);
                return stringBuilder2.toString();
            }
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append("Callable condition");
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

        private String generateMethodDescription(Callable<Boolean> matcher) {
            String methodDescription = "";
            Method enclosingMethod = matcher.getClass().getEnclosingMethod();
            if (enclosingMethod == null) {
                return methodDescription;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(" defined in ");
            stringBuilder.append(enclosingMethod.toString());
            return stringBuilder.toString();
        }
    }

    CallableCondition(final Callable<Boolean> matcher, ConditionSettings settings) {
        this.conditionEvaluationHandler = new ConditionEvaluationHandler(null, settings);
        this.conditionAwaiter = new ConditionAwaiter(new ConditionEvaluationWrapper(matcher, settings, this.conditionEvaluationHandler), settings) {
            protected String getTimeoutMessage() {
                if (Timeout.timeout_message != null) {
                    return Timeout.timeout_message;
                }
                String timeoutMessage;
                Class<? extends Callable> type = matcher;
                if (type == null) {
                    timeoutMessage = "";
                } else {
                    type = type.getClass();
                    Method enclosingMethod = type.getEnclosingMethod();
                    if (!type.isAnonymousClass() || enclosingMethod == null) {
                        String message;
                        if (LambdaErrorMessageGenerator.isLambdaClass(type)) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("with ");
                            stringBuilder.append(LambdaErrorMessageGenerator.generateLambdaErrorMessagePrefix(type, false));
                            message = stringBuilder.toString();
                        } else {
                            message = type.getName();
                        }
                        timeoutMessage = String.format("Condition %s was not fulfilled", new Object[]{message});
                    } else {
                        timeoutMessage = String.format("Condition returned by method \"%s\" in class %s was not fulfilled", new Object[]{enclosingMethod.getName(), enclosingMethod.getDeclaringClass().getName()});
                    }
                }
                return timeoutMessage;
            }
        };
    }

    public Void await() {
        this.conditionAwaiter.await(this.conditionEvaluationHandler);
        return null;
    }
}
