package io.reactivex.internal.subscribers;

import com.google.android.exoplayer2.C0555C;
import io.reactivex.FlowableSubscriber;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.BackpressureHelper;
import java.util.concurrent.atomic.AtomicLong;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public abstract class SinglePostCompleteSubscriber<T, R> extends AtomicLong implements FlowableSubscriber<T>, Subscription {
    static final long COMPLETE_MASK = Long.MIN_VALUE;
    static final long REQUEST_MASK = Long.MAX_VALUE;
    private static final long serialVersionUID = 7917814472626990048L;
    protected final Subscriber<? super R> downstream;
    protected long produced;
    protected Subscription upstream;
    protected R value;

    public SinglePostCompleteSubscriber(Subscriber<? super R> downstream) {
        this.downstream = downstream;
    }

    public void onSubscribe(Subscription s) {
        if (SubscriptionHelper.validate(this.upstream, s)) {
            this.upstream = s;
            this.downstream.onSubscribe(this);
        }
    }

    protected final void complete(R n) {
        long p = this.produced;
        if (p != 0) {
            BackpressureHelper.produced(this, p);
        }
        while (true) {
            long r = get();
            if ((r & Long.MIN_VALUE) != 0) {
                onDrop(n);
                return;
            } else if ((Long.MAX_VALUE & r) != 0) {
                lazySet(C0555C.TIME_UNSET);
                this.downstream.onNext(n);
                this.downstream.onComplete();
                return;
            } else {
                this.value = n;
                if (!compareAndSet(0, Long.MIN_VALUE)) {
                    this.value = null;
                } else {
                    return;
                }
            }
        }
    }

    protected void onDrop(R r) {
    }

    public final void request(long n) {
        if (SubscriptionHelper.validate(n)) {
            while (true) {
                long r = get();
                if ((r & Long.MIN_VALUE) != 0) {
                    break;
                } else if (compareAndSet(r, BackpressureHelper.addCap(r, n))) {
                    this.upstream.request(n);
                    return;
                }
            }
            if (compareAndSet(Long.MIN_VALUE, C0555C.TIME_UNSET)) {
                this.downstream.onNext(this.value);
                this.downstream.onComplete();
            }
        }
    }

    public void cancel() {
        this.upstream.cancel();
    }
}
