package io.reactivex.internal.operators.single;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;

public final class SingleHide<T> extends Single<T> {
    final SingleSource<? extends T> source;

    static final class HideSingleObserver<T> implements SingleObserver<T>, Disposable {
        final SingleObserver<? super T> downstream;
        Disposable upstream;

        HideSingleObserver(SingleObserver<? super T> downstream) {
            this.downstream = downstream;
        }

        public void dispose() {
            this.upstream.dispose();
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
            this.downstream.onSuccess(value);
        }

        public void onError(Throwable e) {
            this.downstream.onError(e);
        }
    }

    public SingleHide(SingleSource<? extends T> source) {
        this.source = source;
    }

    protected void subscribeActual(SingleObserver<? super T> observer) {
        this.source.subscribe(new HideSingleObserver(observer));
    }
}
