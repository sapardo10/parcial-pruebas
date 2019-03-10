package com.google.android.exoplayer2.audio;

import com.google.android.exoplayer2.audio.AudioProcessor.UnhandledFormatException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

final class ResamplingAudioProcessor implements AudioProcessor {
    private ByteBuffer buffer = EMPTY_BUFFER;
    private int channelCount = -1;
    private int encoding = 0;
    private boolean inputEnded;
    private ByteBuffer outputBuffer = EMPTY_BUFFER;
    private int sampleRateHz = -1;

    public boolean configure(int sampleRateHz, int channelCount, int encoding) throws UnhandledFormatException {
        if (!(encoding == 3 || encoding == 2 || encoding == Integer.MIN_VALUE)) {
            if (encoding != 1073741824) {
                throw new UnhandledFormatException(sampleRateHz, channelCount, encoding);
            }
        }
        if (this.sampleRateHz == sampleRateHz && this.channelCount == channelCount && this.encoding == encoding) {
            return false;
        }
        this.sampleRateHz = sampleRateHz;
        this.channelCount = channelCount;
        this.encoding = encoding;
        return true;
    }

    public boolean isActive() {
        int i = this.encoding;
        return (i == 0 || i == 2) ? false : true;
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
        int size = limit - position;
        int i = this.encoding;
        if (i == Integer.MIN_VALUE) {
            i = (size / 3) * 2;
        } else if (i == 3) {
            i = size * 2;
        } else if (i == 1073741824) {
            i = size / 2;
        } else {
            throw new IllegalStateException();
        }
        if (this.buffer.capacity() < i) {
            this.buffer = ByteBuffer.allocateDirect(i).order(ByteOrder.nativeOrder());
        } else {
            this.buffer.clear();
        }
        int i2 = this.encoding;
        int i3;
        if (i2 == Integer.MIN_VALUE) {
            for (i3 = position; i3 < limit; i3 += 3) {
                this.buffer.put(inputBuffer.get(i3 + 1));
                this.buffer.put(inputBuffer.get(i3 + 2));
            }
        } else if (i2 == 3) {
            for (i3 = position; i3 < limit; i3++) {
                this.buffer.put((byte) 0);
                this.buffer.put((byte) ((inputBuffer.get(i3) & 255) - 128));
            }
        } else if (i2 == 1073741824) {
            for (i3 = position; i3 < limit; i3 += 4) {
                this.buffer.put(inputBuffer.get(i3 + 2));
                this.buffer.put(inputBuffer.get(i3 + 3));
            }
        } else {
            throw new IllegalStateException();
        }
        inputBuffer.position(inputBuffer.limit());
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
        this.sampleRateHz = -1;
        this.channelCount = -1;
        this.encoding = 0;
        this.buffer = EMPTY_BUFFER;
    }
}
