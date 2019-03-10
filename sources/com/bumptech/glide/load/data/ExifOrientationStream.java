package com.bumptech.glide.load.data;

import android.support.annotation.NonNull;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class ExifOrientationStream extends FilterInputStream {
    private static final byte[] EXIF_SEGMENT = new byte[]{(byte) -1, (byte) -31, (byte) 0, (byte) 28, (byte) 69, (byte) 120, (byte) 105, (byte) 102, (byte) 0, (byte) 0, (byte) 77, (byte) 77, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 8, (byte) 0, (byte) 1, (byte) 1, (byte) 18, (byte) 0, (byte) 2, (byte) 0, (byte) 0, (byte) 0, (byte) 1, (byte) 0};
    private static final int ORIENTATION_POSITION = (SEGMENT_LENGTH + 2);
    private static final int SEGMENT_LENGTH = EXIF_SEGMENT.length;
    private static final int SEGMENT_START_POSITION = 2;
    private final byte orientation;
    private int position;

    public ExifOrientationStream(InputStream in, int orientation) {
        super(in);
        if (orientation < -1 || orientation > 8) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Cannot add invalid orientation: ");
            stringBuilder.append(orientation);
            throw new IllegalArgumentException(stringBuilder.toString());
        }
        this.orientation = (byte) orientation;
    }

    public boolean markSupported() {
        return false;
    }

    public void mark(int readLimit) {
        throw new UnsupportedOperationException();
    }

    public int read() throws IOException {
        int i = this.position;
        if (i >= 2) {
            int i2 = ORIENTATION_POSITION;
            if (i <= i2) {
                if (i == i2) {
                    i = this.orientation;
                } else {
                    i = EXIF_SEGMENT[i - 2] & 255;
                }
                if (i != -1) {
                    this.position++;
                }
                return i;
            }
        }
        i = super.read();
        if (i != -1) {
            this.position++;
        }
        return i;
    }

    public int read(@NonNull byte[] buffer, int byteOffset, int byteCount) throws IOException {
        int i = this.position;
        int i2 = ORIENTATION_POSITION;
        if (i > i2) {
            i = super.read(buffer, byteOffset, byteCount);
        } else if (i == i2) {
            buffer[byteOffset] = this.orientation;
            i = 1;
        } else if (i < 2) {
            i = super.read(buffer, byteOffset, 2 - i);
        } else {
            i = Math.min(i2 - i, byteCount);
            System.arraycopy(EXIF_SEGMENT, this.position - 2, buffer, byteOffset, i);
        }
        if (i > 0) {
            this.position += i;
        }
        return i;
    }

    public long skip(long byteCount) throws IOException {
        long skipped = super.skip(byteCount);
        if (skipped > 0) {
            this.position = (int) (((long) this.position) + skipped);
        }
        return skipped;
    }

    public void reset() throws IOException {
        throw new UnsupportedOperationException();
    }
}
