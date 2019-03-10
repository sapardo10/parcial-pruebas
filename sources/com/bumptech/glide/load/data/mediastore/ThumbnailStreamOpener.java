package com.bumptech.glide.load.data.mediastore;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.bumptech.glide.load.ImageHeaderParser;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

class ThumbnailStreamOpener {
    private static final FileService DEFAULT_SERVICE = new FileService();
    private static final String TAG = "ThumbStreamOpener";
    private final ArrayPool byteArrayPool;
    private final ContentResolver contentResolver;
    private final List<ImageHeaderParser> parsers;
    private final ThumbnailQuery query;
    private final FileService service;

    int getOrientation(android.net.Uri r6) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:37:0x0053 in {6, 8, 9, 10, 18, 19, 22, 23, 25, 26, 28, 32, 34, 35, 36} preds:[]
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
        r0 = 0;
        r1 = r5.contentResolver;	 Catch:{ IOException -> 0x001c, IOException -> 0x001c }
        r1 = r1.openInputStream(r6);	 Catch:{ IOException -> 0x001c, IOException -> 0x001c }
        r0 = r1;	 Catch:{ IOException -> 0x001c, IOException -> 0x001c }
        r1 = r5.parsers;	 Catch:{ IOException -> 0x001c, IOException -> 0x001c }
        r2 = r5.byteArrayPool;	 Catch:{ IOException -> 0x001c, IOException -> 0x001c }
        r1 = com.bumptech.glide.load.ImageHeaderParserUtils.getOrientation(r1, r0, r2);	 Catch:{ IOException -> 0x001c, IOException -> 0x001c }
        if (r0 == 0) goto L_0x0018;
    L_0x0012:
        r0.close();	 Catch:{ IOException -> 0x0016 }
        goto L_0x0019;
    L_0x0016:
        r2 = move-exception;
        goto L_0x0019;
    L_0x0019:
        return r1;
    L_0x001a:
        r1 = move-exception;
        goto L_0x0049;
    L_0x001c:
        r1 = move-exception;
        r2 = "ThumbStreamOpener";	 Catch:{ all -> 0x001a }
        r3 = 3;	 Catch:{ all -> 0x001a }
        r2 = android.util.Log.isLoggable(r2, r3);	 Catch:{ all -> 0x001a }
        if (r2 == 0) goto L_0x003d;	 Catch:{ all -> 0x001a }
    L_0x0026:
        r2 = "ThumbStreamOpener";	 Catch:{ all -> 0x001a }
        r3 = new java.lang.StringBuilder;	 Catch:{ all -> 0x001a }
        r3.<init>();	 Catch:{ all -> 0x001a }
        r4 = "Failed to open uri: ";	 Catch:{ all -> 0x001a }
        r3.append(r4);	 Catch:{ all -> 0x001a }
        r3.append(r6);	 Catch:{ all -> 0x001a }
        r3 = r3.toString();	 Catch:{ all -> 0x001a }
        android.util.Log.d(r2, r3, r1);	 Catch:{ all -> 0x001a }
        goto L_0x003e;
    L_0x003e:
        if (r0 == 0) goto L_0x0046;
    L_0x0040:
        r0.close();	 Catch:{ IOException -> 0x0044 }
    L_0x0043:
        goto L_0x0047;
    L_0x0044:
        r1 = move-exception;
        goto L_0x0043;
    L_0x0047:
        r1 = -1;
        return r1;
    L_0x0049:
        if (r0 == 0) goto L_0x0051;
    L_0x004b:
        r0.close();	 Catch:{ IOException -> 0x004f }
        goto L_0x0052;
    L_0x004f:
        r2 = move-exception;
        goto L_0x0052;
    L_0x0052:
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.load.data.mediastore.ThumbnailStreamOpener.getOrientation(android.net.Uri):int");
    }

    ThumbnailStreamOpener(List<ImageHeaderParser> parsers, ThumbnailQuery query, ArrayPool byteArrayPool, ContentResolver contentResolver) {
        this(parsers, DEFAULT_SERVICE, query, byteArrayPool, contentResolver);
    }

    ThumbnailStreamOpener(List<ImageHeaderParser> parsers, FileService service, ThumbnailQuery query, ArrayPool byteArrayPool, ContentResolver contentResolver) {
        this.service = service;
        this.query = query;
        this.byteArrayPool = byteArrayPool;
        this.contentResolver = contentResolver;
        this.parsers = parsers;
    }

    public InputStream open(Uri uri) throws FileNotFoundException {
        String path = getPath(uri);
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        File file = this.service.get(path);
        if (!isValid(file)) {
            return null;
        }
        Uri thumbnailUri = Uri.fromFile(file);
        try {
            return this.contentResolver.openInputStream(thumbnailUri);
        } catch (NullPointerException e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("NPE opening uri: ");
            stringBuilder.append(uri);
            stringBuilder.append(" -> ");
            stringBuilder.append(thumbnailUri);
            throw ((FileNotFoundException) new FileNotFoundException(stringBuilder.toString()).initCause(e));
        }
    }

    @Nullable
    private String getPath(@NonNull Uri uri) {
        Cursor cursor = this.query.query(uri);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    String string = cursor.getString(0);
                    if (cursor != null) {
                        cursor.close();
                    }
                    return string;
                }
            } catch (Throwable th) {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    private boolean isValid(File file) {
        return this.service.exists(file) && 0 < this.service.length(file);
    }
}
