package com.bumptech.glide.disklrucache;

import java.io.Closeable;
import java.nio.charset.Charset;

final class Util {
    static final Charset US_ASCII = Charset.forName("US-ASCII");
    static final Charset UTF_8 = Charset.forName("UTF-8");

    static void deleteContents(java.io.File r5) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:16:0x004f in {6, 7, 10, 12, 13, 15} preds:[]
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
        r0 = r5.listFiles();
        if (r0 == 0) goto L_0x0038;
    L_0x0006:
        r1 = r0.length;
        r2 = 0;
    L_0x0008:
        if (r2 >= r1) goto L_0x0037;
    L_0x000a:
        r3 = r0[r2];
        r4 = r3.isDirectory();
        if (r4 == 0) goto L_0x0016;
    L_0x0012:
        deleteContents(r3);
        goto L_0x0017;
    L_0x0017:
        r4 = r3.delete();
        if (r4 == 0) goto L_0x0020;
    L_0x001d:
        r2 = r2 + 1;
        goto L_0x0008;
    L_0x0020:
        r1 = new java.io.IOException;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r4 = "failed to delete file: ";
        r2.append(r4);
        r2.append(r3);
        r2 = r2.toString();
        r1.<init>(r2);
        throw r1;
    L_0x0037:
        return;
    L_0x0038:
        r1 = new java.io.IOException;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "not a readable directory: ";
        r2.append(r3);
        r2.append(r5);
        r2 = r2.toString();
        r1.<init>(r2);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.disklrucache.Util.deleteContents(java.io.File):void");
    }

    static java.lang.String readFully(java.io.Reader r5) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:11:0x0023 in {4, 7, 10} preds:[]
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
        r0 = new java.io.StringWriter;	 Catch:{ all -> 0x001e }
        r0.<init>();	 Catch:{ all -> 0x001e }
        r1 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;	 Catch:{ all -> 0x001e }
        r1 = new char[r1];	 Catch:{ all -> 0x001e }
    L_0x0009:
        r2 = r5.read(r1);	 Catch:{ all -> 0x001e }
        r3 = r2;	 Catch:{ all -> 0x001e }
        r4 = -1;	 Catch:{ all -> 0x001e }
        if (r2 == r4) goto L_0x0016;	 Catch:{ all -> 0x001e }
    L_0x0011:
        r2 = 0;	 Catch:{ all -> 0x001e }
        r0.write(r1, r2, r3);	 Catch:{ all -> 0x001e }
        goto L_0x0009;	 Catch:{ all -> 0x001e }
    L_0x0016:
        r2 = r0.toString();	 Catch:{ all -> 0x001e }
        r5.close();
        return r2;
    L_0x001e:
        r0 = move-exception;
        r5.close();
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.disklrucache.Util.readFully(java.io.Reader):java.lang.String");
    }

    private Util() {
    }

    static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception e) {
            }
        }
    }
}
