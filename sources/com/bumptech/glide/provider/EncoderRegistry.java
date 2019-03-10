package com.bumptech.glide.provider;

import android.support.annotation.NonNull;
import com.bumptech.glide.load.Encoder;
import java.util.ArrayList;
import java.util.List;

public class EncoderRegistry {
    private final List<Entry<?>> encoders = new ArrayList();

    private static final class Entry<T> {
        private final Class<T> dataClass;
        final Encoder<T> encoder;

        Entry(@NonNull Class<T> dataClass, @NonNull Encoder<T> encoder) {
            this.dataClass = dataClass;
            this.encoder = encoder;
        }

        boolean handles(@NonNull Class<?> dataClass) {
            return this.dataClass.isAssignableFrom(dataClass);
        }
    }

    @android.support.annotation.Nullable
    public synchronized <T> com.bumptech.glide.load.Encoder<T> getEncoder(@android.support.annotation.NonNull java.lang.Class<T> r4) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:17:0x0025 in {9, 10, 13, 16} preds:[]
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
        r0 = r3.encoders;	 Catch:{ all -> 0x0022 }
        r0 = r0.iterator();	 Catch:{ all -> 0x0022 }
    L_0x0007:
        r1 = r0.hasNext();	 Catch:{ all -> 0x0022 }
        if (r1 == 0) goto L_0x001f;	 Catch:{ all -> 0x0022 }
    L_0x000d:
        r1 = r0.next();	 Catch:{ all -> 0x0022 }
        r1 = (com.bumptech.glide.provider.EncoderRegistry.Entry) r1;	 Catch:{ all -> 0x0022 }
        r2 = r1.handles(r4);	 Catch:{ all -> 0x0022 }
        if (r2 == 0) goto L_0x001d;	 Catch:{ all -> 0x0022 }
    L_0x0019:
        r0 = r1.encoder;	 Catch:{ all -> 0x0022 }
        monitor-exit(r3);
        return r0;
        goto L_0x0007;
    L_0x001f:
        r0 = 0;
        monitor-exit(r3);
        return r0;
    L_0x0022:
        r4 = move-exception;
        monitor-exit(r3);
        throw r4;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.provider.EncoderRegistry.getEncoder(java.lang.Class):com.bumptech.glide.load.Encoder<T>");
    }

    public synchronized <T> void append(@NonNull Class<T> dataClass, @NonNull Encoder<T> encoder) {
        this.encoders.add(new Entry(dataClass, encoder));
    }

    public synchronized <T> void prepend(@NonNull Class<T> dataClass, @NonNull Encoder<T> encoder) {
        this.encoders.add(0, new Entry(dataClass, encoder));
    }
}
