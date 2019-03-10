package io.reactivex.internal.operators.single;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.plugins.RxJavaPlugins;

public final class SingleDoOnSubscribe<T> extends Single<T> {
    final Consumer<? super Disposable> onSubscribe;
    final SingleSource<T> source;

    static final class DoOnSubscribeSingleObserver<T> implements SingleObserver<T> {
        boolean done;
        final SingleObserver<? super T> downstream;
        final Consumer<? super Disposable> onSubscribe;

        DoOnSubscribeSingleObserver(SingleObserver<? super T> actual, Consumer<? super Disposable> onSubscribe) {
            this.downstream = actual;
            this.onSubscribe = onSubscribe;
        }

        public void onSubscribe(Disposable d) {
            try {
                this.onSubscribe.accept(d);
                this.downstream.onSubscribe(d);
            } catch (Throwable ex) {
                Exceptions.throwIfFatal(ex);
                this.done = true;
                d.dispose();
                EmptyDisposable.error(ex, this.downstream);
            }
        }

        public void onSuccess(T value) {
            if (!this.done) {
                this.downstream.onSuccess(value);
            }
        }

        public void onError(Throwable e) {
            if (this.done) {
                RxJavaPlugins.onError(e);
            } else {
                this.downstream.onError(e);
            }
        }
    }

    public SingleDoOnSubscribe(SingleSource<T> source, Consumer<? super Disposable> onSubscribe) {
        this.source = source;
        this.onSubscribe = onSubscribe;
    }

    protected void subscribeActual(SingleObserver<? super T> observer) {
        this.source.subscribe(new DoOnSubscribeSingleObserver(observer, this.onSubscribe));
    }
}
