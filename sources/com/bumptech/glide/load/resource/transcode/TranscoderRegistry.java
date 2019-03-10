package com.bumptech.glide.load.resource.transcode;

import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

public class TranscoderRegistry {
    private final List<Entry<?, ?>> transcoders = new ArrayList();

    private static final class Entry<Z, R> {
        private final Class<Z> fromClass;
        private final Class<R> toClass;
        final ResourceTranscoder<Z, R> transcoder;

        Entry(@NonNull Class<Z> fromClass, @NonNull Class<R> toClass, @NonNull ResourceTranscoder<Z, R> transcoder) {
            this.fromClass = fromClass;
            this.toClass = toClass;
            this.transcoder = transcoder;
        }

        public boolean handles(@NonNull Class<?> fromClass, @NonNull Class<?> toClass) {
            return this.fromClass.isAssignableFrom(fromClass) && toClass.isAssignableFrom(this.toClass);
        }
    }

    @android.support.annotation.NonNull
    public synchronized <Z, R> com.bumptech.glide.load.resource.transcode.ResourceTranscoder<Z, R> get(@android.support.annotation.NonNull java.lang.Class<Z> r4, @android.support.annotation.NonNull java.lang.Class<R> r5) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:23:0x004d in {6, 15, 16, 19, 22} preds:[]
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
        r3 = this;
        monitor-enter(r3);
        r0 = r5.isAssignableFrom(r4);	 Catch:{ all -> 0x004a }
        if (r0 == 0) goto L_0x000d;	 Catch:{ all -> 0x004a }
    L_0x0007:
        r0 = com.bumptech.glide.load.resource.transcode.UnitTranscoder.get();	 Catch:{ all -> 0x004a }
        monitor-exit(r3);
        return r0;
    L_0x000d:
        r0 = r3.transcoders;	 Catch:{ all -> 0x004a }
        r0 = r0.iterator();	 Catch:{ all -> 0x004a }
    L_0x0013:
        r1 = r0.hasNext();	 Catch:{ all -> 0x004a }
        if (r1 == 0) goto L_0x002b;	 Catch:{ all -> 0x004a }
    L_0x0019:
        r1 = r0.next();	 Catch:{ all -> 0x004a }
        r1 = (com.bumptech.glide.load.resource.transcode.TranscoderRegistry.Entry) r1;	 Catch:{ all -> 0x004a }
        r2 = r1.handles(r4, r5);	 Catch:{ all -> 0x004a }
        if (r2 == 0) goto L_0x0029;	 Catch:{ all -> 0x004a }
    L_0x0025:
        r0 = r1.transcoder;	 Catch:{ all -> 0x004a }
        monitor-exit(r3);
        return r0;
        goto L_0x0013;
    L_0x002b:
        r0 = new java.lang.IllegalArgumentException;	 Catch:{ all -> 0x004a }
        r1 = new java.lang.StringBuilder;	 Catch:{ all -> 0x004a }
        r1.<init>();	 Catch:{ all -> 0x004a }
        r2 = "No transcoder registered to transcode from ";	 Catch:{ all -> 0x004a }
        r1.append(r2);	 Catch:{ all -> 0x004a }
        r1.append(r4);	 Catch:{ all -> 0x004a }
        r2 = " to ";	 Catch:{ all -> 0x004a }
        r1.append(r2);	 Catch:{ all -> 0x004a }
        r1.append(r5);	 Catch:{ all -> 0x004a }
        r1 = r1.toString();	 Catch:{ all -> 0x004a }
        r0.<init>(r1);	 Catch:{ all -> 0x004a }
        throw r0;	 Catch:{ all -> 0x004a }
    L_0x004a:
        r4 = move-exception;
        monitor-exit(r3);
        throw r4;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.load.resource.transcode.TranscoderRegistry.get(java.lang.Class, java.lang.Class):com.bumptech.glide.load.resource.transcode.ResourceTranscoder<Z, R>");
    }

    @android.support.annotation.NonNull
    public synchronized <Z, R> java.util.List<java.lang.Class<R>> getTranscodeClasses(@android.support.annotation.NonNull java.lang.Class<Z> r5, @android.support.annotation.NonNull java.lang.Class<R> r6) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:22:0x0034 in {6, 14, 15, 16, 18, 21} preds:[]
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
        r0 = new java.util.ArrayList;	 Catch:{ all -> 0x0031 }
        r0.<init>();	 Catch:{ all -> 0x0031 }
        r1 = r6.isAssignableFrom(r5);	 Catch:{ all -> 0x0031 }
        if (r1 == 0) goto L_0x0011;	 Catch:{ all -> 0x0031 }
    L_0x000c:
        r0.add(r6);	 Catch:{ all -> 0x0031 }
        monitor-exit(r4);
        return r0;
    L_0x0011:
        r1 = r4.transcoders;	 Catch:{ all -> 0x0031 }
        r1 = r1.iterator();	 Catch:{ all -> 0x0031 }
    L_0x0017:
        r2 = r1.hasNext();	 Catch:{ all -> 0x0031 }
        if (r2 == 0) goto L_0x002f;	 Catch:{ all -> 0x0031 }
    L_0x001d:
        r2 = r1.next();	 Catch:{ all -> 0x0031 }
        r2 = (com.bumptech.glide.load.resource.transcode.TranscoderRegistry.Entry) r2;	 Catch:{ all -> 0x0031 }
        r3 = r2.handles(r5, r6);	 Catch:{ all -> 0x0031 }
        if (r3 == 0) goto L_0x002d;	 Catch:{ all -> 0x0031 }
    L_0x0029:
        r0.add(r6);	 Catch:{ all -> 0x0031 }
        goto L_0x002e;
    L_0x002e:
        goto L_0x0017;
    L_0x002f:
        monitor-exit(r4);
        return r0;
    L_0x0031:
        r5 = move-exception;
        monitor-exit(r4);
        throw r5;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.load.resource.transcode.TranscoderRegistry.getTranscodeClasses(java.lang.Class, java.lang.Class):java.util.List<java.lang.Class<R>>");
    }

    public synchronized <Z, R> void register(@NonNull Class<Z> decodedClass, @NonNull Class<R> transcodedClass, @NonNull ResourceTranscoder<Z, R> transcoder) {
        this.transcoders.add(new Entry(decodedClass, transcodedClass, transcoder));
    }
}
