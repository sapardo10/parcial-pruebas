package io.reactivex.internal.operators.observable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.BiPredicate;
import io.reactivex.internal.disposables.SequentialDisposable;
import java.util.concurrent.atomic.AtomicInteger;

public final class ObservableRetryBiPredicate<T> extends AbstractObservableWithUpstream<T, T> {
    final BiPredicate<? super Integer, ? super Throwable> predicate;

    static final class RetryBiObserver<T> extends AtomicInteger implements Observer<T> {
        private static final long serialVersionUID = -7098360935104053232L;
        final Observer<? super T> downstream;
        final BiPredicate<? super Integer, ? super Throwable> predicate;
        int retries;
        final ObservableSource<? extends T> source;
        final SequentialDisposable upstream;

        RetryBiObserver(Observer<? super T> actual, BiPredicate<? super Integer, ? super Throwable> predicate, SequentialDisposable sa, ObservableSource<? extends T> source) {
            this.downstream = actual;
            this.upstream = sa;
            this.source = source;
            this.predicate = predicate;
        }

        public void onSubscribe(Disposable d) {
            this.upstream.update(d);
        }

        public void onNext(T t) {
            this.downstream.onNext(t);
        }

        public void onError(Throwable t) {
            int i = 1;
            try {
                BiPredicate biPredicate = this.predicate;
                int i2 = this.retries + 1;
                this.retries = i2;
                i = biPredicate.test(Integer.valueOf(i2), t);
                if (i == 0) {
                    this.downstream.onError(t);
                } else {
                    subscribeNext();
                }
            } catch (Throwable e) {
                Exceptions.throwIfFatal(e);
                this.downstream.onError(new CompositeException(t, e));
            }
        }

        public void onComplete() {
            this.downstream.onComplete();
        }

        void subscribeNext() {
            if (getAndIncrement() == 0) {
                int missed = 1;
                while (!this.upstream.isDisposed()) {
                    this.source.subscribe(this);
                    missed = addAndGet(-missed);
                    if (missed == 0) {
                    }
                }
            }
        }
    }

    public ObservableRetryBiPredicate(Observable<T> source, BiPredicate<? super Integer, ? super Throwable> predicate) {
        super(source);
        this.predicate = predicate;
    }

    public void subscribeActual(Observer<? super T> observer) {
        SequentialDisposable sa = new SequentialDisposable();
        observer.onSubscribe(sa);
        new RetryBiObserver(observer, this.predicate, sa, this.source).subscribeNext();
    }
}
