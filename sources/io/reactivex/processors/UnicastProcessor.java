package io.reactivex.processors;

import io.reactivex.annotations.CheckReturnValue;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.queue.SpscLinkedArrayQueue;
import io.reactivex.internal.subscriptions.BasicIntQueueSubscription;
import io.reactivex.internal.subscriptions.EmptySubscription;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.BackpressureHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class UnicastProcessor<T> extends FlowableProcessor<T> {
    volatile boolean cancelled;
    final boolean delayError;
    volatile boolean done;
    final AtomicReference<Subscriber<? super T>> downstream;
    boolean enableOperatorFusion;
    Throwable error;
    final AtomicReference<Runnable> onTerminate;
    final AtomicBoolean once;
    final SpscLinkedArrayQueue<T> queue;
    final AtomicLong requested;
    final BasicIntQueueSubscription<T> wip;

    final class UnicastQueueSubscription extends BasicIntQueueSubscription<T> {
        private static final long serialVersionUID = -4896760517184205454L;

        UnicastQueueSubscription() {
        }

        @Nullable
        public T poll() {
            return UnicastProcessor.this.queue.poll();
        }

        public boolean isEmpty() {
            return UnicastProcessor.this.queue.isEmpty();
        }

        public void clear() {
            UnicastProcessor.this.queue.clear();
        }

        public int requestFusion(int requestedMode) {
            if ((requestedMode & 2) == 0) {
                return 0;
            }
            UnicastProcessor.this.enableOperatorFusion = true;
            return 2;
        }

        public void request(long n) {
            if (SubscriptionHelper.validate(n)) {
                BackpressureHelper.add(UnicastProcessor.this.requested, n);
                UnicastProcessor.this.drain();
            }
        }

        public void cancel() {
            if (!UnicastProcessor.this.cancelled) {
                UnicastProcessor unicastProcessor = UnicastProcessor.this;
                unicastProcessor.cancelled = true;
                unicastProcessor.doTerminate();
                if (!UnicastProcessor.this.enableOperatorFusion) {
                    if (UnicastProcessor.this.wip.getAndIncrement() == 0) {
                        UnicastProcessor.this.queue.clear();
                        UnicastProcessor.this.downstream.lazySet(null);
                    }
                }
            }
        }
    }

    @CheckReturnValue
    @NonNull
    public static <T> UnicastProcessor<T> create() {
        return new UnicastProcessor(bufferSize());
    }

    @CheckReturnValue
    @NonNull
    public static <T> UnicastProcessor<T> create(int capacityHint) {
        return new UnicastProcessor(capacityHint);
    }

    @CheckReturnValue
    @NonNull
    public static <T> UnicastProcessor<T> create(boolean delayError) {
        return new UnicastProcessor(bufferSize(), null, delayError);
    }

    @CheckReturnValue
    @NonNull
    public static <T> UnicastProcessor<T> create(int capacityHint, Runnable onCancelled) {
        ObjectHelper.requireNonNull((Object) onCancelled, "onTerminate");
        return new UnicastProcessor(capacityHint, onCancelled);
    }

    @CheckReturnValue
    @NonNull
    public static <T> UnicastProcessor<T> create(int capacityHint, Runnable onCancelled, boolean delayError) {
        ObjectHelper.requireNonNull((Object) onCancelled, "onTerminate");
        return new UnicastProcessor(capacityHint, onCancelled, delayError);
    }

    UnicastProcessor(int capacityHint) {
        this(capacityHint, null, true);
    }

    UnicastProcessor(int capacityHint, Runnable onTerminate) {
        this(capacityHint, onTerminate, true);
    }

    UnicastProcessor(int capacityHint, Runnable onTerminate, boolean delayError) {
        this.queue = new SpscLinkedArrayQueue(ObjectHelper.verifyPositive(capacityHint, "capacityHint"));
        this.onTerminate = new AtomicReference(onTerminate);
        this.delayError = delayError;
        this.downstream = new AtomicReference();
        this.once = new AtomicBoolean();
        this.wip = new UnicastQueueSubscription();
        this.requested = new AtomicLong();
    }

    void doTerminate() {
        Runnable r = (Runnable) this.onTerminate.getAndSet(null);
        if (r != null) {
            r.run();
        }
    }

    void drainRegular(Subscriber<? super T> a) {
        SpscLinkedArrayQueue<T> q = this.queue;
        boolean failFast = this.delayError ^ true;
        int missed = 1;
        while (true) {
            Subscriber<? super T> subscriber;
            long r = r8.requested.get();
            long e = 0;
            while (r != e) {
                boolean d = r8.done;
                T t = q.poll();
                boolean empty = t == null;
                if (!checkTerminated(failFast, d, empty, a, q)) {
                    if (empty) {
                        subscriber = a;
                        break;
                    } else {
                        a.onNext(t);
                        e++;
                    }
                } else {
                    return;
                }
            }
            subscriber = a;
            if (r == e) {
                if (checkTerminated(failFast, r8.done, q.isEmpty(), a, q)) {
                    return;
                }
            }
            if (e != 0 && r != Long.MAX_VALUE) {
                r8.requested.addAndGet(-e);
            }
            missed = r8.wip.addAndGet(-missed);
            if (missed == 0) {
                return;
            }
        }
    }

    void drainFused(Subscriber<? super T> a) {
        int missed = 1;
        SpscLinkedArrayQueue<T> q = this.queue;
        boolean failFast = this.delayError ^ 1;
        while (!this.cancelled) {
            boolean d = this.done;
            if (failFast && d && this.error != null) {
                q.clear();
                this.downstream.lazySet(null);
                a.onError(this.error);
                return;
            }
            a.onNext(null);
            if (d) {
                this.downstream.lazySet(null);
                Throwable ex = this.error;
                if (ex != null) {
                    a.onError(ex);
                } else {
                    a.onComplete();
                }
                return;
            }
            missed = this.wip.addAndGet(-missed);
            if (missed == 0) {
                return;
            }
        }
        q.clear();
        this.downstream.lazySet(null);
    }

    void drain() {
        if (this.wip.getAndIncrement() == 0) {
            int missed = 1;
            Subscriber<? super T> a = (Subscriber) this.downstream.get();
            while (a == null) {
                missed = this.wip.addAndGet(-missed);
                if (missed != 0) {
                    a = (Subscriber) this.downstream.get();
                } else {
                    return;
                }
            }
            if (this.enableOperatorFusion) {
                drainFused(a);
            } else {
                drainRegular(a);
            }
        }
    }

    boolean checkTerminated(boolean failFast, boolean d, boolean empty, Subscriber<? super T> a, SpscLinkedArrayQueue<T> q) {
        if (this.cancelled) {
            q.clear();
            this.downstream.lazySet(null);
            return true;
        }
        if (d) {
            if (failFast && this.error != null) {
                q.clear();
                this.downstream.lazySet(null);
                a.onError(this.error);
                return true;
            } else if (empty) {
                Throwable e = this.error;
                this.downstream.lazySet(null);
                if (e != null) {
                    a.onError(e);
                } else {
                    a.onComplete();
                }
                return true;
            }
        }
        return false;
    }

    public void onSubscribe(Subscription s) {
        if (!this.done) {
            if (!this.cancelled) {
                s.request(Long.MAX_VALUE);
                return;
            }
        }
        s.cancel();
    }

    public void onNext(T t) {
        ObjectHelper.requireNonNull((Object) t, "onNext called with null. Null values are generally not allowed in 2.x operators and sources.");
        if (!this.done) {
            if (!this.cancelled) {
                this.queue.offer(t);
                drain();
            }
        }
    }

    public void onError(Throwable t) {
        ObjectHelper.requireNonNull((Object) t, "onError called with null. Null values are generally not allowed in 2.x operators and sources.");
        if (!this.done) {
            if (!this.cancelled) {
                this.error = t;
                this.done = true;
                doTerminate();
                drain();
                return;
            }
        }
        RxJavaPlugins.onError(t);
    }

    public void onComplete() {
        if (!this.done) {
            if (!this.cancelled) {
                this.done = true;
                doTerminate();
                drain();
            }
        }
    }

    protected void subscribeActual(Subscriber<? super T> s) {
        if (this.once.get() || !this.once.compareAndSet(false, true)) {
            EmptySubscription.error(new IllegalStateException("This processor allows only a single Subscriber"), s);
            return;
        }
        s.onSubscribe(this.wip);
        this.downstream.set(s);
        if (this.cancelled) {
            this.downstream.lazySet(null);
        } else {
            drain();
        }
    }

    public boolean hasSubscribers() {
        return this.downstream.get() != null;
    }

    @Nullable
    public Throwable getThrowable() {
        if (this.done) {
            return this.error;
        }
        return null;
    }

    public boolean hasComplete() {
        return this.done && this.error == null;
    }

    public boolean hasThrowable() {
        return this.done && this.error != null;
    }
}
