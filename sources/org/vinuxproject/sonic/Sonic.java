package org.vinuxproject.sonic;

public class Sonic {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final int SONIC_AMDF_FREQ = 4000;
    private static final int SONIC_MAX_PITCH = 400;
    private static final int SONIC_MIN_PITCH = 65;
    private short[] downSampleBuffer;
    private short[] inputBuffer;
    private int inputBufferSize;
    private int maxPeriod;
    private int maxRequired;
    private int minPeriod;
    private int newRatePosition = 0;
    private int numChannels;
    private int numInputSamples;
    private int numOutputSamples;
    private int numPitchSamples;
    private int oldRatePosition = 0;
    private short[] outputBuffer;
    private int outputBufferSize;
    private float pitch = 1.0f;
    private short[] pitchBuffer;
    private int pitchBufferSize;
    private int prevMinDiff;
    private int prevPeriod;
    private int quality = 0;
    private float rate = 1.0f;
    private int remainingInputToCopy;
    private int sampleRate;
    private float speed = 1.0f;
    private boolean useChordPitch = false;
    private float volume = 1.0f;

    private short[] resize(short[] oldArray, int newLength) {
        newLength *= this.numChannels;
        short[] newArray = new short[newLength];
        System.arraycopy(oldArray, 0, newArray, 0, oldArray.length <= newLength ? oldArray.length : newLength);
        return newArray;
    }

    private void move(short[] dest, int destPos, short[] source, int sourcePos, int numSamples) {
        int i = this.numChannels;
        System.arraycopy(source, (sourcePos * i) + 0, dest, (destPos * i) + 0, i * numSamples);
    }

    private void scaleSamples(short[] samples, int position, int numSamples, float volume) {
        int fixedPointVolume = (int) (1166016512 * volume);
        int i = this.numChannels;
        int start = position * i;
        i = (i * numSamples) + start;
        for (int xSample = start; xSample < i; xSample++) {
            int value = (samples[xSample] * fixedPointVolume) >> 12;
            if (value > 32767) {
                value = 32767;
            } else if (value < -32767) {
                value = -32767;
            }
            samples[xSample] = (short) value;
        }
    }

    public float getSpeed() {
        return this.speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getRate() {
        return this.rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
        this.oldRatePosition = 0;
        this.newRatePosition = 0;
    }

    public boolean getChordPitch() {
        return this.useChordPitch;
    }

    public void setChordPitch(boolean useChordPitch) {
        this.useChordPitch = useChordPitch;
    }

    public int getQuality() {
        return this.quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public float getVolume() {
        return this.volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    private void allocateStreamBuffers(int sampleRate, int numChannels) {
        this.minPeriod = sampleRate / SONIC_MAX_PITCH;
        this.maxPeriod = sampleRate / 65;
        this.maxRequired = this.maxPeriod * 2;
        int i = this.maxRequired;
        this.inputBufferSize = i;
        this.inputBuffer = new short[(i * numChannels)];
        this.outputBufferSize = i;
        this.outputBuffer = new short[(i * numChannels)];
        this.pitchBufferSize = i;
        this.pitchBuffer = new short[(i * numChannels)];
        this.downSampleBuffer = new short[i];
        this.sampleRate = sampleRate;
        this.numChannels = numChannels;
        this.oldRatePosition = 0;
        this.newRatePosition = 0;
        this.prevPeriod = 0;
    }

    public Sonic(int sampleRate, int numChannels) {
        allocateStreamBuffers(sampleRate, numChannels);
    }

    public int getSampleRate() {
        return this.sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        allocateStreamBuffers(sampleRate, this.numChannels);
    }

    public int getNumChannels() {
        return this.numChannels;
    }

    public void setNumChannels(int numChannels) {
        allocateStreamBuffers(this.sampleRate, numChannels);
    }

    private void enlargeOutputBufferIfNeeded(int numSamples) {
        int i = this.numOutputSamples + numSamples;
        int i2 = this.outputBufferSize;
        if (i > i2) {
            this.outputBufferSize = i2 + ((i2 >> 1) + numSamples);
            this.outputBuffer = resize(this.outputBuffer, this.outputBufferSize);
        }
    }

    private void enlargeInputBufferIfNeeded(int numSamples) {
        int i = this.numInputSamples + numSamples;
        int i2 = this.inputBufferSize;
        if (i > i2) {
            this.inputBufferSize = i2 + ((i2 >> 1) + numSamples);
            this.inputBuffer = resize(this.inputBuffer, this.inputBufferSize);
        }
    }

    private void addFloatSamplesToInputBuffer(float[] samples, int numSamples) {
        if (numSamples != 0) {
            enlargeInputBufferIfNeeded(numSamples);
            int xBuffer = this.numInputSamples * this.numChannels;
            int xSample = 0;
            while (xSample < this.numChannels * numSamples) {
                int xBuffer2 = xBuffer + 1;
                this.inputBuffer[xBuffer] = (short) ((int) (samples[xSample] * 32767.0f));
                xSample++;
                xBuffer = xBuffer2;
            }
            this.numInputSamples += numSamples;
        }
    }

    private void addShortSamplesToInputBuffer(short[] samples, int numSamples) {
        if (numSamples != 0) {
            enlargeInputBufferIfNeeded(numSamples);
            move(this.inputBuffer, this.numInputSamples, samples, 0, numSamples);
            this.numInputSamples += numSamples;
        }
    }

    private void addUnsignedByteSamplesToInputBuffer(byte[] samples, int numSamples) {
        enlargeInputBufferIfNeeded(numSamples);
        int xBuffer = this.numInputSamples * this.numChannels;
        int xSample = 0;
        while (xSample < this.numChannels * numSamples) {
            int xBuffer2 = xBuffer + 1;
            this.inputBuffer[xBuffer] = (short) (((short) ((samples[xSample] & 255) - 128)) << 8);
            xSample++;
            xBuffer = xBuffer2;
        }
        this.numInputSamples += numSamples;
    }

    private void addBytesToInputBuffer(byte[] inBuffer, int numBytes) {
        int numSamples = numBytes / (this.numChannels * 2);
        enlargeInputBufferIfNeeded(numSamples);
        int xBuffer = this.numInputSamples * this.numChannels;
        int xByte = 0;
        while (xByte + 1 < numBytes) {
            int xBuffer2 = xBuffer + 1;
            this.inputBuffer[xBuffer] = (short) ((inBuffer[xByte] & 255) | (inBuffer[xByte + 1] << 8));
            xByte += 2;
            xBuffer = xBuffer2;
        }
        this.numInputSamples += numSamples;
    }

    private void removeInputSamples(int position) {
        int remainingSamples = this.numInputSamples - position;
        short[] sArr = this.inputBuffer;
        move(sArr, 0, sArr, position, remainingSamples);
        this.numInputSamples = remainingSamples;
    }

    private void copyToOutput(short[] samples, int position, int numSamples) {
        enlargeOutputBufferIfNeeded(numSamples);
        move(this.outputBuffer, this.numOutputSamples, samples, position, numSamples);
        this.numOutputSamples += numSamples;
    }

    private int copyInputToOutput(int position) {
        int numSamples = this.remainingInputToCopy;
        if (numSamples > this.maxRequired) {
            numSamples = this.maxRequired;
        }
        copyToOutput(this.inputBuffer, position, numSamples);
        this.remainingInputToCopy -= numSamples;
        return numSamples;
    }

    public int readFloatFromStream(float[] samples, int maxSamples) {
        int numSamples = this.numOutputSamples;
        int remainingSamples = 0;
        if (numSamples == 0) {
            return 0;
        }
        if (numSamples > maxSamples) {
            remainingSamples = numSamples - maxSamples;
            numSamples = maxSamples;
        }
        int xSample = 0;
        while (xSample < this.numChannels * numSamples) {
            int xSample2 = xSample + 1;
            samples[xSample] = ((float) this.outputBuffer[xSample2]) / 32767.0f;
            xSample = xSample2 + 1;
        }
        short[] sArr = this.outputBuffer;
        move(sArr, 0, sArr, numSamples, remainingSamples);
        this.numOutputSamples = remainingSamples;
        return numSamples;
    }

    public int readShortFromStream(short[] samples, int maxSamples) {
        int numSamples = this.numOutputSamples;
        int remainingSamples = 0;
        if (numSamples == 0) {
            return 0;
        }
        if (numSamples > maxSamples) {
            remainingSamples = numSamples - maxSamples;
            numSamples = maxSamples;
        }
        move(samples, 0, this.outputBuffer, 0, numSamples);
        short[] sArr = this.outputBuffer;
        move(sArr, 0, sArr, numSamples, remainingSamples);
        this.numOutputSamples = remainingSamples;
        return numSamples;
    }

    public int readUnsignedByteFromStream(byte[] samples, int maxSamples) {
        int numSamples = this.numOutputSamples;
        int remainingSamples = 0;
        if (numSamples == 0) {
            return 0;
        }
        if (numSamples > maxSamples) {
            remainingSamples = numSamples - maxSamples;
            numSamples = maxSamples;
        }
        for (int xSample = 0; xSample < this.numChannels * numSamples; xSample++) {
            samples[xSample] = (byte) ((this.outputBuffer[xSample] >> 8) + 128);
        }
        short[] sArr = this.outputBuffer;
        move(sArr, 0, sArr, numSamples, remainingSamples);
        this.numOutputSamples = remainingSamples;
        return numSamples;
    }

    public int readBytesFromStream(byte[] outBuffer, int maxBytes) {
        int maxSamples = maxBytes / (this.numChannels * 2);
        int numSamples = this.numOutputSamples;
        int remainingSamples = 0;
        if (numSamples != 0) {
            if (maxSamples != 0) {
                if (numSamples > maxSamples) {
                    remainingSamples = numSamples - maxSamples;
                    numSamples = maxSamples;
                }
                for (int xSample = 0; xSample < this.numChannels * numSamples; xSample++) {
                    short sample = this.outputBuffer[xSample];
                    outBuffer[xSample << 1] = (byte) (sample & 255);
                    outBuffer[(xSample << 1) + 1] = (byte) (sample >> 8);
                }
                short[] sArr = this.outputBuffer;
                move(sArr, 0, sArr, numSamples, remainingSamples);
                this.numOutputSamples = remainingSamples;
                return (numSamples * 2) * this.numChannels;
            }
        }
        return 0;
    }

    public void flushStream() {
        int i;
        int remainingSamples = this.numInputSamples;
        float s = this.speed;
        float f = this.pitch;
        float r = this.rate * f;
        int expectedOutputSamples = this.numOutputSamples + ((int) ((((((float) remainingSamples) / (s / f)) + ((float) this.numPitchSamples)) / r) + 0.5f));
        enlargeInputBufferIfNeeded((this.maxRequired * 2) + remainingSamples);
        int xSample = 0;
        while (true) {
            i = this.maxRequired;
            int i2 = i * 2;
            int i3 = this.numChannels;
            if (xSample >= i2 * i3) {
                break;
            }
            this.inputBuffer[(i3 * remainingSamples) + xSample] = (short) 0;
            xSample++;
        }
        this.numInputSamples += i * 2;
        writeShortToStream(null, 0);
        if (this.numOutputSamples > expectedOutputSamples) {
            this.numOutputSamples = expectedOutputSamples;
        }
        this.numInputSamples = 0;
        this.remainingInputToCopy = 0;
        this.numPitchSamples = 0;
    }

    public int samplesAvailable() {
        return this.numOutputSamples;
    }

    private void downSampleInput(short[] samples, int position, int skip) {
        int numSamples = this.maxRequired / skip;
        int i = this.numChannels;
        int samplesPerValue = i * skip;
        position *= i;
        for (i = 0; i < numSamples; i++) {
            int value = 0;
            for (int j = 0; j < samplesPerValue; j++) {
                value += samples[((i * samplesPerValue) + position) + j];
            }
            this.downSampleBuffer[i] = (short) (value / samplesPerValue);
        }
    }

    private int findPitchPeriodInRange(short[] samples, int position, int minPeriod, int maxPeriod, int[] retDiffs) {
        int bestPeriod = 0;
        int worstPeriod = 255;
        int minDiff = 1;
        int maxDiff = 0;
        position *= this.numChannels;
        for (int period = minPeriod; period <= maxPeriod; period++) {
            int diff = 0;
            for (int i = 0; i < period; i++) {
                short sVal = samples[position + i];
                short pVal = samples[(position + period) + i];
                diff += sVal >= pVal ? sVal - pVal : pVal - sVal;
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
        retDiffs[0] = minDiff / bestPeriod;
        retDiffs[1] = maxDiff / worstPeriod;
        return bestPeriod;
    }

    private boolean prevPeriodBetter(int period, int minDiff, int maxDiff, boolean preferNewPeriod) {
        if (minDiff != 0) {
            if (this.prevPeriod != 0) {
                if (preferNewPeriod) {
                    if (maxDiff > minDiff * 3 || minDiff * 2 <= this.prevMinDiff * 3) {
                        return false;
                    }
                } else if (minDiff <= this.prevMinDiff) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    private int findPitchPeriod(short[] samples, int position, boolean preferNewPeriod) {
        int skip;
        int period;
        int retPeriod;
        int[] diffs = new int[]{0, 0};
        int i = this.sampleRate;
        if (i <= SONIC_AMDF_FREQ || this.quality != 0) {
            skip = 1;
        } else {
            skip = i / SONIC_AMDF_FREQ;
        }
        if (this.numChannels == 1 && skip == 1) {
            period = findPitchPeriodInRange(samples, position, this.minPeriod, this.maxPeriod, diffs);
        } else {
            downSampleInput(samples, position, skip);
            period = findPitchPeriodInRange(this.downSampleBuffer, 0, this.minPeriod / skip, this.maxPeriod / skip, diffs);
            if (skip != 1) {
                int minP;
                int maxP;
                int period2 = period * skip;
                period = period2 - (skip << 2);
                i = (skip << 2) + period2;
                if (period < this.minPeriod) {
                    minP = this.minPeriod;
                } else {
                    minP = period;
                }
                if (i > this.maxPeriod) {
                    maxP = this.maxPeriod;
                } else {
                    maxP = i;
                }
                if (this.numChannels == 1) {
                    period = findPitchPeriodInRange(samples, position, minP, maxP, diffs);
                } else {
                    downSampleInput(samples, position, 1);
                    period = findPitchPeriodInRange(this.downSampleBuffer, 0, minP, maxP, diffs);
                }
            }
        }
        if (prevPeriodBetter(period, diffs[0], diffs[1], preferNewPeriod)) {
            retPeriod = this.prevPeriod;
        } else {
            retPeriod = period;
        }
        this.prevMinDiff = diffs[0];
        this.prevPeriod = period;
        return retPeriod;
    }

    private void overlapAdd(int numSamples, int numChannels, short[] out, int outPos, short[] rampDown, int rampDownPos, short[] rampUp, int rampUpPos) {
        for (int i = 0; i < numChannels; i++) {
            int o = (outPos * numChannels) + i;
            int u = (rampUpPos * numChannels) + i;
            int d = (rampDownPos * numChannels) + i;
            for (int t = 0; t < numSamples; t++) {
                out[o] = (short) (((rampDown[d] * (numSamples - t)) + (rampUp[u] * t)) / numSamples);
                o += numChannels;
                d += numChannels;
                u += numChannels;
            }
        }
    }

    private void overlapAddWithSeparation(int numSamples, int numChannels, int separation, short[] out, int outPos, short[] rampDown, int rampDownPos, short[] rampUp, int rampUpPos) {
        int i = numSamples;
        int i2 = numChannels;
        int i3 = separation;
        for (int i4 = 0; i4 < i2; i4++) {
            int o = (outPos * i2) + i4;
            int u = (rampUpPos * i2) + i4;
            int d = (rampDownPos * i2) + i4;
            for (int t = 0; t < i + i3; t++) {
                if (t < i3) {
                    out[o] = (short) ((rampDown[d] * (i - t)) / i);
                    d += i2;
                } else if (t < i) {
                    out[o] = (short) (((rampDown[d] * (i - t)) + (rampUp[u] * (t - i3))) / i);
                    d += i2;
                    u += i2;
                } else {
                    out[o] = (short) ((rampUp[u] * (t - i3)) / i);
                    u += i2;
                }
                o += i2;
            }
        }
    }

    private void moveNewSamplesToPitchBuffer(int originalNumOutputSamples) {
        int numSamples = this.numOutputSamples - originalNumOutputSamples;
        int i = this.numPitchSamples + numSamples;
        int i2 = this.pitchBufferSize;
        if (i > i2) {
            this.pitchBufferSize = i2 + ((i2 >> 1) + numSamples);
            this.pitchBuffer = resize(this.pitchBuffer, this.pitchBufferSize);
        }
        move(this.pitchBuffer, this.numPitchSamples, this.outputBuffer, originalNumOutputSamples, numSamples);
        this.numOutputSamples = originalNumOutputSamples;
        this.numPitchSamples += numSamples;
    }

    private void removePitchSamples(int numSamples) {
        if (numSamples != 0) {
            short[] sArr = this.pitchBuffer;
            move(sArr, 0, sArr, numSamples, this.numPitchSamples - numSamples);
            this.numPitchSamples -= numSamples;
        }
    }

    private void adjustPitch(int originalNumOutputSamples) {
        int position = 0;
        if (this.numOutputSamples != originalNumOutputSamples) {
            moveNewSamplesToPitchBuffer(originalNumOutputSamples);
            while (this.numPitchSamples - position >= this.maxRequired) {
                int period = findPitchPeriod(this.pitchBuffer, position, false);
                int newPeriod = (int) (((float) period) / this.pitch);
                enlargeOutputBufferIfNeeded(newPeriod);
                int i;
                if (this.pitch >= 1.0f) {
                    i = this.numChannels;
                    short[] sArr = this.outputBuffer;
                    int i2 = this.numOutputSamples;
                    short[] sArr2 = this.pitchBuffer;
                    overlapAdd(newPeriod, i, sArr, i2, sArr2, position, sArr2, (position + period) - newPeriod);
                } else {
                    int separation = newPeriod - period;
                    i = this.numChannels;
                    short[] sArr3 = this.outputBuffer;
                    int i3 = this.numOutputSamples;
                    short[] sArr4 = this.pitchBuffer;
                    overlapAddWithSeparation(period, i, separation, sArr3, i3, sArr4, position, sArr4, position);
                }
                this.numOutputSamples += newPeriod;
                position += period;
            }
            removePitchSamples(position);
        }
    }

    private short interpolate(short[] in, int inPos, int oldSampleRate, int newSampleRate) {
        int i = this.numChannels;
        short left = in[inPos * i];
        short right = in[(inPos * i) + i];
        int position = this.newRatePosition * oldSampleRate;
        int i2 = this.oldRatePosition;
        int leftPosition = i2 * newSampleRate;
        i2 = (i2 + 1) * newSampleRate;
        int ratio = i2 - position;
        int width = i2 - leftPosition;
        return (short) (((ratio * left) + ((width - ratio) * right)) / width);
    }

    private void adjustRate(float rate, int originalNumOutputSamples) {
        int newSampleRate = (int) (((float) this.sampleRate) / rate);
        int oldSampleRate = this.sampleRate;
        while (true) {
            if (newSampleRate <= 16384) {
                if (oldSampleRate <= 16384) {
                    break;
                }
            }
            newSampleRate >>= 1;
            oldSampleRate >>= 1;
        }
        if (this.numOutputSamples != originalNumOutputSamples) {
            moveNewSamplesToPitchBuffer(originalNumOutputSamples);
            int position = 0;
            while (position < this.numPitchSamples - 1) {
                int i;
                int i2;
                while (true) {
                    i = this.oldRatePosition;
                    int i3 = (i + 1) * newSampleRate;
                    i2 = this.newRatePosition;
                    if (i3 <= i2 * oldSampleRate) {
                        break;
                    }
                    enlargeOutputBufferIfNeeded(1);
                    i = 0;
                    while (true) {
                        i3 = this.numChannels;
                        if (i >= i3) {
                            break;
                        }
                        this.outputBuffer[(this.numOutputSamples * i3) + i] = interpolate(this.pitchBuffer, position + i, oldSampleRate, newSampleRate);
                        i++;
                    }
                    this.newRatePosition++;
                    this.numOutputSamples++;
                }
                this.oldRatePosition = i + 1;
                if (this.oldRatePosition == oldSampleRate) {
                    this.oldRatePosition = 0;
                    if (i2 != newSampleRate) {
                        System.out.printf("Assertion failed: newRatePosition != newSampleRate\n", new Object[0]);
                    }
                    this.newRatePosition = 0;
                }
                position++;
            }
            removePitchSamples(position);
        }
    }

    private int skipPitchPeriod(short[] samples, int position, float speed, int period) {
        int newSamples;
        Sonic sonic = this;
        int i = period;
        if (speed >= 2.0f) {
            newSamples = (int) (((float) i) / (speed - 1.0f));
        } else {
            int newSamples2 = period;
            sonic.remainingInputToCopy = (int) ((((float) i) * (2.0f - speed)) / (speed - 1.0f));
            newSamples = newSamples2;
        }
        enlargeOutputBufferIfNeeded(newSamples);
        overlapAdd(newSamples, sonic.numChannels, sonic.outputBuffer, sonic.numOutputSamples, samples, position, samples, position + i);
        sonic.numOutputSamples += newSamples;
        return newSamples;
    }

    private int insertPitchPeriod(short[] samples, int position, float speed, int period) {
        int newSamples;
        if (speed < 0.5f) {
            newSamples = (int) ((((float) period) * speed) / (1.0f - speed));
        } else {
            int newSamples2 = period;
            this.remainingInputToCopy = (int) ((((float) period) * ((2.0f * speed) - 1.0f)) / (1.0f - speed));
            newSamples = newSamples2;
        }
        enlargeOutputBufferIfNeeded(period + newSamples);
        move(this.outputBuffer, this.numOutputSamples, samples, position, period);
        overlapAdd(newSamples, this.numChannels, this.outputBuffer, this.numOutputSamples + period, samples, position + period, samples, position);
        this.numOutputSamples += period + newSamples;
        return newSamples;
    }

    private void changeSpeed(float speed) {
        int numSamples = this.numInputSamples;
        int position = 0;
        if (this.numInputSamples >= this.maxRequired) {
            while (true) {
                int newSamples;
                if (this.remainingInputToCopy > 0) {
                    newSamples = copyInputToOutput(position);
                    position += newSamples;
                    int i = newSamples;
                } else {
                    newSamples = findPitchPeriod(this.inputBuffer, position, true);
                    if (((double) speed) > 1.0d) {
                        position += newSamples + skipPitchPeriod(this.inputBuffer, position, speed, newSamples);
                    } else {
                        position += insertPitchPeriod(this.inputBuffer, position, speed, newSamples);
                    }
                }
                if (this.maxRequired + position > numSamples) {
                    removeInputSamples(position);
                    return;
                }
            }
        }
    }

    private void processStreamInput() {
        int originalNumOutputSamples = this.numOutputSamples;
        float s = this.speed;
        float f = this.pitch;
        s /= f;
        float r = this.rate;
        if (!this.useChordPitch) {
            r *= f;
        }
        if (((double) s) <= 1.00001d) {
            if (((double) s) >= 0.99999d) {
                copyToOutput(this.inputBuffer, 0, this.numInputSamples);
                this.numInputSamples = 0;
                if (!this.useChordPitch && this.pitch != 1.0f) {
                    adjustPitch(originalNumOutputSamples);
                } else if (r != 1.0f) {
                    adjustRate(r, originalNumOutputSamples);
                }
                f = this.volume;
                if (f != 1.0f) {
                    scaleSamples(this.outputBuffer, originalNumOutputSamples, this.numOutputSamples - originalNumOutputSamples, f);
                }
            }
        }
        changeSpeed(s);
        if (!this.useChordPitch) {
        }
        if (r != 1.0f) {
            adjustRate(r, originalNumOutputSamples);
        }
        f = this.volume;
        if (f != 1.0f) {
            scaleSamples(this.outputBuffer, originalNumOutputSamples, this.numOutputSamples - originalNumOutputSamples, f);
        }
    }

    public void writeFloatToStream(float[] samples, int numSamples) {
        addFloatSamplesToInputBuffer(samples, numSamples);
        processStreamInput();
    }

    public void writeShortToStream(short[] samples, int numSamples) {
        addShortSamplesToInputBuffer(samples, numSamples);
        processStreamInput();
    }

    public void writeUnsignedByteToStream(byte[] samples, int numSamples) {
        addUnsignedByteSamplesToInputBuffer(samples, numSamples);
        processStreamInput();
    }

    public void writeBytesToStream(byte[] inBuffer, int numBytes) {
        addBytesToInputBuffer(inBuffer, numBytes);
        processStreamInput();
    }

    public static int changeFloatSpeed(float[] samples, int numSamples, float speed, float pitch, float rate, float volume, boolean useChordPitch, int sampleRate, int numChannels) {
        Sonic stream = new Sonic(sampleRate, numChannels);
        stream.setSpeed(speed);
        stream.setPitch(pitch);
        stream.setRate(rate);
        stream.setVolume(volume);
        stream.setChordPitch(useChordPitch);
        stream.writeFloatToStream(samples, numSamples);
        stream.flushStream();
        numSamples = stream.samplesAvailable();
        stream.readFloatFromStream(samples, numSamples);
        return numSamples;
    }

    public int sonicChangeShortSpeed(short[] samples, int numSamples, float speed, float pitch, float rate, float volume, boolean useChordPitch, int sampleRate, int numChannels) {
        Sonic stream = new Sonic(sampleRate, numChannels);
        stream.setSpeed(speed);
        stream.setPitch(pitch);
        stream.setRate(rate);
        stream.setVolume(volume);
        stream.setChordPitch(useChordPitch);
        stream.writeShortToStream(samples, numSamples);
        stream.flushStream();
        numSamples = stream.samplesAvailable();
        stream.readShortFromStream(samples, numSamples);
        return numSamples;
    }
}
