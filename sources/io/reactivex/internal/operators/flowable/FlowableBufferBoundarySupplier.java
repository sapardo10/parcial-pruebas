package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.queue.MpscLinkedQueue;
import io.reactivex.internal.subscribers.QueueDrainSubscriber;
import io.reactivex.internal.subscriptions.EmptySubscription;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.subscribers.DisposableSubscriber;
import io.reactivex.subscribers.SerializedSubscriber;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FlowableBufferBoundarySupplier<T, U extends Collection<? super T>, B> extends AbstractFlowableWithUpstream<T, U> {
    final Callable<? extends Publisher<B>> boundarySupplier;
    final Callable<U> bufferSupplier;

    static final class BufferBoundarySubscriber<T, U extends Collection<? super T>, B> extends DisposableSubscriber<B> {
        boolean once;
        final BufferBoundarySupplierSubscriber<T, U, B> parent;

        BufferBoundarySubscriber(BufferBoundarySupplierSubscriber<T, U, B> parent) {
            this.parent = parent;
        }

        public void onNext(B b) {
            if (!this.once) {
                this.once = true;
                cancel();
                this.parent.next();
            }
        }

        public void onError(Throwable t) {
            if (this.once) {
                RxJavaPlugins.onError(t);
                return;
            }
            this.once = true;
            this.parent.onError(t);
        }

        public void onComplete() {
            if (!this.once) {
                this.once = true;
                this.parent.next();
            }
        }
    }

    static final class BufferBoundarySupplierSubscriber<T, U extends Collection<? super T>, B> extends QueueDrainSubscriber<T, U, U> implements FlowableSubscriber<T>, Subscription, Disposable {
        final Callable<? extends Publisher<B>> boundarySupplier;
        U buffer;
        final Callable<U> bufferSupplier;
        final AtomicReference<Disposable> other = new AtomicReference();
        Subscription upstream;

        BufferBoundarySupplierSubscriber(Subscriber<? super U> actual, Callable<U> bufferSupplier, Callable<? extends Publisher<B>> boundarySupplier) {
            super(actual, new MpscLinkedQueue());
            this.bufferSupplier = bufferSupplier;
            this.boundarySupplier = boundarySupplier;
        }

        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.validate(this.upstream, s)) {
                this.upstream = s;
                Subscriber<? super U> actual = this.downstream;
                try {
                    this.buffer = (Collection) ObjectHelper.requireNonNull(this.bufferSupplier.call(), "The buffer supplied is null");
                    try {
                        Publisher<B> boundary = (Publisher) ObjectHelper.requireNonNull(this.boundarySupplier.call(), "The boundary publisher supplied is null");
                        BufferBoundarySubscriber<T, U, B> bs = new BufferBoundarySubscriber(this);
                        this.other.set(bs);
                        actual.onSubscribe(this);
                        if (!this.cancelled) {
                            s.request(Long.MAX_VALUE);
                            boundary.subscribe(bs);
                        }
                    } catch (Throwable ex) {
                        Exceptions.throwIfFatal(ex);
                        this.cancelled = true;
                        s.cancel();
                        EmptySubscription.error(ex, actual);
                    }
                } catch (Throwable e) {
                    Exceptions.throwIfFatal(e);
                    this.cancelled = true;
                    s.cancel();
                    EmptySubscription.error(e, actual);
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
            cancel();
            this.downstream.onError(t);
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onComplete() {
            /*
            r4 = this;
            monitor-enter(r4);
            r0 = r4.buffer;	 Catch:{ all -> 0x0024 }
            if (r0 != 0) goto L_0x0007;
        L_0x0005:
            monitor-exit(r4);	 Catch:{ all -> 0x0024 }
            return;
        L_0x0007:
            r1 = 0;
            r4.buffer = r1;	 Catch:{ all -> 0x0024 }
            monitor-exit(r4);	 Catch:{ all -> 0x0024 }
            r1 = r4.queue;
            r1.offer(r0);
            r1 = 1;
            r4.done = r1;
            r1 = r4.enter();
            if (r1 == 0) goto L_0x0022;
        L_0x0019:
            r1 = r4.queue;
            r2 = r4.downstream;
            r3 = 0;
            io.reactivex.internal.util.QueueDrainHelper.drainMaxLoop(r1, r2, r3, r4, r4);
            goto L_0x0023;
        L_0x0023:
            return;
        L_0x0024:
            r0 = move-exception;
            monitor-exit(r4);	 Catch:{ all -> 0x0024 }
            throw r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.flowable.FlowableBufferBoundarySupplier.BufferBoundarySupplierSubscriber.onComplete():void");
        }

        public void request(long n) {
            requested(n);
        }

        public void cancel() {
            if (!this.cancelled) {
                this.cancelled = true;
                this.upstream.cancel();
                disposeOther();
                if (enter()) {
                    this.queue.clear();
                }
            }
        }

        void disposeOther() {
            DisposableHelper.dispose(this.other);
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        void next() {
            /*
            r5 = this;
            r0 = r5.bufferSupplier;	 Catch:{ Throwable -> 0x0054 }
            r0 = r0.call();	 Catch:{ Throwable -> 0x0054 }
            r1 = "The buffer supplied is null";
            r0 = io.reactivex.internal.functions.ObjectHelper.requireNonNull(r0, r1);	 Catch:{ Throwable -> 0x0054 }
            r0 = (java.util.Collection) r0;	 Catch:{ Throwable -> 0x0054 }
            r1 = r5.boundarySupplier;	 Catch:{ Throwable -> 0x0042 }
            r1 = r1.call();	 Catch:{ Throwable -> 0x0042 }
            r2 = "The boundary publisher supplied is null";
            r1 = io.reactivex.internal.functions.ObjectHelper.requireNonNull(r1, r2);	 Catch:{ Throwable -> 0x0042 }
            r1 = (org.reactivestreams.Publisher) r1;	 Catch:{ Throwable -> 0x0042 }
            r2 = new io.reactivex.internal.operators.flowable.FlowableBufferBoundarySupplier$BufferBoundarySubscriber;
            r2.<init>(r5);
            r3 = r5.other;
            r3 = io.reactivex.internal.disposables.DisposableHelper.replace(r3, r2);
            if (r3 == 0) goto L_0x0040;
        L_0x002b:
            monitor-enter(r5);
            r3 = r5.buffer;	 Catch:{ all -> 0x003d }
            if (r3 != 0) goto L_0x0032;
        L_0x0030:
            monitor-exit(r5);	 Catch:{ all -> 0x003d }
            return;
        L_0x0032:
            r5.buffer = r0;	 Catch:{ all -> 0x003d }
            monitor-exit(r5);	 Catch:{ all -> 0x003d }
            r1.subscribe(r2);
            r4 = 0;
            r5.fastPathEmitMax(r3, r4, r5);
            goto L_0x0041;
        L_0x003d:
            r3 = move-exception;
            monitor-exit(r5);	 Catch:{ all -> 0x003d }
            throw r3;
        L_0x0041:
            return;
        L_0x0042:
            r1 = move-exception;
            io.reactivex.exceptions.Exceptions.throwIfFatal(r1);
            r2 = 1;
            r5.cancelled = r2;
            r2 = r5.upstream;
            r2.cancel();
            r2 = r5.downstream;
            r2.onError(r1);
            return;
        L_0x0054:
            r0 = move-exception;
            io.reactivex.exceptions.Exceptions.throwIfFatal(r0);
            r5.cancel();
            r1 = r5.downstream;
            r1.onError(r0);
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.flowable.FlowableBufferBoundarySupplier.BufferBoundarySupplierSubscriber.next():void");
        }

        public void dispose() {
            this.upstream.cancel();
            disposeOther();
        }

        public boolean isDisposed() {
            return this.other.get() == DisposableHelper.DISPOSED;
        }

        public boolean accept(Subscriber<? super U> subscriber, U v) {
            this.downstream.onNext(v);
            return true;
        }
    }

    public FlowableBufferBoundarySupplier(Flowable<T> source, Callable<? extends Publisher<B>> boundarySupplier, Callable<U> bufferSupplier) {
        super(source);
        this.boundarySupplier = boundarySupplier;
        this.bufferSupplier = bufferSupplier;
    }

    protected void subscribeActual(Subscriber<? super U> s) {
        this.source.subscribe(new BufferBoundarySupplierSubscriber(new SerializedSubscriber(s), this.bufferSupplier, this.boundarySupplier));
    }
}
