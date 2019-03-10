package io.reactivex.subjects;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.annotations.CheckReturnValue;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public final class SingleSubject<T> extends Single<T> implements SingleObserver<T> {
    static final SingleDisposable[] EMPTY = new SingleDisposable[0];
    static final SingleDisposable[] TERMINATED = new SingleDisposable[0];
    Throwable error;
    final AtomicReference<SingleDisposable<T>[]> observers = new AtomicReference(EMPTY);
    final AtomicBoolean once = new AtomicBoolean();
    T value;

    static final class SingleDisposable<T> extends AtomicReference<SingleSubject<T>> implements Disposable {
        private static final long serialVersionUID = -7650903191002190468L;
        final SingleObserver<? super T> downstream;

        SingleDisposable(SingleObserver<? super T> actual, SingleSubject<T> parent) {
            this.downstream = actual;
            lazySet(parent);
        }

        public void dispose() {
            SingleSubject<T> parent = (SingleSubject) getAndSet(null);
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
    public static <T> SingleSubject<T> create() {
        return new SingleSubject();
    }

    SingleSubject() {
    }

    public void onSubscribe(@NonNull Disposable d) {
        if (this.observers.get() == TERMINATED) {
            d.dispose();
        }
    }

    public void onSuccess(@NonNull T value) {
        ObjectHelper.requireNonNull((Object) value, "onSuccess called with null. Null values are generally not allowed in 2.x operators and sources.");
        int i = 0;
        if (this.once.compareAndSet(false, true)) {
            this.value = value;
            SingleDisposable[] singleDisposableArr = (SingleDisposable[]) this.observers.getAndSet(TERMINATED);
            int length = singleDisposableArr.length;
            while (i < length) {
                singleDisposableArr[i].downstream.onSuccess(value);
                i++;
            }
        }
    }

    public void onError(@NonNull Throwable e) {
        ObjectHelper.requireNonNull((Object) e, "onError called with null. Null values are generally not allowed in 2.x operators and sources.");
        int i = 0;
        if (this.once.compareAndSet(false, true)) {
            this.error = e;
            SingleDisposable[] singleDisposableArr = (SingleDisposable[]) this.observers.getAndSet(TERMINATED);
            int length = singleDisposableArr.length;
            while (i < length) {
                singleDisposableArr[i].downstream.onError(e);
                i++;
            }
            return;
        }
        RxJavaPlugins.onError(e);
    }

    protected void subscribeActual(@NonNull SingleObserver<? super T> observer) {
        SingleDisposable<T> md = new SingleDisposable(observer, this);
        observer.onSubscribe(md);
        if (!add(md)) {
            Throwable ex = this.error;
            if (ex != null) {
                observer.onError(ex);
            } else {
                observer.onSuccess(this.value);
            }
        } else if (md.isDisposed()) {
            remove(md);
        }
    }

    boolean add(@NonNull SingleDisposable<T> inner) {
        while (true) {
            SingleDisposable[] a = (SingleDisposable[]) this.observers.get();
            if (a == TERMINATED) {
                return false;
            }
            int n = a.length;
            SingleDisposable<T>[] b = new SingleDisposable[(n + 1)];
            System.arraycopy(a, 0, b, 0, n);
            b[n] = inner;
            if (this.observers.compareAndSet(a, b)) {
                return true;
            }
        }
    }

    void remove(@NonNull SingleDisposable<T> inner) {
        while (true) {
            SingleDisposable[] a = (SingleDisposable[]) this.observers.get();
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
                    SingleDisposable<T>[] b;
                    if (n == 1) {
                        b = EMPTY;
                    } else {
                        SingleDisposable<T>[] b2 = new SingleDisposable[(n - 1)];
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

    public boolean hasObservers() {
        return ((SingleDisposable[]) this.observers.get()).length != 0;
    }

    int observerCount() {
        return ((SingleDisposable[]) this.observers.get()).length;
    }
}
