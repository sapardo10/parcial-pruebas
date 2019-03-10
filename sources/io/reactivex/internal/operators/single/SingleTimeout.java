package io.reactivex.internal.operators.single;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

public final class SingleTimeout<T> extends Single<T> {
    final SingleSource<? extends T> other;
    final Scheduler scheduler;
    final SingleSource<T> source;
    final long timeout;
    final TimeUnit unit;

    static final class TimeoutMainObserver<T> extends AtomicReference<Disposable> implements SingleObserver<T>, Runnable, Disposable {
        private static final long serialVersionUID = 37497744973048446L;
        final SingleObserver<? super T> downstream;
        final TimeoutFallbackObserver<T> fallback;
        SingleSource<? extends T> other;
        final AtomicReference<Disposable> task = new AtomicReference();

        static final class TimeoutFallbackObserver<T> extends AtomicReference<Disposable> implements SingleObserver<T> {
            private static final long serialVersionUID = 2071387740092105509L;
            final SingleObserver<? super T> downstream;

            TimeoutFallbackObserver(SingleObserver<? super T> downstream) {
                this.downstream = downstream;
            }

            public void onSubscribe(Disposable d) {
                DisposableHelper.setOnce(this, d);
            }

            public void onSuccess(T t) {
                this.downstream.onSuccess(t);
            }

            public void onError(Throwable e) {
                this.downstream.onError(e);
            }
        }

        TimeoutMainObserver(SingleObserver<? super T> actual, SingleSource<? extends T> other) {
            this.downstream = actual;
            this.other = other;
            if (other != null) {
                this.fallback = new TimeoutFallbackObserver(actual);
            } else {
                this.fallback = null;
            }
        }

        public void run() {
            Disposable d = (Disposable) get();
            if (d != DisposableHelper.DISPOSED && compareAndSet(d, DisposableHelper.DISPOSED)) {
                if (d != null) {
                    d.dispose();
                }
                SingleSource<? extends T> other = this.other;
                if (other == null) {
                    this.downstream.onError(new TimeoutException());
                    return;
                }
                this.other = null;
                other.subscribe(this.fallback);
            }
        }

        public void onSubscribe(Disposable d) {
            DisposableHelper.setOnce(this, d);
        }

        public void onSuccess(T t) {
            Disposable d = (Disposable) get();
            if (d != DisposableHelper.DISPOSED && compareAndSet(d, DisposableHelper.DISPOSED)) {
                DisposableHelper.dispose(this.task);
                this.downstream.onSuccess(t);
            }
        }

        public void onError(Throwable e) {
            Disposable d = (Disposable) get();
            if (d == DisposableHelper.DISPOSED || !compareAndSet(d, DisposableHelper.DISPOSED)) {
                RxJavaPlugins.onError(e);
                return;
            }
            DisposableHelper.dispose(this.task);
            this.downstream.onError(e);
        }

        public void dispose() {
            DisposableHelper.dispose(this);
            DisposableHelper.dispose(this.task);
            AtomicReference atomicReference = this.fallback;
            if (atomicReference != null) {
                DisposableHelper.dispose(atomicReference);
            }
        }

        public boolean isDisposed() {
            return DisposableHelper.isDisposed((Disposable) get());
        }
    }

    public SingleTimeout(SingleSource<T> source, long timeout, TimeUnit unit, Scheduler scheduler, SingleSource<? extends T> other) {
        this.source = source;
        this.timeout = timeout;
        this.unit = unit;
        this.scheduler = scheduler;
        this.other = other;
    }

    protected void subscribeActual(SingleObserver<? super T> observer) {
        TimeoutMainObserver<T> parent = new TimeoutMainObserver(observer, this.other);
        observer.onSubscribe(parent);
        DisposableHelper.replace(parent.task, this.scheduler.scheduleDirect(parent, this.timeout, this.unit));
        this.source.subscribe(parent);
    }
}
