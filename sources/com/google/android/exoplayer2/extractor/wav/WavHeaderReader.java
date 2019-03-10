package com.google.android.exoplayer2.extractor.wav;

import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;

final class WavHeaderReader {
    private static final String TAG = "WavHeaderReader";

    private static final class ChunkHeader {
        public static final int SIZE_IN_BYTES = 8;
        public final int id;
        public final long size;

        private ChunkHeader(int id, long size) {
            this.id = id;
            this.size = size;
        }

        public static ChunkHeader peek(ExtractorInput input, ParsableByteArray scratch) throws IOException, InterruptedException {
            input.peekFully(scratch.data, 0, 8);
            scratch.setPosition(0);
            return new ChunkHeader(scratch.readInt(), scratch.readLittleEndianUnsignedInt());
        }
    }

    public static com.google.android.exoplayer2.extractor.wav.WavHeader peek(com.google.android.exoplayer2.extractor.ExtractorInput r19) throws java.io.IOException, java.lang.InterruptedException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:25:0x00eb in {2, 6, 10, 13, 14, 20, 22, 24} preds:[]
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
        r0 = r19;
        com.google.android.exoplayer2.util.Assertions.checkNotNull(r19);
        r1 = new com.google.android.exoplayer2.util.ParsableByteArray;
        r2 = 16;
        r1.<init>(r2);
        r3 = com.google.android.exoplayer2.extractor.wav.WavHeaderReader.ChunkHeader.peek(r0, r1);
        r4 = r3.id;
        r5 = com.google.android.exoplayer2.audio.WavUtil.RIFF_FOURCC;
        r6 = 0;
        if (r4 == r5) goto L_0x0018;
    L_0x0017:
        return r6;
    L_0x0018:
        r4 = r1.data;
        r5 = 4;
        r7 = 0;
        r0.peekFully(r4, r7, r5);
        r1.setPosition(r7);
        r4 = r1.readInt();
        r5 = com.google.android.exoplayer2.audio.WavUtil.WAVE_FOURCC;
        if (r4 == r5) goto L_0x0041;
    L_0x002a:
        r2 = "WavHeaderReader";
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r7 = "Unsupported RIFF format: ";
        r5.append(r7);
        r5.append(r4);
        r5 = r5.toString();
        com.google.android.exoplayer2.util.Log.m6e(r2, r5);
        return r6;
    L_0x0041:
        r3 = com.google.android.exoplayer2.extractor.wav.WavHeaderReader.ChunkHeader.peek(r0, r1);
    L_0x0045:
        r5 = r3.id;
        r8 = com.google.android.exoplayer2.audio.WavUtil.FMT_FOURCC;
        if (r5 == r8) goto L_0x0056;
    L_0x004b:
        r8 = r3.size;
        r5 = (int) r8;
        r0.advancePeekPosition(r5);
        r3 = com.google.android.exoplayer2.extractor.wav.WavHeaderReader.ChunkHeader.peek(r0, r1);
        goto L_0x0045;
    L_0x0056:
        r8 = r3.size;
        r10 = 16;
        r5 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
        if (r5 < 0) goto L_0x0060;
    L_0x005e:
        r5 = 1;
        goto L_0x0061;
    L_0x0060:
        r5 = 0;
    L_0x0061:
        com.google.android.exoplayer2.util.Assertions.checkState(r5);
        r5 = r1.data;
        r0.peekFully(r5, r7, r2);
        r1.setPosition(r7);
        r5 = r1.readLittleEndianUnsignedShort();
        r14 = r1.readLittleEndianUnsignedShort();
        r15 = r1.readLittleEndianUnsignedIntToInt();
        r16 = r1.readLittleEndianUnsignedIntToInt();
        r13 = r1.readLittleEndianUnsignedShort();
        r12 = r1.readLittleEndianUnsignedShort();
        r7 = r14 * r12;
        r11 = r7 / 8;
        if (r13 != r11) goto L_0x00c8;
    L_0x008a:
        r17 = com.google.android.exoplayer2.audio.WavUtil.getEncodingForType(r5, r12);
        if (r17 != 0) goto L_0x00af;
    L_0x0090:
        r2 = "WavHeaderReader";
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r8 = "Unsupported WAV format: ";
        r7.append(r8);
        r7.append(r12);
        r8 = " bit/sample, type ";
        r7.append(r8);
        r7.append(r5);
        r7 = r7.toString();
        com.google.android.exoplayer2.util.Log.m6e(r2, r7);
        return r6;
    L_0x00af:
        r6 = r3.size;
        r6 = (int) r6;
        r6 = r6 - r2;
        r0.advancePeekPosition(r6);
        r2 = new com.google.android.exoplayer2.extractor.wav.WavHeader;
        r7 = r2;
        r8 = r14;
        r9 = r15;
        r10 = r16;
        r6 = r11;
        r11 = r13;
        r18 = r12;
        r0 = r13;
        r13 = r17;
        r7.<init>(r8, r9, r10, r11, r12, r13);
        return r2;
    L_0x00c8:
        r6 = r11;
        r18 = r12;
        r0 = r13;
        r2 = new com.google.android.exoplayer2.ParserException;
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r8 = "Expected block alignment: ";
        r7.append(r8);
        r7.append(r6);
        r8 = "; got: ";
        r7.append(r8);
        r7.append(r0);
        r7 = r7.toString();
        r2.<init>(r7);
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.wav.WavHeaderReader.peek(com.google.android.exoplayer2.extractor.ExtractorInput):com.google.android.exoplayer2.extractor.wav.WavHeader");
    }

    public static void skipToData(ExtractorInput input, WavHeader wavHeader) throws IOException, InterruptedException {
        Assertions.checkNotNull(input);
        Assertions.checkNotNull(wavHeader);
        input.resetPeekPosition();
        ParsableByteArray scratch = new ParsableByteArray(8);
        ChunkHeader chunkHeader = ChunkHeader.peek(input, scratch);
        while (chunkHeader.id != Util.getIntegerCodeForString("data")) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Ignoring unknown WAV chunk: ");
            stringBuilder.append(chunkHeader.id);
            Log.m10w(str, stringBuilder.toString());
            long bytesToSkip = chunkHeader.size + 8;
            if (chunkHeader.id == Util.getIntegerCodeForString("RIFF")) {
                bytesToSkip = 12;
            }
            if (bytesToSkip <= 2147483647L) {
                input.skipFully((int) bytesToSkip);
                chunkHeader = ChunkHeader.peek(input, scratch);
            } else {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Chunk is too large (~2GB+) to skip; id: ");
                stringBuilder2.append(chunkHeader.id);
                throw new ParserException(stringBuilder2.toString());
            }
        }
        input.skipFully(8);
        wavHeader.setDataBounds(input.getPosition(), chunkHeader.size);
    }

    private WavHeaderReader() {
    }
}
