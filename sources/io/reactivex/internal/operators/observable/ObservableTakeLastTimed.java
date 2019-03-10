package io.reactivex.internal.operators.observable;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.queue.SpscLinkedArrayQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ObservableTakeLastTimed<T> extends AbstractObservableWithUpstream<T, T> {
    final int bufferSize;
    final long count;
    final boolean delayError;
    final Scheduler scheduler;
    final long time;
    final TimeUnit unit;

    static final class TakeLastTimedObserver<T> extends AtomicBoolean implements Observer<T>, Disposable {
        private static final long serialVersionUID = -5677354903406201275L;
        volatile boolean cancelled;
        final long count;
        final boolean delayError;
        final Observer<? super T> downstream;
        Throwable error;
        final SpscLinkedArrayQueue<Object> queue;
        final Scheduler scheduler;
        final long time;
        final TimeUnit unit;
        Disposable upstream;

        TakeLastTimedObserver(Observer<? super T> actual, long count, long time, TimeUnit unit, Scheduler scheduler, int bufferSize, boolean delayError) {
            this.downstream = actual;
            this.count = count;
            this.time = time;
            this.unit = unit;
            this.scheduler = scheduler;
            this.queue = new SpscLinkedArrayQueue(bufferSize);
            this.delayError = delayError;
        }

        public void onSubscribe(Disposable d) {
            if (DisposableHelper.validate(this.upstream, d)) {
                this.upstream = d;
                this.downstream.onSubscribe(this);
            }
        }

        public void onNext(T t) {
            SpscLinkedArrayQueue<Object> q = this.queue;
            long now = this.scheduler.now(this.unit);
            long time = this.time;
            long c = this.count;
            boolean unbounded = c == Long.MAX_VALUE;
            q.offer(Long.valueOf(now), t);
            while (!q.isEmpty()) {
                if (((Long) q.peek()).longValue() > now - time) {
                    if (unbounded || ((long) (q.size() >> 1)) <= c) {
                        return;
                    }
                }
                q.poll();
                q.poll();
            }
        }

        public void onError(Throwable t) {
            this.error = t;
            drain();
        }

        public void onComplete() {
            drain();
        }

        public void dispose() {
            if (!this.cancelled) {
                this.cancelled = true;
                this.upstream.dispose();
                if (compareAndSet(false, true)) {
                    this.queue.clear();
                }
            }
        }

        public boolean isDisposed() {
            return this.cancelled;
        }

        void drain() {
            if (compareAndSet(false, true)) {
                Observer<? super T> a = this.downstream;
                SpscLinkedArrayQueue<Object> q = this.queue;
                boolean delayError = this.delayError;
                while (!this.cancelled) {
                    if (!delayError) {
                        Throwable ex = this.error;
                        if (ex != null) {
                            q.clear();
                            a.onError(ex);
                            return;
                        }
                    }
                    Object ts = q.poll();
                    if (ts == null) {
                        Throwable ex2 = this.error;
                        if (ex2 != null) {
                            a.onError(ex2);
                        } else {
                            a.onComplete();
                        }
                        return;
                    }
                    T o = q.poll();
                    if (((Long) ts).longValue() >= this.scheduler.now(this.unit) - this.time) {
                        a.onNext(o);
                    }
                }
                q.clear();
            }
        }
    }

    public ObservableTakeLastTimed(ObservableSource<T> source, long count, long time, TimeUnit unit, Scheduler scheduler, int bufferSize, boolean delayError) {
        super(source);
        this.count = count;
        this.time = time;
        this.unit = unit;
        this.scheduler = scheduler;
        this.bufferSize = bufferSize;
        this.delayError = delayError;
    }

    public void subscribeActual(Observer<? super T> t) {
        this.source.subscribe(new TakeLastTimedObserver(t, this.count, this.time, this.unit, this.scheduler, this.bufferSize, this.delayError));
    }
}
