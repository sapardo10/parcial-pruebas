package org.awaitility.pollinterval;

import org.awaitility.Duration;

public interface PollInterval {
    Duration next(int i, Duration duration);
}
