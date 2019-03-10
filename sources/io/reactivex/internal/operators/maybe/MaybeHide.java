package io.reactivex.internal.operators.maybe;

import io.reactivex.MaybeObserver;
import io.reactivex.MaybeSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;

public final class MaybeHide<T> extends AbstractMaybeWithUpstream<T, T> {

    static final class HideMaybeObserver<T> implements MaybeObserver<T>, Disposable {
        final MaybeObserver<? super T> downstream;
        Disposable upstream;

        HideMaybeObserver(MaybeObserver<? super T> downstream) {
            this.downstream = downstream;
        }

        public void dispose() {
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
            this.downstream.onSuccess(value);
        }

        public void onError(Throwable e) {
            this.downstream.onError(e);
        }

        public void onComplete() {
            this.downstream.onComplete();
        }
    }

    public MaybeHide(MaybeSource<T> source) {
        super(source);
    }

    protected void subscribeActual(MaybeObserver<? super T> observer) {
        this.source.subscribe(new HideMaybeObserver(observer));
    }
}
