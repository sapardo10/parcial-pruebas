package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.exceptions.MissingBackpressureException;
import io.reactivex.functions.Function;
import io.reactivex.internal.fuseable.QueueSubscription;
import io.reactivex.internal.fuseable.SimpleQueue;
import io.reactivex.internal.queue.SpscArrayQueue;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.AtomicThrowable;
import io.reactivex.internal.util.BackpressureHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FlowableSwitchMap<T, R> extends AbstractFlowableWithUpstream<T, R> {
    final int bufferSize;
    final boolean delayErrors;
    final Function<? super T, ? extends Publisher<? extends R>> mapper;

    static final class SwitchMapInnerSubscriber<T, R> extends AtomicReference<Subscription> implements FlowableSubscriber<R> {
        private static final long serialVersionUID = 3837284832786408377L;
        final int bufferSize;
        volatile boolean done;
        int fusionMode;
        final long index;
        final SwitchMapSubscriber<T, R> parent;
        volatile SimpleQueue<R> queue;

        SwitchMapInnerSubscriber(SwitchMapSubscriber<T, R> parent, long index, int bufferSize) {
            this.parent = parent;
            this.index = index;
            this.bufferSize = bufferSize;
        }

        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.setOnce(this, s)) {
                if (s instanceof QueueSubscription) {
                    QueueSubscription<R> qs = (QueueSubscription) s;
                    int m = qs.requestFusion(7);
                    if (m == 1) {
                        this.fusionMode = m;
                        this.queue = qs;
                        this.done = true;
                        this.parent.drain();
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

        public void onNext(R t) {
            SwitchMapSubscriber<T, R> p = this.parent;
            if (this.index == p.unique) {
                if (this.fusionMode != 0 || this.queue.offer(t)) {
                    p.drain();
                } else {
                    onError(new MissingBackpressureException("Queue full?!"));
                }
            }
        }

        public void onError(Throwable t) {
            SwitchMapSubscriber<T, R> p = this.parent;
            if (this.index == p.unique && p.error.addThrowable(t)) {
                if (!p.delayErrors) {
                    p.upstream.cancel();
                }
                this.done = true;
                p.drain();
                return;
            }
            RxJavaPlugins.onError(t);
        }

        public void onComplete() {
            SwitchMapSubscriber<T, R> p = this.parent;
            if (this.index == p.unique) {
                this.done = true;
                p.drain();
            }
        }

        public void cancel() {
            SubscriptionHelper.cancel(this);
        }
    }

    static final class SwitchMapSubscriber<T, R> extends AtomicInteger implements FlowableSubscriber<T>, Subscription {
        static final SwitchMapInnerSubscriber<Object, Object> CANCELLED = new SwitchMapInnerSubscriber(null, -1, 1);
        private static final long serialVersionUID = -3491074160481096299L;
        final AtomicReference<SwitchMapInnerSubscriber<T, R>> active = new AtomicReference();
        final int bufferSize;
        volatile boolean cancelled;
        final boolean delayErrors;
        volatile boolean done;
        final Subscriber<? super R> downstream;
        final AtomicThrowable error;
        final Function<? super T, ? extends Publisher<? extends R>> mapper;
        final AtomicLong requested = new AtomicLong();
        volatile long unique;
        Subscription upstream;

        static {
            CANCELLED.cancel();
        }

        SwitchMapSubscriber(Subscriber<? super R> actual, Function<? super T, ? extends Publisher<? extends R>> mapper, int bufferSize, boolean delayErrors) {
            this.downstream = actual;
            this.mapper = mapper;
            this.bufferSize = bufferSize;
            this.delayErrors = delayErrors;
            this.error = new AtomicThrowable();
        }

        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.validate(this.upstream, s)) {
                this.upstream = s;
                this.downstream.onSubscribe(this);
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onNext(T r7) {
            /*
            r6 = this;
            r0 = r6.done;
            if (r0 == 0) goto L_0x0005;
        L_0x0004:
            return;
        L_0x0005:
            r0 = r6.unique;
            r2 = 1;
            r0 = r0 + r2;
            r6.unique = r0;
            r2 = r6.active;
            r2 = r2.get();
            r2 = (io.reactivex.internal.operators.flowable.FlowableSwitchMap.SwitchMapInnerSubscriber) r2;
            if (r2 == 0) goto L_0x001a;
        L_0x0016:
            r2.cancel();
            goto L_0x001b;
        L_0x001b:
            r3 = r6.mapper;	 Catch:{ Throwable -> 0x004d }
            r3 = r3.apply(r7);	 Catch:{ Throwable -> 0x004d }
            r4 = "The publisher returned is null";
            r3 = io.reactivex.internal.functions.ObjectHelper.requireNonNull(r3, r4);	 Catch:{ Throwable -> 0x004d }
            r3 = (org.reactivestreams.Publisher) r3;	 Catch:{ Throwable -> 0x004d }
            r4 = new io.reactivex.internal.operators.flowable.FlowableSwitchMap$SwitchMapInnerSubscriber;
            r5 = r6.bufferSize;
            r4.<init>(r6, r0, r5);
        L_0x0031:
            r5 = r6.active;
            r5 = r5.get();
            r2 = r5;
            r2 = (io.reactivex.internal.operators.flowable.FlowableSwitchMap.SwitchMapInnerSubscriber) r2;
            r5 = CANCELLED;
            if (r2 != r5) goto L_0x003f;
        L_0x003e:
            goto L_0x004b;
        L_0x003f:
            r5 = r6.active;
            r5 = r5.compareAndSet(r2, r4);
            if (r5 == 0) goto L_0x004c;
        L_0x0047:
            r3.subscribe(r4);
        L_0x004b:
            return;
        L_0x004c:
            goto L_0x0031;
        L_0x004d:
            r3 = move-exception;
            io.reactivex.exceptions.Exceptions.throwIfFatal(r3);
            r4 = r6.upstream;
            r4.cancel();
            r6.onError(r3);
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.flowable.FlowableSwitchMap.SwitchMapSubscriber.onNext(java.lang.Object):void");
        }

        public void onError(Throwable t) {
            if (this.done || !this.error.addThrowable(t)) {
                RxJavaPlugins.onError(t);
                return;
            }
            if (!this.delayErrors) {
                disposeInner();
            }
            this.done = true;
            drain();
        }

        public void onComplete() {
            if (!this.done) {
                this.done = true;
                drain();
            }
        }

        public void request(long n) {
            if (SubscriptionHelper.validate(n)) {
                BackpressureHelper.add(this.requested, n);
                if (this.unique == 0) {
                    this.upstream.request(Long.MAX_VALUE);
                } else {
                    drain();
                }
            }
        }

        public void cancel() {
            if (!this.cancelled) {
                this.cancelled = true;
                this.upstream.cancel();
                disposeInner();
            }
        }

        void disposeInner() {
            SwitchMapInnerSubscriber<T, R> a = (SwitchMapInnerSubscriber) this.active.get();
            SwitchMapInnerSubscriber<T, R> switchMapInnerSubscriber = CANCELLED;
            if (a != switchMapInnerSubscriber) {
                a = (SwitchMapInnerSubscriber) this.active.getAndSet(switchMapInnerSubscriber);
                if (a != CANCELLED && a != null) {
                    a.cancel();
                }
            }
        }

        void drain() {
            if (getAndIncrement() == 0) {
                Subscriber<? super R> a = this.downstream;
                int missing = 1;
                while (!this.cancelled) {
                    if (this.done) {
                        if (this.delayErrors) {
                            if (this.active.get() == null) {
                                if (((Throwable) this.error.get()) != null) {
                                    a.onError(this.error.terminate());
                                } else {
                                    a.onComplete();
                                }
                                return;
                            }
                        } else if (((Throwable) this.error.get()) != null) {
                            disposeInner();
                            a.onError(this.error.terminate());
                            return;
                        } else if (this.active.get() == null) {
                            a.onComplete();
                            return;
                        }
                    }
                    SwitchMapInnerSubscriber<T, R> inner = (SwitchMapInnerSubscriber) this.active.get();
                    SimpleQueue<R> q = inner != null ? inner.queue : null;
                    if (q != null) {
                        if (inner.done) {
                            if (this.delayErrors) {
                                if (q.isEmpty()) {
                                    this.active.compareAndSet(inner, null);
                                }
                            } else if (((Throwable) this.error.get()) != null) {
                                disposeInner();
                                a.onError(this.error.terminate());
                                return;
                            } else if (q.isEmpty()) {
                                this.active.compareAndSet(inner, null);
                            }
                        }
                        long r = this.requested.get();
                        long e = 0;
                        boolean retry = false;
                        while (e != r) {
                            if (!this.cancelled) {
                                R v;
                                boolean d = inner.done;
                                try {
                                    v = q.poll();
                                } catch (Throwable ex) {
                                    Exceptions.throwIfFatal(ex);
                                    inner.cancel();
                                    this.error.addThrowable(ex);
                                    d = true;
                                    v = null;
                                }
                                boolean empty = v == null;
                                if (inner != this.active.get()) {
                                    retry = true;
                                    break;
                                }
                                if (d) {
                                    if (this.delayErrors) {
                                        if (empty) {
                                            this.active.compareAndSet(inner, null);
                                            retry = true;
                                            break;
                                        }
                                    } else if (((Throwable) this.error.get()) != null) {
                                        a.onError(this.error.terminate());
                                        return;
                                    } else if (empty) {
                                        this.active.compareAndSet(inner, null);
                                        retry = true;
                                        break;
                                    }
                                }
                                if (empty) {
                                    break;
                                }
                                a.onNext(v);
                                e++;
                            } else {
                                return;
                            }
                        }
                        if (e != 0) {
                            if (!this.cancelled) {
                                if (r != Long.MAX_VALUE) {
                                    this.requested.addAndGet(-e);
                                }
                                ((Subscription) inner.get()).request(e);
                            }
                        }
                        if (retry) {
                        }
                    }
                    missing = addAndGet(-missing);
                    if (missing == 0) {
                        return;
                    }
                }
                this.active.lazySet(null);
            }
        }
    }

    public FlowableSwitchMap(Flowable<T> source, Function<? super T, ? extends Publisher<? extends R>> mapper, int bufferSize, boolean delayErrors) {
        super(source);
        this.mapper = mapper;
        this.bufferSize = bufferSize;
        this.delayErrors = delayErrors;
    }

    protected void subscribeActual(Subscriber<? super R> s) {
        if (!FlowableScalarXMap.tryScalarXMapSubscribe(this.source, s, this.mapper)) {
            this.source.subscribe(new SwitchMapSubscriber(s, this.mapper, this.bufferSize, this.delayErrors));
        }
    }
}
