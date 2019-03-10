package io.reactivex.subjects;

import io.reactivex.Observer;
import io.reactivex.annotations.CheckReturnValue;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.observers.DeferredScalarDisposable;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public final class AsyncSubject<T> extends Subject<T> {
    static final AsyncDisposable[] EMPTY = new AsyncDisposable[0];
    static final AsyncDisposable[] TERMINATED = new AsyncDisposable[0];
    Throwable error;
    final AtomicReference<AsyncDisposable<T>[]> subscribers = new AtomicReference(EMPTY);
    T value;

    static final class AsyncDisposable<T> extends DeferredScalarDisposable<T> {
        private static final long serialVersionUID = 5629876084736248016L;
        final AsyncSubject<T> parent;

        AsyncDisposable(Observer<? super T> actual, AsyncSubject<T> parent) {
            super(actual);
            this.parent = parent;
        }

        public void dispose() {
            if (super.tryDispose()) {
                this.parent.remove(this);
            }
        }

        void onComplete() {
            if (!isDisposed()) {
                this.downstream.onComplete();
            }
        }

        void onError(Throwable t) {
            if (isDisposed()) {
                RxJavaPlugins.onError(t);
            } else {
                this.downstream.onError(t);
            }
        }
    }

    @CheckReturnValue
    @NonNull
    public static <T> AsyncSubject<T> create() {
        return new AsyncSubject();
    }

    AsyncSubject() {
    }

    public void onSubscribe(Disposable d) {
        if (this.subscribers.get() == TERMINATED) {
            d.dispose();
        }
    }

    public void onNext(T t) {
        ObjectHelper.requireNonNull((Object) t, "onNext called with null. Null values are generally not allowed in 2.x operators and sources.");
        if (this.subscribers.get() != TERMINATED) {
            this.value = t;
        }
    }

    public void onError(Throwable t) {
        ObjectHelper.requireNonNull((Object) t, "onError called with null. Null values are generally not allowed in 2.x operators and sources.");
        Object obj = this.subscribers.get();
        Object obj2 = TERMINATED;
        if (obj == obj2) {
            RxJavaPlugins.onError(t);
            return;
        }
        this.value = null;
        this.error = t;
        for (AsyncDisposable<T> as : (AsyncDisposable[]) this.subscribers.getAndSet(obj2)) {
            as.onError(t);
        }
    }

    public void onComplete() {
        Object obj = this.subscribers.get();
        Object obj2 = TERMINATED;
        if (obj != obj2) {
            T v = this.value;
            AsyncDisposable[] array = (AsyncDisposable[]) this.subscribers.getAndSet(obj2);
            int i = 0;
            int length;
            if (v == null) {
                length = array.length;
                while (i < length) {
                    array[i].onComplete();
                    i++;
                }
            } else {
                length = array.length;
                while (i < length) {
                    array[i].complete(v);
                    i++;
                }
            }
        }
    }

    public boolean hasObservers() {
        return ((AsyncDisposable[]) this.subscribers.get()).length != 0;
    }

    public boolean hasThrowable() {
        return this.subscribers.get() == TERMINATED && this.error != null;
    }

    public boolean hasComplete() {
        return this.subscribers.get() == TERMINATED && this.error == null;
    }

    public Throwable getThrowable() {
        return this.subscribers.get() == TERMINATED ? this.error : null;
    }

    protected void subscribeActual(Observer<? super T> observer) {
        AsyncDisposable<T> as = new AsyncDisposable(observer, this);
        observer.onSubscribe(as);
        if (!add(as)) {
            Throwable ex = this.error;
            if (ex != null) {
                observer.onError(ex);
                return;
            }
            T v = this.value;
            if (v != null) {
                as.complete(v);
            } else {
                as.onComplete();
            }
        } else if (as.isDisposed()) {
            remove(as);
        }
    }

    boolean add(AsyncDisposable<T> ps) {
        while (true) {
            AsyncDisposable[] a = (AsyncDisposable[]) this.subscribers.get();
            if (a == TERMINATED) {
                return false;
            }
            int n = a.length;
            AsyncDisposable<T>[] b = new AsyncDisposable[(n + 1)];
            System.arraycopy(a, 0, b, 0, n);
            b[n] = ps;
            if (this.subscribers.compareAndSet(a, b)) {
                return true;
            }
        }
    }

    void remove(AsyncDisposable<T> ps) {
        while (true) {
            AsyncDisposable[] a = (AsyncDisposable[]) this.subscribers.get();
            int n = a.length;
            if (n != 0) {
                int j = -1;
                for (int i = 0; i < n; i++) {
                    if (a[i] == ps) {
                        j = i;
                        break;
                    }
                }
                if (j >= 0) {
                    AsyncDisposable<T>[] b;
                    if (n == 1) {
                        b = EMPTY;
                    } else {
                        AsyncDisposable<T>[] b2 = new AsyncDisposable[(n - 1)];
                        System.arraycopy(a, 0, b2, 0, j);
                        System.arraycopy(a, j + 1, b2, j, (n - j) - 1);
                        b = b2;
                    }
                    if (this.subscribers.compareAndSet(a, b)) {
                        return;
                    }
                } else {
                    return;
                }
            }
            return;
        }
    }

    public boolean hasValue() {
        return this.subscribers.get() == TERMINATED && this.value != null;
    }

    @Nullable
    public T getValue() {
        return this.subscribers.get() == TERMINATED ? this.value : null;
    }

    @Deprecated
    public Object[] getValues() {
        if (getValue() == null) {
            return new Object[0];
        }
        return new Object[]{getValue()};
    }

    @Deprecated
    public T[] getValues(T[] array) {
        T v = getValue();
        if (v == null) {
            if (array.length != 0) {
                array[0] = null;
            }
            return array;
        }
        if (array.length == 0) {
            array = Arrays.copyOf(array, 1);
        }
        array[0] = v;
        if (array.length != 1) {
            array[1] = null;
        }
        return array;
    }
}
