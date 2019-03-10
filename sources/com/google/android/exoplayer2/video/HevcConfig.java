package com.google.android.exoplayer2.video;

import android.support.annotation.Nullable;
import java.util.List;

public final class HevcConfig {
    @Nullable
    public final List<byte[]> initializationData;
    public final int nalUnitLengthFieldLength;

    public static com.google.android.exoplayer2.video.HevcConfig parse(com.google.android.exoplayer2.util.ParsableByteArray r14) throws com.google.android.exoplayer2.ParserException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:23:0x0082 in {7, 8, 13, 14, 16, 17, 19, 22} preds:[]
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
        r0 = 21;
        r14.skipBytes(r0);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
        r0 = r14.readUnsignedByte();	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
        r0 = r0 & 3;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
        r1 = r14.readUnsignedByte();	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
        r2 = 0;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
        r3 = r14.getPosition();	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
        r4 = 0;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
    L_0x0015:
        r5 = 1;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
        if (r4 >= r1) goto L_0x0032;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
    L_0x0018:
        r14.skipBytes(r5);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
        r5 = r14.readUnsignedShort();	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
        r6 = 0;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
    L_0x0020:
        if (r6 >= r5) goto L_0x002f;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
    L_0x0022:
        r7 = r14.readUnsignedShort();	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
        r8 = r7 + 4;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
        r2 = r2 + r8;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
        r14.skipBytes(r7);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
        r6 = r6 + 1;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
        goto L_0x0020;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
    L_0x002f:
        r4 = r4 + 1;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
        goto L_0x0015;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
    L_0x0032:
        r14.setPosition(r3);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
        r4 = new byte[r2];	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
        r6 = 0;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
        r7 = 0;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
    L_0x0039:
        if (r7 >= r1) goto L_0x0069;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
    L_0x003b:
        r14.skipBytes(r5);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
        r8 = r14.readUnsignedShort();	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
        r9 = 0;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
    L_0x0043:
        if (r9 >= r8) goto L_0x0066;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
    L_0x0045:
        r10 = r14.readUnsignedShort();	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
        r11 = com.google.android.exoplayer2.util.NalUnitUtil.NAL_START_CODE;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
        r12 = 0;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
        r13 = com.google.android.exoplayer2.util.NalUnitUtil.NAL_START_CODE;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
        r13 = r13.length;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
        java.lang.System.arraycopy(r11, r12, r4, r6, r13);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
        r11 = com.google.android.exoplayer2.util.NalUnitUtil.NAL_START_CODE;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
        r11 = r11.length;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
        r6 = r6 + r11;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
        r11 = r14.data;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
        r12 = r14.getPosition();	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
        java.lang.System.arraycopy(r11, r12, r4, r6, r10);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
        r6 = r6 + r10;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
        r14.skipBytes(r10);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
        r9 = r9 + 1;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
        goto L_0x0043;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
    L_0x0066:
        r7 = r7 + 1;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
        goto L_0x0039;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
    L_0x0069:
        if (r2 != 0) goto L_0x006d;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
    L_0x006b:
        r5 = 0;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
        goto L_0x0071;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
    L_0x006d:
        r5 = java.util.Collections.singletonList(r4);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
    L_0x0071:
        r7 = new com.google.android.exoplayer2.video.HevcConfig;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
        r8 = r0 + 1;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
        r7.<init>(r5, r8);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0079 }
        return r7;
    L_0x0079:
        r0 = move-exception;
        r1 = new com.google.android.exoplayer2.ParserException;
        r2 = "Error parsing HEVC config";
        r1.<init>(r2, r0);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.video.HevcConfig.parse(com.google.android.exoplayer2.util.ParsableByteArray):com.google.android.exoplayer2.video.HevcConfig");
    }

    private HevcConfig(@Nullable List<byte[]> initializationData, int nalUnitLengthFieldLength) {
        this.initializationData = initializationData;
        this.nalUnitLengthFieldLength = nalUnitLengthFieldLength;
    }
}
