package io.reactivex.internal.observers;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.util.BlockingHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

public final class FutureSingleObserver<T> extends CountDownLatch implements SingleObserver<T>, Future<T>, Disposable {
    Throwable error;
    final AtomicReference<Disposable> upstream = new AtomicReference();
    T value;

    public FutureSingleObserver() {
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
        r0 = (io.reactivex.disposables.Disposable) r0;
        if (r0 == r3) goto L_0x0027;
    L_0x000a:
        r1 = io.reactivex.internal.disposables.DisposableHelper.DISPOSED;
        if (r0 != r1) goto L_0x000f;
    L_0x000e:
        goto L_0x0027;
    L_0x000f:
        r1 = r3.upstream;
        r2 = io.reactivex.internal.disposables.DisposableHelper.DISPOSED;
        r1 = r1.compareAndSet(r0, r2);
        if (r1 == 0) goto L_0x0025;
    L_0x0019:
        if (r0 == 0) goto L_0x001f;
    L_0x001b:
        r0.dispose();
        goto L_0x0020;
    L_0x0020:
        r3.countDown();
        r1 = 1;
        return r1;
        goto L_0x0000;
        r1 = 0;
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.observers.FutureSingleObserver.cancel(boolean):boolean");
    }

    public boolean isCancelled() {
        return DisposableHelper.isDisposed((Disposable) this.upstream.get());
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

    public void onSubscribe(Disposable d) {
        DisposableHelper.setOnce(this.upstream, d);
    }

    public void onSuccess(T t) {
        Disposable a = (Disposable) this.upstream.get();
        if (a != DisposableHelper.DISPOSED) {
            this.value = t;
            this.upstream.compareAndSet(a, this);
            countDown();
        }
    }

    public void onError(Throwable t) {
        while (true) {
            Disposable a = (Disposable) this.upstream.get();
            if (a == DisposableHelper.DISPOSED) {
                RxJavaPlugins.onError(t);
                return;
            }
            this.error = t;
            if (this.upstream.compareAndSet(a, this)) {
                countDown();
                return;
            }
        }
    }

    public void dispose() {
    }

    public boolean isDisposed() {
        return isDone();
    }
}
