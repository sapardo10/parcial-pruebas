package io.reactivex.internal.operators.completable;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableSource;

public final class CompletableFromUnsafeSource extends Completable {
    final CompletableSource source;

    public CompletableFromUnsafeSource(CompletableSource source) {
        this.source = source;
    }

    protected void subscribeActual(CompletableObserver observer) {
        this.source.subscribe(observer);
    }
}
