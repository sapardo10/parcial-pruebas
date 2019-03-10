package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Function;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.queue.SpscLinkedArrayQueue;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.AtomicThrowable;
import io.reactivex.internal.util.BackpressureHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FlowableBufferBoundary<T, U extends Collection<? super T>, Open, Close> extends AbstractFlowableWithUpstream<T, U> {
    final Function<? super Open, ? extends Publisher<? extends Close>> bufferClose;
    final Publisher<? extends Open> bufferOpen;
    final Callable<U> bufferSupplier;

    static final class BufferBoundarySubscriber<T, C extends Collection<? super T>, Open, Close> extends AtomicInteger implements FlowableSubscriber<T>, Subscription {
        private static final long serialVersionUID = -8466418554264089604L;
        final Function<? super Open, ? extends Publisher<? extends Close>> bufferClose;
        final Publisher<? extends Open> bufferOpen;
        final Callable<C> bufferSupplier;
        Map<Long, C> buffers = new LinkedHashMap();
        volatile boolean cancelled;
        volatile boolean done;
        final Subscriber<? super C> downstream;
        long emitted;
        final AtomicThrowable errors = new AtomicThrowable();
        long index;
        final SpscLinkedArrayQueue<C> queue = new SpscLinkedArrayQueue(Flowable.bufferSize());
        final AtomicLong requested = new AtomicLong();
        final CompositeDisposable subscribers = new CompositeDisposable();
        final AtomicReference<Subscription> upstream = new AtomicReference();

        static final class BufferOpenSubscriber<Open> extends AtomicReference<Subscription> implements FlowableSubscriber<Open>, Disposable {
            private static final long serialVersionUID = -8498650778633225126L;
            final BufferBoundarySubscriber<?, ?, Open, ?> parent;

            BufferOpenSubscriber(BufferBoundarySubscriber<?, ?, Open, ?> parent) {
                this.parent = parent;
            }

            public void onSubscribe(Subscription s) {
                SubscriptionHelper.setOnce(this, s, Long.MAX_VALUE);
            }

            public void onNext(Open t) {
                this.parent.open(t);
            }

            public void onError(Throwable t) {
                lazySet(SubscriptionHelper.CANCELLED);
                this.parent.boundaryError(this, t);
            }

            public void onComplete() {
                lazySet(SubscriptionHelper.CANCELLED);
                this.parent.openComplete(this);
            }

            public void dispose() {
                SubscriptionHelper.cancel(this);
            }

            public boolean isDisposed() {
                return get() == SubscriptionHelper.CANCELLED;
            }
        }

        public void onComplete() {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:19:0x0034 in {6, 10, 14, 18} preds:[]
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
            r0 = r4.subscribers;
            r0.dispose();
            monitor-enter(r4);
            r0 = r4.buffers;	 Catch:{ all -> 0x0031 }
            if (r0 != 0) goto L_0x000c;	 Catch:{ all -> 0x0031 }
        L_0x000a:
            monitor-exit(r4);	 Catch:{ all -> 0x0031 }
            return;	 Catch:{ all -> 0x0031 }
        L_0x000c:
            r1 = r0.values();	 Catch:{ all -> 0x0031 }
            r1 = r1.iterator();	 Catch:{ all -> 0x0031 }
        L_0x0014:
            r2 = r1.hasNext();	 Catch:{ all -> 0x0031 }
            if (r2 == 0) goto L_0x0026;	 Catch:{ all -> 0x0031 }
        L_0x001a:
            r2 = r1.next();	 Catch:{ all -> 0x0031 }
            r2 = (java.util.Collection) r2;	 Catch:{ all -> 0x0031 }
            r3 = r4.queue;	 Catch:{ all -> 0x0031 }
            r3.offer(r2);	 Catch:{ all -> 0x0031 }
            goto L_0x0014;	 Catch:{ all -> 0x0031 }
        L_0x0026:
            r1 = 0;	 Catch:{ all -> 0x0031 }
            r4.buffers = r1;	 Catch:{ all -> 0x0031 }
            monitor-exit(r4);	 Catch:{ all -> 0x0031 }
            r0 = 1;
            r4.done = r0;
            r4.drain();
            return;
        L_0x0031:
            r0 = move-exception;
            monitor-exit(r4);	 Catch:{ all -> 0x0031 }
            throw r0;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.flowable.FlowableBufferBoundary.BufferBoundarySubscriber.onComplete():void");
        }

        public void onNext(T r4) {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:16:0x0025 in {5, 9, 12, 15} preds:[]
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
            r3 = this;
            monitor-enter(r3);
            r0 = r3.buffers;	 Catch:{ all -> 0x0022 }
            if (r0 != 0) goto L_0x0007;	 Catch:{ all -> 0x0022 }
        L_0x0005:
            monitor-exit(r3);	 Catch:{ all -> 0x0022 }
            return;	 Catch:{ all -> 0x0022 }
        L_0x0007:
            r1 = r0.values();	 Catch:{ all -> 0x0022 }
            r1 = r1.iterator();	 Catch:{ all -> 0x0022 }
        L_0x000f:
            r2 = r1.hasNext();	 Catch:{ all -> 0x0022 }
            if (r2 == 0) goto L_0x001f;	 Catch:{ all -> 0x0022 }
        L_0x0015:
            r2 = r1.next();	 Catch:{ all -> 0x0022 }
            r2 = (java.util.Collection) r2;	 Catch:{ all -> 0x0022 }
            r2.add(r4);	 Catch:{ all -> 0x0022 }
            goto L_0x000f;	 Catch:{ all -> 0x0022 }
            monitor-exit(r3);	 Catch:{ all -> 0x0022 }
            return;	 Catch:{ all -> 0x0022 }
        L_0x0022:
            r0 = move-exception;	 Catch:{ all -> 0x0022 }
            monitor-exit(r3);	 Catch:{ all -> 0x0022 }
            throw r0;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.flowable.FlowableBufferBoundary.BufferBoundarySubscriber.onNext(java.lang.Object):void");
        }

        BufferBoundarySubscriber(Subscriber<? super C> actual, Publisher<? extends Open> bufferOpen, Function<? super Open, ? extends Publisher<? extends Close>> bufferClose, Callable<C> bufferSupplier) {
            this.downstream = actual;
            this.bufferSupplier = bufferSupplier;
            this.bufferOpen = bufferOpen;
            this.bufferClose = bufferClose;
        }

        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.setOnce(this.upstream, s)) {
                BufferOpenSubscriber<Open> open = new BufferOpenSubscriber(this);
                this.subscribers.add(open);
                this.bufferOpen.subscribe(open);
                s.request(Long.MAX_VALUE);
            }
        }

        public void onError(Throwable t) {
            if (this.errors.addThrowable(t)) {
                this.subscribers.dispose();
                synchronized (this) {
                    this.buffers = null;
                }
                this.done = true;
                drain();
                return;
            }
            RxJavaPlugins.onError(t);
        }

        public void request(long n) {
            BackpressureHelper.add(this.requested, n);
            drain();
        }

        public void cancel() {
            if (SubscriptionHelper.cancel(this.upstream)) {
                this.cancelled = true;
                this.subscribers.dispose();
                synchronized (this) {
                    this.buffers = null;
                }
                if (getAndIncrement() != 0) {
                    this.queue.clear();
                }
            }
        }

        void open(Open token) {
            try {
                Collection buf = (Collection) ObjectHelper.requireNonNull(this.bufferSupplier.call(), "The bufferSupplier returned a null Collection");
                Publisher<? extends Close> p = (Publisher) ObjectHelper.requireNonNull(this.bufferClose.apply(token), "The bufferClose returned a null Publisher");
                long idx = this.index;
                this.index = 1 + idx;
                synchronized (this) {
                    Map<Long, C> bufs = this.buffers;
                    if (bufs == null) {
                        return;
                    }
                    bufs.put(Long.valueOf(idx), buf);
                    BufferCloseSubscriber<T, C> bc = new BufferCloseSubscriber(this, idx);
                    this.subscribers.add(bc);
                    p.subscribe(bc);
                }
            } catch (Throwable ex) {
                Exceptions.throwIfFatal(ex);
                SubscriptionHelper.cancel(this.upstream);
                onError(ex);
            }
        }

        void openComplete(BufferOpenSubscriber<Open> os) {
            this.subscribers.delete(os);
            if (this.subscribers.size() == 0) {
                SubscriptionHelper.cancel(this.upstream);
                this.done = true;
                drain();
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        void close(io.reactivex.internal.operators.flowable.FlowableBufferBoundary.BufferCloseSubscriber<T, C> r6, long r7) {
            /*
            r5 = this;
            r0 = r5.subscribers;
            r0.delete(r6);
            r0 = 0;
            r1 = r5.subscribers;
            r1 = r1.size();
            if (r1 != 0) goto L_0x0015;
        L_0x000e:
            r0 = 1;
            r1 = r5.upstream;
            io.reactivex.internal.subscriptions.SubscriptionHelper.cancel(r1);
            goto L_0x0016;
        L_0x0016:
            monitor-enter(r5);
            r1 = r5.buffers;	 Catch:{ all -> 0x0039 }
            if (r1 != 0) goto L_0x001d;
        L_0x001b:
            monitor-exit(r5);	 Catch:{ all -> 0x0039 }
            return;
        L_0x001d:
            r2 = r5.queue;	 Catch:{ all -> 0x0039 }
            r3 = r5.buffers;	 Catch:{ all -> 0x0039 }
            r4 = java.lang.Long.valueOf(r7);	 Catch:{ all -> 0x0039 }
            r3 = r3.remove(r4);	 Catch:{ all -> 0x0039 }
            r2.offer(r3);	 Catch:{ all -> 0x0039 }
            monitor-exit(r5);	 Catch:{ all -> 0x0039 }
            if (r0 == 0) goto L_0x0034;
        L_0x0030:
            r1 = 1;
            r5.done = r1;
            goto L_0x0035;
        L_0x0035:
            r5.drain();
            return;
        L_0x0039:
            r1 = move-exception;
            monitor-exit(r5);	 Catch:{ all -> 0x0039 }
            throw r1;
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.flowable.FlowableBufferBoundary.BufferBoundarySubscriber.close(io.reactivex.internal.operators.flowable.FlowableBufferBoundary$BufferCloseSubscriber, long):void");
        }

        void boundaryError(Disposable subscriber, Throwable ex) {
            SubscriptionHelper.cancel(this.upstream);
            this.subscribers.delete(subscriber);
            onError(ex);
        }

        void drain() {
            if (getAndIncrement() == 0) {
                int missed = 1;
                long e = this.emitted;
                Subscriber<? super C> a = this.downstream;
                SpscLinkedArrayQueue<C> q = this.queue;
                while (true) {
                    long r = this.requested.get();
                    while (e != r) {
                        if (this.cancelled) {
                            q.clear();
                            return;
                        }
                        boolean d = this.done;
                        if (!d || this.errors.get() == null) {
                            Collection v = (Collection) q.poll();
                            boolean empty = v == null;
                            if (d && empty) {
                                a.onComplete();
                                return;
                            } else if (empty) {
                                break;
                            } else {
                                a.onNext(v);
                                e++;
                            }
                        } else {
                            q.clear();
                            a.onError(this.errors.terminate());
                            return;
                        }
                    }
                    if (e == r) {
                        if (this.cancelled) {
                            q.clear();
                            return;
                        } else if (this.done) {
                            if (this.errors.get() != null) {
                                q.clear();
                                a.onError(this.errors.terminate());
                                return;
                            } else if (q.isEmpty()) {
                                a.onComplete();
                                return;
                            }
                        }
                    }
                    this.emitted = e;
                    missed = addAndGet(-missed);
                    if (missed == 0) {
                        return;
                    }
                }
            }
        }
    }

    static final class BufferCloseSubscriber<T, C extends Collection<? super T>> extends AtomicReference<Subscription> implements FlowableSubscriber<Object>, Disposable {
        private static final long serialVersionUID = -8498650778633225126L;
        final long index;
        final BufferBoundarySubscriber<T, C, ?, ?> parent;

        BufferCloseSubscriber(BufferBoundarySubscriber<T, C, ?, ?> parent, long index) {
            this.parent = parent;
            this.index = index;
        }

        public void onSubscribe(Subscription s) {
            SubscriptionHelper.setOnce(this, s, Long.MAX_VALUE);
        }

        public void onNext(Object t) {
            Subscription s = (Subscription) get();
            if (s != SubscriptionHelper.CANCELLED) {
                lazySet(SubscriptionHelper.CANCELLED);
                s.cancel();
                this.parent.close(this, this.index);
            }
        }

        public void onError(Throwable t) {
            if (get() != SubscriptionHelper.CANCELLED) {
                lazySet(SubscriptionHelper.CANCELLED);
                this.parent.boundaryError(this, t);
                return;
            }
            RxJavaPlugins.onError(t);
        }

        public void onComplete() {
            if (get() != SubscriptionHelper.CANCELLED) {
                lazySet(SubscriptionHelper.CANCELLED);
                this.parent.close(this, this.index);
            }
        }

        public void dispose() {
            SubscriptionHelper.cancel(this);
        }

        public boolean isDisposed() {
            return get() == SubscriptionHelper.CANCELLED;
        }
    }

    public FlowableBufferBoundary(Flowable<T> source, Publisher<? extends Open> bufferOpen, Function<? super Open, ? extends Publisher<? extends Close>> bufferClose, Callable<U> bufferSupplier) {
        super(source);
        this.bufferOpen = bufferOpen;
        this.bufferClose = bufferClose;
        this.bufferSupplier = bufferSupplier;
    }

    protected void subscribeActual(Subscriber<? super U> s) {
        BufferBoundarySubscriber<T, U, Open, Close> parent = new BufferBoundarySubscriber(s, this.bufferOpen, this.bufferClose, this.bufferSupplier);
        s.onSubscribe(parent);
        this.source.subscribe(parent);
    }
}
