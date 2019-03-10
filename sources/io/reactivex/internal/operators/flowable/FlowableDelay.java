package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.Scheduler;
import io.reactivex.Scheduler.Worker;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.subscribers.SerializedSubscriber;
import java.util.concurrent.TimeUnit;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FlowableDelay<T> extends AbstractFlowableWithUpstream<T, T> {
    final long delay;
    final boolean delayError;
    final Scheduler scheduler;
    final TimeUnit unit;

    static final class DelaySubscriber<T> implements FlowableSubscriber<T>, Subscription {
        final long delay;
        final boolean delayError;
        final Subscriber<? super T> downstream;
        final TimeUnit unit;
        Subscription upstream;
        /* renamed from: w */
        final Worker f55w;

        final class OnComplete implements Runnable {
            OnComplete() {
            }

            public void run() {
                try {
                    DelaySubscriber.this.downstream.onComplete();
                } finally {
                    DelaySubscriber.this.f55w.dispose();
                }
            }
        }

        final class OnError implements Runnable {
            /* renamed from: t */
            private final Throwable f23t;

            OnError(Throwable t) {
                this.f23t = t;
            }

            public void run() {
                try {
                    DelaySubscriber.this.downstream.onError(this.f23t);
                } finally {
                    DelaySubscriber.this.f55w.dispose();
                }
            }
        }

        final class OnNext implements Runnable {
            /* renamed from: t */
            private final T f24t;

            OnNext(T t) {
                this.f24t = t;
            }

            public void run() {
                DelaySubscriber.this.downstream.onNext(this.f24t);
            }
        }

        DelaySubscriber(Subscriber<? super T> actual, long delay, TimeUnit unit, Worker w, boolean delayError) {
            this.downstream = actual;
            this.delay = delay;
            this.unit = unit;
            this.f55w = w;
            this.delayError = delayError;
        }

        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.validate(this.upstream, s)) {
                this.upstream = s;
                this.downstream.onSubscribe(this);
            }
        }

        public void onNext(T t) {
            this.f55w.schedule(new OnNext(t), this.delay, this.unit);
        }

        public void onError(Throwable t) {
            this.f55w.schedule(new OnError(t), this.delayError ? this.delay : 0, this.unit);
        }

        public void onComplete() {
            this.f55w.schedule(new OnComplete(), this.delay, this.unit);
        }

        public void request(long n) {
            this.upstream.request(n);
        }

        public void cancel() {
            this.upstream.cancel();
            this.f55w.dispose();
        }
    }

    public FlowableDelay(Flowable<T> source, long delay, TimeUnit unit, Scheduler scheduler, boolean delayError) {
        super(source);
        this.delay = delay;
        this.unit = unit;
        this.scheduler = scheduler;
        this.delayError = delayError;
    }

    protected void subscribeActual(Subscriber<? super T> t) {
        Subscriber<? super T> downstream;
        if (this.delayError) {
            downstream = t;
        } else {
            downstream = new SerializedSubscriber(t);
        }
        this.source.subscribe(new DelaySubscriber(downstream, this.delay, this.unit, this.scheduler.createWorker(), this.delayError));
    }
}
