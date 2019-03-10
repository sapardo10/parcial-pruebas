package io.reactivex.processors;

import io.reactivex.Flowable;
import io.reactivex.annotations.BackpressureKind;
import io.reactivex.annotations.BackpressureSupport;
import io.reactivex.annotations.CheckReturnValue;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.SchedulerSupport;
import io.reactivex.exceptions.MissingBackpressureException;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.fuseable.QueueSubscription;
import io.reactivex.internal.fuseable.SimpleQueue;
import io.reactivex.internal.queue.SpscArrayQueue;
import io.reactivex.internal.queue.SpscLinkedArrayQueue;
import io.reactivex.internal.subscriptions.EmptySubscription;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

@BackpressureSupport(BackpressureKind.FULL)
@SchedulerSupport("none")
public final class MulticastProcessor<T> extends FlowableProcessor<T> {
    static final MulticastProcessor$MulticastSubscription[] EMPTY = new MulticastProcessor$MulticastSubscription[0];
    static final MulticastProcessor$MulticastSubscription[] TERMINATED = new MulticastProcessor$MulticastSubscription[0];
    final int bufferSize;
    int consumed;
    volatile boolean done;
    volatile Throwable error;
    int fusionMode;
    final int limit;
    final AtomicBoolean once;
    volatile SimpleQueue<T> queue;
    final boolean refcount;
    final AtomicReference<MulticastProcessor$MulticastSubscription<T>[]> subscribers = new AtomicReference(EMPTY);
    final AtomicReference<Subscription> upstream = new AtomicReference();
    final AtomicInteger wip = new AtomicInteger();

    @CheckReturnValue
    @NonNull
    public static <T> MulticastProcessor<T> create() {
        return new MulticastProcessor(Flowable.bufferSize(), false);
    }

    @CheckReturnValue
    @NonNull
    public static <T> MulticastProcessor<T> create(boolean refCount) {
        return new MulticastProcessor(Flowable.bufferSize(), refCount);
    }

    @CheckReturnValue
    @NonNull
    public static <T> MulticastProcessor<T> create(int bufferSize) {
        return new MulticastProcessor(bufferSize, false);
    }

    @CheckReturnValue
    @NonNull
    public static <T> MulticastProcessor<T> create(int bufferSize, boolean refCount) {
        return new MulticastProcessor(bufferSize, refCount);
    }

    MulticastProcessor(int bufferSize, boolean refCount) {
        ObjectHelper.verifyPositive(bufferSize, "bufferSize");
        this.bufferSize = bufferSize;
        this.limit = bufferSize - (bufferSize >> 2);
        this.refcount = refCount;
        this.once = new AtomicBoolean();
    }

    public void start() {
        if (SubscriptionHelper.setOnce(this.upstream, EmptySubscription.INSTANCE)) {
            this.queue = new SpscArrayQueue(this.bufferSize);
        }
    }

    public void startUnbounded() {
        if (SubscriptionHelper.setOnce(this.upstream, EmptySubscription.INSTANCE)) {
            this.queue = new SpscLinkedArrayQueue(this.bufferSize);
        }
    }

    public void onSubscribe(Subscription s) {
        if (SubscriptionHelper.setOnce(this.upstream, s)) {
            if (s instanceof QueueSubscription) {
                QueueSubscription<T> qs = (QueueSubscription) s;
                int m = qs.requestFusion(3);
                if (m == 1) {
                    this.fusionMode = m;
                    this.queue = qs;
                    this.done = true;
                    drain();
                    return;
                } else if (m == 2) {
                    this.fusionMode = m;
                    this.queue = qs;
                    s.request((long) this.bufferSize);
                    return;
                }
            }
            this.queue = new SpscArrayQueue(this.bufferSize);
            s.request((long) this.bufferSize);
        }
    }

    public void onNext(T t) {
        if (!this.once.get()) {
            if (this.fusionMode == 0) {
                ObjectHelper.requireNonNull(t, "onNext called with null. Null values are generally not allowed in 2.x operators and sources.");
                if (!this.queue.offer(t)) {
                    SubscriptionHelper.cancel(this.upstream);
                    onError(new MissingBackpressureException());
                    return;
                }
            }
            drain();
        }
    }

    public boolean offer(T t) {
        if (this.once.get()) {
            return false;
        }
        ObjectHelper.requireNonNull(t, "offer called with null. Null values are generally not allowed in 2.x operators and sources.");
        if (this.fusionMode == 0) {
            if (this.queue.offer(t)) {
                drain();
                return true;
            }
        }
        return false;
    }

    public void onError(Throwable t) {
        ObjectHelper.requireNonNull(t, "onError called with null. Null values are generally not allowed in 2.x operators and sources.");
        if (this.once.compareAndSet(false, true)) {
            this.error = t;
            this.done = true;
            drain();
            return;
        }
        RxJavaPlugins.onError(t);
    }

    public void onComplete() {
        if (this.once.compareAndSet(false, true)) {
            this.done = true;
            drain();
        }
    }

    public boolean hasSubscribers() {
        return ((MulticastProcessor$MulticastSubscription[]) this.subscribers.get()).length != 0;
    }

    public boolean hasThrowable() {
        return this.once.get() && this.error != null;
    }

    public boolean hasComplete() {
        return this.once.get() && this.error == null;
    }

    public Throwable getThrowable() {
        return this.once.get() ? this.error : null;
    }

    protected void subscribeActual(Subscriber<? super T> s) {
        MulticastProcessor$MulticastSubscription<T> ms = new MulticastProcessor$MulticastSubscription(s, this);
        s.onSubscribe(ms);
        if (!add(ms)) {
            if (!this.once.get()) {
                if (this.refcount) {
                    s.onComplete();
                }
            }
            Throwable ex = this.error;
            if (ex != null) {
                s.onError(ex);
                return;
            }
            s.onComplete();
        } else if (ms.get() == Long.MIN_VALUE) {
            remove(ms);
        } else {
            drain();
        }
    }

    boolean add(MulticastProcessor$MulticastSubscription<T> inner) {
        while (true) {
            MulticastProcessor$MulticastSubscription[] a = (MulticastProcessor$MulticastSubscription[]) this.subscribers.get();
            if (a == TERMINATED) {
                return false;
            }
            int n = a.length;
            MulticastProcessor$MulticastSubscription<T>[] b = new MulticastProcessor$MulticastSubscription[(n + 1)];
            System.arraycopy(a, 0, b, 0, n);
            b[n] = inner;
            if (this.subscribers.compareAndSet(a, b)) {
                return true;
            }
        }
    }

    void remove(MulticastProcessor$MulticastSubscription<T> inner) {
        while (true) {
            MulticastProcessor$MulticastSubscription[] a = (MulticastProcessor$MulticastSubscription[]) this.subscribers.get();
            int n = a.length;
            if (n != 0) {
                int j = -1;
                for (int i = 0; i < n; i++) {
                    if (a[i] == inner) {
                        j = i;
                        break;
                    }
                }
                if (j < 0) {
                    break;
                } else if (n != 1) {
                    MulticastProcessor$MulticastSubscription<T>[] b = new MulticastProcessor$MulticastSubscription[(n - 1)];
                    System.arraycopy(a, 0, b, 0, j);
                    System.arraycopy(a, j + 1, b, j, (n - j) - 1);
                    if (this.subscribers.compareAndSet(a, b)) {
                        break;
                    }
                } else if (this.refcount) {
                    if (this.subscribers.compareAndSet(a, TERMINATED)) {
                        break;
                    }
                } else if (this.subscribers.compareAndSet(a, EMPTY)) {
                    break;
                }
            }
            return;
        }
        SubscriptionHelper.cancel(this.upstream);
        this.once.set(true);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    void drain() {
        /*
        r23 = this;
        r1 = r23;
        r0 = r1.wip;
        r0 = r0.getAndIncrement();
        if (r0 == 0) goto L_0x000b;
    L_0x000a:
        return;
    L_0x000b:
        r0 = 1;
        r2 = r1.subscribers;
        r3 = r1.consumed;
        r4 = r1.limit;
        r5 = r1.fusionMode;
        r22 = r3;
        r3 = r0;
        r0 = r22;
    L_0x0019:
        r6 = r1.queue;
        if (r6 == 0) goto L_0x0165;
    L_0x001d:
        r7 = r2.get();
        r7 = (io.reactivex.processors.MulticastProcessor$MulticastSubscription[]) r7;
        r8 = r7.length;
        if (r8 == 0) goto L_0x0162;
    L_0x0026:
        r9 = -1;
        r11 = r7.length;
        r13 = r9;
        r9 = 0;
    L_0x002b:
        r15 = 0;
        if (r9 >= r11) goto L_0x005a;
    L_0x002f:
        r10 = r7[r9];
        r17 = r10.get();
        r19 = (r17 > r15 ? 1 : (r17 == r15 ? 0 : -1));
        if (r19 < 0) goto L_0x0055;
    L_0x0039:
        r15 = -1;
        r19 = (r13 > r15 ? 1 : (r13 == r15 ? 0 : -1));
        if (r19 != 0) goto L_0x0047;
    L_0x003f:
        r20 = r13;
        r12 = r10.emitted;
        r12 = r17 - r12;
        r13 = r12;
        goto L_0x0057;
    L_0x0047:
        r20 = r13;
        r12 = r10.emitted;
        r12 = r17 - r12;
        r14 = r20;
        r12 = java.lang.Math.min(r14, r12);
        r13 = r12;
        goto L_0x0057;
    L_0x0055:
        r14 = r13;
        r13 = r14;
    L_0x0057:
        r9 = r9 + 1;
        goto L_0x002b;
    L_0x005a:
        r9 = r13;
        r11 = r0;
    L_0x005c:
        r0 = (r9 > r15 ? 1 : (r9 == r15 ? 0 : -1));
        if (r0 <= 0) goto L_0x010a;
    L_0x0060:
        r0 = r2.get();
        r12 = r0;
        r12 = (io.reactivex.processors.MulticastProcessor$MulticastSubscription[]) r12;
        r0 = TERMINATED;
        if (r12 != r0) goto L_0x006f;
    L_0x006b:
        r6.clear();
        return;
    L_0x006f:
        if (r7 == r12) goto L_0x0073;
    L_0x0071:
        goto L_0x0123;
    L_0x0073:
        r13 = r1.done;
        r14 = 1;
        r0 = r6.poll();	 Catch:{ Throwable -> 0x007c }
        r15 = r0;
        goto L_0x008f;
    L_0x007c:
        r0 = move-exception;
        r17 = r0;
        r0 = r17;
        io.reactivex.exceptions.Exceptions.throwIfFatal(r0);
        r15 = r1.upstream;
        io.reactivex.internal.subscriptions.SubscriptionHelper.cancel(r15);
        r13 = 1;
        r15 = 0;
        r1.error = r0;
        r1.done = r14;
    L_0x008f:
        if (r15 != 0) goto L_0x0093;
    L_0x0091:
        r0 = 1;
        goto L_0x0094;
    L_0x0093:
        r0 = 0;
    L_0x0094:
        if (r13 == 0) goto L_0x00d6;
    L_0x0096:
        if (r0 == 0) goto L_0x00d6;
    L_0x0098:
        r14 = r1.error;
        if (r14 == 0) goto L_0x00bb;
    L_0x009c:
        r16 = r8;
        r8 = TERMINATED;
        r8 = r2.getAndSet(r8);
        r8 = (io.reactivex.processors.MulticastProcessor$MulticastSubscription[]) r8;
        r20 = r12;
        r12 = r8.length;
        r21 = r13;
        r13 = 0;
    L_0x00ac:
        if (r13 >= r12) goto L_0x00ba;
    L_0x00ae:
        r17 = r12;
        r12 = r8[r13];
        r12.onError(r14);
        r13 = r13 + 1;
        r12 = r17;
        goto L_0x00ac;
    L_0x00ba:
        goto L_0x00d5;
    L_0x00bb:
        r16 = r8;
        r20 = r12;
        r21 = r13;
        r8 = TERMINATED;
        r8 = r2.getAndSet(r8);
        r8 = (io.reactivex.processors.MulticastProcessor$MulticastSubscription[]) r8;
        r12 = r8.length;
        r13 = 0;
    L_0x00cb:
        if (r13 >= r12) goto L_0x00d5;
    L_0x00cd:
        r17 = r8[r13];
        r17.onComplete();
        r13 = r13 + 1;
        goto L_0x00cb;
    L_0x00d5:
        return;
    L_0x00d6:
        r16 = r8;
        r20 = r12;
        r21 = r13;
        if (r0 == 0) goto L_0x00df;
    L_0x00de:
        goto L_0x010c;
    L_0x00df:
        r8 = r7.length;
        r12 = 0;
    L_0x00e1:
        if (r12 >= r8) goto L_0x00eb;
    L_0x00e3:
        r13 = r7[r12];
        r13.onNext(r15);
        r12 = r12 + 1;
        goto L_0x00e1;
    L_0x00eb:
        r12 = 1;
        r9 = r9 - r12;
        if (r5 == r14) goto L_0x0103;
    L_0x00f0:
        r11 = r11 + 1;
        if (r11 != r4) goto L_0x0102;
    L_0x00f4:
        r11 = 0;
        r8 = r1.upstream;
        r8 = r8.get();
        r8 = (org.reactivestreams.Subscription) r8;
        r12 = (long) r4;
        r8.request(r12);
        goto L_0x0104;
    L_0x0102:
        goto L_0x0104;
    L_0x0104:
        r8 = r16;
        r15 = 0;
        goto L_0x005c;
    L_0x010a:
        r16 = r8;
    L_0x010c:
        r12 = 0;
        r0 = (r9 > r12 ? 1 : (r9 == r12 ? 0 : -1));
        if (r0 != 0) goto L_0x015f;
    L_0x0112:
        r0 = r2.get();
        r0 = (io.reactivex.processors.MulticastProcessor$MulticastSubscription[]) r0;
        r8 = TERMINATED;
        if (r0 != r8) goto L_0x0120;
    L_0x011c:
        r6.clear();
        return;
    L_0x0120:
        if (r7 == r0) goto L_0x0126;
    L_0x0123:
        r0 = r11;
        goto L_0x0019;
    L_0x0126:
        r8 = r1.done;
        if (r8 == 0) goto L_0x015e;
    L_0x012a:
        r8 = r6.isEmpty();
        if (r8 == 0) goto L_0x015e;
    L_0x0130:
        r8 = r1.error;
        if (r8 == 0) goto L_0x0149;
    L_0x0134:
        r12 = TERMINATED;
        r12 = r2.getAndSet(r12);
        r12 = (io.reactivex.processors.MulticastProcessor$MulticastSubscription[]) r12;
        r13 = r12.length;
        r14 = 0;
    L_0x013e:
        if (r14 >= r13) goto L_0x0148;
    L_0x0140:
        r15 = r12[r14];
        r15.onError(r8);
        r14 = r14 + 1;
        goto L_0x013e;
    L_0x0148:
        goto L_0x015d;
    L_0x0149:
        r12 = TERMINATED;
        r12 = r2.getAndSet(r12);
        r12 = (io.reactivex.processors.MulticastProcessor$MulticastSubscription[]) r12;
        r13 = r12.length;
        r14 = 0;
    L_0x0153:
        if (r14 >= r13) goto L_0x015d;
    L_0x0155:
        r15 = r12[r14];
        r15.onComplete();
        r14 = r14 + 1;
        goto L_0x0153;
    L_0x015d:
        return;
    L_0x015e:
        goto L_0x0160;
    L_0x0160:
        r0 = r11;
        goto L_0x0166;
    L_0x0162:
        r16 = r8;
        goto L_0x0166;
    L_0x0166:
        r7 = r1.wip;
        r8 = -r3;
        r3 = r7.addAndGet(r8);
        if (r3 != 0) goto L_0x0171;
        return;
        goto L_0x0019;
        */
        throw new UnsupportedOperationException("Method not decompiled: io.reactivex.processors.MulticastProcessor.drain():void");
    }
}
