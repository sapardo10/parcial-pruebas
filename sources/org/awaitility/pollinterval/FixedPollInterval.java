package org.awaitility.pollinterval;

import java.util.concurrent.TimeUnit;
import org.awaitility.Duration;

public class FixedPollInterval implements PollInterval {
    private final Duration duration;

    public FixedPollInterval(Duration duration) {
        if (duration == null) {
            throw new IllegalArgumentException("Duration cannot be null");
        } else if (duration.isForever()) {
            throw new IllegalArgumentException("Cannot use a fixed poll interval of length 'forever'");
        } else {
            this.duration = duration;
        }
    }

    public FixedPollInterval(long pollInterval, TimeUnit unit) {
        this(new Duration(pollInterval, unit));
    }

    public Duration next(int pollCount, Duration previousDuration) {
        return this.duration;
    }

    public static FixedPollInterval fixed(Duration duration) {
        return new FixedPollInterval(duration);
    }

    public static FixedPollInterval fixed(long pollInterval, TimeUnit unit) {
        return new FixedPollInterval(pollInterval, unit);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FixedPollInterval)) {
            return false;
        }
        return this.duration.equals(((FixedPollInterval) o).duration);
    }

    public int hashCode() {
        return this.duration.hashCode();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("FixedPollInterval{duration=");
        stringBuilder.append(this.duration);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
