package io.reactivex.processors;

import io.reactivex.internal.subscriptions.SubscriptionHelper;
import java.util.concurrent.atomic.AtomicLong;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

final class MulticastProcessor$MulticastSubscription<T> extends AtomicLong implements Subscription {
    private static final long serialVersionUID = -363282618957264509L;
    final Subscriber<? super T> downstream;
    long emitted;
    final MulticastProcessor<T> parent;

    MulticastProcessor$MulticastSubscription(Subscriber<? super T> actual, MulticastProcessor<T> parent) {
        this.downstream = actual;
        this.parent = parent;
    }

    public void request(long n) {
        if (SubscriptionHelper.validate(n)) {
            while (true) {
                long r = get();
                if (r == Long.MIN_VALUE) {
                    return;
                }
                if (r != Long.MAX_VALUE) {
                    long u = r + n;
                    if (u < 0) {
                        u = Long.MAX_VALUE;
                    }
                    if (compareAndSet(r, u)) {
                        this.parent.drain();
                        return;
                    }
                } else {
                    return;
                }
            }
        }
    }

    public void cancel() {
        if (getAndSet(Long.MIN_VALUE) != Long.MIN_VALUE) {
            this.parent.remove(this);
        }
    }

    void onNext(T t) {
        if (get() != Long.MIN_VALUE) {
            this.emitted++;
            this.downstream.onNext(t);
        }
    }

    void onError(Throwable t) {
        if (get() != Long.MIN_VALUE) {
            this.downstream.onError(t);
        }
    }

    void onComplete() {
        if (get() != Long.MIN_VALUE) {
            this.downstream.onComplete();
        }
    }
}
