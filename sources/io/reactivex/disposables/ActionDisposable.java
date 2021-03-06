package io.reactivex.disposables;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.internal.util.ExceptionHelper;

final class ActionDisposable extends ReferenceDisposable<Action> {
    private static final long serialVersionUID = -8219729196779211169L;

    ActionDisposable(Action value) {
        super(value);
    }

    protected void onDisposed(@NonNull Action value) {
        try {
            value.run();
        } catch (Throwable ex) {
            RuntimeException wrapOrThrow = ExceptionHelper.wrapOrThrow(ex);
        }
    }
}
