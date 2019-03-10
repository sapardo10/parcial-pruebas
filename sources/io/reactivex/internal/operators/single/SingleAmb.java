package io.reactivex.internal.operators.single;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicBoolean;

public final class SingleAmb<T> extends Single<T> {
    private final SingleSource<? extends T>[] sources;
    private final Iterable<? extends SingleSource<? extends T>> sourcesIterable;

    static final class AmbSingleObserver<T> extends AtomicBoolean implements SingleObserver<T> {
        private static final long serialVersionUID = -1944085461036028108L;
        final SingleObserver<? super T> downstream;
        final CompositeDisposable set;

        AmbSingleObserver(SingleObserver<? super T> observer, CompositeDisposable set) {
            this.downstream = observer;
            this.set = set;
        }

        public void onSubscribe(Disposable d) {
            this.set.add(d);
        }

        public void onSuccess(T value) {
            if (compareAndSet(false, true)) {
                this.set.dispose();
                this.downstream.onSuccess(value);
            }
        }

        public void onError(Throwable e) {
            if (compareAndSet(false, true)) {
                this.set.dispose();
                this.downstream.onError(e);
                return;
            }
            RxJavaPlugins.onError(e);
        }
    }

    public SingleAmb(SingleSource<? extends T>[] sources, Iterable<? extends SingleSource<? extends T>> sourcesIterable) {
        this.sources = sources;
        this.sourcesIterable = sourcesIterable;
    }

    protected void subscribeActual(SingleObserver<? super T> observer) {
        int count;
        Throwable e;
        SingleSource<? extends T>[] sources = this.sources;
        int count2 = 0;
        if (sources == null) {
            sources = new SingleSource[8];
            try {
                for (SingleSource<? extends T> element : this.sourcesIterable) {
                    if (element == null) {
                        EmptyDisposable.error(new NullPointerException("One of the sources is null"), (SingleObserver) observer);
                        return;
                    }
                    if (count2 == sources.length) {
                        SingleSource<? extends T>[] b = new SingleSource[((count2 >> 2) + count2)];
                        System.arraycopy(sources, 0, b, 0, count2);
                        sources = b;
                    }
                    count = count2 + 1;
                    try {
                        sources[count2] = element;
                        count2 = count;
                    } catch (Throwable th) {
                        e = th;
                        count2 = count;
                    }
                }
            } catch (Throwable th2) {
                e = th2;
                Exceptions.throwIfFatal(e);
                EmptyDisposable.error(e, (SingleObserver) observer);
                return;
            }
        }
        count2 = sources.length;
        CompositeDisposable set = new CompositeDisposable();
        AmbSingleObserver<T> shared = new AmbSingleObserver(observer, set);
        observer.onSubscribe(set);
        count = 0;
        while (count < count2) {
            SingleSource<? extends T> s1 = sources[count];
            if (!shared.get()) {
                if (s1 == null) {
                    set.dispose();
                    Throwable e2 = new NullPointerException("One of the sources is null");
                    if (shared.compareAndSet(false, true)) {
                        observer.onError(e2);
                    } else {
                        RxJavaPlugins.onError(e2);
                    }
                    return;
                }
                s1.subscribe(shared);
                count++;
            } else {
                return;
            }
        }
    }
}
