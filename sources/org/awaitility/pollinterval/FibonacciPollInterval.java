package org.awaitility.pollinterval;

import java.util.concurrent.TimeUnit;
import org.awaitility.Duration;

public class FibonacciPollInterval implements PollInterval {
    private static final int DEFAULT_OFFSET = 0;
    private final int offset;
    private final TimeUnit timeUnit;

    public FibonacciPollInterval() {
        this(TimeUnit.MILLISECONDS);
    }

    public FibonacciPollInterval(TimeUnit timeUnit) {
        this(0, timeUnit);
    }

    public FibonacciPollInterval(int offset, TimeUnit timeUnit) {
        if (offset <= -1) {
            throw new IllegalArgumentException("Offset must be greater than or equal to -1");
        } else if (timeUnit != null) {
            this.offset = offset;
            this.timeUnit = timeUnit;
        } else {
            throw new IllegalArgumentException("Time unit cannot be null");
        }
    }

    public Duration next(int pollCount, Duration previousDuration) {
        return new Duration((long) fibonacci(this.offset + pollCount), this.timeUnit);
    }

    public static FibonacciPollInterval fibonacci() {
        return new FibonacciPollInterval();
    }

    public static FibonacciPollInterval fibonacci(TimeUnit timeUnit) {
        return new FibonacciPollInterval(timeUnit);
    }

    public static FibonacciPollInterval fibonacci(int offset, TimeUnit timeUnit) {
        return new FibonacciPollInterval(offset, timeUnit);
    }

    public FibonacciPollInterval with() {
        return this;
    }

    public FibonacciPollInterval and() {
        return this;
    }

    public FibonacciPollInterval timeUnit(TimeUnit unit) {
        return new FibonacciPollInterval(this.offset, unit);
    }

    public FibonacciPollInterval offset(int offset) {
        return new FibonacciPollInterval(offset, this.timeUnit);
    }

    protected int fibonacci(int value) {
        return fib(value, 1, 0);
    }

    private int fib(int value, int current, int previous) {
        if (value == 0) {
            return previous;
        }
        if (value == 1) {
            return current;
        }
        return fib(value - 1, current + previous, current);
    }

    public boolean equals(Object o) {
        boolean z = true;
        if (this == o) {
            return true;
        }
        if (!(o instanceof FibonacciPollInterval)) {
            return false;
        }
        FibonacciPollInterval that = (FibonacciPollInterval) o;
        if (this.offset != that.offset || this.timeUnit != that.timeUnit) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        return (this.timeUnit.hashCode() * 31) + this.offset;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("FibonacciPollInterval{offset=");
        stringBuilder.append(this.offset);
        stringBuilder.append(", timeUnit=");
        stringBuilder.append(this.timeUnit);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
