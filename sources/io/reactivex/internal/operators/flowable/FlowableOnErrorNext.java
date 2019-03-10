package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Function;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.subscriptions.SubscriptionArbiter;
import io.reactivex.plugins.RxJavaPlugins;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FlowableOnErrorNext<T> extends AbstractFlowableWithUpstream<T, T> {
    final boolean allowFatal;
    final Function<? super Throwable, ? extends Publisher<? extends T>> nextSupplier;

    static final class OnErrorNextSubscriber<T> extends SubscriptionArbiter implements FlowableSubscriber<T> {
        private static final long serialVersionUID = 4063763155303814625L;
        final boolean allowFatal;
        boolean done;
        final Subscriber<? super T> downstream;
        final Function<? super Throwable, ? extends Publisher<? extends T>> nextSupplier;
        boolean once;
        long produced;

        OnErrorNextSubscriber(Subscriber<? super T> actual, Function<? super Throwable, ? extends Publisher<? extends T>> nextSupplier, boolean allowFatal) {
            this.downstream = actual;
            this.nextSupplier = nextSupplier;
            this.allowFatal = allowFatal;
        }

        public void onSubscribe(Subscription s) {
            setSubscription(s);
        }

        public void onNext(T t) {
            if (!this.done) {
                if (!this.once) {
                    this.produced++;
                }
                this.downstream.onNext(t);
            }
        }

        public void onError(Throwable t) {
            if (!this.once) {
                this.once = true;
                if (!this.allowFatal || (t instanceof Exception)) {
                    try {
                        Publisher<? extends T> p = (Publisher) ObjectHelper.requireNonNull(this.nextSupplier.apply(t), "The nextSupplier returned a null Publisher");
                        long mainProduced = this.produced;
                        if (mainProduced != 0) {
                            produced(mainProduced);
                        }
                        p.subscribe(this);
                        return;
                    } catch (Throwable e) {
                        Exceptions.throwIfFatal(e);
                        this.downstream.onError(new CompositeException(t, e));
                        return;
                    }
                }
                this.downstream.onError(t);
            } else if (this.done) {
                RxJavaPlugins.onError(t);
            } else {
                this.downstream.onError(t);
            }
        }

        public void onComplete() {
            if (!this.done) {
                this.done = true;
                this.once = true;
                this.downstream.onComplete();
            }
        }
    }

    public FlowableOnErrorNext(Flowable<T> source, Function<? super Throwable, ? extends Publisher<? extends T>> nextSupplier, boolean allowFatal) {
        super(source);
        this.nextSupplier = nextSupplier;
        this.allowFatal = allowFatal;
    }

    protected void subscribeActual(Subscriber<? super T> s) {
        OnErrorNextSubscriber<T> parent = new OnErrorNextSubscriber(s, this.nextSupplier, this.allowFatal);
        s.onSubscribe(parent);
        this.source.subscribe(parent);
    }
}
