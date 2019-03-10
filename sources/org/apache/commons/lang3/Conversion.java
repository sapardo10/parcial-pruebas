package org.apache.commons.lang3;

import java.util.UUID;

public class Conversion {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final boolean[] FFFF = new boolean[]{false, false, false, false};
    private static final boolean[] FFFT = new boolean[]{false, false, false, true};
    private static final boolean[] FFTF = new boolean[]{false, false, true, false};
    private static final boolean[] FFTT = new boolean[]{false, false, true, true};
    private static final boolean[] FTFF = new boolean[]{false, true, false, false};
    private static final boolean[] FTFT = new boolean[]{false, true, false, true};
    private static final boolean[] FTTF = new boolean[]{false, true, true, false};
    private static final boolean[] FTTT = new boolean[]{false, true, true, true};
    private static final boolean[] TFFF = new boolean[]{true, false, false, false};
    private static final boolean[] TFFT = new boolean[]{true, false, false, true};
    private static final boolean[] TFTF = new boolean[]{true, false, true, false};
    private static final boolean[] TFTT = new boolean[]{true, false, true, true};
    private static final boolean[] TTFF = new boolean[]{true, true, false, false};
    private static final boolean[] TTFT = new boolean[]{true, true, false, true};
    private static final boolean[] TTTF = new boolean[]{true, true, true, false};
    private static final boolean[] TTTT = new boolean[]{true, true, true, true};

    public static byte binaryToByte(boolean[] r6, int r7, byte r8, int r9, int r10) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:13:0x002d in {2, 4, 9, 10, 12} preds:[]
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
        r0 = r6.length;
        if (r0 != 0) goto L_0x0005;
    L_0x0003:
        if (r7 == 0) goto L_0x0007;
    L_0x0005:
        if (r10 != 0) goto L_0x0008;
    L_0x0007:
        return r8;
    L_0x0008:
        r0 = r10 + -1;
        r0 = r0 + r9;
        r1 = 8;
        if (r0 >= r1) goto L_0x0025;
    L_0x000f:
        r0 = r8;
        r1 = 0;
    L_0x0011:
        if (r1 >= r10) goto L_0x0024;
    L_0x0013:
        r2 = r1 + r9;
        r3 = r1 + r7;
        r3 = r6[r3];
        r3 = r3 << r2;
        r4 = 1;
        r4 = r4 << r2;
        r5 = r4 ^ -1;
        r5 = r5 & r0;
        r5 = r5 | r3;
        r0 = (byte) r5;
        r1 = r1 + 1;
        goto L_0x0011;
    L_0x0024:
        return r0;
    L_0x0025:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "nBools-1+dstPos is greater or equal to than 8";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.Conversion.binaryToByte(boolean[], int, byte, int, int):byte");
    }

    public static int binaryToInt(boolean[] r6, int r7, int r8, int r9, int r10) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:13:0x002d in {2, 4, 9, 10, 12} preds:[]
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
        r0 = r6.length;
        if (r0 != 0) goto L_0x0005;
    L_0x0003:
        if (r7 == 0) goto L_0x0007;
    L_0x0005:
        if (r10 != 0) goto L_0x0008;
    L_0x0007:
        return r8;
    L_0x0008:
        r0 = r10 + -1;
        r0 = r0 + r9;
        r1 = 32;
        if (r0 >= r1) goto L_0x0025;
    L_0x000f:
        r0 = r8;
        r1 = 0;
    L_0x0011:
        if (r1 >= r10) goto L_0x0024;
    L_0x0013:
        r2 = r1 + r9;
        r3 = r1 + r7;
        r3 = r6[r3];
        r3 = r3 << r2;
        r4 = 1;
        r4 = r4 << r2;
        r5 = r4 ^ -1;
        r5 = r5 & r0;
        r0 = r5 | r3;
        r1 = r1 + 1;
        goto L_0x0011;
    L_0x0024:
        return r0;
    L_0x0025:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "nBools-1+dstPos is greater or equal to than 32";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.Conversion.binaryToInt(boolean[], int, int, int, int):int");
    }

    public static long binaryToLong(boolean[] r13, int r14, long r15, int r17, int r18) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:17:0x003a in {2, 4, 11, 12, 13, 14, 16} preds:[]
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
        r0 = r13;
        r1 = r18;
        r2 = r0.length;
        if (r2 != 0) goto L_0x0008;
    L_0x0006:
        if (r14 == 0) goto L_0x000a;
    L_0x0008:
        if (r1 != 0) goto L_0x000b;
    L_0x000a:
        return r15;
    L_0x000b:
        r2 = r1 + -1;
        r2 = r2 + r17;
        r3 = 64;
        if (r2 >= r3) goto L_0x0032;
    L_0x0013:
        r2 = r15;
        r4 = 0;
    L_0x0015:
        if (r4 >= r1) goto L_0x0031;
    L_0x0017:
        r5 = r4 + r17;
        r6 = r4 + r14;
        r6 = r0[r6];
        r7 = 1;
        if (r6 == 0) goto L_0x0023;
    L_0x0021:
        r9 = r7;
        goto L_0x0025;
    L_0x0023:
        r9 = 0;
    L_0x0025:
        r9 = r9 << r5;
        r6 = r7 << r5;
        r11 = -1;
        r11 = r11 ^ r6;
        r11 = r11 & r2;
        r2 = r11 | r9;
        r4 = r4 + 1;
        goto L_0x0015;
    L_0x0031:
        return r2;
    L_0x0032:
        r2 = new java.lang.IllegalArgumentException;
        r3 = "nBools-1+dstPos is greater or equal to than 64";
        r2.<init>(r3);
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.Conversion.binaryToLong(boolean[], int, long, int, int):long");
    }

    public static short binaryToShort(boolean[] r6, int r7, short r8, int r9, int r10) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:13:0x002d in {2, 4, 9, 10, 12} preds:[]
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
        r0 = r6.length;
        if (r0 != 0) goto L_0x0005;
    L_0x0003:
        if (r7 == 0) goto L_0x0007;
    L_0x0005:
        if (r10 != 0) goto L_0x0008;
    L_0x0007:
        return r8;
    L_0x0008:
        r0 = r10 + -1;
        r0 = r0 + r9;
        r1 = 16;
        if (r0 >= r1) goto L_0x0025;
    L_0x000f:
        r0 = r8;
        r1 = 0;
    L_0x0011:
        if (r1 >= r10) goto L_0x0024;
    L_0x0013:
        r2 = r1 + r9;
        r3 = r1 + r7;
        r3 = r6[r3];
        r3 = r3 << r2;
        r4 = 1;
        r4 = r4 << r2;
        r5 = r4 ^ -1;
        r5 = r5 & r0;
        r5 = r5 | r3;
        r0 = (short) r5;
        r1 = r1 + 1;
        goto L_0x0011;
    L_0x0024:
        return r0;
    L_0x0025:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "nBools-1+dstPos is greater or equal to than 16";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.Conversion.binaryToShort(boolean[], int, short, int, int):short");
    }

    public static int byteArrayToInt(byte[] r6, int r7, int r8, int r9, int r10) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:13:0x0032 in {2, 4, 9, 10, 12} preds:[]
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
        r0 = r6.length;
        if (r0 != 0) goto L_0x0005;
    L_0x0003:
        if (r7 == 0) goto L_0x0007;
    L_0x0005:
        if (r10 != 0) goto L_0x0008;
    L_0x0007:
        return r8;
    L_0x0008:
        r0 = r10 + -1;
        r0 = r0 * 8;
        r0 = r0 + r9;
        r1 = 32;
        if (r0 >= r1) goto L_0x002a;
    L_0x0011:
        r0 = r8;
        r1 = 0;
    L_0x0013:
        if (r1 >= r10) goto L_0x0029;
    L_0x0015:
        r2 = r1 * 8;
        r2 = r2 + r9;
        r3 = r1 + r7;
        r3 = r6[r3];
        r4 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        r3 = r3 & r4;
        r3 = r3 << r2;
        r4 = r4 << r2;
        r5 = r4 ^ -1;
        r5 = r5 & r0;
        r0 = r5 | r3;
        r1 = r1 + 1;
        goto L_0x0013;
    L_0x0029:
        return r0;
    L_0x002a:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "(nBytes-1)*8+dstPos is greater or equal to than 32";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.Conversion.byteArrayToInt(byte[], int, int, int, int):int");
    }

    public static long byteArrayToLong(byte[] r10, int r11, long r12, int r14, int r15) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:13:0x0034 in {2, 4, 9, 10, 12} preds:[]
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
        r0 = r10.length;
        if (r0 != 0) goto L_0x0005;
    L_0x0003:
        if (r11 == 0) goto L_0x0007;
    L_0x0005:
        if (r15 != 0) goto L_0x0008;
    L_0x0007:
        return r12;
    L_0x0008:
        r0 = r15 + -1;
        r0 = r0 * 8;
        r0 = r0 + r14;
        r1 = 64;
        if (r0 >= r1) goto L_0x002c;
    L_0x0011:
        r0 = r12;
        r2 = 0;
    L_0x0013:
        if (r2 >= r15) goto L_0x002b;
    L_0x0015:
        r3 = r2 * 8;
        r3 = r3 + r14;
        r4 = r2 + r11;
        r4 = r10[r4];
        r4 = (long) r4;
        r6 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        r4 = r4 & r6;
        r4 = r4 << r3;
        r6 = r6 << r3;
        r8 = -1;
        r8 = r8 ^ r6;
        r8 = r8 & r0;
        r0 = r8 | r4;
        r2 = r2 + 1;
        goto L_0x0013;
    L_0x002b:
        return r0;
    L_0x002c:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "(nBytes-1)*8+dstPos is greater or equal to than 64";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.Conversion.byteArrayToLong(byte[], int, long, int, int):long");
    }

    public static short byteArrayToShort(byte[] r6, int r7, short r8, int r9, int r10) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:13:0x0032 in {2, 4, 9, 10, 12} preds:[]
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
        r0 = r6.length;
        if (r0 != 0) goto L_0x0005;
    L_0x0003:
        if (r7 == 0) goto L_0x0007;
    L_0x0005:
        if (r10 != 0) goto L_0x0008;
    L_0x0007:
        return r8;
    L_0x0008:
        r0 = r10 + -1;
        r0 = r0 * 8;
        r0 = r0 + r9;
        r1 = 16;
        if (r0 >= r1) goto L_0x002a;
    L_0x0011:
        r0 = r8;
        r1 = 0;
    L_0x0013:
        if (r1 >= r10) goto L_0x0029;
    L_0x0015:
        r2 = r1 * 8;
        r2 = r2 + r9;
        r3 = r1 + r7;
        r3 = r6[r3];
        r4 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        r3 = r3 & r4;
        r3 = r3 << r2;
        r4 = r4 << r2;
        r5 = r4 ^ -1;
        r5 = r5 & r0;
        r5 = r5 | r3;
        r0 = (short) r5;
        r1 = r1 + 1;
        goto L_0x0013;
    L_0x0029:
        return r0;
    L_0x002a:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "(nBytes-1)*8+dstPos is greater or equal to than 16";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.Conversion.byteArrayToShort(byte[], int, short, int, int):short");
    }

    public static boolean[] byteToBinary(byte r5, int r6, boolean[] r7, int r8, int r9) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:14:0x0027 in {1, 8, 9, 10, 11, 13} preds:[]
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
        if (r9 != 0) goto L_0x0003;
    L_0x0002:
        return r7;
    L_0x0003:
        r0 = r9 + -1;
        r0 = r0 + r6;
        r1 = 8;
        if (r0 >= r1) goto L_0x001f;
    L_0x000a:
        r0 = 0;
    L_0x000b:
        if (r0 >= r9) goto L_0x001e;
    L_0x000d:
        r1 = r0 + r6;
        r2 = r8 + r0;
        r3 = r5 >> r1;
        r4 = 1;
        r3 = r3 & r4;
        if (r3 == 0) goto L_0x0018;
    L_0x0017:
        goto L_0x0019;
    L_0x0018:
        r4 = 0;
    L_0x0019:
        r7[r2] = r4;
        r0 = r0 + 1;
        goto L_0x000b;
    L_0x001e:
        return r7;
    L_0x001f:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "nBools-1+srcPos is greater or equal to than 8";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.Conversion.byteToBinary(byte, int, boolean[], int, int):boolean[]");
    }

    public static java.lang.String byteToHex(byte r7, int r8, java.lang.String r9, int r10, int r11) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:15:0x0046 in {1, 8, 9, 10, 12, 14} preds:[]
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
        if (r11 != 0) goto L_0x0003;
    L_0x0002:
        return r9;
    L_0x0003:
        r0 = r11 + -1;
        r0 = r0 * 4;
        r0 = r0 + r8;
        r1 = 8;
        if (r0 >= r1) goto L_0x003e;
    L_0x000c:
        r0 = new java.lang.StringBuilder;
        r0.<init>(r9);
        r1 = r0.length();
        r2 = 0;
    L_0x0016:
        if (r2 >= r11) goto L_0x0039;
    L_0x0018:
        r3 = r2 * 4;
        r3 = r3 + r8;
        r4 = r7 >> r3;
        r4 = r4 & 15;
        r5 = r10 + r2;
        if (r5 != r1) goto L_0x002d;
    L_0x0023:
        r1 = r1 + 1;
        r5 = intToHexDigit(r4);
        r0.append(r5);
        goto L_0x0036;
    L_0x002d:
        r5 = r10 + r2;
        r6 = intToHexDigit(r4);
        r0.setCharAt(r5, r6);
    L_0x0036:
        r2 = r2 + 1;
        goto L_0x0016;
    L_0x0039:
        r2 = r0.toString();
        return r2;
    L_0x003e:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "(nHexs-1)*4+srcPos is greater or equal to than 8";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.Conversion.byteToHex(byte, int, java.lang.String, int, int):java.lang.String");
    }

    public static byte hexToByte(java.lang.String r6, int r7, byte r8, int r9, int r10) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:10:0x0033 in {1, 6, 7, 9} preds:[]
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
        if (r10 != 0) goto L_0x0003;
    L_0x0002:
        return r8;
    L_0x0003:
        r0 = r10 + -1;
        r0 = r0 * 4;
        r0 = r0 + r9;
        r1 = 8;
        if (r0 >= r1) goto L_0x002b;
    L_0x000c:
        r0 = r8;
        r1 = 0;
    L_0x000e:
        if (r1 >= r10) goto L_0x002a;
    L_0x0010:
        r2 = r1 * 4;
        r2 = r2 + r9;
        r3 = r1 + r7;
        r3 = r6.charAt(r3);
        r3 = hexDigitToInt(r3);
        r4 = 15;
        r3 = r3 & r4;
        r3 = r3 << r2;
        r4 = r4 << r2;
        r5 = r4 ^ -1;
        r5 = r5 & r0;
        r5 = r5 | r3;
        r0 = (byte) r5;
        r1 = r1 + 1;
        goto L_0x000e;
    L_0x002a:
        return r0;
    L_0x002b:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "(nHexs-1)*4+dstPos is greater or equal to than 8";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.Conversion.hexToByte(java.lang.String, int, byte, int, int):byte");
    }

    public static int hexToInt(java.lang.String r6, int r7, int r8, int r9, int r10) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:10:0x0033 in {1, 6, 7, 9} preds:[]
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
        if (r10 != 0) goto L_0x0003;
    L_0x0002:
        return r8;
    L_0x0003:
        r0 = r10 + -1;
        r0 = r0 * 4;
        r0 = r0 + r9;
        r1 = 32;
        if (r0 >= r1) goto L_0x002b;
    L_0x000c:
        r0 = r8;
        r1 = 0;
    L_0x000e:
        if (r1 >= r10) goto L_0x002a;
    L_0x0010:
        r2 = r1 * 4;
        r2 = r2 + r9;
        r3 = r1 + r7;
        r3 = r6.charAt(r3);
        r3 = hexDigitToInt(r3);
        r4 = 15;
        r3 = r3 & r4;
        r3 = r3 << r2;
        r4 = r4 << r2;
        r5 = r4 ^ -1;
        r5 = r5 & r0;
        r0 = r5 | r3;
        r1 = r1 + 1;
        goto L_0x000e;
    L_0x002a:
        return r0;
    L_0x002b:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "(nHexs-1)*4+dstPos is greater or equal to than 32";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.Conversion.hexToInt(java.lang.String, int, int, int, int):int");
    }

    public static long hexToLong(java.lang.String r10, int r11, long r12, int r14, int r15) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:10:0x0035 in {1, 6, 7, 9} preds:[]
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
        if (r15 != 0) goto L_0x0003;
    L_0x0002:
        return r12;
    L_0x0003:
        r0 = r15 + -1;
        r0 = r0 * 4;
        r0 = r0 + r14;
        r1 = 64;
        if (r0 >= r1) goto L_0x002d;
    L_0x000c:
        r0 = r12;
        r2 = 0;
    L_0x000e:
        if (r2 >= r15) goto L_0x002c;
    L_0x0010:
        r3 = r2 * 4;
        r3 = r3 + r14;
        r4 = r2 + r11;
        r4 = r10.charAt(r4);
        r4 = hexDigitToInt(r4);
        r4 = (long) r4;
        r6 = 15;
        r4 = r4 & r6;
        r4 = r4 << r3;
        r6 = r6 << r3;
        r8 = -1;
        r8 = r8 ^ r6;
        r8 = r8 & r0;
        r0 = r8 | r4;
        r2 = r2 + 1;
        goto L_0x000e;
    L_0x002c:
        return r0;
    L_0x002d:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "(nHexs-1)*4+dstPos is greater or equal to than 64";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.Conversion.hexToLong(java.lang.String, int, long, int, int):long");
    }

    public static short hexToShort(java.lang.String r6, int r7, short r8, int r9, int r10) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:10:0x0033 in {1, 6, 7, 9} preds:[]
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
        if (r10 != 0) goto L_0x0003;
    L_0x0002:
        return r8;
    L_0x0003:
        r0 = r10 + -1;
        r0 = r0 * 4;
        r0 = r0 + r9;
        r1 = 16;
        if (r0 >= r1) goto L_0x002b;
    L_0x000c:
        r0 = r8;
        r1 = 0;
    L_0x000e:
        if (r1 >= r10) goto L_0x002a;
    L_0x0010:
        r2 = r1 * 4;
        r2 = r2 + r9;
        r3 = r1 + r7;
        r3 = r6.charAt(r3);
        r3 = hexDigitToInt(r3);
        r4 = 15;
        r3 = r3 & r4;
        r3 = r3 << r2;
        r4 = r4 << r2;
        r5 = r4 ^ -1;
        r5 = r5 & r0;
        r5 = r5 | r3;
        r0 = (short) r5;
        r1 = r1 + 1;
        goto L_0x000e;
    L_0x002a:
        return r0;
    L_0x002b:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "(nHexs-1)*4+dstPos is greater or equal to than 16";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.Conversion.hexToShort(java.lang.String, int, short, int, int):short");
    }

    public static long intArrayToLong(int[] r10, int r11, long r12, int r14, int r15) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:13:0x0037 in {2, 4, 9, 10, 12} preds:[]
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
        r0 = r10.length;
        if (r0 != 0) goto L_0x0005;
    L_0x0003:
        if (r11 == 0) goto L_0x0007;
    L_0x0005:
        if (r15 != 0) goto L_0x0008;
    L_0x0007:
        return r12;
    L_0x0008:
        r0 = r15 + -1;
        r0 = r0 * 32;
        r0 = r0 + r14;
        r1 = 64;
        if (r0 >= r1) goto L_0x002f;
    L_0x0011:
        r0 = r12;
        r2 = 0;
    L_0x0013:
        if (r2 >= r15) goto L_0x002e;
    L_0x0015:
        r3 = r2 * 32;
        r3 = r3 + r14;
        r4 = r2 + r11;
        r4 = r10[r4];
        r4 = (long) r4;
        r6 = 4294967295; // 0xffffffff float:NaN double:2.1219957905E-314;
        r4 = r4 & r6;
        r4 = r4 << r3;
        r6 = r6 << r3;
        r8 = -1;
        r8 = r8 ^ r6;
        r8 = r8 & r0;
        r0 = r8 | r4;
        r2 = r2 + 1;
        goto L_0x0013;
    L_0x002e:
        return r0;
    L_0x002f:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "(nInts-1)*32+dstPos is greater or equal to than 64";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.Conversion.intArrayToLong(int[], int, long, int, int):long");
    }

    public static boolean[] intToBinary(int r5, int r6, boolean[] r7, int r8, int r9) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:14:0x0027 in {1, 8, 9, 10, 11, 13} preds:[]
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
        if (r9 != 0) goto L_0x0003;
    L_0x0002:
        return r7;
    L_0x0003:
        r0 = r9 + -1;
        r0 = r0 + r6;
        r1 = 32;
        if (r0 >= r1) goto L_0x001f;
    L_0x000a:
        r0 = 0;
    L_0x000b:
        if (r0 >= r9) goto L_0x001e;
    L_0x000d:
        r1 = r0 + r6;
        r2 = r8 + r0;
        r3 = r5 >> r1;
        r4 = 1;
        r3 = r3 & r4;
        if (r3 == 0) goto L_0x0018;
    L_0x0017:
        goto L_0x0019;
    L_0x0018:
        r4 = 0;
    L_0x0019:
        r7[r2] = r4;
        r0 = r0 + 1;
        goto L_0x000b;
    L_0x001e:
        return r7;
    L_0x001f:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "nBools-1+srcPos is greater or equal to than 32";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.Conversion.intToBinary(int, int, boolean[], int, int):boolean[]");
    }

    public static byte[] intToByteArray(int r4, int r5, byte[] r6, int r7, int r8) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:10:0x0027 in {1, 6, 7, 9} preds:[]
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
        if (r8 != 0) goto L_0x0003;
    L_0x0002:
        return r6;
    L_0x0003:
        r0 = r8 + -1;
        r0 = r0 * 8;
        r0 = r0 + r5;
        r1 = 32;
        if (r0 >= r1) goto L_0x001f;
    L_0x000c:
        r0 = 0;
    L_0x000d:
        if (r0 >= r8) goto L_0x001e;
    L_0x000f:
        r1 = r0 * 8;
        r1 = r1 + r5;
        r2 = r7 + r0;
        r3 = r4 >> r1;
        r3 = r3 & 255;
        r3 = (byte) r3;
        r6[r2] = r3;
        r0 = r0 + 1;
        goto L_0x000d;
    L_0x001e:
        return r6;
    L_0x001f:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "(nBytes-1)*8+srcPos is greater or equal to than 32";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.Conversion.intToByteArray(int, int, byte[], int, int):byte[]");
    }

    public static java.lang.String intToHex(int r7, int r8, java.lang.String r9, int r10, int r11) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:15:0x0046 in {1, 8, 9, 10, 12, 14} preds:[]
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
        if (r11 != 0) goto L_0x0003;
    L_0x0002:
        return r9;
    L_0x0003:
        r0 = r11 + -1;
        r0 = r0 * 4;
        r0 = r0 + r8;
        r1 = 32;
        if (r0 >= r1) goto L_0x003e;
    L_0x000c:
        r0 = new java.lang.StringBuilder;
        r0.<init>(r9);
        r1 = r0.length();
        r2 = 0;
    L_0x0016:
        if (r2 >= r11) goto L_0x0039;
    L_0x0018:
        r3 = r2 * 4;
        r3 = r3 + r8;
        r4 = r7 >> r3;
        r4 = r4 & 15;
        r5 = r10 + r2;
        if (r5 != r1) goto L_0x002d;
    L_0x0023:
        r1 = r1 + 1;
        r5 = intToHexDigit(r4);
        r0.append(r5);
        goto L_0x0036;
    L_0x002d:
        r5 = r10 + r2;
        r6 = intToHexDigit(r4);
        r0.setCharAt(r5, r6);
    L_0x0036:
        r2 = r2 + 1;
        goto L_0x0016;
    L_0x0039:
        r2 = r0.toString();
        return r2;
    L_0x003e:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "(nHexs-1)*4+srcPos is greater or equal to than 32";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.Conversion.intToHex(int, int, java.lang.String, int, int):java.lang.String");
    }

    public static short[] intToShortArray(int r5, int r6, short[] r7, int r8, int r9) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:10:0x0029 in {1, 6, 7, 9} preds:[]
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
        if (r9 != 0) goto L_0x0003;
    L_0x0002:
        return r7;
    L_0x0003:
        r0 = r9 + -1;
        r0 = r0 * 16;
        r0 = r0 + r6;
        r1 = 32;
        if (r0 >= r1) goto L_0x0021;
    L_0x000c:
        r0 = 0;
    L_0x000d:
        if (r0 >= r9) goto L_0x0020;
    L_0x000f:
        r1 = r0 * 16;
        r1 = r1 + r6;
        r2 = r8 + r0;
        r3 = 65535; // 0xffff float:9.1834E-41 double:3.23786E-319;
        r4 = r5 >> r1;
        r3 = r3 & r4;
        r3 = (short) r3;
        r7[r2] = r3;
        r0 = r0 + 1;
        goto L_0x000d;
    L_0x0020:
        return r7;
    L_0x0021:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "(nShorts-1)*16+srcPos is greater or equal to than 32";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.Conversion.intToShortArray(int, int, short[], int, int):short[]");
    }

    public static boolean[] longToBinary(long r8, int r10, boolean[] r11, int r12, int r13) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:14:0x002d in {1, 8, 9, 10, 11, 13} preds:[]
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
        if (r13 != 0) goto L_0x0003;
    L_0x0002:
        return r11;
    L_0x0003:
        r0 = r13 + -1;
        r0 = r0 + r10;
        r1 = 64;
        if (r0 >= r1) goto L_0x0025;
    L_0x000a:
        r0 = 0;
    L_0x000b:
        if (r0 >= r13) goto L_0x0024;
    L_0x000d:
        r1 = r0 + r10;
        r2 = r12 + r0;
        r3 = 1;
        r5 = r8 >> r1;
        r3 = r3 & r5;
        r5 = 0;
        r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
        if (r7 == 0) goto L_0x001e;
    L_0x001c:
        r3 = 1;
        goto L_0x001f;
    L_0x001e:
        r3 = 0;
    L_0x001f:
        r11[r2] = r3;
        r0 = r0 + 1;
        goto L_0x000b;
    L_0x0024:
        return r11;
    L_0x0025:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "nBools-1+srcPos is greater or equal to than 64";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.Conversion.longToBinary(long, int, boolean[], int, int):boolean[]");
    }

    public static byte[] longToByteArray(long r7, int r9, byte[] r10, int r11, int r12) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:10:0x0029 in {1, 6, 7, 9} preds:[]
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
        if (r12 != 0) goto L_0x0003;
    L_0x0002:
        return r10;
    L_0x0003:
        r0 = r12 + -1;
        r0 = r0 * 8;
        r0 = r0 + r9;
        r1 = 64;
        if (r0 >= r1) goto L_0x0021;
    L_0x000c:
        r0 = 0;
    L_0x000d:
        if (r0 >= r12) goto L_0x0020;
    L_0x000f:
        r1 = r0 * 8;
        r1 = r1 + r9;
        r2 = r11 + r0;
        r3 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        r5 = r7 >> r1;
        r3 = r3 & r5;
        r3 = (int) r3;
        r3 = (byte) r3;
        r10[r2] = r3;
        r0 = r0 + 1;
        goto L_0x000d;
    L_0x0020:
        return r10;
    L_0x0021:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "(nBytes-1)*8+srcPos is greater or equal to than 64";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.Conversion.longToByteArray(long, int, byte[], int, int):byte[]");
    }

    public static java.lang.String longToHex(long r8, int r10, java.lang.String r11, int r12, int r13) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:15:0x0048 in {1, 8, 9, 10, 12, 14} preds:[]
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
        if (r13 != 0) goto L_0x0003;
    L_0x0002:
        return r11;
    L_0x0003:
        r0 = r13 + -1;
        r0 = r0 * 4;
        r0 = r0 + r10;
        r1 = 64;
        if (r0 >= r1) goto L_0x0040;
    L_0x000c:
        r0 = new java.lang.StringBuilder;
        r0.<init>(r11);
        r1 = r0.length();
        r2 = 0;
    L_0x0016:
        if (r2 >= r13) goto L_0x003b;
    L_0x0018:
        r3 = r2 * 4;
        r3 = r3 + r10;
        r4 = 15;
        r6 = r8 >> r3;
        r4 = r4 & r6;
        r4 = (int) r4;
        r5 = r12 + r2;
        if (r5 != r1) goto L_0x002f;
    L_0x0025:
        r1 = r1 + 1;
        r5 = intToHexDigit(r4);
        r0.append(r5);
        goto L_0x0038;
    L_0x002f:
        r5 = r12 + r2;
        r6 = intToHexDigit(r4);
        r0.setCharAt(r5, r6);
    L_0x0038:
        r2 = r2 + 1;
        goto L_0x0016;
    L_0x003b:
        r2 = r0.toString();
        return r2;
    L_0x0040:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "(nHexs-1)*4+srcPos is greater or equal to than 64";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.Conversion.longToHex(long, int, java.lang.String, int, int):java.lang.String");
    }

    public static int[] longToIntArray(long r7, int r9, int[] r10, int r11, int r12) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:10:0x0028 in {1, 6, 7, 9} preds:[]
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
        if (r12 != 0) goto L_0x0003;
    L_0x0002:
        return r10;
    L_0x0003:
        r0 = r12 + -1;
        r0 = r0 * 32;
        r0 = r0 + r9;
        r1 = 64;
        if (r0 >= r1) goto L_0x0020;
    L_0x000c:
        r0 = 0;
    L_0x000d:
        if (r0 >= r12) goto L_0x001f;
    L_0x000f:
        r1 = r0 * 32;
        r1 = r1 + r9;
        r2 = r11 + r0;
        r3 = -1;
        r5 = r7 >> r1;
        r3 = r3 & r5;
        r3 = (int) r3;
        r10[r2] = r3;
        r0 = r0 + 1;
        goto L_0x000d;
    L_0x001f:
        return r10;
    L_0x0020:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "(nInts-1)*32+srcPos is greater or equal to than 64";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.Conversion.longToIntArray(long, int, int[], int, int):int[]");
    }

    public static short[] longToShortArray(long r7, int r9, short[] r10, int r11, int r12) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:10:0x002a in {1, 6, 7, 9} preds:[]
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
        if (r12 != 0) goto L_0x0003;
    L_0x0002:
        return r10;
    L_0x0003:
        r0 = r12 + -1;
        r0 = r0 * 16;
        r0 = r0 + r9;
        r1 = 64;
        if (r0 >= r1) goto L_0x0022;
    L_0x000c:
        r0 = 0;
    L_0x000d:
        if (r0 >= r12) goto L_0x0021;
    L_0x000f:
        r1 = r0 * 16;
        r1 = r1 + r9;
        r2 = r11 + r0;
        r3 = 65535; // 0xffff float:9.1834E-41 double:3.23786E-319;
        r5 = r7 >> r1;
        r3 = r3 & r5;
        r3 = (int) r3;
        r3 = (short) r3;
        r10[r2] = r3;
        r0 = r0 + 1;
        goto L_0x000d;
    L_0x0021:
        return r10;
    L_0x0022:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "(nShorts-1)*16+srcPos is greater or equal to than 64";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.Conversion.longToShortArray(long, int, short[], int, int):short[]");
    }

    public static int shortArrayToInt(short[] r6, int r7, int r8, int r9, int r10) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:13:0x0033 in {2, 4, 9, 10, 12} preds:[]
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
        r0 = r6.length;
        if (r0 != 0) goto L_0x0005;
    L_0x0003:
        if (r7 == 0) goto L_0x0007;
    L_0x0005:
        if (r10 != 0) goto L_0x0008;
    L_0x0007:
        return r8;
    L_0x0008:
        r0 = r10 + -1;
        r0 = r0 * 16;
        r0 = r0 + r9;
        r1 = 32;
        if (r0 >= r1) goto L_0x002b;
    L_0x0011:
        r0 = r8;
        r1 = 0;
    L_0x0013:
        if (r1 >= r10) goto L_0x002a;
    L_0x0015:
        r2 = r1 * 16;
        r2 = r2 + r9;
        r3 = r1 + r7;
        r3 = r6[r3];
        r4 = 65535; // 0xffff float:9.1834E-41 double:3.23786E-319;
        r3 = r3 & r4;
        r3 = r3 << r2;
        r4 = r4 << r2;
        r5 = r4 ^ -1;
        r5 = r5 & r0;
        r0 = r5 | r3;
        r1 = r1 + 1;
        goto L_0x0013;
    L_0x002a:
        return r0;
    L_0x002b:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "(nShorts-1)*16+dstPos is greater or equal to than 32";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.Conversion.shortArrayToInt(short[], int, int, int, int):int");
    }

    public static long shortArrayToLong(short[] r10, int r11, long r12, int r14, int r15) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:13:0x0035 in {2, 4, 9, 10, 12} preds:[]
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
        r0 = r10.length;
        if (r0 != 0) goto L_0x0005;
    L_0x0003:
        if (r11 == 0) goto L_0x0007;
    L_0x0005:
        if (r15 != 0) goto L_0x0008;
    L_0x0007:
        return r12;
    L_0x0008:
        r0 = r15 + -1;
        r0 = r0 * 16;
        r0 = r0 + r14;
        r1 = 64;
        if (r0 >= r1) goto L_0x002d;
    L_0x0011:
        r0 = r12;
        r2 = 0;
    L_0x0013:
        if (r2 >= r15) goto L_0x002c;
    L_0x0015:
        r3 = r2 * 16;
        r3 = r3 + r14;
        r4 = r2 + r11;
        r4 = r10[r4];
        r4 = (long) r4;
        r6 = 65535; // 0xffff float:9.1834E-41 double:3.23786E-319;
        r4 = r4 & r6;
        r4 = r4 << r3;
        r6 = r6 << r3;
        r8 = -1;
        r8 = r8 ^ r6;
        r8 = r8 & r0;
        r0 = r8 | r4;
        r2 = r2 + 1;
        goto L_0x0013;
    L_0x002c:
        return r0;
    L_0x002d:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "(nShorts-1)*16+dstPos is greater or equal to than 64";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.Conversion.shortArrayToLong(short[], int, long, int, int):long");
    }

    public static boolean[] shortToBinary(short r5, int r6, boolean[] r7, int r8, int r9) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:14:0x0028 in {1, 8, 9, 10, 11, 13} preds:[]
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
        if (r9 != 0) goto L_0x0003;
    L_0x0002:
        return r7;
    L_0x0003:
        r0 = r9 + -1;
        r0 = r0 + r6;
        r1 = 16;
        if (r0 >= r1) goto L_0x0020;
        r0 = 0;
    L_0x000c:
        if (r0 >= r9) goto L_0x001f;
    L_0x000e:
        r1 = r0 + r6;
        r2 = r8 + r0;
        r3 = r5 >> r1;
        r4 = 1;
        r3 = r3 & r4;
        if (r3 == 0) goto L_0x0019;
    L_0x0018:
        goto L_0x001a;
    L_0x0019:
        r4 = 0;
    L_0x001a:
        r7[r2] = r4;
        r0 = r0 + 1;
        goto L_0x000c;
    L_0x001f:
        return r7;
    L_0x0020:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "nBools-1+srcPos is greater or equal to than 16";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.Conversion.shortToBinary(short, int, boolean[], int, int):boolean[]");
    }

    public static byte[] shortToByteArray(short r4, int r5, byte[] r6, int r7, int r8) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:10:0x0027 in {1, 6, 7, 9} preds:[]
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
        if (r8 != 0) goto L_0x0003;
    L_0x0002:
        return r6;
    L_0x0003:
        r0 = r8 + -1;
        r0 = r0 * 8;
        r0 = r0 + r5;
        r1 = 16;
        if (r0 >= r1) goto L_0x001f;
    L_0x000c:
        r0 = 0;
    L_0x000d:
        if (r0 >= r8) goto L_0x001e;
    L_0x000f:
        r1 = r0 * 8;
        r1 = r1 + r5;
        r2 = r7 + r0;
        r3 = r4 >> r1;
        r3 = r3 & 255;
        r3 = (byte) r3;
        r6[r2] = r3;
        r0 = r0 + 1;
        goto L_0x000d;
    L_0x001e:
        return r6;
    L_0x001f:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "(nBytes-1)*8+srcPos is greater or equal to than 16";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.Conversion.shortToByteArray(short, int, byte[], int, int):byte[]");
    }

    public static java.lang.String shortToHex(short r7, int r8, java.lang.String r9, int r10, int r11) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:15:0x0046 in {1, 8, 9, 10, 12, 14} preds:[]
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
        if (r11 != 0) goto L_0x0003;
    L_0x0002:
        return r9;
    L_0x0003:
        r0 = r11 + -1;
        r0 = r0 * 4;
        r0 = r0 + r8;
        r1 = 16;
        if (r0 >= r1) goto L_0x003e;
    L_0x000c:
        r0 = new java.lang.StringBuilder;
        r0.<init>(r9);
        r1 = r0.length();
        r2 = 0;
    L_0x0016:
        if (r2 >= r11) goto L_0x0039;
    L_0x0018:
        r3 = r2 * 4;
        r3 = r3 + r8;
        r4 = r7 >> r3;
        r4 = r4 & 15;
        r5 = r10 + r2;
        if (r5 != r1) goto L_0x002d;
    L_0x0023:
        r1 = r1 + 1;
        r5 = intToHexDigit(r4);
        r0.append(r5);
        goto L_0x0036;
    L_0x002d:
        r5 = r10 + r2;
        r6 = intToHexDigit(r4);
        r0.setCharAt(r5, r6);
    L_0x0036:
        r2 = r2 + 1;
        goto L_0x0016;
    L_0x0039:
        r2 = r0.toString();
        return r2;
    L_0x003e:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "(nHexs-1)*4+srcPos is greater or equal to than 16";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.Conversion.shortToHex(short, int, java.lang.String, int, int):java.lang.String");
    }

    public static int hexDigitToInt(char hexDigit) {
        int digit = Character.digit(hexDigit, 16);
        if (digit >= 0) {
            return digit;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Cannot interpret '");
        stringBuilder.append(hexDigit);
        stringBuilder.append("' as a hexadecimal digit");
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int hexDigitMsb0ToInt(char r3) {
        /*
        switch(r3) {
            case 48: goto L_0x004b;
            case 49: goto L_0x0048;
            case 50: goto L_0x0046;
            case 51: goto L_0x0043;
            case 52: goto L_0x0041;
            case 53: goto L_0x003e;
            case 54: goto L_0x003c;
            case 55: goto L_0x0039;
            case 56: goto L_0x0037;
            case 57: goto L_0x0034;
            default: goto L_0x0003;
        };
    L_0x0003:
        switch(r3) {
            case 65: goto L_0x0032;
            case 66: goto L_0x002f;
            case 67: goto L_0x002d;
            case 68: goto L_0x002a;
            case 69: goto L_0x0028;
            case 70: goto L_0x0025;
            default: goto L_0x0006;
        };
    L_0x0006:
        switch(r3) {
            case 97: goto L_0x0032;
            case 98: goto L_0x002f;
            case 99: goto L_0x002d;
            case 100: goto L_0x002a;
            case 101: goto L_0x0028;
            case 102: goto L_0x0025;
            default: goto L_0x0009;
        };
    L_0x0009:
        r0 = new java.lang.IllegalArgumentException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Cannot interpret '";
        r1.append(r2);
        r1.append(r3);
        r2 = "' as a hexadecimal digit";
        r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
    L_0x0025:
        r0 = 15;
        return r0;
    L_0x0028:
        r0 = 7;
        return r0;
    L_0x002a:
        r0 = 11;
        return r0;
    L_0x002d:
        r0 = 3;
        return r0;
    L_0x002f:
        r0 = 13;
        return r0;
    L_0x0032:
        r0 = 5;
        return r0;
    L_0x0034:
        r0 = 9;
        return r0;
    L_0x0037:
        r0 = 1;
        return r0;
    L_0x0039:
        r0 = 14;
        return r0;
    L_0x003c:
        r0 = 6;
        return r0;
    L_0x003e:
        r0 = 10;
        return r0;
    L_0x0041:
        r0 = 2;
        return r0;
    L_0x0043:
        r0 = 12;
        return r0;
    L_0x0046:
        r0 = 4;
        return r0;
    L_0x0048:
        r0 = 8;
        return r0;
    L_0x004b:
        r0 = 0;
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.Conversion.hexDigitMsb0ToInt(char):int");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean[] hexDigitToBinary(char r3) {
        /*
        switch(r3) {
            case 48: goto L_0x00ac;
            case 49: goto L_0x00a3;
            case 50: goto L_0x009a;
            case 51: goto L_0x0091;
            case 52: goto L_0x0088;
            case 53: goto L_0x007f;
            case 54: goto L_0x0076;
            case 55: goto L_0x006d;
            case 56: goto L_0x0064;
            case 57: goto L_0x005b;
            default: goto L_0x0003;
        };
    L_0x0003:
        switch(r3) {
            case 65: goto L_0x0052;
            case 66: goto L_0x0049;
            case 67: goto L_0x0040;
            case 68: goto L_0x0037;
            case 69: goto L_0x002e;
            case 70: goto L_0x0025;
            default: goto L_0x0006;
        };
    L_0x0006:
        switch(r3) {
            case 97: goto L_0x0052;
            case 98: goto L_0x0049;
            case 99: goto L_0x0040;
            case 100: goto L_0x0037;
            case 101: goto L_0x002e;
            case 102: goto L_0x0025;
            default: goto L_0x0009;
        };
    L_0x0009:
        r0 = new java.lang.IllegalArgumentException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Cannot interpret '";
        r1.append(r2);
        r1.append(r3);
        r2 = "' as a hexadecimal digit";
        r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
    L_0x0025:
        r0 = TTTT;
        r0 = r0.clone();
        r0 = (boolean[]) r0;
        return r0;
    L_0x002e:
        r0 = FTTT;
        r0 = r0.clone();
        r0 = (boolean[]) r0;
        return r0;
    L_0x0037:
        r0 = TFTT;
        r0 = r0.clone();
        r0 = (boolean[]) r0;
        return r0;
    L_0x0040:
        r0 = FFTT;
        r0 = r0.clone();
        r0 = (boolean[]) r0;
        return r0;
    L_0x0049:
        r0 = TTFT;
        r0 = r0.clone();
        r0 = (boolean[]) r0;
        return r0;
    L_0x0052:
        r0 = FTFT;
        r0 = r0.clone();
        r0 = (boolean[]) r0;
        return r0;
    L_0x005b:
        r0 = TFFT;
        r0 = r0.clone();
        r0 = (boolean[]) r0;
        return r0;
    L_0x0064:
        r0 = FFFT;
        r0 = r0.clone();
        r0 = (boolean[]) r0;
        return r0;
    L_0x006d:
        r0 = TTTF;
        r0 = r0.clone();
        r0 = (boolean[]) r0;
        return r0;
    L_0x0076:
        r0 = FTTF;
        r0 = r0.clone();
        r0 = (boolean[]) r0;
        return r0;
    L_0x007f:
        r0 = TFTF;
        r0 = r0.clone();
        r0 = (boolean[]) r0;
        return r0;
    L_0x0088:
        r0 = FFTF;
        r0 = r0.clone();
        r0 = (boolean[]) r0;
        return r0;
    L_0x0091:
        r0 = TTFF;
        r0 = r0.clone();
        r0 = (boolean[]) r0;
        return r0;
    L_0x009a:
        r0 = FTFF;
        r0 = r0.clone();
        r0 = (boolean[]) r0;
        return r0;
    L_0x00a3:
        r0 = TFFF;
        r0 = r0.clone();
        r0 = (boolean[]) r0;
        return r0;
    L_0x00ac:
        r0 = FFFF;
        r0 = r0.clone();
        r0 = (boolean[]) r0;
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.Conversion.hexDigitToBinary(char):boolean[]");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean[] hexDigitMsb0ToBinary(char r3) {
        /*
        switch(r3) {
            case 48: goto L_0x00ac;
            case 49: goto L_0x00a3;
            case 50: goto L_0x009a;
            case 51: goto L_0x0091;
            case 52: goto L_0x0088;
            case 53: goto L_0x007f;
            case 54: goto L_0x0076;
            case 55: goto L_0x006d;
            case 56: goto L_0x0064;
            case 57: goto L_0x005b;
            default: goto L_0x0003;
        };
    L_0x0003:
        switch(r3) {
            case 65: goto L_0x0052;
            case 66: goto L_0x0049;
            case 67: goto L_0x0040;
            case 68: goto L_0x0037;
            case 69: goto L_0x002e;
            case 70: goto L_0x0025;
            default: goto L_0x0006;
        };
    L_0x0006:
        switch(r3) {
            case 97: goto L_0x0052;
            case 98: goto L_0x0049;
            case 99: goto L_0x0040;
            case 100: goto L_0x0037;
            case 101: goto L_0x002e;
            case 102: goto L_0x0025;
            default: goto L_0x0009;
        };
    L_0x0009:
        r0 = new java.lang.IllegalArgumentException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Cannot interpret '";
        r1.append(r2);
        r1.append(r3);
        r2 = "' as a hexadecimal digit";
        r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
    L_0x0025:
        r0 = TTTT;
        r0 = r0.clone();
        r0 = (boolean[]) r0;
        return r0;
    L_0x002e:
        r0 = TTTF;
        r0 = r0.clone();
        r0 = (boolean[]) r0;
        return r0;
    L_0x0037:
        r0 = TTFT;
        r0 = r0.clone();
        r0 = (boolean[]) r0;
        return r0;
    L_0x0040:
        r0 = TTFF;
        r0 = r0.clone();
        r0 = (boolean[]) r0;
        return r0;
    L_0x0049:
        r0 = TFTT;
        r0 = r0.clone();
        r0 = (boolean[]) r0;
        return r0;
    L_0x0052:
        r0 = TFTF;
        r0 = r0.clone();
        r0 = (boolean[]) r0;
        return r0;
    L_0x005b:
        r0 = TFFT;
        r0 = r0.clone();
        r0 = (boolean[]) r0;
        return r0;
    L_0x0064:
        r0 = TFFF;
        r0 = r0.clone();
        r0 = (boolean[]) r0;
        return r0;
    L_0x006d:
        r0 = FTTT;
        r0 = r0.clone();
        r0 = (boolean[]) r0;
        return r0;
    L_0x0076:
        r0 = FTTF;
        r0 = r0.clone();
        r0 = (boolean[]) r0;
        return r0;
    L_0x007f:
        r0 = FTFT;
        r0 = r0.clone();
        r0 = (boolean[]) r0;
        return r0;
    L_0x0088:
        r0 = FTFF;
        r0 = r0.clone();
        r0 = (boolean[]) r0;
        return r0;
    L_0x0091:
        r0 = FFTT;
        r0 = r0.clone();
        r0 = (boolean[]) r0;
        return r0;
    L_0x009a:
        r0 = FFTF;
        r0 = r0.clone();
        r0 = (boolean[]) r0;
        return r0;
    L_0x00a3:
        r0 = FFFT;
        r0 = r0.clone();
        r0 = (boolean[]) r0;
        return r0;
    L_0x00ac:
        r0 = FFFF;
        r0 = r0.clone();
        r0 = (boolean[]) r0;
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.Conversion.hexDigitMsb0ToBinary(char):boolean[]");
    }

    public static char binaryToHexDigit(boolean[] src) {
        return binaryToHexDigit(src, 0);
    }

    public static char binaryToHexDigit(boolean[] src, int srcPos) {
        if (src.length == 0) {
            throw new IllegalArgumentException("Cannot convert an empty array.");
        } else if (src.length <= srcPos + 3 || !src[srcPos + 3]) {
            if (src.length <= srcPos + 2 || !src[srcPos + 2]) {
                if (src.length <= srcPos + 1 || !src[srcPos + 1]) {
                    return src[srcPos] ? '1' : '0';
                }
                return src[srcPos] ? '3' : '2';
            } else if (src.length <= srcPos + 1 || !src[srcPos + 1]) {
                return src[srcPos] ? '5' : '4';
            } else {
                return src[srcPos] ? '7' : '6';
            }
        } else if (src.length <= srcPos + 2 || !src[srcPos + 2]) {
            if (src.length <= srcPos + 1 || !src[srcPos + 1]) {
                return src[srcPos] ? '9' : '8';
            }
            return src[srcPos] ? 'b' : 'a';
        } else if (src.length <= srcPos + 1 || !src[srcPos + 1]) {
            return src[srcPos] ? 'd' : 'c';
        } else {
            return src[srcPos] ? 'f' : 'e';
        }
    }

    public static char binaryToHexDigitMsb0_4bits(boolean[] src) {
        return binaryToHexDigitMsb0_4bits(src, 0);
    }

    public static char binaryToHexDigitMsb0_4bits(boolean[] src, int srcPos) {
        StringBuilder stringBuilder;
        if (src.length > 8) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("src.length>8: src.length=");
            stringBuilder.append(src.length);
            throw new IllegalArgumentException(stringBuilder.toString());
        } else if (src.length - srcPos < 4) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("src.length-srcPos<4: src.length=");
            stringBuilder.append(src.length);
            stringBuilder.append(", srcPos=");
            stringBuilder.append(srcPos);
            throw new IllegalArgumentException(stringBuilder.toString());
        } else if (src[srcPos + 3]) {
            if (src[srcPos + 2]) {
                if (src[srcPos + 1]) {
                    return src[srcPos] ? 'f' : '7';
                }
                return src[srcPos] ? 'b' : '3';
            } else if (src[srcPos + 1]) {
                return src[srcPos] ? 'd' : '5';
            } else {
                return src[srcPos] ? '9' : '1';
            }
        } else if (src[srcPos + 2]) {
            if (src[srcPos + 1]) {
                return src[srcPos] ? 'e' : '6';
            }
            return src[srcPos] ? 'a' : '2';
        } else if (src[srcPos + 1]) {
            return src[srcPos] ? 'c' : '4';
        } else {
            return src[srcPos] ? '8' : '0';
        }
    }

    public static char binaryBeMsb0ToHexDigit(boolean[] src) {
        return binaryBeMsb0ToHexDigit(src, 0);
    }

    public static char binaryBeMsb0ToHexDigit(boolean[] src, int srcPos) {
        if (src.length != 0) {
            int beSrcPos = (src.length - 1) - srcPos;
            int srcLen = Math.min(4, beSrcPos + 1);
            boolean[] paddedSrc = new boolean[4];
            System.arraycopy(src, (beSrcPos + 1) - srcLen, paddedSrc, 4 - srcLen, srcLen);
            src = paddedSrc;
            char c;
            if (src[0]) {
                if (src.length <= 0 + 1 || !src[0 + 1]) {
                    if (src.length <= 0 + 2 || !src[0 + 2]) {
                        c = (src.length <= 0 + 3 || !src[0 + 3]) ? '8' : '9';
                        return c;
                    }
                    c = (src.length <= 0 + 3 || !src[0 + 3]) ? 'a' : 'b';
                    return c;
                } else if (src.length <= 0 + 2 || !src[0 + 2]) {
                    c = (src.length <= 0 + 3 || !src[0 + 3]) ? 'c' : 'd';
                    return c;
                } else {
                    c = (src.length <= 0 + 3 || !src[0 + 3]) ? 'e' : 'f';
                    return c;
                }
            } else if (src.length <= 0 + 1 || !src[0 + 1]) {
                if (src.length <= 0 + 2 || !src[0 + 2]) {
                    c = (src.length <= 0 + 3 || !src[0 + 3]) ? '0' : '1';
                    return c;
                }
                c = (src.length <= 0 + 3 || !src[0 + 3]) ? '2' : '3';
                return c;
            } else if (src.length <= 0 + 2 || !src[0 + 2]) {
                c = (src.length <= 0 + 3 || !src[0 + 3]) ? '4' : '5';
                return c;
            } else {
                c = (src.length <= 0 + 3 || !src[0 + 3]) ? '6' : '7';
                return c;
            }
        }
        throw new IllegalArgumentException("Cannot convert an empty array.");
    }

    public static char intToHexDigit(int nibble) {
        char c = Character.forDigit(nibble, '\u0010');
        if (c != '\u0000') {
            return c;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("nibble value not between 0 and 15: ");
        stringBuilder.append(nibble);
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public static char intToHexDigitMsb0(int nibble) {
        switch (nibble) {
            case 0:
                return '0';
            case 1:
                return '8';
            case 2:
                return '4';
            case 3:
                return 'c';
            case 4:
                return '2';
            case 5:
                return 'a';
            case 6:
                return '6';
            case 7:
                return 'e';
            case 8:
                return '1';
            case 9:
                return '9';
            case 10:
                return '5';
            case 11:
                return 'd';
            case 12:
                return '3';
            case 13:
                return 'b';
            case 14:
                return '7';
            case 15:
                return 'f';
            default:
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("nibble value not between 0 and 15: ");
                stringBuilder.append(nibble);
                throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    public static byte[] uuidToByteArray(UUID src, byte[] dst, int dstPos, int nBytes) {
        if (nBytes == 0) {
            return dst;
        }
        if (nBytes <= 16) {
            longToByteArray(src.getMostSignificantBits(), 0, dst, dstPos, nBytes > 8 ? 8 : nBytes);
            if (nBytes >= 8) {
                longToByteArray(src.getLeastSignificantBits(), 0, dst, dstPos + 8, nBytes - 8);
            }
            return dst;
        }
        throw new IllegalArgumentException("nBytes is greater than 16");
    }

    public static UUID byteArrayToUuid(byte[] src, int srcPos) {
        if (src.length - srcPos >= 16) {
            return new UUID(byteArrayToLong(src, srcPos, 0, 0, 8), byteArrayToLong(src, srcPos + 8, 0, 0, 8));
        }
        throw new IllegalArgumentException("Need at least 16 bytes for UUID");
    }
}
