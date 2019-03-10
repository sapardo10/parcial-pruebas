package io.reactivex.processors;

import io.reactivex.annotations.CheckReturnValue;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.exceptions.MissingBackpressureException;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.BackpressureHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class PublishProcessor<T> extends FlowableProcessor<T> {
    static final PublishSubscription[] EMPTY = new PublishSubscription[0];
    static final PublishSubscription[] TERMINATED = new PublishSubscription[0];
    Throwable error;
    final AtomicReference<PublishSubscription<T>[]> subscribers = new AtomicReference(EMPTY);

    static final class PublishSubscription<T> extends AtomicLong implements Subscription {
        private static final long serialVersionUID = 3562861878281475070L;
        final Subscriber<? super T> downstream;
        final PublishProcessor<T> parent;

        PublishSubscription(Subscriber<? super T> actual, PublishProcessor<T> parent) {
            this.downstream = actual;
            this.parent = parent;
        }

        public void onNext(T t) {
            long r = get();
            if (r != Long.MIN_VALUE) {
                if (r != 0) {
                    this.downstream.onNext(t);
                    BackpressureHelper.producedCancel(this, 1);
                } else {
                    cancel();
                    this.downstream.onError(new MissingBackpressureException("Could not emit value due to lack of requests"));
                }
            }
        }

        public void onError(Throwable t) {
            if (get() != Long.MIN_VALUE) {
                this.downstream.onError(t);
            } else {
                RxJavaPlugins.onError(t);
            }
        }

        public void onComplete() {
            if (get() != Long.MIN_VALUE) {
                this.downstream.onComplete();
            }
        }

        public void request(long n) {
            if (SubscriptionHelper.validate(n)) {
                BackpressureHelper.addCancel(this, n);
            }
        }

        public void cancel() {
            if (getAndSet(Long.MIN_VALUE) != Long.MIN_VALUE) {
                this.parent.remove(this);
            }
        }

        public boolean isCancelled() {
            return get() == Long.MIN_VALUE;
        }

        boolean isFull() {
            return get() == 0;
        }
    }

    @CheckReturnValue
    @NonNull
    public static <T> PublishProcessor<T> create() {
        return new PublishProcessor();
    }

    PublishProcessor() {
    }

    protected void subscribeActual(Subscriber<? super T> t) {
        PublishSubscription<T> ps = new PublishSubscription(t, this);
        t.onSubscribe(ps);
        if (!add(ps)) {
            Throwable ex = this.error;
            if (ex != null) {
                t.onError(ex);
            } else {
                t.onComplete();
            }
        } else if (ps.isCancelled()) {
            remove(ps);
        }
    }

    boolean add(PublishSubscription<T> ps) {
        while (true) {
            PublishSubscription[] a = (PublishSubscription[]) this.subscribers.get();
            if (a == TERMINATED) {
                return false;
            }
            int n = a.length;
            PublishSubscription<T>[] b = new PublishSubscription[(n + 1)];
            System.arraycopy(a, 0, b, 0, n);
            b[n] = ps;
            if (this.subscribers.compareAndSet(a, b)) {
                return true;
            }
        }
    }

    void remove(PublishSubscription<T> ps) {
        while (true) {
            PublishSubscription[] a = (PublishSubscription[]) this.subscribers.get();
            if (a == TERMINATED) {
                break;
            } else if (a == EMPTY) {
                break;
            } else {
                int n = a.length;
                int j = -1;
                for (int i = 0; i < n; i++) {
                    if (a[i] == ps) {
                        j = i;
                        break;
                    }
                }
                if (j >= 0) {
                    PublishSubscription<T>[] b;
                    if (n == 1) {
                        b = EMPTY;
                    } else {
                        PublishSubscription<T>[] b2 = new PublishSubscription[(n - 1)];
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
        }
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
        for (PublishSubscription<T> s : (PublishSubscription[]) this.subscribers.get()) {
            s.onNext(t);
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
        this.error = t;
        for (PublishSubscription<T> s : (PublishSubscription[]) this.subscribers.getAndSet(obj2)) {
            s.onError(t);
        }
    }

    public void onComplete() {
        Object obj = this.subscribers.get();
        Object obj2 = TERMINATED;
        if (obj != obj2) {
            for (PublishSubscription<T> s : (PublishSubscription[]) this.subscribers.getAndSet(obj2)) {
                s.onComplete();
            }
        }
    }

    public boolean offer(T t) {
        if (t == null) {
            onError(new NullPointerException("onNext called with null. Null values are generally not allowed in 2.x operators and sources."));
            return true;
        }
        PublishSubscription[] array = (PublishSubscription[]) this.subscribers.get();
        int i = 0;
        for (PublishSubscription<T> s : array) {
            if (s.isFull()) {
                return false;
            }
        }
        int length = array.length;
        while (i < length) {
            array[i].onNext(t);
            i++;
        }
        return true;
    }

    public boolean hasSubscribers() {
        return ((PublishSubscription[]) this.subscribers.get()).length != 0;
    }

    @Nullable
    public Throwable getThrowable() {
        if (this.subscribers.get() == TERMINATED) {
            return this.error;
        }
        return null;
    }

    public boolean hasThrowable() {
        return this.subscribers.get() == TERMINATED && this.error != null;
    }

    public boolean hasComplete() {
        return this.subscribers.get() == TERMINATED && this.error == null;
    }
}
