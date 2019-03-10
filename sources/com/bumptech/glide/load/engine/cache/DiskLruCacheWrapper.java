package com.bumptech.glide.load.engine.cache;

import android.util.Log;
import com.bumptech.glide.disklrucache.DiskLruCache;
import com.bumptech.glide.disklrucache.DiskLruCache.Editor;
import com.bumptech.glide.disklrucache.DiskLruCache.Value;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.cache.DiskCache.Writer;
import java.io.File;
import java.io.IOException;

public class DiskLruCacheWrapper implements DiskCache {
    private static final int APP_VERSION = 1;
    private static final String TAG = "DiskLruCacheWrapper";
    private static final int VALUE_COUNT = 1;
    private static DiskLruCacheWrapper wrapper;
    private final File directory;
    private DiskLruCache diskLruCache;
    private final long maxSize;
    private final SafeKeyGenerator safeKeyGenerator;
    private final DiskCacheWriteLocker writeLocker = new DiskCacheWriteLocker();

    public synchronized void clear() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:23:0x002a in {2, 13, 14, 16, 19, 22} preds:[]
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
        r3 = this;
        monitor-enter(r3);
        r0 = r3.getDiskCache();	 Catch:{ IOException -> 0x000e }
        r0.delete();	 Catch:{ IOException -> 0x000e }
    L_0x0008:
        r3.resetDiskCache();	 Catch:{ all -> 0x0027 }
        goto L_0x0021;
    L_0x000c:
        r0 = move-exception;
        goto L_0x0023;
    L_0x000e:
        r0 = move-exception;
        r1 = "DiskLruCacheWrapper";	 Catch:{ all -> 0x000c }
        r2 = 5;	 Catch:{ all -> 0x000c }
        r1 = android.util.Log.isLoggable(r1, r2);	 Catch:{ all -> 0x000c }
        if (r1 == 0) goto L_0x0020;	 Catch:{ all -> 0x000c }
    L_0x0018:
        r1 = "DiskLruCacheWrapper";	 Catch:{ all -> 0x000c }
        r2 = "Unable to clear disk cache or disk cache cleared externally";	 Catch:{ all -> 0x000c }
        android.util.Log.w(r1, r2, r0);	 Catch:{ all -> 0x000c }
        goto L_0x0008;
    L_0x0020:
        goto L_0x0008;
    L_0x0021:
        monitor-exit(r3);
        return;
    L_0x0023:
        r3.resetDiskCache();	 Catch:{ all -> 0x0027 }
        throw r0;	 Catch:{ all -> 0x0027 }
    L_0x0027:
        r0 = move-exception;
        monitor-exit(r3);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper.clear():void");
    }

    @Deprecated
    public static synchronized DiskCache get(File directory, long maxSize) {
        DiskCache diskCache;
        synchronized (DiskLruCacheWrapper.class) {
            if (wrapper == null) {
                wrapper = new DiskLruCacheWrapper(directory, maxSize);
            }
            diskCache = wrapper;
        }
        return diskCache;
    }

    public static DiskCache create(File directory, long maxSize) {
        return new DiskLruCacheWrapper(directory, maxSize);
    }

    @Deprecated
    protected DiskLruCacheWrapper(File directory, long maxSize) {
        this.directory = directory;
        this.maxSize = maxSize;
        this.safeKeyGenerator = new SafeKeyGenerator();
    }

    private synchronized DiskLruCache getDiskCache() throws IOException {
        if (this.diskLruCache == null) {
            this.diskLruCache = DiskLruCache.open(this.directory, 1, 1, this.maxSize);
        }
        return this.diskLruCache;
    }

    public File get(Key key) {
        String safeKey = this.safeKeyGenerator.getSafeKey(key);
        if (Log.isLoggable(TAG, 2)) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Get: Obtained: ");
            stringBuilder.append(safeKey);
            stringBuilder.append(" for for Key: ");
            stringBuilder.append(key);
            Log.v(str, stringBuilder.toString());
        }
        File result = null;
        try {
            Value value = getDiskCache().get(safeKey);
            if (value != null) {
                result = value.getFile(0);
            }
        } catch (IOException e) {
            if (Log.isLoggable(TAG, 5)) {
                Log.w(TAG, "Unable to get from disk cache", e);
            }
        }
        return result;
    }

    public void put(Key key, Writer writer) {
        Editor editor;
        String safeKey = this.safeKeyGenerator.getSafeKey(key);
        this.writeLocker.acquire(safeKey);
        try {
            if (Log.isLoggable(TAG, 2)) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Put: Obtained: ");
                stringBuilder.append(safeKey);
                stringBuilder.append(" for for Key: ");
                stringBuilder.append(key);
                Log.v(str, stringBuilder.toString());
            }
            try {
                DiskLruCache diskCache = getDiskCache();
                if (diskCache.get(safeKey) == null) {
                    editor = diskCache.edit(safeKey);
                    if (editor != null) {
                        if (writer.write(editor.getFile(null))) {
                            editor.commit();
                        }
                        editor.abortUnlessCommitted();
                        this.writeLocker.release(safeKey);
                        return;
                    }
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("Had two simultaneous puts for: ");
                    stringBuilder2.append(safeKey);
                    throw new IllegalStateException(stringBuilder2.toString());
                }
            } catch (IOException e) {
                if (Log.isLoggable(TAG, 5)) {
                    Log.w(TAG, "Unable to put to disk cache", e);
                }
            } catch (Throwable th) {
                editor.abortUnlessCommitted();
            }
        } finally {
            this.writeLocker.release(safeKey);
        }
    }

    public void delete(Key key) {
        try {
            getDiskCache().remove(this.safeKeyGenerator.getSafeKey(key));
        } catch (IOException e) {
            if (Log.isLoggable(TAG, 5)) {
                Log.w(TAG, "Unable to delete from disk cache", e);
            }
        }
    }

    private synchronized void resetDiskCache() {
        this.diskLruCache = null;
    }
}
