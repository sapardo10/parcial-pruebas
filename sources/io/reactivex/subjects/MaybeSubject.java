package io.reactivex.subjects;

import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;
import io.reactivex.annotations.CheckReturnValue;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public final class MaybeSubject<T> extends Maybe<T> implements MaybeObserver<T> {
    static final MaybeDisposable[] EMPTY = new MaybeDisposable[0];
    static final MaybeDisposable[] TERMINATED = new MaybeDisposable[0];
    Throwable error;
    final AtomicReference<MaybeDisposable<T>[]> observers = new AtomicReference(EMPTY);
    final AtomicBoolean once = new AtomicBoolean();
    T value;

    static final class MaybeDisposable<T> extends AtomicReference<MaybeSubject<T>> implements Disposable {
        private static final long serialVersionUID = -7650903191002190468L;
        final MaybeObserver<? super T> downstream;

        MaybeDisposable(MaybeObserver<? super T> actual, MaybeSubject<T> parent) {
            this.downstream = actual;
            lazySet(parent);
        }

        public void dispose() {
            MaybeSubject<T> parent = (MaybeSubject) getAndSet(null);
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
    public static <T> MaybeSubject<T> create() {
        return new MaybeSubject();
    }

    MaybeSubject() {
    }

    public void onSubscribe(Disposable d) {
        if (this.observers.get() == TERMINATED) {
            d.dispose();
        }
    }

    public void onSuccess(T value) {
        ObjectHelper.requireNonNull((Object) value, "onSuccess called with null. Null values are generally not allowed in 2.x operators and sources.");
        int i = 0;
        if (this.once.compareAndSet(false, true)) {
            this.value = value;
            MaybeDisposable[] maybeDisposableArr = (MaybeDisposable[]) this.observers.getAndSet(TERMINATED);
            int length = maybeDisposableArr.length;
            while (i < length) {
                maybeDisposableArr[i].downstream.onSuccess(value);
                i++;
            }
        }
    }

    public void onError(Throwable e) {
        ObjectHelper.requireNonNull((Object) e, "onError called with null. Null values are generally not allowed in 2.x operators and sources.");
        int i = 0;
        if (this.once.compareAndSet(false, true)) {
            this.error = e;
            MaybeDisposable[] maybeDisposableArr = (MaybeDisposable[]) this.observers.getAndSet(TERMINATED);
            int length = maybeDisposableArr.length;
            while (i < length) {
                maybeDisposableArr[i].downstream.onError(e);
                i++;
            }
            return;
        }
        RxJavaPlugins.onError(e);
    }

    public void onComplete() {
        int i = 0;
        if (this.once.compareAndSet(false, true)) {
            MaybeDisposable[] maybeDisposableArr = (MaybeDisposable[]) this.observers.getAndSet(TERMINATED);
            int length = maybeDisposableArr.length;
            while (i < length) {
                maybeDisposableArr[i].downstream.onComplete();
                i++;
            }
        }
    }

    protected void subscribeActual(MaybeObserver<? super T> observer) {
        MaybeDisposable<T> md = new MaybeDisposable(observer, this);
        observer.onSubscribe(md);
        if (!add(md)) {
            Throwable ex = this.error;
            if (ex != null) {
                observer.onError(ex);
                return;
            }
            T v = this.value;
            if (v == null) {
                observer.onComplete();
            } else {
                observer.onSuccess(v);
            }
        } else if (md.isDisposed()) {
            remove(md);
        }
    }

    boolean add(MaybeDisposable<T> inner) {
        while (true) {
            MaybeDisposable[] a = (MaybeDisposable[]) this.observers.get();
            if (a == TERMINATED) {
                return false;
            }
            int n = a.length;
            MaybeDisposable<T>[] b = new MaybeDisposable[(n + 1)];
            System.arraycopy(a, 0, b, 0, n);
            b[n] = inner;
            if (this.observers.compareAndSet(a, b)) {
                return true;
            }
        }
    }

    void remove(MaybeDisposable<T> inner) {
        while (true) {
            MaybeDisposable[] a = (MaybeDisposable[]) this.observers.get();
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
                    MaybeDisposable<T>[] b;
                    if (n == 1) {
                        b = EMPTY;
                    } else {
                        MaybeDisposable<T>[] b2 = new MaybeDisposable[(n - 1)];
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
    public T getValue() {
        if (this.observers.get() == TERMINATED) {
            return this.value;
        }
        return null;
    }

    public boolean hasValue() {
        return this.observers.get() == TERMINATED && this.value != null;
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
        return this.observers.get() == TERMINATED && this.value == null && this.error == null;
    }

    public boolean hasObservers() {
        return ((MaybeDisposable[]) this.observers.get()).length != 0;
    }

    int observerCount() {
        return ((MaybeDisposable[]) this.observers.get()).length;
    }
}
