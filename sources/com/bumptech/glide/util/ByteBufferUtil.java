package com.bumptech.glide.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.concurrent.atomic.AtomicReference;

public final class ByteBufferUtil {
    private static final AtomicReference<byte[]> BUFFER_REF = new AtomicReference();
    private static final int BUFFER_SIZE = 16384;

    private static class ByteBufferStream extends InputStream {
        private static final int UNSET = -1;
        @NonNull
        private final ByteBuffer byteBuffer;
        private int markPos = -1;

        ByteBufferStream(@NonNull ByteBuffer byteBuffer) {
            this.byteBuffer = byteBuffer;
        }

        public int available() {
            return this.byteBuffer.remaining();
        }

        public int read() {
            if (this.byteBuffer.hasRemaining()) {
                return this.byteBuffer.get();
            }
            return -1;
        }

        public synchronized void mark(int readLimit) {
            this.markPos = this.byteBuffer.position();
        }

        public boolean markSupported() {
            return true;
        }

        public int read(@NonNull byte[] buffer, int byteOffset, int byteCount) throws IOException {
            if (!this.byteBuffer.hasRemaining()) {
                return -1;
            }
            int toRead = Math.min(byteCount, available());
            this.byteBuffer.get(buffer, byteOffset, toRead);
            return toRead;
        }

        public synchronized void reset() throws IOException {
            if (this.markPos != -1) {
                this.byteBuffer.position(this.markPos);
            } else {
                throw new IOException("Cannot reset to unset mark position");
            }
        }

        public long skip(long byteCount) throws IOException {
            if (!this.byteBuffer.hasRemaining()) {
                return -1;
            }
            long toSkip = Math.min(byteCount, (long) available());
            ByteBuffer byteBuffer = this.byteBuffer;
            byteBuffer.position((int) (((long) byteBuffer.position()) + toSkip));
            return toSkip;
        }
    }

    static final class SafeArray {
        final byte[] data;
        final int limit;
        final int offset;

        SafeArray(@NonNull byte[] data, int offset, int limit) {
            this.data = data;
            this.offset = offset;
            this.limit = limit;
        }
    }

    public static void toFile(@android.support.annotation.NonNull java.nio.ByteBuffer r5, @android.support.annotation.NonNull java.io.File r6) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:33:0x0045 in {6, 8, 9, 12, 15, 16, 21, 23, 24, 28, 30, 31, 32} preds:[]
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
        r0 = 0;
        r5.position(r0);
        r1 = 0;
        r2 = 0;
        r3 = new java.io.RandomAccessFile;	 Catch:{ all -> 0x0031 }
        r4 = "rw";	 Catch:{ all -> 0x0031 }
        r3.<init>(r6, r4);	 Catch:{ all -> 0x0031 }
        r1 = r3;	 Catch:{ all -> 0x0031 }
        r3 = r1.getChannel();	 Catch:{ all -> 0x0031 }
        r2 = r3;	 Catch:{ all -> 0x0031 }
        r2.write(r5);	 Catch:{ all -> 0x0031 }
        r2.force(r0);	 Catch:{ all -> 0x0031 }
        r2.close();	 Catch:{ all -> 0x0031 }
        r1.close();	 Catch:{ all -> 0x0031 }
        if (r2 == 0) goto L_0x0028;
    L_0x0022:
        r2.close();	 Catch:{ IOException -> 0x0026 }
        goto L_0x0029;
    L_0x0026:
        r0 = move-exception;
        goto L_0x0029;
        r1.close();	 Catch:{ IOException -> 0x002e }
    L_0x002d:
        goto L_0x0030;
    L_0x002e:
        r0 = move-exception;
        goto L_0x002d;
    L_0x0030:
        return;
    L_0x0031:
        r0 = move-exception;
        if (r2 == 0) goto L_0x003a;
    L_0x0034:
        r2.close();	 Catch:{ IOException -> 0x0038 }
        goto L_0x003b;
    L_0x0038:
        r3 = move-exception;
        goto L_0x003b;
    L_0x003b:
        if (r1 == 0) goto L_0x0043;
    L_0x003d:
        r1.close();	 Catch:{ IOException -> 0x0041 }
        goto L_0x0044;
    L_0x0041:
        r3 = move-exception;
        goto L_0x0044;
    L_0x0044:
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.util.ByteBufferUtil.toFile(java.nio.ByteBuffer, java.io.File):void");
    }

    private ByteBufferUtil() {
    }

    @NonNull
    public static ByteBuffer fromFile(@NonNull File file) throws IOException {
        RandomAccessFile raf = null;
        FileChannel channel = null;
        try {
            long fileLength = file.length();
            if (fileLength > 2147483647L) {
                throw new IOException("File too large to map into memory");
            } else if (fileLength != 0) {
                raf = new RandomAccessFile(file, "r");
                channel = raf.getChannel();
                ByteBuffer load = channel.map(MapMode.READ_ONLY, 0, fileLength).load();
                if (channel != null) {
                    try {
                        channel.close();
                    } catch (IOException e) {
                    }
                }
                try {
                    raf.close();
                } catch (IOException e2) {
                }
                return load;
            } else {
                throw new IOException("File unsuitable for memory mapping");
            }
        } catch (Throwable th) {
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException e3) {
                }
            }
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e4) {
                }
            }
        }
    }

    public static void toStream(@NonNull ByteBuffer byteBuffer, @NonNull OutputStream os) throws IOException {
        SafeArray safeArray = getSafeArray(byteBuffer);
        if (safeArray != null) {
            os.write(safeArray.data, safeArray.offset, safeArray.offset + safeArray.limit);
            return;
        }
        byte[] buffer = (byte[]) BUFFER_REF.getAndSet(null);
        if (buffer == null) {
            buffer = new byte[16384];
        }
        while (byteBuffer.remaining() > 0) {
            int toRead = Math.min(byteBuffer.remaining(), buffer.length);
            byteBuffer.get(buffer, 0, toRead);
            os.write(buffer, 0, toRead);
        }
        BUFFER_REF.set(buffer);
    }

    @NonNull
    public static byte[] toBytes(@NonNull ByteBuffer byteBuffer) {
        SafeArray safeArray = getSafeArray(byteBuffer);
        if (safeArray != null && safeArray.offset == 0 && safeArray.limit == safeArray.data.length) {
            return byteBuffer.array();
        }
        ByteBuffer toCopy = byteBuffer.asReadOnlyBuffer();
        byte[] result = new byte[toCopy.limit()];
        toCopy.position(0);
        toCopy.get(result);
        return result;
    }

    @NonNull
    public static InputStream toStream(@NonNull ByteBuffer buffer) {
        return new ByteBufferStream(buffer);
    }

    @NonNull
    public static ByteBuffer fromStream(@NonNull InputStream stream) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream(16384);
        byte[] buffer = (byte[]) BUFFER_REF.getAndSet(null);
        if (buffer == null) {
            buffer = new byte[16384];
        }
        while (true) {
            int read = stream.read(buffer);
            int n = read;
            if (read >= 0) {
                outStream.write(buffer, 0, n);
            } else {
                BUFFER_REF.set(buffer);
                byte[] bytes = outStream.toByteArray();
                return (ByteBuffer) ByteBuffer.allocateDirect(bytes.length).put(bytes).position(0);
            }
        }
    }

    @Nullable
    private static SafeArray getSafeArray(@NonNull ByteBuffer byteBuffer) {
        if (byteBuffer.isReadOnly() || !byteBuffer.hasArray()) {
            return null;
        }
        return new SafeArray(byteBuffer.array(), byteBuffer.arrayOffset(), byteBuffer.limit());
    }
}
