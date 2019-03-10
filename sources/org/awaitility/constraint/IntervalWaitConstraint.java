package org.awaitility.constraint;

import org.awaitility.Duration;

public class IntervalWaitConstraint extends AtMostWaitConstraint {
    private final Duration atLeastConstraint;

    private static IntervalWaitConstraint between(Duration notBeforeThan, Duration notLaterThan) {
        return new IntervalWaitConstraint(notBeforeThan, notLaterThan);
    }

    IntervalWaitConstraint(Duration atLeastConstraint, Duration atMostDuration) {
        super(atMostDuration);
        this.atLeastConstraint = atLeastConstraint;
    }

    public Duration getMinWaitTime() {
        return this.atLeastConstraint;
    }

    public WaitConstraint withMaxWaitTime(Duration maxWaitTime) {
        return between(this.atLeastConstraint, maxWaitTime);
    }
}
