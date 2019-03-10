package com.google.android.exoplayer2.util;

import android.support.v4.view.MotionEventCompat;

public final class ParsableBitArray {
    private int bitOffset;
    private int byteLimit;
    private int byteOffset;
    public byte[] data;

    public ParsableBitArray() {
        this.data = Util.EMPTY_BYTE_ARRAY;
    }

    public ParsableBitArray(byte[] data) {
        this(data, data.length);
    }

    public ParsableBitArray(byte[] data, int limit) {
        this.data = data;
        this.byteLimit = limit;
    }

    public void reset(byte[] data) {
        reset(data, data.length);
    }

    public void reset(ParsableByteArray parsableByteArray) {
        reset(parsableByteArray.data, parsableByteArray.limit());
        setPosition(parsableByteArray.getPosition() * 8);
    }

    public void reset(byte[] data, int limit) {
        this.data = data;
        this.byteOffset = 0;
        this.bitOffset = 0;
        this.byteLimit = limit;
    }

    public int bitsLeft() {
        return ((this.byteLimit - this.byteOffset) * 8) - this.bitOffset;
    }

    public int getPosition() {
        return (this.byteOffset * 8) + this.bitOffset;
    }

    public int getBytePosition() {
        Assertions.checkState(this.bitOffset == 0);
        return this.byteOffset;
    }

    public void setPosition(int position) {
        this.byteOffset = position / 8;
        this.bitOffset = position - (this.byteOffset * 8);
        assertValidOffset();
    }

    public void skipBit() {
        int i = this.bitOffset + 1;
        this.bitOffset = i;
        if (i == 8) {
            this.bitOffset = 0;
            this.byteOffset++;
        }
        assertValidOffset();
    }

    public void skipBits(int numBits) {
        int numBytes = numBits / 8;
        this.byteOffset += numBytes;
        this.bitOffset += numBits - (numBytes * 8);
        int i = this.bitOffset;
        if (i > 7) {
            this.byteOffset++;
            this.bitOffset = i - 8;
        }
        assertValidOffset();
    }

    public boolean readBit() {
        boolean returnValue = (this.data[this.byteOffset] & (128 >> this.bitOffset)) != 0;
        skipBit();
        return returnValue;
    }

    public int readBits(int numBits) {
        if (numBits == 0) {
            return 0;
        }
        int i;
        int returnValue = 0;
        this.bitOffset += numBits;
        while (true) {
            i = this.bitOffset;
            if (i <= 8) {
                break;
            }
            this.bitOffset = i - 8;
            byte[] bArr = this.data;
            int i2 = this.byteOffset;
            this.byteOffset = i2 + 1;
            returnValue |= (bArr[i2] & 255) << this.bitOffset;
        }
        byte[] bArr2 = this.data;
        int i3 = this.byteOffset;
        returnValue = (returnValue | ((bArr2[i3] & 255) >> (8 - i))) & (-1 >>> (32 - numBits));
        if (i == 8) {
            this.bitOffset = 0;
            this.byteOffset = i3 + 1;
        }
        assertValidOffset();
        return returnValue;
    }

    public void readBits(byte[] buffer, int offset, int numBits) {
        int i;
        int to = (numBits >> 3) + offset;
        for (i = offset; i < to; i++) {
            byte[] bArr = this.data;
            int i2 = this.byteOffset;
            this.byteOffset = i2 + 1;
            byte b = bArr[i2];
            int i3 = this.bitOffset;
            buffer[i] = (byte) (b << i3);
            buffer[i] = (byte) (((255 & bArr[this.byteOffset]) >> (8 - i3)) | buffer[i]);
        }
        i = numBits & 7;
        if (i != 0) {
            buffer[to] = (byte) (buffer[to] & (255 >> i));
            int i4 = this.bitOffset;
            if (i4 + i > 8) {
                b = buffer[to];
                byte[] bArr2 = this.data;
                int i5 = this.byteOffset;
                this.byteOffset = i5 + 1;
                buffer[to] = (byte) (b | ((bArr2[i5] & 255) << i4));
                this.bitOffset = i4 - 8;
            }
            this.bitOffset += i;
            bArr = this.data;
            i2 = this.byteOffset;
            int lastDataByteTrailingBits = 255 & bArr[i2];
            i4 = this.bitOffset;
            buffer[to] = (byte) (buffer[to] | ((byte) ((lastDataByteTrailingBits >> (8 - i4)) << (8 - i))));
            if (i4 == 8) {
                this.bitOffset = 0;
                this.byteOffset = i2 + 1;
            }
            assertValidOffset();
        }
    }

    public void byteAlign() {
        if (this.bitOffset != 0) {
            this.bitOffset = 0;
            this.byteOffset++;
            assertValidOffset();
        }
    }

    public void readBytes(byte[] buffer, int offset, int length) {
        Assertions.checkState(this.bitOffset == 0);
        System.arraycopy(this.data, this.byteOffset, buffer, offset, length);
        this.byteOffset += length;
        assertValidOffset();
    }

    public void skipBytes(int length) {
        Assertions.checkState(this.bitOffset == 0);
        this.byteOffset += length;
        assertValidOffset();
    }

    public void putInt(int value, int numBits) {
        int remainingBitsToRead = numBits;
        if (numBits < 32) {
            value &= (1 << numBits) - 1;
        }
        int firstByteReadSize = Math.min(8 - this.bitOffset, numBits);
        int i = this.bitOffset;
        int firstByteRightPaddingSize = (8 - i) - firstByteReadSize;
        i = (MotionEventCompat.ACTION_POINTER_INDEX_MASK >> i) | ((1 << firstByteRightPaddingSize) - 1);
        byte[] bArr = this.data;
        int currentByteIndex = this.byteOffset;
        bArr[currentByteIndex] = (byte) (bArr[currentByteIndex] & i);
        bArr[currentByteIndex] = (byte) (bArr[currentByteIndex] | ((value >>> (numBits - firstByteReadSize)) << firstByteRightPaddingSize));
        remainingBitsToRead -= firstByteReadSize;
        currentByteIndex++;
        while (remainingBitsToRead > 8) {
            int currentByteIndex2 = currentByteIndex + 1;
            this.data[currentByteIndex] = (byte) (value >>> (remainingBitsToRead - 8));
            remainingBitsToRead -= 8;
            currentByteIndex = currentByteIndex2;
        }
        int lastByteRightPaddingSize = 8 - remainingBitsToRead;
        bArr = this.data;
        bArr[currentByteIndex] = (byte) (bArr[currentByteIndex] & ((1 << lastByteRightPaddingSize) - 1));
        bArr[currentByteIndex] = (byte) (bArr[currentByteIndex] | ((value & ((1 << remainingBitsToRead) - 1)) << lastByteRightPaddingSize));
        skipBits(numBits);
        assertValidOffset();
    }

    private void assertValidOffset() {
        boolean z;
        int i = this.byteOffset;
        if (i >= 0) {
            int i2 = this.byteLimit;
            if (i < i2 || (i == i2 && this.bitOffset == 0)) {
                z = true;
                Assertions.checkState(z);
            }
        }
        z = false;
        Assertions.checkState(z);
    }
}
