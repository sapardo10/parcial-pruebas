package io.reactivex.internal.operators.completable;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Action;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicInteger;

public final class CompletableDoFinally extends Completable {
    final Action onFinally;
    final CompletableSource source;

    static final class DoFinallyObserver extends AtomicInteger implements CompletableObserver, Disposable {
        private static final long serialVersionUID = 4109457741734051389L;
        final CompletableObserver downstream;
        final Action onFinally;
        Disposable upstream;

        DoFinallyObserver(CompletableObserver actual, Action onFinally) {
            this.downstream = actual;
            this.onFinally = onFinally;
        }

        public void onSubscribe(Disposable d) {
            if (DisposableHelper.validate(this.upstream, d)) {
                this.upstream = d;
                this.downstream.onSubscribe(this);
            }
        }

        public void onError(Throwable t) {
            this.downstream.onError(t);
            runFinally();
        }

        public void onComplete() {
            this.downstream.onComplete();
            runFinally();
        }

        public void dispose() {
            this.upstream.dispose();
            runFinally();
        }

        public boolean isDisposed() {
            return this.upstream.isDisposed();
        }

        void runFinally() {
            if (compareAndSet(0, 1)) {
                try {
                    this.onFinally.run();
                } catch (Throwable ex) {
                    Exceptions.throwIfFatal(ex);
                    RxJavaPlugins.onError(ex);
                }
            }
        }
    }

    public CompletableDoFinally(CompletableSource source, Action onFinally) {
        this.source = source;
        this.onFinally = onFinally;
    }

    protected void subscribeActual(CompletableObserver observer) {
        this.source.subscribe(new DoFinallyObserver(observer, this.onFinally));
    }
}
