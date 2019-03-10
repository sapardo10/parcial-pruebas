package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.flowables.ConnectableFlowable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.internal.disposables.ResettableConnectable;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.fuseable.HasUpstreamPublisher;
import io.reactivex.internal.subscribers.SubscriberResourceWrapper;
import io.reactivex.internal.subscriptions.EmptySubscription;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.BackpressureHelper;
import io.reactivex.internal.util.NotificationLite;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Timed;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FlowableReplay<T> extends ConnectableFlowable<T> implements HasUpstreamPublisher<T>, ResettableConnectable {
    static final Callable DEFAULT_UNBOUNDED_FACTORY = new DefaultUnboundedFactory();
    final Callable<? extends ReplayBuffer<T>> bufferFactory;
    final AtomicReference<ReplaySubscriber<T>> current;
    final Publisher<T> onSubscribe;
    final Flowable<T> source;

    static final class DefaultUnboundedFactory implements Callable<Object> {
        DefaultUnboundedFactory() {
        }

        public Object call() {
            return new UnboundedReplayBuffer(16);
        }
    }

    static final class Node extends AtomicReference<Node> {
        private static final long serialVersionUID = 245354315435971818L;
        final long index;
        final Object value;

        Node(Object value, long index) {
            this.value = value;
            this.index = index;
        }
    }

    interface ReplayBuffer<T> {
        void complete();

        void error(Throwable th);

        void next(T t);

        void replay(InnerSubscription<T> innerSubscription);
    }

    static final class ReplayBufferTask<T> implements Callable<ReplayBuffer<T>> {
        private final int bufferSize;

        ReplayBufferTask(int bufferSize) {
            this.bufferSize = bufferSize;
        }

        public ReplayBuffer<T> call() {
            return new SizeBoundReplayBuffer(this.bufferSize);
        }
    }

    static final class ScheduledReplayBufferTask<T> implements Callable<ReplayBuffer<T>> {
        private final int bufferSize;
        private final long maxAge;
        private final Scheduler scheduler;
        private final TimeUnit unit;

        ScheduledReplayBufferTask(int bufferSize, long maxAge, TimeUnit unit, Scheduler scheduler) {
            this.bufferSize = bufferSize;
            this.maxAge = maxAge;
            this.unit = unit;
            this.scheduler = scheduler;
        }

        public ReplayBuffer<T> call() {
            return new SizeAndTimeBoundReplayBuffer(this.bufferSize, this.maxAge, this.unit, this.scheduler);
        }
    }

    static class BoundedReplayBuffer<T> extends AtomicReference<Node> implements ReplayBuffer<T> {
        private static final long serialVersionUID = 2346567790059478686L;
        long index;
        int size;
        Node tail;

        public final void replay(io.reactivex.internal.operators.flowable.FlowableReplay.InnerSubscription<T> r14) {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:68:0x00ae in {6, 11, 14, 15, 18, 19, 29, 32, 33, 39, 40, 41, 42, 43, 48, 49, 50, 57, 60, 63, 67} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r13 = this;
            monitor-enter(r14);
            r0 = r14.emitting;	 Catch:{ all -> 0x00ab }
            r1 = 1;	 Catch:{ all -> 0x00ab }
            if (r0 == 0) goto L_0x000a;	 Catch:{ all -> 0x00ab }
        L_0x0006:
            r14.missed = r1;	 Catch:{ all -> 0x00ab }
            monitor-exit(r14);	 Catch:{ all -> 0x00ab }
            return;	 Catch:{ all -> 0x00ab }
        L_0x000a:
            r14.emitting = r1;	 Catch:{ all -> 0x00ab }
            monitor-exit(r14);	 Catch:{ all -> 0x00ab }
        L_0x000d:
            r0 = r14.isDisposed();
            if (r0 == 0) goto L_0x0014;
        L_0x0013:
            return;
        L_0x0014:
            r2 = r14.get();
            r4 = 9223372036854775807; // 0x7fffffffffffffff float:NaN double:NaN;
            r0 = 0;
            r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
            if (r6 != 0) goto L_0x0024;
        L_0x0022:
            r4 = 1;
            goto L_0x0025;
        L_0x0024:
            r4 = 0;
        L_0x0025:
            r5 = 0;
            r7 = r14.index();
            r7 = (io.reactivex.internal.operators.flowable.FlowableReplay.Node) r7;
            if (r7 != 0) goto L_0x003d;
        L_0x002f:
            r7 = r13.getHead();
            r14.index = r7;
            r8 = r14.totalRequested;
            r9 = r7.index;
            io.reactivex.internal.util.BackpressureHelper.add(r8, r9);
            goto L_0x003e;
        L_0x003e:
            r8 = 0;
            r10 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1));
            if (r10 == 0) goto L_0x008b;
        L_0x0044:
            r10 = r7.get();
            r10 = (io.reactivex.internal.operators.flowable.FlowableReplay.Node) r10;
            if (r10 == 0) goto L_0x008a;
        L_0x004c:
            r8 = r10.value;
            r8 = r13.leaveTransform(r8);
            r9 = 0;
            r11 = r14.child;	 Catch:{ Throwable -> 0x006d }
            r11 = io.reactivex.internal.util.NotificationLite.accept(r8, r11);	 Catch:{ Throwable -> 0x006d }
            if (r11 == 0) goto L_0x005e;	 Catch:{ Throwable -> 0x006d }
        L_0x005b:
            r14.index = r9;	 Catch:{ Throwable -> 0x006d }
            return;
            r11 = 1;
            r5 = r5 + r11;
            r2 = r2 - r11;
            r7 = r10;
            r8 = r14.isDisposed();
            if (r8 == 0) goto L_0x006b;
        L_0x006a:
            return;
            goto L_0x003e;
        L_0x006d:
            r0 = move-exception;
            io.reactivex.exceptions.Exceptions.throwIfFatal(r0);
            r14.index = r9;
            r14.dispose();
            r1 = io.reactivex.internal.util.NotificationLite.isError(r8);
            if (r1 != 0) goto L_0x0088;
        L_0x007c:
            r1 = io.reactivex.internal.util.NotificationLite.isComplete(r8);
            if (r1 != 0) goto L_0x0088;
        L_0x0082:
            r1 = r14.child;
            r1.onError(r0);
            goto L_0x0089;
        L_0x0089:
            return;
        L_0x008a:
            goto L_0x008c;
        L_0x008c:
            r10 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1));
            if (r10 == 0) goto L_0x0099;
        L_0x0090:
            r14.index = r7;
            if (r4 != 0) goto L_0x0098;
        L_0x0094:
            r14.produced(r5);
            goto L_0x009a;
        L_0x0098:
            goto L_0x009a;
        L_0x009a:
            monitor-enter(r14);
            r8 = r14.missed;	 Catch:{ all -> 0x00a8 }
            if (r8 != 0) goto L_0x00a3;	 Catch:{ all -> 0x00a8 }
        L_0x009f:
            r14.emitting = r0;	 Catch:{ all -> 0x00a8 }
            monitor-exit(r14);	 Catch:{ all -> 0x00a8 }
            return;	 Catch:{ all -> 0x00a8 }
        L_0x00a3:
            r14.missed = r0;	 Catch:{ all -> 0x00a8 }
            monitor-exit(r14);	 Catch:{ all -> 0x00a8 }
            goto L_0x000d;	 Catch:{ all -> 0x00a8 }
        L_0x00a8:
            r0 = move-exception;	 Catch:{ all -> 0x00a8 }
            monitor-exit(r14);	 Catch:{ all -> 0x00a8 }
            throw r0;
        L_0x00ab:
            r0 = move-exception;
            monitor-exit(r14);	 Catch:{ all -> 0x00ab }
            throw r0;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.flowable.FlowableReplay.BoundedReplayBuffer.replay(io.reactivex.internal.operators.flowable.FlowableReplay$InnerSubscription):void");
        }

        BoundedReplayBuffer() {
            Node n = new Node(null, 0);
            this.tail = n;
            set(n);
        }

        final void addLast(Node n) {
            this.tail.set(n);
            this.tail = n;
            this.size++;
        }

        final void removeFirst() {
            Node next = (Node) ((Node) get()).get();
            if (next != null) {
                this.size--;
                setFirst(next);
                return;
            }
            throw new IllegalStateException("Empty list!");
        }

        final void removeSome(int n) {
            Node head = (Node) get();
            while (n > 0) {
                head = (Node) head.get();
                n--;
                this.size--;
            }
            setFirst(head);
        }

        final void setFirst(Node n) {
            set(n);
        }

        public final void next(T value) {
            Object o = enterTransform(NotificationLite.next(value));
            long j = this.index + 1;
            this.index = j;
            addLast(new Node(o, j));
            truncate();
        }

        public final void error(Throwable e) {
            Object o = enterTransform(NotificationLite.error(e));
            long j = this.index + 1;
            this.index = j;
            addLast(new Node(o, j));
            truncateFinal();
        }

        public final void complete() {
            Object o = enterTransform(NotificationLite.complete());
            long j = this.index + 1;
            this.index = j;
            addLast(new Node(o, j));
            truncateFinal();
        }

        final void trimHead() {
            Node head = (Node) get();
            if (head.value != null) {
                Node n = new Node(null, 0);
                n.lazySet(head.get());
                set(n);
            }
        }

        Object enterTransform(Object value) {
            return value;
        }

        Object leaveTransform(Object value) {
            return value;
        }

        void truncate() {
        }

        void truncateFinal() {
            trimHead();
        }

        final void collect(Collection<? super T> output) {
            Node n = getHead();
            while (true) {
                Node next = (Node) n.get();
                if (next != null) {
                    Object v = leaveTransform(next.value);
                    if (!NotificationLite.isComplete(v)) {
                        if (!NotificationLite.isError(v)) {
                            output.add(NotificationLite.getValue(v));
                            n = next;
                        } else {
                            return;
                        }
                    }
                    return;
                }
                return;
            }
        }

        boolean hasError() {
            return this.tail.value != null && NotificationLite.isError(leaveTransform(this.tail.value));
        }

        boolean hasCompleted() {
            return this.tail.value != null && NotificationLite.isComplete(leaveTransform(this.tail.value));
        }

        Node getHead() {
            return (Node) get();
        }
    }

    static final class InnerSubscription<T> extends AtomicLong implements Subscription, Disposable {
        static final long CANCELLED = Long.MIN_VALUE;
        private static final long serialVersionUID = -4453897557930727610L;
        final Subscriber<? super T> child;
        boolean emitting;
        Object index;
        boolean missed;
        final ReplaySubscriber<T> parent;
        final AtomicLong totalRequested = new AtomicLong();

        InnerSubscription(ReplaySubscriber<T> parent, Subscriber<? super T> child) {
            this.parent = parent;
            this.child = child;
        }

        public void request(long n) {
            if (!SubscriptionHelper.validate(n)) {
                return;
            }
            if (BackpressureHelper.addCancel(this, n) != Long.MIN_VALUE) {
                BackpressureHelper.add(this.totalRequested, n);
                this.parent.manageRequests();
                this.parent.buffer.replay(this);
            }
        }

        public long produced(long n) {
            return BackpressureHelper.producedCancel(this, n);
        }

        public boolean isDisposed() {
            return get() == Long.MIN_VALUE;
        }

        public void cancel() {
            dispose();
        }

        public void dispose() {
            if (getAndSet(Long.MIN_VALUE) != Long.MIN_VALUE) {
                this.parent.remove(this);
                this.parent.manageRequests();
            }
        }

        <U> U index() {
            return this.index;
        }
    }

    static final class ReplayPublisher<T> implements Publisher<T> {
        private final Callable<? extends ReplayBuffer<T>> bufferFactory;
        private final AtomicReference<ReplaySubscriber<T>> curr;

        ReplayPublisher(AtomicReference<ReplaySubscriber<T>> curr, Callable<? extends ReplayBuffer<T>> bufferFactory) {
            this.curr = curr;
            this.bufferFactory = bufferFactory;
        }

        public void subscribe(Subscriber<? super T> child) {
            ReplaySubscriber<T> r;
            ReplaySubscriber<T> u;
            InnerSubscription<T> inner;
            while (true) {
                r = (ReplaySubscriber) this.curr.get();
                if (r != null) {
                    break;
                }
                try {
                    u = new ReplaySubscriber((ReplayBuffer) this.bufferFactory.call());
                    if (this.curr.compareAndSet(null, u)) {
                        break;
                    }
                } catch (Throwable ex) {
                    Exceptions.throwIfFatal(ex);
                    EmptySubscription.error(ex, child);
                    return;
                }
                inner = new InnerSubscription(r, child);
                child.onSubscribe(inner);
                r.add(inner);
                if (inner.isDisposed()) {
                    r.manageRequests();
                    r.buffer.replay(inner);
                    return;
                }
                r.remove(inner);
            }
            r = u;
            inner = new InnerSubscription(r, child);
            child.onSubscribe(inner);
            r.add(inner);
            if (inner.isDisposed()) {
                r.manageRequests();
                r.buffer.replay(inner);
                return;
            }
            r.remove(inner);
        }
    }

    static final class UnboundedReplayBuffer<T> extends ArrayList<Object> implements ReplayBuffer<T> {
        private static final long serialVersionUID = 7063189396499112664L;
        volatile int size;

        public void replay(io.reactivex.internal.operators.flowable.FlowableReplay.InnerSubscription<T> r15) {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:62:0x0097 in {6, 12, 15, 16, 25, 28, 29, 35, 36, 37, 42, 43, 44, 51, 54, 57, 61} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r14 = this;
            monitor-enter(r15);
            r0 = r15.emitting;	 Catch:{ all -> 0x0094 }
            r1 = 1;	 Catch:{ all -> 0x0094 }
            if (r0 == 0) goto L_0x000a;	 Catch:{ all -> 0x0094 }
        L_0x0006:
            r15.missed = r1;	 Catch:{ all -> 0x0094 }
            monitor-exit(r15);	 Catch:{ all -> 0x0094 }
            return;	 Catch:{ all -> 0x0094 }
        L_0x000a:
            r15.emitting = r1;	 Catch:{ all -> 0x0094 }
            monitor-exit(r15);	 Catch:{ all -> 0x0094 }
            r0 = r15.child;
        L_0x000f:
            r1 = r15.isDisposed();
            if (r1 == 0) goto L_0x0016;
        L_0x0015:
            return;
        L_0x0016:
            r1 = r14.size;
            r2 = r15.index();
            r2 = (java.lang.Integer) r2;
            r3 = 0;
            if (r2 == 0) goto L_0x0026;
        L_0x0021:
            r4 = r2.intValue();
            goto L_0x0027;
        L_0x0026:
            r4 = 0;
        L_0x0027:
            r5 = r15.get();
            r7 = r5;
            r9 = 0;
        L_0x002e:
            r11 = 0;
            r13 = (r5 > r11 ? 1 : (r5 == r11 ? 0 : -1));
            if (r13 == 0) goto L_0x0069;
        L_0x0034:
            if (r4 >= r1) goto L_0x0069;
        L_0x0036:
            r11 = r14.get(r4);
            r12 = io.reactivex.internal.util.NotificationLite.accept(r11, r0);	 Catch:{ Throwable -> 0x0050 }
            if (r12 == 0) goto L_0x0041;
        L_0x0040:
            return;
            r12 = r15.isDisposed();
            if (r12 == 0) goto L_0x0049;
        L_0x0048:
            return;
        L_0x0049:
            r4 = r4 + 1;
            r12 = 1;
            r5 = r5 - r12;
            r9 = r9 + r12;
            goto L_0x002e;
        L_0x0050:
            r3 = move-exception;
            io.reactivex.exceptions.Exceptions.throwIfFatal(r3);
            r15.dispose();
            r12 = io.reactivex.internal.util.NotificationLite.isError(r11);
            if (r12 != 0) goto L_0x0067;
        L_0x005d:
            r12 = io.reactivex.internal.util.NotificationLite.isComplete(r11);
            if (r12 != 0) goto L_0x0067;
        L_0x0063:
            r0.onError(r3);
            goto L_0x0068;
        L_0x0068:
            return;
            r13 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1));
            if (r13 == 0) goto L_0x0082;
        L_0x006e:
            r11 = java.lang.Integer.valueOf(r4);
            r15.index = r11;
            r11 = 9223372036854775807; // 0x7fffffffffffffff float:NaN double:NaN;
            r13 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1));
            if (r13 == 0) goto L_0x0081;
        L_0x007d:
            r15.produced(r9);
            goto L_0x0083;
        L_0x0081:
            goto L_0x0083;
        L_0x0083:
            monitor-enter(r15);
            r11 = r15.missed;	 Catch:{ all -> 0x0091 }
            if (r11 != 0) goto L_0x008c;	 Catch:{ all -> 0x0091 }
        L_0x0088:
            r15.emitting = r3;	 Catch:{ all -> 0x0091 }
            monitor-exit(r15);	 Catch:{ all -> 0x0091 }
            return;	 Catch:{ all -> 0x0091 }
        L_0x008c:
            r15.missed = r3;	 Catch:{ all -> 0x0091 }
            monitor-exit(r15);	 Catch:{ all -> 0x0091 }
            goto L_0x000f;	 Catch:{ all -> 0x0091 }
        L_0x0091:
            r3 = move-exception;	 Catch:{ all -> 0x0091 }
            monitor-exit(r15);	 Catch:{ all -> 0x0091 }
            throw r3;
        L_0x0094:
            r0 = move-exception;
            monitor-exit(r15);	 Catch:{ all -> 0x0094 }
            throw r0;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.flowable.FlowableReplay.UnboundedReplayBuffer.replay(io.reactivex.internal.operators.flowable.FlowableReplay$InnerSubscription):void");
        }

        UnboundedReplayBuffer(int capacityHint) {
            super(capacityHint);
        }

        public void next(T value) {
            add(NotificationLite.next(value));
            this.size++;
        }

        public void error(Throwable e) {
            add(NotificationLite.error(e));
            this.size++;
        }

        public void complete() {
            add(NotificationLite.complete());
            this.size++;
        }
    }

    static final class MulticastFlowable<R, U> extends Flowable<R> {
        private final Callable<? extends ConnectableFlowable<U>> connectableFactory;
        private final Function<? super Flowable<U>, ? extends Publisher<R>> selector;

        final class DisposableConsumer implements Consumer<Disposable> {
            private final SubscriberResourceWrapper<R> srw;

            DisposableConsumer(SubscriberResourceWrapper<R> srw) {
                this.srw = srw;
            }

            public void accept(Disposable r) {
                this.srw.setResource(r);
            }
        }

        MulticastFlowable(Callable<? extends ConnectableFlowable<U>> connectableFactory, Function<? super Flowable<U>, ? extends Publisher<R>> selector) {
            this.connectableFactory = connectableFactory;
            this.selector = selector;
        }

        protected void subscribeActual(Subscriber<? super R> child) {
            try {
                ConnectableFlowable<U> cf = (ConnectableFlowable) ObjectHelper.requireNonNull(this.connectableFactory.call(), "The connectableFactory returned null");
                try {
                    Publisher<R> observable = (Publisher) ObjectHelper.requireNonNull(this.selector.apply(cf), "The selector returned a null Publisher");
                    SubscriberResourceWrapper<R> srw = new SubscriberResourceWrapper(child);
                    observable.subscribe(srw);
                    cf.connect(new DisposableConsumer(srw));
                } catch (Throwable e) {
                    Exceptions.throwIfFatal(e);
                    EmptySubscription.error(e, child);
                }
            } catch (Throwable e2) {
                Exceptions.throwIfFatal(e2);
                EmptySubscription.error(e2, child);
            }
        }
    }

    static final class ReplaySubscriber<T> extends AtomicReference<Subscription> implements FlowableSubscriber<T>, Disposable {
        static final InnerSubscription[] EMPTY = new InnerSubscription[0];
        static final InnerSubscription[] TERMINATED = new InnerSubscription[0];
        private static final long serialVersionUID = 7224554242710036740L;
        final ReplayBuffer<T> buffer;
        boolean done;
        final AtomicInteger management = new AtomicInteger();
        long maxChildRequested;
        long maxUpstreamRequested;
        final AtomicBoolean shouldConnect = new AtomicBoolean();
        final AtomicReference<InnerSubscription<T>[]> subscribers = new AtomicReference(EMPTY);

        boolean add(io.reactivex.internal.operators.flowable.FlowableReplay.InnerSubscription<T> r5) {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:11:0x002c in {3, 7, 8, 10} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r4 = this;
            if (r5 == 0) goto L_0x0026;
        L_0x0002:
            r0 = r4.subscribers;
            r0 = r0.get();
            r0 = (io.reactivex.internal.operators.flowable.FlowableReplay.InnerSubscription[]) r0;
            r1 = TERMINATED;
            r2 = 0;
            if (r0 != r1) goto L_0x0010;
        L_0x000f:
            return r2;
        L_0x0010:
            r1 = r0.length;
            r3 = r1 + 1;
            r3 = new io.reactivex.internal.operators.flowable.FlowableReplay.InnerSubscription[r3];
            java.lang.System.arraycopy(r0, r2, r3, r2, r1);
            r3[r1] = r5;
            r2 = r4.subscribers;
            r2 = r2.compareAndSet(r0, r3);
            if (r2 == 0) goto L_0x0024;
        L_0x0022:
            r2 = 1;
            return r2;
            goto L_0x0002;
        L_0x0026:
            r0 = new java.lang.NullPointerException;
            r0.<init>();
            throw r0;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.flowable.FlowableReplay.ReplaySubscriber.add(io.reactivex.internal.operators.flowable.FlowableReplay$InnerSubscription):boolean");
        }

        ReplaySubscriber(ReplayBuffer<T> buffer) {
            this.buffer = buffer;
        }

        public boolean isDisposed() {
            return this.subscribers.get() == TERMINATED;
        }

        public void dispose() {
            this.subscribers.set(TERMINATED);
            SubscriptionHelper.cancel(this);
        }

        void remove(InnerSubscription<T> p) {
            while (true) {
                InnerSubscription[] c = (InnerSubscription[]) this.subscribers.get();
                int len = c.length;
                if (len != 0) {
                    int j = -1;
                    for (int i = 0; i < len; i++) {
                        if (c[i].equals(p)) {
                            j = i;
                            break;
                        }
                    }
                    if (j >= 0) {
                        InnerSubscription<T>[] u;
                        if (len == 1) {
                            u = EMPTY;
                        } else {
                            InnerSubscription<T>[] u2 = new InnerSubscription[(len - 1)];
                            System.arraycopy(c, 0, u2, 0, j);
                            System.arraycopy(c, j + 1, u2, j, (len - j) - 1);
                            u = u2;
                        }
                        if (this.subscribers.compareAndSet(c, u)) {
                            return;
                        }
                    } else {
                        return;
                    }
                }
                return;
            }
        }

        public void onSubscribe(Subscription p) {
            if (SubscriptionHelper.setOnce(this, p)) {
                manageRequests();
                for (InnerSubscription<T> rp : (InnerSubscription[]) this.subscribers.get()) {
                    this.buffer.replay(rp);
                }
            }
        }

        public void onNext(T t) {
            if (!this.done) {
                this.buffer.next(t);
                for (InnerSubscription<T> rp : (InnerSubscription[]) this.subscribers.get()) {
                    this.buffer.replay(rp);
                }
            }
        }

        public void onError(Throwable e) {
            if (this.done) {
                RxJavaPlugins.onError(e);
                return;
            }
            this.done = true;
            this.buffer.error(e);
            for (InnerSubscription<T> rp : (InnerSubscription[]) this.subscribers.getAndSet(TERMINATED)) {
                this.buffer.replay(rp);
            }
        }

        public void onComplete() {
            if (!this.done) {
                this.done = true;
                this.buffer.complete();
                for (InnerSubscription<T> rp : (InnerSubscription[]) this.subscribers.getAndSet(TERMINATED)) {
                    this.buffer.replay(rp);
                }
            }
        }

        void manageRequests() {
            if (this.management.getAndIncrement() == 0) {
                int missed = 1;
                while (!isDisposed()) {
                    InnerSubscription[] a = (InnerSubscription[]) r0.subscribers.get();
                    long ri = r0.maxChildRequested;
                    long maxTotalRequests = ri;
                    for (InnerSubscription<T> rp : a) {
                        maxTotalRequests = Math.max(maxTotalRequests, rp.totalRequested.get());
                    }
                    long ur = r0.maxUpstreamRequested;
                    Subscription p = (Subscription) get();
                    long diff = maxTotalRequests - ri;
                    if (diff != 0) {
                        r0.maxChildRequested = maxTotalRequests;
                        if (p == null) {
                            long u = ur + diff;
                            if (u < 0) {
                                u = Long.MAX_VALUE;
                            }
                            r0.maxUpstreamRequested = u;
                        } else if (ur != 0) {
                            r0.maxUpstreamRequested = 0;
                            p.request(ur + diff);
                        } else {
                            p.request(diff);
                        }
                    } else if (ur != 0 && p != null) {
                        r0.maxUpstreamRequested = 0;
                        p.request(ur);
                    }
                    missed = r0.management.addAndGet(-missed);
                    if (missed == 0) {
                        return;
                    }
                }
            }
        }
    }

    static final class SizeAndTimeBoundReplayBuffer<T> extends BoundedReplayBuffer<T> {
        private static final long serialVersionUID = 3457957419649567404L;
        final int limit;
        final long maxAge;
        final Scheduler scheduler;
        final TimeUnit unit;

        SizeAndTimeBoundReplayBuffer(int limit, long maxAge, TimeUnit unit, Scheduler scheduler) {
            this.scheduler = scheduler;
            this.limit = limit;
            this.maxAge = maxAge;
            this.unit = unit;
        }

        Object enterTransform(Object value) {
            return new Timed(value, this.scheduler.now(this.unit), this.unit);
        }

        Object leaveTransform(Object value) {
            return ((Timed) value).value();
        }

        void truncate() {
            long timeLimit = this.scheduler.now(this.unit) - this.maxAge;
            Node prev = (Node) get();
            Node next = (Node) prev.get();
            int e = 0;
            while (next != null) {
                if (this.size <= this.limit) {
                    if (next.value.time() > timeLimit) {
                        break;
                    }
                    e++;
                    this.size--;
                    prev = next;
                    next = (Node) next.get();
                } else {
                    e++;
                    this.size--;
                    prev = next;
                    next = (Node) next.get();
                }
            }
            if (e != 0) {
                setFirst(prev);
            }
        }

        void truncateFinal() {
            long timeLimit = this.scheduler.now(this.unit) - this.maxAge;
            Node prev = (Node) get();
            int e = 0;
            for (Node next = (Node) prev.get(); next != null && this.size > 1; next = (Node) next.get()) {
                if (next.value.time() > timeLimit) {
                    break;
                }
                e++;
                this.size--;
                prev = next;
            }
            if (e != 0) {
                setFirst(prev);
            }
        }

        Node getHead() {
            long timeLimit = this.scheduler.now(this.unit) - this.maxAge;
            Node prev = (Node) get();
            Node next = (Node) prev.get();
            while (next != null) {
                Timed<?> v = next.value;
                if (!NotificationLite.isComplete(v.value())) {
                    if (!NotificationLite.isError(v.value())) {
                        if (v.time() > timeLimit) {
                            break;
                        }
                        prev = next;
                        next = (Node) next.get();
                    } else {
                        break;
                    }
                }
                break;
            }
            return prev;
        }
    }

    static final class SizeBoundReplayBuffer<T> extends BoundedReplayBuffer<T> {
        private static final long serialVersionUID = -5898283885385201806L;
        final int limit;

        SizeBoundReplayBuffer(int limit) {
            this.limit = limit;
        }

        void truncate() {
            if (this.size > this.limit) {
                removeFirst();
            }
        }
    }

    static final class ConnectableFlowableReplay<T> extends ConnectableFlowable<T> {
        private final ConnectableFlowable<T> cf;
        private final Flowable<T> flowable;

        ConnectableFlowableReplay(ConnectableFlowable<T> cf, Flowable<T> flowable) {
            this.cf = cf;
            this.flowable = flowable;
        }

        public void connect(Consumer<? super Disposable> connection) {
            this.cf.connect(connection);
        }

        protected void subscribeActual(Subscriber<? super T> s) {
            this.flowable.subscribe(s);
        }
    }

    public void connect(io.reactivex.functions.Consumer<? super io.reactivex.disposables.Disposable> r7) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:35:0x0069 in {4, 5, 10, 11, 16, 17, 23, 24, 25, 28, 29, 31, 34} preds:[]
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
        r6 = this;
    L_0x0000:
        r0 = r6.current;
        r0 = r0.get();
        r0 = (io.reactivex.internal.operators.flowable.FlowableReplay.ReplaySubscriber) r0;
        if (r0 == 0) goto L_0x0012;
    L_0x000a:
        r1 = r0.isDisposed();
        if (r1 == 0) goto L_0x0011;
    L_0x0010:
        goto L_0x0012;
    L_0x0011:
        goto L_0x002a;
    L_0x0012:
        r1 = r6.bufferFactory;	 Catch:{ Throwable -> 0x0060 }
        r1 = r1.call();	 Catch:{ Throwable -> 0x0060 }
        r1 = (io.reactivex.internal.operators.flowable.FlowableReplay.ReplayBuffer) r1;	 Catch:{ Throwable -> 0x0060 }
        r2 = new io.reactivex.internal.operators.flowable.FlowableReplay$ReplaySubscriber;
        r2.<init>(r1);
        r3 = r6.current;
        r3 = r3.compareAndSet(r0, r2);
        if (r3 != 0) goto L_0x0029;
    L_0x0028:
        goto L_0x0000;
    L_0x0029:
        r0 = r2;
    L_0x002a:
        r1 = r0.shouldConnect;
        r1 = r1.get();
        r2 = 1;
        r3 = 0;
        if (r1 != 0) goto L_0x003e;
    L_0x0034:
        r1 = r0.shouldConnect;
        r1 = r1.compareAndSet(r3, r2);
        if (r1 == 0) goto L_0x003e;
    L_0x003c:
        r1 = 1;
        goto L_0x003f;
    L_0x003e:
        r1 = 0;
        r7.accept(r0);	 Catch:{ Throwable -> 0x004e }
        if (r1 == 0) goto L_0x004c;
    L_0x0046:
        r2 = r6.source;
        r2.subscribe(r0);
        goto L_0x004d;
    L_0x004d:
        return;
    L_0x004e:
        r4 = move-exception;
        if (r1 == 0) goto L_0x0057;
    L_0x0051:
        r5 = r0.shouldConnect;
        r5.compareAndSet(r2, r3);
        goto L_0x0058;
    L_0x0058:
        io.reactivex.exceptions.Exceptions.throwIfFatal(r4);
        r2 = io.reactivex.internal.util.ExceptionHelper.wrapOrThrow(r4);
        throw r2;
    L_0x0060:
        r1 = move-exception;
        io.reactivex.exceptions.Exceptions.throwIfFatal(r1);
        r2 = io.reactivex.internal.util.ExceptionHelper.wrapOrThrow(r1);
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.flowable.FlowableReplay.connect(io.reactivex.functions.Consumer):void");
    }

    public static <U, R> Flowable<R> multicastSelector(Callable<? extends ConnectableFlowable<U>> connectableFactory, Function<? super Flowable<U>, ? extends Publisher<R>> selector) {
        return new MulticastFlowable(connectableFactory, selector);
    }

    public static <T> ConnectableFlowable<T> observeOn(ConnectableFlowable<T> cf, Scheduler scheduler) {
        return RxJavaPlugins.onAssembly(new ConnectableFlowableReplay(cf, cf.observeOn(scheduler)));
    }

    public static <T> ConnectableFlowable<T> createFrom(Flowable<? extends T> source) {
        return create((Flowable) source, DEFAULT_UNBOUNDED_FACTORY);
    }

    public static <T> ConnectableFlowable<T> create(Flowable<T> source, int bufferSize) {
        if (bufferSize == Integer.MAX_VALUE) {
            return createFrom(source);
        }
        return create((Flowable) source, new ReplayBufferTask(bufferSize));
    }

    public static <T> ConnectableFlowable<T> create(Flowable<T> source, long maxAge, TimeUnit unit, Scheduler scheduler) {
        return create(source, maxAge, unit, scheduler, Integer.MAX_VALUE);
    }

    public static <T> ConnectableFlowable<T> create(Flowable<T> source, long maxAge, TimeUnit unit, Scheduler scheduler, int bufferSize) {
        return create((Flowable) source, new ScheduledReplayBufferTask(bufferSize, maxAge, unit, scheduler));
    }

    static <T> ConnectableFlowable<T> create(Flowable<T> source, Callable<? extends ReplayBuffer<T>> bufferFactory) {
        AtomicReference<ReplaySubscriber<T>> curr = new AtomicReference();
        return RxJavaPlugins.onAssembly(new FlowableReplay(new ReplayPublisher(curr, bufferFactory), source, curr, bufferFactory));
    }

    private FlowableReplay(Publisher<T> onSubscribe, Flowable<T> source, AtomicReference<ReplaySubscriber<T>> current, Callable<? extends ReplayBuffer<T>> bufferFactory) {
        this.onSubscribe = onSubscribe;
        this.source = source;
        this.current = current;
        this.bufferFactory = bufferFactory;
    }

    public Publisher<T> source() {
        return this.source;
    }

    protected void subscribeActual(Subscriber<? super T> s) {
        this.onSubscribe.subscribe(s);
    }

    public void resetIf(Disposable connectionObject) {
        this.current.compareAndSet((ReplaySubscriber) connectionObject, null);
    }
}
