package io.reactivex.internal.operators.mixed;

import io.reactivex.MaybeObserver;
import io.reactivex.MaybeSource;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.fuseable.SimplePlainQueue;
import io.reactivex.internal.queue.SpscLinkedArrayQueue;
import io.reactivex.internal.util.AtomicThrowable;
import io.reactivex.internal.util.ErrorMode;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class ObservableConcatMapMaybe<T, R> extends Observable<R> {
    final ErrorMode errorMode;
    final Function<? super T, ? extends MaybeSource<? extends R>> mapper;
    final int prefetch;
    final Observable<T> source;

    static final class ConcatMapMaybeMainObserver<T, R> extends AtomicInteger implements Observer<T>, Disposable {
        static final int STATE_ACTIVE = 1;
        static final int STATE_INACTIVE = 0;
        static final int STATE_RESULT_VALUE = 2;
        private static final long serialVersionUID = -9140123220065488293L;
        volatile boolean cancelled;
        volatile boolean done;
        final Observer<? super R> downstream;
        final ErrorMode errorMode;
        final AtomicThrowable errors = new AtomicThrowable();
        final ConcatMapMaybeObserver<R> inner = new ConcatMapMaybeObserver(this);
        R item;
        final Function<? super T, ? extends MaybeSource<? extends R>> mapper;
        final SimplePlainQueue<T> queue;
        volatile int state;
        Disposable upstream;

        static final class ConcatMapMaybeObserver<R> extends AtomicReference<Disposable> implements MaybeObserver<R> {
            private static final long serialVersionUID = -3051469169682093892L;
            final ConcatMapMaybeMainObserver<?, R> parent;

            ConcatMapMaybeObserver(ConcatMapMaybeMainObserver<?, R> parent) {
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

        ConcatMapMaybeMainObserver(Observer<? super R> downstream, Function<? super T, ? extends MaybeSource<? extends R>> mapper, int prefetch, ErrorMode errorMode) {
            this.downstream = downstream;
            this.mapper = mapper;
            this.errorMode = errorMode;
            this.queue = new SpscLinkedArrayQueue(prefetch);
        }

        public void onSubscribe(Disposable d) {
            if (DisposableHelper.validate(this.upstream, d)) {
                this.upstream = d;
                this.downstream.onSubscribe(this);
            }
        }

        public void onNext(T t) {
            this.queue.offer(t);
            drain();
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

        public void dispose() {
            this.cancelled = true;
            this.upstream.dispose();
            this.inner.dispose();
            if (getAndIncrement() == 0) {
                this.queue.clear();
                this.item = null;
            }
        }

        public boolean isDisposed() {
            return this.cancelled;
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
                    this.upstream.dispose();
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
            r12 = this;
            r0 = r12.getAndIncrement();
            if (r0 == 0) goto L_0x0007;
        L_0x0006:
            return;
        L_0x0007:
            r0 = 1;
            r1 = r12.downstream;
            r2 = r12.errorMode;
            r3 = r12.queue;
            r4 = r12.errors;
        L_0x0010:
            r5 = r12.cancelled;
            r6 = 0;
            if (r5 == 0) goto L_0x001c;
        L_0x0015:
            r3.clear();
            r12.item = r6;
            goto L_0x009f;
        L_0x001c:
            r5 = r12.state;
            r7 = r4.get();
            if (r7 == 0) goto L_0x003d;
        L_0x0024:
            r7 = io.reactivex.internal.util.ErrorMode.IMMEDIATE;
            if (r2 == r7) goto L_0x0030;
        L_0x0028:
            r7 = io.reactivex.internal.util.ErrorMode.BOUNDARY;
            if (r2 != r7) goto L_0x002f;
        L_0x002c:
            if (r5 != 0) goto L_0x002f;
        L_0x002e:
            goto L_0x0030;
        L_0x002f:
            goto L_0x003e;
        L_0x0030:
            r3.clear();
            r12.item = r6;
            r6 = r4.terminate();
            r1.onError(r6);
            return;
        L_0x003e:
            r7 = 0;
            if (r5 != 0) goto L_0x0090;
        L_0x0041:
            r6 = r12.done;
            r8 = r3.poll();
            r9 = 1;
            if (r8 != 0) goto L_0x004c;
        L_0x004a:
            r7 = 1;
        L_0x004c:
            if (r6 == 0) goto L_0x005e;
        L_0x004e:
            if (r7 == 0) goto L_0x005e;
        L_0x0050:
            r9 = r4.terminate();
            if (r9 != 0) goto L_0x005a;
        L_0x0056:
            r1.onComplete();
            goto L_0x005d;
        L_0x005a:
            r1.onError(r9);
        L_0x005d:
            return;
            if (r7 == 0) goto L_0x0062;
        L_0x0061:
            goto L_0x009f;
        L_0x0062:
            r10 = r12.mapper;	 Catch:{ Throwable -> 0x0079 }
            r10 = r10.apply(r8);	 Catch:{ Throwable -> 0x0079 }
            r11 = "The mapper returned a null MaybeSource";
            r10 = io.reactivex.internal.functions.ObjectHelper.requireNonNull(r10, r11);	 Catch:{ Throwable -> 0x0079 }
            r10 = (io.reactivex.MaybeSource) r10;	 Catch:{ Throwable -> 0x0079 }
            r12.state = r9;
            r9 = r12.inner;
            r10.subscribe(r9);
            goto L_0x009f;
        L_0x0079:
            r9 = move-exception;
            io.reactivex.exceptions.Exceptions.throwIfFatal(r9);
            r10 = r12.upstream;
            r10.dispose();
            r3.clear();
            r4.addThrowable(r9);
            r9 = r4.terminate();
            r1.onError(r9);
            return;
        L_0x0090:
            r8 = 2;
            if (r5 != r8) goto L_0x009e;
        L_0x0093:
            r8 = r12.item;
            r12.item = r6;
            r1.onNext(r8);
            r12.state = r7;
            goto L_0x0010;
        L_0x009f:
            r5 = -r0;
            r0 = r12.addAndGet(r5);
            if (r0 != 0) goto L_0x00a8;
            return;
        L_0x00a8:
            goto L_0x0010;
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.mixed.ObservableConcatMapMaybe.ConcatMapMaybeMainObserver.drain():void");
        }
    }

    public ObservableConcatMapMaybe(Observable<T> source, Function<? super T, ? extends MaybeSource<? extends R>> mapper, ErrorMode errorMode, int prefetch) {
        this.source = source;
        this.mapper = mapper;
        this.errorMode = errorMode;
        this.prefetch = prefetch;
    }

    protected void subscribeActual(Observer<? super R> observer) {
        if (!ScalarXMapZHelper.tryAsMaybe(this.source, this.mapper, observer)) {
            this.source.subscribe(new ConcatMapMaybeMainObserver(observer, this.mapper, this.prefetch, this.errorMode));
        }
    }
}
