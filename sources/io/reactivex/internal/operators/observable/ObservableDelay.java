package io.reactivex.internal.operators.observable;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.Scheduler.Worker;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.observers.SerializedObserver;
import java.util.concurrent.TimeUnit;

public final class ObservableDelay<T> extends AbstractObservableWithUpstream<T, T> {
    final long delay;
    final boolean delayError;
    final Scheduler scheduler;
    final TimeUnit unit;

    static final class DelayObserver<T> implements Observer<T>, Disposable {
        final long delay;
        final boolean delayError;
        final Observer<? super T> downstream;
        final TimeUnit unit;
        Disposable upstream;
        /* renamed from: w */
        final Worker f47w;

        final class OnComplete implements Runnable {
            OnComplete() {
            }

            public void run() {
                try {
                    DelayObserver.this.downstream.onComplete();
                } finally {
                    DelayObserver.this.f47w.dispose();
                }
            }
        }

        final class OnError implements Runnable {
            private final Throwable throwable;

            OnError(Throwable throwable) {
                this.throwable = throwable;
            }

            public void run() {
                try {
                    DelayObserver.this.downstream.onError(this.throwable);
                } finally {
                    DelayObserver.this.f47w.dispose();
                }
            }
        }

        final class OnNext implements Runnable {
            /* renamed from: t */
            private final T f29t;

            OnNext(T t) {
                this.f29t = t;
            }

            public void run() {
                DelayObserver.this.downstream.onNext(this.f29t);
            }
        }

        DelayObserver(Observer<? super T> actual, long delay, TimeUnit unit, Worker w, boolean delayError) {
            this.downstream = actual;
            this.delay = delay;
            this.unit = unit;
            this.f47w = w;
            this.delayError = delayError;
        }

        public void onSubscribe(Disposable d) {
            if (DisposableHelper.validate(this.upstream, d)) {
                this.upstream = d;
                this.downstream.onSubscribe(this);
            }
        }

        public void onNext(T t) {
            this.f47w.schedule(new OnNext(t), this.delay, this.unit);
        }

        public void onError(Throwable t) {
            this.f47w.schedule(new OnError(t), this.delayError ? this.delay : 0, this.unit);
        }

        public void onComplete() {
            this.f47w.schedule(new OnComplete(), this.delay, this.unit);
        }

        public void dispose() {
            this.upstream.dispose();
            this.f47w.dispose();
        }

        public boolean isDisposed() {
            return this.f47w.isDisposed();
        }
    }

    public ObservableDelay(ObservableSource<T> source, long delay, TimeUnit unit, Scheduler scheduler, boolean delayError) {
        super(source);
        this.delay = delay;
        this.unit = unit;
        this.scheduler = scheduler;
        this.delayError = delayError;
    }

    public void subscribeActual(Observer<? super T> t) {
        Observer<T> observer;
        if (this.delayError) {
            observer = t;
        } else {
            observer = new SerializedObserver(t);
        }
        this.source.subscribe(new DelayObserver(observer, this.delay, this.unit, this.scheduler.createWorker(), this.delayError));
    }
}
