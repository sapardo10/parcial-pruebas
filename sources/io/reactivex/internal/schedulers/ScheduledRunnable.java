package io.reactivex.internal.schedulers;

import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableContainer;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReferenceArray;

public final class ScheduledRunnable extends AtomicReferenceArray<Object> implements Runnable, Callable<Object>, Disposable {
    static final Object ASYNC_DISPOSED = new Object();
    static final Object DONE = new Object();
    static final int FUTURE_INDEX = 1;
    static final Object PARENT_DISPOSED = new Object();
    static final int PARENT_INDEX = 0;
    static final Object SYNC_DISPOSED = new Object();
    static final int THREAD_INDEX = 2;
    private static final long serialVersionUID = -6120223772001106981L;
    final Runnable actual;

    public void run() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:43:0x0081 in {3, 8, 14, 15, 22, 23, 25, 31, 32, 39, 40, 41, 42} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r5 = this;
        r0 = java.lang.Thread.currentThread();
        r1 = 2;
        r5.lazySet(r1, r0);
        r0 = 0;
        r2 = 1;
        r3 = 0;
        r4 = r5.actual;	 Catch:{ Throwable -> 0x0013 }
        r4.run();	 Catch:{ Throwable -> 0x0013 }
        goto L_0x0017;
    L_0x0011:
        r4 = move-exception;
        goto L_0x004c;
    L_0x0013:
        r4 = move-exception;
        io.reactivex.plugins.RxJavaPlugins.onError(r4);	 Catch:{ all -> 0x0011 }
    L_0x0017:
        r5.lazySet(r1, r0);
        r0 = r5.get(r3);
        r1 = PARENT_DISPOSED;
        if (r0 == r1) goto L_0x0033;
    L_0x0022:
        r1 = DONE;
        r1 = r5.compareAndSet(r3, r0, r1);
        if (r1 == 0) goto L_0x0033;
    L_0x002a:
        if (r0 == 0) goto L_0x0033;
    L_0x002c:
        r1 = r0;
        r1 = (io.reactivex.internal.disposables.DisposableContainer) r1;
        r1.delete(r5);
        goto L_0x0034;
    L_0x0034:
        r0 = r5.get(r2);
        r1 = SYNC_DISPOSED;
        if (r0 == r1) goto L_0x004a;
    L_0x003c:
        r1 = ASYNC_DISPOSED;
        if (r0 == r1) goto L_0x004a;
    L_0x0040:
        r1 = DONE;
        r1 = r5.compareAndSet(r2, r0, r1);
        if (r1 == 0) goto L_0x0049;
    L_0x0048:
        goto L_0x004a;
    L_0x0049:
        goto L_0x0034;
        return;
    L_0x004c:
        r5.lazySet(r1, r0);
        r0 = r5.get(r3);
        r1 = PARENT_DISPOSED;
        if (r0 == r1) goto L_0x0068;
    L_0x0057:
        r1 = DONE;
        r1 = r5.compareAndSet(r3, r0, r1);
        if (r1 == 0) goto L_0x0068;
    L_0x005f:
        if (r0 == 0) goto L_0x0068;
    L_0x0061:
        r1 = r0;
        r1 = (io.reactivex.internal.disposables.DisposableContainer) r1;
        r1.delete(r5);
        goto L_0x0069;
    L_0x0069:
        r0 = r5.get(r2);
        r1 = SYNC_DISPOSED;
        if (r0 == r1) goto L_0x007f;
    L_0x0071:
        r1 = ASYNC_DISPOSED;
        if (r0 == r1) goto L_0x007f;
    L_0x0075:
        r1 = DONE;
        r1 = r5.compareAndSet(r2, r0, r1);
        if (r1 != 0) goto L_0x007e;
    L_0x007d:
        goto L_0x0069;
    L_0x007e:
        goto L_0x0080;
    L_0x0080:
        throw r4;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.schedulers.ScheduledRunnable.run():void");
    }

    public ScheduledRunnable(Runnable actual, DisposableContainer parent) {
        super(3);
        this.actual = actual;
        lazySet(0, parent);
    }

    public Object call() {
        run();
        return null;
    }

    public void setFuture(Future<?> f) {
        while (true) {
            Object o = get(1);
            if (o != DONE) {
                if (o == SYNC_DISPOSED) {
                    f.cancel(false);
                    return;
                } else if (o == ASYNC_DISPOSED) {
                    f.cancel(true);
                    return;
                } else if (compareAndSet(1, o, f)) {
                    return;
                }
            } else {
                return;
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void dispose() {
        /*
        r5 = this;
    L_0x0000:
        r0 = 1;
        r1 = r5.get(r0);
        r2 = DONE;
        r3 = 0;
        if (r1 == r2) goto L_0x003a;
    L_0x000a:
        r2 = SYNC_DISPOSED;
        if (r1 == r2) goto L_0x003a;
    L_0x000e:
        r2 = ASYNC_DISPOSED;
        if (r1 != r2) goto L_0x0013;
    L_0x0012:
        goto L_0x003b;
    L_0x0013:
        r2 = 2;
        r2 = r5.get(r2);
        r4 = java.lang.Thread.currentThread();
        if (r2 == r4) goto L_0x0020;
    L_0x001e:
        r2 = 1;
        goto L_0x0021;
    L_0x0020:
        r2 = 0;
    L_0x0021:
        if (r2 == 0) goto L_0x0026;
    L_0x0023:
        r4 = ASYNC_DISPOSED;
        goto L_0x0028;
    L_0x0026:
        r4 = SYNC_DISPOSED;
    L_0x0028:
        r0 = r5.compareAndSet(r0, r1, r4);
        if (r0 == 0) goto L_0x0038;
    L_0x002e:
        if (r1 == 0) goto L_0x0037;
    L_0x0030:
        r0 = r1;
        r0 = (java.util.concurrent.Future) r0;
        r0.cancel(r2);
        goto L_0x003b;
    L_0x0037:
        goto L_0x003b;
        goto L_0x0000;
    L_0x003b:
        r0 = r5.get(r3);
        r1 = DONE;
        if (r0 == r1) goto L_0x0059;
    L_0x0043:
        r1 = PARENT_DISPOSED;
        if (r0 == r1) goto L_0x0059;
    L_0x0047:
        if (r0 != 0) goto L_0x004a;
    L_0x0049:
        goto L_0x0059;
    L_0x004a:
        r1 = r5.compareAndSet(r3, r0, r1);
        if (r1 == 0) goto L_0x0057;
    L_0x0050:
        r1 = r0;
        r1 = (io.reactivex.internal.disposables.DisposableContainer) r1;
        r1.delete(r5);
        return;
        goto L_0x003b;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.schedulers.ScheduledRunnable.dispose():void");
    }

    public boolean isDisposed() {
        Object o = get(0);
        if (o != PARENT_DISPOSED) {
            if (o != DONE) {
                return false;
            }
        }
        return true;
    }
}
