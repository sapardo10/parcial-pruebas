package io.reactivex.internal.operators.completable;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

public final class CompletableUsing<R> extends Completable {
    final Function<? super R, ? extends CompletableSource> completableFunction;
    final Consumer<? super R> disposer;
    final boolean eager;
    final Callable<R> resourceSupplier;

    static final class UsingObserver<R> extends AtomicReference<Object> implements CompletableObserver, Disposable {
        private static final long serialVersionUID = -674404550052917487L;
        final Consumer<? super R> disposer;
        final CompletableObserver downstream;
        final boolean eager;
        Disposable upstream;

        UsingObserver(CompletableObserver actual, R resource, Consumer<? super R> disposer, boolean eager) {
            super(resource);
            this.downstream = actual;
            this.disposer = disposer;
            this.eager = eager;
        }

        public void dispose() {
            this.upstream.dispose();
            this.upstream = DisposableHelper.DISPOSED;
            disposeResourceAfter();
        }

        void disposeResourceAfter() {
            UsingObserver resource = getAndSet(this);
            if (resource != this) {
                try {
                    this.disposer.accept(resource);
                } catch (Throwable ex) {
                    Exceptions.throwIfFatal(ex);
                    RxJavaPlugins.onError(ex);
                }
            }
        }

        public boolean isDisposed() {
            return this.upstream.isDisposed();
        }

        public void onSubscribe(Disposable d) {
            if (DisposableHelper.validate(this.upstream, d)) {
                this.upstream = d;
                this.downstream.onSubscribe(this);
            }
        }

        public void onError(Throwable e) {
            this.upstream = DisposableHelper.DISPOSED;
            if (this.eager) {
                UsingObserver resource = getAndSet(this);
                if (resource != this) {
                    try {
                        this.disposer.accept(resource);
                    } catch (Throwable ex) {
                        Exceptions.throwIfFatal(ex);
                        e = new CompositeException(e, ex);
                    }
                } else {
                    return;
                }
            }
            this.downstream.onError(e);
            if (!this.eager) {
                disposeResourceAfter();
            }
        }

        public void onComplete() {
            this.upstream = DisposableHelper.DISPOSED;
            if (this.eager) {
                UsingObserver resource = getAndSet(this);
                if (resource != this) {
                    try {
                        this.disposer.accept(resource);
                    } catch (Throwable ex) {
                        Exceptions.throwIfFatal(ex);
                        this.downstream.onError(ex);
                        return;
                    }
                }
                return;
            }
            this.downstream.onComplete();
            if (!this.eager) {
                disposeResourceAfter();
            }
        }
    }

    public CompletableUsing(Callable<R> resourceSupplier, Function<? super R, ? extends CompletableSource> completableFunction, Consumer<? super R> disposer, boolean eager) {
        this.resourceSupplier = resourceSupplier;
        this.completableFunction = completableFunction;
        this.disposer = disposer;
        this.eager = eager;
    }

    protected void subscribeActual(CompletableObserver observer) {
        try {
            R resource = this.resourceSupplier.call();
            try {
                ((CompletableSource) ObjectHelper.requireNonNull(this.completableFunction.apply(resource), "The completableFunction returned a null CompletableSource")).subscribe(new UsingObserver(observer, resource, this.disposer, this.eager));
            } catch (Throwable exc) {
                Exceptions.throwIfFatal(exc);
                RxJavaPlugins.onError(exc);
            }
        } catch (Throwable ex) {
            Exceptions.throwIfFatal(ex);
            EmptyDisposable.error(ex, observer);
        }
    }
}
