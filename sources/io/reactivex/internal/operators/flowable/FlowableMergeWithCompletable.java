package io.reactivex.internal.operators.flowable;

import io.reactivex.CompletableObserver;
import io.reactivex.CompletableSource;
import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.AtomicThrowable;
import io.reactivex.internal.util.HalfSerializer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FlowableMergeWithCompletable<T> extends AbstractFlowableWithUpstream<T, T> {
    final CompletableSource other;

    static final class MergeWithSubscriber<T> extends AtomicInteger implements FlowableSubscriber<T>, Subscription {
        private static final long serialVersionUID = -4592979584110982903L;
        final Subscriber<? super T> downstream;
        final AtomicThrowable error = new AtomicThrowable();
        volatile boolean mainDone;
        final AtomicReference<Subscription> mainSubscription = new AtomicReference();
        volatile boolean otherDone;
        final OtherObserver otherObserver = new OtherObserver(this);
        final AtomicLong requested = new AtomicLong();

        static final class OtherObserver extends AtomicReference<Disposable> implements CompletableObserver {
            private static final long serialVersionUID = -2935427570954647017L;
            final MergeWithSubscriber<?> parent;

            OtherObserver(MergeWithSubscriber<?> parent) {
                this.parent = parent;
            }

            public void onSubscribe(Disposable d) {
                DisposableHelper.setOnce(this, d);
            }

            public void onError(Throwable e) {
                this.parent.otherError(e);
            }

            public void onComplete() {
                this.parent.otherComplete();
            }
        }

        MergeWithSubscriber(Subscriber<? super T> downstream) {
            this.downstream = downstream;
        }

        public void onSubscribe(Subscription s) {
            SubscriptionHelper.deferredSetOnce(this.mainSubscription, this.requested, s);
        }

        public void onNext(T t) {
            HalfSerializer.onNext(this.downstream, (Object) t, (AtomicInteger) this, this.error);
        }

        public void onError(Throwable ex) {
            SubscriptionHelper.cancel(this.mainSubscription);
            HalfSerializer.onError(this.downstream, ex, (AtomicInteger) this, this.error);
        }

        public void onComplete() {
            this.mainDone = true;
            if (this.otherDone) {
                HalfSerializer.onComplete(this.downstream, (AtomicInteger) this, this.error);
            }
        }

        public void request(long n) {
            SubscriptionHelper.deferredRequest(this.mainSubscription, this.requested, n);
        }

        public void cancel() {
            SubscriptionHelper.cancel(this.mainSubscription);
            DisposableHelper.dispose(this.otherObserver);
        }

        void otherError(Throwable ex) {
            SubscriptionHelper.cancel(this.mainSubscription);
            HalfSerializer.onError(this.downstream, ex, (AtomicInteger) this, this.error);
        }

        void otherComplete() {
            this.otherDone = true;
            if (this.mainDone) {
                HalfSerializer.onComplete(this.downstream, (AtomicInteger) this, this.error);
            }
        }
    }

    public FlowableMergeWithCompletable(Flowable<T> source, CompletableSource other) {
        super(source);
        this.other = other;
    }

    protected void subscribeActual(Subscriber<? super T> subscriber) {
        MergeWithSubscriber<T> parent = new MergeWithSubscriber(subscriber);
        subscriber.onSubscribe(parent);
        this.source.subscribe(parent);
        this.other.subscribe(parent.otherObserver);
    }
}
