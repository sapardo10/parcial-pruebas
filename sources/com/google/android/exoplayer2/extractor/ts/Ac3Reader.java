package com.google.android.exoplayer2.extractor.ts;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.audio.Ac3Util;
import com.google.android.exoplayer2.audio.Ac3Util.SyncFrameInfo;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.extractor.ts.TsPayloadReader.TrackIdGenerator;
import com.google.android.exoplayer2.util.ParsableBitArray;
import com.google.android.exoplayer2.util.ParsableByteArray;

public final class Ac3Reader implements ElementaryStreamReader {
    private static final int HEADER_SIZE = 128;
    private static final int STATE_FINDING_SYNC = 0;
    private static final int STATE_READING_HEADER = 1;
    private static final int STATE_READING_SAMPLE = 2;
    private int bytesRead;
    private Format format;
    private final ParsableBitArray headerScratchBits;
    private final ParsableByteArray headerScratchBytes;
    private final String language;
    private boolean lastByteWas0B;
    private TrackOutput output;
    private long sampleDurationUs;
    private int sampleSize;
    private int state;
    private long timeUs;
    private String trackFormatId;

    public Ac3Reader() {
        this(null);
    }

    public Ac3Reader(String language) {
        this.headerScratchBits = new ParsableBitArray(new byte[128]);
        this.headerScratchBytes = new ParsableByteArray(this.headerScratchBits.data);
        this.state = 0;
        this.language = language;
    }

    public void seek() {
        this.state = 0;
        this.bytesRead = 0;
        this.lastByteWas0B = false;
    }

    public void createTracks(ExtractorOutput extractorOutput, TrackIdGenerator generator) {
        generator.generateNewId();
        this.trackFormatId = generator.getFormatId();
        this.output = extractorOutput.track(generator.getTrackId(), 1);
    }

    public void packetStarted(long pesTimeUs, int flags) {
        this.timeUs = pesTimeUs;
    }

    public void consume(ParsableByteArray data) {
        while (data.bytesLeft() > 0) {
            switch (this.state) {
                case 0:
                    if (!skipToNextSync(data)) {
                        break;
                    }
                    this.state = 1;
                    this.headerScratchBytes.data[0] = (byte) 11;
                    this.headerScratchBytes.data[1] = (byte) 119;
                    this.bytesRead = 2;
                    break;
                case 1:
                    if (!continueRead(data, this.headerScratchBytes.data, 128)) {
                        break;
                    }
                    parseHeader();
                    this.headerScratchBytes.setPosition(0);
                    this.output.sampleData(this.headerScratchBytes, 128);
                    this.state = 2;
                    break;
                case 2:
                    int bytesToRead = Math.min(data.bytesLeft(), this.sampleSize - this.bytesRead);
                    this.output.sampleData(data, bytesToRead);
                    this.bytesRead += bytesToRead;
                    int i = this.bytesRead;
                    int i2 = this.sampleSize;
                    if (i != i2) {
                        break;
                    }
                    this.output.sampleMetadata(this.timeUs, 1, i2, 0, null);
                    this.timeUs += this.sampleDurationUs;
                    this.state = 0;
                    break;
                default:
                    break;
            }
        }
    }

    public void packetFinished() {
    }

    private boolean continueRead(ParsableByteArray source, byte[] target, int targetLength) {
        int bytesToRead = Math.min(source.bytesLeft(), targetLength - this.bytesRead);
        source.readBytes(target, this.bytesRead, bytesToRead);
        this.bytesRead += bytesToRead;
        return this.bytesRead == targetLength;
    }

    private boolean skipToNextSync(ParsableByteArray pesBuffer) {
        while (true) {
            boolean z = false;
            if (pesBuffer.bytesLeft() <= 0) {
                return false;
            }
            if (this.lastByteWas0B) {
                int secondByte = pesBuffer.readUnsignedByte();
                if (secondByte == 119) {
                    this.lastByteWas0B = false;
                    return true;
                }
                if (secondByte == 11) {
                    z = true;
                }
                this.lastByteWas0B = z;
            } else {
                if (pesBuffer.readUnsignedByte() == 11) {
                    z = true;
                }
                this.lastByteWas0B = z;
            }
        }
    }

    private void parseHeader() {
        this.headerScratchBits.setPosition(0);
        SyncFrameInfo frameInfo = Ac3Util.parseAc3SyncframeInfo(this.headerScratchBits);
        if (this.format != null && frameInfo.channelCount == this.format.channelCount && frameInfo.sampleRate == this.format.sampleRate) {
            if (frameInfo.mimeType == this.format.sampleMimeType) {
                this.sampleSize = frameInfo.frameSize;
                this.sampleDurationUs = (((long) frameInfo.sampleCount) * 1000000) / ((long) this.format.sampleRate);
            }
        }
        this.format = Format.createAudioSampleFormat(this.trackFormatId, frameInfo.mimeType, null, -1, -1, frameInfo.channelCount, frameInfo.sampleRate, null, null, 0, this.language);
        this.output.format(this.format);
        this.sampleSize = frameInfo.frameSize;
        this.sampleDurationUs = (((long) frameInfo.sampleCount) * 1000000) / ((long) this.format.sampleRate);
    }
}
