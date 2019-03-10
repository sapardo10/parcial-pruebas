package com.bumptech.glide.load.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.util.Pools.Pool;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import com.bumptech.glide.util.Preconditions;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MultiModelLoaderFactory {
    private static final Factory DEFAULT_FACTORY = new Factory();
    private static final ModelLoader<Object, Object> EMPTY_MODEL_LOADER = new EmptyModelLoader();
    private final Set<Entry<?, ?>> alreadyUsedEntries;
    private final List<Entry<?, ?>> entries;
    private final Factory factory;
    private final Pool<List<Throwable>> throwableListPool;

    private static class Entry<Model, Data> {
        final Class<Data> dataClass;
        final ModelLoaderFactory<? extends Model, ? extends Data> factory;
        private final Class<Model> modelClass;

        public Entry(@NonNull Class<Model> modelClass, @NonNull Class<Data> dataClass, @NonNull ModelLoaderFactory<? extends Model, ? extends Data> factory) {
            this.modelClass = modelClass;
            this.dataClass = dataClass;
            this.factory = factory;
        }

        public boolean handles(@NonNull Class<?> modelClass, @NonNull Class<?> dataClass) {
            return handles(modelClass) && this.dataClass.isAssignableFrom(dataClass);
        }

        public boolean handles(@NonNull Class<?> modelClass) {
            return this.modelClass.isAssignableFrom(modelClass);
        }
    }

    static class Factory {
        Factory() {
        }

        @NonNull
        public <Model, Data> MultiModelLoader<Model, Data> build(@NonNull List<ModelLoader<Model, Data>> modelLoaders, @NonNull Pool<List<Throwable>> throwableListPool) {
            return new MultiModelLoader(modelLoaders, throwableListPool);
        }
    }

    private static class EmptyModelLoader implements ModelLoader<Object, Object> {
        EmptyModelLoader() {
        }

        @Nullable
        public LoadData<Object> buildLoadData(@NonNull Object o, int width, int height, @NonNull Options options) {
            return null;
        }

        public boolean handles(@NonNull Object o) {
            return false;
        }
    }

    @android.support.annotation.NonNull
    public synchronized <Model, Data> com.bumptech.glide.load.model.ModelLoader<Model, Data> build(@android.support.annotation.NonNull java.lang.Class<Model> r6, @android.support.annotation.NonNull java.lang.Class<Data> r7) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:40:0x0076 in {7, 10, 11, 12, 17, 23, 28, 31, 37, 39} preds:[]
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
        r5 = this;
        monitor-enter(r5);
        r0 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x006d }
        r0.<init>();	 Catch:{ Throwable -> 0x006d }
        r1 = 0;	 Catch:{ Throwable -> 0x006d }
        r2 = r5.entries;	 Catch:{ Throwable -> 0x006d }
        r2 = r2.iterator();	 Catch:{ Throwable -> 0x006d }
    L_0x000d:
        r3 = r2.hasNext();	 Catch:{ Throwable -> 0x006d }
        if (r3 == 0) goto L_0x003d;	 Catch:{ Throwable -> 0x006d }
    L_0x0013:
        r3 = r2.next();	 Catch:{ Throwable -> 0x006d }
        r3 = (com.bumptech.glide.load.model.MultiModelLoaderFactory.Entry) r3;	 Catch:{ Throwable -> 0x006d }
        r4 = r5.alreadyUsedEntries;	 Catch:{ Throwable -> 0x006d }
        r4 = r4.contains(r3);	 Catch:{ Throwable -> 0x006d }
        if (r4 == 0) goto L_0x0023;	 Catch:{ Throwable -> 0x006d }
    L_0x0021:
        r1 = 1;	 Catch:{ Throwable -> 0x006d }
        goto L_0x000d;	 Catch:{ Throwable -> 0x006d }
    L_0x0023:
        r4 = r3.handles(r6, r7);	 Catch:{ Throwable -> 0x006d }
        if (r4 == 0) goto L_0x003b;	 Catch:{ Throwable -> 0x006d }
    L_0x0029:
        r4 = r5.alreadyUsedEntries;	 Catch:{ Throwable -> 0x006d }
        r4.add(r3);	 Catch:{ Throwable -> 0x006d }
        r4 = r5.build(r3);	 Catch:{ Throwable -> 0x006d }
        r0.add(r4);	 Catch:{ Throwable -> 0x006d }
        r4 = r5.alreadyUsedEntries;	 Catch:{ Throwable -> 0x006d }
        r4.remove(r3);	 Catch:{ Throwable -> 0x006d }
        goto L_0x003c;	 Catch:{ Throwable -> 0x006d }
    L_0x003c:
        goto L_0x000d;	 Catch:{ Throwable -> 0x006d }
    L_0x003d:
        r2 = r0.size();	 Catch:{ Throwable -> 0x006d }
        r3 = 1;	 Catch:{ Throwable -> 0x006d }
        if (r2 <= r3) goto L_0x004e;	 Catch:{ Throwable -> 0x006d }
    L_0x0044:
        r2 = r5.factory;	 Catch:{ Throwable -> 0x006d }
        r3 = r5.throwableListPool;	 Catch:{ Throwable -> 0x006d }
        r2 = r2.build(r0, r3);	 Catch:{ Throwable -> 0x006d }
        monitor-exit(r5);
        return r2;
    L_0x004e:
        r2 = r0.size();	 Catch:{ Throwable -> 0x006d }
        if (r2 != r3) goto L_0x005d;	 Catch:{ Throwable -> 0x006d }
    L_0x0054:
        r2 = 0;	 Catch:{ Throwable -> 0x006d }
        r2 = r0.get(r2);	 Catch:{ Throwable -> 0x006d }
        r2 = (com.bumptech.glide.load.model.ModelLoader) r2;	 Catch:{ Throwable -> 0x006d }
        monitor-exit(r5);
        return r2;
    L_0x005d:
        if (r1 == 0) goto L_0x0065;
    L_0x005f:
        r2 = emptyModelLoader();	 Catch:{ Throwable -> 0x006d }
        monitor-exit(r5);
        return r2;
    L_0x0065:
        r2 = new com.bumptech.glide.Registry$NoModelLoaderAvailableException;	 Catch:{ Throwable -> 0x006d }
        r2.<init>(r6, r7);	 Catch:{ Throwable -> 0x006d }
        throw r2;	 Catch:{ Throwable -> 0x006d }
    L_0x006b:
        r6 = move-exception;
        goto L_0x0074;
    L_0x006d:
        r0 = move-exception;
        r1 = r5.alreadyUsedEntries;	 Catch:{ all -> 0x006b }
        r1.clear();	 Catch:{ all -> 0x006b }
        throw r0;	 Catch:{ all -> 0x006b }
    L_0x0074:
        monitor-exit(r5);
        throw r6;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.load.model.MultiModelLoaderFactory.build(java.lang.Class, java.lang.Class):com.bumptech.glide.load.model.ModelLoader<Model, Data>");
    }

    @android.support.annotation.NonNull
    synchronized <Model> java.util.List<com.bumptech.glide.load.model.ModelLoader<Model, ?>> build(@android.support.annotation.NonNull java.lang.Class<Model> r5) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:24:0x0048 in {7, 11, 12, 13, 15, 21, 23} preds:[]
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
        r4 = this;
        monitor-enter(r4);
        r0 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x003f }
        r0.<init>();	 Catch:{ Throwable -> 0x003f }
        r1 = r4.entries;	 Catch:{ Throwable -> 0x003f }
        r1 = r1.iterator();	 Catch:{ Throwable -> 0x003f }
    L_0x000c:
        r2 = r1.hasNext();	 Catch:{ Throwable -> 0x003f }
        if (r2 == 0) goto L_0x003b;	 Catch:{ Throwable -> 0x003f }
    L_0x0012:
        r2 = r1.next();	 Catch:{ Throwable -> 0x003f }
        r2 = (com.bumptech.glide.load.model.MultiModelLoaderFactory.Entry) r2;	 Catch:{ Throwable -> 0x003f }
        r3 = r4.alreadyUsedEntries;	 Catch:{ Throwable -> 0x003f }
        r3 = r3.contains(r2);	 Catch:{ Throwable -> 0x003f }
        if (r3 == 0) goto L_0x0021;	 Catch:{ Throwable -> 0x003f }
    L_0x0020:
        goto L_0x000c;	 Catch:{ Throwable -> 0x003f }
    L_0x0021:
        r3 = r2.handles(r5);	 Catch:{ Throwable -> 0x003f }
        if (r3 == 0) goto L_0x0039;	 Catch:{ Throwable -> 0x003f }
    L_0x0027:
        r3 = r4.alreadyUsedEntries;	 Catch:{ Throwable -> 0x003f }
        r3.add(r2);	 Catch:{ Throwable -> 0x003f }
        r3 = r4.build(r2);	 Catch:{ Throwable -> 0x003f }
        r0.add(r3);	 Catch:{ Throwable -> 0x003f }
        r3 = r4.alreadyUsedEntries;	 Catch:{ Throwable -> 0x003f }
        r3.remove(r2);	 Catch:{ Throwable -> 0x003f }
        goto L_0x003a;
    L_0x003a:
        goto L_0x000c;
    L_0x003b:
        monitor-exit(r4);
        return r0;
    L_0x003d:
        r5 = move-exception;
        goto L_0x0046;
    L_0x003f:
        r0 = move-exception;
        r1 = r4.alreadyUsedEntries;	 Catch:{ all -> 0x003d }
        r1.clear();	 Catch:{ all -> 0x003d }
        throw r0;	 Catch:{ all -> 0x003d }
    L_0x0046:
        monitor-exit(r4);
        throw r5;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.load.model.MultiModelLoaderFactory.build(java.lang.Class):java.util.List<com.bumptech.glide.load.model.ModelLoader<Model, ?>>");
    }

    @android.support.annotation.NonNull
    synchronized java.util.List<java.lang.Class<?>> getDataClasses(@android.support.annotation.NonNull java.lang.Class<?> r5) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:18:0x0033 in {10, 11, 12, 14, 17} preds:[]
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
        r4 = this;
        monitor-enter(r4);
        r0 = new java.util.ArrayList;	 Catch:{ all -> 0x0030 }
        r0.<init>();	 Catch:{ all -> 0x0030 }
        r1 = r4.entries;	 Catch:{ all -> 0x0030 }
        r1 = r1.iterator();	 Catch:{ all -> 0x0030 }
    L_0x000c:
        r2 = r1.hasNext();	 Catch:{ all -> 0x0030 }
        if (r2 == 0) goto L_0x002e;	 Catch:{ all -> 0x0030 }
    L_0x0012:
        r2 = r1.next();	 Catch:{ all -> 0x0030 }
        r2 = (com.bumptech.glide.load.model.MultiModelLoaderFactory.Entry) r2;	 Catch:{ all -> 0x0030 }
        r3 = r2.dataClass;	 Catch:{ all -> 0x0030 }
        r3 = r0.contains(r3);	 Catch:{ all -> 0x0030 }
        if (r3 != 0) goto L_0x002c;	 Catch:{ all -> 0x0030 }
    L_0x0020:
        r3 = r2.handles(r5);	 Catch:{ all -> 0x0030 }
        if (r3 == 0) goto L_0x002c;	 Catch:{ all -> 0x0030 }
    L_0x0026:
        r3 = r2.dataClass;	 Catch:{ all -> 0x0030 }
        r0.add(r3);	 Catch:{ all -> 0x0030 }
        goto L_0x002d;
    L_0x002d:
        goto L_0x000c;
    L_0x002e:
        monitor-exit(r4);
        return r0;
    L_0x0030:
        r5 = move-exception;
        monitor-exit(r4);
        throw r5;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.load.model.MultiModelLoaderFactory.getDataClasses(java.lang.Class):java.util.List<java.lang.Class<?>>");
    }

    @android.support.annotation.NonNull
    synchronized <Model, Data> java.util.List<com.bumptech.glide.load.model.ModelLoaderFactory<? extends Model, ? extends Data>> remove(@android.support.annotation.NonNull java.lang.Class<Model> r5, @android.support.annotation.NonNull java.lang.Class<Data> r6) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:17:0x0031 in {8, 9, 10, 13, 16} preds:[]
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
        r4 = this;
        monitor-enter(r4);
        r0 = new java.util.ArrayList;	 Catch:{ all -> 0x002e }
        r0.<init>();	 Catch:{ all -> 0x002e }
        r1 = r4.entries;	 Catch:{ all -> 0x002e }
        r1 = r1.iterator();	 Catch:{ all -> 0x002e }
    L_0x000c:
        r2 = r1.hasNext();	 Catch:{ all -> 0x002e }
        if (r2 == 0) goto L_0x002b;	 Catch:{ all -> 0x002e }
    L_0x0012:
        r2 = r1.next();	 Catch:{ all -> 0x002e }
        r2 = (com.bumptech.glide.load.model.MultiModelLoaderFactory.Entry) r2;	 Catch:{ all -> 0x002e }
        r3 = r2.handles(r5, r6);	 Catch:{ all -> 0x002e }
        if (r3 == 0) goto L_0x0029;	 Catch:{ all -> 0x002e }
    L_0x001e:
        r1.remove();	 Catch:{ all -> 0x002e }
        r3 = r4.getFactory(r2);	 Catch:{ all -> 0x002e }
        r0.add(r3);	 Catch:{ all -> 0x002e }
        goto L_0x002a;
    L_0x002a:
        goto L_0x000c;
        monitor-exit(r4);
        return r0;
    L_0x002e:
        r5 = move-exception;
        monitor-exit(r4);
        throw r5;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.load.model.MultiModelLoaderFactory.remove(java.lang.Class, java.lang.Class):java.util.List<com.bumptech.glide.load.model.ModelLoaderFactory<? extends Model, ? extends Data>>");
    }

    public MultiModelLoaderFactory(@NonNull Pool<List<Throwable>> throwableListPool) {
        this(throwableListPool, DEFAULT_FACTORY);
    }

    @VisibleForTesting
    MultiModelLoaderFactory(@NonNull Pool<List<Throwable>> throwableListPool, @NonNull Factory factory) {
        this.entries = new ArrayList();
        this.alreadyUsedEntries = new HashSet();
        this.throwableListPool = throwableListPool;
        this.factory = factory;
    }

    synchronized <Model, Data> void append(@NonNull Class<Model> modelClass, @NonNull Class<Data> dataClass, @NonNull ModelLoaderFactory<? extends Model, ? extends Data> factory) {
        add(modelClass, dataClass, factory, true);
    }

    synchronized <Model, Data> void prepend(@NonNull Class<Model> modelClass, @NonNull Class<Data> dataClass, @NonNull ModelLoaderFactory<? extends Model, ? extends Data> factory) {
        add(modelClass, dataClass, factory, false);
    }

    private <Model, Data> void add(@NonNull Class<Model> modelClass, @NonNull Class<Data> dataClass, @NonNull ModelLoaderFactory<? extends Model, ? extends Data> factory, boolean append) {
        Entry<Model, Data> entry = new Entry(modelClass, dataClass, factory);
        List list = this.entries;
        list.add(append ? list.size() : 0, entry);
    }

    @NonNull
    synchronized <Model, Data> List<ModelLoaderFactory<? extends Model, ? extends Data>> replace(@NonNull Class<Model> modelClass, @NonNull Class<Data> dataClass, @NonNull ModelLoaderFactory<? extends Model, ? extends Data> factory) {
        List<ModelLoaderFactory<? extends Model, ? extends Data>> removed;
        removed = remove(modelClass, dataClass);
        append(modelClass, dataClass, factory);
        return removed;
    }

    @NonNull
    private <Model, Data> ModelLoaderFactory<Model, Data> getFactory(@NonNull Entry<?, ?> entry) {
        return entry.factory;
    }

    @NonNull
    private <Model, Data> ModelLoader<Model, Data> build(@NonNull Entry<?, ?> entry) {
        return (ModelLoader) Preconditions.checkNotNull(entry.factory.build(this));
    }

    @NonNull
    private static <Model, Data> ModelLoader<Model, Data> emptyModelLoader() {
        return EMPTY_MODEL_LOADER;
    }
}
