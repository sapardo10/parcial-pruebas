package io.reactivex.internal.subscriptions;

import io.reactivex.annotations.Nullable;
import org.reactivestreams.Subscriber;

public class DeferredScalarSubscription<T> extends BasicIntQueueSubscription<T> {
    static final int CANCELLED = 4;
    static final int FUSED_CONSUMED = 32;
    static final int FUSED_EMPTY = 8;
    static final int FUSED_READY = 16;
    static final int HAS_REQUEST_HAS_VALUE = 3;
    static final int HAS_REQUEST_NO_VALUE = 2;
    static final int NO_REQUEST_HAS_VALUE = 1;
    static final int NO_REQUEST_NO_VALUE = 0;
    private static final long serialVersionUID = -2151279923272604993L;
    protected final Subscriber<? super T> downstream;
    protected T value;

    public DeferredScalarSubscription(Subscriber<? super T> downstream) {
        this.downstream = downstream;
    }

    public final void request(long n) {
        if (SubscriptionHelper.validate(n)) {
            while (true) {
                int state = get();
                if ((state & -2) == 0) {
                    if (state == 1) {
                        break;
                    } else if (compareAndSet(0, 2)) {
                        return;
                    }
                } else {
                    return;
                }
            }
            if (compareAndSet(1, 3)) {
                T v = this.value;
                if (v != null) {
                    this.value = null;
                    Subscriber<? super T> a = this.downstream;
                    a.onNext(v);
                    if (get() != 4) {
                        a.onComplete();
                    }
                }
            }
        }
    }

    public final void complete(T v) {
        Subscriber<? super T> a;
        int state = get();
        while (state != 8) {
            if ((state & -3) == 0) {
                if (state == 2) {
                    lazySet(3);
                    a = this.downstream;
                    a.onNext(v);
                    if (get() != 4) {
                        a.onComplete();
                    }
                    return;
                }
                this.value = v;
                if (!compareAndSet(0, 1)) {
                    state = get();
                    if (state == 4) {
                        this.value = null;
                        return;
                    }
                } else {
                    return;
                }
            }
            return;
        }
        this.value = v;
        lazySet(16);
        a = this.downstream;
        a.onNext(v);
        if (get() != 4) {
            a.onComplete();
        }
    }

    public final int requestFusion(int mode) {
        if ((mode & 2) == 0) {
            return 0;
        }
        lazySet(8);
        return 2;
    }

    @Nullable
    public final T poll() {
        if (get() != 16) {
            return null;
        }
        lazySet(32);
        T v = this.value;
        this.value = null;
        return v;
    }

    public final boolean isEmpty() {
        return get() != 16;
    }

    public final void clear() {
        lazySet(32);
        this.value = null;
    }

    public void cancel() {
        set(4);
        this.value = null;
    }

    public final boolean isCancelled() {
        return get() == 4;
    }

    public final boolean tryCancel() {
        return getAndSet(4) != 4;
    }
}
