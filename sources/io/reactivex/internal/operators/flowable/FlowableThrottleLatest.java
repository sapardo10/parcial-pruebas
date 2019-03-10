package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.Scheduler;
import io.reactivex.Scheduler.Worker;
import io.reactivex.exceptions.MissingBackpressureException;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.BackpressureHelper;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FlowableThrottleLatest<T> extends AbstractFlowableWithUpstream<T, T> {
    final boolean emitLast;
    final Scheduler scheduler;
    final long timeout;
    final TimeUnit unit;

    static final class ThrottleLatestSubscriber<T> extends AtomicInteger implements FlowableSubscriber<T>, Subscription, Runnable {
        private static final long serialVersionUID = -8296689127439125014L;
        volatile boolean cancelled;
        volatile boolean done;
        final Subscriber<? super T> downstream;
        final boolean emitLast;
        long emitted;
        Throwable error;
        final AtomicReference<T> latest = new AtomicReference();
        final AtomicLong requested = new AtomicLong();
        final long timeout;
        volatile boolean timerFired;
        boolean timerRunning;
        final TimeUnit unit;
        Subscription upstream;
        final Worker worker;

        ThrottleLatestSubscriber(Subscriber<? super T> downstream, long timeout, TimeUnit unit, Worker worker, boolean emitLast) {
            this.downstream = downstream;
            this.timeout = timeout;
            this.unit = unit;
            this.worker = worker;
            this.emitLast = emitLast;
        }

        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.validate(this.upstream, s)) {
                this.upstream = s;
                this.downstream.onSubscribe(this);
                s.request(Long.MAX_VALUE);
            }
        }

        public void onNext(T t) {
            this.latest.set(t);
            drain();
        }

        public void onError(Throwable t) {
            this.error = t;
            this.done = true;
            drain();
        }

        public void onComplete() {
            this.done = true;
            drain();
        }

        public void request(long n) {
            if (SubscriptionHelper.validate(n)) {
                BackpressureHelper.add(this.requested, n);
            }
        }

        public void cancel() {
            this.cancelled = true;
            this.upstream.cancel();
            this.worker.dispose();
            if (getAndIncrement() == 0) {
                this.latest.lazySet(null);
            }
        }

        public void run() {
            this.timerFired = true;
            drain();
        }

        void drain() {
            ThrottleLatestSubscriber throttleLatestSubscriber = this;
            if (getAndIncrement() == 0) {
                int missed = 1;
                AtomicReference<T> latest = throttleLatestSubscriber.latest;
                AtomicLong requested = throttleLatestSubscriber.requested;
                Subscriber<? super T> downstream = throttleLatestSubscriber.downstream;
                while (!throttleLatestSubscriber.cancelled) {
                    boolean d = throttleLatestSubscriber.done;
                    if (!d || throttleLatestSubscriber.error == null) {
                        boolean empty = latest.get() == null;
                        if (d) {
                            if (empty || !throttleLatestSubscriber.emitLast) {
                                latest.lazySet(null);
                                downstream.onComplete();
                            } else {
                                T v = latest.getAndSet(null);
                                long e = throttleLatestSubscriber.emitted;
                                if (e != requested.get()) {
                                    throttleLatestSubscriber.emitted = 1 + e;
                                    downstream.onNext(v);
                                    downstream.onComplete();
                                } else {
                                    downstream.onError(new MissingBackpressureException("Could not emit final value due to lack of requests"));
                                }
                            }
                            throttleLatestSubscriber.worker.dispose();
                            return;
                        }
                        if (!empty) {
                            if (throttleLatestSubscriber.timerRunning) {
                                if (throttleLatestSubscriber.timerFired) {
                                }
                            }
                            T v2 = latest.getAndSet(null);
                            long e2 = throttleLatestSubscriber.emitted;
                            if (e2 != requested.get()) {
                                downstream.onNext(v2);
                                throttleLatestSubscriber.emitted = 1 + e2;
                                throttleLatestSubscriber.timerFired = false;
                                throttleLatestSubscriber.timerRunning = true;
                                throttleLatestSubscriber.worker.schedule(throttleLatestSubscriber, throttleLatestSubscriber.timeout, throttleLatestSubscriber.unit);
                            } else {
                                throttleLatestSubscriber.upstream.cancel();
                                downstream.onError(new MissingBackpressureException("Could not emit value due to lack of requests"));
                                throttleLatestSubscriber.worker.dispose();
                                return;
                            }
                        } else if (throttleLatestSubscriber.timerFired) {
                            throttleLatestSubscriber.timerRunning = false;
                            throttleLatestSubscriber.timerFired = false;
                        }
                        missed = addAndGet(-missed);
                        if (missed == 0) {
                            return;
                        }
                    } else {
                        latest.lazySet(null);
                        downstream.onError(throttleLatestSubscriber.error);
                        throttleLatestSubscriber.worker.dispose();
                        return;
                    }
                }
                latest.lazySet(null);
            }
        }
    }

    public FlowableThrottleLatest(Flowable<T> source, long timeout, TimeUnit unit, Scheduler scheduler, boolean emitLast) {
        super(source);
        this.timeout = timeout;
        this.unit = unit;
        this.scheduler = scheduler;
        this.emitLast = emitLast;
    }

    protected void subscribeActual(Subscriber<? super T> s) {
        this.source.subscribe(new ThrottleLatestSubscriber(s, this.timeout, this.unit, this.scheduler.createWorker(), this.emitLast));
    }
}
