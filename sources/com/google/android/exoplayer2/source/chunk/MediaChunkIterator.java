package com.google.android.exoplayer2.source.chunk;

import com.google.android.exoplayer2.upstream.DataSpec;
import java.util.NoSuchElementException;

public interface MediaChunkIterator {
    public static final MediaChunkIterator EMPTY = new C09831();

    /* renamed from: com.google.android.exoplayer2.source.chunk.MediaChunkIterator$1 */
    static class C09831 implements MediaChunkIterator {
        C09831() {
        }

        public boolean isEnded() {
            return true;
        }

        public boolean next() {
            return false;
        }

        public DataSpec getDataSpec() {
            throw new NoSuchElementException();
        }

        public long getChunkStartTimeUs() {
            throw new NoSuchElementException();
        }

        public long getChunkEndTimeUs() {
            throw new NoSuchElementException();
        }
    }

    long getChunkEndTimeUs();

    long getChunkStartTimeUs();

    DataSpec getDataSpec();

    boolean isEnded();

    boolean next();
}