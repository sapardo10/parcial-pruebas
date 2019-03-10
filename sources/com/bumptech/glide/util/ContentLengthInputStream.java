package com.bumptech.glide.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class ContentLengthInputStream extends FilterInputStream {
    private static final String TAG = "ContentLengthStream";
    private static final int UNKNOWN = -1;
    private final long contentLength;
    private int readSoFar;

    @NonNull
    public static InputStream obtain(@NonNull InputStream other, @Nullable String contentLengthHeader) {
        return obtain(other, (long) parseContentLength(contentLengthHeader));
    }

    @NonNull
    public static InputStream obtain(@NonNull InputStream other, long contentLength) {
        return new ContentLengthInputStream(other, contentLength);
    }

    private static int parseContentLength(@Nullable String contentLengthHeader) {
        if (TextUtils.isEmpty(contentLengthHeader)) {
            return -1;
        }
        try {
            return Integer.parseInt(contentLengthHeader);
        } catch (NumberFormatException e) {
            if (!Log.isLoggable(TAG, 3)) {
                return -1;
            }
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("failed to parse content length header: ");
            stringBuilder.append(contentLengthHeader);
            Log.d(str, stringBuilder.toString(), e);
            return -1;
        }
    }

    private ContentLengthInputStream(@NonNull InputStream in, long contentLength) {
        super(in);
        this.contentLength = contentLength;
    }

    public synchronized int available() throws IOException {
        return (int) Math.max(this.contentLength - ((long) this.readSoFar), (long) this.in.available());
    }

    public synchronized int read() throws IOException {
        int value;
        value = super.read();
        checkReadSoFarOrThrow(value >= 0 ? 1 : -1);
        return value;
    }

    public int read(byte[] buffer) throws IOException {
        return read(buffer, 0, buffer.length);
    }

    public synchronized int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        return checkReadSoFarOrThrow(super.read(buffer, byteOffset, byteCount));
    }

    private int checkReadSoFarOrThrow(int read) throws IOException {
        if (read >= 0) {
            this.readSoFar += read;
        } else if (this.contentLength - ((long) this.readSoFar) > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Failed to read all expected data, expected: ");
            stringBuilder.append(this.contentLength);
            stringBuilder.append(", but read: ");
            stringBuilder.append(this.readSoFar);
            throw new IOException(stringBuilder.toString());
        }
        return read;
    }
}
