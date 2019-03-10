package io.reactivex.processors;

import io.reactivex.annotations.CheckReturnValue;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.subscriptions.DeferredScalarSubscription;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class AsyncProcessor<T> extends FlowableProcessor<T> {
    static final AsyncSubscription[] EMPTY = new AsyncSubscription[0];
    static final AsyncSubscription[] TERMINATED = new AsyncSubscription[0];
    Throwable error;
    final AtomicReference<AsyncSubscription<T>[]> subscribers = new AtomicReference(EMPTY);
    T value;

    static final class AsyncSubscription<T> extends DeferredScalarSubscription<T> {
        private static final long serialVersionUID = 5629876084736248016L;
        final AsyncProcessor<T> parent;

        AsyncSubscription(Subscriber<? super T> actual, AsyncProcessor<T> parent) {
            super(actual);
            this.parent = parent;
        }

        public void cancel() {
            if (super.tryCancel()) {
                this.parent.remove(this);
            }
        }

        void onComplete() {
            if (!isCancelled()) {
                this.downstream.onComplete();
            }
        }

        void onError(Throwable t) {
            if (isCancelled()) {
                RxJavaPlugins.onError(t);
            } else {
                this.downstream.onError(t);
            }
        }
    }

    @CheckReturnValue
    @NonNull
    public static <T> AsyncProcessor<T> create() {
        return new AsyncProcessor();
    }

    AsyncProcessor() {
    }

    public void onSubscribe(Subscription s) {
        if (this.subscribers.get() == TERMINATED) {
            s.cancel();
        } else {
            s.request(Long.MAX_VALUE);
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
        for (AsyncSubscription<T> as : (AsyncSubscription[]) this.subscribers.getAndSet(obj2)) {
            as.onError(t);
        }
    }

    public void onComplete() {
        Object obj = this.subscribers.get();
        Object obj2 = TERMINATED;
        if (obj != obj2) {
            T v = this.value;
            AsyncSubscription[] array = (AsyncSubscription[]) this.subscribers.getAndSet(obj2);
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

    public boolean hasSubscribers() {
        return ((AsyncSubscription[]) this.subscribers.get()).length != 0;
    }

    public boolean hasThrowable() {
        return this.subscribers.get() == TERMINATED && this.error != null;
    }

    public boolean hasComplete() {
        return this.subscribers.get() == TERMINATED && this.error == null;
    }

    @Nullable
    public Throwable getThrowable() {
        return this.subscribers.get() == TERMINATED ? this.error : null;
    }

    protected void subscribeActual(Subscriber<? super T> s) {
        AsyncSubscription<T> as = new AsyncSubscription(s, this);
        s.onSubscribe(as);
        if (!add(as)) {
            Throwable ex = this.error;
            if (ex != null) {
                s.onError(ex);
                return;
            }
            T v = this.value;
            if (v != null) {
                as.complete(v);
            } else {
                as.onComplete();
            }
        } else if (as.isCancelled()) {
            remove(as);
        }
    }

    boolean add(AsyncSubscription<T> ps) {
        while (true) {
            AsyncSubscription[] a = (AsyncSubscription[]) this.subscribers.get();
            if (a == TERMINATED) {
                return false;
            }
            int n = a.length;
            AsyncSubscription<T>[] b = new AsyncSubscription[(n + 1)];
            System.arraycopy(a, 0, b, 0, n);
            b[n] = ps;
            if (this.subscribers.compareAndSet(a, b)) {
                return true;
            }
        }
    }

    void remove(AsyncSubscription<T> ps) {
        while (true) {
            AsyncSubscription[] a = (AsyncSubscription[]) this.subscribers.get();
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
                    AsyncSubscription<T>[] b;
                    if (n == 1) {
                        b = EMPTY;
                    } else {
                        AsyncSubscription<T>[] b2 = new AsyncSubscription[(n - 1)];
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
