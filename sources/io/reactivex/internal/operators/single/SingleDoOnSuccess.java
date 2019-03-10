package io.reactivex.internal.operators.single;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Consumer;

public final class SingleDoOnSuccess<T> extends Single<T> {
    final Consumer<? super T> onSuccess;
    final SingleSource<T> source;

    final class DoOnSuccess implements SingleObserver<T> {
        final SingleObserver<? super T> downstream;

        DoOnSuccess(SingleObserver<? super T> observer) {
            this.downstream = observer;
        }

        public void onSubscribe(Disposable d) {
            this.downstream.onSubscribe(d);
        }

        public void onSuccess(T value) {
            try {
                SingleDoOnSuccess.this.onSuccess.accept(value);
                this.downstream.onSuccess(value);
            } catch (Throwable ex) {
                Exceptions.throwIfFatal(ex);
                this.downstream.onError(ex);
            }
        }

        public void onError(Throwable e) {
            this.downstream.onError(e);
        }
    }

    public SingleDoOnSuccess(SingleSource<T> source, Consumer<? super T> onSuccess) {
        this.source = source;
        this.onSuccess = onSuccess;
    }

    protected void subscribeActual(SingleObserver<? super T> observer) {
        this.source.subscribe(new DoOnSuccess(observer));
    }
}
