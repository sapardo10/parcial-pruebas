package okio;

final class AsyncTimeout$Watchdog extends Thread {
    AsyncTimeout$Watchdog() {
        super("Okio Watchdog");
        setDaemon(true);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void run() {
        /*
        r3 = this;
    L_0x0000:
        r0 = okio.AsyncTimeout.class;
        monitor-enter(r0);	 Catch:{ InterruptedException -> 0x001c }
        r1 = okio.AsyncTimeout.awaitTimeout();	 Catch:{ all -> 0x0019 }
        if (r1 != 0) goto L_0x000b;
    L_0x0009:
        monitor-exit(r0);	 Catch:{ all -> 0x0019 }
        goto L_0x0000;
    L_0x000b:
        r2 = okio.AsyncTimeout.head;	 Catch:{ all -> 0x0019 }
        if (r1 != r2) goto L_0x0014;
    L_0x000f:
        r2 = 0;
        okio.AsyncTimeout.head = r2;	 Catch:{ all -> 0x0019 }
        monitor-exit(r0);	 Catch:{ all -> 0x0019 }
        return;
    L_0x0014:
        monitor-exit(r0);	 Catch:{ all -> 0x0019 }
        r1.timedOut();	 Catch:{ InterruptedException -> 0x001c }
        goto L_0x001d;
    L_0x0019:
        r1 = move-exception;
        monitor-exit(r0);	 Catch:{ all -> 0x0019 }
        throw r1;	 Catch:{ InterruptedException -> 0x001c }
    L_0x001c:
        r0 = move-exception;
    L_0x001d:
        goto L_0x0000;
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.AsyncTimeout$Watchdog.run():void");
    }
}
