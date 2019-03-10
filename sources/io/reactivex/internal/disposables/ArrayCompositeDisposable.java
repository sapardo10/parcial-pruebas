package io.reactivex.internal.disposables;

import io.reactivex.disposables.Disposable;
import java.util.concurrent.atomic.AtomicReferenceArray;

public final class ArrayCompositeDisposable extends AtomicReferenceArray<Disposable> implements Disposable {
    private static final long serialVersionUID = 2746389416410565408L;

    public ArrayCompositeDisposable(int capacity) {
        super(capacity);
    }

    public boolean setResource(int index, Disposable resource) {
        while (true) {
            Disposable o = (Disposable) get(index);
            if (o == DisposableHelper.DISPOSED) {
                resource.dispose();
                return false;
            } else if (compareAndSet(index, o, resource)) {
                break;
            }
        }
        if (o != null) {
            o.dispose();
        }
        return true;
    }

    public Disposable replaceResource(int index, Disposable resource) {
        while (true) {
            Disposable o = (Disposable) get(index);
            if (o == DisposableHelper.DISPOSED) {
                resource.dispose();
                return null;
            } else if (compareAndSet(index, o, resource)) {
                return o;
            }
        }
    }

    public void dispose() {
        if (get(0) != DisposableHelper.DISPOSED) {
            int s = length();
            for (int i = 0; i < s; i++) {
                if (((Disposable) get(i)) != DisposableHelper.DISPOSED) {
                    Disposable o = (Disposable) getAndSet(i, DisposableHelper.DISPOSED);
                    if (o != DisposableHelper.DISPOSED && o != null) {
                        o.dispose();
                    }
                }
            }
        }
    }

    public boolean isDisposed() {
        return get(0) == DisposableHelper.DISPOSED;
    }
}