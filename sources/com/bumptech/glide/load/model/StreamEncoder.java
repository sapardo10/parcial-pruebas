package com.bumptech.glide.load.model;

import com.bumptech.glide.load.Encoder;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import java.io.InputStream;

public class StreamEncoder implements Encoder<InputStream> {
    private static final String TAG = "StreamEncoder";
    private final ArrayPool byteArrayPool;

    public boolean encode(@android.support.annotation.NonNull java.io.InputStream r7, @android.support.annotation.NonNull java.io.File r8, @android.support.annotation.NonNull com.bumptech.glide.load.Options r9) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:40:0x0060 in {5, 10, 12, 20, 21, 24, 25, 26, 27, 28, 30, 34, 36, 37, 39} preds:[]
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
        r6 = this;
        r0 = r6.byteArrayPool;
        r1 = byte[].class;
        r2 = 65536; // 0x10000 float:9.18355E-41 double:3.2379E-319;
        r0 = r0.get(r2, r1);
        r0 = (byte[]) r0;
        r1 = 0;
        r2 = 0;
        r3 = new java.io.FileOutputStream;	 Catch:{ IOException -> 0x002e }
        r3.<init>(r8);	 Catch:{ IOException -> 0x002e }
        r2 = r3;	 Catch:{ IOException -> 0x002e }
    L_0x0014:
        r3 = r7.read(r0);	 Catch:{ IOException -> 0x002e }
        r4 = r3;	 Catch:{ IOException -> 0x002e }
        r5 = -1;	 Catch:{ IOException -> 0x002e }
        if (r3 == r5) goto L_0x0021;	 Catch:{ IOException -> 0x002e }
    L_0x001c:
        r3 = 0;	 Catch:{ IOException -> 0x002e }
        r2.write(r0, r3, r4);	 Catch:{ IOException -> 0x002e }
        goto L_0x0014;	 Catch:{ IOException -> 0x002e }
    L_0x0021:
        r2.close();	 Catch:{ IOException -> 0x002e }
        r1 = 1;
        r2.close();	 Catch:{ IOException -> 0x002a }
        goto L_0x0046;
    L_0x002a:
        r3 = move-exception;
        goto L_0x0048;
    L_0x002c:
        r3 = move-exception;
        goto L_0x0051;
    L_0x002e:
        r3 = move-exception;
        r4 = "StreamEncoder";	 Catch:{ all -> 0x002c }
        r5 = 3;	 Catch:{ all -> 0x002c }
        r4 = android.util.Log.isLoggable(r4, r5);	 Catch:{ all -> 0x002c }
        if (r4 == 0) goto L_0x0040;	 Catch:{ all -> 0x002c }
    L_0x0038:
        r4 = "StreamEncoder";	 Catch:{ all -> 0x002c }
        r5 = "Failed to encode data onto the OutputStream";	 Catch:{ all -> 0x002c }
        android.util.Log.d(r4, r5, r3);	 Catch:{ all -> 0x002c }
        goto L_0x0041;
    L_0x0041:
        if (r2 == 0) goto L_0x0049;
    L_0x0043:
        r2.close();	 Catch:{ IOException -> 0x0047 }
    L_0x0046:
        goto L_0x004a;
    L_0x0047:
        r3 = move-exception;
    L_0x0048:
        goto L_0x004a;
    L_0x004a:
        r3 = r6.byteArrayPool;
        r3.put(r0);
        return r1;
    L_0x0051:
        if (r2 == 0) goto L_0x0059;
    L_0x0053:
        r2.close();	 Catch:{ IOException -> 0x0057 }
        goto L_0x005a;
    L_0x0057:
        r4 = move-exception;
        goto L_0x005a;
    L_0x005a:
        r4 = r6.byteArrayPool;
        r4.put(r0);
        throw r3;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.load.model.StreamEncoder.encode(java.io.InputStream, java.io.File, com.bumptech.glide.load.Options):boolean");
    }

    public StreamEncoder(ArrayPool byteArrayPool) {
        this.byteArrayPool = byteArrayPool;
    }
}
