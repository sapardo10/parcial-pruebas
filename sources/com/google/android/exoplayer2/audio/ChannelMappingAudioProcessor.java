package com.google.android.exoplayer2.audio;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.util.Assertions;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

final class ChannelMappingAudioProcessor implements AudioProcessor {
    private boolean active;
    private ByteBuffer buffer = EMPTY_BUFFER;
    private int channelCount = -1;
    private boolean inputEnded;
    private ByteBuffer outputBuffer = EMPTY_BUFFER;
    @Nullable
    private int[] outputChannels;
    @Nullable
    private int[] pendingOutputChannels;
    private int sampleRateHz = -1;

    public boolean configure(int r8, int r9, int r10) throws com.google.android.exoplayer2.audio.AudioProcessor.UnhandledFormatException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:32:0x0058 in {3, 11, 14, 15, 23, 24, 25, 27, 29, 31} preds:[]
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
        r0 = r7.pendingOutputChannels;
        r1 = r7.outputChannels;
        r0 = java.util.Arrays.equals(r0, r1);
        r1 = 1;
        r0 = r0 ^ r1;
        r2 = r7.pendingOutputChannels;
        r7.outputChannels = r2;
        r2 = r7.outputChannels;
        r3 = 0;
        if (r2 != 0) goto L_0x0016;
    L_0x0013:
        r7.active = r3;
        return r0;
    L_0x0016:
        r2 = 2;
        if (r10 != r2) goto L_0x0052;
    L_0x0019:
        if (r0 != 0) goto L_0x0024;
    L_0x001b:
        r2 = r7.sampleRateHz;
        if (r2 != r8) goto L_0x0024;
    L_0x001f:
        r2 = r7.channelCount;
        if (r2 != r9) goto L_0x0024;
    L_0x0023:
        return r3;
        r7.sampleRateHz = r8;
        r7.channelCount = r9;
        r2 = r7.outputChannels;
        r2 = r2.length;
        if (r9 == r2) goto L_0x0030;
    L_0x002e:
        r2 = 1;
        goto L_0x0031;
    L_0x0030:
        r2 = 0;
    L_0x0031:
        r7.active = r2;
        r2 = 0;
    L_0x0034:
        r4 = r7.outputChannels;
        r5 = r4.length;
        if (r2 >= r5) goto L_0x0050;
    L_0x0039:
        r4 = r4[r2];
        if (r4 >= r9) goto L_0x004a;
    L_0x003d:
        r5 = r7.active;
        if (r4 == r2) goto L_0x0043;
    L_0x0041:
        r6 = 1;
        goto L_0x0044;
    L_0x0043:
        r6 = 0;
    L_0x0044:
        r5 = r5 | r6;
        r7.active = r5;
        r2 = r2 + 1;
        goto L_0x0034;
    L_0x004a:
        r1 = new com.google.android.exoplayer2.audio.AudioProcessor$UnhandledFormatException;
        r1.<init>(r8, r9, r10);
        throw r1;
        return r1;
    L_0x0052:
        r1 = new com.google.android.exoplayer2.audio.AudioProcessor$UnhandledFormatException;
        r1.<init>(r8, r9, r10);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.audio.ChannelMappingAudioProcessor.configure(int, int, int):boolean");
    }

    public void setChannelMap(@Nullable int[] outputChannels) {
        this.pendingOutputChannels = outputChannels;
    }

    public boolean isActive() {
        return this.active;
    }

    public int getOutputChannelCount() {
        int[] iArr = this.outputChannels;
        return iArr == null ? this.channelCount : iArr.length;
    }

    public int getOutputEncoding() {
        return 2;
    }

    public int getOutputSampleRateHz() {
        return this.sampleRateHz;
    }

    public void queueInput(ByteBuffer inputBuffer) {
        Assertions.checkState(this.outputChannels != null);
        int position = inputBuffer.position();
        int limit = inputBuffer.limit();
        int outputSize = (this.outputChannels.length * ((limit - position) / (this.channelCount * 2))) * 2;
        if (this.buffer.capacity() < outputSize) {
            this.buffer = ByteBuffer.allocateDirect(outputSize).order(ByteOrder.nativeOrder());
        } else {
            this.buffer.clear();
        }
        while (position < limit) {
            for (int channelIndex : this.outputChannels) {
                this.buffer.putShort(inputBuffer.getShort((channelIndex * 2) + position));
            }
            position += this.channelCount * 2;
        }
        inputBuffer.position(limit);
        this.buffer.flip();
        this.outputBuffer = this.buffer;
    }

    public void queueEndOfStream() {
        this.inputEnded = true;
    }

    public ByteBuffer getOutput() {
        ByteBuffer outputBuffer = this.outputBuffer;
        this.outputBuffer = EMPTY_BUFFER;
        return outputBuffer;
    }

    public boolean isEnded() {
        return this.inputEnded && this.outputBuffer == EMPTY_BUFFER;
    }

    public void flush() {
        this.outputBuffer = EMPTY_BUFFER;
        this.inputEnded = false;
    }

    public void reset() {
        flush();
        this.buffer = EMPTY_BUFFER;
        this.channelCount = -1;
        this.sampleRateHz = -1;
        this.outputChannels = null;
        this.pendingOutputChannels = null;
        this.active = false;
    }
}
