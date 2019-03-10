package io.reactivex.internal.operators.observable;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.Scheduler.Worker;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class ObservableThrottleLatest<T> extends AbstractObservableWithUpstream<T, T> {
    final boolean emitLast;
    final Scheduler scheduler;
    final long timeout;
    final TimeUnit unit;

    static final class ThrottleLatestObserver<T> extends AtomicInteger implements Observer<T>, Disposable, Runnable {
        private static final long serialVersionUID = -8296689127439125014L;
        volatile boolean cancelled;
        volatile boolean done;
        final Observer<? super T> downstream;
        final boolean emitLast;
        Throwable error;
        final AtomicReference<T> latest = new AtomicReference();
        final long timeout;
        volatile boolean timerFired;
        boolean timerRunning;
        final TimeUnit unit;
        Disposable upstream;
        final Worker worker;

        ThrottleLatestObserver(Observer<? super T> downstream, long timeout, TimeUnit unit, Worker worker, boolean emitLast) {
            this.downstream = downstream;
            this.timeout = timeout;
            this.unit = unit;
            this.worker = worker;
            this.emitLast = emitLast;
        }

        public void onSubscribe(Disposable d) {
            if (DisposableHelper.validate(this.upstream, d)) {
                this.upstream = d;
                this.downstream.onSubscribe(this);
            }
        }

        public void onNext(T t) {
            this.latest.set(t);
            drain();
        }

        public void onError(Throwable t) {
            this.error = t;
            this.done = true;
            drain();
        }

        public void onComplete() {
            this.done = true;
            drain();
        }

        public void dispose() {
            this.cancelled = true;
            this.upstream.dispose();
            this.worker.dispose();
            if (getAndIncrement() == 0) {
                this.latest.lazySet(null);
            }
        }

        public boolean isDisposed() {
            return this.cancelled;
        }

        public void run() {
            this.timerFired = true;
            drain();
        }

        void drain() {
            if (getAndIncrement() == 0) {
                int missed = 1;
                AtomicReference<T> latest = this.latest;
                Observer<? super T> downstream = this.downstream;
                while (!this.cancelled) {
                    boolean d = this.done;
                    if (!d || this.error == null) {
                        boolean empty = latest.get() == null;
                        if (d) {
                            T v = latest.getAndSet(null);
                            if (!empty && this.emitLast) {
                                downstream.onNext(v);
                            }
                            downstream.onComplete();
                            this.worker.dispose();
                            return;
                        }
                        if (!empty) {
                            if (this.timerRunning) {
                                if (this.timerFired) {
                                }
                            }
                            downstream.onNext(latest.getAndSet(null));
                            this.timerFired = false;
                            this.timerRunning = true;
                            this.worker.schedule(this, this.timeout, this.unit);
                        } else if (this.timerFired) {
                            this.timerRunning = false;
                            this.timerFired = false;
                        }
                        missed = addAndGet(-missed);
                        if (missed == 0) {
                            return;
                        }
                    } else {
                        latest.lazySet(null);
                        downstream.onError(this.error);
                        this.worker.dispose();
                        return;
                    }
                }
                latest.lazySet(null);
            }
        }
    }

    public ObservableThrottleLatest(Observable<T> source, long timeout, TimeUnit unit, Scheduler scheduler, boolean emitLast) {
        super(source);
        this.timeout = timeout;
        this.unit = unit;
        this.scheduler = scheduler;
        this.emitLast = emitLast;
    }

    protected void subscribeActual(Observer<? super T> observer) {
        this.source.subscribe(new ThrottleLatestObserver(observer, this.timeout, this.unit, this.scheduler.createWorker(), this.emitLast));
    }
}
