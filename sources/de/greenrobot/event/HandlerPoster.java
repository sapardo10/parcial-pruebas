package de.greenrobot.event;

import android.os.Handler;
import android.os.Looper;

final class HandlerPoster extends Handler {
    private final EventBus eventBus;
    private boolean handlerActive;
    private final int maxMillisInsideHandleMessage;
    private final PendingPostQueue queue = new PendingPostQueue();

    public void handleMessage(android.os.Message r10) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:34:0x0051 in {12, 15, 19, 20, 26, 29, 30, 33} preds:[]
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
        r9 = this;
        r0 = 0;
        r1 = android.os.SystemClock.uptimeMillis();	 Catch:{ all -> 0x004d }
    L_0x0005:
        r3 = r9.queue;	 Catch:{ all -> 0x004d }
        r3 = r3.poll();	 Catch:{ all -> 0x004d }
        if (r3 != 0) goto L_0x0023;	 Catch:{ all -> 0x004d }
    L_0x000d:
        monitor-enter(r9);	 Catch:{ all -> 0x004d }
        r4 = r9.queue;	 Catch:{ all -> 0x0020 }
        r4 = r4.poll();	 Catch:{ all -> 0x0020 }
        r3 = r4;	 Catch:{ all -> 0x0020 }
        if (r3 != 0) goto L_0x001e;	 Catch:{ all -> 0x0020 }
    L_0x0017:
        r4 = 0;	 Catch:{ all -> 0x0020 }
        r9.handlerActive = r4;	 Catch:{ all -> 0x0020 }
        monitor-exit(r9);	 Catch:{ all -> 0x0020 }
        r9.handlerActive = r0;
        return;
    L_0x001e:
        monitor-exit(r9);	 Catch:{ all -> 0x0020 }
        goto L_0x0024;	 Catch:{ all -> 0x0020 }
    L_0x0020:
        r4 = move-exception;	 Catch:{ all -> 0x0020 }
        monitor-exit(r9);	 Catch:{ all -> 0x0020 }
        throw r4;	 Catch:{ all -> 0x004d }
    L_0x0024:
        r4 = r9.eventBus;	 Catch:{ all -> 0x004d }
        r4.invokeSubscriber(r3);	 Catch:{ all -> 0x004d }
        r4 = android.os.SystemClock.uptimeMillis();	 Catch:{ all -> 0x004d }
        r4 = r4 - r1;	 Catch:{ all -> 0x004d }
        r6 = r9.maxMillisInsideHandleMessage;	 Catch:{ all -> 0x004d }
        r6 = (long) r6;	 Catch:{ all -> 0x004d }
        r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));	 Catch:{ all -> 0x004d }
        if (r8 < 0) goto L_0x004b;	 Catch:{ all -> 0x004d }
    L_0x0035:
        r6 = r9.obtainMessage();	 Catch:{ all -> 0x004d }
        r6 = r9.sendMessage(r6);	 Catch:{ all -> 0x004d }
        if (r6 == 0) goto L_0x0043;
    L_0x003f:
        r0 = 1;
        r9.handlerActive = r0;
        return;
    L_0x0043:
        r6 = new de.greenrobot.event.EventBusException;	 Catch:{ all -> 0x004d }
        r7 = "Could not send handler message";	 Catch:{ all -> 0x004d }
        r6.<init>(r7);	 Catch:{ all -> 0x004d }
        throw r6;	 Catch:{ all -> 0x004d }
        goto L_0x0005;
    L_0x004d:
        r1 = move-exception;
        r9.handlerActive = r0;
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.greenrobot.event.HandlerPoster.handleMessage(android.os.Message):void");
    }

    HandlerPoster(EventBus eventBus, Looper looper, int maxMillisInsideHandleMessage) {
        super(looper);
        this.eventBus = eventBus;
        this.maxMillisInsideHandleMessage = maxMillisInsideHandleMessage;
    }

    void enqueue(Subscription subscription, Object event) {
        PendingPost pendingPost = PendingPost.obtainPendingPost(subscription, event);
        synchronized (this) {
            this.queue.enqueue(pendingPost);
            if (!this.handlerActive) {
                this.handlerActive = true;
                if (!sendMessage(obtainMessage())) {
                    throw new EventBusException("Could not send handler message");
                }
            }
        }
    }
}
