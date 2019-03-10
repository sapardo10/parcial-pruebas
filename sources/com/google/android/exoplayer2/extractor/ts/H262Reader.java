package com.google.android.exoplayer2.extractor.ts;

import android.util.Pair;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.extractor.ts.TsPayloadReader.TrackIdGenerator;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.NalUnitUtil;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.util.Arrays;
import java.util.Collections;

public final class H262Reader implements ElementaryStreamReader {
    private static final double[] FRAME_RATE_VALUES = new double[]{23.976023976023978d, 24.0d, 25.0d, 29.97002997002997d, 30.0d, 50.0d, 59.94005994005994d, 60.0d};
    private static final int START_EXTENSION = 181;
    private static final int START_GROUP = 184;
    private static final int START_PICTURE = 0;
    private static final int START_SEQUENCE_HEADER = 179;
    private static final int START_USER_DATA = 178;
    private final CsdBuffer csdBuffer;
    private String formatId;
    private long frameDurationUs;
    private boolean hasOutputFormat;
    private TrackOutput output;
    private long pesTimeUs;
    private final boolean[] prefixFlags;
    private boolean sampleHasPicture;
    private boolean sampleIsKeyframe;
    private long samplePosition;
    private long sampleTimeUs;
    private boolean startedFirstSample;
    private long totalBytesWritten;
    private final NalUnitTargetBuffer userData;
    private final ParsableByteArray userDataParsable;
    private final UserDataReader userDataReader;

    private static final class CsdBuffer {
        private static final byte[] START_CODE = new byte[]{(byte) 0, (byte) 0, (byte) 1};
        public byte[] data;
        private boolean isFilling;
        public int length;
        public int sequenceExtensionPosition;

        public CsdBuffer(int initialCapacity) {
            this.data = new byte[initialCapacity];
        }

        public void reset() {
            this.isFilling = false;
            this.length = 0;
            this.sequenceExtensionPosition = 0;
        }

        public boolean onStartCode(int startCodeValue, int bytesAlreadyPassed) {
            if (this.isFilling) {
                this.length -= bytesAlreadyPassed;
                if (this.sequenceExtensionPosition == 0 && startCodeValue == H262Reader.START_EXTENSION) {
                    this.sequenceExtensionPosition = this.length;
                } else {
                    this.isFilling = false;
                    return true;
                }
            } else if (startCodeValue == H262Reader.START_SEQUENCE_HEADER) {
                this.isFilling = true;
            }
            byte[] bArr = START_CODE;
            onData(bArr, 0, bArr.length);
            return false;
        }

        public void onData(byte[] newData, int offset, int limit) {
            if (this.isFilling) {
                int readLength = limit - offset;
                byte[] bArr = this.data;
                int length = bArr.length;
                int i = this.length;
                if (length < i + readLength) {
                    this.data = Arrays.copyOf(bArr, (i + readLength) * 2);
                }
                System.arraycopy(newData, offset, this.data, this.length, readLength);
                this.length += readLength;
            }
        }
    }

    public H262Reader() {
        this(null);
    }

    public H262Reader(UserDataReader userDataReader) {
        this.userDataReader = userDataReader;
        this.prefixFlags = new boolean[4];
        this.csdBuffer = new CsdBuffer(128);
        if (userDataReader != null) {
            this.userData = new NalUnitTargetBuffer(START_USER_DATA, 128);
            this.userDataParsable = new ParsableByteArray();
            return;
        }
        this.userData = null;
        this.userDataParsable = null;
    }

    public void seek() {
        NalUnitUtil.clearPrefixFlags(this.prefixFlags);
        this.csdBuffer.reset();
        if (this.userDataReader != null) {
            this.userData.reset();
        }
        this.totalBytesWritten = 0;
        this.startedFirstSample = false;
    }

    public void createTracks(ExtractorOutput extractorOutput, TrackIdGenerator idGenerator) {
        idGenerator.generateNewId();
        this.formatId = idGenerator.getFormatId();
        this.output = extractorOutput.track(idGenerator.getTrackId(), 2);
        UserDataReader userDataReader = this.userDataReader;
        if (userDataReader != null) {
            userDataReader.createTracks(extractorOutput, idGenerator);
        }
    }

    public void packetStarted(long pesTimeUs, int flags) {
        this.pesTimeUs = pesTimeUs;
    }

    public void consume(ParsableByteArray data) {
        ParsableByteArray parsableByteArray = data;
        int offset = data.getPosition();
        int limit = data.limit();
        byte[] dataArray = parsableByteArray.data;
        this.totalBytesWritten += (long) data.bytesLeft();
        this.output.sampleData(parsableByteArray, data.bytesLeft());
        while (true) {
            int startCodeOffset = NalUnitUtil.findNalUnit(dataArray, offset, limit, r0.prefixFlags);
            if (startCodeOffset == limit) {
                break;
            }
            int bytesAlreadyPassed;
            boolean z;
            int startCodeValue = parsableByteArray.data[startCodeOffset + 3] & 255;
            int lengthToStartCode = startCodeOffset - offset;
            if (!r0.hasOutputFormat) {
                if (lengthToStartCode > 0) {
                    r0.csdBuffer.onData(dataArray, offset, startCodeOffset);
                }
                if (r0.csdBuffer.onStartCode(startCodeValue, lengthToStartCode < 0 ? -lengthToStartCode : 0)) {
                    Pair<Format, Long> result = parseCsdBuffer(r0.csdBuffer, r0.formatId);
                    r0.output.format((Format) result.first);
                    r0.frameDurationUs = ((Long) result.second).longValue();
                    r0.hasOutputFormat = true;
                }
            }
            if (r0.userDataReader != null) {
                bytesAlreadyPassed = 0;
                if (lengthToStartCode > 0) {
                    r0.userData.appendToNalUnit(dataArray, offset, startCodeOffset);
                } else {
                    bytesAlreadyPassed = -lengthToStartCode;
                }
                if (r0.userData.endNalUnit(bytesAlreadyPassed)) {
                    r0.userDataParsable.reset(r0.userData.nalData, NalUnitUtil.unescapeStream(r0.userData.nalData, r0.userData.nalLength));
                    r0.userDataReader.consume(r0.sampleTimeUs, r0.userDataParsable);
                }
                if (startCodeValue == START_USER_DATA && parsableByteArray.data[startCodeOffset + 2] == (byte) 1) {
                    r0.userData.startNalUnit(startCodeValue);
                }
            }
            if (startCodeValue != 0) {
                if (startCodeValue != START_SEQUENCE_HEADER) {
                    if (startCodeValue == START_GROUP) {
                        r0.sampleIsKeyframe = true;
                        offset = startCodeOffset + 3;
                    }
                    offset = startCodeOffset + 3;
                }
            }
            bytesAlreadyPassed = limit - startCodeOffset;
            if (r0.startedFirstSample && r0.sampleHasPicture && r0.hasOutputFormat) {
                r0.output.sampleMetadata(r0.sampleTimeUs, r0.sampleIsKeyframe, ((int) (r0.totalBytesWritten - r0.samplePosition)) - bytesAlreadyPassed, bytesAlreadyPassed, null);
            }
            if (r0.startedFirstSample) {
                if (!r0.sampleHasPicture) {
                    z = false;
                    if (startCodeValue == 0) {
                        z = true;
                    }
                    r0.sampleHasPicture = z;
                    offset = startCodeOffset + 3;
                }
            }
            r0.samplePosition = r0.totalBytesWritten - ((long) bytesAlreadyPassed);
            long j = r0.pesTimeUs;
            if (j == C0555C.TIME_UNSET) {
                j = r0.startedFirstSample ? r0.sampleTimeUs + r0.frameDurationUs : 0;
            }
            r0.sampleTimeUs = j;
            z = false;
            r0.sampleIsKeyframe = false;
            r0.pesTimeUs = C0555C.TIME_UNSET;
            r0.startedFirstSample = true;
            if (startCodeValue == 0) {
                z = true;
            }
            r0.sampleHasPicture = z;
            offset = startCodeOffset + 3;
        }
        if (!r0.hasOutputFormat) {
            r0.csdBuffer.onData(dataArray, offset, limit);
        }
        if (r0.userDataReader != null) {
            r0.userData.appendToNalUnit(dataArray, offset, limit);
        }
    }

    public void packetFinished() {
    }

    private static Pair<Format, Long> parseCsdBuffer(CsdBuffer csdBuffer, String formatId) {
        float pixelWidthHeightRatio;
        int i;
        int i2;
        CsdBuffer csdBuffer2 = csdBuffer;
        byte[] csdData = Arrays.copyOf(csdBuffer2.data, csdBuffer2.length);
        int firstByte = csdData[4] & 255;
        int secondByte = csdData[5] & 255;
        int width = (firstByte << 4) | (secondByte >> 4);
        int height = ((secondByte & 15) << 8) | (csdData[6] & 255);
        int aspectRatioCode = (csdData[7] & PsExtractor.VIDEO_STREAM_MASK) >> 4;
        switch (aspectRatioCode) {
            case 2:
                pixelWidthHeightRatio = ((float) (height * 4)) / ((float) (width * 3));
                break;
            case 3:
                pixelWidthHeightRatio = ((float) (height * 16)) / ((float) (width * 9));
                break;
            case 4:
                pixelWidthHeightRatio = ((float) (height * 121)) / ((float) (width * 100));
                break;
            default:
                pixelWidthHeightRatio = 1.0f;
                break;
        }
        Format format = Format.createVideoSampleFormat(formatId, MimeTypes.VIDEO_MPEG2, null, -1, -1, width, height, -1.0f, Collections.singletonList(csdData), -1, pixelWidthHeightRatio, null);
        long frameDurationUs = 0;
        int frameRateCodeMinusOne = (csdData[7] & 15) - 1;
        if (frameRateCodeMinusOne >= 0) {
            double[] dArr = FRAME_RATE_VALUES;
            if (frameRateCodeMinusOne < dArr.length) {
                double frameRate = dArr[frameRateCodeMinusOne];
                int sequenceExtensionPosition = csdBuffer2.sequenceExtensionPosition;
                int frameRateExtensionN = (csdData[sequenceExtensionPosition + 9] & 96) >> 5;
                int frameRateExtensionD = csdData[sequenceExtensionPosition + 9] & 31;
                if (frameRateExtensionN != frameRateExtensionD) {
                    double d = (double) frameRateExtensionN;
                    Double.isNaN(d);
                    d += 1.0d;
                    double d2 = (double) (frameRateExtensionD + 1);
                    Double.isNaN(d2);
                    frameRate *= d / d2;
                } else {
                    i = aspectRatioCode;
                    i2 = firstByte;
                    int i3 = frameRateExtensionN;
                }
                frameDurationUs = (long) (1000000.0d / frameRate);
                return Pair.create(format, Long.valueOf(frameDurationUs));
            }
        }
        i = aspectRatioCode;
        i2 = firstByte;
        return Pair.create(format, Long.valueOf(frameDurationUs));
    }
}
