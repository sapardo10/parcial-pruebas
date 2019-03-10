package org.awaitility.pollinterval;

import org.awaitility.Duration;
import org.awaitility.core.Function;

public class IterativePollInterval implements PollInterval {
    private final Function<Duration, Duration> function;
    private final Duration startDuration;

    public IterativePollInterval(Function<Duration, Duration> function) {
        this(function, null, false);
    }

    public IterativePollInterval(Function<Duration, Duration> function, Duration startDuration) {
        this(function, startDuration, true);
    }

    private IterativePollInterval(Function<Duration, Duration> function, Duration startDuration, boolean startDurationExplicitlyDefined) {
        if (function != null) {
            if (startDurationExplicitlyDefined) {
                if (startDuration == null) {
                    throw new IllegalArgumentException("Start duration cannot be null");
                }
            }
            if (startDurationExplicitlyDefined) {
                if (startDuration.isForever()) {
                    throw new IllegalArgumentException("Cannot use a poll interval of length 'forever'");
                }
            }
            this.function = function;
            this.startDuration = startDuration;
            return;
        }
        throw new IllegalArgumentException("Function<Duration, Duration> cannot be null");
    }

    public Duration next(int pollCount, Duration previousDuration) {
        Duration durationToUse;
        if (pollCount != 1 || this.startDuration == null) {
            durationToUse = previousDuration;
        } else {
            durationToUse = this.startDuration;
        }
        return (Duration) this.function.apply(durationToUse);
    }

    public static IterativePollInterval iterative(Function<Duration, Duration> function) {
        return new IterativePollInterval(function);
    }

    public static IterativePollInterval iterative(Function<Duration, Duration> function, Duration startDuration) {
        return new IterativePollInterval(function, startDuration);
    }

    public IterativePollInterval with() {
        return this;
    }

    public IterativePollInterval startDuration(Duration duration) {
        return new IterativePollInterval(this.function, duration);
    }

    public boolean equals(Object o) {
        boolean z = true;
        if (this == o) {
            return true;
        }
        if (!(o instanceof IterativePollInterval)) {
            return false;
        }
        IterativePollInterval that = (IterativePollInterval) o;
        if (!this.function.equals(that.function) || !this.startDuration.equals(that.startDuration)) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        return (this.function.hashCode() * 31) + this.startDuration.hashCode();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("IterativePollInterval{function=");
        stringBuilder.append(this.function);
        stringBuilder.append(", startDuration=");
        stringBuilder.append(this.startDuration);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
