package com.google.android.exoplayer2.source.chunk;

import java.util.NoSuchElementException;

public abstract class BaseMediaChunkIterator implements MediaChunkIterator {
    private long currentIndex;
    private final long fromIndex;
    private final long toIndex;

    public BaseMediaChunkIterator(long fromIndex, long toIndex) {
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
        this.currentIndex = fromIndex - 1;
    }

    public boolean isEnded() {
        return this.currentIndex > this.toIndex;
    }

    public boolean next() {
        this.currentIndex++;
        return isEnded() ^ 1;
    }

    protected void checkInBounds() {
        long j = this.currentIndex;
        if (j < this.fromIndex || j > this.toIndex) {
            throw new NoSuchElementException();
        }
    }

    protected long getCurrentIndex() {
        return this.currentIndex;
    }
}
