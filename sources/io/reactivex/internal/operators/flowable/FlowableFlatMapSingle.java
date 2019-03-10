package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Function;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.queue.SpscLinkedArrayQueue;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.AtomicThrowable;
import io.reactivex.internal.util.BackpressureHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FlowableFlatMapSingle<T, R> extends AbstractFlowableWithUpstream<T, R> {
    final boolean delayErrors;
    final Function<? super T, ? extends SingleSource<? extends R>> mapper;
    final int maxConcurrency;

    static final class FlatMapSingleSubscriber<T, R> extends AtomicInteger implements FlowableSubscriber<T>, Subscription {
        private static final long serialVersionUID = 8600231336733376951L;
        final AtomicInteger active = new AtomicInteger(1);
        volatile boolean cancelled;
        final boolean delayErrors;
        final Subscriber<? super R> downstream;
        final AtomicThrowable errors = new AtomicThrowable();
        final Function<? super T, ? extends SingleSource<? extends R>> mapper;
        final int maxConcurrency;
        final AtomicReference<SpscLinkedArrayQueue<R>> queue = new AtomicReference();
        final AtomicLong requested = new AtomicLong();
        final CompositeDisposable set = new CompositeDisposable();
        Subscription upstream;

        final class InnerObserver extends AtomicReference<Disposable> implements SingleObserver<R>, Disposable {
            private static final long serialVersionUID = -502562646270949838L;

            InnerObserver() {
            }

            public void onSubscribe(Disposable d) {
                DisposableHelper.setOnce(this, d);
            }

            public void onSuccess(R value) {
                FlatMapSingleSubscriber.this.innerSuccess(this, value);
            }

            public void onError(Throwable e) {
                FlatMapSingleSubscriber.this.innerError(this, e);
            }

            public boolean isDisposed() {
                return DisposableHelper.isDisposed((Disposable) get());
            }

            public void dispose() {
                DisposableHelper.dispose(this);
            }
        }

        FlatMapSingleSubscriber(Subscriber<? super R> actual, Function<? super T, ? extends SingleSource<? extends R>> mapper, boolean delayErrors, int maxConcurrency) {
            this.downstream = actual;
            this.mapper = mapper;
            this.delayErrors = delayErrors;
            this.maxConcurrency = maxConcurrency;
        }

        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.validate(this.upstream, s)) {
                this.upstream = s;
                this.downstream.onSubscribe(this);
                if (this.maxConcurrency == Integer.MAX_VALUE) {
                    s.request(Long.MAX_VALUE);
                } else {
                    s.request((long) this.maxConcurrency);
                }
            }
        }

        public void onNext(T t) {
            try {
                SingleSource<? extends R> ms = (SingleSource) ObjectHelper.requireNonNull(this.mapper.apply(t), "The mapper returned a null SingleSource");
                this.active.getAndIncrement();
                InnerObserver inner = new InnerObserver();
                if (!this.cancelled && this.set.add(inner)) {
                    ms.subscribe(inner);
                }
            } catch (Throwable ex) {
                Exceptions.throwIfFatal(ex);
                this.upstream.cancel();
                onError(ex);
            }
        }

        public void onError(Throwable t) {
            this.active.decrementAndGet();
            if (this.errors.addThrowable(t)) {
                if (!this.delayErrors) {
                    this.set.dispose();
                }
                drain();
                return;
            }
            RxJavaPlugins.onError(t);
        }

        public void onComplete() {
            this.active.decrementAndGet();
            drain();
        }

        public void cancel() {
            this.cancelled = true;
            this.upstream.cancel();
            this.set.dispose();
        }

        public void request(long n) {
            if (SubscriptionHelper.validate(n)) {
                BackpressureHelper.add(this.requested, n);
                drain();
            }
        }

        void innerSuccess(InnerObserver inner, R value) {
            this.set.delete(inner);
            if (get() == 0) {
                boolean d = true;
                if (compareAndSet(0, 1)) {
                    if (this.active.decrementAndGet() != 0) {
                        d = false;
                    }
                    SpscLinkedArrayQueue<R> q;
                    if (this.requested.get() != 0) {
                        this.downstream.onNext(value);
                        q = (SpscLinkedArrayQueue) this.queue.get();
                        if (d && (q == null || q.isEmpty())) {
                            Throwable ex = this.errors.terminate();
                            if (ex != null) {
                                this.downstream.onError(ex);
                            } else {
                                this.downstream.onComplete();
                            }
                            return;
                        }
                        BackpressureHelper.produced(this.requested, 1);
                        if (this.maxConcurrency != Integer.MAX_VALUE) {
                            this.upstream.request(1);
                        }
                    } else {
                        q = getOrCreateQueue();
                        synchronized (q) {
                            q.offer(value);
                        }
                    }
                    if (decrementAndGet() != 0) {
                        drainLoop();
                    }
                    return;
                }
            }
            SpscLinkedArrayQueue<R> q2 = getOrCreateQueue();
            synchronized (q2) {
                q2.offer(value);
            }
            this.active.decrementAndGet();
            if (getAndIncrement() == 0) {
                drainLoop();
            }
        }

        SpscLinkedArrayQueue<R> getOrCreateQueue() {
            while (true) {
                SpscLinkedArrayQueue<R> current = (SpscLinkedArrayQueue) this.queue.get();
                if (current != null) {
                    return current;
                }
                current = new SpscLinkedArrayQueue(Flowable.bufferSize());
                if (this.queue.compareAndSet(null, current)) {
                    return current;
                }
            }
        }

        void innerError(InnerObserver inner, Throwable e) {
            this.set.delete(inner);
            if (this.errors.addThrowable(e)) {
                if (!this.delayErrors) {
                    this.upstream.cancel();
                    this.set.dispose();
                } else if (this.maxConcurrency != Integer.MAX_VALUE) {
                    this.upstream.request(1);
                }
                this.active.decrementAndGet();
                drain();
                return;
            }
            RxJavaPlugins.onError(e);
        }

        void drain() {
            if (getAndIncrement() == 0) {
                drainLoop();
            }
        }

        void clear() {
            SpscLinkedArrayQueue<R> q = (SpscLinkedArrayQueue) this.queue.get();
            if (q != null) {
                q.clear();
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        void drainLoop() {
            /*
            r14 = this;
            r0 = 1;
            r1 = r14.downstream;
            r2 = r14.active;
            r3 = r14.queue;
        L_0x0007:
            r4 = r14.requested;
            r4 = r4.get();
            r6 = 0;
        L_0x000f:
            r8 = 1;
            r9 = 0;
            r10 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1));
            if (r10 == 0) goto L_0x0075;
        L_0x0015:
            r10 = r14.cancelled;
            if (r10 == 0) goto L_0x001d;
        L_0x0019:
            r14.clear();
            return;
        L_0x001d:
            r10 = r14.delayErrors;
            if (r10 != 0) goto L_0x0039;
        L_0x0021:
            r10 = r14.errors;
            r10 = r10.get();
            r10 = (java.lang.Throwable) r10;
            if (r10 == 0) goto L_0x0038;
        L_0x002b:
            r8 = r14.errors;
            r8 = r8.terminate();
            r14.clear();
            r1.onError(r8);
            return;
        L_0x0038:
            goto L_0x003a;
        L_0x003a:
            r10 = r2.get();
            if (r10 != 0) goto L_0x0042;
        L_0x0040:
            r10 = 1;
            goto L_0x0043;
        L_0x0042:
            r10 = 0;
        L_0x0043:
            r11 = r3.get();
            r11 = (io.reactivex.internal.queue.SpscLinkedArrayQueue) r11;
            if (r11 == 0) goto L_0x0050;
        L_0x004b:
            r12 = r11.poll();
            goto L_0x0051;
        L_0x0050:
            r12 = 0;
        L_0x0051:
            if (r12 != 0) goto L_0x0055;
        L_0x0053:
            r13 = 1;
            goto L_0x0056;
        L_0x0055:
            r13 = 0;
        L_0x0056:
            if (r10 == 0) goto L_0x006a;
        L_0x0058:
            if (r13 == 0) goto L_0x006a;
        L_0x005a:
            r8 = r14.errors;
            r8 = r8.terminate();
            if (r8 == 0) goto L_0x0066;
        L_0x0062:
            r1.onError(r8);
            goto L_0x0069;
        L_0x0066:
            r1.onComplete();
        L_0x0069:
            return;
            if (r13 == 0) goto L_0x006e;
        L_0x006d:
            goto L_0x0076;
        L_0x006e:
            r1.onNext(r12);
            r8 = 1;
            r6 = r6 + r8;
            goto L_0x000f;
        L_0x0076:
            r10 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1));
            if (r10 != 0) goto L_0x00ce;
        L_0x007a:
            r10 = r14.cancelled;
            if (r10 == 0) goto L_0x0082;
        L_0x007e:
            r14.clear();
            return;
        L_0x0082:
            r10 = r14.delayErrors;
            if (r10 != 0) goto L_0x009e;
        L_0x0086:
            r10 = r14.errors;
            r10 = r10.get();
            r10 = (java.lang.Throwable) r10;
            if (r10 == 0) goto L_0x009d;
        L_0x0090:
            r8 = r14.errors;
            r8 = r8.terminate();
            r14.clear();
            r1.onError(r8);
            return;
        L_0x009d:
            goto L_0x009f;
        L_0x009f:
            r10 = r2.get();
            if (r10 != 0) goto L_0x00a7;
        L_0x00a5:
            r10 = 1;
            goto L_0x00a8;
        L_0x00a7:
            r10 = 0;
        L_0x00a8:
            r11 = r3.get();
            r11 = (io.reactivex.internal.queue.SpscLinkedArrayQueue) r11;
            if (r11 == 0) goto L_0x00b9;
        L_0x00b0:
            r12 = r11.isEmpty();
            if (r12 == 0) goto L_0x00b7;
        L_0x00b6:
            goto L_0x00b9;
        L_0x00b7:
            r8 = 0;
        L_0x00b9:
            if (r10 == 0) goto L_0x00cd;
        L_0x00bb:
            if (r8 == 0) goto L_0x00cd;
        L_0x00bd:
            r9 = r14.errors;
            r9 = r9.terminate();
            if (r9 == 0) goto L_0x00c9;
        L_0x00c5:
            r1.onError(r9);
            goto L_0x00cc;
        L_0x00c9:
            r1.onComplete();
        L_0x00cc:
            return;
        L_0x00cd:
            goto L_0x00cf;
        L_0x00cf:
            r8 = 0;
            r10 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
            if (r10 == 0) goto L_0x00e8;
        L_0x00d5:
            r8 = r14.requested;
            io.reactivex.internal.util.BackpressureHelper.produced(r8, r6);
            r8 = r14.maxConcurrency;
            r9 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
            if (r8 == r9) goto L_0x00e7;
        L_0x00e1:
            r8 = r14.upstream;
            r8.request(r6);
            goto L_0x00e9;
        L_0x00e7:
            goto L_0x00e9;
        L_0x00e9:
            r8 = -r0;
            r0 = r14.addAndGet(r8);
            if (r0 != 0) goto L_0x00f2;
            return;
            goto L_0x0007;
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.flowable.FlowableFlatMapSingle.FlatMapSingleSubscriber.drainLoop():void");
        }
    }

    public FlowableFlatMapSingle(Flowable<T> source, Function<? super T, ? extends SingleSource<? extends R>> mapper, boolean delayError, int maxConcurrency) {
        super(source);
        this.mapper = mapper;
        this.delayErrors = delayError;
        this.maxConcurrency = maxConcurrency;
    }

    protected void subscribeActual(Subscriber<? super R> s) {
        this.source.subscribe(new FlatMapSingleSubscriber(s, this.mapper, this.delayErrors, this.maxConcurrency));
    }
}
