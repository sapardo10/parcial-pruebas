package io.reactivex.internal.operators.completable;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableSource;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.util.AtomicThrowable;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

public final class CompletableMergeDelayErrorIterable extends Completable {
    final Iterable<? extends CompletableSource> sources;

    public CompletableMergeDelayErrorIterable(Iterable<? extends CompletableSource> sources) {
        this.sources = sources;
    }

    public void subscribeActual(CompletableObserver observer) {
        Throwable ex;
        CompositeDisposable set = new CompositeDisposable();
        observer.onSubscribe(set);
        try {
            Iterator<? extends CompletableSource> iterator = (Iterator) ObjectHelper.requireNonNull(this.sources.iterator(), "The source iterator returned is null");
            AtomicInteger wip = new AtomicInteger(1);
            AtomicThrowable error = new AtomicThrowable();
            while (!set.isDisposed()) {
                try {
                    if (!iterator.hasNext()) {
                        if (wip.decrementAndGet() == 0) {
                            ex = error.terminate();
                            if (ex == null) {
                                observer.onComplete();
                            } else {
                                observer.onError(ex);
                            }
                        }
                        return;
                    } else if (!set.isDisposed()) {
                        try {
                            CompletableSource c = (CompletableSource) ObjectHelper.requireNonNull(iterator.next(), "The iterator returned a null CompletableSource");
                            if (!set.isDisposed()) {
                                wip.getAndIncrement();
                                c.subscribe(new MergeInnerCompletableObserver(observer, set, error, wip));
                            } else {
                                return;
                            }
                        } catch (Throwable e) {
                            Exceptions.throwIfFatal(e);
                            error.addThrowable(e);
                        }
                    } else {
                        return;
                    }
                } catch (Throwable ex2) {
                    Exceptions.throwIfFatal(ex2);
                    error.addThrowable(ex2);
                }
            }
        } catch (Throwable e2) {
            Exceptions.throwIfFatal(e2);
            observer.onError(e2);
        }
    }
}
