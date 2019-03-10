package com.google.android.exoplayer2.util;

public final class LibraryLoader {
    private boolean isAvailable;
    private boolean loadAttempted;
    private String[] nativeLibraries;

    public synchronized boolean isAvailable() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:24:0x0025 in {6, 13, 15, 16, 20, 23} preds:[]
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
        monitor-enter(r5);
        r0 = r5.loadAttempted;	 Catch:{ all -> 0x0022 }
        if (r0 == 0) goto L_0x0009;	 Catch:{ all -> 0x0022 }
    L_0x0005:
        r0 = r5.isAvailable;	 Catch:{ all -> 0x0022 }
        monitor-exit(r5);
        return r0;
    L_0x0009:
        r0 = 1;
        r5.loadAttempted = r0;	 Catch:{ all -> 0x0022 }
        r1 = r5.nativeLibraries;	 Catch:{ UnsatisfiedLinkError -> 0x001d }
        r2 = r1.length;	 Catch:{ UnsatisfiedLinkError -> 0x001d }
        r3 = 0;	 Catch:{ UnsatisfiedLinkError -> 0x001d }
    L_0x0010:
        if (r3 >= r2) goto L_0x001a;	 Catch:{ UnsatisfiedLinkError -> 0x001d }
    L_0x0012:
        r4 = r1[r3];	 Catch:{ UnsatisfiedLinkError -> 0x001d }
        java.lang.System.loadLibrary(r4);	 Catch:{ UnsatisfiedLinkError -> 0x001d }
        r3 = r3 + 1;	 Catch:{ UnsatisfiedLinkError -> 0x001d }
        goto L_0x0010;	 Catch:{ UnsatisfiedLinkError -> 0x001d }
    L_0x001a:
        r5.isAvailable = r0;	 Catch:{ UnsatisfiedLinkError -> 0x001d }
        goto L_0x001e;
    L_0x001d:
        r0 = move-exception;
    L_0x001e:
        r0 = r5.isAvailable;	 Catch:{ all -> 0x0022 }
        monitor-exit(r5);
        return r0;
    L_0x0022:
        r0 = move-exception;
        monitor-exit(r5);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.util.LibraryLoader.isAvailable():boolean");
    }

    public LibraryLoader(String... libraries) {
        this.nativeLibraries = libraries;
    }

    public synchronized void setLibraries(String... libraries) {
        Assertions.checkState(!this.loadAttempted, "Cannot set libraries after loading");
        this.nativeLibraries = libraries;
    }
}
