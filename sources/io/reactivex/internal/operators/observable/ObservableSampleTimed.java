package io.reactivex.internal.operators.observable;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.observers.SerializedObserver;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class ObservableSampleTimed<T> extends AbstractObservableWithUpstream<T, T> {
    final boolean emitLast;
    final long period;
    final Scheduler scheduler;
    final TimeUnit unit;

    static abstract class SampleTimedObserver<T> extends AtomicReference<T> implements Observer<T>, Disposable, Runnable {
        private static final long serialVersionUID = -3517602651313910099L;
        final Observer<? super T> downstream;
        final long period;
        final Scheduler scheduler;
        final AtomicReference<Disposable> timer = new AtomicReference();
        final TimeUnit unit;
        Disposable upstream;

        abstract void complete();

        SampleTimedObserver(Observer<? super T> actual, long period, TimeUnit unit, Scheduler scheduler) {
            this.downstream = actual;
            this.period = period;
            this.unit = unit;
            this.scheduler = scheduler;
        }

        public void onSubscribe(Disposable d) {
            if (DisposableHelper.validate(this.upstream, d)) {
                this.upstream = d;
                this.downstream.onSubscribe(this);
                Scheduler scheduler = this.scheduler;
                long j = this.period;
                DisposableHelper.replace(this.timer, scheduler.schedulePeriodicallyDirect(this, j, j, this.unit));
            }
        }

        public void onNext(T t) {
            lazySet(t);
        }

        public void onError(Throwable t) {
            cancelTimer();
            this.downstream.onError(t);
        }

        public void onComplete() {
            cancelTimer();
            complete();
        }

        void cancelTimer() {
            DisposableHelper.dispose(this.timer);
        }

        public void dispose() {
            cancelTimer();
            this.upstream.dispose();
        }

        public boolean isDisposed() {
            return this.upstream.isDisposed();
        }

        void emit() {
            T value = getAndSet(null);
            if (value != null) {
                this.downstream.onNext(value);
            }
        }
    }

    static final class SampleTimedEmitLast<T> extends SampleTimedObserver<T> {
        private static final long serialVersionUID = -7139995637533111443L;
        final AtomicInteger wip = new AtomicInteger(1);

        SampleTimedEmitLast(Observer<? super T> actual, long period, TimeUnit unit, Scheduler scheduler) {
            super(actual, period, unit, scheduler);
        }

        void complete() {
            emit();
            if (this.wip.decrementAndGet() == 0) {
                this.downstream.onComplete();
            }
        }

        public void run() {
            if (this.wip.incrementAndGet() == 2) {
                emit();
                if (this.wip.decrementAndGet() == 0) {
                    this.downstream.onComplete();
                }
            }
        }
    }

    static final class SampleTimedNoLast<T> extends SampleTimedObserver<T> {
        private static final long serialVersionUID = -7139995637533111443L;

        SampleTimedNoLast(Observer<? super T> actual, long period, TimeUnit unit, Scheduler scheduler) {
            super(actual, period, unit, scheduler);
        }

        void complete() {
            this.downstream.onComplete();
        }

        public void run() {
            emit();
        }
    }

    public ObservableSampleTimed(ObservableSource<T> source, long period, TimeUnit unit, Scheduler scheduler, boolean emitLast) {
        super(source);
        this.period = period;
        this.unit = unit;
        this.scheduler = scheduler;
        this.emitLast = emitLast;
    }

    public void subscribeActual(Observer<? super T> t) {
        SerializedObserver<T> serial = new SerializedObserver(t);
        if (this.emitLast) {
            this.source.subscribe(new SampleTimedEmitLast(serial, this.period, this.unit, this.scheduler));
            return;
        }
        this.source.subscribe(new SampleTimedNoLast(serial, this.period, this.unit, this.scheduler));
    }
}
