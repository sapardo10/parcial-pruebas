package com.google.android.exoplayer2.extractor.ts;

import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.audio.Ac3Util;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.SeekMap.Unseekable;
import com.google.android.exoplayer2.extractor.ts.TsPayloadReader.TrackIdGenerator;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;

public final class Ac3Extractor implements Extractor {
    private static final int AC3_SYNC_WORD = 2935;
    public static final ExtractorsFactory FACTORY = -$$Lambda$Ac3Extractor$c2Fqr1pF6vjFNOhLk9sPPtkNnGE.INSTANCE;
    private static final int ID3_TAG = Util.getIntegerCodeForString("ID3");
    private static final int MAX_SNIFF_BYTES = 8192;
    private static final int MAX_SYNC_FRAME_SIZE = 2786;
    private final long firstSampleTimestampUs;
    private final Ac3Reader reader;
    private final ParsableByteArray sampleData;
    private boolean startedPacket;

    public Ac3Extractor() {
        this(0);
    }

    public Ac3Extractor(long firstSampleTimestampUs) {
        this.firstSampleTimestampUs = firstSampleTimestampUs;
        this.reader = new Ac3Reader();
        this.sampleData = new ParsableByteArray((int) MAX_SYNC_FRAME_SIZE);
    }

    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        int length;
        ParsableByteArray scratch = new ParsableByteArray(10);
        int startPosition = 0;
        while (true) {
            input.peekFully(scratch.data, 0, 10);
            scratch.setPosition(0);
            if (scratch.readUnsignedInt24() != ID3_TAG) {
                break;
            }
            scratch.skipBytes(3);
            length = scratch.readSynchSafeInt();
            startPosition += length + 10;
            input.advancePeekPosition(length);
        }
        input.resetPeekPosition();
        input.advancePeekPosition(startPosition);
        int headerPosition = startPosition;
        length = 0;
        while (true) {
            input.peekFully(scratch.data, 0, 6);
            scratch.setPosition(0);
            if (scratch.readUnsignedShort() != AC3_SYNC_WORD) {
                length = 0;
                input.resetPeekPosition();
                headerPosition++;
                if (headerPosition - startPosition >= 8192) {
                    return false;
                }
                input.advancePeekPosition(headerPosition);
            } else {
                length++;
                if (length >= 4) {
                    return true;
                }
                int frameSize = Ac3Util.parseAc3SyncframeSize(scratch.data);
                if (frameSize == -1) {
                    return false;
                }
                input.advancePeekPosition(frameSize - 6);
            }
        }
    }

    public void init(ExtractorOutput output) {
        this.reader.createTracks(output, new TrackIdGenerator(0, 1));
        output.endTracks();
        output.seekMap(new Unseekable(C0555C.TIME_UNSET));
    }

    public void seek(long position, long timeUs) {
        this.startedPacket = false;
        this.reader.seek();
    }

    public void release() {
    }

    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        int bytesRead = input.read(this.sampleData.data, 0, MAX_SYNC_FRAME_SIZE);
        if (bytesRead == -1) {
            return -1;
        }
        this.sampleData.setPosition(0);
        this.sampleData.setLimit(bytesRead);
        if (!this.startedPacket) {
            this.reader.packetStarted(this.firstSampleTimestampUs, 4);
            this.startedPacket = true;
        }
        this.reader.consume(this.sampleData);
        return 0;
    }
}
