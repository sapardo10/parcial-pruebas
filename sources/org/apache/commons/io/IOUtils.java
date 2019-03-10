package org.apache.commons.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.CharArrayWriter;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.Selector;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.io.output.StringBuilderWriter;

public class IOUtils {
    private static final int DEFAULT_BUFFER_SIZE = 4096;
    public static final char DIR_SEPARATOR = File.separatorChar;
    public static final char DIR_SEPARATOR_UNIX = '/';
    public static final char DIR_SEPARATOR_WINDOWS = '\\';
    public static final int EOF = -1;
    public static final String LINE_SEPARATOR;
    public static final String LINE_SEPARATOR_UNIX = "\n";
    public static final String LINE_SEPARATOR_WINDOWS = "\r\n";
    private static final int SKIP_BUFFER_SIZE = 2048;
    private static byte[] SKIP_BYTE_BUFFER;
    private static char[] SKIP_CHAR_BUFFER;

    public static int read(java.io.InputStream r4, byte[] r5, int r6, int r7) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:12:0x002e in {5, 6, 7, 9, 11} preds:[]
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
        if (r7 < 0) goto L_0x0017;
    L_0x0002:
        r0 = r7;
    L_0x0003:
        if (r0 <= 0) goto L_0x0013;
    L_0x0005:
        r1 = r7 - r0;
        r2 = r6 + r1;
        r2 = r4.read(r5, r2, r0);
        r3 = -1;
        if (r3 != r2) goto L_0x0011;
    L_0x0010:
        goto L_0x0014;
    L_0x0011:
        r0 = r0 - r2;
        goto L_0x0003;
    L_0x0014:
        r1 = r7 - r0;
        return r1;
    L_0x0017:
        r0 = new java.lang.IllegalArgumentException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Length must not be negative: ";
        r1.append(r2);
        r1.append(r7);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.io.IOUtils.read(java.io.InputStream, byte[], int, int):int");
    }

    public static int read(java.io.Reader r4, char[] r5, int r6, int r7) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:12:0x002e in {5, 6, 7, 9, 11} preds:[]
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
        if (r7 < 0) goto L_0x0017;
    L_0x0002:
        r0 = r7;
    L_0x0003:
        if (r0 <= 0) goto L_0x0013;
    L_0x0005:
        r1 = r7 - r0;
        r2 = r6 + r1;
        r2 = r4.read(r5, r2, r0);
        r3 = -1;
        if (r3 != r2) goto L_0x0011;
    L_0x0010:
        goto L_0x0014;
    L_0x0011:
        r0 = r0 - r2;
        goto L_0x0003;
    L_0x0014:
        r1 = r7 - r0;
        return r1;
    L_0x0017:
        r0 = new java.lang.IllegalArgumentException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Length must not be negative: ";
        r1.append(r2);
        r1.append(r7);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.io.IOUtils.read(java.io.Reader, char[], int, int):int");
    }

    public static long skip(java.io.InputStream r8, long r9) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:18:0x0048 in {4, 5, 11, 12, 13, 15, 17} preds:[]
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
        r0 = 0;
        r2 = (r9 > r0 ? 1 : (r9 == r0 ? 0 : -1));
        if (r2 < 0) goto L_0x0031;
    L_0x0006:
        r2 = SKIP_BYTE_BUFFER;
        if (r2 != 0) goto L_0x0011;
    L_0x000a:
        r2 = 2048; // 0x800 float:2.87E-42 double:1.0118E-320;
        r2 = new byte[r2];
        SKIP_BYTE_BUFFER = r2;
        goto L_0x0012;
    L_0x0012:
        r2 = r9;
    L_0x0013:
        r4 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1));
        if (r4 <= 0) goto L_0x002d;
    L_0x0017:
        r4 = SKIP_BYTE_BUFFER;
        r5 = 0;
        r6 = 2048; // 0x800 float:2.87E-42 double:1.0118E-320;
        r6 = java.lang.Math.min(r2, r6);
        r6 = (int) r6;
        r4 = r8.read(r4, r5, r6);
        r4 = (long) r4;
        r6 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1));
        if (r6 >= 0) goto L_0x002b;
    L_0x002a:
        goto L_0x002e;
    L_0x002b:
        r2 = r2 - r4;
        goto L_0x0013;
    L_0x002e:
        r0 = r9 - r2;
        return r0;
    L_0x0031:
        r0 = new java.lang.IllegalArgumentException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Skip count must be non-negative, actual: ";
        r1.append(r2);
        r1.append(r9);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.io.IOUtils.skip(java.io.InputStream, long):long");
    }

    public static long skip(java.io.Reader r8, long r9) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:18:0x0048 in {4, 5, 11, 12, 13, 15, 17} preds:[]
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
        r0 = 0;
        r2 = (r9 > r0 ? 1 : (r9 == r0 ? 0 : -1));
        if (r2 < 0) goto L_0x0031;
    L_0x0006:
        r2 = SKIP_CHAR_BUFFER;
        if (r2 != 0) goto L_0x0011;
    L_0x000a:
        r2 = 2048; // 0x800 float:2.87E-42 double:1.0118E-320;
        r2 = new char[r2];
        SKIP_CHAR_BUFFER = r2;
        goto L_0x0012;
    L_0x0012:
        r2 = r9;
    L_0x0013:
        r4 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1));
        if (r4 <= 0) goto L_0x002d;
    L_0x0017:
        r4 = SKIP_CHAR_BUFFER;
        r5 = 0;
        r6 = 2048; // 0x800 float:2.87E-42 double:1.0118E-320;
        r6 = java.lang.Math.min(r2, r6);
        r6 = (int) r6;
        r4 = r8.read(r4, r5, r6);
        r4 = (long) r4;
        r6 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1));
        if (r6 >= 0) goto L_0x002b;
    L_0x002a:
        goto L_0x002e;
    L_0x002b:
        r2 = r2 - r4;
        goto L_0x0013;
    L_0x002e:
        r0 = r9 - r2;
        return r0;
    L_0x0031:
        r0 = new java.lang.IllegalArgumentException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Skip count must be non-negative, actual: ";
        r1.append(r2);
        r1.append(r9);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.io.IOUtils.skip(java.io.Reader, long):long");
    }

    public static long skip(java.nio.channels.ReadableByteChannel r10, long r11) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:14:0x0048 in {7, 8, 9, 11, 13} preds:[]
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
        r0 = 0;
        r2 = (r11 > r0 ? 1 : (r11 == r0 ? 0 : -1));
        if (r2 < 0) goto L_0x0031;
    L_0x0006:
        r2 = 2048; // 0x800 float:2.87E-42 double:1.0118E-320;
        r4 = java.lang.Math.min(r11, r2);
        r4 = (int) r4;
        r4 = java.nio.ByteBuffer.allocate(r4);
        r5 = r11;
    L_0x0012:
        r7 = (r5 > r0 ? 1 : (r5 == r0 ? 0 : -1));
        if (r7 <= 0) goto L_0x002d;
    L_0x0016:
        r7 = 0;
        r4.position(r7);
        r7 = java.lang.Math.min(r5, r2);
        r7 = (int) r7;
        r4.limit(r7);
        r7 = r10.read(r4);
        r8 = -1;
        if (r7 != r8) goto L_0x002a;
    L_0x0029:
        goto L_0x002e;
    L_0x002a:
        r8 = (long) r7;
        r5 = r5 - r8;
        goto L_0x0012;
    L_0x002e:
        r0 = r11 - r5;
        return r0;
    L_0x0031:
        r0 = new java.lang.IllegalArgumentException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Skip count must be non-negative, actual: ";
        r1.append(r2);
        r1.append(r11);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.io.IOUtils.skip(java.nio.channels.ReadableByteChannel, long):long");
    }

    public static byte[] toByteArray(java.io.InputStream r5, int r6) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:16:0x0053 in {3, 8, 11, 13, 15} preds:[]
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
        if (r6 < 0) goto L_0x003c;
    L_0x0002:
        if (r6 != 0) goto L_0x0008;
    L_0x0004:
        r0 = 0;
        r0 = new byte[r0];
        return r0;
    L_0x0008:
        r0 = new byte[r6];
        r1 = 0;
    L_0x000b:
        if (r1 >= r6) goto L_0x0019;
    L_0x000d:
        r2 = r6 - r1;
        r2 = r5.read(r0, r1, r2);
        r3 = r2;
        r4 = -1;
        if (r2 == r4) goto L_0x0019;
    L_0x0017:
        r1 = r1 + r3;
        goto L_0x000b;
        if (r1 != r6) goto L_0x001d;
    L_0x001c:
        return r0;
    L_0x001d:
        r2 = new java.io.IOException;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "Unexpected readed size. current: ";
        r3.append(r4);
        r3.append(r1);
        r4 = ", excepted: ";
        r3.append(r4);
        r3.append(r6);
        r3 = r3.toString();
        r2.<init>(r3);
        throw r2;
    L_0x003c:
        r0 = new java.lang.IllegalArgumentException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Size must be equal or greater than zero: ";
        r1.append(r2);
        r1.append(r6);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.io.IOUtils.toByteArray(java.io.InputStream, int):byte[]");
    }

    static {
        StringBuilderWriter buf = new StringBuilderWriter(4);
        PrintWriter out = new PrintWriter(buf);
        out.println();
        LINE_SEPARATOR = buf.toString();
        out.close();
    }

    public static void close(URLConnection conn) {
        if (conn instanceof HttpURLConnection) {
            ((HttpURLConnection) conn).disconnect();
        }
    }

    public static void closeQuietly(Reader input) {
        closeQuietly((Closeable) input);
    }

    public static void closeQuietly(Writer output) {
        closeQuietly((Closeable) output);
    }

    public static void closeQuietly(InputStream input) {
        closeQuietly((Closeable) input);
    }

    public static void closeQuietly(OutputStream output) {
        closeQuietly((Closeable) output);
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
            }
        }
    }

    public static void closeQuietly(Closeable... closeables) {
        if (closeables != null) {
            for (Closeable closeable : closeables) {
                closeQuietly(closeable);
            }
        }
    }

    public static void closeQuietly(Socket sock) {
        if (sock != null) {
            try {
                sock.close();
            } catch (IOException e) {
            }
        }
    }

    public static void closeQuietly(Selector selector) {
        if (selector != null) {
            try {
                selector.close();
            } catch (IOException e) {
            }
        }
    }

    public static void closeQuietly(ServerSocket sock) {
        if (sock != null) {
            try {
                sock.close();
            } catch (IOException e) {
            }
        }
    }

    public static InputStream toBufferedInputStream(InputStream input) throws IOException {
        return ByteArrayOutputStream.toBufferedInputStream(input);
    }

    public static InputStream toBufferedInputStream(InputStream input, int size) throws IOException {
        return ByteArrayOutputStream.toBufferedInputStream(input, size);
    }

    public static BufferedReader toBufferedReader(Reader reader) {
        return reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
    }

    public static BufferedReader toBufferedReader(Reader reader, int size) {
        return reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader, size);
    }

    public static BufferedReader buffer(Reader reader) {
        return reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
    }

    public static BufferedReader buffer(Reader reader, int size) {
        return reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader, size);
    }

    public static BufferedWriter buffer(Writer writer) {
        return writer instanceof BufferedWriter ? (BufferedWriter) writer : new BufferedWriter(writer);
    }

    public static BufferedWriter buffer(Writer writer, int size) {
        return writer instanceof BufferedWriter ? (BufferedWriter) writer : new BufferedWriter(writer, size);
    }

    public static BufferedOutputStream buffer(OutputStream outputStream) {
        if (outputStream != null) {
            return outputStream instanceof BufferedOutputStream ? (BufferedOutputStream) outputStream : new BufferedOutputStream(outputStream);
        } else {
            throw new NullPointerException();
        }
    }

    public static BufferedOutputStream buffer(OutputStream outputStream, int size) {
        if (outputStream != null) {
            return outputStream instanceof BufferedOutputStream ? (BufferedOutputStream) outputStream : new BufferedOutputStream(outputStream, size);
        } else {
            throw new NullPointerException();
        }
    }

    public static BufferedInputStream buffer(InputStream inputStream) {
        if (inputStream != null) {
            return inputStream instanceof BufferedInputStream ? (BufferedInputStream) inputStream : new BufferedInputStream(inputStream);
        } else {
            throw new NullPointerException();
        }
    }

    public static BufferedInputStream buffer(InputStream inputStream, int size) {
        if (inputStream != null) {
            return inputStream instanceof BufferedInputStream ? (BufferedInputStream) inputStream : new BufferedInputStream(inputStream, size);
        } else {
            throw new NullPointerException();
        }
    }

    public static byte[] toByteArray(InputStream input) throws IOException {
        OutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    public static byte[] toByteArray(InputStream input, long size) throws IOException {
        if (size <= 2147483647L) {
            return toByteArray(input, (int) size);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Size cannot be greater than Integer max value: ");
        stringBuilder.append(size);
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    @Deprecated
    public static byte[] toByteArray(Reader input) throws IOException {
        return toByteArray(input, Charset.defaultCharset());
    }

    public static byte[] toByteArray(Reader input, Charset encoding) throws IOException {
        OutputStream output = new ByteArrayOutputStream();
        copy(input, output, encoding);
        return output.toByteArray();
    }

    public static byte[] toByteArray(Reader input, String encoding) throws IOException {
        return toByteArray(input, Charsets.toCharset(encoding));
    }

    @Deprecated
    public static byte[] toByteArray(String input) throws IOException {
        return input.getBytes(Charset.defaultCharset());
    }

    public static byte[] toByteArray(URI uri) throws IOException {
        return toByteArray(uri.toURL());
    }

    public static byte[] toByteArray(URL url) throws IOException {
        URLConnection conn = url.openConnection();
        try {
            byte[] toByteArray = toByteArray(conn);
            return toByteArray;
        } finally {
            close(conn);
        }
    }

    public static byte[] toByteArray(URLConnection urlConn) throws IOException {
        InputStream inputStream = urlConn.getInputStream();
        try {
            byte[] toByteArray = toByteArray(inputStream);
            return toByteArray;
        } finally {
            inputStream.close();
        }
    }

    @Deprecated
    public static char[] toCharArray(InputStream is) throws IOException {
        return toCharArray(is, Charset.defaultCharset());
    }

    public static char[] toCharArray(InputStream is, Charset encoding) throws IOException {
        Writer output = new CharArrayWriter();
        copy(is, output, encoding);
        return output.toCharArray();
    }

    public static char[] toCharArray(InputStream is, String encoding) throws IOException {
        return toCharArray(is, Charsets.toCharset(encoding));
    }

    public static char[] toCharArray(Reader input) throws IOException {
        Writer sw = new CharArrayWriter();
        copy(input, sw);
        return sw.toCharArray();
    }

    @Deprecated
    public static String toString(InputStream input) throws IOException {
        return toString(input, Charset.defaultCharset());
    }

    public static String toString(InputStream input, Charset encoding) throws IOException {
        Writer sw = new StringBuilderWriter();
        copy(input, sw, encoding);
        return sw.toString();
    }

    public static String toString(InputStream input, String encoding) throws IOException {
        return toString(input, Charsets.toCharset(encoding));
    }

    public static String toString(Reader input) throws IOException {
        Writer sw = new StringBuilderWriter();
        copy(input, sw);
        return sw.toString();
    }

    @Deprecated
    public static String toString(URI uri) throws IOException {
        return toString(uri, Charset.defaultCharset());
    }

    public static String toString(URI uri, Charset encoding) throws IOException {
        return toString(uri.toURL(), Charsets.toCharset(encoding));
    }

    public static String toString(URI uri, String encoding) throws IOException {
        return toString(uri, Charsets.toCharset(encoding));
    }

    @Deprecated
    public static String toString(URL url) throws IOException {
        return toString(url, Charset.defaultCharset());
    }

    public static String toString(URL url, Charset encoding) throws IOException {
        InputStream inputStream = url.openStream();
        try {
            String iOUtils = toString(inputStream, encoding);
            return iOUtils;
        } finally {
            inputStream.close();
        }
    }

    public static String toString(URL url, String encoding) throws IOException {
        return toString(url, Charsets.toCharset(encoding));
    }

    @Deprecated
    public static String toString(byte[] input) throws IOException {
        return new String(input, Charset.defaultCharset());
    }

    public static String toString(byte[] input, String encoding) throws IOException {
        return new String(input, Charsets.toCharset(encoding));
    }

    @Deprecated
    public static List<String> readLines(InputStream input) throws IOException {
        return readLines(input, Charset.defaultCharset());
    }

    public static List<String> readLines(InputStream input, Charset encoding) throws IOException {
        return readLines(new InputStreamReader(input, Charsets.toCharset(encoding)));
    }

    public static List<String> readLines(InputStream input, String encoding) throws IOException {
        return readLines(input, Charsets.toCharset(encoding));
    }

    public static List<String> readLines(Reader input) throws IOException {
        BufferedReader reader = toBufferedReader(input);
        List<String> list = new ArrayList();
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            list.add(line);
        }
        return list;
    }

    public static LineIterator lineIterator(Reader reader) {
        return new LineIterator(reader);
    }

    public static LineIterator lineIterator(InputStream input, Charset encoding) throws IOException {
        return new LineIterator(new InputStreamReader(input, Charsets.toCharset(encoding)));
    }

    public static LineIterator lineIterator(InputStream input, String encoding) throws IOException {
        return lineIterator(input, Charsets.toCharset(encoding));
    }

    @Deprecated
    public static InputStream toInputStream(CharSequence input) {
        return toInputStream(input, Charset.defaultCharset());
    }

    public static InputStream toInputStream(CharSequence input, Charset encoding) {
        return toInputStream(input.toString(), encoding);
    }

    public static InputStream toInputStream(CharSequence input, String encoding) throws IOException {
        return toInputStream(input, Charsets.toCharset(encoding));
    }

    @Deprecated
    public static InputStream toInputStream(String input) {
        return toInputStream(input, Charset.defaultCharset());
    }

    public static InputStream toInputStream(String input, Charset encoding) {
        return new ByteArrayInputStream(input.getBytes(Charsets.toCharset(encoding)));
    }

    public static InputStream toInputStream(String input, String encoding) throws IOException {
        return new ByteArrayInputStream(input.getBytes(Charsets.toCharset(encoding)));
    }

    public static void write(byte[] data, OutputStream output) throws IOException {
        if (data != null) {
            output.write(data);
        }
    }

    public static void writeChunked(byte[] data, OutputStream output) throws IOException {
        if (data != null) {
            int bytes = data.length;
            int offset = 0;
            while (bytes > 0) {
                int chunk = Math.min(bytes, 4096);
                output.write(data, offset, chunk);
                bytes -= chunk;
                offset += chunk;
            }
        }
    }

    @Deprecated
    public static void write(byte[] data, Writer output) throws IOException {
        write(data, output, Charset.defaultCharset());
    }

    public static void write(byte[] data, Writer output, Charset encoding) throws IOException {
        if (data != null) {
            output.write(new String(data, Charsets.toCharset(encoding)));
        }
    }

    public static void write(byte[] data, Writer output, String encoding) throws IOException {
        write(data, output, Charsets.toCharset(encoding));
    }

    public static void write(char[] data, Writer output) throws IOException {
        if (data != null) {
            output.write(data);
        }
    }

    public static void writeChunked(char[] data, Writer output) throws IOException {
        if (data != null) {
            int bytes = data.length;
            int offset = 0;
            while (bytes > 0) {
                int chunk = Math.min(bytes, 4096);
                output.write(data, offset, chunk);
                bytes -= chunk;
                offset += chunk;
            }
        }
    }

    @Deprecated
    public static void write(char[] data, OutputStream output) throws IOException {
        write(data, output, Charset.defaultCharset());
    }

    public static void write(char[] data, OutputStream output, Charset encoding) throws IOException {
        if (data != null) {
            output.write(new String(data).getBytes(Charsets.toCharset(encoding)));
        }
    }

    public static void write(char[] data, OutputStream output, String encoding) throws IOException {
        write(data, output, Charsets.toCharset(encoding));
    }

    public static void write(CharSequence data, Writer output) throws IOException {
        if (data != null) {
            write(data.toString(), output);
        }
    }

    @Deprecated
    public static void write(CharSequence data, OutputStream output) throws IOException {
        write(data, output, Charset.defaultCharset());
    }

    public static void write(CharSequence data, OutputStream output, Charset encoding) throws IOException {
        if (data != null) {
            write(data.toString(), output, encoding);
        }
    }

    public static void write(CharSequence data, OutputStream output, String encoding) throws IOException {
        write(data, output, Charsets.toCharset(encoding));
    }

    public static void write(String data, Writer output) throws IOException {
        if (data != null) {
            output.write(data);
        }
    }

    @Deprecated
    public static void write(String data, OutputStream output) throws IOException {
        write(data, output, Charset.defaultCharset());
    }

    public static void write(String data, OutputStream output, Charset encoding) throws IOException {
        if (data != null) {
            output.write(data.getBytes(Charsets.toCharset(encoding)));
        }
    }

    public static void write(String data, OutputStream output, String encoding) throws IOException {
        write(data, output, Charsets.toCharset(encoding));
    }

    @Deprecated
    public static void write(StringBuffer data, Writer output) throws IOException {
        if (data != null) {
            output.write(data.toString());
        }
    }

    @Deprecated
    public static void write(StringBuffer data, OutputStream output) throws IOException {
        write(data, output, (String) null);
    }

    @Deprecated
    public static void write(StringBuffer data, OutputStream output, String encoding) throws IOException {
        if (data != null) {
            output.write(data.toString().getBytes(Charsets.toCharset(encoding)));
        }
    }

    @Deprecated
    public static void writeLines(Collection<?> lines, String lineEnding, OutputStream output) throws IOException {
        writeLines((Collection) lines, lineEnding, output, Charset.defaultCharset());
    }

    public static void writeLines(Collection<?> lines, String lineEnding, OutputStream output, Charset encoding) throws IOException {
        if (lines != null) {
            if (lineEnding == null) {
                lineEnding = LINE_SEPARATOR;
            }
            Charset cs = Charsets.toCharset(encoding);
            for (Object line : lines) {
                if (line != null) {
                    output.write(line.toString().getBytes(cs));
                }
                output.write(lineEnding.getBytes(cs));
            }
        }
    }

    public static void writeLines(Collection<?> lines, String lineEnding, OutputStream output, String encoding) throws IOException {
        writeLines((Collection) lines, lineEnding, output, Charsets.toCharset(encoding));
    }

    public static void writeLines(Collection<?> lines, String lineEnding, Writer writer) throws IOException {
        if (lines != null) {
            if (lineEnding == null) {
                lineEnding = LINE_SEPARATOR;
            }
            for (Object line : lines) {
                if (line != null) {
                    writer.write(line.toString());
                }
                writer.write(lineEnding);
            }
        }
    }

    public static int copy(InputStream input, OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        if (count > 2147483647L) {
            return -1;
        }
        return (int) count;
    }

    public static long copy(InputStream input, OutputStream output, int bufferSize) throws IOException {
        return copyLarge(input, output, new byte[bufferSize]);
    }

    public static long copyLarge(InputStream input, OutputStream output) throws IOException {
        return copy(input, output, 4096);
    }

    public static long copyLarge(InputStream input, OutputStream output, byte[] buffer) throws IOException {
        long count = 0;
        while (true) {
            int read = input.read(buffer);
            int n = read;
            if (-1 == read) {
                return count;
            }
            output.write(buffer, 0, n);
            count += (long) n;
        }
    }

    public static long copyLarge(InputStream input, OutputStream output, long inputOffset, long length) throws IOException {
        return copyLarge(input, output, inputOffset, length, new byte[4096]);
    }

    public static long copyLarge(InputStream input, OutputStream output, long inputOffset, long length, byte[] buffer) throws IOException {
        InputStream inputStream = input;
        long j = inputOffset;
        long j2 = length;
        byte[] bArr = buffer;
        long j3 = 0;
        if (j > 0) {
            skipFully(inputStream, j);
        }
        if (j2 == 0) {
            return 0;
        }
        int bufferLength = bArr.length;
        int bytesToRead = bufferLength;
        if (j2 > 0 && j2 < ((long) bufferLength)) {
            bytesToRead = (int) j2;
        }
        long totalRead = 0;
        while (bytesToRead > 0) {
            int read = inputStream.read(bArr, 0, bytesToRead);
            int read2 = read;
            if (-1 == read) {
                break;
            }
            output.write(bArr, 0, read2);
            totalRead += (long) read2;
            if (j2 > j3) {
                bytesToRead = (int) Math.min(j2 - totalRead, (long) bufferLength);
                j3 = 0;
            } else {
                j3 = 0;
            }
        }
        OutputStream outputStream = output;
        return totalRead;
    }

    @Deprecated
    public static void copy(InputStream input, Writer output) throws IOException {
        copy(input, output, Charset.defaultCharset());
    }

    public static void copy(InputStream input, Writer output, Charset inputEncoding) throws IOException {
        copy(new InputStreamReader(input, Charsets.toCharset(inputEncoding)), output);
    }

    public static void copy(InputStream input, Writer output, String inputEncoding) throws IOException {
        copy(input, output, Charsets.toCharset(inputEncoding));
    }

    public static int copy(Reader input, Writer output) throws IOException {
        long count = copyLarge(input, output);
        if (count > 2147483647L) {
            return -1;
        }
        return (int) count;
    }

    public static long copyLarge(Reader input, Writer output) throws IOException {
        return copyLarge(input, output, new char[4096]);
    }

    public static long copyLarge(Reader input, Writer output, char[] buffer) throws IOException {
        long count = 0;
        while (true) {
            int read = input.read(buffer);
            int n = read;
            if (-1 == read) {
                return count;
            }
            output.write(buffer, 0, n);
            count += (long) n;
        }
    }

    public static long copyLarge(Reader input, Writer output, long inputOffset, long length) throws IOException {
        return copyLarge(input, output, inputOffset, length, new char[4096]);
    }

    public static long copyLarge(Reader input, Writer output, long inputOffset, long length, char[] buffer) throws IOException {
        Reader reader = input;
        long j = inputOffset;
        long j2 = length;
        char[] cArr = buffer;
        long j3 = 0;
        if (j > 0) {
            skipFully(reader, j);
        }
        if (j2 == 0) {
            return 0;
        }
        int bytesToRead = cArr.length;
        if (j2 > 0 && j2 < ((long) cArr.length)) {
            bytesToRead = (int) j2;
        }
        long totalRead = 0;
        while (bytesToRead > 0) {
            int read = reader.read(cArr, 0, bytesToRead);
            int read2 = read;
            if (-1 == read) {
                break;
            }
            output.write(cArr, 0, read2);
            totalRead += (long) read2;
            if (j2 > j3) {
                bytesToRead = (int) Math.min(j2 - totalRead, (long) cArr.length);
                j3 = 0;
            } else {
                j3 = 0;
            }
        }
        Writer writer = output;
        return totalRead;
    }

    @Deprecated
    public static void copy(Reader input, OutputStream output) throws IOException {
        copy(input, output, Charset.defaultCharset());
    }

    public static void copy(Reader input, OutputStream output, Charset outputEncoding) throws IOException {
        Writer out = new OutputStreamWriter(output, Charsets.toCharset(outputEncoding));
        copy(input, out);
        out.flush();
    }

    public static void copy(Reader input, OutputStream output, String outputEncoding) throws IOException {
        copy(input, output, Charsets.toCharset(outputEncoding));
    }

    public static boolean contentEquals(InputStream input1, InputStream input2) throws IOException {
        boolean z = true;
        if (input1 == input2) {
            return true;
        }
        if (!(input1 instanceof BufferedInputStream)) {
            input1 = new BufferedInputStream(input1);
        }
        if (!(input2 instanceof BufferedInputStream)) {
            input2 = new BufferedInputStream(input2);
        }
        for (int ch = input1.read(); -1 != ch; ch = input1.read()) {
            if (ch != input2.read()) {
                return false;
            }
        }
        if (input2.read() != -1) {
            z = false;
        }
        return z;
    }

    public static boolean contentEquals(Reader input1, Reader input2) throws IOException {
        boolean z = true;
        if (input1 == input2) {
            return true;
        }
        input1 = toBufferedReader(input1);
        input2 = toBufferedReader(input2);
        for (int ch = input1.read(); -1 != ch; ch = input1.read()) {
            if (ch != input2.read()) {
                return false;
            }
        }
        if (input2.read() != -1) {
            z = false;
        }
        return z;
    }

    public static boolean contentEqualsIgnoreEOL(Reader input1, Reader input2) throws IOException {
        boolean z = true;
        if (input1 == input2) {
            return true;
        }
        BufferedReader br1 = toBufferedReader(input1);
        BufferedReader br2 = toBufferedReader(input2);
        String line1 = br1.readLine();
        String line2 = br2.readLine();
        while (line1 != null && line2 != null && line1.equals(line2)) {
            line1 = br1.readLine();
            line2 = br2.readLine();
        }
        if (line1 != null) {
            z = line1.equals(line2);
        } else if (line2 != null) {
            z = false;
        }
        return z;
    }

    public static void skipFully(InputStream input, long toSkip) throws IOException {
        if (toSkip >= 0) {
            long skipped = skip(input, toSkip);
            if (skipped != toSkip) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Bytes to skip: ");
                stringBuilder.append(toSkip);
                stringBuilder.append(" actual: ");
                stringBuilder.append(skipped);
                throw new EOFException(stringBuilder.toString());
            }
            return;
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("Bytes to skip must not be negative: ");
        stringBuilder2.append(toSkip);
        throw new IllegalArgumentException(stringBuilder2.toString());
    }

    public static void skipFully(ReadableByteChannel input, long toSkip) throws IOException {
        if (toSkip >= 0) {
            long skipped = skip(input, toSkip);
            if (skipped != toSkip) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Bytes to skip: ");
                stringBuilder.append(toSkip);
                stringBuilder.append(" actual: ");
                stringBuilder.append(skipped);
                throw new EOFException(stringBuilder.toString());
            }
            return;
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("Bytes to skip must not be negative: ");
        stringBuilder2.append(toSkip);
        throw new IllegalArgumentException(stringBuilder2.toString());
    }

    public static void skipFully(Reader input, long toSkip) throws IOException {
        long skipped = skip(input, toSkip);
        if (skipped != toSkip) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Chars to skip: ");
            stringBuilder.append(toSkip);
            stringBuilder.append(" actual: ");
            stringBuilder.append(skipped);
            throw new EOFException(stringBuilder.toString());
        }
    }

    public static int read(Reader input, char[] buffer) throws IOException {
        return read(input, buffer, 0, buffer.length);
    }

    public static int read(InputStream input, byte[] buffer) throws IOException {
        return read(input, buffer, 0, buffer.length);
    }

    public static int read(ReadableByteChannel input, ByteBuffer buffer) throws IOException {
        int length = buffer.remaining();
        while (buffer.remaining() > 0) {
            if (-1 == input.read(buffer)) {
                break;
            }
        }
        return length - buffer.remaining();
    }

    public static void readFully(Reader input, char[] buffer, int offset, int length) throws IOException {
        int actual = read(input, buffer, offset, length);
        if (actual != length) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Length to read: ");
            stringBuilder.append(length);
            stringBuilder.append(" actual: ");
            stringBuilder.append(actual);
            throw new EOFException(stringBuilder.toString());
        }
    }

    public static void readFully(Reader input, char[] buffer) throws IOException {
        readFully(input, buffer, 0, buffer.length);
    }

    public static void readFully(InputStream input, byte[] buffer, int offset, int length) throws IOException {
        int actual = read(input, buffer, offset, length);
        if (actual != length) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Length to read: ");
            stringBuilder.append(length);
            stringBuilder.append(" actual: ");
            stringBuilder.append(actual);
            throw new EOFException(stringBuilder.toString());
        }
    }

    public static void readFully(InputStream input, byte[] buffer) throws IOException {
        readFully(input, buffer, 0, buffer.length);
    }

    public static byte[] readFully(InputStream input, int length) throws IOException {
        byte[] buffer = new byte[length];
        readFully(input, buffer, 0, buffer.length);
        return buffer;
    }

    public static void readFully(ReadableByteChannel input, ByteBuffer buffer) throws IOException {
        int expected = buffer.remaining();
        int actual = read(input, buffer);
        if (actual != expected) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Length to read: ");
            stringBuilder.append(expected);
            stringBuilder.append(" actual: ");
            stringBuilder.append(actual);
            throw new EOFException(stringBuilder.toString());
        }
    }
}
