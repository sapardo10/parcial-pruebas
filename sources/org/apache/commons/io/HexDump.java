package org.apache.commons.io;

public class HexDump {
    public static final String EOL = System.getProperty("line.separator");
    private static final char[] _hexcodes = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static final int[] _shifts = new int[]{28, 24, 20, 16, 12, 8, 4, 0};

    public static void dump(byte[] r9, long r10, java.io.OutputStream r12, int r13) throws java.io.IOException, java.lang.ArrayIndexOutOfBoundsException, java.lang.IllegalArgumentException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:32:0x00ac in {9, 10, 14, 15, 16, 23, 24, 25, 26, 27, 29, 31} preds:[]
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
        if (r13 < 0) goto L_0x008b;
    L_0x0002:
        r0 = r9.length;
        if (r13 >= r0) goto L_0x008b;
    L_0x0005:
        if (r12 == 0) goto L_0x0083;
    L_0x0007:
        r0 = (long) r13;
        r0 = r0 + r10;
        r2 = new java.lang.StringBuilder;
        r3 = 74;
        r2.<init>(r3);
        r3 = r13;
    L_0x0011:
        r4 = r9.length;
        if (r3 >= r4) goto L_0x0082;
    L_0x0014:
        r4 = r9.length;
        r4 = r4 - r3;
        r5 = 16;
        if (r4 <= r5) goto L_0x001d;
    L_0x001a:
        r4 = 16;
        goto L_0x001e;
    L_0x001e:
        r6 = dump(r2, r0);
        r7 = 32;
        r6.append(r7);
        r6 = 0;
    L_0x0028:
        if (r6 >= r5) goto L_0x003f;
    L_0x002a:
        if (r6 >= r4) goto L_0x0034;
    L_0x002c:
        r8 = r6 + r3;
        r8 = r9[r8];
        dump(r2, r8);
        goto L_0x0039;
    L_0x0034:
        r8 = "  ";
        r2.append(r8);
    L_0x0039:
        r2.append(r7);
        r6 = r6 + 1;
        goto L_0x0028;
    L_0x003f:
        r5 = 0;
    L_0x0040:
        if (r5 >= r4) goto L_0x0062;
    L_0x0042:
        r6 = r5 + r3;
        r6 = r9[r6];
        if (r6 < r7) goto L_0x0059;
    L_0x0048:
        r6 = r5 + r3;
        r6 = r9[r6];
        r8 = 127; // 0x7f float:1.78E-43 double:6.27E-322;
        if (r6 >= r8) goto L_0x0059;
    L_0x0050:
        r6 = r5 + r3;
        r6 = r9[r6];
        r6 = (char) r6;
        r2.append(r6);
        goto L_0x005f;
        r6 = 46;
        r2.append(r6);
    L_0x005f:
        r5 = r5 + 1;
        goto L_0x0040;
    L_0x0062:
        r5 = EOL;
        r2.append(r5);
        r5 = r2.toString();
        r6 = java.nio.charset.Charset.defaultCharset();
        r5 = r5.getBytes(r6);
        r12.write(r5);
        r12.flush();
        r5 = 0;
        r2.setLength(r5);
        r5 = (long) r4;
        r0 = r0 + r5;
        r3 = r3 + 16;
        goto L_0x0011;
    L_0x0082:
        return;
    L_0x0083:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "cannot write to nullstream";
        r0.<init>(r1);
        throw r0;
        r0 = new java.lang.ArrayIndexOutOfBoundsException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "illegal index: ";
        r1.append(r2);
        r1.append(r13);
        r2 = " into array of length ";
        r1.append(r2);
        r2 = r9.length;
        r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.io.HexDump.dump(byte[], long, java.io.OutputStream, int):void");
    }

    private static StringBuilder dump(StringBuilder _lbuffer, long value) {
        for (int j = 0; j < 8; j++) {
            _lbuffer.append(_hexcodes[((int) (value >> _shifts[j])) & 15]);
        }
        return _lbuffer;
    }

    private static StringBuilder dump(StringBuilder _cbuffer, byte value) {
        for (int j = 0; j < 2; j++) {
            _cbuffer.append(_hexcodes[(value >> _shifts[j + 6]) & 15]);
        }
        return _cbuffer;
    }
}
