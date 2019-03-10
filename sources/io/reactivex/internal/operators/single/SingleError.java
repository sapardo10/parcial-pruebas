package io.reactivex.internal.operators.single;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.internal.functions.ObjectHelper;
import java.util.concurrent.Callable;

public final class SingleError<T> extends Single<T> {
    final Callable<? extends Throwable> errorSupplier;

    public SingleError(Callable<? extends Throwable> errorSupplier) {
        this.errorSupplier = errorSupplier;
    }

    protected void subscribeActual(SingleObserver<? super T> observer) {
        Throwable error;
        try {
            error = (Throwable) ObjectHelper.requireNonNull(this.errorSupplier.call(), "Callable returned null throwable. Null values are generally not allowed in 2.x operators and sources.");
        } catch (Throwable th) {
            error = th;
            Exceptions.throwIfFatal(error);
        }
        EmptyDisposable.error(error, (SingleObserver) observer);
    }
}
