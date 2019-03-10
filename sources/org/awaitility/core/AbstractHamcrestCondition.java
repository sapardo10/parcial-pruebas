package org.awaitility.core;

import java.util.concurrent.Callable;
import org.awaitility.Duration;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

public abstract class AbstractHamcrestCondition<T> implements Condition<T> {
    private ConditionAwaiter conditionAwaiter;
    private final ConditionEvaluationHandler<T> conditionEvaluationHandler;
    private volatile T lastResult;

    protected abstract String getCallableDescription(Callable<T> callable);

    protected AbstractHamcrestCondition(final Callable<T> supplier, final Matcher<? super T> matcher, ConditionSettings settings) {
        if (supplier == null) {
            throw new IllegalArgumentException("You must specify a supplier (was null).");
        } else if (matcher != null) {
            this.conditionEvaluationHandler = new ConditionEvaluationHandler(matcher, settings);
            final Callable<T> callable = supplier;
            final Matcher<? super T> matcher2 = matcher;
            this.conditionAwaiter = new ConditionAwaiter(new ConditionEvaluator() {
                public ConditionEvaluationResult eval(Duration pollInterval) throws Exception {
                    AbstractHamcrestCondition.this.lastResult = supplier.call();
                    boolean matches = matcher.matches(AbstractHamcrestCondition.this.lastResult);
                    if (matches) {
                        AbstractHamcrestCondition.this.conditionEvaluationHandler.handleConditionResultMatch(AbstractHamcrestCondition.this.getMatchMessage(supplier, matcher), AbstractHamcrestCondition.this.lastResult, pollInterval);
                    } else {
                        AbstractHamcrestCondition.this.conditionEvaluationHandler.handleConditionResultMismatch(AbstractHamcrestCondition.this.getMismatchMessage(supplier, matcher), AbstractHamcrestCondition.this.lastResult, pollInterval);
                    }
                    return new ConditionEvaluationResult(matches);
                }
            }, settings) {
                protected String getTimeoutMessage() {
                    return AbstractHamcrestCondition.this.getMismatchMessage(callable, matcher2);
                }
            };
        } else {
            throw new IllegalArgumentException("You must specify a matcher (was null).");
        }
    }

    private String getMatchMessage(Callable<T> supplier, Matcher<? super T> matcher) {
        return String.format("%s reached its end value of %s", new Object[]{getCallableDescription(supplier), HamcrestToStringFilter.filter(matcher)});
    }

    private String getMismatchMessage(Callable<T> supplier, Matcher<? super T> matcher) {
        Description mismatchDescription = new StringDescription();
        matcher.describeMismatch(this.lastResult, mismatchDescription);
        if (mismatchDescription.toString() != null && mismatchDescription.toString().isEmpty()) {
            mismatchDescription.appendText("was ").appendValue(this.lastResult);
        }
        return String.format("%s expected %s but %s", new Object[]{getCallableDescription(supplier), HamcrestToStringFilter.filter(matcher), mismatchDescription});
    }

    public T await() {
        this.conditionAwaiter.await(this.conditionEvaluationHandler);
        return this.lastResult;
    }
}
