package com.bumptech.glide.provider;

import android.support.annotation.NonNull;
import com.bumptech.glide.load.ResourceEncoder;
import java.util.ArrayList;
import java.util.List;

public class ResourceEncoderRegistry {
    private final List<Entry<?>> encoders = new ArrayList();

    private static final class Entry<T> {
        final ResourceEncoder<T> encoder;
        private final Class<T> resourceClass;

        Entry(@NonNull Class<T> resourceClass, @NonNull ResourceEncoder<T> encoder) {
            this.resourceClass = resourceClass;
            this.encoder = encoder;
        }

        boolean handles(@NonNull Class<?> resourceClass) {
            return this.resourceClass.isAssignableFrom(resourceClass);
        }
    }

    @android.support.annotation.Nullable
    public synchronized <Z> com.bumptech.glide.load.ResourceEncoder<Z> get(@android.support.annotation.NonNull java.lang.Class<Z> r5) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:17:0x0026 in {9, 10, 13, 16} preds:[]
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
        r0 = 0;
        r1 = r4.encoders;	 Catch:{ all -> 0x0023 }
        r1 = r1.size();	 Catch:{ all -> 0x0023 }
    L_0x0008:
        if (r0 >= r1) goto L_0x0020;	 Catch:{ all -> 0x0023 }
    L_0x000a:
        r2 = r4.encoders;	 Catch:{ all -> 0x0023 }
        r2 = r2.get(r0);	 Catch:{ all -> 0x0023 }
        r2 = (com.bumptech.glide.provider.ResourceEncoderRegistry.Entry) r2;	 Catch:{ all -> 0x0023 }
        r3 = r2.handles(r5);	 Catch:{ all -> 0x0023 }
        if (r3 == 0) goto L_0x001c;	 Catch:{ all -> 0x0023 }
    L_0x0018:
        r3 = r2.encoder;	 Catch:{ all -> 0x0023 }
        monitor-exit(r4);
        return r3;
        r0 = r0 + 1;
        goto L_0x0008;
    L_0x0020:
        r0 = 0;
        monitor-exit(r4);
        return r0;
    L_0x0023:
        r5 = move-exception;
        monitor-exit(r4);
        throw r5;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.provider.ResourceEncoderRegistry.get(java.lang.Class):com.bumptech.glide.load.ResourceEncoder<Z>");
    }

    public synchronized <Z> void append(@NonNull Class<Z> resourceClass, @NonNull ResourceEncoder<Z> encoder) {
        this.encoders.add(new Entry(resourceClass, encoder));
    }

    public synchronized <Z> void prepend(@NonNull Class<Z> resourceClass, @NonNull ResourceEncoder<Z> encoder) {
        this.encoders.add(0, new Entry(resourceClass, encoder));
    }
}
