package org.awaitility.core;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class SameThreadExecutorService extends AbstractExecutorService {
    private final Lock lock = new ReentrantLock();
    private int runningTasks = 0;
    private boolean shutdown = false;
    private final Condition termination = this.lock.newCondition();

    public boolean awaitTermination(long r6, java.util.concurrent.TimeUnit r8) throws java.lang.InterruptedException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:16:0x0032 in {5, 9, 12, 15} preds:[]
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
        r0 = r8.toNanos(r6);
        r2 = r5.lock;
        r2.lock();
    L_0x0009:
        r2 = r5.isTerminated();	 Catch:{ all -> 0x002b }
        if (r2 == 0) goto L_0x0016;
    L_0x000f:
        r2 = 1;
        r3 = r5.lock;
        r3.unlock();
        return r2;
    L_0x0016:
        r2 = 0;
        r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r4 > 0) goto L_0x0023;
    L_0x001c:
        r2 = 0;
        r3 = r5.lock;
        r3.unlock();
        return r2;
    L_0x0023:
        r2 = r5.termination;	 Catch:{ all -> 0x002b }
        r2 = r2.awaitNanos(r0);	 Catch:{ all -> 0x002b }
        r0 = r2;
        goto L_0x0009;
    L_0x002b:
        r2 = move-exception;
        r3 = r5.lock;
        r3.unlock();
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.awaitility.core.SameThreadExecutorService.awaitTermination(long, java.util.concurrent.TimeUnit):boolean");
    }

    SameThreadExecutorService() {
    }

    public void execute(Runnable command) {
        startTask();
        try {
            command.run();
        } finally {
            endTask();
        }
    }

    public boolean isShutdown() {
        this.lock.lock();
        try {
            boolean z = this.shutdown;
            return z;
        } finally {
            this.lock.unlock();
        }
    }

    public void shutdown() {
        this.lock.lock();
        try {
            this.shutdown = true;
        } finally {
            this.lock.unlock();
        }
    }

    public List<Runnable> shutdownNow() {
        shutdown();
        return Collections.emptyList();
    }

    public boolean isTerminated() {
        this.lock.lock();
        try {
            boolean z = this.shutdown && this.runningTasks == 0;
            this.lock.unlock();
            return z;
        } catch (Throwable th) {
            this.lock.unlock();
        }
    }

    private void startTask() {
        this.lock.lock();
        try {
            if (isShutdown()) {
                throw new RejectedExecutionException("Executor already shutdown");
            }
            this.runningTasks++;
        } finally {
            this.lock.unlock();
        }
    }

    private void endTask() {
        this.lock.lock();
        try {
            this.runningTasks--;
            if (isTerminated()) {
                this.termination.signalAll();
            }
            this.lock.unlock();
        } catch (Throwable th) {
            this.lock.unlock();
        }
    }
}
