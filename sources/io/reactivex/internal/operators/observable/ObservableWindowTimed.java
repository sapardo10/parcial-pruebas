package io.reactivex.internal.operators.observable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.Scheduler.Worker;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.observers.QueueDrainObserver;
import io.reactivex.internal.queue.MpscLinkedQueue;
import io.reactivex.internal.util.NotificationLite;
import io.reactivex.observers.SerializedObserver;
import io.reactivex.subjects.UnicastSubject;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public final class ObservableWindowTimed<T> extends AbstractObservableWithUpstream<T, Observable<T>> {
    final int bufferSize;
    final long maxSize;
    final boolean restartTimerOnMaxSize;
    final Scheduler scheduler;
    final long timeskip;
    final long timespan;
    final TimeUnit unit;

    static final class WindowExactBoundedObserver<T> extends QueueDrainObserver<T, Object, Observable<T>> implements Disposable {
        final int bufferSize;
        long count;
        final long maxSize;
        long producerIndex;
        final boolean restartTimerOnMaxSize;
        final Scheduler scheduler;
        volatile boolean terminated;
        final AtomicReference<Disposable> timer = new AtomicReference();
        final long timespan;
        final TimeUnit unit;
        Disposable upstream;
        UnicastSubject<T> window;
        final Worker worker;

        static final class ConsumerIndexHolder implements Runnable {
            final long index;
            final WindowExactBoundedObserver<?> parent;

            ConsumerIndexHolder(long index, WindowExactBoundedObserver<?> parent) {
                this.index = index;
                this.parent = parent;
            }

            public void run() {
                WindowExactBoundedObserver<?> p = this.parent;
                if (p.cancelled) {
                    p.terminated = true;
                    p.disposeTimer();
                } else {
                    p.queue.offer(this);
                }
                if (p.enter()) {
                    p.drainLoop();
                }
            }
        }

        WindowExactBoundedObserver(Observer<? super Observable<T>> actual, long timespan, TimeUnit unit, Scheduler scheduler, int bufferSize, long maxSize, boolean restartTimerOnMaxSize) {
            super(actual, new MpscLinkedQueue());
            this.timespan = timespan;
            this.unit = unit;
            this.scheduler = scheduler;
            this.bufferSize = bufferSize;
            this.maxSize = maxSize;
            this.restartTimerOnMaxSize = restartTimerOnMaxSize;
            if (restartTimerOnMaxSize) {
                this.worker = scheduler.createWorker();
            } else {
                this.worker = null;
            }
        }

        public void onSubscribe(Disposable d) {
            if (DisposableHelper.validate(this.upstream, d)) {
                this.upstream = d;
                Observer<? super Observable<T>> a = this.downstream;
                a.onSubscribe(this);
                if (!this.cancelled) {
                    Disposable task;
                    UnicastSubject<T> w = UnicastSubject.create(this.bufferSize);
                    this.window = w;
                    a.onNext(w);
                    ConsumerIndexHolder consumerIndexHolder = new ConsumerIndexHolder(this.producerIndex, this);
                    if (this.restartTimerOnMaxSize) {
                        Worker worker = this.worker;
                        long j = this.timespan;
                        task = worker.schedulePeriodically(consumerIndexHolder, j, j, this.unit);
                    } else {
                        Scheduler scheduler = this.scheduler;
                        long j2 = this.timespan;
                        task = scheduler.schedulePeriodicallyDirect(consumerIndexHolder, j2, j2, this.unit);
                    }
                    DisposableHelper.replace(this.timer, task);
                }
            }
        }

        public void onNext(T t) {
            if (!this.terminated) {
                if (fastEnter()) {
                    UnicastSubject<T> w = this.window;
                    w.onNext(t);
                    long c = this.count + 1;
                    if (c >= this.maxSize) {
                        this.producerIndex++;
                        this.count = 0;
                        w.onComplete();
                        w = UnicastSubject.create(this.bufferSize);
                        this.window = w;
                        this.downstream.onNext(w);
                        if (this.restartTimerOnMaxSize) {
                            ((Disposable) this.timer.get()).dispose();
                            Disposable task = this.worker;
                            Runnable consumerIndexHolder = new ConsumerIndexHolder(this.producerIndex, this);
                            long j = this.timespan;
                            DisposableHelper.replace(this.timer, task.schedulePeriodically(consumerIndexHolder, j, j, this.unit));
                        }
                    } else {
                        this.count = c;
                    }
                    if (leave(-1) == 0) {
                        return;
                    }
                } else {
                    this.queue.offer(NotificationLite.next(t));
                    if (!enter()) {
                        return;
                    }
                }
                drainLoop();
            }
        }

        public void onError(Throwable t) {
            this.error = t;
            this.done = true;
            if (enter()) {
                drainLoop();
            }
            this.downstream.onError(t);
            disposeTimer();
        }

        public void onComplete() {
            this.done = true;
            if (enter()) {
                drainLoop();
            }
            this.downstream.onComplete();
            disposeTimer();
        }

        public void dispose() {
            this.cancelled = true;
        }

        public boolean isDisposed() {
            return this.cancelled;
        }

        void disposeTimer() {
            DisposableHelper.dispose(this.timer);
            Worker w = this.worker;
            if (w != null) {
                w.dispose();
            }
        }

        void drainLoop() {
            MpscLinkedQueue<Object> q = this.queue;
            Observer<? super Observable<T>> a = this.downstream;
            UnicastSubject<T> w = this.window;
            int missed = 1;
            while (!r0.terminated) {
                boolean d = r0.done;
                ConsumerIndexHolder o = q.poll();
                boolean empty = o == null;
                boolean isHolder = o instanceof ConsumerIndexHolder;
                if (d && (empty || isHolder)) {
                    r0.window = null;
                    q.clear();
                    disposeTimer();
                    Throwable err = r0.error;
                    if (err != null) {
                        w.onError(err);
                    } else {
                        w.onComplete();
                    }
                    return;
                } else if (empty) {
                    missed = leave(-missed);
                    if (missed == 0) {
                        return;
                    }
                } else if (isHolder) {
                    ConsumerIndexHolder consumerIndexHolder = o;
                    if (!r0.restartTimerOnMaxSize) {
                        if (r0.producerIndex == consumerIndexHolder.index) {
                        }
                    }
                    w.onComplete();
                    r0.count = 0;
                    w = UnicastSubject.create(r0.bufferSize);
                    r0.window = w;
                    a.onNext(w);
                } else {
                    MpscLinkedQueue<Object> q2;
                    Observer<? super Observable<T>> a2;
                    w.onNext(NotificationLite.getValue(o));
                    long c = r0.count + 1;
                    if (c >= r0.maxSize) {
                        r0.producerIndex++;
                        r0.count = 0;
                        w.onComplete();
                        w = UnicastSubject.create(r0.bufferSize);
                        r0.window = w;
                        r0.downstream.onNext(w);
                        if (r0.restartTimerOnMaxSize) {
                            Disposable tm = (Disposable) r0.timer.get();
                            tm.dispose();
                            Worker worker = r0.worker;
                            q2 = q;
                            a2 = a;
                            Runnable consumerIndexHolder2 = new ConsumerIndexHolder(r0.producerIndex, r0);
                            q = r0.timespan;
                            q = worker.schedulePeriodically(consumerIndexHolder2, q, q, r0.unit);
                            if (!r0.timer.compareAndSet(tm, q)) {
                                q.dispose();
                            }
                        } else {
                            q2 = q;
                            a2 = a;
                        }
                    } else {
                        q2 = q;
                        a2 = a;
                        r0.count = c;
                    }
                    q = q2;
                    a = a2;
                }
            }
            r0.upstream.dispose();
            q.clear();
            disposeTimer();
        }
    }

    static final class WindowExactUnboundedObserver<T> extends QueueDrainObserver<T, Object, Observable<T>> implements Observer<T>, Disposable, Runnable {
        static final Object NEXT = new Object();
        final int bufferSize;
        final Scheduler scheduler;
        volatile boolean terminated;
        final AtomicReference<Disposable> timer = new AtomicReference();
        final long timespan;
        final TimeUnit unit;
        Disposable upstream;
        UnicastSubject<T> window;

        WindowExactUnboundedObserver(Observer<? super Observable<T>> actual, long timespan, TimeUnit unit, Scheduler scheduler, int bufferSize) {
            super(actual, new MpscLinkedQueue());
            this.timespan = timespan;
            this.unit = unit;
            this.scheduler = scheduler;
            this.bufferSize = bufferSize;
        }

        public void onSubscribe(Disposable d) {
            if (DisposableHelper.validate(this.upstream, d)) {
                this.upstream = d;
                this.window = UnicastSubject.create(this.bufferSize);
                Observer<? super Observable<T>> a = this.downstream;
                a.onSubscribe(this);
                a.onNext(this.window);
                if (!this.cancelled) {
                    Scheduler scheduler = this.scheduler;
                    long j = this.timespan;
                    DisposableHelper.replace(this.timer, scheduler.schedulePeriodicallyDirect(this, j, j, this.unit));
                }
            }
        }

        public void onNext(T t) {
            if (!this.terminated) {
                if (fastEnter()) {
                    this.window.onNext(t);
                    if (leave(-1) == 0) {
                        return;
                    }
                } else {
                    this.queue.offer(NotificationLite.next(t));
                    if (!enter()) {
                        return;
                    }
                }
                drainLoop();
            }
        }

        public void onError(Throwable t) {
            this.error = t;
            this.done = true;
            if (enter()) {
                drainLoop();
            }
            disposeTimer();
            this.downstream.onError(t);
        }

        public void onComplete() {
            this.done = true;
            if (enter()) {
                drainLoop();
            }
            disposeTimer();
            this.downstream.onComplete();
        }

        public void dispose() {
            this.cancelled = true;
        }

        public boolean isDisposed() {
            return this.cancelled;
        }

        void disposeTimer() {
            DisposableHelper.dispose(this.timer);
        }

        public void run() {
            if (this.cancelled) {
                this.terminated = true;
                disposeTimer();
            }
            this.queue.offer(NEXT);
            if (enter()) {
                drainLoop();
            }
        }

        void drainLoop() {
            MpscLinkedQueue<Object> q = this.queue;
            Observer<? super Observable<T>> a = this.downstream;
            UnicastSubject<T> w = this.window;
            int missed = 1;
            while (true) {
                boolean term = this.terminated;
                boolean d = this.done;
                Object o = q.poll();
                if (!(d && (o == null || o == NEXT))) {
                    if (o == null) {
                        missed = leave(-missed);
                        if (missed == 0) {
                            return;
                        }
                    } else if (o == NEXT) {
                        w.onComplete();
                        if (term) {
                            this.upstream.dispose();
                        } else {
                            w = UnicastSubject.create(this.bufferSize);
                            this.window = w;
                            a.onNext(w);
                        }
                    } else {
                        w.onNext(NotificationLite.getValue(o));
                    }
                }
            }
            this.window = null;
            q.clear();
            disposeTimer();
            Throwable err = this.error;
            if (err != null) {
                w.onError(err);
            } else {
                w.onComplete();
            }
        }
    }

    static final class WindowSkipObserver<T> extends QueueDrainObserver<T, Object, Observable<T>> implements Disposable, Runnable {
        final int bufferSize;
        volatile boolean terminated;
        final long timeskip;
        final long timespan;
        final TimeUnit unit;
        Disposable upstream;
        final List<UnicastSubject<T>> windows = new LinkedList();
        final Worker worker;

        final class CompletionTask implements Runnable {
            /* renamed from: w */
            private final UnicastSubject<T> f31w;

            CompletionTask(UnicastSubject<T> w) {
                this.f31w = w;
            }

            public void run() {
                WindowSkipObserver.this.complete(this.f31w);
            }
        }

        static final class SubjectWork<T> {
            final boolean open;
            /* renamed from: w */
            final UnicastSubject<T> f32w;

            SubjectWork(UnicastSubject<T> w, boolean open) {
                this.f32w = w;
                this.open = open;
            }
        }

        WindowSkipObserver(Observer<? super Observable<T>> actual, long timespan, long timeskip, TimeUnit unit, Worker worker, int bufferSize) {
            super(actual, new MpscLinkedQueue());
            this.timespan = timespan;
            this.timeskip = timeskip;
            this.unit = unit;
            this.worker = worker;
            this.bufferSize = bufferSize;
        }

        public void onSubscribe(Disposable d) {
            if (DisposableHelper.validate(this.upstream, d)) {
                this.upstream = d;
                this.downstream.onSubscribe(this);
                if (!this.cancelled) {
                    UnicastSubject<T> w = UnicastSubject.create(this.bufferSize);
                    this.windows.add(w);
                    this.downstream.onNext(w);
                    this.worker.schedule(new CompletionTask(w), this.timespan, this.unit);
                    Worker worker = this.worker;
                    long j = this.timeskip;
                    worker.schedulePeriodically(this, j, j, this.unit);
                }
            }
        }

        public void onNext(T t) {
            if (fastEnter()) {
                for (UnicastSubject<T> w : this.windows) {
                    w.onNext(t);
                }
                if (leave(-1) == 0) {
                    return;
                }
            } else {
                this.queue.offer(t);
                if (!enter()) {
                    return;
                }
            }
            drainLoop();
        }

        public void onError(Throwable t) {
            this.error = t;
            this.done = true;
            if (enter()) {
                drainLoop();
            }
            this.downstream.onError(t);
            disposeWorker();
        }

        public void onComplete() {
            this.done = true;
            if (enter()) {
                drainLoop();
            }
            this.downstream.onComplete();
            disposeWorker();
        }

        public void dispose() {
            this.cancelled = true;
        }

        public boolean isDisposed() {
            return this.cancelled;
        }

        void disposeWorker() {
            this.worker.dispose();
        }

        void complete(UnicastSubject<T> w) {
            this.queue.offer(new SubjectWork(w, false));
            if (enter()) {
                drainLoop();
            }
        }

        void drainLoop() {
            MpscLinkedQueue<Object> q = this.queue;
            Observer<? super Observable<T>> a = this.downstream;
            List<UnicastSubject<T>> ws = this.windows;
            int missed = 1;
            while (!this.terminated) {
                boolean d = this.done;
                SubjectWork<T> v = q.poll();
                boolean empty = v == null;
                boolean sw = v instanceof SubjectWork;
                if (d && (empty || sw)) {
                    q.clear();
                    Throwable e = this.error;
                    if (e != null) {
                        for (UnicastSubject<T> w : ws) {
                            w.onError(e);
                        }
                    } else {
                        for (UnicastSubject<T> w2 : ws) {
                            w2.onComplete();
                        }
                    }
                    disposeWorker();
                    ws.clear();
                    return;
                } else if (empty) {
                    missed = leave(-missed);
                    if (missed == 0) {
                        return;
                    }
                } else if (sw) {
                    SubjectWork<T> work = v;
                    if (!work.open) {
                        ws.remove(work.f32w);
                        work.f32w.onComplete();
                        if (ws.isEmpty() && this.cancelled) {
                            this.terminated = true;
                        }
                    } else if (!this.cancelled) {
                        UnicastSubject<T> w3 = UnicastSubject.create(this.bufferSize);
                        ws.add(w3);
                        a.onNext(w3);
                        this.worker.schedule(new CompletionTask(w3), this.timespan, this.unit);
                    }
                } else {
                    for (UnicastSubject<T> w4 : ws) {
                        w4.onNext(v);
                    }
                }
            }
            this.upstream.dispose();
            disposeWorker();
            q.clear();
            ws.clear();
        }

        public void run() {
            SubjectWork<T> sw = new SubjectWork(UnicastSubject.create(this.bufferSize), true);
            if (!this.cancelled) {
                this.queue.offer(sw);
            }
            if (enter()) {
                drainLoop();
            }
        }
    }

    public ObservableWindowTimed(ObservableSource<T> source, long timespan, long timeskip, TimeUnit unit, Scheduler scheduler, long maxSize, int bufferSize, boolean restartTimerOnMaxSize) {
        super(source);
        this.timespan = timespan;
        this.timeskip = timeskip;
        this.unit = unit;
        this.scheduler = scheduler;
        this.maxSize = maxSize;
        this.bufferSize = bufferSize;
        this.restartTimerOnMaxSize = restartTimerOnMaxSize;
    }

    public void subscribeActual(Observer<? super Observable<T>> t) {
        SerializedObserver<Observable<T>> actual = new SerializedObserver(t);
        if (this.timespan != this.timeskip) {
            this.source.subscribe(new WindowSkipObserver(actual, this.timespan, this.timeskip, this.unit, this.scheduler.createWorker(), this.bufferSize));
        } else if (this.maxSize == Long.MAX_VALUE) {
            this.source.subscribe(new WindowExactUnboundedObserver(actual, this.timespan, this.unit, this.scheduler, this.bufferSize));
        } else {
            this.source.subscribe(new WindowExactBoundedObserver(actual, this.timespan, this.unit, this.scheduler, this.bufferSize, this.maxSize, this.restartTimerOnMaxSize));
        }
    }
}
