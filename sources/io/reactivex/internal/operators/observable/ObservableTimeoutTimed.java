package io.reactivex.internal.operators.observable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.Scheduler.Worker;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.disposables.SequentialDisposable;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public final class ObservableTimeoutTimed<T> extends AbstractObservableWithUpstream<T, T> {
    final ObservableSource<? extends T> other;
    final Scheduler scheduler;
    final long timeout;
    final TimeUnit unit;

    interface TimeoutSupport {
        void onTimeout(long j);
    }

    static final class TimeoutTask implements Runnable {
        final long idx;
        final TimeoutSupport parent;

        TimeoutTask(long idx, TimeoutSupport parent) {
            this.idx = idx;
            this.parent = parent;
        }

        public void run() {
            this.parent.onTimeout(this.idx);
        }
    }

    static final class FallbackObserver<T> implements Observer<T> {
        final AtomicReference<Disposable> arbiter;
        final Observer<? super T> downstream;

        FallbackObserver(Observer<? super T> actual, AtomicReference<Disposable> arbiter) {
            this.downstream = actual;
            this.arbiter = arbiter;
        }

        public void onSubscribe(Disposable d) {
            DisposableHelper.replace(this.arbiter, d);
        }

        public void onNext(T t) {
            this.downstream.onNext(t);
        }

        public void onError(Throwable t) {
            this.downstream.onError(t);
        }

        public void onComplete() {
            this.downstream.onComplete();
        }
    }

    static final class TimeoutFallbackObserver<T> extends AtomicReference<Disposable> implements Observer<T>, Disposable, TimeoutSupport {
        private static final long serialVersionUID = 3764492702657003550L;
        final Observer<? super T> downstream;
        ObservableSource<? extends T> fallback;
        final AtomicLong index = new AtomicLong();
        final SequentialDisposable task = new SequentialDisposable();
        final long timeout;
        final TimeUnit unit;
        final AtomicReference<Disposable> upstream = new AtomicReference();
        final Worker worker;

        TimeoutFallbackObserver(Observer<? super T> actual, long timeout, TimeUnit unit, Worker worker, ObservableSource<? extends T> fallback) {
            this.downstream = actual;
            this.timeout = timeout;
            this.unit = unit;
            this.worker = worker;
            this.fallback = fallback;
        }

        public void onSubscribe(Disposable d) {
            DisposableHelper.setOnce(this.upstream, d);
        }

        public void onNext(T t) {
            long idx = this.index.get();
            if (idx != Long.MAX_VALUE) {
                if (this.index.compareAndSet(idx, idx + 1)) {
                    ((Disposable) this.task.get()).dispose();
                    this.downstream.onNext(t);
                    startTimeout(1 + idx);
                }
            }
        }

        void startTimeout(long nextIndex) {
            this.task.replace(this.worker.schedule(new TimeoutTask(nextIndex, this), this.timeout, this.unit));
        }

        public void onError(Throwable t) {
            if (this.index.getAndSet(Long.MAX_VALUE) != Long.MAX_VALUE) {
                this.task.dispose();
                this.downstream.onError(t);
                this.worker.dispose();
                return;
            }
            RxJavaPlugins.onError(t);
        }

        public void onComplete() {
            if (this.index.getAndSet(Long.MAX_VALUE) != Long.MAX_VALUE) {
                this.task.dispose();
                this.downstream.onComplete();
                this.worker.dispose();
            }
        }

        public void onTimeout(long idx) {
            if (this.index.compareAndSet(idx, Long.MAX_VALUE)) {
                DisposableHelper.dispose(this.upstream);
                ObservableSource<? extends T> f = this.fallback;
                this.fallback = null;
                f.subscribe(new FallbackObserver(this.downstream, this));
                this.worker.dispose();
            }
        }

        public void dispose() {
            DisposableHelper.dispose(this.upstream);
            DisposableHelper.dispose(this);
            this.worker.dispose();
        }

        public boolean isDisposed() {
            return DisposableHelper.isDisposed((Disposable) get());
        }
    }

    static final class TimeoutObserver<T> extends AtomicLong implements Observer<T>, Disposable, TimeoutSupport {
        private static final long serialVersionUID = 3764492702657003550L;
        final Observer<? super T> downstream;
        final SequentialDisposable task = new SequentialDisposable();
        final long timeout;
        final TimeUnit unit;
        final AtomicReference<Disposable> upstream = new AtomicReference();
        final Worker worker;

        TimeoutObserver(Observer<? super T> actual, long timeout, TimeUnit unit, Worker worker) {
            this.downstream = actual;
            this.timeout = timeout;
            this.unit = unit;
            this.worker = worker;
        }

        public void onSubscribe(Disposable d) {
            DisposableHelper.setOnce(this.upstream, d);
        }

        public void onNext(T t) {
            long idx = get();
            if (idx != Long.MAX_VALUE) {
                if (compareAndSet(idx, idx + 1)) {
                    ((Disposable) this.task.get()).dispose();
                    this.downstream.onNext(t);
                    startTimeout(1 + idx);
                }
            }
        }

        void startTimeout(long nextIndex) {
            this.task.replace(this.worker.schedule(new TimeoutTask(nextIndex, this), this.timeout, this.unit));
        }

        public void onError(Throwable t) {
            if (getAndSet(Long.MAX_VALUE) != Long.MAX_VALUE) {
                this.task.dispose();
                this.downstream.onError(t);
                this.worker.dispose();
                return;
            }
            RxJavaPlugins.onError(t);
        }

        public void onComplete() {
            if (getAndSet(Long.MAX_VALUE) != Long.MAX_VALUE) {
                this.task.dispose();
                this.downstream.onComplete();
                this.worker.dispose();
            }
        }

        public void onTimeout(long idx) {
            if (compareAndSet(idx, Long.MAX_VALUE)) {
                DisposableHelper.dispose(this.upstream);
                this.downstream.onError(new TimeoutException());
                this.worker.dispose();
            }
        }

        public void dispose() {
            DisposableHelper.dispose(this.upstream);
            this.worker.dispose();
        }

        public boolean isDisposed() {
            return DisposableHelper.isDisposed((Disposable) this.upstream.get());
        }
    }

    public ObservableTimeoutTimed(Observable<T> source, long timeout, TimeUnit unit, Scheduler scheduler, ObservableSource<? extends T> other) {
        super(source);
        this.timeout = timeout;
        this.unit = unit;
        this.scheduler = scheduler;
        this.other = other;
    }

    protected void subscribeActual(Observer<? super T> observer) {
        if (this.other == null) {
            TimeoutObserver<T> timeoutObserver = new TimeoutObserver(observer, this.timeout, this.unit, this.scheduler.createWorker());
            observer.onSubscribe(timeoutObserver);
            timeoutObserver.startTimeout(0);
            this.source.subscribe(timeoutObserver);
            return;
        }
        TimeoutFallbackObserver<T> timeoutFallbackObserver = new TimeoutFallbackObserver(observer, this.timeout, this.unit, this.scheduler.createWorker(), this.other);
        observer.onSubscribe(timeoutFallbackObserver);
        timeoutFallbackObserver.startTimeout(0);
        this.source.subscribe(timeoutFallbackObserver);
    }
}
