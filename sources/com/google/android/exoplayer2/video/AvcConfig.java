package com.google.android.exoplayer2.video;

import com.google.android.exoplayer2.util.CodecSpecificDataUtil;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.util.List;

public final class AvcConfig {
    public final int height;
    public final List<byte[]> initializationData;
    public final int nalUnitLengthFieldLength;
    public final float pixelWidthAspectRatio;
    public final int width;

    public static com.google.android.exoplayer2.video.AvcConfig parse(com.google.android.exoplayer2.util.ParsableByteArray r14) throws com.google.android.exoplayer2.ParserException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:21:0x007b in {6, 9, 12, 13, 15, 17, 20} preds:[]
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
        r0 = 4;
        r14.skipBytes(r0);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r0 = r14.readUnsignedByte();	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r1 = 3;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r0 = r0 & r1;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r0 = r0 + 1;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        if (r0 == r1) goto L_0x006c;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
    L_0x000e:
        r1 = new java.util.ArrayList;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r1.<init>();	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r2 = r14.readUnsignedByte();	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r8 = r2 & 31;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r2 = 0;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
    L_0x001a:
        if (r2 >= r8) goto L_0x0026;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
    L_0x001c:
        r3 = buildNalUnitForChild(r14);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r1.add(r3);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r2 = r2 + 1;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        goto L_0x001a;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
    L_0x0026:
        r2 = r14.readUnsignedByte();	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r9 = r2;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r2 = 0;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
    L_0x002c:
        if (r2 >= r9) goto L_0x0038;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
    L_0x002e:
        r3 = buildNalUnitForChild(r14);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r1.add(r3);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r2 = r2 + 1;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        goto L_0x002c;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
    L_0x0038:
        r2 = -1;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r3 = -1;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r4 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        if (r8 <= 0) goto L_0x005d;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
    L_0x003e:
        r5 = 0;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r6 = r1.get(r5);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r6 = (byte[]) r6;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r5 = r1.get(r5);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r5 = (byte[]) r5;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r7 = r6.length;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r5 = com.google.android.exoplayer2.util.NalUnitUtil.parseSpsNalUnit(r5, r0, r7);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r7 = r5.width;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r2 = r7;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r7 = r5.height;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r3 = r7;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r7 = r5.pixelWidthAspectRatio;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r4 = r7;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r10 = r2;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r11 = r3;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r12 = r4;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        goto L_0x0060;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
    L_0x005d:
        r10 = r2;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r11 = r3;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r12 = r4;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
    L_0x0060:
        r13 = new com.google.android.exoplayer2.video.AvcConfig;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r2 = r13;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r3 = r1;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r4 = r0;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r5 = r10;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r6 = r11;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r7 = r12;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r2.<init>(r3, r4, r5, r6, r7);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        return r13;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
    L_0x006c:
        r1 = new java.lang.IllegalStateException;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        r1.<init>();	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
        throw r1;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0072 }
    L_0x0072:
        r0 = move-exception;
        r1 = new com.google.android.exoplayer2.ParserException;
        r2 = "Error parsing AVC config";
        r1.<init>(r2, r0);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.video.AvcConfig.parse(com.google.android.exoplayer2.util.ParsableByteArray):com.google.android.exoplayer2.video.AvcConfig");
    }

    private AvcConfig(List<byte[]> initializationData, int nalUnitLengthFieldLength, int width, int height, float pixelWidthAspectRatio) {
        this.initializationData = initializationData;
        this.nalUnitLengthFieldLength = nalUnitLengthFieldLength;
        this.width = width;
        this.height = height;
        this.pixelWidthAspectRatio = pixelWidthAspectRatio;
    }

    private static byte[] buildNalUnitForChild(ParsableByteArray data) {
        int length = data.readUnsignedShort();
        int offset = data.getPosition();
        data.skipBytes(length);
        return CodecSpecificDataUtil.buildNalUnit(data.data, offset, length);
    }
}
