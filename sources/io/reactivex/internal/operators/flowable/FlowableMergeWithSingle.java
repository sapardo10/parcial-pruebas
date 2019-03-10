package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.fuseable.SimplePlainQueue;
import io.reactivex.internal.queue.SpscArrayQueue;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.AtomicThrowable;
import io.reactivex.internal.util.BackpressureHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FlowableMergeWithSingle<T> extends AbstractFlowableWithUpstream<T, T> {
    final SingleSource<? extends T> other;

    static final class MergeWithObserver<T> extends AtomicInteger implements FlowableSubscriber<T>, Subscription {
        static final int OTHER_STATE_CONSUMED_OR_EMPTY = 2;
        static final int OTHER_STATE_HAS_VALUE = 1;
        private static final long serialVersionUID = -4592979584110982903L;
        volatile boolean cancelled;
        int consumed;
        final Subscriber<? super T> downstream;
        long emitted;
        final AtomicThrowable error = new AtomicThrowable();
        final int limit;
        volatile boolean mainDone;
        final AtomicReference<Subscription> mainSubscription = new AtomicReference();
        final OtherObserver<T> otherObserver = new OtherObserver(this);
        volatile int otherState;
        final int prefetch = Flowable.bufferSize();
        volatile SimplePlainQueue<T> queue;
        final AtomicLong requested = new AtomicLong();
        T singleItem;

        static final class OtherObserver<T> extends AtomicReference<Disposable> implements SingleObserver<T> {
            private static final long serialVersionUID = -2935427570954647017L;
            final MergeWithObserver<T> parent;

            OtherObserver(MergeWithObserver<T> parent) {
                this.parent = parent;
            }

            public void onSubscribe(Disposable d) {
                DisposableHelper.setOnce(this, d);
            }

            public void onSuccess(T t) {
                this.parent.otherSuccess(t);
            }

            public void onError(Throwable e) {
                this.parent.otherError(e);
            }
        }

        MergeWithObserver(Subscriber<? super T> downstream) {
            this.downstream = downstream;
            int i = this.prefetch;
            this.limit = i - (i >> 2);
        }

        public void onSubscribe(Subscription s) {
            SubscriptionHelper.setOnce(this.mainSubscription, s, (long) this.prefetch);
        }

        public void onNext(T t) {
            if (compareAndSet(0, 1)) {
                long e = this.emitted;
                if (this.requested.get() != e) {
                    SimplePlainQueue<T> q = this.queue;
                    if (q != null) {
                        if (!q.isEmpty()) {
                            q.offer(t);
                        }
                    }
                    this.emitted = 1 + e;
                    this.downstream.onNext(t);
                    int c = this.consumed + 1;
                    if (c == this.limit) {
                        this.consumed = 0;
                        ((Subscription) this.mainSubscription.get()).request((long) c);
                    } else {
                        this.consumed = c;
                    }
                } else {
                    getOrCreateQueue().offer(t);
                }
                if (decrementAndGet() == 0) {
                    return;
                }
            } else {
                getOrCreateQueue().offer(t);
                if (getAndIncrement() != 0) {
                    return;
                }
            }
            drainLoop();
        }

        public void onError(Throwable ex) {
            if (this.error.addThrowable(ex)) {
                SubscriptionHelper.cancel(this.mainSubscription);
                drain();
                return;
            }
            RxJavaPlugins.onError(ex);
        }

        public void onComplete() {
            this.mainDone = true;
            drain();
        }

        public void request(long n) {
            BackpressureHelper.add(this.requested, n);
            drain();
        }

        public void cancel() {
            this.cancelled = true;
            SubscriptionHelper.cancel(this.mainSubscription);
            DisposableHelper.dispose(this.otherObserver);
            if (getAndIncrement() == 0) {
                this.queue = null;
                this.singleItem = null;
            }
        }

        void otherSuccess(T value) {
            if (compareAndSet(0, 1)) {
                long e = this.emitted;
                if (this.requested.get() != e) {
                    this.emitted = 1 + e;
                    this.downstream.onNext(value);
                    this.otherState = 2;
                } else {
                    this.singleItem = value;
                    this.otherState = 1;
                    if (decrementAndGet() == 0) {
                        return;
                    }
                }
            } else {
                this.singleItem = value;
                this.otherState = 1;
                if (getAndIncrement() != 0) {
                    return;
                }
            }
            drainLoop();
        }

        void otherError(Throwable ex) {
            if (this.error.addThrowable(ex)) {
                SubscriptionHelper.cancel(this.mainSubscription);
                drain();
                return;
            }
            RxJavaPlugins.onError(ex);
        }

        SimplePlainQueue<T> getOrCreateQueue() {
            SimplePlainQueue<T> q = this.queue;
            if (q != null) {
                return q;
            }
            SpscArrayQueue q2 = new SpscArrayQueue(Flowable.bufferSize());
            this.queue = q2;
            return q2;
        }

        void drain() {
            if (getAndIncrement() == 0) {
                drainLoop();
            }
        }

        void drainLoop() {
            Subscriber<? super T> actual = this.downstream;
            int missed = 1;
            long e = this.emitted;
            int c = this.consumed;
            int lim = this.limit;
            while (true) {
                long r = r0.requested.get();
                while (e != r) {
                    boolean d;
                    if (r0.cancelled) {
                        r0.singleItem = null;
                        r0.queue = null;
                        return;
                    } else if (r0.error.get() != null) {
                        r0.singleItem = null;
                        r0.queue = null;
                        actual.onError(r0.error.terminate());
                        return;
                    } else {
                        int os = r0.otherState;
                        if (os == 1) {
                            T v = r0.singleItem;
                            r0.singleItem = null;
                            r0.otherState = 2;
                            actual.onNext(v);
                            e++;
                        } else {
                            d = r0.mainDone;
                            SimplePlainQueue<T> q = r0.queue;
                            T v2 = q != null ? q.poll() : null;
                            boolean empty = v2 == null;
                            if (d && empty && os == 2) {
                                r0.queue = null;
                                actual.onComplete();
                                return;
                            } else if (empty) {
                                break;
                            } else {
                                long e2;
                                actual.onNext(v2);
                                e++;
                                c++;
                                if (c == lim) {
                                    c = 0;
                                    e2 = e;
                                    ((Subscription) r0.mainSubscription.get()).request((long) lim);
                                } else {
                                    e2 = e;
                                }
                                e = e2;
                            }
                        }
                    }
                }
                if (e == r) {
                    if (r0.cancelled) {
                        r0.singleItem = null;
                        r0.queue = null;
                        return;
                    } else if (r0.error.get() != null) {
                        r0.singleItem = null;
                        r0.queue = null;
                        actual.onError(r0.error.terminate());
                        return;
                    } else {
                        boolean z;
                        boolean empty2;
                        d = r0.mainDone;
                        q = r0.queue;
                        if (q != null) {
                            if (!q.isEmpty()) {
                                z = false;
                                empty2 = z;
                                if (!d && empty2 && r0.otherState == 2) {
                                    r0.queue = null;
                                    actual.onComplete();
                                    return;
                                }
                            }
                        }
                        z = true;
                        empty2 = z;
                        if (!d) {
                        }
                    }
                }
                r0.emitted = e;
                r0.consumed = c;
                missed = addAndGet(-missed);
                if (missed == 0) {
                    return;
                }
            }
        }
    }

    public FlowableMergeWithSingle(Flowable<T> source, SingleSource<? extends T> other) {
        super(source);
        this.other = other;
    }

    protected void subscribeActual(Subscriber<? super T> subscriber) {
        MergeWithObserver<T> parent = new MergeWithObserver(subscriber);
        subscriber.onSubscribe(parent);
        this.source.subscribe(parent);
        this.other.subscribe(parent.otherObserver);
    }
}
