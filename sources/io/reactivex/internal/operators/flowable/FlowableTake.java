package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.internal.subscriptions.EmptySubscription;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicBoolean;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FlowableTake<T> extends AbstractFlowableWithUpstream<T, T> {
    final long limit;

    static final class TakeSubscriber<T> extends AtomicBoolean implements FlowableSubscriber<T>, Subscription {
        private static final long serialVersionUID = -5636543848937116287L;
        boolean done;
        final Subscriber<? super T> downstream;
        final long limit;
        long remaining;
        Subscription upstream;

        TakeSubscriber(Subscriber<? super T> actual, long limit) {
            this.downstream = actual;
            this.limit = limit;
            this.remaining = limit;
        }

        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.validate(this.upstream, s)) {
                this.upstream = s;
                if (this.limit == 0) {
                    s.cancel();
                    this.done = true;
                    EmptySubscription.complete(this.downstream);
                    return;
                }
                this.downstream.onSubscribe(this);
            }
        }

        public void onNext(T t) {
            if (!this.done) {
                long j = this.remaining;
                this.remaining = j - 1;
                if (j > 0) {
                    boolean stop = this.remaining == 0;
                    this.downstream.onNext(t);
                    if (stop) {
                        this.upstream.cancel();
                        onComplete();
                    }
                }
            }
        }

        public void onError(Throwable t) {
            if (this.done) {
                RxJavaPlugins.onError(t);
                return;
            }
            this.done = true;
            this.upstream.cancel();
            this.downstream.onError(t);
        }

        public void onComplete() {
            if (!this.done) {
                this.done = true;
                this.downstream.onComplete();
            }
        }

        public void request(long n) {
            if (SubscriptionHelper.validate(n)) {
                if (!get() && compareAndSet(false, true)) {
                    if (n >= this.limit) {
                        this.upstream.request(Long.MAX_VALUE);
                        return;
                    }
                }
                this.upstream.request(n);
            }
        }

        public void cancel() {
            this.upstream.cancel();
        }
    }

    public FlowableTake(Flowable<T> source, long limit) {
        super(source);
        this.limit = limit;
    }

    protected void subscribeActual(Subscriber<? super T> s) {
        this.source.subscribe(new TakeSubscriber(s, this.limit));
    }
}
