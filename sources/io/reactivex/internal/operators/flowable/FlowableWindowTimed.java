package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.Scheduler;
import io.reactivex.Scheduler.Worker;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.MissingBackpressureException;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.disposables.SequentialDisposable;
import io.reactivex.internal.fuseable.SimplePlainQueue;
import io.reactivex.internal.queue.MpscLinkedQueue;
import io.reactivex.internal.subscribers.QueueDrainSubscriber;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.NotificationLite;
import io.reactivex.processors.UnicastProcessor;
import io.reactivex.subscribers.SerializedSubscriber;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FlowableWindowTimed<T> extends AbstractFlowableWithUpstream<T, Flowable<T>> {
    final int bufferSize;
    final long maxSize;
    final boolean restartTimerOnMaxSize;
    final Scheduler scheduler;
    final long timeskip;
    final long timespan;
    final TimeUnit unit;

    static final class WindowExactBoundedSubscriber<T> extends QueueDrainSubscriber<T, Object, Flowable<T>> implements Subscription {
        final int bufferSize;
        long count;
        final long maxSize;
        long producerIndex;
        final boolean restartTimerOnMaxSize;
        final Scheduler scheduler;
        volatile boolean terminated;
        final SequentialDisposable timer = new SequentialDisposable();
        final long timespan;
        final TimeUnit unit;
        Subscription upstream;
        UnicastProcessor<T> window;
        final Worker worker;

        static final class ConsumerIndexHolder implements Runnable {
            final long index;
            final WindowExactBoundedSubscriber<?> parent;

            ConsumerIndexHolder(long index, WindowExactBoundedSubscriber<?> parent) {
                this.index = index;
                this.parent = parent;
            }

            public void run() {
                WindowExactBoundedSubscriber<?> p = this.parent;
                if (p.cancelled) {
                    p.terminated = true;
                    p.dispose();
                } else {
                    p.queue.offer(this);
                }
                if (p.enter()) {
                    p.drainLoop();
                }
            }
        }

        WindowExactBoundedSubscriber(Subscriber<? super Flowable<T>> actual, long timespan, TimeUnit unit, Scheduler scheduler, int bufferSize, long maxSize, boolean restartTimerOnMaxSize) {
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

        public void onSubscribe(Subscription s) {
            Subscription subscription = s;
            if (SubscriptionHelper.validate(this.upstream, subscription)) {
                r0.upstream = subscription;
                Subscriber<? super Flowable<T>> a = r0.downstream;
                a.onSubscribe(r0);
                if (!r0.cancelled) {
                    UnicastProcessor<T> w = UnicastProcessor.create(r0.bufferSize);
                    r0.window = w;
                    long r = requested();
                    if (r != 0) {
                        Disposable task;
                        a.onNext(w);
                        if (r != Long.MAX_VALUE) {
                            produced(1);
                        }
                        ConsumerIndexHolder consumerIndexHolder = new ConsumerIndexHolder(r0.producerIndex, r0);
                        if (r0.restartTimerOnMaxSize) {
                            Worker worker = r0.worker;
                            long j = r0.timespan;
                            task = worker.schedulePeriodically(consumerIndexHolder, j, j, r0.unit);
                        } else {
                            Scheduler scheduler = r0.scheduler;
                            long j2 = r0.timespan;
                            task = scheduler.schedulePeriodicallyDirect(consumerIndexHolder, j2, j2, r0.unit);
                        }
                        if (r0.timer.replace(task)) {
                            subscription.request(Long.MAX_VALUE);
                        }
                    } else {
                        r0.cancelled = true;
                        s.cancel();
                        a.onError(new MissingBackpressureException("Could not deliver initial window due to lack of requests."));
                    }
                }
            }
        }

        public void onNext(T t) {
            if (!this.terminated) {
                if (fastEnter()) {
                    UnicastProcessor<T> w = r0.window;
                    w.onNext(t);
                    long c = r0.count + 1;
                    if (c >= r0.maxSize) {
                        r0.producerIndex++;
                        r0.count = 0;
                        w.onComplete();
                        long r = requested();
                        if (r != 0) {
                            w = UnicastProcessor.create(r0.bufferSize);
                            r0.window = w;
                            r0.downstream.onNext(w);
                            if (r != Long.MAX_VALUE) {
                                produced(1);
                            }
                            if (r0.restartTimerOnMaxSize) {
                                ((Disposable) r0.timer.get()).dispose();
                                Worker worker = r0.worker;
                                Runnable consumerIndexHolder = new ConsumerIndexHolder(r0.producerIndex, r0);
                                long j = r0.timespan;
                                r0.timer.replace(worker.schedulePeriodically(consumerIndexHolder, j, j, r0.unit));
                            }
                        } else {
                            r0.window = null;
                            r0.upstream.cancel();
                            r0.downstream.onError(new MissingBackpressureException("Could not deliver window due to lack of requests"));
                            dispose();
                            return;
                        }
                    }
                    r0.count = c;
                    if (leave(-1) == 0) {
                        return;
                    }
                } else {
                    T t2 = t;
                    r0.queue.offer(NotificationLite.next(t));
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
            dispose();
        }

        public void onComplete() {
            this.done = true;
            if (enter()) {
                drainLoop();
            }
            this.downstream.onComplete();
            dispose();
        }

        public void request(long n) {
            requested(n);
        }

        public void cancel() {
            this.cancelled = true;
        }

        public void dispose() {
            DisposableHelper.dispose(this.timer);
            Worker w = this.worker;
            if (w != null) {
                w.dispose();
            }
        }

        void drainLoop() {
            SimplePlainQueue<Object> q = this.queue;
            Subscriber<? super Flowable<T>> a = this.downstream;
            UnicastProcessor w = this.window;
            int missed = 1;
            while (!r0.terminated) {
                boolean d = r0.done;
                ConsumerIndexHolder o = q.poll();
                boolean empty = o == null;
                boolean isHolder = o instanceof ConsumerIndexHolder;
                if (d && (empty || isHolder)) {
                    r0.window = null;
                    q.clear();
                    Throwable err = r0.error;
                    if (err != null) {
                        w.onError(err);
                    } else {
                        w.onComplete();
                    }
                    dispose();
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
                    w = UnicastProcessor.create(r0.bufferSize);
                    r0.window = w;
                    long r = requested();
                    if (r != 0) {
                        a.onNext(w);
                        if (r != Long.MAX_VALUE) {
                            produced(1);
                        }
                    } else {
                        r0.window = null;
                        r0.queue.clear();
                        r0.upstream.cancel();
                        a.onError(new MissingBackpressureException("Could not deliver first window due to lack of requests."));
                        dispose();
                        return;
                    }
                } else {
                    SimplePlainQueue<Object> simplePlainQueue;
                    Subscriber<? super Flowable<T>> subscriber;
                    w.onNext(NotificationLite.getValue(o));
                    long c = r0.count + 1;
                    if (c >= r0.maxSize) {
                        r0.producerIndex++;
                        r0.count = 0;
                        w.onComplete();
                        long r2 = requested();
                        if (r2 != 0) {
                            UnicastProcessor<T> w2;
                            UnicastProcessor<T> w3 = UnicastProcessor.create(r0.bufferSize);
                            r0.window = w3;
                            r0.downstream.onNext(w3);
                            if (r2 != Long.MAX_VALUE) {
                                produced(1);
                            }
                            if (r0.restartTimerOnMaxSize) {
                                ((Disposable) r0.timer.get()).dispose();
                                Worker worker = r0.worker;
                                simplePlainQueue = q;
                                subscriber = a;
                                ConsumerIndexHolder consumerIndexHolder2 = new ConsumerIndexHolder(r0.producerIndex, r0);
                                q = r0.timespan;
                                w2 = w3;
                                r0.timer.replace(worker.schedulePeriodically(consumerIndexHolder2, q, q, r0.unit));
                            } else {
                                simplePlainQueue = q;
                                subscriber = a;
                                w2 = w3;
                            }
                            w = w2;
                        } else {
                            subscriber = a;
                            r0.window = null;
                            r0.upstream.cancel();
                            r0.downstream.onError(new MissingBackpressureException("Could not deliver window due to lack of requests"));
                            dispose();
                            return;
                        }
                    }
                    simplePlainQueue = q;
                    subscriber = a;
                    r0.count = c;
                    q = simplePlainQueue;
                    a = subscriber;
                }
            }
            r0.upstream.cancel();
            q.clear();
            dispose();
        }
    }

    static final class WindowExactUnboundedSubscriber<T> extends QueueDrainSubscriber<T, Object, Flowable<T>> implements FlowableSubscriber<T>, Subscription, Runnable {
        static final Object NEXT = new Object();
        final int bufferSize;
        final Scheduler scheduler;
        volatile boolean terminated;
        final SequentialDisposable timer = new SequentialDisposable();
        final long timespan;
        final TimeUnit unit;
        Subscription upstream;
        UnicastProcessor<T> window;

        WindowExactUnboundedSubscriber(Subscriber<? super Flowable<T>> actual, long timespan, TimeUnit unit, Scheduler scheduler, int bufferSize) {
            super(actual, new MpscLinkedQueue());
            this.timespan = timespan;
            this.unit = unit;
            this.scheduler = scheduler;
            this.bufferSize = bufferSize;
        }

        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.validate(this.upstream, s)) {
                this.upstream = s;
                this.window = UnicastProcessor.create(this.bufferSize);
                Subscriber<? super Flowable<T>> a = this.downstream;
                a.onSubscribe(this);
                long r = requested();
                if (r != 0) {
                    a.onNext(this.window);
                    if (r != Long.MAX_VALUE) {
                        produced(1);
                    }
                    if (!this.cancelled) {
                        SequentialDisposable sequentialDisposable = this.timer;
                        Scheduler scheduler = this.scheduler;
                        long j = this.timespan;
                        if (sequentialDisposable.replace(scheduler.schedulePeriodicallyDirect(this, j, j, this.unit))) {
                            s.request(Long.MAX_VALUE);
                        }
                    }
                } else {
                    this.cancelled = true;
                    s.cancel();
                    a.onError(new MissingBackpressureException("Could not deliver first window due to lack of requests."));
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
            this.downstream.onError(t);
            dispose();
        }

        public void onComplete() {
            this.done = true;
            if (enter()) {
                drainLoop();
            }
            this.downstream.onComplete();
            dispose();
        }

        public void request(long n) {
            requested(n);
        }

        public void cancel() {
            this.cancelled = true;
        }

        public void dispose() {
            DisposableHelper.dispose(this.timer);
        }

        public void run() {
            if (this.cancelled) {
                this.terminated = true;
                dispose();
            }
            this.queue.offer(NEXT);
            if (enter()) {
                drainLoop();
            }
        }

        void drainLoop() {
            SimplePlainQueue<Object> q = this.queue;
            Subscriber<? super Flowable<T>> a = this.downstream;
            UnicastProcessor<T> w = this.window;
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
                            this.upstream.cancel();
                        } else {
                            w = UnicastProcessor.create(this.bufferSize);
                            this.window = w;
                            long r = requested();
                            if (r != 0) {
                                a.onNext(w);
                                if (r != Long.MAX_VALUE) {
                                    produced(1);
                                }
                            } else {
                                this.window = null;
                                this.queue.clear();
                                this.upstream.cancel();
                                dispose();
                                a.onError(new MissingBackpressureException("Could not deliver first window due to lack of requests."));
                                return;
                            }
                        }
                    } else {
                        w.onNext(NotificationLite.getValue(o));
                    }
                }
            }
            this.window = null;
            q.clear();
            dispose();
            Throwable err = this.error;
            if (err != null) {
                w.onError(err);
            } else {
                w.onComplete();
            }
        }
    }

    static final class WindowSkipSubscriber<T> extends QueueDrainSubscriber<T, Object, Flowable<T>> implements Subscription, Runnable {
        final int bufferSize;
        volatile boolean terminated;
        final long timeskip;
        final long timespan;
        final TimeUnit unit;
        Subscription upstream;
        final List<UnicastProcessor<T>> windows = new LinkedList();
        final Worker worker;

        final class Completion implements Runnable {
            private final UnicastProcessor<T> processor;

            Completion(UnicastProcessor<T> processor) {
                this.processor = processor;
            }

            public void run() {
                WindowSkipSubscriber.this.complete(this.processor);
            }
        }

        static final class SubjectWork<T> {
            final boolean open;
            /* renamed from: w */
            final UnicastProcessor<T> f27w;

            SubjectWork(UnicastProcessor<T> w, boolean open) {
                this.f27w = w;
                this.open = open;
            }
        }

        WindowSkipSubscriber(Subscriber<? super Flowable<T>> actual, long timespan, long timeskip, TimeUnit unit, Worker worker, int bufferSize) {
            super(actual, new MpscLinkedQueue());
            this.timespan = timespan;
            this.timeskip = timeskip;
            this.unit = unit;
            this.worker = worker;
            this.bufferSize = bufferSize;
        }

        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.validate(this.upstream, s)) {
                this.upstream = s;
                this.downstream.onSubscribe(this);
                if (!this.cancelled) {
                    long r = requested();
                    if (r != 0) {
                        UnicastProcessor<T> w = UnicastProcessor.create(this.bufferSize);
                        this.windows.add(w);
                        this.downstream.onNext(w);
                        if (r != Long.MAX_VALUE) {
                            produced(1);
                        }
                        this.worker.schedule(new Completion(w), this.timespan, this.unit);
                        Worker worker = this.worker;
                        long j = this.timeskip;
                        worker.schedulePeriodically(this, j, j, this.unit);
                        s.request(Long.MAX_VALUE);
                    } else {
                        s.cancel();
                        this.downstream.onError(new MissingBackpressureException("Could not emit the first window due to lack of requests"));
                    }
                }
            }
        }

        public void onNext(T t) {
            if (fastEnter()) {
                for (UnicastProcessor<T> w : this.windows) {
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
            dispose();
        }

        public void onComplete() {
            this.done = true;
            if (enter()) {
                drainLoop();
            }
            this.downstream.onComplete();
            dispose();
        }

        public void request(long n) {
            requested(n);
        }

        public void cancel() {
            this.cancelled = true;
        }

        public void dispose() {
            this.worker.dispose();
        }

        void complete(UnicastProcessor<T> w) {
            this.queue.offer(new SubjectWork(w, false));
            if (enter()) {
                drainLoop();
            }
        }

        void drainLoop() {
            SimplePlainQueue<Object> q = this.queue;
            Subscriber<? super Flowable<T>> a = this.downstream;
            List<UnicastProcessor<T>> ws = this.windows;
            int missed = 1;
            while (!r0.terminated) {
                boolean d = r0.done;
                SubjectWork<T> v = q.poll();
                boolean empty = v == null;
                boolean sw = v instanceof SubjectWork;
                if (d && (empty || sw)) {
                    q.clear();
                    Throwable e = r0.error;
                    if (e != null) {
                        for (UnicastProcessor<T> w : ws) {
                            w.onError(e);
                        }
                    } else {
                        for (UnicastProcessor<T> w2 : ws) {
                            w2.onComplete();
                        }
                    }
                    ws.clear();
                    dispose();
                    return;
                } else if (empty) {
                    missed = leave(-missed);
                    if (missed == 0) {
                        return;
                    }
                } else {
                    SimplePlainQueue<Object> simplePlainQueue;
                    int missed2;
                    boolean z;
                    if (sw) {
                        SubjectWork<T> work = v;
                        if (!work.open) {
                            simplePlainQueue = q;
                            missed2 = missed;
                            z = d;
                            ws.remove(work.f27w);
                            work.f27w.onComplete();
                            if (ws.isEmpty() && r0.cancelled) {
                                r0.terminated = true;
                            }
                        } else if (r0.cancelled) {
                            simplePlainQueue = q;
                            missed2 = missed;
                            missed = missed2;
                            q = simplePlainQueue;
                        } else {
                            long r = requested();
                            if (r != 0) {
                                UnicastProcessor<T> w3 = UnicastProcessor.create(r0.bufferSize);
                                ws.add(w3);
                                a.onNext(w3);
                                if (r != Long.MAX_VALUE) {
                                    produced(1);
                                }
                                missed2 = missed;
                                simplePlainQueue = q;
                                r0.worker.schedule(new Completion(w3), r0.timespan, r0.unit);
                            } else {
                                simplePlainQueue = q;
                                missed2 = missed;
                                z = d;
                                a.onError(new MissingBackpressureException("Can't emit window due to lack of requests"));
                            }
                        }
                    } else {
                        simplePlainQueue = q;
                        missed2 = missed;
                        z = d;
                        for (UnicastProcessor<T> w4 : ws) {
                            w4.onNext(v);
                        }
                    }
                    missed = missed2;
                    q = simplePlainQueue;
                }
            }
            r0.upstream.cancel();
            dispose();
            q.clear();
            ws.clear();
        }

        public void run() {
            SubjectWork<T> sw = new SubjectWork(UnicastProcessor.create(this.bufferSize), true);
            if (!this.cancelled) {
                this.queue.offer(sw);
            }
            if (enter()) {
                drainLoop();
            }
        }
    }

    public FlowableWindowTimed(Flowable<T> source, long timespan, long timeskip, TimeUnit unit, Scheduler scheduler, long maxSize, int bufferSize, boolean restartTimerOnMaxSize) {
        super(source);
        this.timespan = timespan;
        this.timeskip = timeskip;
        this.unit = unit;
        this.scheduler = scheduler;
        this.maxSize = maxSize;
        this.bufferSize = bufferSize;
        this.restartTimerOnMaxSize = restartTimerOnMaxSize;
    }

    protected void subscribeActual(Subscriber<? super Flowable<T>> s) {
        SerializedSubscriber<Flowable<T>> actual = new SerializedSubscriber(s);
        if (this.timespan != this.timeskip) {
            this.source.subscribe(new WindowSkipSubscriber(actual, this.timespan, this.timeskip, this.unit, this.scheduler.createWorker(), this.bufferSize));
        } else if (this.maxSize == Long.MAX_VALUE) {
            this.source.subscribe(new WindowExactUnboundedSubscriber(actual, this.timespan, this.unit, this.scheduler, this.bufferSize));
        } else {
            this.source.subscribe(new WindowExactBoundedSubscriber(actual, this.timespan, this.unit, this.scheduler, this.bufferSize, this.maxSize, this.restartTimerOnMaxSize));
        }
    }
}
