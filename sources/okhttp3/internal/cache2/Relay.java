package okhttp3.internal.cache2;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.ByteString;
import okio.Source;
import okio.Timeout;

final class Relay {
    private static final long FILE_HEADER_SIZE = 32;
    static final ByteString PREFIX_CLEAN = ByteString.encodeUtf8("OkHttp cache v1\n");
    static final ByteString PREFIX_DIRTY = ByteString.encodeUtf8("OkHttp DIRTY :(\n");
    private static final int SOURCE_FILE = 2;
    private static final int SOURCE_UPSTREAM = 1;
    final Buffer buffer = new Buffer();
    final long bufferMaxSize;
    boolean complete;
    RandomAccessFile file;
    private final ByteString metadata;
    int sourceCount;
    Source upstream;
    final Buffer upstreamBuffer = new Buffer();
    long upstreamPos;
    Thread upstreamReader;

    class RelaySource implements Source {
        private FileOperator fileOperator = new FileOperator(Relay.this.file.getChannel());
        private long sourcePos;
        private final Timeout timeout = new Timeout();

        public long read(okio.Buffer r22, long r23) throws java.io.IOException {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:102:0x015c in {10, 13, 16, 21, 25, 38, 41, 43, 56, 57, 65, 68, 70, 74, 76, 78, 80, 82, 88, 92, 96, 99, 101} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r21 = this;
            r1 = r21;
            r2 = r23;
            r0 = r1.fileOperator;
            if (r0 == 0) goto L_0x0154;
        L_0x0008:
            r4 = okhttp3.internal.cache2.Relay.this;
            monitor-enter(r4);
        L_0x000b:
            r5 = r1.sourcePos;	 Catch:{ all -> 0x0151 }
            r0 = okhttp3.internal.cache2.Relay.this;	 Catch:{ all -> 0x0151 }
            r7 = r0.upstreamPos;	 Catch:{ all -> 0x0151 }
            r9 = r7;	 Catch:{ all -> 0x0151 }
            r11 = -1;	 Catch:{ all -> 0x0151 }
            r0 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));	 Catch:{ all -> 0x0151 }
            if (r0 != 0) goto L_0x003a;	 Catch:{ all -> 0x0151 }
        L_0x0018:
            r0 = okhttp3.internal.cache2.Relay.this;	 Catch:{ all -> 0x0151 }
            r0 = r0.complete;	 Catch:{ all -> 0x0151 }
            if (r0 == 0) goto L_0x0020;	 Catch:{ all -> 0x0151 }
        L_0x001e:
            monitor-exit(r4);	 Catch:{ all -> 0x0151 }
            return r11;	 Catch:{ all -> 0x0151 }
        L_0x0020:
            r0 = okhttp3.internal.cache2.Relay.this;	 Catch:{ all -> 0x0151 }
            r0 = r0.upstreamReader;	 Catch:{ all -> 0x0151 }
            if (r0 == 0) goto L_0x002e;	 Catch:{ all -> 0x0151 }
        L_0x0026:
            r0 = r1.timeout;	 Catch:{ all -> 0x0151 }
            r5 = okhttp3.internal.cache2.Relay.this;	 Catch:{ all -> 0x0151 }
            r0.waitUntilNotified(r5);	 Catch:{ all -> 0x0151 }
            goto L_0x000b;	 Catch:{ all -> 0x0151 }
        L_0x002e:
            r0 = okhttp3.internal.cache2.Relay.this;	 Catch:{ all -> 0x0151 }
            r5 = java.lang.Thread.currentThread();	 Catch:{ all -> 0x0151 }
            r0.upstreamReader = r5;	 Catch:{ all -> 0x0151 }
            r0 = 1;	 Catch:{ all -> 0x0151 }
            monitor-exit(r4);	 Catch:{ all -> 0x0151 }
            r5 = r0;	 Catch:{ all -> 0x0151 }
            goto L_0x004d;	 Catch:{ all -> 0x0151 }
        L_0x003a:
            r0 = okhttp3.internal.cache2.Relay.this;	 Catch:{ all -> 0x0151 }
            r0 = r0.buffer;	 Catch:{ all -> 0x0151 }
            r5 = r0.size();	 Catch:{ all -> 0x0151 }
            r5 = r9 - r5;	 Catch:{ all -> 0x0151 }
            r7 = r1.sourcePos;	 Catch:{ all -> 0x0151 }
            r0 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1));	 Catch:{ all -> 0x0151 }
            if (r0 >= 0) goto L_0x0133;	 Catch:{ all -> 0x0151 }
        L_0x004a:
            r0 = 2;	 Catch:{ all -> 0x0151 }
            monitor-exit(r4);	 Catch:{ all -> 0x0151 }
            r5 = r0;
        L_0x004d:
            r0 = 2;
            r6 = 32;
            if (r5 != r0) goto L_0x006c;
        L_0x0052:
            r11 = r1.sourcePos;
            r11 = r9 - r11;
            r11 = java.lang.Math.min(r2, r11);
            r13 = r1.fileOperator;
            r14 = r1.sourcePos;
            r14 = r14 + r6;
            r16 = r22;
            r17 = r11;
            r13.read(r14, r16, r17);
            r6 = r1.sourcePos;
            r6 = r6 + r11;
            r1.sourcePos = r6;
            return r11;
        L_0x006c:
            r4 = 0;
            r0 = okhttp3.internal.cache2.Relay.this;	 Catch:{ all -> 0x011f }
            r0 = r0.upstream;	 Catch:{ all -> 0x011f }
            r8 = okhttp3.internal.cache2.Relay.this;	 Catch:{ all -> 0x011f }
            r8 = r8.upstreamBuffer;	 Catch:{ all -> 0x011f }
            r13 = okhttp3.internal.cache2.Relay.this;	 Catch:{ all -> 0x011f }
            r13 = r13.bufferMaxSize;	 Catch:{ all -> 0x011f }
            r13 = r0.read(r8, r13);	 Catch:{ all -> 0x011f }
            r0 = (r13 > r11 ? 1 : (r13 == r11 ? 0 : -1));
            if (r0 != 0) goto L_0x009c;
        L_0x0081:
            r0 = okhttp3.internal.cache2.Relay.this;	 Catch:{ all -> 0x0098 }
            r0.commit(r9);	 Catch:{ all -> 0x0098 }
            r6 = okhttp3.internal.cache2.Relay.this;
            monitor-enter(r6);
            r0 = okhttp3.internal.cache2.Relay.this;	 Catch:{ all -> 0x0095 }
            r0.upstreamReader = r4;	 Catch:{ all -> 0x0095 }
            r0 = okhttp3.internal.cache2.Relay.this;	 Catch:{ all -> 0x0095 }
            r0.notifyAll();	 Catch:{ all -> 0x0095 }
            monitor-exit(r6);	 Catch:{ all -> 0x0095 }
            return r11;	 Catch:{ all -> 0x0095 }
        L_0x0095:
            r0 = move-exception;	 Catch:{ all -> 0x0095 }
            monitor-exit(r6);	 Catch:{ all -> 0x0095 }
            throw r0;
        L_0x0098:
            r0 = move-exception;
            r15 = r5;
            goto L_0x0121;
        L_0x009c:
            r11 = java.lang.Math.min(r13, r2);	 Catch:{ all -> 0x011f }
            r0 = okhttp3.internal.cache2.Relay.this;	 Catch:{ all -> 0x011f }
            r15 = r0.upstreamBuffer;	 Catch:{ all -> 0x011f }
            r17 = 0;	 Catch:{ all -> 0x011f }
            r16 = r22;	 Catch:{ all -> 0x011f }
            r19 = r11;	 Catch:{ all -> 0x011f }
            r15.copyTo(r16, r17, r19);	 Catch:{ all -> 0x011f }
            r8 = r5;
            r4 = r1.sourcePos;	 Catch:{ all -> 0x011c }
            r4 = r4 + r11;	 Catch:{ all -> 0x011c }
            r1.sourcePos = r4;	 Catch:{ all -> 0x011c }
            r15 = r1.fileOperator;	 Catch:{ all -> 0x011c }
            r16 = r9 + r6;	 Catch:{ all -> 0x011c }
            r0 = okhttp3.internal.cache2.Relay.this;	 Catch:{ all -> 0x011c }
            r0 = r0.upstreamBuffer;	 Catch:{ all -> 0x011c }
            r18 = r0.clone();	 Catch:{ all -> 0x011c }
            r19 = r13;	 Catch:{ all -> 0x011c }
            r15.write(r16, r18, r19);	 Catch:{ all -> 0x011c }
            r4 = okhttp3.internal.cache2.Relay.this;	 Catch:{ all -> 0x011c }
            monitor-enter(r4);	 Catch:{ all -> 0x011c }
            r0 = okhttp3.internal.cache2.Relay.this;	 Catch:{ all -> 0x0114 }
            r0 = r0.buffer;	 Catch:{ all -> 0x0114 }
            r5 = okhttp3.internal.cache2.Relay.this;	 Catch:{ all -> 0x0114 }
            r5 = r5.upstreamBuffer;	 Catch:{ all -> 0x0114 }
            r0.write(r5, r13);	 Catch:{ all -> 0x0114 }
            r0 = okhttp3.internal.cache2.Relay.this;	 Catch:{ all -> 0x0114 }
            r0 = r0.buffer;	 Catch:{ all -> 0x0114 }
            r5 = r0.size();	 Catch:{ all -> 0x0114 }
            r0 = okhttp3.internal.cache2.Relay.this;	 Catch:{ all -> 0x0114 }
            r15 = r8;
            r7 = r0.bufferMaxSize;	 Catch:{ all -> 0x011a }
            r0 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));	 Catch:{ all -> 0x011a }
            if (r0 <= 0) goto L_0x00f8;	 Catch:{ all -> 0x011a }
        L_0x00e3:
            r0 = okhttp3.internal.cache2.Relay.this;	 Catch:{ all -> 0x011a }
            r0 = r0.buffer;	 Catch:{ all -> 0x011a }
            r5 = okhttp3.internal.cache2.Relay.this;	 Catch:{ all -> 0x011a }
            r5 = r5.buffer;	 Catch:{ all -> 0x011a }
            r5 = r5.size();	 Catch:{ all -> 0x011a }
            r7 = okhttp3.internal.cache2.Relay.this;	 Catch:{ all -> 0x011a }
            r7 = r7.bufferMaxSize;	 Catch:{ all -> 0x011a }
            r5 = r5 - r7;	 Catch:{ all -> 0x011a }
            r0.skip(r5);	 Catch:{ all -> 0x011a }
            goto L_0x00f9;	 Catch:{ all -> 0x011a }
        L_0x00f9:
            r0 = okhttp3.internal.cache2.Relay.this;	 Catch:{ all -> 0x011a }
            r5 = r0.upstreamPos;	 Catch:{ all -> 0x011a }
            r5 = r5 + r13;	 Catch:{ all -> 0x011a }
            r0.upstreamPos = r5;	 Catch:{ all -> 0x011a }
            monitor-exit(r4);	 Catch:{ all -> 0x011a }
            r5 = okhttp3.internal.cache2.Relay.this;
            monitor-enter(r5);
            r0 = okhttp3.internal.cache2.Relay.this;	 Catch:{ all -> 0x0111 }
            r4 = 0;	 Catch:{ all -> 0x0111 }
            r0.upstreamReader = r4;	 Catch:{ all -> 0x0111 }
            r0 = okhttp3.internal.cache2.Relay.this;	 Catch:{ all -> 0x0111 }
            r0.notifyAll();	 Catch:{ all -> 0x0111 }
            monitor-exit(r5);	 Catch:{ all -> 0x0111 }
            return r11;	 Catch:{ all -> 0x0111 }
        L_0x0111:
            r0 = move-exception;	 Catch:{ all -> 0x0111 }
            monitor-exit(r5);	 Catch:{ all -> 0x0111 }
            throw r0;
        L_0x0114:
            r0 = move-exception;
            r15 = r8;
        L_0x0116:
            monitor-exit(r4);	 Catch:{ all -> 0x011a }
            throw r0;	 Catch:{ all -> 0x0118 }
        L_0x0118:
            r0 = move-exception;
            goto L_0x0121;
        L_0x011a:
            r0 = move-exception;
            goto L_0x0116;
        L_0x011c:
            r0 = move-exception;
            r15 = r8;
            goto L_0x0121;
        L_0x011f:
            r0 = move-exception;
            r15 = r5;
        L_0x0121:
            r5 = okhttp3.internal.cache2.Relay.this;
            monitor-enter(r5);
            r4 = okhttp3.internal.cache2.Relay.this;	 Catch:{ all -> 0x0130 }
            r6 = 0;	 Catch:{ all -> 0x0130 }
            r4.upstreamReader = r6;	 Catch:{ all -> 0x0130 }
            r4 = okhttp3.internal.cache2.Relay.this;	 Catch:{ all -> 0x0130 }
            r4.notifyAll();	 Catch:{ all -> 0x0130 }
            monitor-exit(r5);	 Catch:{ all -> 0x0130 }
            throw r0;
        L_0x0130:
            r0 = move-exception;
            monitor-exit(r5);	 Catch:{ all -> 0x0130 }
            throw r0;
        L_0x0133:
            r7 = r1.sourcePos;	 Catch:{ all -> 0x0151 }
            r7 = r9 - r7;	 Catch:{ all -> 0x0151 }
            r7 = java.lang.Math.min(r2, r7);	 Catch:{ all -> 0x0151 }
            r0 = okhttp3.internal.cache2.Relay.this;	 Catch:{ all -> 0x0151 }
            r11 = r0.buffer;	 Catch:{ all -> 0x0151 }
            r12 = r1.sourcePos;	 Catch:{ all -> 0x0151 }
            r14 = r12 - r5;	 Catch:{ all -> 0x0151 }
            r12 = r22;	 Catch:{ all -> 0x0151 }
            r13 = r14;	 Catch:{ all -> 0x0151 }
            r15 = r7;	 Catch:{ all -> 0x0151 }
            r11.copyTo(r12, r13, r15);	 Catch:{ all -> 0x0151 }
            r11 = r1.sourcePos;	 Catch:{ all -> 0x0151 }
            r11 = r11 + r7;	 Catch:{ all -> 0x0151 }
            r1.sourcePos = r11;	 Catch:{ all -> 0x0151 }
            monitor-exit(r4);	 Catch:{ all -> 0x0151 }
            return r7;	 Catch:{ all -> 0x0151 }
        L_0x0151:
            r0 = move-exception;	 Catch:{ all -> 0x0151 }
            monitor-exit(r4);	 Catch:{ all -> 0x0151 }
            throw r0;
        L_0x0154:
            r0 = new java.lang.IllegalStateException;
            r4 = "closed";
            r0.<init>(r4);
            throw r0;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache2.Relay.RelaySource.read(okio.Buffer, long):long");
        }

        RelaySource() {
        }

        public Timeout timeout() {
            return this.timeout;
        }

        public void close() throws IOException {
            if (this.fileOperator != null) {
                this.fileOperator = null;
                RandomAccessFile fileToClose = null;
                synchronized (Relay.this) {
                    Relay relay = Relay.this;
                    relay.sourceCount--;
                    if (Relay.this.sourceCount == 0) {
                        fileToClose = Relay.this.file;
                        Relay.this.file = null;
                    }
                }
                if (fileToClose != null) {
                    Util.closeQuietly(fileToClose);
                }
            }
        }
    }

    private Relay(RandomAccessFile file, Source upstream, long upstreamPos, ByteString metadata, long bufferMaxSize) {
        this.file = file;
        this.upstream = upstream;
        this.complete = upstream == null;
        this.upstreamPos = upstreamPos;
        this.metadata = metadata;
        this.bufferMaxSize = bufferMaxSize;
    }

    public static Relay edit(File file, Source upstream, ByteString metadata, long bufferMaxSize) throws IOException {
        File file2 = file;
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        Relay relay = new Relay(randomAccessFile, upstream, 0, metadata, bufferMaxSize);
        randomAccessFile.setLength(0);
        relay.writeHeader(PREFIX_DIRTY, -1, -1);
        return relay;
    }

    public static Relay read(File file) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        FileOperator fileOperator = new FileOperator(randomAccessFile.getChannel());
        Buffer header = new Buffer();
        fileOperator.read(0, header, 32);
        if (header.readByteString((long) PREFIX_CLEAN.size()).equals(PREFIX_CLEAN)) {
            long upstreamSize = header.readLong();
            long metadataSize = header.readLong();
            Buffer metadataBuffer = new Buffer();
            fileOperator.read(upstreamSize + 32, metadataBuffer, metadataSize);
            return new Relay(randomAccessFile, null, upstreamSize, metadataBuffer.readByteString(), 0);
        }
        throw new IOException("unreadable cache file");
    }

    private void writeHeader(ByteString prefix, long upstreamSize, long metadataSize) throws IOException {
        Buffer header = new Buffer();
        header.write(prefix);
        header.writeLong(upstreamSize);
        header.writeLong(metadataSize);
        if (header.size() == 32) {
            new FileOperator(this.file.getChannel()).write(0, header, 32);
            return;
        }
        throw new IllegalArgumentException();
    }

    private void writeMetadata(long upstreamSize) throws IOException {
        Buffer metadataBuffer = new Buffer();
        metadataBuffer.write(this.metadata);
        new FileOperator(this.file.getChannel()).write(32 + upstreamSize, metadataBuffer, (long) this.metadata.size());
    }

    void commit(long upstreamSize) throws IOException {
        writeMetadata(upstreamSize);
        this.file.getChannel().force(false);
        writeHeader(PREFIX_CLEAN, upstreamSize, (long) this.metadata.size());
        this.file.getChannel().force(false);
        synchronized (this) {
            this.complete = true;
        }
        Util.closeQuietly(this.upstream);
        this.upstream = null;
    }

    boolean isClosed() {
        return this.file == null;
    }

    public ByteString metadata() {
        return this.metadata;
    }

    public Source newSource() {
        synchronized (this) {
            if (this.file == null) {
                return null;
            }
            this.sourceCount++;
            return new RelaySource();
        }
    }
}
