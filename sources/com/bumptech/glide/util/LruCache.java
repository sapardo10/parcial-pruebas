package com.bumptech.glide.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

public class LruCache<T, Y> {
    private final Map<T, Y> cache = new LinkedHashMap(100, 0.75f, true);
    private long currentSize;
    private final long initialMaxSize;
    private long maxSize;

    protected synchronized void trimToSize(long r8) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:11:0x0035 in {5, 7, 10} preds:[]
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
        r7 = this;
        monitor-enter(r7);
    L_0x0001:
        r0 = r7.currentSize;	 Catch:{ all -> 0x0032 }
        r2 = (r0 > r8 ? 1 : (r0 == r8 ? 0 : -1));	 Catch:{ all -> 0x0032 }
        if (r2 <= 0) goto L_0x0030;	 Catch:{ all -> 0x0032 }
    L_0x0007:
        r0 = r7.cache;	 Catch:{ all -> 0x0032 }
        r0 = r0.entrySet();	 Catch:{ all -> 0x0032 }
        r0 = r0.iterator();	 Catch:{ all -> 0x0032 }
        r1 = r0.next();	 Catch:{ all -> 0x0032 }
        r1 = (java.util.Map.Entry) r1;	 Catch:{ all -> 0x0032 }
        r2 = r1.getValue();	 Catch:{ all -> 0x0032 }
        r3 = r7.currentSize;	 Catch:{ all -> 0x0032 }
        r5 = r7.getSize(r2);	 Catch:{ all -> 0x0032 }
        r5 = (long) r5;	 Catch:{ all -> 0x0032 }
        r3 = r3 - r5;	 Catch:{ all -> 0x0032 }
        r7.currentSize = r3;	 Catch:{ all -> 0x0032 }
        r3 = r1.getKey();	 Catch:{ all -> 0x0032 }
        r0.remove();	 Catch:{ all -> 0x0032 }
        r7.onItemEvicted(r3, r2);	 Catch:{ all -> 0x0032 }
        goto L_0x0001;
    L_0x0030:
        monitor-exit(r7);
        return;
    L_0x0032:
        r8 = move-exception;
        monitor-exit(r7);
        throw r8;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.util.LruCache.trimToSize(long):void");
    }

    public LruCache(long size) {
        this.initialMaxSize = size;
        this.maxSize = size;
    }

    public synchronized void setSizeMultiplier(float multiplier) {
        if (multiplier >= 0.0f) {
            this.maxSize = (long) Math.round(((float) this.initialMaxSize) * multiplier);
            evict();
        } else {
            throw new IllegalArgumentException("Multiplier must be >= 0");
        }
    }

    protected int getSize(@Nullable Y y) {
        return 1;
    }

    protected synchronized int getCount() {
        return this.cache.size();
    }

    protected void onItemEvicted(@NonNull T t, @Nullable Y y) {
    }

    public synchronized long getMaxSize() {
        return this.maxSize;
    }

    public synchronized long getCurrentSize() {
        return this.currentSize;
    }

    public synchronized boolean contains(@NonNull T key) {
        return this.cache.containsKey(key);
    }

    @Nullable
    public synchronized Y get(@NonNull T key) {
        return this.cache.get(key);
    }

    @Nullable
    public synchronized Y put(@NonNull T key, @Nullable Y item) {
        int itemSize = getSize(item);
        if (((long) itemSize) >= this.maxSize) {
            onItemEvicted(key, item);
            return null;
        }
        if (item != null) {
            this.currentSize += (long) itemSize;
        }
        Y old = this.cache.put(key, item);
        if (old != null) {
            this.currentSize -= (long) getSize(old);
            if (!old.equals(item)) {
                onItemEvicted(key, old);
            }
        }
        evict();
        return old;
    }

    @Nullable
    public synchronized Y remove(@NonNull T key) {
        Y value;
        value = this.cache.remove(key);
        if (value != null) {
            this.currentSize -= (long) getSize(value);
        }
        return value;
    }

    public void clearMemory() {
        trimToSize(0);
    }

    private void evict() {
        trimToSize(this.maxSize);
    }
}
