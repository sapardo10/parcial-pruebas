package io.reactivex.internal.operators.maybe;

import io.reactivex.MaybeObserver;
import io.reactivex.MaybeSource;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Function;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.functions.ObjectHelper;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

public final class MaybeFlatMapSingle<T, R> extends Single<R> {
    final Function<? super T, ? extends SingleSource<? extends R>> mapper;
    final MaybeSource<T> source;

    static final class FlatMapMaybeObserver<T, R> extends AtomicReference<Disposable> implements MaybeObserver<T>, Disposable {
        private static final long serialVersionUID = 4827726964688405508L;
        final SingleObserver<? super R> downstream;
        final Function<? super T, ? extends SingleSource<? extends R>> mapper;

        FlatMapMaybeObserver(SingleObserver<? super R> actual, Function<? super T, ? extends SingleSource<? extends R>> mapper) {
            this.downstream = actual;
            this.mapper = mapper;
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
            try {
                SingleSource<? extends R> ss = (SingleSource) ObjectHelper.requireNonNull(this.mapper.apply(value), "The mapper returned a null SingleSource");
                if (!isDisposed()) {
                    ss.subscribe(new FlatMapSingleObserver(this, this.downstream));
                }
            } catch (Throwable ex) {
                Exceptions.throwIfFatal(ex);
                onError(ex);
            }
        }

        public void onError(Throwable e) {
            this.downstream.onError(e);
        }

        public void onComplete() {
            this.downstream.onError(new NoSuchElementException());
        }
    }

    static final class FlatMapSingleObserver<R> implements SingleObserver<R> {
        final SingleObserver<? super R> downstream;
        final AtomicReference<Disposable> parent;

        FlatMapSingleObserver(AtomicReference<Disposable> parent, SingleObserver<? super R> downstream) {
            this.parent = parent;
            this.downstream = downstream;
        }

        public void onSubscribe(Disposable d) {
            DisposableHelper.replace(this.parent, d);
        }

        public void onSuccess(R value) {
            this.downstream.onSuccess(value);
        }

        public void onError(Throwable e) {
            this.downstream.onError(e);
        }
    }

    public MaybeFlatMapSingle(MaybeSource<T> source, Function<? super T, ? extends SingleSource<? extends R>> mapper) {
        this.source = source;
        this.mapper = mapper;
    }

    protected void subscribeActual(SingleObserver<? super R> downstream) {
        this.source.subscribe(new FlatMapMaybeObserver(downstream, this.mapper));
    }
}
