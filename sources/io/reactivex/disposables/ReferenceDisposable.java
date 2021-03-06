package io.reactivex.disposables;

import io.reactivex.annotations.NonNull;
import io.reactivex.internal.functions.ObjectHelper;
import java.util.concurrent.atomic.AtomicReference;

abstract class ReferenceDisposable<T> extends AtomicReference<T> implements Disposable {
    private static final long serialVersionUID = 6537757548749041217L;

    protected abstract void onDisposed(@NonNull T t);

    ReferenceDisposable(T value) {
        super(ObjectHelper.requireNonNull((Object) value, "value is null"));
    }

    public final void dispose() {
        if (get() != null) {
            T value = getAndSet(null);
            if (value != null) {
                onDisposed(value);
            }
        }
    }

    public final boolean isDisposed() {
        return get() == null;
    }
}
