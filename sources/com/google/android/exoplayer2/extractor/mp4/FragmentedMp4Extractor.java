package com.google.android.exoplayer2.extractor.mp4;

import android.support.annotation.Nullable;
import android.util.Pair;
import android.util.SparseArray;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.drm.DrmInitData.SchemeData;
import com.google.android.exoplayer2.extractor.ChunkIndex;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.extractor.TrackOutput.CryptoData;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.google.android.exoplayer2.text.cea.CeaUtil;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.NalUnitUtil;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class FragmentedMp4Extractor implements Extractor {
    private static final Format EMSG_FORMAT = Format.createSampleFormat(null, MimeTypes.APPLICATION_EMSG, Long.MAX_VALUE);
    public static final ExtractorsFactory FACTORY = -$$Lambda$FragmentedMp4Extractor$i0zfpH_PcF0vytkdatCL0xeWFhQ.INSTANCE;
    public static final int FLAG_ENABLE_EMSG_TRACK = 4;
    private static final int FLAG_SIDELOADED = 8;
    public static final int FLAG_WORKAROUND_EVERY_VIDEO_FRAME_IS_SYNC_FRAME = 1;
    public static final int FLAG_WORKAROUND_IGNORE_EDIT_LISTS = 16;
    public static final int FLAG_WORKAROUND_IGNORE_TFDT_BOX = 2;
    private static final byte[] PIFF_SAMPLE_ENCRYPTION_BOX_EXTENDED_TYPE = new byte[]{(byte) -94, (byte) 57, (byte) 79, (byte) 82, (byte) 90, (byte) -101, (byte) 79, (byte) 20, (byte) -94, (byte) 68, (byte) 108, (byte) 66, (byte) 124, (byte) 100, (byte) -115, (byte) -12};
    private static final int SAMPLE_GROUP_TYPE_seig = Util.getIntegerCodeForString("seig");
    private static final int STATE_READING_ATOM_HEADER = 0;
    private static final int STATE_READING_ATOM_PAYLOAD = 1;
    private static final int STATE_READING_ENCRYPTION_DATA = 2;
    private static final int STATE_READING_SAMPLE_CONTINUE = 4;
    private static final int STATE_READING_SAMPLE_START = 3;
    private static final String TAG = "FragmentedMp4Extractor";
    @Nullable
    private final TrackOutput additionalEmsgTrackOutput;
    private ParsableByteArray atomData;
    private final ParsableByteArray atomHeader;
    private int atomHeaderBytesRead;
    private long atomSize;
    private int atomType;
    private TrackOutput[] cea608TrackOutputs;
    private final List<Format> closedCaptionFormats;
    private final ArrayDeque<ContainerAtom> containerAtoms;
    private TrackBundle currentTrackBundle;
    private long durationUs;
    private TrackOutput[] emsgTrackOutputs;
    private long endOfMdatPosition;
    private final byte[] extendedTypeScratch;
    private ExtractorOutput extractorOutput;
    private final int flags;
    private boolean haveOutputSeekMap;
    private final ParsableByteArray nalBuffer;
    private final ParsableByteArray nalPrefix;
    private final ParsableByteArray nalStartCode;
    private int parserState;
    private int pendingMetadataSampleBytes;
    private final ArrayDeque<MetadataSampleInfo> pendingMetadataSampleInfos;
    private long pendingSeekTimeUs;
    private boolean processSeiNalUnitPayload;
    private int sampleBytesWritten;
    private int sampleCurrentNalBytesRemaining;
    private int sampleSize;
    private long segmentIndexEarliestPresentationTimeUs;
    @Nullable
    private final DrmInitData sideloadedDrmInitData;
    @Nullable
    private final Track sideloadedTrack;
    @Nullable
    private final TimestampAdjuster timestampAdjuster;
    private final SparseArray<TrackBundle> trackBundles;

    private static final class MetadataSampleInfo {
        public final long presentationTimeDeltaUs;
        public final int size;

        public MetadataSampleInfo(long presentationTimeDeltaUs, int size) {
            this.presentationTimeDeltaUs = presentationTimeDeltaUs;
            this.size = size;
        }
    }

    private static final class TrackBundle {
        public int currentSampleInTrackRun;
        public int currentSampleIndex;
        public int currentTrackRunIndex;
        private final ParsableByteArray defaultInitializationVector = new ParsableByteArray();
        public DefaultSampleValues defaultSampleValues;
        private final ParsableByteArray encryptionSignalByte = new ParsableByteArray(1);
        public int firstSampleToOutputIndex;
        public final TrackFragment fragment = new TrackFragment();
        public final TrackOutput output;
        public Track track;

        public TrackBundle(TrackOutput output) {
            this.output = output;
        }

        public void init(Track track, DefaultSampleValues defaultSampleValues) {
            this.track = (Track) Assertions.checkNotNull(track);
            this.defaultSampleValues = (DefaultSampleValues) Assertions.checkNotNull(defaultSampleValues);
            this.output.format(track.format);
            reset();
        }

        public void updateDrmInitData(DrmInitData drmInitData) {
            TrackEncryptionBox encryptionBox = this.track.getSampleDescriptionEncryptionBox(this.fragment.header.sampleDescriptionIndex);
            this.output.format(this.track.format.copyWithDrmInitData(drmInitData.copyWithSchemeType(encryptionBox != null ? encryptionBox.schemeType : null)));
        }

        public void reset() {
            this.fragment.reset();
            this.currentSampleIndex = 0;
            this.currentTrackRunIndex = 0;
            this.currentSampleInTrackRun = 0;
            this.firstSampleToOutputIndex = 0;
        }

        public void seek(long timeUs) {
            long timeMs = C0555C.usToMs(timeUs);
            int searchIndex = this.currentSampleIndex;
            while (searchIndex < this.fragment.sampleCount) {
                if (this.fragment.getSamplePresentationTime(searchIndex) < timeMs) {
                    if (this.fragment.sampleIsSyncFrameTable[searchIndex]) {
                        this.firstSampleToOutputIndex = searchIndex;
                    }
                    searchIndex++;
                } else {
                    return;
                }
            }
        }

        public boolean next() {
            this.currentSampleIndex++;
            this.currentSampleInTrackRun++;
            int i = this.currentSampleInTrackRun;
            int[] iArr = this.fragment.trunLength;
            int i2 = this.currentTrackRunIndex;
            if (i != iArr[i2]) {
                return true;
            }
            this.currentTrackRunIndex = i2 + 1;
            this.currentSampleInTrackRun = 0;
            return false;
        }

        public int outputSampleEncryptionData() {
            TrackEncryptionBox encryptionBox = getEncryptionBoxIfEncrypted();
            if (encryptionBox == null) {
                return 0;
            }
            ParsableByteArray initializationVectorData;
            int vectorSize;
            if (encryptionBox.perSampleIvSize != 0) {
                initializationVectorData = this.fragment.sampleEncryptionData;
                vectorSize = encryptionBox.perSampleIvSize;
            } else {
                vectorSize = encryptionBox.defaultInitializationVector;
                this.defaultInitializationVector.reset(vectorSize, vectorSize.length);
                initializationVectorData = this.defaultInitializationVector;
                vectorSize = vectorSize.length;
            }
            boolean subsampleEncryption = this.fragment.sampleHasSubsampleEncryptionTable(this.currentSampleIndex);
            this.encryptionSignalByte.data[0] = (byte) ((subsampleEncryption ? 128 : 0) | vectorSize);
            this.encryptionSignalByte.setPosition(0);
            this.output.sampleData(this.encryptionSignalByte, 1);
            this.output.sampleData(initializationVectorData, vectorSize);
            if (!subsampleEncryption) {
                return vectorSize + 1;
            }
            ParsableByteArray subsampleEncryptionData = this.fragment.sampleEncryptionData;
            int subsampleCount = subsampleEncryptionData.readUnsignedShort();
            subsampleEncryptionData.skipBytes(-2);
            int subsampleDataLength = (subsampleCount * 6) + 2;
            this.output.sampleData(subsampleEncryptionData, subsampleDataLength);
            return (vectorSize + 1) + subsampleDataLength;
        }

        private void skipSampleEncryptionData() {
            TrackEncryptionBox encryptionBox = getEncryptionBoxIfEncrypted();
            if (encryptionBox != null) {
                ParsableByteArray sampleEncryptionData = this.fragment.sampleEncryptionData;
                if (encryptionBox.perSampleIvSize != 0) {
                    sampleEncryptionData.skipBytes(encryptionBox.perSampleIvSize);
                }
                if (this.fragment.sampleHasSubsampleEncryptionTable(this.currentSampleIndex)) {
                    sampleEncryptionData.skipBytes(sampleEncryptionData.readUnsignedShort() * 6);
                }
            }
        }

        private TrackEncryptionBox getEncryptionBoxIfEncrypted() {
            TrackEncryptionBox encryptionBox;
            int sampleDescriptionIndex = this.fragment.header.sampleDescriptionIndex;
            if (this.fragment.trackEncryptionBox != null) {
                encryptionBox = this.fragment.trackEncryptionBox;
            } else {
                encryptionBox = this.track.getSampleDescriptionEncryptionBox(sampleDescriptionIndex);
            }
            return (encryptionBox == null || !encryptionBox.isEncrypted) ? null : encryptionBox;
        }
    }

    private static void parseSaiz(com.google.android.exoplayer2.extractor.mp4.TrackEncryptionBox r12, com.google.android.exoplayer2.util.ParsableByteArray r13, com.google.android.exoplayer2.extractor.mp4.TrackFragment r14) throws com.google.android.exoplayer2.ParserException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:24:0x006f in {2, 3, 12, 13, 14, 15, 17, 18, 19, 21, 23} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r0 = r12.perSampleIvSize;
        r1 = 8;
        r13.setPosition(r1);
        r2 = r13.readInt();
        r3 = com.google.android.exoplayer2.extractor.mp4.Atom.parseFullAtomFlags(r2);
        r4 = r3 & 1;
        r5 = 1;
        if (r4 != r5) goto L_0x0018;
    L_0x0014:
        r13.skipBytes(r1);
        goto L_0x0019;
    L_0x0019:
        r1 = r13.readUnsignedByte();
        r4 = r13.readUnsignedIntToInt();
        r6 = r14.sampleCount;
        if (r4 != r6) goto L_0x004e;
    L_0x0025:
        r6 = 0;
        r7 = 0;
        if (r1 != 0) goto L_0x003e;
    L_0x0029:
        r8 = r14.sampleHasSubsampleEncryptionTable;
        r9 = 0;
    L_0x002c:
        if (r9 >= r4) goto L_0x003d;
    L_0x002e:
        r10 = r13.readUnsignedByte();
        r6 = r6 + r10;
        if (r10 <= r0) goto L_0x0037;
    L_0x0035:
        r11 = 1;
        goto L_0x0038;
    L_0x0037:
        r11 = 0;
    L_0x0038:
        r8[r9] = r11;
        r9 = r9 + 1;
        goto L_0x002c;
    L_0x003d:
        goto L_0x004a;
    L_0x003e:
        if (r1 <= r0) goto L_0x0041;
    L_0x0040:
        goto L_0x0042;
    L_0x0041:
        r5 = 0;
    L_0x0042:
        r8 = r1 * r4;
        r6 = r6 + r8;
        r8 = r14.sampleHasSubsampleEncryptionTable;
        java.util.Arrays.fill(r8, r7, r4, r5);
    L_0x004a:
        r14.initEncryptionData(r6);
        return;
    L_0x004e:
        r5 = new com.google.android.exoplayer2.ParserException;
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r7 = "Length mismatch: ";
        r6.append(r7);
        r6.append(r4);
        r7 = ", ";
        r6.append(r7);
        r7 = r14.sampleCount;
        r6.append(r7);
        r6 = r6.toString();
        r5.<init>(r6);
        throw r5;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor.parseSaiz(com.google.android.exoplayer2.extractor.mp4.TrackEncryptionBox, com.google.android.exoplayer2.util.ParsableByteArray, com.google.android.exoplayer2.extractor.mp4.TrackFragment):void");
    }

    private boolean readAtomHeader(com.google.android.exoplayer2.extractor.ExtractorInput r11) throws java.io.IOException, java.lang.InterruptedException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:66:0x0162 in {4, 5, 6, 9, 16, 17, 20, 21, 22, 29, 30, 31, 36, 37, 39, 44, 45, 46, 53, 55, 57, 60, 61, 63, 65} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r10 = this;
        r0 = r10.atomHeaderBytesRead;
        r1 = 8;
        r2 = 0;
        r3 = 1;
        if (r0 != 0) goto L_0x002b;
    L_0x0008:
        r0 = r10.atomHeader;
        r0 = r0.data;
        r0 = r11.readFully(r0, r2, r1, r3);
        if (r0 != 0) goto L_0x0013;
    L_0x0012:
        return r2;
    L_0x0013:
        r10.atomHeaderBytesRead = r1;
        r0 = r10.atomHeader;
        r0.setPosition(r2);
        r0 = r10.atomHeader;
        r4 = r0.readUnsignedInt();
        r10.atomSize = r4;
        r0 = r10.atomHeader;
        r0 = r0.readInt();
        r10.atomType = r0;
        goto L_0x002c;
    L_0x002c:
        r4 = r10.atomSize;
        r6 = 1;
        r0 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r0 != 0) goto L_0x004b;
    L_0x0034:
        r0 = 8;
        r4 = r10.atomHeader;
        r4 = r4.data;
        r11.readFully(r4, r1, r0);
        r4 = r10.atomHeaderBytesRead;
        r4 = r4 + r0;
        r10.atomHeaderBytesRead = r4;
        r4 = r10.atomHeader;
        r4 = r4.readUnsignedLongToLong();
        r10.atomSize = r4;
        goto L_0x0081;
    L_0x004b:
        r6 = 0;
        r0 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r0 != 0) goto L_0x0081;
    L_0x0051:
        r4 = r11.getLength();
        r6 = -1;
        r0 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r0 != 0) goto L_0x006e;
    L_0x005b:
        r0 = r10.containerAtoms;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x006e;
    L_0x0063:
        r0 = r10.containerAtoms;
        r0 = r0.peek();
        r0 = (com.google.android.exoplayer2.extractor.mp4.Atom.ContainerAtom) r0;
        r4 = r0.endPosition;
        goto L_0x006f;
    L_0x006f:
        r0 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r0 == 0) goto L_0x0080;
    L_0x0073:
        r6 = r11.getPosition();
        r6 = r4 - r6;
        r0 = r10.atomHeaderBytesRead;
        r8 = (long) r0;
        r6 = r6 + r8;
        r10.atomSize = r6;
        goto L_0x0082;
    L_0x0080:
        goto L_0x0082;
    L_0x0082:
        r4 = r10.atomSize;
        r0 = r10.atomHeaderBytesRead;
        r6 = (long) r0;
        r0 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r0 < 0) goto L_0x015a;
    L_0x008b:
        r4 = r11.getPosition();
        r0 = r10.atomHeaderBytesRead;
        r6 = (long) r0;
        r4 = r4 - r6;
        r0 = r10.atomType;
        r6 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_moof;
        if (r0 != r6) goto L_0x00b6;
    L_0x0099:
        r0 = r10.trackBundles;
        r0 = r0.size();
        r6 = 0;
    L_0x00a0:
        if (r6 >= r0) goto L_0x00b5;
    L_0x00a2:
        r7 = r10.trackBundles;
        r7 = r7.valueAt(r6);
        r7 = (com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor.TrackBundle) r7;
        r7 = r7.fragment;
        r7.atomPosition = r4;
        r7.auxiliaryDataPosition = r4;
        r7.dataPosition = r4;
        r6 = r6 + 1;
        goto L_0x00a0;
    L_0x00b5:
        goto L_0x00b7;
    L_0x00b7:
        r0 = r10.atomType;
        r6 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_mdat;
        r7 = 0;
        if (r0 != r6) goto L_0x00dd;
    L_0x00be:
        r10.currentTrackBundle = r7;
        r0 = r10.atomSize;
        r0 = r0 + r4;
        r10.endOfMdatPosition = r0;
        r0 = r10.haveOutputSeekMap;
        if (r0 != 0) goto L_0x00d8;
    L_0x00c9:
        r0 = r10.extractorOutput;
        r1 = new com.google.android.exoplayer2.extractor.SeekMap$Unseekable;
        r6 = r10.durationUs;
        r1.<init>(r6, r4);
        r0.seekMap(r1);
        r10.haveOutputSeekMap = r3;
        goto L_0x00d9;
    L_0x00d9:
        r0 = 2;
        r10.parserState = r0;
        return r3;
    L_0x00dd:
        r0 = r10.atomType;
        r0 = shouldParseContainerAtom(r0);
        if (r0 == 0) goto L_0x010c;
    L_0x00e5:
        r0 = r11.getPosition();
        r6 = r10.atomSize;
        r0 = r0 + r6;
        r6 = 8;
        r0 = r0 - r6;
        r2 = r10.containerAtoms;
        r6 = new com.google.android.exoplayer2.extractor.mp4.Atom$ContainerAtom;
        r7 = r10.atomType;
        r6.<init>(r7, r0);
        r2.push(r6);
        r6 = r10.atomSize;
        r2 = r10.atomHeaderBytesRead;
        r8 = (long) r2;
        r2 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r2 != 0) goto L_0x0108;
    L_0x0104:
        r10.processAtomEnded(r0);
        goto L_0x010b;
    L_0x0108:
        r10.enterReadingAtomHeaderState();
    L_0x010b:
        goto L_0x0151;
    L_0x010c:
        r0 = r10.atomType;
        r0 = shouldParseLeafAtom(r0);
        r8 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        if (r0 == 0) goto L_0x0147;
    L_0x0117:
        r0 = r10.atomHeaderBytesRead;
        if (r0 != r1) goto L_0x013f;
    L_0x011b:
        r6 = r10.atomSize;
        r0 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r0 > 0) goto L_0x0137;
    L_0x0121:
        r0 = new com.google.android.exoplayer2.util.ParsableByteArray;
        r6 = (int) r6;
        r0.<init>(r6);
        r10.atomData = r0;
        r0 = r10.atomHeader;
        r0 = r0.data;
        r6 = r10.atomData;
        r6 = r6.data;
        java.lang.System.arraycopy(r0, r2, r6, r2, r1);
        r10.parserState = r3;
        goto L_0x0151;
    L_0x0137:
        r0 = new com.google.android.exoplayer2.ParserException;
        r1 = "Leaf atom with length > 2147483647 (unsupported).";
        r0.<init>(r1);
        throw r0;
    L_0x013f:
        r0 = new com.google.android.exoplayer2.ParserException;
        r1 = "Leaf atom defines extended atom size (unsupported).";
        r0.<init>(r1);
        throw r0;
    L_0x0147:
        r0 = r10.atomSize;
        r2 = (r0 > r8 ? 1 : (r0 == r8 ? 0 : -1));
        if (r2 > 0) goto L_0x0152;
    L_0x014d:
        r10.atomData = r7;
        r10.parserState = r3;
    L_0x0151:
        return r3;
    L_0x0152:
        r0 = new com.google.android.exoplayer2.ParserException;
        r1 = "Skipping atom with length > 2147483647 (unsupported).";
        r0.<init>(r1);
        throw r0;
    L_0x015a:
        r0 = new com.google.android.exoplayer2.ParserException;
        r1 = "Atom size less than header length (unsupported).";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor.readAtomHeader(com.google.android.exoplayer2.extractor.ExtractorInput):boolean");
    }

    private void readEncryptionData(com.google.android.exoplayer2.extractor.ExtractorInput r10) throws java.io.IOException, java.lang.InterruptedException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:18:0x0053 in {6, 7, 8, 11, 15, 17} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r9 = this;
        r0 = 0;
        r1 = 9223372036854775807; // 0x7fffffffffffffff float:NaN double:NaN;
        r3 = r9.trackBundles;
        r3 = r3.size();
        r4 = 0;
    L_0x000d:
        if (r4 >= r3) goto L_0x0033;
    L_0x000f:
        r5 = r9.trackBundles;
        r5 = r5.valueAt(r4);
        r5 = (com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor.TrackBundle) r5;
        r5 = r5.fragment;
        r6 = r5.sampleEncryptionDataNeedsFill;
        if (r6 == 0) goto L_0x002f;
    L_0x001d:
        r6 = r5.auxiliaryDataPosition;
        r8 = (r6 > r1 ? 1 : (r6 == r1 ? 0 : -1));
        if (r8 >= 0) goto L_0x002f;
    L_0x0023:
        r1 = r5.auxiliaryDataPosition;
        r6 = r9.trackBundles;
        r6 = r6.valueAt(r4);
        r0 = r6;
        r0 = (com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor.TrackBundle) r0;
        goto L_0x0030;
    L_0x0030:
        r4 = r4 + 1;
        goto L_0x000d;
    L_0x0033:
        if (r0 != 0) goto L_0x0039;
    L_0x0035:
        r4 = 3;
        r9.parserState = r4;
        return;
    L_0x0039:
        r4 = r10.getPosition();
        r4 = r1 - r4;
        r4 = (int) r4;
        if (r4 < 0) goto L_0x004b;
    L_0x0042:
        r10.skipFully(r4);
        r5 = r0.fragment;
        r5.fillEncryptionData(r10);
        return;
    L_0x004b:
        r5 = new com.google.android.exoplayer2.ParserException;
        r6 = "Offset to encryption data was negative.";
        r5.<init>(r6);
        throw r5;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor.readEncryptionData(com.google.android.exoplayer2.extractor.ExtractorInput):void");
    }

    public FragmentedMp4Extractor() {
        this(0);
    }

    public FragmentedMp4Extractor(int flags) {
        this(flags, null);
    }

    public FragmentedMp4Extractor(int flags, @Nullable TimestampAdjuster timestampAdjuster) {
        this(flags, timestampAdjuster, null, null);
    }

    public FragmentedMp4Extractor(int flags, @Nullable TimestampAdjuster timestampAdjuster, @Nullable Track sideloadedTrack, @Nullable DrmInitData sideloadedDrmInitData) {
        this(flags, timestampAdjuster, sideloadedTrack, sideloadedDrmInitData, Collections.emptyList());
    }

    public FragmentedMp4Extractor(int flags, @Nullable TimestampAdjuster timestampAdjuster, @Nullable Track sideloadedTrack, @Nullable DrmInitData sideloadedDrmInitData, List<Format> closedCaptionFormats) {
        this(flags, timestampAdjuster, sideloadedTrack, sideloadedDrmInitData, closedCaptionFormats, null);
    }

    public FragmentedMp4Extractor(int flags, @Nullable TimestampAdjuster timestampAdjuster, @Nullable Track sideloadedTrack, @Nullable DrmInitData sideloadedDrmInitData, List<Format> closedCaptionFormats, @Nullable TrackOutput additionalEmsgTrackOutput) {
        this.flags = (sideloadedTrack != null ? 8 : 0) | flags;
        this.timestampAdjuster = timestampAdjuster;
        this.sideloadedTrack = sideloadedTrack;
        this.sideloadedDrmInitData = sideloadedDrmInitData;
        this.closedCaptionFormats = Collections.unmodifiableList(closedCaptionFormats);
        this.additionalEmsgTrackOutput = additionalEmsgTrackOutput;
        this.atomHeader = new ParsableByteArray(16);
        this.nalStartCode = new ParsableByteArray(NalUnitUtil.NAL_START_CODE);
        this.nalPrefix = new ParsableByteArray(5);
        this.nalBuffer = new ParsableByteArray();
        this.extendedTypeScratch = new byte[16];
        this.containerAtoms = new ArrayDeque();
        this.pendingMetadataSampleInfos = new ArrayDeque();
        this.trackBundles = new SparseArray();
        this.durationUs = C0555C.TIME_UNSET;
        this.pendingSeekTimeUs = C0555C.TIME_UNSET;
        this.segmentIndexEarliestPresentationTimeUs = C0555C.TIME_UNSET;
        enterReadingAtomHeaderState();
    }

    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        return Sniffer.sniffFragmented(input);
    }

    public void init(ExtractorOutput output) {
        this.extractorOutput = output;
        Track track = this.sideloadedTrack;
        if (track != null) {
            TrackBundle bundle = new TrackBundle(output.track(0, track.type));
            bundle.init(this.sideloadedTrack, new DefaultSampleValues(0, 0, 0, 0));
            this.trackBundles.put(0, bundle);
            maybeInitExtraTracks();
            this.extractorOutput.endTracks();
        }
    }

    public void seek(long position, long timeUs) {
        int trackCount = this.trackBundles.size();
        for (int i = 0; i < trackCount; i++) {
            ((TrackBundle) this.trackBundles.valueAt(i)).reset();
        }
        this.pendingMetadataSampleInfos.clear();
        this.pendingMetadataSampleBytes = 0;
        this.pendingSeekTimeUs = timeUs;
        this.containerAtoms.clear();
        enterReadingAtomHeaderState();
    }

    public void release() {
    }

    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        while (true) {
            switch (this.parserState) {
                case 0:
                    if (readAtomHeader(input)) {
                        break;
                    }
                    return -1;
                case 1:
                    readAtomPayload(input);
                    break;
                case 2:
                    readEncryptionData(input);
                    break;
                default:
                    if (!readSample(input)) {
                        break;
                    }
                    return 0;
            }
        }
    }

    private void enterReadingAtomHeaderState() {
        this.parserState = 0;
        this.atomHeaderBytesRead = 0;
    }

    private void readAtomPayload(ExtractorInput input) throws IOException, InterruptedException {
        int atomPayloadSize = ((int) this.atomSize) - this.atomHeaderBytesRead;
        ParsableByteArray parsableByteArray = this.atomData;
        if (parsableByteArray != null) {
            input.readFully(parsableByteArray.data, 8, atomPayloadSize);
            onLeafAtomRead(new LeafAtom(this.atomType, this.atomData), input.getPosition());
        } else {
            input.skipFully(atomPayloadSize);
        }
        processAtomEnded(input.getPosition());
    }

    private void processAtomEnded(long atomEndPosition) throws ParserException {
        while (!this.containerAtoms.isEmpty() && ((ContainerAtom) this.containerAtoms.peek()).endPosition == atomEndPosition) {
            onContainerAtomRead((ContainerAtom) this.containerAtoms.pop());
        }
        enterReadingAtomHeaderState();
    }

    private void onLeafAtomRead(LeafAtom leaf, long inputPosition) throws ParserException {
        if (this.containerAtoms.isEmpty()) {
            if (leaf.type == Atom.TYPE_sidx) {
                Pair<Long, ChunkIndex> result = parseSidx(leaf.data, inputPosition);
                this.segmentIndexEarliestPresentationTimeUs = ((Long) result.first).longValue();
                this.extractorOutput.seekMap((SeekMap) result.second);
                this.haveOutputSeekMap = true;
            } else if (leaf.type == Atom.TYPE_emsg) {
                onEmsgLeafAtomRead(leaf.data);
                return;
            }
            return;
        }
        ((ContainerAtom) this.containerAtoms.peek()).add(leaf);
    }

    private void onContainerAtomRead(ContainerAtom container) throws ParserException {
        if (container.type == Atom.TYPE_moov) {
            onMoovContainerAtomRead(container);
        } else if (container.type == Atom.TYPE_moof) {
            onMoofContainerAtomRead(container);
        } else if (!this.containerAtoms.isEmpty()) {
            ((ContainerAtom) this.containerAtoms.peek()).add(container);
        }
    }

    private void onMoovContainerAtomRead(ContainerAtom moov) throws ParserException {
        DrmInitData drmInitData;
        int i;
        int moovContainerChildrenSize;
        SparseArray<Track> tracks;
        ContainerAtom containerAtom = moov;
        Assertions.checkState(this.sideloadedTrack == null, "Unexpected moov box.");
        DrmInitData drmInitData2 = r0.sideloadedDrmInitData;
        if (drmInitData2 != null) {
            drmInitData = drmInitData2;
        } else {
            drmInitData = getDrmInitDataFromAtoms(containerAtom.leafChildren);
        }
        ContainerAtom mvex = containerAtom.getContainerAtomOfType(Atom.TYPE_mvex);
        SparseArray<DefaultSampleValues> defaultSampleValuesArray = new SparseArray();
        int mvexChildrenSize = mvex.leafChildren.size();
        long duration = C0555C.TIME_UNSET;
        for (i = 0; i < mvexChildrenSize; i++) {
            LeafAtom atom = (LeafAtom) mvex.leafChildren.get(i);
            if (atom.type == Atom.TYPE_trex) {
                Pair<Integer, DefaultSampleValues> trexData = parseTrex(atom.data);
                defaultSampleValuesArray.put(((Integer) trexData.first).intValue(), trexData.second);
            } else if (atom.type == Atom.TYPE_mehd) {
                duration = parseMehd(atom.data);
            }
        }
        SparseArray<Track> tracks2 = new SparseArray();
        int moovContainerChildrenSize2 = containerAtom.containerChildren.size();
        i = 0;
        while (i < moovContainerChildrenSize2) {
            int i2;
            ContainerAtom atom2 = (ContainerAtom) containerAtom.containerChildren.get(i);
            if (atom2.type == Atom.TYPE_trak) {
                i2 = i;
                moovContainerChildrenSize = moovContainerChildrenSize2;
                tracks = tracks2;
                Track track = AtomParsers.parseTrak(atom2, containerAtom.getLeafAtomOfType(Atom.TYPE_mvhd), duration, drmInitData, (r0.flags & 16) != 0, false);
                if (track != null) {
                    tracks.put(track.id, track);
                }
            } else {
                i2 = i;
                ContainerAtom containerAtom2 = atom2;
                moovContainerChildrenSize = moovContainerChildrenSize2;
                tracks = tracks2;
            }
            i = i2 + 1;
            tracks2 = tracks;
            moovContainerChildrenSize2 = moovContainerChildrenSize;
        }
        moovContainerChildrenSize = moovContainerChildrenSize2;
        tracks = tracks2;
        int trackCount = tracks.size();
        if (r0.trackBundles.size() == 0) {
            int i3 = 0;
            while (i3 < trackCount) {
                Track track2 = (Track) tracks.valueAt(i3);
                TrackBundle trackBundle = new TrackBundle(r0.extractorOutput.track(i3, track2.type));
                trackBundle.init(track2, getDefaultSampleValues(defaultSampleValuesArray, track2.id));
                r0.trackBundles.put(track2.id, trackBundle);
                atom2 = mvex;
                r0.durationUs = Math.max(r0.durationUs, track2.durationUs);
                i3++;
                mvex = atom2;
                containerAtom = moov;
            }
            maybeInitExtraTracks();
            r0.extractorOutput.endTracks();
            return;
        }
        Assertions.checkState(r0.trackBundles.size() == trackCount);
        for (int i4 = 0; i4 < trackCount; i4++) {
            Track track3 = (Track) tracks.valueAt(i4);
            ((TrackBundle) r0.trackBundles.get(track3.id)).init(track3, getDefaultSampleValues(defaultSampleValuesArray, track3.id));
        }
    }

    private DefaultSampleValues getDefaultSampleValues(SparseArray<DefaultSampleValues> defaultSampleValuesArray, int trackId) {
        if (defaultSampleValuesArray.size() == 1) {
            return (DefaultSampleValues) defaultSampleValuesArray.valueAt(0);
        }
        return (DefaultSampleValues) Assertions.checkNotNull(defaultSampleValuesArray.get(trackId));
    }

    private void onMoofContainerAtomRead(ContainerAtom moof) throws ParserException {
        DrmInitData drmInitData;
        int trackCount;
        int i;
        parseMoof(moof, this.trackBundles, this.flags, this.extendedTypeScratch);
        if (this.sideloadedDrmInitData != null) {
            drmInitData = null;
        } else {
            drmInitData = getDrmInitDataFromAtoms(moof.leafChildren);
        }
        if (drmInitData != null) {
            trackCount = this.trackBundles.size();
            for (i = 0; i < trackCount; i++) {
                ((TrackBundle) this.trackBundles.valueAt(i)).updateDrmInitData(drmInitData);
            }
        }
        if (this.pendingSeekTimeUs != C0555C.TIME_UNSET) {
            trackCount = this.trackBundles.size();
            for (i = 0; i < trackCount; i++) {
                ((TrackBundle) this.trackBundles.valueAt(i)).seek(this.pendingSeekTimeUs);
            }
            this.pendingSeekTimeUs = C0555C.TIME_UNSET;
        }
    }

    private void maybeInitExtraTracks() {
        int i;
        TrackOutput trackOutput;
        if (this.emsgTrackOutputs == null) {
            int emsgTrackOutputCount;
            this.emsgTrackOutputs = new TrackOutput[2];
            i = 0;
            trackOutput = this.additionalEmsgTrackOutput;
            if (trackOutput != null) {
                emsgTrackOutputCount = 0 + 1;
                this.emsgTrackOutputs[0] = trackOutput;
                i = emsgTrackOutputCount;
            }
            if ((this.flags & 4) != 0) {
                emsgTrackOutputCount = i + 1;
                this.emsgTrackOutputs[i] = this.extractorOutput.track(this.trackBundles.size(), 4);
                i = emsgTrackOutputCount;
            }
            this.emsgTrackOutputs = (TrackOutput[]) Arrays.copyOf(this.emsgTrackOutputs, i);
            for (TrackOutput eventMessageTrackOutput : this.emsgTrackOutputs) {
                eventMessageTrackOutput.format(EMSG_FORMAT);
            }
        }
        if (this.cea608TrackOutputs == null) {
            this.cea608TrackOutputs = new TrackOutput[this.closedCaptionFormats.size()];
            for (i = 0; i < this.cea608TrackOutputs.length; i++) {
                trackOutput = this.extractorOutput.track((this.trackBundles.size() + 1) + i, 3);
                trackOutput.format((Format) this.closedCaptionFormats.get(i));
                this.cea608TrackOutputs[i] = trackOutput;
            }
        }
    }

    private void onEmsgLeafAtomRead(ParsableByteArray atom) {
        ParsableByteArray parsableByteArray = atom;
        TrackOutput[] trackOutputArr = this.emsgTrackOutputs;
        if (trackOutputArr != null) {
            if (trackOutputArr.length != 0) {
                parsableByteArray.setPosition(12);
                int sampleSize = atom.bytesLeft();
                atom.readNullTerminatedString();
                atom.readNullTerminatedString();
                long presentationTimeDeltaUs = Util.scaleLargeTimestamp(atom.readUnsignedInt(), 1000000, atom.readUnsignedInt());
                for (TrackOutput emsgTrackOutput : r0.emsgTrackOutputs) {
                    parsableByteArray.setPosition(12);
                    emsgTrackOutput.sampleData(parsableByteArray, sampleSize);
                }
                long sampleTimeUs = r0.segmentIndexEarliestPresentationTimeUs;
                if (sampleTimeUs != C0555C.TIME_UNSET) {
                    long sampleTimeUs2;
                    sampleTimeUs += presentationTimeDeltaUs;
                    TimestampAdjuster timestampAdjuster = r0.timestampAdjuster;
                    if (timestampAdjuster != null) {
                        sampleTimeUs2 = timestampAdjuster.adjustSampleTimestamp(sampleTimeUs);
                    } else {
                        sampleTimeUs2 = sampleTimeUs;
                    }
                    trackOutputArr = r0.emsgTrackOutputs;
                    int length = trackOutputArr.length;
                    int i = 0;
                    while (i < length) {
                        int i2 = i;
                        int i3 = length;
                        trackOutputArr[i].sampleMetadata(sampleTimeUs2, 1, sampleSize, 0, null);
                        i = i2 + 1;
                        length = i3;
                    }
                } else {
                    r0.pendingMetadataSampleInfos.addLast(new MetadataSampleInfo(presentationTimeDeltaUs, sampleSize));
                    r0.pendingMetadataSampleBytes += sampleSize;
                }
            }
        }
    }

    private static Pair<Integer, DefaultSampleValues> parseTrex(ParsableByteArray trex) {
        trex.setPosition(12);
        return Pair.create(Integer.valueOf(trex.readInt()), new DefaultSampleValues(trex.readUnsignedIntToInt() - 1, trex.readUnsignedIntToInt(), trex.readUnsignedIntToInt(), trex.readInt()));
    }

    private static long parseMehd(ParsableByteArray mehd) {
        mehd.setPosition(8);
        return Atom.parseFullAtomVersion(mehd.readInt()) == 0 ? mehd.readUnsignedInt() : mehd.readUnsignedLongToLong();
    }

    private static void parseMoof(ContainerAtom moof, SparseArray<TrackBundle> trackBundleArray, int flags, byte[] extendedTypeScratch) throws ParserException {
        int moofContainerChildrenSize = moof.containerChildren.size();
        for (int i = 0; i < moofContainerChildrenSize; i++) {
            ContainerAtom child = (ContainerAtom) moof.containerChildren.get(i);
            if (child.type == Atom.TYPE_traf) {
                parseTraf(child, trackBundleArray, flags, extendedTypeScratch);
            }
        }
    }

    private static void parseTraf(ContainerAtom traf, SparseArray<TrackBundle> trackBundleArray, int flags, byte[] extendedTypeScratch) throws ParserException {
        ContainerAtom containerAtom = traf;
        int i = flags;
        LeafAtom tfhd = containerAtom.getLeafAtomOfType(Atom.TYPE_tfhd);
        TrackBundle trackBundle = parseTfhd(tfhd.data, trackBundleArray);
        if (trackBundle != null) {
            TrackFragment fragment = trackBundle.fragment;
            long decodeTime = fragment.nextFragmentDecodeTime;
            trackBundle.reset();
            if (containerAtom.getLeafAtomOfType(Atom.TYPE_tfdt) != null && (i & 2) == 0) {
                decodeTime = parseTfdt(containerAtom.getLeafAtomOfType(Atom.TYPE_tfdt).data);
            }
            parseTruns(containerAtom, trackBundle, decodeTime, i);
            TrackEncryptionBox encryptionBox = trackBundle.track.getSampleDescriptionEncryptionBox(fragment.header.sampleDescriptionIndex);
            LeafAtom saiz = containerAtom.getLeafAtomOfType(Atom.TYPE_saiz);
            if (saiz != null) {
                parseSaiz(encryptionBox, saiz.data, fragment);
            }
            LeafAtom saio = containerAtom.getLeafAtomOfType(Atom.TYPE_saio);
            if (saio != null) {
                parseSaio(saio.data, fragment);
            }
            LeafAtom senc = containerAtom.getLeafAtomOfType(Atom.TYPE_senc);
            if (senc != null) {
                parseSenc(senc.data, fragment);
            }
            LeafAtom sbgp = containerAtom.getLeafAtomOfType(Atom.TYPE_sbgp);
            LeafAtom sgpd = containerAtom.getLeafAtomOfType(Atom.TYPE_sgpd);
            if (sbgp == null || sgpd == null) {
            } else {
                String str;
                ParsableByteArray parsableByteArray = sbgp.data;
                ParsableByteArray parsableByteArray2 = sgpd.data;
                if (encryptionBox != null) {
                    str = encryptionBox.schemeType;
                } else {
                    str = null;
                }
                parseSgpd(parsableByteArray, parsableByteArray2, str, fragment);
            }
            i = containerAtom.leafChildren.size();
            int i2 = 0;
            while (i2 < i) {
                LeafAtom atom = (LeafAtom) containerAtom.leafChildren.get(i2);
                int leafChildrenSize = i;
                if (atom.type == Atom.TYPE_uuid) {
                    parseUuid(atom.data, fragment, extendedTypeScratch);
                } else {
                    byte[] bArr = extendedTypeScratch;
                }
                i2++;
                i = leafChildrenSize;
                containerAtom = traf;
            }
            i = extendedTypeScratch;
        }
    }

    private static void parseTruns(ContainerAtom traf, TrackBundle trackBundle, long decodeTime, int flags) {
        TrackBundle trackBundle2 = trackBundle;
        List<LeafAtom> leafChildren = traf.leafChildren;
        int leafChildrenSize = leafChildren.size();
        int trunCount = 0;
        int totalSampleCount = 0;
        for (int i = 0; i < leafChildrenSize; i++) {
            LeafAtom atom = (LeafAtom) leafChildren.get(i);
            if (atom.type == Atom.TYPE_trun) {
                ParsableByteArray trunData = atom.data;
                trunData.setPosition(12);
                int trunSampleCount = trunData.readUnsignedIntToInt();
                if (trunSampleCount > 0) {
                    totalSampleCount += trunSampleCount;
                    trunCount++;
                }
            }
        }
        trackBundle2.currentTrackRunIndex = 0;
        trackBundle2.currentSampleInTrackRun = 0;
        trackBundle2.currentSampleIndex = 0;
        trackBundle2.fragment.initTables(trunCount, totalSampleCount);
        int trunStartPosition = 0;
        int trunIndex = 0;
        for (int i2 = 0; i2 < leafChildrenSize; i2++) {
            LeafAtom trun = (LeafAtom) leafChildren.get(i2);
            if (trun.type == Atom.TYPE_trun) {
                int trunIndex2 = trunIndex + 1;
                trunStartPosition = parseTrun(trackBundle, trunIndex, decodeTime, flags, trun.data, trunStartPosition);
                trunIndex = trunIndex2;
            }
        }
    }

    private static void parseSaio(ParsableByteArray saio, TrackFragment out) throws ParserException {
        saio.setPosition(8);
        int fullAtom = saio.readInt();
        if ((Atom.parseFullAtomFlags(fullAtom) & 1) == 1) {
            saio.skipBytes(8);
        }
        int entryCount = saio.readUnsignedIntToInt();
        if (entryCount == 1) {
            out.auxiliaryDataPosition += Atom.parseFullAtomVersion(fullAtom) == 0 ? saio.readUnsignedInt() : saio.readUnsignedLongToLong();
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unexpected saio entry count: ");
        stringBuilder.append(entryCount);
        throw new ParserException(stringBuilder.toString());
    }

    private static TrackBundle parseTfhd(ParsableByteArray tfhd, SparseArray<TrackBundle> trackBundles) {
        tfhd.setPosition(8);
        int atomFlags = Atom.parseFullAtomFlags(tfhd.readInt());
        TrackBundle trackBundle = getTrackBundle(trackBundles, tfhd.readInt());
        if (trackBundle == null) {
            return null;
        }
        if ((atomFlags & 1) != 0) {
            long baseDataPosition = tfhd.readUnsignedLongToLong();
            trackBundle.fragment.dataPosition = baseDataPosition;
            trackBundle.fragment.auxiliaryDataPosition = baseDataPosition;
        }
        DefaultSampleValues defaultSampleValues = trackBundle.defaultSampleValues;
        trackBundle.fragment.header = new DefaultSampleValues((atomFlags & 2) != 0 ? tfhd.readUnsignedIntToInt() - 1 : defaultSampleValues.sampleDescriptionIndex, (atomFlags & 8) != 0 ? tfhd.readUnsignedIntToInt() : defaultSampleValues.duration, (atomFlags & 16) != 0 ? tfhd.readUnsignedIntToInt() : defaultSampleValues.size, (atomFlags & 32) != 0 ? tfhd.readUnsignedIntToInt() : defaultSampleValues.flags);
        return trackBundle;
    }

    @Nullable
    private static TrackBundle getTrackBundle(SparseArray<TrackBundle> trackBundles, int trackId) {
        if (trackBundles.size() == 1) {
            return (TrackBundle) trackBundles.valueAt(0);
        }
        return (TrackBundle) trackBundles.get(trackId);
    }

    private static long parseTfdt(ParsableByteArray tfdt) {
        tfdt.setPosition(8);
        return Atom.parseFullAtomVersion(tfdt.readInt()) == 1 ? tfdt.readUnsignedLongToLong() : tfdt.readUnsignedInt();
    }

    private static int parseTrun(TrackBundle trackBundle, int index, long decodeTime, int flags, ParsableByteArray trun, int trackRunStart) {
        int firstSampleFlags;
        long cumulativeTime;
        boolean sampleDurationsPresent;
        boolean firstSampleFlagsPresent;
        DefaultSampleValues defaultSampleValues;
        boolean sampleSizesPresent;
        boolean sampleFlagsPresent;
        TrackBundle trackBundle2 = trackBundle;
        trun.setPosition(8);
        int fullAtom = trun.readInt();
        int atomFlags = Atom.parseFullAtomFlags(fullAtom);
        Track track = trackBundle2.track;
        TrackFragment fragment = trackBundle2.fragment;
        DefaultSampleValues defaultSampleValues2 = fragment.header;
        fragment.trunLength[index] = trun.readUnsignedIntToInt();
        fragment.trunDataPosition[index] = fragment.dataPosition;
        if ((atomFlags & 1) != 0) {
            long[] jArr = fragment.trunDataPosition;
            jArr[index] = jArr[index] + ((long) trun.readInt());
        }
        boolean firstSampleFlagsPresent2 = (atomFlags & 4) != 0;
        int firstSampleFlags2 = defaultSampleValues2.flags;
        if (firstSampleFlagsPresent2) {
            firstSampleFlags2 = trun.readUnsignedIntToInt();
        }
        boolean sampleDurationsPresent2 = (atomFlags & 256) != 0;
        boolean sampleSizesPresent2 = (atomFlags & 512) != 0;
        boolean sampleFlagsPresent2 = (atomFlags & 1024) != 0;
        boolean sampleCompositionTimeOffsetsPresent = (atomFlags & 2048) != 0;
        long edtsOffset = 0;
        if (track.editListDurations != null && track.editListDurations.length == 1 && track.editListDurations[0] == 0) {
            firstSampleFlags = firstSampleFlags2;
            edtsOffset = Util.scaleLargeTimestamp(track.editListMediaTimes[0], 1000, track.timescale);
        } else {
            firstSampleFlags = firstSampleFlags2;
        }
        int[] sampleSizeTable = fragment.sampleSizeTable;
        int[] sampleCompositionTimeOffsetTable = fragment.sampleCompositionTimeOffsetTable;
        long[] sampleDecodingTimeTable = fragment.sampleDecodingTimeTable;
        boolean[] sampleIsSyncFrameTable = fragment.sampleIsSyncFrameTable;
        boolean workaroundEveryVideoFrameIsSyncFrame = track.type == 2 && (flags & 1) != 0;
        int trackRunEnd = trackRunStart + fragment.trunLength[index];
        boolean[] sampleIsSyncFrameTable2 = sampleIsSyncFrameTable;
        boolean workaroundEveryVideoFrameIsSyncFrame2 = workaroundEveryVideoFrameIsSyncFrame;
        long timescale = track.timescale;
        if (index > 0) {
            cumulativeTime = fragment.nextFragmentDecodeTime;
        } else {
            Track track2 = track;
            cumulativeTime = decodeTime;
        }
        long cumulativeTime2 = cumulativeTime;
        atomFlags = trackRunStart;
        while (atomFlags < trackRunEnd) {
            int readUnsignedIntToInt;
            int sampleDuration = sampleDurationsPresent2 ? trun.readUnsignedIntToInt() : defaultSampleValues2.duration;
            if (sampleSizesPresent2) {
                readUnsignedIntToInt = trun.readUnsignedIntToInt();
                sampleDurationsPresent = sampleDurationsPresent2;
            } else {
                sampleDurationsPresent = sampleDurationsPresent2;
                readUnsignedIntToInt = defaultSampleValues2.size;
            }
            int sampleSize = readUnsignedIntToInt;
            if (atomFlags == 0 && firstSampleFlagsPresent2) {
                firstSampleFlagsPresent = firstSampleFlagsPresent2;
                readUnsignedIntToInt = firstSampleFlags;
            } else if (sampleFlagsPresent2) {
                readUnsignedIntToInt = trun.readInt();
                firstSampleFlagsPresent = firstSampleFlagsPresent2;
            } else {
                firstSampleFlagsPresent = firstSampleFlagsPresent2;
                readUnsignedIntToInt = defaultSampleValues2.flags;
            }
            int sampleFlags = readUnsignedIntToInt;
            if (sampleCompositionTimeOffsetsPresent) {
                defaultSampleValues = defaultSampleValues2;
                sampleSizesPresent = sampleSizesPresent2;
                sampleFlagsPresent = sampleFlagsPresent2;
                sampleCompositionTimeOffsetTable[atomFlags] = (int) ((((long) trun.readInt()) * 1000) / timescale);
            } else {
                defaultSampleValues = defaultSampleValues2;
                sampleSizesPresent = sampleSizesPresent2;
                sampleFlagsPresent = sampleFlagsPresent2;
                sampleCompositionTimeOffsetTable[atomFlags] = 0;
            }
            sampleDecodingTimeTable[atomFlags] = Util.scaleLargeTimestamp(cumulativeTime2, 1000, timescale) - edtsOffset;
            sampleSizeTable[atomFlags] = sampleSize;
            boolean z = ((sampleFlags >> 16) & 1) == 0 && (!workaroundEveryVideoFrameIsSyncFrame2 || atomFlags == 0);
            sampleIsSyncFrameTable2[atomFlags] = z;
            cumulativeTime2 += (long) sampleDuration;
            atomFlags++;
            timescale = timescale;
            sampleDurationsPresent2 = sampleDurationsPresent;
            firstSampleFlagsPresent2 = firstSampleFlagsPresent;
            defaultSampleValues2 = defaultSampleValues;
            sampleSizesPresent2 = sampleSizesPresent;
            sampleFlagsPresent2 = sampleFlagsPresent;
        }
        defaultSampleValues = defaultSampleValues2;
        firstSampleFlagsPresent = firstSampleFlagsPresent2;
        sampleDurationsPresent = sampleDurationsPresent2;
        sampleSizesPresent = sampleSizesPresent2;
        sampleFlagsPresent = sampleFlagsPresent2;
        fragment.nextFragmentDecodeTime = cumulativeTime2;
        return trackRunEnd;
    }

    private static void parseUuid(ParsableByteArray uuid, TrackFragment out, byte[] extendedTypeScratch) throws ParserException {
        uuid.setPosition(8);
        uuid.readBytes(extendedTypeScratch, 0, 16);
        if (Arrays.equals(extendedTypeScratch, PIFF_SAMPLE_ENCRYPTION_BOX_EXTENDED_TYPE)) {
            parseSenc(uuid, 16, out);
        }
    }

    private static void parseSenc(ParsableByteArray senc, TrackFragment out) throws ParserException {
        parseSenc(senc, 0, out);
    }

    private static void parseSenc(ParsableByteArray senc, int offset, TrackFragment out) throws ParserException {
        senc.setPosition(offset + 8);
        int flags = Atom.parseFullAtomFlags(senc.readInt());
        if ((flags & 1) == 0) {
            boolean subsampleEncryption = (flags & 2) != 0;
            int sampleCount = senc.readUnsignedIntToInt();
            if (sampleCount == out.sampleCount) {
                Arrays.fill(out.sampleHasSubsampleEncryptionTable, 0, sampleCount, subsampleEncryption);
                out.initEncryptionData(senc.bytesLeft());
                out.fillEncryptionData(senc);
                return;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Length mismatch: ");
            stringBuilder.append(sampleCount);
            stringBuilder.append(", ");
            stringBuilder.append(out.sampleCount);
            throw new ParserException(stringBuilder.toString());
        }
        throw new ParserException("Overriding TrackEncryptionBox parameters is unsupported.");
    }

    private static void parseSgpd(ParsableByteArray sbgp, ParsableByteArray sgpd, String schemeType, TrackFragment out) throws ParserException {
        ParsableByteArray parsableByteArray = sbgp;
        ParsableByteArray parsableByteArray2 = sgpd;
        TrackFragment trackFragment = out;
        parsableByteArray.setPosition(8);
        int sbgpFullAtom = sbgp.readInt();
        if (sbgp.readInt() == SAMPLE_GROUP_TYPE_seig) {
            if (Atom.parseFullAtomVersion(sbgpFullAtom) == 1) {
                parsableByteArray.skipBytes(4);
            }
            if (sbgp.readInt() == 1) {
                parsableByteArray2.setPosition(8);
                int sgpdFullAtom = sgpd.readInt();
                if (sgpd.readInt() == SAMPLE_GROUP_TYPE_seig) {
                    int sgpdVersion = Atom.parseFullAtomVersion(sgpdFullAtom);
                    if (sgpdVersion == 1) {
                        if (sgpd.readUnsignedInt() == 0) {
                            throw new ParserException("Variable length description in sgpd found (unsupported)");
                        }
                    } else if (sgpdVersion >= 2) {
                        parsableByteArray2.skipBytes(4);
                    }
                    if (sgpd.readUnsignedInt() == 1) {
                        parsableByteArray2.skipBytes(1);
                        int patternByte = sgpd.readUnsignedByte();
                        int cryptByteBlock = (patternByte & PsExtractor.VIDEO_STREAM_MASK) >> 4;
                        int skipByteBlock = patternByte & 15;
                        boolean isProtected = sgpd.readUnsignedByte() == 1;
                        if (isProtected) {
                            byte[] constantIv;
                            int perSampleIvSize = sgpd.readUnsignedByte();
                            byte[] keyId = new byte[16];
                            parsableByteArray2.readBytes(keyId, 0, keyId.length);
                            if (isProtected && perSampleIvSize == 0) {
                                int constantIvSize = sgpd.readUnsignedByte();
                                byte[] constantIv2 = new byte[constantIvSize];
                                parsableByteArray2.readBytes(constantIv2, 0, constantIvSize);
                                constantIv = constantIv2;
                            } else {
                                constantIv = null;
                            }
                            trackFragment.definesEncryptionData = true;
                            trackFragment.trackEncryptionBox = new TrackEncryptionBox(isProtected, schemeType, perSampleIvSize, keyId, cryptByteBlock, skipByteBlock, constantIv);
                            return;
                        }
                        return;
                    }
                    throw new ParserException("Entry count in sgpd != 1 (unsupported).");
                }
                return;
            }
            throw new ParserException("Entry count in sbgp != 1 (unsupported).");
        }
    }

    private static Pair<Long, ChunkIndex> parseSidx(ParsableByteArray atom, long inputPosition) throws ParserException {
        long offset;
        long earliestPresentationTime;
        int version;
        int referenceCount;
        long earliestPresentationTime2;
        ParsableByteArray parsableByteArray = atom;
        parsableByteArray.setPosition(8);
        int fullAtom = atom.readInt();
        int version2 = Atom.parseFullAtomVersion(fullAtom);
        parsableByteArray.skipBytes(4);
        long timescale = atom.readUnsignedInt();
        long offset2 = inputPosition;
        if (version2 == 0) {
            offset = offset2 + atom.readUnsignedInt();
            earliestPresentationTime = atom.readUnsignedInt();
        } else {
            offset = offset2 + atom.readUnsignedLongToLong();
            earliestPresentationTime = atom.readUnsignedLongToLong();
        }
        long earliestPresentationTimeUs = Util.scaleLargeTimestamp(earliestPresentationTime, 1000000, timescale);
        parsableByteArray.skipBytes(2);
        int referenceCount2 = atom.readUnsignedShort();
        int[] sizes = new int[referenceCount2];
        long[] offsets = new long[referenceCount2];
        long[] durationsUs = new long[referenceCount2];
        long[] timesUs = new long[referenceCount2];
        long timeUs = earliestPresentationTimeUs;
        long time = earliestPresentationTime;
        long offset3 = offset;
        int i = 0;
        while (i < referenceCount2) {
            int firstInt = atom.readInt();
            long[] timesUs2;
            if ((firstInt & Integer.MIN_VALUE) == 0) {
                long referenceDuration = atom.readUnsignedInt();
                sizes[i] = Integer.MAX_VALUE & firstInt;
                offsets[i] = offset3;
                timesUs[i] = timeUs;
                time += referenceDuration;
                timesUs2 = timesUs;
                int fullAtom2 = fullAtom;
                version = version2;
                fullAtom = offsets;
                long[] durationsUs2 = durationsUs;
                referenceCount = referenceCount2;
                earliestPresentationTime2 = earliestPresentationTime;
                int[] sizes2 = sizes;
                timeUs = Util.scaleLargeTimestamp(time, 1000000, timescale);
                durationsUs2[i] = timeUs - timesUs2[i];
                parsableByteArray.skipBytes(4);
                offset3 += (long) sizes2[i];
                i++;
                offsets = fullAtom;
                durationsUs = durationsUs2;
                timesUs = timesUs2;
                sizes = sizes2;
                referenceCount2 = referenceCount;
                fullAtom = fullAtom2;
                version2 = version;
                earliestPresentationTime = earliestPresentationTime2;
            } else {
                version = version2;
                timesUs2 = timesUs;
                fullAtom = offsets;
                version2 = durationsUs;
                referenceCount = referenceCount2;
                earliestPresentationTime2 = earliestPresentationTime;
                earliestPresentationTime = sizes;
                throw new ParserException("Unhandled indirect reference");
            }
        }
        version = version2;
        referenceCount = referenceCount2;
        earliestPresentationTime2 = earliestPresentationTime;
        return Pair.create(Long.valueOf(earliestPresentationTimeUs), new ChunkIndex(sizes, offsets, durationsUs, timesUs));
    }

    private boolean readSample(ExtractorInput input) throws IOException, InterruptedException {
        int bytesToSkip;
        long sampleTimeUs;
        ExtractorInput extractorInput = input;
        int i = 4;
        int i2 = 1;
        int i3 = 0;
        if (this.parserState == 3) {
            if (r0.currentTrackBundle == null) {
                TrackBundle currentTrackBundle = getNextFragmentRun(r0.trackBundles);
                if (currentTrackBundle == null) {
                    bytesToSkip = (int) (r0.endOfMdatPosition - input.getPosition());
                    if (bytesToSkip >= 0) {
                        extractorInput.skipFully(bytesToSkip);
                        enterReadingAtomHeaderState();
                        return false;
                    }
                    throw new ParserException("Offset to end of mdat was negative.");
                }
                int bytesToSkip2 = (int) (currentTrackBundle.fragment.trunDataPosition[currentTrackBundle.currentTrackRunIndex] - input.getPosition());
                if (bytesToSkip2 < 0) {
                    Log.m10w(TAG, "Ignoring negative offset to sample data.");
                    bytesToSkip2 = 0;
                }
                extractorInput.skipFully(bytesToSkip2);
                r0.currentTrackBundle = currentTrackBundle;
            }
            r0.sampleSize = r0.currentTrackBundle.fragment.sampleSizeTable[r0.currentTrackBundle.currentSampleIndex];
            if (r0.currentTrackBundle.currentSampleIndex < r0.currentTrackBundle.firstSampleToOutputIndex) {
                extractorInput.skipFully(r0.sampleSize);
                r0.currentTrackBundle.skipSampleEncryptionData();
                if (!r0.currentTrackBundle.next()) {
                    r0.currentTrackBundle = null;
                }
                r0.parserState = 3;
                return true;
            }
            if (r0.currentTrackBundle.track.sampleTransformation == 1) {
                r0.sampleSize -= 8;
                extractorInput.skipFully(8);
            }
            r0.sampleBytesWritten = r0.currentTrackBundle.outputSampleEncryptionData();
            r0.sampleSize += r0.sampleBytesWritten;
            r0.parserState = 4;
            r0.sampleCurrentNalBytesRemaining = 0;
        }
        TrackFragment fragment = r0.currentTrackBundle.fragment;
        Track track = r0.currentTrackBundle.track;
        TrackOutput output = r0.currentTrackBundle.output;
        int sampleIndex = r0.currentTrackBundle.currentSampleIndex;
        long sampleTimeUs2 = fragment.getSamplePresentationTime(sampleIndex) * 1000;
        TimestampAdjuster timestampAdjuster = r0.timestampAdjuster;
        if (timestampAdjuster != null) {
            sampleTimeUs = timestampAdjuster.adjustSampleTimestamp(sampleTimeUs2);
        } else {
            sampleTimeUs = sampleTimeUs2;
        }
        int i4;
        if (track.nalUnitLengthFieldLength == 0) {
            while (true) {
                bytesToSkip = r0.sampleBytesWritten;
                i4 = r0.sampleSize;
                if (bytesToSkip >= i4) {
                    break;
                }
                r0.sampleBytesWritten += output.sampleData(extractorInput, i4 - bytesToSkip, false);
            }
        } else {
            byte[] nalPrefixData = r0.nalPrefix.data;
            nalPrefixData[0] = (byte) 0;
            nalPrefixData[1] = (byte) 0;
            nalPrefixData[2] = (byte) 0;
            int nalUnitPrefixLength = track.nalUnitLengthFieldLength + 1;
            int nalUnitLengthFieldLengthDiff = 4 - track.nalUnitLengthFieldLength;
            while (r0.sampleBytesWritten < r0.sampleSize) {
                bytesToSkip = r0.sampleCurrentNalBytesRemaining;
                if (bytesToSkip == 0) {
                    boolean z;
                    extractorInput.readFully(nalPrefixData, nalUnitLengthFieldLengthDiff, nalUnitPrefixLength);
                    r0.nalPrefix.setPosition(i3);
                    r0.sampleCurrentNalBytesRemaining = r0.nalPrefix.readUnsignedIntToInt() - i2;
                    r0.nalStartCode.setPosition(i3);
                    output.sampleData(r0.nalStartCode, i);
                    output.sampleData(r0.nalPrefix, i2);
                    if (r0.cea608TrackOutputs.length > 0) {
                        if (NalUnitUtil.isNalUnitSei(track.format.sampleMimeType, nalPrefixData[i])) {
                            z = true;
                            r0.processSeiNalUnitPayload = z;
                            r0.sampleBytesWritten += 5;
                            r0.sampleSize += nalUnitLengthFieldLengthDiff;
                        }
                    }
                    z = false;
                    r0.processSeiNalUnitPayload = z;
                    r0.sampleBytesWritten += 5;
                    r0.sampleSize += nalUnitLengthFieldLengthDiff;
                } else {
                    if (r0.processSeiNalUnitPayload) {
                        r0.nalBuffer.reset(bytesToSkip);
                        extractorInput.readFully(r0.nalBuffer.data, i3, r0.sampleCurrentNalBytesRemaining);
                        output.sampleData(r0.nalBuffer, r0.sampleCurrentNalBytesRemaining);
                        bytesToSkip = r0.sampleCurrentNalBytesRemaining;
                        i4 = NalUnitUtil.unescapeStream(r0.nalBuffer.data, r0.nalBuffer.limit());
                        r0.nalBuffer.setPosition(MimeTypes.VIDEO_H265.equals(track.format.sampleMimeType));
                        r0.nalBuffer.setLimit(i4);
                        CeaUtil.consume(sampleTimeUs, r0.nalBuffer, r0.cea608TrackOutputs);
                    } else {
                        bytesToSkip = output.sampleData(extractorInput, bytesToSkip, false);
                    }
                    r0.sampleBytesWritten += bytesToSkip;
                    r0.sampleCurrentNalBytesRemaining -= bytesToSkip;
                    i = 4;
                    i2 = 1;
                    i3 = 0;
                }
            }
        }
        bytesToSkip = fragment.sampleIsSyncFrameTable[sampleIndex];
        CryptoData cryptoData = null;
        TrackEncryptionBox encryptionBox = r0.currentTrackBundle.getEncryptionBoxIfEncrypted();
        if (encryptionBox != null) {
            bytesToSkip |= 1073741824;
            cryptoData = encryptionBox.cryptoData;
        }
        long sampleTimeUs3 = sampleTimeUs;
        output.sampleMetadata(sampleTimeUs, bytesToSkip, r0.sampleSize, 0, cryptoData);
        outputPendingMetadataSamples(sampleTimeUs3);
        if (!r0.currentTrackBundle.next()) {
            r0.currentTrackBundle = null;
        }
        r0.parserState = 3;
        return true;
    }

    private void outputPendingMetadataSamples(long sampleTimeUs) {
        FragmentedMp4Extractor fragmentedMp4Extractor = this;
        while (!fragmentedMp4Extractor.pendingMetadataSampleInfos.isEmpty()) {
            MetadataSampleInfo sampleInfo = (MetadataSampleInfo) fragmentedMp4Extractor.pendingMetadataSampleInfos.removeFirst();
            fragmentedMp4Extractor.pendingMetadataSampleBytes -= sampleInfo.size;
            long metadataTimeUs = sampleTimeUs + sampleInfo.presentationTimeDeltaUs;
            TimestampAdjuster timestampAdjuster = fragmentedMp4Extractor.timestampAdjuster;
            if (timestampAdjuster != null) {
                metadataTimeUs = timestampAdjuster.adjustSampleTimestamp(metadataTimeUs);
            }
            for (TrackOutput emsgTrackOutput : fragmentedMp4Extractor.emsgTrackOutputs) {
                emsgTrackOutput.sampleMetadata(metadataTimeUs, 1, sampleInfo.size, fragmentedMp4Extractor.pendingMetadataSampleBytes, null);
            }
        }
    }

    private static TrackBundle getNextFragmentRun(SparseArray<TrackBundle> trackBundles) {
        TrackBundle nextTrackBundle = null;
        long nextTrackRunOffset = Long.MAX_VALUE;
        int trackBundlesSize = trackBundles.size();
        for (int i = 0; i < trackBundlesSize; i++) {
            TrackBundle trackBundle = (TrackBundle) trackBundles.valueAt(i);
            if (trackBundle.currentTrackRunIndex != trackBundle.fragment.trunCount) {
                long trunOffset = trackBundle.fragment.trunDataPosition[trackBundle.currentTrackRunIndex];
                if (trunOffset < nextTrackRunOffset) {
                    nextTrackBundle = trackBundle;
                    nextTrackRunOffset = trunOffset;
                }
            }
        }
        return nextTrackBundle;
    }

    private static DrmInitData getDrmInitDataFromAtoms(List<LeafAtom> leafChildren) {
        List schemeDatas = null;
        int leafChildrenSize = leafChildren.size();
        for (int i = 0; i < leafChildrenSize; i++) {
            LeafAtom child = (LeafAtom) leafChildren.get(i);
            if (child.type == Atom.TYPE_pssh) {
                if (schemeDatas == null) {
                    schemeDatas = new ArrayList();
                }
                byte[] psshData = child.data.data;
                UUID uuid = PsshAtomUtil.parseUuid(psshData);
                if (uuid == null) {
                    Log.m10w(TAG, "Skipped pssh atom (failed to extract uuid)");
                } else {
                    schemeDatas.add(new SchemeData(uuid, MimeTypes.VIDEO_MP4, psshData));
                }
            }
        }
        return schemeDatas == null ? null : new DrmInitData(schemeDatas);
    }

    private static boolean shouldParseLeafAtom(int atom) {
        if (!(atom == Atom.TYPE_hdlr || atom == Atom.TYPE_mdhd || atom == Atom.TYPE_mvhd || atom == Atom.TYPE_sidx || atom == Atom.TYPE_stsd || atom == Atom.TYPE_tfdt || atom == Atom.TYPE_tfhd || atom == Atom.TYPE_tkhd || atom == Atom.TYPE_trex || atom == Atom.TYPE_trun || atom == Atom.TYPE_pssh || atom == Atom.TYPE_saiz || atom == Atom.TYPE_saio || atom == Atom.TYPE_senc || atom == Atom.TYPE_uuid || atom == Atom.TYPE_sbgp || atom == Atom.TYPE_sgpd || atom == Atom.TYPE_elst || atom == Atom.TYPE_mehd)) {
            if (atom != Atom.TYPE_emsg) {
                return false;
            }
        }
        return true;
    }

    private static boolean shouldParseContainerAtom(int atom) {
        if (!(atom == Atom.TYPE_moov || atom == Atom.TYPE_trak || atom == Atom.TYPE_mdia || atom == Atom.TYPE_minf || atom == Atom.TYPE_stbl || atom == Atom.TYPE_moof || atom == Atom.TYPE_traf || atom == Atom.TYPE_mvex)) {
            if (atom != Atom.TYPE_edts) {
                return false;
            }
        }
        return true;
    }
}
