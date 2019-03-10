package com.google.android.exoplayer2.decoder;

import java.nio.ByteBuffer;

public class DecoderInputBuffer extends Buffer {
    public static final int BUFFER_REPLACEMENT_MODE_DIRECT = 2;
    public static final int BUFFER_REPLACEMENT_MODE_DISABLED = 0;
    public static final int BUFFER_REPLACEMENT_MODE_NORMAL = 1;
    private final int bufferReplacementMode;
    public final CryptoInfo cryptoInfo = new CryptoInfo();
    public ByteBuffer data;
    public long timeUs;

    public static DecoderInputBuffer newFlagsOnlyInstance() {
        return new DecoderInputBuffer(0);
    }

    public DecoderInputBuffer(int bufferReplacementMode) {
        this.bufferReplacementMode = bufferReplacementMode;
    }

    public void ensureSpaceForWrite(int length) {
        int capacity = this.data;
        if (capacity == 0) {
            this.data = createReplacementByteBuffer(length);
            return;
        }
        capacity = capacity.capacity();
        int position = this.data.position();
        int requiredCapacity = position + length;
        if (capacity < requiredCapacity) {
            ByteBuffer newData = createReplacementByteBuffer(requiredCapacity);
            if (position > 0) {
                this.data.position(0);
                this.data.limit(position);
                newData.put(this.data);
            }
            this.data = newData;
        }
    }

    public final boolean isFlagsOnly() {
        return this.data == null && this.bufferReplacementMode == 0;
    }

    public final boolean isEncrypted() {
        return getFlag(1073741824);
    }

    public final void flip() {
        this.data.flip();
    }

    public void clear() {
        super.clear();
        ByteBuffer byteBuffer = this.data;
        if (byteBuffer != null) {
            byteBuffer.clear();
        }
    }

    private ByteBuffer createReplacementByteBuffer(int requiredCapacity) {
        int i = this.bufferReplacementMode;
        if (i == 1) {
            return ByteBuffer.allocate(requiredCapacity);
        }
        if (i == 2) {
            return ByteBuffer.allocateDirect(requiredCapacity);
        }
        i = this.data;
        i = i == 0 ? 0 : i.capacity();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Buffer too small (");
        stringBuilder.append(i);
        stringBuilder.append(" < ");
        stringBuilder.append(requiredCapacity);
        stringBuilder.append(")");
        throw new IllegalStateException(stringBuilder.toString());
    }
}
