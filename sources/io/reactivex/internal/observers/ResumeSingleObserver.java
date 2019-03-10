package io.reactivex.internal.observers;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import java.util.concurrent.atomic.AtomicReference;

public final class ResumeSingleObserver<T> implements SingleObserver<T> {
    final SingleObserver<? super T> downstream;
    final AtomicReference<Disposable> parent;

    public ResumeSingleObserver(AtomicReference<Disposable> parent, SingleObserver<? super T> downstream) {
        this.parent = parent;
        this.downstream = downstream;
    }

    public void onSubscribe(Disposable d) {
        DisposableHelper.replace(this.parent, d);
    }

    public void onSuccess(T value) {
        this.downstream.onSuccess(value);
    }

    public void onError(Throwable e) {
        this.downstream.onError(e);
    }
}
