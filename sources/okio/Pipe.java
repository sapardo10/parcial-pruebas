package okio;

import java.io.IOException;

public final class Pipe {
    final Buffer buffer = new Buffer();
    final long maxBufferSize;
    private final Sink sink = new PipeSink();
    boolean sinkClosed;
    private final Source source = new PipeSource();
    boolean sourceClosed;

    final class PipeSink implements Sink {
        final Timeout timeout = new Timeout();

        public void write(okio.Buffer r8, long r9) throws java.io.IOException {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:22:0x005b in {11, 12, 14, 16, 18, 21} preds:[]
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
            r7 = this;
            r0 = okio.Pipe.this;
            r0 = r0.buffer;
            monitor-enter(r0);
            r1 = okio.Pipe.this;	 Catch:{ all -> 0x0058 }
            r1 = r1.sinkClosed;	 Catch:{ all -> 0x0058 }
            if (r1 != 0) goto L_0x0050;	 Catch:{ all -> 0x0058 }
        L_0x000b:
            r1 = 0;	 Catch:{ all -> 0x0058 }
            r3 = (r9 > r1 ? 1 : (r9 == r1 ? 0 : -1));	 Catch:{ all -> 0x0058 }
            if (r3 <= 0) goto L_0x004e;	 Catch:{ all -> 0x0058 }
        L_0x0011:
            r3 = okio.Pipe.this;	 Catch:{ all -> 0x0058 }
            r3 = r3.sourceClosed;	 Catch:{ all -> 0x0058 }
            if (r3 != 0) goto L_0x0046;	 Catch:{ all -> 0x0058 }
        L_0x0017:
            r3 = okio.Pipe.this;	 Catch:{ all -> 0x0058 }
            r3 = r3.maxBufferSize;	 Catch:{ all -> 0x0058 }
            r5 = okio.Pipe.this;	 Catch:{ all -> 0x0058 }
            r5 = r5.buffer;	 Catch:{ all -> 0x0058 }
            r5 = r5.size();	 Catch:{ all -> 0x0058 }
            r3 = r3 - r5;	 Catch:{ all -> 0x0058 }
            r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1));	 Catch:{ all -> 0x0058 }
            if (r5 != 0) goto L_0x0032;	 Catch:{ all -> 0x0058 }
        L_0x0028:
            r1 = r7.timeout;	 Catch:{ all -> 0x0058 }
            r2 = okio.Pipe.this;	 Catch:{ all -> 0x0058 }
            r2 = r2.buffer;	 Catch:{ all -> 0x0058 }
            r1.waitUntilNotified(r2);	 Catch:{ all -> 0x0058 }
            goto L_0x000b;	 Catch:{ all -> 0x0058 }
        L_0x0032:
            r1 = java.lang.Math.min(r3, r9);	 Catch:{ all -> 0x0058 }
            r5 = okio.Pipe.this;	 Catch:{ all -> 0x0058 }
            r5 = r5.buffer;	 Catch:{ all -> 0x0058 }
            r5.write(r8, r1);	 Catch:{ all -> 0x0058 }
            r9 = r9 - r1;	 Catch:{ all -> 0x0058 }
            r5 = okio.Pipe.this;	 Catch:{ all -> 0x0058 }
            r5 = r5.buffer;	 Catch:{ all -> 0x0058 }
            r5.notifyAll();	 Catch:{ all -> 0x0058 }
            goto L_0x000b;	 Catch:{ all -> 0x0058 }
        L_0x0046:
            r1 = new java.io.IOException;	 Catch:{ all -> 0x0058 }
            r2 = "source is closed";	 Catch:{ all -> 0x0058 }
            r1.<init>(r2);	 Catch:{ all -> 0x0058 }
            throw r1;	 Catch:{ all -> 0x0058 }
        L_0x004e:
            monitor-exit(r0);	 Catch:{ all -> 0x0058 }
            return;	 Catch:{ all -> 0x0058 }
        L_0x0050:
            r1 = new java.lang.IllegalStateException;	 Catch:{ all -> 0x0058 }
            r2 = "closed";	 Catch:{ all -> 0x0058 }
            r1.<init>(r2);	 Catch:{ all -> 0x0058 }
            throw r1;	 Catch:{ all -> 0x0058 }
        L_0x0058:
            r1 = move-exception;	 Catch:{ all -> 0x0058 }
            monitor-exit(r0);	 Catch:{ all -> 0x0058 }
            throw r1;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: okio.Pipe.PipeSink.write(okio.Buffer, long):void");
        }

        PipeSink() {
        }

        public void flush() throws IOException {
            synchronized (Pipe.this.buffer) {
                if (Pipe.this.sinkClosed) {
                    throw new IllegalStateException("closed");
                }
                if (Pipe.this.sourceClosed) {
                    if (Pipe.this.buffer.size() > 0) {
                        throw new IOException("source is closed");
                    }
                }
            }
        }

        public void close() throws IOException {
            synchronized (Pipe.this.buffer) {
                if (Pipe.this.sinkClosed) {
                    return;
                }
                if (Pipe.this.sourceClosed) {
                    if (Pipe.this.buffer.size() > 0) {
                        throw new IOException("source is closed");
                    }
                }
                Pipe.this.sinkClosed = true;
                Pipe.this.buffer.notifyAll();
            }
        }

        public Timeout timeout() {
            return this.timeout;
        }
    }

    final class PipeSource implements Source {
        final Timeout timeout = new Timeout();

        public long read(okio.Buffer r7, long r8) throws java.io.IOException {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:21:0x0049 in {11, 12, 15, 17, 20} preds:[]
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
            r6 = this;
            r0 = okio.Pipe.this;
            r0 = r0.buffer;
            monitor-enter(r0);
            r1 = okio.Pipe.this;	 Catch:{ all -> 0x0046 }
            r1 = r1.sourceClosed;	 Catch:{ all -> 0x0046 }
            if (r1 != 0) goto L_0x003e;	 Catch:{ all -> 0x0046 }
        L_0x000b:
            r1 = okio.Pipe.this;	 Catch:{ all -> 0x0046 }
            r1 = r1.buffer;	 Catch:{ all -> 0x0046 }
            r1 = r1.size();	 Catch:{ all -> 0x0046 }
            r3 = 0;	 Catch:{ all -> 0x0046 }
            r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));	 Catch:{ all -> 0x0046 }
            if (r5 != 0) goto L_0x002d;	 Catch:{ all -> 0x0046 }
        L_0x0019:
            r1 = okio.Pipe.this;	 Catch:{ all -> 0x0046 }
            r1 = r1.sinkClosed;	 Catch:{ all -> 0x0046 }
            if (r1 == 0) goto L_0x0023;	 Catch:{ all -> 0x0046 }
        L_0x001f:
            r1 = -1;	 Catch:{ all -> 0x0046 }
            monitor-exit(r0);	 Catch:{ all -> 0x0046 }
            return r1;	 Catch:{ all -> 0x0046 }
        L_0x0023:
            r1 = r6.timeout;	 Catch:{ all -> 0x0046 }
            r2 = okio.Pipe.this;	 Catch:{ all -> 0x0046 }
            r2 = r2.buffer;	 Catch:{ all -> 0x0046 }
            r1.waitUntilNotified(r2);	 Catch:{ all -> 0x0046 }
            goto L_0x000b;	 Catch:{ all -> 0x0046 }
        L_0x002d:
            r1 = okio.Pipe.this;	 Catch:{ all -> 0x0046 }
            r1 = r1.buffer;	 Catch:{ all -> 0x0046 }
            r1 = r1.read(r7, r8);	 Catch:{ all -> 0x0046 }
            r3 = okio.Pipe.this;	 Catch:{ all -> 0x0046 }
            r3 = r3.buffer;	 Catch:{ all -> 0x0046 }
            r3.notifyAll();	 Catch:{ all -> 0x0046 }
            monitor-exit(r0);	 Catch:{ all -> 0x0046 }
            return r1;	 Catch:{ all -> 0x0046 }
        L_0x003e:
            r1 = new java.lang.IllegalStateException;	 Catch:{ all -> 0x0046 }
            r2 = "closed";	 Catch:{ all -> 0x0046 }
            r1.<init>(r2);	 Catch:{ all -> 0x0046 }
            throw r1;	 Catch:{ all -> 0x0046 }
        L_0x0046:
            r1 = move-exception;	 Catch:{ all -> 0x0046 }
            monitor-exit(r0);	 Catch:{ all -> 0x0046 }
            throw r1;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: okio.Pipe.PipeSource.read(okio.Buffer, long):long");
        }

        PipeSource() {
        }

        public void close() throws IOException {
            synchronized (Pipe.this.buffer) {
                Pipe.this.sourceClosed = true;
                Pipe.this.buffer.notifyAll();
            }
        }

        public Timeout timeout() {
            return this.timeout;
        }
    }

    public Pipe(long maxBufferSize) {
        if (maxBufferSize >= 1) {
            this.maxBufferSize = maxBufferSize;
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("maxBufferSize < 1: ");
        stringBuilder.append(maxBufferSize);
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public final Source source() {
        return this.source;
    }

    public final Sink sink() {
        return this.sink;
    }
}
