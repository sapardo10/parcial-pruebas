package org.awaitility.core;

import org.awaitility.Duration;
import org.hamcrest.Matcher;

public class EvaluatedCondition<T> {
    private final String alias;
    private final boolean conditionIsFulfilled;
    private final T currentConditionValue;
    private final String description;
    private final long elapsedTimeInMS;
    private final Matcher<? super T> matcher;
    private final Duration pollInterval;
    private final long remainingTimeInMS;

    EvaluatedCondition(String description, Matcher<? super T> matcher, T currentConditionValue, long elapsedTimeInMS, long remainingTimeInMS, boolean isConditionSatisfied, String alias, Duration pollInterval) {
        this.description = description;
        this.matcher = matcher;
        this.currentConditionValue = currentConditionValue;
        this.elapsedTimeInMS = elapsedTimeInMS;
        this.remainingTimeInMS = remainingTimeInMS;
        this.conditionIsFulfilled = isConditionSatisfied;
        this.alias = alias;
        this.pollInterval = pollInterval;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean isHamcrestCondition() {
        return this.matcher != null;
    }

    public Matcher<? super T> getMatcher() {
        return this.matcher;
    }

    public T getValue() {
        return this.currentConditionValue;
    }

    public long getElapsedTimeInMS() {
        return this.elapsedTimeInMS;
    }

    public long getRemainingTimeInMS() {
        return this.remainingTimeInMS;
    }

    public boolean isConditionRunningForever() {
        return getRemainingTimeInMS() == Long.MAX_VALUE;
    }

    public boolean isSatisfied() {
        return this.conditionIsFulfilled;
    }

    public String getAlias() {
        return this.alias;
    }

    public boolean hasAlias() {
        return this.alias != null;
    }

    public Duration getPollInterval() {
        return this.pollInterval;
    }
}
