package com.google.android.exoplayer2.audio;

import com.google.android.exoplayer2.util.Assertions;
import java.nio.ShortBuffer;
import java.util.Arrays;

final class Sonic {
    private static final int AMDF_FREQUENCY = 4000;
    private static final int MAXIMUM_PITCH = 400;
    private static final int MINIMUM_PITCH = 65;
    private final int channelCount;
    private final short[] downSampleBuffer;
    private short[] inputBuffer;
    private int inputFrameCount;
    private final int inputSampleRateHz;
    private int maxDiff;
    private final int maxPeriod;
    private final int maxRequiredFrameCount = (this.maxPeriod * 2);
    private int minDiff;
    private final int minPeriod;
    private int newRatePosition;
    private int oldRatePosition;
    private short[] outputBuffer;
    private int outputFrameCount;
    private final float pitch;
    private short[] pitchBuffer;
    private int pitchFrameCount;
    private int prevMinDiff;
    private int prevPeriod;
    private final float rate;
    private int remainingInputToCopyFrameCount;
    private final float speed;

    public Sonic(int inputSampleRateHz, int channelCount, float speed, float pitch, int outputSampleRateHz) {
        this.inputSampleRateHz = inputSampleRateHz;
        this.channelCount = channelCount;
        this.speed = speed;
        this.pitch = pitch;
        this.rate = ((float) inputSampleRateHz) / ((float) outputSampleRateHz);
        this.minPeriod = inputSampleRateHz / MAXIMUM_PITCH;
        this.maxPeriod = inputSampleRateHz / 65;
        int i = this.maxRequiredFrameCount;
        this.downSampleBuffer = new short[i];
        this.inputBuffer = new short[(i * channelCount)];
        this.outputBuffer = new short[(i * channelCount)];
        this.pitchBuffer = new short[(i * channelCount)];
    }

    public void queueInput(ShortBuffer buffer) {
        int framesToWrite = buffer.remaining();
        int i = this.channelCount;
        framesToWrite /= i;
        i = (i * framesToWrite) * 2;
        this.inputBuffer = ensureSpaceForAdditionalFrames(this.inputBuffer, this.inputFrameCount, framesToWrite);
        buffer.get(this.inputBuffer, this.inputFrameCount * this.channelCount, i / 2);
        this.inputFrameCount += framesToWrite;
        processStreamInput();
    }

    public void getOutput(ShortBuffer buffer) {
        int framesToRead = Math.min(buffer.remaining() / this.channelCount, this.outputFrameCount);
        buffer.put(this.outputBuffer, 0, this.channelCount * framesToRead);
        this.outputFrameCount -= framesToRead;
        Object obj = this.outputBuffer;
        int i = this.channelCount;
        System.arraycopy(obj, framesToRead * i, obj, 0, this.outputFrameCount * i);
    }

    public void queueEndOfStream() {
        int i;
        int remainingFrameCount = this.inputFrameCount;
        float s = this.speed;
        float f = this.pitch;
        float r = this.rate * f;
        int expectedOutputFrames = this.outputFrameCount + ((int) ((((((float) remainingFrameCount) / (s / f)) + ((float) this.pitchFrameCount)) / r) + 0.5f));
        this.inputBuffer = ensureSpaceForAdditionalFrames(this.inputBuffer, this.inputFrameCount, (this.maxRequiredFrameCount * 2) + remainingFrameCount);
        int xSample = 0;
        while (true) {
            i = this.maxRequiredFrameCount;
            int i2 = i * 2;
            int i3 = this.channelCount;
            if (xSample >= i2 * i3) {
                break;
            }
            this.inputBuffer[(i3 * remainingFrameCount) + xSample] = (short) 0;
            xSample++;
        }
        this.inputFrameCount += i * 2;
        processStreamInput();
        if (this.outputFrameCount > expectedOutputFrames) {
            this.outputFrameCount = expectedOutputFrames;
        }
        this.inputFrameCount = 0;
        this.remainingInputToCopyFrameCount = 0;
        this.pitchFrameCount = 0;
    }

    public void flush() {
        this.inputFrameCount = 0;
        this.outputFrameCount = 0;
        this.pitchFrameCount = 0;
        this.oldRatePosition = 0;
        this.newRatePosition = 0;
        this.remainingInputToCopyFrameCount = 0;
        this.prevPeriod = 0;
        this.prevMinDiff = 0;
        this.minDiff = 0;
        this.maxDiff = 0;
    }

    public int getFramesAvailable() {
        return this.outputFrameCount;
    }

    private short[] ensureSpaceForAdditionalFrames(short[] buffer, int frameCount, int additionalFrameCount) {
        int currentCapacityFrames = buffer.length;
        int i = this.channelCount;
        currentCapacityFrames /= i;
        if (frameCount + additionalFrameCount <= currentCapacityFrames) {
            return buffer;
        }
        return Arrays.copyOf(buffer, i * (((currentCapacityFrames * 3) / 2) + additionalFrameCount));
    }

    private void removeProcessedInputFrames(int positionFrames) {
        int remainingFrames = this.inputFrameCount - positionFrames;
        Object obj = this.inputBuffer;
        int i = this.channelCount;
        System.arraycopy(obj, positionFrames * i, obj, 0, i * remainingFrames);
        this.inputFrameCount = remainingFrames;
    }

    private void copyToOutput(short[] samples, int positionFrames, int frameCount) {
        this.outputBuffer = ensureSpaceForAdditionalFrames(this.outputBuffer, this.outputFrameCount, frameCount);
        int i = this.channelCount;
        System.arraycopy(samples, positionFrames * i, this.outputBuffer, this.outputFrameCount * i, i * frameCount);
        this.outputFrameCount += frameCount;
    }

    private int copyInputToOutput(int positionFrames) {
        int frameCount = Math.min(this.maxRequiredFrameCount, this.remainingInputToCopyFrameCount);
        copyToOutput(this.inputBuffer, positionFrames, frameCount);
        this.remainingInputToCopyFrameCount -= frameCount;
        return frameCount;
    }

    private void downSampleInput(short[] samples, int position, int skip) {
        int frameCount = this.maxRequiredFrameCount / skip;
        int i = this.channelCount;
        int samplesPerValue = i * skip;
        position *= i;
        for (i = 0; i < frameCount; i++) {
            int value = 0;
            for (int j = 0; j < samplesPerValue; j++) {
                value += samples[((i * samplesPerValue) + position) + j];
            }
            this.downSampleBuffer[i] = (short) (value / samplesPerValue);
        }
    }

    private int findPitchPeriodInRange(short[] samples, int position, int minPeriod, int maxPeriod) {
        int bestPeriod = 0;
        int worstPeriod = 255;
        int minDiff = 1;
        int maxDiff = 0;
        position *= this.channelCount;
        for (int period = minPeriod; period <= maxPeriod; period++) {
            int diff = 0;
            for (int i = 0; i < period; i++) {
                diff += Math.abs(samples[position + i] - samples[(position + period) + i]);
            }
            if (diff * bestPeriod < minDiff * period) {
                minDiff = diff;
                bestPeriod = period;
            }
            if (diff * worstPeriod > maxDiff * period) {
                maxDiff = diff;
                worstPeriod = period;
            }
        }
        this.minDiff = minDiff / bestPeriod;
        this.maxDiff = maxDiff / worstPeriod;
        return bestPeriod;
    }

    private boolean previousPeriodBetter(int minDiff, int maxDiff) {
        if (minDiff != 0) {
            if (this.prevPeriod != 0) {
                if (maxDiff <= minDiff * 3 && minDiff * 2 > this.prevMinDiff * 3) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    private int findPitchPeriod(short[] samples, int position) {
        int period;
        int retPeriod;
        int i = this.inputSampleRateHz;
        i = i > AMDF_FREQUENCY ? i / AMDF_FREQUENCY : 1;
        if (this.channelCount == 1 && i == 1) {
            period = findPitchPeriodInRange(samples, position, this.minPeriod, this.maxPeriod);
        } else {
            downSampleInput(samples, position, i);
            period = findPitchPeriodInRange(this.downSampleBuffer, 0, this.minPeriod / i, this.maxPeriod / i);
            if (i != 1) {
                period *= i;
                int minP = period - (i * 4);
                int maxP = (i * 4) + period;
                if (minP < this.minPeriod) {
                    minP = this.minPeriod;
                }
                if (maxP > this.maxPeriod) {
                    maxP = this.maxPeriod;
                }
                if (this.channelCount == 1) {
                    period = findPitchPeriodInRange(samples, position, minP, maxP);
                } else {
                    downSampleInput(samples, position, 1);
                    period = findPitchPeriodInRange(this.downSampleBuffer, 0, minP, maxP);
                }
            }
        }
        if (previousPeriodBetter(this.minDiff, this.maxDiff)) {
            retPeriod = this.prevPeriod;
        } else {
            retPeriod = period;
        }
        this.prevMinDiff = this.minDiff;
        this.prevPeriod = period;
        return retPeriod;
    }

    private void moveNewSamplesToPitchBuffer(int originalOutputFrameCount) {
        int frameCount = this.outputFrameCount - originalOutputFrameCount;
        this.pitchBuffer = ensureSpaceForAdditionalFrames(this.pitchBuffer, this.pitchFrameCount, frameCount);
        Object obj = this.outputBuffer;
        int i = this.channelCount;
        System.arraycopy(obj, originalOutputFrameCount * i, this.pitchBuffer, this.pitchFrameCount * i, i * frameCount);
        this.outputFrameCount = originalOutputFrameCount;
        this.pitchFrameCount += frameCount;
    }

    private void removePitchFrames(int frameCount) {
        if (frameCount != 0) {
            Object obj = this.pitchBuffer;
            int i = this.channelCount;
            System.arraycopy(obj, frameCount * i, obj, 0, (this.pitchFrameCount - frameCount) * i);
            this.pitchFrameCount -= frameCount;
        }
    }

    private short interpolate(short[] in, int inPos, int oldSampleRate, int newSampleRate) {
        short left = in[inPos];
        short right = in[this.channelCount + inPos];
        int position = this.newRatePosition * oldSampleRate;
        int i = this.oldRatePosition;
        int leftPosition = i * newSampleRate;
        i = (i + 1) * newSampleRate;
        int ratio = i - position;
        int width = i - leftPosition;
        return (short) (((ratio * left) + ((width - ratio) * right)) / width);
    }

    private void adjustRate(float rate, int originalOutputFrameCount) {
        if (this.outputFrameCount != originalOutputFrameCount) {
            int newSampleRate = (int) (((float) this.inputSampleRateHz) / rate);
            int oldSampleRate = this.inputSampleRateHz;
            while (true) {
                if (newSampleRate <= 16384) {
                    if (oldSampleRate <= 16384) {
                        break;
                    }
                }
                newSampleRate /= 2;
                oldSampleRate /= 2;
            }
            moveNewSamplesToPitchBuffer(originalOutputFrameCount);
            int position = 0;
            while (true) {
                int i = this.pitchFrameCount;
                boolean z = true;
                if (position < i - 1) {
                    int i2;
                    while (true) {
                        i = this.oldRatePosition;
                        int i3 = (i + 1) * newSampleRate;
                        i2 = this.newRatePosition;
                        if (i3 <= i2 * oldSampleRate) {
                            break;
                        }
                        this.outputBuffer = ensureSpaceForAdditionalFrames(this.outputBuffer, this.outputFrameCount, 1);
                        i = 0;
                        while (true) {
                            i3 = this.channelCount;
                            if (i >= i3) {
                                break;
                            }
                            this.outputBuffer[(this.outputFrameCount * i3) + i] = interpolate(this.pitchBuffer, (i3 * position) + i, oldSampleRate, newSampleRate);
                            i++;
                        }
                        this.newRatePosition++;
                        this.outputFrameCount++;
                    }
                    this.oldRatePosition = i + 1;
                    if (this.oldRatePosition == oldSampleRate) {
                        this.oldRatePosition = 0;
                        if (i2 != newSampleRate) {
                            z = false;
                        }
                        Assertions.checkState(z);
                        this.newRatePosition = 0;
                    }
                    position++;
                } else {
                    removePitchFrames(i - 1);
                    return;
                }
            }
        }
    }

    private int skipPitchPeriod(short[] samples, int position, float speed, int period) {
        int newFrameCount;
        if (speed >= 2.0f) {
            newFrameCount = (int) (((float) period) / (speed - 1.0f));
        } else {
            int newFrameCount2 = period;
            this.remainingInputToCopyFrameCount = (int) ((((float) period) * (2.0f - speed)) / (speed - 1.0f));
            newFrameCount = newFrameCount2;
        }
        this.outputBuffer = ensureSpaceForAdditionalFrames(this.outputBuffer, this.outputFrameCount, newFrameCount);
        overlapAdd(newFrameCount, this.channelCount, this.outputBuffer, this.outputFrameCount, samples, position, samples, position + period);
        this.outputFrameCount += newFrameCount;
        return newFrameCount;
    }

    private int insertPitchPeriod(short[] samples, int position, float speed, int period) {
        int newFrameCount;
        int newFrameCount2;
        if (speed < 0.5f) {
            newFrameCount = (int) ((((float) period) * speed) / (1.0f - speed));
        } else {
            newFrameCount2 = period;
            this.remainingInputToCopyFrameCount = (int) ((((float) period) * ((2.0f * speed) - 1.0f)) / (1.0f - speed));
            newFrameCount = newFrameCount2;
        }
        this.outputBuffer = ensureSpaceForAdditionalFrames(this.outputBuffer, this.outputFrameCount, period + newFrameCount);
        newFrameCount2 = this.channelCount;
        System.arraycopy(samples, position * newFrameCount2, this.outputBuffer, this.outputFrameCount * newFrameCount2, newFrameCount2 * period);
        overlapAdd(newFrameCount, this.channelCount, this.outputBuffer, this.outputFrameCount + period, samples, position + period, samples, position);
        this.outputFrameCount += period + newFrameCount;
        return newFrameCount;
    }

    private void changeSpeed(float speed) {
        if (this.inputFrameCount >= this.maxRequiredFrameCount) {
            int frameCount = this.inputFrameCount;
            int positionFrames = 0;
            while (true) {
                if (this.remainingInputToCopyFrameCount > 0) {
                    positionFrames += copyInputToOutput(positionFrames);
                } else {
                    int period = findPitchPeriod(this.inputBuffer, positionFrames);
                    if (((double) speed) > 1.0d) {
                        positionFrames += skipPitchPeriod(this.inputBuffer, positionFrames, speed, period) + period;
                    } else {
                        positionFrames += insertPitchPeriod(this.inputBuffer, positionFrames, speed, period);
                    }
                }
                if (this.maxRequiredFrameCount + positionFrames > frameCount) {
                    removeProcessedInputFrames(positionFrames);
                    return;
                }
            }
        }
    }

    private void processStreamInput() {
        int originalOutputFrameCount = this.outputFrameCount;
        float s = this.speed;
        float f = this.pitch;
        s /= f;
        float r = this.rate * f;
        if (((double) s) <= 1.00001d) {
            if (((double) s) >= 0.99999d) {
                copyToOutput(this.inputBuffer, 0, this.inputFrameCount);
                this.inputFrameCount = 0;
                if (r != 1.0f) {
                    adjustRate(r, originalOutputFrameCount);
                }
            }
        }
        changeSpeed(s);
        if (r != 1.0f) {
            adjustRate(r, originalOutputFrameCount);
        }
    }

    private static void overlapAdd(int frameCount, int channelCount, short[] out, int outPosition, short[] rampDown, int rampDownPosition, short[] rampUp, int rampUpPosition) {
        for (int i = 0; i < channelCount; i++) {
            int o = (outPosition * channelCount) + i;
            int u = (rampUpPosition * channelCount) + i;
            int d = (rampDownPosition * channelCount) + i;
            for (int t = 0; t < frameCount; t++) {
                out[o] = (short) (((rampDown[d] * (frameCount - t)) + (rampUp[u] * t)) / frameCount);
                o += channelCount;
                d += channelCount;
                u += channelCount;
            }
        }
    }
}
