package org.apache.commons.io.monitor;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadFactory;

public final class FileAlterationMonitor implements Runnable {
    private final long interval;
    private final List<FileAlterationObserver> observers;
    private volatile boolean running;
    private Thread thread;
    private ThreadFactory threadFactory;

    public synchronized void start() throws java.lang.Exception {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:21:0x0045 in {7, 10, 11, 14, 17, 20} preds:[]
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
        r2 = this;
        monitor-enter(r2);
        r0 = r2.running;	 Catch:{ all -> 0x0042 }
        if (r0 != 0) goto L_0x003a;	 Catch:{ all -> 0x0042 }
    L_0x0005:
        r0 = r2.observers;	 Catch:{ all -> 0x0042 }
        r0 = r0.iterator();	 Catch:{ all -> 0x0042 }
    L_0x000b:
        r1 = r0.hasNext();	 Catch:{ all -> 0x0042 }
        if (r1 == 0) goto L_0x001b;	 Catch:{ all -> 0x0042 }
    L_0x0011:
        r1 = r0.next();	 Catch:{ all -> 0x0042 }
        r1 = (org.apache.commons.io.monitor.FileAlterationObserver) r1;	 Catch:{ all -> 0x0042 }
        r1.initialize();	 Catch:{ all -> 0x0042 }
        goto L_0x000b;	 Catch:{ all -> 0x0042 }
        r0 = 1;	 Catch:{ all -> 0x0042 }
        r2.running = r0;	 Catch:{ all -> 0x0042 }
        r0 = r2.threadFactory;	 Catch:{ all -> 0x0042 }
        if (r0 == 0) goto L_0x002c;	 Catch:{ all -> 0x0042 }
    L_0x0023:
        r0 = r2.threadFactory;	 Catch:{ all -> 0x0042 }
        r0 = r0.newThread(r2);	 Catch:{ all -> 0x0042 }
        r2.thread = r0;	 Catch:{ all -> 0x0042 }
        goto L_0x0033;	 Catch:{ all -> 0x0042 }
    L_0x002c:
        r0 = new java.lang.Thread;	 Catch:{ all -> 0x0042 }
        r0.<init>(r2);	 Catch:{ all -> 0x0042 }
        r2.thread = r0;	 Catch:{ all -> 0x0042 }
    L_0x0033:
        r0 = r2.thread;	 Catch:{ all -> 0x0042 }
        r0.start();	 Catch:{ all -> 0x0042 }
        monitor-exit(r2);
        return;
    L_0x003a:
        r0 = new java.lang.IllegalStateException;	 Catch:{ all -> 0x0042 }
        r1 = "Monitor is already running";	 Catch:{ all -> 0x0042 }
        r0.<init>(r1);	 Catch:{ all -> 0x0042 }
        throw r0;	 Catch:{ all -> 0x0042 }
    L_0x0042:
        r0 = move-exception;
        monitor-exit(r2);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.io.monitor.FileAlterationMonitor.start():void");
    }

    public synchronized void stop(long r3) throws java.lang.Exception {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:25:0x003a in {7, 10, 15, 18, 21, 24} preds:[]
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
        r2 = this;
        monitor-enter(r2);
        r0 = r2.running;	 Catch:{ all -> 0x0037 }
        if (r0 == 0) goto L_0x002f;	 Catch:{ all -> 0x0037 }
    L_0x0005:
        r0 = 0;	 Catch:{ all -> 0x0037 }
        r2.running = r0;	 Catch:{ all -> 0x0037 }
        r0 = r2.thread;	 Catch:{ InterruptedException -> 0x000e }
        r0.join(r3);	 Catch:{ InterruptedException -> 0x000e }
        goto L_0x0016;
    L_0x000e:
        r0 = move-exception;
        r1 = java.lang.Thread.currentThread();	 Catch:{ all -> 0x0037 }
        r1.interrupt();	 Catch:{ all -> 0x0037 }
    L_0x0016:
        r0 = r2.observers;	 Catch:{ all -> 0x0037 }
        r0 = r0.iterator();	 Catch:{ all -> 0x0037 }
    L_0x001c:
        r1 = r0.hasNext();	 Catch:{ all -> 0x0037 }
        if (r1 == 0) goto L_0x002c;	 Catch:{ all -> 0x0037 }
    L_0x0022:
        r1 = r0.next();	 Catch:{ all -> 0x0037 }
        r1 = (org.apache.commons.io.monitor.FileAlterationObserver) r1;	 Catch:{ all -> 0x0037 }
        r1.destroy();	 Catch:{ all -> 0x0037 }
        goto L_0x001c;
        monitor-exit(r2);
        return;
    L_0x002f:
        r0 = new java.lang.IllegalStateException;	 Catch:{ all -> 0x0037 }
        r1 = "Monitor is not running";	 Catch:{ all -> 0x0037 }
        r0.<init>(r1);	 Catch:{ all -> 0x0037 }
        throw r0;	 Catch:{ all -> 0x0037 }
    L_0x0037:
        r3 = move-exception;
        monitor-exit(r2);
        throw r3;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.io.monitor.FileAlterationMonitor.stop(long):void");
    }

    public FileAlterationMonitor() {
        this(10000);
    }

    public FileAlterationMonitor(long interval) {
        this.observers = new CopyOnWriteArrayList();
        this.thread = null;
        this.running = false;
        this.interval = interval;
    }

    public FileAlterationMonitor(long interval, FileAlterationObserver... observers) {
        this(interval);
        if (observers != null) {
            for (FileAlterationObserver observer : observers) {
                addObserver(observer);
            }
        }
    }

    public long getInterval() {
        return this.interval;
    }

    public synchronized void setThreadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
    }

    public void addObserver(FileAlterationObserver observer) {
        if (observer != null) {
            this.observers.add(observer);
        }
    }

    public void removeObserver(FileAlterationObserver observer) {
        if (observer != null) {
            while (this.observers.remove(observer)) {
            }
        }
    }

    public Iterable<FileAlterationObserver> getObservers() {
        return this.observers;
    }

    public synchronized void stop() throws Exception {
        stop(this.interval);
    }

    public void run() {
        while (this.running) {
            for (FileAlterationObserver observer : this.observers) {
                observer.checkAndNotify();
            }
            if (this.running) {
                try {
                    Thread.sleep(this.interval);
                } catch (InterruptedException e) {
                }
            } else {
                return;
            }
        }
    }
}
