package de.greenrobot.event;

final class BackgroundPoster implements Runnable {
    private final EventBus eventBus;
    private volatile boolean executorRunning;
    private final PendingPostQueue queue = new PendingPostQueue();

    public void run() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:31:0x0050 in {11, 14, 18, 19, 21, 28, 30} preds:[]
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
    L_0x0000:
        r0 = 0;
        r1 = r5.queue;	 Catch:{ InterruptedException -> 0x0029 }
        r2 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;	 Catch:{ InterruptedException -> 0x0029 }
        r1 = r1.poll(r2);	 Catch:{ InterruptedException -> 0x0029 }
        if (r1 != 0) goto L_0x0020;	 Catch:{ InterruptedException -> 0x0029 }
    L_0x000b:
        monitor-enter(r5);	 Catch:{ InterruptedException -> 0x0029 }
        r2 = r5.queue;	 Catch:{ all -> 0x001d }
        r2 = r2.poll();	 Catch:{ all -> 0x001d }
        r1 = r2;	 Catch:{ all -> 0x001d }
        if (r1 != 0) goto L_0x001b;	 Catch:{ all -> 0x001d }
    L_0x0015:
        r5.executorRunning = r0;	 Catch:{ all -> 0x001d }
        monitor-exit(r5);	 Catch:{ all -> 0x001d }
        r5.executorRunning = r0;
        return;
    L_0x001b:
        monitor-exit(r5);	 Catch:{ all -> 0x001d }
        goto L_0x0021;	 Catch:{ all -> 0x001d }
    L_0x001d:
        r2 = move-exception;	 Catch:{ all -> 0x001d }
        monitor-exit(r5);	 Catch:{ all -> 0x001d }
        throw r2;	 Catch:{ InterruptedException -> 0x0029 }
    L_0x0021:
        r2 = r5.eventBus;	 Catch:{ InterruptedException -> 0x0029 }
        r2.invokeSubscriber(r1);	 Catch:{ InterruptedException -> 0x0029 }
        goto L_0x0000;
    L_0x0027:
        r1 = move-exception;
        goto L_0x004d;
    L_0x0029:
        r1 = move-exception;
        r2 = "Event";	 Catch:{ all -> 0x0027 }
        r3 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0027 }
        r3.<init>();	 Catch:{ all -> 0x0027 }
        r4 = java.lang.Thread.currentThread();	 Catch:{ all -> 0x0027 }
        r4 = r4.getName();	 Catch:{ all -> 0x0027 }
        r3.append(r4);	 Catch:{ all -> 0x0027 }
        r4 = " was interruppted";	 Catch:{ all -> 0x0027 }
        r3.append(r4);	 Catch:{ all -> 0x0027 }
        r3 = r3.toString();	 Catch:{ all -> 0x0027 }
        android.util.Log.w(r2, r3, r1);	 Catch:{ all -> 0x0027 }
        r5.executorRunning = r0;
        return;
    L_0x004d:
        r5.executorRunning = r0;
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.greenrobot.event.BackgroundPoster.run():void");
    }

    BackgroundPoster(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void enqueue(Subscription subscription, Object event) {
        PendingPost pendingPost = PendingPost.obtainPendingPost(subscription, event);
        synchronized (this) {
            this.queue.enqueue(pendingPost);
            if (!this.executorRunning) {
                this.executorRunning = true;
                this.eventBus.getExecutorService().execute(this);
            }
        }
    }
}
