package com.google.android.exoplayer2.audio;

import com.google.android.exoplayer2.audio.AudioProcessor.UnhandledFormatException;
import com.google.android.exoplayer2.util.Util;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

final class TrimmingAudioProcessor implements AudioProcessor {
    private static final int OUTPUT_ENCODING = 2;
    private ByteBuffer buffer = EMPTY_BUFFER;
    private int bytesPerFrame;
    private int channelCount = -1;
    private byte[] endBuffer = Util.EMPTY_BYTE_ARRAY;
    private int endBufferSize;
    private boolean inputEnded;
    private boolean isActive;
    private ByteBuffer outputBuffer = EMPTY_BUFFER;
    private int pendingTrimStartBytes;
    private boolean receivedInputSinceConfigure;
    private int sampleRateHz = -1;
    private int trimEndFrames;
    private int trimStartFrames;
    private long trimmedFrameCount;

    public void setTrimFrameCount(int trimStartFrames, int trimEndFrames) {
        this.trimStartFrames = trimStartFrames;
        this.trimEndFrames = trimEndFrames;
    }

    public void resetTrimmedFrameCount() {
        this.trimmedFrameCount = 0;
    }

    public long getTrimmedFrameCount() {
        return this.trimmedFrameCount;
    }

    public boolean configure(int sampleRateHz, int channelCount, int encoding) throws UnhandledFormatException {
        if (encoding == 2) {
            boolean z;
            int i = this.endBufferSize;
            if (i > 0) {
                this.trimmedFrameCount += (long) (i / this.bytesPerFrame);
            }
            this.channelCount = channelCount;
            this.sampleRateHz = sampleRateHz;
            this.bytesPerFrame = Util.getPcmFrameSize(2, channelCount);
            int i2 = this.trimEndFrames;
            i = this.bytesPerFrame;
            this.endBuffer = new byte[(i2 * i)];
            this.endBufferSize = 0;
            int i3 = this.trimStartFrames;
            this.pendingTrimStartBytes = i * i3;
            boolean wasActive = this.isActive;
            if (i3 == 0) {
                if (i2 == 0) {
                    z = false;
                    this.isActive = z;
                    this.receivedInputSinceConfigure = false;
                    if (wasActive == this.isActive) {
                        return true;
                    }
                    return false;
                }
            }
            z = true;
            this.isActive = z;
            this.receivedInputSinceConfigure = false;
            if (wasActive == this.isActive) {
                return false;
            }
            return true;
        }
        throw new UnhandledFormatException(sampleRateHz, channelCount, encoding);
    }

    public boolean isActive() {
        return this.isActive;
    }

    public int getOutputChannelCount() {
        return this.channelCount;
    }

    public int getOutputEncoding() {
        return 2;
    }

    public int getOutputSampleRateHz() {
        return this.sampleRateHz;
    }

    public void queueInput(ByteBuffer inputBuffer) {
        int position = inputBuffer.position();
        int limit = inputBuffer.limit();
        int remaining = limit - position;
        if (remaining != 0) {
            this.receivedInputSinceConfigure = true;
            int trimBytes = Math.min(remaining, this.pendingTrimStartBytes);
            this.trimmedFrameCount += (long) (trimBytes / this.bytesPerFrame);
            this.pendingTrimStartBytes -= trimBytes;
            inputBuffer.position(position + trimBytes);
            if (this.pendingTrimStartBytes <= 0) {
                remaining -= trimBytes;
                int remainingBytesToOutput = (this.endBufferSize + remaining) - this.endBuffer.length;
                if (this.buffer.capacity() < remainingBytesToOutput) {
                    this.buffer = ByteBuffer.allocateDirect(remainingBytesToOutput).order(ByteOrder.nativeOrder());
                } else {
                    this.buffer.clear();
                }
                int endBufferBytesToOutput = Util.constrainValue(remainingBytesToOutput, 0, this.endBufferSize);
                this.buffer.put(this.endBuffer, 0, endBufferBytesToOutput);
                int inputBufferBytesToOutput = Util.constrainValue(remainingBytesToOutput - endBufferBytesToOutput, 0, remaining);
                inputBuffer.limit(inputBuffer.position() + inputBufferBytesToOutput);
                this.buffer.put(inputBuffer);
                inputBuffer.limit(limit);
                remaining -= inputBufferBytesToOutput;
                this.endBufferSize -= endBufferBytesToOutput;
                Object obj = this.endBuffer;
                System.arraycopy(obj, endBufferBytesToOutput, obj, 0, this.endBufferSize);
                inputBuffer.get(this.endBuffer, this.endBufferSize, remaining);
                this.endBufferSize += remaining;
                this.buffer.flip();
                this.outputBuffer = this.buffer;
            }
        }
    }

    public void queueEndOfStream() {
        this.inputEnded = true;
    }

    public ByteBuffer getOutput() {
        ByteBuffer outputBuffer = this.outputBuffer;
        if (this.inputEnded && this.endBufferSize > 0 && outputBuffer == EMPTY_BUFFER) {
            int capacity = this.buffer.capacity();
            int i = this.endBufferSize;
            if (capacity < i) {
                this.buffer = ByteBuffer.allocateDirect(i).order(ByteOrder.nativeOrder());
            } else {
                this.buffer.clear();
            }
            this.buffer.put(this.endBuffer, 0, this.endBufferSize);
            this.endBufferSize = 0;
            this.buffer.flip();
            outputBuffer = this.buffer;
        }
        this.outputBuffer = EMPTY_BUFFER;
        return outputBuffer;
    }

    public boolean isEnded() {
        return this.inputEnded && this.endBufferSize == 0 && this.outputBuffer == EMPTY_BUFFER;
    }

    public void flush() {
        this.outputBuffer = EMPTY_BUFFER;
        this.inputEnded = false;
        if (this.receivedInputSinceConfigure) {
            this.pendingTrimStartBytes = 0;
        }
        this.endBufferSize = 0;
    }

    public void reset() {
        flush();
        this.buffer = EMPTY_BUFFER;
        this.channelCount = -1;
        this.sampleRateHz = -1;
        this.endBuffer = Util.EMPTY_BYTE_ARRAY;
    }
}
