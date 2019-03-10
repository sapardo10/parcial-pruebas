package okio;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

public class AsyncTimeout extends Timeout {
    private static final long IDLE_TIMEOUT_MILLIS = TimeUnit.SECONDS.toMillis(60);
    private static final long IDLE_TIMEOUT_NANOS = TimeUnit.MILLISECONDS.toNanos(IDLE_TIMEOUT_MILLIS);
    private static final int TIMEOUT_WRITE_SIZE = 65536;
    @Nullable
    static AsyncTimeout head;
    private boolean inQueue;
    @Nullable
    private AsyncTimeout next;
    private long timeoutAt;

    private static synchronized boolean cancelScheduledTimeout(okio.AsyncTimeout r3) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:20:0x001f in {10, 13, 16, 19} preds:[]
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
        r0 = okio.AsyncTimeout.class;
        monitor-enter(r0);
        r1 = head;	 Catch:{ all -> 0x001c }
    L_0x0005:
        if (r1 == 0) goto L_0x0019;	 Catch:{ all -> 0x001c }
    L_0x0007:
        r2 = r1.next;	 Catch:{ all -> 0x001c }
        if (r2 != r3) goto L_0x0015;	 Catch:{ all -> 0x001c }
    L_0x000b:
        r2 = r3.next;	 Catch:{ all -> 0x001c }
        r1.next = r2;	 Catch:{ all -> 0x001c }
        r2 = 0;	 Catch:{ all -> 0x001c }
        r3.next = r2;	 Catch:{ all -> 0x001c }
        r2 = 0;
        monitor-exit(r0);
        return r2;
    L_0x0015:
        r2 = r1.next;	 Catch:{ all -> 0x001c }
        r1 = r2;
        goto L_0x0005;
    L_0x0019:
        r1 = 1;
        monitor-exit(r0);
        return r1;
    L_0x001c:
        r3 = move-exception;
        monitor-exit(r0);
        throw r3;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.AsyncTimeout.cancelScheduledTimeout(okio.AsyncTimeout):boolean");
    }

    private static synchronized void scheduleTimeout(okio.AsyncTimeout r9, long r10, boolean r12) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:36:0x0079 in {5, 6, 10, 13, 15, 21, 22, 26, 27, 29, 32, 35} preds:[]
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
        r0 = okio.AsyncTimeout.class;
        monitor-enter(r0);
        r1 = head;	 Catch:{ all -> 0x0076 }
        if (r1 != 0) goto L_0x0017;	 Catch:{ all -> 0x0076 }
    L_0x0007:
        r1 = new okio.AsyncTimeout;	 Catch:{ all -> 0x0076 }
        r1.<init>();	 Catch:{ all -> 0x0076 }
        head = r1;	 Catch:{ all -> 0x0076 }
        r1 = new okio.AsyncTimeout$Watchdog;	 Catch:{ all -> 0x0076 }
        r1.<init>();	 Catch:{ all -> 0x0076 }
        r1.start();	 Catch:{ all -> 0x0076 }
        goto L_0x0018;	 Catch:{ all -> 0x0076 }
    L_0x0018:
        r1 = java.lang.System.nanoTime();	 Catch:{ all -> 0x0076 }
        r3 = 0;	 Catch:{ all -> 0x0076 }
        r5 = (r10 > r3 ? 1 : (r10 == r3 ? 0 : -1));	 Catch:{ all -> 0x0076 }
        if (r5 == 0) goto L_0x0031;	 Catch:{ all -> 0x0076 }
    L_0x0022:
        if (r12 == 0) goto L_0x0031;	 Catch:{ all -> 0x0076 }
    L_0x0024:
        r3 = r9.deadlineNanoTime();	 Catch:{ all -> 0x0076 }
        r3 = r3 - r1;	 Catch:{ all -> 0x0076 }
        r3 = java.lang.Math.min(r10, r3);	 Catch:{ all -> 0x0076 }
        r3 = r3 + r1;	 Catch:{ all -> 0x0076 }
        r9.timeoutAt = r3;	 Catch:{ all -> 0x0076 }
        goto L_0x0043;	 Catch:{ all -> 0x0076 }
        r5 = (r10 > r3 ? 1 : (r10 == r3 ? 0 : -1));	 Catch:{ all -> 0x0076 }
        if (r5 == 0) goto L_0x003b;	 Catch:{ all -> 0x0076 }
    L_0x0036:
        r3 = r1 + r10;	 Catch:{ all -> 0x0076 }
        r9.timeoutAt = r3;	 Catch:{ all -> 0x0076 }
        goto L_0x0043;	 Catch:{ all -> 0x0076 }
    L_0x003b:
        if (r12 == 0) goto L_0x0070;	 Catch:{ all -> 0x0076 }
    L_0x003d:
        r3 = r9.deadlineNanoTime();	 Catch:{ all -> 0x0076 }
        r9.timeoutAt = r3;	 Catch:{ all -> 0x0076 }
    L_0x0043:
        r3 = r9.remainingNanos(r1);	 Catch:{ all -> 0x0076 }
        r5 = head;	 Catch:{ all -> 0x0076 }
    L_0x0049:
        r6 = r5.next;	 Catch:{ all -> 0x0076 }
        if (r6 == 0) goto L_0x005c;	 Catch:{ all -> 0x0076 }
    L_0x004d:
        r6 = r5.next;	 Catch:{ all -> 0x0076 }
        r6 = r6.remainingNanos(r1);	 Catch:{ all -> 0x0076 }
        r8 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1));	 Catch:{ all -> 0x0076 }
        if (r8 >= 0) goto L_0x0058;	 Catch:{ all -> 0x0076 }
    L_0x0057:
        goto L_0x005c;	 Catch:{ all -> 0x0076 }
    L_0x0058:
        r6 = r5.next;	 Catch:{ all -> 0x0076 }
        r5 = r6;	 Catch:{ all -> 0x0076 }
        goto L_0x0049;	 Catch:{ all -> 0x0076 }
        r6 = r5.next;	 Catch:{ all -> 0x0076 }
        r9.next = r6;	 Catch:{ all -> 0x0076 }
        r5.next = r9;	 Catch:{ all -> 0x0076 }
        r6 = head;	 Catch:{ all -> 0x0076 }
        if (r5 != r6) goto L_0x006d;	 Catch:{ all -> 0x0076 }
    L_0x0067:
        r6 = okio.AsyncTimeout.class;	 Catch:{ all -> 0x0076 }
        r6.notify();	 Catch:{ all -> 0x0076 }
        goto L_0x006e;
    L_0x006e:
        monitor-exit(r0);
        return;
    L_0x0070:
        r3 = new java.lang.AssertionError;	 Catch:{ all -> 0x0076 }
        r3.<init>();	 Catch:{ all -> 0x0076 }
        throw r3;	 Catch:{ all -> 0x0076 }
    L_0x0076:
        r9 = move-exception;
        monitor-exit(r0);
        throw r9;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.AsyncTimeout.scheduleTimeout(okio.AsyncTimeout, long, boolean):void");
    }

    public final void enter() {
        if (this.inQueue) {
            throw new IllegalStateException("Unbalanced enter/exit");
        }
        long timeoutNanos = timeoutNanos();
        boolean hasDeadline = hasDeadline();
        if (timeoutNanos != 0 || hasDeadline) {
            this.inQueue = true;
            scheduleTimeout(this, timeoutNanos, hasDeadline);
        }
    }

    public final boolean exit() {
        if (!this.inQueue) {
            return false;
        }
        this.inQueue = false;
        return cancelScheduledTimeout(this);
    }

    private long remainingNanos(long now) {
        return this.timeoutAt - now;
    }

    protected void timedOut() {
    }

    public final Sink sink(Sink sink) {
        return new AsyncTimeout$1(this, sink);
    }

    public final Source source(Source source) {
        return new AsyncTimeout$2(this, source);
    }

    final void exit(boolean throwOnTimeout) throws IOException {
        if (!exit()) {
            return;
        }
        if (throwOnTimeout) {
            throw newTimeoutException(null);
        }
    }

    final IOException exit(IOException cause) throws IOException {
        if (exit()) {
            return newTimeoutException(cause);
        }
        return cause;
    }

    protected IOException newTimeoutException(@Nullable IOException cause) {
        InterruptedIOException e = new InterruptedIOException("timeout");
        if (cause != null) {
            e.initCause(cause);
        }
        return e;
    }

    @Nullable
    static AsyncTimeout awaitTimeout() throws InterruptedException {
        AsyncTimeout node = head.next;
        AsyncTimeout asyncTimeout = null;
        if (node == null) {
            long startNanos = System.nanoTime();
            AsyncTimeout.class.wait(IDLE_TIMEOUT_MILLIS);
            if (head.next == null && System.nanoTime() - startNanos >= IDLE_TIMEOUT_NANOS) {
                asyncTimeout = head;
            }
            return asyncTimeout;
        }
        startNanos = node.remainingNanos(System.nanoTime());
        if (startNanos > 0) {
            long waitMillis = startNanos / 1000000;
            AsyncTimeout.class.wait(waitMillis, (int) (startNanos - (1000000 * waitMillis)));
            return null;
        }
        head.next = node.next;
        node.next = null;
        return node;
    }
}
