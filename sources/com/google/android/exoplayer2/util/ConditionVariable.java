package com.google.android.exoplayer2.util;

public final class ConditionVariable {
    private boolean isOpen;

    public synchronized void block() throws java.lang.InterruptedException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:11:0x000e in {5, 7, 10} preds:[]
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
        r1 = this;
        monitor-enter(r1);
    L_0x0001:
        r0 = r1.isOpen;	 Catch:{ all -> 0x000b }
        if (r0 != 0) goto L_0x0009;	 Catch:{ all -> 0x000b }
    L_0x0005:
        r1.wait();	 Catch:{ all -> 0x000b }
        goto L_0x0001;
    L_0x0009:
        monitor-exit(r1);
        return;
    L_0x000b:
        r0 = move-exception;
        monitor-exit(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.util.ConditionVariable.block():void");
    }

    public synchronized boolean block(long r7) throws java.lang.InterruptedException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:14:0x0022 in {7, 10, 13} preds:[]
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
        r6 = this;
        monitor-enter(r6);
        r0 = android.os.SystemClock.elapsedRealtime();	 Catch:{ all -> 0x001f }
        r2 = r0 + r7;	 Catch:{ all -> 0x001f }
    L_0x0007:
        r4 = r6.isOpen;	 Catch:{ all -> 0x001f }
        if (r4 != 0) goto L_0x001a;	 Catch:{ all -> 0x001f }
    L_0x000b:
        r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));	 Catch:{ all -> 0x001f }
        if (r4 >= 0) goto L_0x001a;	 Catch:{ all -> 0x001f }
    L_0x000f:
        r4 = r2 - r0;	 Catch:{ all -> 0x001f }
        r6.wait(r4);	 Catch:{ all -> 0x001f }
        r4 = android.os.SystemClock.elapsedRealtime();	 Catch:{ all -> 0x001f }
        r0 = r4;	 Catch:{ all -> 0x001f }
        goto L_0x0007;	 Catch:{ all -> 0x001f }
        r4 = r6.isOpen;	 Catch:{ all -> 0x001f }
        monitor-exit(r6);
        return r4;
    L_0x001f:
        r7 = move-exception;
        monitor-exit(r6);
        throw r7;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.util.ConditionVariable.block(long):boolean");
    }

    public synchronized boolean open() {
        if (this.isOpen) {
            return false;
        }
        this.isOpen = true;
        notifyAll();
        return true;
    }

    public synchronized boolean close() {
        boolean wasOpen;
        wasOpen = this.isOpen;
        this.isOpen = false;
        return wasOpen;
    }
}
