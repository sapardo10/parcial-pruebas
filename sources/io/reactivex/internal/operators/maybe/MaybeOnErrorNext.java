package io.reactivex.internal.operators.maybe;

import io.reactivex.MaybeObserver;
import io.reactivex.MaybeSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Function;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.functions.ObjectHelper;
import java.util.concurrent.atomic.AtomicReference;

public final class MaybeOnErrorNext<T> extends AbstractMaybeWithUpstream<T, T> {
    final boolean allowFatal;
    final Function<? super Throwable, ? extends MaybeSource<? extends T>> resumeFunction;

    static final class OnErrorNextMaybeObserver<T> extends AtomicReference<Disposable> implements MaybeObserver<T>, Disposable {
        private static final long serialVersionUID = 2026620218879969836L;
        final boolean allowFatal;
        final MaybeObserver<? super T> downstream;
        final Function<? super Throwable, ? extends MaybeSource<? extends T>> resumeFunction;

        static final class NextMaybeObserver<T> implements MaybeObserver<T> {
            final MaybeObserver<? super T> downstream;
            final AtomicReference<Disposable> upstream;

            NextMaybeObserver(MaybeObserver<? super T> actual, AtomicReference<Disposable> d) {
                this.downstream = actual;
                this.upstream = d;
            }

            public void onSubscribe(Disposable d) {
                DisposableHelper.setOnce(this.upstream, d);
            }

            public void onSuccess(T value) {
                this.downstream.onSuccess(value);
            }

            public void onError(Throwable e) {
                this.downstream.onError(e);
            }

            public void onComplete() {
                this.downstream.onComplete();
            }
        }

        OnErrorNextMaybeObserver(MaybeObserver<? super T> actual, Function<? super Throwable, ? extends MaybeSource<? extends T>> resumeFunction, boolean allowFatal) {
            this.downstream = actual;
            this.resumeFunction = resumeFunction;
            this.allowFatal = allowFatal;
        }

        public void dispose() {
            DisposableHelper.dispose(this);
        }

        public boolean isDisposed() {
            return DisposableHelper.isDisposed((Disposable) get());
        }

        public void onSubscribe(Disposable d) {
            if (DisposableHelper.setOnce(this, d)) {
                this.downstream.onSubscribe(this);
            }
        }

        public void onSuccess(T value) {
            this.downstream.onSuccess(value);
        }

        public void onError(Throwable e) {
            if (this.allowFatal || (e instanceof Exception)) {
                try {
                    MaybeSource<? extends T> m = (MaybeSource) ObjectHelper.requireNonNull(this.resumeFunction.apply(e), "The resumeFunction returned a null MaybeSource");
                    DisposableHelper.replace(this, null);
                    m.subscribe(new NextMaybeObserver(this.downstream, this));
                    return;
                } catch (Throwable ex) {
                    Exceptions.throwIfFatal(ex);
                    this.downstream.onError(new CompositeException(e, ex));
                    return;
                }
            }
            this.downstream.onError(e);
        }

        public void onComplete() {
            this.downstream.onComplete();
        }
    }

    public MaybeOnErrorNext(MaybeSource<T> source, Function<? super Throwable, ? extends MaybeSource<? extends T>> resumeFunction, boolean allowFatal) {
        super(source);
        this.resumeFunction = resumeFunction;
        this.allowFatal = allowFatal;
    }

    protected void subscribeActual(MaybeObserver<? super T> observer) {
        this.source.subscribe(new OnErrorNextMaybeObserver(observer, this.resumeFunction, this.allowFatal));
    }
}
