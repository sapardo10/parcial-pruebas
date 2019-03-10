package com.google.android.exoplayer2.util;

import java.io.IOException;
import java.util.Collections;
import java.util.PriorityQueue;

public final class PriorityTaskManager {
    private int highestPriority = Integer.MIN_VALUE;
    private final Object lock = new Object();
    private final PriorityQueue<Integer> queue = new PriorityQueue(10, Collections.reverseOrder());

    public static class PriorityTooLowException extends IOException {
        public PriorityTooLowException(int priority, int highestPriority) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Priority too low [priority=");
            stringBuilder.append(priority);
            stringBuilder.append(", highest=");
            stringBuilder.append(highestPriority);
            stringBuilder.append("]");
            super(stringBuilder.toString());
        }
    }

    public void proceed(int r3) throws java.lang.InterruptedException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:11:0x0012 in {5, 7, 10} preds:[]
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
        r2 = this;
        r0 = r2.lock;
        monitor-enter(r0);
    L_0x0003:
        r1 = r2.highestPriority;	 Catch:{ all -> 0x000f }
        if (r1 == r3) goto L_0x000d;	 Catch:{ all -> 0x000f }
    L_0x0007:
        r1 = r2.lock;	 Catch:{ all -> 0x000f }
        r1.wait();	 Catch:{ all -> 0x000f }
        goto L_0x0003;	 Catch:{ all -> 0x000f }
    L_0x000d:
        monitor-exit(r0);	 Catch:{ all -> 0x000f }
        return;	 Catch:{ all -> 0x000f }
    L_0x000f:
        r1 = move-exception;	 Catch:{ all -> 0x000f }
        monitor-exit(r0);	 Catch:{ all -> 0x000f }
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.util.PriorityTaskManager.proceed(int):void");
    }

    public void add(int priority) {
        synchronized (this.lock) {
            this.queue.add(Integer.valueOf(priority));
            this.highestPriority = Math.max(this.highestPriority, priority);
        }
    }

    public boolean proceedNonBlocking(int priority) {
        boolean z;
        synchronized (this.lock) {
            z = this.highestPriority == priority;
        }
        return z;
    }

    public void proceedOrThrow(int priority) throws PriorityTooLowException {
        synchronized (this.lock) {
            if (this.highestPriority == priority) {
            } else {
                throw new PriorityTooLowException(priority, this.highestPriority);
            }
        }
    }

    public void remove(int priority) {
        synchronized (this.lock) {
            this.queue.remove(Integer.valueOf(priority));
            this.highestPriority = this.queue.isEmpty() ? Integer.MIN_VALUE : ((Integer) Util.castNonNull(this.queue.peek())).intValue();
            this.lock.notifyAll();
        }
    }
}
