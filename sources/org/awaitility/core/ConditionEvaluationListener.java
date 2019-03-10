package org.awaitility.core;

public interface ConditionEvaluationListener<T> {
    void conditionEvaluated(EvaluatedCondition<T> evaluatedCondition);
}
