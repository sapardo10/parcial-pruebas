package com.google.android.exoplayer2.util;

import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import java.nio.ByteBuffer;

public final class NalUnitUtil {
    public static final float[] ASPECT_RATIO_IDC_VALUES = new float[]{1.0f, 1.0f, 1.0909091f, 0.90909094f, 1.4545455f, 1.2121212f, 2.1818182f, 1.8181819f, 2.909091f, 2.4242425f, 1.6363636f, 1.3636364f, 1.939394f, 1.6161616f, 1.3333334f, 1.5f, 2.0f};
    public static final int EXTENDED_SAR = 255;
    private static final int H264_NAL_UNIT_TYPE_SEI = 6;
    private static final int H264_NAL_UNIT_TYPE_SPS = 7;
    private static final int H265_NAL_UNIT_TYPE_PREFIX_SEI = 39;
    public static final byte[] NAL_START_CODE = new byte[]{(byte) 0, (byte) 0, (byte) 0, (byte) 1};
    private static final String TAG = "NalUnitUtil";
    private static int[] scratchEscapePositions = new int[10];
    private static final Object scratchEscapePositionsLock = new Object();

    public static final class PpsData {
        public final boolean bottomFieldPicOrderInFramePresentFlag;
        public final int picParameterSetId;
        public final int seqParameterSetId;

        public PpsData(int picParameterSetId, int seqParameterSetId, boolean bottomFieldPicOrderInFramePresentFlag) {
            this.picParameterSetId = picParameterSetId;
            this.seqParameterSetId = seqParameterSetId;
            this.bottomFieldPicOrderInFramePresentFlag = bottomFieldPicOrderInFramePresentFlag;
        }
    }

    public static final class SpsData {
        public final int constraintsFlagsAndReservedZero2Bits;
        public final boolean deltaPicOrderAlwaysZeroFlag;
        public final boolean frameMbsOnlyFlag;
        public final int frameNumLength;
        public final int height;
        public final int levelIdc;
        public final int picOrderCntLsbLength;
        public final int picOrderCountType;
        public final float pixelWidthAspectRatio;
        public final int profileIdc;
        public final boolean separateColorPlaneFlag;
        public final int seqParameterSetId;
        public final int width;

        public SpsData(int profileIdc, int constraintsFlagsAndReservedZero2Bits, int levelIdc, int seqParameterSetId, int width, int height, float pixelWidthAspectRatio, boolean separateColorPlaneFlag, boolean frameMbsOnlyFlag, int frameNumLength, int picOrderCountType, int picOrderCntLsbLength, boolean deltaPicOrderAlwaysZeroFlag) {
            this.profileIdc = profileIdc;
            this.constraintsFlagsAndReservedZero2Bits = constraintsFlagsAndReservedZero2Bits;
            this.levelIdc = levelIdc;
            this.seqParameterSetId = seqParameterSetId;
            this.width = width;
            this.height = height;
            this.pixelWidthAspectRatio = pixelWidthAspectRatio;
            this.separateColorPlaneFlag = separateColorPlaneFlag;
            this.frameMbsOnlyFlag = frameMbsOnlyFlag;
            this.frameNumLength = frameNumLength;
            this.picOrderCountType = picOrderCountType;
            this.picOrderCntLsbLength = picOrderCntLsbLength;
            this.deltaPicOrderAlwaysZeroFlag = deltaPicOrderAlwaysZeroFlag;
        }
    }

    public static int unescapeStream(byte[] r11, int r12) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:23:0x0058 in {9, 10, 11, 12, 17, 20, 22} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r0 = scratchEscapePositionsLock;
        monitor-enter(r0);
        r1 = 0;
        r2 = 0;
    L_0x0005:
        if (r1 >= r12) goto L_0x002f;
    L_0x0007:
        r3 = findNextUnescapeIndex(r11, r1, r12);	 Catch:{ all -> 0x002d }
        r1 = r3;	 Catch:{ all -> 0x002d }
        if (r1 >= r12) goto L_0x002c;	 Catch:{ all -> 0x002d }
    L_0x000e:
        r3 = scratchEscapePositions;	 Catch:{ all -> 0x002d }
        r3 = r3.length;	 Catch:{ all -> 0x002d }
        if (r3 > r2) goto L_0x0021;	 Catch:{ all -> 0x002d }
    L_0x0013:
        r3 = scratchEscapePositions;	 Catch:{ all -> 0x002d }
        r4 = scratchEscapePositions;	 Catch:{ all -> 0x002d }
        r4 = r4.length;	 Catch:{ all -> 0x002d }
        r4 = r4 * 2;	 Catch:{ all -> 0x002d }
        r3 = java.util.Arrays.copyOf(r3, r4);	 Catch:{ all -> 0x002d }
        scratchEscapePositions = r3;	 Catch:{ all -> 0x002d }
        goto L_0x0022;	 Catch:{ all -> 0x002d }
    L_0x0022:
        r3 = scratchEscapePositions;	 Catch:{ all -> 0x002d }
        r4 = r2 + 1;	 Catch:{ all -> 0x002d }
        r3[r2] = r1;	 Catch:{ all -> 0x002d }
        r1 = r1 + 3;	 Catch:{ all -> 0x002d }
        r2 = r4;	 Catch:{ all -> 0x002d }
        goto L_0x0005;	 Catch:{ all -> 0x002d }
    L_0x002c:
        goto L_0x0005;	 Catch:{ all -> 0x002d }
    L_0x002d:
        r1 = move-exception;	 Catch:{ all -> 0x002d }
        goto L_0x0056;	 Catch:{ all -> 0x002d }
    L_0x002f:
        r3 = r12 - r2;	 Catch:{ all -> 0x002d }
        r4 = 0;	 Catch:{ all -> 0x002d }
        r5 = 0;	 Catch:{ all -> 0x002d }
        r6 = 0;	 Catch:{ all -> 0x002d }
    L_0x0034:
        if (r6 >= r2) goto L_0x004f;	 Catch:{ all -> 0x002d }
    L_0x0036:
        r7 = scratchEscapePositions;	 Catch:{ all -> 0x002d }
        r7 = r7[r6];	 Catch:{ all -> 0x002d }
        r8 = r7 - r4;	 Catch:{ all -> 0x002d }
        java.lang.System.arraycopy(r11, r4, r11, r5, r8);	 Catch:{ all -> 0x002d }
        r5 = r5 + r8;	 Catch:{ all -> 0x002d }
        r9 = r5 + 1;	 Catch:{ all -> 0x002d }
        r10 = 0;	 Catch:{ all -> 0x002d }
        r11[r5] = r10;	 Catch:{ all -> 0x002d }
        r5 = r9 + 1;	 Catch:{ all -> 0x002d }
        r11[r9] = r10;	 Catch:{ all -> 0x002d }
        r9 = r8 + 3;	 Catch:{ all -> 0x002d }
        r4 = r4 + r9;	 Catch:{ all -> 0x002d }
        r6 = r6 + 1;	 Catch:{ all -> 0x002d }
        goto L_0x0034;	 Catch:{ all -> 0x002d }
    L_0x004f:
        r6 = r3 - r5;	 Catch:{ all -> 0x002d }
        java.lang.System.arraycopy(r11, r4, r11, r5, r6);	 Catch:{ all -> 0x002d }
        monitor-exit(r0);	 Catch:{ all -> 0x002d }
        return r3;	 Catch:{ all -> 0x002d }
    L_0x0056:
        monitor-exit(r0);	 Catch:{ all -> 0x002d }
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.util.NalUnitUtil.unescapeStream(byte[], int):int");
    }

    public static void discardToSps(ByteBuffer data) {
        int length = data.position();
        int consecutiveZeros = 0;
        int offset = 0;
        while (offset + 1 < length) {
            int value = data.get(offset) & 255;
            if (consecutiveZeros == 3) {
                if (value == 1 && (data.get(offset + 1) & 31) == 7) {
                    ByteBuffer offsetData = data.duplicate();
                    offsetData.position(offset - 3);
                    offsetData.limit(length);
                    data.position(0);
                    data.put(offsetData);
                    return;
                }
            } else if (value == 0) {
                consecutiveZeros++;
            }
            if (value != 0) {
                consecutiveZeros = 0;
            }
            offset++;
        }
        data.clear();
    }

    public static boolean isNalUnitSei(String mimeType, byte nalUnitHeaderFirstByte) {
        if (MimeTypes.VIDEO_H264.equals(mimeType)) {
            if ((nalUnitHeaderFirstByte & 31) == 6) {
                return true;
            }
        }
        if (!(MimeTypes.VIDEO_H265.equals(mimeType) && ((nalUnitHeaderFirstByte & 126) >> 1) == 39)) {
            return false;
        }
        return true;
    }

    public static int getNalUnitType(byte[] data, int offset) {
        return data[offset + 3] & 31;
    }

    public static int getH265NalUnitType(byte[] data, int offset) {
        return (data[offset + 3] & 126) >> 1;
    }

    public static SpsData parseSpsNalUnit(byte[] nalData, int nalOffset, int nalLimit) {
        int chromaFormatIdc;
        boolean separateColorPlaneFlag;
        int frameNumLength;
        int picOrderCntType;
        int picOrderCntLsbLength;
        int subHeightC;
        boolean deltaPicOrderAlwaysZeroFlag;
        int picOrderCntLsbLength2;
        long numRefFramesInPicOrderCntCycle;
        int i;
        int picOrderCntLsbLength3;
        int picWidthInMbs;
        int picHeightInMapUnits;
        boolean frameMbsOnlyFlag;
        int frameHeightInMbs;
        int frameWidth;
        int frameHeight;
        int frameWidth2;
        int frameHeight2;
        int subWidthC;
        float pixelWidthHeightRatio;
        float pixelWidthHeightRatio2;
        float[] fArr;
        ParsableNalUnitBitArray data = new ParsableNalUnitBitArray(nalData, nalOffset, nalLimit);
        data.skipBits(8);
        int profileIdc = data.readBits(8);
        int constraintsFlagsAndReservedZero2Bits = data.readBits(8);
        int levelIdc = data.readBits(8);
        int seqParameterSetId = data.readUnsignedExpGolombCodedInt();
        boolean separateColorPlaneFlag2 = false;
        if (!(profileIdc == 100 || profileIdc == 110 || profileIdc == 122 || profileIdc == 244 || profileIdc == 44 || profileIdc == 83 || profileIdc == 86 || profileIdc == 118 || profileIdc == 128)) {
            if (profileIdc != TsExtractor.TS_STREAM_TYPE_DTS) {
                chromaFormatIdc = 1;
                separateColorPlaneFlag = false;
                frameNumLength = data.readUnsignedExpGolombCodedInt() + 4;
                picOrderCntType = data.readUnsignedExpGolombCodedInt();
                picOrderCntLsbLength = 0;
                subHeightC = 1;
                if (picOrderCntType == 0) {
                    deltaPicOrderAlwaysZeroFlag = false;
                    picOrderCntLsbLength2 = data.readUnsignedExpGolombCodedInt() + 4;
                } else if (picOrderCntType != 1) {
                    separateColorPlaneFlag2 = data.readBit();
                    data.readSignedExpGolombCodedInt();
                    data.readSignedExpGolombCodedInt();
                    numRefFramesInPicOrderCntCycle = (long) data.readUnsignedExpGolombCodedInt();
                    i = 0;
                    while (true) {
                        picOrderCntLsbLength3 = picOrderCntLsbLength;
                        if (((long) i) < numRefFramesInPicOrderCntCycle) {
                            break;
                        }
                        data.readUnsignedExpGolombCodedInt();
                        i++;
                        picOrderCntLsbLength = picOrderCntLsbLength3;
                    }
                    deltaPicOrderAlwaysZeroFlag = separateColorPlaneFlag2;
                    picOrderCntLsbLength2 = picOrderCntLsbLength3;
                } else {
                    deltaPicOrderAlwaysZeroFlag = false;
                    picOrderCntLsbLength2 = 0;
                }
                data.readUnsignedExpGolombCodedInt();
                data.skipBit();
                picWidthInMbs = data.readUnsignedExpGolombCodedInt() + 1;
                picHeightInMapUnits = data.readUnsignedExpGolombCodedInt() + 1;
                frameMbsOnlyFlag = data.readBit();
                frameHeightInMbs = (2 - frameMbsOnlyFlag) * picHeightInMapUnits;
                if (!frameMbsOnlyFlag) {
                    data.skipBit();
                }
                data.skipBit();
                frameWidth = picWidthInMbs * 16;
                frameHeight = frameHeightInMbs * 16;
                if (data.readBit()) {
                    frameWidth2 = frameWidth;
                    frameHeight2 = frameHeight;
                } else {
                    int frameCropLeftOffset = data.readUnsignedExpGolombCodedInt();
                    i = data.readUnsignedExpGolombCodedInt();
                    picOrderCntLsbLength3 = data.readUnsignedExpGolombCodedInt();
                    int frameCropBottomOffset = data.readUnsignedExpGolombCodedInt();
                    if (chromaFormatIdc != 0) {
                        picOrderCntLsbLength = 2 - frameMbsOnlyFlag;
                        frameWidth2 = 1;
                    } else {
                        subWidthC = chromaFormatIdc != 3 ? 1 : 2;
                        if (chromaFormatIdc == 1) {
                            subHeightC = 2;
                        }
                        frameWidth2 = subWidthC;
                        picOrderCntLsbLength = (2 - frameMbsOnlyFlag) * subHeightC;
                    }
                    frameWidth2 = frameWidth - ((frameCropLeftOffset + i) * frameWidth2);
                    frameHeight2 = frameHeight - ((picOrderCntLsbLength3 + frameCropBottomOffset) * picOrderCntLsbLength);
                }
                pixelWidthHeightRatio = 1.0f;
                if (data.readBit()) {
                    if (data.readBit()) {
                        subWidthC = data.readBits(8);
                        if (subWidthC != 255) {
                            subHeightC = data.readBits(16);
                            int sarHeight = data.readBits(16);
                            if (subHeightC == 0 && sarHeight != 0) {
                                pixelWidthHeightRatio = ((float) subHeightC) / ((float) sarHeight);
                            }
                            pixelWidthHeightRatio2 = pixelWidthHeightRatio;
                        } else {
                            fArr = ASPECT_RATIO_IDC_VALUES;
                            if (subWidthC >= fArr.length) {
                                pixelWidthHeightRatio2 = fArr[subWidthC];
                            } else {
                                String str = TAG;
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("Unexpected aspect_ratio_idc value: ");
                                stringBuilder.append(subWidthC);
                                Log.m10w(str, stringBuilder.toString());
                            }
                        }
                        return new SpsData(profileIdc, constraintsFlagsAndReservedZero2Bits, levelIdc, seqParameterSetId, frameWidth2, frameHeight2, pixelWidthHeightRatio2, separateColorPlaneFlag, frameMbsOnlyFlag, frameNumLength, picOrderCntType, picOrderCntLsbLength2, deltaPicOrderAlwaysZeroFlag);
                    }
                }
                pixelWidthHeightRatio2 = 1.0f;
                return new SpsData(profileIdc, constraintsFlagsAndReservedZero2Bits, levelIdc, seqParameterSetId, frameWidth2, frameHeight2, pixelWidthHeightRatio2, separateColorPlaneFlag, frameMbsOnlyFlag, frameNumLength, picOrderCntType, picOrderCntLsbLength2, deltaPicOrderAlwaysZeroFlag);
            }
        }
        picOrderCntLsbLength = data.readUnsignedExpGolombCodedInt();
        if (picOrderCntLsbLength == 3) {
            separateColorPlaneFlag2 = data.readBit();
        }
        data.readUnsignedExpGolombCodedInt();
        data.readUnsignedExpGolombCodedInt();
        data.skipBit();
        if (data.readBit()) {
            frameHeight = picOrderCntLsbLength != 3 ? 8 : 12;
            frameCropLeftOffset = 0;
            while (frameCropLeftOffset < frameHeight) {
                if (data.readBit()) {
                    skipScalingList(data, frameCropLeftOffset < 6 ? 16 : 64);
                }
                frameCropLeftOffset++;
            }
        }
        chromaFormatIdc = picOrderCntLsbLength;
        separateColorPlaneFlag = separateColorPlaneFlag2;
        frameNumLength = data.readUnsignedExpGolombCodedInt() + 4;
        picOrderCntType = data.readUnsignedExpGolombCodedInt();
        picOrderCntLsbLength = 0;
        subHeightC = 1;
        if (picOrderCntType == 0) {
            deltaPicOrderAlwaysZeroFlag = false;
            picOrderCntLsbLength2 = data.readUnsignedExpGolombCodedInt() + 4;
        } else if (picOrderCntType != 1) {
            deltaPicOrderAlwaysZeroFlag = false;
            picOrderCntLsbLength2 = 0;
        } else {
            separateColorPlaneFlag2 = data.readBit();
            data.readSignedExpGolombCodedInt();
            data.readSignedExpGolombCodedInt();
            numRefFramesInPicOrderCntCycle = (long) data.readUnsignedExpGolombCodedInt();
            i = 0;
            while (true) {
                picOrderCntLsbLength3 = picOrderCntLsbLength;
                if (((long) i) < numRefFramesInPicOrderCntCycle) {
                    break;
                }
                data.readUnsignedExpGolombCodedInt();
                i++;
                picOrderCntLsbLength = picOrderCntLsbLength3;
            }
            deltaPicOrderAlwaysZeroFlag = separateColorPlaneFlag2;
            picOrderCntLsbLength2 = picOrderCntLsbLength3;
        }
        data.readUnsignedExpGolombCodedInt();
        data.skipBit();
        picWidthInMbs = data.readUnsignedExpGolombCodedInt() + 1;
        picHeightInMapUnits = data.readUnsignedExpGolombCodedInt() + 1;
        frameMbsOnlyFlag = data.readBit();
        frameHeightInMbs = (2 - frameMbsOnlyFlag) * picHeightInMapUnits;
        if (!frameMbsOnlyFlag) {
            data.skipBit();
        }
        data.skipBit();
        frameWidth = picWidthInMbs * 16;
        frameHeight = frameHeightInMbs * 16;
        if (data.readBit()) {
            frameWidth2 = frameWidth;
            frameHeight2 = frameHeight;
        } else {
            int frameCropLeftOffset2 = data.readUnsignedExpGolombCodedInt();
            i = data.readUnsignedExpGolombCodedInt();
            picOrderCntLsbLength3 = data.readUnsignedExpGolombCodedInt();
            int frameCropBottomOffset2 = data.readUnsignedExpGolombCodedInt();
            if (chromaFormatIdc != 0) {
                if (chromaFormatIdc != 3) {
                }
                if (chromaFormatIdc == 1) {
                    subHeightC = 2;
                }
                frameWidth2 = subWidthC;
                picOrderCntLsbLength = (2 - frameMbsOnlyFlag) * subHeightC;
            } else {
                picOrderCntLsbLength = 2 - frameMbsOnlyFlag;
                frameWidth2 = 1;
            }
            frameWidth2 = frameWidth - ((frameCropLeftOffset2 + i) * frameWidth2);
            frameHeight2 = frameHeight - ((picOrderCntLsbLength3 + frameCropBottomOffset2) * picOrderCntLsbLength);
        }
        pixelWidthHeightRatio = 1.0f;
        if (data.readBit()) {
            if (data.readBit()) {
                subWidthC = data.readBits(8);
                if (subWidthC != 255) {
                    fArr = ASPECT_RATIO_IDC_VALUES;
                    if (subWidthC >= fArr.length) {
                        String str2 = TAG;
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("Unexpected aspect_ratio_idc value: ");
                        stringBuilder2.append(subWidthC);
                        Log.m10w(str2, stringBuilder2.toString());
                    } else {
                        pixelWidthHeightRatio2 = fArr[subWidthC];
                    }
                } else {
                    subHeightC = data.readBits(16);
                    int sarHeight2 = data.readBits(16);
                    if (subHeightC == 0) {
                    }
                    pixelWidthHeightRatio2 = pixelWidthHeightRatio;
                }
                return new SpsData(profileIdc, constraintsFlagsAndReservedZero2Bits, levelIdc, seqParameterSetId, frameWidth2, frameHeight2, pixelWidthHeightRatio2, separateColorPlaneFlag, frameMbsOnlyFlag, frameNumLength, picOrderCntType, picOrderCntLsbLength2, deltaPicOrderAlwaysZeroFlag);
            }
        }
        pixelWidthHeightRatio2 = 1.0f;
        return new SpsData(profileIdc, constraintsFlagsAndReservedZero2Bits, levelIdc, seqParameterSetId, frameWidth2, frameHeight2, pixelWidthHeightRatio2, separateColorPlaneFlag, frameMbsOnlyFlag, frameNumLength, picOrderCntType, picOrderCntLsbLength2, deltaPicOrderAlwaysZeroFlag);
    }

    public static PpsData parsePpsNalUnit(byte[] nalData, int nalOffset, int nalLimit) {
        ParsableNalUnitBitArray data = new ParsableNalUnitBitArray(nalData, nalOffset, nalLimit);
        data.skipBits(8);
        int picParameterSetId = data.readUnsignedExpGolombCodedInt();
        int seqParameterSetId = data.readUnsignedExpGolombCodedInt();
        data.skipBit();
        return new PpsData(picParameterSetId, seqParameterSetId, data.readBit());
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int findNalUnit(byte[] r7, int r8, int r9, boolean[] r10) {
        /*
        r0 = r9 - r8;
        r1 = 0;
        r2 = 1;
        if (r0 < 0) goto L_0x0008;
    L_0x0006:
        r3 = 1;
        goto L_0x0009;
    L_0x0008:
        r3 = 0;
    L_0x0009:
        com.google.android.exoplayer2.util.Assertions.checkState(r3);
        if (r0 != 0) goto L_0x000f;
    L_0x000e:
        return r9;
    L_0x000f:
        r3 = 2;
        if (r10 == 0) goto L_0x0044;
    L_0x0012:
        r4 = r10[r1];
        if (r4 == 0) goto L_0x001c;
    L_0x0016:
        clearPrefixFlags(r10);
        r1 = r8 + -3;
        return r1;
    L_0x001c:
        if (r0 <= r2) goto L_0x002c;
    L_0x001e:
        r4 = r10[r2];
        if (r4 == 0) goto L_0x002c;
    L_0x0022:
        r4 = r7[r8];
        if (r4 != r2) goto L_0x002c;
    L_0x0026:
        clearPrefixFlags(r10);
        r1 = r8 + -2;
        return r1;
        if (r0 <= r3) goto L_0x0043;
    L_0x002f:
        r4 = r10[r3];
        if (r4 == 0) goto L_0x0043;
    L_0x0033:
        r4 = r7[r8];
        if (r4 != 0) goto L_0x0043;
    L_0x0037:
        r4 = r8 + 1;
        r4 = r7[r4];
        if (r4 != r2) goto L_0x0043;
    L_0x003d:
        clearPrefixFlags(r10);
        r1 = r8 + -1;
        return r1;
    L_0x0043:
        goto L_0x0045;
    L_0x0045:
        r4 = r9 + -1;
        r5 = r8 + 2;
    L_0x0049:
        if (r5 >= r4) goto L_0x0072;
    L_0x004b:
        r6 = r7[r5];
        r6 = r6 & 254;
        if (r6 == 0) goto L_0x0052;
    L_0x0051:
        goto L_0x006f;
    L_0x0052:
        r6 = r5 + -2;
        r6 = r7[r6];
        if (r6 != 0) goto L_0x006c;
    L_0x0058:
        r6 = r5 + -1;
        r6 = r7[r6];
        if (r6 != 0) goto L_0x006c;
    L_0x005e:
        r6 = r7[r5];
        if (r6 != r2) goto L_0x006c;
    L_0x0062:
        if (r10 == 0) goto L_0x0068;
    L_0x0064:
        clearPrefixFlags(r10);
        goto L_0x0069;
    L_0x0069:
        r1 = r5 + -2;
        return r1;
        r5 = r5 + -2;
    L_0x006f:
        r5 = r5 + 3;
        goto L_0x0049;
    L_0x0072:
        if (r10 == 0) goto L_0x00d4;
    L_0x0074:
        if (r0 <= r3) goto L_0x008b;
    L_0x0076:
        r5 = r9 + -3;
        r5 = r7[r5];
        if (r5 != 0) goto L_0x0089;
    L_0x007c:
        r5 = r9 + -2;
        r5 = r7[r5];
        if (r5 != 0) goto L_0x0089;
    L_0x0082:
        r5 = r9 + -1;
        r5 = r7[r5];
        if (r5 != r2) goto L_0x0089;
    L_0x0088:
        goto L_0x00a8;
    L_0x0089:
        r5 = 0;
        goto L_0x00a9;
    L_0x008b:
        if (r0 != r3) goto L_0x009e;
    L_0x008d:
        r5 = r10[r3];
        if (r5 == 0) goto L_0x0089;
    L_0x0091:
        r5 = r9 + -2;
        r5 = r7[r5];
        if (r5 != 0) goto L_0x0089;
    L_0x0097:
        r5 = r9 + -1;
        r5 = r7[r5];
        if (r5 != r2) goto L_0x0089;
    L_0x009d:
        goto L_0x00a8;
    L_0x009e:
        r5 = r10[r2];
        if (r5 == 0) goto L_0x0089;
    L_0x00a2:
        r5 = r9 + -1;
        r5 = r7[r5];
        if (r5 != r2) goto L_0x0089;
    L_0x00a8:
        r5 = 1;
    L_0x00a9:
        r10[r1] = r5;
        if (r0 <= r2) goto L_0x00ba;
    L_0x00ad:
        r5 = r9 + -2;
        r5 = r7[r5];
        if (r5 != 0) goto L_0x00c6;
    L_0x00b3:
        r5 = r9 + -1;
        r5 = r7[r5];
        if (r5 != 0) goto L_0x00c6;
    L_0x00b9:
        goto L_0x00c4;
    L_0x00ba:
        r5 = r10[r3];
        if (r5 == 0) goto L_0x00c6;
    L_0x00be:
        r5 = r9 + -1;
        r5 = r7[r5];
        if (r5 != 0) goto L_0x00c6;
    L_0x00c4:
        r5 = 1;
        goto L_0x00c7;
    L_0x00c6:
        r5 = 0;
    L_0x00c7:
        r10[r2] = r5;
        r5 = r9 + -1;
        r5 = r7[r5];
        if (r5 != 0) goto L_0x00d1;
    L_0x00cf:
        r1 = 1;
    L_0x00d1:
        r10[r3] = r1;
        goto L_0x00d5;
    L_0x00d5:
        return r9;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.util.NalUnitUtil.findNalUnit(byte[], int, int, boolean[]):int");
    }

    public static void clearPrefixFlags(boolean[] prefixFlags) {
        prefixFlags[0] = false;
        prefixFlags[1] = false;
        prefixFlags[2] = false;
    }

    private static int findNextUnescapeIndex(byte[] bytes, int offset, int limit) {
        int i = offset;
        while (i < limit - 2) {
            if (bytes[i] == (byte) 0 && bytes[i + 1] == (byte) 0 && bytes[i + 2] == (byte) 3) {
                return i;
            }
            i++;
        }
        return limit;
    }

    private static void skipScalingList(ParsableNalUnitBitArray bitArray, int size) {
        int lastScale = 8;
        int nextScale = 8;
        for (int i = 0; i < size; i++) {
            if (nextScale != 0) {
                nextScale = ((lastScale + bitArray.readSignedExpGolombCodedInt()) + 256) % 256;
            }
            lastScale = nextScale == 0 ? lastScale : nextScale;
        }
    }

    private NalUnitUtil() {
    }
}
