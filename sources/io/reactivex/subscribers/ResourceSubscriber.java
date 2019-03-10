package io.reactivex.subscribers;

import io.reactivex.FlowableSubscriber;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.ListCompositeDisposable;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.EndConsumerHelper;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Subscription;

public abstract class ResourceSubscriber<T> implements FlowableSubscriber<T>, Disposable {
    private final AtomicLong missedRequested = new AtomicLong();
    private final ListCompositeDisposable resources = new ListCompositeDisposable();
    private final AtomicReference<Subscription> upstream = new AtomicReference();

    public final void add(Disposable resource) {
        ObjectHelper.requireNonNull((Object) resource, "resource is null");
        this.resources.add(resource);
    }

    public final void onSubscribe(Subscription s) {
        if (EndConsumerHelper.setOnce(this.upstream, s, getClass())) {
            long r = this.missedRequested.getAndSet(0);
            if (r != 0) {
                s.request(r);
            }
            onStart();
        }
    }

    protected void onStart() {
        request(Long.MAX_VALUE);
    }

    protected final void request(long n) {
        SubscriptionHelper.deferredRequest(this.upstream, this.missedRequested, n);
    }

    public final void dispose() {
        if (SubscriptionHelper.cancel(this.upstream)) {
            this.resources.dispose();
        }
    }

    public final boolean isDisposed() {
        return SubscriptionHelper.isCancelled((Subscription) this.upstream.get());
    }
}
