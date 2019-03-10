package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.Scheduler;
import io.reactivex.Scheduler.Worker;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.BackpressureHelper;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FlowableSubscribeOn<T> extends AbstractFlowableWithUpstream<T, T> {
    final boolean nonScheduledRequests;
    final Scheduler scheduler;

    static final class SubscribeOnSubscriber<T> extends AtomicReference<Thread> implements FlowableSubscriber<T>, Subscription, Runnable {
        private static final long serialVersionUID = 8094547886072529208L;
        final Subscriber<? super T> downstream;
        final boolean nonScheduledRequests;
        final AtomicLong requested = new AtomicLong();
        Publisher<T> source;
        final AtomicReference<Subscription> upstream = new AtomicReference();
        final Worker worker;

        static final class Request implements Runnable {
            /* renamed from: n */
            final long f25n;
            final Subscription upstream;

            Request(Subscription s, long n) {
                this.upstream = s;
                this.f25n = n;
            }

            public void run() {
                this.upstream.request(this.f25n);
            }
        }

        SubscribeOnSubscriber(Subscriber<? super T> actual, Worker worker, Publisher<T> source, boolean requestOn) {
            this.downstream = actual;
            this.worker = worker;
            this.source = source;
            this.nonScheduledRequests = requestOn ^ 1;
        }

        public void run() {
            lazySet(Thread.currentThread());
            Publisher<T> src = this.source;
            this.source = null;
            src.subscribe(this);
        }

        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.setOnce(this.upstream, s)) {
                long r = this.requested.getAndSet(0);
                if (r != 0) {
                    requestUpstream(r, s);
                }
            }
        }

        public void onNext(T t) {
            this.downstream.onNext(t);
        }

        public void onError(Throwable t) {
            this.downstream.onError(t);
            this.worker.dispose();
        }

        public void onComplete() {
            this.downstream.onComplete();
            this.worker.dispose();
        }

        public void request(long n) {
            if (SubscriptionHelper.validate(n)) {
                Subscription s = (Subscription) this.upstream.get();
                if (s != null) {
                    requestUpstream(n, s);
                    return;
                }
                BackpressureHelper.add(this.requested, n);
                s = (Subscription) this.upstream.get();
                if (s != null) {
                    long r = this.requested.getAndSet(0);
                    if (r != 0) {
                        requestUpstream(r, s);
                    }
                }
            }
        }

        void requestUpstream(long n, Subscription s) {
            if (!this.nonScheduledRequests) {
                if (Thread.currentThread() != get()) {
                    this.worker.schedule(new Request(s, n));
                    return;
                }
            }
            s.request(n);
        }

        public void cancel() {
            SubscriptionHelper.cancel(this.upstream);
            this.worker.dispose();
        }
    }

    public FlowableSubscribeOn(Flowable<T> source, Scheduler scheduler, boolean nonScheduledRequests) {
        super(source);
        this.scheduler = scheduler;
        this.nonScheduledRequests = nonScheduledRequests;
    }

    public void subscribeActual(Subscriber<? super T> s) {
        Worker w = this.scheduler.createWorker();
        SubscribeOnSubscriber<T> sos = new SubscribeOnSubscriber(s, w, this.source, this.nonScheduledRequests);
        s.onSubscribe(sos);
        w.schedule(sos);
    }
}
