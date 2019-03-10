package org.awaitility.core;

import java.util.concurrent.TimeUnit;
import org.awaitility.Duration;
import org.hamcrest.Matcher;

class ConditionEvaluationHandler<T> {
    private final Matcher<? super T> matcher;
    private final ConditionSettings settings;
    private final StopWatch watch = new StopWatch();

    private static class StopWatch {
        private long startTime;

        private StopWatch() {
        }

        public void start() {
            this.startTime = System.nanoTime();
        }

        long getElapsedTimeInMS() {
            return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - this.startTime);
        }
    }

    ConditionEvaluationHandler(Matcher<? super T> matcher, ConditionSettings settings) {
        this.matcher = matcher;
        this.settings = settings;
    }

    void handleConditionResultMismatch(String mismatchMessage, T currentConditionValue, Duration pollInterval) {
        ConditionEvaluationListener<T> listener = this.settings.getConditionEvaluationListener();
        if (listener != null) {
            long elapsedTimeInMS = r1.watch.getElapsedTimeInMS();
            try {
                listener.conditionEvaluated(new EvaluatedCondition(mismatchMessage, r1.matcher, currentConditionValue, elapsedTimeInMS, getRemainingTimeInMS(elapsedTimeInMS, r1.settings.getMaxWaitTime()), false, r1.settings.getAlias(), pollInterval));
            } catch (ClassCastException e) {
                m15xa0fe456(e, listener);
            }
        }
    }

    void handleConditionResultMatch(String matchMessage, T currentConditionValue, Duration pollInterval) {
        ConditionEvaluationListener<T> listener = this.settings.getConditionEvaluationListener();
        if (listener != null) {
            long elapsedTimeInMS = r1.watch.getElapsedTimeInMS();
            try {
                listener.conditionEvaluated(new EvaluatedCondition(matchMessage, r1.matcher, currentConditionValue, elapsedTimeInMS, getRemainingTimeInMS(elapsedTimeInMS, r1.settings.getMaxWaitTime()), true, r1.settings.getAlias(), pollInterval));
            } catch (ClassCastException e) {
                m15xa0fe456(e, listener);
            }
        }
    }

    private long getRemainingTimeInMS(long elapsedTimeInMS, Duration maxWaitTime) {
        if (maxWaitTime.equals(Duration.FOREVER)) {
            return Long.MAX_VALUE;
        }
        return maxWaitTime.getValueInMS() - elapsedTimeInMS;
    }

    /* renamed from: throwClassCastExceptionBecauseConditionEvaluationListenerCouldntBeApplied */
    private void m15xa0fe456(ClassCastException e, ConditionEvaluationListener listener) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Cannot apply condition evaluation listener ");
        stringBuilder.append(listener.getClass().getName());
        stringBuilder.append(" because ");
        stringBuilder.append(e.getMessage());
        throw new ClassCastException(stringBuilder.toString());
    }

    public void start() {
        this.watch.start();
    }
}
