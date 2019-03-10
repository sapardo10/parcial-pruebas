package okhttp3;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import okhttp3.RealCall.AsyncCall;
import okhttp3.internal.Util;

public final class Dispatcher {
    @Nullable
    private ExecutorService executorService;
    @Nullable
    private Runnable idleCallback;
    private int maxRequests = 64;
    private int maxRequestsPerHost = 5;
    private final Deque<AsyncCall> readyAsyncCalls = new ArrayDeque();
    private final Deque<AsyncCall> runningAsyncCalls = new ArrayDeque();
    private final Deque<RealCall> runningSyncCalls = new ArrayDeque();

    public synchronized void cancelAll() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:20:0x0050 in {5, 9, 14, 16, 19} preds:[]
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
        r3 = this;
        monitor-enter(r3);
        r0 = r3.readyAsyncCalls;	 Catch:{ all -> 0x004d }
        r0 = r0.iterator();	 Catch:{ all -> 0x004d }
    L_0x0007:
        r1 = r0.hasNext();	 Catch:{ all -> 0x004d }
        if (r1 == 0) goto L_0x001b;	 Catch:{ all -> 0x004d }
    L_0x000d:
        r1 = r0.next();	 Catch:{ all -> 0x004d }
        r1 = (okhttp3.RealCall.AsyncCall) r1;	 Catch:{ all -> 0x004d }
        r2 = r1.get();	 Catch:{ all -> 0x004d }
        r2.cancel();	 Catch:{ all -> 0x004d }
        goto L_0x0007;	 Catch:{ all -> 0x004d }
    L_0x001b:
        r0 = r3.runningAsyncCalls;	 Catch:{ all -> 0x004d }
        r0 = r0.iterator();	 Catch:{ all -> 0x004d }
    L_0x0021:
        r1 = r0.hasNext();	 Catch:{ all -> 0x004d }
        if (r1 == 0) goto L_0x0035;	 Catch:{ all -> 0x004d }
    L_0x0027:
        r1 = r0.next();	 Catch:{ all -> 0x004d }
        r1 = (okhttp3.RealCall.AsyncCall) r1;	 Catch:{ all -> 0x004d }
        r2 = r1.get();	 Catch:{ all -> 0x004d }
        r2.cancel();	 Catch:{ all -> 0x004d }
        goto L_0x0021;	 Catch:{ all -> 0x004d }
    L_0x0035:
        r0 = r3.runningSyncCalls;	 Catch:{ all -> 0x004d }
        r0 = r0.iterator();	 Catch:{ all -> 0x004d }
    L_0x003b:
        r1 = r0.hasNext();	 Catch:{ all -> 0x004d }
        if (r1 == 0) goto L_0x004b;	 Catch:{ all -> 0x004d }
    L_0x0041:
        r1 = r0.next();	 Catch:{ all -> 0x004d }
        r1 = (okhttp3.RealCall) r1;	 Catch:{ all -> 0x004d }
        r1.cancel();	 Catch:{ all -> 0x004d }
        goto L_0x003b;
    L_0x004b:
        monitor-exit(r3);
        return;
    L_0x004d:
        r0 = move-exception;
        monitor-exit(r3);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.Dispatcher.cancelAll():void");
    }

    public synchronized java.util.List<okhttp3.Call> queuedCalls() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:12:0x0029 in {5, 8, 11} preds:[]
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
        r4 = this;
        monitor-enter(r4);
        r0 = new java.util.ArrayList;	 Catch:{ all -> 0x0026 }
        r0.<init>();	 Catch:{ all -> 0x0026 }
        r1 = r4.readyAsyncCalls;	 Catch:{ all -> 0x0026 }
        r1 = r1.iterator();	 Catch:{ all -> 0x0026 }
    L_0x000c:
        r2 = r1.hasNext();	 Catch:{ all -> 0x0026 }
        if (r2 == 0) goto L_0x0020;	 Catch:{ all -> 0x0026 }
    L_0x0012:
        r2 = r1.next();	 Catch:{ all -> 0x0026 }
        r2 = (okhttp3.RealCall.AsyncCall) r2;	 Catch:{ all -> 0x0026 }
        r3 = r2.get();	 Catch:{ all -> 0x0026 }
        r0.add(r3);	 Catch:{ all -> 0x0026 }
        goto L_0x000c;	 Catch:{ all -> 0x0026 }
    L_0x0020:
        r1 = java.util.Collections.unmodifiableList(r0);	 Catch:{ all -> 0x0026 }
        monitor-exit(r4);
        return r1;
    L_0x0026:
        r0 = move-exception;
        monitor-exit(r4);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.Dispatcher.queuedCalls():java.util.List<okhttp3.Call>");
    }

    public synchronized java.util.List<okhttp3.Call> runningCalls() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:12:0x002e in {5, 8, 11} preds:[]
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
        r4 = this;
        monitor-enter(r4);
        r0 = new java.util.ArrayList;	 Catch:{ all -> 0x002b }
        r0.<init>();	 Catch:{ all -> 0x002b }
        r1 = r4.runningSyncCalls;	 Catch:{ all -> 0x002b }
        r0.addAll(r1);	 Catch:{ all -> 0x002b }
        r1 = r4.runningAsyncCalls;	 Catch:{ all -> 0x002b }
        r1 = r1.iterator();	 Catch:{ all -> 0x002b }
    L_0x0011:
        r2 = r1.hasNext();	 Catch:{ all -> 0x002b }
        if (r2 == 0) goto L_0x0025;	 Catch:{ all -> 0x002b }
    L_0x0017:
        r2 = r1.next();	 Catch:{ all -> 0x002b }
        r2 = (okhttp3.RealCall.AsyncCall) r2;	 Catch:{ all -> 0x002b }
        r3 = r2.get();	 Catch:{ all -> 0x002b }
        r0.add(r3);	 Catch:{ all -> 0x002b }
        goto L_0x0011;	 Catch:{ all -> 0x002b }
    L_0x0025:
        r1 = java.util.Collections.unmodifiableList(r0);	 Catch:{ all -> 0x002b }
        monitor-exit(r4);
        return r1;
    L_0x002b:
        r0 = move-exception;
        monitor-exit(r4);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.Dispatcher.runningCalls():java.util.List<okhttp3.Call>");
    }

    public Dispatcher(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public synchronized ExecutorService executorService() {
        if (this.executorService == null) {
            this.executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS, new SynchronousQueue(), Util.threadFactory("OkHttp Dispatcher", false));
        }
        return this.executorService;
    }

    public synchronized void setMaxRequests(int maxRequests) {
        if (maxRequests >= 1) {
            this.maxRequests = maxRequests;
            promoteCalls();
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("max < 1: ");
            stringBuilder.append(maxRequests);
            throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    public synchronized int getMaxRequests() {
        return this.maxRequests;
    }

    public synchronized void setMaxRequestsPerHost(int maxRequestsPerHost) {
        if (maxRequestsPerHost >= 1) {
            this.maxRequestsPerHost = maxRequestsPerHost;
            promoteCalls();
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("max < 1: ");
            stringBuilder.append(maxRequestsPerHost);
            throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    public synchronized int getMaxRequestsPerHost() {
        return this.maxRequestsPerHost;
    }

    public synchronized void setIdleCallback(@Nullable Runnable idleCallback) {
        this.idleCallback = idleCallback;
    }

    synchronized void enqueue(AsyncCall call) {
        if (this.runningAsyncCalls.size() >= this.maxRequests || runningCallsForHost(call) >= this.maxRequestsPerHost) {
            this.readyAsyncCalls.add(call);
        } else {
            this.runningAsyncCalls.add(call);
            executorService().execute(call);
        }
    }

    private void promoteCalls() {
        if (this.runningAsyncCalls.size() < this.maxRequests && !this.readyAsyncCalls.isEmpty()) {
            Iterator<AsyncCall> i = this.readyAsyncCalls.iterator();
            while (i.hasNext()) {
                AsyncCall call = (AsyncCall) i.next();
                if (runningCallsForHost(call) < this.maxRequestsPerHost) {
                    i.remove();
                    this.runningAsyncCalls.add(call);
                    executorService().execute(call);
                }
                if (this.runningAsyncCalls.size() >= this.maxRequests) {
                    return;
                }
            }
        }
    }

    private int runningCallsForHost(AsyncCall call) {
        int result = 0;
        for (AsyncCall c : this.runningAsyncCalls) {
            if (!c.get().forWebSocket) {
                if (c.host().equals(call.host())) {
                    result++;
                }
            }
        }
        return result;
    }

    synchronized void executed(RealCall call) {
        this.runningSyncCalls.add(call);
    }

    void finished(AsyncCall call) {
        finished(this.runningAsyncCalls, call, true);
    }

    void finished(RealCall call) {
        finished(this.runningSyncCalls, call, false);
    }

    private <T> void finished(Deque<T> calls, T call, boolean promoteCalls) {
        synchronized (this) {
            if (calls.remove(call)) {
                if (promoteCalls) {
                    promoteCalls();
                }
                int runningCallsCount = runningCallsCount();
                Runnable idleCallback = this.idleCallback;
            } else {
                throw new AssertionError("Call wasn't in-flight!");
            }
        }
        if (runningCallsCount == 0 && idleCallback != null) {
            idleCallback.run();
        }
    }

    public synchronized int queuedCallsCount() {
        return this.readyAsyncCalls.size();
    }

    public synchronized int runningCallsCount() {
        return this.runningAsyncCalls.size() + this.runningSyncCalls.size();
    }
}
