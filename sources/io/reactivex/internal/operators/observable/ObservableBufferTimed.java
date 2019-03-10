package io.reactivex.internal.operators.observable;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.Scheduler.Worker;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.observers.QueueDrainObserver;
import io.reactivex.internal.queue.MpscLinkedQueue;
import io.reactivex.internal.util.QueueDrainHelper;
import io.reactivex.observers.SerializedObserver;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public final class ObservableBufferTimed<T, U extends Collection<? super T>> extends AbstractObservableWithUpstream<T, U> {
    final Callable<U> bufferSupplier;
    final int maxSize;
    final boolean restartTimerOnMaxSize;
    final Scheduler scheduler;
    final long timeskip;
    final long timespan;
    final TimeUnit unit;

    static final class BufferExactBoundedObserver<T, U extends Collection<? super T>> extends QueueDrainObserver<T, U, U> implements Runnable, Disposable {
        U buffer;
        final Callable<U> bufferSupplier;
        long consumerIndex;
        final int maxSize;
        long producerIndex;
        final boolean restartTimerOnMaxSize;
        Disposable timer;
        final long timespan;
        final TimeUnit unit;
        Disposable upstream;
        /* renamed from: w */
        final Worker f66w;

        BufferExactBoundedObserver(Observer<? super U> actual, Callable<U> bufferSupplier, long timespan, TimeUnit unit, int maxSize, boolean restartOnMaxSize, Worker w) {
            super(actual, new MpscLinkedQueue());
            this.bufferSupplier = bufferSupplier;
            this.timespan = timespan;
            this.unit = unit;
            this.maxSize = maxSize;
            this.restartTimerOnMaxSize = restartOnMaxSize;
            this.f66w = w;
        }

        public void onSubscribe(Disposable d) {
            if (DisposableHelper.validate(this.upstream, d)) {
                this.upstream = d;
                try {
                    this.buffer = (Collection) ObjectHelper.requireNonNull(this.bufferSupplier.call(), "The buffer supplied is null");
                    this.downstream.onSubscribe(this);
                    Worker worker = this.f66w;
                    long j = this.timespan;
                    this.timer = worker.schedulePeriodically(this, j, j, this.unit);
                } catch (Throwable e) {
                    Exceptions.throwIfFatal(e);
                    d.dispose();
                    EmptyDisposable.error(e, this.downstream);
                    this.f66w.dispose();
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
            r9.fastPathOrderedEmit(r0, r1, r9);
            r1 = r9.bufferSupplier;	 Catch:{ Throwable -> 0x005e }
            r1 = r1.call();	 Catch:{ Throwable -> 0x005e }
            r2 = "The buffer supplied is null";
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
            r2 = r9.f66w;
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
            r2 = r9.downstream;
            r2.onError(r1);
            r9.dispose();
            return;
        L_0x006b:
            r0 = move-exception;
            monitor-exit(r9);	 Catch:{ all -> 0x006b }
            throw r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.observable.ObservableBufferTimed.BufferExactBoundedObserver.onNext(java.lang.Object):void");
        }

        public void onError(Throwable t) {
            synchronized (this) {
                this.buffer = null;
            }
            this.downstream.onError(t);
            this.f66w.dispose();
        }

        public void onComplete() {
            U b;
            this.f66w.dispose();
            synchronized (this) {
                b = this.buffer;
                this.buffer = null;
            }
            this.queue.offer(b);
            this.done = true;
            if (enter()) {
                QueueDrainHelper.drainLoop(this.queue, this.downstream, false, this, this);
            }
        }

        public void accept(Observer<? super U> a, U v) {
            a.onNext(v);
        }

        public void dispose() {
            if (!this.cancelled) {
                this.cancelled = true;
                this.upstream.dispose();
                this.f66w.dispose();
                synchronized (this) {
                    this.buffer = null;
                }
            }
        }

        public boolean isDisposed() {
            return this.cancelled;
        }

        public void run() {
            try {
                Collection next = (Collection) ObjectHelper.requireNonNull(this.bufferSupplier.call(), "The bufferSupplier returned a null buffer");
                synchronized (this) {
                    U current = this.buffer;
                    if (current != null) {
                        if (this.producerIndex == this.consumerIndex) {
                            this.buffer = next;
                            fastPathOrderedEmit(current, false, this);
                            return;
                        }
                    }
                }
            } catch (Throwable e) {
                Exceptions.throwIfFatal(e);
                dispose();
                this.downstream.onError(e);
            }
        }
    }

    static final class BufferExactUnboundedObserver<T, U extends Collection<? super T>> extends QueueDrainObserver<T, U, U> implements Runnable, Disposable {
        U buffer;
        final Callable<U> bufferSupplier;
        final Scheduler scheduler;
        final AtomicReference<Disposable> timer = new AtomicReference();
        final long timespan;
        final TimeUnit unit;
        Disposable upstream;

        BufferExactUnboundedObserver(Observer<? super U> actual, Callable<U> bufferSupplier, long timespan, TimeUnit unit, Scheduler scheduler) {
            super(actual, new MpscLinkedQueue());
            this.bufferSupplier = bufferSupplier;
            this.timespan = timespan;
            this.unit = unit;
            this.scheduler = scheduler;
        }

        public void onSubscribe(Disposable d) {
            if (DisposableHelper.validate(this.upstream, d)) {
                this.upstream = d;
                try {
                    this.buffer = (Collection) ObjectHelper.requireNonNull(this.bufferSupplier.call(), "The buffer supplied is null");
                    this.downstream.onSubscribe(this);
                    if (!this.cancelled) {
                        Scheduler scheduler = this.scheduler;
                        long j = this.timespan;
                        Disposable task = scheduler.schedulePeriodicallyDirect(this, j, j, this.unit);
                        if (!this.timer.compareAndSet(null, task)) {
                            task.dispose();
                        }
                    }
                } catch (Throwable e) {
                    Exceptions.throwIfFatal(e);
                    dispose();
                    EmptyDisposable.error(e, this.downstream);
                }
            }
        }

        public void onNext(T t) {
            synchronized (this) {
                U b = this.buffer;
                if (b == null) {
                    return;
                }
                b.add(t);
            }
        }

        public void onError(Throwable t) {
            synchronized (this) {
                this.buffer = null;
            }
            this.downstream.onError(t);
            DisposableHelper.dispose(this.timer);
        }

        public void onComplete() {
            synchronized (this) {
                U b = this.buffer;
                this.buffer = null;
            }
            if (b != null) {
                this.queue.offer(b);
                this.done = true;
                if (enter()) {
                    QueueDrainHelper.drainLoop(this.queue, this.downstream, false, null, this);
                }
            }
            DisposableHelper.dispose(this.timer);
        }

        public void dispose() {
            DisposableHelper.dispose(this.timer);
            this.upstream.dispose();
        }

        public boolean isDisposed() {
            return this.timer.get() == DisposableHelper.DISPOSED;
        }

        public void run() {
            try {
                U current;
                Collection next = (Collection) ObjectHelper.requireNonNull(this.bufferSupplier.call(), "The bufferSupplier returned a null buffer");
                synchronized (this) {
                    current = this.buffer;
                    if (current != null) {
                        this.buffer = next;
                    }
                }
                if (current == null) {
                    DisposableHelper.dispose(this.timer);
                } else {
                    fastPathEmit(current, false, this);
                }
            } catch (Throwable e) {
                Exceptions.throwIfFatal(e);
                this.downstream.onError(e);
                dispose();
            }
        }

        public void accept(Observer<? super U> observer, U v) {
            this.downstream.onNext(v);
        }
    }

    static final class BufferSkipBoundedObserver<T, U extends Collection<? super T>> extends QueueDrainObserver<T, U, U> implements Runnable, Disposable {
        final Callable<U> bufferSupplier;
        final List<U> buffers = new LinkedList();
        final long timeskip;
        final long timespan;
        final TimeUnit unit;
        Disposable upstream;
        /* renamed from: w */
        final Worker f67w;

        final class RemoveFromBuffer implements Runnable {
            /* renamed from: b */
            private final U f28b;

            RemoveFromBuffer(U b) {
                this.f28b = b;
            }

            public void run() {
                synchronized (BufferSkipBoundedObserver.this) {
                    BufferSkipBoundedObserver.this.buffers.remove(this.f28b);
                }
                BufferSkipBoundedObserver bufferSkipBoundedObserver = BufferSkipBoundedObserver.this;
                bufferSkipBoundedObserver.fastPathOrderedEmit(this.f28b, false, bufferSkipBoundedObserver.f67w);
            }
        }

        final class RemoveFromBufferEmit implements Runnable {
            private final U buffer;

            RemoveFromBufferEmit(U buffer) {
                this.buffer = buffer;
            }

            public void run() {
                synchronized (BufferSkipBoundedObserver.this) {
                    BufferSkipBoundedObserver.this.buffers.remove(this.buffer);
                }
                BufferSkipBoundedObserver bufferSkipBoundedObserver = BufferSkipBoundedObserver.this;
                bufferSkipBoundedObserver.fastPathOrderedEmit(this.buffer, false, bufferSkipBoundedObserver.f67w);
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
            r4 = r5.f67w;
            io.reactivex.internal.util.QueueDrainHelper.drainLoop(r1, r2, r3, r4, r5);
            goto L_0x0039;
        L_0x0039:
            return;
        L_0x003a:
            r0 = move-exception;
            monitor-exit(r5);	 Catch:{ all -> 0x003a }
            throw r0;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.observable.ObservableBufferTimed.BufferSkipBoundedObserver.onComplete():void");
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
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.observable.ObservableBufferTimed.BufferSkipBoundedObserver.onNext(java.lang.Object):void");
        }

        BufferSkipBoundedObserver(Observer<? super U> actual, Callable<U> bufferSupplier, long timespan, long timeskip, TimeUnit unit, Worker w) {
            super(actual, new MpscLinkedQueue());
            this.bufferSupplier = bufferSupplier;
            this.timespan = timespan;
            this.timeskip = timeskip;
            this.unit = unit;
            this.f67w = w;
        }

        public void onSubscribe(Disposable d) {
            if (DisposableHelper.validate(this.upstream, d)) {
                this.upstream = d;
                try {
                    Collection b = (Collection) ObjectHelper.requireNonNull(this.bufferSupplier.call(), "The buffer supplied is null");
                    this.buffers.add(b);
                    this.downstream.onSubscribe(this);
                    Worker worker = this.f67w;
                    long j = this.timeskip;
                    worker.schedulePeriodically(this, j, j, this.unit);
                    this.f67w.schedule(new RemoveFromBufferEmit(b), this.timespan, this.unit);
                } catch (Throwable e) {
                    Exceptions.throwIfFatal(e);
                    d.dispose();
                    EmptyDisposable.error(e, this.downstream);
                    this.f67w.dispose();
                }
            }
        }

        public void onError(Throwable t) {
            this.done = true;
            clear();
            this.downstream.onError(t);
            this.f67w.dispose();
        }

        public void dispose() {
            if (!this.cancelled) {
                this.cancelled = true;
                clear();
                this.upstream.dispose();
                this.f67w.dispose();
            }
        }

        public boolean isDisposed() {
            return this.cancelled;
        }

        void clear() {
            synchronized (this) {
                this.buffers.clear();
            }
        }

        public void run() {
            if (!this.cancelled) {
                try {
                    Collection b = (Collection) ObjectHelper.requireNonNull(this.bufferSupplier.call(), "The bufferSupplier returned a null buffer");
                    synchronized (this) {
                        if (this.cancelled) {
                            return;
                        }
                        this.buffers.add(b);
                        this.f67w.schedule(new RemoveFromBuffer(b), this.timespan, this.unit);
                    }
                } catch (Throwable e) {
                    Exceptions.throwIfFatal(e);
                    this.downstream.onError(e);
                    dispose();
                }
            }
        }

        public void accept(Observer<? super U> a, U v) {
            a.onNext(v);
        }
    }

    public ObservableBufferTimed(ObservableSource<T> source, long timespan, long timeskip, TimeUnit unit, Scheduler scheduler, Callable<U> bufferSupplier, int maxSize, boolean restartTimerOnMaxSize) {
        super(source);
        this.timespan = timespan;
        this.timeskip = timeskip;
        this.unit = unit;
        this.scheduler = scheduler;
        this.bufferSupplier = bufferSupplier;
        this.maxSize = maxSize;
        this.restartTimerOnMaxSize = restartTimerOnMaxSize;
    }

    protected void subscribeActual(Observer<? super U> t) {
        if (this.timespan == this.timeskip && this.maxSize == Integer.MAX_VALUE) {
            this.source.subscribe(new BufferExactUnboundedObserver(new SerializedObserver(t), this.bufferSupplier, this.timespan, this.unit, this.scheduler));
            return;
        }
        Worker w = this.scheduler.createWorker();
        if (this.timespan == this.timeskip) {
            this.source.subscribe(new BufferExactBoundedObserver(new SerializedObserver(t), this.bufferSupplier, this.timespan, this.unit, this.maxSize, this.restartTimerOnMaxSize, w));
        } else {
            this.source.subscribe(new BufferSkipBoundedObserver(new SerializedObserver(t), this.bufferSupplier, this.timespan, this.timeskip, this.unit, w));
        }
    }
}
