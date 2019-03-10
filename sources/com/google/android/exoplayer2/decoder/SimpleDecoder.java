package com.google.android.exoplayer2.decoder;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.util.Assertions;
import java.util.ArrayDeque;

public abstract class SimpleDecoder<I extends DecoderInputBuffer, O extends OutputBuffer, E extends Exception> implements Decoder<I, O, E> {
    private int availableInputBufferCount;
    private final I[] availableInputBuffers;
    private int availableOutputBufferCount;
    private final O[] availableOutputBuffers;
    private final Thread decodeThread;
    private I dequeuedInputBuffer;
    private E exception;
    private boolean flushed;
    private final Object lock = new Object();
    private final ArrayDeque<I> queuedInputBuffers = new ArrayDeque();
    private final ArrayDeque<O> queuedOutputBuffers = new ArrayDeque();
    private boolean released;
    private int skippedOutputBufferCount;

    /* renamed from: com.google.android.exoplayer2.decoder.SimpleDecoder$1 */
    class C05771 extends Thread {
        C05771() {
        }

        public void run() {
            SimpleDecoder.this.run();
        }
    }

    private boolean decode() throws java.lang.InterruptedException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:60:0x009e in {7, 11, 16, 19, 20, 23, 25, 27, 28, 35, 38, 39, 45, 48, 49, 52, 55, 59} preds:[]
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
        r7 = this;
        r0 = r7.lock;
        monitor-enter(r0);
    L_0x0003:
        r1 = r7.released;	 Catch:{ all -> 0x009b }
        if (r1 != 0) goto L_0x0013;	 Catch:{ all -> 0x009b }
    L_0x0007:
        r1 = r7.canDecodeBuffer();	 Catch:{ all -> 0x009b }
        if (r1 != 0) goto L_0x0013;	 Catch:{ all -> 0x009b }
    L_0x000d:
        r1 = r7.lock;	 Catch:{ all -> 0x009b }
        r1.wait();	 Catch:{ all -> 0x009b }
        goto L_0x0003;	 Catch:{ all -> 0x009b }
        r1 = r7.released;	 Catch:{ all -> 0x009b }
        r2 = 0;	 Catch:{ all -> 0x009b }
        if (r1 == 0) goto L_0x001b;	 Catch:{ all -> 0x009b }
    L_0x0019:
        monitor-exit(r0);	 Catch:{ all -> 0x009b }
        return r2;	 Catch:{ all -> 0x009b }
    L_0x001b:
        r1 = r7.queuedInputBuffers;	 Catch:{ all -> 0x009b }
        r1 = r1.removeFirst();	 Catch:{ all -> 0x009b }
        r1 = (com.google.android.exoplayer2.decoder.DecoderInputBuffer) r1;	 Catch:{ all -> 0x009b }
        r3 = r7.availableOutputBuffers;	 Catch:{ all -> 0x009b }
        r4 = r7.availableOutputBufferCount;	 Catch:{ all -> 0x009b }
        r5 = 1;	 Catch:{ all -> 0x009b }
        r4 = r4 - r5;	 Catch:{ all -> 0x009b }
        r7.availableOutputBufferCount = r4;	 Catch:{ all -> 0x009b }
        r3 = r3[r4];	 Catch:{ all -> 0x009b }
        r4 = r7.flushed;	 Catch:{ all -> 0x009b }
        r7.flushed = r2;	 Catch:{ all -> 0x009b }
        monitor-exit(r0);	 Catch:{ all -> 0x009b }
        r0 = r1.isEndOfStream();
        if (r0 == 0) goto L_0x003d;
    L_0x0038:
        r0 = 4;
        r3.addFlag(r0);
        goto L_0x006e;
    L_0x003d:
        r0 = r1.isDecodeOnly();
        if (r0 == 0) goto L_0x0049;
    L_0x0043:
        r0 = -2147483648; // 0xffffffff80000000 float:-0.0 double:NaN;
        r3.addFlag(r0);
        goto L_0x004a;
    L_0x004a:
        r0 = r7.decode(r1, r3, r4);	 Catch:{ RuntimeException -> 0x0059, OutOfMemoryError -> 0x0051 }
        r7.exception = r0;	 Catch:{ RuntimeException -> 0x0059, OutOfMemoryError -> 0x0051 }
        goto L_0x0060;
    L_0x0051:
        r0 = move-exception;
        r6 = r7.createUnexpectedDecodeException(r0);
        r7.exception = r6;
        goto L_0x0061;
    L_0x0059:
        r0 = move-exception;
        r6 = r7.createUnexpectedDecodeException(r0);
        r7.exception = r6;
    L_0x0061:
        r0 = r7.exception;
        if (r0 == 0) goto L_0x006d;
    L_0x0065:
        r0 = r7.lock;
        monitor-enter(r0);
        monitor-exit(r0);	 Catch:{ all -> 0x006a }
        return r2;	 Catch:{ all -> 0x006a }
    L_0x006a:
        r2 = move-exception;	 Catch:{ all -> 0x006a }
        monitor-exit(r0);	 Catch:{ all -> 0x006a }
        throw r2;
    L_0x006e:
        r6 = r7.lock;
        monitor-enter(r6);
        r0 = r7.flushed;	 Catch:{ all -> 0x0098 }
        if (r0 == 0) goto L_0x0079;	 Catch:{ all -> 0x0098 }
    L_0x0075:
        r3.release();	 Catch:{ all -> 0x0098 }
        goto L_0x0093;	 Catch:{ all -> 0x0098 }
    L_0x0079:
        r0 = r3.isDecodeOnly();	 Catch:{ all -> 0x0098 }
        if (r0 == 0) goto L_0x0088;	 Catch:{ all -> 0x0098 }
    L_0x007f:
        r0 = r7.skippedOutputBufferCount;	 Catch:{ all -> 0x0098 }
        r0 = r0 + r5;	 Catch:{ all -> 0x0098 }
        r7.skippedOutputBufferCount = r0;	 Catch:{ all -> 0x0098 }
        r3.release();	 Catch:{ all -> 0x0098 }
        goto L_0x0093;	 Catch:{ all -> 0x0098 }
    L_0x0088:
        r0 = r7.skippedOutputBufferCount;	 Catch:{ all -> 0x0098 }
        r3.skippedOutputBufferCount = r0;	 Catch:{ all -> 0x0098 }
        r7.skippedOutputBufferCount = r2;	 Catch:{ all -> 0x0098 }
        r0 = r7.queuedOutputBuffers;	 Catch:{ all -> 0x0098 }
        r0.addLast(r3);	 Catch:{ all -> 0x0098 }
    L_0x0093:
        r7.releaseInputBufferInternal(r1);	 Catch:{ all -> 0x0098 }
        monitor-exit(r6);	 Catch:{ all -> 0x0098 }
        return r5;	 Catch:{ all -> 0x0098 }
    L_0x0098:
        r0 = move-exception;	 Catch:{ all -> 0x0098 }
        monitor-exit(r6);	 Catch:{ all -> 0x0098 }
        throw r0;
    L_0x009b:
        r1 = move-exception;
        monitor-exit(r0);	 Catch:{ all -> 0x009b }
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.decoder.SimpleDecoder.decode():boolean");
    }

    private void run() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:9:0x0010 in {3, 5, 8} preds:[]
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
        r2 = this;
    L_0x0000:
        r0 = r2.decode();	 Catch:{ InterruptedException -> 0x0009 }
        if (r0 == 0) goto L_0x0007;
    L_0x0006:
        goto L_0x0000;
        return;
    L_0x0009:
        r0 = move-exception;
        r1 = new java.lang.IllegalStateException;
        r1.<init>(r0);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.decoder.SimpleDecoder.run():void");
    }

    protected abstract I createInputBuffer();

    protected abstract O createOutputBuffer();

    protected abstract E createUnexpectedDecodeException(Throwable th);

    @Nullable
    protected abstract E decode(I i, O o, boolean z);

    public final void flush() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:20:0x0045 in {6, 7, 10, 14, 16, 19} preds:[]
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
        r2 = this;
        r0 = r2.lock;
        monitor-enter(r0);
        r1 = 1;
        r2.flushed = r1;	 Catch:{ all -> 0x0042 }
        r1 = 0;	 Catch:{ all -> 0x0042 }
        r2.skippedOutputBufferCount = r1;	 Catch:{ all -> 0x0042 }
        r1 = r2.dequeuedInputBuffer;	 Catch:{ all -> 0x0042 }
        if (r1 == 0) goto L_0x0016;	 Catch:{ all -> 0x0042 }
    L_0x000d:
        r1 = r2.dequeuedInputBuffer;	 Catch:{ all -> 0x0042 }
        r2.releaseInputBufferInternal(r1);	 Catch:{ all -> 0x0042 }
        r1 = 0;	 Catch:{ all -> 0x0042 }
        r2.dequeuedInputBuffer = r1;	 Catch:{ all -> 0x0042 }
        goto L_0x0017;	 Catch:{ all -> 0x0042 }
    L_0x0017:
        r1 = r2.queuedInputBuffers;	 Catch:{ all -> 0x0042 }
        r1 = r1.isEmpty();	 Catch:{ all -> 0x0042 }
        if (r1 != 0) goto L_0x002b;	 Catch:{ all -> 0x0042 }
    L_0x001f:
        r1 = r2.queuedInputBuffers;	 Catch:{ all -> 0x0042 }
        r1 = r1.removeFirst();	 Catch:{ all -> 0x0042 }
        r1 = (com.google.android.exoplayer2.decoder.DecoderInputBuffer) r1;	 Catch:{ all -> 0x0042 }
        r2.releaseInputBufferInternal(r1);	 Catch:{ all -> 0x0042 }
        goto L_0x0017;	 Catch:{ all -> 0x0042 }
    L_0x002c:
        r1 = r2.queuedOutputBuffers;	 Catch:{ all -> 0x0042 }
        r1 = r1.isEmpty();	 Catch:{ all -> 0x0042 }
        if (r1 != 0) goto L_0x0040;	 Catch:{ all -> 0x0042 }
    L_0x0034:
        r1 = r2.queuedOutputBuffers;	 Catch:{ all -> 0x0042 }
        r1 = r1.removeFirst();	 Catch:{ all -> 0x0042 }
        r1 = (com.google.android.exoplayer2.decoder.OutputBuffer) r1;	 Catch:{ all -> 0x0042 }
        r1.release();	 Catch:{ all -> 0x0042 }
        goto L_0x002c;	 Catch:{ all -> 0x0042 }
    L_0x0040:
        monitor-exit(r0);	 Catch:{ all -> 0x0042 }
        return;	 Catch:{ all -> 0x0042 }
    L_0x0042:
        r1 = move-exception;	 Catch:{ all -> 0x0042 }
        monitor-exit(r0);	 Catch:{ all -> 0x0042 }
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.decoder.SimpleDecoder.flush():void");
    }

    protected SimpleDecoder(I[] inputBuffers, O[] outputBuffers) {
        int i;
        this.availableInputBuffers = inputBuffers;
        this.availableInputBufferCount = inputBuffers.length;
        for (i = 0; i < this.availableInputBufferCount; i++) {
            this.availableInputBuffers[i] = createInputBuffer();
        }
        this.availableOutputBuffers = outputBuffers;
        this.availableOutputBufferCount = outputBuffers.length;
        for (i = 0; i < this.availableOutputBufferCount; i++) {
            this.availableOutputBuffers[i] = createOutputBuffer();
        }
        this.decodeThread = new C05771();
        this.decodeThread.start();
    }

    protected final void setInitialInputBufferSize(int size) {
        Assertions.checkState(this.availableInputBufferCount == this.availableInputBuffers.length);
        for (I inputBuffer : this.availableInputBuffers) {
            inputBuffer.ensureSpaceForWrite(size);
        }
    }

    public final I dequeueInputBuffer() throws Exception {
        I i;
        synchronized (this.lock) {
            DecoderInputBuffer decoderInputBuffer;
            maybeThrowException();
            Assertions.checkState(this.dequeuedInputBuffer == null);
            if (this.availableInputBufferCount == 0) {
                decoderInputBuffer = null;
            } else {
                DecoderInputBuffer[] decoderInputBufferArr = this.availableInputBuffers;
                int i2 = this.availableInputBufferCount - 1;
                this.availableInputBufferCount = i2;
                decoderInputBuffer = decoderInputBufferArr[i2];
            }
            this.dequeuedInputBuffer = decoderInputBuffer;
            i = this.dequeuedInputBuffer;
        }
        return i;
    }

    public final void queueInputBuffer(I inputBuffer) throws Exception {
        synchronized (this.lock) {
            maybeThrowException();
            Assertions.checkArgument(inputBuffer == this.dequeuedInputBuffer);
            this.queuedInputBuffers.addLast(inputBuffer);
            maybeNotifyDecodeLoop();
            this.dequeuedInputBuffer = null;
        }
    }

    public final O dequeueOutputBuffer() throws Exception {
        synchronized (this.lock) {
            maybeThrowException();
            if (this.queuedOutputBuffers.isEmpty()) {
                return null;
            }
            OutputBuffer outputBuffer = (OutputBuffer) this.queuedOutputBuffers.removeFirst();
            return outputBuffer;
        }
    }

    protected void releaseOutputBuffer(O outputBuffer) {
        synchronized (this.lock) {
            releaseOutputBufferInternal(outputBuffer);
            maybeNotifyDecodeLoop();
        }
    }

    public void release() {
        synchronized (this.lock) {
            this.released = true;
            this.lock.notify();
        }
        try {
            this.decodeThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void maybeThrowException() throws Exception {
        Exception exception = this.exception;
        if (exception != null) {
            throw exception;
        }
    }

    private void maybeNotifyDecodeLoop() {
        if (canDecodeBuffer()) {
            this.lock.notify();
        }
    }

    private boolean canDecodeBuffer() {
        return !this.queuedInputBuffers.isEmpty() && this.availableOutputBufferCount > 0;
    }

    private void releaseInputBufferInternal(I inputBuffer) {
        inputBuffer.clear();
        DecoderInputBuffer[] decoderInputBufferArr = this.availableInputBuffers;
        int i = this.availableInputBufferCount;
        this.availableInputBufferCount = i + 1;
        decoderInputBufferArr[i] = inputBuffer;
    }

    private void releaseOutputBufferInternal(O outputBuffer) {
        outputBuffer.clear();
        OutputBuffer[] outputBufferArr = this.availableOutputBuffers;
        int i = this.availableOutputBufferCount;
        this.availableOutputBufferCount = i + 1;
        outputBufferArr[i] = outputBuffer;
    }
}
