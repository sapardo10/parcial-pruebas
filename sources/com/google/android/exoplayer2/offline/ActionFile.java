package com.google.android.exoplayer2.offline;

import com.google.android.exoplayer2.util.AtomicFile;
import java.io.File;

public final class ActionFile {
    static final int VERSION = 0;
    private final File actionFile;
    private final AtomicFile atomicFile;

    public com.google.android.exoplayer2.offline.DownloadAction[] load(com.google.android.exoplayer2.offline.DownloadAction.Deserializer... r8) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:20:0x0052 in {3, 11, 13, 16, 19} preds:[]
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
        r0 = r7.actionFile;
        r0 = r0.exists();
        if (r0 != 0) goto L_0x000c;
    L_0x0008:
        r0 = 0;
        r0 = new com.google.android.exoplayer2.offline.DownloadAction[r0];
        return r0;
    L_0x000c:
        r0 = 0;
        r1 = r7.atomicFile;	 Catch:{ all -> 0x004d }
        r1 = r1.openRead();	 Catch:{ all -> 0x004d }
        r0 = r1;	 Catch:{ all -> 0x004d }
        r1 = new java.io.DataInputStream;	 Catch:{ all -> 0x004d }
        r1.<init>(r0);	 Catch:{ all -> 0x004d }
        r2 = r1.readInt();	 Catch:{ all -> 0x004d }
        if (r2 > 0) goto L_0x0036;	 Catch:{ all -> 0x004d }
    L_0x001f:
        r3 = r1.readInt();	 Catch:{ all -> 0x004d }
        r4 = new com.google.android.exoplayer2.offline.DownloadAction[r3];	 Catch:{ all -> 0x004d }
        r5 = 0;	 Catch:{ all -> 0x004d }
    L_0x0026:
        if (r5 >= r3) goto L_0x0031;	 Catch:{ all -> 0x004d }
    L_0x0028:
        r6 = com.google.android.exoplayer2.offline.DownloadAction.deserializeFromStream(r8, r1);	 Catch:{ all -> 0x004d }
        r4[r5] = r6;	 Catch:{ all -> 0x004d }
        r5 = r5 + 1;
        goto L_0x0026;
        com.google.android.exoplayer2.util.Util.closeQuietly(r0);
        return r4;
    L_0x0036:
        r3 = new java.io.IOException;	 Catch:{ all -> 0x004d }
        r4 = new java.lang.StringBuilder;	 Catch:{ all -> 0x004d }
        r4.<init>();	 Catch:{ all -> 0x004d }
        r5 = "Unsupported action file version: ";	 Catch:{ all -> 0x004d }
        r4.append(r5);	 Catch:{ all -> 0x004d }
        r4.append(r2);	 Catch:{ all -> 0x004d }
        r4 = r4.toString();	 Catch:{ all -> 0x004d }
        r3.<init>(r4);	 Catch:{ all -> 0x004d }
        throw r3;	 Catch:{ all -> 0x004d }
    L_0x004d:
        r1 = move-exception;
        com.google.android.exoplayer2.util.Util.closeQuietly(r0);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.offline.ActionFile.load(com.google.android.exoplayer2.offline.DownloadAction$Deserializer[]):com.google.android.exoplayer2.offline.DownloadAction[]");
    }

    public void store(com.google.android.exoplayer2.offline.DownloadAction... r5) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:11:0x0030 in {4, 7, 10} preds:[]
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
        r1 = new java.io.DataOutputStream;	 Catch:{ all -> 0x002b }
        r2 = r4.atomicFile;	 Catch:{ all -> 0x002b }
        r2 = r2.startWrite();	 Catch:{ all -> 0x002b }
        r1.<init>(r2);	 Catch:{ all -> 0x002b }
        r0 = r1;	 Catch:{ all -> 0x002b }
        r1 = 0;	 Catch:{ all -> 0x002b }
        r0.writeInt(r1);	 Catch:{ all -> 0x002b }
        r2 = r5.length;	 Catch:{ all -> 0x002b }
        r0.writeInt(r2);	 Catch:{ all -> 0x002b }
        r2 = r5.length;	 Catch:{ all -> 0x002b }
    L_0x0016:
        if (r1 >= r2) goto L_0x0020;	 Catch:{ all -> 0x002b }
    L_0x0018:
        r3 = r5[r1];	 Catch:{ all -> 0x002b }
        com.google.android.exoplayer2.offline.DownloadAction.serializeToStream(r3, r0);	 Catch:{ all -> 0x002b }
        r1 = r1 + 1;	 Catch:{ all -> 0x002b }
        goto L_0x0016;	 Catch:{ all -> 0x002b }
    L_0x0020:
        r1 = r4.atomicFile;	 Catch:{ all -> 0x002b }
        r1.endWrite(r0);	 Catch:{ all -> 0x002b }
        r0 = 0;
        com.google.android.exoplayer2.util.Util.closeQuietly(r0);
        return;
    L_0x002b:
        r1 = move-exception;
        com.google.android.exoplayer2.util.Util.closeQuietly(r0);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.offline.ActionFile.store(com.google.android.exoplayer2.offline.DownloadAction[]):void");
    }

    public ActionFile(File actionFile) {
        this.actionFile = actionFile;
        this.atomicFile = new AtomicFile(actionFile);
    }
}
