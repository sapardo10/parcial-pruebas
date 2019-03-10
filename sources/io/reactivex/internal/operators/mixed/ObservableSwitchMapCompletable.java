package io.reactivex.internal.operators.mixed;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableSource;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.util.AtomicThrowable;
import io.reactivex.internal.util.ExceptionHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicReference;

public final class ObservableSwitchMapCompletable<T> extends Completable {
    final boolean delayErrors;
    final Function<? super T, ? extends CompletableSource> mapper;
    final Observable<T> source;

    static final class SwitchMapCompletableObserver<T> implements Observer<T>, Disposable {
        static final SwitchMapInnerObserver INNER_DISPOSED = new SwitchMapInnerObserver(null);
        final boolean delayErrors;
        volatile boolean done;
        final CompletableObserver downstream;
        final AtomicThrowable errors = new AtomicThrowable();
        final AtomicReference<SwitchMapInnerObserver> inner = new AtomicReference();
        final Function<? super T, ? extends CompletableSource> mapper;
        Disposable upstream;

        static final class SwitchMapInnerObserver extends AtomicReference<Disposable> implements CompletableObserver {
            private static final long serialVersionUID = -8003404460084760287L;
            final SwitchMapCompletableObserver<?> parent;

            SwitchMapInnerObserver(SwitchMapCompletableObserver<?> parent) {
                this.parent = parent;
            }

            public void onSubscribe(Disposable d) {
                DisposableHelper.setOnce(this, d);
            }

            public void onError(Throwable e) {
                this.parent.innerError(this, e);
            }

            public void onComplete() {
                this.parent.innerComplete(this);
            }

            void dispose() {
                DisposableHelper.dispose(this);
            }
        }

        SwitchMapCompletableObserver(CompletableObserver downstream, Function<? super T, ? extends CompletableSource> mapper, boolean delayErrors) {
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
            r0 = r4.mapper;	 Catch:{ Throwable -> 0x0037 }
            r0 = r0.apply(r5);	 Catch:{ Throwable -> 0x0037 }
            r1 = "The mapper returned a null CompletableSource";
            r0 = io.reactivex.internal.functions.ObjectHelper.requireNonNull(r0, r1);	 Catch:{ Throwable -> 0x0037 }
            r0 = (io.reactivex.CompletableSource) r0;	 Catch:{ Throwable -> 0x0037 }
            r1 = new io.reactivex.internal.operators.mixed.ObservableSwitchMapCompletable$SwitchMapCompletableObserver$SwitchMapInnerObserver;
            r1.<init>(r4);
        L_0x0014:
            r2 = r4.inner;
            r2 = r2.get();
            r2 = (io.reactivex.internal.operators.mixed.ObservableSwitchMapCompletable.SwitchMapCompletableObserver.SwitchMapInnerObserver) r2;
            r3 = INNER_DISPOSED;
            if (r2 != r3) goto L_0x0021;
        L_0x0020:
            goto L_0x0034;
        L_0x0021:
            r3 = r4.inner;
            r3 = r3.compareAndSet(r2, r1);
            if (r3 == 0) goto L_0x0035;
        L_0x0029:
            if (r2 == 0) goto L_0x002f;
        L_0x002b:
            r2.dispose();
            goto L_0x0030;
        L_0x0030:
            r0.subscribe(r1);
        L_0x0034:
            return;
            goto L_0x0014;
        L_0x0037:
            r0 = move-exception;
            io.reactivex.exceptions.Exceptions.throwIfFatal(r0);
            r1 = r4.upstream;
            r1.dispose();
            r4.onError(r0);
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.mixed.ObservableSwitchMapCompletable.SwitchMapCompletableObserver.onNext(java.lang.Object):void");
        }

        public void onError(Throwable t) {
            if (!this.errors.addThrowable(t)) {
                RxJavaPlugins.onError(t);
            } else if (this.delayErrors) {
                onComplete();
            } else {
                disposeInner();
                Throwable ex = this.errors.terminate();
                if (ex != ExceptionHelper.TERMINATED) {
                    this.downstream.onError(ex);
                }
            }
        }

        public void onComplete() {
            this.done = true;
            if (this.inner.get() == null) {
                Throwable ex = this.errors.terminate();
                if (ex == null) {
                    this.downstream.onComplete();
                } else {
                    this.downstream.onError(ex);
                }
            }
        }

        void disposeInner() {
            SwitchMapInnerObserver o = (SwitchMapInnerObserver) this.inner.getAndSet(INNER_DISPOSED);
            if (o != null && o != INNER_DISPOSED) {
                o.dispose();
            }
        }

        public void dispose() {
            this.upstream.dispose();
            disposeInner();
        }

        public boolean isDisposed() {
            return this.inner.get() == INNER_DISPOSED;
        }

        void innerError(SwitchMapInnerObserver sender, Throwable error) {
            if (this.inner.compareAndSet(sender, null)) {
                if (this.errors.addThrowable(error)) {
                    if (!this.delayErrors) {
                        dispose();
                        Throwable ex = this.errors.terminate();
                        if (ex != ExceptionHelper.TERMINATED) {
                            this.downstream.onError(ex);
                        }
                    } else if (this.done) {
                        this.downstream.onError(this.errors.terminate());
                    }
                    return;
                }
            }
            RxJavaPlugins.onError(error);
        }

        void innerComplete(SwitchMapInnerObserver sender) {
            if (!this.inner.compareAndSet(sender, null)) {
                return;
            }
            if (this.done) {
                Throwable ex = this.errors.terminate();
                if (ex == null) {
                    this.downstream.onComplete();
                } else {
                    this.downstream.onError(ex);
                }
            }
        }
    }

    public ObservableSwitchMapCompletable(Observable<T> source, Function<? super T, ? extends CompletableSource> mapper, boolean delayErrors) {
        this.source = source;
        this.mapper = mapper;
        this.delayErrors = delayErrors;
    }

    protected void subscribeActual(CompletableObserver observer) {
        if (!ScalarXMapZHelper.tryAsCompletable(this.source, this.mapper, observer)) {
            this.source.subscribe(new SwitchMapCompletableObserver(observer, this.mapper, this.delayErrors));
        }
    }
}
