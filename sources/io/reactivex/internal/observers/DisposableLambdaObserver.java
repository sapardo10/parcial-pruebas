package io.reactivex.internal.observers;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.plugins.RxJavaPlugins;

public final class DisposableLambdaObserver<T> implements Observer<T>, Disposable {
    final Observer<? super T> downstream;
    final Action onDispose;
    final Consumer<? super Disposable> onSubscribe;
    Disposable upstream;

    public DisposableLambdaObserver(Observer<? super T> actual, Consumer<? super Disposable> onSubscribe, Action onDispose) {
        this.downstream = actual;
        this.onSubscribe = onSubscribe;
        this.onDispose = onDispose;
    }

    public void onSubscribe(Disposable d) {
        try {
            this.onSubscribe.accept(d);
            if (DisposableHelper.validate(this.upstream, d)) {
                this.upstream = d;
                this.downstream.onSubscribe(this);
            }
        } catch (Throwable e) {
            Exceptions.throwIfFatal(e);
            d.dispose();
            this.upstream = DisposableHelper.DISPOSED;
            EmptyDisposable.error(e, this.downstream);
        }
    }

    public void onNext(T t) {
        this.downstream.onNext(t);
    }

    public void onError(Throwable t) {
        if (this.upstream != DisposableHelper.DISPOSED) {
            this.downstream.onError(t);
        } else {
            RxJavaPlugins.onError(t);
        }
    }

    public void onComplete() {
        if (this.upstream != DisposableHelper.DISPOSED) {
            this.downstream.onComplete();
        }
    }

    public void dispose() {
        try {
            this.onDispose.run();
        } catch (Throwable e) {
            Exceptions.throwIfFatal(e);
            RxJavaPlugins.onError(e);
        }
        this.upstream.dispose();
    }

    public boolean isDisposed() {
        return this.upstream.isDisposed();
    }
}
