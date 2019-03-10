package org.awaitility.core;

import org.awaitility.Duration;
import org.awaitility.constraint.WaitConstraint;
import org.awaitility.pollinterval.PollInterval;

public class ConditionSettings {
    private final String alias;
    private final boolean catchUncaughtExceptions;
    private final ConditionEvaluationListener conditionEvaluationListener;
    private final ExecutorLifecycle executorLifecycle;
    private final ExceptionIgnorer ignoreExceptions;
    private final Duration pollDelay;
    private final PollInterval pollInterval;
    private final WaitConstraint waitConstraint;

    ConditionSettings(String alias, boolean catchUncaughtExceptions, WaitConstraint waitConstraint, PollInterval pollInterval, Duration pollDelay, ConditionEvaluationListener conditionEvaluationListener, ExceptionIgnorer ignoreExceptions, ExecutorLifecycle executorLifecycle) {
        if (waitConstraint == null) {
            throw new IllegalArgumentException("You must specify a maximum waiting time (was null).");
        } else if (pollInterval != null) {
            this.executorLifecycle = executorLifecycle;
            this.alias = alias;
            this.waitConstraint = waitConstraint;
            this.pollInterval = pollInterval;
            this.pollDelay = pollDelay;
            this.catchUncaughtExceptions = catchUncaughtExceptions;
            this.conditionEvaluationListener = conditionEvaluationListener;
            this.ignoreExceptions = ignoreExceptions;
        } else {
            throw new IllegalArgumentException("You must specify a poll interval (was null).");
        }
    }

    public String getAlias() {
        return this.alias;
    }

    public Duration getMaxWaitTime() {
        return this.waitConstraint.getMaxWaitTime();
    }

    public Duration getMinWaitTime() {
        return this.waitConstraint.getMinWaitTime();
    }

    public PollInterval getPollInterval() {
        return this.pollInterval;
    }

    public Duration getPollDelay() {
        return this.pollDelay;
    }

    public boolean hasAlias() {
        return this.alias != null;
    }

    public boolean shouldCatchUncaughtExceptions() {
        return this.catchUncaughtExceptions;
    }

    public ConditionEvaluationListener getConditionEvaluationListener() {
        return this.conditionEvaluationListener;
    }

    public boolean shouldExceptionBeIgnored(Throwable e) {
        return this.ignoreExceptions.shouldIgnoreException(e);
    }

    public ExecutorLifecycle getExecutorLifecycle() {
        return this.executorLifecycle;
    }
}
