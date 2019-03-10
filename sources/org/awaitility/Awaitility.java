package org.awaitility;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.awaitility.constraint.AtMostWaitConstraint;
import org.awaitility.constraint.WaitConstraint;
import org.awaitility.core.ConditionEvaluationListener;
import org.awaitility.core.ConditionFactory;
import org.awaitility.core.ExceptionIgnorer;
import org.awaitility.core.ExecutorLifecycle;
import org.awaitility.core.FieldSupplierBuilder;
import org.awaitility.core.Function;
import org.awaitility.core.HamcrestExceptionIgnorer;
import org.awaitility.core.InternalExecutorServiceFactory;
import org.awaitility.core.Predicate;
import org.awaitility.core.PredicateExceptionIgnorer;
import org.awaitility.core.Supplier;
import org.awaitility.pollinterval.FixedPollInterval;
import org.awaitility.pollinterval.PollInterval;
import org.hamcrest.Matcher;

public class Awaitility {
    private static final Duration DEFAULT_POLL_DELAY = null;
    private static final PollInterval DEFAULT_POLL_INTERVAL = new FixedPollInterval(Duration.ONE_HUNDRED_MILLISECONDS);
    private static volatile boolean defaultCatchUncaughtExceptions = true;
    private static volatile ConditionEvaluationListener defaultConditionEvaluationListener = null;
    private static volatile ExceptionIgnorer defaultExceptionIgnorer = new PredicateExceptionIgnorer(new C12201());
    private static volatile ExecutorLifecycle defaultExecutorLifecycle = null;
    private static volatile Duration defaultPollDelay = DEFAULT_POLL_DELAY;
    private static volatile PollInterval defaultPollInterval = DEFAULT_POLL_INTERVAL;
    private static volatile WaitConstraint defaultWaitConstraint = AtMostWaitConstraint.TEN_SECONDS;

    /* renamed from: org.awaitility.Awaitility$1 */
    static class C12201 implements Predicate<Throwable> {
        C12201() {
        }

        public boolean matches(Throwable e) {
            return false;
        }
    }

    /* renamed from: org.awaitility.Awaitility$2 */
    static class C12212 implements Predicate<Throwable> {
        C12212() {
        }

        public boolean matches(Throwable e) {
            return true;
        }
    }

    /* renamed from: org.awaitility.Awaitility$4 */
    static class C12234 implements Supplier<ExecutorService> {
        C12234() {
        }

        public ExecutorService get() {
            return InternalExecutorServiceFactory.sameThreadExecutorService();
        }
    }

    /* renamed from: org.awaitility.Awaitility$6 */
    static class C12256 implements Predicate<Throwable> {
        C12256() {
        }

        public boolean matches(Throwable e) {
            return false;
        }
    }

    public static void catchUncaughtExceptionsByDefault() {
        defaultCatchUncaughtExceptions = true;
    }

    public static void doNotCatchUncaughtExceptionsByDefault() {
        defaultCatchUncaughtExceptions = false;
    }

    public static void ignoreExceptionsByDefault() {
        defaultExceptionIgnorer = new PredicateExceptionIgnorer(new C12212());
    }

    public static void ignoreExceptionByDefault(final Class<? extends Throwable> exceptionType) {
        defaultExceptionIgnorer = new PredicateExceptionIgnorer(new Predicate<Throwable>() {
            public boolean matches(Throwable e) {
                return e.getClass().equals(exceptionType);
            }
        });
    }

    public static void ignoreExceptionsByDefaultMatching(Predicate<? super Throwable> predicate) {
        defaultExceptionIgnorer = new PredicateExceptionIgnorer(predicate);
    }

    public static void ignoreExceptionsByDefaultMatching(Matcher<? super Throwable> matcher) {
        defaultExceptionIgnorer = new HamcrestExceptionIgnorer(matcher);
    }

    public static void pollInSameThread() {
        defaultExecutorLifecycle = ExecutorLifecycle.withNormalCleanupBehavior(new C12234());
    }

    public static void pollExecutorService(ExecutorService executorService) {
        defaultExecutorLifecycle = ExecutorLifecycle.withoutCleanup(executorService);
    }

    public static void pollThread(final Function<Runnable, Thread> threadSupplier) {
        defaultExecutorLifecycle = ExecutorLifecycle.withNormalCleanupBehavior(new Supplier<ExecutorService>() {
            public ExecutorService get() {
                return InternalExecutorServiceFactory.create(threadSupplier);
            }
        });
    }

    public static void reset() {
        defaultPollInterval = DEFAULT_POLL_INTERVAL;
        defaultPollDelay = DEFAULT_POLL_DELAY;
        defaultWaitConstraint = AtMostWaitConstraint.TEN_SECONDS;
        defaultCatchUncaughtExceptions = true;
        defaultConditionEvaluationListener = null;
        defaultExecutorLifecycle = null;
        defaultExceptionIgnorer = new PredicateExceptionIgnorer(new C12256());
        Thread.setDefaultUncaughtExceptionHandler(null);
    }

    public static ConditionFactory await() {
        return await(null);
    }

    public static ConditionFactory await(String alias) {
        return new ConditionFactory(alias, defaultWaitConstraint, defaultPollInterval, defaultPollDelay, defaultCatchUncaughtExceptions, defaultExceptionIgnorer, defaultConditionEvaluationListener, defaultExecutorLifecycle);
    }

    public static ConditionFactory catchUncaughtExceptions() {
        return new ConditionFactory(null, defaultWaitConstraint, defaultPollInterval, defaultPollDelay, defaultCatchUncaughtExceptions, defaultExceptionIgnorer, defaultConditionEvaluationListener, defaultExecutorLifecycle);
    }

    public static ConditionFactory dontCatchUncaughtExceptions() {
        return new ConditionFactory(null, defaultWaitConstraint, defaultPollInterval, defaultPollDelay, false, defaultExceptionIgnorer, defaultConditionEvaluationListener, defaultExecutorLifecycle);
    }

    public static ConditionFactory with() {
        return new ConditionFactory(null, defaultWaitConstraint, defaultPollInterval, defaultPollDelay, defaultCatchUncaughtExceptions, defaultExceptionIgnorer, defaultConditionEvaluationListener, defaultExecutorLifecycle);
    }

    public static ConditionFactory given() {
        return new ConditionFactory(null, defaultWaitConstraint, defaultPollInterval, defaultPollDelay, defaultCatchUncaughtExceptions, defaultExceptionIgnorer, defaultConditionEvaluationListener, defaultExecutorLifecycle);
    }

    public static ConditionFactory waitAtMost(Duration timeout) {
        return new ConditionFactory(null, defaultWaitConstraint.withMaxWaitTime(timeout), defaultPollInterval, defaultPollDelay, defaultCatchUncaughtExceptions, defaultExceptionIgnorer, defaultConditionEvaluationListener, defaultExecutorLifecycle);
    }

    public static ConditionFactory waitAtMost(long value, TimeUnit unit) {
        return new ConditionFactory(null, defaultWaitConstraint.withMaxWaitTime(new Duration(value, unit)), defaultPollInterval, defaultPollDelay, defaultCatchUncaughtExceptions, defaultExceptionIgnorer, defaultConditionEvaluationListener, defaultExecutorLifecycle);
    }

    public static void setDefaultPollInterval(long pollInterval, TimeUnit unit) {
        defaultPollInterval = new FixedPollInterval(new Duration(pollInterval, unit));
    }

    public static void setDefaultPollDelay(long pollDelay, TimeUnit unit) {
        defaultPollDelay = new Duration(pollDelay, unit);
    }

    public static void setDefaultTimeout(long timeout, TimeUnit unit) {
        defaultWaitConstraint = defaultWaitConstraint.withMaxWaitTime(new Duration(timeout, unit));
    }

    public static void setDefaultPollInterval(Duration pollInterval) {
        if (pollInterval != null) {
            defaultPollInterval = new FixedPollInterval(pollInterval);
            return;
        }
        throw new IllegalArgumentException("You must specify a poll interval (was null).");
    }

    public static void setDefaultPollInterval(PollInterval pollInterval) {
        if (pollInterval != null) {
            defaultPollInterval = pollInterval;
            return;
        }
        throw new IllegalArgumentException("You must specify a poll interval (was null).");
    }

    public static void setDefaultPollDelay(Duration pollDelay) {
        if (pollDelay != null) {
            defaultPollDelay = pollDelay;
            return;
        }
        throw new IllegalArgumentException("You must specify a poll delay (was null).");
    }

    public static void setDefaultTimeout(Duration defaultTimeout) {
        if (defaultTimeout != null) {
            defaultWaitConstraint = defaultWaitConstraint.withMaxWaitTime(defaultTimeout);
            return;
        }
        throw new IllegalArgumentException("You must specify a default timeout (was null).");
    }

    public static void setDefaultConditionEvaluationListener(ConditionEvaluationListener defaultConditionEvaluationListener) {
        defaultConditionEvaluationListener = defaultConditionEvaluationListener;
    }

    public static FieldSupplierBuilder fieldIn(Object object) {
        return new FieldSupplierBuilder(object);
    }

    public static FieldSupplierBuilder fieldIn(Class<?> clazz) {
        return new FieldSupplierBuilder(clazz);
    }
}
