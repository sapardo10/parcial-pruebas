package io.reactivex.internal.operators.parallel;

import io.reactivex.FlowableSubscriber;
import io.reactivex.Scheduler;
import io.reactivex.Scheduler.Worker;
import io.reactivex.exceptions.MissingBackpressureException;
import io.reactivex.internal.fuseable.ConditionalSubscriber;
import io.reactivex.internal.queue.SpscArrayQueue;
import io.reactivex.internal.schedulers.SchedulerMultiWorkerSupport;
import io.reactivex.internal.schedulers.SchedulerMultiWorkerSupport.WorkerCallback;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.BackpressureHelper;
import io.reactivex.parallel.ParallelFlowable;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class ParallelRunOn<T> extends ParallelFlowable<T> {
    final int prefetch;
    final Scheduler scheduler;
    final ParallelFlowable<? extends T> source;

    final class MultiWorkerCallback implements WorkerCallback {
        final Subscriber<T>[] parents;
        final Subscriber<? super T>[] subscribers;

        MultiWorkerCallback(Subscriber<? super T>[] subscribers, Subscriber<T>[] parents) {
            this.subscribers = subscribers;
            this.parents = parents;
        }

        public void onWorker(int i, Worker w) {
            ParallelRunOn.this.createSubscriber(i, this.subscribers, this.parents, w);
        }
    }

    static abstract class BaseRunOnSubscriber<T> extends AtomicInteger implements FlowableSubscriber<T>, Subscription, Runnable {
        private static final long serialVersionUID = 9222303586456402150L;
        volatile boolean cancelled;
        int consumed;
        volatile boolean done;
        Throwable error;
        final int limit;
        final int prefetch;
        final SpscArrayQueue<T> queue;
        final AtomicLong requested = new AtomicLong();
        Subscription upstream;
        final Worker worker;

        BaseRunOnSubscriber(int prefetch, SpscArrayQueue<T> queue, Worker worker) {
            this.prefetch = prefetch;
            this.queue = queue;
            this.limit = prefetch - (prefetch >> 2);
            this.worker = worker;
        }

        public final void onNext(T t) {
            if (!this.done) {
                if (this.queue.offer(t)) {
                    schedule();
                    return;
                }
                this.upstream.cancel();
                onError(new MissingBackpressureException("Queue is full?!"));
            }
        }

        public final void onError(Throwable t) {
            if (this.done) {
                RxJavaPlugins.onError(t);
                return;
            }
            this.error = t;
            this.done = true;
            schedule();
        }

        public final void onComplete() {
            if (!this.done) {
                this.done = true;
                schedule();
            }
        }

        public final void request(long n) {
            if (SubscriptionHelper.validate(n)) {
                BackpressureHelper.add(this.requested, n);
                schedule();
            }
        }

        public final void cancel() {
            if (!this.cancelled) {
                this.cancelled = true;
                this.upstream.cancel();
                this.worker.dispose();
                if (getAndIncrement() == 0) {
                    this.queue.clear();
                }
            }
        }

        final void schedule() {
            if (getAndIncrement() == 0) {
                this.worker.schedule(this);
            }
        }
    }

    static final class RunOnConditionalSubscriber<T> extends BaseRunOnSubscriber<T> {
        private static final long serialVersionUID = 1075119423897941642L;
        final ConditionalSubscriber<? super T> downstream;

        RunOnConditionalSubscriber(ConditionalSubscriber<? super T> actual, int prefetch, SpscArrayQueue<T> queue, Worker worker) {
            super(prefetch, queue, worker);
            this.downstream = actual;
        }

        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.validate(this.upstream, s)) {
                this.upstream = s;
                this.downstream.onSubscribe(this);
                s.request((long) this.prefetch);
            }
        }

        public void run() {
            int missed = 1;
            int c = this.consumed;
            SpscArrayQueue<T> q = this.queue;
            ConditionalSubscriber<? super T> a = this.downstream;
            int lim = this.limit;
            while (true) {
                long r = r0.requested.get();
                long e = 0;
                while (e != r) {
                    if (r0.cancelled) {
                        q.clear();
                        return;
                    }
                    boolean d = r0.done;
                    if (d) {
                        Throwable ex = r0.error;
                        if (ex != null) {
                            q.clear();
                            a.onError(ex);
                            r0.worker.dispose();
                            return;
                        }
                    }
                    T v = q.poll();
                    boolean empty = v == null;
                    if (d && empty) {
                        a.onComplete();
                        r0.worker.dispose();
                        return;
                    } else if (empty) {
                        break;
                    } else {
                        long e2;
                        if (a.tryOnNext(v)) {
                            e++;
                        }
                        c++;
                        int p = c;
                        if (p == lim) {
                            c = 0;
                            e2 = e;
                            r0.upstream.request((long) p);
                        } else {
                            e2 = e;
                        }
                        e = e2;
                    }
                }
                if (e == r) {
                    if (r0.cancelled) {
                        q.clear();
                        return;
                    } else if (r0.done) {
                        Throwable ex2 = r0.error;
                        if (ex2 != null) {
                            q.clear();
                            a.onError(ex2);
                            r0.worker.dispose();
                            return;
                        } else if (q.isEmpty()) {
                            a.onComplete();
                            r0.worker.dispose();
                            return;
                        }
                    }
                }
                if (e != 0 && r != Long.MAX_VALUE) {
                    r0.requested.addAndGet(-e);
                }
                int w = get();
                if (w == missed) {
                    r0.consumed = c;
                    missed = addAndGet(-missed);
                    if (missed == 0) {
                        return;
                    }
                } else {
                    missed = w;
                }
            }
        }
    }

    static final class RunOnSubscriber<T> extends BaseRunOnSubscriber<T> {
        private static final long serialVersionUID = 1075119423897941642L;
        final Subscriber<? super T> downstream;

        RunOnSubscriber(Subscriber<? super T> actual, int prefetch, SpscArrayQueue<T> queue, Worker worker) {
            super(prefetch, queue, worker);
            this.downstream = actual;
        }

        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.validate(this.upstream, s)) {
                this.upstream = s;
                this.downstream.onSubscribe(this);
                s.request((long) this.prefetch);
            }
        }

        public void run() {
            int missed = 1;
            int c = this.consumed;
            SpscArrayQueue<T> q = this.queue;
            Subscriber<? super T> a = this.downstream;
            int lim = this.limit;
            while (true) {
                long r = r0.requested.get();
                long e = 0;
                while (e != r) {
                    if (r0.cancelled) {
                        q.clear();
                        return;
                    }
                    boolean d = r0.done;
                    if (d) {
                        Throwable ex = r0.error;
                        if (ex != null) {
                            q.clear();
                            a.onError(ex);
                            r0.worker.dispose();
                            return;
                        }
                    }
                    T v = q.poll();
                    boolean empty = v == null;
                    if (d && empty) {
                        a.onComplete();
                        r0.worker.dispose();
                        return;
                    } else if (empty) {
                        break;
                    } else {
                        long e2;
                        a.onNext(v);
                        e++;
                        c++;
                        int p = c;
                        if (p == lim) {
                            c = 0;
                            e2 = e;
                            r0.upstream.request((long) p);
                        } else {
                            e2 = e;
                        }
                        e = e2;
                    }
                }
                if (e == r) {
                    if (r0.cancelled) {
                        q.clear();
                        return;
                    } else if (r0.done) {
                        Throwable ex2 = r0.error;
                        if (ex2 != null) {
                            q.clear();
                            a.onError(ex2);
                            r0.worker.dispose();
                            return;
                        } else if (q.isEmpty()) {
                            a.onComplete();
                            r0.worker.dispose();
                            return;
                        }
                    }
                }
                if (e != 0 && r != Long.MAX_VALUE) {
                    r0.requested.addAndGet(-e);
                }
                int w = get();
                if (w == missed) {
                    r0.consumed = c;
                    missed = addAndGet(-missed);
                    if (missed == 0) {
                        return;
                    }
                } else {
                    missed = w;
                }
            }
        }
    }

    public ParallelRunOn(ParallelFlowable<? extends T> parent, Scheduler scheduler, int prefetch) {
        this.source = parent;
        this.scheduler = scheduler;
        this.prefetch = prefetch;
    }

    public void subscribe(Subscriber<? super T>[] subscribers) {
        if (validate(subscribers)) {
            int n = subscribers.length;
            Subscriber<T>[] parents = new Subscriber[n];
            SchedulerMultiWorkerSupport multiworker = this.scheduler;
            if (multiworker instanceof SchedulerMultiWorkerSupport) {
                multiworker.createWorkers(n, new MultiWorkerCallback(subscribers, parents));
            } else {
                for (int i = 0; i < n; i++) {
                    createSubscriber(i, subscribers, parents, this.scheduler.createWorker());
                }
            }
            this.source.subscribe(parents);
        }
    }

    void createSubscriber(int i, Subscriber<? super T>[] subscribers, Subscriber<T>[] parents, Worker worker) {
        Subscriber<? super T> a = subscribers[i];
        SpscArrayQueue<T> q = new SpscArrayQueue(this.prefetch);
        if (a instanceof ConditionalSubscriber) {
            parents[i] = new RunOnConditionalSubscriber((ConditionalSubscriber) a, this.prefetch, q, worker);
        } else {
            parents[i] = new RunOnSubscriber(a, this.prefetch, q, worker);
        }
    }

    public int parallelism() {
        return this.source.parallelism();
    }
}
