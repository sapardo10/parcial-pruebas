package io.reactivex.internal.disposables;

import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.ProtocolViolationException;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicReference;

public enum DisposableHelper implements Disposable {
    DISPOSED;

    public static boolean isDisposed(Disposable d) {
        return d == DISPOSED;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean set(java.util.concurrent.atomic.AtomicReference<io.reactivex.disposables.Disposable> r2, io.reactivex.disposables.Disposable r3) {
        /*
    L_0x0000:
        r0 = r2.get();
        r0 = (io.reactivex.disposables.Disposable) r0;
        r1 = DISPOSED;
        if (r0 != r1) goto L_0x0013;
    L_0x000a:
        if (r3 == 0) goto L_0x0010;
    L_0x000c:
        r3.dispose();
        goto L_0x0011;
    L_0x0011:
        r1 = 0;
        return r1;
    L_0x0013:
        r1 = r2.compareAndSet(r0, r3);
        if (r1 == 0) goto L_0x0022;
    L_0x0019:
        if (r0 == 0) goto L_0x001f;
    L_0x001b:
        r0.dispose();
        goto L_0x0020;
    L_0x0020:
        r1 = 1;
        return r1;
        goto L_0x0000;
        */
        throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.disposables.DisposableHelper.set(java.util.concurrent.atomic.AtomicReference, io.reactivex.disposables.Disposable):boolean");
    }

    public static boolean setOnce(AtomicReference<Disposable> field, Disposable d) {
        ObjectHelper.requireNonNull((Object) d, "d is null");
        if (field.compareAndSet(null, d)) {
            return true;
        }
        d.dispose();
        if (field.get() != DISPOSED) {
            reportDisposableSet();
        }
        return false;
    }

    public static boolean replace(AtomicReference<Disposable> field, Disposable d) {
        while (true) {
            Disposable current = (Disposable) field.get();
            if (current == DISPOSED) {
                break;
            } else if (field.compareAndSet(current, d)) {
                return true;
            }
        }
        if (d != null) {
            d.dispose();
        }
        return false;
    }

    public static boolean dispose(AtomicReference<Disposable> field) {
        Disposable current = (Disposable) field.get();
        Disposable d = DISPOSED;
        if (current != d) {
            current = (Disposable) field.getAndSet(d);
            if (current != d) {
                if (current != null) {
                    current.dispose();
                }
                return true;
            }
        }
        return false;
    }

    public static boolean validate(Disposable current, Disposable next) {
        if (next == null) {
            RxJavaPlugins.onError(new NullPointerException("next is null"));
            return false;
        } else if (current == null) {
            return true;
        } else {
            next.dispose();
            reportDisposableSet();
            return false;
        }
    }

    public static void reportDisposableSet() {
        RxJavaPlugins.onError(new ProtocolViolationException("Disposable already set!"));
    }

    public static boolean trySet(AtomicReference<Disposable> field, Disposable d) {
        if (field.compareAndSet(null, d)) {
            return true;
        }
        if (field.get() == DISPOSED) {
            d.dispose();
        }
        return false;
    }

    public void dispose() {
    }

    public boolean isDisposed() {
        return true;
    }
}
