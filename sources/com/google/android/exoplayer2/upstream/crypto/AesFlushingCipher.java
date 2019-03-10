package com.google.android.exoplayer2.upstream.crypto;

import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class AesFlushingCipher {
    private final int blockSize;
    private final Cipher cipher;
    private final byte[] flushedBlock;
    private int pendingXorBytes;
    private final byte[] zerosBlock;

    public AesFlushingCipher(int mode, byte[] secretKey, long nonce, long offset) {
        try {
            this.cipher = Cipher.getInstance("AES/CTR/NoPadding");
            this.blockSize = this.cipher.getBlockSize();
            this.zerosBlock = new byte[this.blockSize];
            this.flushedBlock = new byte[this.blockSize];
            int startPadding = (int) (offset % ((long) this.blockSize));
            this.cipher.init(mode, new SecretKeySpec(secretKey, Util.splitAtFirst(this.cipher.getAlgorithm(), "/")[0]), new IvParameterSpec(getInitializationVector(nonce, offset / ((long) this.blockSize))));
            if (startPadding != 0) {
                updateInPlace(new byte[startPadding], 0, startPadding);
            }
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateInPlace(byte[] data, int offset, int length) {
        update(data, offset, length, data, offset);
    }

    public void update(byte[] in, int inOffset, int length, byte[] out, int outOffset) {
        AesFlushingCipher aesFlushingCipher = this;
        int inOffset2 = inOffset;
        int length2 = length;
        int outOffset2 = outOffset;
        while (true) {
            int i = aesFlushingCipher.pendingXorBytes;
            if (i <= 0) {
                break;
            }
            out[outOffset2] = (byte) (in[inOffset2] ^ aesFlushingCipher.flushedBlock[aesFlushingCipher.blockSize - i]);
            outOffset2++;
            inOffset2++;
            aesFlushingCipher.pendingXorBytes = i - 1;
            length2--;
            if (length2 == 0) {
                return;
            }
        }
        int written = nonFlushingUpdate(in, inOffset2, length2, out, outOffset2);
        if (length2 != written) {
            int bytesToFlush = length2 - written;
            boolean z = false;
            Assertions.checkState(bytesToFlush < aesFlushingCipher.blockSize);
            outOffset2 += written;
            aesFlushingCipher.pendingXorBytes = aesFlushingCipher.blockSize - bytesToFlush;
            if (nonFlushingUpdate(aesFlushingCipher.zerosBlock, 0, aesFlushingCipher.pendingXorBytes, aesFlushingCipher.flushedBlock, 0) == aesFlushingCipher.blockSize) {
                z = true;
            }
            Assertions.checkState(z);
            int i2 = 0;
            while (i2 < bytesToFlush) {
                int outOffset3 = outOffset2 + 1;
                out[outOffset2] = aesFlushingCipher.flushedBlock[i2];
                i2++;
                outOffset2 = outOffset3;
            }
        }
    }

    private int nonFlushingUpdate(byte[] in, int inOffset, int length, byte[] out, int outOffset) {
        try {
            return this.cipher.update(in, inOffset, length, out, outOffset);
        } catch (ShortBufferException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] getInitializationVector(long nonce, long counter) {
        return ByteBuffer.allocate(16).putLong(nonce).putLong(counter).array();
    }
}
