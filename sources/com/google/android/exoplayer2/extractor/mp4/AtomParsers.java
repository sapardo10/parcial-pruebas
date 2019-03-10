package com.google.android.exoplayer2.extractor.mp4;

import android.util.Pair;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.audio.Ac3Util;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.Metadata.Entry;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.CodecSpecificDataUtil;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.AvcConfig;
import com.google.android.exoplayer2.video.HevcConfig;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final class AtomParsers {
    private static final int MAX_GAPLESS_TRIM_SIZE_SAMPLES = 3;
    private static final String TAG = "AtomParsers";
    private static final int TYPE_clcp = Util.getIntegerCodeForString("clcp");
    private static final int TYPE_meta = Util.getIntegerCodeForString("meta");
    private static final int TYPE_sbtl = Util.getIntegerCodeForString("sbtl");
    private static final int TYPE_soun = Util.getIntegerCodeForString("soun");
    private static final int TYPE_subt = Util.getIntegerCodeForString("subt");
    private static final int TYPE_text = Util.getIntegerCodeForString("text");
    private static final int TYPE_vide = Util.getIntegerCodeForString("vide");
    private static final byte[] opusMagic = Util.getUtf8Bytes("OpusHead");

    private static final class ChunkIterator {
        private final ParsableByteArray chunkOffsets;
        private final boolean chunkOffsetsAreLongs;
        public int index;
        public final int length;
        private int nextSamplesPerChunkChangeIndex;
        public int numSamples;
        public long offset;
        private int remainingSamplesPerChunkChanges;
        private final ParsableByteArray stsc;

        public ChunkIterator(ParsableByteArray stsc, ParsableByteArray chunkOffsets, boolean chunkOffsetsAreLongs) {
            this.stsc = stsc;
            this.chunkOffsets = chunkOffsets;
            this.chunkOffsetsAreLongs = chunkOffsetsAreLongs;
            chunkOffsets.setPosition(12);
            this.length = chunkOffsets.readUnsignedIntToInt();
            stsc.setPosition(12);
            this.remainingSamplesPerChunkChanges = stsc.readUnsignedIntToInt();
            boolean z = true;
            if (stsc.readInt() != 1) {
                z = false;
            }
            Assertions.checkState(z, "first_chunk must be 1");
            this.index = -1;
        }

        public boolean moveNext() {
            int i = this.index + 1;
            this.index = i;
            if (i == this.length) {
                return false;
            }
            long readUnsignedLongToLong;
            if (this.chunkOffsetsAreLongs) {
                readUnsignedLongToLong = this.chunkOffsets.readUnsignedLongToLong();
            } else {
                readUnsignedLongToLong = this.chunkOffsets.readUnsignedInt();
            }
            this.offset = readUnsignedLongToLong;
            if (this.index == this.nextSamplesPerChunkChangeIndex) {
                this.numSamples = this.stsc.readUnsignedIntToInt();
                this.stsc.skipBytes(4);
                i = this.remainingSamplesPerChunkChanges - 1;
                this.remainingSamplesPerChunkChanges = i;
                this.nextSamplesPerChunkChangeIndex = i > 0 ? this.stsc.readUnsignedIntToInt() - 1 : -1;
            }
            return true;
        }
    }

    private interface SampleSizeBox {
        int getSampleCount();

        boolean isFixedSampleSize();

        int readNextSampleSize();
    }

    private static final class StsdData {
        public static final int STSD_HEADER_SIZE = 8;
        public Format format;
        public int nalUnitLengthFieldLength;
        public int requiredSampleTransformation = 0;
        public final TrackEncryptionBox[] trackEncryptionBoxes;

        public StsdData(int numberOfEntries) {
            this.trackEncryptionBoxes = new TrackEncryptionBox[numberOfEntries];
        }
    }

    private static final class TkhdData {
        private final long duration;
        private final int id;
        private final int rotationDegrees;

        public TkhdData(int id, long duration, int rotationDegrees) {
            this.id = id;
            this.duration = duration;
            this.rotationDegrees = rotationDegrees;
        }
    }

    static final class StszSampleSizeBox implements SampleSizeBox {
        private final ParsableByteArray data;
        private final int fixedSampleSize = this.data.readUnsignedIntToInt();
        private final int sampleCount = this.data.readUnsignedIntToInt();

        public StszSampleSizeBox(LeafAtom stszAtom) {
            this.data = stszAtom.data;
            this.data.setPosition(12);
        }

        public int getSampleCount() {
            return this.sampleCount;
        }

        public int readNextSampleSize() {
            int i = this.fixedSampleSize;
            return i == 0 ? this.data.readUnsignedIntToInt() : i;
        }

        public boolean isFixedSampleSize() {
            return this.fixedSampleSize != 0;
        }
    }

    static final class Stz2SampleSizeBox implements SampleSizeBox {
        private int currentByte;
        private final ParsableByteArray data;
        private final int fieldSize = (this.data.readUnsignedIntToInt() & 255);
        private final int sampleCount = this.data.readUnsignedIntToInt();
        private int sampleIndex;

        public Stz2SampleSizeBox(LeafAtom stz2Atom) {
            this.data = stz2Atom.data;
            this.data.setPosition(12);
        }

        public int getSampleCount() {
            return this.sampleCount;
        }

        public int readNextSampleSize() {
            int i = this.fieldSize;
            if (i == 8) {
                return this.data.readUnsignedByte();
            }
            if (i == 16) {
                return this.data.readUnsignedShort();
            }
            i = this.sampleIndex;
            this.sampleIndex = i + 1;
            if (i % 2 != 0) {
                return this.currentByte & 15;
            }
            this.currentByte = this.data.readUnsignedByte();
            return (this.currentByte & PsExtractor.VIDEO_STREAM_MASK) >> 4;
        }

        public boolean isFixedSampleSize() {
            return false;
        }
    }

    public static com.google.android.exoplayer2.extractor.mp4.TrackSampleTable parseStbl(com.google.android.exoplayer2.extractor.mp4.Track r72, com.google.android.exoplayer2.extractor.mp4.Atom.ContainerAtom r73, com.google.android.exoplayer2.extractor.GaplessInfoHolder r74) throws com.google.android.exoplayer2.ParserException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:213:0x065f in {2, 5, 9, 12, 13, 16, 17, 20, 23, 24, 29, 30, 31, 39, 40, 41, 42, 51, 54, 58, 59, 60, 63, 64, 67, 68, 73, 74, 75, 79, 80, 81, 82, 87, 88, 89, 96, 97, 100, 101, 102, 103, 107, 108, 113, 126, 127, 133, 134, 135, 136, 144, 146, 149, 150, 161, 162, 165, 166, 167, 168, 169, 172, 173, 176, 177, 180, 181, 184, 185, 187, 188, 194, 195, 202, 203, 204, 205, 207, 208, 210, 212} preds:[]
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
        r9 = r72;
        r10 = r73;
        r11 = r74;
        r0 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_stsz;
        r12 = r10.getLeafAtomOfType(r0);
        if (r12 == 0) goto L_0x0015;
    L_0x000e:
        r0 = new com.google.android.exoplayer2.extractor.mp4.AtomParsers$StszSampleSizeBox;
        r0.<init>(r12);
        r13 = r0;
        goto L_0x0024;
    L_0x0015:
        r0 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_stz2;
        r0 = r10.getLeafAtomOfType(r0);
        if (r0 == 0) goto L_0x0655;
    L_0x001d:
        r1 = new com.google.android.exoplayer2.extractor.mp4.AtomParsers$Stz2SampleSizeBox;
        r1.<init>(r0);
        r0 = r1;
        r13 = r0;
    L_0x0024:
        r14 = r13.getSampleCount();
        r0 = 0;
        if (r14 != 0) goto L_0x0042;
    L_0x002b:
        r15 = new com.google.android.exoplayer2.extractor.mp4.TrackSampleTable;
        r2 = new long[r0];
        r3 = new int[r0];
        r4 = 0;
        r5 = new long[r0];
        r6 = new int[r0];
        r7 = -9223372036854775807; // 0x8000000000000001 float:1.4E-45 double:-4.9E-324;
        r0 = r15;
        r1 = r72;
        r0.<init>(r1, r2, r3, r4, r5, r6, r7);
        return r15;
    L_0x0042:
        r1 = 0;
        r2 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_stco;
        r2 = r10.getLeafAtomOfType(r2);
        if (r2 != 0) goto L_0x0055;
    L_0x004b:
        r1 = 1;
        r3 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_co64;
        r2 = r10.getLeafAtomOfType(r3);
        r7 = r1;
        r15 = r2;
        goto L_0x0057;
    L_0x0055:
        r7 = r1;
        r15 = r2;
    L_0x0057:
        r8 = r15.data;
        r1 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_stsc;
        r1 = r10.getLeafAtomOfType(r1);
        r6 = r1.data;
        r1 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_stts;
        r1 = r10.getLeafAtomOfType(r1);
        r5 = r1.data;
        r1 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_stss;
        r4 = r10.getLeafAtomOfType(r1);
        r1 = 0;
        if (r4 == 0) goto L_0x0075;
    L_0x0072:
        r2 = r4.data;
        goto L_0x0076;
    L_0x0075:
        r2 = r1;
    L_0x0076:
        r3 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_ctts;
        r3 = r10.getLeafAtomOfType(r3);
        if (r3 == 0) goto L_0x0081;
    L_0x007e:
        r1 = r3.data;
    L_0x0081:
        r0 = new com.google.android.exoplayer2.extractor.mp4.AtomParsers$ChunkIterator;
        r0.<init>(r6, r8, r7);
        r17 = r3;
        r3 = 12;
        r5.setPosition(r3);
        r18 = r5.readUnsignedIntToInt();
        r3 = 1;
        r18 = r18 + -1;
        r20 = r5.readUnsignedIntToInt();
        r3 = r5.readUnsignedIntToInt();
        r22 = 0;
        r23 = 0;
        r24 = 0;
        if (r1 == 0) goto L_0x00b0;
    L_0x00a4:
        r25 = r4;
        r4 = 12;
        r1.setPosition(r4);
        r23 = r1.readUnsignedIntToInt();
        goto L_0x00b2;
    L_0x00b0:
        r25 = r4;
    L_0x00b2:
        r4 = -1;
        r26 = 0;
        if (r2 == 0) goto L_0x00d5;
    L_0x00b7:
        r27 = r4;
        r4 = 12;
        r2.setPosition(r4);
        r26 = r2.readUnsignedIntToInt();
        if (r26 <= 0) goto L_0x00d1;
    L_0x00c4:
        r4 = r2.readUnsignedIntToInt();
        r19 = 1;
        r4 = r4 + -1;
        r19 = r2;
        r27 = r4;
        goto L_0x00d9;
    L_0x00d1:
        r2 = 0;
        r19 = r2;
        goto L_0x00d9;
    L_0x00d5:
        r27 = r4;
        r19 = r2;
        r2 = r13.isFixedSampleSize();
        if (r2 == 0) goto L_0x00f5;
    L_0x00e0:
        r2 = "audio/raw";
        r4 = r9.format;
        r4 = r4.sampleMimeType;
        r2 = r2.equals(r4);
        if (r2 == 0) goto L_0x00f4;
    L_0x00ec:
        if (r18 != 0) goto L_0x00f4;
    L_0x00ee:
        if (r23 != 0) goto L_0x00f4;
    L_0x00f0:
        if (r26 != 0) goto L_0x00f4;
    L_0x00f2:
        r2 = 1;
        goto L_0x00f7;
    L_0x00f4:
        goto L_0x00f6;
    L_0x00f6:
        r2 = 0;
    L_0x00f7:
        r28 = r2;
        r2 = 0;
        r29 = 0;
        if (r28 != 0) goto L_0x02a2;
    L_0x00fe:
        r4 = new long[r14];
        r31 = r2;
        r2 = new int[r14];
        r32 = r6;
        r6 = new long[r14];
        r33 = r7;
        r7 = new int[r14];
        r34 = 0;
        r36 = 0;
        r37 = 0;
        r10 = r26;
        r9 = r27;
        r11 = r31;
        r26 = r34;
        r34 = r8;
        r35 = r12;
        r12 = r20;
        r20 = r24;
        r8 = r3;
        r3 = r37;
        r37 = r15;
        r15 = r18;
        r18 = r22;
    L_0x012b:
        if (r3 >= r14) goto L_0x01e8;
    L_0x012d:
        r22 = 1;
    L_0x012f:
        if (r36 != 0) goto L_0x014a;
    L_0x0131:
        r24 = r0.moveNext();
        r22 = r24;
        if (r24 == 0) goto L_0x014a;
    L_0x0139:
        r38 = r14;
        r24 = r15;
        r14 = r0.offset;
        r26 = r14;
        r14 = r0.numSamples;
        r36 = r14;
        r15 = r24;
        r14 = r38;
        goto L_0x012f;
    L_0x014a:
        r38 = r14;
        r24 = r15;
        if (r22 != 0) goto L_0x016f;
    L_0x0150:
        r14 = "AtomParsers";
        r15 = "Unexpected end of chunk data";
        com.google.android.exoplayer2.util.Log.m10w(r14, r15);
        r14 = r3;
        r4 = java.util.Arrays.copyOf(r4, r14);
        r2 = java.util.Arrays.copyOf(r2, r14);
        r6 = java.util.Arrays.copyOf(r6, r14);
        r7 = java.util.Arrays.copyOf(r7, r14);
        r39 = r0;
        r15 = r1;
        r0 = r36;
        goto L_0x01f1;
    L_0x016f:
        if (r1 == 0) goto L_0x0186;
    L_0x0171:
        if (r18 != 0) goto L_0x0180;
    L_0x0173:
        if (r23 <= 0) goto L_0x0180;
    L_0x0175:
        r18 = r1.readUnsignedIntToInt();
        r20 = r1.readInt();
        r23 = r23 + -1;
        goto L_0x0171;
        r18 = r18 + -1;
        r14 = r20;
        goto L_0x0188;
    L_0x0186:
        r14 = r20;
    L_0x0188:
        r4[r3] = r26;
        r15 = r13.readNextSampleSize();
        r2[r3] = r15;
        r15 = r2[r3];
        if (r15 <= r11) goto L_0x0197;
    L_0x0194:
        r11 = r2[r3];
        goto L_0x0198;
    L_0x0198:
        r39 = r0;
        r15 = r1;
        r0 = (long) r14;
        r0 = r29 + r0;
        r6[r3] = r0;
        if (r19 != 0) goto L_0x01a4;
    L_0x01a2:
        r0 = 1;
        goto L_0x01a5;
    L_0x01a4:
        r0 = 0;
    L_0x01a5:
        r7[r3] = r0;
        if (r3 != r9) goto L_0x01b8;
    L_0x01a9:
        r0 = 1;
        r7[r3] = r0;
        r10 = r10 + -1;
        if (r10 <= 0) goto L_0x01b7;
    L_0x01b0:
        r1 = r19.readUnsignedIntToInt();
        r9 = r1 + -1;
        goto L_0x01b9;
    L_0x01b7:
        goto L_0x01b9;
    L_0x01b9:
        r0 = (long) r8;
        r29 = r29 + r0;
        r12 = r12 + -1;
        if (r12 != 0) goto L_0x01ce;
    L_0x01c0:
        if (r24 <= 0) goto L_0x01ce;
    L_0x01c2:
        r0 = r5.readUnsignedIntToInt();
        r8 = r5.readInt();
        r1 = r24 + -1;
        r12 = r0;
        goto L_0x01d1;
        r1 = r24;
    L_0x01d1:
        r0 = r2[r3];
        r20 = r1;
        r0 = (long) r0;
        r26 = r26 + r0;
        r36 = r36 + -1;
        r3 = r3 + 1;
        r1 = r15;
        r15 = r20;
        r0 = r39;
        r20 = r14;
        r14 = r38;
        goto L_0x012b;
    L_0x01e8:
        r39 = r0;
        r38 = r14;
        r24 = r15;
        r15 = r1;
        r0 = r36;
    L_0x01f1:
        r1 = r20;
        r20 = r2;
        r2 = (long) r1;
        r2 = r29 + r2;
        r22 = 1;
    L_0x01fa:
        if (r23 <= 0) goto L_0x020b;
    L_0x01fc:
        r31 = r15.readUnsignedIntToInt();
        if (r31 == 0) goto L_0x0205;
    L_0x0202:
        r22 = 0;
        goto L_0x020c;
    L_0x0205:
        r15.readInt();
        r23 = r23 + -1;
        goto L_0x01fa;
    L_0x020c:
        if (r10 != 0) goto L_0x022a;
    L_0x020e:
        if (r12 != 0) goto L_0x022a;
    L_0x0210:
        if (r0 != 0) goto L_0x022a;
    L_0x0212:
        if (r24 != 0) goto L_0x022a;
    L_0x0214:
        if (r18 != 0) goto L_0x022a;
    L_0x0216:
        if (r22 != 0) goto L_0x0219;
    L_0x0218:
        goto L_0x022a;
    L_0x0219:
        r31 = r1;
        r40 = r2;
        r36 = r5;
        r5 = r24;
        r3 = r72;
        r70 = r18;
        r18 = r4;
        r4 = r70;
        goto L_0x0286;
    L_0x022a:
        r31 = r1;
        r1 = "AtomParsers";
        r40 = r2;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "Inconsistent stbl box for track ";
        r2.append(r3);
        r36 = r5;
        r3 = r72;
        r70 = r18;
        r18 = r4;
        r4 = r70;
        r5 = r3.id;
        r2.append(r5);
        r5 = ": remainingSynchronizationSamples ";
        r2.append(r5);
        r2.append(r10);
        r5 = ", remainingSamplesAtTimestampDelta ";
        r2.append(r5);
        r2.append(r12);
        r5 = ", remainingSamplesInChunk ";
        r2.append(r5);
        r2.append(r0);
        r5 = ", remainingTimestampDeltaChanges ";
        r2.append(r5);
        r5 = r24;
        r2.append(r5);
        r24 = r0;
        r0 = ", remainingSamplesAtTimestampOffset ";
        r2.append(r0);
        r2.append(r4);
        if (r22 != 0) goto L_0x027a;
    L_0x0277:
        r0 = ", ctts invalid";
        goto L_0x027c;
    L_0x027a:
        r0 = "";
    L_0x027c:
        r2.append(r0);
        r0 = r2.toString();
        com.google.android.exoplayer2.util.Log.m10w(r1, r0);
    L_0x0286:
        r22 = r4;
        r27 = r9;
        r26 = r10;
        r24 = r11;
        r0 = r39;
        r10 = r40;
        r9 = r3;
        r70 = r18;
        r18 = r5;
        r5 = r7;
        r7 = r70;
        r71 = r12;
        r12 = r8;
        r8 = r20;
        r20 = r71;
        goto L_0x02f9;
    L_0x02a2:
        r39 = r0;
        r31 = r2;
        r36 = r5;
        r32 = r6;
        r33 = r7;
        r34 = r8;
        r35 = r12;
        r38 = r14;
        r37 = r15;
        r15 = r1;
        r1 = r0.length;
        r1 = new long[r1];
        r2 = r0.length;
        r2 = new int[r2];
    L_0x02bd:
        r4 = r0.moveNext();
        if (r4 == 0) goto L_0x02d0;
    L_0x02c3:
        r4 = r0.index;
        r5 = r0.offset;
        r1[r4] = r5;
        r4 = r0.index;
        r5 = r0.numSamples;
        r2[r4] = r5;
        goto L_0x02bd;
    L_0x02d0:
        r4 = r9.format;
        r4 = r4.pcmEncoding;
        r5 = r9.format;
        r5 = r5.channelCount;
        r4 = com.google.android.exoplayer2.util.Util.getPcmFrameSize(r4, r5);
        r5 = (long) r3;
        r5 = com.google.android.exoplayer2.extractor.mp4.FixedSampleSizeRechunker.rechunk(r4, r1, r2, r5);
        r6 = r5.offsets;
        r7 = r5.sizes;
        r8 = r5.maximumSize;
        r10 = r5.timestamps;
        r11 = r5.flags;
        r1 = r5.duration;
        r12 = r3;
        r5 = r11;
        r31 = r24;
        r14 = r38;
        r24 = r8;
        r8 = r7;
        r7 = r6;
        r6 = r10;
        r10 = r1;
    L_0x02f9:
        r41 = 1000000; // 0xf4240 float:1.401298E-39 double:4.940656E-318;
        r1 = r9.timescale;
        r39 = r10;
        r43 = r1;
        r46 = com.google.android.exoplayer2.util.Util.scaleLargeTimestamp(r39, r41, r43);
        r1 = r9.editListDurations;
        if (r1 == 0) goto L_0x0624;
    L_0x030a:
        r1 = r74.hasGaplessInfo();
        if (r1 == 0) goto L_0x0329;
    L_0x0310:
        r42 = r0;
        r57 = r7;
        r38 = r10;
        r43 = r12;
        r40 = r13;
        r41 = r14;
        r60 = r15;
        r13 = r5;
        r12 = r6;
        r15 = r8;
        r70 = r34;
        r34 = r33;
        r33 = r70;
        goto L_0x063b;
    L_0x0329:
        r1 = r9.editListDurations;
        r1 = r1.length;
        r48 = 0;
        r4 = 1;
        if (r1 != r4) goto L_0x041b;
    L_0x0331:
        r1 = r9.type;
        if (r1 != r4) goto L_0x041b;
    L_0x0335:
        r1 = r6.length;
        r4 = 2;
        if (r1 < r4) goto L_0x041b;
    L_0x0339:
        r1 = r9.editListMediaTimes;
        r4 = 0;
        r50 = r1[r4];
        r1 = r9.editListDurations;
        r38 = r1[r4];
        r2 = r9.timescale;
        r4 = r0;
        r0 = r9.movieTimescale;
        r40 = r2;
        r42 = r0;
        r0 = com.google.android.exoplayer2.util.Util.scaleLargeTimestamp(r38, r40, r42);
        r54 = r50 + r0;
        r39 = r6;
        r40 = r10;
        r42 = r50;
        r44 = r54;
        r0 = canApplyEditWithGaplessInfo(r39, r40, r42, r44);
        if (r0 == 0) goto L_0x0409;
    L_0x035f:
        r44 = r10 - r54;
        r0 = 0;
        r1 = r6[r0];
        r38 = r50 - r1;
        r0 = r9.format;
        r0 = r0.sampleRate;
        r0 = (long) r0;
        r2 = r9.timescale;
        r40 = r0;
        r42 = r2;
        r2 = com.google.android.exoplayer2.util.Util.scaleLargeTimestamp(r38, r40, r42);
        r0 = r9.format;
        r0 = r0.sampleRate;
        r0 = (long) r0;
        r57 = r4;
        r56 = r5;
        r4 = r9.timescale;
        r38 = r44;
        r40 = r0;
        r42 = r4;
        r4 = com.google.android.exoplayer2.util.Util.scaleLargeTimestamp(r38, r40, r42);
        r0 = (r2 > r48 ? 1 : (r2 == r48 ? 0 : -1));
        if (r0 != 0) goto L_0x03a4;
    L_0x038e:
        r0 = (r4 > r48 ? 1 : (r4 == r48 ? 0 : -1));
        if (r0 == 0) goto L_0x0393;
    L_0x0392:
        goto L_0x03a4;
    L_0x0393:
        r58 = r8;
        r43 = r12;
        r42 = r57;
        r12 = r6;
        r57 = r7;
        r70 = r34;
        r34 = r33;
        r33 = r70;
        goto L_0x042c;
    L_0x03a4:
        r0 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        r38 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1));
        if (r38 > 0) goto L_0x03f5;
    L_0x03ab:
        r38 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1));
        if (r38 > 0) goto L_0x03f5;
    L_0x03af:
        r0 = (int) r2;
        r1 = r74;
        r1.encoderDelay = r0;
        r0 = (int) r4;
        r1.encoderPadding = r0;
        r0 = r9.timescale;
        r38 = r2;
        r2 = 1000000; // 0xf4240 float:1.401298E-39 double:4.940656E-318;
        com.google.android.exoplayer2.util.Util.scaleLargeTimestampsInPlace(r6, r2, r0);
        r0 = r9.editListDurations;
        r1 = 0;
        r58 = r0[r1];
        r60 = 1000000; // 0xf4240 float:1.401298E-39 double:4.940656E-318;
        r0 = r9.movieTimescale;
        r62 = r0;
        r40 = com.google.android.exoplayer2.util.Util.scaleLargeTimestamp(r58, r60, r62);
        r16 = new com.google.android.exoplayer2.extractor.mp4.TrackSampleTable;
        r42 = r57;
        r0 = r16;
        r1 = r72;
        r2 = r7;
        r3 = r8;
        r52 = r4;
        r4 = r24;
        r5 = r6;
        r43 = r12;
        r12 = r6;
        r6 = r56;
        r57 = r7;
        r58 = r8;
        r70 = r34;
        r34 = r33;
        r33 = r70;
        r7 = r40;
        r0.<init>(r1, r2, r3, r4, r5, r6, r7);
        return r16;
    L_0x03f5:
        r38 = r2;
        r52 = r4;
        r58 = r8;
        r43 = r12;
        r42 = r57;
        r12 = r6;
        r57 = r7;
        r70 = r34;
        r34 = r33;
        r33 = r70;
        goto L_0x042c;
    L_0x0409:
        r42 = r4;
        r56 = r5;
        r57 = r7;
        r58 = r8;
        r43 = r12;
        r12 = r6;
        r70 = r34;
        r34 = r33;
        r33 = r70;
        goto L_0x042c;
    L_0x041b:
        r42 = r0;
        r56 = r5;
        r57 = r7;
        r58 = r8;
        r43 = r12;
        r12 = r6;
        r70 = r34;
        r34 = r33;
        r33 = r70;
    L_0x042c:
        r0 = r9.editListDurations;
        r0 = r0.length;
        r1 = 1;
        if (r0 != r1) goto L_0x0475;
    L_0x0432:
        r0 = r9.editListDurations;
        r1 = 0;
        r2 = r0[r1];
        r0 = (r2 > r48 ? 1 : (r2 == r48 ? 0 : -1));
        if (r0 != 0) goto L_0x0475;
    L_0x043b:
        r0 = r9.editListMediaTimes;
        r38 = r0[r1];
        r0 = 0;
    L_0x0440:
        r1 = r12.length;
        if (r0 >= r1) goto L_0x0455;
    L_0x0443:
        r1 = r12[r0];
        r3 = r1 - r38;
        r5 = 1000000; // 0xf4240 float:1.401298E-39 double:4.940656E-318;
        r7 = r9.timescale;
        r1 = com.google.android.exoplayer2.util.Util.scaleLargeTimestamp(r3, r5, r7);
        r12[r0] = r1;
        r0 = r0 + 1;
        goto L_0x0440;
    L_0x0455:
        r1 = r10 - r38;
        r3 = 1000000; // 0xf4240 float:1.401298E-39 double:4.940656E-318;
        r5 = r9.timescale;
        r40 = com.google.android.exoplayer2.util.Util.scaleLargeTimestamp(r1, r3, r5);
        r16 = new com.google.android.exoplayer2.extractor.mp4.TrackSampleTable;
        r0 = r16;
        r1 = r72;
        r2 = r57;
        r3 = r58;
        r4 = r24;
        r5 = r12;
        r6 = r56;
        r7 = r40;
        r0.<init>(r1, r2, r3, r4, r5, r6, r7);
        return r16;
        r0 = r9.type;
        r1 = 1;
        if (r0 != r1) goto L_0x047d;
    L_0x047b:
        r0 = 1;
        goto L_0x047e;
    L_0x047d:
        r0 = 0;
    L_0x047e:
        r7 = r0;
        r0 = 0;
        r1 = 0;
        r2 = 0;
        r3 = r9.editListDurations;
        r3 = r3.length;
        r8 = new int[r3];
        r3 = r9.editListDurations;
        r3 = r3.length;
        r6 = new int[r3];
        r3 = 0;
        r5 = r0;
        r4 = r1;
    L_0x048f:
        r0 = r9.editListDurations;
        r0 = r0.length;
        if (r3 >= r0) goto L_0x0508;
    L_0x0494:
        r0 = r9.editListMediaTimes;
        r38 = r10;
        r10 = r0[r3];
        r0 = -1;
        r40 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1));
        if (r40 == 0) goto L_0x04f5;
    L_0x04a0:
        r0 = r9.editListDurations;
        r48 = r0[r3];
        r0 = r9.timescale;
        r40 = r13;
        r41 = r14;
        r13 = r9.movieTimescale;
        r50 = r0;
        r52 = r13;
        r0 = com.google.android.exoplayer2.util.Util.scaleLargeTimestamp(r48, r50, r52);
        r13 = 1;
        r14 = com.google.android.exoplayer2.util.Util.binarySearchCeil(r12, r10, r13, r13);
        r8[r3] = r14;
        r13 = r10 + r0;
        r44 = r0;
        r0 = 0;
        r1 = com.google.android.exoplayer2.util.Util.binarySearchCeil(r12, r13, r7, r0);
        r6[r3] = r1;
    L_0x04c6:
        r1 = r8[r3];
        r13 = r6[r3];
        if (r1 >= r13) goto L_0x04de;
    L_0x04cc:
        r1 = r8[r3];
        r13 = r56;
        r1 = r13[r1];
        r14 = 1;
        r1 = r1 & r14;
        if (r1 != 0) goto L_0x04e1;
    L_0x04d6:
        r1 = r8[r3];
        r1 = r1 + r14;
        r8[r3] = r1;
        r56 = r13;
        goto L_0x04c6;
    L_0x04de:
        r13 = r56;
        r14 = 1;
    L_0x04e1:
        r1 = r6[r3];
        r16 = r8[r3];
        r1 = r1 - r16;
        r5 = r5 + r1;
        r1 = r8[r3];
        if (r4 == r1) goto L_0x04ee;
    L_0x04ec:
        r1 = 1;
        goto L_0x04ef;
    L_0x04ee:
        r1 = 0;
    L_0x04ef:
        r1 = r1 | r2;
        r2 = r6[r3];
        r4 = r2;
        r2 = r1;
        goto L_0x04fd;
    L_0x04f5:
        r40 = r13;
        r41 = r14;
        r13 = r56;
        r0 = 0;
        r14 = 1;
    L_0x04fd:
        r3 = r3 + 1;
        r56 = r13;
        r10 = r38;
        r13 = r40;
        r14 = r41;
        goto L_0x048f;
    L_0x0508:
        r38 = r10;
        r40 = r13;
        r41 = r14;
        r13 = r56;
        r0 = 0;
        r14 = 1;
        r10 = r41;
        if (r5 == r10) goto L_0x0517;
    L_0x0516:
        goto L_0x0518;
    L_0x0517:
        r14 = 0;
    L_0x0518:
        r11 = r2 | r14;
        if (r11 == 0) goto L_0x051f;
    L_0x051c:
        r1 = new long[r5];
        goto L_0x0521;
    L_0x051f:
        r1 = r57;
    L_0x0521:
        r14 = r1;
        if (r11 == 0) goto L_0x0527;
    L_0x0524:
        r1 = new int[r5];
        goto L_0x0529;
    L_0x0527:
        r1 = r58;
    L_0x0529:
        r3 = r1;
        if (r11 == 0) goto L_0x052d;
    L_0x052c:
        goto L_0x052f;
    L_0x052d:
        r0 = r24;
    L_0x052f:
        if (r11 == 0) goto L_0x0534;
    L_0x0531:
        r1 = new int[r5];
        goto L_0x0535;
    L_0x0534:
        r1 = r13;
    L_0x0535:
        r2 = r1;
        r1 = new long[r5];
        r44 = 0;
        r16 = 0;
        r21 = 0;
        r41 = r10;
        r10 = r16;
        r16 = r0;
        r0 = r21;
    L_0x0546:
        r21 = r4;
        r4 = r9.editListDurations;
        r4 = r4.length;
        if (r0 >= r4) goto L_0x05e8;
    L_0x054d:
        r4 = r9.editListMediaTimes;
        r54 = r4[r0];
        r4 = r8[r0];
        r56 = r5;
        r5 = r6[r0];
        if (r11 == 0) goto L_0x056d;
    L_0x0559:
        r59 = r6;
        r6 = r5 - r4;
        r60 = r15;
        r15 = r57;
        java.lang.System.arraycopy(r15, r4, r14, r10, r6);
        r15 = r58;
        java.lang.System.arraycopy(r15, r4, r3, r10, r6);
        java.lang.System.arraycopy(r13, r4, r2, r10, r6);
        goto L_0x0573;
    L_0x056d:
        r59 = r6;
        r60 = r15;
        r15 = r58;
    L_0x0573:
        r6 = r4;
        r70 = r16;
        r16 = r10;
        r10 = r70;
    L_0x057a:
        if (r6 >= r5) goto L_0x05c2;
    L_0x057c:
        r50 = 1000000; // 0xf4240 float:1.401298E-39 double:4.940656E-318;
        r58 = r4;
        r61 = r5;
        r4 = r9.movieTimescale;
        r48 = r44;
        r52 = r4;
        r4 = com.google.android.exoplayer2.util.Util.scaleLargeTimestamp(r48, r50, r52);
        r48 = r12[r6];
        r62 = r48 - r54;
        r64 = 1000000; // 0xf4240 float:1.401298E-39 double:4.940656E-318;
        r68 = r7;
        r69 = r8;
        r7 = r9.timescale;
        r66 = r7;
        r7 = com.google.android.exoplayer2.util.Util.scaleLargeTimestamp(r62, r64, r66);
        r48 = r4 + r7;
        r1[r16] = r48;
        if (r11 == 0) goto L_0x05b0;
    L_0x05a6:
        r62 = r1;
        r1 = r3[r16];
        if (r1 <= r10) goto L_0x05b2;
    L_0x05ac:
        r1 = r15[r6];
        r10 = r1;
        goto L_0x05b2;
    L_0x05b0:
        r62 = r1;
        r16 = r16 + 1;
        r6 = r6 + 1;
        r4 = r58;
        r5 = r61;
        r1 = r62;
        r7 = r68;
        r8 = r69;
        goto L_0x057a;
    L_0x05c2:
        r62 = r1;
        r58 = r4;
        r61 = r5;
        r68 = r7;
        r69 = r8;
        r1 = r9.editListDurations;
        r4 = r1[r0];
        r44 = r44 + r4;
        r0 = r0 + 1;
        r58 = r15;
        r4 = r21;
        r5 = r56;
        r6 = r59;
        r15 = r60;
        r1 = r62;
        r70 = r16;
        r16 = r10;
        r10 = r70;
        goto L_0x0546;
    L_0x05e8:
        r62 = r1;
        r56 = r5;
        r59 = r6;
        r68 = r7;
        r69 = r8;
        r60 = r15;
        r15 = r58;
        r50 = 1000000; // 0xf4240 float:1.401298E-39 double:4.940656E-318;
        r0 = r9.movieTimescale;
        r48 = r44;
        r52 = r0;
        r48 = com.google.android.exoplayer2.util.Util.scaleLargeTimestamp(r48, r50, r52);
        r50 = new com.google.android.exoplayer2.extractor.mp4.TrackSampleTable;
        r0 = r50;
        r51 = r62;
        r1 = r72;
        r52 = r2;
        r2 = r14;
        r53 = r3;
        r4 = r16;
        r54 = r56;
        r5 = r51;
        r55 = r59;
        r6 = r52;
        r56 = r68;
        r58 = r69;
        r7 = r48;
        r0.<init>(r1, r2, r3, r4, r5, r6, r7);
        return r50;
    L_0x0624:
        r42 = r0;
        r57 = r7;
        r38 = r10;
        r43 = r12;
        r40 = r13;
        r41 = r14;
        r60 = r15;
        r13 = r5;
        r12 = r6;
        r15 = r8;
        r70 = r34;
        r34 = r33;
        r33 = r70;
    L_0x063b:
        r0 = r9.timescale;
        r2 = 1000000; // 0xf4240 float:1.401298E-39 double:4.940656E-318;
        com.google.android.exoplayer2.util.Util.scaleLargeTimestampsInPlace(r12, r2, r0);
        r10 = new com.google.android.exoplayer2.extractor.mp4.TrackSampleTable;
        r0 = r10;
        r1 = r72;
        r2 = r57;
        r3 = r15;
        r4 = r24;
        r5 = r12;
        r6 = r13;
        r7 = r46;
        r0.<init>(r1, r2, r3, r4, r5, r6, r7);
        return r10;
    L_0x0655:
        r35 = r12;
        r1 = new com.google.android.exoplayer2.ParserException;
        r2 = "Track has no sample table size information";
        r1.<init>(r2);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.mp4.AtomParsers.parseStbl(com.google.android.exoplayer2.extractor.mp4.Track, com.google.android.exoplayer2.extractor.mp4.Atom$ContainerAtom, com.google.android.exoplayer2.extractor.GaplessInfoHolder):com.google.android.exoplayer2.extractor.mp4.TrackSampleTable");
    }

    public static Track parseTrak(ContainerAtom trak, LeafAtom mvhd, long duration, DrmInitData drmInitData, boolean ignoreEditLists, boolean isQuickTime) throws ParserException {
        ContainerAtom containerAtom = trak;
        ContainerAtom mdia = containerAtom.getContainerAtomOfType(Atom.TYPE_mdia);
        int trackType = parseHdlr(mdia.getLeafAtomOfType(Atom.TYPE_hdlr).data);
        if (trackType == -1) {
            return null;
        }
        long duration2;
        long[] editListDurations;
        long[] editListMediaTimes;
        Track track;
        TkhdData tkhdData = parseTkhd(containerAtom.getLeafAtomOfType(Atom.TYPE_tkhd).data);
        if (duration == C0555C.TIME_UNSET) {
            duration2 = tkhdData.duration;
        } else {
            duration2 = duration;
        }
        long movieTimescale = parseMvhd(mvhd.data);
        long durationUs = duration2 == C0555C.TIME_UNSET ? C0555C.TIME_UNSET : Util.scaleLargeTimestamp(duration2, 1000000, movieTimescale);
        ContainerAtom stbl = mdia.getContainerAtomOfType(Atom.TYPE_minf).getContainerAtomOfType(Atom.TYPE_stbl);
        Pair<Long, String> mdhdData = parseMdhd(mdia.getLeafAtomOfType(Atom.TYPE_mdhd).data);
        StsdData stsdData = parseStsd(stbl.getLeafAtomOfType(Atom.TYPE_stsd).data, tkhdData.id, tkhdData.rotationDegrees, (String) mdhdData.second, drmInitData, isQuickTime);
        if (ignoreEditLists) {
            editListDurations = null;
            editListMediaTimes = null;
        } else {
            Pair<long[], long[]> edtsData = parseEdts(containerAtom.getContainerAtomOfType(Atom.TYPE_edts));
            editListDurations = (long[]) edtsData.first;
            editListMediaTimes = (long[]) edtsData.second;
        }
        if (stsdData.format == null) {
            track = null;
            StsdData stsdData2 = stsdData;
            Pair<Long, String> pair = mdhdData;
            ContainerAtom containerAtom2 = stbl;
        } else {
            int access$100 = tkhdData.id;
            long longValue = ((Long) mdhdData.first).longValue();
            Format format = stsdData.format;
            int i = stsdData.requiredSampleTransformation;
            Track track2 = new Track(access$100, trackType, longValue, movieTimescale, durationUs, format, i, stsdData.trackEncryptionBoxes, stsdData.nalUnitLengthFieldLength, editListDurations, editListMediaTimes);
        }
        return track;
    }

    public static Metadata parseUdta(LeafAtom udtaAtom, boolean isQuickTime) {
        if (isQuickTime) {
            return null;
        }
        ParsableByteArray udtaData = udtaAtom.data;
        udtaData.setPosition(8);
        while (udtaData.bytesLeft() >= 8) {
            int atomPosition = udtaData.getPosition();
            int atomSize = udtaData.readInt();
            if (udtaData.readInt() == Atom.TYPE_meta) {
                udtaData.setPosition(atomPosition);
                return parseMetaAtom(udtaData, atomPosition + atomSize);
            }
            udtaData.skipBytes(atomSize - 8);
        }
        return null;
    }

    private static Metadata parseMetaAtom(ParsableByteArray meta, int limit) {
        meta.skipBytes(12);
        while (meta.getPosition() < limit) {
            int atomPosition = meta.getPosition();
            int atomSize = meta.readInt();
            if (meta.readInt() == Atom.TYPE_ilst) {
                meta.setPosition(atomPosition);
                return parseIlst(meta, atomPosition + atomSize);
            }
            meta.skipBytes(atomSize - 8);
        }
        return null;
    }

    private static Metadata parseIlst(ParsableByteArray ilst, int limit) {
        ilst.skipBytes(8);
        List entries = new ArrayList();
        while (ilst.getPosition() < limit) {
            Entry entry = MetadataUtil.parseIlstElement(ilst);
            if (entry != null) {
                entries.add(entry);
            }
        }
        return entries.isEmpty() ? null : new Metadata(entries);
    }

    private static long parseMvhd(ParsableByteArray mvhd) {
        int i = 8;
        mvhd.setPosition(8);
        if (Atom.parseFullAtomVersion(mvhd.readInt()) != 0) {
            i = 16;
        }
        mvhd.skipBytes(i);
        return mvhd.readUnsignedInt();
    }

    private static TkhdData parseTkhd(ParsableByteArray tkhd) {
        long duration;
        int rotationDegrees;
        int durationByteCount = 8;
        tkhd.setPosition(8);
        int version = Atom.parseFullAtomVersion(tkhd.readInt());
        tkhd.skipBytes(version == 0 ? 8 : 16);
        int trackId = tkhd.readInt();
        tkhd.skipBytes(4);
        boolean durationUnknown = true;
        int durationPosition = tkhd.getPosition();
        if (version == 0) {
            durationByteCount = 4;
        }
        for (int i = 0; i < durationByteCount; i++) {
            if (tkhd.data[durationPosition + i] != (byte) -1) {
                durationUnknown = false;
                break;
            }
        }
        if (durationUnknown) {
            tkhd.skipBytes(durationByteCount);
            duration = C0555C.TIME_UNSET;
        } else {
            duration = version == 0 ? tkhd.readUnsignedInt() : tkhd.readUnsignedLongToLong();
            if (duration == 0) {
                duration = C0555C.TIME_UNSET;
            }
        }
        tkhd.skipBytes(16);
        int a00 = tkhd.readInt();
        int a01 = tkhd.readInt();
        tkhd.skipBytes(4);
        int a10 = tkhd.readInt();
        int a11 = tkhd.readInt();
        if (a00 == 0 && a01 == 65536 && a10 == (-65536) && a11 == 0) {
            rotationDegrees = 90;
        } else if (a00 == 0 && a01 == (-65536) && a10 == 65536 && a11 == 0) {
            rotationDegrees = 270;
        } else if (a00 == (-65536) && a01 == 0 && a10 == 0 && a11 == (-65536)) {
            rotationDegrees = 180;
        } else {
            rotationDegrees = 0;
        }
        return new TkhdData(trackId, duration, rotationDegrees);
    }

    private static int parseHdlr(ParsableByteArray hdlr) {
        hdlr.setPosition(16);
        int trackType = hdlr.readInt();
        if (trackType == TYPE_soun) {
            return 1;
        }
        if (trackType == TYPE_vide) {
            return 2;
        }
        if (!(trackType == TYPE_text || trackType == TYPE_sbtl || trackType == TYPE_subt)) {
            if (trackType != TYPE_clcp) {
                if (trackType == TYPE_meta) {
                    return 4;
                }
                return -1;
            }
        }
        return 3;
    }

    private static Pair<Long, String> parseMdhd(ParsableByteArray mdhd) {
        int i = 8;
        mdhd.setPosition(8);
        int version = Atom.parseFullAtomVersion(mdhd.readInt());
        mdhd.skipBytes(version == 0 ? 8 : 16);
        long timescale = mdhd.readUnsignedInt();
        if (version == 0) {
            i = 4;
        }
        mdhd.skipBytes(i);
        i = mdhd.readUnsignedShort();
        String language = new StringBuilder();
        language.append("");
        language.append((char) (((i >> 10) & 31) + 96));
        language.append((char) (((i >> 5) & 31) + 96));
        language.append((char) ((i & 31) + 96));
        return Pair.create(Long.valueOf(timescale), language.toString());
    }

    private static StsdData parseStsd(ParsableByteArray stsd, int trackId, int rotationDegrees, String language, DrmInitData drmInitData, boolean isQuickTime) throws ParserException {
        ParsableByteArray parsableByteArray = stsd;
        parsableByteArray.setPosition(12);
        int numberOfEntries = stsd.readInt();
        StsdData out = new StsdData(numberOfEntries);
        for (int i = 0; i < numberOfEntries; i++) {
            int childAtomType;
            int childStartPosition = stsd.getPosition();
            int childAtomSize = stsd.readInt();
            Assertions.checkArgument(childAtomSize > 0, "childAtomSize should be positive");
            int childAtomType2 = stsd.readInt();
            if (childAtomType2 == Atom.TYPE_avc1 || childAtomType2 == Atom.TYPE_avc3 || childAtomType2 == Atom.TYPE_encv || childAtomType2 == Atom.TYPE_mp4v || childAtomType2 == Atom.TYPE_hvc1 || childAtomType2 == Atom.TYPE_hev1 || childAtomType2 == Atom.TYPE_s263 || childAtomType2 == Atom.TYPE_vp08) {
                childAtomType = childAtomType2;
            } else if (childAtomType2 == Atom.TYPE_vp09) {
                childAtomType = childAtomType2;
            } else {
                if (!(childAtomType2 == Atom.TYPE_mp4a || childAtomType2 == Atom.TYPE_enca || childAtomType2 == Atom.TYPE_ac_3 || childAtomType2 == Atom.TYPE_ec_3 || childAtomType2 == Atom.TYPE_dtsc || childAtomType2 == Atom.TYPE_dtse || childAtomType2 == Atom.TYPE_dtsh || childAtomType2 == Atom.TYPE_dtsl || childAtomType2 == Atom.TYPE_samr || childAtomType2 == Atom.TYPE_sawb || childAtomType2 == Atom.TYPE_lpcm || childAtomType2 == Atom.TYPE_sowt || childAtomType2 == Atom.TYPE__mp3 || childAtomType2 == Atom.TYPE_alac || childAtomType2 == Atom.TYPE_alaw || childAtomType2 == Atom.TYPE_ulaw || childAtomType2 == Atom.TYPE_Opus)) {
                    if (childAtomType2 != Atom.TYPE_fLaC) {
                        if (!(childAtomType2 == Atom.TYPE_TTML || childAtomType2 == Atom.TYPE_tx3g || childAtomType2 == Atom.TYPE_wvtt || childAtomType2 == Atom.TYPE_stpp)) {
                            if (childAtomType2 != Atom.TYPE_c608) {
                                if (childAtomType2 == Atom.TYPE_camm) {
                                    out.format = Format.createSampleFormat(Integer.toString(trackId), MimeTypes.APPLICATION_CAMERA_MOTION, null, -1, null);
                                    childAtomType = childAtomType2;
                                } else {
                                    childAtomType = childAtomType2;
                                }
                                parsableByteArray.setPosition(childStartPosition + childAtomSize);
                            }
                        }
                        parseTextSampleEntry(stsd, childAtomType2, childStartPosition, childAtomSize, trackId, language, out);
                        childAtomType = childAtomType2;
                        parsableByteArray.setPosition(childStartPosition + childAtomSize);
                    }
                }
                parseAudioSampleEntry(stsd, childAtomType2, childStartPosition, childAtomSize, trackId, language, isQuickTime, drmInitData, out, i);
                parsableByteArray.setPosition(childStartPosition + childAtomSize);
            }
            parseVideoSampleEntry(stsd, childAtomType, childStartPosition, childAtomSize, trackId, rotationDegrees, drmInitData, out, i);
            parsableByteArray.setPosition(childStartPosition + childAtomSize);
        }
        return out;
    }

    private static void parseTextSampleEntry(ParsableByteArray parent, int atomType, int position, int atomSize, int trackId, String language, StsdData out) throws ParserException {
        String mimeType;
        ParsableByteArray parsableByteArray = parent;
        int i = atomType;
        StsdData stsdData = out;
        parsableByteArray.setPosition((position + 8) + 8);
        List<byte[]> initializationData = null;
        long subsampleOffsetUs = Long.MAX_VALUE;
        if (i == Atom.TYPE_TTML) {
            mimeType = MimeTypes.APPLICATION_TTML;
        } else if (i == Atom.TYPE_tx3g) {
            mimeType = MimeTypes.APPLICATION_TX3G;
            int sampleDescriptionLength = (atomSize - 8) - 8;
            byte[] sampleDescriptionData = new byte[sampleDescriptionLength];
            parsableByteArray.readBytes(sampleDescriptionData, 0, sampleDescriptionLength);
            initializationData = Collections.singletonList(sampleDescriptionData);
        } else if (i == Atom.TYPE_wvtt) {
            mimeType = MimeTypes.APPLICATION_MP4VTT;
        } else if (i == Atom.TYPE_stpp) {
            mimeType = MimeTypes.APPLICATION_TTML;
            subsampleOffsetUs = 0;
        } else if (i == Atom.TYPE_c608) {
            mimeType = MimeTypes.APPLICATION_MP4CEA608;
            stsdData.requiredSampleTransformation = 1;
        } else {
            throw new IllegalStateException();
        }
        stsdData.format = Format.createTextSampleFormat(Integer.toString(trackId), mimeType, null, -1, 0, language, -1, null, subsampleOffsetUs, initializationData);
    }

    private static void parseVideoSampleEntry(ParsableByteArray parent, int atomType, int position, int size, int trackId, int rotationDegrees, DrmInitData drmInitData, StsdData out, int entryIndex) throws ParserException {
        int atomType2;
        ParsableByteArray parsableByteArray = parent;
        int i = position;
        int i2 = size;
        DrmInitData drmInitData2 = drmInitData;
        StsdData stsdData = out;
        parsableByteArray.setPosition((i + 8) + 8);
        parsableByteArray.skipBytes(16);
        int width = parent.readUnsignedShort();
        int height = parent.readUnsignedShort();
        parsableByteArray.skipBytes(50);
        int childPosition = parent.getPosition();
        int atomType3 = atomType;
        if (atomType3 == Atom.TYPE_encv) {
            Pair<Integer, TrackEncryptionBox> sampleEntryEncryptionData = parseSampleEntryEncryptionData(parsableByteArray, i, i2);
            if (sampleEntryEncryptionData != null) {
                DrmInitData drmInitData3;
                atomType3 = ((Integer) sampleEntryEncryptionData.first).intValue();
                if (drmInitData2 == null) {
                    drmInitData3 = null;
                } else {
                    drmInitData3 = drmInitData2.copyWithSchemeType(((TrackEncryptionBox) sampleEntryEncryptionData.second).schemeType);
                }
                drmInitData2 = drmInitData3;
                stsdData.trackEncryptionBoxes[entryIndex] = (TrackEncryptionBox) sampleEntryEncryptionData.second;
            }
            parsableByteArray.setPosition(childPosition);
            atomType2 = atomType3;
        } else {
            atomType2 = atomType3;
        }
        boolean pixelWidthHeightRatioFromPasp = false;
        float pixelWidthHeightRatio = 1.0f;
        int childPosition2 = childPosition;
        List<byte[]> initializationData = null;
        String mimeType = null;
        byte[] projectionData = null;
        int stereoMode = -1;
        while (childPosition2 - i < i2) {
            parsableByteArray.setPosition(childPosition2);
            int childStartPosition = parent.getPosition();
            int childAtomSize = parent.readInt();
            if (childAtomSize != 0 || parent.getPosition() - i != i2) {
                boolean z = true;
                Assertions.checkArgument(childAtomSize > 0, "childAtomSize should be positive");
                atomType3 = parent.readInt();
                List<byte[]> initializationData2;
                if (atomType3 == Atom.TYPE_avcC) {
                    if (mimeType != null) {
                        z = false;
                    }
                    Assertions.checkState(z);
                    mimeType = MimeTypes.VIDEO_H264;
                    parsableByteArray.setPosition(childStartPosition + 8);
                    AvcConfig avcConfig = AvcConfig.parse(parent);
                    initializationData2 = avcConfig.initializationData;
                    stsdData.nalUnitLengthFieldLength = avcConfig.nalUnitLengthFieldLength;
                    if (!pixelWidthHeightRatioFromPasp) {
                        pixelWidthHeightRatio = avcConfig.pixelWidthAspectRatio;
                    }
                    initializationData = initializationData2;
                } else if (atomType3 == Atom.TYPE_hvcC) {
                    if (mimeType != null) {
                        z = false;
                    }
                    Assertions.checkState(z);
                    mimeType = MimeTypes.VIDEO_H265;
                    parsableByteArray.setPosition(childStartPosition + 8);
                    HevcConfig hevcConfig = HevcConfig.parse(parent);
                    initializationData2 = hevcConfig.initializationData;
                    stsdData.nalUnitLengthFieldLength = hevcConfig.nalUnitLengthFieldLength;
                    initializationData = initializationData2;
                } else if (atomType3 == Atom.TYPE_vpcC) {
                    if (mimeType != null) {
                        z = false;
                    }
                    Assertions.checkState(z);
                    mimeType = atomType2 == Atom.TYPE_vp08 ? MimeTypes.VIDEO_VP8 : MimeTypes.VIDEO_VP9;
                } else if (atomType3 == Atom.TYPE_d263) {
                    if (mimeType != null) {
                        z = false;
                    }
                    Assertions.checkState(z);
                    mimeType = MimeTypes.VIDEO_H263;
                } else if (atomType3 == Atom.TYPE_esds) {
                    if (mimeType != null) {
                        z = false;
                    }
                    Assertions.checkState(z);
                    Pair<String, byte[]> mimeTypeAndInitializationData = parseEsdsFromParent(parsableByteArray, childStartPosition);
                    mimeType = mimeTypeAndInitializationData.first;
                    initializationData = Collections.singletonList(mimeTypeAndInitializationData.second);
                } else if (atomType3 == Atom.TYPE_pasp) {
                    pixelWidthHeightRatio = parsePaspFromParent(parsableByteArray, childStartPosition);
                    pixelWidthHeightRatioFromPasp = true;
                } else if (atomType3 == Atom.TYPE_sv3d) {
                    projectionData = parseProjFromParent(parsableByteArray, childStartPosition, childAtomSize);
                } else if (atomType3 == Atom.TYPE_st3d) {
                    childPosition = parent.readUnsignedByte();
                    parsableByteArray.skipBytes(3);
                    if (childPosition == 0) {
                        switch (parent.readUnsignedByte()) {
                            case 0:
                                stereoMode = 0;
                                break;
                            case 1:
                                stereoMode = 1;
                                break;
                            case 2:
                                stereoMode = 2;
                                break;
                            case 3:
                                stereoMode = 3;
                                break;
                            default:
                                break;
                        }
                    }
                }
                childPosition2 += childAtomSize;
            } else if (mimeType == null) {
                stsdData.format = Format.createVideoSampleFormat(Integer.toString(trackId), mimeType, null, -1, -1, width, height, -1.0f, initializationData, rotationDegrees, pixelWidthHeightRatio, projectionData, stereoMode, null, drmInitData2);
            }
        }
        if (mimeType == null) {
            stsdData.format = Format.createVideoSampleFormat(Integer.toString(trackId), mimeType, null, -1, -1, width, height, -1.0f, initializationData, rotationDegrees, pixelWidthHeightRatio, projectionData, stereoMode, null, drmInitData2);
        }
    }

    private static Pair<long[], long[]> parseEdts(ContainerAtom edtsAtom) {
        if (edtsAtom != null) {
            LeafAtom leafAtomOfType = edtsAtom.getLeafAtomOfType(Atom.TYPE_elst);
            LeafAtom elst = leafAtomOfType;
            if (leafAtomOfType != null) {
                ParsableByteArray elstData = elst.data;
                elstData.setPosition(8);
                int version = Atom.parseFullAtomVersion(elstData.readInt());
                int entryCount = elstData.readUnsignedIntToInt();
                long[] editListDurations = new long[entryCount];
                long[] editListMediaTimes = new long[entryCount];
                int i = 0;
                while (i < entryCount) {
                    editListDurations[i] = version == 1 ? elstData.readUnsignedLongToLong() : elstData.readUnsignedInt();
                    editListMediaTimes[i] = version == 1 ? elstData.readLong() : (long) elstData.readInt();
                    if (elstData.readShort() == 1) {
                        elstData.skipBytes(2);
                        i++;
                    } else {
                        throw new IllegalArgumentException("Unsupported media rate.");
                    }
                }
                return Pair.create(editListDurations, editListMediaTimes);
            }
        }
        return Pair.create(null, null);
    }

    private static float parsePaspFromParent(ParsableByteArray parent, int position) {
        parent.setPosition(position + 8);
        return ((float) parent.readUnsignedIntToInt()) / ((float) parent.readUnsignedIntToInt());
    }

    private static void parseAudioSampleEntry(ParsableByteArray parent, int atomType, int position, int size, int trackId, String language, boolean isQuickTime, DrmInitData drmInitData, StsdData out, int entryIndex) throws ParserException {
        int quickTimeSoundDescriptionVersion;
        int quickTimeSoundDescriptionVersion2;
        int channelCount;
        int sampleRate;
        int atomType2;
        Pair<Integer, TrackEncryptionBox> sampleEntryEncryptionData;
        DrmInitData drmInitData2;
        int atomType3;
        String mimeType;
        int channelCount2;
        int sampleRate2;
        byte[] initializationData;
        String mimeType2;
        int childAtomType;
        String mimeType3;
        DrmInitData drmInitData3;
        int atomType4;
        int quickTimeSoundDescriptionVersion3;
        int childAtomSize;
        int childPosition;
        int esdsAtomPosition;
        String mimeType4;
        List list;
        StsdData stsdData;
        ParsableByteArray parsableByteArray = parent;
        int i = position;
        int i2 = size;
        String str = language;
        DrmInitData drmInitData4 = drmInitData;
        StsdData stsdData2 = out;
        parsableByteArray.setPosition((i + 8) + 8);
        if (isQuickTime) {
            quickTimeSoundDescriptionVersion = parent.readUnsignedShort();
            parsableByteArray.skipBytes(6);
            quickTimeSoundDescriptionVersion2 = quickTimeSoundDescriptionVersion;
        } else {
            parsableByteArray.skipBytes(8);
            quickTimeSoundDescriptionVersion2 = 0;
        }
        if (quickTimeSoundDescriptionVersion2 != 0) {
            if (quickTimeSoundDescriptionVersion2 != 1) {
                if (quickTimeSoundDescriptionVersion2 == 2) {
                    parsableByteArray.skipBytes(16);
                    quickTimeSoundDescriptionVersion = (int) Math.round(parent.readDouble());
                    channelCount = parent.readUnsignedIntToInt();
                    parsableByteArray.skipBytes(20);
                    sampleRate = quickTimeSoundDescriptionVersion;
                    quickTimeSoundDescriptionVersion = parent.getPosition();
                    atomType2 = atomType;
                    if (atomType2 != Atom.TYPE_enca) {
                        sampleEntryEncryptionData = parseSampleEntryEncryptionData(parsableByteArray, i, i2);
                        if (sampleEntryEncryptionData != null) {
                            atomType2 = ((Integer) sampleEntryEncryptionData.first).intValue();
                            if (drmInitData4 != null) {
                                drmInitData2 = null;
                            } else {
                                drmInitData2 = drmInitData4.copyWithSchemeType(((TrackEncryptionBox) sampleEntryEncryptionData.second).schemeType);
                            }
                            drmInitData4 = drmInitData2;
                            stsdData2.trackEncryptionBoxes[entryIndex] = (TrackEncryptionBox) sampleEntryEncryptionData.second;
                        }
                        parsableByteArray.setPosition(quickTimeSoundDescriptionVersion);
                        drmInitData2 = drmInitData4;
                        atomType3 = atomType2;
                    } else {
                        drmInitData2 = drmInitData4;
                        atomType3 = atomType2;
                    }
                    mimeType = null;
                    if (atomType3 == Atom.TYPE_ac_3) {
                        mimeType = MimeTypes.AUDIO_AC3;
                    } else if (atomType3 == Atom.TYPE_ec_3) {
                        mimeType = MimeTypes.AUDIO_E_AC3;
                    } else if (atomType3 != Atom.TYPE_dtsc) {
                        mimeType = MimeTypes.AUDIO_DTS;
                    } else {
                        if (atomType3 != Atom.TYPE_dtsh) {
                            if (atomType3 == Atom.TYPE_dtsl) {
                                if (atomType3 == Atom.TYPE_dtse) {
                                    mimeType = MimeTypes.AUDIO_DTS_EXPRESS;
                                } else if (atomType3 == Atom.TYPE_samr) {
                                    mimeType = MimeTypes.AUDIO_AMR_NB;
                                } else if (atomType3 != Atom.TYPE_sawb) {
                                    mimeType = MimeTypes.AUDIO_AMR_WB;
                                } else {
                                    if (atomType3 != Atom.TYPE_lpcm) {
                                        if (atomType3 == Atom.TYPE_sowt) {
                                            if (atomType3 == Atom.TYPE__mp3) {
                                                mimeType = MimeTypes.AUDIO_MPEG;
                                            } else if (atomType3 == Atom.TYPE_alac) {
                                                mimeType = MimeTypes.AUDIO_ALAC;
                                            } else if (atomType3 == Atom.TYPE_alaw) {
                                                mimeType = MimeTypes.AUDIO_ALAW;
                                            } else if (atomType3 == Atom.TYPE_ulaw) {
                                                mimeType = MimeTypes.AUDIO_MLAW;
                                            } else if (atomType3 == Atom.TYPE_Opus) {
                                                mimeType = MimeTypes.AUDIO_OPUS;
                                            } else if (atomType3 == Atom.TYPE_fLaC) {
                                                mimeType = MimeTypes.AUDIO_FLAC;
                                            }
                                        }
                                    }
                                    mimeType = MimeTypes.AUDIO_RAW;
                                }
                            }
                        }
                        mimeType = MimeTypes.AUDIO_DTS_HD;
                    }
                    atomType2 = quickTimeSoundDescriptionVersion;
                    channelCount2 = channelCount;
                    sampleRate2 = sampleRate;
                    initializationData = null;
                    mimeType2 = mimeType;
                    while (atomType2 - i < i2) {
                        parsableByteArray.setPosition(atomType2);
                        sampleRate = parent.readInt();
                        Assertions.checkArgument(sampleRate <= 0, "childAtomSize should be positive");
                        childAtomType = parent.readInt();
                        if (childAtomType != Atom.TYPE_esds) {
                            mimeType3 = mimeType2;
                            drmInitData3 = drmInitData2;
                            atomType4 = atomType3;
                            channelCount = childAtomType;
                            quickTimeSoundDescriptionVersion3 = quickTimeSoundDescriptionVersion2;
                            quickTimeSoundDescriptionVersion2 = atomType2;
                        } else if (isQuickTime || childAtomType != Atom.TYPE_wave) {
                            if (childAtomType == Atom.TYPE_dac3) {
                                parsableByteArray.setPosition(atomType2 + 8);
                                stsdData2.format = Ac3Util.parseAc3AnnexFFormat(parsableByteArray, Integer.toString(trackId), str, drmInitData2);
                                mimeType3 = mimeType2;
                                drmInitData3 = drmInitData2;
                                atomType4 = atomType3;
                                channelCount = childAtomType;
                                quickTimeSoundDescriptionVersion3 = quickTimeSoundDescriptionVersion2;
                                quickTimeSoundDescriptionVersion2 = atomType2;
                            } else if (childAtomType == Atom.TYPE_dec3) {
                                parsableByteArray.setPosition(atomType2 + 8);
                                stsdData2.format = Ac3Util.parseEAc3AnnexFFormat(parsableByteArray, Integer.toString(trackId), str, drmInitData2);
                                mimeType3 = mimeType2;
                                drmInitData3 = drmInitData2;
                                atomType4 = atomType3;
                                channelCount = childAtomType;
                                quickTimeSoundDescriptionVersion3 = quickTimeSoundDescriptionVersion2;
                                quickTimeSoundDescriptionVersion2 = atomType2;
                            } else if (childAtomType != Atom.TYPE_ddts) {
                                childAtomSize = sampleRate;
                                mimeType3 = mimeType2;
                                childPosition = atomType2;
                                drmInitData3 = drmInitData2;
                                atomType4 = atomType3;
                                int childAtomType2 = childAtomType;
                                quickTimeSoundDescriptionVersion3 = quickTimeSoundDescriptionVersion2;
                                stsdData2.format = Format.createAudioSampleFormat(Integer.toString(trackId), mimeType2, null, -1, -1, channelCount2, sampleRate2, null, drmInitData3, 0, language);
                                sampleRate = childAtomSize;
                                quickTimeSoundDescriptionVersion2 = childPosition;
                                channelCount = childAtomType2;
                            } else {
                                childAtomSize = sampleRate;
                                mimeType3 = mimeType2;
                                childPosition = atomType2;
                                drmInitData3 = drmInitData2;
                                atomType4 = atomType3;
                                quickTimeSoundDescriptionVersion3 = quickTimeSoundDescriptionVersion2;
                                channelCount = childAtomType;
                                if (channelCount != Atom.TYPE_alac) {
                                    sampleRate = childAtomSize;
                                    byte[] initializationData2 = new byte[sampleRate];
                                    quickTimeSoundDescriptionVersion2 = childPosition;
                                    parsableByteArray.setPosition(quickTimeSoundDescriptionVersion2);
                                    parsableByteArray.readBytes(initializationData2, 0, sampleRate);
                                    initializationData = initializationData2;
                                    mimeType2 = mimeType3;
                                } else {
                                    sampleRate = childAtomSize;
                                    quickTimeSoundDescriptionVersion2 = childPosition;
                                    if (channelCount == Atom.TYPE_dOps) {
                                        quickTimeSoundDescriptionVersion = sampleRate - 8;
                                        Object obj = opusMagic;
                                        byte[] initializationData3 = new byte[(obj.length + quickTimeSoundDescriptionVersion)];
                                        System.arraycopy(obj, 0, initializationData3, 0, obj.length);
                                        parsableByteArray.setPosition(quickTimeSoundDescriptionVersion2 + 8);
                                        parsableByteArray.readBytes(initializationData3, opusMagic.length, quickTimeSoundDescriptionVersion);
                                        initializationData = initializationData3;
                                        mimeType2 = mimeType3;
                                    } else if (sampleRate == Atom.TYPE_dfLa) {
                                        quickTimeSoundDescriptionVersion = sampleRate - 12;
                                        byte[] initializationData4 = new byte[quickTimeSoundDescriptionVersion];
                                        parsableByteArray.setPosition(quickTimeSoundDescriptionVersion2 + 12);
                                        parsableByteArray.readBytes(initializationData4, 0, quickTimeSoundDescriptionVersion);
                                        initializationData = initializationData4;
                                        mimeType2 = mimeType3;
                                    }
                                }
                                atomType2 = quickTimeSoundDescriptionVersion2 + sampleRate;
                                drmInitData2 = drmInitData3;
                                atomType3 = atomType4;
                                quickTimeSoundDescriptionVersion2 = quickTimeSoundDescriptionVersion3;
                            }
                            mimeType2 = mimeType3;
                            atomType2 = quickTimeSoundDescriptionVersion2 + sampleRate;
                            drmInitData2 = drmInitData3;
                            atomType3 = atomType4;
                            quickTimeSoundDescriptionVersion2 = quickTimeSoundDescriptionVersion3;
                        } else {
                            mimeType3 = mimeType2;
                            drmInitData3 = drmInitData2;
                            atomType4 = atomType3;
                            channelCount = childAtomType;
                            quickTimeSoundDescriptionVersion3 = quickTimeSoundDescriptionVersion2;
                            quickTimeSoundDescriptionVersion2 = atomType2;
                        }
                        if (channelCount != Atom.TYPE_esds) {
                            atomType2 = quickTimeSoundDescriptionVersion2;
                        } else {
                            atomType2 = findEsdsPosition(parsableByteArray, quickTimeSoundDescriptionVersion2, sampleRate);
                        }
                        esdsAtomPosition = atomType2;
                        if (esdsAtomPosition == -1) {
                            Pair<String, byte[]> mimeTypeAndInitializationData = parseEsdsFromParent(parsableByteArray, esdsAtomPosition);
                            mimeType2 = (String) mimeTypeAndInitializationData.first;
                            initializationData = (byte[]) mimeTypeAndInitializationData.second;
                            if (MimeTypes.AUDIO_AAC.equals(mimeType2)) {
                                Pair<Integer, Integer> audioSpecificConfig = CodecSpecificDataUtil.parseAacAudioSpecificConfig(initializationData);
                                sampleRate2 = ((Integer) audioSpecificConfig.first).intValue();
                                channelCount2 = ((Integer) audioSpecificConfig.second).intValue();
                            }
                        } else {
                            mimeType2 = mimeType3;
                        }
                        atomType2 = quickTimeSoundDescriptionVersion2 + sampleRate;
                        drmInitData2 = drmInitData3;
                        atomType3 = atomType4;
                        quickTimeSoundDescriptionVersion2 = quickTimeSoundDescriptionVersion3;
                    }
                    mimeType3 = mimeType2;
                    drmInitData3 = drmInitData2;
                    atomType4 = atomType3;
                    quickTimeSoundDescriptionVersion3 = quickTimeSoundDescriptionVersion2;
                    quickTimeSoundDescriptionVersion2 = atomType2;
                    if (stsdData2.format != null) {
                        mimeType4 = mimeType3;
                        if (mimeType4 == null) {
                            atomType3 = MimeTypes.AUDIO_RAW.equals(mimeType4) ? 2 : -1;
                            mimeType = Integer.toString(trackId);
                            if (initializationData != null) {
                                list = null;
                            } else {
                                list = Collections.singletonList(initializationData);
                            }
                            stsdData2.format = Format.createAudioSampleFormat(mimeType, mimeType4, null, -1, -1, channelCount2, sampleRate2, atomType3, list, drmInitData3, 0, language);
                        } else {
                            int i3 = quickTimeSoundDescriptionVersion2;
                            stsdData = stsdData2;
                        }
                    } else {
                        stsdData = stsdData2;
                        String str2 = mimeType3;
                    }
                }
                return;
            }
        }
        channelCount = parent.readUnsignedShort();
        parsableByteArray.skipBytes(6);
        sampleRate = parent.readUnsignedFixedPoint1616();
        if (quickTimeSoundDescriptionVersion2 == 1) {
            parsableByteArray.skipBytes(16);
        }
        quickTimeSoundDescriptionVersion = parent.getPosition();
        atomType2 = atomType;
        if (atomType2 != Atom.TYPE_enca) {
            drmInitData2 = drmInitData4;
            atomType3 = atomType2;
        } else {
            sampleEntryEncryptionData = parseSampleEntryEncryptionData(parsableByteArray, i, i2);
            if (sampleEntryEncryptionData != null) {
                atomType2 = ((Integer) sampleEntryEncryptionData.first).intValue();
                if (drmInitData4 != null) {
                    drmInitData2 = drmInitData4.copyWithSchemeType(((TrackEncryptionBox) sampleEntryEncryptionData.second).schemeType);
                } else {
                    drmInitData2 = null;
                }
                drmInitData4 = drmInitData2;
                stsdData2.trackEncryptionBoxes[entryIndex] = (TrackEncryptionBox) sampleEntryEncryptionData.second;
            }
            parsableByteArray.setPosition(quickTimeSoundDescriptionVersion);
            drmInitData2 = drmInitData4;
            atomType3 = atomType2;
        }
        mimeType = null;
        if (atomType3 == Atom.TYPE_ac_3) {
            mimeType = MimeTypes.AUDIO_AC3;
        } else if (atomType3 == Atom.TYPE_ec_3) {
            mimeType = MimeTypes.AUDIO_E_AC3;
        } else if (atomType3 != Atom.TYPE_dtsc) {
            if (atomType3 != Atom.TYPE_dtsh) {
                if (atomType3 == Atom.TYPE_dtsl) {
                    if (atomType3 == Atom.TYPE_dtse) {
                        mimeType = MimeTypes.AUDIO_DTS_EXPRESS;
                    } else if (atomType3 == Atom.TYPE_samr) {
                        mimeType = MimeTypes.AUDIO_AMR_NB;
                    } else if (atomType3 != Atom.TYPE_sawb) {
                        if (atomType3 != Atom.TYPE_lpcm) {
                            if (atomType3 == Atom.TYPE_sowt) {
                                if (atomType3 == Atom.TYPE__mp3) {
                                    mimeType = MimeTypes.AUDIO_MPEG;
                                } else if (atomType3 == Atom.TYPE_alac) {
                                    mimeType = MimeTypes.AUDIO_ALAC;
                                } else if (atomType3 == Atom.TYPE_alaw) {
                                    mimeType = MimeTypes.AUDIO_ALAW;
                                } else if (atomType3 == Atom.TYPE_ulaw) {
                                    mimeType = MimeTypes.AUDIO_MLAW;
                                } else if (atomType3 == Atom.TYPE_Opus) {
                                    mimeType = MimeTypes.AUDIO_OPUS;
                                } else if (atomType3 == Atom.TYPE_fLaC) {
                                    mimeType = MimeTypes.AUDIO_FLAC;
                                }
                            }
                        }
                        mimeType = MimeTypes.AUDIO_RAW;
                    } else {
                        mimeType = MimeTypes.AUDIO_AMR_WB;
                    }
                }
            }
            mimeType = MimeTypes.AUDIO_DTS_HD;
        } else {
            mimeType = MimeTypes.AUDIO_DTS;
        }
        atomType2 = quickTimeSoundDescriptionVersion;
        channelCount2 = channelCount;
        sampleRate2 = sampleRate;
        initializationData = null;
        mimeType2 = mimeType;
        while (atomType2 - i < i2) {
            parsableByteArray.setPosition(atomType2);
            sampleRate = parent.readInt();
            if (sampleRate <= 0) {
            }
            Assertions.checkArgument(sampleRate <= 0, "childAtomSize should be positive");
            childAtomType = parent.readInt();
            if (childAtomType != Atom.TYPE_esds) {
                mimeType3 = mimeType2;
                drmInitData3 = drmInitData2;
                atomType4 = atomType3;
                channelCount = childAtomType;
                quickTimeSoundDescriptionVersion3 = quickTimeSoundDescriptionVersion2;
                quickTimeSoundDescriptionVersion2 = atomType2;
            } else {
                if (isQuickTime) {
                }
                if (childAtomType == Atom.TYPE_dac3) {
                    parsableByteArray.setPosition(atomType2 + 8);
                    stsdData2.format = Ac3Util.parseAc3AnnexFFormat(parsableByteArray, Integer.toString(trackId), str, drmInitData2);
                    mimeType3 = mimeType2;
                    drmInitData3 = drmInitData2;
                    atomType4 = atomType3;
                    channelCount = childAtomType;
                    quickTimeSoundDescriptionVersion3 = quickTimeSoundDescriptionVersion2;
                    quickTimeSoundDescriptionVersion2 = atomType2;
                } else if (childAtomType == Atom.TYPE_dec3) {
                    parsableByteArray.setPosition(atomType2 + 8);
                    stsdData2.format = Ac3Util.parseEAc3AnnexFFormat(parsableByteArray, Integer.toString(trackId), str, drmInitData2);
                    mimeType3 = mimeType2;
                    drmInitData3 = drmInitData2;
                    atomType4 = atomType3;
                    channelCount = childAtomType;
                    quickTimeSoundDescriptionVersion3 = quickTimeSoundDescriptionVersion2;
                    quickTimeSoundDescriptionVersion2 = atomType2;
                } else if (childAtomType != Atom.TYPE_ddts) {
                    childAtomSize = sampleRate;
                    mimeType3 = mimeType2;
                    childPosition = atomType2;
                    drmInitData3 = drmInitData2;
                    atomType4 = atomType3;
                    quickTimeSoundDescriptionVersion3 = quickTimeSoundDescriptionVersion2;
                    channelCount = childAtomType;
                    if (channelCount != Atom.TYPE_alac) {
                        sampleRate = childAtomSize;
                        quickTimeSoundDescriptionVersion2 = childPosition;
                        if (channelCount == Atom.TYPE_dOps) {
                            quickTimeSoundDescriptionVersion = sampleRate - 8;
                            Object obj2 = opusMagic;
                            byte[] initializationData32 = new byte[(obj2.length + quickTimeSoundDescriptionVersion)];
                            System.arraycopy(obj2, 0, initializationData32, 0, obj2.length);
                            parsableByteArray.setPosition(quickTimeSoundDescriptionVersion2 + 8);
                            parsableByteArray.readBytes(initializationData32, opusMagic.length, quickTimeSoundDescriptionVersion);
                            initializationData = initializationData32;
                            mimeType2 = mimeType3;
                        } else if (sampleRate == Atom.TYPE_dfLa) {
                            quickTimeSoundDescriptionVersion = sampleRate - 12;
                            byte[] initializationData42 = new byte[quickTimeSoundDescriptionVersion];
                            parsableByteArray.setPosition(quickTimeSoundDescriptionVersion2 + 12);
                            parsableByteArray.readBytes(initializationData42, 0, quickTimeSoundDescriptionVersion);
                            initializationData = initializationData42;
                            mimeType2 = mimeType3;
                        }
                    } else {
                        sampleRate = childAtomSize;
                        byte[] initializationData22 = new byte[sampleRate];
                        quickTimeSoundDescriptionVersion2 = childPosition;
                        parsableByteArray.setPosition(quickTimeSoundDescriptionVersion2);
                        parsableByteArray.readBytes(initializationData22, 0, sampleRate);
                        initializationData = initializationData22;
                        mimeType2 = mimeType3;
                    }
                    atomType2 = quickTimeSoundDescriptionVersion2 + sampleRate;
                    drmInitData2 = drmInitData3;
                    atomType3 = atomType4;
                    quickTimeSoundDescriptionVersion2 = quickTimeSoundDescriptionVersion3;
                } else {
                    childAtomSize = sampleRate;
                    mimeType3 = mimeType2;
                    childPosition = atomType2;
                    drmInitData3 = drmInitData2;
                    atomType4 = atomType3;
                    int childAtomType22 = childAtomType;
                    quickTimeSoundDescriptionVersion3 = quickTimeSoundDescriptionVersion2;
                    stsdData2.format = Format.createAudioSampleFormat(Integer.toString(trackId), mimeType2, null, -1, -1, channelCount2, sampleRate2, null, drmInitData3, 0, language);
                    sampleRate = childAtomSize;
                    quickTimeSoundDescriptionVersion2 = childPosition;
                    channelCount = childAtomType22;
                }
                mimeType2 = mimeType3;
                atomType2 = quickTimeSoundDescriptionVersion2 + sampleRate;
                drmInitData2 = drmInitData3;
                atomType3 = atomType4;
                quickTimeSoundDescriptionVersion2 = quickTimeSoundDescriptionVersion3;
            }
            if (channelCount != Atom.TYPE_esds) {
                atomType2 = findEsdsPosition(parsableByteArray, quickTimeSoundDescriptionVersion2, sampleRate);
            } else {
                atomType2 = quickTimeSoundDescriptionVersion2;
            }
            esdsAtomPosition = atomType2;
            if (esdsAtomPosition == -1) {
                mimeType2 = mimeType3;
            } else {
                Pair<String, byte[]> mimeTypeAndInitializationData2 = parseEsdsFromParent(parsableByteArray, esdsAtomPosition);
                mimeType2 = (String) mimeTypeAndInitializationData2.first;
                initializationData = (byte[]) mimeTypeAndInitializationData2.second;
                if (MimeTypes.AUDIO_AAC.equals(mimeType2)) {
                    Pair<Integer, Integer> audioSpecificConfig2 = CodecSpecificDataUtil.parseAacAudioSpecificConfig(initializationData);
                    sampleRate2 = ((Integer) audioSpecificConfig2.first).intValue();
                    channelCount2 = ((Integer) audioSpecificConfig2.second).intValue();
                }
            }
            atomType2 = quickTimeSoundDescriptionVersion2 + sampleRate;
            drmInitData2 = drmInitData3;
            atomType3 = atomType4;
            quickTimeSoundDescriptionVersion2 = quickTimeSoundDescriptionVersion3;
        }
        mimeType3 = mimeType2;
        drmInitData3 = drmInitData2;
        atomType4 = atomType3;
        quickTimeSoundDescriptionVersion3 = quickTimeSoundDescriptionVersion2;
        quickTimeSoundDescriptionVersion2 = atomType2;
        if (stsdData2.format != null) {
            stsdData = stsdData2;
            String str22 = mimeType3;
        } else {
            mimeType4 = mimeType3;
            if (mimeType4 == null) {
                int i32 = quickTimeSoundDescriptionVersion2;
                stsdData = stsdData2;
            } else {
                if (MimeTypes.AUDIO_RAW.equals(mimeType4)) {
                }
                mimeType = Integer.toString(trackId);
                if (initializationData != null) {
                    list = Collections.singletonList(initializationData);
                } else {
                    list = null;
                }
                stsdData2.format = Format.createAudioSampleFormat(mimeType, mimeType4, null, -1, -1, channelCount2, sampleRate2, atomType3, list, drmInitData3, 0, language);
            }
        }
    }

    private static int findEsdsPosition(ParsableByteArray parent, int position, int size) {
        int childAtomPosition = parent.getPosition();
        while (childAtomPosition - position < size) {
            parent.setPosition(childAtomPosition);
            int childAtomSize = parent.readInt();
            Assertions.checkArgument(childAtomSize > 0, "childAtomSize should be positive");
            if (parent.readInt() == Atom.TYPE_esds) {
                return childAtomPosition;
            }
            childAtomPosition += childAtomSize;
        }
        return -1;
    }

    private static Pair<String, byte[]> parseEsdsFromParent(ParsableByteArray parent, int position) {
        parent.setPosition((position + 8) + 4);
        parent.skipBytes(1);
        parseExpandableClassSize(parent);
        parent.skipBytes(2);
        int flags = parent.readUnsignedByte();
        if ((flags & 128) != 0) {
            parent.skipBytes(2);
        }
        if ((flags & 64) != 0) {
            parent.skipBytes(parent.readUnsignedShort());
        }
        if ((flags & 32) != 0) {
            parent.skipBytes(2);
        }
        parent.skipBytes(1);
        parseExpandableClassSize(parent);
        String mimeType = MimeTypes.getMimeTypeFromMp4ObjectType(parent.readUnsignedByte());
        if (!MimeTypes.AUDIO_MPEG.equals(mimeType)) {
            if (!MimeTypes.AUDIO_DTS.equals(mimeType)) {
                if (!MimeTypes.AUDIO_DTS_HD.equals(mimeType)) {
                    parent.skipBytes(12);
                    parent.skipBytes(1);
                    int initializationDataSize = parseExpandableClassSize(parent);
                    byte[] initializationData = new byte[initializationDataSize];
                    parent.readBytes(initializationData, 0, initializationDataSize);
                    return Pair.create(mimeType, initializationData);
                }
            }
        }
        return Pair.create(mimeType, null);
    }

    private static Pair<Integer, TrackEncryptionBox> parseSampleEntryEncryptionData(ParsableByteArray parent, int position, int size) {
        int childPosition = parent.getPosition();
        while (childPosition - position < size) {
            parent.setPosition(childPosition);
            int childAtomSize = parent.readInt();
            Assertions.checkArgument(childAtomSize > 0, "childAtomSize should be positive");
            if (parent.readInt() == Atom.TYPE_sinf) {
                Pair<Integer, TrackEncryptionBox> result = parseCommonEncryptionSinfFromParent(parent, childPosition, childAtomSize);
                if (result != null) {
                    return result;
                }
            }
            childPosition += childAtomSize;
        }
        return null;
    }

    static Pair<Integer, TrackEncryptionBox> parseCommonEncryptionSinfFromParent(ParsableByteArray parent, int position, int size) {
        int childPosition = position + 8;
        int schemeInformationBoxPosition = -1;
        int schemeInformationBoxSize = 0;
        String schemeType = null;
        Integer dataFormat = null;
        while (childPosition - position < size) {
            parent.setPosition(childPosition);
            int childAtomSize = parent.readInt();
            int childAtomType = parent.readInt();
            if (childAtomType == Atom.TYPE_frma) {
                dataFormat = Integer.valueOf(parent.readInt());
            } else if (childAtomType == Atom.TYPE_schm) {
                parent.skipBytes(4);
                schemeType = parent.readString(4);
            } else if (childAtomType == Atom.TYPE_schi) {
                schemeInformationBoxPosition = childPosition;
                schemeInformationBoxSize = childAtomSize;
            }
            childPosition += childAtomSize;
        }
        if (!C0555C.CENC_TYPE_cenc.equals(schemeType) && !C0555C.CENC_TYPE_cbc1.equals(schemeType)) {
            if (!C0555C.CENC_TYPE_cens.equals(schemeType)) {
                if (!C0555C.CENC_TYPE_cbcs.equals(schemeType)) {
                    return null;
                }
            }
        }
        boolean z = true;
        Assertions.checkArgument(dataFormat != null, "frma atom is mandatory");
        Assertions.checkArgument(schemeInformationBoxPosition != -1, "schi atom is mandatory");
        TrackEncryptionBox encryptionBox = parseSchiFromParent(parent, schemeInformationBoxPosition, schemeInformationBoxSize, schemeType);
        if (encryptionBox == null) {
            z = false;
        }
        Assertions.checkArgument(z, "tenc atom is mandatory");
        return Pair.create(dataFormat, encryptionBox);
    }

    private static TrackEncryptionBox parseSchiFromParent(ParsableByteArray parent, int position, int size, String schemeType) {
        ParsableByteArray parsableByteArray = parent;
        int childPosition = position + 8;
        while (childPosition - position < size) {
            parsableByteArray.setPosition(childPosition);
            int childAtomSize = parent.readInt();
            if (parent.readInt() == Atom.TYPE_tenc) {
                int patternByte;
                byte[] constantIv;
                int version = Atom.parseFullAtomVersion(parent.readInt());
                boolean defaultIsProtected = true;
                parsableByteArray.skipBytes(1);
                int defaultCryptByteBlock = 0;
                int defaultSkipByteBlock = 0;
                if (version == 0) {
                    parsableByteArray.skipBytes(1);
                } else {
                    patternByte = parent.readUnsignedByte();
                    defaultCryptByteBlock = (patternByte & PsExtractor.VIDEO_STREAM_MASK) >> 4;
                    defaultSkipByteBlock = patternByte & 15;
                }
                if (parent.readUnsignedByte() != 1) {
                    defaultIsProtected = false;
                }
                patternByte = parent.readUnsignedByte();
                byte[] bArr = new byte[16];
                parsableByteArray.readBytes(bArr, 0, bArr.length);
                if (defaultIsProtected && patternByte == 0) {
                    int constantIvSize = parent.readUnsignedByte();
                    byte[] constantIv2 = new byte[constantIvSize];
                    parsableByteArray.readBytes(constantIv2, 0, constantIvSize);
                    constantIv = constantIv2;
                } else {
                    constantIv = null;
                }
                byte[] defaultKeyId = bArr;
                return new TrackEncryptionBox(defaultIsProtected, schemeType, patternByte, bArr, defaultCryptByteBlock, defaultSkipByteBlock, constantIv);
            }
            childPosition += childAtomSize;
        }
        return null;
    }

    private static byte[] parseProjFromParent(ParsableByteArray parent, int position, int size) {
        int childPosition = position + 8;
        while (childPosition - position < size) {
            parent.setPosition(childPosition);
            int childAtomSize = parent.readInt();
            if (parent.readInt() == Atom.TYPE_proj) {
                return Arrays.copyOfRange(parent.data, childPosition, childPosition + childAtomSize);
            }
            childPosition += childAtomSize;
        }
        return null;
    }

    private static int parseExpandableClassSize(ParsableByteArray data) {
        int currentByte = data.readUnsignedByte();
        int size = currentByte & 127;
        while ((currentByte & 128) == 128) {
            currentByte = data.readUnsignedByte();
            size = (size << 7) | (currentByte & 127);
        }
        return size;
    }

    private static boolean canApplyEditWithGaplessInfo(long[] timestamps, long duration, long editStartTime, long editEndTime) {
        int lastIndex = timestamps.length - 1;
        int latestDelayIndex = Util.constrainValue(3, 0, lastIndex);
        int earliestPaddingIndex = Util.constrainValue(timestamps.length - 3, 0, lastIndex);
        if (timestamps[0] > editStartTime || editStartTime >= timestamps[latestDelayIndex] || timestamps[earliestPaddingIndex] >= editEndTime || editEndTime > duration) {
            return false;
        }
        return true;
    }

    private AtomParsers() {
    }
}
