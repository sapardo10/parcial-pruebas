package io.reactivex.internal.operators.single;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class SingleCache<T> extends Single<T> implements SingleObserver<T> {
    static final CacheDisposable[] EMPTY = new CacheDisposable[0];
    static final CacheDisposable[] TERMINATED = new CacheDisposable[0];
    Throwable error;
    final AtomicReference<CacheDisposable<T>[]> observers = new AtomicReference(EMPTY);
    final SingleSource<? extends T> source;
    T value;
    final AtomicInteger wip = new AtomicInteger();

    static final class CacheDisposable<T> extends AtomicBoolean implements Disposable {
        private static final long serialVersionUID = 7514387411091976596L;
        final SingleObserver<? super T> downstream;
        final SingleCache<T> parent;

        CacheDisposable(SingleObserver<? super T> actual, SingleCache<T> parent) {
            this.downstream = actual;
            this.parent = parent;
        }

        public boolean isDisposed() {
            return get();
        }

        public void dispose() {
            if (compareAndSet(false, true)) {
                this.parent.remove(this);
            }
        }
    }

    public SingleCache(SingleSource<? extends T> source) {
        this.source = source;
    }

    protected void subscribeActual(SingleObserver<? super T> observer) {
        CacheDisposable<T> d = new CacheDisposable(observer, this);
        observer.onSubscribe(d);
        if (add(d)) {
            if (d.isDisposed()) {
                remove(d);
            }
            if (this.wip.getAndIncrement() == 0) {
                this.source.subscribe(this);
            }
            return;
        }
        Throwable ex = this.error;
        if (ex != null) {
            observer.onError(ex);
        } else {
            observer.onSuccess(this.value);
        }
    }

    boolean add(CacheDisposable<T> observer) {
        while (true) {
            CacheDisposable[] a = (CacheDisposable[]) this.observers.get();
            if (a == TERMINATED) {
                return false;
            }
            int n = a.length;
            CacheDisposable<T>[] b = new CacheDisposable[(n + 1)];
            System.arraycopy(a, 0, b, 0, n);
            b[n] = observer;
            if (this.observers.compareAndSet(a, b)) {
                return true;
            }
        }
    }

    void remove(CacheDisposable<T> observer) {
        while (true) {
            CacheDisposable[] a = (CacheDisposable[]) this.observers.get();
            int n = a.length;
            if (n != 0) {
                int j = -1;
                for (int i = 0; i < n; i++) {
                    if (a[i] == observer) {
                        j = i;
                        break;
                    }
                }
                if (j >= 0) {
                    CacheDisposable<T>[] b;
                    if (n == 1) {
                        b = EMPTY;
                    } else {
                        CacheDisposable<T>[] b2 = new CacheDisposable[(n - 1)];
                        System.arraycopy(a, 0, b2, 0, j);
                        System.arraycopy(a, j + 1, b2, j, (n - j) - 1);
                        b = b2;
                    }
                    if (this.observers.compareAndSet(a, b)) {
                        return;
                    }
                } else {
                    return;
                }
            }
            return;
        }
    }

    public void onSubscribe(Disposable d) {
    }

    public void onSuccess(T value) {
        this.value = value;
        for (CacheDisposable<T> d : (CacheDisposable[]) this.observers.getAndSet(TERMINATED)) {
            if (!d.isDisposed()) {
                d.downstream.onSuccess(value);
            }
        }
    }

    public void onError(Throwable e) {
        this.error = e;
        for (CacheDisposable<T> d : (CacheDisposable[]) this.observers.getAndSet(TERMINATED)) {
            if (!d.isDisposed()) {
                d.downstream.onError(e);
            }
        }
    }
}
