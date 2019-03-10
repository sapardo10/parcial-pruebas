package com.google.android.exoplayer2.extractor.amr;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.ConstantBitrateSeekMap;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.SeekMap.Unseekable;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;

public final class AmrExtractor implements Extractor {
    public static final ExtractorsFactory FACTORY = -$$Lambda$AmrExtractor$lVuGuaAcylUV-_XE4-hSR1hBylI.INSTANCE;
    public static final int FLAG_ENABLE_CONSTANT_BITRATE_SEEKING = 1;
    private static final int MAX_FRAME_SIZE_BYTES = frameSizeBytesByTypeWb[8];
    private static final int NUM_SAME_SIZE_CONSTANT_BIT_RATE_THRESHOLD = 20;
    private static final int SAMPLE_RATE_NB = 8000;
    private static final int SAMPLE_RATE_WB = 16000;
    private static final int SAMPLE_TIME_PER_FRAME_US = 20000;
    private static final byte[] amrSignatureNb = Util.getUtf8Bytes("#!AMR\n");
    private static final byte[] amrSignatureWb = Util.getUtf8Bytes("#!AMR-WB\n");
    private static final int[] frameSizeBytesByTypeNb = new int[]{13, 14, 16, 18, 20, 21, 27, 32, 6, 7, 6, 6, 1, 1, 1, 1};
    private static final int[] frameSizeBytesByTypeWb = new int[]{18, 24, 33, 37, 41, 47, 51, 59, 61, 6, 1, 1, 1, 1, 1, 1};
    private int currentSampleBytesRemaining;
    private int currentSampleSize;
    private long currentSampleTimeUs;
    private ExtractorOutput extractorOutput;
    private long firstSamplePosition;
    private int firstSampleSize;
    private final int flags;
    private boolean hasOutputFormat;
    private boolean hasOutputSeekMap;
    private boolean isWideBand;
    private int numSamplesWithSameSize;
    private final byte[] scratch;
    @Nullable
    private SeekMap seekMap;
    private long timeOffsetUs;
    private TrackOutput trackOutput;

    public AmrExtractor() {
        this(0);
    }

    public AmrExtractor(int flags) {
        this.flags = flags;
        this.scratch = new byte[1];
        this.firstSampleSize = -1;
    }

    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        return readAmrHeader(input);
    }

    public void init(ExtractorOutput extractorOutput) {
        this.extractorOutput = extractorOutput;
        this.trackOutput = extractorOutput.track(0, 1);
        extractorOutput.endTracks();
    }

    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        if (input.getPosition() == 0) {
            if (!readAmrHeader(input)) {
                throw new ParserException("Could not find AMR header.");
            }
        }
        maybeOutputFormat();
        int sampleReadResult = readSample(input);
        maybeOutputSeekMap(input.getLength(), sampleReadResult);
        return sampleReadResult;
    }

    public void seek(long position, long timeUs) {
        this.currentSampleTimeUs = 0;
        this.currentSampleSize = 0;
        this.currentSampleBytesRemaining = 0;
        if (position != 0) {
            SeekMap seekMap = this.seekMap;
            if (seekMap instanceof ConstantBitrateSeekMap) {
                this.timeOffsetUs = ((ConstantBitrateSeekMap) seekMap).getTimeUsAtPosition(position);
                return;
            }
        }
        this.timeOffsetUs = 0;
    }

    public void release() {
    }

    static int frameSizeBytesByTypeNb(int frameType) {
        return frameSizeBytesByTypeNb[frameType];
    }

    static int frameSizeBytesByTypeWb(int frameType) {
        return frameSizeBytesByTypeWb[frameType];
    }

    static byte[] amrSignatureNb() {
        byte[] bArr = amrSignatureNb;
        return Arrays.copyOf(bArr, bArr.length);
    }

    static byte[] amrSignatureWb() {
        byte[] bArr = amrSignatureWb;
        return Arrays.copyOf(bArr, bArr.length);
    }

    private boolean readAmrHeader(ExtractorInput input) throws IOException, InterruptedException {
        if (peekAmrSignature(input, amrSignatureNb)) {
            this.isWideBand = false;
            input.skipFully(amrSignatureNb.length);
            return true;
        } else if (!peekAmrSignature(input, amrSignatureWb)) {
            return false;
        } else {
            this.isWideBand = true;
            input.skipFully(amrSignatureWb.length);
            return true;
        }
    }

    private boolean peekAmrSignature(ExtractorInput input, byte[] amrSignature) throws IOException, InterruptedException {
        input.resetPeekPosition();
        byte[] header = new byte[amrSignature.length];
        input.peekFully(header, 0, amrSignature.length);
        return Arrays.equals(header, amrSignature);
    }

    private void maybeOutputFormat() {
        if (!this.hasOutputFormat) {
            this.hasOutputFormat = true;
            this.trackOutput.format(Format.createAudioSampleFormat(null, this.isWideBand ? MimeTypes.AUDIO_AMR_WB : MimeTypes.AUDIO_AMR_NB, null, -1, MAX_FRAME_SIZE_BYTES, 1, this.isWideBand ? SAMPLE_RATE_WB : 8000, -1, null, null, 0, null));
        }
    }

    private int readSample(ExtractorInput extractorInput) throws IOException, InterruptedException {
        if (this.currentSampleBytesRemaining == 0) {
            try {
                this.currentSampleSize = peekNextSampleSize(extractorInput);
                this.currentSampleBytesRemaining = this.currentSampleSize;
                if (this.firstSampleSize == -1) {
                    this.firstSamplePosition = extractorInput.getPosition();
                    this.firstSampleSize = this.currentSampleSize;
                }
                if (this.firstSampleSize == this.currentSampleSize) {
                    this.numSamplesWithSameSize++;
                }
            } catch (EOFException e) {
                return -1;
            }
        }
        int bytesAppended = this.trackOutput.sampleData(extractorInput, this.currentSampleBytesRemaining, true);
        if (bytesAppended == -1) {
            return -1;
        }
        this.currentSampleBytesRemaining -= bytesAppended;
        if (this.currentSampleBytesRemaining > 0) {
            return 0;
        }
        this.trackOutput.sampleMetadata(this.timeOffsetUs + this.currentSampleTimeUs, 1, this.currentSampleSize, 0, null);
        this.currentSampleTimeUs += 20000;
        return 0;
    }

    private int peekNextSampleSize(ExtractorInput extractorInput) throws IOException, InterruptedException {
        extractorInput.resetPeekPosition();
        extractorInput.peekFully(this.scratch, 0, 1);
        byte frameHeader = this.scratch[0];
        if ((frameHeader & 131) <= 0) {
            return getFrameSizeInBytes((frameHeader >> 3) & 15);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Invalid padding bits for frame header ");
        stringBuilder.append(frameHeader);
        throw new ParserException(stringBuilder.toString());
    }

    private int getFrameSizeInBytes(int frameType) throws ParserException {
        if (isValidFrameType(frameType)) {
            return this.isWideBand ? frameSizeBytesByTypeWb[frameType] : frameSizeBytesByTypeNb[frameType];
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Illegal AMR ");
            stringBuilder.append(this.isWideBand ? "WB" : "NB");
            stringBuilder.append(" frame type ");
            stringBuilder.append(frameType);
            throw new ParserException(stringBuilder.toString());
        }
    }

    private boolean isValidFrameType(int frameType) {
        if (frameType >= 0 && frameType <= 15) {
            if (!isWideBandValidFrameType(frameType)) {
                if (isNarrowBandValidFrameType(frameType)) {
                }
            }
            return true;
        }
        return false;
    }

    private boolean isWideBandValidFrameType(int frameType) {
        return this.isWideBand && (frameType < 10 || frameType > 13);
    }

    private boolean isNarrowBandValidFrameType(int frameType) {
        return !this.isWideBand && (frameType < 12 || frameType > 14);
    }

    private void maybeOutputSeekMap(long inputLength, int sampleReadResult) {
        if (!this.hasOutputSeekMap) {
            if (!((this.flags & 1) == 0 || inputLength == -1)) {
                int i = this.firstSampleSize;
                if (i == -1 || i == this.currentSampleSize) {
                    if (this.numSamplesWithSameSize < 20) {
                        if (sampleReadResult != -1) {
                        }
                    }
                    this.seekMap = getConstantBitrateSeekMap(inputLength);
                    this.extractorOutput.seekMap(this.seekMap);
                    this.hasOutputSeekMap = true;
                }
            }
            this.seekMap = new Unseekable(C0555C.TIME_UNSET);
            this.extractorOutput.seekMap(this.seekMap);
            this.hasOutputSeekMap = true;
        }
    }

    private SeekMap getConstantBitrateSeekMap(long inputLength) {
        return new ConstantBitrateSeekMap(inputLength, this.firstSamplePosition, getBitrateFromFrameSize(this.firstSampleSize, 20000), this.firstSampleSize);
    }

    private static int getBitrateFromFrameSize(int frameSize, long durationUsPerFrame) {
        return (int) ((((long) (frameSize * 8)) * 1000000) / durationUsPerFrame);
    }
}
