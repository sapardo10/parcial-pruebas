package org.apache.commons.io.input;

import java.io.Reader;
import java.io.Serializable;

public class CharSequenceReader extends Reader implements Serializable {
    private static final long serialVersionUID = 3724187752191401220L;
    private final CharSequence charSequence;
    private int idx;
    private int mark;

    public int read(char[] r7, int r8, int r9) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:19:0x005f in {2, 12, 13, 14, 16, 18} preds:[]
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
        r6 = this;
        r0 = r6.idx;
        r1 = r6.charSequence;
        r1 = r1.length();
        r2 = -1;
        if (r0 < r1) goto L_0x000c;
    L_0x000b:
        return r2;
    L_0x000c:
        if (r7 == 0) goto L_0x0057;
    L_0x000e:
        if (r9 < 0) goto L_0x002e;
    L_0x0010:
        if (r8 < 0) goto L_0x002e;
    L_0x0012:
        r0 = r8 + r9;
        r1 = r7.length;
        if (r0 > r1) goto L_0x002e;
    L_0x0017:
        r0 = 0;
        r1 = 0;
    L_0x0019:
        if (r1 >= r9) goto L_0x002d;
    L_0x001b:
        r3 = r6.read();
        if (r3 != r2) goto L_0x0022;
    L_0x0021:
        return r0;
    L_0x0022:
        r4 = r8 + r1;
        r5 = (char) r3;
        r7[r4] = r5;
        r0 = r0 + 1;
        r1 = r1 + 1;
        goto L_0x0019;
    L_0x002d:
        return r0;
        r0 = new java.lang.IndexOutOfBoundsException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Array Size=";
        r1.append(r2);
        r2 = r7.length;
        r1.append(r2);
        r2 = ", offset=";
        r1.append(r2);
        r1.append(r8);
        r2 = ", length=";
        r1.append(r2);
        r1.append(r9);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
    L_0x0057:
        r0 = new java.lang.NullPointerException;
        r1 = "Character array is missing";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.io.input.CharSequenceReader.read(char[], int, int):int");
    }

    public CharSequenceReader(CharSequence charSequence) {
        this.charSequence = charSequence != null ? charSequence : "";
    }

    public void close() {
        this.idx = 0;
        this.mark = 0;
    }

    public void mark(int readAheadLimit) {
        this.mark = this.idx;
    }

    public boolean markSupported() {
        return true;
    }

    public int read() {
        if (this.idx >= this.charSequence.length()) {
            return -1;
        }
        CharSequence charSequence = this.charSequence;
        int i = this.idx;
        this.idx = i + 1;
        return charSequence.charAt(i);
    }

    public void reset() {
        this.idx = this.mark;
    }

    public long skip(long n) {
        if (n < 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Number of characters to skip is less than zero: ");
            stringBuilder.append(n);
            throw new IllegalArgumentException(stringBuilder.toString());
        } else if (this.idx >= this.charSequence.length()) {
            return -1;
        } else {
            int dest = (int) Math.min((long) this.charSequence.length(), ((long) this.idx) + n);
            int count = dest - this.idx;
            this.idx = dest;
            return (long) count;
        }
    }

    public String toString() {
        return this.charSequence.toString();
    }
}
