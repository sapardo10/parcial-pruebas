package com.google.android.exoplayer2.extractor.ts;

import android.util.SparseArray;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.extractor.ts.TsPayloadReader.TrackIdGenerator;
import com.google.android.exoplayer2.util.CodecSpecificDataUtil;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.NalUnitUtil;
import com.google.android.exoplayer2.util.NalUnitUtil.PpsData;
import com.google.android.exoplayer2.util.NalUnitUtil.SpsData;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.ParsableNalUnitBitArray;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class H264Reader implements ElementaryStreamReader {
    private static final int NAL_UNIT_TYPE_PPS = 8;
    private static final int NAL_UNIT_TYPE_SEI = 6;
    private static final int NAL_UNIT_TYPE_SPS = 7;
    private final boolean allowNonIdrKeyframes;
    private final boolean detectAccessUnits;
    private String formatId;
    private boolean hasOutputFormat;
    private TrackOutput output;
    private long pesTimeUs;
    private final NalUnitTargetBuffer pps = new NalUnitTargetBuffer(8, 128);
    private final boolean[] prefixFlags = new boolean[3];
    private boolean randomAccessIndicator;
    private SampleReader sampleReader;
    private final NalUnitTargetBuffer sei = new NalUnitTargetBuffer(6, 128);
    private final SeiReader seiReader;
    private final ParsableByteArray seiWrapper = new ParsableByteArray();
    private final NalUnitTargetBuffer sps = new NalUnitTargetBuffer(7, 128);
    private long totalBytesWritten;

    private static final class SampleReader {
        private static final int DEFAULT_BUFFER_SIZE = 128;
        private static final int NAL_UNIT_TYPE_AUD = 9;
        private static final int NAL_UNIT_TYPE_IDR = 5;
        private static final int NAL_UNIT_TYPE_NON_IDR = 1;
        private static final int NAL_UNIT_TYPE_PARTITION_A = 2;
        private final boolean allowNonIdrKeyframes;
        private final ParsableNalUnitBitArray bitArray = new ParsableNalUnitBitArray(this.buffer, 0, 0);
        private byte[] buffer = new byte[128];
        private int bufferLength;
        private final boolean detectAccessUnits;
        private boolean isFilling;
        private long nalUnitStartPosition;
        private long nalUnitTimeUs;
        private int nalUnitType;
        private final TrackOutput output;
        private final SparseArray<PpsData> pps = new SparseArray();
        private SliceHeaderData previousSliceHeader = new SliceHeaderData();
        private boolean readingSample;
        private boolean sampleIsKeyframe;
        private long samplePosition;
        private long sampleTimeUs;
        private SliceHeaderData sliceHeader = new SliceHeaderData();
        private final SparseArray<SpsData> sps = new SparseArray();

        private static final class SliceHeaderData {
            private static final int SLICE_TYPE_ALL_I = 7;
            private static final int SLICE_TYPE_I = 2;
            private boolean bottomFieldFlag;
            private boolean bottomFieldFlagPresent;
            private int deltaPicOrderCnt0;
            private int deltaPicOrderCnt1;
            private int deltaPicOrderCntBottom;
            private boolean fieldPicFlag;
            private int frameNum;
            private boolean hasSliceType;
            private boolean idrPicFlag;
            private int idrPicId;
            private boolean isComplete;
            private int nalRefIdc;
            private int picOrderCntLsb;
            private int picParameterSetId;
            private int sliceType;
            private SpsData spsData;

            private SliceHeaderData() {
            }

            public void clear() {
                this.hasSliceType = false;
                this.isComplete = false;
            }

            public void setSliceType(int sliceType) {
                this.sliceType = sliceType;
                this.hasSliceType = true;
            }

            public void setAll(SpsData spsData, int nalRefIdc, int sliceType, int frameNum, int picParameterSetId, boolean fieldPicFlag, boolean bottomFieldFlagPresent, boolean bottomFieldFlag, boolean idrPicFlag, int idrPicId, int picOrderCntLsb, int deltaPicOrderCntBottom, int deltaPicOrderCnt0, int deltaPicOrderCnt1) {
                this.spsData = spsData;
                this.nalRefIdc = nalRefIdc;
                this.sliceType = sliceType;
                this.frameNum = frameNum;
                this.picParameterSetId = picParameterSetId;
                this.fieldPicFlag = fieldPicFlag;
                this.bottomFieldFlagPresent = bottomFieldFlagPresent;
                this.bottomFieldFlag = bottomFieldFlag;
                this.idrPicFlag = idrPicFlag;
                this.idrPicId = idrPicId;
                this.picOrderCntLsb = picOrderCntLsb;
                this.deltaPicOrderCntBottom = deltaPicOrderCntBottom;
                this.deltaPicOrderCnt0 = deltaPicOrderCnt0;
                this.deltaPicOrderCnt1 = deltaPicOrderCnt1;
                this.isComplete = true;
                this.hasSliceType = true;
            }

            public boolean isISlice() {
                if (this.hasSliceType) {
                    int i = this.sliceType;
                    if (i == 7 || i == 2) {
                        return true;
                    }
                }
                return false;
            }

            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            private boolean isFirstVclNalUnitOfPicture(com.google.android.exoplayer2.extractor.ts.H264Reader.SampleReader.SliceHeaderData r4) {
                /*
                r3 = this;
                r0 = r3.isComplete;
                r1 = 1;
                if (r0 == 0) goto L_0x0074;
            L_0x0005:
                r0 = r4.isComplete;
                if (r0 == 0) goto L_0x0073;
            L_0x0009:
                r0 = r3.frameNum;
                r2 = r4.frameNum;
                if (r0 != r2) goto L_0x0073;
            L_0x000f:
                r0 = r3.picParameterSetId;
                r2 = r4.picParameterSetId;
                if (r0 != r2) goto L_0x0073;
            L_0x0015:
                r0 = r3.fieldPicFlag;
                r2 = r4.fieldPicFlag;
                if (r0 != r2) goto L_0x0073;
            L_0x001b:
                r0 = r3.bottomFieldFlagPresent;
                if (r0 == 0) goto L_0x0029;
            L_0x001f:
                r0 = r4.bottomFieldFlagPresent;
                if (r0 == 0) goto L_0x0029;
            L_0x0023:
                r0 = r3.bottomFieldFlag;
                r2 = r4.bottomFieldFlag;
                if (r0 != r2) goto L_0x0073;
            L_0x0029:
                r0 = r3.nalRefIdc;
                r2 = r4.nalRefIdc;
                if (r0 == r2) goto L_0x0033;
            L_0x002f:
                if (r0 == 0) goto L_0x0073;
            L_0x0031:
                if (r2 == 0) goto L_0x0073;
            L_0x0033:
                r0 = r3.spsData;
                r0 = r0.picOrderCountType;
                if (r0 != 0) goto L_0x004b;
            L_0x0039:
                r0 = r4.spsData;
                r0 = r0.picOrderCountType;
                if (r0 != 0) goto L_0x004b;
            L_0x003f:
                r0 = r3.picOrderCntLsb;
                r2 = r4.picOrderCntLsb;
                if (r0 != r2) goto L_0x0073;
            L_0x0045:
                r0 = r3.deltaPicOrderCntBottom;
                r2 = r4.deltaPicOrderCntBottom;
                if (r0 != r2) goto L_0x0073;
            L_0x004b:
                r0 = r3.spsData;
                r0 = r0.picOrderCountType;
                if (r0 != r1) goto L_0x0063;
            L_0x0051:
                r0 = r4.spsData;
                r0 = r0.picOrderCountType;
                if (r0 != r1) goto L_0x0063;
            L_0x0057:
                r0 = r3.deltaPicOrderCnt0;
                r2 = r4.deltaPicOrderCnt0;
                if (r0 != r2) goto L_0x0073;
            L_0x005d:
                r0 = r3.deltaPicOrderCnt1;
                r2 = r4.deltaPicOrderCnt1;
                if (r0 != r2) goto L_0x0073;
            L_0x0063:
                r0 = r3.idrPicFlag;
                r2 = r4.idrPicFlag;
                if (r0 != r2) goto L_0x0073;
            L_0x0069:
                if (r0 == 0) goto L_0x0074;
            L_0x006b:
                if (r2 == 0) goto L_0x0074;
            L_0x006d:
                r0 = r3.idrPicId;
                r2 = r4.idrPicId;
                if (r0 == r2) goto L_0x0074;
            L_0x0073:
                goto L_0x0075;
            L_0x0074:
                r1 = 0;
            L_0x0075:
                return r1;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.ts.H264Reader.SampleReader.SliceHeaderData.isFirstVclNalUnitOfPicture(com.google.android.exoplayer2.extractor.ts.H264Reader$SampleReader$SliceHeaderData):boolean");
            }
        }

        public SampleReader(TrackOutput output, boolean allowNonIdrKeyframes, boolean detectAccessUnits) {
            this.output = output;
            this.allowNonIdrKeyframes = allowNonIdrKeyframes;
            this.detectAccessUnits = detectAccessUnits;
            reset();
        }

        public boolean needsSpsPps() {
            return this.detectAccessUnits;
        }

        public void putSps(SpsData spsData) {
            this.sps.append(spsData.seqParameterSetId, spsData);
        }

        public void putPps(PpsData ppsData) {
            this.pps.append(ppsData.picParameterSetId, ppsData);
        }

        public void reset() {
            this.isFilling = false;
            this.readingSample = false;
            this.sliceHeader.clear();
        }

        public void startNalUnit(long position, int type, long pesTimeUs) {
            this.nalUnitType = type;
            this.nalUnitTimeUs = pesTimeUs;
            this.nalUnitStartPosition = position;
            if (this.allowNonIdrKeyframes) {
                if (this.nalUnitType != 1) {
                }
                SliceHeaderData newSliceHeader = this.previousSliceHeader;
                this.previousSliceHeader = this.sliceHeader;
                this.sliceHeader = newSliceHeader;
                this.sliceHeader.clear();
                this.bufferLength = 0;
                this.isFilling = true;
            }
            if (this.detectAccessUnits) {
                int i = this.nalUnitType;
                if (!(i == 5 || i == 1)) {
                    if (i == 2) {
                    }
                }
                SliceHeaderData newSliceHeader2 = this.previousSliceHeader;
                this.previousSliceHeader = this.sliceHeader;
                this.sliceHeader = newSliceHeader2;
                this.sliceHeader.clear();
                this.bufferLength = 0;
                this.isFilling = true;
            }
        }

        public void appendToNalUnit(byte[] data, int offset, int limit) {
            int i = offset;
            if (this.isFilling) {
                int readLength = limit - i;
                byte[] bArr = r0.buffer;
                int length = bArr.length;
                int i2 = r0.bufferLength;
                if (length < i2 + readLength) {
                    r0.buffer = Arrays.copyOf(bArr, (i2 + readLength) * 2);
                }
                System.arraycopy(data, i, r0.buffer, r0.bufferLength, readLength);
                r0.bufferLength += readLength;
                r0.bitArray.reset(r0.buffer, 0, r0.bufferLength);
                if (r0.bitArray.canReadBits(8)) {
                    r0.bitArray.skipBit();
                    int nalRefIdc = r0.bitArray.readBits(2);
                    r0.bitArray.skipBits(5);
                    if (r0.bitArray.canReadExpGolombCodedNum()) {
                        r0.bitArray.readUnsignedExpGolombCodedInt();
                        if (r0.bitArray.canReadExpGolombCodedNum()) {
                            length = r0.bitArray.readUnsignedExpGolombCodedInt();
                            if (!r0.detectAccessUnits) {
                                r0.isFilling = false;
                                r0.sliceHeader.setSliceType(length);
                            } else if (r0.bitArray.canReadExpGolombCodedNum()) {
                                int picParameterSetId = r0.bitArray.readUnsignedExpGolombCodedInt();
                                if (r0.pps.indexOfKey(picParameterSetId) < 0) {
                                    r0.isFilling = false;
                                    return;
                                }
                                PpsData ppsData = (PpsData) r0.pps.get(picParameterSetId);
                                SpsData spsData = (SpsData) r0.sps.get(ppsData.seqParameterSetId);
                                if (spsData.separateColorPlaneFlag) {
                                    if (r0.bitArray.canReadBits(2)) {
                                        r0.bitArray.skipBits(2);
                                    } else {
                                        return;
                                    }
                                }
                                if (r0.bitArray.canReadBits(spsData.frameNumLength)) {
                                    boolean bottomFieldFlagPresent;
                                    boolean bottomFieldFlag;
                                    int idrPicId;
                                    int picOrderCntLsb;
                                    int deltaPicOrderCntBottom;
                                    int deltaPicOrderCnt0;
                                    int deltaPicOrderCnt1;
                                    boolean fieldPicFlag = false;
                                    int frameNum = r0.bitArray.readBits(spsData.frameNumLength);
                                    if (spsData.frameMbsOnlyFlag) {
                                        bottomFieldFlagPresent = false;
                                        bottomFieldFlag = false;
                                    } else if (r0.bitArray.canReadBits(1)) {
                                        fieldPicFlag = r0.bitArray.readBit();
                                        if (!fieldPicFlag) {
                                            bottomFieldFlagPresent = false;
                                            bottomFieldFlag = false;
                                        } else if (r0.bitArray.canReadBits(1)) {
                                            bottomFieldFlagPresent = true;
                                            bottomFieldFlag = r0.bitArray.readBit();
                                        } else {
                                            return;
                                        }
                                    } else {
                                        return;
                                    }
                                    boolean idrPicFlag = r0.nalUnitType == 5;
                                    if (!idrPicFlag) {
                                        idrPicId = 0;
                                    } else if (r0.bitArray.canReadExpGolombCodedNum()) {
                                        idrPicId = r0.bitArray.readUnsignedExpGolombCodedInt();
                                    } else {
                                        return;
                                    }
                                    if (spsData.picOrderCountType == 0) {
                                        if (r0.bitArray.canReadBits(spsData.picOrderCntLsbLength)) {
                                            picOrderCntLsb = r0.bitArray.readBits(spsData.picOrderCntLsbLength);
                                            if (!ppsData.bottomFieldPicOrderInFramePresentFlag || fieldPicFlag) {
                                                deltaPicOrderCntBottom = 0;
                                                deltaPicOrderCnt0 = 0;
                                                deltaPicOrderCnt1 = 0;
                                            } else if (r0.bitArray.canReadExpGolombCodedNum()) {
                                                deltaPicOrderCntBottom = r0.bitArray.readSignedExpGolombCodedInt();
                                                deltaPicOrderCnt0 = 0;
                                                deltaPicOrderCnt1 = 0;
                                            } else {
                                                return;
                                            }
                                        }
                                        return;
                                    } else if (spsData.picOrderCountType != 1 || spsData.deltaPicOrderAlwaysZeroFlag) {
                                        picOrderCntLsb = 0;
                                        deltaPicOrderCntBottom = 0;
                                        deltaPicOrderCnt0 = 0;
                                        deltaPicOrderCnt1 = 0;
                                    } else if (r0.bitArray.canReadExpGolombCodedNum()) {
                                        picOrderCntLsb = r0.bitArray.readSignedExpGolombCodedInt();
                                        if (!ppsData.bottomFieldPicOrderInFramePresentFlag || fieldPicFlag) {
                                            deltaPicOrderCnt0 = picOrderCntLsb;
                                            picOrderCntLsb = 0;
                                            deltaPicOrderCntBottom = 0;
                                            deltaPicOrderCnt1 = 0;
                                        } else if (r0.bitArray.canReadExpGolombCodedNum()) {
                                            deltaPicOrderCnt0 = picOrderCntLsb;
                                            picOrderCntLsb = 0;
                                            deltaPicOrderCntBottom = 0;
                                            deltaPicOrderCnt1 = r0.bitArray.readSignedExpGolombCodedInt();
                                        } else {
                                            return;
                                        }
                                    } else {
                                        return;
                                    }
                                    r0.sliceHeader.setAll(spsData, nalRefIdc, length, frameNum, picParameterSetId, fieldPicFlag, bottomFieldFlagPresent, bottomFieldFlag, idrPicFlag, idrPicId, picOrderCntLsb, deltaPicOrderCntBottom, deltaPicOrderCnt0, deltaPicOrderCnt1);
                                    r0.isFilling = false;
                                }
                            }
                        }
                    }
                }
            }
        }

        public boolean endNalUnit(long position, int offset, boolean hasOutputFormat, boolean randomAccessIndicator) {
            boolean treatIFrameAsKeyframe;
            boolean z;
            int i;
            int i2 = 0;
            if (this.nalUnitType != 9) {
                if (this.detectAccessUnits) {
                    if (this.sliceHeader.isFirstVclNalUnitOfPicture(this.previousSliceHeader)) {
                    }
                }
                treatIFrameAsKeyframe = this.allowNonIdrKeyframes ? this.sliceHeader.isISlice() : randomAccessIndicator;
                z = this.sampleIsKeyframe;
                i = this.nalUnitType;
                if (i != 5) {
                    if (treatIFrameAsKeyframe || i != 1) {
                        this.sampleIsKeyframe = i2 | z;
                        return this.sampleIsKeyframe;
                    }
                }
                i2 = 1;
                this.sampleIsKeyframe = i2 | z;
                return this.sampleIsKeyframe;
            }
            if (hasOutputFormat && this.readingSample) {
                outputSample(offset + ((int) (position - this.nalUnitStartPosition)));
            }
            this.samplePosition = this.nalUnitStartPosition;
            this.sampleTimeUs = this.nalUnitTimeUs;
            this.sampleIsKeyframe = false;
            this.readingSample = true;
            if (this.allowNonIdrKeyframes) {
            }
            z = this.sampleIsKeyframe;
            i = this.nalUnitType;
            if (i != 5) {
                if (treatIFrameAsKeyframe) {
                }
                this.sampleIsKeyframe = i2 | z;
                return this.sampleIsKeyframe;
            }
            i2 = 1;
            this.sampleIsKeyframe = i2 | z;
            return this.sampleIsKeyframe;
        }

        private void outputSample(int offset) {
            this.output.sampleMetadata(this.sampleTimeUs, this.sampleIsKeyframe, (int) (this.nalUnitStartPosition - this.samplePosition), offset, null);
        }
    }

    public H264Reader(SeiReader seiReader, boolean allowNonIdrKeyframes, boolean detectAccessUnits) {
        this.seiReader = seiReader;
        this.allowNonIdrKeyframes = allowNonIdrKeyframes;
        this.detectAccessUnits = detectAccessUnits;
    }

    public void seek() {
        NalUnitUtil.clearPrefixFlags(this.prefixFlags);
        this.sps.reset();
        this.pps.reset();
        this.sei.reset();
        this.sampleReader.reset();
        this.totalBytesWritten = 0;
        this.randomAccessIndicator = false;
    }

    public void createTracks(ExtractorOutput extractorOutput, TrackIdGenerator idGenerator) {
        idGenerator.generateNewId();
        this.formatId = idGenerator.getFormatId();
        this.output = extractorOutput.track(idGenerator.getTrackId(), 2);
        this.sampleReader = new SampleReader(this.output, this.allowNonIdrKeyframes, this.detectAccessUnits);
        this.seiReader.createTracks(extractorOutput, idGenerator);
    }

    public void packetStarted(long pesTimeUs, int flags) {
        this.pesTimeUs = pesTimeUs;
        this.randomAccessIndicator |= (flags & 2) != 0 ? 1 : 0;
    }

    public void consume(ParsableByteArray data) {
        ParsableByteArray parsableByteArray = data;
        int offset = data.getPosition();
        int limit = data.limit();
        byte[] dataArray = parsableByteArray.data;
        this.totalBytesWritten += (long) data.bytesLeft();
        this.output.sampleData(parsableByteArray, data.bytesLeft());
        int offset2 = offset;
        while (true) {
            int nalUnitOffset = NalUnitUtil.findNalUnit(dataArray, offset2, limit, r7.prefixFlags);
            if (nalUnitOffset == limit) {
                nalUnitData(dataArray, offset2, limit);
                return;
            }
            int nalUnitType = NalUnitUtil.getNalUnitType(dataArray, nalUnitOffset);
            int lengthToNalUnit = nalUnitOffset - offset2;
            if (lengthToNalUnit > 0) {
                nalUnitData(dataArray, offset2, nalUnitOffset);
            }
            int bytesWrittenPastPosition = limit - nalUnitOffset;
            long j = r7.totalBytesWritten - ((long) bytesWrittenPastPosition);
            endNalUnit(j, bytesWrittenPastPosition, lengthToNalUnit < 0 ? -lengthToNalUnit : 0, r7.pesTimeUs);
            startNalUnit(j, nalUnitType, r7.pesTimeUs);
            offset2 = nalUnitOffset + 3;
        }
    }

    public void packetFinished() {
    }

    private void startNalUnit(long position, int nalUnitType, long pesTimeUs) {
        if (this.hasOutputFormat) {
            if (!this.sampleReader.needsSpsPps()) {
                this.sei.startNalUnit(nalUnitType);
                this.sampleReader.startNalUnit(position, nalUnitType, pesTimeUs);
            }
        }
        this.sps.startNalUnit(nalUnitType);
        this.pps.startNalUnit(nalUnitType);
        this.sei.startNalUnit(nalUnitType);
        this.sampleReader.startNalUnit(position, nalUnitType, pesTimeUs);
    }

    private void nalUnitData(byte[] dataArray, int offset, int limit) {
        if (this.hasOutputFormat) {
            if (!this.sampleReader.needsSpsPps()) {
                this.sei.appendToNalUnit(dataArray, offset, limit);
                this.sampleReader.appendToNalUnit(dataArray, offset, limit);
            }
        }
        this.sps.appendToNalUnit(dataArray, offset, limit);
        this.pps.appendToNalUnit(dataArray, offset, limit);
        this.sei.appendToNalUnit(dataArray, offset, limit);
        this.sampleReader.appendToNalUnit(dataArray, offset, limit);
    }

    private void endNalUnit(long position, int offset, int discardPadding, long pesTimeUs) {
        int i = discardPadding;
        if (this.hasOutputFormat) {
            if (!r0.sampleReader.needsSpsPps()) {
                if (r0.sei.endNalUnit(i)) {
                    long j = pesTimeUs;
                } else {
                    r0.seiWrapper.reset(r0.sei.nalData, NalUnitUtil.unescapeStream(r0.sei.nalData, r0.sei.nalLength));
                    r0.seiWrapper.setPosition(4);
                    r0.seiReader.consume(pesTimeUs, r0.seiWrapper);
                }
                if (r0.sampleReader.endNalUnit(position, offset, r0.hasOutputFormat, r0.randomAccessIndicator)) {
                    r0.randomAccessIndicator = false;
                }
            }
        }
        r0.sps.endNalUnit(i);
        r0.pps.endNalUnit(i);
        if (r0.hasOutputFormat) {
            if (r0.sps.isCompleted()) {
                r0.sampleReader.putSps(NalUnitUtil.parseSpsNalUnit(r0.sps.nalData, 3, r0.sps.nalLength));
                r0.sps.reset();
            } else if (r0.pps.isCompleted()) {
                r0.sampleReader.putPps(NalUnitUtil.parsePpsNalUnit(r0.pps.nalData, 3, r0.pps.nalLength));
                r0.pps.reset();
            }
        } else if (r0.sps.isCompleted() && r0.pps.isCompleted()) {
            List<byte[]> initializationData = new ArrayList();
            initializationData.add(Arrays.copyOf(r0.sps.nalData, r0.sps.nalLength));
            initializationData.add(Arrays.copyOf(r0.pps.nalData, r0.pps.nalLength));
            SpsData spsData = NalUnitUtil.parseSpsNalUnit(r0.sps.nalData, 3, r0.sps.nalLength);
            PpsData ppsData = NalUnitUtil.parsePpsNalUnit(r0.pps.nalData, 3, r0.pps.nalLength);
            TrackOutput trackOutput = r0.output;
            String str = r0.formatId;
            String str2 = MimeTypes.VIDEO_H264;
            String buildAvcCodecString = CodecSpecificDataUtil.buildAvcCodecString(spsData.profileIdc, spsData.constraintsFlagsAndReservedZero2Bits, spsData.levelIdc);
            int i2 = spsData.width;
            int i3 = spsData.height;
            List<byte[]> list = initializationData;
            TrackOutput trackOutput2 = trackOutput;
            float f = spsData.pixelWidthAspectRatio;
            SpsData spsData2 = spsData;
            trackOutput2.format(Format.createVideoSampleFormat(str, str2, buildAvcCodecString, -1, -1, i2, i3, -1.0f, list, -1, f, null));
            r0.hasOutputFormat = true;
            r0.sampleReader.putSps(spsData2);
            r0.sampleReader.putPps(ppsData);
            r0.sps.reset();
            r0.pps.reset();
        }
        if (r0.sei.endNalUnit(i)) {
            long j2 = pesTimeUs;
        } else {
            r0.seiWrapper.reset(r0.sei.nalData, NalUnitUtil.unescapeStream(r0.sei.nalData, r0.sei.nalLength));
            r0.seiWrapper.setPosition(4);
            r0.seiReader.consume(pesTimeUs, r0.seiWrapper);
        }
        if (r0.sampleReader.endNalUnit(position, offset, r0.hasOutputFormat, r0.randomAccessIndicator)) {
            r0.randomAccessIndicator = false;
        }
    }
}
