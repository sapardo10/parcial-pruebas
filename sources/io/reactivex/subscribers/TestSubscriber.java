package io.reactivex.subscribers;

import io.reactivex.FlowableSubscriber;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.fuseable.QueueSubscription;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.ExceptionHelper;
import io.reactivex.observers.BaseTestConsumer;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class TestSubscriber<T> extends BaseTestConsumer<T, TestSubscriber<T>> implements FlowableSubscriber<T>, Subscription, Disposable {
    private volatile boolean cancelled;
    private final Subscriber<? super T> downstream;
    private final AtomicLong missedRequested;
    private QueueSubscription<T> qs;
    private final AtomicReference<Subscription> upstream;

    enum EmptySubscriber implements FlowableSubscriber<Object> {
        INSTANCE;

        public void onSubscribe(Subscription s) {
        }

        public void onNext(Object t) {
        }

        public void onError(Throwable t) {
        }

        public void onComplete() {
        }
    }

    public static <T> TestSubscriber<T> create() {
        return new TestSubscriber();
    }

    public static <T> TestSubscriber<T> create(long initialRequested) {
        return new TestSubscriber(initialRequested);
    }

    public static <T> TestSubscriber<T> create(Subscriber<? super T> delegate) {
        return new TestSubscriber((Subscriber) delegate);
    }

    public TestSubscriber() {
        this(EmptySubscriber.INSTANCE, Long.MAX_VALUE);
    }

    public TestSubscriber(long initialRequest) {
        this(EmptySubscriber.INSTANCE, initialRequest);
    }

    public TestSubscriber(Subscriber<? super T> downstream) {
        this(downstream, Long.MAX_VALUE);
    }

    public TestSubscriber(Subscriber<? super T> actual, long initialRequest) {
        if (initialRequest >= 0) {
            this.downstream = actual;
            this.upstream = new AtomicReference();
            this.missedRequested = new AtomicLong(initialRequest);
            return;
        }
        throw new IllegalArgumentException("Negative initial request not allowed");
    }

    public void onSubscribe(Subscription s) {
        this.lastThread = Thread.currentThread();
        if (s == null) {
            this.errors.add(new NullPointerException("onSubscribe received a null Subscription"));
        } else if (this.upstream.compareAndSet(null, s)) {
            if (this.initialFusionMode != 0) {
                if (s instanceof QueueSubscription) {
                    this.qs = (QueueSubscription) s;
                    int m = this.qs.requestFusion(this.initialFusionMode);
                    this.establishedFusionMode = m;
                    if (m == 1) {
                        this.checkSubscriptionOnce = true;
                        this.lastThread = Thread.currentThread();
                        while (true) {
                            try {
                                T poll = this.qs.poll();
                                T t = poll;
                                if (poll == null) {
                                    break;
                                }
                                this.values.add(t);
                            } catch (Throwable ex) {
                                this.errors.add(ex);
                            }
                        }
                        this.completions++;
                        return;
                    }
                }
            }
            this.downstream.onSubscribe(s);
            long mr = this.missedRequested.getAndSet(0);
            if (mr != 0) {
                s.request(mr);
            }
            onStart();
        } else {
            s.cancel();
            if (this.upstream.get() != SubscriptionHelper.CANCELLED) {
                List list = this.errors;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("onSubscribe received multiple subscriptions: ");
                stringBuilder.append(s);
                list.add(new IllegalStateException(stringBuilder.toString()));
            }
        }
    }

    protected void onStart() {
    }

    public void onNext(T t) {
        if (!this.checkSubscriptionOnce) {
            this.checkSubscriptionOnce = true;
            if (this.upstream.get() == null) {
                this.errors.add(new IllegalStateException("onSubscribe not called in proper order"));
            }
        }
        this.lastThread = Thread.currentThread();
        if (this.establishedFusionMode == 2) {
            while (true) {
                try {
                    T poll = this.qs.poll();
                    t = poll;
                    if (poll == null) {
                        break;
                    }
                    this.values.add(t);
                } catch (Throwable ex) {
                    this.errors.add(ex);
                    this.qs.cancel();
                }
            }
            return;
        }
        this.values.add(t);
        if (t == null) {
            this.errors.add(new NullPointerException("onNext received a null value"));
        }
        this.downstream.onNext(t);
    }

    public void onError(Throwable t) {
        if (!this.checkSubscriptionOnce) {
            this.checkSubscriptionOnce = true;
            if (this.upstream.get() == null) {
                this.errors.add(new NullPointerException("onSubscribe not called in proper order"));
            }
        }
        try {
            this.lastThread = Thread.currentThread();
            this.errors.add(t);
            if (t == null) {
                this.errors.add(new IllegalStateException("onError received a null Throwable"));
            }
            this.downstream.onError(t);
        } finally {
            this.done.countDown();
        }
    }

    public void onComplete() {
        if (!this.checkSubscriptionOnce) {
            this.checkSubscriptionOnce = true;
            if (this.upstream.get() == null) {
                this.errors.add(new IllegalStateException("onSubscribe not called in proper order"));
            }
        }
        try {
            this.lastThread = Thread.currentThread();
            this.completions++;
            this.downstream.onComplete();
        } finally {
            this.done.countDown();
        }
    }

    public final void request(long n) {
        SubscriptionHelper.deferredRequest(this.upstream, this.missedRequested, n);
    }

    public final void cancel() {
        if (!this.cancelled) {
            this.cancelled = true;
            SubscriptionHelper.cancel(this.upstream);
        }
    }

    public final boolean isCancelled() {
        return this.cancelled;
    }

    public final void dispose() {
        cancel();
    }

    public final boolean isDisposed() {
        return this.cancelled;
    }

    public final boolean hasSubscription() {
        return this.upstream.get() != null;
    }

    public final TestSubscriber<T> assertSubscribed() {
        if (this.upstream.get() != null) {
            return this;
        }
        throw fail("Not subscribed!");
    }

    public final TestSubscriber<T> assertNotSubscribed() {
        if (this.upstream.get() != null) {
            throw fail("Subscribed!");
        } else if (this.errors.isEmpty()) {
            return this;
        } else {
            throw fail("Not subscribed but errors found");
        }
    }

    final TestSubscriber<T> setInitialFusionMode(int mode) {
        this.initialFusionMode = mode;
        return this;
    }

    final TestSubscriber<T> assertFusionMode(int mode) {
        int m = this.establishedFusionMode;
        if (m == mode) {
            return this;
        }
        if (this.qs != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Fusion mode different. Expected: ");
            stringBuilder.append(fusionModeToString(mode));
            stringBuilder.append(", actual: ");
            stringBuilder.append(fusionModeToString(m));
            throw new AssertionError(stringBuilder.toString());
        }
        throw fail("Upstream is not fuseable");
    }

    static String fusionModeToString(int mode) {
        switch (mode) {
            case 0:
                return "NONE";
            case 1:
                return "SYNC";
            case 2:
                return "ASYNC";
            default:
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unknown(");
                stringBuilder.append(mode);
                stringBuilder.append(")");
                return stringBuilder.toString();
        }
    }

    final TestSubscriber<T> assertFuseable() {
        if (this.qs != null) {
            return this;
        }
        throw new AssertionError("Upstream is not fuseable.");
    }

    final TestSubscriber<T> assertNotFuseable() {
        if (this.qs == null) {
            return this;
        }
        throw new AssertionError("Upstream is fuseable.");
    }

    public final TestSubscriber<T> assertOf(Consumer<? super TestSubscriber<T>> check) {
        try {
            check.accept(this);
            return this;
        } catch (Throwable ex) {
            RuntimeException wrapOrThrow = ExceptionHelper.wrapOrThrow(ex);
        }
    }

    public final TestSubscriber<T> requestMore(long n) {
        request(n);
        return this;
    }
}
