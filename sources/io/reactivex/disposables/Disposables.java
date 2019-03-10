package io.reactivex.disposables;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.internal.functions.Functions;
import io.reactivex.internal.functions.ObjectHelper;
import java.util.concurrent.Future;
import org.reactivestreams.Subscription;

public final class Disposables {
    private Disposables() {
        throw new IllegalStateException("No instances!");
    }

    @NonNull
    public static Disposable fromRunnable(@NonNull Runnable run) {
        ObjectHelper.requireNonNull((Object) run, "run is null");
        return new RunnableDisposable(run);
    }

    @NonNull
    public static Disposable fromAction(@NonNull Action run) {
        ObjectHelper.requireNonNull((Object) run, "run is null");
        return new ActionDisposable(run);
    }

    @NonNull
    public static Disposable fromFuture(@NonNull Future<?> future) {
        ObjectHelper.requireNonNull((Object) future, "future is null");
        return fromFuture(future, true);
    }

    @NonNull
    public static Disposable fromFuture(@NonNull Future<?> future, boolean allowInterrupt) {
        ObjectHelper.requireNonNull((Object) future, "future is null");
        return new FutureDisposable(future, allowInterrupt);
    }

    @NonNull
    public static Disposable fromSubscription(@NonNull Subscription subscription) {
        ObjectHelper.requireNonNull((Object) subscription, "subscription is null");
        return new SubscriptionDisposable(subscription);
    }

    @NonNull
    public static Disposable empty() {
        return fromRunnable(Functions.EMPTY_RUNNABLE);
    }

    @NonNull
    public static Disposable disposed() {
        return EmptyDisposable.INSTANCE;
    }
}
