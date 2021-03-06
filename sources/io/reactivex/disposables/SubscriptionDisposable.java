package io.reactivex.disposables;

import io.reactivex.annotations.NonNull;
import org.reactivestreams.Subscription;

final class SubscriptionDisposable extends ReferenceDisposable<Subscription> {
    private static final long serialVersionUID = -707001650852963139L;

    SubscriptionDisposable(Subscription value) {
        super(value);
    }

    protected void onDisposed(@NonNull Subscription value) {
        value.cancel();
    }
}
