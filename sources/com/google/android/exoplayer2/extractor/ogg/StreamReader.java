package com.google.android.exoplayer2.extractor.ogg;

import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.SeekMap.Unseekable;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.io.IOException;

abstract class StreamReader {
    private static final int STATE_END_OF_INPUT = 3;
    private static final int STATE_READ_HEADERS = 0;
    private static final int STATE_READ_PAYLOAD = 2;
    private static final int STATE_SKIP_HEADERS = 1;
    private long currentGranule;
    private ExtractorOutput extractorOutput;
    private boolean formatSet;
    private long lengthOfReadPacket;
    private final OggPacket oggPacket = new OggPacket();
    private OggSeeker oggSeeker;
    private long payloadStartPosition;
    private int sampleRate;
    private boolean seekMapSet;
    private SetupData setupData;
    private int state;
    private long targetGranule;
    private TrackOutput trackOutput;

    static class SetupData {
        Format format;
        OggSeeker oggSeeker;

        SetupData() {
        }
    }

    private static final class UnseekableOggSeeker implements OggSeeker {
        private UnseekableOggSeeker() {
        }

        public long read(ExtractorInput input) throws IOException, InterruptedException {
            return -1;
        }

        public long startSeek(long timeUs) {
            return 0;
        }

        public SeekMap createSeekMap() {
            return new Unseekable(C0555C.TIME_UNSET);
        }
    }

    protected abstract long preparePayload(ParsableByteArray parsableByteArray);

    protected abstract boolean readHeaders(ParsableByteArray parsableByteArray, long j, SetupData setupData) throws IOException, InterruptedException;

    void init(ExtractorOutput output, TrackOutput trackOutput) {
        this.extractorOutput = output;
        this.trackOutput = trackOutput;
        reset(true);
    }

    protected void reset(boolean headerData) {
        if (headerData) {
            this.setupData = new SetupData();
            this.payloadStartPosition = 0;
            this.state = 0;
        } else {
            this.state = 1;
        }
        this.targetGranule = -1;
        this.currentGranule = 0;
    }

    final void seek(long position, long timeUs) {
        this.oggPacket.reset();
        if (position == 0) {
            reset(this.seekMapSet ^ 1);
        } else if (this.state != 0) {
            this.targetGranule = this.oggSeeker.startSeek(timeUs);
            this.state = 2;
        }
    }

    final int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        switch (this.state) {
            case 0:
                return readHeaders(input);
            case 1:
                input.skipFully((int) this.payloadStartPosition);
                this.state = 2;
                return 0;
            case 2:
                return readPayload(input, seekPosition);
            default:
                throw new IllegalStateException();
        }
    }

    private int readHeaders(ExtractorInput input) throws IOException, InterruptedException {
        StreamReader streamReader = this;
        boolean readingHeaders = true;
        while (readingHeaders) {
            if (streamReader.oggPacket.populate(input)) {
                streamReader.lengthOfReadPacket = input.getPosition() - streamReader.payloadStartPosition;
                readingHeaders = readHeaders(streamReader.oggPacket.getPayload(), streamReader.payloadStartPosition, streamReader.setupData);
                if (readingHeaders) {
                    streamReader.payloadStartPosition = input.getPosition();
                }
            } else {
                streamReader.state = 3;
                return -1;
            }
        }
        ExtractorInput extractorInput = input;
        streamReader.sampleRate = streamReader.setupData.format.sampleRate;
        if (!streamReader.formatSet) {
            streamReader.trackOutput.format(streamReader.setupData.format);
            streamReader.formatSet = true;
        }
        boolean z;
        if (streamReader.setupData.oggSeeker != null) {
            streamReader.oggSeeker = streamReader.setupData.oggSeeker;
            z = readingHeaders;
        } else if (input.getLength() == -1) {
            streamReader.oggSeeker = new UnseekableOggSeeker();
            z = readingHeaders;
        } else {
            OggPageHeader firstPayloadPageHeader = streamReader.oggPacket.getPageHeader();
            DefaultOggSeeker defaultOggSeeker = r0;
            DefaultOggSeeker defaultOggSeeker2 = new DefaultOggSeeker(streamReader.payloadStartPosition, input.getLength(), this, (long) (firstPayloadPageHeader.headerSize + firstPayloadPageHeader.bodySize), firstPayloadPageHeader.granulePosition, (firstPayloadPageHeader.type & 4) != 0);
            streamReader.oggSeeker = defaultOggSeeker;
        }
        streamReader.setupData = null;
        streamReader.state = 2;
        streamReader.oggPacket.trimPayload();
        return 0;
    }

    private int readPayload(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        ExtractorInput extractorInput = input;
        long position = this.oggSeeker.read(extractorInput);
        if (position >= 0) {
            seekPosition.position = position;
            return 1;
        }
        PositionHolder positionHolder = seekPosition;
        if (position < -1) {
            onSeekEnd(-(2 + position));
        }
        if (!r0.seekMapSet) {
            r0.extractorOutput.seekMap(r0.oggSeeker.createSeekMap());
            r0.seekMapSet = true;
        }
        if (r0.lengthOfReadPacket <= 0) {
            if (!r0.oggPacket.populate(extractorInput)) {
                r0.state = 3;
                return -1;
            }
        }
        r0.lengthOfReadPacket = 0;
        ParsableByteArray payload = r0.oggPacket.getPayload();
        long granulesInPacket = preparePayload(payload);
        if (granulesInPacket >= 0) {
            long timeUs = r0.currentGranule;
            if (timeUs + granulesInPacket >= r0.targetGranule) {
                timeUs = convertGranuleToTime(timeUs);
                r0.trackOutput.sampleData(payload, payload.limit());
                r0.trackOutput.sampleMetadata(timeUs, 1, payload.limit(), 0, null);
                r0.targetGranule = -1;
                r0.currentGranule += granulesInPacket;
                return 0;
            }
        }
        r0.currentGranule += granulesInPacket;
        return 0;
    }

    protected long convertGranuleToTime(long granule) {
        return (1000000 * granule) / ((long) this.sampleRate);
    }

    protected long convertTimeToGranule(long timeUs) {
        return (((long) this.sampleRate) * timeUs) / 1000000;
    }

    protected void onSeekEnd(long currentGranule) {
        this.currentGranule = currentGranule;
    }
}
