package org.apache.commons.io.input;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

public class ReaderInputStream extends InputStream {
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private final CharsetEncoder encoder;
    private final CharBuffer encoderIn;
    private final ByteBuffer encoderOut;
    private boolean endOfInput;
    private CoderResult lastCoderResult;
    private final Reader reader;

    public int read(byte[] r4, int r5, int r6) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:31:0x007b in {8, 13, 18, 19, 20, 24, 25, 26, 28, 30} preds:[]
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
        r3 = this;
        if (r4 == 0) goto L_0x0073;
    L_0x0002:
        if (r6 < 0) goto L_0x004a;
    L_0x0004:
        if (r5 < 0) goto L_0x004a;
    L_0x0006:
        r0 = r5 + r6;
        r1 = r4.length;
        if (r0 > r1) goto L_0x004a;
    L_0x000b:
        r0 = 0;
        if (r6 != 0) goto L_0x0010;
    L_0x000e:
        r1 = 0;
        return r1;
    L_0x0011:
        if (r6 <= 0) goto L_0x003f;
    L_0x0013:
        r1 = r3.encoderOut;
        r1 = r1.hasRemaining();
        if (r1 == 0) goto L_0x002e;
    L_0x001b:
        r1 = r3.encoderOut;
        r1 = r1.remaining();
        r1 = java.lang.Math.min(r1, r6);
        r2 = r3.encoderOut;
        r2.get(r4, r5, r1);
        r5 = r5 + r1;
        r6 = r6 - r1;
        r0 = r0 + r1;
        goto L_0x0011;
    L_0x002e:
        r3.fillBuffer();
        r1 = r3.endOfInput;
        if (r1 == 0) goto L_0x003e;
    L_0x0035:
        r1 = r3.encoderOut;
        r1 = r1.hasRemaining();
        if (r1 != 0) goto L_0x003e;
    L_0x003d:
        goto L_0x0040;
    L_0x003e:
        goto L_0x0011;
    L_0x0040:
        if (r0 != 0) goto L_0x0048;
    L_0x0042:
        r1 = r3.endOfInput;
        if (r1 == 0) goto L_0x0048;
    L_0x0046:
        r1 = -1;
        goto L_0x0049;
    L_0x0048:
        r1 = r0;
    L_0x0049:
        return r1;
        r0 = new java.lang.IndexOutOfBoundsException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Array Size=";
        r1.append(r2);
        r2 = r4.length;
        r1.append(r2);
        r2 = ", offset=";
        r1.append(r2);
        r1.append(r5);
        r2 = ", length=";
        r1.append(r2);
        r1.append(r6);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
    L_0x0073:
        r0 = new java.lang.NullPointerException;
        r1 = "Byte array must not be null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.io.input.ReaderInputStream.read(byte[], int, int):int");
    }

    public ReaderInputStream(Reader reader, CharsetEncoder encoder) {
        this(reader, encoder, 1024);
    }

    public ReaderInputStream(Reader reader, CharsetEncoder encoder, int bufferSize) {
        this.reader = reader;
        this.encoder = encoder;
        this.encoderIn = CharBuffer.allocate(bufferSize);
        this.encoderIn.flip();
        this.encoderOut = ByteBuffer.allocate(128);
        this.encoderOut.flip();
    }

    public ReaderInputStream(Reader reader, Charset charset, int bufferSize) {
        this(reader, charset.newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE), bufferSize);
    }

    public ReaderInputStream(Reader reader, Charset charset) {
        this(reader, charset, 1024);
    }

    public ReaderInputStream(Reader reader, String charsetName, int bufferSize) {
        this(reader, Charset.forName(charsetName), bufferSize);
    }

    public ReaderInputStream(Reader reader, String charsetName) {
        this(reader, charsetName, 1024);
    }

    @Deprecated
    public ReaderInputStream(Reader reader) {
        this(reader, Charset.defaultCharset());
    }

    private void fillBuffer() throws IOException {
        if (!this.endOfInput) {
            CoderResult coderResult = this.lastCoderResult;
            if (coderResult == null || coderResult.isUnderflow()) {
                this.encoderIn.compact();
                int position = this.encoderIn.position();
                int c = this.reader.read(this.encoderIn.array(), position, this.encoderIn.remaining());
                if (c == -1) {
                    this.endOfInput = true;
                } else {
                    this.encoderIn.position(position + c);
                }
                this.encoderIn.flip();
                this.encoderOut.compact();
                this.lastCoderResult = this.encoder.encode(this.encoderIn, this.encoderOut, this.endOfInput);
                this.encoderOut.flip();
            }
        }
        this.encoderOut.compact();
        this.lastCoderResult = this.encoder.encode(this.encoderIn, this.encoderOut, this.endOfInput);
        this.encoderOut.flip();
    }

    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    public int read() throws IOException {
        while (!this.encoderOut.hasRemaining()) {
            fillBuffer();
            if (this.endOfInput && !this.encoderOut.hasRemaining()) {
                return -1;
            }
        }
        return this.encoderOut.get() & 255;
    }

    public void close() throws IOException {
        this.reader.close();
    }
}
