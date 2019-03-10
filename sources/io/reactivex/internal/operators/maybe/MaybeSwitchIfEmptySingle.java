package io.reactivex.internal.operators.maybe;

import io.reactivex.MaybeObserver;
import io.reactivex.MaybeSource;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.fuseable.HasUpstreamMaybeSource;
import java.util.concurrent.atomic.AtomicReference;

public final class MaybeSwitchIfEmptySingle<T> extends Single<T> implements HasUpstreamMaybeSource<T> {
    final SingleSource<? extends T> other;
    final MaybeSource<T> source;

    static final class SwitchIfEmptyMaybeObserver<T> extends AtomicReference<Disposable> implements MaybeObserver<T>, Disposable {
        private static final long serialVersionUID = 4603919676453758899L;
        final SingleObserver<? super T> downstream;
        final SingleSource<? extends T> other;

        static final class OtherSingleObserver<T> implements SingleObserver<T> {
            final SingleObserver<? super T> downstream;
            final AtomicReference<Disposable> parent;

            OtherSingleObserver(SingleObserver<? super T> actual, AtomicReference<Disposable> parent) {
                this.downstream = actual;
                this.parent = parent;
            }

            public void onSubscribe(Disposable d) {
                DisposableHelper.setOnce(this.parent, d);
            }

            public void onSuccess(T value) {
                this.downstream.onSuccess(value);
            }

            public void onError(Throwable e) {
                this.downstream.onError(e);
            }
        }

        SwitchIfEmptyMaybeObserver(SingleObserver<? super T> actual, SingleSource<? extends T> other) {
            this.downstream = actual;
            this.other = other;
        }

        public void dispose() {
            DisposableHelper.dispose(this);
        }

        public boolean isDisposed() {
            return DisposableHelper.isDisposed((Disposable) get());
        }

        public void onSubscribe(Disposable d) {
            if (DisposableHelper.setOnce(this, d)) {
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
            Disposable d = (Disposable) get();
            if (d == DisposableHelper.DISPOSED) {
                return;
            }
            if (compareAndSet(d, null)) {
                this.other.subscribe(new OtherSingleObserver(this.downstream, this));
            }
        }
    }

    public MaybeSwitchIfEmptySingle(MaybeSource<T> source, SingleSource<? extends T> other) {
        this.source = source;
        this.other = other;
    }

    public MaybeSource<T> source() {
        return this.source;
    }

    protected void subscribeActual(SingleObserver<? super T> observer) {
        this.source.subscribe(new SwitchIfEmptyMaybeObserver(observer, this.other));
    }
}
