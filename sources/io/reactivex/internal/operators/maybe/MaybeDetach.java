package io.reactivex.internal.operators.maybe;

import io.reactivex.MaybeObserver;
import io.reactivex.MaybeSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;

public final class MaybeDetach<T> extends AbstractMaybeWithUpstream<T, T> {

    static final class DetachMaybeObserver<T> implements MaybeObserver<T>, Disposable {
        MaybeObserver<? super T> downstream;
        Disposable upstream;

        DetachMaybeObserver(MaybeObserver<? super T> downstream) {
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
            MaybeObserver<? super T> a = this.downstream;
            if (a != null) {
                this.downstream = null;
                a.onSuccess(value);
            }
        }

        public void onError(Throwable e) {
            this.upstream = DisposableHelper.DISPOSED;
            MaybeObserver<? super T> a = this.downstream;
            if (a != null) {
                this.downstream = null;
                a.onError(e);
            }
        }

        public void onComplete() {
            this.upstream = DisposableHelper.DISPOSED;
            MaybeObserver<? super T> a = this.downstream;
            if (a != null) {
                this.downstream = null;
                a.onComplete();
            }
        }
    }

    public MaybeDetach(MaybeSource<T> source) {
        super(source);
    }

    protected void subscribeActual(MaybeObserver<? super T> observer) {
        this.source.subscribe(new DetachMaybeObserver(observer));
    }
}
