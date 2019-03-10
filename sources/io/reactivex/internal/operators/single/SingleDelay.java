package io.reactivex.internal.operators.single;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.SequentialDisposable;
import java.util.concurrent.TimeUnit;

public final class SingleDelay<T> extends Single<T> {
    final boolean delayError;
    final Scheduler scheduler;
    final SingleSource<? extends T> source;
    final long time;
    final TimeUnit unit;

    final class Delay implements SingleObserver<T> {
        final SingleObserver<? super T> downstream;
        private final SequentialDisposable sd;

        final class OnError implements Runnable {
            /* renamed from: e */
            private final Throwable f33e;

            OnError(Throwable e) {
                this.f33e = e;
            }

            public void run() {
                Delay.this.downstream.onError(this.f33e);
            }
        }

        final class OnSuccess implements Runnable {
            private final T value;

            OnSuccess(T value) {
                this.value = value;
            }

            public void run() {
                Delay.this.downstream.onSuccess(this.value);
            }
        }

        Delay(SequentialDisposable sd, SingleObserver<? super T> observer) {
            this.sd = sd;
            this.downstream = observer;
        }

        public void onSubscribe(Disposable d) {
            this.sd.replace(d);
        }

        public void onSuccess(T value) {
            this.sd.replace(SingleDelay.this.scheduler.scheduleDirect(new OnSuccess(value), SingleDelay.this.time, SingleDelay.this.unit));
        }

        public void onError(Throwable e) {
            this.sd.replace(SingleDelay.this.scheduler.scheduleDirect(new OnError(e), SingleDelay.this.delayError ? SingleDelay.this.time : 0, SingleDelay.this.unit));
        }
    }

    public SingleDelay(SingleSource<? extends T> source, long time, TimeUnit unit, Scheduler scheduler, boolean delayError) {
        this.source = source;
        this.time = time;
        this.unit = unit;
        this.scheduler = scheduler;
        this.delayError = delayError;
    }

    protected void subscribeActual(SingleObserver<? super T> observer) {
        SequentialDisposable sd = new SequentialDisposable();
        observer.onSubscribe(sd);
        this.source.subscribe(new Delay(sd, observer));
    }
}
