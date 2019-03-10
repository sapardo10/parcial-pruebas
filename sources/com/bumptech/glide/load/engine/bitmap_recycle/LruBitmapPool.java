package com.bumptech.glide.load.engine.bitmap_recycle;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class LruBitmapPool implements BitmapPool {
    private static final Config DEFAULT_CONFIG = Config.ARGB_8888;
    private static final String TAG = "LruBitmapPool";
    private final Set<Config> allowedConfigs;
    private long currentSize;
    private int evictions;
    private int hits;
    private final long initialMaxSize;
    private long maxSize;
    private int misses;
    private int puts;
    private final LruPoolStrategy strategy;
    private final BitmapTracker tracker;

    private interface BitmapTracker {
        void add(Bitmap bitmap);

        void remove(Bitmap bitmap);
    }

    private static final class NullBitmapTracker implements BitmapTracker {
        NullBitmapTracker() {
        }

        public void add(Bitmap bitmap) {
        }

        public void remove(Bitmap bitmap) {
        }
    }

    private static class ThrowingBitmapTracker implements BitmapTracker {
        private final Set<Bitmap> bitmaps = Collections.synchronizedSet(new HashSet());

        private ThrowingBitmapTracker() {
        }

        public void add(Bitmap bitmap) {
            if (this.bitmaps.contains(bitmap)) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Can't add already added bitmap: ");
                stringBuilder.append(bitmap);
                stringBuilder.append(" [");
                stringBuilder.append(bitmap.getWidth());
                stringBuilder.append("x");
                stringBuilder.append(bitmap.getHeight());
                stringBuilder.append("]");
                throw new IllegalStateException(stringBuilder.toString());
            }
            this.bitmaps.add(bitmap);
        }

        public void remove(Bitmap bitmap) {
            if (this.bitmaps.contains(bitmap)) {
                this.bitmaps.remove(bitmap);
                return;
            }
            throw new IllegalStateException("Cannot remove bitmap not in tracker");
        }
    }

    private synchronized void trimToSize(long r6) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:25:0x0074 in {8, 9, 12, 16, 17, 19, 21, 24} preds:[]
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
        monitor-enter(r5);
    L_0x0001:
        r0 = r5.currentSize;	 Catch:{ all -> 0x0071 }
        r2 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1));	 Catch:{ all -> 0x0071 }
        if (r2 <= 0) goto L_0x006f;	 Catch:{ all -> 0x0071 }
    L_0x0007:
        r0 = r5.strategy;	 Catch:{ all -> 0x0071 }
        r0 = r0.removeLast();	 Catch:{ all -> 0x0071 }
        if (r0 != 0) goto L_0x002a;	 Catch:{ all -> 0x0071 }
    L_0x000f:
        r1 = "LruBitmapPool";	 Catch:{ all -> 0x0071 }
        r2 = 5;	 Catch:{ all -> 0x0071 }
        r1 = android.util.Log.isLoggable(r1, r2);	 Catch:{ all -> 0x0071 }
        if (r1 == 0) goto L_0x0023;	 Catch:{ all -> 0x0071 }
    L_0x0018:
        r1 = "LruBitmapPool";	 Catch:{ all -> 0x0071 }
        r2 = "Size mismatch, resetting";	 Catch:{ all -> 0x0071 }
        android.util.Log.w(r1, r2);	 Catch:{ all -> 0x0071 }
        r5.dumpUnchecked();	 Catch:{ all -> 0x0071 }
        goto L_0x0024;	 Catch:{ all -> 0x0071 }
    L_0x0024:
        r1 = 0;	 Catch:{ all -> 0x0071 }
        r5.currentSize = r1;	 Catch:{ all -> 0x0071 }
        monitor-exit(r5);
        return;
    L_0x002a:
        r1 = r5.tracker;	 Catch:{ all -> 0x0071 }
        r1.remove(r0);	 Catch:{ all -> 0x0071 }
        r1 = r5.currentSize;	 Catch:{ all -> 0x0071 }
        r3 = r5.strategy;	 Catch:{ all -> 0x0071 }
        r3 = r3.getSize(r0);	 Catch:{ all -> 0x0071 }
        r3 = (long) r3;	 Catch:{ all -> 0x0071 }
        r1 = r1 - r3;	 Catch:{ all -> 0x0071 }
        r5.currentSize = r1;	 Catch:{ all -> 0x0071 }
        r1 = r5.evictions;	 Catch:{ all -> 0x0071 }
        r1 = r1 + 1;	 Catch:{ all -> 0x0071 }
        r5.evictions = r1;	 Catch:{ all -> 0x0071 }
        r1 = "LruBitmapPool";	 Catch:{ all -> 0x0071 }
        r2 = 3;	 Catch:{ all -> 0x0071 }
        r1 = android.util.Log.isLoggable(r1, r2);	 Catch:{ all -> 0x0071 }
        if (r1 == 0) goto L_0x0067;	 Catch:{ all -> 0x0071 }
    L_0x004a:
        r1 = "LruBitmapPool";	 Catch:{ all -> 0x0071 }
        r2 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0071 }
        r2.<init>();	 Catch:{ all -> 0x0071 }
        r3 = "Evicting bitmap=";	 Catch:{ all -> 0x0071 }
        r2.append(r3);	 Catch:{ all -> 0x0071 }
        r3 = r5.strategy;	 Catch:{ all -> 0x0071 }
        r3 = r3.logBitmap(r0);	 Catch:{ all -> 0x0071 }
        r2.append(r3);	 Catch:{ all -> 0x0071 }
        r2 = r2.toString();	 Catch:{ all -> 0x0071 }
        android.util.Log.d(r1, r2);	 Catch:{ all -> 0x0071 }
        goto L_0x0068;	 Catch:{ all -> 0x0071 }
    L_0x0068:
        r5.dump();	 Catch:{ all -> 0x0071 }
        r0.recycle();	 Catch:{ all -> 0x0071 }
        goto L_0x0001;
    L_0x006f:
        monitor-exit(r5);
        return;
    L_0x0071:
        r6 = move-exception;
        monitor-exit(r5);
        throw r6;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool.trimToSize(long):void");
    }

    LruBitmapPool(long maxSize, LruPoolStrategy strategy, Set<Config> allowedConfigs) {
        this.initialMaxSize = maxSize;
        this.maxSize = maxSize;
        this.strategy = strategy;
        this.allowedConfigs = allowedConfigs;
        this.tracker = new NullBitmapTracker();
    }

    public LruBitmapPool(long maxSize) {
        this(maxSize, getDefaultStrategy(), getDefaultAllowedConfigs());
    }

    public LruBitmapPool(long maxSize, Set<Config> allowedConfigs) {
        this(maxSize, getDefaultStrategy(), allowedConfigs);
    }

    public long getMaxSize() {
        return this.maxSize;
    }

    public synchronized void setSizeMultiplier(float sizeMultiplier) {
        this.maxSize = (long) Math.round(((float) this.initialMaxSize) * sizeMultiplier);
        evict();
    }

    public synchronized void put(Bitmap bitmap) {
        if (bitmap == null) {
            throw new NullPointerException("Bitmap must not be null");
        } else if (bitmap.isRecycled()) {
            throw new IllegalStateException("Cannot pool recycled bitmap");
        } else {
            if (bitmap.isMutable() && ((long) this.strategy.getSize(bitmap)) <= this.maxSize) {
                if (this.allowedConfigs.contains(bitmap.getConfig())) {
                    int size = this.strategy.getSize(bitmap);
                    this.strategy.put(bitmap);
                    this.tracker.add(bitmap);
                    this.puts++;
                    this.currentSize += (long) size;
                    if (Log.isLoggable(TAG, 2)) {
                        String str = TAG;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Put bitmap in pool=");
                        stringBuilder.append(this.strategy.logBitmap(bitmap));
                        Log.v(str, stringBuilder.toString());
                    }
                    dump();
                    evict();
                    return;
                }
            }
            if (Log.isLoggable(TAG, 2)) {
                String str2 = TAG;
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Reject bitmap from pool, bitmap: ");
                stringBuilder2.append(this.strategy.logBitmap(bitmap));
                stringBuilder2.append(", is mutable: ");
                stringBuilder2.append(bitmap.isMutable());
                stringBuilder2.append(", is allowed config: ");
                stringBuilder2.append(this.allowedConfigs.contains(bitmap.getConfig()));
                Log.v(str2, stringBuilder2.toString());
            }
            bitmap.recycle();
        }
    }

    private void evict() {
        trimToSize(this.maxSize);
    }

    @NonNull
    public Bitmap get(int width, int height, Config config) {
        Bitmap result = getDirtyOrNull(width, height, config);
        if (result == null) {
            return createBitmap(width, height, config);
        }
        result.eraseColor(0);
        return result;
    }

    @NonNull
    public Bitmap getDirty(int width, int height, Config config) {
        Bitmap result = getDirtyOrNull(width, height, config);
        if (result == null) {
            return createBitmap(width, height, config);
        }
        return result;
    }

    @NonNull
    private static Bitmap createBitmap(int width, int height, @Nullable Config config) {
        return Bitmap.createBitmap(width, height, config != null ? config : DEFAULT_CONFIG);
    }

    @TargetApi(26)
    private static void assertNotHardwareConfig(Config config) {
        if (VERSION.SDK_INT >= 26 && config == Config.HARDWARE) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Cannot create a mutable Bitmap with config: ");
            stringBuilder.append(config);
            stringBuilder.append(". Consider setting Downsampler#ALLOW_HARDWARE_CONFIG to false in your RequestOptions and/or in GlideBuilder.setDefaultRequestOptions");
            throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    @Nullable
    private synchronized Bitmap getDirtyOrNull(int width, int height, @Nullable Config config) {
        Bitmap result;
        assertNotHardwareConfig(config);
        result = this.strategy.get(width, height, config != null ? config : DEFAULT_CONFIG);
        if (result == null) {
            if (Log.isLoggable(TAG, 3)) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Missing bitmap=");
                stringBuilder.append(this.strategy.logBitmap(width, height, config));
                Log.d(str, stringBuilder.toString());
            }
            this.misses++;
        } else {
            this.hits++;
            this.currentSize -= (long) this.strategy.getSize(result);
            this.tracker.remove(result);
            normalize(result);
        }
        if (Log.isLoggable(TAG, 2)) {
            str = TAG;
            stringBuilder = new StringBuilder();
            stringBuilder.append("Get bitmap=");
            stringBuilder.append(this.strategy.logBitmap(width, height, config));
            Log.v(str, stringBuilder.toString());
        }
        dump();
        return result;
    }

    private static void normalize(Bitmap bitmap) {
        bitmap.setHasAlpha(true);
        maybeSetPreMultiplied(bitmap);
    }

    @TargetApi(19)
    private static void maybeSetPreMultiplied(Bitmap bitmap) {
        if (VERSION.SDK_INT >= 19) {
            bitmap.setPremultiplied(true);
        }
    }

    public void clearMemory() {
        if (Log.isLoggable(TAG, 3)) {
            Log.d(TAG, "clearMemory");
        }
        trimToSize(0);
    }

    @SuppressLint({"InlinedApi"})
    public void trimMemory(int level) {
        if (Log.isLoggable(TAG, 3)) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("trimMemory, level=");
            stringBuilder.append(level);
            Log.d(str, stringBuilder.toString());
        }
        if (level >= 40) {
            clearMemory();
            return;
        }
        if (level < 20) {
            if (level != 15) {
                return;
            }
        }
        trimToSize(getMaxSize() / 2);
    }

    private void dump() {
        if (Log.isLoggable(TAG, 2)) {
            dumpUnchecked();
        }
    }

    private void dumpUnchecked() {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Hits=");
        stringBuilder.append(this.hits);
        stringBuilder.append(", misses=");
        stringBuilder.append(this.misses);
        stringBuilder.append(", puts=");
        stringBuilder.append(this.puts);
        stringBuilder.append(", evictions=");
        stringBuilder.append(this.evictions);
        stringBuilder.append(", currentSize=");
        stringBuilder.append(this.currentSize);
        stringBuilder.append(", maxSize=");
        stringBuilder.append(this.maxSize);
        stringBuilder.append("\nStrategy=");
        stringBuilder.append(this.strategy);
        Log.v(str, stringBuilder.toString());
    }

    private static LruPoolStrategy getDefaultStrategy() {
        if (VERSION.SDK_INT >= 19) {
            return new SizeConfigStrategy();
        }
        return new AttributeStrategy();
    }

    @TargetApi(26)
    private static Set<Config> getDefaultAllowedConfigs() {
        Set<Config> configs = new HashSet(Arrays.asList(Config.values()));
        if (VERSION.SDK_INT >= 19) {
            configs.add(null);
        }
        if (VERSION.SDK_INT >= 26) {
            configs.remove(Config.HARDWARE);
        }
        return Collections.unmodifiableSet(configs);
    }
}
