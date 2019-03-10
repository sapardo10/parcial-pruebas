package com.google.android.exoplayer2.upstream.cache;

import android.support.annotation.NonNull;
import com.google.android.exoplayer2.extractor.ChunkIndex;
import com.google.android.exoplayer2.upstream.cache.Cache.Listener;
import java.util.Arrays;
import java.util.TreeSet;

public final class CachedRegionTracker implements Listener {
    public static final int CACHED_TO_END = -2;
    public static final int NOT_CACHED = -1;
    private static final String TAG = "CachedRegionTracker";
    private final Cache cache;
    private final String cacheKey;
    private final ChunkIndex chunkIndex;
    private final Region lookupRegion;
    private final TreeSet<Region> regions;

    private static class Region implements Comparable<Region> {
        public long endOffset;
        public int endOffsetIndex;
        public long startOffset;

        public Region(long position, long endOffset) {
            this.startOffset = position;
            this.endOffset = endOffset;
        }

        public int compareTo(@NonNull Region another) {
            long j = this.startOffset;
            long j2 = another.startOffset;
            if (j < j2) {
                return -1;
            }
            return j == j2 ? 0 : 1;
        }
    }

    public CachedRegionTracker(com.google.android.exoplayer2.upstream.cache.Cache r4, java.lang.String r5, com.google.android.exoplayer2.extractor.ChunkIndex r6) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:13:0x0038 in {6, 9, 12} preds:[]
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
        r3.<init>();
        r3.cache = r4;
        r3.cacheKey = r5;
        r3.chunkIndex = r6;
        r0 = new java.util.TreeSet;
        r0.<init>();
        r3.regions = r0;
        r0 = new com.google.android.exoplayer2.upstream.cache.CachedRegionTracker$Region;
        r1 = 0;
        r0.<init>(r1, r1);
        r3.lookupRegion = r0;
        monitor-enter(r3);
        r0 = r4.addListener(r5, r3);	 Catch:{ all -> 0x0035 }
        r1 = r0.descendingIterator();	 Catch:{ all -> 0x0035 }
    L_0x0022:
        r2 = r1.hasNext();	 Catch:{ all -> 0x0035 }
        if (r2 == 0) goto L_0x0032;	 Catch:{ all -> 0x0035 }
    L_0x0028:
        r2 = r1.next();	 Catch:{ all -> 0x0035 }
        r2 = (com.google.android.exoplayer2.upstream.cache.CacheSpan) r2;	 Catch:{ all -> 0x0035 }
        r3.mergeSpan(r2);	 Catch:{ all -> 0x0035 }
        goto L_0x0022;	 Catch:{ all -> 0x0035 }
        monitor-exit(r3);	 Catch:{ all -> 0x0035 }
        return;	 Catch:{ all -> 0x0035 }
    L_0x0035:
        r0 = move-exception;	 Catch:{ all -> 0x0035 }
        monitor-exit(r3);	 Catch:{ all -> 0x0035 }
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.upstream.cache.CachedRegionTracker.<init>(com.google.android.exoplayer2.upstream.cache.Cache, java.lang.String, com.google.android.exoplayer2.extractor.ChunkIndex):void");
    }

    public void release() {
        this.cache.removeListener(this.cacheKey, this);
    }

    public synchronized int getRegionEndTimeMs(long byteOffset) {
        this.lookupRegion.startOffset = byteOffset;
        Region floorRegion = (Region) this.regions.floor(this.lookupRegion);
        if (floorRegion != null && byteOffset <= floorRegion.endOffset) {
            if (floorRegion.endOffsetIndex != -1) {
                int index = floorRegion.endOffsetIndex;
                if (index == this.chunkIndex.length - 1 && floorRegion.endOffset == this.chunkIndex.offsets[index] + ((long) this.chunkIndex.sizes[index])) {
                    return -2;
                }
                return (int) ((this.chunkIndex.timesUs[index] + ((this.chunkIndex.durationsUs[index] * (floorRegion.endOffset - this.chunkIndex.offsets[index])) / ((long) this.chunkIndex.sizes[index]))) / 1000);
            }
        }
        return -1;
    }

    public synchronized void onSpanAdded(Cache cache, CacheSpan span) {
        mergeSpan(span);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void onSpanRemoved(com.google.android.exoplayer2.upstream.cache.Cache r8, com.google.android.exoplayer2.upstream.cache.CacheSpan r9) {
        /*
        r7 = this;
        monitor-enter(r7);
        r0 = new com.google.android.exoplayer2.upstream.cache.CachedRegionTracker$Region;	 Catch:{ all -> 0x0071 }
        r1 = r9.position;	 Catch:{ all -> 0x0071 }
        r3 = r9.position;	 Catch:{ all -> 0x0071 }
        r5 = r9.length;	 Catch:{ all -> 0x0071 }
        r3 = r3 + r5;
        r0.<init>(r1, r3);	 Catch:{ all -> 0x0071 }
        r1 = r7.regions;	 Catch:{ all -> 0x0071 }
        r1 = r1.floor(r0);	 Catch:{ all -> 0x0071 }
        r1 = (com.google.android.exoplayer2.upstream.cache.CachedRegionTracker.Region) r1;	 Catch:{ all -> 0x0071 }
        if (r1 != 0) goto L_0x0020;
    L_0x0017:
        r2 = "CachedRegionTracker";
        r3 = "Removed a span we were not aware of";
        com.google.android.exoplayer2.util.Log.m6e(r2, r3);	 Catch:{ all -> 0x0071 }
        monitor-exit(r7);
        return;
    L_0x0020:
        r2 = r7.regions;	 Catch:{ all -> 0x0071 }
        r2.remove(r1);	 Catch:{ all -> 0x0071 }
        r2 = r1.startOffset;	 Catch:{ all -> 0x0071 }
        r4 = r0.startOffset;	 Catch:{ all -> 0x0071 }
        r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r6 >= 0) goto L_0x004f;
    L_0x002d:
        r2 = new com.google.android.exoplayer2.upstream.cache.CachedRegionTracker$Region;	 Catch:{ all -> 0x0071 }
        r3 = r1.startOffset;	 Catch:{ all -> 0x0071 }
        r5 = r0.startOffset;	 Catch:{ all -> 0x0071 }
        r2.<init>(r3, r5);	 Catch:{ all -> 0x0071 }
        r3 = r7.chunkIndex;	 Catch:{ all -> 0x0071 }
        r3 = r3.offsets;	 Catch:{ all -> 0x0071 }
        r4 = r2.endOffset;	 Catch:{ all -> 0x0071 }
        r3 = java.util.Arrays.binarySearch(r3, r4);	 Catch:{ all -> 0x0071 }
        if (r3 >= 0) goto L_0x0046;
    L_0x0042:
        r4 = -r3;
        r4 = r4 + -2;
        goto L_0x0047;
    L_0x0046:
        r4 = r3;
    L_0x0047:
        r2.endOffsetIndex = r4;	 Catch:{ all -> 0x0071 }
        r4 = r7.regions;	 Catch:{ all -> 0x0071 }
        r4.add(r2);	 Catch:{ all -> 0x0071 }
        goto L_0x0050;
    L_0x0050:
        r2 = r1.endOffset;	 Catch:{ all -> 0x0071 }
        r4 = r0.endOffset;	 Catch:{ all -> 0x0071 }
        r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r6 <= 0) goto L_0x006e;
    L_0x0058:
        r2 = new com.google.android.exoplayer2.upstream.cache.CachedRegionTracker$Region;	 Catch:{ all -> 0x0071 }
        r3 = r0.endOffset;	 Catch:{ all -> 0x0071 }
        r5 = 1;
        r3 = r3 + r5;
        r5 = r1.endOffset;	 Catch:{ all -> 0x0071 }
        r2.<init>(r3, r5);	 Catch:{ all -> 0x0071 }
        r3 = r1.endOffsetIndex;	 Catch:{ all -> 0x0071 }
        r2.endOffsetIndex = r3;	 Catch:{ all -> 0x0071 }
        r3 = r7.regions;	 Catch:{ all -> 0x0071 }
        r3.add(r2);	 Catch:{ all -> 0x0071 }
        goto L_0x006f;
    L_0x006f:
        monitor-exit(r7);
        return;
    L_0x0071:
        r8 = move-exception;
        monitor-exit(r7);
        throw r8;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.upstream.cache.CachedRegionTracker.onSpanRemoved(com.google.android.exoplayer2.upstream.cache.Cache, com.google.android.exoplayer2.upstream.cache.CacheSpan):void");
    }

    public void onSpanTouched(Cache cache, CacheSpan oldSpan, CacheSpan newSpan) {
    }

    private void mergeSpan(CacheSpan span) {
        Region newRegion = new Region(span.position, span.position + span.length);
        Region floorRegion = (Region) this.regions.floor(newRegion);
        Region ceilingRegion = (Region) this.regions.ceiling(newRegion);
        boolean floorConnects = regionsConnect(floorRegion, newRegion);
        if (regionsConnect(newRegion, ceilingRegion)) {
            if (floorConnects) {
                floorRegion.endOffset = ceilingRegion.endOffset;
                floorRegion.endOffsetIndex = ceilingRegion.endOffsetIndex;
            } else {
                newRegion.endOffset = ceilingRegion.endOffset;
                newRegion.endOffsetIndex = ceilingRegion.endOffsetIndex;
                this.regions.add(newRegion);
            }
            this.regions.remove(ceilingRegion);
        } else if (floorConnects) {
            floorRegion.endOffset = newRegion.endOffset;
            index = floorRegion.endOffsetIndex;
            while (index < this.chunkIndex.length - 1 && this.chunkIndex.offsets[index + 1] <= floorRegion.endOffset) {
                index++;
            }
            floorRegion.endOffsetIndex = index;
        } else {
            index = Arrays.binarySearch(this.chunkIndex.offsets, newRegion.endOffset);
            newRegion.endOffsetIndex = index < 0 ? (-index) - 2 : index;
            this.regions.add(newRegion);
        }
    }

    private boolean regionsConnect(Region lower, Region upper) {
        return (lower == null || upper == null || lower.endOffset != upper.startOffset) ? false : true;
    }
}
