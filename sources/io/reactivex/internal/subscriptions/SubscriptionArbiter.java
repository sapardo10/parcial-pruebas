package io.reactivex.internal.subscriptions;

import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.util.BackpressureHelper;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Subscription;

public class SubscriptionArbiter extends AtomicInteger implements Subscription {
    private static final long serialVersionUID = -2189523197179400958L;
    Subscription actual;
    volatile boolean cancelled;
    final AtomicLong missedProduced = new AtomicLong();
    final AtomicLong missedRequested = new AtomicLong();
    final AtomicReference<Subscription> missedSubscription = new AtomicReference();
    long requested;
    protected boolean unbounded;

    public final void setSubscription(Subscription s) {
        if (this.cancelled) {
            s.cancel();
            return;
        }
        ObjectHelper.requireNonNull((Object) s, "s is null");
        Subscription a;
        if (get() == 0 && compareAndSet(0, 1)) {
            a = this.actual;
            if (a != null) {
                a.cancel();
            }
            this.actual = s;
            long r = this.requested;
            if (decrementAndGet() != 0) {
                drainLoop();
            }
            if (r != 0) {
                s.request(r);
            }
            return;
        }
        a = (Subscription) this.missedSubscription.getAndSet(s);
        if (a != null) {
            a.cancel();
        }
        drain();
    }

    public final void request(long n) {
        if (SubscriptionHelper.validate(n)) {
            if (!this.unbounded) {
                if (get() == 0 && compareAndSet(0, 1)) {
                    long r = this.requested;
                    if (r != Long.MAX_VALUE) {
                        r = BackpressureHelper.addCap(r, n);
                        this.requested = r;
                        if (r == Long.MAX_VALUE) {
                            this.unbounded = true;
                        }
                    }
                    Subscription a = this.actual;
                    if (decrementAndGet() != 0) {
                        drainLoop();
                    }
                    if (a != null) {
                        a.request(n);
                    }
                    return;
                }
                BackpressureHelper.add(this.missedRequested, n);
                drain();
            }
        }
    }

    public final void produced(long n) {
        if (!this.unbounded) {
            if (get() == 0 && compareAndSet(0, 1)) {
                long r = this.requested;
                if (r != Long.MAX_VALUE) {
                    long u = r - n;
                    if (u < 0) {
                        SubscriptionHelper.reportMoreProduced(u);
                        u = 0;
                    }
                    this.requested = u;
                }
                if (decrementAndGet() != 0) {
                    drainLoop();
                    return;
                }
                return;
            }
            BackpressureHelper.add(this.missedProduced, n);
            drain();
        }
    }

    public void cancel() {
        if (!this.cancelled) {
            this.cancelled = true;
            drain();
        }
    }

    final void drain() {
        if (getAndIncrement() == 0) {
            drainLoop();
        }
    }

    final void drainLoop() {
        SubscriptionArbiter subscriptionArbiter = this;
        int missed = 1;
        long requestAmount = 0;
        Subscription requestTarget = null;
        while (true) {
            Subscription ms = (Subscription) subscriptionArbiter.missedSubscription.get();
            if (ms != null) {
                ms = (Subscription) subscriptionArbiter.missedSubscription.getAndSet(null);
            }
            long mr = subscriptionArbiter.missedRequested.get();
            if (mr != 0) {
                mr = subscriptionArbiter.missedRequested.getAndSet(0);
            }
            long mp = subscriptionArbiter.missedProduced.get();
            if (mp != 0) {
                mp = subscriptionArbiter.missedProduced.getAndSet(0);
            }
            Subscription a = subscriptionArbiter.actual;
            if (subscriptionArbiter.cancelled) {
                if (a != null) {
                    a.cancel();
                    subscriptionArbiter.actual = null;
                }
                if (ms != null) {
                    ms.cancel();
                }
            } else {
                long r = subscriptionArbiter.requested;
                if (r != Long.MAX_VALUE) {
                    long u = BackpressureHelper.addCap(r, mr);
                    if (u != Long.MAX_VALUE) {
                        long v = u - mp;
                        if (v < 0) {
                            SubscriptionHelper.reportMoreProduced(v);
                            v = 0;
                        }
                        r = v;
                    } else {
                        r = u;
                    }
                    subscriptionArbiter.requested = r;
                }
                if (ms != null) {
                    if (a != null) {
                        a.cancel();
                    }
                    subscriptionArbiter.actual = ms;
                    if (r != 0) {
                        requestAmount = BackpressureHelper.addCap(requestAmount, r);
                        requestTarget = ms;
                    }
                } else if (a != null && mr != 0) {
                    requestAmount = BackpressureHelper.addCap(requestAmount, mr);
                    requestTarget = a;
                }
            }
            missed = addAndGet(-missed);
            if (missed == 0) {
                break;
            }
        }
        if (requestAmount != 0) {
            requestTarget.request(requestAmount);
        }
    }

    public final boolean isUnbounded() {
        return this.unbounded;
    }

    public final boolean isCancelled() {
        return this.cancelled;
    }
}
