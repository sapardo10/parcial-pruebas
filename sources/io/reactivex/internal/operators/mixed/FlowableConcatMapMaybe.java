package io.reactivex.internal.operators.mixed;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.MaybeObserver;
import io.reactivex.MaybeSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.MissingBackpressureException;
import io.reactivex.functions.Function;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.fuseable.SimplePlainQueue;
import io.reactivex.internal.queue.SpscArrayQueue;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.AtomicThrowable;
import io.reactivex.internal.util.BackpressureHelper;
import io.reactivex.internal.util.ErrorMode;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FlowableConcatMapMaybe<T, R> extends Flowable<R> {
    final ErrorMode errorMode;
    final Function<? super T, ? extends MaybeSource<? extends R>> mapper;
    final int prefetch;
    final Flowable<T> source;

    static final class ConcatMapMaybeSubscriber<T, R> extends AtomicInteger implements FlowableSubscriber<T>, Subscription {
        static final int STATE_ACTIVE = 1;
        static final int STATE_INACTIVE = 0;
        static final int STATE_RESULT_VALUE = 2;
        private static final long serialVersionUID = -9140123220065488293L;
        volatile boolean cancelled;
        int consumed;
        volatile boolean done;
        final Subscriber<? super R> downstream;
        long emitted;
        final ErrorMode errorMode;
        final AtomicThrowable errors = new AtomicThrowable();
        final ConcatMapMaybeObserver<R> inner = new ConcatMapMaybeObserver(this);
        R item;
        final Function<? super T, ? extends MaybeSource<? extends R>> mapper;
        final int prefetch;
        final SimplePlainQueue<T> queue;
        final AtomicLong requested = new AtomicLong();
        volatile int state;
        Subscription upstream;

        static final class ConcatMapMaybeObserver<R> extends AtomicReference<Disposable> implements MaybeObserver<R> {
            private static final long serialVersionUID = -3051469169682093892L;
            final ConcatMapMaybeSubscriber<?, R> parent;

            ConcatMapMaybeObserver(ConcatMapMaybeSubscriber<?, R> parent) {
                this.parent = parent;
            }

            public void onSubscribe(Disposable d) {
                DisposableHelper.replace(this, d);
            }

            public void onSuccess(R t) {
                this.parent.innerSuccess(t);
            }

            public void onError(Throwable e) {
                this.parent.innerError(e);
            }

            public void onComplete() {
                this.parent.innerComplete();
            }

            void dispose() {
                DisposableHelper.dispose(this);
            }
        }

        ConcatMapMaybeSubscriber(Subscriber<? super R> downstream, Function<? super T, ? extends MaybeSource<? extends R>> mapper, int prefetch, ErrorMode errorMode) {
            this.downstream = downstream;
            this.mapper = mapper;
            this.prefetch = prefetch;
            this.errorMode = errorMode;
            this.queue = new SpscArrayQueue(prefetch);
        }

        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.validate(this.upstream, s)) {
                this.upstream = s;
                this.downstream.onSubscribe(this);
                s.request((long) this.prefetch);
            }
        }

        public void onNext(T t) {
            if (this.queue.offer(t)) {
                drain();
                return;
            }
            this.upstream.cancel();
            onError(new MissingBackpressureException("queue full?!"));
        }

        public void onError(Throwable t) {
            if (this.errors.addThrowable(t)) {
                if (this.errorMode == ErrorMode.IMMEDIATE) {
                    this.inner.dispose();
                }
                this.done = true;
                drain();
                return;
            }
            RxJavaPlugins.onError(t);
        }

        public void onComplete() {
            this.done = true;
            drain();
        }

        public void request(long n) {
            BackpressureHelper.add(this.requested, n);
            drain();
        }

        public void cancel() {
            this.cancelled = true;
            this.upstream.cancel();
            this.inner.dispose();
            if (getAndIncrement() == 0) {
                this.queue.clear();
                this.item = null;
            }
        }

        void innerSuccess(R item) {
            this.item = item;
            this.state = 2;
            drain();
        }

        void innerComplete() {
            this.state = 0;
            drain();
        }

        void innerError(Throwable ex) {
            if (this.errors.addThrowable(ex)) {
                if (this.errorMode != ErrorMode.END) {
                    this.upstream.cancel();
                }
                this.state = 0;
                drain();
                return;
            }
            RxJavaPlugins.onError(ex);
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        void drain() {
            /*
            r16 = this;
            r1 = r16;
            r0 = r16.getAndIncrement();
            if (r0 == 0) goto L_0x0009;
        L_0x0008:
            return;
        L_0x0009:
            r0 = 1;
            r2 = r1.downstream;
            r3 = r1.errorMode;
            r4 = r1.queue;
            r5 = r1.errors;
            r6 = r1.requested;
            r7 = r1.prefetch;
            r8 = r7 >> 1;
            r7 = r7 - r8;
            r8 = r0;
        L_0x001a:
            r0 = r1.cancelled;
            r9 = 0;
            if (r0 == 0) goto L_0x0026;
        L_0x001f:
            r4.clear();
            r1.item = r9;
            goto L_0x00cd;
        L_0x0026:
            r10 = r1.state;
            r0 = r5.get();
            if (r0 == 0) goto L_0x0047;
        L_0x002e:
            r0 = io.reactivex.internal.util.ErrorMode.IMMEDIATE;
            if (r3 == r0) goto L_0x003a;
        L_0x0032:
            r0 = io.reactivex.internal.util.ErrorMode.BOUNDARY;
            if (r3 != r0) goto L_0x0039;
        L_0x0036:
            if (r10 != 0) goto L_0x0039;
        L_0x0038:
            goto L_0x003a;
        L_0x0039:
            goto L_0x0048;
        L_0x003a:
            r4.clear();
            r1.item = r9;
            r0 = r5.terminate();
            r2.onError(r0);
            return;
        L_0x0048:
            r0 = 0;
            if (r10 != 0) goto L_0x00ae;
        L_0x004b:
            r9 = r1.done;
            r11 = r4.poll();
            r12 = 1;
            if (r11 != 0) goto L_0x0056;
        L_0x0054:
            r13 = 1;
            goto L_0x0057;
        L_0x0056:
            r13 = 0;
        L_0x0057:
            if (r9 == 0) goto L_0x0069;
        L_0x0059:
            if (r13 == 0) goto L_0x0069;
        L_0x005b:
            r0 = r5.terminate();
            if (r0 != 0) goto L_0x0065;
        L_0x0061:
            r2.onComplete();
            goto L_0x0068;
        L_0x0065:
            r2.onError(r0);
        L_0x0068:
            return;
            if (r13 == 0) goto L_0x006d;
        L_0x006c:
            goto L_0x00cd;
        L_0x006d:
            r14 = r1.consumed;
            r14 = r14 + r12;
            if (r14 != r7) goto L_0x007c;
        L_0x0072:
            r1.consumed = r0;
            r0 = r1.upstream;
            r15 = r13;
            r12 = (long) r7;
            r0.request(r12);
            goto L_0x007f;
        L_0x007c:
            r15 = r13;
            r1.consumed = r14;
        L_0x007f:
            r0 = r1.mapper;	 Catch:{ Throwable -> 0x0097 }
            r0 = r0.apply(r11);	 Catch:{ Throwable -> 0x0097 }
            r12 = "The mapper returned a null MaybeSource";
            r0 = io.reactivex.internal.functions.ObjectHelper.requireNonNull(r0, r12);	 Catch:{ Throwable -> 0x0097 }
            r0 = (io.reactivex.MaybeSource) r0;	 Catch:{ Throwable -> 0x0097 }
            r12 = 1;
            r1.state = r12;
            r12 = r1.inner;
            r0.subscribe(r12);
            goto L_0x00cd;
        L_0x0097:
            r0 = move-exception;
            io.reactivex.exceptions.Exceptions.throwIfFatal(r0);
            r12 = r1.upstream;
            r12.cancel();
            r4.clear();
            r5.addThrowable(r0);
            r0 = r5.terminate();
            r2.onError(r0);
            return;
        L_0x00ae:
            r11 = 2;
            if (r10 != r11) goto L_0x00cc;
        L_0x00b1:
            r11 = r1.emitted;
            r13 = r6.get();
            r15 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1));
            if (r15 == 0) goto L_0x00cb;
        L_0x00bb:
            r13 = r1.item;
            r1.item = r9;
            r2.onNext(r13);
            r14 = 1;
            r14 = r14 + r11;
            r1.emitted = r14;
            r1.state = r0;
            goto L_0x001a;
        L_0x00cb:
            goto L_0x00cd;
        L_0x00cd:
            r0 = -r8;
            r8 = r1.addAndGet(r0);
            if (r8 != 0) goto L_0x00d6;
            return;
        L_0x00d6:
            goto L_0x001a;
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.mixed.FlowableConcatMapMaybe.ConcatMapMaybeSubscriber.drain():void");
        }
    }

    public FlowableConcatMapMaybe(Flowable<T> source, Function<? super T, ? extends MaybeSource<? extends R>> mapper, ErrorMode errorMode, int prefetch) {
        this.source = source;
        this.mapper = mapper;
        this.errorMode = errorMode;
        this.prefetch = prefetch;
    }

    protected void subscribeActual(Subscriber<? super R> s) {
        this.source.subscribe(new ConcatMapMaybeSubscriber(s, this.mapper, this.prefetch, this.errorMode));
    }
}
