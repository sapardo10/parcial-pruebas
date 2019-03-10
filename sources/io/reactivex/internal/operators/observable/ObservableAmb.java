package io.reactivex.internal.operators.observable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class ObservableAmb<T> extends Observable<T> {
    final ObservableSource<? extends T>[] sources;
    final Iterable<? extends ObservableSource<? extends T>> sourcesIterable;

    static final class AmbCoordinator<T> implements Disposable {
        final Observer<? super T> downstream;
        final AmbInnerObserver<T>[] observers;
        final AtomicInteger winner = new AtomicInteger();

        AmbCoordinator(Observer<? super T> actual, int count) {
            this.downstream = actual;
            this.observers = new AmbInnerObserver[count];
        }

        public void subscribe(ObservableSource<? extends T>[] sources) {
            int i;
            AmbInnerObserver<T>[] as = this.observers;
            int len = as.length;
            for (i = 0; i < len; i++) {
                as[i] = new AmbInnerObserver(this, i + 1, this.downstream);
            }
            this.winner.lazySet(0);
            this.downstream.onSubscribe(this);
            for (i = 0; i < len && this.winner.get() == 0; i++) {
                sources[i].subscribe(as[i]);
            }
        }

        public boolean win(int index) {
            int w = this.winner.get();
            boolean z = false;
            if (w != 0) {
                if (w == index) {
                    z = true;
                }
                return z;
            } else if (!this.winner.compareAndSet(0, index)) {
                return false;
            } else {
                AmbInnerObserver<T>[] a = this.observers;
                int n = a.length;
                for (int i = 0; i < n; i++) {
                    if (i + 1 != index) {
                        a[i].dispose();
                    }
                }
                return true;
            }
        }

        public void dispose() {
            if (this.winner.get() != -1) {
                this.winner.lazySet(-1);
                for (AmbInnerObserver<T> a : this.observers) {
                    a.dispose();
                }
            }
        }

        public boolean isDisposed() {
            return this.winner.get() == -1;
        }
    }

    static final class AmbInnerObserver<T> extends AtomicReference<Disposable> implements Observer<T> {
        private static final long serialVersionUID = -1185974347409665484L;
        final Observer<? super T> downstream;
        final int index;
        final AmbCoordinator<T> parent;
        boolean won;

        AmbInnerObserver(AmbCoordinator<T> parent, int index, Observer<? super T> downstream) {
            this.parent = parent;
            this.index = index;
            this.downstream = downstream;
        }

        public void onSubscribe(Disposable d) {
            DisposableHelper.setOnce(this, d);
        }

        public void onNext(T t) {
            if (this.won) {
                this.downstream.onNext(t);
            } else if (this.parent.win(this.index)) {
                this.won = true;
                this.downstream.onNext(t);
            } else {
                ((Disposable) get()).dispose();
            }
        }

        public void onError(Throwable t) {
            if (this.won) {
                this.downstream.onError(t);
            } else if (this.parent.win(this.index)) {
                this.won = true;
                this.downstream.onError(t);
            } else {
                RxJavaPlugins.onError(t);
            }
        }

        public void onComplete() {
            if (this.won) {
                this.downstream.onComplete();
            } else if (this.parent.win(this.index)) {
                this.won = true;
                this.downstream.onComplete();
            }
        }

        public void dispose() {
            DisposableHelper.dispose(this);
        }
    }

    public ObservableAmb(ObservableSource<? extends T>[] sources, Iterable<? extends ObservableSource<? extends T>> sourcesIterable) {
        this.sources = sources;
        this.sourcesIterable = sourcesIterable;
    }

    public void subscribeActual(Observer<? super T> observer) {
        Throwable e;
        ObservableSource<? extends T>[] sources = this.sources;
        int count = 0;
        if (sources == null) {
            sources = new Observable[8];
            try {
                for (ObservableSource<? extends T> p : this.sourcesIterable) {
                    if (p == null) {
                        EmptyDisposable.error(new NullPointerException("One of the sources is null"), (Observer) observer);
                        return;
                    }
                    if (count == sources.length) {
                        ObservableSource<? extends T>[] b = new ObservableSource[((count >> 2) + count)];
                        System.arraycopy(sources, 0, b, 0, count);
                        sources = b;
                    }
                    int count2 = count + 1;
                    try {
                        sources[count] = p;
                        count = count2;
                    } catch (Throwable th) {
                        e = th;
                        count = count2;
                    }
                }
            } catch (Throwable th2) {
                e = th2;
                Exceptions.throwIfFatal(e);
                EmptyDisposable.error(e, (Observer) observer);
                return;
            }
        }
        count = sources.length;
        if (count == 0) {
            EmptyDisposable.complete((Observer) observer);
        } else if (count == 1) {
            sources[0].subscribe(observer);
        } else {
            new AmbCoordinator(observer, count).subscribe(sources);
        }
    }
}
