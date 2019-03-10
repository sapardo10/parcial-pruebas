package org.awaitility.constraint;

import org.awaitility.Duration;

public interface WaitConstraint {
    Duration getMaxWaitTime();

    Duration getMinWaitTime();

    WaitConstraint withMaxWaitTime(Duration duration);

    WaitConstraint withMinWaitTime(Duration duration);
}
