package okio;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.TimeUnit;

public class Timeout {
    public static final Timeout NONE = new Timeout$1();
    private long deadlineNanoTime;
    private boolean hasDeadline;
    private long timeoutNanos;

    public Timeout timeout(long timeout, TimeUnit unit) {
        if (timeout < 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("timeout < 0: ");
            stringBuilder.append(timeout);
            throw new IllegalArgumentException(stringBuilder.toString());
        } else if (unit != null) {
            this.timeoutNanos = unit.toNanos(timeout);
            return this;
        } else {
            throw new IllegalArgumentException("unit == null");
        }
    }

    public long timeoutNanos() {
        return this.timeoutNanos;
    }

    public boolean hasDeadline() {
        return this.hasDeadline;
    }

    public long deadlineNanoTime() {
        if (this.hasDeadline) {
            return this.deadlineNanoTime;
        }
        throw new IllegalStateException("No deadline");
    }

    public Timeout deadlineNanoTime(long deadlineNanoTime) {
        this.hasDeadline = true;
        this.deadlineNanoTime = deadlineNanoTime;
        return this;
    }

    public final Timeout deadline(long duration, TimeUnit unit) {
        if (duration <= 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("duration <= 0: ");
            stringBuilder.append(duration);
            throw new IllegalArgumentException(stringBuilder.toString());
        } else if (unit != null) {
            return deadlineNanoTime(System.nanoTime() + unit.toNanos(duration));
        } else {
            throw new IllegalArgumentException("unit == null");
        }
    }

    public Timeout clearTimeout() {
        this.timeoutNanos = 0;
        return this;
    }

    public Timeout clearDeadline() {
        this.hasDeadline = false;
        return this;
    }

    public void throwIfReached() throws IOException {
        if (Thread.interrupted()) {
            Thread.currentThread().interrupt();
            throw new InterruptedIOException("interrupted");
        }
        if (this.hasDeadline) {
            if (this.deadlineNanoTime - System.nanoTime() <= 0) {
                throw new InterruptedIOException("deadline reached");
            }
        }
    }

    public final void waitUntilNotified(Object monitor) throws InterruptedIOException {
        try {
            boolean hasDeadline = hasDeadline();
            long timeoutNanos = timeoutNanos();
            if (hasDeadline || timeoutNanos != 0) {
                long waitNanos;
                long start = System.nanoTime();
                if (hasDeadline && timeoutNanos != 0) {
                    waitNanos = Math.min(timeoutNanos, deadlineNanoTime() - start);
                } else if (hasDeadline) {
                    waitNanos = deadlineNanoTime() - start;
                } else {
                    waitNanos = timeoutNanos;
                }
                long elapsedNanos = 0;
                if (waitNanos > 0) {
                    long waitMillis = waitNanos / 1000000;
                    Long.signum(waitMillis);
                    monitor.wait(waitMillis, (int) (waitNanos - (1000000 * waitMillis)));
                    elapsedNanos = System.nanoTime() - start;
                }
                if (elapsedNanos >= waitNanos) {
                    throw new InterruptedIOException("timeout");
                }
                return;
            }
            monitor.wait();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InterruptedIOException("interrupted");
        }
    }
}
