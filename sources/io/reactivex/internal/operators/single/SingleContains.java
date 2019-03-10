package io.reactivex.internal.operators.single;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.BiPredicate;

public final class SingleContains<T> extends Single<Boolean> {
    final BiPredicate<Object, Object> comparer;
    final SingleSource<T> source;
    final Object value;

    final class ContainsSingleObserver implements SingleObserver<T> {
        private final SingleObserver<? super Boolean> downstream;

        ContainsSingleObserver(SingleObserver<? super Boolean> observer) {
            this.downstream = observer;
        }

        public void onSubscribe(Disposable d) {
            this.downstream.onSubscribe(d);
        }

        public void onSuccess(T v) {
            try {
                this.downstream.onSuccess(Boolean.valueOf(SingleContains.this.comparer.test(v, SingleContains.this.value)));
            } catch (Throwable ex) {
                Exceptions.throwIfFatal(ex);
                this.downstream.onError(ex);
            }
        }

        public void onError(Throwable e) {
            this.downstream.onError(e);
        }
    }

    public SingleContains(SingleSource<T> source, Object value, BiPredicate<Object, Object> comparer) {
        this.source = source;
        this.value = value;
        this.comparer = comparer;
    }

    protected void subscribeActual(SingleObserver<? super Boolean> observer) {
        this.source.subscribe(new ContainsSingleObserver(observer));
    }
}
