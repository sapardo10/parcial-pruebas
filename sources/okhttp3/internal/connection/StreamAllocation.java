package okhttp3.internal.connection;

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.Socket;
import okhttp3.Address;
import okhttp3.Call;
import okhttp3.Connection;
import okhttp3.ConnectionPool;
import okhttp3.EventListener;
import okhttp3.Interceptor$Chain;
import okhttp3.OkHttpClient;
import okhttp3.Route;
import okhttp3.internal.Internal;
import okhttp3.internal.Util;
import okhttp3.internal.connection.RouteSelector.Selection;
import okhttp3.internal.http.HttpCodec;

public final class StreamAllocation {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public final Address address;
    public final Call call;
    private final Object callStackTrace;
    private boolean canceled;
    private HttpCodec codec;
    private RealConnection connection;
    private final ConnectionPool connectionPool;
    public final EventListener eventListener;
    private int refusedStreamCount;
    private boolean released;
    private boolean reportedAcquired;
    private Route route;
    private Selection routeSelection;
    private final RouteSelector routeSelector;

    public static final class StreamAllocationReference extends WeakReference<StreamAllocation> {
        public final Object callStackTrace;

        StreamAllocationReference(StreamAllocation referent, Object callStackTrace) {
            super(referent);
            this.callStackTrace = callStackTrace;
        }
    }

    private okhttp3.internal.connection.RealConnection findConnection(int r19, int r20, int r21, int r22, boolean r23) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:96:0x0157 in {11, 12, 15, 16, 20, 21, 22, 26, 27, 29, 30, 32, 38, 39, 40, 51, 52, 53, 54, 57, 58, 59, 60, 64, 71, 72, 75, 79, 82, 85, 88, 90, 92, 95} preds:[]
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
        r18 = this;
        r1 = r18;
        r2 = 0;
        r3 = 0;
        r4 = 0;
        r5 = r1.connectionPool;
        monitor-enter(r5);
        r0 = r1.released;	 Catch:{ all -> 0x0154 }
        if (r0 != 0) goto L_0x014c;	 Catch:{ all -> 0x0154 }
    L_0x000c:
        r0 = r1.codec;	 Catch:{ all -> 0x0154 }
        if (r0 != 0) goto L_0x0144;	 Catch:{ all -> 0x0154 }
    L_0x0010:
        r0 = r1.canceled;	 Catch:{ all -> 0x0154 }
        if (r0 != 0) goto L_0x013c;	 Catch:{ all -> 0x0154 }
    L_0x0014:
        r0 = r1.connection;	 Catch:{ all -> 0x0154 }
        r6 = r18.releaseIfNoNewStreams();	 Catch:{ all -> 0x0154 }
        r7 = r1.connection;	 Catch:{ all -> 0x0154 }
        if (r7 == 0) goto L_0x0023;	 Catch:{ all -> 0x0154 }
    L_0x001e:
        r7 = r1.connection;	 Catch:{ all -> 0x0154 }
        r3 = r7;	 Catch:{ all -> 0x0154 }
        r0 = 0;	 Catch:{ all -> 0x0154 }
        goto L_0x0024;	 Catch:{ all -> 0x0154 }
    L_0x0024:
        r7 = r1.reportedAcquired;	 Catch:{ all -> 0x0154 }
        if (r7 != 0) goto L_0x002b;	 Catch:{ all -> 0x0154 }
    L_0x0028:
        r0 = 0;	 Catch:{ all -> 0x0154 }
        r7 = r0;	 Catch:{ all -> 0x0154 }
        goto L_0x002c;	 Catch:{ all -> 0x0154 }
    L_0x002b:
        r7 = r0;	 Catch:{ all -> 0x0154 }
    L_0x002c:
        if (r3 != 0) goto L_0x0045;	 Catch:{ all -> 0x0154 }
    L_0x002e:
        r0 = okhttp3.internal.Internal.instance;	 Catch:{ all -> 0x0154 }
        r8 = r1.connectionPool;	 Catch:{ all -> 0x0154 }
        r9 = r1.address;	 Catch:{ all -> 0x0154 }
        r10 = 0;	 Catch:{ all -> 0x0154 }
        r0.get(r8, r9, r1, r10);	 Catch:{ all -> 0x0154 }
        r0 = r1.connection;	 Catch:{ all -> 0x0154 }
        if (r0 == 0) goto L_0x0041;	 Catch:{ all -> 0x0154 }
    L_0x003c:
        r2 = 1;	 Catch:{ all -> 0x0154 }
        r0 = r1.connection;	 Catch:{ all -> 0x0154 }
        r3 = r0;	 Catch:{ all -> 0x0154 }
        goto L_0x0046;	 Catch:{ all -> 0x0154 }
    L_0x0041:
        r0 = r1.route;	 Catch:{ all -> 0x0154 }
        r4 = r0;	 Catch:{ all -> 0x0154 }
        goto L_0x0046;	 Catch:{ all -> 0x0154 }
    L_0x0046:
        monitor-exit(r5);	 Catch:{ all -> 0x0154 }
        okhttp3.internal.Util.closeQuietly(r6);
        if (r7 == 0) goto L_0x0054;
    L_0x004c:
        r0 = r1.eventListener;
        r5 = r1.call;
        r0.connectionReleased(r5, r7);
        goto L_0x0055;
    L_0x0055:
        if (r2 == 0) goto L_0x005f;
    L_0x0057:
        r0 = r1.eventListener;
        r5 = r1.call;
        r0.connectionAcquired(r5, r3);
        goto L_0x0060;
    L_0x0060:
        if (r3 == 0) goto L_0x0063;
    L_0x0062:
        return r3;
    L_0x0063:
        r0 = 0;
        if (r4 != 0) goto L_0x007b;
    L_0x0066:
        r5 = r1.routeSelection;
        if (r5 == 0) goto L_0x0070;
    L_0x006a:
        r5 = r5.hasNext();
        if (r5 != 0) goto L_0x007b;
    L_0x0070:
        r0 = 1;
        r5 = r1.routeSelector;
        r5 = r5.next();
        r1.routeSelection = r5;
        r8 = r0;
        goto L_0x007d;
        r8 = r0;
    L_0x007d:
        r9 = r1.connectionPool;
        monitor-enter(r9);
        r0 = r1.canceled;	 Catch:{ all -> 0x0139 }
        if (r0 != 0) goto L_0x0131;	 Catch:{ all -> 0x0139 }
    L_0x0084:
        if (r8 == 0) goto L_0x00b2;	 Catch:{ all -> 0x0139 }
    L_0x0086:
        r0 = r1.routeSelection;	 Catch:{ all -> 0x0139 }
        r0 = r0.getAll();	 Catch:{ all -> 0x0139 }
        r5 = 0;	 Catch:{ all -> 0x0139 }
        r10 = r0.size();	 Catch:{ all -> 0x0139 }
    L_0x0091:
        if (r5 >= r10) goto L_0x00b1;	 Catch:{ all -> 0x0139 }
    L_0x0093:
        r11 = r0.get(r5);	 Catch:{ all -> 0x0139 }
        r11 = (okhttp3.Route) r11;	 Catch:{ all -> 0x0139 }
        r12 = okhttp3.internal.Internal.instance;	 Catch:{ all -> 0x0139 }
        r13 = r1.connectionPool;	 Catch:{ all -> 0x0139 }
        r14 = r1.address;	 Catch:{ all -> 0x0139 }
        r12.get(r13, r14, r1, r11);	 Catch:{ all -> 0x0139 }
        r12 = r1.connection;	 Catch:{ all -> 0x0139 }
        if (r12 == 0) goto L_0x00ad;	 Catch:{ all -> 0x0139 }
    L_0x00a6:
        r2 = 1;	 Catch:{ all -> 0x0139 }
        r12 = r1.connection;	 Catch:{ all -> 0x0139 }
        r3 = r12;	 Catch:{ all -> 0x0139 }
        r1.route = r11;	 Catch:{ all -> 0x0139 }
        goto L_0x00b3;	 Catch:{ all -> 0x0139 }
        r5 = r5 + 1;	 Catch:{ all -> 0x0139 }
        goto L_0x0091;	 Catch:{ all -> 0x0139 }
    L_0x00b1:
        goto L_0x00b3;	 Catch:{ all -> 0x0139 }
    L_0x00b3:
        if (r2 != 0) goto L_0x00d1;	 Catch:{ all -> 0x0139 }
    L_0x00b5:
        if (r4 != 0) goto L_0x00bf;	 Catch:{ all -> 0x0139 }
    L_0x00b7:
        r0 = r1.routeSelection;	 Catch:{ all -> 0x0139 }
        r0 = r0.next();	 Catch:{ all -> 0x0139 }
        r4 = r0;	 Catch:{ all -> 0x0139 }
        goto L_0x00c0;	 Catch:{ all -> 0x0139 }
    L_0x00c0:
        r1.route = r4;	 Catch:{ all -> 0x0139 }
        r0 = 0;	 Catch:{ all -> 0x0139 }
        r1.refusedStreamCount = r0;	 Catch:{ all -> 0x0139 }
        r5 = new okhttp3.internal.connection.RealConnection;	 Catch:{ all -> 0x0139 }
        r10 = r1.connectionPool;	 Catch:{ all -> 0x0139 }
        r5.<init>(r10, r4);	 Catch:{ all -> 0x0139 }
        r3 = r5;	 Catch:{ all -> 0x0139 }
        r1.acquire(r3, r0);	 Catch:{ all -> 0x0139 }
        goto L_0x00d2;	 Catch:{ all -> 0x0139 }
    L_0x00d2:
        monitor-exit(r9);	 Catch:{ all -> 0x0139 }
        if (r2 == 0) goto L_0x00dd;
    L_0x00d5:
        r0 = r1.eventListener;
        r5 = r1.call;
        r0.connectionAcquired(r5, r3);
        return r3;
    L_0x00dd:
        r0 = r1.call;
        r5 = r1.eventListener;
        r10 = r3;
        r11 = r19;
        r12 = r20;
        r13 = r21;
        r14 = r22;
        r15 = r23;
        r16 = r0;
        r17 = r5;
        r10.connect(r11, r12, r13, r14, r15, r16, r17);
        r0 = r18.routeDatabase();
        r5 = r3.route();
        r0.connected(r5);
        r5 = 0;
        r10 = r1.connectionPool;
        monitor-enter(r10);
        r0 = 1;
        r1.reportedAcquired = r0;	 Catch:{ all -> 0x012e }
        r0 = okhttp3.internal.Internal.instance;	 Catch:{ all -> 0x012e }
        r9 = r1.connectionPool;	 Catch:{ all -> 0x012e }
        r0.put(r9, r3);	 Catch:{ all -> 0x012e }
        r0 = r3.isMultiplexed();	 Catch:{ all -> 0x012e }
        if (r0 == 0) goto L_0x0121;	 Catch:{ all -> 0x012e }
    L_0x0112:
        r0 = okhttp3.internal.Internal.instance;	 Catch:{ all -> 0x012e }
        r9 = r1.connectionPool;	 Catch:{ all -> 0x012e }
        r11 = r1.address;	 Catch:{ all -> 0x012e }
        r0 = r0.deduplicate(r9, r11, r1);	 Catch:{ all -> 0x012e }
        r5 = r0;	 Catch:{ all -> 0x012e }
        r0 = r1.connection;	 Catch:{ all -> 0x012e }
        r3 = r0;	 Catch:{ all -> 0x012e }
        goto L_0x0122;	 Catch:{ all -> 0x012e }
    L_0x0122:
        monitor-exit(r10);	 Catch:{ all -> 0x012e }
        okhttp3.internal.Util.closeQuietly(r5);
        r0 = r1.eventListener;
        r9 = r1.call;
        r0.connectionAcquired(r9, r3);
        return r3;
    L_0x012e:
        r0 = move-exception;
        monitor-exit(r10);	 Catch:{ all -> 0x012e }
        throw r0;
    L_0x0131:
        r0 = new java.io.IOException;	 Catch:{ all -> 0x0139 }
        r5 = "Canceled";	 Catch:{ all -> 0x0139 }
        r0.<init>(r5);	 Catch:{ all -> 0x0139 }
        throw r0;	 Catch:{ all -> 0x0139 }
    L_0x0139:
        r0 = move-exception;	 Catch:{ all -> 0x0139 }
        monitor-exit(r9);	 Catch:{ all -> 0x0139 }
        throw r0;
    L_0x013c:
        r0 = new java.io.IOException;	 Catch:{ all -> 0x0154 }
        r6 = "Canceled";	 Catch:{ all -> 0x0154 }
        r0.<init>(r6);	 Catch:{ all -> 0x0154 }
        throw r0;	 Catch:{ all -> 0x0154 }
    L_0x0144:
        r0 = new java.lang.IllegalStateException;	 Catch:{ all -> 0x0154 }
        r6 = "codec != null";	 Catch:{ all -> 0x0154 }
        r0.<init>(r6);	 Catch:{ all -> 0x0154 }
        throw r0;	 Catch:{ all -> 0x0154 }
    L_0x014c:
        r0 = new java.lang.IllegalStateException;	 Catch:{ all -> 0x0154 }
        r6 = "released";	 Catch:{ all -> 0x0154 }
        r0.<init>(r6);	 Catch:{ all -> 0x0154 }
        throw r0;	 Catch:{ all -> 0x0154 }
    L_0x0154:
        r0 = move-exception;	 Catch:{ all -> 0x0154 }
        monitor-exit(r5);	 Catch:{ all -> 0x0154 }
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.connection.StreamAllocation.findConnection(int, int, int, int, boolean):okhttp3.internal.connection.RealConnection");
    }

    private okhttp3.internal.connection.RealConnection findHealthyConnection(int r4, int r5, int r6, int r7, boolean r8, boolean r9) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:16:0x001c in {6, 10, 11, 15} preds:[]
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
    L_0x0000:
        r0 = r3.findConnection(r4, r5, r6, r7, r8);
        r1 = r3.connectionPool;
        monitor-enter(r1);
        r2 = r0.successCount;	 Catch:{ all -> 0x0019 }
        if (r2 != 0) goto L_0x000d;	 Catch:{ all -> 0x0019 }
    L_0x000b:
        monitor-exit(r1);	 Catch:{ all -> 0x0019 }
        return r0;	 Catch:{ all -> 0x0019 }
    L_0x000d:
        monitor-exit(r1);	 Catch:{ all -> 0x0019 }
        r1 = r0.isHealthy(r9);
        if (r1 != 0) goto L_0x0018;
    L_0x0014:
        r3.noNewStreams();
        goto L_0x0000;
    L_0x0018:
        return r0;
    L_0x0019:
        r2 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x0019 }
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.connection.StreamAllocation.findHealthyConnection(int, int, int, int, boolean, boolean):okhttp3.internal.connection.RealConnection");
    }

    private void release(okhttp3.internal.connection.RealConnection r5) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:9:0x0027 in {5, 6, 8} preds:[]
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
        r0 = 0;
        r1 = r5.allocations;
        r1 = r1.size();
    L_0x0007:
        if (r0 >= r1) goto L_0x0021;
    L_0x0009:
        r2 = r5.allocations;
        r2 = r2.get(r0);
        r2 = (java.lang.ref.Reference) r2;
        r3 = r2.get();
        if (r3 != r4) goto L_0x001d;
    L_0x0017:
        r3 = r5.allocations;
        r3.remove(r0);
        return;
        r0 = r0 + 1;
        goto L_0x0007;
    L_0x0021:
        r0 = new java.lang.IllegalStateException;
        r0.<init>();
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.connection.StreamAllocation.release(okhttp3.internal.connection.RealConnection):void");
    }

    public void streamFailed(java.io.IOException r8) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:49:0x0079 in {7, 8, 13, 14, 15, 16, 23, 24, 30, 31, 32, 33, 37, 38, 42, 43, 44, 48} preds:[]
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
        r7 = this;
        r0 = 0;
        r1 = r7.connectionPool;
        monitor-enter(r1);
        r2 = r8 instanceof okhttp3.internal.http2.StreamResetException;	 Catch:{ all -> 0x0076 }
        r3 = 0;	 Catch:{ all -> 0x0076 }
        r4 = 1;	 Catch:{ all -> 0x0076 }
        if (r2 == 0) goto L_0x002a;	 Catch:{ all -> 0x0076 }
    L_0x000a:
        r2 = r8;	 Catch:{ all -> 0x0076 }
        r2 = (okhttp3.internal.http2.StreamResetException) r2;	 Catch:{ all -> 0x0076 }
        r5 = r2.errorCode;	 Catch:{ all -> 0x0076 }
        r6 = okhttp3.internal.http2.ErrorCode.REFUSED_STREAM;	 Catch:{ all -> 0x0076 }
        if (r5 != r6) goto L_0x0019;	 Catch:{ all -> 0x0076 }
    L_0x0013:
        r5 = r7.refusedStreamCount;	 Catch:{ all -> 0x0076 }
        r5 = r5 + r4;	 Catch:{ all -> 0x0076 }
        r7.refusedStreamCount = r5;	 Catch:{ all -> 0x0076 }
        goto L_0x001a;	 Catch:{ all -> 0x0076 }
    L_0x001a:
        r5 = r2.errorCode;	 Catch:{ all -> 0x0076 }
        r6 = okhttp3.internal.http2.ErrorCode.REFUSED_STREAM;	 Catch:{ all -> 0x0076 }
        if (r5 != r6) goto L_0x0026;	 Catch:{ all -> 0x0076 }
    L_0x0020:
        r5 = r7.refusedStreamCount;	 Catch:{ all -> 0x0076 }
        if (r5 <= r4) goto L_0x0025;	 Catch:{ all -> 0x0076 }
    L_0x0024:
        goto L_0x0026;	 Catch:{ all -> 0x0076 }
    L_0x0025:
        goto L_0x0029;	 Catch:{ all -> 0x0076 }
    L_0x0026:
        r0 = 1;	 Catch:{ all -> 0x0076 }
        r7.route = r3;	 Catch:{ all -> 0x0076 }
    L_0x0029:
        goto L_0x0056;	 Catch:{ all -> 0x0076 }
    L_0x002a:
        r2 = r7.connection;	 Catch:{ all -> 0x0076 }
        if (r2 == 0) goto L_0x0029;	 Catch:{ all -> 0x0076 }
    L_0x002e:
        r2 = r7.connection;	 Catch:{ all -> 0x0076 }
        r2 = r2.isMultiplexed();	 Catch:{ all -> 0x0076 }
        if (r2 == 0) goto L_0x003c;	 Catch:{ all -> 0x0076 }
    L_0x0036:
        r2 = r8 instanceof okhttp3.internal.http2.ConnectionShutdownException;	 Catch:{ all -> 0x0076 }
        if (r2 == 0) goto L_0x003b;	 Catch:{ all -> 0x0076 }
    L_0x003a:
        goto L_0x003c;	 Catch:{ all -> 0x0076 }
    L_0x003b:
        goto L_0x0056;	 Catch:{ all -> 0x0076 }
    L_0x003c:
        r0 = 1;	 Catch:{ all -> 0x0076 }
        r2 = r7.connection;	 Catch:{ all -> 0x0076 }
        r2 = r2.successCount;	 Catch:{ all -> 0x0076 }
        if (r2 != 0) goto L_0x0055;	 Catch:{ all -> 0x0076 }
    L_0x0043:
        r2 = r7.route;	 Catch:{ all -> 0x0076 }
        if (r2 == 0) goto L_0x0051;	 Catch:{ all -> 0x0076 }
    L_0x0047:
        if (r8 == 0) goto L_0x0051;	 Catch:{ all -> 0x0076 }
    L_0x0049:
        r2 = r7.routeSelector;	 Catch:{ all -> 0x0076 }
        r5 = r7.route;	 Catch:{ all -> 0x0076 }
        r2.connectFailed(r5, r8);	 Catch:{ all -> 0x0076 }
        goto L_0x0052;	 Catch:{ all -> 0x0076 }
    L_0x0052:
        r7.route = r3;	 Catch:{ all -> 0x0076 }
        goto L_0x0056;	 Catch:{ all -> 0x0076 }
    L_0x0056:
        r2 = r7.connection;	 Catch:{ all -> 0x0076 }
        r3 = 0;	 Catch:{ all -> 0x0076 }
        r3 = r7.deallocate(r0, r3, r4);	 Catch:{ all -> 0x0076 }
        r4 = r7.connection;	 Catch:{ all -> 0x0076 }
        if (r4 != 0) goto L_0x0065;	 Catch:{ all -> 0x0076 }
    L_0x0061:
        r4 = r7.reportedAcquired;	 Catch:{ all -> 0x0076 }
        if (r4 != 0) goto L_0x0066;	 Catch:{ all -> 0x0076 }
    L_0x0065:
        r2 = 0;	 Catch:{ all -> 0x0076 }
    L_0x0066:
        monitor-exit(r1);	 Catch:{ all -> 0x0076 }
        okhttp3.internal.Util.closeQuietly(r3);
        if (r2 == 0) goto L_0x0074;
    L_0x006c:
        r1 = r7.eventListener;
        r4 = r7.call;
        r1.connectionReleased(r4, r2);
        goto L_0x0075;
    L_0x0075:
        return;
    L_0x0076:
        r2 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x0076 }
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.connection.StreamAllocation.streamFailed(java.io.IOException):void");
    }

    public StreamAllocation(ConnectionPool connectionPool, Address address, Call call, EventListener eventListener, Object callStackTrace) {
        this.connectionPool = connectionPool;
        this.address = address;
        this.call = call;
        this.eventListener = eventListener;
        this.routeSelector = new RouteSelector(address, routeDatabase(), call, eventListener);
        this.callStackTrace = callStackTrace;
    }

    public HttpCodec newStream(OkHttpClient client, Interceptor$Chain chain, boolean doExtensiveHealthChecks) {
        try {
            HttpCodec resultCodec = findHealthyConnection(chain.connectTimeoutMillis(), chain.readTimeoutMillis(), chain.writeTimeoutMillis(), client.pingIntervalMillis(), client.retryOnConnectionFailure(), doExtensiveHealthChecks).newCodec(client, chain, this);
            synchronized (this.connectionPool) {
                this.codec = resultCodec;
            }
            return resultCodec;
        } catch (IOException e) {
            throw new RouteException(e);
        }
    }

    private Socket releaseIfNoNewStreams() {
        RealConnection allocatedConnection = this.connection;
        if (allocatedConnection == null || !allocatedConnection.noNewStreams) {
            return null;
        }
        return deallocate(false, false, true);
    }

    public void streamFinished(boolean noNewStreams, HttpCodec codec, long bytesRead, IOException e) {
        Socket socket;
        this.eventListener.responseBodyEnd(this.call, bytesRead);
        synchronized (this.connectionPool) {
            if (codec != null) {
                if (codec == this.codec) {
                    if (!noNewStreams) {
                        RealConnection realConnection = this.connection;
                        realConnection.successCount++;
                    }
                    Connection releasedConnection = this.connection;
                    socket = deallocate(noNewStreams, false, true);
                    if (this.connection != null) {
                        releasedConnection = null;
                    }
                    boolean callEnd = this.released;
                }
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("expected ");
            stringBuilder.append(this.codec);
            stringBuilder.append(" but was ");
            stringBuilder.append(codec);
            throw new IllegalStateException(stringBuilder.toString());
        }
        Util.closeQuietly(socket);
        if (releasedConnection != null) {
            this.eventListener.connectionReleased(this.call, releasedConnection);
        }
        if (e != null) {
            this.eventListener.callFailed(this.call, e);
        } else if (callEnd) {
            this.eventListener.callEnd(this.call);
        }
    }

    public HttpCodec codec() {
        HttpCodec httpCodec;
        synchronized (this.connectionPool) {
            httpCodec = this.codec;
        }
        return httpCodec;
    }

    private RouteDatabase routeDatabase() {
        return Internal.instance.routeDatabase(this.connectionPool);
    }

    public Route route() {
        return this.route;
    }

    public synchronized RealConnection connection() {
        return this.connection;
    }

    public void release() {
        Socket socket;
        synchronized (this.connectionPool) {
            Connection releasedConnection = this.connection;
            socket = deallocate(false, true, false);
            if (this.connection != null) {
                releasedConnection = null;
            }
        }
        Util.closeQuietly(socket);
        if (releasedConnection != null) {
            this.eventListener.connectionReleased(this.call, releasedConnection);
        }
    }

    public void noNewStreams() {
        Socket socket;
        synchronized (this.connectionPool) {
            Connection releasedConnection = this.connection;
            socket = deallocate(true, false, false);
            if (this.connection != null) {
                releasedConnection = null;
            }
        }
        Util.closeQuietly(socket);
        if (releasedConnection != null) {
            this.eventListener.connectionReleased(this.call, releasedConnection);
        }
    }

    private Socket deallocate(boolean noNewStreams, boolean released, boolean streamFinished) {
        if (streamFinished) {
            this.codec = null;
        }
        if (released) {
            this.released = true;
        }
        Socket socket = null;
        RealConnection realConnection = this.connection;
        if (realConnection != null) {
            if (noNewStreams) {
                realConnection.noNewStreams = true;
            }
            if (this.codec == null && (this.released || this.connection.noNewStreams)) {
                release(this.connection);
                if (this.connection.allocations.isEmpty()) {
                    this.connection.idleAtNanos = System.nanoTime();
                    if (Internal.instance.connectionBecameIdle(this.connectionPool, this.connection)) {
                        socket = this.connection.socket();
                    }
                }
                this.connection = null;
            }
        }
        return socket;
    }

    public void cancel() {
        synchronized (this.connectionPool) {
            this.canceled = true;
            HttpCodec codecToCancel = this.codec;
            RealConnection connectionToCancel = this.connection;
        }
        if (codecToCancel != null) {
            codecToCancel.cancel();
        } else if (connectionToCancel != null) {
            connectionToCancel.cancel();
        }
    }

    public void acquire(RealConnection connection, boolean reportedAcquired) {
        if (this.connection == null) {
            this.connection = connection;
            this.reportedAcquired = reportedAcquired;
            connection.allocations.add(new StreamAllocationReference(this, this.callStackTrace));
            return;
        }
        throw new IllegalStateException();
    }

    public Socket releaseAndAcquire(RealConnection newConnection) {
        if (this.codec == null && this.connection.allocations.size() == 1) {
            Reference<StreamAllocation> onlyAllocation = (Reference) this.connection.allocations.get(0);
            Socket socket = deallocate(true, false, false);
            this.connection = newConnection;
            newConnection.allocations.add(onlyAllocation);
            return socket;
        }
        throw new IllegalStateException();
    }

    public boolean hasMoreRoutes() {
        if (this.route == null) {
            Selection selection = this.routeSelection;
            if (selection != null) {
                if (selection.hasNext()) {
                }
            }
            if (!this.routeSelector.hasNext()) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        RealConnection connection = connection();
        return connection != null ? connection.toString() : this.address.toString();
    }
}
