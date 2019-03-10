package com.google.android.exoplayer2.upstream.cache;

import com.google.android.exoplayer2.upstream.DataSink;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.cache.Cache.CacheException;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.ReusableBufferedOutputStream;
import com.google.android.exoplayer2.util.Util;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class CacheDataSink implements DataSink {
    public static final int DEFAULT_BUFFER_SIZE = 20480;
    private final int bufferSize;
    private ReusableBufferedOutputStream bufferedOutputStream;
    private final Cache cache;
    private DataSpec dataSpec;
    private long dataSpecBytesWritten;
    private File file;
    private final long maxCacheFileSize;
    private OutputStream outputStream;
    private long outputStreamBytesWritten;
    private final boolean syncFileDescriptor;
    private FileOutputStream underlyingFileOutputStream;

    public static class CacheDataSinkException extends CacheException {
        public CacheDataSinkException(IOException cause) {
            super((Throwable) cause);
        }
    }

    public CacheDataSink(Cache cache, long maxCacheFileSize) {
        this(cache, maxCacheFileSize, DEFAULT_BUFFER_SIZE, true);
    }

    public CacheDataSink(Cache cache, long maxCacheFileSize, boolean syncFileDescriptor) {
        this(cache, maxCacheFileSize, DEFAULT_BUFFER_SIZE, syncFileDescriptor);
    }

    public CacheDataSink(Cache cache, long maxCacheFileSize, int bufferSize) {
        this(cache, maxCacheFileSize, bufferSize, true);
    }

    public CacheDataSink(Cache cache, long maxCacheFileSize, int bufferSize, boolean syncFileDescriptor) {
        this.cache = (Cache) Assertions.checkNotNull(cache);
        this.maxCacheFileSize = maxCacheFileSize;
        this.bufferSize = bufferSize;
        this.syncFileDescriptor = syncFileDescriptor;
    }

    public void open(DataSpec dataSpec) throws CacheDataSinkException {
        if (dataSpec.length == -1) {
            if (!dataSpec.isFlagSet(2)) {
                this.dataSpec = null;
                return;
            }
        }
        this.dataSpec = dataSpec;
        this.dataSpecBytesWritten = 0;
        try {
            openNextOutputStream();
        } catch (IOException e) {
            throw new CacheDataSinkException(e);
        }
    }

    public void write(byte[] buffer, int offset, int length) throws CacheDataSinkException {
        if (this.dataSpec != null) {
            int bytesWritten = 0;
            while (bytesWritten < length) {
                try {
                    if (this.outputStreamBytesWritten == this.maxCacheFileSize) {
                        closeCurrentOutputStream();
                        openNextOutputStream();
                    }
                    int bytesToWrite = (int) Math.min((long) (length - bytesWritten), this.maxCacheFileSize - this.outputStreamBytesWritten);
                    this.outputStream.write(buffer, offset + bytesWritten, bytesToWrite);
                    bytesWritten += bytesToWrite;
                    this.outputStreamBytesWritten += (long) bytesToWrite;
                    this.dataSpecBytesWritten += (long) bytesToWrite;
                } catch (int bytesWritten2) {
                    throw new CacheDataSinkException(bytesWritten2);
                }
            }
        }
    }

    public void close() throws CacheDataSinkException {
        if (this.dataSpec != null) {
            try {
                closeCurrentOutputStream();
            } catch (IOException e) {
                throw new CacheDataSinkException(e);
            }
        }
    }

    private void openNextOutputStream() throws IOException {
        long maxLength;
        if (this.dataSpec.length == -1) {
            maxLength = this.maxCacheFileSize;
        } else {
            maxLength = Math.min(this.dataSpec.length - this.dataSpecBytesWritten, this.maxCacheFileSize);
        }
        this.file = this.cache.startFile(this.dataSpec.key, this.dataSpecBytesWritten + this.dataSpec.absoluteStreamPosition, maxLength);
        this.underlyingFileOutputStream = new FileOutputStream(this.file);
        int i = this.bufferSize;
        if (i > 0) {
            ReusableBufferedOutputStream reusableBufferedOutputStream = this.bufferedOutputStream;
            if (reusableBufferedOutputStream == null) {
                this.bufferedOutputStream = new ReusableBufferedOutputStream(this.underlyingFileOutputStream, i);
            } else {
                reusableBufferedOutputStream.reset(this.underlyingFileOutputStream);
            }
            this.outputStream = this.bufferedOutputStream;
        } else {
            this.outputStream = this.underlyingFileOutputStream;
        }
        this.outputStreamBytesWritten = 0;
    }

    private void closeCurrentOutputStream() throws IOException {
        OutputStream outputStream = this.outputStream;
        if (outputStream != null) {
            try {
                outputStream.flush();
                if (this.syncFileDescriptor) {
                    this.underlyingFileOutputStream.getFD().sync();
                }
                Util.closeQuietly(this.outputStream);
                this.outputStream = null;
                boolean success = this.file;
                this.file = null;
                if (true) {
                    this.cache.commitFile(success);
                } else {
                    success.delete();
                }
            } catch (Throwable th) {
                Util.closeQuietly(this.outputStream);
                this.outputStream = null;
                File fileToCommit = this.file;
                this.file = null;
                if (false) {
                    this.cache.commitFile(fileToCommit);
                } else {
                    fileToCommit.delete();
                }
            }
        }
    }
}
