package com.google.android.exoplayer2.upstream;

import com.google.android.exoplayer2.util.Assertions;

public final class DefaultAllocator implements Allocator {
    private static final int AVAILABLE_EXTRA_CAPACITY = 100;
    private int allocatedCount;
    private Allocation[] availableAllocations;
    private int availableCount;
    private final int individualAllocationSize;
    private final byte[] initialAllocationBlock;
    private final Allocation[] singleAllocationReleaseHolder;
    private int targetBufferSize;
    private final boolean trimOnReset;

    public synchronized void release(com.google.android.exoplayer2.upstream.Allocation[] r7) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:15:0x0044 in {4, 5, 8, 11, 14} preds:[]
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
        r6 = this;
        monitor-enter(r6);
        r0 = r6.availableCount;	 Catch:{ all -> 0x0041 }
        r1 = r7.length;	 Catch:{ all -> 0x0041 }
        r0 = r0 + r1;	 Catch:{ all -> 0x0041 }
        r1 = r6.availableAllocations;	 Catch:{ all -> 0x0041 }
        r1 = r1.length;	 Catch:{ all -> 0x0041 }
        if (r0 < r1) goto L_0x0022;	 Catch:{ all -> 0x0041 }
    L_0x000a:
        r0 = r6.availableAllocations;	 Catch:{ all -> 0x0041 }
        r1 = r6.availableAllocations;	 Catch:{ all -> 0x0041 }
        r1 = r1.length;	 Catch:{ all -> 0x0041 }
        r1 = r1 * 2;	 Catch:{ all -> 0x0041 }
        r2 = r6.availableCount;	 Catch:{ all -> 0x0041 }
        r3 = r7.length;	 Catch:{ all -> 0x0041 }
        r2 = r2 + r3;	 Catch:{ all -> 0x0041 }
        r1 = java.lang.Math.max(r1, r2);	 Catch:{ all -> 0x0041 }
        r0 = java.util.Arrays.copyOf(r0, r1);	 Catch:{ all -> 0x0041 }
        r0 = (com.google.android.exoplayer2.upstream.Allocation[]) r0;	 Catch:{ all -> 0x0041 }
        r6.availableAllocations = r0;	 Catch:{ all -> 0x0041 }
        goto L_0x0023;	 Catch:{ all -> 0x0041 }
    L_0x0023:
        r0 = r7.length;	 Catch:{ all -> 0x0041 }
        r1 = 0;	 Catch:{ all -> 0x0041 }
    L_0x0025:
        if (r1 >= r0) goto L_0x0036;	 Catch:{ all -> 0x0041 }
    L_0x0027:
        r2 = r7[r1];	 Catch:{ all -> 0x0041 }
        r3 = r6.availableAllocations;	 Catch:{ all -> 0x0041 }
        r4 = r6.availableCount;	 Catch:{ all -> 0x0041 }
        r5 = r4 + 1;	 Catch:{ all -> 0x0041 }
        r6.availableCount = r5;	 Catch:{ all -> 0x0041 }
        r3[r4] = r2;	 Catch:{ all -> 0x0041 }
        r1 = r1 + 1;	 Catch:{ all -> 0x0041 }
        goto L_0x0025;	 Catch:{ all -> 0x0041 }
    L_0x0036:
        r0 = r6.allocatedCount;	 Catch:{ all -> 0x0041 }
        r1 = r7.length;	 Catch:{ all -> 0x0041 }
        r0 = r0 - r1;	 Catch:{ all -> 0x0041 }
        r6.allocatedCount = r0;	 Catch:{ all -> 0x0041 }
        r6.notifyAll();	 Catch:{ all -> 0x0041 }
        monitor-exit(r6);
        return;
    L_0x0041:
        r7 = move-exception;
        monitor-exit(r6);
        throw r7;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.upstream.DefaultAllocator.release(com.google.android.exoplayer2.upstream.Allocation[]):void");
    }

    public synchronized void trim() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:32:0x0068 in {5, 13, 16, 17, 18, 22, 23, 24, 28, 31} preds:[]
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
        r8 = this;
        monitor-enter(r8);
        r0 = r8.targetBufferSize;	 Catch:{ all -> 0x0065 }
        r1 = r8.individualAllocationSize;	 Catch:{ all -> 0x0065 }
        r0 = com.google.android.exoplayer2.util.Util.ceilDivide(r0, r1);	 Catch:{ all -> 0x0065 }
        r1 = 0;	 Catch:{ all -> 0x0065 }
        r2 = r8.allocatedCount;	 Catch:{ all -> 0x0065 }
        r2 = r0 - r2;	 Catch:{ all -> 0x0065 }
        r1 = java.lang.Math.max(r1, r2);	 Catch:{ all -> 0x0065 }
        r2 = r8.availableCount;	 Catch:{ all -> 0x0065 }
        if (r1 < r2) goto L_0x0018;
    L_0x0016:
        monitor-exit(r8);
        return;
    L_0x0018:
        r2 = r8.initialAllocationBlock;	 Catch:{ all -> 0x0065 }
        if (r2 == 0) goto L_0x0058;	 Catch:{ all -> 0x0065 }
    L_0x001c:
        r2 = 0;	 Catch:{ all -> 0x0065 }
        r3 = r8.availableCount;	 Catch:{ all -> 0x0065 }
        r3 = r3 + -1;	 Catch:{ all -> 0x0065 }
    L_0x0021:
        if (r2 > r3) goto L_0x004c;	 Catch:{ all -> 0x0065 }
    L_0x0023:
        r4 = r8.availableAllocations;	 Catch:{ all -> 0x0065 }
        r4 = r4[r2];	 Catch:{ all -> 0x0065 }
        r5 = r4.data;	 Catch:{ all -> 0x0065 }
        r6 = r8.initialAllocationBlock;	 Catch:{ all -> 0x0065 }
        if (r5 != r6) goto L_0x0030;	 Catch:{ all -> 0x0065 }
    L_0x002d:
        r2 = r2 + 1;	 Catch:{ all -> 0x0065 }
        goto L_0x004b;	 Catch:{ all -> 0x0065 }
    L_0x0030:
        r5 = r8.availableAllocations;	 Catch:{ all -> 0x0065 }
        r5 = r5[r3];	 Catch:{ all -> 0x0065 }
        r6 = r5.data;	 Catch:{ all -> 0x0065 }
        r7 = r8.initialAllocationBlock;	 Catch:{ all -> 0x0065 }
        if (r6 == r7) goto L_0x003d;	 Catch:{ all -> 0x0065 }
    L_0x003a:
        r3 = r3 + -1;	 Catch:{ all -> 0x0065 }
        goto L_0x004b;	 Catch:{ all -> 0x0065 }
    L_0x003d:
        r6 = r8.availableAllocations;	 Catch:{ all -> 0x0065 }
        r7 = r2 + 1;	 Catch:{ all -> 0x0065 }
        r6[r2] = r5;	 Catch:{ all -> 0x0065 }
        r2 = r8.availableAllocations;	 Catch:{ all -> 0x0065 }
        r6 = r3 + -1;	 Catch:{ all -> 0x0065 }
        r2[r3] = r4;	 Catch:{ all -> 0x0065 }
        r3 = r6;	 Catch:{ all -> 0x0065 }
        r2 = r7;	 Catch:{ all -> 0x0065 }
    L_0x004b:
        goto L_0x0021;	 Catch:{ all -> 0x0065 }
    L_0x004c:
        r4 = java.lang.Math.max(r1, r2);	 Catch:{ all -> 0x0065 }
        r1 = r4;	 Catch:{ all -> 0x0065 }
        r4 = r8.availableCount;	 Catch:{ all -> 0x0065 }
        if (r1 < r4) goto L_0x0057;
    L_0x0055:
        monitor-exit(r8);
        return;
    L_0x0057:
        goto L_0x0059;
    L_0x0059:
        r2 = r8.availableAllocations;	 Catch:{ all -> 0x0065 }
        r3 = r8.availableCount;	 Catch:{ all -> 0x0065 }
        r4 = 0;	 Catch:{ all -> 0x0065 }
        java.util.Arrays.fill(r2, r1, r3, r4);	 Catch:{ all -> 0x0065 }
        r8.availableCount = r1;	 Catch:{ all -> 0x0065 }
        monitor-exit(r8);
        return;
    L_0x0065:
        r0 = move-exception;
        monitor-exit(r8);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.upstream.DefaultAllocator.trim():void");
    }

    public DefaultAllocator(boolean trimOnReset, int individualAllocationSize) {
        this(trimOnReset, individualAllocationSize, 0);
    }

    public DefaultAllocator(boolean trimOnReset, int individualAllocationSize, int initialAllocationCount) {
        boolean z = false;
        Assertions.checkArgument(individualAllocationSize > 0);
        if (initialAllocationCount >= 0) {
            z = true;
        }
        Assertions.checkArgument(z);
        this.trimOnReset = trimOnReset;
        this.individualAllocationSize = individualAllocationSize;
        this.availableCount = initialAllocationCount;
        this.availableAllocations = new Allocation[(initialAllocationCount + 100)];
        if (initialAllocationCount > 0) {
            this.initialAllocationBlock = new byte[(initialAllocationCount * individualAllocationSize)];
            for (int i = 0; i < initialAllocationCount; i++) {
                this.availableAllocations[i] = new Allocation(this.initialAllocationBlock, i * individualAllocationSize);
            }
        } else {
            this.initialAllocationBlock = null;
        }
        this.singleAllocationReleaseHolder = new Allocation[1];
    }

    public synchronized void reset() {
        if (this.trimOnReset) {
            setTargetBufferSize(0);
        }
    }

    public synchronized void setTargetBufferSize(int targetBufferSize) {
        boolean targetBufferSizeReduced = targetBufferSize < this.targetBufferSize;
        this.targetBufferSize = targetBufferSize;
        if (targetBufferSizeReduced) {
            trim();
        }
    }

    public synchronized Allocation allocate() {
        Allocation allocation;
        this.allocatedCount++;
        if (this.availableCount > 0) {
            allocation = this.availableAllocations;
            int i = this.availableCount - 1;
            this.availableCount = i;
            allocation = allocation[i];
            this.availableAllocations[this.availableCount] = null;
        } else {
            allocation = new Allocation(new byte[this.individualAllocationSize], 0);
        }
        return allocation;
    }

    public synchronized void release(Allocation allocation) {
        this.singleAllocationReleaseHolder[0] = allocation;
        release(this.singleAllocationReleaseHolder);
    }

    public synchronized int getTotalBytesAllocated() {
        return this.allocatedCount * this.individualAllocationSize;
    }

    public int getIndividualAllocationLength() {
        return this.individualAllocationSize;
    }
}
