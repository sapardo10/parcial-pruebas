package io.reactivex.internal.operators.observable;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.observers.DeferredScalarDisposable;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.Callable;

public final class ObservableFromCallable<T> extends Observable<T> implements Callable<T> {
    final Callable<? extends T> callable;

    public ObservableFromCallable(Callable<? extends T> callable) {
        this.callable = callable;
    }

    public void subscribeActual(Observer<? super T> observer) {
        DeferredScalarDisposable<T> d = new DeferredScalarDisposable(observer);
        observer.onSubscribe(d);
        if (!d.isDisposed()) {
            try {
                d.complete(ObjectHelper.requireNonNull(this.callable.call(), "Callable returned null"));
            } catch (Throwable e) {
                Exceptions.throwIfFatal(e);
                if (d.isDisposed()) {
                    RxJavaPlugins.onError(e);
                } else {
                    observer.onError(e);
                }
            }
        }
    }

    public T call() throws Exception {
        return ObjectHelper.requireNonNull(this.callable.call(), "The callable returned a null value");
    }
}
