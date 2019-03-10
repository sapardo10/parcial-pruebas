package okhttp3.internal.cache;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import okio.BufferedSink;

final class DiskLruCache$Entry {
    final File[] cleanFiles;
    DiskLruCache$Editor currentEditor;
    final File[] dirtyFiles;
    final String key;
    final long[] lengths;
    boolean readable;
    long sequenceNumber;
    final /* synthetic */ DiskLruCache this$0;

    void setLengths(java.lang.String[] r5) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:15:0x0025 in {7, 9, 12, 14} preds:[]
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
        r4 = this;
        r0 = r5.length;
        r1 = r4.this$0;
        r1 = r1.valueCount;
        if (r0 != r1) goto L_0x0020;
    L_0x0007:
        r0 = 0;
    L_0x0008:
        r1 = r5.length;	 Catch:{ NumberFormatException -> 0x001a }
        if (r0 >= r1) goto L_0x0018;	 Catch:{ NumberFormatException -> 0x001a }
    L_0x000b:
        r1 = r4.lengths;	 Catch:{ NumberFormatException -> 0x001a }
        r2 = r5[r0];	 Catch:{ NumberFormatException -> 0x001a }
        r2 = java.lang.Long.parseLong(r2);	 Catch:{ NumberFormatException -> 0x001a }
        r1[r0] = r2;	 Catch:{ NumberFormatException -> 0x001a }
        r0 = r0 + 1;
        goto L_0x0008;
        return;
    L_0x001a:
        r0 = move-exception;
        r1 = r4.invalidLengths(r5);
        throw r1;
    L_0x0020:
        r0 = r4.invalidLengths(r5);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.DiskLruCache$Entry.setLengths(java.lang.String[]):void");
    }

    okhttp3.internal.cache.DiskLruCache.Snapshot snapshot() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:26:0x0063 in {6, 8, 15, 16, 17, 20, 21, 23, 25} preds:[]
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
        r10 = this;
        r0 = r10.this$0;
        r0 = java.lang.Thread.holdsLock(r0);
        if (r0 == 0) goto L_0x005d;
    L_0x0008:
        r0 = r10.this$0;
        r0 = r0.valueCount;
        r0 = new okio.Source[r0];
        r1 = r10.lengths;
        r1 = r1.clone();
        r8 = r1;
        r8 = (long[]) r8;
        r1 = 0;
    L_0x0018:
        r2 = r10.this$0;	 Catch:{ FileNotFoundException -> 0x003e }
        r2 = r2.valueCount;	 Catch:{ FileNotFoundException -> 0x003e }
        if (r1 >= r2) goto L_0x002f;	 Catch:{ FileNotFoundException -> 0x003e }
    L_0x001e:
        r2 = r10.this$0;	 Catch:{ FileNotFoundException -> 0x003e }
        r2 = r2.fileSystem;	 Catch:{ FileNotFoundException -> 0x003e }
        r3 = r10.cleanFiles;	 Catch:{ FileNotFoundException -> 0x003e }
        r3 = r3[r1];	 Catch:{ FileNotFoundException -> 0x003e }
        r2 = r2.source(r3);	 Catch:{ FileNotFoundException -> 0x003e }
        r0[r1] = r2;	 Catch:{ FileNotFoundException -> 0x003e }
        r1 = r1 + 1;	 Catch:{ FileNotFoundException -> 0x003e }
        goto L_0x0018;	 Catch:{ FileNotFoundException -> 0x003e }
    L_0x002f:
        r9 = new okhttp3.internal.cache.DiskLruCache$Snapshot;	 Catch:{ FileNotFoundException -> 0x003e }
        r2 = r10.this$0;	 Catch:{ FileNotFoundException -> 0x003e }
        r3 = r10.key;	 Catch:{ FileNotFoundException -> 0x003e }
        r4 = r10.sequenceNumber;	 Catch:{ FileNotFoundException -> 0x003e }
        r1 = r9;	 Catch:{ FileNotFoundException -> 0x003e }
        r6 = r0;	 Catch:{ FileNotFoundException -> 0x003e }
        r7 = r8;	 Catch:{ FileNotFoundException -> 0x003e }
        r1.<init>(r2, r3, r4, r6, r7);	 Catch:{ FileNotFoundException -> 0x003e }
        return r9;
    L_0x003e:
        r1 = move-exception;
        r2 = 0;
    L_0x0040:
        r3 = r10.this$0;
        r3 = r3.valueCount;
        if (r2 >= r3) goto L_0x0053;
    L_0x0046:
        r3 = r0[r2];
        if (r3 == 0) goto L_0x0052;
    L_0x004a:
        r3 = r0[r2];
        okhttp3.internal.Util.closeQuietly(r3);
        r2 = r2 + 1;
        goto L_0x0040;
    L_0x0052:
        goto L_0x0054;
    L_0x0054:
        r2 = r10.this$0;	 Catch:{ IOException -> 0x005a }
        r2.removeEntry(r10);	 Catch:{ IOException -> 0x005a }
        goto L_0x005b;
    L_0x005a:
        r2 = move-exception;
    L_0x005b:
        r2 = 0;
        return r2;
    L_0x005d:
        r0 = new java.lang.AssertionError;
        r0.<init>();
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.DiskLruCache$Entry.snapshot():okhttp3.internal.cache.DiskLruCache$Snapshot");
    }

    DiskLruCache$Entry(DiskLruCache diskLruCache, String key) {
        this.this$0 = diskLruCache;
        this.key = key;
        this.lengths = new long[diskLruCache.valueCount];
        this.cleanFiles = new File[diskLruCache.valueCount];
        this.dirtyFiles = new File[diskLruCache.valueCount];
        StringBuilder fileBuilder = new StringBuilder(key).append('.');
        int truncateTo = fileBuilder.length();
        for (int i = 0; i < diskLruCache.valueCount; i++) {
            fileBuilder.append(i);
            this.cleanFiles[i] = new File(diskLruCache.directory, fileBuilder.toString());
            fileBuilder.append(".tmp");
            this.dirtyFiles[i] = new File(diskLruCache.directory, fileBuilder.toString());
            fileBuilder.setLength(truncateTo);
        }
    }

    void writeLengths(BufferedSink writer) throws IOException {
        for (long length : this.lengths) {
            writer.writeByte(32).writeDecimalLong(length);
        }
    }

    private IOException invalidLengths(String[] strings) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("unexpected journal line: ");
        stringBuilder.append(Arrays.toString(strings));
        throw new IOException(stringBuilder.toString());
    }
}
