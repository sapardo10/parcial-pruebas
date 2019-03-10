package org.awaitility.core;

import java.util.concurrent.TimeUnit;

public class ConditionEvaluationLogger implements ConditionEvaluationListener<Object> {
    private final TimeUnit timeUnit;

    public ConditionEvaluationLogger() {
        this(TimeUnit.MILLISECONDS);
    }

    public ConditionEvaluationLogger(TimeUnit timeUnit) {
        if (timeUnit != null) {
            this.timeUnit = timeUnit;
            return;
        }
        throw new IllegalArgumentException("TimeUnit cannot be null");
    }

    public void conditionEvaluated(EvaluatedCondition<Object> condition) {
        String description = condition.getDescription();
        long elapsedTime = this.timeUnit.convert(condition.getElapsedTimeInMS(), TimeUnit.MILLISECONDS);
        long remainingTime = this.timeUnit.convert(condition.getRemainingTimeInMS(), TimeUnit.MILLISECONDS);
        String timeUnitAsString = this.timeUnit.toString().toLowerCase();
        if (condition.isSatisfied()) {
            System.out.printf("%s after %d %s (remaining time %d %s, last poll interval was %d %s)%n", new Object[]{description, Long.valueOf(elapsedTime), timeUnitAsString, Long.valueOf(remainingTime), timeUnitAsString, Long.valueOf(condition.getPollInterval().getValue()), condition.getPollInterval().getTimeUnitAsString()});
            return;
        }
        System.out.printf("%s (elapsed time %d %s, remaining time %d %s (last poll interval was %d %s))%n", new Object[]{description, Long.valueOf(elapsedTime), timeUnitAsString, Long.valueOf(remainingTime), timeUnitAsString, Long.valueOf(condition.getPollInterval().getValue()), condition.getPollInterval().getTimeUnitAsString()});
    }
}
