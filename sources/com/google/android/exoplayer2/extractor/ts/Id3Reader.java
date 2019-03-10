package com.google.android.exoplayer2.extractor.ts;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.extractor.ts.TsPayloadReader.TrackIdGenerator;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.ParsableByteArray;

public final class Id3Reader implements ElementaryStreamReader {
    private static final int ID3_HEADER_SIZE = 10;
    private static final String TAG = "Id3Reader";
    private final ParsableByteArray id3Header = new ParsableByteArray(10);
    private TrackOutput output;
    private int sampleBytesRead;
    private int sampleSize;
    private long sampleTimeUs;
    private boolean writingSample;

    public void seek() {
        this.writingSample = false;
    }

    public void createTracks(ExtractorOutput extractorOutput, TrackIdGenerator idGenerator) {
        idGenerator.generateNewId();
        this.output = extractorOutput.track(idGenerator.getTrackId(), 4);
        this.output.format(Format.createSampleFormat(idGenerator.getFormatId(), MimeTypes.APPLICATION_ID3, null, -1, null));
    }

    public void packetStarted(long pesTimeUs, int flags) {
        if ((flags & 4) != 0) {
            this.writingSample = true;
            this.sampleTimeUs = pesTimeUs;
            this.sampleSize = 0;
            this.sampleBytesRead = 0;
        }
    }

    public void consume(ParsableByteArray data) {
        if (this.writingSample) {
            int bytesAvailable = data.bytesLeft();
            int i = this.sampleBytesRead;
            if (i < 10) {
                i = Math.min(bytesAvailable, 10 - i);
                System.arraycopy(data.data, data.getPosition(), this.id3Header.data, this.sampleBytesRead, i);
                if (this.sampleBytesRead + i == 10) {
                    this.id3Header.setPosition(0);
                    if (73 == this.id3Header.readUnsignedByte() && 68 == this.id3Header.readUnsignedByte()) {
                        if (51 == this.id3Header.readUnsignedByte()) {
                            this.id3Header.skipBytes(3);
                            this.sampleSize = this.id3Header.readSynchSafeInt() + 10;
                        }
                    }
                    Log.m10w(TAG, "Discarding invalid ID3 tag");
                    this.writingSample = false;
                    return;
                }
            }
            i = Math.min(bytesAvailable, this.sampleSize - this.sampleBytesRead);
            this.output.sampleData(data, i);
            this.sampleBytesRead += i;
        }
    }

    public void packetFinished() {
        if (this.writingSample) {
            int i = this.sampleSize;
            if (i != 0) {
                if (this.sampleBytesRead == i) {
                    this.output.sampleMetadata(this.sampleTimeUs, 1, i, 0, null);
                    this.writingSample = false;
                }
            }
        }
    }
}
