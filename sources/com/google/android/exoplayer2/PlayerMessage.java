package com.google.android.exoplayer2;

import android.os.Handler;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.util.Assertions;

public final class PlayerMessage {
    private boolean deleteAfterDelivery = true;
    private Handler handler;
    private boolean isCanceled;
    private boolean isDelivered;
    private boolean isProcessed;
    private boolean isSent;
    @Nullable
    private Object payload;
    private long positionMs = C0555C.TIME_UNSET;
    private final Sender sender;
    private final Target target;
    private final Timeline timeline;
    private int type;
    private int windowIndex;

    public interface Sender {
        void sendMessage(PlayerMessage playerMessage);
    }

    public interface Target {
        void handleMessage(int i, @Nullable Object obj) throws ExoPlaybackException;
    }

    public synchronized boolean blockUntilDelivered() throws java.lang.InterruptedException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:16:0x002b in {4, 5, 9, 12, 15} preds:[]
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
        monitor-enter(r2);
        r0 = r2.isSent;	 Catch:{ all -> 0x0028 }
        com.google.android.exoplayer2.util.Assertions.checkState(r0);	 Catch:{ all -> 0x0028 }
        r0 = r2.handler;	 Catch:{ all -> 0x0028 }
        r0 = r0.getLooper();	 Catch:{ all -> 0x0028 }
        r0 = r0.getThread();	 Catch:{ all -> 0x0028 }
        r1 = java.lang.Thread.currentThread();	 Catch:{ all -> 0x0028 }
        if (r0 == r1) goto L_0x0018;	 Catch:{ all -> 0x0028 }
    L_0x0016:
        r0 = 1;	 Catch:{ all -> 0x0028 }
        goto L_0x0019;	 Catch:{ all -> 0x0028 }
    L_0x0018:
        r0 = 0;	 Catch:{ all -> 0x0028 }
    L_0x0019:
        com.google.android.exoplayer2.util.Assertions.checkState(r0);	 Catch:{ all -> 0x0028 }
    L_0x001c:
        r0 = r2.isProcessed;	 Catch:{ all -> 0x0028 }
        if (r0 != 0) goto L_0x0024;	 Catch:{ all -> 0x0028 }
    L_0x0020:
        r2.wait();	 Catch:{ all -> 0x0028 }
        goto L_0x001c;	 Catch:{ all -> 0x0028 }
    L_0x0024:
        r0 = r2.isDelivered;	 Catch:{ all -> 0x0028 }
        monitor-exit(r2);
        return r0;
    L_0x0028:
        r0 = move-exception;
        monitor-exit(r2);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.PlayerMessage.blockUntilDelivered():boolean");
    }

    public PlayerMessage(Sender sender, Target target, Timeline timeline, int defaultWindowIndex, Handler defaultHandler) {
        this.sender = sender;
        this.target = target;
        this.timeline = timeline;
        this.handler = defaultHandler;
        this.windowIndex = defaultWindowIndex;
    }

    public Timeline getTimeline() {
        return this.timeline;
    }

    public Target getTarget() {
        return this.target;
    }

    public PlayerMessage setType(int messageType) {
        Assertions.checkState(this.isSent ^ 1);
        this.type = messageType;
        return this;
    }

    public int getType() {
        return this.type;
    }

    public PlayerMessage setPayload(@Nullable Object payload) {
        Assertions.checkState(this.isSent ^ 1);
        this.payload = payload;
        return this;
    }

    @Nullable
    public Object getPayload() {
        return this.payload;
    }

    public PlayerMessage setHandler(Handler handler) {
        Assertions.checkState(this.isSent ^ 1);
        this.handler = handler;
        return this;
    }

    public Handler getHandler() {
        return this.handler;
    }

    public long getPositionMs() {
        return this.positionMs;
    }

    public PlayerMessage setPosition(long positionMs) {
        Assertions.checkState(this.isSent ^ 1);
        this.positionMs = positionMs;
        return this;
    }

    public PlayerMessage setPosition(int windowIndex, long positionMs) {
        boolean z = true;
        Assertions.checkState(this.isSent ^ true);
        if (positionMs == C0555C.TIME_UNSET) {
            z = false;
        }
        Assertions.checkArgument(z);
        if (windowIndex < 0 || (!this.timeline.isEmpty() && windowIndex >= this.timeline.getWindowCount())) {
            throw new IllegalSeekPositionException(this.timeline, windowIndex, positionMs);
        }
        this.windowIndex = windowIndex;
        this.positionMs = positionMs;
        return this;
    }

    public int getWindowIndex() {
        return this.windowIndex;
    }

    public PlayerMessage setDeleteAfterDelivery(boolean deleteAfterDelivery) {
        Assertions.checkState(this.isSent ^ 1);
        this.deleteAfterDelivery = deleteAfterDelivery;
        return this;
    }

    public boolean getDeleteAfterDelivery() {
        return this.deleteAfterDelivery;
    }

    public PlayerMessage send() {
        Assertions.checkState(this.isSent ^ true);
        if (this.positionMs == C0555C.TIME_UNSET) {
            Assertions.checkArgument(this.deleteAfterDelivery);
        }
        this.isSent = true;
        this.sender.sendMessage(this);
        return this;
    }

    public synchronized PlayerMessage cancel() {
        Assertions.checkState(this.isSent);
        this.isCanceled = true;
        markAsProcessed(false);
        return this;
    }

    public synchronized boolean isCanceled() {
        return this.isCanceled;
    }

    public synchronized void markAsProcessed(boolean isDelivered) {
        this.isDelivered |= isDelivered;
        this.isProcessed = true;
        notifyAll();
    }
}
