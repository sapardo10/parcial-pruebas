package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FlowableSkip<T> extends AbstractFlowableWithUpstream<T, T> {
    /* renamed from: n */
    final long f62n;

    static final class SkipSubscriber<T> implements FlowableSubscriber<T>, Subscription {
        final Subscriber<? super T> downstream;
        long remaining;
        Subscription upstream;

        SkipSubscriber(Subscriber<? super T> actual, long n) {
            this.downstream = actual;
            this.remaining = n;
        }

        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.validate(this.upstream, s)) {
                long n = this.remaining;
                this.upstream = s;
                this.downstream.onSubscribe(this);
                s.request(n);
            }
        }

        public void onNext(T t) {
            long j = this.remaining;
            if (j != 0) {
                this.remaining = j - 1;
            } else {
                this.downstream.onNext(t);
            }
        }

        public void onError(Throwable t) {
            this.downstream.onError(t);
        }

        public void onComplete() {
            this.downstream.onComplete();
        }

        public void request(long n) {
            this.upstream.request(n);
        }

        public void cancel() {
            this.upstream.cancel();
        }
    }

    public FlowableSkip(Flowable<T> source, long n) {
        super(source);
        this.f62n = n;
    }

    protected void subscribeActual(Subscriber<? super T> s) {
        this.source.subscribe(new SkipSubscriber(s, this.f62n));
    }
}
