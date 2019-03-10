package com.google.android.exoplayer2.extractor.ts;

import android.support.v4.view.InputDeviceCompat;
import android.util.SparseArray;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.SeekMap.Unseekable;
import com.google.android.exoplayer2.extractor.ts.TsPayloadReader.TrackIdGenerator;
import com.google.android.exoplayer2.util.ParsableBitArray;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import java.io.IOException;

public final class PsExtractor implements Extractor {
    public static final int AUDIO_STREAM = 192;
    public static final int AUDIO_STREAM_MASK = 224;
    public static final ExtractorsFactory FACTORY = -$$Lambda$PsExtractor$U8l9TedlJUwsYwV9EOSFo_ngcXY.INSTANCE;
    private static final long MAX_SEARCH_LENGTH = 1048576;
    private static final long MAX_SEARCH_LENGTH_AFTER_AUDIO_AND_VIDEO_FOUND = 8192;
    private static final int MAX_STREAM_ID_PLUS_ONE = 256;
    static final int MPEG_PROGRAM_END_CODE = 441;
    static final int PACKET_START_CODE_PREFIX = 1;
    static final int PACK_START_CODE = 442;
    public static final int PRIVATE_STREAM_1 = 189;
    static final int SYSTEM_HEADER_START_CODE = 443;
    public static final int VIDEO_STREAM = 224;
    public static final int VIDEO_STREAM_MASK = 240;
    private final PsDurationReader durationReader;
    private boolean foundAllTracks;
    private boolean foundAudioTrack;
    private boolean foundVideoTrack;
    private boolean hasOutputSeekMap;
    private long lastTrackPosition;
    private ExtractorOutput output;
    private PsBinarySearchSeeker psBinarySearchSeeker;
    private final ParsableByteArray psPacketBuffer;
    private final SparseArray<PesReader> psPayloadReaders;
    private final TimestampAdjuster timestampAdjuster;

    private static final class PesReader {
        private static final int PES_SCRATCH_SIZE = 64;
        private boolean dtsFlag;
        private int extendedHeaderLength;
        private final ElementaryStreamReader pesPayloadReader;
        private final ParsableBitArray pesScratch = new ParsableBitArray(new byte[64]);
        private boolean ptsFlag;
        private boolean seenFirstDts;
        private long timeUs;
        private final TimestampAdjuster timestampAdjuster;

        public PesReader(ElementaryStreamReader pesPayloadReader, TimestampAdjuster timestampAdjuster) {
            this.pesPayloadReader = pesPayloadReader;
            this.timestampAdjuster = timestampAdjuster;
        }

        public void seek() {
            this.seenFirstDts = false;
            this.pesPayloadReader.seek();
        }

        public void consume(ParsableByteArray data) throws ParserException {
            data.readBytes(this.pesScratch.data, 0, 3);
            this.pesScratch.setPosition(0);
            parseHeader();
            data.readBytes(this.pesScratch.data, 0, this.extendedHeaderLength);
            this.pesScratch.setPosition(0);
            parseHeaderExtension();
            this.pesPayloadReader.packetStarted(this.timeUs, 4);
            this.pesPayloadReader.consume(data);
            this.pesPayloadReader.packetFinished();
        }

        private void parseHeader() {
            this.pesScratch.skipBits(8);
            this.ptsFlag = this.pesScratch.readBit();
            this.dtsFlag = this.pesScratch.readBit();
            this.pesScratch.skipBits(6);
            this.extendedHeaderLength = this.pesScratch.readBits(8);
        }

        private void parseHeaderExtension() {
            this.timeUs = 0;
            if (this.ptsFlag) {
                this.pesScratch.skipBits(4);
                long pts = ((long) this.pesScratch.readBits(3)) << 30;
                this.pesScratch.skipBits(1);
                pts |= (long) (this.pesScratch.readBits(15) << 15);
                this.pesScratch.skipBits(1);
                pts |= (long) this.pesScratch.readBits(15);
                this.pesScratch.skipBits(1);
                if (!this.seenFirstDts && this.dtsFlag) {
                    this.pesScratch.skipBits(4);
                    long dts = ((long) this.pesScratch.readBits(3)) << 30;
                    this.pesScratch.skipBits(1);
                    dts |= (long) (this.pesScratch.readBits(15) << 15);
                    this.pesScratch.skipBits(1);
                    dts |= (long) this.pesScratch.readBits(15);
                    this.pesScratch.skipBits(1);
                    this.timestampAdjuster.adjustTsTimestamp(dts);
                    this.seenFirstDts = true;
                }
                this.timeUs = this.timestampAdjuster.adjustTsTimestamp(pts);
            }
        }
    }

    public PsExtractor() {
        this(new TimestampAdjuster(0));
    }

    public PsExtractor(TimestampAdjuster timestampAdjuster) {
        this.timestampAdjuster = timestampAdjuster;
        this.psPacketBuffer = new ParsableByteArray(4096);
        this.psPayloadReaders = new SparseArray();
        this.durationReader = new PsDurationReader();
    }

    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        byte[] scratch = new byte[14];
        boolean z = false;
        input.peekFully(scratch, 0, 14);
        if (PACK_START_CODE != (((((scratch[0] & 255) << 24) | ((scratch[1] & 255) << 16)) | ((scratch[2] & 255) << 8)) | (scratch[3] & 255)) || (scratch[4] & 196) != 68 || (scratch[6] & 4) != 4 || (scratch[8] & 4) != 4 || (scratch[9] & 1) != 1 || (scratch[12] & 3) != 3) {
            return false;
        }
        input.advancePeekPosition(scratch[13] & 7);
        input.peekFully(scratch, 0, 3);
        if (1 == ((scratch[2] & 255) | (((scratch[0] & 255) << 16) | ((scratch[1] & 255) << 8)))) {
            z = true;
        }
        return z;
    }

    public void init(ExtractorOutput output) {
        this.output = output;
    }

    public void seek(long position, long timeUs) {
        PsBinarySearchSeeker psBinarySearchSeeker;
        int i;
        if (!(this.timestampAdjuster.getTimestampOffsetUs() == C0555C.TIME_UNSET)) {
            if (this.timestampAdjuster.getFirstSampleTimestampUs() != 0) {
                if (this.timestampAdjuster.getFirstSampleTimestampUs() != timeUs) {
                }
            }
            psBinarySearchSeeker = this.psBinarySearchSeeker;
            if (psBinarySearchSeeker != null) {
                psBinarySearchSeeker.setSeekTargetUs(timeUs);
            }
            for (i = 0; i < this.psPayloadReaders.size(); i++) {
                ((PesReader) this.psPayloadReaders.valueAt(i)).seek();
            }
        }
        this.timestampAdjuster.reset();
        this.timestampAdjuster.setFirstSampleTimestampUs(timeUs);
        psBinarySearchSeeker = this.psBinarySearchSeeker;
        if (psBinarySearchSeeker != null) {
            psBinarySearchSeeker.setSeekTargetUs(timeUs);
        }
        for (i = 0; i < this.psPayloadReaders.size(); i++) {
            ((PesReader) this.psPayloadReaders.valueAt(i)).seek();
        }
    }

    public void release() {
    }

    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        PsExtractor psExtractor = this;
        ExtractorInput extractorInput = input;
        PositionHolder positionHolder = seekPosition;
        long inputLength = input.getLength();
        boolean canReadDuration = inputLength != -1;
        if (canReadDuration && !psExtractor.durationReader.isDurationReadFinished()) {
            return psExtractor.durationReader.readDuration(extractorInput, positionHolder);
        }
        maybeOutputSeekMap(inputLength);
        PsBinarySearchSeeker psBinarySearchSeeker = psExtractor.psBinarySearchSeeker;
        if (psBinarySearchSeeker != null && psBinarySearchSeeker.isSeeking()) {
            return psExtractor.psBinarySearchSeeker.handlePendingSeek(extractorInput, positionHolder, null);
        }
        input.resetPeekPosition();
        long peekBytesLeft = inputLength != -1 ? inputLength - input.getPeekPosition() : -1;
        if ((peekBytesLeft != -1 && peekBytesLeft < 4) || !extractorInput.peekFully(psExtractor.psPacketBuffer.data, 0, 4, true)) {
            return -1;
        }
        psExtractor.psPacketBuffer.setPosition(0);
        int nextStartCode = psExtractor.psPacketBuffer.readInt();
        if (nextStartCode == MPEG_PROGRAM_END_CODE) {
            return -1;
        }
        if (nextStartCode == PACK_START_CODE) {
            extractorInput.peekFully(psExtractor.psPacketBuffer.data, 0, 10);
            psExtractor.psPacketBuffer.setPosition(9);
            extractorInput.skipFully((psExtractor.psPacketBuffer.readUnsignedByte() & 7) + 14);
            return 0;
        } else if (nextStartCode == SYSTEM_HEADER_START_CODE) {
            extractorInput.peekFully(psExtractor.psPacketBuffer.data, 0, 2);
            psExtractor.psPacketBuffer.setPosition(0);
            extractorInput.skipFully(psExtractor.psPacketBuffer.readUnsignedShort() + 6);
            return 0;
        } else if (((nextStartCode & InputDeviceCompat.SOURCE_ANY) >> 8) != 1) {
            extractorInput.skipFully(1);
            return 0;
        } else {
            int streamId = nextStartCode & 255;
            PesReader payloadReader = (PesReader) psExtractor.psPayloadReaders.get(streamId);
            if (psExtractor.foundAllTracks) {
            } else {
                if (payloadReader == null) {
                    ElementaryStreamReader elementaryStreamReader = null;
                    if (streamId == PRIVATE_STREAM_1) {
                        elementaryStreamReader = new Ac3Reader();
                        psExtractor.foundAudioTrack = true;
                        psExtractor.lastTrackPosition = input.getPosition();
                    } else {
                        if (streamId & 224) {
                            elementaryStreamReader = new MpegAudioReader();
                            psExtractor.foundAudioTrack = true;
                            psExtractor.lastTrackPosition = input.getPosition();
                        } else if (streamId & VIDEO_STREAM_MASK) {
                            elementaryStreamReader = new H262Reader();
                            psExtractor.foundVideoTrack = true;
                            psExtractor.lastTrackPosition = input.getPosition();
                        }
                    }
                    if (elementaryStreamReader != null) {
                        elementaryStreamReader.createTracks(psExtractor.output, new TrackIdGenerator(streamId, true));
                        canReadDuration = new PesReader(elementaryStreamReader, psExtractor.timestampAdjuster);
                        psExtractor.psPayloadReaders.put(streamId, canReadDuration);
                        payloadReader = canReadDuration;
                    }
                }
                long maxSearchPosition = (psExtractor.foundAudioTrack && psExtractor.foundVideoTrack) ? psExtractor.lastTrackPosition + 8192 : 1048576;
                if (input.getPosition() > maxSearchPosition) {
                    psExtractor.foundAllTracks = true;
                    psExtractor.output.endTracks();
                }
            }
            extractorInput.peekFully(psExtractor.psPacketBuffer.data, 0, 2);
            psExtractor.psPacketBuffer.setPosition(0);
            int pesLength = psExtractor.psPacketBuffer.readUnsignedShort() + 6;
            if (payloadReader == null) {
                extractorInput.skipFully(pesLength);
            } else {
                psExtractor.psPacketBuffer.reset(pesLength);
                extractorInput.readFully(psExtractor.psPacketBuffer.data, 0, pesLength);
                psExtractor.psPacketBuffer.setPosition(6);
                payloadReader.consume(psExtractor.psPacketBuffer);
                ParsableByteArray parsableByteArray = psExtractor.psPacketBuffer;
                parsableByteArray.setLimit(parsableByteArray.capacity());
            }
            return 0;
        }
    }

    private void maybeOutputSeekMap(long inputLength) {
        if (!this.hasOutputSeekMap) {
            this.hasOutputSeekMap = true;
            if (this.durationReader.getDurationUs() != C0555C.TIME_UNSET) {
                this.psBinarySearchSeeker = new PsBinarySearchSeeker(this.durationReader.getScrTimestampAdjuster(), this.durationReader.getDurationUs(), inputLength);
                this.output.seekMap(this.psBinarySearchSeeker.getSeekMap());
                return;
            }
            this.output.seekMap(new Unseekable(this.durationReader.getDurationUs()));
        }
    }
}
