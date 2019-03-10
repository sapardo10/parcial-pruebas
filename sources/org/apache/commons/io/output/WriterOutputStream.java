package org.apache.commons.io.output;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

public class WriterOutputStream extends OutputStream {
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private final CharsetDecoder decoder;
    private final ByteBuffer decoderIn;
    private final CharBuffer decoderOut;
    private final boolean writeImmediately;
    private final Writer writer;

    private static void checkIbmJdkWithBrokenUTF16(java.nio.charset.Charset r10) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:20:0x0068 in {2, 7, 8, 11, 14, 17, 19} preds:[]
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
        r0 = "UTF-16";
        r1 = r10.name();
        r0 = r0.equals(r1);
        if (r0 != 0) goto L_0x000d;
    L_0x000c:
        return;
    L_0x000d:
        r0 = "vés";
        r1 = "vés";
        r1 = r1.getBytes(r10);
        r2 = r10.newDecoder();
        r3 = 16;
        r3 = java.nio.ByteBuffer.allocate(r3);
        r4 = "vés";
        r4 = r4.length();
        r4 = java.nio.CharBuffer.allocate(r4);
        r5 = r1.length;
        r6 = 0;
    L_0x002b:
        if (r6 >= r5) goto L_0x004f;
    L_0x002d:
        r7 = r1[r6];
        r3.put(r7);
        r3.flip();
        r7 = r5 + -1;
        if (r6 != r7) goto L_0x003b;
    L_0x0039:
        r7 = 1;
        goto L_0x003c;
    L_0x003b:
        r7 = 0;
    L_0x003c:
        r2.decode(r3, r4, r7);	 Catch:{ IllegalArgumentException -> 0x0046 }
        r3.compact();
        r6 = r6 + 1;
        goto L_0x002b;
    L_0x0046:
        r7 = move-exception;
        r8 = new java.lang.UnsupportedOperationException;
        r9 = "UTF-16 requested when runninng on an IBM JDK with broken UTF-16 support. Please find a JDK that supports UTF-16 if you intend to use UF-16 with WriterOutputStream";
        r8.<init>(r9);
        throw r8;
        r4.rewind();
        r6 = "vés";
        r7 = r4.toString();
        r6 = r6.equals(r7);
        if (r6 == 0) goto L_0x0060;
    L_0x005f:
        return;
    L_0x0060:
        r6 = new java.lang.UnsupportedOperationException;
        r7 = "UTF-16 requested when runninng on an IBM JDK with broken UTF-16 support. Please find a JDK that supports UTF-16 if you intend to use UF-16 with WriterOutputStream";
        r6.<init>(r7);
        throw r6;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.io.output.WriterOutputStream.checkIbmJdkWithBrokenUTF16(java.nio.charset.Charset):void");
    }

    private void processInput(boolean r4) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:10:0x002e in {3, 7, 9} preds:[]
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
        r0 = r3.decoderIn;
        r0.flip();
    L_0x0005:
        r0 = r3.decoder;
        r1 = r3.decoderIn;
        r2 = r3.decoderOut;
        r0 = r0.decode(r1, r2, r4);
        r1 = r0.isOverflow();
        if (r1 == 0) goto L_0x0019;
    L_0x0015:
        r3.flushOutput();
        goto L_0x0005;
    L_0x0019:
        r1 = r0.isUnderflow();
        if (r1 == 0) goto L_0x0026;
        r1 = r3.decoderIn;
        r1.compact();
        return;
    L_0x0026:
        r1 = new java.io.IOException;
        r2 = "Unexpected coder result";
        r1.<init>(r2);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.io.output.WriterOutputStream.processInput(boolean):void");
    }

    public WriterOutputStream(Writer writer, CharsetDecoder decoder) {
        this(writer, decoder, 1024, false);
    }

    public WriterOutputStream(Writer writer, CharsetDecoder decoder, int bufferSize, boolean writeImmediately) {
        this.decoderIn = ByteBuffer.allocate(128);
        checkIbmJdkWithBrokenUTF16(decoder.charset());
        this.writer = writer;
        this.decoder = decoder;
        this.writeImmediately = writeImmediately;
        this.decoderOut = CharBuffer.allocate(bufferSize);
    }

    public WriterOutputStream(Writer writer, Charset charset, int bufferSize, boolean writeImmediately) {
        this(writer, charset.newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE).replaceWith("?"), bufferSize, writeImmediately);
    }

    public WriterOutputStream(Writer writer, Charset charset) {
        this(writer, charset, 1024, false);
    }

    public WriterOutputStream(Writer writer, String charsetName, int bufferSize, boolean writeImmediately) {
        this(writer, Charset.forName(charsetName), bufferSize, writeImmediately);
    }

    public WriterOutputStream(Writer writer, String charsetName) {
        this(writer, charsetName, 1024, false);
    }

    @Deprecated
    public WriterOutputStream(Writer writer) {
        this(writer, Charset.defaultCharset(), 1024, false);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        while (len > 0) {
            int c = Math.min(len, this.decoderIn.remaining());
            this.decoderIn.put(b, off, c);
            processInput(false);
            len -= c;
            off += c;
        }
        if (this.writeImmediately) {
            flushOutput();
        }
    }

    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    public void write(int b) throws IOException {
        write(new byte[]{(byte) b}, 0, 1);
    }

    public void flush() throws IOException {
        flushOutput();
        this.writer.flush();
    }

    public void close() throws IOException {
        processInput(true);
        flushOutput();
        this.writer.close();
    }

    private void flushOutput() throws IOException {
        if (this.decoderOut.position() > 0) {
            this.writer.write(this.decoderOut.array(), 0, this.decoderOut.position());
            this.decoderOut.rewind();
        }
    }
}
