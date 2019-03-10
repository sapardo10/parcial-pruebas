package org.awaitility.core;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.awaitility.Duration;
import org.awaitility.classpath.ClassPathResolver;
import org.awaitility.constraint.AtMostWaitConstraint;
import org.awaitility.constraint.WaitConstraint;
import org.awaitility.pollinterval.FixedPollInterval;
import org.awaitility.pollinterval.PollInterval;
import org.awaitility.spi.ProxyConditionFactory;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

public class ConditionFactory {
    private final String alias;
    private final boolean catchUncaughtExceptions;
    private final ConditionEvaluationListener conditionEvaluationListener;
    private final ExceptionIgnorer exceptionsIgnorer;
    private final ExecutorLifecycle executorLifecycle;
    private final Duration pollDelay;
    private final PollInterval pollInterval;
    private final WaitConstraint timeoutConstraint;

    /* renamed from: org.awaitility.core.ConditionFactory$3 */
    class C12343 implements Predicate<Throwable> {
        C12343() {
        }

        public boolean matches(Throwable e) {
            return true;
        }
    }

    /* renamed from: org.awaitility.core.ConditionFactory$4 */
    class C12354 implements Predicate<Throwable> {
        C12354() {
        }

        public boolean matches(Throwable e) {
            return false;
        }
    }

    /* renamed from: org.awaitility.core.ConditionFactory$6 */
    class C12376 implements Supplier<ExecutorService> {
        C12376() {
        }

        public ExecutorService get() {
            return InternalExecutorServiceFactory.sameThreadExecutorService();
        }
    }

    public ConditionFactory(String alias, WaitConstraint timeoutConstraint, PollInterval pollInterval, Duration pollDelay, boolean catchUncaughtExceptions, ExceptionIgnorer exceptionsIgnorer, ConditionEvaluationListener conditionEvaluationListener, ExecutorLifecycle executorLifecycle) {
        if (pollInterval == null) {
            throw new IllegalArgumentException("pollInterval cannot be null");
        } else if (timeoutConstraint != null) {
            this.alias = alias;
            this.timeoutConstraint = timeoutConstraint;
            this.pollInterval = pollInterval;
            this.catchUncaughtExceptions = catchUncaughtExceptions;
            this.pollDelay = pollDelay;
            this.conditionEvaluationListener = conditionEvaluationListener;
            this.exceptionsIgnorer = exceptionsIgnorer;
            this.executorLifecycle = executorLifecycle;
        } else {
            throw new IllegalArgumentException("timeout cannot be null");
        }
    }

    public ConditionFactory conditionEvaluationListener(ConditionEvaluationListener conditionEvaluationListener) {
        return new ConditionFactory(this.alias, this.timeoutConstraint, this.pollInterval, this.pollDelay, this.catchUncaughtExceptions, this.exceptionsIgnorer, conditionEvaluationListener, this.executorLifecycle);
    }

    public ConditionFactory timeout(Duration timeout) {
        return atMost(timeout);
    }

    public ConditionFactory atMost(Duration timeout) {
        return new ConditionFactory(this.alias, this.timeoutConstraint.withMaxWaitTime(timeout), this.pollInterval, this.pollDelay, this.catchUncaughtExceptions, this.exceptionsIgnorer, this.conditionEvaluationListener, this.executorLifecycle);
    }

    public ConditionFactory alias(String alias) {
        return new ConditionFactory(alias, this.timeoutConstraint, this.pollInterval, this.pollDelay, this.catchUncaughtExceptions, this.exceptionsIgnorer, this.conditionEvaluationListener, this.executorLifecycle);
    }

    public ConditionFactory atLeast(Duration timeout) {
        return new ConditionFactory(this.alias, this.timeoutConstraint.withMinWaitTime(timeout), this.pollInterval, this.pollDelay, this.catchUncaughtExceptions, this.exceptionsIgnorer, this.conditionEvaluationListener, this.executorLifecycle);
    }

    public ConditionFactory atLeast(long timeout, TimeUnit unit) {
        return atLeast(new Duration(timeout, unit));
    }

    public ConditionFactory between(Duration atLeast, Duration atMost) {
        return atLeast(atLeast).and().atMost(atMost);
    }

    public ConditionFactory between(long atLeastDuration, TimeUnit atLeastTimeUnit, long atMostDuration, TimeUnit atMostTimeUnit) {
        return between(new Duration(atLeastDuration, atLeastTimeUnit), new Duration(atMostDuration, atMostTimeUnit));
    }

    public ConditionFactory forever() {
        return new ConditionFactory(this.alias, AtMostWaitConstraint.FOREVER, this.pollInterval, this.pollDelay, this.catchUncaughtExceptions, this.exceptionsIgnorer, this.conditionEvaluationListener, this.executorLifecycle);
    }

    public ConditionFactory pollInterval(Duration pollInterval) {
        return new ConditionFactory(this.alias, this.timeoutConstraint, new FixedPollInterval(pollInterval), this.pollDelay, this.catchUncaughtExceptions, this.exceptionsIgnorer, this.conditionEvaluationListener, this.executorLifecycle);
    }

    public ConditionFactory timeout(long timeout, TimeUnit unit) {
        return atMost(timeout, unit);
    }

    public ConditionFactory pollDelay(long delay, TimeUnit unit) {
        return new ConditionFactory(this.alias, this.timeoutConstraint, this.pollInterval, new Duration(delay, unit), this.catchUncaughtExceptions, this.exceptionsIgnorer, this.conditionEvaluationListener, this.executorLifecycle);
    }

    public ConditionFactory pollDelay(Duration pollDelay) {
        if (pollDelay != null) {
            return new ConditionFactory(this.alias, this.timeoutConstraint, this.pollInterval, pollDelay, this.catchUncaughtExceptions, this.exceptionsIgnorer, this.conditionEvaluationListener, this.executorLifecycle);
        }
        throw new IllegalArgumentException("pollDelay cannot be null");
    }

    public ConditionFactory atMost(long timeout, TimeUnit unit) {
        return atMost(new Duration(timeout, unit));
    }

    public ConditionFactory pollInterval(long pollInterval, TimeUnit unit) {
        PollInterval fixedPollInterval = new FixedPollInterval(new Duration(pollInterval, unit));
        return new ConditionFactory(this.alias, this.timeoutConstraint, fixedPollInterval, definePollDelay(this.pollDelay, fixedPollInterval), this.catchUncaughtExceptions, this.exceptionsIgnorer, this.conditionEvaluationListener, this.executorLifecycle);
    }

    public ConditionFactory pollInterval(PollInterval pollInterval) {
        return new ConditionFactory(this.alias, this.timeoutConstraint, pollInterval, definePollDelay(this.pollDelay, pollInterval), this.catchUncaughtExceptions, this.exceptionsIgnorer, this.conditionEvaluationListener, this.executorLifecycle);
    }

    public ConditionFactory catchUncaughtExceptions() {
        return new ConditionFactory(this.alias, this.timeoutConstraint, this.pollInterval, this.pollDelay, true, this.exceptionsIgnorer, this.conditionEvaluationListener, this.executorLifecycle);
    }

    public ConditionFactory ignoreExceptionsInstanceOf(final Class<? extends Throwable> exceptionType) {
        if (exceptionType != null) {
            return new ConditionFactory(this.alias, this.timeoutConstraint, this.pollInterval, this.pollDelay, this.catchUncaughtExceptions, new PredicateExceptionIgnorer(new Predicate<Throwable>() {
                public boolean matches(Throwable e) {
                    return exceptionType.isAssignableFrom(e.getClass());
                }
            }), this.conditionEvaluationListener, this.executorLifecycle);
        }
        throw new IllegalArgumentException("exceptionType cannot be null");
    }

    public ConditionFactory ignoreException(final Class<? extends Throwable> exceptionType) {
        if (exceptionType != null) {
            return new ConditionFactory(this.alias, this.timeoutConstraint, this.pollInterval, this.pollDelay, this.catchUncaughtExceptions, new PredicateExceptionIgnorer(new Predicate<Throwable>() {
                public boolean matches(Throwable e) {
                    return e.getClass().equals(exceptionType);
                }
            }), this.conditionEvaluationListener, this.executorLifecycle);
        }
        throw new IllegalArgumentException("exception cannot be null");
    }

    public ConditionFactory ignoreExceptions() {
        return ignoreExceptionsMatching(new C12343());
    }

    public ConditionFactory ignoreNoExceptions() {
        return ignoreExceptionsMatching(new C12354());
    }

    public ConditionFactory ignoreExceptionsMatching(Matcher<? super Throwable> matcher) {
        return new ConditionFactory(this.alias, this.timeoutConstraint, this.pollInterval, this.pollDelay, this.catchUncaughtExceptions, new HamcrestExceptionIgnorer(matcher), this.conditionEvaluationListener, this.executorLifecycle);
    }

    public ConditionFactory ignoreExceptionsMatching(Predicate<? super Throwable> predicate) {
        return new ConditionFactory(this.alias, this.timeoutConstraint, this.pollInterval, this.pollDelay, this.catchUncaughtExceptions, new PredicateExceptionIgnorer(predicate), this.conditionEvaluationListener, this.executorLifecycle);
    }

    public ConditionFactory await() {
        return this;
    }

    public ConditionFactory await(String alias) {
        return new ConditionFactory(alias, this.timeoutConstraint, this.pollInterval, this.pollDelay, this.catchUncaughtExceptions, this.exceptionsIgnorer, this.conditionEvaluationListener, this.executorLifecycle);
    }

    public ConditionFactory and() {
        return this;
    }

    public ConditionFactory with() {
        return this;
    }

    public ConditionFactory then() {
        return this;
    }

    public ConditionFactory given() {
        return this;
    }

    public ConditionFactory dontCatchUncaughtExceptions() {
        return new ConditionFactory(this.alias, this.timeoutConstraint, this.pollInterval, this.pollDelay, false, this.exceptionsIgnorer, this.conditionEvaluationListener, this.executorLifecycle);
    }

    public ConditionFactory pollExecutorService(ExecutorService executorService) {
        if (executorService != null) {
            if (executorService instanceof ScheduledExecutorService) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Poll executor service cannot be an instance of ");
                stringBuilder.append(ScheduledExecutorService.class.getName());
                throw new IllegalArgumentException(stringBuilder.toString());
            }
        }
        return new ConditionFactory(this.alias, this.timeoutConstraint, this.pollInterval, this.pollDelay, false, this.exceptionsIgnorer, this.conditionEvaluationListener, ExecutorLifecycle.withoutCleanup(executorService));
    }

    public ConditionFactory pollThread(final Function<Runnable, Thread> threadSupplier) {
        return new ConditionFactory(this.alias, this.timeoutConstraint, this.pollInterval, this.pollDelay, false, this.exceptionsIgnorer, this.conditionEvaluationListener, ExecutorLifecycle.withNormalCleanupBehavior(new Supplier<ExecutorService>() {
            public ExecutorService get() {
                return InternalExecutorServiceFactory.create(threadSupplier);
            }
        }));
    }

    public ConditionFactory pollInSameThread() {
        return new ConditionFactory(this.alias, this.timeoutConstraint, this.pollInterval, this.pollDelay, false, this.exceptionsIgnorer, this.conditionEvaluationListener, ExecutorLifecycle.withNormalCleanupBehavior(new C12376()));
    }

    public <T> T untilCall(T proxyMethodReturnValue, Matcher<? super T> matcher) {
        if (ClassPathResolver.existInCP("java.util.ServiceLoader")) {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl == null) {
                cl = ClassLoader.getSystemClassLoader();
            }
            Iterator<ProxyConditionFactory> iterator = ServiceLoader.load(ProxyConditionFactory.class, cl).iterator();
            if (iterator.hasNext()) {
                ProxyConditionFactory<T> factory = (ProxyConditionFactory) iterator.next();
                if (factory != null) {
                    return until(factory.createProxyCondition(proxyMethodReturnValue, matcher, generateConditionSettings()));
                }
                throw new IllegalArgumentException("Internal error: Proxy condition plugin initialization returned null, please report an issue.");
            }
            throw new UnsupportedOperationException("There's currently no plugin installed that can handle proxy conditions, please consider adding 'awaitility-proxy' to the classpath. If using Maven you can do:<dependency>\n\t<groupId>org.awaitility</groupId>\n\t<artifactId>awaitility</artifactId>\n\t<version>${awaitility.version}</version>\n</dependency>\n");
        }
        throw new UnsupportedOperationException("java.util.ServiceLoader not found in classpath so cannot create condition");
    }

    public <T> T until(Callable<T> supplier, Matcher<? super T> matcher) {
        return until(new CallableHamcrestCondition(supplier, matcher, generateConditionSettings()));
    }

    public <T> T until(Callable<T> supplier, final Predicate<? super T> predicate) {
        return until((Callable) supplier, new TypeSafeMatcher<T>() {
            protected void describeMismatchSafely(T item, Description description) {
                description.appendText("it returned <false> for input of ").appendValue(item);
            }

            public void describeTo(Description description) {
                description.appendText("the predicate to return <true>");
            }

            protected boolean matchesSafely(T item) {
                return predicate.matches(item);
            }
        });
    }

    public void untilAsserted(ThrowingRunnable assertion) {
        until(new AssertionCondition(assertion, generateConditionSettings()));
    }

    public Integer untilAtomic(final AtomicInteger atomic, Matcher<? super Integer> matcher) {
        return (Integer) until(new CallableHamcrestCondition(new Callable<Integer>() {
            public Integer call() {
                return Integer.valueOf(atomic.get());
            }
        }, matcher, generateConditionSettings()));
    }

    public Long untilAtomic(final AtomicLong atomic, Matcher<? super Long> matcher) {
        return (Long) until(new CallableHamcrestCondition(new Callable<Long>() {
            public Long call() {
                return Long.valueOf(atomic.get());
            }
        }, matcher, generateConditionSettings()));
    }

    public void untilAtomic(final AtomicBoolean atomic, Matcher<? super Boolean> matcher) {
        until(new CallableHamcrestCondition(new Callable<Boolean>() {
            public Boolean call() {
                return Boolean.valueOf(atomic.get());
            }
        }, matcher, generateConditionSettings()));
    }

    public void untilTrue(AtomicBoolean atomic) {
        untilAtomic(atomic, Matchers.anyOf(Matchers.is(Boolean.TRUE), Matchers.is(Boolean.valueOf(true))));
    }

    public void untilFalse(AtomicBoolean atomic) {
        untilAtomic(atomic, Matchers.anyOf(Matchers.is(Boolean.FALSE), Matchers.is(Boolean.valueOf(false))));
    }

    public <V> V untilAtomic(final AtomicReference<V> atomic, Matcher<? super V> matcher) {
        return until(new CallableHamcrestCondition(new Callable<V>() {
            public V call() {
                return atomic.get();
            }
        }, matcher, generateConditionSettings()));
    }

    public void until(Callable<Boolean> conditionEvaluator) {
        until(new CallableCondition(conditionEvaluator, generateConditionSettings()));
    }

    private ConditionSettings generateConditionSettings() {
        Duration actualPollDelay = definePollDelay(this.pollDelay, this.pollInterval);
        if (actualPollDelay.isForever()) {
            throw new IllegalArgumentException("Cannot delay polling forever");
        }
        Duration timeout = this.timeoutConstraint.getMaxWaitTime();
        long timeoutInMS = timeout.getValueInMS();
        if (!timeout.isForever()) {
            if (timeoutInMS <= actualPollDelay.getValueInMS()) {
                throw new IllegalStateException(String.format("Timeout (%s %s) must be greater than the poll delay (%s %s).", new Object[]{Long.valueOf(timeout.getValue()), timeout.getTimeUnitAsString(), Long.valueOf(actualPollDelay.getValue()), actualPollDelay.getTimeUnitAsString()}));
            }
        }
        if (!(actualPollDelay.isForever() || timeout.isForever())) {
            if (timeoutInMS <= actualPollDelay.getValueInMS()) {
                throw new IllegalStateException(String.format("Timeout (%s %s) must be greater than the poll delay (%s %s).", new Object[]{Long.valueOf(timeout.getValue()), timeout.getTimeUnitAsString(), Long.valueOf(actualPollDelay.getValue()), actualPollDelay.getTimeUnitAsString()}));
            }
        }
        return new ConditionSettings(this.alias, this.catchUncaughtExceptions, this.timeoutConstraint, this.pollInterval, actualPollDelay, this.conditionEvaluationListener, this.exceptionsIgnorer, this.executorLifecycle == null ? ExecutorLifecycle.withNormalCleanupBehavior(new Supplier<ExecutorService>() {

            /* renamed from: org.awaitility.core.ConditionFactory$12$1 */
            class C12311 implements BiFunction<Runnable, String, Thread> {
                C12311() {
                }

                public Thread apply(Runnable r, String threadName) {
                    return new Thread(Thread.currentThread().getThreadGroup(), r, threadName);
                }
            }

            public ExecutorService get() {
                return InternalExecutorServiceFactory.create(new C12311(), ConditionFactory.this.alias);
            }
        }) : this.executorLifecycle);
    }

    private <T> T until(Condition<T> condition) {
        return condition.await();
    }

    private Duration definePollDelay(Duration pollDelay, PollInterval pollInterval) {
        if (pollDelay != null) {
            return pollDelay;
        }
        if (pollInterval == null || !(pollInterval instanceof FixedPollInterval)) {
            return Duration.ZERO;
        }
        return pollInterval.next(1, Duration.ZERO);
    }
}
