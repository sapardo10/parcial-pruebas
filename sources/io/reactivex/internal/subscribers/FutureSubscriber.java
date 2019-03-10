package io.reactivex.internal.subscribers;

import io.reactivex.FlowableSubscriber;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.BlockingHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.NoSuchElementException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Subscription;

public final class FutureSubscriber<T> extends CountDownLatch implements FlowableSubscriber<T>, Future<T>, Subscription {
    Throwable error;
    final AtomicReference<Subscription> upstream = new AtomicReference();
    T value;

    public FutureSubscriber() {
        super(1);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean cancel(boolean r4) {
        /*
        r3 = this;
    L_0x0000:
        r0 = r3.upstream;
        r0 = r0.get();
        r0 = (org.reactivestreams.Subscription) r0;
        if (r0 == r3) goto L_0x0027;
    L_0x000a:
        r1 = io.reactivex.internal.subscriptions.SubscriptionHelper.CANCELLED;
        if (r0 != r1) goto L_0x000f;
    L_0x000e:
        goto L_0x0027;
    L_0x000f:
        r1 = r3.upstream;
        r2 = io.reactivex.internal.subscriptions.SubscriptionHelper.CANCELLED;
        r1 = r1.compareAndSet(r0, r2);
        if (r1 == 0) goto L_0x0025;
    L_0x0019:
        if (r0 == 0) goto L_0x001f;
    L_0x001b:
        r0.cancel();
        goto L_0x0020;
    L_0x0020:
        r3.countDown();
        r1 = 1;
        return r1;
        goto L_0x0000;
        r1 = 0;
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.subscribers.FutureSubscriber.cancel(boolean):boolean");
    }

    public boolean isCancelled() {
        return SubscriptionHelper.isCancelled((Subscription) this.upstream.get());
    }

    public boolean isDone() {
        return getCount() == 0;
    }

    public T get() throws InterruptedException, ExecutionException {
        if (getCount() != 0) {
            BlockingHelper.verifyNonBlocking();
            await();
        }
        if (isCancelled()) {
            throw new CancellationException();
        }
        Throwable ex = this.error;
        if (ex == null) {
            return this.value;
        }
        throw new ExecutionException(ex);
    }

    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (getCount() != 0) {
            BlockingHelper.verifyNonBlocking();
            if (!await(timeout, unit)) {
                throw new TimeoutException();
            }
        }
        if (isCancelled()) {
            throw new CancellationException();
        }
        Throwable ex = this.error;
        if (ex == null) {
            return this.value;
        }
        throw new ExecutionException(ex);
    }

    public void onSubscribe(Subscription s) {
        SubscriptionHelper.setOnce(this.upstream, s, Long.MAX_VALUE);
    }

    public void onNext(T t) {
        if (this.value != null) {
            ((Subscription) this.upstream.get()).cancel();
            onError(new IndexOutOfBoundsException("More than one element received"));
            return;
        }
        this.value = t;
    }

    public void onError(Throwable t) {
        while (true) {
            SubscriptionHelper a = (Subscription) this.upstream.get();
            if (a == this) {
                break;
            } else if (a == SubscriptionHelper.CANCELLED) {
                break;
            } else {
                this.error = t;
                if (this.upstream.compareAndSet(a, this)) {
                    countDown();
                    return;
                }
            }
        }
        RxJavaPlugins.onError(t);
    }

    public void onComplete() {
        if (this.value == null) {
            onError(new NoSuchElementException("The source is empty"));
            return;
        }
        while (true) {
            SubscriptionHelper a = (Subscription) this.upstream.get();
            if (a == this) {
                break;
            } else if (a == SubscriptionHelper.CANCELLED) {
                break;
            } else if (this.upstream.compareAndSet(a, this)) {
                countDown();
                return;
            }
        }
    }

    public void cancel() {
    }

    public void request(long n) {
    }
}
