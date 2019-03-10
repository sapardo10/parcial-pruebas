package com.google.android.exoplayer2.extractor.ogg;

import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;

final class OggPageHeader {
    public static final int EMPTY_PAGE_HEADER_SIZE = 27;
    public static final int MAX_PAGE_PAYLOAD = 65025;
    public static final int MAX_PAGE_SIZE = 65307;
    public static final int MAX_SEGMENT_COUNT = 255;
    private static final int TYPE_OGGS = Util.getIntegerCodeForString("OggS");
    public int bodySize;
    public long granulePosition;
    public int headerSize;
    public final int[] laces = new int[255];
    public long pageChecksum;
    public int pageSegmentCount;
    public long pageSequenceNumber;
    public int revision;
    private final ParsableByteArray scratch = new ParsableByteArray(255);
    public long streamSerialNumber;
    public int type;

    public boolean populate(com.google.android.exoplayer2.extractor.ExtractorInput r10, boolean r11) throws java.io.IOException, java.lang.InterruptedException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:34:0x00d1 in {4, 5, 6, 7, 11, 15, 17, 21, 23, 27, 28, 31, 33} preds:[]
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
        r0 = r9.scratch;
        r0.reset();
        r9.reset();
        r0 = r10.getLength();
        r2 = 1;
        r3 = 0;
        r4 = -1;
        r6 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r6 == 0) goto L_0x0026;
    L_0x0014:
        r0 = r10.getLength();
        r4 = r10.getPeekPosition();
        r0 = r0 - r4;
        r4 = 27;
        r6 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r6 < 0) goto L_0x0024;
    L_0x0023:
        goto L_0x0027;
    L_0x0024:
        r0 = 0;
        goto L_0x0028;
    L_0x0027:
        r0 = 1;
    L_0x0028:
        if (r0 == 0) goto L_0x00c7;
    L_0x002a:
        r1 = r9.scratch;
        r1 = r1.data;
        r4 = 27;
        r1 = r10.peekFully(r1, r3, r4, r2);
        if (r1 != 0) goto L_0x0038;
    L_0x0036:
        goto L_0x00c7;
    L_0x0038:
        r1 = r9.scratch;
        r5 = r1.readUnsignedInt();
        r1 = TYPE_OGGS;
        r7 = (long) r1;
        r1 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));
        if (r1 == 0) goto L_0x0050;
    L_0x0045:
        if (r11 == 0) goto L_0x0048;
    L_0x0047:
        return r3;
    L_0x0048:
        r1 = new com.google.android.exoplayer2.ParserException;
        r2 = "expected OggS capture pattern at begin of page";
        r1.<init>(r2);
        throw r1;
    L_0x0050:
        r1 = r9.scratch;
        r1 = r1.readUnsignedByte();
        r9.revision = r1;
        r1 = r9.revision;
        if (r1 == 0) goto L_0x0068;
    L_0x005c:
        if (r11 == 0) goto L_0x005f;
    L_0x005e:
        return r3;
    L_0x005f:
        r1 = new com.google.android.exoplayer2.ParserException;
        r2 = "unsupported bit stream revision";
        r1.<init>(r2);
        throw r1;
    L_0x0068:
        r1 = r9.scratch;
        r1 = r1.readUnsignedByte();
        r9.type = r1;
        r1 = r9.scratch;
        r5 = r1.readLittleEndianLong();
        r9.granulePosition = r5;
        r1 = r9.scratch;
        r5 = r1.readLittleEndianUnsignedInt();
        r9.streamSerialNumber = r5;
        r1 = r9.scratch;
        r5 = r1.readLittleEndianUnsignedInt();
        r9.pageSequenceNumber = r5;
        r1 = r9.scratch;
        r5 = r1.readLittleEndianUnsignedInt();
        r9.pageChecksum = r5;
        r1 = r9.scratch;
        r1 = r1.readUnsignedByte();
        r9.pageSegmentCount = r1;
        r1 = r9.pageSegmentCount;
        r1 = r1 + r4;
        r9.headerSize = r1;
        r1 = r9.scratch;
        r1.reset();
        r1 = r9.scratch;
        r1 = r1.data;
        r4 = r9.pageSegmentCount;
        r10.peekFully(r1, r3, r4);
        r1 = 0;
    L_0x00ac:
        r3 = r9.pageSegmentCount;
        if (r1 >= r3) goto L_0x00c6;
    L_0x00b0:
        r3 = r9.laces;
        r4 = r9.scratch;
        r4 = r4.readUnsignedByte();
        r3[r1] = r4;
        r3 = r9.bodySize;
        r4 = r9.laces;
        r4 = r4[r1];
        r3 = r3 + r4;
        r9.bodySize = r3;
        r1 = r1 + 1;
        goto L_0x00ac;
    L_0x00c6:
        return r2;
        if (r11 == 0) goto L_0x00cb;
    L_0x00ca:
        return r3;
    L_0x00cb:
        r1 = new java.io.EOFException;
        r1.<init>();
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.ogg.OggPageHeader.populate(com.google.android.exoplayer2.extractor.ExtractorInput, boolean):boolean");
    }

    OggPageHeader() {
    }

    public void reset() {
        this.revision = 0;
        this.type = 0;
        this.granulePosition = 0;
        this.streamSerialNumber = 0;
        this.pageSequenceNumber = 0;
        this.pageChecksum = 0;
        this.pageSegmentCount = 0;
        this.headerSize = 0;
        this.bodySize = 0;
    }
}
