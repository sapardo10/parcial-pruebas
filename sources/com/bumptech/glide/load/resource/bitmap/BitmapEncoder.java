package com.bumptech.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.bumptech.glide.load.EncodeStrategy;
import com.bumptech.glide.load.Option;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceEncoder;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;

public class BitmapEncoder implements ResourceEncoder<Bitmap> {
    public static final Option<CompressFormat> COMPRESSION_FORMAT = Option.memory("com.bumptech.glide.load.resource.bitmap.BitmapEncoder.CompressionFormat");
    public static final Option<Integer> COMPRESSION_QUALITY = Option.memory("com.bumptech.glide.load.resource.bitmap.BitmapEncoder.CompressionQuality", Integer.valueOf(90));
    private static final String TAG = "BitmapEncoder";
    @Nullable
    private final ArrayPool arrayPool;

    public boolean encode(@android.support.annotation.NonNull com.bumptech.glide.load.engine.Resource<android.graphics.Bitmap> r12, @android.support.annotation.NonNull java.io.File r13, @android.support.annotation.NonNull com.bumptech.glide.load.Options r14) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:51:0x00d7 in {7, 8, 12, 13, 15, 23, 24, 28, 29, 34, 35, 37, 41, 43, 44, 47, 50} preds:[]
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
        r11 = this;
        r0 = r12.get();
        r0 = (android.graphics.Bitmap) r0;
        r1 = r11.getFormat(r0, r14);
        r2 = "encode: [%dx%d] %s";
        r3 = r0.getWidth();
        r3 = java.lang.Integer.valueOf(r3);
        r4 = r0.getHeight();
        r4 = java.lang.Integer.valueOf(r4);
        com.bumptech.glide.util.pool.GlideTrace.beginSectionFormat(r2, r3, r4, r1);
        r2 = com.bumptech.glide.util.LogTime.getLogTime();	 Catch:{ all -> 0x00d2 }
        r4 = COMPRESSION_QUALITY;	 Catch:{ all -> 0x00d2 }
        r4 = r14.get(r4);	 Catch:{ all -> 0x00d2 }
        r4 = (java.lang.Integer) r4;	 Catch:{ all -> 0x00d2 }
        r4 = r4.intValue();	 Catch:{ all -> 0x00d2 }
        r5 = 0;
        r6 = 0;
        r7 = new java.io.FileOutputStream;	 Catch:{ IOException -> 0x0055 }
        r7.<init>(r13);	 Catch:{ IOException -> 0x0055 }
        r6 = r7;	 Catch:{ IOException -> 0x0055 }
        r7 = r11.arrayPool;	 Catch:{ IOException -> 0x0055 }
        if (r7 == 0) goto L_0x0044;	 Catch:{ IOException -> 0x0055 }
    L_0x003b:
        r7 = new com.bumptech.glide.load.data.BufferedOutputStream;	 Catch:{ IOException -> 0x0055 }
        r8 = r11.arrayPool;	 Catch:{ IOException -> 0x0055 }
        r7.<init>(r6, r8);	 Catch:{ IOException -> 0x0055 }
        r6 = r7;	 Catch:{ IOException -> 0x0055 }
        goto L_0x0045;	 Catch:{ IOException -> 0x0055 }
    L_0x0045:
        r0.compress(r1, r4, r6);	 Catch:{ IOException -> 0x0055 }
        r6.close();	 Catch:{ IOException -> 0x0055 }
        r5 = 1;
        r6.close();	 Catch:{ IOException -> 0x0051 }
    L_0x0050:
        goto L_0x006f;
    L_0x0051:
        r7 = move-exception;
        goto L_0x0050;
    L_0x0053:
        r7 = move-exception;
        goto L_0x00c7;
    L_0x0055:
        r7 = move-exception;
        r8 = "BitmapEncoder";	 Catch:{ all -> 0x0053 }
        r9 = 3;	 Catch:{ all -> 0x0053 }
        r8 = android.util.Log.isLoggable(r8, r9);	 Catch:{ all -> 0x0053 }
        if (r8 == 0) goto L_0x0067;	 Catch:{ all -> 0x0053 }
    L_0x005f:
        r8 = "BitmapEncoder";	 Catch:{ all -> 0x0053 }
        r9 = "Failed to encode Bitmap";	 Catch:{ all -> 0x0053 }
        android.util.Log.d(r8, r9, r7);	 Catch:{ all -> 0x0053 }
        goto L_0x0068;
    L_0x0068:
        if (r6 == 0) goto L_0x006e;
    L_0x006a:
        r6.close();	 Catch:{ IOException -> 0x0051 }
        goto L_0x0050;
    L_0x006f:
        r7 = "BitmapEncoder";	 Catch:{ all -> 0x00d2 }
        r8 = 2;	 Catch:{ all -> 0x00d2 }
        r7 = android.util.Log.isLoggable(r7, r8);	 Catch:{ all -> 0x00d2 }
        if (r7 == 0) goto L_0x00c1;	 Catch:{ all -> 0x00d2 }
    L_0x0078:
        r7 = "BitmapEncoder";	 Catch:{ all -> 0x00d2 }
        r8 = new java.lang.StringBuilder;	 Catch:{ all -> 0x00d2 }
        r8.<init>();	 Catch:{ all -> 0x00d2 }
        r9 = "Compressed with type: ";	 Catch:{ all -> 0x00d2 }
        r8.append(r9);	 Catch:{ all -> 0x00d2 }
        r8.append(r1);	 Catch:{ all -> 0x00d2 }
        r9 = " of size ";	 Catch:{ all -> 0x00d2 }
        r8.append(r9);	 Catch:{ all -> 0x00d2 }
        r9 = com.bumptech.glide.util.Util.getBitmapByteSize(r0);	 Catch:{ all -> 0x00d2 }
        r8.append(r9);	 Catch:{ all -> 0x00d2 }
        r9 = " in ";	 Catch:{ all -> 0x00d2 }
        r8.append(r9);	 Catch:{ all -> 0x00d2 }
        r9 = com.bumptech.glide.util.LogTime.getElapsedMillis(r2);	 Catch:{ all -> 0x00d2 }
        r8.append(r9);	 Catch:{ all -> 0x00d2 }
        r9 = ", options format: ";	 Catch:{ all -> 0x00d2 }
        r8.append(r9);	 Catch:{ all -> 0x00d2 }
        r9 = COMPRESSION_FORMAT;	 Catch:{ all -> 0x00d2 }
        r9 = r14.get(r9);	 Catch:{ all -> 0x00d2 }
        r8.append(r9);	 Catch:{ all -> 0x00d2 }
        r9 = ", hasAlpha: ";	 Catch:{ all -> 0x00d2 }
        r8.append(r9);	 Catch:{ all -> 0x00d2 }
        r9 = r0.hasAlpha();	 Catch:{ all -> 0x00d2 }
        r8.append(r9);	 Catch:{ all -> 0x00d2 }
        r8 = r8.toString();	 Catch:{ all -> 0x00d2 }
        android.util.Log.v(r7, r8);	 Catch:{ all -> 0x00d2 }
        goto L_0x00c2;
        com.bumptech.glide.util.pool.GlideTrace.endSection();
        return r5;
    L_0x00c7:
        if (r6 == 0) goto L_0x00cf;
    L_0x00c9:
        r6.close();	 Catch:{ IOException -> 0x00cd }
        goto L_0x00d0;
    L_0x00cd:
        r8 = move-exception;
        goto L_0x00d0;
        throw r7;	 Catch:{ all -> 0x00d2 }
    L_0x00d2:
        r2 = move-exception;
        com.bumptech.glide.util.pool.GlideTrace.endSection();
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.load.resource.bitmap.BitmapEncoder.encode(com.bumptech.glide.load.engine.Resource, java.io.File, com.bumptech.glide.load.Options):boolean");
    }

    public BitmapEncoder(@NonNull ArrayPool arrayPool) {
        this.arrayPool = arrayPool;
    }

    @Deprecated
    public BitmapEncoder() {
        this.arrayPool = null;
    }

    private CompressFormat getFormat(Bitmap bitmap, Options options) {
        CompressFormat format = (CompressFormat) options.get(COMPRESSION_FORMAT);
        if (format != null) {
            return format;
        }
        if (bitmap.hasAlpha()) {
            return CompressFormat.PNG;
        }
        return CompressFormat.JPEG;
    }

    @NonNull
    public EncodeStrategy getEncodeStrategy(@NonNull Options options) {
        return EncodeStrategy.TRANSFORMED;
    }
}
