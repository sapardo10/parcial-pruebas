package org.apache.commons.io;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LineIterator implements Iterator<String> {
    private final BufferedReader bufferedReader;
    private String cachedLine;
    private boolean finished = false;

    public boolean hasNext() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:20:0x002d in {2, 5, 11, 15, 16, 19} preds:[]
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
        r4 = this;
        r0 = r4.cachedLine;
        r1 = 1;
        if (r0 == 0) goto L_0x0006;
    L_0x0005:
        return r1;
    L_0x0006:
        r0 = r4.finished;
        r2 = 0;
        if (r0 == 0) goto L_0x000c;
    L_0x000b:
        return r2;
    L_0x000d:
        r0 = r4.bufferedReader;	 Catch:{ IOException -> 0x0023 }
        r0 = r0.readLine();	 Catch:{ IOException -> 0x0023 }
        if (r0 != 0) goto L_0x0018;	 Catch:{ IOException -> 0x0023 }
    L_0x0015:
        r4.finished = r1;	 Catch:{ IOException -> 0x0023 }
        return r2;	 Catch:{ IOException -> 0x0023 }
    L_0x0018:
        r3 = r4.isValidLine(r0);	 Catch:{ IOException -> 0x0023 }
        if (r3 == 0) goto L_0x0021;	 Catch:{ IOException -> 0x0023 }
    L_0x001e:
        r4.cachedLine = r0;	 Catch:{ IOException -> 0x0023 }
        return r1;
        goto L_0x000d;
    L_0x0023:
        r0 = move-exception;
        r4.close();
        r1 = new java.lang.IllegalStateException;
        r1.<init>(r0);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.io.LineIterator.hasNext():boolean");
    }

    public LineIterator(Reader reader) throws IllegalArgumentException {
        if (reader == null) {
            throw new IllegalArgumentException("Reader must not be null");
        } else if (reader instanceof BufferedReader) {
            this.bufferedReader = (BufferedReader) reader;
        } else {
            this.bufferedReader = new BufferedReader(reader);
        }
    }

    protected boolean isValidLine(String line) {
        return true;
    }

    public String next() {
        return nextLine();
    }

    public String nextLine() {
        if (hasNext()) {
            String currentLine = this.cachedLine;
            this.cachedLine = null;
            return currentLine;
        }
        throw new NoSuchElementException("No more lines");
    }

    public void close() {
        this.finished = true;
        IOUtils.closeQuietly(this.bufferedReader);
        this.cachedLine = null;
    }

    public void remove() {
        throw new UnsupportedOperationException("Remove unsupported on LineIterator");
    }

    public static void closeQuietly(LineIterator iterator) {
        if (iterator != null) {
            iterator.close();
        }
    }
}
