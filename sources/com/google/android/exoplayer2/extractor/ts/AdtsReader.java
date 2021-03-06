package com.google.android.exoplayer2.extractor.ts;

import android.support.v4.app.FrameMetricsAggregator;
import android.util.Pair;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.DummyTrackOutput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.extractor.ts.TsPayloadReader.TrackIdGenerator;
import com.google.android.exoplayer2.util.CodecSpecificDataUtil;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.ParsableBitArray;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.util.Arrays;
import java.util.Collections;

public final class AdtsReader implements ElementaryStreamReader {
    private static final int CRC_SIZE = 2;
    private static final int HEADER_SIZE = 5;
    private static final int ID3_HEADER_SIZE = 10;
    private static final byte[] ID3_IDENTIFIER = new byte[]{(byte) 73, (byte) 68, (byte) 51};
    private static final int ID3_SIZE_OFFSET = 6;
    private static final int MATCH_STATE_FF = 512;
    private static final int MATCH_STATE_I = 768;
    private static final int MATCH_STATE_ID = 1024;
    private static final int MATCH_STATE_START = 256;
    private static final int MATCH_STATE_VALUE_SHIFT = 8;
    private static final int STATE_CHECKING_ADTS_HEADER = 1;
    private static final int STATE_FINDING_SAMPLE = 0;
    private static final int STATE_READING_ADTS_HEADER = 3;
    private static final int STATE_READING_ID3_HEADER = 2;
    private static final int STATE_READING_SAMPLE = 4;
    private static final String TAG = "AdtsReader";
    private static final int VERSION_UNSET = -1;
    private final ParsableBitArray adtsScratch;
    private int bytesRead;
    private int currentFrameVersion;
    private TrackOutput currentOutput;
    private long currentSampleDuration;
    private final boolean exposeId3;
    private int firstFrameSampleRateIndex;
    private int firstFrameVersion;
    private String formatId;
    private boolean foundFirstFrame;
    private boolean hasCrc;
    private boolean hasOutputFormat;
    private final ParsableByteArray id3HeaderBuffer;
    private TrackOutput id3Output;
    private final String language;
    private int matchState;
    private TrackOutput output;
    private long sampleDurationUs;
    private int sampleSize;
    private int state;
    private long timeUs;

    public AdtsReader(boolean exposeId3) {
        this(exposeId3, null);
    }

    public AdtsReader(boolean exposeId3, String language) {
        this.adtsScratch = new ParsableBitArray(new byte[7]);
        this.id3HeaderBuffer = new ParsableByteArray(Arrays.copyOf(ID3_IDENTIFIER, 10));
        setFindingSampleState();
        this.firstFrameVersion = -1;
        this.firstFrameSampleRateIndex = -1;
        this.sampleDurationUs = C0555C.TIME_UNSET;
        this.exposeId3 = exposeId3;
        this.language = language;
    }

    public static boolean isAdtsSyncWord(int candidateSyncWord) {
        return (65526 & candidateSyncWord) == 65520;
    }

    public void seek() {
        resetSync();
    }

    public void createTracks(ExtractorOutput extractorOutput, TrackIdGenerator idGenerator) {
        idGenerator.generateNewId();
        this.formatId = idGenerator.getFormatId();
        this.output = extractorOutput.track(idGenerator.getTrackId(), 1);
        if (this.exposeId3) {
            idGenerator.generateNewId();
            this.id3Output = extractorOutput.track(idGenerator.getTrackId(), 4);
            this.id3Output.format(Format.createSampleFormat(idGenerator.getFormatId(), MimeTypes.APPLICATION_ID3, null, -1, null));
            return;
        }
        this.id3Output = new DummyTrackOutput();
    }

    public void packetStarted(long pesTimeUs, int flags) {
        this.timeUs = pesTimeUs;
    }

    public void consume(ParsableByteArray data) throws ParserException {
        while (data.bytesLeft() > 0) {
            switch (this.state) {
                case 0:
                    findNextSample(data);
                    break;
                case 1:
                    checkAdtsHeader(data);
                    break;
                case 2:
                    if (!continueRead(data, this.id3HeaderBuffer.data, 10)) {
                        break;
                    }
                    parseId3Header();
                    break;
                case 3:
                    if (!continueRead(data, this.adtsScratch.data, this.hasCrc ? 7 : 5)) {
                        break;
                    }
                    parseAdtsHeader();
                    break;
                case 4:
                    readSample(data);
                    break;
                default:
                    throw new IllegalStateException();
            }
        }
    }

    public void packetFinished() {
    }

    public long getSampleDurationUs() {
        return this.sampleDurationUs;
    }

    private void resetSync() {
        this.foundFirstFrame = false;
        setFindingSampleState();
    }

    private boolean continueRead(ParsableByteArray source, byte[] target, int targetLength) {
        int bytesToRead = Math.min(source.bytesLeft(), targetLength - this.bytesRead);
        source.readBytes(target, this.bytesRead, bytesToRead);
        this.bytesRead += bytesToRead;
        return this.bytesRead == targetLength;
    }

    private void setFindingSampleState() {
        this.state = 0;
        this.bytesRead = 0;
        this.matchState = 256;
    }

    private void setReadingId3HeaderState() {
        this.state = 2;
        this.bytesRead = ID3_IDENTIFIER.length;
        this.sampleSize = 0;
        this.id3HeaderBuffer.setPosition(0);
    }

    private void setReadingSampleState(TrackOutput outputToUse, long currentSampleDuration, int priorReadBytes, int sampleSize) {
        this.state = 4;
        this.bytesRead = priorReadBytes;
        this.currentOutput = outputToUse;
        this.currentSampleDuration = currentSampleDuration;
        this.sampleSize = sampleSize;
    }

    private void setReadingAdtsHeaderState() {
        this.state = 3;
        this.bytesRead = 0;
    }

    private void setCheckingAdtsHeaderState() {
        this.state = 1;
        this.bytesRead = 0;
    }

    private void findNextSample(ParsableByteArray pesBuffer) {
        byte[] adtsData = pesBuffer.data;
        int position = pesBuffer.getPosition();
        int endOffset = pesBuffer.limit();
        while (position < endOffset) {
            int position2 = position + 1;
            position = adtsData[position] & 255;
            if (this.matchState == 512 && isAdtsSyncBytes((byte) -1, (byte) position)) {
                if (!this.foundFirstFrame) {
                    if (checkSyncPositionValid(pesBuffer, position2 - 2)) {
                    }
                }
                this.currentFrameVersion = (position & 8) >> 3;
                this.hasCrc = (position & 1) == 0;
                if (this.foundFirstFrame) {
                    setReadingAdtsHeaderState();
                } else {
                    setCheckingAdtsHeaderState();
                }
                pesBuffer.setPosition(position2);
                return;
            }
            int i = this.matchState;
            int i2 = i | position;
            if (i2 == 329) {
                this.matchState = MATCH_STATE_I;
            } else if (i2 == FrameMetricsAggregator.EVERY_DURATION) {
                this.matchState = 512;
            } else if (i2 == 836) {
                this.matchState = 1024;
            } else if (i2 == 1075) {
                setReadingId3HeaderState();
                pesBuffer.setPosition(position2);
                return;
            } else if (i != 256) {
                this.matchState = 256;
                position = position2 - 1;
            }
            position = position2;
        }
        pesBuffer.setPosition(position);
    }

    private void checkAdtsHeader(ParsableByteArray buffer) {
        if (buffer.bytesLeft() != 0) {
            this.adtsScratch.data[0] = buffer.data[buffer.getPosition()];
            this.adtsScratch.setPosition(2);
            int currentFrameSampleRateIndex = this.adtsScratch.readBits(4);
            int i = this.firstFrameSampleRateIndex;
            if (i == -1 || currentFrameSampleRateIndex == i) {
                if (!this.foundFirstFrame) {
                    this.foundFirstFrame = true;
                    this.firstFrameVersion = this.currentFrameVersion;
                    this.firstFrameSampleRateIndex = currentFrameSampleRateIndex;
                }
                setReadingAdtsHeaderState();
                return;
            }
            resetSync();
        }
    }

    private boolean checkSyncPositionValid(ParsableByteArray pesBuffer, int syncPositionCandidate) {
        pesBuffer.setPosition(syncPositionCandidate + 1);
        boolean z = true;
        if (!tryRead(pesBuffer, this.adtsScratch.data, 1)) {
            return false;
        }
        this.adtsScratch.setPosition(4);
        int currentFrameVersion = this.adtsScratch.readBits(1);
        int i = this.firstFrameVersion;
        if (i != -1 && currentFrameVersion != i) {
            return false;
        }
        if (this.firstFrameSampleRateIndex != -1) {
            if (!tryRead(pesBuffer, this.adtsScratch.data, 1)) {
                return true;
            }
            this.adtsScratch.setPosition(2);
            if (this.adtsScratch.readBits(4) != this.firstFrameSampleRateIndex) {
                return false;
            }
            pesBuffer.setPosition(syncPositionCandidate + 2);
        }
        if (!tryRead(pesBuffer, this.adtsScratch.data, 4)) {
            return true;
        }
        this.adtsScratch.setPosition(14);
        int frameSize = this.adtsScratch.readBits(13);
        if (frameSize <= 6) {
            return false;
        }
        i = syncPositionCandidate + frameSize;
        if (i + 1 >= pesBuffer.limit()) {
            return true;
        }
        if (!isAdtsSyncBytes(pesBuffer.data[i], pesBuffer.data[i + 1]) || (this.firstFrameVersion != -1 && ((pesBuffer.data[i + 1] & 8) >> 3) != currentFrameVersion)) {
            z = false;
        }
        return z;
    }

    private boolean isAdtsSyncBytes(byte firstByte, byte secondByte) {
        return isAdtsSyncWord(((firstByte & 255) << 8) | (secondByte & 255));
    }

    private boolean tryRead(ParsableByteArray source, byte[] target, int targetLength) {
        if (source.bytesLeft() < targetLength) {
            return false;
        }
        source.readBytes(target, 0, targetLength);
        return true;
    }

    private void parseId3Header() {
        this.id3Output.sampleData(this.id3HeaderBuffer, 10);
        this.id3HeaderBuffer.setPosition(6);
        setReadingSampleState(this.id3Output, 0, 10, this.id3HeaderBuffer.readSynchSafeInt() + 10);
    }

    private void parseAdtsHeader() throws ParserException {
        int audioObjectType;
        int sampleSize;
        this.adtsScratch.setPosition(0);
        if (this.hasOutputFormat) {
            r6.adtsScratch.skipBits(10);
        } else {
            audioObjectType = r6.adtsScratch.readBits(2) + 1;
            if (audioObjectType != 2) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Detected audio object type: ");
                stringBuilder.append(audioObjectType);
                stringBuilder.append(", but assuming AAC LC.");
                Log.m10w(str, stringBuilder.toString());
                audioObjectType = 2;
            }
            r6.adtsScratch.skipBits(5);
            byte[] audioSpecificConfig = CodecSpecificDataUtil.buildAacAudioSpecificConfig(audioObjectType, r6.firstFrameSampleRateIndex, r6.adtsScratch.readBits(3));
            Pair<Integer, Integer> audioParams = CodecSpecificDataUtil.parseAacAudioSpecificConfig(audioSpecificConfig);
            Format format = Format.createAudioSampleFormat(r6.formatId, MimeTypes.AUDIO_AAC, null, -1, -1, ((Integer) audioParams.second).intValue(), ((Integer) audioParams.first).intValue(), Collections.singletonList(audioSpecificConfig), null, 0, r6.language);
            r6.sampleDurationUs = 1024000000 / ((long) format.sampleRate);
            r6.output.format(format);
            r6.hasOutputFormat = true;
        }
        r6.adtsScratch.skipBits(4);
        audioObjectType = (r6.adtsScratch.readBits(13) - 2) - 5;
        if (r6.hasCrc) {
            sampleSize = audioObjectType - 2;
        } else {
            sampleSize = audioObjectType;
        }
        setReadingSampleState(r6.output, r6.sampleDurationUs, 0, sampleSize);
    }

    private void readSample(ParsableByteArray data) {
        int bytesToRead = Math.min(data.bytesLeft(), this.sampleSize - this.bytesRead);
        this.currentOutput.sampleData(data, bytesToRead);
        this.bytesRead += bytesToRead;
        int i = this.bytesRead;
        int i2 = this.sampleSize;
        if (i == i2) {
            this.currentOutput.sampleMetadata(this.timeUs, 1, i2, 0, null);
            this.timeUs += this.currentSampleDuration;
            setFindingSampleState();
        }
    }
}
