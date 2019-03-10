package com.bumptech.glide.provider;

import android.support.annotation.NonNull;
import com.bumptech.glide.load.ResourceDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceDecoderRegistry {
    private final List<String> bucketPriorityList = new ArrayList();
    private final Map<String, List<Entry<?, ?>>> decoders = new HashMap();

    private static class Entry<T, R> {
        private final Class<T> dataClass;
        final ResourceDecoder<T, R> decoder;
        final Class<R> resourceClass;

        public Entry(@NonNull Class<T> dataClass, @NonNull Class<R> resourceClass, ResourceDecoder<T, R> decoder) {
            this.dataClass = dataClass;
            this.resourceClass = resourceClass;
            this.decoder = decoder;
        }

        public boolean handles(@NonNull Class<?> dataClass, @NonNull Class<?> resourceClass) {
            if (this.dataClass.isAssignableFrom(dataClass)) {
                if (resourceClass.isAssignableFrom(this.resourceClass)) {
                    return true;
                }
            }
            return false;
        }
    }

    @android.support.annotation.NonNull
    public synchronized <T, R> java.util.List<com.bumptech.glide.load.ResourceDecoder<T, R>> getDecoders(@android.support.annotation.NonNull java.lang.Class<T> r8, @android.support.annotation.NonNull java.lang.Class<R> r9) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:23:0x0048 in {7, 14, 15, 16, 17, 19, 22} preds:[]
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
        r0 = new java.util.ArrayList;	 Catch:{ all -> 0x0045 }
        r0.<init>();	 Catch:{ all -> 0x0045 }
        r1 = r7.bucketPriorityList;	 Catch:{ all -> 0x0045 }
        r1 = r1.iterator();	 Catch:{ all -> 0x0045 }
    L_0x000c:
        r2 = r1.hasNext();	 Catch:{ all -> 0x0045 }
        if (r2 == 0) goto L_0x0043;	 Catch:{ all -> 0x0045 }
    L_0x0012:
        r2 = r1.next();	 Catch:{ all -> 0x0045 }
        r2 = (java.lang.String) r2;	 Catch:{ all -> 0x0045 }
        r3 = r7.decoders;	 Catch:{ all -> 0x0045 }
        r3 = r3.get(r2);	 Catch:{ all -> 0x0045 }
        r3 = (java.util.List) r3;	 Catch:{ all -> 0x0045 }
        if (r3 != 0) goto L_0x0023;	 Catch:{ all -> 0x0045 }
    L_0x0022:
        goto L_0x000c;	 Catch:{ all -> 0x0045 }
    L_0x0023:
        r4 = r3.iterator();	 Catch:{ all -> 0x0045 }
    L_0x0027:
        r5 = r4.hasNext();	 Catch:{ all -> 0x0045 }
        if (r5 == 0) goto L_0x0041;	 Catch:{ all -> 0x0045 }
    L_0x002d:
        r5 = r4.next();	 Catch:{ all -> 0x0045 }
        r5 = (com.bumptech.glide.provider.ResourceDecoderRegistry.Entry) r5;	 Catch:{ all -> 0x0045 }
        r6 = r5.handles(r8, r9);	 Catch:{ all -> 0x0045 }
        if (r6 == 0) goto L_0x003f;	 Catch:{ all -> 0x0045 }
    L_0x0039:
        r6 = r5.decoder;	 Catch:{ all -> 0x0045 }
        r0.add(r6);	 Catch:{ all -> 0x0045 }
        goto L_0x0040;
    L_0x0040:
        goto L_0x0027;
        goto L_0x000c;
    L_0x0043:
        monitor-exit(r7);
        return r0;
    L_0x0045:
        r8 = move-exception;
        monitor-exit(r7);
        throw r8;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.provider.ResourceDecoderRegistry.getDecoders(java.lang.Class, java.lang.Class):java.util.List<com.bumptech.glide.load.ResourceDecoder<T, R>>");
    }

    @android.support.annotation.NonNull
    public synchronized <T, R> java.util.List<java.lang.Class<R>> getResourceClasses(@android.support.annotation.NonNull java.lang.Class<T> r8, @android.support.annotation.NonNull java.lang.Class<R> r9) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:26:0x0051 in {7, 16, 17, 18, 19, 20, 22, 25} preds:[]
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
        r0 = new java.util.ArrayList;	 Catch:{ all -> 0x004e }
        r0.<init>();	 Catch:{ all -> 0x004e }
        r1 = r7.bucketPriorityList;	 Catch:{ all -> 0x004e }
        r1 = r1.iterator();	 Catch:{ all -> 0x004e }
    L_0x000c:
        r2 = r1.hasNext();	 Catch:{ all -> 0x004e }
        if (r2 == 0) goto L_0x004c;	 Catch:{ all -> 0x004e }
    L_0x0012:
        r2 = r1.next();	 Catch:{ all -> 0x004e }
        r2 = (java.lang.String) r2;	 Catch:{ all -> 0x004e }
        r3 = r7.decoders;	 Catch:{ all -> 0x004e }
        r3 = r3.get(r2);	 Catch:{ all -> 0x004e }
        r3 = (java.util.List) r3;	 Catch:{ all -> 0x004e }
        if (r3 != 0) goto L_0x0023;	 Catch:{ all -> 0x004e }
    L_0x0022:
        goto L_0x000c;	 Catch:{ all -> 0x004e }
    L_0x0023:
        r4 = r3.iterator();	 Catch:{ all -> 0x004e }
    L_0x0027:
        r5 = r4.hasNext();	 Catch:{ all -> 0x004e }
        if (r5 == 0) goto L_0x004a;	 Catch:{ all -> 0x004e }
    L_0x002d:
        r5 = r4.next();	 Catch:{ all -> 0x004e }
        r5 = (com.bumptech.glide.provider.ResourceDecoderRegistry.Entry) r5;	 Catch:{ all -> 0x004e }
        r6 = r5.handles(r8, r9);	 Catch:{ all -> 0x004e }
        if (r6 == 0) goto L_0x0048;	 Catch:{ all -> 0x004e }
    L_0x0039:
        r6 = r5.resourceClass;	 Catch:{ all -> 0x004e }
        r6 = r0.contains(r6);	 Catch:{ all -> 0x004e }
        if (r6 != 0) goto L_0x0047;	 Catch:{ all -> 0x004e }
    L_0x0041:
        r6 = r5.resourceClass;	 Catch:{ all -> 0x004e }
        r0.add(r6);	 Catch:{ all -> 0x004e }
        goto L_0x0049;
    L_0x0047:
        goto L_0x0049;
    L_0x0049:
        goto L_0x0027;
        goto L_0x000c;
    L_0x004c:
        monitor-exit(r7);
        return r0;
    L_0x004e:
        r8 = move-exception;
        monitor-exit(r7);
        throw r8;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.provider.ResourceDecoderRegistry.getResourceClasses(java.lang.Class, java.lang.Class):java.util.List<java.lang.Class<R>>");
    }

    public synchronized void setBucketPriorityList(@android.support.annotation.NonNull java.util.List<java.lang.String> r5) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:16:0x0035 in {8, 9, 10, 12, 15} preds:[]
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
        r0 = new java.util.ArrayList;	 Catch:{ all -> 0x0032 }
        r1 = r4.bucketPriorityList;	 Catch:{ all -> 0x0032 }
        r0.<init>(r1);	 Catch:{ all -> 0x0032 }
        r1 = r4.bucketPriorityList;	 Catch:{ all -> 0x0032 }
        r1.clear();	 Catch:{ all -> 0x0032 }
        r1 = r4.bucketPriorityList;	 Catch:{ all -> 0x0032 }
        r1.addAll(r5);	 Catch:{ all -> 0x0032 }
        r1 = r0.iterator();	 Catch:{ all -> 0x0032 }
    L_0x0016:
        r2 = r1.hasNext();	 Catch:{ all -> 0x0032 }
        if (r2 == 0) goto L_0x0030;	 Catch:{ all -> 0x0032 }
    L_0x001c:
        r2 = r1.next();	 Catch:{ all -> 0x0032 }
        r2 = (java.lang.String) r2;	 Catch:{ all -> 0x0032 }
        r3 = r5.contains(r2);	 Catch:{ all -> 0x0032 }
        if (r3 != 0) goto L_0x002e;	 Catch:{ all -> 0x0032 }
    L_0x0028:
        r3 = r4.bucketPriorityList;	 Catch:{ all -> 0x0032 }
        r3.add(r2);	 Catch:{ all -> 0x0032 }
        goto L_0x002f;
    L_0x002f:
        goto L_0x0016;
    L_0x0030:
        monitor-exit(r4);
        return;
    L_0x0032:
        r5 = move-exception;
        monitor-exit(r4);
        throw r5;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.provider.ResourceDecoderRegistry.setBucketPriorityList(java.util.List):void");
    }

    public synchronized <T, R> void append(@NonNull String bucket, @NonNull ResourceDecoder<T, R> decoder, @NonNull Class<T> dataClass, @NonNull Class<R> resourceClass) {
        getOrAddEntryList(bucket).add(new Entry(dataClass, resourceClass, decoder));
    }

    public synchronized <T, R> void prepend(@NonNull String bucket, @NonNull ResourceDecoder<T, R> decoder, @NonNull Class<T> dataClass, @NonNull Class<R> resourceClass) {
        getOrAddEntryList(bucket).add(0, new Entry(dataClass, resourceClass, decoder));
    }

    @NonNull
    private synchronized List<Entry<?, ?>> getOrAddEntryList(@NonNull String bucket) {
        List<Entry<?, ?>> entries;
        if (!this.bucketPriorityList.contains(bucket)) {
            this.bucketPriorityList.add(bucket);
        }
        entries = (List) this.decoders.get(bucket);
        if (entries == null) {
            entries = new ArrayList();
            this.decoders.put(bucket, entries);
        }
        return entries;
    }
}
