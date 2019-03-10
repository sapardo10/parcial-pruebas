package com.google.android.exoplayer2.extractor.ts;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.ConstantBitrateSeekMap;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.SeekMap.Unseekable;
import com.google.android.exoplayer2.extractor.ts.TsPayloadReader.TrackIdGenerator;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.ParsableBitArray;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;

public final class AdtsExtractor implements Extractor {
    public static final ExtractorsFactory FACTORY = -$$Lambda$AdtsExtractor$cqGYwjddB4W6E3ogPGiWfjTa23c.INSTANCE;
    public static final int FLAG_ENABLE_CONSTANT_BITRATE_SEEKING = 1;
    private static final int ID3_TAG = Util.getIntegerCodeForString("ID3");
    private static final int MAX_PACKET_SIZE = 2048;
    private static final int MAX_SNIFF_BYTES = 8192;
    private static final int NUM_FRAMES_FOR_AVERAGE_FRAME_SIZE = 1000;
    private int averageFrameSize;
    @Nullable
    private ExtractorOutput extractorOutput;
    private long firstFramePosition;
    private long firstSampleTimestampUs;
    private final long firstStreamSampleTimestampUs;
    private final int flags;
    private boolean hasCalculatedAverageFrameSize;
    private boolean hasOutputSeekMap;
    private final ParsableByteArray packetBuffer;
    private final AdtsReader reader;
    private final ParsableByteArray scratch;
    private final ParsableBitArray scratchBits;
    private boolean startedPacket;

    public AdtsExtractor() {
        this(0);
    }

    public AdtsExtractor(long firstStreamSampleTimestampUs) {
        this(firstStreamSampleTimestampUs, 0);
    }

    public AdtsExtractor(long firstStreamSampleTimestampUs, int flags) {
        this.firstStreamSampleTimestampUs = firstStreamSampleTimestampUs;
        this.firstSampleTimestampUs = firstStreamSampleTimestampUs;
        this.flags = flags;
        this.reader = new AdtsReader(true);
        this.packetBuffer = new ParsableByteArray(2048);
        this.averageFrameSize = -1;
        this.firstFramePosition = -1;
        this.scratch = new ParsableByteArray(10);
        this.scratchBits = new ParsableBitArray(this.scratch.data);
    }

    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        int startPosition = peekId3Header(input);
        int headerPosition = startPosition;
        int totalValidFramesSize = 0;
        int validFramesCount = 0;
        while (true) {
            input.peekFully(this.scratch.data, 0, 2);
            this.scratch.setPosition(0);
            if (AdtsReader.isAdtsSyncWord(this.scratch.readUnsignedShort())) {
                validFramesCount++;
                if (validFramesCount >= 4 && totalValidFramesSize > TsExtractor.TS_PACKET_SIZE) {
                    return true;
                }
                input.peekFully(this.scratch.data, 0, 4);
                this.scratchBits.setPosition(14);
                int frameSize = this.scratchBits.readBits(13);
                if (frameSize <= 6) {
                    return false;
                }
                input.advancePeekPosition(frameSize - 6);
                totalValidFramesSize += frameSize;
            } else {
                validFramesCount = 0;
                totalValidFramesSize = 0;
                input.resetPeekPosition();
                headerPosition++;
                if (headerPosition - startPosition >= 8192) {
                    return false;
                }
                input.advancePeekPosition(headerPosition);
            }
        }
    }

    public void init(ExtractorOutput output) {
        this.extractorOutput = output;
        this.reader.createTracks(output, new TrackIdGenerator(0, 1));
        output.endTracks();
    }

    public void seek(long position, long timeUs) {
        this.startedPacket = false;
        this.reader.seek();
        this.firstSampleTimestampUs = this.firstStreamSampleTimestampUs + timeUs;
    }

    public void release() {
    }

    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        long inputLength = input.getLength();
        boolean canUseConstantBitrateSeeking = ((this.flags & 1) == 0 || inputLength == -1) ? false : true;
        if (canUseConstantBitrateSeeking) {
            calculateAverageFrameSize(input);
        }
        int bytesRead = input.read(this.packetBuffer.data, 0, 2048);
        boolean readEndOfStream = bytesRead == -1;
        maybeOutputSeekMap(inputLength, canUseConstantBitrateSeeking, readEndOfStream);
        if (readEndOfStream) {
            return -1;
        }
        this.packetBuffer.setPosition(0);
        this.packetBuffer.setLimit(bytesRead);
        if (!this.startedPacket) {
            this.reader.packetStarted(this.firstSampleTimestampUs, 4);
            this.startedPacket = true;
        }
        this.reader.consume(this.packetBuffer);
        return 0;
    }

    private int peekId3Header(ExtractorInput input) throws IOException, InterruptedException {
        int firstFramePosition = 0;
        while (true) {
            input.peekFully(this.scratch.data, 0, 10);
            this.scratch.setPosition(0);
            if (this.scratch.readUnsignedInt24() != ID3_TAG) {
                break;
            }
            this.scratch.skipBytes(3);
            int length = this.scratch.readSynchSafeInt();
            firstFramePosition += length + 10;
            input.advancePeekPosition(length);
        }
        input.resetPeekPosition();
        input.advancePeekPosition(firstFramePosition);
        if (this.firstFramePosition == -1) {
            this.firstFramePosition = (long) firstFramePosition;
        }
        return firstFramePosition;
    }

    private void maybeOutputSeekMap(long inputLength, boolean canUseConstantBitrateSeeking, boolean readEndOfStream) {
        if (!this.hasOutputSeekMap) {
            boolean useConstantBitrateSeeking = canUseConstantBitrateSeeking && this.averageFrameSize > 0;
            if (useConstantBitrateSeeking) {
                if (this.reader.getSampleDurationUs() == C0555C.TIME_UNSET && !readEndOfStream) {
                    return;
                }
            }
            ExtractorOutput extractorOutput = (ExtractorOutput) Assertions.checkNotNull(this.extractorOutput);
            if (!useConstantBitrateSeeking || this.reader.getSampleDurationUs() == C0555C.TIME_UNSET) {
                extractorOutput.seekMap(new Unseekable(C0555C.TIME_UNSET));
            } else {
                extractorOutput.seekMap(getConstantBitrateSeekMap(inputLength));
            }
            this.hasOutputSeekMap = true;
        }
    }

    private void calculateAverageFrameSize(ExtractorInput input) throws IOException, InterruptedException {
        if (!this.hasCalculatedAverageFrameSize) {
            this.averageFrameSize = -1;
            input.resetPeekPosition();
            if (input.getPosition() == 0) {
                peekId3Header(input);
            }
            int numValidFrames = 0;
            long totalValidFramesSize = 0;
            while (input.peekFully(this.scratch.data, 0, 2, true)) {
                this.scratch.setPosition(0);
                if (!AdtsReader.isAdtsSyncWord(this.scratch.readUnsignedShort())) {
                    numValidFrames = 0;
                    break;
                } else if (!input.peekFully(this.scratch.data, 0, 4, true)) {
                    break;
                } else {
                    this.scratchBits.setPosition(14);
                    int currentFrameSize = this.scratchBits.readBits(13);
                    if (currentFrameSize > 6) {
                        totalValidFramesSize += (long) currentFrameSize;
                        numValidFrames++;
                        if (numValidFrames == 1000) {
                            break;
                        } else if (!input.advancePeekPosition(currentFrameSize - 6, true)) {
                            break;
                        }
                    } else {
                        this.hasCalculatedAverageFrameSize = true;
                        throw new ParserException("Malformed ADTS stream");
                    }
                }
            }
            input.resetPeekPosition();
            if (numValidFrames > 0) {
                this.averageFrameSize = (int) (totalValidFramesSize / ((long) numValidFrames));
            } else {
                this.averageFrameSize = -1;
            }
            this.hasCalculatedAverageFrameSize = true;
        }
    }

    private SeekMap getConstantBitrateSeekMap(long inputLength) {
        return new ConstantBitrateSeekMap(inputLength, this.firstFramePosition, getBitrateFromFrameSize(this.averageFrameSize, this.reader.getSampleDurationUs()), this.averageFrameSize);
    }

    private static int getBitrateFromFrameSize(int frameSize, long durationUsPerFrame) {
        return (int) ((((long) (frameSize * 8)) * 1000000) / durationUsPerFrame);
    }
}
