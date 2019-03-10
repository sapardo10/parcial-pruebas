package io.reactivex.internal.operators.observable;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.Scheduler.Worker;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.schedulers.TrampolineScheduler;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public final class ObservableIntervalRange extends Observable<Long> {
    final long end;
    final long initialDelay;
    final long period;
    final Scheduler scheduler;
    final long start;
    final TimeUnit unit;

    static final class IntervalRangeObserver extends AtomicReference<Disposable> implements Disposable, Runnable {
        private static final long serialVersionUID = 1891866368734007884L;
        long count;
        final Observer<? super Long> downstream;
        final long end;

        IntervalRangeObserver(Observer<? super Long> actual, long start, long end) {
            this.downstream = actual;
            this.count = start;
            this.end = end;
        }

        public void dispose() {
            DisposableHelper.dispose(this);
        }

        public boolean isDisposed() {
            return get() == DisposableHelper.DISPOSED;
        }

        public void run() {
            if (!isDisposed()) {
                long c = this.count;
                this.downstream.onNext(Long.valueOf(c));
                if (c == this.end) {
                    DisposableHelper.dispose(this);
                    this.downstream.onComplete();
                    return;
                }
                this.count = 1 + c;
            }
        }

        public void setResource(Disposable d) {
            DisposableHelper.setOnce(this, d);
        }
    }

    public ObservableIntervalRange(long start, long end, long initialDelay, long period, TimeUnit unit, Scheduler scheduler) {
        this.initialDelay = initialDelay;
        this.period = period;
        this.unit = unit;
        this.scheduler = scheduler;
        this.start = start;
        this.end = end;
    }

    public void subscribeActual(Observer<? super Long> observer) {
        IntervalRangeObserver is = new IntervalRangeObserver(observer, this.start, this.end);
        observer.onSubscribe(is);
        Scheduler sch = this.scheduler;
        if (sch instanceof TrampolineScheduler) {
            Worker worker = sch.createWorker();
            is.setResource(worker);
            worker.schedulePeriodically(is, this.initialDelay, this.period, this.unit);
            return;
        }
        is.setResource(sch.schedulePeriodicallyDirect(is, this.initialDelay, this.period, this.unit));
    }
}
