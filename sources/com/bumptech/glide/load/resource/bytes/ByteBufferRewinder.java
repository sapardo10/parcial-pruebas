package com.bumptech.glide.load.resource.bytes;

import android.support.annotation.NonNull;
import com.bumptech.glide.load.data.DataRewinder;
import java.nio.ByteBuffer;

public class ByteBufferRewinder implements DataRewinder<ByteBuffer> {
    private final ByteBuffer buffer;

    public static class Factory implements com.bumptech.glide.load.data.DataRewinder.Factory<ByteBuffer> {
        @NonNull
        public DataRewinder<ByteBuffer> build(ByteBuffer data) {
            return new ByteBufferRewinder(data);
        }

        @NonNull
        public Class<ByteBuffer> getDataClass() {
            return ByteBuffer.class;
        }
    }

    public ByteBufferRewinder(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @NonNull
    public ByteBuffer rewindAndGet() {
        this.buffer.position(0);
        return this.buffer;
    }

    public void cleanup() {
    }
}
