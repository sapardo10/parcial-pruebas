package com.google.android.exoplayer2.offline;

import android.net.Uri;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class DownloadAction {
    @Nullable
    private static Deserializer[] defaultDeserializers;
    public final byte[] data;
    public final boolean isRemoveAction;
    public final String type;
    public final Uri uri;
    public final int version;

    public static abstract class Deserializer {
        public final String type;
        public final int version;

        public abstract DownloadAction readFromStream(int i, DataInputStream dataInputStream) throws IOException;

        public Deserializer(String type, int version) {
            this.type = type;
            this.version = version;
        }
    }

    public static com.google.android.exoplayer2.offline.DownloadAction deserializeFromStream(com.google.android.exoplayer2.offline.DownloadAction.Deserializer[] r7, java.io.InputStream r8) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:11:0x0047 in {7, 8, 10} preds:[]
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
        r0 = new java.io.DataInputStream;
        r0.<init>(r8);
        r1 = r0.readUTF();
        r2 = r0.readInt();
        r3 = r7.length;
        r4 = 0;
    L_0x000f:
        if (r4 >= r3) goto L_0x0028;
    L_0x0011:
        r5 = r7[r4];
        r6 = r5.type;
        r6 = r1.equals(r6);
        if (r6 == 0) goto L_0x0024;
    L_0x001b:
        r6 = r5.version;
        if (r6 < r2) goto L_0x0024;
    L_0x001f:
        r3 = r5.readFromStream(r2, r0);
        return r3;
        r4 = r4 + 1;
        goto L_0x000f;
    L_0x0028:
        r3 = new com.google.android.exoplayer2.offline.DownloadException;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "No deserializer found for:";
        r4.append(r5);
        r4.append(r1);
        r5 = ", ";
        r4.append(r5);
        r4.append(r2);
        r4 = r4.toString();
        r3.<init>(r4);
        throw r3;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.offline.DownloadAction.deserializeFromStream(com.google.android.exoplayer2.offline.DownloadAction$Deserializer[], java.io.InputStream):com.google.android.exoplayer2.offline.DownloadAction");
    }

    public abstract Downloader createDownloader(DownloaderConstructorHelper downloaderConstructorHelper);

    protected abstract void writeToStream(DataOutputStream dataOutputStream) throws IOException;

    public static synchronized Deserializer[] getDefaultDeserializers() {
        synchronized (DownloadAction.class) {
            if (defaultDeserializers != null) {
                Deserializer[] deserializerArr = defaultDeserializers;
                return deserializerArr;
            }
            int count;
            deserializerArr = new Deserializer[4];
            int count2 = 0 + 1;
            deserializerArr[0] = ProgressiveDownloadAction.DESERIALIZER;
            try {
                count = count2 + 1;
                try {
                    deserializerArr[count2] = getDeserializer(Class.forName("com.google.android.exoplayer2.source.dash.offline.DashDownloadAction"));
                } catch (Exception e) {
                }
            } catch (Exception e2) {
                count = count2;
            }
            try {
                count2 = count + 1;
                try {
                    deserializerArr[count] = getDeserializer(Class.forName("com.google.android.exoplayer2.source.hls.offline.HlsDownloadAction"));
                } catch (Exception e3) {
                }
            } catch (Exception e4) {
                count2 = count;
            }
            try {
                count = count2 + 1;
                try {
                    deserializerArr[count2] = getDeserializer(Class.forName("com.google.android.exoplayer2.source.smoothstreaming.offline.SsDownloadAction"));
                } catch (Exception e5) {
                }
            } catch (Exception e6) {
                count = count2;
            }
            defaultDeserializers = (Deserializer[]) Arrays.copyOf((Object[]) Assertions.checkNotNull(deserializerArr), count);
            Deserializer[] deserializerArr2 = defaultDeserializers;
            return deserializerArr2;
        }
    }

    public static void serializeToStream(DownloadAction action, OutputStream output) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(output);
        dataOutputStream.writeUTF(action.type);
        dataOutputStream.writeInt(action.version);
        action.writeToStream(dataOutputStream);
        dataOutputStream.flush();
    }

    protected DownloadAction(String type, int version, Uri uri, boolean isRemoveAction, @Nullable byte[] data) {
        this.type = type;
        this.version = version;
        this.uri = uri;
        this.isRemoveAction = isRemoveAction;
        this.data = data != null ? data : Util.EMPTY_BYTE_ARRAY;
    }

    public final byte[] toByteArray() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            serializeToStream(this, output);
            return output.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException();
        }
    }

    public boolean isSameMedia(DownloadAction other) {
        return this.uri.equals(other.uri);
    }

    public List<StreamKey> getKeys() {
        return Collections.emptyList();
    }

    public boolean equals(@Nullable Object o) {
        boolean z = false;
        if (o != null) {
            if (getClass() == o.getClass()) {
                DownloadAction that = (DownloadAction) o;
                if (this.type.equals(that.type) && this.version == that.version) {
                    if (this.uri.equals(that.uri) && this.isRemoveAction == that.isRemoveAction) {
                        if (Arrays.equals(this.data, that.data)) {
                            z = true;
                            return z;
                        }
                    }
                }
                return z;
            }
        }
        return false;
    }

    public int hashCode() {
        return (((this.uri.hashCode() * 31) + this.isRemoveAction) * 31) + Arrays.hashCode(this.data);
    }

    private static Deserializer getDeserializer(Class<?> clazz) throws NoSuchFieldException, IllegalAccessException {
        return (Deserializer) Assertions.checkNotNull(clazz.getDeclaredField("DESERIALIZER").get(null));
    }
}
