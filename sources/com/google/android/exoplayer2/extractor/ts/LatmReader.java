package com.google.android.exoplayer2.extractor.ts;

import android.support.annotation.Nullable;
import android.util.Pair;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.extractor.ts.TsPayloadReader.TrackIdGenerator;
import com.google.android.exoplayer2.util.CodecSpecificDataUtil;
import com.google.android.exoplayer2.util.ParsableBitArray;
import com.google.android.exoplayer2.util.ParsableByteArray;

public final class LatmReader implements ElementaryStreamReader {
    private static final int INITIAL_BUFFER_SIZE = 1024;
    private static final int STATE_FINDING_SYNC_1 = 0;
    private static final int STATE_FINDING_SYNC_2 = 1;
    private static final int STATE_READING_HEADER = 2;
    private static final int STATE_READING_SAMPLE = 3;
    private static final int SYNC_BYTE_FIRST = 86;
    private static final int SYNC_BYTE_SECOND = 224;
    private int audioMuxVersionA;
    private int bytesRead;
    private int channelCount;
    private Format format;
    private String formatId;
    private int frameLengthType;
    private final String language;
    private int numSubframes;
    private long otherDataLenBits;
    private boolean otherDataPresent;
    private TrackOutput output;
    private final ParsableBitArray sampleBitArray = new ParsableBitArray(this.sampleDataBuffer.data);
    private final ParsableByteArray sampleDataBuffer = new ParsableByteArray(1024);
    private long sampleDurationUs;
    private int sampleRateHz;
    private int sampleSize;
    private int secondHeaderByte;
    private int state;
    private boolean streamMuxRead;
    private long timeUs;

    private int parsePayloadLengthInfo(com.google.android.exoplayer2.util.ParsableBitArray r4) throws com.google.android.exoplayer2.ParserException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:8:0x0018 in {4, 5, 7} preds:[]
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
        r3 = this;
        r0 = 0;
        r1 = r3.frameLengthType;
        if (r1 != 0) goto L_0x0012;
    L_0x0005:
        r1 = 8;
        r1 = r4.readBits(r1);
        r0 = r0 + r1;
        r2 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        if (r1 == r2) goto L_0x0011;
    L_0x0010:
        return r0;
    L_0x0011:
        goto L_0x0005;
    L_0x0012:
        r1 = new com.google.android.exoplayer2.ParserException;
        r1.<init>();
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.ts.LatmReader.parsePayloadLengthInfo(com.google.android.exoplayer2.util.ParsableBitArray):int");
    }

    private void parseStreamMuxConfig(com.google.android.exoplayer2.util.ParsableBitArray r23) throws com.google.android.exoplayer2.ParserException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:45:0x00ed in {2, 3, 7, 8, 18, 19, 20, 21, 26, 30, 31, 32, 35, 36, 38, 40, 42, 44} preds:[]
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
        r22 = this;
        r0 = r22;
        r1 = r23;
        r2 = 1;
        r3 = r1.readBits(r2);
        r4 = 0;
        if (r3 != r2) goto L_0x0011;
    L_0x000c:
        r5 = r1.readBits(r2);
        goto L_0x0012;
    L_0x0011:
        r5 = 0;
    L_0x0012:
        r0.audioMuxVersionA = r5;
        r5 = r0.audioMuxVersionA;
        if (r5 != 0) goto L_0x00e7;
    L_0x0018:
        if (r3 != r2) goto L_0x001e;
    L_0x001a:
        latmGetValue(r23);
        goto L_0x001f;
    L_0x001f:
        r5 = r23.readBit();
        if (r5 == 0) goto L_0x00e1;
    L_0x0025:
        r5 = 6;
        r5 = r1.readBits(r5);
        r0.numSubframes = r5;
        r5 = 4;
        r5 = r1.readBits(r5);
        r6 = 3;
        r6 = r1.readBits(r6);
        if (r5 != 0) goto L_0x00da;
    L_0x0038:
        if (r6 != 0) goto L_0x00da;
    L_0x003a:
        r7 = 8;
        if (r3 != 0) goto L_0x008b;
    L_0x003e:
        r8 = r23.getPosition();
        r9 = r22.parseAudioSpecificConfig(r23);
        r1.setPosition(r8);
        r10 = r9 + 7;
        r10 = r10 / r7;
        r10 = new byte[r10];
        r1.readBits(r10, r4, r9);
        r11 = r0.formatId;
        r12 = "audio/mp4a-latm";
        r13 = 0;
        r14 = -1;
        r15 = -1;
        r4 = r0.channelCount;
        r7 = r0.sampleRateHz;
        r18 = java.util.Collections.singletonList(r10);
        r19 = 0;
        r20 = 0;
        r2 = r0.language;
        r16 = r4;
        r17 = r7;
        r21 = r2;
        r2 = com.google.android.exoplayer2.Format.createAudioSampleFormat(r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, r21);
        r4 = r0.format;
        r4 = r2.equals(r4);
        if (r4 != 0) goto L_0x0089;
    L_0x0078:
        r0.format = r2;
        r11 = 1024000000; // 0x3d090000 float:0.033447266 double:5.059232213E-315;
        r4 = r2.sampleRate;
        r13 = (long) r4;
        r11 = r11 / r13;
        r0.sampleDurationUs = r11;
        r4 = r0.output;
        r4.format(r2);
        goto L_0x008a;
    L_0x008a:
        goto L_0x0099;
    L_0x008b:
        r7 = latmGetValue(r23);
        r2 = (int) r7;
        r4 = r22.parseAudioSpecificConfig(r23);
        r7 = r2 - r4;
        r1.skipBits(r7);
    L_0x0099:
        r22.parseFrameLength(r23);
        r2 = r23.readBit();
        r0.otherDataPresent = r2;
        r7 = 0;
        r0.otherDataLenBits = r7;
        r2 = r0.otherDataPresent;
        if (r2 == 0) goto L_0x00ca;
    L_0x00aa:
        r2 = 1;
        if (r3 != r2) goto L_0x00b4;
    L_0x00ad:
        r7 = latmGetValue(r23);
        r0.otherDataLenBits = r7;
        goto L_0x00cb;
    L_0x00b5:
        r2 = r23.readBit();
        r7 = r0.otherDataLenBits;
        r4 = 8;
        r7 = r7 << r4;
        r9 = r1.readBits(r4);
        r9 = (long) r9;
        r7 = r7 + r9;
        r0.otherDataLenBits = r7;
        if (r2 != 0) goto L_0x00c9;
    L_0x00c8:
        goto L_0x00cb;
    L_0x00c9:
        goto L_0x00b5;
    L_0x00cb:
        r2 = r23.readBit();
        if (r2 == 0) goto L_0x00d7;
    L_0x00d1:
        r4 = 8;
        r1.skipBits(r4);
        goto L_0x00d8;
        return;
        r2 = new com.google.android.exoplayer2.ParserException;
        r2.<init>();
        throw r2;
    L_0x00e1:
        r2 = new com.google.android.exoplayer2.ParserException;
        r2.<init>();
        throw r2;
    L_0x00e7:
        r2 = new com.google.android.exoplayer2.ParserException;
        r2.<init>();
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.ts.LatmReader.parseStreamMuxConfig(com.google.android.exoplayer2.util.ParsableBitArray):void");
    }

    public LatmReader(@Nullable String language) {
        this.language = language;
    }

    public void seek() {
        this.state = 0;
        this.streamMuxRead = false;
    }

    public void createTracks(ExtractorOutput extractorOutput, TrackIdGenerator idGenerator) {
        idGenerator.generateNewId();
        this.output = extractorOutput.track(idGenerator.getTrackId(), 1);
        this.formatId = idGenerator.getFormatId();
    }

    public void packetStarted(long pesTimeUs, int flags) {
        this.timeUs = pesTimeUs;
    }

    public void consume(ParsableByteArray data) throws ParserException {
        while (data.bytesLeft() > 0) {
            int secondByte;
            switch (this.state) {
                case 0:
                    if (data.readUnsignedByte() != 86) {
                        break;
                    }
                    this.state = 1;
                    break;
                case 1:
                    secondByte = data.readUnsignedByte();
                    if ((secondByte & 224) != 224) {
                        if (secondByte == 86) {
                            break;
                        }
                        this.state = 0;
                        break;
                    }
                    this.secondHeaderByte = secondByte;
                    this.state = 2;
                    break;
                case 2:
                    this.sampleSize = ((this.secondHeaderByte & -225) << 8) | data.readUnsignedByte();
                    if (this.sampleSize > this.sampleDataBuffer.data.length) {
                        resetBufferForSize(this.sampleSize);
                    }
                    this.bytesRead = 0;
                    this.state = 3;
                    break;
                case 3:
                    secondByte = Math.min(data.bytesLeft(), this.sampleSize - this.bytesRead);
                    data.readBytes(this.sampleBitArray.data, this.bytesRead, secondByte);
                    this.bytesRead += secondByte;
                    if (this.bytesRead != this.sampleSize) {
                        break;
                    }
                    this.sampleBitArray.setPosition(0);
                    parseAudioMuxElement(this.sampleBitArray);
                    this.state = 0;
                    break;
                default:
                    throw new IllegalStateException();
            }
        }
    }

    public void packetFinished() {
    }

    private void parseAudioMuxElement(ParsableBitArray data) throws ParserException {
        if (!data.readBit()) {
            this.streamMuxRead = true;
            parseStreamMuxConfig(data);
        } else if (!this.streamMuxRead) {
            return;
        }
        if (this.audioMuxVersionA != 0) {
            throw new ParserException();
        } else if (this.numSubframes == 0) {
            parsePayloadMux(data, parsePayloadLengthInfo(data));
            if (this.otherDataPresent) {
                data.skipBits((int) this.otherDataLenBits);
            }
        } else {
            throw new ParserException();
        }
    }

    private void parseFrameLength(ParsableBitArray data) {
        this.frameLengthType = data.readBits(3);
        switch (this.frameLengthType) {
            case 0:
                data.skipBits(8);
                return;
            case 1:
                data.skipBits(9);
                return;
            case 3:
            case 4:
            case 5:
                data.skipBits(6);
                return;
            case 6:
            case 7:
                data.skipBits(1);
                return;
            default:
                throw new IllegalStateException();
        }
    }

    private int parseAudioSpecificConfig(ParsableBitArray data) throws ParserException {
        int bitsLeft = data.bitsLeft();
        Pair<Integer, Integer> config = CodecSpecificDataUtil.parseAacAudioSpecificConfig(data, true);
        this.sampleRateHz = ((Integer) config.first).intValue();
        this.channelCount = ((Integer) config.second).intValue();
        return bitsLeft - data.bitsLeft();
    }

    private void parsePayloadMux(ParsableBitArray data, int muxLengthBytes) {
        int bitPosition = data.getPosition();
        if ((bitPosition & 7) == 0) {
            this.sampleDataBuffer.setPosition(bitPosition >> 3);
        } else {
            data.readBits(this.sampleDataBuffer.data, 0, muxLengthBytes * 8);
            this.sampleDataBuffer.setPosition(0);
        }
        this.output.sampleData(this.sampleDataBuffer, muxLengthBytes);
        this.output.sampleMetadata(this.timeUs, 1, muxLengthBytes, 0, null);
        this.timeUs += this.sampleDurationUs;
    }

    private void resetBufferForSize(int newSize) {
        this.sampleDataBuffer.reset(newSize);
        this.sampleBitArray.reset(this.sampleDataBuffer.data);
    }

    private static long latmGetValue(ParsableBitArray data) {
        return (long) data.readBits((data.readBits(2) + 1) * 8);
    }
}
