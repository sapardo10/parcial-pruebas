package io.reactivex.subjects;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.annotations.CheckReturnValue;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public final class CompletableSubject extends Completable implements CompletableObserver {
    static final CompletableDisposable[] EMPTY = new CompletableDisposable[0];
    static final CompletableDisposable[] TERMINATED = new CompletableDisposable[0];
    Throwable error;
    final AtomicReference<CompletableDisposable[]> observers = new AtomicReference(EMPTY);
    final AtomicBoolean once = new AtomicBoolean();

    static final class CompletableDisposable extends AtomicReference<CompletableSubject> implements Disposable {
        private static final long serialVersionUID = -7650903191002190468L;
        final CompletableObserver downstream;

        CompletableDisposable(CompletableObserver actual, CompletableSubject parent) {
            this.downstream = actual;
            lazySet(parent);
        }

        public void dispose() {
            CompletableSubject parent = (CompletableSubject) getAndSet(null);
            if (parent != null) {
                parent.remove(this);
            }
        }

        public boolean isDisposed() {
            return get() == null;
        }
    }

    @CheckReturnValue
    @NonNull
    public static CompletableSubject create() {
        return new CompletableSubject();
    }

    CompletableSubject() {
    }

    public void onSubscribe(Disposable d) {
        if (this.observers.get() == TERMINATED) {
            d.dispose();
        }
    }

    public void onError(Throwable e) {
        ObjectHelper.requireNonNull((Object) e, "onError called with null. Null values are generally not allowed in 2.x operators and sources.");
        int i = 0;
        if (this.once.compareAndSet(false, true)) {
            this.error = e;
            CompletableDisposable[] completableDisposableArr = (CompletableDisposable[]) this.observers.getAndSet(TERMINATED);
            int length = completableDisposableArr.length;
            while (i < length) {
                completableDisposableArr[i].downstream.onError(e);
                i++;
            }
            return;
        }
        RxJavaPlugins.onError(e);
    }

    public void onComplete() {
        int i = 0;
        if (this.once.compareAndSet(false, true)) {
            CompletableDisposable[] completableDisposableArr = (CompletableDisposable[]) this.observers.getAndSet(TERMINATED);
            int length = completableDisposableArr.length;
            while (i < length) {
                completableDisposableArr[i].downstream.onComplete();
                i++;
            }
        }
    }

    protected void subscribeActual(CompletableObserver observer) {
        CompletableDisposable md = new CompletableDisposable(observer, this);
        observer.onSubscribe(md);
        if (!add(md)) {
            Throwable ex = this.error;
            if (ex != null) {
                observer.onError(ex);
            } else {
                observer.onComplete();
            }
        } else if (md.isDisposed()) {
            remove(md);
        }
    }

    boolean add(CompletableDisposable inner) {
        while (true) {
            CompletableDisposable[] a = (CompletableDisposable[]) this.observers.get();
            if (a == TERMINATED) {
                return false;
            }
            int n = a.length;
            CompletableDisposable[] b = new CompletableDisposable[(n + 1)];
            System.arraycopy(a, 0, b, 0, n);
            b[n] = inner;
            if (this.observers.compareAndSet(a, b)) {
                return true;
            }
        }
    }

    void remove(CompletableDisposable inner) {
        while (true) {
            CompletableDisposable[] a = (CompletableDisposable[]) this.observers.get();
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
                    CompletableDisposable[] b;
                    if (n == 1) {
                        b = EMPTY;
                    } else {
                        CompletableDisposable[] b2 = new CompletableDisposable[(n - 1)];
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

    @Nullable
    public Throwable getThrowable() {
        if (this.observers.get() == TERMINATED) {
            return this.error;
        }
        return null;
    }

    public boolean hasThrowable() {
        return this.observers.get() == TERMINATED && this.error != null;
    }

    public boolean hasComplete() {
        return this.observers.get() == TERMINATED && this.error == null;
    }

    public boolean hasObservers() {
        return ((CompletableDisposable[]) this.observers.get()).length != 0;
    }

    int observerCount() {
        return ((CompletableDisposable[]) this.observers.get()).length;
    }
}
