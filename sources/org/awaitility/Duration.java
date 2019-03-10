package org.awaitility;

import java.util.concurrent.TimeUnit;

public class Duration implements Comparable<Duration> {
    public static final Duration FIVE_HUNDRED_MILLISECONDS = new Duration(500, TimeUnit.MILLISECONDS);
    public static final Duration FIVE_MINUTES = new Duration(300, TimeUnit.SECONDS);
    public static final Duration FIVE_SECONDS = new Duration(5, TimeUnit.SECONDS);
    public static final Duration FOREVER = new Duration(Long.MAX_VALUE, TimeUnit.DAYS);
    private static final int NONE = -1;
    public static final Duration ONE_HUNDRED_MILLISECONDS = new Duration(100, TimeUnit.MILLISECONDS);
    public static final Duration ONE_MILLISECOND = new Duration(1, TimeUnit.MILLISECONDS);
    public static final Duration ONE_MINUTE = new Duration(60, TimeUnit.SECONDS);
    public static final Duration ONE_SECOND = new Duration(1, TimeUnit.SECONDS);
    public static final Duration TEN_MINUTES = new Duration(600, TimeUnit.SECONDS);
    public static final Duration TEN_SECONDS = new Duration(10, TimeUnit.SECONDS);
    public static final Duration TWO_HUNDRED_MILLISECONDS = new Duration(200, TimeUnit.MILLISECONDS);
    public static final Duration TWO_MINUTES = new Duration(120, TimeUnit.SECONDS);
    public static final Duration TWO_SECONDS = new Duration(2, TimeUnit.SECONDS);
    public static final Duration ZERO = new Duration(0, TimeUnit.MILLISECONDS);
    private final TimeUnit unit;
    private final long value;

    private static abstract class BiFunction {
        abstract long apply(long j, long j2);

        protected abstract Duration handleSpecialCases(Duration duration, Duration duration2);

        private BiFunction() {
        }

        public final Duration apply(Duration lhs, Duration rhs) {
            if (lhs == null || rhs == null) {
                throw new IllegalArgumentException("Duration cannot be null");
            }
            Duration specialDuration = handleSpecialCases(lhs, rhs);
            if (specialDuration != null) {
                return specialDuration;
            }
            Duration newDuration;
            if (lhs.getTimeUnit().ordinal() > rhs.getTimeUnit().ordinal()) {
                newDuration = new Duration(apply(rhs.getTimeUnit().convert(lhs.getValue(), lhs.getTimeUnit()), rhs.getValue()), rhs.getTimeUnit());
            } else if (lhs.getTimeUnit().ordinal() < rhs.getTimeUnit().ordinal()) {
                newDuration = new Duration(apply(lhs.getValue(), lhs.getTimeUnit().convert(rhs.getValue(), rhs.getTimeUnit())), lhs.getTimeUnit());
            } else {
                newDuration = new Duration(apply(lhs.getValue(), rhs.getValue()), lhs.getTimeUnit());
            }
            return newDuration;
        }
    }

    private static class Divide extends BiFunction {
        private Divide() {
            super();
        }

        protected Duration handleSpecialCases(Duration lhs, Duration rhs) {
            if (lhs == Duration.FOREVER) {
                return Duration.FOREVER;
            }
            if (rhs == Duration.FOREVER) {
                throw new IllegalArgumentException("Cannot divide by infinity");
            } else if (Duration.ZERO.equals(lhs)) {
                return Duration.ZERO;
            } else {
                return null;
            }
        }

        long apply(long operand1, long operand2) {
            return operand1 / operand2;
        }
    }

    private static class Minus extends BiFunction {
        private Minus() {
            super();
        }

        protected Duration handleSpecialCases(Duration lhs, Duration rhs) {
            if (!lhs.isZero() && rhs.isZero()) {
                return lhs;
            }
            if (lhs == Duration.FOREVER) {
                return Duration.FOREVER;
            }
            if (rhs == Duration.FOREVER) {
                return Duration.ZERO;
            }
            if (Duration.FOREVER.equals(rhs)) {
                return Duration.ZERO;
            }
            return null;
        }

        long apply(long operand1, long operand2) {
            return operand1 - operand2;
        }
    }

    private static class Multiply extends BiFunction {
        private Multiply() {
            super();
        }

        protected Duration handleSpecialCases(Duration lhs, Duration rhs) {
            if (!lhs.isZero()) {
                if (!rhs.isZero()) {
                    if (lhs != Duration.FOREVER) {
                        if (rhs != Duration.FOREVER) {
                            return null;
                        }
                    }
                    return Duration.FOREVER;
                }
            }
            return Duration.ZERO;
        }

        long apply(long operand1, long operand2) {
            return operand1 * operand2;
        }
    }

    private static class Plus extends BiFunction {
        private Plus() {
            super();
        }

        protected Duration handleSpecialCases(Duration lhs, Duration rhs) {
            if (Duration.ZERO.equals(rhs)) {
                return lhs;
            }
            if (Duration.ZERO.equals(lhs)) {
                return rhs;
            }
            if (lhs != Duration.FOREVER) {
                if (rhs != Duration.FOREVER) {
                    return null;
                }
            }
            return Duration.FOREVER;
        }

        long apply(long operand1, long operand2) {
            return operand1 + operand2;
        }
    }

    private Duration() {
        this.value = -1;
        this.unit = null;
    }

    public Duration(long value, TimeUnit unit) {
        if (value < 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("value must be >= 0, was ");
            stringBuilder.append(value);
            throw new IllegalArgumentException(stringBuilder.toString());
        } else if (unit != null) {
            this.value = value;
            this.unit = unit;
        } else {
            throw new IllegalArgumentException("TimeUnit cannot be null");
        }
    }

    public TimeUnit getTimeUnit() {
        return this.unit;
    }

    public String getTimeUnitAsString() {
        TimeUnit timeUnit = this.unit;
        return timeUnit == null ? "<not defined>" : timeUnit.toString().toLowerCase();
    }

    public boolean isForever() {
        return this == FOREVER;
    }

    public boolean isZero() {
        return equals(ZERO);
    }

    public long getValue() {
        return this.value;
    }

    public long getValueInMS() {
        long j = this.value;
        if (j == -1) {
            return j;
        }
        return TimeUnit.MILLISECONDS.convert(this.value, this.unit);
    }

    public long getValueInNS() {
        long j = this.value;
        if (j == -1) {
            return j;
        }
        return TimeUnit.NANOSECONDS.convert(this.value, this.unit);
    }

    public Duration plus(long amount) {
        Plus plus = new Plus();
        TimeUnit timeUnit = this.unit;
        return plus.apply(this, timeUnit == null ? FOREVER : new Duration(amount, timeUnit));
    }

    public Duration plus(long amount, TimeUnit timeUnit) {
        if (timeUnit != null) {
            return new Plus().apply(this, new Duration(amount, timeUnit));
        }
        throw new IllegalArgumentException("Time unit cannot be null");
    }

    public Duration plus(Duration duration) {
        return new Plus().apply(this, duration);
    }

    public Duration multiply(long amount) {
        Multiply multiply = new Multiply();
        TimeUnit timeUnit = this.unit;
        return multiply.apply(this, timeUnit == null ? FOREVER : new Duration(amount, timeUnit));
    }

    public Duration divide(long amount) {
        Divide divide = new Divide();
        TimeUnit timeUnit = this.unit;
        return divide.apply(this, timeUnit == null ? FOREVER : new Duration(amount, timeUnit));
    }

    public Duration minus(long amount) {
        Minus minus = new Minus();
        TimeUnit timeUnit = this.unit;
        return minus.apply(this, timeUnit == null ? FOREVER : new Duration(amount, timeUnit));
    }

    public Duration minus(long amount, TimeUnit timeUnit) {
        if (timeUnit != null) {
            return new Minus().apply(this, new Duration(amount, timeUnit));
        }
        throw new IllegalArgumentException("Time unit cannot be null");
    }

    public Duration minus(Duration duration) {
        return new Minus().apply(this, duration);
    }

    public boolean equals(Object o) {
        boolean z = true;
        if (this == o) {
            return true;
        }
        if (o != null) {
            if (getClass() == o.getClass()) {
                if (getValueInMS() != ((Duration) o).getValueInMS()) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }

    public int hashCode() {
        long j = this.value;
        int result = ((int) (j ^ (j >>> 32))) * 31;
        TimeUnit timeUnit = this.unit;
        return result + (timeUnit != null ? timeUnit.hashCode() : 0);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Duration{unit=");
        stringBuilder.append(this.unit);
        stringBuilder.append(", value=");
        stringBuilder.append(this.value);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    public int compareTo(Duration other) {
        int i = 1;
        if (other == null) {
            return 1;
        }
        long x = getValueInMS();
        long y = other.getValueInMS();
        if (x < y) {
            i = -1;
        } else if (x == y) {
            i = 0;
        }
        return i;
    }
}
