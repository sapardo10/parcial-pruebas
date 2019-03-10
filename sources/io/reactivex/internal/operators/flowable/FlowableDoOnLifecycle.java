package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.LongConsumer;
import io.reactivex.internal.subscriptions.EmptySubscription;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.plugins.RxJavaPlugins;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FlowableDoOnLifecycle<T> extends AbstractFlowableWithUpstream<T, T> {
    private final Action onCancel;
    private final LongConsumer onRequest;
    private final Consumer<? super Subscription> onSubscribe;

    static final class SubscriptionLambdaSubscriber<T> implements FlowableSubscriber<T>, Subscription {
        final Subscriber<? super T> downstream;
        final Action onCancel;
        final LongConsumer onRequest;
        final Consumer<? super Subscription> onSubscribe;
        Subscription upstream;

        SubscriptionLambdaSubscriber(Subscriber<? super T> actual, Consumer<? super Subscription> onSubscribe, LongConsumer onRequest, Action onCancel) {
            this.downstream = actual;
            this.onSubscribe = onSubscribe;
            this.onCancel = onCancel;
            this.onRequest = onRequest;
        }

        public void onSubscribe(Subscription s) {
            try {
                this.onSubscribe.accept(s);
                if (SubscriptionHelper.validate(this.upstream, s)) {
                    this.upstream = s;
                    this.downstream.onSubscribe(this);
                }
            } catch (Throwable e) {
                Exceptions.throwIfFatal(e);
                s.cancel();
                this.upstream = SubscriptionHelper.CANCELLED;
                EmptySubscription.error(e, this.downstream);
            }
        }

        public void onNext(T t) {
            this.downstream.onNext(t);
        }

        public void onError(Throwable t) {
            if (this.upstream != SubscriptionHelper.CANCELLED) {
                this.downstream.onError(t);
            } else {
                RxJavaPlugins.onError(t);
            }
        }

        public void onComplete() {
            if (this.upstream != SubscriptionHelper.CANCELLED) {
                this.downstream.onComplete();
            }
        }

        public void request(long n) {
            try {
                this.onRequest.accept(n);
            } catch (Throwable e) {
                Exceptions.throwIfFatal(e);
                RxJavaPlugins.onError(e);
            }
            this.upstream.request(n);
        }

        public void cancel() {
            try {
                this.onCancel.run();
            } catch (Throwable e) {
                Exceptions.throwIfFatal(e);
                RxJavaPlugins.onError(e);
            }
            this.upstream.cancel();
        }
    }

    public FlowableDoOnLifecycle(Flowable<T> source, Consumer<? super Subscription> onSubscribe, LongConsumer onRequest, Action onCancel) {
        super(source);
        this.onSubscribe = onSubscribe;
        this.onRequest = onRequest;
        this.onCancel = onCancel;
    }

    protected void subscribeActual(Subscriber<? super T> s) {
        this.source.subscribe(new SubscriptionLambdaSubscriber(s, this.onSubscribe, this.onRequest, this.onCancel));
    }
}
