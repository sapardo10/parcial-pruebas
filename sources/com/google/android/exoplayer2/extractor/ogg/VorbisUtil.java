package com.google.android.exoplayer2.extractor.ogg;

import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.util.Arrays;

final class VorbisUtil {
    private static final String TAG = "VorbisUtil";

    public static final class CodeBook {
        public final int dimensions;
        public final int entries;
        public final boolean isOrdered;
        public final long[] lengthMap;
        public final int lookupType;

        public CodeBook(int dimensions, int entries, long[] lengthMap, int lookupType, boolean isOrdered) {
            this.dimensions = dimensions;
            this.entries = entries;
            this.lengthMap = lengthMap;
            this.lookupType = lookupType;
            this.isOrdered = isOrdered;
        }
    }

    public static final class CommentHeader {
        public final String[] comments;
        public final int length;
        public final String vendor;

        public CommentHeader(String vendor, String[] comments, int length) {
            this.vendor = vendor;
            this.comments = comments;
            this.length = length;
        }
    }

    public static final class Mode {
        public final boolean blockFlag;
        public final int mapping;
        public final int transformType;
        public final int windowType;

        public Mode(boolean blockFlag, int windowType, int transformType, int mapping) {
            this.blockFlag = blockFlag;
            this.windowType = windowType;
            this.transformType = transformType;
            this.mapping = mapping;
        }
    }

    public static final class VorbisIdHeader {
        public final int bitrateMax;
        public final int bitrateMin;
        public final int bitrateNominal;
        public final int blockSize0;
        public final int blockSize1;
        public final int channels;
        public final byte[] data;
        public final boolean framingFlag;
        public final long sampleRate;
        public final long version;

        public VorbisIdHeader(long version, int channels, long sampleRate, int bitrateMax, int bitrateNominal, int bitrateMin, int blockSize0, int blockSize1, boolean framingFlag, byte[] data) {
            this.version = version;
            this.channels = channels;
            this.sampleRate = sampleRate;
            this.bitrateMax = bitrateMax;
            this.bitrateNominal = bitrateNominal;
            this.bitrateMin = bitrateMin;
            this.blockSize0 = blockSize0;
            this.blockSize1 = blockSize1;
            this.framingFlag = framingFlag;
            this.data = data;
        }

        public int getApproximateBitrate() {
            int i = this.bitrateNominal;
            return i == 0 ? (this.bitrateMin + this.bitrateMax) / 2 : i;
        }
    }

    private static com.google.android.exoplayer2.extractor.ogg.VorbisUtil.CodeBook readBook(com.google.android.exoplayer2.extractor.ogg.VorbisBitArray r12) throws com.google.android.exoplayer2.ParserException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:44:0x00e4 in {10, 11, 12, 13, 14, 22, 23, 24, 29, 30, 34, 35, 36, 37, 39, 41, 43} preds:[]
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
        r0 = 24;
        r1 = r12.readBits(r0);
        r2 = 5653314; // 0x564342 float:7.92198E-39 double:2.793108E-317;
        if (r1 != r2) goto L_0x00c9;
    L_0x000b:
        r1 = 16;
        r1 = r12.readBits(r1);
        r0 = r12.readBits(r0);
        r8 = new long[r0];
        r9 = r12.readBit();
        r2 = 5;
        r3 = 1;
        if (r9 != 0) goto L_0x0049;
    L_0x001f:
        r4 = r12.readBit();
        r5 = 0;
    L_0x0024:
        r6 = r8.length;
        if (r5 >= r6) goto L_0x0048;
    L_0x0027:
        if (r4 == 0) goto L_0x003d;
    L_0x0029:
        r6 = r12.readBit();
        if (r6 == 0) goto L_0x0038;
    L_0x002f:
        r6 = r12.readBits(r2);
        r6 = r6 + r3;
        r6 = (long) r6;
        r8[r5] = r6;
        goto L_0x0045;
    L_0x0038:
        r6 = 0;
        r8[r5] = r6;
        goto L_0x0045;
    L_0x003d:
        r6 = r12.readBits(r2);
        r6 = r6 + r3;
        r6 = (long) r6;
        r8[r5] = r6;
    L_0x0045:
        r5 = r5 + 1;
        goto L_0x0024;
    L_0x0048:
        goto L_0x006f;
    L_0x0049:
        r2 = r12.readBits(r2);
        r2 = r2 + r3;
        r4 = 0;
    L_0x004f:
        r5 = r8.length;
        if (r4 >= r5) goto L_0x006e;
    L_0x0052:
        r5 = r0 - r4;
        r5 = iLog(r5);
        r5 = r12.readBits(r5);
        r6 = 0;
    L_0x005d:
        if (r6 >= r5) goto L_0x006a;
    L_0x005f:
        r7 = r8.length;
        if (r4 >= r7) goto L_0x006a;
    L_0x0062:
        r10 = (long) r2;
        r8[r4] = r10;
        r4 = r4 + 1;
        r6 = r6 + 1;
        goto L_0x005d;
        r2 = r2 + 1;
        goto L_0x004f;
    L_0x006f:
        r2 = 4;
        r10 = r12.readBits(r2);
        r4 = 2;
        if (r10 > r4) goto L_0x00b2;
    L_0x0077:
        if (r10 == r3) goto L_0x007d;
    L_0x0079:
        if (r10 != r4) goto L_0x007c;
    L_0x007b:
        goto L_0x007d;
    L_0x007c:
        goto L_0x00a6;
    L_0x007d:
        r4 = 32;
        r12.skipBits(r4);
        r12.skipBits(r4);
        r2 = r12.readBits(r2);
        r2 = r2 + r3;
        r12.skipBits(r3);
        if (r10 != r3) goto L_0x009b;
    L_0x008f:
        if (r1 == 0) goto L_0x0098;
    L_0x0091:
        r3 = (long) r0;
        r5 = (long) r1;
        r3 = mapType1QuantValues(r3, r5);
        goto L_0x009f;
    L_0x0098:
        r3 = 0;
        goto L_0x009f;
    L_0x009b:
        r3 = (long) r0;
        r5 = (long) r1;
        r3 = r3 * r5;
    L_0x009f:
        r5 = (long) r2;
        r5 = r5 * r3;
        r5 = (int) r5;
        r12.skipBits(r5);
    L_0x00a6:
        r11 = new com.google.android.exoplayer2.extractor.ogg.VorbisUtil$CodeBook;
        r2 = r11;
        r3 = r1;
        r4 = r0;
        r5 = r8;
        r6 = r10;
        r7 = r9;
        r2.<init>(r3, r4, r5, r6, r7);
        return r11;
    L_0x00b2:
        r2 = new com.google.android.exoplayer2.ParserException;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "lookup type greater than 2 not decodable: ";
        r3.append(r4);
        r3.append(r10);
        r3 = r3.toString();
        r2.<init>(r3);
        throw r2;
    L_0x00c9:
        r0 = new com.google.android.exoplayer2.ParserException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "expected code book to start with [0x56, 0x43, 0x42] at ";
        r1.append(r2);
        r2 = r12.getPosition();
        r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.ogg.VorbisUtil.readBook(com.google.android.exoplayer2.extractor.ogg.VorbisBitArray):com.google.android.exoplayer2.extractor.ogg.VorbisUtil$CodeBook");
    }

    public static com.google.android.exoplayer2.extractor.ogg.VorbisUtil.CommentHeader readVorbisCommentHeader(com.google.android.exoplayer2.util.ParsableByteArray r10) throws com.google.android.exoplayer2.ParserException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:10:0x0054 in {3, 7, 9} preds:[]
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
        r0 = 3;
        r1 = 0;
        verifyVorbisHeaderCapturePattern(r0, r10, r1);
        r0 = 7;
        r1 = r10.readLittleEndianUnsignedInt();
        r1 = (int) r1;
        r0 = r0 + 4;
        r2 = r10.readString(r1);
        r3 = r2.length();
        r0 = r0 + r3;
        r3 = r10.readLittleEndianUnsignedInt();
        r5 = (int) r3;
        r5 = new java.lang.String[r5];
        r0 = r0 + 4;
        r6 = 0;
    L_0x0020:
        r7 = (long) r6;
        r9 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1));
        if (r9 >= 0) goto L_0x003c;
    L_0x0025:
        r7 = r10.readLittleEndianUnsignedInt();
        r1 = (int) r7;
        r0 = r0 + 4;
        r7 = r10.readString(r1);
        r5[r6] = r7;
        r7 = r5[r6];
        r7 = r7.length();
        r0 = r0 + r7;
        r6 = r6 + 1;
        goto L_0x0020;
    L_0x003c:
        r6 = r10.readUnsignedByte();
        r6 = r6 & 1;
        if (r6 == 0) goto L_0x004c;
    L_0x0044:
        r0 = r0 + 1;
        r6 = new com.google.android.exoplayer2.extractor.ogg.VorbisUtil$CommentHeader;
        r6.<init>(r2, r5, r0);
        return r6;
    L_0x004c:
        r6 = new com.google.android.exoplayer2.ParserException;
        r7 = "framing bit expected to be set";
        r6.<init>(r7);
        throw r6;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.ogg.VorbisUtil.readVorbisCommentHeader(com.google.android.exoplayer2.util.ParsableByteArray):com.google.android.exoplayer2.extractor.ogg.VorbisUtil$CommentHeader");
    }

    public static com.google.android.exoplayer2.extractor.ogg.VorbisUtil.Mode[] readVorbisModes(com.google.android.exoplayer2.util.ParsableByteArray r6, int r7) throws com.google.android.exoplayer2.ParserException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:15:0x005e in {2, 7, 9, 12, 14} preds:[]
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
        r0 = 5;
        r1 = 0;
        verifyVorbisHeaderCapturePattern(r0, r6, r1);
        r0 = r6.readUnsignedByte();
        r0 = r0 + 1;
        r1 = new com.google.android.exoplayer2.extractor.ogg.VorbisBitArray;
        r2 = r6.data;
        r1.<init>(r2);
        r2 = r6.getPosition();
        r2 = r2 * 8;
        r1.skipBits(r2);
        r2 = 0;
    L_0x001c:
        if (r2 >= r0) goto L_0x0024;
    L_0x001e:
        readBook(r1);
        r2 = r2 + 1;
        goto L_0x001c;
    L_0x0024:
        r2 = 6;
        r2 = r1.readBits(r2);
        r2 = r2 + 1;
        r3 = 0;
    L_0x002c:
        if (r3 >= r2) goto L_0x0041;
    L_0x002e:
        r4 = 16;
        r4 = r1.readBits(r4);
        if (r4 != 0) goto L_0x0039;
    L_0x0036:
        r3 = r3 + 1;
        goto L_0x002c;
    L_0x0039:
        r4 = new com.google.android.exoplayer2.ParserException;
        r5 = "placeholder of time domain transforms not zeroed out";
        r4.<init>(r5);
        throw r4;
        readFloors(r1);
        readResidues(r1);
        readMappings(r7, r1);
        r3 = readModes(r1);
        r4 = r1.readBit();
        if (r4 == 0) goto L_0x0056;
    L_0x0055:
        return r3;
    L_0x0056:
        r4 = new com.google.android.exoplayer2.ParserException;
        r5 = "framing bit after modes not set as expected";
        r4.<init>(r5);
        throw r4;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.ogg.VorbisUtil.readVorbisModes(com.google.android.exoplayer2.util.ParsableByteArray, int):com.google.android.exoplayer2.extractor.ogg.VorbisUtil$Mode[]");
    }

    public static int iLog(int x) {
        int val = 0;
        while (x > 0) {
            val++;
            x >>>= 1;
        }
        return val;
    }

    public static VorbisIdHeader readVorbisIdentificationHeader(ParsableByteArray headerData) throws ParserException {
        ParsableByteArray parsableByteArray = headerData;
        verifyVorbisHeaderCapturePattern(1, parsableByteArray, false);
        long version = headerData.readLittleEndianUnsignedInt();
        int channels = headerData.readUnsignedByte();
        long sampleRate = headerData.readLittleEndianUnsignedInt();
        int bitrateMax = headerData.readLittleEndianInt();
        int bitrateNominal = headerData.readLittleEndianInt();
        int bitrateMin = headerData.readLittleEndianInt();
        int blockSize = headerData.readUnsignedByte();
        int blockSize0 = (int) Math.pow(2.0d, (double) (blockSize & 15));
        return new VorbisIdHeader(version, channels, sampleRate, bitrateMax, bitrateNominal, bitrateMin, blockSize0, (int) Math.pow(2.0d, (double) ((blockSize & PsExtractor.VIDEO_STREAM_MASK) >> 4)), (headerData.readUnsignedByte() & 1) > 0, Arrays.copyOf(parsableByteArray.data, headerData.limit()));
    }

    public static boolean verifyVorbisHeaderCapturePattern(int headerType, ParsableByteArray header, boolean quiet) throws ParserException {
        StringBuilder stringBuilder;
        if (header.bytesLeft() < 7) {
            if (quiet) {
                return false;
            }
            stringBuilder = new StringBuilder();
            stringBuilder.append("too short header: ");
            stringBuilder.append(header.bytesLeft());
            throw new ParserException(stringBuilder.toString());
        } else if (header.readUnsignedByte() == headerType) {
            if (header.readUnsignedByte() == 118) {
                if (header.readUnsignedByte() == 111) {
                    if (header.readUnsignedByte() == 114) {
                        if (header.readUnsignedByte() == 98) {
                            if (header.readUnsignedByte() == 105) {
                                if (header.readUnsignedByte() == 115) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
            if (quiet) {
                return false;
            }
            throw new ParserException("expected characters 'vorbis'");
        } else if (quiet) {
            return false;
        } else {
            stringBuilder = new StringBuilder();
            stringBuilder.append("expected header type ");
            stringBuilder.append(Integer.toHexString(headerType));
            throw new ParserException(stringBuilder.toString());
        }
    }

    private static Mode[] readModes(VorbisBitArray bitArray) {
        int modeCount = bitArray.readBits(6) + 1;
        Mode[] modes = new Mode[modeCount];
        for (int i = 0; i < modeCount; i++) {
            modes[i] = new Mode(bitArray.readBit(), bitArray.readBits(16), bitArray.readBits(16), bitArray.readBits(8));
        }
        return modes;
    }

    private static void readMappings(int channels, VorbisBitArray bitArray) throws ParserException {
        int mappingsCount = bitArray.readBits(6) + 1;
        for (int i = 0; i < mappingsCount; i++) {
            int mappingType = bitArray.readBits(16);
            if (mappingType != 0) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("mapping type other than 0 not supported: ");
                stringBuilder.append(mappingType);
                Log.m6e(str, stringBuilder.toString());
            } else {
                int submaps;
                int couplingSteps;
                if (bitArray.readBit()) {
                    submaps = bitArray.readBits(4) + 1;
                } else {
                    submaps = 1;
                }
                if (bitArray.readBit()) {
                    couplingSteps = bitArray.readBits(8) + 1;
                    for (int j = 0; j < couplingSteps; j++) {
                        bitArray.skipBits(iLog(channels - 1));
                        bitArray.skipBits(iLog(channels - 1));
                    }
                }
                if (bitArray.readBits(2) == 0) {
                    if (submaps > 1) {
                        for (couplingSteps = 0; couplingSteps < channels; couplingSteps++) {
                            bitArray.skipBits(4);
                        }
                    }
                    for (int j2 = 0; j2 < submaps; j2++) {
                        bitArray.skipBits(8);
                        bitArray.skipBits(8);
                        bitArray.skipBits(8);
                    }
                } else {
                    throw new ParserException("to reserved bits must be zero after mapping coupling steps");
                }
            }
        }
    }

    private static void readResidues(VorbisBitArray bitArray) throws ParserException {
        int residueCount = bitArray.readBits(6) + 1;
        int i = 0;
        while (i < residueCount) {
            if (bitArray.readBits(16) <= 2) {
                int j;
                int highBits;
                bitArray.skipBits(24);
                bitArray.skipBits(24);
                bitArray.skipBits(24);
                int classifications = bitArray.readBits(6) + 1;
                bitArray.skipBits(8);
                int[] cascade = new int[classifications];
                for (j = 0; j < classifications; j++) {
                    highBits = 0;
                    int lowBits = bitArray.readBits(3);
                    if (bitArray.readBit()) {
                        highBits = bitArray.readBits(5);
                    }
                    cascade[j] = (highBits * 8) + lowBits;
                }
                for (j = 0; j < classifications; j++) {
                    for (highBits = 0; highBits < 8; highBits++) {
                        if ((cascade[j] & (1 << highBits)) != 0) {
                            bitArray.skipBits(8);
                        }
                    }
                }
                i++;
            } else {
                throw new ParserException("residueType greater than 2 is not decodable");
            }
        }
    }

    private static void readFloors(VorbisBitArray bitArray) throws ParserException {
        int floorCount = bitArray.readBits(6) + 1;
        for (int i = 0; i < floorCount; i++) {
            int floorType = bitArray.readBits(16);
            int floorNumberOfBooks;
            int j;
            switch (floorType) {
                case 0:
                    bitArray.skipBits(8);
                    bitArray.skipBits(16);
                    bitArray.skipBits(16);
                    bitArray.skipBits(6);
                    bitArray.skipBits(8);
                    floorNumberOfBooks = bitArray.readBits(4) + 1;
                    for (j = 0; j < floorNumberOfBooks; j++) {
                        bitArray.skipBits(8);
                    }
                    break;
                case 1:
                    int j2;
                    int classSubclasses;
                    floorNumberOfBooks = bitArray.readBits(5);
                    int maximumClass = -1;
                    int[] partitionClassList = new int[floorNumberOfBooks];
                    for (int j3 = 0; j3 < floorNumberOfBooks; j3++) {
                        partitionClassList[j3] = bitArray.readBits(4);
                        if (partitionClassList[j3] > maximumClass) {
                            maximumClass = partitionClassList[j3];
                        }
                    }
                    int[] classDimensions = new int[(maximumClass + 1)];
                    for (j2 = 0; j2 < classDimensions.length; j2++) {
                        classDimensions[j2] = bitArray.readBits(3) + 1;
                        classSubclasses = bitArray.readBits(2);
                        if (classSubclasses > 0) {
                            bitArray.skipBits(8);
                        }
                        for (int k = 0; k < (1 << classSubclasses); k++) {
                            bitArray.skipBits(8);
                        }
                    }
                    bitArray.skipBits(2);
                    j = bitArray.readBits(4);
                    int count = 0;
                    classSubclasses = 0;
                    for (j2 = 0; j2 < floorNumberOfBooks; j2++) {
                        count += classDimensions[partitionClassList[j2]];
                        while (classSubclasses < count) {
                            bitArray.skipBits(j);
                            classSubclasses++;
                        }
                    }
                    break;
                default:
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("floor type greater than 1 not decodable: ");
                    stringBuilder.append(floorType);
                    throw new ParserException(stringBuilder.toString());
            }
        }
    }

    private static long mapType1QuantValues(long entries, long dimension) {
        double d = (double) entries;
        double d2 = (double) dimension;
        Double.isNaN(d2);
        return (long) Math.floor(Math.pow(d, 1.0d / d2));
    }

    private VorbisUtil() {
    }
}
