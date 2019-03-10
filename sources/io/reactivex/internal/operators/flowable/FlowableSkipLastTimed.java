package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.Scheduler;
import io.reactivex.internal.queue.SpscLinkedArrayQueue;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.BackpressureHelper;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FlowableSkipLastTimed<T> extends AbstractFlowableWithUpstream<T, T> {
    final int bufferSize;
    final boolean delayError;
    final Scheduler scheduler;
    final long time;
    final TimeUnit unit;

    static final class SkipLastTimedSubscriber<T> extends AtomicInteger implements FlowableSubscriber<T>, Subscription {
        private static final long serialVersionUID = -5677354903406201275L;
        volatile boolean cancelled;
        final boolean delayError;
        volatile boolean done;
        final Subscriber<? super T> downstream;
        Throwable error;
        final SpscLinkedArrayQueue<Object> queue;
        final AtomicLong requested = new AtomicLong();
        final Scheduler scheduler;
        final long time;
        final TimeUnit unit;
        Subscription upstream;

        SkipLastTimedSubscriber(Subscriber<? super T> actual, long time, TimeUnit unit, Scheduler scheduler, int bufferSize, boolean delayError) {
            this.downstream = actual;
            this.time = time;
            this.unit = unit;
            this.scheduler = scheduler;
            this.queue = new SpscLinkedArrayQueue(bufferSize);
            this.delayError = delayError;
        }

        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.validate(this.upstream, s)) {
                this.upstream = s;
                this.downstream.onSubscribe(this);
                s.request(Long.MAX_VALUE);
            }
        }

        public void onNext(T t) {
            this.queue.offer(Long.valueOf(this.scheduler.now(this.unit)), t);
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

        public void request(long n) {
            if (SubscriptionHelper.validate(n)) {
                BackpressureHelper.add(this.requested, n);
                drain();
            }
        }

        public void cancel() {
            if (!this.cancelled) {
                this.cancelled = true;
                this.upstream.cancel();
                if (getAndIncrement() == 0) {
                    this.queue.clear();
                }
            }
        }

        void drain() {
            SkipLastTimedSubscriber skipLastTimedSubscriber = this;
            if (getAndIncrement() == 0) {
                int missed = 1;
                Subscriber<? super T> a = skipLastTimedSubscriber.downstream;
                SpscLinkedArrayQueue<Object> q = skipLastTimedSubscriber.queue;
                boolean delayError = skipLastTimedSubscriber.delayError;
                TimeUnit unit = skipLastTimedSubscriber.unit;
                Scheduler scheduler = skipLastTimedSubscriber.scheduler;
                long time = skipLastTimedSubscriber.time;
                while (true) {
                    boolean delayError2;
                    long r = skipLastTimedSubscriber.requested.get();
                    long e = 0;
                    while (e != r) {
                        boolean d = skipLastTimedSubscriber.done;
                        Long ts = (Long) q.peek();
                        boolean empty = ts == null;
                        long now = scheduler.now(unit);
                        if (!empty && ts.longValue() > now - time) {
                            empty = true;
                        }
                        if (!checkTerminated(d, empty, a, delayError)) {
                            if (empty) {
                                delayError2 = delayError;
                                break;
                            }
                            q.poll();
                            delayError2 = delayError;
                            a.onNext(q.poll());
                            e++;
                            delayError = delayError2;
                        } else {
                            return;
                        }
                    }
                    delayError2 = delayError;
                    if (e != 0) {
                        BackpressureHelper.produced(skipLastTimedSubscriber.requested, e);
                    }
                    missed = addAndGet(-missed);
                    if (missed != 0) {
                        delayError = delayError2;
                    } else {
                        return;
                    }
                }
            }
        }

        boolean checkTerminated(boolean d, boolean empty, Subscriber<? super T> a, boolean delayError) {
            if (this.cancelled) {
                this.queue.clear();
                return true;
            }
            if (d) {
                Throwable e;
                if (!delayError) {
                    e = this.error;
                    if (e != null) {
                        this.queue.clear();
                        a.onError(e);
                        return true;
                    } else if (empty) {
                        a.onComplete();
                        return true;
                    }
                } else if (empty) {
                    e = this.error;
                    if (e != null) {
                        a.onError(e);
                    } else {
                        a.onComplete();
                    }
                    return true;
                }
            }
            return false;
        }
    }

    public FlowableSkipLastTimed(Flowable<T> source, long time, TimeUnit unit, Scheduler scheduler, int bufferSize, boolean delayError) {
        super(source);
        this.time = time;
        this.unit = unit;
        this.scheduler = scheduler;
        this.bufferSize = bufferSize;
        this.delayError = delayError;
    }

    protected void subscribeActual(Subscriber<? super T> s) {
        this.source.subscribe(new SkipLastTimedSubscriber(s, this.time, this.unit, this.scheduler, this.bufferSize, this.delayError));
    }
}
