package io.reactivex.internal.operators.single;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;

public final class SingleDetach<T> extends Single<T> {
    final SingleSource<T> source;

    static final class DetachSingleObserver<T> implements SingleObserver<T>, Disposable {
        SingleObserver<? super T> downstream;
        Disposable upstream;

        DetachSingleObserver(SingleObserver<? super T> downstream) {
            this.downstream = downstream;
        }

        public void dispose() {
            this.downstream = null;
            this.upstream.dispose();
            this.upstream = DisposableHelper.DISPOSED;
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
            SingleObserver<? super T> a = this.downstream;
            if (a != null) {
                this.downstream = null;
                a.onSuccess(value);
            }
        }

        public void onError(Throwable e) {
            this.upstream = DisposableHelper.DISPOSED;
            SingleObserver<? super T> a = this.downstream;
            if (a != null) {
                this.downstream = null;
                a.onError(e);
            }
        }
    }

    public SingleDetach(SingleSource<T> source) {
        this.source = source;
    }

    protected void subscribeActual(SingleObserver<? super T> observer) {
        this.source.subscribe(new DetachSingleObserver(observer));
    }
}
