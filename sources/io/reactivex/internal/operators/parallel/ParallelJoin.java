package io.reactivex.internal.operators.parallel;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.exceptions.MissingBackpressureException;
import io.reactivex.internal.fuseable.SimplePlainQueue;
import io.reactivex.internal.fuseable.SimpleQueue;
import io.reactivex.internal.queue.SpscArrayQueue;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.AtomicThrowable;
import io.reactivex.internal.util.BackpressureHelper;
import io.reactivex.parallel.ParallelFlowable;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class ParallelJoin<T> extends Flowable<T> {
    final boolean delayErrors;
    final int prefetch;
    final ParallelFlowable<? extends T> source;

    static abstract class JoinSubscriptionBase<T> extends AtomicInteger implements Subscription {
        private static final long serialVersionUID = 3100232009247827843L;
        volatile boolean cancelled;
        final AtomicInteger done = new AtomicInteger();
        final Subscriber<? super T> downstream;
        final AtomicThrowable errors = new AtomicThrowable();
        final AtomicLong requested = new AtomicLong();
        final JoinInnerSubscriber<T>[] subscribers;

        abstract void drain();

        abstract void onComplete();

        abstract void onError(Throwable th);

        abstract void onNext(JoinInnerSubscriber<T> joinInnerSubscriber, T t);

        JoinSubscriptionBase(Subscriber<? super T> actual, int n, int prefetch) {
            this.downstream = actual;
            JoinInnerSubscriber<T>[] a = new JoinInnerSubscriber[n];
            for (int i = 0; i < n; i++) {
                a[i] = new JoinInnerSubscriber(this, prefetch);
            }
            this.subscribers = a;
            this.done.lazySet(n);
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
                cancelAll();
                if (getAndIncrement() == 0) {
                    cleanup();
                }
            }
        }

        void cancelAll() {
            int i = 0;
            while (true) {
                JoinInnerSubscriber<T> s = this.subscribers;
                if (i < s.length) {
                    s[i].cancel();
                    i++;
                } else {
                    return;
                }
            }
        }

        void cleanup() {
            int i = 0;
            while (true) {
                JoinInnerSubscriber<T> s = this.subscribers;
                if (i < s.length) {
                    s[i].queue = null;
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    static final class JoinInnerSubscriber<T> extends AtomicReference<Subscription> implements FlowableSubscriber<T> {
        private static final long serialVersionUID = 8410034718427740355L;
        final int limit;
        final JoinSubscriptionBase<T> parent;
        final int prefetch;
        long produced;
        volatile SimplePlainQueue<T> queue;

        JoinInnerSubscriber(JoinSubscriptionBase<T> parent, int prefetch) {
            this.parent = parent;
            this.prefetch = prefetch;
            this.limit = prefetch - (prefetch >> 2);
        }

        public void onSubscribe(Subscription s) {
            SubscriptionHelper.setOnce(this, s, (long) this.prefetch);
        }

        public void onNext(T t) {
            this.parent.onNext(this, t);
        }

        public void onError(Throwable t) {
            this.parent.onError(t);
        }

        public void onComplete() {
            this.parent.onComplete();
        }

        public void requestOne() {
            long p = this.produced + 1;
            if (p == ((long) this.limit)) {
                this.produced = 0;
                ((Subscription) get()).request(p);
                return;
            }
            this.produced = p;
        }

        public void request(long n) {
            long p = this.produced + n;
            if (p >= ((long) this.limit)) {
                this.produced = 0;
                ((Subscription) get()).request(p);
                return;
            }
            this.produced = p;
        }

        public boolean cancel() {
            return SubscriptionHelper.cancel(this);
        }

        SimplePlainQueue<T> getQueue() {
            SimplePlainQueue<T> q = this.queue;
            if (q != null) {
                return q;
            }
            SpscArrayQueue q2 = new SpscArrayQueue(this.prefetch);
            this.queue = q2;
            return q2;
        }
    }

    static final class JoinSubscription<T> extends JoinSubscriptionBase<T> {
        private static final long serialVersionUID = 6312374661811000451L;

        JoinSubscription(Subscriber<? super T> actual, int n, int prefetch) {
            super(actual, n, prefetch);
        }

        public void onNext(JoinInnerSubscriber<T> inner, T value) {
            if (get() == 0 && compareAndSet(0, 1)) {
                if (this.requested.get() != 0) {
                    this.downstream.onNext(value);
                    if (this.requested.get() != Long.MAX_VALUE) {
                        this.requested.decrementAndGet();
                    }
                    inner.request(1);
                } else if (!inner.getQueue().offer(value)) {
                    cancelAll();
                    Throwable mbe = new MissingBackpressureException("Queue full?!");
                    if (this.errors.compareAndSet(null, mbe)) {
                        this.downstream.onError(mbe);
                    } else {
                        RxJavaPlugins.onError(mbe);
                    }
                    return;
                }
                if (decrementAndGet() == 0) {
                    return;
                }
            } else if (!inner.getQueue().offer(value)) {
                cancelAll();
                onError(new MissingBackpressureException("Queue full?!"));
                return;
            } else if (getAndIncrement() != 0) {
                return;
            }
            drainLoop();
        }

        public void onError(Throwable e) {
            if (this.errors.compareAndSet(null, e)) {
                cancelAll();
                drain();
            } else if (e != this.errors.get()) {
                RxJavaPlugins.onError(e);
            }
        }

        public void onComplete() {
            this.done.decrementAndGet();
            drain();
        }

        void drain() {
            if (getAndIncrement() == 0) {
                drainLoop();
            }
        }

        void drainLoop() {
            int missed = 1;
            JoinInnerSubscriber<T>[] s = this.subscribers;
            Subscriber<? super T> a = this.downstream;
            while (true) {
                long r = r0.requested.get();
                long e = 0;
                while (e != r) {
                    if (r0.cancelled) {
                        cleanup();
                        return;
                    }
                    Throwable ex = (Throwable) r0.errors.get();
                    if (ex != null) {
                        cleanup();
                        a.onError(ex);
                        return;
                    }
                    boolean d = r0.done.get() == 0;
                    boolean empty = true;
                    for (JoinInnerSubscriber<T> inner : s) {
                        SimplePlainQueue<T> q = inner.queue;
                        if (q != null) {
                            T v = q.poll();
                            if (v != null) {
                                empty = false;
                                a.onNext(v);
                                inner.requestOne();
                                long j = e + 1;
                                e = j;
                                if (j == r) {
                                    break;
                                }
                            }
                        }
                    }
                    if (d && empty) {
                        a.onComplete();
                        return;
                    } else if (empty) {
                        break;
                    }
                }
                if (e == r) {
                    if (r0.cancelled) {
                        cleanup();
                        return;
                    }
                    Throwable ex2 = (Throwable) r0.errors.get();
                    if (ex2 != null) {
                        cleanup();
                        a.onError(ex2);
                        return;
                    }
                    boolean d2 = r0.done.get() == 0;
                    boolean empty2 = true;
                    for (JoinInnerSubscriber<T> inner2 : s) {
                        SimpleQueue<T> q2 = inner2.queue;
                        if (q2 != null && !q2.isEmpty()) {
                            empty2 = false;
                            break;
                        }
                    }
                    if (d2 && empty) {
                        a.onComplete();
                        return;
                    }
                }
                if (e != 0 && r != Long.MAX_VALUE) {
                    r0.requested.addAndGet(-e);
                }
                int w = get();
                if (w == missed) {
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

    static final class JoinSubscriptionDelayError<T> extends JoinSubscriptionBase<T> {
        private static final long serialVersionUID = -5737965195918321883L;

        JoinSubscriptionDelayError(Subscriber<? super T> actual, int n, int prefetch) {
            super(actual, n, prefetch);
        }

        void onNext(JoinInnerSubscriber<T> inner, T value) {
            if (get() == 0 && compareAndSet(0, 1)) {
                if (this.requested.get() != 0) {
                    this.downstream.onNext(value);
                    if (this.requested.get() != Long.MAX_VALUE) {
                        this.requested.decrementAndGet();
                    }
                    inner.request(1);
                } else if (!inner.getQueue().offer(value)) {
                    inner.cancel();
                    this.errors.addThrowable(new MissingBackpressureException("Queue full?!"));
                    this.done.decrementAndGet();
                    drainLoop();
                    return;
                }
                if (decrementAndGet() == 0) {
                    return;
                }
            } else {
                if (!inner.getQueue().offer(value)) {
                    if (inner.cancel()) {
                        this.errors.addThrowable(new MissingBackpressureException("Queue full?!"));
                        this.done.decrementAndGet();
                    }
                }
                if (getAndIncrement() != 0) {
                    return;
                }
            }
            drainLoop();
        }

        void onError(Throwable e) {
            this.errors.addThrowable(e);
            this.done.decrementAndGet();
            drain();
        }

        void onComplete() {
            this.done.decrementAndGet();
            drain();
        }

        void drain() {
            if (getAndIncrement() == 0) {
                drainLoop();
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        void drainLoop() {
            /*
            r20 = this;
            r0 = r20;
            r1 = 1;
            r2 = r0.subscribers;
            r3 = r2.length;
            r4 = r0.downstream;
        L_0x0008:
            r5 = r0.requested;
            r5 = r5.get();
            r7 = 0;
        L_0x0010:
            r10 = 1;
            r11 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1));
            if (r11 == 0) goto L_0x0072;
        L_0x0015:
            r11 = r0.cancelled;
            if (r11 == 0) goto L_0x001d;
        L_0x0019:
            r20.cleanup();
            return;
        L_0x001d:
            r11 = r0.done;
            r11 = r11.get();
            if (r11 != 0) goto L_0x0027;
        L_0x0025:
            r11 = 1;
            goto L_0x0028;
        L_0x0027:
            r11 = 0;
        L_0x0028:
            r12 = 1;
            r13 = 0;
        L_0x002a:
            if (r13 >= r3) goto L_0x0050;
        L_0x002c:
            r14 = r2[r13];
            r15 = r14.queue;
            if (r15 == 0) goto L_0x004c;
        L_0x0032:
            r9 = r15.poll();
            if (r9 == 0) goto L_0x004b;
        L_0x0038:
            r12 = 0;
            r4.onNext(r9);
            r14.requestOne();
            r17 = 1;
            r17 = r7 + r17;
            r7 = r17;
            r19 = (r17 > r5 ? 1 : (r17 == r5 ? 0 : -1));
            if (r19 != 0) goto L_0x004a;
        L_0x0049:
            goto L_0x0073;
        L_0x004a:
            goto L_0x004d;
        L_0x004b:
            goto L_0x004d;
        L_0x004d:
            r13 = r13 + 1;
            goto L_0x002a;
        L_0x0050:
            if (r11 == 0) goto L_0x006c;
        L_0x0052:
            if (r12 == 0) goto L_0x006c;
        L_0x0054:
            r9 = r0.errors;
            r9 = r9.get();
            r9 = (java.lang.Throwable) r9;
            if (r9 == 0) goto L_0x0068;
        L_0x005e:
            r10 = r0.errors;
            r10 = r10.terminate();
            r4.onError(r10);
            goto L_0x006b;
        L_0x0068:
            r4.onComplete();
        L_0x006b:
            return;
            if (r12 == 0) goto L_0x0070;
        L_0x006f:
            goto L_0x0073;
            goto L_0x0010;
        L_0x0073:
            r9 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1));
            if (r9 != 0) goto L_0x00c1;
        L_0x0077:
            r9 = r0.cancelled;
            if (r9 == 0) goto L_0x007f;
        L_0x007b:
            r20.cleanup();
            return;
        L_0x007f:
            r9 = r0.done;
            r9 = r9.get();
            if (r9 != 0) goto L_0x008a;
        L_0x0087:
            r16 = 1;
            goto L_0x008c;
        L_0x008a:
            r16 = 0;
        L_0x008c:
            r9 = r16;
            r10 = 1;
            r11 = 0;
        L_0x0090:
            if (r11 >= r3) goto L_0x00a4;
        L_0x0092:
            r12 = r2[r11];
            r13 = r12.queue;
            if (r13 == 0) goto L_0x00a0;
        L_0x0098:
            r14 = r13.isEmpty();
            if (r14 != 0) goto L_0x00a0;
        L_0x009e:
            r10 = 0;
            goto L_0x00a4;
            r11 = r11 + 1;
            goto L_0x0090;
        L_0x00a4:
            if (r9 == 0) goto L_0x00c0;
        L_0x00a6:
            if (r10 == 0) goto L_0x00c0;
        L_0x00a8:
            r11 = r0.errors;
            r11 = r11.get();
            r11 = (java.lang.Throwable) r11;
            if (r11 == 0) goto L_0x00bc;
        L_0x00b2:
            r12 = r0.errors;
            r12 = r12.terminate();
            r4.onError(r12);
            goto L_0x00bf;
        L_0x00bc:
            r4.onComplete();
        L_0x00bf:
            return;
        L_0x00c0:
            goto L_0x00c2;
        L_0x00c2:
            r9 = 0;
            r11 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1));
            if (r11 == 0) goto L_0x00d8;
        L_0x00c8:
            r9 = 9223372036854775807; // 0x7fffffffffffffff float:NaN double:NaN;
            r11 = (r5 > r9 ? 1 : (r5 == r9 ? 0 : -1));
            if (r11 == 0) goto L_0x00d8;
        L_0x00d1:
            r9 = r0.requested;
            r10 = -r7;
            r9.addAndGet(r10);
            goto L_0x00d9;
        L_0x00d9:
            r9 = r20.get();
            if (r9 != r1) goto L_0x00e9;
        L_0x00df:
            r10 = -r1;
            r1 = r0.addAndGet(r10);
            if (r1 != 0) goto L_0x00e8;
            return;
        L_0x00e8:
            goto L_0x00ea;
        L_0x00e9:
            r1 = r9;
        L_0x00ea:
            goto L_0x0008;
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.parallel.ParallelJoin.JoinSubscriptionDelayError.drainLoop():void");
        }
    }

    public ParallelJoin(ParallelFlowable<? extends T> source, int prefetch, boolean delayErrors) {
        this.source = source;
        this.prefetch = prefetch;
        this.delayErrors = delayErrors;
    }

    protected void subscribeActual(Subscriber<? super T> s) {
        JoinSubscriptionBase<T> parent;
        if (this.delayErrors) {
            parent = new JoinSubscriptionDelayError(s, this.source.parallelism(), this.prefetch);
        } else {
            parent = new JoinSubscription(s, this.source.parallelism(), this.prefetch);
        }
        s.onSubscribe(parent);
        this.source.subscribe(parent.subscribers);
    }
}
