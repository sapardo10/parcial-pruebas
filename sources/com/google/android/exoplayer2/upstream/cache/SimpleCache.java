package com.google.android.exoplayer2.upstream.cache;

import android.os.ConditionVariable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.upstream.cache.Cache.CacheException;
import com.google.android.exoplayer2.upstream.cache.Cache.Listener;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

public final class SimpleCache implements Cache {
    private static final String TAG = "SimpleCache";
    private static boolean cacheFolderLockingDisabled;
    private static final HashSet<File> lockedCacheDirs = new HashSet();
    private final File cacheDir;
    private final CacheEvictor evictor;
    private final CachedContentIndex index;
    private final HashMap<String, ArrayList<Listener>> listeners;
    private boolean released;
    private long totalSpace;

    public synchronized void release() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:31:0x003a in {5, 12, 22, 24, 27, 30} preds:[]
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
        monitor-enter(r4);
        r0 = r4.released;	 Catch:{ all -> 0x0037 }
        if (r0 == 0) goto L_0x0007;
    L_0x0005:
        monitor-exit(r4);
        return;
    L_0x0007:
        r0 = r4.listeners;	 Catch:{ all -> 0x0037 }
        r0.clear();	 Catch:{ all -> 0x0037 }
        r4.removeStaleSpans();	 Catch:{ all -> 0x0037 }
        r0 = 1;
        r1 = r4.index;	 Catch:{ CacheException -> 0x001f }
        r1.store();	 Catch:{ CacheException -> 0x001f }
        r1 = r4.cacheDir;	 Catch:{ all -> 0x0037 }
        unlockFolder(r1);	 Catch:{ all -> 0x0037 }
    L_0x001a:
        r4.released = r0;	 Catch:{ all -> 0x0037 }
        goto L_0x002d;
    L_0x001d:
        r1 = move-exception;
        goto L_0x002f;
    L_0x001f:
        r1 = move-exception;
        r2 = "SimpleCache";	 Catch:{ all -> 0x001d }
        r3 = "Storing index file failed";	 Catch:{ all -> 0x001d }
        com.google.android.exoplayer2.util.Log.m7e(r2, r3, r1);	 Catch:{ all -> 0x001d }
        r1 = r4.cacheDir;	 Catch:{ all -> 0x0037 }
        unlockFolder(r1);	 Catch:{ all -> 0x0037 }
        goto L_0x001a;
    L_0x002d:
        monitor-exit(r4);
        return;
    L_0x002f:
        r2 = r4.cacheDir;	 Catch:{ all -> 0x0037 }
        unlockFolder(r2);	 Catch:{ all -> 0x0037 }
        r4.released = r0;	 Catch:{ all -> 0x0037 }
        throw r1;	 Catch:{ all -> 0x0037 }
    L_0x0037:
        r0 = move-exception;
        monitor-exit(r4);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.upstream.cache.SimpleCache.release():void");
    }

    public synchronized com.google.android.exoplayer2.upstream.cache.SimpleCacheSpan startReadWrite(java.lang.String r2, long r3) throws java.lang.InterruptedException, com.google.android.exoplayer2.upstream.cache.Cache.CacheException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:12:0x0010 in {5, 8, 11} preds:[]
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
        r1 = this;
        monitor-enter(r1);
    L_0x0001:
        r0 = r1.startReadWriteNonBlocking(r2, r3);	 Catch:{ all -> 0x000d }
        if (r0 == 0) goto L_0x0009;
    L_0x0007:
        monitor-exit(r1);
        return r0;
    L_0x0009:
        r1.wait();	 Catch:{ all -> 0x000d }
        goto L_0x0001;
    L_0x000d:
        r2 = move-exception;
        monitor-exit(r1);
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.upstream.cache.SimpleCache.startReadWrite(java.lang.String, long):com.google.android.exoplayer2.upstream.cache.SimpleCacheSpan");
    }

    public static synchronized boolean isCacheFolderLocked(File cacheFolder) {
        boolean contains;
        synchronized (SimpleCache.class) {
            contains = lockedCacheDirs.contains(cacheFolder.getAbsoluteFile());
        }
        return contains;
    }

    @Deprecated
    public static synchronized void disableCacheFolderLocking() {
        synchronized (SimpleCache.class) {
            cacheFolderLockingDisabled = true;
            lockedCacheDirs.clear();
        }
    }

    public SimpleCache(File cacheDir, CacheEvictor evictor) {
        this(cacheDir, evictor, null, false);
    }

    public SimpleCache(File cacheDir, CacheEvictor evictor, byte[] secretKey) {
        this(cacheDir, evictor, secretKey, secretKey != null);
    }

    public SimpleCache(File cacheDir, CacheEvictor evictor, byte[] secretKey, boolean encrypt) {
        this(cacheDir, evictor, new CachedContentIndex(cacheDir, secretKey, encrypt));
    }

    SimpleCache(File cacheDir, CacheEvictor evictor, CachedContentIndex index) {
        if (lockFolder(cacheDir)) {
            this.cacheDir = cacheDir;
            this.evictor = evictor;
            this.index = index;
            this.listeners = new HashMap();
            final ConditionVariable conditionVariable = new ConditionVariable();
            new Thread("SimpleCache.initialize()") {
                public void run() {
                    synchronized (SimpleCache.this) {
                        conditionVariable.open();
                        SimpleCache.this.initialize();
                        SimpleCache.this.evictor.onCacheInitialized();
                    }
                }
            }.start();
            conditionVariable.block();
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Another SimpleCache instance uses the folder: ");
        stringBuilder.append(cacheDir);
        throw new IllegalStateException(stringBuilder.toString());
    }

    public synchronized NavigableSet<CacheSpan> addListener(String key, Listener listener) {
        Assertions.checkState(!this.released);
        ArrayList<Listener> listenersForKey = (ArrayList) this.listeners.get(key);
        if (listenersForKey == null) {
            listenersForKey = new ArrayList();
            this.listeners.put(key, listenersForKey);
        }
        listenersForKey.add(listener);
        return getCachedSpans(key);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void removeListener(java.lang.String r3, com.google.android.exoplayer2.upstream.cache.Cache.Listener r4) {
        /*
        r2 = this;
        monitor-enter(r2);
        r0 = r2.released;	 Catch:{ all -> 0x0024 }
        if (r0 == 0) goto L_0x0007;
    L_0x0005:
        monitor-exit(r2);
        return;
    L_0x0007:
        r0 = r2.listeners;	 Catch:{ all -> 0x0024 }
        r0 = r0.get(r3);	 Catch:{ all -> 0x0024 }
        r0 = (java.util.ArrayList) r0;	 Catch:{ all -> 0x0024 }
        if (r0 == 0) goto L_0x0021;
    L_0x0011:
        r0.remove(r4);	 Catch:{ all -> 0x0024 }
        r1 = r0.isEmpty();	 Catch:{ all -> 0x0024 }
        if (r1 == 0) goto L_0x0020;
    L_0x001a:
        r1 = r2.listeners;	 Catch:{ all -> 0x0024 }
        r1.remove(r3);	 Catch:{ all -> 0x0024 }
        goto L_0x0022;
    L_0x0020:
        goto L_0x0022;
    L_0x0022:
        monitor-exit(r2);
        return;
    L_0x0024:
        r3 = move-exception;
        monitor-exit(r2);
        throw r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.upstream.cache.SimpleCache.removeListener(java.lang.String, com.google.android.exoplayer2.upstream.cache.Cache$Listener):void");
    }

    @NonNull
    public synchronized NavigableSet<CacheSpan> getCachedSpans(String key) {
        NavigableSet<CacheSpan> treeSet;
        Assertions.checkState(!this.released);
        CachedContent cachedContent = this.index.get(key);
        if (cachedContent != null) {
            if (!cachedContent.isEmpty()) {
                treeSet = new TreeSet(cachedContent.getSpans());
            }
        }
        treeSet = new TreeSet();
        return treeSet;
    }

    public synchronized Set<String> getKeys() {
        Assertions.checkState(!this.released);
        return new HashSet(this.index.getKeys());
    }

    public synchronized long getCacheSpace() {
        Assertions.checkState(!this.released);
        return this.totalSpace;
    }

    @Nullable
    public synchronized SimpleCacheSpan startReadWriteNonBlocking(String key, long position) throws CacheException {
        Assertions.checkState(!this.released);
        SimpleCacheSpan cacheSpan = getSpan(key, position);
        if (cacheSpan.isCached) {
            try {
                SimpleCacheSpan newCacheSpan = this.index.get(key).touch(cacheSpan);
                notifySpanTouched(cacheSpan, newCacheSpan);
                return newCacheSpan;
            } catch (CacheException e) {
                return cacheSpan;
            }
        }
        CachedContent cachedContent = this.index.getOrAdd(key);
        if (cachedContent.isLocked()) {
            return null;
        }
        cachedContent.setLocked(true);
        return cacheSpan;
    }

    public synchronized File startFile(String key, long position, long maxLength) throws CacheException {
        CachedContent cachedContent;
        Assertions.checkState(!this.released);
        cachedContent = this.index.get(key);
        Assertions.checkNotNull(cachedContent);
        Assertions.checkState(cachedContent.isLocked());
        if (!this.cacheDir.exists()) {
            this.cacheDir.mkdirs();
            removeStaleSpans();
        }
        this.evictor.onStartFile(this, key, position, maxLength);
        return SimpleCacheSpan.getCacheFile(this.cacheDir, cachedContent.id, position, System.currentTimeMillis());
    }

    public synchronized void commitFile(File file) throws CacheException {
        boolean z = true;
        Assertions.checkState(!this.released);
        SimpleCacheSpan span = SimpleCacheSpan.createCacheEntry(file, this.index);
        Assertions.checkState(span != null);
        CachedContent cachedContent = this.index.get(span.key);
        Assertions.checkNotNull(cachedContent);
        Assertions.checkState(cachedContent.isLocked());
        if (!file.exists()) {
            return;
        }
        if (file.length() == 0) {
            file.delete();
            return;
        }
        long length = ContentMetadataInternal.getContentLength(cachedContent.getMetadata());
        if (length != -1) {
            if (span.position + span.length > length) {
                z = false;
            }
            Assertions.checkState(z);
        }
        addSpan(span);
        this.index.store();
        notifyAll();
    }

    public synchronized void releaseHoleSpan(CacheSpan holeSpan) {
        Assertions.checkState(!this.released);
        CachedContent cachedContent = this.index.get(holeSpan.key);
        Assertions.checkNotNull(cachedContent);
        Assertions.checkState(cachedContent.isLocked());
        cachedContent.setLocked(false);
        this.index.maybeRemove(cachedContent.key);
        notifyAll();
    }

    public synchronized void removeSpan(CacheSpan span) {
        Assertions.checkState(!this.released);
        removeSpanInternal(span);
    }

    public synchronized boolean isCached(String key, long position, long length) {
        boolean z;
        z = true;
        Assertions.checkState(!this.released);
        CachedContent cachedContent = this.index.get(key);
        if (cachedContent == null || cachedContent.getCachedBytesLength(position, length) < length) {
            z = false;
        }
        return z;
    }

    public synchronized long getCachedLength(String key, long position, long length) {
        CachedContent cachedContent;
        Assertions.checkState(!this.released);
        cachedContent = this.index.get(key);
        return cachedContent != null ? cachedContent.getCachedBytesLength(position, length) : -length;
    }

    public synchronized void setContentLength(String key, long length) throws CacheException {
        ContentMetadataMutations mutations = new ContentMetadataMutations();
        ContentMetadataInternal.setContentLength(mutations, length);
        applyContentMetadataMutations(key, mutations);
    }

    public synchronized long getContentLength(String key) {
        return ContentMetadataInternal.getContentLength(getContentMetadata(key));
    }

    public synchronized void applyContentMetadataMutations(String key, ContentMetadataMutations mutations) throws CacheException {
        Assertions.checkState(!this.released);
        this.index.applyContentMetadataMutations(key, mutations);
        this.index.store();
    }

    public synchronized ContentMetadata getContentMetadata(String key) {
        Assertions.checkState(!this.released);
        return this.index.getContentMetadata(key);
    }

    private SimpleCacheSpan getSpan(String key, long position) throws CacheException {
        CachedContent cachedContent = this.index.get(key);
        if (cachedContent == null) {
            return SimpleCacheSpan.createOpenHole(key, position);
        }
        SimpleCacheSpan span;
        while (true) {
            span = cachedContent.getSpan(position);
            if (span.isCached && !span.file.exists()) {
                removeStaleSpans();
            }
        }
        return span;
    }

    private void initialize() {
        if (this.cacheDir.exists()) {
            this.index.load();
            File[] files = this.cacheDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (!file.getName().equals(CachedContentIndex.FILE_NAME)) {
                        SimpleCacheSpan span = file.length() > 0 ? SimpleCacheSpan.createCacheEntry(file, this.index) : null;
                        if (span != null) {
                            addSpan(span);
                        } else {
                            file.delete();
                        }
                    }
                }
                this.index.removeEmpty();
                try {
                    this.index.store();
                } catch (CacheException e) {
                    Log.m7e(TAG, "Storing index file failed", e);
                }
                return;
            }
            return;
        }
        this.cacheDir.mkdirs();
    }

    private void addSpan(SimpleCacheSpan span) {
        this.index.getOrAdd(span.key).addSpan(span);
        this.totalSpace += span.length;
        notifySpanAdded(span);
    }

    private void removeSpanInternal(CacheSpan span) {
        CachedContent cachedContent = this.index.get(span.key);
        if (cachedContent != null) {
            if (cachedContent.removeSpan(span)) {
                this.totalSpace -= span.length;
                this.index.maybeRemove(cachedContent.key);
                notifySpanRemoved(span);
            }
        }
    }

    private void removeStaleSpans() {
        ArrayList<CacheSpan> spansToBeRemoved = new ArrayList();
        for (CachedContent cachedContent : this.index.getAll()) {
            Iterator it = cachedContent.getSpans().iterator();
            while (it.hasNext()) {
                CacheSpan span = (CacheSpan) it.next();
                if (!span.file.exists()) {
                    spansToBeRemoved.add(span);
                }
            }
        }
        for (int i = 0; i < spansToBeRemoved.size(); i++) {
            removeSpanInternal((CacheSpan) spansToBeRemoved.get(i));
        }
    }

    private void notifySpanRemoved(CacheSpan span) {
        ArrayList<Listener> keyListeners = (ArrayList) this.listeners.get(span.key);
        if (keyListeners != null) {
            for (int i = keyListeners.size() - 1; i >= 0; i--) {
                ((Listener) keyListeners.get(i)).onSpanRemoved(this, span);
            }
        }
        this.evictor.onSpanRemoved(this, span);
    }

    private void notifySpanAdded(SimpleCacheSpan span) {
        ArrayList<Listener> keyListeners = (ArrayList) this.listeners.get(span.key);
        if (keyListeners != null) {
            for (int i = keyListeners.size() - 1; i >= 0; i--) {
                ((Listener) keyListeners.get(i)).onSpanAdded(this, span);
            }
        }
        this.evictor.onSpanAdded(this, span);
    }

    private void notifySpanTouched(SimpleCacheSpan oldSpan, CacheSpan newSpan) {
        ArrayList<Listener> keyListeners = (ArrayList) this.listeners.get(oldSpan.key);
        if (keyListeners != null) {
            for (int i = keyListeners.size() - 1; i >= 0; i--) {
                ((Listener) keyListeners.get(i)).onSpanTouched(this, oldSpan, newSpan);
            }
        }
        this.evictor.onSpanTouched(this, oldSpan, newSpan);
    }

    private static synchronized boolean lockFolder(File cacheDir) {
        synchronized (SimpleCache.class) {
            if (cacheFolderLockingDisabled) {
                return true;
            }
            boolean add = lockedCacheDirs.add(cacheDir.getAbsoluteFile());
            return add;
        }
    }

    private static synchronized void unlockFolder(File cacheDir) {
        synchronized (SimpleCache.class) {
            if (!cacheFolderLockingDisabled) {
                lockedCacheDirs.remove(cacheDir.getAbsoluteFile());
            }
        }
    }
}
