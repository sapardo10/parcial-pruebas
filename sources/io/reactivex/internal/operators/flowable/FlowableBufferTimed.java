package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.Scheduler.Worker;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.queue.MpscLinkedQueue;
import io.reactivex.internal.subscribers.QueueDrainSubscriber;
import io.reactivex.internal.subscriptions.EmptySubscription;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.QueueDrainHelper;
import io.reactivex.subscribers.SerializedSubscriber;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FlowableBufferTimed<T, U extends Collection<? super T>> extends AbstractFlowableWithUpstream<T, U> {
    final Callable<U> bufferSupplier;
    final int maxSize;
    final boolean restartTimerOnMaxSize;
    final Scheduler scheduler;
    final long timeskip;
    final long timespan;
    final TimeUnit unit;

    static final class BufferExactBoundedSubscriber<T, U extends Collection<? super T>> extends QueueDrainSubscriber<T, U, U> implements Subscription, Runnable, Disposable {
        U buffer;
        final Callable<U> bufferSupplier;
        long consumerIndex;
        final int maxSize;
        long producerIndex;
        final boolean restartTimerOnMaxSize;
        Disposable timer;
        final long timespan;
        final TimeUnit unit;
        Subscription upstream;
        /* renamed from: w */
        final Worker f69w;

        BufferExactBoundedSubscriber(Subscriber<? super U> actual, Callable<U> bufferSupplier, long timespan, TimeUnit unit, int maxSize, boolean restartOnMaxSize, Worker w) {
            super(actual, new MpscLinkedQueue());
            this.bufferSupplier = bufferSupplier;
            this.timespan = timespan;
            this.unit = unit;
            this.maxSize = maxSize;
            this.restartTimerOnMaxSize = restartOnMaxSize;
            this.f69w = w;
        }

        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.validate(this.upstream, s)) {
                this.upstream = s;
                try {
                    this.buffer = (Collection) ObjectHelper.requireNonNull(this.bufferSupplier.call(), "The supplied buffer is null");
                    this.downstream.onSubscribe(this);
                    Worker worker = this.f69w;
                    long j = this.timespan;
                    this.timer = worker.schedulePeriodically(this, j, j, this.unit);
                    s.request(Long.MAX_VALUE);
                } catch (Throwable e) {
                    Exceptions.throwIfFatal(e);
                    this.f69w.dispose();
                    s.cancel();
                    EmptySubscription.error(e, this.downstream);
                }
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onNext(T r10) {
            /*
            r9 = this;
            monitor-enter(r9);
            r0 = r9.buffer;	 Catch:{ all -> 0x006b }
            if (r0 != 0) goto L_0x0007;
        L_0x0005:
            monitor-exit(r9);	 Catch:{ all -> 0x006b }
            return;
        L_0x0007:
            r0.add(r10);	 Catch:{ all -> 0x006b }
            r1 = r0.size();	 Catch:{ all -> 0x006b }
            r2 = r9.maxSize;	 Catch:{ all -> 0x006b }
            if (r1 >= r2) goto L_0x0014;
        L_0x0012:
            monitor-exit(r9);	 Catch:{ all -> 0x006b }
            return;
        L_0x0014:
            r1 = 0;
            r9.buffer = r1;	 Catch:{ all -> 0x006b }
            r1 = r9.producerIndex;	 Catch:{ all -> 0x006b }
            r3 = 1;
            r1 = r1 + r3;
            r9.producerIndex = r1;	 Catch:{ all -> 0x006b }
            monitor-exit(r9);	 Catch:{ all -> 0x006b }
            r1 = r9.restartTimerOnMaxSize;
            if (r1 == 0) goto L_0x0029;
        L_0x0023:
            r1 = r9.timer;
            r1.dispose();
            goto L_0x002a;
        L_0x002a:
            r1 = 0;
            r9.fastPathOrderedEmitMax(r0, r1, r9);
            r1 = r9.bufferSupplier;	 Catch:{ Throwable -> 0x005e }
            r1 = r1.call();	 Catch:{ Throwable -> 0x005e }
            r2 = "The supplied buffer is null";
            r1 = io.reactivex.internal.functions.ObjectHelper.requireNonNull(r1, r2);	 Catch:{ Throwable -> 0x005e }
            r1 = (java.util.Collection) r1;	 Catch:{ Throwable -> 0x005e }
            monitor-enter(r9);
            r9.buffer = r1;	 Catch:{ all -> 0x005b }
            r5 = r9.consumerIndex;	 Catch:{ all -> 0x005b }
            r5 = r5 + r3;
            r9.consumerIndex = r5;	 Catch:{ all -> 0x005b }
            monitor-exit(r9);	 Catch:{ all -> 0x005b }
            r0 = r9.restartTimerOnMaxSize;
            if (r0 == 0) goto L_0x0059;
        L_0x004a:
            r2 = r9.f69w;
            r6 = r9.timespan;
            r8 = r9.unit;
            r3 = r9;
            r4 = r6;
            r0 = r2.schedulePeriodically(r3, r4, r6, r8);
            r9.timer = r0;
            goto L_0x005a;
        L_0x005a:
            return;
        L_0x005b:
            r0 = move-exception;
            monitor-exit(r9);	 Catch:{ all -> 0x005b }
            throw r0;
        L_0x005e:
            r1 = move-exception;
            io.reactivex.exceptions.Exceptions.throwIfFatal(r1);
            r9.cancel();
            r2 = r9.downstream;
            r2.onError(r1);
            return;
        L_0x006b:
            r0 = move-exception;
            monitor-exit(r9);	 Catch:{ all -> 0x006b }
            throw r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.flowable.FlowableBufferTimed.BufferExactBoundedSubscriber.onNext(java.lang.Object):void");
        }

        public void onError(Throwable t) {
            synchronized (this) {
                this.buffer = null;
            }
            this.downstream.onError(t);
            this.f69w.dispose();
        }

        public void onComplete() {
            U b;
            synchronized (this) {
                b = this.buffer;
                this.buffer = null;
            }
            this.queue.offer(b);
            this.done = true;
            if (enter()) {
                QueueDrainHelper.drainMaxLoop(this.queue, this.downstream, false, this, this);
            }
            this.f69w.dispose();
        }

        public boolean accept(Subscriber<? super U> a, U v) {
            a.onNext(v);
            return true;
        }

        public void request(long n) {
            requested(n);
        }

        public void cancel() {
            if (!this.cancelled) {
                this.cancelled = true;
                dispose();
            }
        }

        public void dispose() {
            synchronized (this) {
                this.buffer = null;
            }
            this.upstream.cancel();
            this.f69w.dispose();
        }

        public boolean isDisposed() {
            return this.f69w.isDisposed();
        }

        public void run() {
            try {
                Collection next = (Collection) ObjectHelper.requireNonNull(this.bufferSupplier.call(), "The supplied buffer is null");
                synchronized (this) {
                    U current = this.buffer;
                    if (current != null) {
                        if (this.producerIndex == this.consumerIndex) {
                            this.buffer = next;
                            fastPathOrderedEmitMax(current, false, this);
                            return;
                        }
                    }
                }
            } catch (Throwable e) {
                Exceptions.throwIfFatal(e);
                cancel();
                this.downstream.onError(e);
            }
        }
    }

    static final class BufferExactUnboundedSubscriber<T, U extends Collection<? super T>> extends QueueDrainSubscriber<T, U, U> implements Subscription, Runnable, Disposable {
        U buffer;
        final Callable<U> bufferSupplier;
        final Scheduler scheduler;
        final AtomicReference<Disposable> timer = new AtomicReference();
        final long timespan;
        final TimeUnit unit;
        Subscription upstream;

        BufferExactUnboundedSubscriber(Subscriber<? super U> actual, Callable<U> bufferSupplier, long timespan, TimeUnit unit, Scheduler scheduler) {
            super(actual, new MpscLinkedQueue());
            this.bufferSupplier = bufferSupplier;
            this.timespan = timespan;
            this.unit = unit;
            this.scheduler = scheduler;
        }

        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.validate(this.upstream, s)) {
                this.upstream = s;
                try {
                    this.buffer = (Collection) ObjectHelper.requireNonNull(this.bufferSupplier.call(), "The supplied buffer is null");
                    this.downstream.onSubscribe(this);
                    if (!this.cancelled) {
                        s.request(Long.MAX_VALUE);
                        Scheduler scheduler = this.scheduler;
                        long j = this.timespan;
                        Disposable d = scheduler.schedulePeriodicallyDirect(this, j, j, this.unit);
                        if (!this.timer.compareAndSet(null, d)) {
                            d.dispose();
                        }
                    }
                } catch (Throwable e) {
                    Exceptions.throwIfFatal(e);
                    cancel();
                    EmptySubscription.error(e, this.downstream);
                }
            }
        }

        public void onNext(T t) {
            synchronized (this) {
                U b = this.buffer;
                if (b != null) {
                    b.add(t);
                }
            }
        }

        public void onError(Throwable t) {
            DisposableHelper.dispose(this.timer);
            synchronized (this) {
                this.buffer = null;
            }
            this.downstream.onError(t);
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onComplete() {
            /*
            r5 = this;
            r0 = r5.timer;
            io.reactivex.internal.disposables.DisposableHelper.dispose(r0);
            monitor-enter(r5);
            r0 = r5.buffer;	 Catch:{ all -> 0x0029 }
            if (r0 != 0) goto L_0x000c;
        L_0x000a:
            monitor-exit(r5);	 Catch:{ all -> 0x0029 }
            return;
        L_0x000c:
            r1 = 0;
            r5.buffer = r1;	 Catch:{ all -> 0x0029 }
            monitor-exit(r5);	 Catch:{ all -> 0x0029 }
            r2 = r5.queue;
            r2.offer(r0);
            r2 = 1;
            r5.done = r2;
            r2 = r5.enter();
            if (r2 == 0) goto L_0x0027;
        L_0x001e:
            r2 = r5.queue;
            r3 = r5.downstream;
            r4 = 0;
            io.reactivex.internal.util.QueueDrainHelper.drainMaxLoop(r2, r3, r4, r1, r5);
            goto L_0x0028;
        L_0x0028:
            return;
        L_0x0029:
            r0 = move-exception;
            monitor-exit(r5);	 Catch:{ all -> 0x0029 }
            throw r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.flowable.FlowableBufferTimed.BufferExactUnboundedSubscriber.onComplete():void");
        }

        public void request(long n) {
            requested(n);
        }

        public void cancel() {
            this.cancelled = true;
            this.upstream.cancel();
            DisposableHelper.dispose(this.timer);
        }

        public void run() {
            try {
                Collection next = (Collection) ObjectHelper.requireNonNull(this.bufferSupplier.call(), "The supplied buffer is null");
                synchronized (this) {
                    U current = this.buffer;
                    if (current == null) {
                        return;
                    }
                    this.buffer = next;
                    fastPathEmitMax(current, false, this);
                }
            } catch (Throwable e) {
                Exceptions.throwIfFatal(e);
                cancel();
                this.downstream.onError(e);
            }
        }

        public boolean accept(Subscriber<? super U> subscriber, U v) {
            this.downstream.onNext(v);
            return true;
        }

        public void dispose() {
            cancel();
        }

        public boolean isDisposed() {
            return this.timer.get() == DisposableHelper.DISPOSED;
        }
    }

    static final class BufferSkipBoundedSubscriber<T, U extends Collection<? super T>> extends QueueDrainSubscriber<T, U, U> implements Subscription, Runnable {
        final Callable<U> bufferSupplier;
        final List<U> buffers = new LinkedList();
        final long timeskip;
        final long timespan;
        final TimeUnit unit;
        Subscription upstream;
        /* renamed from: w */
        final Worker f70w;

        final class RemoveFromBuffer implements Runnable {
            private final U buffer;

            RemoveFromBuffer(U buffer) {
                this.buffer = buffer;
            }

            public void run() {
                synchronized (BufferSkipBoundedSubscriber.this) {
                    BufferSkipBoundedSubscriber.this.buffers.remove(this.buffer);
                }
                BufferSkipBoundedSubscriber bufferSkipBoundedSubscriber = BufferSkipBoundedSubscriber.this;
                bufferSkipBoundedSubscriber.fastPathOrderedEmitMax(this.buffer, false, bufferSkipBoundedSubscriber.f70w);
            }
        }

        public void onComplete() {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:17:0x003d in {7, 10, 11, 12, 16} preds:[]
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
            r5 = this;
            monitor-enter(r5);
            r0 = new java.util.ArrayList;	 Catch:{ all -> 0x003a }
            r1 = r5.buffers;	 Catch:{ all -> 0x003a }
            r0.<init>(r1);	 Catch:{ all -> 0x003a }
            r1 = r5.buffers;	 Catch:{ all -> 0x003a }
            r1.clear();	 Catch:{ all -> 0x003a }
            monitor-exit(r5);	 Catch:{ all -> 0x003a }
            r1 = r0.iterator();
        L_0x0012:
            r2 = r1.hasNext();
            if (r2 == 0) goto L_0x0024;
        L_0x0018:
            r2 = r1.next();
            r2 = (java.util.Collection) r2;
            r3 = r5.queue;
            r3.offer(r2);
            goto L_0x0012;
        L_0x0024:
            r1 = 1;
            r5.done = r1;
            r1 = r5.enter();
            if (r1 == 0) goto L_0x0038;
        L_0x002d:
            r1 = r5.queue;
            r2 = r5.downstream;
            r3 = 0;
            r4 = r5.f70w;
            io.reactivex.internal.util.QueueDrainHelper.drainMaxLoop(r1, r2, r3, r4, r5);
            goto L_0x0039;
        L_0x0039:
            return;
        L_0x003a:
            r0 = move-exception;
            monitor-exit(r5);	 Catch:{ all -> 0x003a }
            throw r0;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.flowable.FlowableBufferTimed.BufferSkipBoundedSubscriber.onComplete():void");
        }

        public void onNext(T r3) {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:11:0x001c in {5, 7, 10} preds:[]
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
            r2 = this;
            monitor-enter(r2);
            r0 = r2.buffers;	 Catch:{ all -> 0x0019 }
            r0 = r0.iterator();	 Catch:{ all -> 0x0019 }
        L_0x0007:
            r1 = r0.hasNext();	 Catch:{ all -> 0x0019 }
            if (r1 == 0) goto L_0x0017;	 Catch:{ all -> 0x0019 }
        L_0x000d:
            r1 = r0.next();	 Catch:{ all -> 0x0019 }
            r1 = (java.util.Collection) r1;	 Catch:{ all -> 0x0019 }
            r1.add(r3);	 Catch:{ all -> 0x0019 }
            goto L_0x0007;	 Catch:{ all -> 0x0019 }
        L_0x0017:
            monitor-exit(r2);	 Catch:{ all -> 0x0019 }
            return;	 Catch:{ all -> 0x0019 }
        L_0x0019:
            r0 = move-exception;	 Catch:{ all -> 0x0019 }
            monitor-exit(r2);	 Catch:{ all -> 0x0019 }
            throw r0;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.flowable.FlowableBufferTimed.BufferSkipBoundedSubscriber.onNext(java.lang.Object):void");
        }

        BufferSkipBoundedSubscriber(Subscriber<? super U> actual, Callable<U> bufferSupplier, long timespan, long timeskip, TimeUnit unit, Worker w) {
            super(actual, new MpscLinkedQueue());
            this.bufferSupplier = bufferSupplier;
            this.timespan = timespan;
            this.timeskip = timeskip;
            this.unit = unit;
            this.f70w = w;
        }

        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.validate(this.upstream, s)) {
                this.upstream = s;
                try {
                    Collection b = (Collection) ObjectHelper.requireNonNull(this.bufferSupplier.call(), "The supplied buffer is null");
                    this.buffers.add(b);
                    this.downstream.onSubscribe(this);
                    s.request(Long.MAX_VALUE);
                    Worker worker = this.f70w;
                    long j = this.timeskip;
                    worker.schedulePeriodically(this, j, j, this.unit);
                    this.f70w.schedule(new RemoveFromBuffer(b), this.timespan, this.unit);
                } catch (Throwable e) {
                    Exceptions.throwIfFatal(e);
                    this.f70w.dispose();
                    s.cancel();
                    EmptySubscription.error(e, this.downstream);
                }
            }
        }

        public void onError(Throwable t) {
            this.done = true;
            this.f70w.dispose();
            clear();
            this.downstream.onError(t);
        }

        public void request(long n) {
            requested(n);
        }

        public void cancel() {
            this.cancelled = true;
            this.upstream.cancel();
            this.f70w.dispose();
            clear();
        }

        void clear() {
            synchronized (this) {
                this.buffers.clear();
            }
        }

        public void run() {
            if (!this.cancelled) {
                try {
                    Collection b = (Collection) ObjectHelper.requireNonNull(this.bufferSupplier.call(), "The supplied buffer is null");
                    synchronized (this) {
                        if (this.cancelled) {
                            return;
                        }
                        this.buffers.add(b);
                        this.f70w.schedule(new RemoveFromBuffer(b), this.timespan, this.unit);
                    }
                } catch (Throwable e) {
                    Exceptions.throwIfFatal(e);
                    cancel();
                    this.downstream.onError(e);
                }
            }
        }

        public boolean accept(Subscriber<? super U> a, U v) {
            a.onNext(v);
            return true;
        }
    }

    public FlowableBufferTimed(Flowable<T> source, long timespan, long timeskip, TimeUnit unit, Scheduler scheduler, Callable<U> bufferSupplier, int maxSize, boolean restartTimerOnMaxSize) {
        super(source);
        this.timespan = timespan;
        this.timeskip = timeskip;
        this.unit = unit;
        this.scheduler = scheduler;
        this.bufferSupplier = bufferSupplier;
        this.maxSize = maxSize;
        this.restartTimerOnMaxSize = restartTimerOnMaxSize;
    }

    protected void subscribeActual(Subscriber<? super U> s) {
        if (this.timespan == this.timeskip && this.maxSize == Integer.MAX_VALUE) {
            this.source.subscribe(new BufferExactUnboundedSubscriber(new SerializedSubscriber(s), this.bufferSupplier, this.timespan, this.unit, this.scheduler));
            return;
        }
        Worker w = this.scheduler.createWorker();
        if (this.timespan == this.timeskip) {
            this.source.subscribe(new BufferExactBoundedSubscriber(new SerializedSubscriber(s), this.bufferSupplier, this.timespan, this.unit, this.maxSize, this.restartTimerOnMaxSize, w));
        } else {
            this.source.subscribe(new BufferSkipBoundedSubscriber(new SerializedSubscriber(s), this.bufferSupplier, this.timespan, this.timeskip, this.unit, w));
        }
    }
}
