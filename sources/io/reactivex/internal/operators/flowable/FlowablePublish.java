package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.exceptions.MissingBackpressureException;
import io.reactivex.flowables.ConnectableFlowable;
import io.reactivex.internal.fuseable.HasUpstreamPublisher;
import io.reactivex.internal.fuseable.QueueSubscription;
import io.reactivex.internal.fuseable.SimpleQueue;
import io.reactivex.internal.queue.SpscArrayQueue;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.BackpressureHelper;
import io.reactivex.internal.util.NotificationLite;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FlowablePublish<T> extends ConnectableFlowable<T> implements HasUpstreamPublisher<T> {
    static final long CANCELLED = Long.MIN_VALUE;
    final int bufferSize;
    final AtomicReference<PublishSubscriber<T>> current;
    final Publisher<T> onSubscribe;
    final Flowable<T> source;

    static final class FlowablePublisher<T> implements Publisher<T> {
        private final int bufferSize;
        private final AtomicReference<PublishSubscriber<T>> curr;

        FlowablePublisher(AtomicReference<PublishSubscriber<T>> curr, int bufferSize) {
            this.curr = curr;
            this.bufferSize = bufferSize;
        }

        public void subscribe(Subscriber<? super T> child) {
            PublishSubscriber<T> r;
            InnerSubscriber<T> inner = new InnerSubscriber(child);
            child.onSubscribe(inner);
            while (true) {
                r = (PublishSubscriber) this.curr.get();
                if (r != null) {
                    if (!r.isDisposed()) {
                        if (r.add(inner)) {
                            break;
                        }
                    }
                }
                PublishSubscriber<T> u = new PublishSubscriber(this.curr, this.bufferSize);
                if (this.curr.compareAndSet(r, u)) {
                    r = u;
                    if (r.add(inner)) {
                        break;
                    }
                }
            }
            if (inner.get() == Long.MIN_VALUE) {
                r.remove(inner);
            } else {
                inner.parent = r;
            }
            r.dispatch();
        }
    }

    static final class InnerSubscriber<T> extends AtomicLong implements Subscription {
        private static final long serialVersionUID = -4453897557930727610L;
        final Subscriber<? super T> child;
        long emitted;
        volatile PublishSubscriber<T> parent;

        InnerSubscriber(Subscriber<? super T> child) {
            this.child = child;
        }

        public void request(long n) {
            if (SubscriptionHelper.validate(n)) {
                BackpressureHelper.addCancel(this, n);
                PublishSubscriber<T> p = this.parent;
                if (p != null) {
                    p.dispatch();
                }
            }
        }

        public void cancel() {
            if (get() == Long.MIN_VALUE) {
                return;
            }
            if (getAndSet(Long.MIN_VALUE) != Long.MIN_VALUE) {
                PublishSubscriber<T> p = this.parent;
                if (p != null) {
                    p.remove(this);
                    p.dispatch();
                }
            }
        }
    }

    static final class PublishSubscriber<T> extends AtomicInteger implements FlowableSubscriber<T>, Disposable {
        static final InnerSubscriber[] EMPTY = new InnerSubscriber[0];
        static final InnerSubscriber[] TERMINATED = new InnerSubscriber[0];
        private static final long serialVersionUID = -202316842419149694L;
        final int bufferSize;
        final AtomicReference<PublishSubscriber<T>> current;
        volatile SimpleQueue<T> queue;
        final AtomicBoolean shouldConnect;
        int sourceMode;
        final AtomicReference<InnerSubscriber<T>[]> subscribers = new AtomicReference(EMPTY);
        volatile Object terminalEvent;
        final AtomicReference<Subscription> upstream = new AtomicReference();

        PublishSubscriber(AtomicReference<PublishSubscriber<T>> current, int bufferSize) {
            this.current = current;
            this.shouldConnect = new AtomicBoolean();
            this.bufferSize = bufferSize;
        }

        public void dispose() {
            Object obj = this.subscribers.get();
            Object obj2 = TERMINATED;
            if (obj == obj2) {
                return;
            }
            if (((InnerSubscriber[]) this.subscribers.getAndSet(obj2)) != TERMINATED) {
                this.current.compareAndSet(this, null);
                SubscriptionHelper.cancel(this.upstream);
            }
        }

        public boolean isDisposed() {
            return this.subscribers.get() == TERMINATED;
        }

        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.setOnce(this.upstream, s)) {
                if (s instanceof QueueSubscription) {
                    QueueSubscription<T> qs = (QueueSubscription) s;
                    int m = qs.requestFusion(7);
                    if (m == 1) {
                        this.sourceMode = m;
                        this.queue = qs;
                        this.terminalEvent = NotificationLite.complete();
                        dispatch();
                        return;
                    } else if (m == 2) {
                        this.sourceMode = m;
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
            if (this.sourceMode != 0 || this.queue.offer(t)) {
                dispatch();
            } else {
                onError(new MissingBackpressureException("Prefetch queue is full?!"));
            }
        }

        public void onError(Throwable e) {
            if (this.terminalEvent == null) {
                this.terminalEvent = NotificationLite.error(e);
                dispatch();
                return;
            }
            RxJavaPlugins.onError(e);
        }

        public void onComplete() {
            if (this.terminalEvent == null) {
                this.terminalEvent = NotificationLite.complete();
                dispatch();
            }
        }

        boolean add(InnerSubscriber<T> producer) {
            while (true) {
                InnerSubscriber[] c = (InnerSubscriber[]) this.subscribers.get();
                if (c == TERMINATED) {
                    return false;
                }
                int len = c.length;
                InnerSubscriber<T>[] u = new InnerSubscriber[(len + 1)];
                System.arraycopy(c, 0, u, 0, len);
                u[len] = producer;
                if (this.subscribers.compareAndSet(c, u)) {
                    return true;
                }
            }
        }

        void remove(InnerSubscriber<T> producer) {
            while (true) {
                InnerSubscriber[] c = (InnerSubscriber[]) this.subscribers.get();
                int len = c.length;
                if (len == 0) {
                    break;
                }
                int j = -1;
                for (int i = 0; i < len; i++) {
                    if (c[i].equals(producer)) {
                        j = i;
                        break;
                    }
                }
                if (j >= 0) {
                    InnerSubscriber<T>[] u;
                    if (len == 1) {
                        u = EMPTY;
                    } else {
                        InnerSubscriber<T>[] u2 = new InnerSubscriber[(len - 1)];
                        System.arraycopy(c, 0, u2, 0, j);
                        System.arraycopy(c, j + 1, u2, j, (len - j) - 1);
                        u = u2;
                    }
                    if (this.subscribers.compareAndSet(c, u)) {
                        break;
                    }
                } else {
                    return;
                }
            }
        }

        boolean checkTerminated(Object term, boolean empty) {
            int i = 0;
            if (term != null) {
                if (!NotificationLite.isComplete(term)) {
                    Throwable t = NotificationLite.getError(term);
                    this.current.compareAndSet(this, null);
                    InnerSubscriber[] a = (InnerSubscriber[]) this.subscribers.getAndSet(TERMINATED);
                    if (a.length != 0) {
                        int length = a.length;
                        while (i < length) {
                            a[i].child.onError(t);
                            i++;
                        }
                    } else {
                        RxJavaPlugins.onError(t);
                    }
                    return true;
                } else if (empty) {
                    this.current.compareAndSet(this, null);
                    InnerSubscriber[] innerSubscriberArr = (InnerSubscriber[]) this.subscribers.getAndSet(TERMINATED);
                    int length2 = innerSubscriberArr.length;
                    while (i < length2) {
                        innerSubscriberArr[i].child.onComplete();
                        i++;
                    }
                    return true;
                }
            }
            return false;
        }

        void dispatch() {
            Throwable ex;
            PublishSubscriber publishSubscriber = this;
            if (getAndIncrement() == 0) {
                AtomicReference<InnerSubscriber<T>[]> subscribers = publishSubscriber.subscribers;
                InnerSubscriber<T>[] ps = (InnerSubscriber[]) subscribers.get();
                int missed = 1;
                while (true) {
                    boolean empty;
                    int len;
                    int cancelled;
                    long maxRequested;
                    int i;
                    int length;
                    long r;
                    Object term;
                    Object term2;
                    T v;
                    SimpleQueue<T> simpleQueue;
                    T value;
                    boolean subscribersChanged;
                    int subscribersChanged2;
                    T v2;
                    long ipr;
                    Object obj;
                    InnerSubscriber<T>[] term3;
                    long j;
                    Object term4 = publishSubscriber.terminalEvent;
                    SimpleQueue<T> q = publishSubscriber.queue;
                    if (q != null) {
                        if (!q.isEmpty()) {
                            empty = false;
                            if (checkTerminated(term4, empty)) {
                                if (empty) {
                                    len = ps.length;
                                    cancelled = 0;
                                    maxRequested = Long.MAX_VALUE;
                                    for (InnerSubscriber<T> ip : ps) {
                                        r = ip.get();
                                        if (r == Long.MIN_VALUE) {
                                            maxRequested = Math.min(maxRequested, r - ip.emitted);
                                        } else {
                                            cancelled++;
                                        }
                                    }
                                    if (len != cancelled) {
                                        term = publishSubscriber.terminalEvent;
                                        try {
                                            term4 = q.poll();
                                        } catch (Throwable ex2) {
                                            ex2 = ex2;
                                            Exceptions.throwIfFatal(ex2);
                                            ((Subscription) publishSubscriber.upstream.get()).cancel();
                                            term = NotificationLite.error(ex2);
                                            publishSubscriber.terminalEvent = term;
                                            term4 = null;
                                        }
                                        if (!checkTerminated(term, term4 != null)) {
                                            return;
                                        }
                                        if (publishSubscriber.sourceMode != 1) {
                                            ((Subscription) publishSubscriber.upstream.get()).request(1);
                                        }
                                    } else {
                                        i = 0;
                                        while (((long) i) < maxRequested) {
                                            term2 = publishSubscriber.terminalEvent;
                                            try {
                                                v = q.poll();
                                            } catch (Throwable ex22) {
                                                ex22 = ex22;
                                                Exceptions.throwIfFatal(ex22);
                                                ((Subscription) publishSubscriber.upstream.get()).cancel();
                                                term2 = NotificationLite.error(ex22);
                                                publishSubscriber.terminalEvent = term2;
                                                v = null;
                                            }
                                            empty = v != null;
                                            if (checkTerminated(term2, empty)) {
                                                if (!empty) {
                                                    simpleQueue = q;
                                                    v = term2;
                                                    break;
                                                }
                                                value = NotificationLite.getValue(v);
                                                length = ps.length;
                                                subscribersChanged = false;
                                                subscribersChanged2 = 0;
                                                while (subscribersChanged2 < length) {
                                                    v2 = v;
                                                    v = ps[subscribersChanged2];
                                                    ipr = v.get();
                                                    if (ipr == Long.MIN_VALUE) {
                                                        if (ipr == Long.MAX_VALUE) {
                                                            simpleQueue = q;
                                                            obj = term2;
                                                            v.emitted++;
                                                        } else {
                                                            simpleQueue = q;
                                                            obj = term2;
                                                        }
                                                        v.child.onNext(value);
                                                    } else {
                                                        simpleQueue = q;
                                                        obj = term2;
                                                        subscribersChanged = true;
                                                    }
                                                    subscribersChanged2++;
                                                    v = v2;
                                                    q = simpleQueue;
                                                    term2 = obj;
                                                }
                                                simpleQueue = q;
                                                obj = term2;
                                                i++;
                                                term3 = (InnerSubscriber[]) subscribers.get();
                                                if (subscribersChanged) {
                                                    if (term3 != ps) {
                                                        j = 1;
                                                        q = simpleQueue;
                                                    }
                                                }
                                                ps = term3;
                                                break;
                                            }
                                            return;
                                        }
                                        if (i > 0) {
                                            if (publishSubscriber.sourceMode != 1) {
                                                ((Subscription) publishSubscriber.upstream.get()).request((long) i);
                                            }
                                        }
                                        if (maxRequested != 0 || empty) {
                                        }
                                    }
                                }
                                missed = addAndGet(-missed);
                                if (missed == 0) {
                                    ps = (InnerSubscriber[]) subscribers.get();
                                } else {
                                    return;
                                }
                            }
                            return;
                        }
                    }
                    empty = true;
                    if (checkTerminated(term4, empty)) {
                        if (empty) {
                        } else {
                            len = ps.length;
                            cancelled = 0;
                            maxRequested = Long.MAX_VALUE;
                            for (InnerSubscriber<T> ip2 : ps) {
                                r = ip2.get();
                                if (r == Long.MIN_VALUE) {
                                    cancelled++;
                                } else {
                                    maxRequested = Math.min(maxRequested, r - ip2.emitted);
                                }
                            }
                            if (len != cancelled) {
                                i = 0;
                                while (((long) i) < maxRequested) {
                                    term2 = publishSubscriber.terminalEvent;
                                    v = q.poll();
                                    if (v != null) {
                                    }
                                    empty = v != null;
                                    if (checkTerminated(term2, empty)) {
                                        if (!empty) {
                                            value = NotificationLite.getValue(v);
                                            length = ps.length;
                                            subscribersChanged = false;
                                            subscribersChanged2 = 0;
                                            while (subscribersChanged2 < length) {
                                                v2 = v;
                                                v = ps[subscribersChanged2];
                                                ipr = v.get();
                                                if (ipr == Long.MIN_VALUE) {
                                                    simpleQueue = q;
                                                    obj = term2;
                                                    subscribersChanged = true;
                                                } else {
                                                    if (ipr == Long.MAX_VALUE) {
                                                        simpleQueue = q;
                                                        obj = term2;
                                                    } else {
                                                        simpleQueue = q;
                                                        obj = term2;
                                                        v.emitted++;
                                                    }
                                                    v.child.onNext(value);
                                                }
                                                subscribersChanged2++;
                                                v = v2;
                                                q = simpleQueue;
                                                term2 = obj;
                                            }
                                            simpleQueue = q;
                                            obj = term2;
                                            i++;
                                            term3 = (InnerSubscriber[]) subscribers.get();
                                            if (subscribersChanged) {
                                                if (term3 != ps) {
                                                    j = 1;
                                                    q = simpleQueue;
                                                }
                                            }
                                            ps = term3;
                                            break;
                                        }
                                        simpleQueue = q;
                                        v = term2;
                                        break;
                                    }
                                    return;
                                }
                                if (i > 0) {
                                    if (publishSubscriber.sourceMode != 1) {
                                        ((Subscription) publishSubscriber.upstream.get()).request((long) i);
                                    }
                                }
                                if (maxRequested != 0) {
                                }
                            } else {
                                term = publishSubscriber.terminalEvent;
                                term4 = q.poll();
                                if (term4 != null) {
                                }
                                if (!checkTerminated(term, term4 != null)) {
                                    if (publishSubscriber.sourceMode != 1) {
                                        ((Subscription) publishSubscriber.upstream.get()).request(1);
                                    }
                                } else {
                                    return;
                                }
                            }
                        }
                        missed = addAndGet(-missed);
                        if (missed == 0) {
                            ps = (InnerSubscriber[]) subscribers.get();
                        } else {
                            return;
                        }
                    }
                    return;
                }
            }
        }
    }

    public void connect(io.reactivex.functions.Consumer<? super io.reactivex.disposables.Disposable> r5) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:27:0x0052 in {4, 5, 8, 9, 14, 15, 21, 22, 23, 26} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r4 = this;
    L_0x0000:
        r0 = r4.current;
        r0 = r0.get();
        r0 = (io.reactivex.internal.operators.flowable.FlowablePublish.PublishSubscriber) r0;
        if (r0 == 0) goto L_0x0012;
    L_0x000a:
        r1 = r0.isDisposed();
        if (r1 == 0) goto L_0x0011;
    L_0x0010:
        goto L_0x0012;
    L_0x0011:
        goto L_0x0025;
    L_0x0012:
        r1 = new io.reactivex.internal.operators.flowable.FlowablePublish$PublishSubscriber;
        r2 = r4.current;
        r3 = r4.bufferSize;
        r1.<init>(r2, r3);
        r2 = r4.current;
        r2 = r2.compareAndSet(r0, r1);
        if (r2 != 0) goto L_0x0024;
    L_0x0023:
        goto L_0x0000;
    L_0x0024:
        r0 = r1;
    L_0x0025:
        r1 = r0.shouldConnect;
        r1 = r1.get();
        r2 = 1;
        r3 = 0;
        if (r1 != 0) goto L_0x0038;
    L_0x002f:
        r1 = r0.shouldConnect;
        r1 = r1.compareAndSet(r3, r2);
        if (r1 == 0) goto L_0x0038;
    L_0x0037:
        goto L_0x0039;
    L_0x0038:
        r2 = 0;
    L_0x0039:
        r1 = r2;
        r5.accept(r0);	 Catch:{ Throwable -> 0x0049 }
        if (r1 == 0) goto L_0x0047;
    L_0x0041:
        r2 = r4.source;
        r2.subscribe(r0);
        goto L_0x0048;
    L_0x0048:
        return;
    L_0x0049:
        r2 = move-exception;
        io.reactivex.exceptions.Exceptions.throwIfFatal(r2);
        r3 = io.reactivex.internal.util.ExceptionHelper.wrapOrThrow(r2);
        throw r3;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.flowable.FlowablePublish.connect(io.reactivex.functions.Consumer):void");
    }

    public static <T> ConnectableFlowable<T> create(Flowable<T> source, int bufferSize) {
        AtomicReference<PublishSubscriber<T>> curr = new AtomicReference();
        return RxJavaPlugins.onAssembly(new FlowablePublish(new FlowablePublisher(curr, bufferSize), source, curr, bufferSize));
    }

    private FlowablePublish(Publisher<T> onSubscribe, Flowable<T> source, AtomicReference<PublishSubscriber<T>> current, int bufferSize) {
        this.onSubscribe = onSubscribe;
        this.source = source;
        this.current = current;
        this.bufferSize = bufferSize;
    }

    public Publisher<T> source() {
        return this.source;
    }

    protected void subscribeActual(Subscriber<? super T> s) {
        this.onSubscribe.subscribe(s);
    }
}
