package org.apache.commons.io.input;

import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;

public class NullReader extends Reader {
    private boolean eof;
    private long mark;
    private final boolean markSupported;
    private long position;
    private long readlimit;
    private final long size;
    private final boolean throwEofException;

    public NullReader(long size) {
        this(size, true, false);
    }

    public NullReader(long size, boolean markSupported, boolean throwEofException) {
        this.mark = -1;
        this.size = size;
        this.markSupported = markSupported;
        this.throwEofException = throwEofException;
    }

    public long getPosition() {
        return this.position;
    }

    public long getSize() {
        return this.size;
    }

    public void close() throws IOException {
        this.eof = false;
        this.position = 0;
        this.mark = -1;
    }

    public synchronized void mark(int readlimit) {
        if (this.markSupported) {
            this.mark = this.position;
            this.readlimit = (long) readlimit;
        } else {
            throw new UnsupportedOperationException("Mark not supported");
        }
    }

    public boolean markSupported() {
        return this.markSupported;
    }

    public int read() throws IOException {
        if (this.eof) {
            throw new IOException("Read after end of file");
        }
        long j = this.position;
        if (j == this.size) {
            return doEndOfFile();
        }
        this.position = j + 1;
        return processChar();
    }

    public int read(char[] chars) throws IOException {
        return read(chars, 0, chars.length);
    }

    public int read(char[] chars, int offset, int length) throws IOException {
        if (this.eof) {
            throw new IOException("Read after end of file");
        }
        long j = this.position;
        long j2 = this.size;
        if (j == j2) {
            return doEndOfFile();
        }
        this.position = j + ((long) length);
        int returnLength = length;
        long j3 = this.position;
        if (j3 > j2) {
            returnLength = length - ((int) (j3 - j2));
            this.position = j2;
        }
        processChars(chars, offset, returnLength);
        return returnLength;
    }

    public synchronized void reset() throws IOException {
        if (!this.markSupported) {
            throw new UnsupportedOperationException("Mark not supported");
        } else if (this.mark < 0) {
            throw new IOException("No position has been marked");
        } else if (this.position <= this.mark + this.readlimit) {
            this.position = this.mark;
            this.eof = false;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Marked position [");
            stringBuilder.append(this.mark);
            stringBuilder.append("] is no longer valid - passed the read limit [");
            stringBuilder.append(this.readlimit);
            stringBuilder.append("]");
            throw new IOException(stringBuilder.toString());
        }
    }

    public long skip(long numberOfChars) throws IOException {
        if (this.eof) {
            throw new IOException("Skip after end of file");
        }
        long j = this.position;
        long j2 = this.size;
        if (j == j2) {
            return (long) doEndOfFile();
        }
        this.position = j + numberOfChars;
        j = numberOfChars;
        long j3 = this.position;
        if (j3 > j2) {
            j = numberOfChars - (j3 - j2);
            this.position = j2;
        }
        return j;
    }

    protected int processChar() {
        return 0;
    }

    protected void processChars(char[] chars, int offset, int length) {
    }

    private int doEndOfFile() throws EOFException {
        this.eof = true;
        if (!this.throwEofException) {
            return -1;
        }
        throw new EOFException();
    }
}
