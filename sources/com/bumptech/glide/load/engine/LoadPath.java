package com.bumptech.glide.load.engine;

import android.support.annotation.NonNull;
import android.support.v4.util.Pools.Pool;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataRewinder;
import com.bumptech.glide.util.Preconditions;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class LoadPath<Data, ResourceType, Transcode> {
    private final Class<Data> dataClass;
    private final List<? extends DecodePath<Data, ResourceType, Transcode>> decodePaths;
    private final String failureMessage;
    private final Pool<List<Throwable>> listPool;

    private com.bumptech.glide.load.engine.Resource<Transcode> loadWithExceptionList(com.bumptech.glide.load.data.DataRewinder<Data> r14, @android.support.annotation.NonNull com.bumptech.glide.load.Options r15, int r16, int r17, com.bumptech.glide.load.engine.DecodePath.DecodeCallback<ResourceType> r18, java.util.List<java.lang.Throwable> r19) throws com.bumptech.glide.load.engine.GlideException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:15:0x0043 in {5, 7, 9, 10, 12, 14} preds:[]
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
        r13 = this;
        r1 = r13;
        r2 = r19;
        r0 = 0;
        r3 = 0;
        r4 = r1.decodePaths;
        r4 = r4.size();
        r5 = r0;
    L_0x000c:
        if (r3 >= r4) goto L_0x0033;
    L_0x000e:
        r0 = r1.decodePaths;
        r0 = r0.get(r3);
        r12 = r0;
        r12 = (com.bumptech.glide.load.engine.DecodePath) r12;
        r6 = r12;
        r7 = r14;
        r8 = r16;
        r9 = r17;
        r10 = r15;
        r11 = r18;
        r0 = r6.decode(r7, r8, r9, r10, r11);	 Catch:{ GlideException -> 0x0026 }
        r5 = r0;
        goto L_0x002c;
    L_0x0026:
        r0 = move-exception;
        r6 = r0;
        r0 = r6;
        r2.add(r0);
    L_0x002c:
        if (r5 == 0) goto L_0x002f;
    L_0x002e:
        goto L_0x0033;
        r3 = r3 + 1;
        goto L_0x000c;
    L_0x0033:
        if (r5 == 0) goto L_0x0036;
    L_0x0035:
        return r5;
    L_0x0036:
        r0 = new com.bumptech.glide.load.engine.GlideException;
        r3 = r1.failureMessage;
        r4 = new java.util.ArrayList;
        r4.<init>(r2);
        r0.<init>(r3, r4);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.load.engine.LoadPath.loadWithExceptionList(com.bumptech.glide.load.data.DataRewinder, com.bumptech.glide.load.Options, int, int, com.bumptech.glide.load.engine.DecodePath$DecodeCallback, java.util.List):com.bumptech.glide.load.engine.Resource<Transcode>");
    }

    public LoadPath(Class<Data> dataClass, Class<ResourceType> resourceClass, Class<Transcode> transcodeClass, List<DecodePath<Data, ResourceType, Transcode>> decodePaths, Pool<List<Throwable>> listPool) {
        this.dataClass = dataClass;
        this.listPool = listPool;
        this.decodePaths = (List) Preconditions.checkNotEmpty((Collection) decodePaths);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Failed LoadPath{");
        stringBuilder.append(dataClass.getSimpleName());
        stringBuilder.append("->");
        stringBuilder.append(resourceClass.getSimpleName());
        stringBuilder.append("->");
        stringBuilder.append(transcodeClass.getSimpleName());
        stringBuilder.append("}");
        this.failureMessage = stringBuilder.toString();
    }

    public Resource<Transcode> load(DataRewinder<Data> rewinder, @NonNull Options options, int width, int height, DecodeCallback<ResourceType> decodeCallback) throws GlideException {
        List<Throwable> throwables = (List) Preconditions.checkNotNull(this.listPool.acquire());
        try {
            Resource<Transcode> loadWithExceptionList = loadWithExceptionList(rewinder, options, width, height, decodeCallback, throwables);
            return loadWithExceptionList;
        } finally {
            this.listPool.release(throwables);
        }
    }

    public Class<Data> getDataClass() {
        return this.dataClass;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("LoadPath{decodePaths=");
        stringBuilder.append(Arrays.toString(this.decodePaths.toArray()));
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
