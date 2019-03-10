package org.jsoup.internal;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import org.jsoup.helper.Validate;

public final class ConstrainableInputStream extends BufferedInputStream {
    private static final int DefaultSize = 32768;
    private final boolean capped;
    private boolean interrupted;
    private final int maxSize;
    private int remaining;
    private long startTime;
    private long timeout = 0;

    private ConstrainableInputStream(InputStream in, int bufferSize, int maxSize) {
        super(in, bufferSize);
        boolean z = true;
        Validate.isTrue(maxSize >= 0);
        this.maxSize = maxSize;
        this.remaining = maxSize;
        if (maxSize == 0) {
            z = false;
        }
        this.capped = z;
        this.startTime = System.nanoTime();
    }

    public static ConstrainableInputStream wrap(InputStream in, int bufferSize, int maxSize) {
        if (in instanceof ConstrainableInputStream) {
            return (ConstrainableInputStream) in;
        }
        return new ConstrainableInputStream(in, bufferSize, maxSize);
    }

    public int read(byte[] b, int off, int len) throws IOException {
        if (!this.interrupted) {
            if (!this.capped || this.remaining > 0) {
                if (Thread.interrupted()) {
                    this.interrupted = true;
                    return -1;
                } else if (expired()) {
                    throw new SocketTimeoutException("Read timeout");
                } else {
                    if (this.capped && len > this.remaining) {
                        len = this.remaining;
                    }
                    try {
                        int read = super.read(b, off, len);
                        this.remaining -= read;
                        return read;
                    } catch (SocketTimeoutException e) {
                        return 0;
                    }
                }
            }
        }
        return -1;
    }

    public ByteBuffer readToByteBuffer(int max) throws IOException {
        boolean localCapped = true;
        Validate.isTrue(max >= 0, "maxSize must be 0 (unlimited) or larger");
        if (max <= 0) {
            localCapped = false;
        }
        int bufferSize = 32768;
        if (localCapped && max < 32768) {
            bufferSize = max;
        }
        byte[] readBuffer = new byte[bufferSize];
        ByteArrayOutputStream outStream = new ByteArrayOutputStream(bufferSize);
        int remaining = max;
        while (true) {
            int read = read(readBuffer);
            if (read == -1) {
                break;
            }
            if (localCapped) {
                if (read >= remaining) {
                    break;
                }
                remaining -= read;
            }
            outStream.write(readBuffer, 0, read);
            return ByteBuffer.wrap(outStream.toByteArray());
        }
        outStream.write(readBuffer, 0, remaining);
        return ByteBuffer.wrap(outStream.toByteArray());
    }

    public void reset() throws IOException {
        super.reset();
        this.remaining = this.maxSize - this.markpos;
    }

    public ConstrainableInputStream timeout(long startTimeNanos, long timeoutMillis) {
        this.startTime = startTimeNanos;
        this.timeout = 1000000 * timeoutMillis;
        return this;
    }

    private boolean expired() {
        boolean z = false;
        if (this.timeout == 0) {
            return false;
        }
        if (System.nanoTime() - this.startTime > this.timeout) {
            z = true;
        }
        return z;
    }
}
