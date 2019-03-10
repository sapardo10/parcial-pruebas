package com.bumptech.glide.load.data;

import android.support.annotation.NonNull;
import com.bumptech.glide.load.data.DataRewinder.Factory;
import java.util.HashMap;
import java.util.Map;

public class DataRewinderRegistry {
    private static final Factory<?> DEFAULT_FACTORY = new C09411();
    private final Map<Class<?>, Factory<?>> rewinders = new HashMap();

    /* renamed from: com.bumptech.glide.load.data.DataRewinderRegistry$1 */
    class C09411 implements Factory<Object> {
        C09411() {
        }

        @NonNull
        public DataRewinder<Object> build(@NonNull Object data) {
            return new DefaultRewinder(data);
        }

        @NonNull
        public Class<Object> getDataClass() {
            throw new UnsupportedOperationException("Not implemented");
        }
    }

    private static final class DefaultRewinder implements DataRewinder<Object> {
        private final Object data;

        DefaultRewinder(@NonNull Object data) {
            this.data = data;
        }

        @NonNull
        public Object rewindAndGet() {
            return this.data;
        }

        public void cleanup() {
        }
    }

    @android.support.annotation.NonNull
    public synchronized <T> com.bumptech.glide.load.data.DataRewinder<T> build(@android.support.annotation.NonNull T r6) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:22:0x004c in {9, 10, 11, 12, 14, 15, 18, 21} preds:[]
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
        com.bumptech.glide.util.Preconditions.checkNotNull(r6);	 Catch:{ all -> 0x0049 }
        r0 = r5.rewinders;	 Catch:{ all -> 0x0049 }
        r1 = r6.getClass();	 Catch:{ all -> 0x0049 }
        r0 = r0.get(r1);	 Catch:{ all -> 0x0049 }
        r0 = (com.bumptech.glide.load.data.DataRewinder.Factory) r0;	 Catch:{ all -> 0x0049 }
        if (r0 != 0) goto L_0x003b;	 Catch:{ all -> 0x0049 }
    L_0x0012:
        r1 = r5.rewinders;	 Catch:{ all -> 0x0049 }
        r1 = r1.values();	 Catch:{ all -> 0x0049 }
        r1 = r1.iterator();	 Catch:{ all -> 0x0049 }
    L_0x001c:
        r2 = r1.hasNext();	 Catch:{ all -> 0x0049 }
        if (r2 == 0) goto L_0x003a;	 Catch:{ all -> 0x0049 }
    L_0x0022:
        r2 = r1.next();	 Catch:{ all -> 0x0049 }
        r2 = (com.bumptech.glide.load.data.DataRewinder.Factory) r2;	 Catch:{ all -> 0x0049 }
        r3 = r2.getDataClass();	 Catch:{ all -> 0x0049 }
        r4 = r6.getClass();	 Catch:{ all -> 0x0049 }
        r3 = r3.isAssignableFrom(r4);	 Catch:{ all -> 0x0049 }
        if (r3 == 0) goto L_0x0038;	 Catch:{ all -> 0x0049 }
    L_0x0036:
        r0 = r2;	 Catch:{ all -> 0x0049 }
        goto L_0x003c;	 Catch:{ all -> 0x0049 }
        goto L_0x001c;	 Catch:{ all -> 0x0049 }
    L_0x003a:
        goto L_0x003c;	 Catch:{ all -> 0x0049 }
    L_0x003c:
        if (r0 != 0) goto L_0x0042;	 Catch:{ all -> 0x0049 }
    L_0x003e:
        r1 = DEFAULT_FACTORY;	 Catch:{ all -> 0x0049 }
        r0 = r1;	 Catch:{ all -> 0x0049 }
        goto L_0x0043;	 Catch:{ all -> 0x0049 }
    L_0x0043:
        r1 = r0.build(r6);	 Catch:{ all -> 0x0049 }
        monitor-exit(r5);
        return r1;
    L_0x0049:
        r6 = move-exception;
        monitor-exit(r5);
        throw r6;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.load.data.DataRewinderRegistry.build(java.lang.Object):com.bumptech.glide.load.data.DataRewinder<T>");
    }

    public synchronized void register(@NonNull Factory<?> factory) {
        this.rewinders.put(factory.getDataClass(), factory);
    }
}
