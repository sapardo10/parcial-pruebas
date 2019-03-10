package org.awaitility.core;

class ConditionEvaluationResult {
    private final boolean successful;
    private final Throwable throwable;
    private final Throwable trace;

    ConditionEvaluationResult(boolean successful) {
        this(successful, null, null);
    }

    ConditionEvaluationResult(boolean successful, Throwable throwable, Throwable trace) {
        this.successful = successful;
        this.throwable = throwable;
        this.trace = trace;
    }

    boolean isSuccessful() {
        return this.successful;
    }

    boolean isError() {
        return isSuccessful() ^ 1;
    }

    Throwable getThrowable() {
        return this.throwable;
    }

    Throwable getTrace() {
        return this.trace;
    }

    boolean hasThrowable() {
        return this.throwable != null;
    }

    boolean hasTrace() {
        return this.trace != null;
    }

    public boolean equals(Object o) {
        boolean z = true;
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConditionEvaluationResult)) {
            return false;
        }
        ConditionEvaluationResult result = (ConditionEvaluationResult) o;
        if (this.successful != result.successful) {
            return false;
        }
        Throwable th = this.throwable;
        if (th != null) {
            z = th.equals(result.throwable);
        } else if (result.throwable != null) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        Throwable th = this.throwable;
        return ((th != null ? th.hashCode() : 0) * 31) + this.successful;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ConditionEvaluationResult{trace=");
        stringBuilder.append(this.trace);
        stringBuilder.append(", throwable=");
        stringBuilder.append(this.throwable);
        stringBuilder.append(", successful=");
        stringBuilder.append(this.successful);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
