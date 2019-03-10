package io.reactivex.disposables;

import io.reactivex.annotations.NonNull;

final class RunnableDisposable extends ReferenceDisposable<Runnable> {
    private static final long serialVersionUID = -8219729196779211169L;

    RunnableDisposable(Runnable value) {
        super(value);
    }

    protected void onDisposed(@NonNull Runnable value) {
        value.run();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("RunnableDisposable(disposed=");
        stringBuilder.append(isDisposed());
        stringBuilder.append(", ");
        stringBuilder.append(get());
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}
