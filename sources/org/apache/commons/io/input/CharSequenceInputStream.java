package org.apache.commons.io.input;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

public class CharSequenceInputStream extends InputStream {
    private static final int BUFFER_SIZE = 2048;
    private static final int NO_MARK = -1;
    private final ByteBuffer bbuf;
    private final CharBuffer cbuf;
    private final CharsetEncoder encoder;
    private int mark_bbuf;
    private int mark_cbuf;

    public int read(byte[] r5, int r6, int r7) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:34:0x0092 in {6, 11, 16, 21, 22, 23, 27, 28, 29, 31, 33} preds:[]
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
        r4 = this;
        if (r5 == 0) goto L_0x008a;
    L_0x0002:
        if (r7 < 0) goto L_0x0061;
    L_0x0004:
        r0 = r6 + r7;
        r1 = r5.length;
        if (r0 > r1) goto L_0x0061;
    L_0x0009:
        if (r7 != 0) goto L_0x000d;
    L_0x000b:
        r0 = 0;
        return r0;
    L_0x000d:
        r0 = r4.bbuf;
        r0 = r0.hasRemaining();
        r1 = -1;
        if (r0 != 0) goto L_0x001f;
    L_0x0016:
        r0 = r4.cbuf;
        r0 = r0.hasRemaining();
        if (r0 != 0) goto L_0x001f;
    L_0x001e:
        return r1;
        r0 = 0;
    L_0x0021:
        if (r7 <= 0) goto L_0x0053;
    L_0x0023:
        r2 = r4.bbuf;
        r2 = r2.hasRemaining();
        if (r2 == 0) goto L_0x003e;
    L_0x002b:
        r2 = r4.bbuf;
        r2 = r2.remaining();
        r2 = java.lang.Math.min(r2, r7);
        r3 = r4.bbuf;
        r3.get(r5, r6, r2);
        r6 = r6 + r2;
        r7 = r7 - r2;
        r0 = r0 + r2;
        goto L_0x0021;
    L_0x003e:
        r4.fillBuffer();
        r2 = r4.bbuf;
        r2 = r2.hasRemaining();
        if (r2 != 0) goto L_0x0052;
    L_0x0049:
        r2 = r4.cbuf;
        r2 = r2.hasRemaining();
        if (r2 != 0) goto L_0x0052;
    L_0x0051:
        goto L_0x0054;
    L_0x0052:
        goto L_0x0021;
    L_0x0054:
        if (r0 != 0) goto L_0x005f;
    L_0x0056:
        r2 = r4.cbuf;
        r2 = r2.hasRemaining();
        if (r2 != 0) goto L_0x005f;
    L_0x005e:
        goto L_0x0060;
    L_0x005f:
        r1 = r0;
    L_0x0060:
        return r1;
        r0 = new java.lang.IndexOutOfBoundsException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Array Size=";
        r1.append(r2);
        r2 = r5.length;
        r1.append(r2);
        r2 = ", offset=";
        r1.append(r2);
        r1.append(r6);
        r2 = ", length=";
        r1.append(r2);
        r1.append(r7);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
    L_0x008a:
        r0 = new java.lang.NullPointerException;
        r1 = "Byte array is null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.io.input.CharSequenceInputStream.read(byte[], int, int):int");
    }

    public synchronized void reset() throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:23:0x0085 in {9, 10, 11, 14, 16, 17, 19, 22} preds:[]
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
        r4 = this;
        monitor-enter(r4);
        r0 = r4.mark_cbuf;	 Catch:{ all -> 0x0082 }
        r1 = -1;	 Catch:{ all -> 0x0082 }
        if (r0 == r1) goto L_0x007f;	 Catch:{ all -> 0x0082 }
    L_0x0006:
        r0 = r4.cbuf;	 Catch:{ all -> 0x0082 }
        r0 = r0.position();	 Catch:{ all -> 0x0082 }
        if (r0 == 0) goto L_0x003c;	 Catch:{ all -> 0x0082 }
    L_0x000e:
        r0 = r4.encoder;	 Catch:{ all -> 0x0082 }
        r0.reset();	 Catch:{ all -> 0x0082 }
        r0 = r4.cbuf;	 Catch:{ all -> 0x0082 }
        r0.rewind();	 Catch:{ all -> 0x0082 }
        r0 = r4.bbuf;	 Catch:{ all -> 0x0082 }
        r0.rewind();	 Catch:{ all -> 0x0082 }
        r0 = r4.bbuf;	 Catch:{ all -> 0x0082 }
        r2 = 0;	 Catch:{ all -> 0x0082 }
        r0.limit(r2);	 Catch:{ all -> 0x0082 }
    L_0x0023:
        r0 = r4.cbuf;	 Catch:{ all -> 0x0082 }
        r0 = r0.position();	 Catch:{ all -> 0x0082 }
        r3 = r4.mark_cbuf;	 Catch:{ all -> 0x0082 }
        if (r0 >= r3) goto L_0x003b;	 Catch:{ all -> 0x0082 }
    L_0x002d:
        r0 = r4.bbuf;	 Catch:{ all -> 0x0082 }
        r0.rewind();	 Catch:{ all -> 0x0082 }
        r0 = r4.bbuf;	 Catch:{ all -> 0x0082 }
        r0.limit(r2);	 Catch:{ all -> 0x0082 }
        r4.fillBuffer();	 Catch:{ all -> 0x0082 }
        goto L_0x0023;	 Catch:{ all -> 0x0082 }
    L_0x003b:
        goto L_0x003d;	 Catch:{ all -> 0x0082 }
    L_0x003d:
        r0 = r4.cbuf;	 Catch:{ all -> 0x0082 }
        r0 = r0.position();	 Catch:{ all -> 0x0082 }
        r2 = r4.mark_cbuf;	 Catch:{ all -> 0x0082 }
        if (r0 != r2) goto L_0x0053;	 Catch:{ all -> 0x0082 }
    L_0x0047:
        r0 = r4.bbuf;	 Catch:{ all -> 0x0082 }
        r2 = r4.mark_bbuf;	 Catch:{ all -> 0x0082 }
        r0.position(r2);	 Catch:{ all -> 0x0082 }
        r4.mark_cbuf = r1;	 Catch:{ all -> 0x0082 }
        r4.mark_bbuf = r1;	 Catch:{ all -> 0x0082 }
        goto L_0x0080;	 Catch:{ all -> 0x0082 }
    L_0x0053:
        r0 = new java.lang.IllegalStateException;	 Catch:{ all -> 0x0082 }
        r1 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0082 }
        r1.<init>();	 Catch:{ all -> 0x0082 }
        r2 = "Unexpected CharBuffer postion: actual=";	 Catch:{ all -> 0x0082 }
        r1.append(r2);	 Catch:{ all -> 0x0082 }
        r2 = r4.cbuf;	 Catch:{ all -> 0x0082 }
        r2 = r2.position();	 Catch:{ all -> 0x0082 }
        r1.append(r2);	 Catch:{ all -> 0x0082 }
        r2 = " ";	 Catch:{ all -> 0x0082 }
        r1.append(r2);	 Catch:{ all -> 0x0082 }
        r2 = "expected=";	 Catch:{ all -> 0x0082 }
        r1.append(r2);	 Catch:{ all -> 0x0082 }
        r2 = r4.mark_cbuf;	 Catch:{ all -> 0x0082 }
        r1.append(r2);	 Catch:{ all -> 0x0082 }
        r1 = r1.toString();	 Catch:{ all -> 0x0082 }
        r0.<init>(r1);	 Catch:{ all -> 0x0082 }
        throw r0;	 Catch:{ all -> 0x0082 }
    L_0x0080:
        monitor-exit(r4);
        return;
    L_0x0082:
        r0 = move-exception;
        monitor-exit(r4);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.io.input.CharSequenceInputStream.reset():void");
    }

    public CharSequenceInputStream(CharSequence cs, Charset charset, int bufferSize) {
        this.encoder = charset.newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
        float maxBytesPerChar = this.encoder.maxBytesPerChar();
        if (((float) bufferSize) >= maxBytesPerChar) {
            this.bbuf = ByteBuffer.allocate(bufferSize);
            this.bbuf.flip();
            this.cbuf = CharBuffer.wrap(cs);
            this.mark_cbuf = -1;
            this.mark_bbuf = -1;
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Buffer size ");
        stringBuilder.append(bufferSize);
        stringBuilder.append(" is less than maxBytesPerChar ");
        stringBuilder.append(maxBytesPerChar);
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public CharSequenceInputStream(CharSequence cs, String charset, int bufferSize) {
        this(cs, Charset.forName(charset), bufferSize);
    }

    public CharSequenceInputStream(CharSequence cs, Charset charset) {
        this(cs, charset, 2048);
    }

    public CharSequenceInputStream(CharSequence cs, String charset) {
        this(cs, charset, 2048);
    }

    private void fillBuffer() throws CharacterCodingException {
        this.bbuf.compact();
        CoderResult result = this.encoder.encode(this.cbuf, this.bbuf, true);
        if (result.isError()) {
            result.throwException();
        }
        this.bbuf.flip();
    }

    public int read() throws IOException {
        while (!this.bbuf.hasRemaining()) {
            fillBuffer();
            if (!this.bbuf.hasRemaining() && !this.cbuf.hasRemaining()) {
                return -1;
            }
        }
        return this.bbuf.get() & 255;
    }

    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    public long skip(long n) throws IOException {
        long skipped = 0;
        while (n > 0 && available() > 0) {
            read();
            n--;
            skipped++;
        }
        return skipped;
    }

    public int available() throws IOException {
        return this.bbuf.remaining() + this.cbuf.remaining();
    }

    public void close() throws IOException {
    }

    public synchronized void mark(int readlimit) {
        this.mark_cbuf = this.cbuf.position();
        this.mark_bbuf = this.bbuf.position();
        this.cbuf.mark();
        this.bbuf.mark();
    }

    public boolean markSupported() {
        return true;
    }
}
