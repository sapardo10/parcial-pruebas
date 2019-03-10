package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FlowableFromObservable<T> extends Flowable<T> {
    private final Observable<T> upstream;

    static final class SubscriberObserver<T> implements Observer<T>, Subscription {
        final Subscriber<? super T> downstream;
        Disposable upstream;

        SubscriberObserver(Subscriber<? super T> s) {
            this.downstream = s;
        }

        public void onComplete() {
            this.downstream.onComplete();
        }

        public void onError(Throwable e) {
            this.downstream.onError(e);
        }

        public void onNext(T value) {
            this.downstream.onNext(value);
        }

        public void onSubscribe(Disposable d) {
            this.upstream = d;
            this.downstream.onSubscribe(this);
        }

        public void cancel() {
            this.upstream.dispose();
        }

        public void request(long n) {
        }
    }

    public FlowableFromObservable(Observable<T> upstream) {
        this.upstream = upstream;
    }

    protected void subscribeActual(Subscriber<? super T> s) {
        this.upstream.subscribe(new SubscriberObserver(s));
    }
}
