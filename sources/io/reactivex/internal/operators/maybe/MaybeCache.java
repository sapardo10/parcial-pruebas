package io.reactivex.internal.operators.maybe;

import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;
import io.reactivex.MaybeSource;
import io.reactivex.disposables.Disposable;
import java.util.concurrent.atomic.AtomicReference;

public final class MaybeCache<T> extends Maybe<T> implements MaybeObserver<T> {
    static final CacheDisposable[] EMPTY = new CacheDisposable[0];
    static final CacheDisposable[] TERMINATED = new CacheDisposable[0];
    Throwable error;
    final AtomicReference<CacheDisposable<T>[]> observers = new AtomicReference(EMPTY);
    final AtomicReference<MaybeSource<T>> source;
    T value;

    static final class CacheDisposable<T> extends AtomicReference<MaybeCache<T>> implements Disposable {
        private static final long serialVersionUID = -5791853038359966195L;
        final MaybeObserver<? super T> downstream;

        CacheDisposable(MaybeObserver<? super T> actual, MaybeCache<T> parent) {
            super(parent);
            this.downstream = actual;
        }

        public void dispose() {
            MaybeCache<T> mc = (MaybeCache) getAndSet(null);
            if (mc != null) {
                mc.remove(this);
            }
        }

        public boolean isDisposed() {
            return get() == null;
        }
    }

    public MaybeCache(MaybeSource<T> source) {
        this.source = new AtomicReference(source);
    }

    protected void subscribeActual(MaybeObserver<? super T> observer) {
        CacheDisposable<T> parent = new CacheDisposable(observer, this);
        observer.onSubscribe(parent);
        if (!add(parent)) {
            if (!parent.isDisposed()) {
                Throwable ex = this.error;
                if (ex != null) {
                    observer.onError(ex);
                } else {
                    T v = this.value;
                    if (v != null) {
                        observer.onSuccess(v);
                    } else {
                        observer.onComplete();
                    }
                }
            }
        } else if (parent.isDisposed()) {
            remove(parent);
        } else {
            MaybeSource<T> src = (MaybeSource) this.source.getAndSet(null);
            if (src != null) {
                src.subscribe(this);
            }
        }
    }

    public void onSubscribe(Disposable d) {
    }

    public void onSuccess(T value) {
        this.value = value;
        for (CacheDisposable<T> inner : (CacheDisposable[]) this.observers.getAndSet(TERMINATED)) {
            if (!inner.isDisposed()) {
                inner.downstream.onSuccess(value);
            }
        }
    }

    public void onError(Throwable e) {
        this.error = e;
        for (CacheDisposable<T> inner : (CacheDisposable[]) this.observers.getAndSet(TERMINATED)) {
            if (!inner.isDisposed()) {
                inner.downstream.onError(e);
            }
        }
    }

    public void onComplete() {
        for (CacheDisposable<T> inner : (CacheDisposable[]) this.observers.getAndSet(TERMINATED)) {
            if (!inner.isDisposed()) {
                inner.downstream.onComplete();
            }
        }
    }

    boolean add(CacheDisposable<T> inner) {
        while (true) {
            CacheDisposable[] a = (CacheDisposable[]) this.observers.get();
            if (a == TERMINATED) {
                return false;
            }
            int n = a.length;
            CacheDisposable<T>[] b = new CacheDisposable[(n + 1)];
            System.arraycopy(a, 0, b, 0, n);
            b[n] = inner;
            if (this.observers.compareAndSet(a, b)) {
                return true;
            }
        }
    }

    void remove(CacheDisposable<T> inner) {
        while (true) {
            CacheDisposable[] a = (CacheDisposable[]) this.observers.get();
            int n = a.length;
            if (n != 0) {
                int j = -1;
                for (int i = 0; i < n; i++) {
                    if (a[i] == inner) {
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
}
