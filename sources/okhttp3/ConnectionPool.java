package okhttp3;

import java.lang.ref.Reference;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import okhttp3.internal.Util;
import okhttp3.internal.connection.RealConnection;
import okhttp3.internal.connection.RouteDatabase;
import okhttp3.internal.connection.StreamAllocation;
import okhttp3.internal.connection.StreamAllocation.StreamAllocationReference;
import okhttp3.internal.platform.Platform;

public final class ConnectionPool {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final Executor executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS, new SynchronousQueue(), Util.threadFactory("OkHttp ConnectionPool", true));
    private final Runnable cleanupRunnable;
    boolean cleanupRunning;
    private final Deque<RealConnection> connections;
    private final long keepAliveDurationNs;
    private final int maxIdleConnections;
    final RouteDatabase routeDatabase;

    long cleanup(long r11) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:38:0x0064 in {8, 11, 12, 13, 18, 22, 26, 29, 33, 37} preds:[]
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
        r10 = this;
        r0 = 0;
        r1 = 0;
        r2 = 0;
        r3 = -9223372036854775808;
        monitor-enter(r10);
        r5 = r10.connections;	 Catch:{ all -> 0x0061 }
        r5 = r5.iterator();	 Catch:{ all -> 0x0061 }
    L_0x000c:
        r6 = r5.hasNext();	 Catch:{ all -> 0x0061 }
        if (r6 == 0) goto L_0x0030;	 Catch:{ all -> 0x0061 }
    L_0x0012:
        r6 = r5.next();	 Catch:{ all -> 0x0061 }
        r6 = (okhttp3.internal.connection.RealConnection) r6;	 Catch:{ all -> 0x0061 }
        r7 = r10.pruneAndGetAllocationCount(r6, r11);	 Catch:{ all -> 0x0061 }
        if (r7 <= 0) goto L_0x0021;	 Catch:{ all -> 0x0061 }
    L_0x001e:
        r0 = r0 + 1;	 Catch:{ all -> 0x0061 }
        goto L_0x000c;	 Catch:{ all -> 0x0061 }
    L_0x0021:
        r1 = r1 + 1;	 Catch:{ all -> 0x0061 }
        r7 = r6.idleAtNanos;	 Catch:{ all -> 0x0061 }
        r7 = r11 - r7;	 Catch:{ all -> 0x0061 }
        r9 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1));	 Catch:{ all -> 0x0061 }
        if (r9 <= 0) goto L_0x002e;	 Catch:{ all -> 0x0061 }
    L_0x002b:
        r3 = r7;	 Catch:{ all -> 0x0061 }
        r2 = r6;	 Catch:{ all -> 0x0061 }
        goto L_0x002f;	 Catch:{ all -> 0x0061 }
    L_0x002f:
        goto L_0x000c;	 Catch:{ all -> 0x0061 }
        r5 = r10.keepAliveDurationNs;	 Catch:{ all -> 0x0061 }
        r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));	 Catch:{ all -> 0x0061 }
        if (r7 >= 0) goto L_0x0050;	 Catch:{ all -> 0x0061 }
    L_0x0037:
        r5 = r10.maxIdleConnections;	 Catch:{ all -> 0x0061 }
        if (r1 <= r5) goto L_0x003c;	 Catch:{ all -> 0x0061 }
    L_0x003b:
        goto L_0x0050;	 Catch:{ all -> 0x0061 }
    L_0x003c:
        if (r1 <= 0) goto L_0x0043;	 Catch:{ all -> 0x0061 }
    L_0x003e:
        r5 = r10.keepAliveDurationNs;	 Catch:{ all -> 0x0061 }
        r5 = r5 - r3;	 Catch:{ all -> 0x0061 }
        monitor-exit(r10);	 Catch:{ all -> 0x0061 }
        return r5;	 Catch:{ all -> 0x0061 }
    L_0x0043:
        if (r0 <= 0) goto L_0x0049;	 Catch:{ all -> 0x0061 }
    L_0x0045:
        r5 = r10.keepAliveDurationNs;	 Catch:{ all -> 0x0061 }
        monitor-exit(r10);	 Catch:{ all -> 0x0061 }
        return r5;	 Catch:{ all -> 0x0061 }
    L_0x0049:
        r5 = 0;	 Catch:{ all -> 0x0061 }
        r10.cleanupRunning = r5;	 Catch:{ all -> 0x0061 }
        r5 = -1;	 Catch:{ all -> 0x0061 }
        monitor-exit(r10);	 Catch:{ all -> 0x0061 }
        return r5;	 Catch:{ all -> 0x0061 }
        r5 = r10.connections;	 Catch:{ all -> 0x0061 }
        r5.remove(r2);	 Catch:{ all -> 0x0061 }
        monitor-exit(r10);	 Catch:{ all -> 0x0061 }
        r5 = r2.socket();
        okhttp3.internal.Util.closeQuietly(r5);
        r5 = 0;
        return r5;
    L_0x0061:
        r5 = move-exception;
        monitor-exit(r10);	 Catch:{ all -> 0x0061 }
        throw r5;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.ConnectionPool.cleanup(long):long");
    }

    public void evictAll() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:22:0x004a in {8, 9, 10, 16, 17, 21} preds:[]
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
        r0 = new java.util.ArrayList;
        r0.<init>();
        monitor-enter(r4);
        r1 = r4.connections;	 Catch:{ all -> 0x0047 }
        r1 = r1.iterator();	 Catch:{ all -> 0x0047 }
    L_0x000c:
        r2 = r1.hasNext();	 Catch:{ all -> 0x0047 }
        if (r2 == 0) goto L_0x002c;	 Catch:{ all -> 0x0047 }
    L_0x0012:
        r2 = r1.next();	 Catch:{ all -> 0x0047 }
        r2 = (okhttp3.internal.connection.RealConnection) r2;	 Catch:{ all -> 0x0047 }
        r3 = r2.allocations;	 Catch:{ all -> 0x0047 }
        r3 = r3.isEmpty();	 Catch:{ all -> 0x0047 }
        if (r3 == 0) goto L_0x002a;	 Catch:{ all -> 0x0047 }
    L_0x0020:
        r3 = 1;	 Catch:{ all -> 0x0047 }
        r2.noNewStreams = r3;	 Catch:{ all -> 0x0047 }
        r0.add(r2);	 Catch:{ all -> 0x0047 }
        r1.remove();	 Catch:{ all -> 0x0047 }
        goto L_0x002b;	 Catch:{ all -> 0x0047 }
    L_0x002b:
        goto L_0x000c;	 Catch:{ all -> 0x0047 }
        monitor-exit(r4);	 Catch:{ all -> 0x0047 }
        r1 = r0.iterator();
    L_0x0032:
        r2 = r1.hasNext();
        if (r2 == 0) goto L_0x0046;
    L_0x0038:
        r2 = r1.next();
        r2 = (okhttp3.internal.connection.RealConnection) r2;
        r3 = r2.socket();
        okhttp3.internal.Util.closeQuietly(r3);
        goto L_0x0032;
    L_0x0046:
        return;
    L_0x0047:
        r1 = move-exception;
        monitor-exit(r4);	 Catch:{ all -> 0x0047 }
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.ConnectionPool.evictAll():void");
    }

    public synchronized int idleConnectionCount() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:15:0x0025 in {8, 9, 11, 14} preds:[]
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
        r0 = 0;
        r1 = r4.connections;	 Catch:{ all -> 0x0022 }
        r1 = r1.iterator();	 Catch:{ all -> 0x0022 }
    L_0x0008:
        r2 = r1.hasNext();	 Catch:{ all -> 0x0022 }
        if (r2 == 0) goto L_0x0020;	 Catch:{ all -> 0x0022 }
    L_0x000e:
        r2 = r1.next();	 Catch:{ all -> 0x0022 }
        r2 = (okhttp3.internal.connection.RealConnection) r2;	 Catch:{ all -> 0x0022 }
        r3 = r2.allocations;	 Catch:{ all -> 0x0022 }
        r3 = r3.isEmpty();	 Catch:{ all -> 0x0022 }
        if (r3 == 0) goto L_0x001f;
    L_0x001c:
        r0 = r0 + 1;
    L_0x001f:
        goto L_0x0008;
    L_0x0020:
        monitor-exit(r4);
        return r0;
    L_0x0022:
        r0 = move-exception;
        monitor-exit(r4);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.ConnectionPool.idleConnectionCount():int");
    }

    public ConnectionPool() {
        this(5, 5, TimeUnit.MINUTES);
    }

    public ConnectionPool(int maxIdleConnections, long keepAliveDuration, TimeUnit timeUnit) {
        this.cleanupRunnable = new ConnectionPool$1(this);
        this.connections = new ArrayDeque();
        this.routeDatabase = new RouteDatabase();
        this.maxIdleConnections = maxIdleConnections;
        this.keepAliveDurationNs = timeUnit.toNanos(keepAliveDuration);
        if (keepAliveDuration <= 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("keepAliveDuration <= 0: ");
            stringBuilder.append(keepAliveDuration);
            throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    public synchronized int connectionCount() {
        return this.connections.size();
    }

    @Nullable
    RealConnection get(Address address, StreamAllocation streamAllocation, Route route) {
        for (RealConnection connection : this.connections) {
            if (connection.isEligible(address, route)) {
                streamAllocation.acquire(connection, true);
                return connection;
            }
        }
        return null;
    }

    @Nullable
    Socket deduplicate(Address address, StreamAllocation streamAllocation) {
        for (RealConnection connection : this.connections) {
            if (connection.isEligible(address, null)) {
                if (connection.isMultiplexed()) {
                    if (connection != streamAllocation.connection()) {
                        return streamAllocation.releaseAndAcquire(connection);
                    }
                }
            }
        }
        return null;
    }

    void put(RealConnection connection) {
        if (!this.cleanupRunning) {
            this.cleanupRunning = true;
            executor.execute(this.cleanupRunnable);
        }
        this.connections.add(connection);
    }

    boolean connectionBecameIdle(RealConnection connection) {
        if (!connection.noNewStreams) {
            if (this.maxIdleConnections != 0) {
                notifyAll();
                return false;
            }
        }
        this.connections.remove(connection);
        return true;
    }

    private int pruneAndGetAllocationCount(RealConnection connection, long now) {
        List<Reference<StreamAllocation>> references = connection.allocations;
        int i = 0;
        while (i < references.size()) {
            Reference<StreamAllocation> reference = (Reference) references.get(i);
            if (reference.get() != null) {
                i++;
            } else {
                StreamAllocationReference streamAllocRef = (StreamAllocationReference) reference;
                String message = new StringBuilder();
                message.append("A connection to ");
                message.append(connection.route().address().url());
                message.append(" was leaked. Did you forget to close a response body?");
                Platform.get().logCloseableLeak(message.toString(), streamAllocRef.callStackTrace);
                references.remove(i);
                connection.noNewStreams = true;
                if (references.isEmpty()) {
                    connection.idleAtNanos = now - this.keepAliveDurationNs;
                    return 0;
                }
            }
        }
        return references.size();
    }
}
