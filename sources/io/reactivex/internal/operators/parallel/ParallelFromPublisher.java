package io.reactivex.internal.operators.parallel;

import io.reactivex.FlowableSubscriber;
import io.reactivex.exceptions.MissingBackpressureException;
import io.reactivex.internal.fuseable.QueueSubscription;
import io.reactivex.internal.fuseable.SimpleQueue;
import io.reactivex.internal.queue.SpscArrayQueue;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.BackpressureHelper;
import io.reactivex.parallel.ParallelFlowable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLongArray;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class ParallelFromPublisher<T> extends ParallelFlowable<T> {
    final int parallelism;
    final int prefetch;
    final Publisher<? extends T> source;

    static final class ParallelDispatcher<T> extends AtomicInteger implements FlowableSubscriber<T> {
        private static final long serialVersionUID = -4470634016609963609L;
        volatile boolean cancelled;
        volatile boolean done;
        final long[] emissions;
        Throwable error;
        int index;
        final int limit;
        final int prefetch;
        int produced;
        SimpleQueue<T> queue;
        final AtomicLongArray requests;
        int sourceMode;
        final AtomicInteger subscriberCount = new AtomicInteger();
        final Subscriber<? super T>[] subscribers;
        Subscription upstream;

        final class RailSubscription implements Subscription {
            /* renamed from: j */
            final int f49j;
            /* renamed from: m */
            final int f50m;

            RailSubscription(int j, int m) {
                this.f49j = j;
                this.f50m = m;
            }

            public void request(long n) {
                if (SubscriptionHelper.validate(n)) {
                    AtomicLongArray ra = ParallelDispatcher.this.requests;
                    while (true) {
                        long r = ra.get(this.f49j);
                        if (r != Long.MAX_VALUE) {
                            if (ra.compareAndSet(this.f49j, r, BackpressureHelper.addCap(r, n))) {
                                break;
                            }
                        } else {
                            return;
                        }
                    }
                    if (ParallelDispatcher.this.subscriberCount.get() == this.f50m) {
                        ParallelDispatcher.this.drain();
                    }
                }
            }

            public void cancel() {
                if (ParallelDispatcher.this.requests.compareAndSet(this.f49j + this.f50m, 0, 1)) {
                    ParallelDispatcher parallelDispatcher = ParallelDispatcher.this;
                    int i = this.f50m;
                    parallelDispatcher.cancel(i + i);
                }
            }
        }

        ParallelDispatcher(Subscriber<? super T>[] subscribers, int prefetch) {
            this.subscribers = subscribers;
            this.prefetch = prefetch;
            this.limit = prefetch - (prefetch >> 2);
            int m = subscribers.length;
            this.requests = new AtomicLongArray((m + m) + 1);
            this.requests.lazySet(m + m, (long) m);
            this.emissions = new long[m];
        }

        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.validate(this.upstream, s)) {
                this.upstream = s;
                if (s instanceof QueueSubscription) {
                    QueueSubscription<T> qs = (QueueSubscription) s;
                    int m = qs.requestFusion(7);
                    if (m == 1) {
                        this.sourceMode = m;
                        this.queue = qs;
                        this.done = true;
                        setupSubscribers();
                        drain();
                        return;
                    } else if (m == 2) {
                        this.sourceMode = m;
                        this.queue = qs;
                        setupSubscribers();
                        s.request((long) this.prefetch);
                        return;
                    }
                }
                this.queue = new SpscArrayQueue(this.prefetch);
                setupSubscribers();
                s.request((long) this.prefetch);
            }
        }

        void setupSubscribers() {
            Subscriber<? super T>[] subs = this.subscribers;
            int m = subs.length;
            for (int i = 0; i < m && !this.cancelled; i++) {
                this.subscriberCount.lazySet(i + 1);
                subs[i].onSubscribe(new RailSubscription(i, m));
            }
        }

        public void onNext(T t) {
            if (this.sourceMode == 0) {
                if (!this.queue.offer(t)) {
                    this.upstream.cancel();
                    onError(new MissingBackpressureException("Queue is full?"));
                    return;
                }
            }
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

        void cancel(int m) {
            if (this.requests.decrementAndGet(m) == 0) {
                this.cancelled = true;
                this.upstream.cancel();
                if (getAndIncrement() == 0) {
                    this.queue.clear();
                }
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        void drainAsync() {
            /*
            r23 = this;
            r1 = r23;
            r0 = 1;
            r2 = r1.queue;
            r3 = r1.subscribers;
            r4 = r1.requests;
            r5 = r1.emissions;
            r6 = r5.length;
            r7 = r1.index;
            r8 = r1.produced;
            r22 = r7;
            r7 = r0;
            r0 = r22;
        L_0x0015:
            r9 = 0;
            r10 = r9;
            r9 = r8;
            r8 = r0;
        L_0x0019:
            r0 = r1.cancelled;
            if (r0 == 0) goto L_0x0021;
        L_0x001d:
            r2.clear();
            return;
        L_0x0021:
            r11 = r1.done;
            r12 = 0;
            if (r11 == 0) goto L_0x003a;
        L_0x0026:
            r0 = r1.error;
            if (r0 == 0) goto L_0x0039;
        L_0x002a:
            r2.clear();
            r13 = r3.length;
        L_0x002e:
            if (r12 >= r13) goto L_0x0038;
        L_0x0030:
            r14 = r3[r12];
            r14.onError(r0);
            r12 = r12 + 1;
            goto L_0x002e;
        L_0x0038:
            return;
        L_0x0039:
            goto L_0x003b;
        L_0x003b:
            r13 = r2.isEmpty();
            if (r11 == 0) goto L_0x004f;
        L_0x0041:
            if (r13 == 0) goto L_0x004f;
        L_0x0043:
            r0 = r3.length;
        L_0x0044:
            if (r12 >= r0) goto L_0x004e;
        L_0x0046:
            r14 = r3[r12];
            r14.onComplete();
            r12 = r12 + 1;
            goto L_0x0044;
        L_0x004e:
            return;
            if (r13 == 0) goto L_0x0053;
        L_0x0052:
            goto L_0x0071;
        L_0x0053:
            r14 = r4.get(r8);
            r16 = r5[r8];
            r0 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1));
            if (r0 == 0) goto L_0x00ba;
        L_0x005d:
            r0 = r6 + r8;
            r18 = r4.get(r0);
            r20 = 0;
            r0 = (r18 > r20 ? 1 : (r18 == r20 ? 0 : -1));
            if (r0 != 0) goto L_0x00ba;
        L_0x0069:
            r0 = r2.poll();	 Catch:{ Throwable -> 0x009f }
            if (r0 != 0) goto L_0x0078;
        L_0x0071:
            r19 = r4;
            r20 = r5;
            r0 = r8;
            r8 = r9;
            goto L_0x00cc;
        L_0x0078:
            r12 = r3[r8];
            r12.onNext(r0);
            r18 = 1;
            r18 = r16 + r18;
            r5[r8] = r18;
            r9 = r9 + 1;
            r12 = r9;
            r18 = r0;
            r0 = r1.limit;
            if (r12 != r0) goto L_0x0098;
        L_0x008c:
            r9 = 0;
            r0 = r1.upstream;
            r19 = r4;
            r20 = r5;
            r4 = (long) r12;
            r0.request(r4);
            goto L_0x009c;
        L_0x0098:
            r19 = r4;
            r20 = r5;
        L_0x009c:
            r0 = 0;
            r10 = r0;
            goto L_0x00c0;
        L_0x009f:
            r0 = move-exception;
            r19 = r4;
            r20 = r5;
            r4 = r0;
            r0 = r4;
            io.reactivex.exceptions.Exceptions.throwIfFatal(r0);
            r4 = r1.upstream;
            r4.cancel();
            r4 = r3.length;
        L_0x00af:
            if (r12 >= r4) goto L_0x00b9;
        L_0x00b1:
            r5 = r3[r12];
            r5.onError(r0);
            r12 = r12 + 1;
            goto L_0x00af;
        L_0x00b9:
            return;
        L_0x00ba:
            r19 = r4;
            r20 = r5;
            r10 = r10 + 1;
        L_0x00c0:
            r8 = r8 + 1;
            if (r8 != r6) goto L_0x00c7;
        L_0x00c4:
            r0 = 0;
            r8 = r0;
            goto L_0x00c8;
        L_0x00c8:
            if (r10 != r6) goto L_0x00e9;
        L_0x00ca:
            r0 = r8;
            r8 = r9;
        L_0x00cc:
            r4 = r23.get();
            if (r4 != r7) goto L_0x00e1;
        L_0x00d2:
            r1.index = r0;
            r1.produced = r8;
            r5 = -r7;
            r5 = r1.addAndGet(r5);
            if (r5 != 0) goto L_0x00df;
            return;
        L_0x00df:
            r7 = r5;
            goto L_0x00e3;
        L_0x00e1:
            r5 = r4;
            r7 = r5;
        L_0x00e3:
            r4 = r19;
            r5 = r20;
            goto L_0x0015;
            r4 = r19;
            r5 = r20;
            goto L_0x0019;
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.parallel.ParallelFromPublisher.ParallelDispatcher.drainAsync():void");
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        void drainSync() {
            /*
            r21 = this;
            r1 = r21;
            r0 = 1;
            r2 = r1.queue;
            r3 = r1.subscribers;
            r4 = r1.requests;
            r5 = r1.emissions;
            r6 = r5.length;
            r7 = r1.index;
            r20 = r7;
            r7 = r0;
            r0 = r20;
        L_0x0013:
            r8 = 0;
            r9 = r8;
            r8 = r0;
        L_0x0016:
            r0 = r1.cancelled;
            if (r0 == 0) goto L_0x001e;
        L_0x001a:
            r2.clear();
            return;
        L_0x001e:
            r10 = r2.isEmpty();
            if (r10 == 0) goto L_0x0031;
        L_0x0024:
            r0 = r3.length;
            r11 = 0;
        L_0x0026:
            if (r11 >= r0) goto L_0x0030;
        L_0x0028:
            r12 = r3[r11];
            r12.onComplete();
            r11 = r11 + 1;
            goto L_0x0026;
        L_0x0030:
            return;
        L_0x0031:
            r12 = r4.get(r8);
            r14 = r5[r8];
            r0 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1));
            if (r0 == 0) goto L_0x008b;
        L_0x003b:
            r0 = r6 + r8;
            r16 = r4.get(r0);
            r18 = 0;
            r0 = (r16 > r18 ? 1 : (r16 == r18 ? 0 : -1));
            if (r0 != 0) goto L_0x008b;
        L_0x0047:
            r0 = r2.poll();	 Catch:{ Throwable -> 0x006d }
            if (r0 != 0) goto L_0x005d;
        L_0x004e:
            r11 = r3.length;
            r17 = r2;
            r2 = 0;
        L_0x0052:
            if (r2 >= r11) goto L_0x005c;
        L_0x0054:
            r16 = r3[r2];
            r16.onComplete();
            r2 = r2 + 1;
            goto L_0x0052;
        L_0x005c:
            return;
        L_0x005d:
            r17 = r2;
            r2 = r3[r8];
            r2.onNext(r0);
            r18 = 1;
            r18 = r14 + r18;
            r5[r8] = r18;
            r0 = 0;
            r9 = r0;
            goto L_0x008f;
        L_0x006d:
            r0 = move-exception;
            r17 = r2;
            r2 = r0;
            r0 = r2;
            io.reactivex.exceptions.Exceptions.throwIfFatal(r0);
            r2 = r1.upstream;
            r2.cancel();
            r2 = r3.length;
            r11 = 0;
        L_0x007c:
            if (r11 >= r2) goto L_0x008a;
        L_0x007e:
            r16 = r2;
            r2 = r3[r11];
            r2.onError(r0);
            r11 = r11 + 1;
            r2 = r16;
            goto L_0x007c;
        L_0x008a:
            return;
        L_0x008b:
            r17 = r2;
            r9 = r9 + 1;
        L_0x008f:
            r8 = r8 + 1;
            if (r8 != r6) goto L_0x0096;
        L_0x0093:
            r0 = 0;
            r8 = r0;
            goto L_0x0097;
        L_0x0097:
            if (r9 != r6) goto L_0x00b4;
            r0 = r21.get();
            if (r0 != r7) goto L_0x00ad;
        L_0x00a0:
            r1.index = r8;
            r2 = -r7;
            r2 = r1.addAndGet(r2);
            if (r2 != 0) goto L_0x00ab;
            return;
        L_0x00ab:
            r7 = r2;
            goto L_0x00af;
        L_0x00ad:
            r2 = r0;
            r7 = r2;
        L_0x00af:
            r0 = r8;
            r2 = r17;
            goto L_0x0013;
            r2 = r17;
            goto L_0x0016;
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.parallel.ParallelFromPublisher.ParallelDispatcher.drainSync():void");
        }

        void drain() {
            if (getAndIncrement() == 0) {
                if (this.sourceMode == 1) {
                    drainSync();
                } else {
                    drainAsync();
                }
            }
        }
    }

    public ParallelFromPublisher(Publisher<? extends T> source, int parallelism, int prefetch) {
        this.source = source;
        this.parallelism = parallelism;
        this.prefetch = prefetch;
    }

    public int parallelism() {
        return this.parallelism;
    }

    public void subscribe(Subscriber<? super T>[] subscribers) {
        if (validate(subscribers)) {
            this.source.subscribe(new ParallelDispatcher(subscribers, this.prefetch));
        }
    }
}
