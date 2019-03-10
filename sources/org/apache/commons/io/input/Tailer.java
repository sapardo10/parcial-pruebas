package org.apache.commons.io.input;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import org.apache.commons.io.IOUtils;

public class Tailer implements Runnable {
    private static final int DEFAULT_BUFSIZE = 4096;
    private static final Charset DEFAULT_CHARSET = Charset.defaultCharset();
    private static final int DEFAULT_DELAY_MILLIS = 1000;
    private static final String RAF_MODE = "r";
    private final Charset cset;
    private final long delayMillis;
    private final boolean end;
    private final File file;
    private final byte[] inbuf;
    private final TailerListener listener;
    private final boolean reOpen;
    private volatile boolean run;

    public void run() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:84:0x0156 in {11, 15, 18, 21, 22, 23, 36, 40, 42, 46, 49, 52, 53, 56, 57, 63, 64, 65, 66, 73, 79, 81, 83} preds:[]
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
        r12 = this;
        r0 = 0;
        r1 = 0;
        r3 = 0;
        r5 = r3;
        r7 = r12.getRun();	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        if (r7 == 0) goto L_0x005e;
        if (r0 != 0) goto L_0x005e;
        r7 = new java.io.RandomAccessFile;	 Catch:{ FileNotFoundException -> 0x0023 }
        r8 = r12.file;	 Catch:{ FileNotFoundException -> 0x0023 }
        r9 = "r";	 Catch:{ FileNotFoundException -> 0x0023 }
        r7.<init>(r8, r9);	 Catch:{ FileNotFoundException -> 0x0023 }
        r0 = r7;
        goto L_0x002d;
    L_0x0023:
        r7 = move-exception;
        r8 = r12.listener;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r8.fileNotFound();	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        if (r0 != 0) goto L_0x0039;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r7 = r12.delayMillis;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        java.lang.Thread.sleep(r7);	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        goto L_0x0007;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r7 = r12.end;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        if (r7 == 0) goto L_0x0049;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r7 = r12.file;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r7 = r7.length();	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        goto L_0x004c;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r7 = r3;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r5 = r7;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r7 = r12.file;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r7 = r7.lastModified();	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r1 = r7;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r0.seek(r5);	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        goto L_0x0007;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r3 = r12.getRun();	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        if (r3 == 0) goto L_0x012d;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r3 = r12.file;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r3 = org.apache.commons.io.FileUtils.isFileNewer(r3, r1);	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r4 = r12.file;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r7 = r4.length();	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r4 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1));	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        if (r4 >= 0) goto L_0x00bc;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r4 = r12.listener;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r4.fileRotated();	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r4 = r0;
        r9 = new java.io.RandomAccessFile;	 Catch:{ FileNotFoundException -> 0x00b0 }
        r10 = r12.file;	 Catch:{ FileNotFoundException -> 0x00b0 }
        r11 = "r";	 Catch:{ FileNotFoundException -> 0x00b0 }
        r9.<init>(r10, r11);	 Catch:{ FileNotFoundException -> 0x00b0 }
        r0 = r9;
        r12.readLines(r4);	 Catch:{ IOException -> 0x009c }
        goto L_0x00a6;
    L_0x009c:
        r9 = move-exception;
        r10 = r12.listener;	 Catch:{ FileNotFoundException -> 0x00b0 }
        r10.handle(r9);	 Catch:{ FileNotFoundException -> 0x00b0 }
        r5 = 0;	 Catch:{ FileNotFoundException -> 0x00b0 }
        org.apache.commons.io.IOUtils.closeQuietly(r4);	 Catch:{ FileNotFoundException -> 0x00b0 }
        goto L_0x005f;
    L_0x00b0:
        r4 = move-exception;
        r9 = r12.listener;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r9.fileNotFound();	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        goto L_0x005f;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r4 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1));	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        if (r4 <= 0) goto L_0x00d4;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r9 = r12.readLines(r0);	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r4 = r9;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r6 = r12.file;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r9 = r6.lastModified();	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r1 = r9;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r5 = r4;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        goto L_0x00f2;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        if (r3 == 0) goto L_0x00f1;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r4 = 0;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r0.seek(r4);	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r9 = r12.readLines(r0);	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r4 = r9;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r6 = r12.file;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r9 = r6.lastModified();	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r1 = r9;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r5 = r4;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        goto L_0x00f2;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r4 = r12.reOpen;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        if (r4 == 0) goto L_0x00fe;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        org.apache.commons.io.IOUtils.closeQuietly(r0);	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        goto L_0x00ff;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r9 = r12.delayMillis;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        java.lang.Thread.sleep(r9);	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r4 = r12.getRun();	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        if (r4 == 0) goto L_0x0129;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r4 = r12.reOpen;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        if (r4 == 0) goto L_0x0129;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r4 = new java.io.RandomAccessFile;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r9 = r12.file;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r10 = "r";	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r4.<init>(r9, r10);	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r0 = r4;	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        r0.seek(r5);	 Catch:{ InterruptedException -> 0x013e, Exception -> 0x0131 }
        goto L_0x012a;
        goto L_0x005f;
        goto L_0x0138;
    L_0x012f:
        r1 = move-exception;
        goto L_0x0151;
    L_0x0131:
        r1 = move-exception;
        r12.stop(r1);	 Catch:{ all -> 0x012f }
        org.apache.commons.io.IOUtils.closeQuietly(r0);
        goto L_0x014f;
    L_0x013e:
        r1 = move-exception;
        r2 = java.lang.Thread.currentThread();	 Catch:{ all -> 0x012f }
        r2.interrupt();	 Catch:{ all -> 0x012f }
        r12.stop(r1);	 Catch:{ all -> 0x012f }
        goto L_0x0138;
        return;
        org.apache.commons.io.IOUtils.closeQuietly(r0);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.io.input.Tailer.run():void");
    }

    public Tailer(File file, TailerListener listener) {
        this(file, listener, 1000);
    }

    public Tailer(File file, TailerListener listener, long delayMillis) {
        this(file, listener, delayMillis, false);
    }

    public Tailer(File file, TailerListener listener, long delayMillis, boolean end) {
        this(file, listener, delayMillis, end, 4096);
    }

    public Tailer(File file, TailerListener listener, long delayMillis, boolean end, boolean reOpen) {
        this(file, listener, delayMillis, end, reOpen, 4096);
    }

    public Tailer(File file, TailerListener listener, long delayMillis, boolean end, int bufSize) {
        this(file, listener, delayMillis, end, false, bufSize);
    }

    public Tailer(File file, TailerListener listener, long delayMillis, boolean end, boolean reOpen, int bufSize) {
        this(file, DEFAULT_CHARSET, listener, delayMillis, end, reOpen, bufSize);
    }

    public Tailer(File file, Charset cset, TailerListener listener, long delayMillis, boolean end, boolean reOpen, int bufSize) {
        this.run = true;
        this.file = file;
        this.delayMillis = delayMillis;
        this.end = end;
        this.inbuf = new byte[bufSize];
        this.listener = listener;
        listener.init(this);
        this.reOpen = reOpen;
        this.cset = cset;
    }

    public static Tailer create(File file, TailerListener listener, long delayMillis, boolean end, int bufSize) {
        return create(file, listener, delayMillis, end, false, bufSize);
    }

    public static Tailer create(File file, TailerListener listener, long delayMillis, boolean end, boolean reOpen, int bufSize) {
        return create(file, DEFAULT_CHARSET, listener, delayMillis, end, reOpen, bufSize);
    }

    public static Tailer create(File file, Charset charset, TailerListener listener, long delayMillis, boolean end, boolean reOpen, int bufSize) {
        Tailer tailer = new Tailer(file, charset, listener, delayMillis, end, reOpen, bufSize);
        Thread thread = new Thread(tailer);
        thread.setDaemon(true);
        thread.start();
        return tailer;
    }

    public static Tailer create(File file, TailerListener listener, long delayMillis, boolean end) {
        return create(file, listener, delayMillis, end, 4096);
    }

    public static Tailer create(File file, TailerListener listener, long delayMillis, boolean end, boolean reOpen) {
        return create(file, listener, delayMillis, end, reOpen, 4096);
    }

    public static Tailer create(File file, TailerListener listener, long delayMillis) {
        return create(file, listener, delayMillis, false);
    }

    public static Tailer create(File file, TailerListener listener) {
        return create(file, listener, 1000, false);
    }

    public File getFile() {
        return this.file;
    }

    protected boolean getRun() {
        return this.run;
    }

    public long getDelay() {
        return this.delayMillis;
    }

    private void stop(Exception e) {
        this.listener.handle(e);
        stop();
    }

    public void stop() {
        this.run = false;
    }

    private long readLines(RandomAccessFile reader) throws IOException {
        Tailer tailer = this;
        RandomAccessFile randomAccessFile = reader;
        OutputStream lineBuf = new ByteArrayOutputStream(64);
        long pos = reader.getFilePointer();
        long rePos = pos;
        boolean seenCR = false;
        while (getRun()) {
            int read = randomAccessFile.read(tailer.inbuf);
            int num = read;
            if (read == -1) {
                break;
            }
            for (read = 0; read < num; read++) {
                byte ch = tailer.inbuf[read];
                if (ch == (byte) 10) {
                    seenCR = false;
                    tailer.listener.handle(new String(lineBuf.toByteArray(), tailer.cset));
                    lineBuf.reset();
                    rePos = (((long) read) + pos) + 1;
                } else if (ch != (byte) 13) {
                    if (seenCR) {
                        seenCR = false;
                        tailer.listener.handle(new String(lineBuf.toByteArray(), tailer.cset));
                        lineBuf.reset();
                        rePos = (((long) read) + pos) + 1;
                    }
                    lineBuf.write(ch);
                } else {
                    if (seenCR) {
                        lineBuf.write(13);
                    }
                    seenCR = true;
                }
            }
            pos = reader.getFilePointer();
        }
        IOUtils.closeQuietly(lineBuf);
        randomAccessFile.seek(rePos);
        TailerListener tailerListener = tailer.listener;
        if (tailerListener instanceof TailerListenerAdapter) {
            ((TailerListenerAdapter) tailerListener).endOfFileReached();
        }
        return rePos;
    }
}
