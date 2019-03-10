package org.apache.commons.io.input;

import java.io.IOException;
import java.io.InputStream;

public class BoundedInputStream extends InputStream {
    private final InputStream in;
    private long mark;
    private final long max;
    private long pos;
    private boolean propagateClose;

    public BoundedInputStream(InputStream in, long size) {
        this.pos = 0;
        this.mark = -1;
        this.propagateClose = true;
        this.max = size;
        this.in = in;
    }

    public BoundedInputStream(InputStream in) {
        this(in, -1);
    }

    public int read() throws IOException {
        long j = this.max;
        if (j >= 0 && this.pos >= j) {
            return -1;
        }
        int result = this.in.read();
        this.pos++;
        return result;
    }

    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    public int read(byte[] b, int off, int len) throws IOException {
        long j = this.max;
        if (j >= 0 && this.pos >= j) {
            return -1;
        }
        j = this.max;
        int bytesRead = this.in.read(b, off, (int) (j >= 0 ? Math.min((long) len, j - this.pos) : (long) len));
        if (bytesRead == -1) {
            return -1;
        }
        this.pos += (long) bytesRead;
        return bytesRead;
    }

    public long skip(long n) throws IOException {
        long j = this.max;
        long skippedBytes = this.in.skip(j >= 0 ? Math.min(n, j - this.pos) : n);
        this.pos += skippedBytes;
        return skippedBytes;
    }

    public int available() throws IOException {
        long j = this.max;
        if (j < 0 || this.pos < j) {
            return this.in.available();
        }
        return 0;
    }

    public String toString() {
        return this.in.toString();
    }

    public void close() throws IOException {
        if (this.propagateClose) {
            this.in.close();
        }
    }

    public synchronized void reset() throws IOException {
        this.in.reset();
        this.pos = this.mark;
    }

    public synchronized void mark(int readlimit) {
        this.in.mark(readlimit);
        this.mark = this.pos;
    }

    public boolean markSupported() {
        return this.in.markSupported();
    }

    public boolean isPropagateClose() {
        return this.propagateClose;
    }

    public void setPropagateClose(boolean propagateClose) {
        this.propagateClose = propagateClose;
    }
}
