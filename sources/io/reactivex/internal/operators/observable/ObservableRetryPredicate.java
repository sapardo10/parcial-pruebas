package io.reactivex.internal.operators.observable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Predicate;
import io.reactivex.internal.disposables.SequentialDisposable;
import java.util.concurrent.atomic.AtomicInteger;

public final class ObservableRetryPredicate<T> extends AbstractObservableWithUpstream<T, T> {
    final long count;
    final Predicate<? super Throwable> predicate;

    static final class RepeatObserver<T> extends AtomicInteger implements Observer<T> {
        private static final long serialVersionUID = -7098360935104053232L;
        final Observer<? super T> downstream;
        final Predicate<? super Throwable> predicate;
        long remaining;
        final ObservableSource<? extends T> source;
        final SequentialDisposable upstream;

        RepeatObserver(Observer<? super T> actual, long count, Predicate<? super Throwable> predicate, SequentialDisposable sa, ObservableSource<? extends T> source) {
            this.downstream = actual;
            this.upstream = sa;
            this.source = source;
            this.predicate = predicate;
            this.remaining = count;
        }

        public void onSubscribe(Disposable d) {
            this.upstream.update(d);
        }

        public void onNext(T t) {
            this.downstream.onNext(t);
        }

        public void onError(Throwable t) {
            long r = this.remaining;
            if (r != Long.MAX_VALUE) {
                this.remaining = r - 1;
            }
            if (r == 0) {
                this.downstream.onError(t);
            } else {
                try {
                    if (this.predicate.test(t)) {
                        subscribeNext();
                    } else {
                        this.downstream.onError(t);
                    }
                } catch (Throwable e) {
                    Exceptions.throwIfFatal(e);
                    this.downstream.onError(new CompositeException(t, e));
                }
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

    public ObservableRetryPredicate(Observable<T> source, long count, Predicate<? super Throwable> predicate) {
        super(source);
        this.predicate = predicate;
        this.count = count;
    }

    public void subscribeActual(Observer<? super T> observer) {
        SequentialDisposable sa = new SequentialDisposable();
        observer.onSubscribe(sa);
        new RepeatObserver(observer, this.count, this.predicate, sa, this.source).subscribeNext();
    }
}
