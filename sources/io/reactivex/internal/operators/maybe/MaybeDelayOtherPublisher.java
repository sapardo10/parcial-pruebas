package io.reactivex.internal.operators.maybe;

import io.reactivex.FlowableSubscriber;
import io.reactivex.MaybeObserver;
import io.reactivex.MaybeSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

public final class MaybeDelayOtherPublisher<T, U> extends AbstractMaybeWithUpstream<T, T> {
    final Publisher<U> other;

    static final class DelayMaybeObserver<T, U> implements MaybeObserver<T>, Disposable {
        final OtherSubscriber<T> other;
        final Publisher<U> otherSource;
        Disposable upstream;

        DelayMaybeObserver(MaybeObserver<? super T> actual, Publisher<U> otherSource) {
            this.other = new OtherSubscriber(actual);
            this.otherSource = otherSource;
        }

        public void dispose() {
            this.upstream.dispose();
            this.upstream = DisposableHelper.DISPOSED;
            SubscriptionHelper.cancel(this.other);
        }

        public boolean isDisposed() {
            return SubscriptionHelper.isCancelled((Subscription) this.other.get());
        }

        public void onSubscribe(Disposable d) {
            if (DisposableHelper.validate(this.upstream, d)) {
                this.upstream = d;
                this.other.downstream.onSubscribe(this);
            }
        }

        public void onSuccess(T value) {
            this.upstream = DisposableHelper.DISPOSED;
            this.other.value = value;
            subscribeNext();
        }

        public void onError(Throwable e) {
            this.upstream = DisposableHelper.DISPOSED;
            this.other.error = e;
            subscribeNext();
        }

        public void onComplete() {
            this.upstream = DisposableHelper.DISPOSED;
            subscribeNext();
        }

        void subscribeNext() {
            this.otherSource.subscribe(this.other);
        }
    }

    static final class OtherSubscriber<T> extends AtomicReference<Subscription> implements FlowableSubscriber<Object> {
        private static final long serialVersionUID = -1215060610805418006L;
        final MaybeObserver<? super T> downstream;
        Throwable error;
        T value;

        OtherSubscriber(MaybeObserver<? super T> downstream) {
            this.downstream = downstream;
        }

        public void onSubscribe(Subscription s) {
            SubscriptionHelper.setOnce(this, s, Long.MAX_VALUE);
        }

        public void onNext(Object t) {
            Subscription s = (Subscription) get();
            if (s != SubscriptionHelper.CANCELLED) {
                lazySet(SubscriptionHelper.CANCELLED);
                s.cancel();
                onComplete();
            }
        }

        public void onError(Throwable t) {
            if (this.error == null) {
                this.downstream.onError(t);
                return;
            }
            this.downstream.onError(new CompositeException(e, t));
        }

        public void onComplete() {
            Throwable e = this.error;
            if (e != null) {
                this.downstream.onError(e);
                return;
            }
            T v = this.value;
            if (v != null) {
                this.downstream.onSuccess(v);
            } else {
                this.downstream.onComplete();
            }
        }
    }

    public MaybeDelayOtherPublisher(MaybeSource<T> source, Publisher<U> other) {
        super(source);
        this.other = other;
    }

    protected void subscribeActual(MaybeObserver<? super T> observer) {
        this.source.subscribe(new DelayMaybeObserver(observer, this.other));
    }
}
