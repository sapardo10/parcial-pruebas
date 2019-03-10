package io.reactivex.internal.operators.completable;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableSource;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public final class CompletableDelay extends Completable {
    final long delay;
    final boolean delayError;
    final Scheduler scheduler;
    final CompletableSource source;
    final TimeUnit unit;

    static final class Delay extends AtomicReference<Disposable> implements CompletableObserver, Runnable, Disposable {
        private static final long serialVersionUID = 465972761105851022L;
        final long delay;
        final boolean delayError;
        final CompletableObserver downstream;
        Throwable error;
        final Scheduler scheduler;
        final TimeUnit unit;

        Delay(CompletableObserver downstream, long delay, TimeUnit unit, Scheduler scheduler, boolean delayError) {
            this.downstream = downstream;
            this.delay = delay;
            this.unit = unit;
            this.scheduler = scheduler;
            this.delayError = delayError;
        }

        public void onSubscribe(Disposable d) {
            if (DisposableHelper.setOnce(this, d)) {
                this.downstream.onSubscribe(this);
            }
        }

        public void onComplete() {
            DisposableHelper.replace(this, this.scheduler.scheduleDirect(this, this.delay, this.unit));
        }

        public void onError(Throwable e) {
            this.error = e;
            DisposableHelper.replace(this, this.scheduler.scheduleDirect(this, this.delayError ? this.delay : 0, this.unit));
        }

        public void dispose() {
            DisposableHelper.dispose(this);
        }

        public boolean isDisposed() {
            return DisposableHelper.isDisposed((Disposable) get());
        }

        public void run() {
            Throwable e = this.error;
            this.error = null;
            if (e != null) {
                this.downstream.onError(e);
            } else {
                this.downstream.onComplete();
            }
        }
    }

    public CompletableDelay(CompletableSource source, long delay, TimeUnit unit, Scheduler scheduler, boolean delayError) {
        this.source = source;
        this.delay = delay;
        this.unit = unit;
        this.scheduler = scheduler;
        this.delayError = delayError;
    }

    protected void subscribeActual(CompletableObserver observer) {
        this.source.subscribe(new Delay(observer, this.delay, this.unit, this.scheduler, this.delayError));
    }
}
