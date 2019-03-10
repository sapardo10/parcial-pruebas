package com.google.android.exoplayer2.audio;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.audio.AudioProcessor.UnhandledFormatException;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

public final class SonicAudioProcessor implements AudioProcessor {
    private static final float CLOSE_THRESHOLD = 0.01f;
    public static final float MAXIMUM_PITCH = 8.0f;
    public static final float MAXIMUM_SPEED = 8.0f;
    public static final float MINIMUM_PITCH = 0.1f;
    public static final float MINIMUM_SPEED = 0.1f;
    private static final int MIN_BYTES_FOR_SPEEDUP_CALCULATION = 1024;
    public static final int SAMPLE_RATE_NO_CHANGE = -1;
    private ByteBuffer buffer = EMPTY_BUFFER;
    private int channelCount = -1;
    private long inputBytes;
    private boolean inputEnded;
    private ByteBuffer outputBuffer = EMPTY_BUFFER;
    private long outputBytes;
    private int outputSampleRateHz = -1;
    private int pendingOutputSampleRateHz = -1;
    private float pitch = 1.0f;
    private int sampleRateHz = -1;
    private ShortBuffer shortBuffer = this.buffer.asShortBuffer();
    @Nullable
    private Sonic sonic;
    private float speed = 1.0f;

    public float setSpeed(float speed) {
        speed = Util.constrainValue(speed, 0.1f, 8.0f);
        if (this.speed != speed) {
            this.speed = speed;
            this.sonic = null;
        }
        flush();
        return speed;
    }

    public float setPitch(float pitch) {
        pitch = Util.constrainValue(pitch, 0.1f, 8.0f);
        if (this.pitch != pitch) {
            this.pitch = pitch;
            this.sonic = null;
        }
        flush();
        return pitch;
    }

    public void setOutputSampleRateHz(int sampleRateHz) {
        this.pendingOutputSampleRateHz = sampleRateHz;
    }

    public long scaleDurationForSpeedup(long duration) {
        long j = this.outputBytes;
        if (j >= 1024) {
            long scaleLargeTimestamp;
            int i = r0.outputSampleRateHz;
            int i2 = r0.sampleRateHz;
            if (i == i2) {
                scaleLargeTimestamp = Util.scaleLargeTimestamp(duration, r0.inputBytes, j);
            } else {
                scaleLargeTimestamp = Util.scaleLargeTimestamp(duration, r0.inputBytes * ((long) i), j * ((long) i2));
            }
            return scaleLargeTimestamp;
        }
        double d = (double) r0.speed;
        double d2 = (double) duration;
        Double.isNaN(d);
        Double.isNaN(d2);
        return (long) (d * d2);
    }

    public boolean configure(int sampleRateHz, int channelCount, int encoding) throws UnhandledFormatException {
        if (encoding == 2) {
            int outputSampleRateHz = this.pendingOutputSampleRateHz;
            if (outputSampleRateHz == -1) {
                outputSampleRateHz = sampleRateHz;
            }
            if (this.sampleRateHz == sampleRateHz && this.channelCount == channelCount && this.outputSampleRateHz == outputSampleRateHz) {
                return false;
            }
            this.sampleRateHz = sampleRateHz;
            this.channelCount = channelCount;
            this.outputSampleRateHz = outputSampleRateHz;
            this.sonic = null;
            return true;
        }
        throw new UnhandledFormatException(sampleRateHz, channelCount, encoding);
    }

    public boolean isActive() {
        if (this.sampleRateHz != -1) {
            if (Math.abs(this.speed - 1.0f) < CLOSE_THRESHOLD) {
                if (Math.abs(this.pitch - 1.0f) < CLOSE_THRESHOLD) {
                    if (this.outputSampleRateHz != this.sampleRateHz) {
                    }
                }
            }
            return true;
        }
        return false;
    }

    public int getOutputChannelCount() {
        return this.channelCount;
    }

    public int getOutputEncoding() {
        return 2;
    }

    public int getOutputSampleRateHz() {
        return this.outputSampleRateHz;
    }

    public void queueInput(ByteBuffer inputBuffer) {
        Assertions.checkState(this.sonic != null);
        if (inputBuffer.hasRemaining()) {
            ShortBuffer shortBuffer = inputBuffer.asShortBuffer();
            int inputSize = inputBuffer.remaining();
            this.inputBytes += (long) inputSize;
            this.sonic.queueInput(shortBuffer);
            inputBuffer.position(inputBuffer.position() + inputSize);
        }
        int outputSize = (this.sonic.getFramesAvailable() * this.channelCount) * 2;
        if (outputSize > 0) {
            if (this.buffer.capacity() < outputSize) {
                this.buffer = ByteBuffer.allocateDirect(outputSize).order(ByteOrder.nativeOrder());
                this.shortBuffer = this.buffer.asShortBuffer();
            } else {
                this.buffer.clear();
                this.shortBuffer.clear();
            }
            this.sonic.getOutput(this.shortBuffer);
            this.outputBytes += (long) outputSize;
            this.buffer.limit(outputSize);
            this.outputBuffer = this.buffer;
        }
    }

    public void queueEndOfStream() {
        Assertions.checkState(this.sonic != null);
        this.sonic.queueEndOfStream();
        this.inputEnded = true;
    }

    public ByteBuffer getOutput() {
        ByteBuffer outputBuffer = this.outputBuffer;
        this.outputBuffer = EMPTY_BUFFER;
        return outputBuffer;
    }

    public boolean isEnded() {
        if (this.inputEnded) {
            Sonic sonic = this.sonic;
            if (sonic == null || sonic.getFramesAvailable() == 0) {
                return true;
            }
        }
        return false;
    }

    public void flush() {
        if (isActive()) {
            Sonic sonic = this.sonic;
            if (sonic == null) {
                this.sonic = new Sonic(this.sampleRateHz, this.channelCount, this.speed, this.pitch, this.outputSampleRateHz);
            } else {
                sonic.flush();
            }
        }
        this.outputBuffer = EMPTY_BUFFER;
        this.inputBytes = 0;
        this.outputBytes = 0;
        this.inputEnded = false;
    }

    public void reset() {
        this.speed = 1.0f;
        this.pitch = 1.0f;
        this.channelCount = -1;
        this.sampleRateHz = -1;
        this.outputSampleRateHz = -1;
        this.buffer = EMPTY_BUFFER;
        this.shortBuffer = this.buffer.asShortBuffer();
        this.outputBuffer = EMPTY_BUFFER;
        this.pendingOutputSampleRateHz = -1;
        this.sonic = null;
        this.inputBytes = 0;
        this.outputBytes = 0;
        this.inputEnded = false;
    }
}
