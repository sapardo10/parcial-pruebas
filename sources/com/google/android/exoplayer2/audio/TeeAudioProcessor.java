package com.google.android.exoplayer2.audio;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.audio.AudioProcessor.UnhandledFormatException;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class TeeAudioProcessor implements AudioProcessor {
    private final AudioBufferSink audioBufferSink;
    private ByteBuffer buffer = EMPTY_BUFFER;
    private int channelCount = -1;
    private int encoding;
    private boolean inputEnded;
    private boolean isActive;
    private ByteBuffer outputBuffer = EMPTY_BUFFER;
    private int sampleRateHz = -1;

    public interface AudioBufferSink {
        void flush(int i, int i2, int i3);

        void handleBuffer(ByteBuffer byteBuffer);
    }

    public static final class WavFileAudioBufferSink implements AudioBufferSink {
        private static final int FILE_SIZE_MINUS_44_OFFSET = 40;
        private static final int FILE_SIZE_MINUS_8_OFFSET = 4;
        private static final int HEADER_LENGTH = 44;
        private static final String TAG = "WaveFileAudioBufferSink";
        private int bytesWritten;
        private int channelCount;
        private int counter;
        private int encoding;
        private final String outputFileNamePrefix;
        @Nullable
        private RandomAccessFile randomAccessFile;
        private int sampleRateHz;
        private final byte[] scratchBuffer = new byte[1024];
        private final ByteBuffer scratchByteBuffer = ByteBuffer.wrap(this.scratchBuffer).order(ByteOrder.LITTLE_ENDIAN);

        public WavFileAudioBufferSink(String outputFileNamePrefix) {
            this.outputFileNamePrefix = outputFileNamePrefix;
        }

        public void flush(int sampleRateHz, int channelCount, int encoding) {
            try {
                reset();
            } catch (IOException e) {
                Log.m7e(TAG, "Error resetting", e);
            }
            this.sampleRateHz = sampleRateHz;
            this.channelCount = channelCount;
            this.encoding = encoding;
        }

        public void handleBuffer(ByteBuffer buffer) {
            try {
                maybePrepareFile();
                writeBuffer(buffer);
            } catch (IOException e) {
                Log.m7e(TAG, "Error writing data", e);
            }
        }

        private void maybePrepareFile() throws IOException {
            if (this.randomAccessFile == null) {
                RandomAccessFile randomAccessFile = new RandomAccessFile(getNextOutputFileName(), "rw");
                writeFileHeader(randomAccessFile);
                this.randomAccessFile = randomAccessFile;
                this.bytesWritten = 44;
            }
        }

        private void writeFileHeader(RandomAccessFile randomAccessFile) throws IOException {
            randomAccessFile.writeInt(WavUtil.RIFF_FOURCC);
            randomAccessFile.writeInt(-1);
            randomAccessFile.writeInt(WavUtil.WAVE_FOURCC);
            randomAccessFile.writeInt(WavUtil.FMT_FOURCC);
            this.scratchByteBuffer.clear();
            this.scratchByteBuffer.putInt(16);
            this.scratchByteBuffer.putShort((short) WavUtil.getTypeForEncoding(this.encoding));
            this.scratchByteBuffer.putShort((short) this.channelCount);
            this.scratchByteBuffer.putInt(this.sampleRateHz);
            int bytesPerSample = Util.getPcmFrameSize(this.encoding, this.channelCount);
            this.scratchByteBuffer.putInt(this.sampleRateHz * bytesPerSample);
            this.scratchByteBuffer.putShort((short) bytesPerSample);
            this.scratchByteBuffer.putShort((short) ((bytesPerSample * 8) / this.channelCount));
            randomAccessFile.write(this.scratchBuffer, 0, this.scratchByteBuffer.position());
            randomAccessFile.writeInt(WavUtil.DATA_FOURCC);
            randomAccessFile.writeInt(-1);
        }

        private void writeBuffer(ByteBuffer buffer) throws IOException {
            RandomAccessFile randomAccessFile = (RandomAccessFile) Assertions.checkNotNull(this.randomAccessFile);
            while (buffer.hasRemaining()) {
                int bytesToWrite = Math.min(buffer.remaining(), this.scratchBuffer.length);
                buffer.get(this.scratchBuffer, 0, bytesToWrite);
                randomAccessFile.write(this.scratchBuffer, 0, bytesToWrite);
                this.bytesWritten += bytesToWrite;
            }
        }

        private void reset() throws IOException {
            RandomAccessFile randomAccessFile = this.randomAccessFile;
            if (randomAccessFile != null) {
                try {
                    this.scratchByteBuffer.clear();
                    this.scratchByteBuffer.putInt(this.bytesWritten - 8);
                    randomAccessFile.seek(4);
                    randomAccessFile.write(this.scratchBuffer, 0, 4);
                    this.scratchByteBuffer.clear();
                    this.scratchByteBuffer.putInt(this.bytesWritten - 44);
                    randomAccessFile.seek(40);
                    randomAccessFile.write(this.scratchBuffer, 0, 4);
                } catch (IOException e) {
                    Log.m11w(TAG, "Error updating file size", e);
                }
                try {
                    randomAccessFile.close();
                } finally {
                    this.randomAccessFile = null;
                }
            }
        }

        private String getNextOutputFileName() {
            Object[] objArr = new Object[2];
            objArr[0] = this.outputFileNamePrefix;
            int i = this.counter;
            this.counter = i + 1;
            objArr[1] = Integer.valueOf(i);
            return Util.formatInvariant("%s-%04d.wav", objArr);
        }
    }

    public TeeAudioProcessor(AudioBufferSink audioBufferSink) {
        this.audioBufferSink = (AudioBufferSink) Assertions.checkNotNull(audioBufferSink);
    }

    public boolean configure(int sampleRateHz, int channelCount, int encoding) throws UnhandledFormatException {
        this.sampleRateHz = sampleRateHz;
        this.channelCount = channelCount;
        this.encoding = encoding;
        boolean wasActive = this.isActive;
        this.isActive = true;
        return wasActive ^ 1;
    }

    public boolean isActive() {
        return this.isActive;
    }

    public int getOutputChannelCount() {
        return this.channelCount;
    }

    public int getOutputEncoding() {
        return this.encoding;
    }

    public int getOutputSampleRateHz() {
        return this.sampleRateHz;
    }

    public void queueInput(ByteBuffer buffer) {
        int remaining = buffer.remaining();
        if (remaining != 0) {
            this.audioBufferSink.handleBuffer(buffer.asReadOnlyBuffer());
            if (this.buffer.capacity() < remaining) {
                this.buffer = ByteBuffer.allocateDirect(remaining).order(ByteOrder.nativeOrder());
            } else {
                this.buffer.clear();
            }
            this.buffer.put(buffer);
            this.buffer.flip();
            this.outputBuffer = this.buffer;
        }
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
        return this.inputEnded && this.buffer == EMPTY_BUFFER;
    }

    public void flush() {
        this.outputBuffer = EMPTY_BUFFER;
        this.inputEnded = false;
        this.audioBufferSink.flush(this.sampleRateHz, this.channelCount, this.encoding);
    }

    public void reset() {
        flush();
        this.buffer = EMPTY_BUFFER;
        this.sampleRateHz = -1;
        this.channelCount = -1;
        this.encoding = -1;
    }
}
