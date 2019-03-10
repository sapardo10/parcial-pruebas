package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.BackpressureHelper;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FlowableOnBackpressureLatest<T> extends AbstractFlowableWithUpstream<T, T> {

    static final class BackpressureLatestSubscriber<T> extends AtomicInteger implements FlowableSubscriber<T>, Subscription {
        private static final long serialVersionUID = 163080509307634843L;
        volatile boolean cancelled;
        final AtomicReference<T> current = new AtomicReference();
        volatile boolean done;
        final Subscriber<? super T> downstream;
        Throwable error;
        final AtomicLong requested = new AtomicLong();
        Subscription upstream;

        BackpressureLatestSubscriber(Subscriber<? super T> downstream) {
            this.downstream = downstream;
        }

        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.validate(this.upstream, s)) {
                this.upstream = s;
                this.downstream.onSubscribe(this);
                s.request(Long.MAX_VALUE);
            }
        }

        public void onNext(T t) {
            this.current.lazySet(t);
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
                drain();
            }
        }

        public void cancel() {
            if (!this.cancelled) {
                this.cancelled = true;
                this.upstream.cancel();
                if (getAndIncrement() == 0) {
                    this.current.lazySet(null);
                }
            }
        }

        void drain() {
            if (getAndIncrement() == 0) {
                Subscriber<? super T> a = this.downstream;
                int missed = 1;
                AtomicLong r = this.requested;
                AtomicReference<T> q = this.current;
                while (true) {
                    boolean z;
                    boolean d;
                    long e = 0;
                    while (true) {
                        z = true;
                        if (e == r.get()) {
                            break;
                        }
                        d = this.done;
                        T v = q.getAndSet(null);
                        boolean empty = v == null;
                        if (!checkTerminated(d, empty, a, q)) {
                            if (empty) {
                                break;
                            }
                            a.onNext(v);
                            e++;
                        } else {
                            return;
                        }
                        if (e == r.get()) {
                            d = this.done;
                            if (q.get() == null) {
                                z = false;
                            }
                            if (checkTerminated(d, z, a, q)) {
                                return;
                            }
                        }
                        if (e != 0) {
                            BackpressureHelper.produced(r, e);
                        }
                        missed = addAndGet(-missed);
                        if (missed == 0) {
                            return;
                        }
                    }
                    if (e == r.get()) {
                        d = this.done;
                        if (q.get() == null) {
                            z = false;
                        }
                        if (checkTerminated(d, z, a, q)) {
                            return;
                        }
                    }
                    if (e != 0) {
                        BackpressureHelper.produced(r, e);
                    }
                    missed = addAndGet(-missed);
                    if (missed == 0) {
                        return;
                    }
                }
            }
        }

        boolean checkTerminated(boolean d, boolean empty, Subscriber<?> a, AtomicReference<T> q) {
            if (this.cancelled) {
                q.lazySet(null);
                return true;
            }
            if (d) {
                Throwable e = this.error;
                if (e != null) {
                    q.lazySet(null);
                    a.onError(e);
                    return true;
                } else if (empty) {
                    a.onComplete();
                    return true;
                }
            }
            return false;
        }
    }

    public FlowableOnBackpressureLatest(Flowable<T> source) {
        super(source);
    }

    protected void subscribeActual(Subscriber<? super T> s) {
        this.source.subscribe(new BackpressureLatestSubscriber(s));
    }
}
