package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.internal.subscriptions.SubscriptionArbiter;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FlowableSwitchIfEmpty<T> extends AbstractFlowableWithUpstream<T, T> {
    final Publisher<? extends T> other;

    static final class SwitchIfEmptySubscriber<T> implements FlowableSubscriber<T> {
        final SubscriptionArbiter arbiter = new SubscriptionArbiter();
        final Subscriber<? super T> downstream;
        boolean empty = true;
        final Publisher<? extends T> other;

        SwitchIfEmptySubscriber(Subscriber<? super T> actual, Publisher<? extends T> other) {
            this.downstream = actual;
            this.other = other;
        }

        public void onSubscribe(Subscription s) {
            this.arbiter.setSubscription(s);
        }

        public void onNext(T t) {
            if (this.empty) {
                this.empty = false;
            }
            this.downstream.onNext(t);
        }

        public void onError(Throwable t) {
            this.downstream.onError(t);
        }

        public void onComplete() {
            if (this.empty) {
                this.empty = false;
                this.other.subscribe(this);
                return;
            }
            this.downstream.onComplete();
        }
    }

    public FlowableSwitchIfEmpty(Flowable<T> source, Publisher<? extends T> other) {
        super(source);
        this.other = other;
    }

    protected void subscribeActual(Subscriber<? super T> s) {
        SwitchIfEmptySubscriber<T> parent = new SwitchIfEmptySubscriber(s, this.other);
        s.onSubscribe(parent.arbiter);
        this.source.subscribe(parent);
    }
}
