package io.reactivex.internal.operators.completable;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableSource;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.disposables.SequentialDisposable;
import java.util.concurrent.atomic.AtomicReference;

public final class CompletableSubscribeOn extends Completable {
    final Scheduler scheduler;
    final CompletableSource source;

    static final class SubscribeOnObserver extends AtomicReference<Disposable> implements CompletableObserver, Disposable, Runnable {
        private static final long serialVersionUID = 7000911171163930287L;
        final CompletableObserver downstream;
        final CompletableSource source;
        final SequentialDisposable task = new SequentialDisposable();

        SubscribeOnObserver(CompletableObserver actual, CompletableSource source) {
            this.downstream = actual;
            this.source = source;
        }

        public void run() {
            this.source.subscribe(this);
        }

        public void onSubscribe(Disposable d) {
            DisposableHelper.setOnce(this, d);
        }

        public void onError(Throwable e) {
            this.downstream.onError(e);
        }

        public void onComplete() {
            this.downstream.onComplete();
        }

        public void dispose() {
            DisposableHelper.dispose(this);
            this.task.dispose();
        }

        public boolean isDisposed() {
            return DisposableHelper.isDisposed((Disposable) get());
        }
    }

    public CompletableSubscribeOn(CompletableSource source, Scheduler scheduler) {
        this.source = source;
        this.scheduler = scheduler;
    }

    protected void subscribeActual(CompletableObserver observer) {
        SubscribeOnObserver parent = new SubscribeOnObserver(observer, this.source);
        observer.onSubscribe(parent);
        parent.task.replace(this.scheduler.scheduleDirect(parent));
    }
}
