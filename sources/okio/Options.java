package okio;

import java.util.AbstractList;
import java.util.RandomAccess;

public final class Options extends AbstractList<ByteString> implements RandomAccess {
    final ByteString[] byteStrings;
    final int[] trie;

    private static void buildTrieRecursive(long r24, okio.Buffer r26, int r27, java.util.List<okio.ByteString> r28, int r29, int r30, java.util.List<java.lang.Integer> r31) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:73:0x021d in {6, 8, 11, 12, 19, 20, 21, 28, 29, 30, 31, 38, 39, 40, 45, 46, 47, 48, 49, 50, 55, 56, 57, 61, 66, 68, 69, 70, 72} preds:[]
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
        r0 = r26;
        r1 = r27;
        r10 = r28;
        r2 = r29;
        r11 = r30;
        r12 = r31;
        if (r2 >= r11) goto L_0x0217;
    L_0x000e:
        r3 = r29;
    L_0x0010:
        if (r3 >= r11) goto L_0x0027;
    L_0x0012:
        r4 = r10.get(r3);
        r4 = (okio.ByteString) r4;
        r4 = r4.size();
        if (r4 < r1) goto L_0x0021;
    L_0x001e:
        r3 = r3 + 1;
        goto L_0x0010;
    L_0x0021:
        r4 = new java.lang.AssertionError;
        r4.<init>();
        throw r4;
        r3 = r28.get(r29);
        r3 = (okio.ByteString) r3;
        r4 = r11 + -1;
        r4 = r10.get(r4);
        r13 = r4;
        r13 = (okio.ByteString) r13;
        r4 = -1;
        r5 = r3.size();
        if (r1 != r5) goto L_0x0055;
    L_0x003e:
        r5 = r12.get(r2);
        r5 = (java.lang.Integer) r5;
        r4 = r5.intValue();
        r2 = r2 + 1;
        r5 = r10.get(r2);
        r3 = r5;
        r3 = (okio.ByteString) r3;
        r15 = r2;
        r14 = r3;
        r9 = r4;
        goto L_0x0058;
    L_0x0055:
        r15 = r2;
        r14 = r3;
        r9 = r4;
    L_0x0058:
        r2 = r14.getByte(r1);
        r3 = r13.getByte(r1);
        r16 = -1;
        r4 = 2;
        if (r2 == r3) goto L_0x016a;
    L_0x0066:
        r2 = 1;
        r3 = r15 + 1;
        r8 = r2;
    L_0x006a:
        if (r3 >= r11) goto L_0x008b;
    L_0x006c:
        r2 = r3 + -1;
        r2 = r10.get(r2);
        r2 = (okio.ByteString) r2;
        r2 = r2.getByte(r1);
        r6 = r10.get(r3);
        r6 = (okio.ByteString) r6;
        r6 = r6.getByte(r1);
        if (r2 == r6) goto L_0x0087;
    L_0x0084:
        r8 = r8 + 1;
        goto L_0x0088;
    L_0x0088:
        r3 = r3 + 1;
        goto L_0x006a;
    L_0x008b:
        r2 = intCount(r26);
        r2 = (long) r2;
        r2 = r24 + r2;
        r2 = r2 + r4;
        r4 = r8 * 2;
        r4 = (long) r4;
        r18 = r2 + r4;
        r0.writeInt(r8);
        r0.writeInt(r9);
        r2 = r15;
    L_0x009f:
        if (r2 >= r11) goto L_0x00c5;
    L_0x00a1:
        r3 = r10.get(r2);
        r3 = (okio.ByteString) r3;
        r3 = r3.getByte(r1);
        if (r2 == r15) goto L_0x00bd;
    L_0x00ad:
        r4 = r2 + -1;
        r4 = r10.get(r4);
        r4 = (okio.ByteString) r4;
        r4 = r4.getByte(r1);
        if (r3 == r4) goto L_0x00bc;
    L_0x00bb:
        goto L_0x00bd;
    L_0x00bc:
        goto L_0x00c2;
    L_0x00bd:
        r4 = r3 & 255;
        r0.writeInt(r4);
    L_0x00c2:
        r2 = r2 + 1;
        goto L_0x009f;
    L_0x00c5:
        r2 = new okio.Buffer;
        r2.<init>();
        r7 = r2;
        r2 = r15;
        r6 = r2;
    L_0x00cd:
        if (r6 >= r11) goto L_0x0154;
    L_0x00cf:
        r2 = r10.get(r6);
        r2 = (okio.ByteString) r2;
        r5 = r2.getByte(r1);
        r2 = r30;
        r3 = r6 + 1;
    L_0x00dd:
        if (r3 >= r11) goto L_0x00f1;
    L_0x00df:
        r4 = r10.get(r3);
        r4 = (okio.ByteString) r4;
        r4 = r4.getByte(r1);
        if (r5 == r4) goto L_0x00ee;
    L_0x00eb:
        r2 = r3;
        r4 = r2;
        goto L_0x00f2;
    L_0x00ee:
        r3 = r3 + 1;
        goto L_0x00dd;
    L_0x00f1:
        r4 = r2;
    L_0x00f2:
        r2 = r6 + 1;
        if (r2 != r4) goto L_0x011e;
    L_0x00f6:
        r2 = r1 + 1;
        r3 = r10.get(r6);
        r3 = (okio.ByteString) r3;
        r3 = r3.size();
        if (r2 != r3) goto L_0x011d;
    L_0x0104:
        r2 = r12.get(r6);
        r2 = (java.lang.Integer) r2;
        r2 = r2.intValue();
        r0.writeInt(r2);
        r21 = r4;
        r22 = r5;
        r20 = r6;
        r29 = r7;
        r23 = r8;
        r12 = r9;
        goto L_0x0149;
    L_0x011d:
        goto L_0x011f;
    L_0x011f:
        r2 = intCount(r7);
        r2 = (long) r2;
        r2 = r18 + r2;
        r2 = r2 * r16;
        r2 = (int) r2;
        r0.writeInt(r2);
        r20 = r1 + 1;
        r2 = r18;
        r21 = r4;
        r4 = r7;
        r22 = r5;
        r5 = r20;
        r20 = r6;
        r6 = r28;
        r29 = r7;
        r7 = r20;
        r23 = r8;
        r8 = r21;
        r12 = r9;
        r9 = r31;
        buildTrieRecursive(r2, r4, r5, r6, r7, r8, r9);
    L_0x0149:
        r6 = r21;
        r7 = r29;
        r9 = r12;
        r8 = r23;
        r12 = r31;
        goto L_0x00cd;
    L_0x0154:
        r20 = r6;
        r29 = r7;
        r23 = r8;
        r12 = r9;
        r2 = r29.size();
        r4 = r29;
        r0.write(r4, r2);
        r20 = r12;
        r12 = r31;
        goto L_0x0216;
    L_0x016a:
        r12 = r9;
        r2 = 0;
        r3 = r27;
        r6 = r14.size();
        r7 = r13.size();
        r6 = java.lang.Math.min(r6, r7);
        r9 = r2;
    L_0x017b:
        if (r3 >= r6) goto L_0x018d;
    L_0x017d:
        r2 = r14.getByte(r3);
        r7 = r13.getByte(r3);
        if (r2 != r7) goto L_0x018c;
    L_0x0187:
        r9 = r9 + 1;
        r3 = r3 + 1;
        goto L_0x017b;
    L_0x018c:
        goto L_0x018e;
    L_0x018e:
        r2 = intCount(r26);
        r2 = (long) r2;
        r2 = r24 + r2;
        r2 = r2 + r4;
        r4 = (long) r9;
        r2 = r2 + r4;
        r4 = 1;
        r18 = r2 + r4;
        r2 = -r9;
        r0.writeInt(r2);
        r0.writeInt(r12);
        r2 = r27;
    L_0x01a5:
        r3 = r1 + r9;
        if (r2 >= r3) goto L_0x01b5;
    L_0x01a9:
        r3 = r14.getByte(r2);
        r3 = r3 & 255;
        r0.writeInt(r3);
        r2 = r2 + 1;
        goto L_0x01a5;
    L_0x01b5:
        r2 = r15 + 1;
        if (r2 != r11) goto L_0x01e3;
    L_0x01b9:
        r2 = r1 + r9;
        r3 = r10.get(r15);
        r3 = (okio.ByteString) r3;
        r3 = r3.size();
        if (r2 != r3) goto L_0x01d9;
    L_0x01c7:
        r20 = r12;
        r12 = r31;
        r2 = r12.get(r15);
        r2 = (java.lang.Integer) r2;
        r2 = r2.intValue();
        r0.writeInt(r2);
        goto L_0x0216;
    L_0x01d9:
        r20 = r12;
        r12 = r31;
        r2 = new java.lang.AssertionError;
        r2.<init>();
        throw r2;
    L_0x01e3:
        r20 = r12;
        r12 = r31;
        r2 = new okio.Buffer;
        r2.<init>();
        r8 = r2;
        r2 = intCount(r8);
        r2 = (long) r2;
        r2 = r18 + r2;
        r2 = r2 * r16;
        r2 = (int) r2;
        r0.writeInt(r2);
        r5 = r1 + r9;
        r2 = r18;
        r4 = r8;
        r6 = r28;
        r7 = r15;
        r29 = r8;
        r8 = r30;
        r16 = r9;
        r9 = r31;
        buildTrieRecursive(r2, r4, r5, r6, r7, r8, r9);
        r2 = r29.size();
        r4 = r29;
        r0.write(r4, r2);
    L_0x0216:
        return;
    L_0x0217:
        r3 = new java.lang.AssertionError;
        r3.<init>();
        throw r3;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.Options.buildTrieRecursive(long, okio.Buffer, int, java.util.List, int, int, java.util.List):void");
    }

    public static okio.Options of(okio.ByteString... r11) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:46:0x0104 in {3, 7, 11, 22, 27, 28, 29, 31, 32, 33, 37, 41, 43, 45} preds:[]
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
        r0 = r11.length;
        r1 = 0;
        if (r0 != 0) goto L_0x0012;
    L_0x0004:
        r0 = new okio.Options;
        r1 = new okio.ByteString[r1];
        r2 = 2;
        r2 = new int[r2];
        r2 = {0, -1};
        r0.<init>(r1, r2);
        return r0;
    L_0x0012:
        r0 = new java.util.ArrayList;
        r2 = java.util.Arrays.asList(r11);
        r0.<init>(r2);
        java.util.Collections.sort(r0);
        r2 = new java.util.ArrayList;
        r2.<init>();
        r3 = 0;
    L_0x0024:
        r4 = r0.size();
        if (r3 >= r4) goto L_0x0035;
    L_0x002a:
        r4 = -1;
        r4 = java.lang.Integer.valueOf(r4);
        r2.add(r4);
        r3 = r3 + 1;
        goto L_0x0024;
    L_0x0035:
        r3 = 0;
    L_0x0036:
        r4 = r0.size();
        if (r3 >= r4) goto L_0x004c;
    L_0x003c:
        r4 = r11[r3];
        r4 = java.util.Collections.binarySearch(r0, r4);
        r5 = java.lang.Integer.valueOf(r3);
        r2.set(r4, r5);
        r3 = r3 + 1;
        goto L_0x0036;
    L_0x004c:
        r1 = r0.get(r1);
        r1 = (okio.ByteString) r1;
        r1 = r1.size();
        if (r1 == 0) goto L_0x00fc;
    L_0x0058:
        r1 = 0;
    L_0x0059:
        r3 = r0.size();
        if (r1 >= r3) goto L_0x00bf;
    L_0x005f:
        r3 = r0.get(r1);
        r3 = (okio.ByteString) r3;
        r4 = r1 + 1;
    L_0x0067:
        r5 = r0.size();
        if (r4 >= r5) goto L_0x00bb;
    L_0x006d:
        r5 = r0.get(r4);
        r5 = (okio.ByteString) r5;
        r6 = r5.startsWith(r3);
        if (r6 != 0) goto L_0x007a;
    L_0x0079:
        goto L_0x00bc;
    L_0x007a:
        r6 = r5.size();
        r7 = r3.size();
        if (r6 == r7) goto L_0x00a4;
    L_0x0084:
        r6 = r2.get(r4);
        r6 = (java.lang.Integer) r6;
        r6 = r6.intValue();
        r7 = r2.get(r1);
        r7 = (java.lang.Integer) r7;
        r7 = r7.intValue();
        if (r6 <= r7) goto L_0x00a1;
    L_0x009a:
        r0.remove(r4);
        r2.remove(r4);
        goto L_0x00a3;
    L_0x00a1:
        r4 = r4 + 1;
    L_0x00a3:
        goto L_0x0067;
    L_0x00a4:
        r6 = new java.lang.IllegalArgumentException;
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r8 = "duplicate option: ";
        r7.append(r8);
        r7.append(r5);
        r7 = r7.toString();
        r6.<init>(r7);
        throw r6;
    L_0x00bc:
        r1 = r1 + 1;
        goto L_0x0059;
    L_0x00bf:
        r5 = new okio.Buffer;
        r5.<init>();
        r3 = 0;
        r6 = 0;
        r8 = 0;
        r9 = r0.size();
        r7 = r0;
        r10 = r2;
        buildTrieRecursive(r3, r5, r6, r7, r8, r9, r10);
        r1 = intCount(r5);
        r1 = new int[r1];
        r3 = 0;
    L_0x00d8:
        r4 = r1.length;
        if (r3 >= r4) goto L_0x00e4;
    L_0x00db:
        r4 = r5.readInt();
        r1[r3] = r4;
        r3 = r3 + 1;
        goto L_0x00d8;
    L_0x00e4:
        r3 = r5.exhausted();
        if (r3 == 0) goto L_0x00f6;
    L_0x00ea:
        r3 = new okio.Options;
        r4 = r11.clone();
        r4 = (okio.ByteString[]) r4;
        r3.<init>(r4, r1);
        return r3;
    L_0x00f6:
        r3 = new java.lang.AssertionError;
        r3.<init>();
        throw r3;
    L_0x00fc:
        r1 = new java.lang.IllegalArgumentException;
        r3 = "the empty byte string is not a supported option";
        r1.<init>(r3);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.Options.of(okio.ByteString[]):okio.Options");
    }

    private Options(ByteString[] byteStrings, int[] trie) {
        this.byteStrings = byteStrings;
        this.trie = trie;
    }

    public ByteString get(int i) {
        return this.byteStrings[i];
    }

    public final int size() {
        return this.byteStrings.length;
    }

    private static int intCount(Buffer trieBytes) {
        return (int) (trieBytes.size() / 4);
    }
}
