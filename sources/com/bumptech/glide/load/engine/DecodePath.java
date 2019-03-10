package com.bumptech.glide.load.engine;

import android.support.annotation.NonNull;
import android.support.v4.util.Pools.Pool;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.data.DataRewinder;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;
import com.bumptech.glide.util.Preconditions;
import java.util.List;

public class DecodePath<DataType, ResourceType, Transcode> {
    private static final String TAG = "DecodePath";
    private final Class<DataType> dataClass;
    private final List<? extends ResourceDecoder<DataType, ResourceType>> decoders;
    private final String failureMessage;
    private final Pool<List<Throwable>> listPool;
    private final ResourceTranscoder<ResourceType, Transcode> transcoder;

    interface DecodeCallback<ResourceType> {
        @NonNull
        Resource<ResourceType> onResourceDecoded(@NonNull Resource<ResourceType> resource);
    }

    @android.support.annotation.NonNull
    private com.bumptech.glide.load.engine.Resource<ResourceType> decodeResourceWithList(com.bumptech.glide.load.data.DataRewinder<DataType> r9, int r10, int r11, @android.support.annotation.NonNull com.bumptech.glide.load.Options r12, java.util.List<java.lang.Throwable> r13) throws com.bumptech.glide.load.engine.GlideException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:23:0x0065 in {7, 8, 9, 13, 14, 15, 17, 18, 20, 22} preds:[]
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
        r8 = this;
        r0 = 0;
        r1 = 0;
        r2 = r8.decoders;
        r2 = r2.size();
    L_0x0008:
        if (r1 >= r2) goto L_0x0055;
    L_0x000a:
        r3 = r8.decoders;
        r3 = r3.get(r1);
        r3 = (com.bumptech.glide.load.ResourceDecoder) r3;
        r4 = r9.rewindAndGet();	 Catch:{ IOException -> 0x0029, IOException -> 0x0029, IOException -> 0x0029 }
        r5 = r3.handles(r4, r12);	 Catch:{ IOException -> 0x0029, IOException -> 0x0029, IOException -> 0x0029 }
        if (r5 == 0) goto L_0x0027;	 Catch:{ IOException -> 0x0029, IOException -> 0x0029, IOException -> 0x0029 }
    L_0x001c:
        r5 = r9.rewindAndGet();	 Catch:{ IOException -> 0x0029, IOException -> 0x0029, IOException -> 0x0029 }
        r4 = r5;	 Catch:{ IOException -> 0x0029, IOException -> 0x0029, IOException -> 0x0029 }
        r5 = r3.decode(r4, r10, r11, r12);	 Catch:{ IOException -> 0x0029, IOException -> 0x0029, IOException -> 0x0029 }
        r0 = r5;
        goto L_0x0028;
    L_0x0028:
        goto L_0x004e;
    L_0x0029:
        r4 = move-exception;
        r5 = "DecodePath";
        r6 = 2;
        r5 = android.util.Log.isLoggable(r5, r6);
        if (r5 == 0) goto L_0x004a;
    L_0x0033:
        r5 = "DecodePath";
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r7 = "Failed to decode data for ";
        r6.append(r7);
        r6.append(r3);
        r6 = r6.toString();
        android.util.Log.v(r5, r6, r4);
        goto L_0x004b;
    L_0x004b:
        r13.add(r4);
    L_0x004e:
        if (r0 == 0) goto L_0x0051;
    L_0x0050:
        goto L_0x0055;
        r1 = r1 + 1;
        goto L_0x0008;
    L_0x0055:
        if (r0 == 0) goto L_0x0058;
    L_0x0057:
        return r0;
    L_0x0058:
        r1 = new com.bumptech.glide.load.engine.GlideException;
        r2 = r8.failureMessage;
        r3 = new java.util.ArrayList;
        r3.<init>(r13);
        r1.<init>(r2, r3);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.load.engine.DecodePath.decodeResourceWithList(com.bumptech.glide.load.data.DataRewinder, int, int, com.bumptech.glide.load.Options, java.util.List):com.bumptech.glide.load.engine.Resource<ResourceType>");
    }

    public DecodePath(Class<DataType> dataClass, Class<ResourceType> resourceClass, Class<Transcode> transcodeClass, List<? extends ResourceDecoder<DataType, ResourceType>> decoders, ResourceTranscoder<ResourceType, Transcode> transcoder, Pool<List<Throwable>> listPool) {
        this.dataClass = dataClass;
        this.decoders = decoders;
        this.transcoder = transcoder;
        this.listPool = listPool;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Failed DecodePath{");
        stringBuilder.append(dataClass.getSimpleName());
        stringBuilder.append("->");
        stringBuilder.append(resourceClass.getSimpleName());
        stringBuilder.append("->");
        stringBuilder.append(transcodeClass.getSimpleName());
        stringBuilder.append("}");
        this.failureMessage = stringBuilder.toString();
    }

    public Resource<Transcode> decode(DataRewinder<DataType> rewinder, int width, int height, @NonNull Options options, DecodeCallback<ResourceType> callback) throws GlideException {
        return this.transcoder.transcode(callback.onResourceDecoded(decodeResource(rewinder, width, height, options)), options);
    }

    @NonNull
    private Resource<ResourceType> decodeResource(DataRewinder<DataType> rewinder, int width, int height, @NonNull Options options) throws GlideException {
        List<Throwable> exceptions = (List) Preconditions.checkNotNull(this.listPool.acquire());
        try {
            Resource<ResourceType> decodeResourceWithList = decodeResourceWithList(rewinder, width, height, options, exceptions);
            return decodeResourceWithList;
        } finally {
            this.listPool.release(exceptions);
        }
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("DecodePath{ dataClass=");
        stringBuilder.append(this.dataClass);
        stringBuilder.append(", decoders=");
        stringBuilder.append(this.decoders);
        stringBuilder.append(", transcoder=");
        stringBuilder.append(this.transcoder);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
