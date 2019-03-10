package okhttp3.internal.cache2;

import java.nio.channels.FileChannel;

final class FileOperator {
    private final FileChannel fileChannel;

    public void read(long r10, okio.Buffer r12, long r13) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:8:0x001d in {4, 5, 7} preds:[]
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
        r9 = this;
        r0 = 0;
        r2 = (r13 > r0 ? 1 : (r13 == r0 ? 0 : -1));
        if (r2 < 0) goto L_0x0017;
    L_0x0006:
        r2 = (r13 > r0 ? 1 : (r13 == r0 ? 0 : -1));
        if (r2 <= 0) goto L_0x0016;
    L_0x000a:
        r3 = r9.fileChannel;
        r4 = r10;
        r6 = r13;
        r8 = r12;
        r2 = r3.transferTo(r4, r6, r8);
        r10 = r10 + r2;
        r13 = r13 - r2;
        goto L_0x0006;
    L_0x0016:
        return;
    L_0x0017:
        r0 = new java.lang.IndexOutOfBoundsException;
        r0.<init>();
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache2.FileOperator.read(long, okio.Buffer, long):void");
    }

    public void write(long r14, okio.Buffer r16, long r17) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:12:0x002c in {7, 9, 11} preds:[]
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
        r13 = this;
        r0 = 0;
        r2 = (r17 > r0 ? 1 : (r17 == r0 ? 0 : -1));
        if (r2 < 0) goto L_0x0025;
    L_0x0006:
        r2 = r16.size();
        r4 = (r17 > r2 ? 1 : (r17 == r2 ? 0 : -1));
        if (r4 > 0) goto L_0x0025;
    L_0x000e:
        r11 = r14;
        r2 = r17;
    L_0x0011:
        r4 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1));
        if (r4 <= 0) goto L_0x0023;
    L_0x0015:
        r4 = r13;
        r5 = r4.fileChannel;
        r6 = r16;
        r7 = r11;
        r9 = r2;
        r5 = r5.transferFrom(r6, r7, r9);
        r11 = r11 + r5;
        r2 = r2 - r5;
        goto L_0x0011;
    L_0x0023:
        r4 = r13;
        return;
    L_0x0025:
        r4 = r13;
        r0 = new java.lang.IndexOutOfBoundsException;
        r0.<init>();
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache2.FileOperator.write(long, okio.Buffer, long):void");
    }

    FileOperator(FileChannel fileChannel) {
        this.fileChannel = fileChannel;
    }
}
