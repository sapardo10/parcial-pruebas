package io.reactivex.internal.operators.observable;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Action;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.fuseable.QueueDisposable;
import io.reactivex.internal.observers.BasicIntQueueDisposable;
import io.reactivex.plugins.RxJavaPlugins;

public final class ObservableDoFinally<T> extends AbstractObservableWithUpstream<T, T> {
    final Action onFinally;

    static final class DoFinallyObserver<T> extends BasicIntQueueDisposable<T> implements Observer<T> {
        private static final long serialVersionUID = 4109457741734051389L;
        final Observer<? super T> downstream;
        final Action onFinally;
        QueueDisposable<T> qd;
        boolean syncFused;
        Disposable upstream;

        DoFinallyObserver(Observer<? super T> actual, Action onFinally) {
            this.downstream = actual;
            this.onFinally = onFinally;
        }

        public void onSubscribe(Disposable d) {
            if (DisposableHelper.validate(this.upstream, d)) {
                this.upstream = d;
                if (d instanceof QueueDisposable) {
                    this.qd = (QueueDisposable) d;
                }
                this.downstream.onSubscribe(this);
            }
        }

        public void onNext(T t) {
            this.downstream.onNext(t);
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

        public int requestFusion(int mode) {
            QueueDisposable<T> qd = this.qd;
            boolean z = false;
            if (qd == null || (mode & 4) != 0) {
                return 0;
            }
            int m = qd.requestFusion(mode);
            if (m != 0) {
                if (m == 1) {
                    z = true;
                }
                this.syncFused = z;
            }
            return m;
        }

        public void clear() {
            this.qd.clear();
        }

        public boolean isEmpty() {
            return this.qd.isEmpty();
        }

        @Nullable
        public T poll() throws Exception {
            T v = this.qd.poll();
            if (v == null && this.syncFused) {
                runFinally();
            }
            return v;
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

    public ObservableDoFinally(ObservableSource<T> source, Action onFinally) {
        super(source);
        this.onFinally = onFinally;
    }

    protected void subscribeActual(Observer<? super T> observer) {
        this.source.subscribe(new DoFinallyObserver(observer, this.onFinally));
    }
}
