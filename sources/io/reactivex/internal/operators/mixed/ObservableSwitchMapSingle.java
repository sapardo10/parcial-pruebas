package io.reactivex.internal.operators.mixed;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.util.AtomicThrowable;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class ObservableSwitchMapSingle<T, R> extends Observable<R> {
    final boolean delayErrors;
    final Function<? super T, ? extends SingleSource<? extends R>> mapper;
    final Observable<T> source;

    static final class SwitchMapSingleMainObserver<T, R> extends AtomicInteger implements Observer<T>, Disposable {
        static final SwitchMapSingleObserver<Object> INNER_DISPOSED = new SwitchMapSingleObserver(null);
        private static final long serialVersionUID = -5402190102429853762L;
        volatile boolean cancelled;
        final boolean delayErrors;
        volatile boolean done;
        final Observer<? super R> downstream;
        final AtomicThrowable errors = new AtomicThrowable();
        final AtomicReference<SwitchMapSingleObserver<R>> inner = new AtomicReference();
        final Function<? super T, ? extends SingleSource<? extends R>> mapper;
        Disposable upstream;

        static final class SwitchMapSingleObserver<R> extends AtomicReference<Disposable> implements SingleObserver<R> {
            private static final long serialVersionUID = 8042919737683345351L;
            volatile R item;
            final SwitchMapSingleMainObserver<?, R> parent;

            SwitchMapSingleObserver(SwitchMapSingleMainObserver<?, R> parent) {
                this.parent = parent;
            }

            public void onSubscribe(Disposable d) {
                DisposableHelper.setOnce(this, d);
            }

            public void onSuccess(R t) {
                this.item = t;
                this.parent.drain();
            }

            public void onError(Throwable e) {
                this.parent.innerError(this, e);
            }

            void dispose() {
                DisposableHelper.dispose(this);
            }
        }

        SwitchMapSingleMainObserver(Observer<? super R> downstream, Function<? super T, ? extends SingleSource<? extends R>> mapper, boolean delayErrors) {
            this.downstream = downstream;
            this.mapper = mapper;
            this.delayErrors = delayErrors;
        }

        public void onSubscribe(Disposable d) {
            if (DisposableHelper.validate(this.upstream, d)) {
                this.upstream = d;
                this.downstream.onSubscribe(this);
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onNext(T r5) {
            /*
            r4 = this;
            r0 = r4.inner;
            r0 = r0.get();
            r0 = (io.reactivex.internal.operators.mixed.ObservableSwitchMapSingle.SwitchMapSingleMainObserver.SwitchMapSingleObserver) r0;
            if (r0 == 0) goto L_0x000e;
        L_0x000a:
            r0.dispose();
            goto L_0x000f;
        L_0x000f:
            r1 = r4.mapper;	 Catch:{ Throwable -> 0x003f }
            r1 = r1.apply(r5);	 Catch:{ Throwable -> 0x003f }
            r2 = "The mapper returned a null SingleSource";
            r1 = io.reactivex.internal.functions.ObjectHelper.requireNonNull(r1, r2);	 Catch:{ Throwable -> 0x003f }
            r1 = (io.reactivex.SingleSource) r1;	 Catch:{ Throwable -> 0x003f }
            r2 = new io.reactivex.internal.operators.mixed.ObservableSwitchMapSingle$SwitchMapSingleMainObserver$SwitchMapSingleObserver;
            r2.<init>(r4);
        L_0x0023:
            r3 = r4.inner;
            r3 = r3.get();
            r0 = r3;
            r0 = (io.reactivex.internal.operators.mixed.ObservableSwitchMapSingle.SwitchMapSingleMainObserver.SwitchMapSingleObserver) r0;
            r3 = INNER_DISPOSED;
            if (r0 != r3) goto L_0x0031;
        L_0x0030:
            goto L_0x003d;
        L_0x0031:
            r3 = r4.inner;
            r3 = r3.compareAndSet(r0, r2);
            if (r3 == 0) goto L_0x003e;
        L_0x0039:
            r1.subscribe(r2);
        L_0x003d:
            return;
        L_0x003e:
            goto L_0x0023;
        L_0x003f:
            r1 = move-exception;
            io.reactivex.exceptions.Exceptions.throwIfFatal(r1);
            r2 = r4.upstream;
            r2.dispose();
            r2 = r4.inner;
            r3 = INNER_DISPOSED;
            r2.getAndSet(r3);
            r4.onError(r1);
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.mixed.ObservableSwitchMapSingle.SwitchMapSingleMainObserver.onNext(java.lang.Object):void");
        }

        public void onError(Throwable t) {
            if (this.errors.addThrowable(t)) {
                if (!this.delayErrors) {
                    disposeInner();
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

        void disposeInner() {
            SwitchMapSingleObserver<R> current = (SwitchMapSingleObserver) this.inner.getAndSet(INNER_DISPOSED);
            if (current != null && current != INNER_DISPOSED) {
                current.dispose();
            }
        }

        public void dispose() {
            this.cancelled = true;
            this.upstream.dispose();
            disposeInner();
        }

        public boolean isDisposed() {
            return this.cancelled;
        }

        void innerError(SwitchMapSingleObserver<R> sender, Throwable ex) {
            if (this.inner.compareAndSet(sender, null)) {
                if (this.errors.addThrowable(ex)) {
                    if (!this.delayErrors) {
                        this.upstream.dispose();
                        disposeInner();
                    }
                    drain();
                    return;
                }
            }
            RxJavaPlugins.onError(ex);
        }

        void drain() {
            if (getAndIncrement() == 0) {
                int missed = 1;
                Observer<? super R> downstream = this.downstream;
                AtomicThrowable errors = this.errors;
                AtomicReference<SwitchMapSingleObserver<R>> inner = this.inner;
                while (!this.cancelled) {
                    if (errors.get() != null) {
                        if (!this.delayErrors) {
                            downstream.onError(errors.terminate());
                            return;
                        }
                    }
                    boolean d = this.done;
                    SwitchMapSingleObserver<R> current = (SwitchMapSingleObserver) inner.get();
                    boolean empty = current == null;
                    if (d && empty) {
                        Throwable ex = errors.terminate();
                        if (ex != null) {
                            downstream.onError(ex);
                        } else {
                            downstream.onComplete();
                        }
                        return;
                    }
                    if (!empty) {
                        if (current.item != null) {
                            inner.compareAndSet(current, null);
                            downstream.onNext(current.item);
                        }
                    }
                    missed = addAndGet(-missed);
                    if (missed == 0) {
                        return;
                    }
                }
            }
        }
    }

    public ObservableSwitchMapSingle(Observable<T> source, Function<? super T, ? extends SingleSource<? extends R>> mapper, boolean delayErrors) {
        this.source = source;
        this.mapper = mapper;
        this.delayErrors = delayErrors;
    }

    protected void subscribeActual(Observer<? super R> observer) {
        if (!ScalarXMapZHelper.tryAsSingle(this.source, this.mapper, observer)) {
            this.source.subscribe(new SwitchMapSingleMainObserver(observer, this.mapper, this.delayErrors));
        }
    }
}
