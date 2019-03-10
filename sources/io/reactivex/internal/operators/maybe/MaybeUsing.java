package io.reactivex.internal.operators.maybe;

import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;
import io.reactivex.MaybeSource;
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

public final class MaybeUsing<T, D> extends Maybe<T> {
    final boolean eager;
    final Consumer<? super D> resourceDisposer;
    final Callable<? extends D> resourceSupplier;
    final Function<? super D, ? extends MaybeSource<? extends T>> sourceSupplier;

    static final class UsingObserver<T, D> extends AtomicReference<Object> implements MaybeObserver<T>, Disposable {
        private static final long serialVersionUID = -674404550052917487L;
        final Consumer<? super D> disposer;
        final MaybeObserver<? super T> downstream;
        final boolean eager;
        Disposable upstream;

        UsingObserver(MaybeObserver<? super T> actual, D resource, Consumer<? super D> disposer, boolean eager) {
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

        public void onSuccess(T value) {
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
            this.downstream.onSuccess(value);
            if (!this.eager) {
                disposeResourceAfter();
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

    public MaybeUsing(Callable<? extends D> resourceSupplier, Function<? super D, ? extends MaybeSource<? extends T>> sourceSupplier, Consumer<? super D> resourceDisposer, boolean eager) {
        this.resourceSupplier = resourceSupplier;
        this.sourceSupplier = sourceSupplier;
        this.resourceDisposer = resourceDisposer;
        this.eager = eager;
    }

    protected void subscribeActual(MaybeObserver<? super T> observer) {
        try {
            D resource = this.resourceSupplier.call();
            try {
                ((MaybeSource) ObjectHelper.requireNonNull(this.sourceSupplier.apply(resource), "The sourceSupplier returned a null MaybeSource")).subscribe(new UsingObserver(observer, resource, this.resourceDisposer, this.eager));
            } catch (Throwable exc) {
                Exceptions.throwIfFatal(exc);
                RxJavaPlugins.onError(exc);
            }
        } catch (Throwable ex) {
            Exceptions.throwIfFatal(ex);
            EmptyDisposable.error(ex, (MaybeObserver) observer);
        }
    }
}
