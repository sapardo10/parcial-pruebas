package com.bumptech.glide.request;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RequestFutureTarget<R> implements FutureTarget<R>, RequestListener<R>, Runnable {
    private static final Waiter DEFAULT_WAITER = new Waiter();
    private final boolean assertBackgroundThread;
    @Nullable
    private GlideException exception;
    private final int height;
    private boolean isCancelled;
    private boolean loadFailed;
    private final Handler mainHandler;
    @Nullable
    private Request request;
    @Nullable
    private R resource;
    private boolean resultReceived;
    private final Waiter waiter;
    private final int width;

    @VisibleForTesting
    static class Waiter {
        Waiter() {
        }

        void waitForTimeout(Object toWaitOn, long timeoutMillis) throws InterruptedException {
            toWaitOn.wait(timeoutMillis);
        }

        void notifyAll(Object toNotify) {
            toNotify.notifyAll();
        }
    }

    private synchronized R doGet(java.lang.Long r8) throws java.util.concurrent.ExecutionException, java.lang.InterruptedException, java.util.concurrent.TimeoutException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:58:0x0095 in {6, 7, 16, 20, 28, 29, 30, 41, 44, 46, 48, 50, 52, 54, 57} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r7 = this;
        monitor-enter(r7);
        r0 = r7.assertBackgroundThread;	 Catch:{ all -> 0x0092 }
        if (r0 == 0) goto L_0x000f;	 Catch:{ all -> 0x0092 }
    L_0x0005:
        r0 = r7.isDone();	 Catch:{ all -> 0x0092 }
        if (r0 != 0) goto L_0x000f;	 Catch:{ all -> 0x0092 }
    L_0x000b:
        com.bumptech.glide.util.Util.assertBackgroundThread();	 Catch:{ all -> 0x0092 }
        goto L_0x0010;	 Catch:{ all -> 0x0092 }
    L_0x0010:
        r0 = r7.isCancelled;	 Catch:{ all -> 0x0092 }
        if (r0 != 0) goto L_0x008c;	 Catch:{ all -> 0x0092 }
    L_0x0014:
        r0 = r7.loadFailed;	 Catch:{ all -> 0x0092 }
        if (r0 != 0) goto L_0x0084;	 Catch:{ all -> 0x0092 }
    L_0x0018:
        r0 = r7.resultReceived;	 Catch:{ all -> 0x0092 }
        if (r0 == 0) goto L_0x0020;	 Catch:{ all -> 0x0092 }
    L_0x001c:
        r0 = r7.resource;	 Catch:{ all -> 0x0092 }
        monitor-exit(r7);
        return r0;
    L_0x0020:
        r0 = 0;
        if (r8 != 0) goto L_0x002a;
    L_0x0024:
        r2 = r7.waiter;	 Catch:{ all -> 0x0092 }
        r2.waitForTimeout(r7, r0);	 Catch:{ all -> 0x0092 }
        goto L_0x0054;	 Catch:{ all -> 0x0092 }
    L_0x002a:
        r2 = r8.longValue();	 Catch:{ all -> 0x0092 }
        r4 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1));	 Catch:{ all -> 0x0092 }
        if (r4 <= 0) goto L_0x0053;	 Catch:{ all -> 0x0092 }
    L_0x0032:
        r0 = java.lang.System.currentTimeMillis();	 Catch:{ all -> 0x0092 }
        r2 = r8.longValue();	 Catch:{ all -> 0x0092 }
        r2 = r2 + r0;	 Catch:{ all -> 0x0092 }
    L_0x003b:
        r4 = r7.isDone();	 Catch:{ all -> 0x0092 }
        if (r4 != 0) goto L_0x0052;	 Catch:{ all -> 0x0092 }
    L_0x0041:
        r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));	 Catch:{ all -> 0x0092 }
        if (r4 >= 0) goto L_0x0052;	 Catch:{ all -> 0x0092 }
    L_0x0045:
        r4 = r7.waiter;	 Catch:{ all -> 0x0092 }
        r5 = r2 - r0;	 Catch:{ all -> 0x0092 }
        r4.waitForTimeout(r7, r5);	 Catch:{ all -> 0x0092 }
        r4 = java.lang.System.currentTimeMillis();	 Catch:{ all -> 0x0092 }
        r0 = r4;	 Catch:{ all -> 0x0092 }
        goto L_0x003b;	 Catch:{ all -> 0x0092 }
    L_0x0052:
        goto L_0x0054;	 Catch:{ all -> 0x0092 }
    L_0x0054:
        r0 = java.lang.Thread.interrupted();	 Catch:{ all -> 0x0092 }
        if (r0 != 0) goto L_0x007e;	 Catch:{ all -> 0x0092 }
    L_0x005a:
        r0 = r7.loadFailed;	 Catch:{ all -> 0x0092 }
        if (r0 != 0) goto L_0x0076;	 Catch:{ all -> 0x0092 }
    L_0x005e:
        r0 = r7.isCancelled;	 Catch:{ all -> 0x0092 }
        if (r0 != 0) goto L_0x0070;	 Catch:{ all -> 0x0092 }
    L_0x0062:
        r0 = r7.resultReceived;	 Catch:{ all -> 0x0092 }
        if (r0 == 0) goto L_0x006a;	 Catch:{ all -> 0x0092 }
    L_0x0066:
        r0 = r7.resource;	 Catch:{ all -> 0x0092 }
        monitor-exit(r7);
        return r0;
    L_0x006a:
        r0 = new java.util.concurrent.TimeoutException;	 Catch:{ all -> 0x0092 }
        r0.<init>();	 Catch:{ all -> 0x0092 }
        throw r0;	 Catch:{ all -> 0x0092 }
    L_0x0070:
        r0 = new java.util.concurrent.CancellationException;	 Catch:{ all -> 0x0092 }
        r0.<init>();	 Catch:{ all -> 0x0092 }
        throw r0;	 Catch:{ all -> 0x0092 }
    L_0x0076:
        r0 = new java.util.concurrent.ExecutionException;	 Catch:{ all -> 0x0092 }
        r1 = r7.exception;	 Catch:{ all -> 0x0092 }
        r0.<init>(r1);	 Catch:{ all -> 0x0092 }
        throw r0;	 Catch:{ all -> 0x0092 }
    L_0x007e:
        r0 = new java.lang.InterruptedException;	 Catch:{ all -> 0x0092 }
        r0.<init>();	 Catch:{ all -> 0x0092 }
        throw r0;	 Catch:{ all -> 0x0092 }
    L_0x0084:
        r0 = new java.util.concurrent.ExecutionException;	 Catch:{ all -> 0x0092 }
        r1 = r7.exception;	 Catch:{ all -> 0x0092 }
        r0.<init>(r1);	 Catch:{ all -> 0x0092 }
        throw r0;	 Catch:{ all -> 0x0092 }
    L_0x008c:
        r0 = new java.util.concurrent.CancellationException;	 Catch:{ all -> 0x0092 }
        r0.<init>();	 Catch:{ all -> 0x0092 }
        throw r0;	 Catch:{ all -> 0x0092 }
    L_0x0092:
        r8 = move-exception;
        monitor-exit(r7);
        throw r8;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.request.RequestFutureTarget.doGet(java.lang.Long):R");
    }

    public RequestFutureTarget(Handler mainHandler, int width, int height) {
        this(mainHandler, width, height, true, DEFAULT_WAITER);
    }

    RequestFutureTarget(Handler mainHandler, int width, int height, boolean assertBackgroundThread, Waiter waiter) {
        this.mainHandler = mainHandler;
        this.width = width;
        this.height = height;
        this.assertBackgroundThread = assertBackgroundThread;
        this.waiter = waiter;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean cancel(boolean r3) {
        /*
        r2 = this;
        monitor-enter(r2);
        r0 = r2.isDone();	 Catch:{ all -> 0x001b }
        if (r0 == 0) goto L_0x000a;
    L_0x0007:
        r0 = 0;
        monitor-exit(r2);
        return r0;
    L_0x000a:
        r0 = 1;
        r2.isCancelled = r0;	 Catch:{ all -> 0x001b }
        r1 = r2.waiter;	 Catch:{ all -> 0x001b }
        r1.notifyAll(r2);	 Catch:{ all -> 0x001b }
        if (r3 == 0) goto L_0x0018;
    L_0x0014:
        r2.clearOnMainThread();	 Catch:{ all -> 0x001b }
        goto L_0x0019;
    L_0x0019:
        monitor-exit(r2);
        return r0;
    L_0x001b:
        r3 = move-exception;
        monitor-exit(r2);
        throw r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.request.RequestFutureTarget.cancel(boolean):boolean");
    }

    public synchronized boolean isCancelled() {
        return this.isCancelled;
    }

    public synchronized boolean isDone() {
        boolean z;
        if (!(this.isCancelled || this.resultReceived)) {
            if (!this.loadFailed) {
                z = false;
            }
        }
        z = true;
        return z;
    }

    public R get() throws InterruptedException, ExecutionException {
        try {
            return doGet(null);
        } catch (TimeoutException e) {
            throw new AssertionError(e);
        }
    }

    public R get(long time, @NonNull TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        return doGet(Long.valueOf(timeUnit.toMillis(time)));
    }

    public void getSize(@NonNull SizeReadyCallback cb) {
        cb.onSizeReady(this.width, this.height);
    }

    public void removeCallback(@NonNull SizeReadyCallback cb) {
    }

    public void setRequest(@Nullable Request request) {
        this.request = request;
    }

    @Nullable
    public Request getRequest() {
        return this.request;
    }

    public void onLoadCleared(@Nullable Drawable placeholder) {
    }

    public void onLoadStarted(@Nullable Drawable placeholder) {
    }

    public synchronized void onLoadFailed(@Nullable Drawable errorDrawable) {
    }

    public synchronized void onResourceReady(@NonNull R r, @Nullable Transition<? super R> transition) {
    }

    public void run() {
        Request request = this.request;
        if (request != null) {
            request.clear();
            this.request = null;
        }
    }

    private void clearOnMainThread() {
        this.mainHandler.post(this);
    }

    public void onStart() {
    }

    public void onStop() {
    }

    public void onDestroy() {
    }

    public synchronized boolean onLoadFailed(@Nullable GlideException e, Object model, Target<R> target, boolean isFirstResource) {
        this.loadFailed = true;
        this.exception = e;
        this.waiter.notifyAll(this);
        return false;
    }

    public synchronized boolean onResourceReady(R resource, Object model, Target<R> target, DataSource dataSource, boolean isFirstResource) {
        this.resultReceived = true;
        this.resource = resource;
        this.waiter.notifyAll(this);
        return false;
    }
}
