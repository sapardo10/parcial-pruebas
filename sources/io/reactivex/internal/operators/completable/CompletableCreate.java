package io.reactivex.internal.operators.completable;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Cancellable;
import io.reactivex.internal.disposables.CancellableDisposable;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicReference;

public final class CompletableCreate extends Completable {
    final CompletableOnSubscribe source;

    static final class Emitter extends AtomicReference<Disposable> implements CompletableEmitter, Disposable {
        private static final long serialVersionUID = -2467358622224974244L;
        final CompletableObserver downstream;

        Emitter(CompletableObserver downstream) {
            this.downstream = downstream;
        }

        public void onComplete() {
            if (get() != DisposableHelper.DISPOSED) {
                Disposable d = (Disposable) getAndSet(DisposableHelper.DISPOSED);
                if (d != DisposableHelper.DISPOSED) {
                    try {
                        this.downstream.onComplete();
                        if (d != null) {
                            d.dispose();
                        }
                    } catch (Throwable th) {
                        if (d != null) {
                            d.dispose();
                        }
                    }
                }
            }
        }

        public void onError(Throwable t) {
            if (!tryOnError(t)) {
                RxJavaPlugins.onError(t);
            }
        }

        public boolean tryOnError(Throwable t) {
            if (t == null) {
                t = new NullPointerException("onError called with null. Null values are generally not allowed in 2.x operators and sources.");
            }
            if (get() != DisposableHelper.DISPOSED) {
                Disposable d = (Disposable) getAndSet(DisposableHelper.DISPOSED);
                if (d != DisposableHelper.DISPOSED) {
                    try {
                        this.downstream.onError(t);
                        if (d != null) {
                            d.dispose();
                        }
                        return true;
                    } catch (Throwable th) {
                        if (d != null) {
                            d.dispose();
                        }
                    }
                }
            }
            return false;
        }

        public void setDisposable(Disposable d) {
            DisposableHelper.set(this, d);
        }

        public void setCancellable(Cancellable c) {
            setDisposable(new CancellableDisposable(c));
        }

        public void dispose() {
            DisposableHelper.dispose(this);
        }

        public boolean isDisposed() {
            return DisposableHelper.isDisposed((Disposable) get());
        }

        public String toString() {
            return String.format("%s{%s}", new Object[]{getClass().getSimpleName(), super.toString()});
        }
    }

    public CompletableCreate(CompletableOnSubscribe source) {
        this.source = source;
    }

    protected void subscribeActual(CompletableObserver observer) {
        Emitter parent = new Emitter(observer);
        observer.onSubscribe(parent);
        try {
            this.source.subscribe(parent);
        } catch (Throwable ex) {
            Exceptions.throwIfFatal(ex);
            parent.onError(ex);
        }
    }
}
