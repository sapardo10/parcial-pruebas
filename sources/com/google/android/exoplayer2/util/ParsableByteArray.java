package com.google.android.exoplayer2.util;

import android.support.annotation.Nullable;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import org.objenesis.instantiator.util.ClassDefinitionUtils;

public final class ParsableByteArray {
    public byte[] data;
    private int limit;
    private int position;

    public long readUtf8EncodedLong() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:24:0x007b in {7, 9, 10, 11, 17, 19, 21, 23} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r11 = this;
        r0 = 0;
        r1 = r11.data;
        r2 = r11.position;
        r1 = r1[r2];
        r1 = (long) r1;
        r3 = 7;
    L_0x0009:
        r4 = 6;
        if (r3 < 0) goto L_0x002a;
    L_0x000c:
        r5 = 1;
        r6 = r5 << r3;
        r6 = (long) r6;
        r6 = r6 & r1;
        r8 = 0;
        r10 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r10 != 0) goto L_0x0027;
    L_0x0017:
        r6 = 7;
        if (r3 >= r4) goto L_0x0022;
    L_0x001a:
        r7 = r5 << r3;
        r7 = r7 - r5;
        r7 = (long) r7;
        r1 = r1 & r7;
        r0 = 7 - r3;
        goto L_0x002a;
    L_0x0022:
        if (r3 != r6) goto L_0x0026;
    L_0x0024:
        r0 = 1;
        goto L_0x002a;
    L_0x0026:
        goto L_0x002a;
    L_0x0027:
        r3 = r3 + -1;
        goto L_0x0009;
    L_0x002a:
        if (r0 == 0) goto L_0x0064;
    L_0x002c:
        r3 = 1;
    L_0x002d:
        if (r3 >= r0) goto L_0x005d;
    L_0x002f:
        r5 = r11.data;
        r6 = r11.position;
        r6 = r6 + r3;
        r5 = r5[r6];
        r6 = r5 & 192;
        r7 = 128; // 0x80 float:1.794E-43 double:6.32E-322;
        if (r6 != r7) goto L_0x0046;
    L_0x003c:
        r6 = r1 << r4;
        r8 = r5 & 63;
        r8 = (long) r8;
        r1 = r6 | r8;
        r3 = r3 + 1;
        goto L_0x002d;
    L_0x0046:
        r4 = new java.lang.NumberFormatException;
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r7 = "Invalid UTF-8 sequence continuation byte: ";
        r6.append(r7);
        r6.append(r1);
        r6 = r6.toString();
        r4.<init>(r6);
        throw r4;
        r3 = r11.position;
        r3 = r3 + r0;
        r11.position = r3;
        return r1;
    L_0x0064:
        r3 = new java.lang.NumberFormatException;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "Invalid UTF-8 sequence first byte: ";
        r4.append(r5);
        r4.append(r1);
        r4 = r4.toString();
        r3.<init>(r4);
        throw r3;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.util.ParsableByteArray.readUtf8EncodedLong():long");
    }

    public ParsableByteArray() {
        this.data = Util.EMPTY_BYTE_ARRAY;
    }

    public ParsableByteArray(int limit) {
        this.data = new byte[limit];
        this.limit = limit;
    }

    public ParsableByteArray(byte[] data) {
        this.data = data;
        this.limit = data.length;
    }

    public ParsableByteArray(byte[] data, int limit) {
        this.data = data;
        this.limit = limit;
    }

    public void reset() {
        this.position = 0;
        this.limit = 0;
    }

    public void reset(int limit) {
        reset(capacity() < limit ? new byte[limit] : this.data, limit);
    }

    public void reset(byte[] data) {
        reset(data, data.length);
    }

    public void reset(byte[] data, int limit) {
        this.data = data;
        this.limit = limit;
        this.position = 0;
    }

    public int bytesLeft() {
        return this.limit - this.position;
    }

    public int limit() {
        return this.limit;
    }

    public void setLimit(int limit) {
        boolean z = limit >= 0 && limit <= this.data.length;
        Assertions.checkArgument(z);
        this.limit = limit;
    }

    public int getPosition() {
        return this.position;
    }

    public int capacity() {
        return this.data.length;
    }

    public void setPosition(int position) {
        boolean z = position >= 0 && position <= this.limit;
        Assertions.checkArgument(z);
        this.position = position;
    }

    public void skipBytes(int bytes) {
        setPosition(this.position + bytes);
    }

    public void readBytes(ParsableBitArray bitArray, int length) {
        readBytes(bitArray.data, 0, length);
        bitArray.setPosition(0);
    }

    public void readBytes(byte[] buffer, int offset, int length) {
        System.arraycopy(this.data, this.position, buffer, offset, length);
        this.position += length;
    }

    public void readBytes(ByteBuffer buffer, int length) {
        buffer.put(this.data, this.position, length);
        this.position += length;
    }

    public int peekUnsignedByte() {
        return this.data[this.position] & 255;
    }

    public char peekChar() {
        byte[] bArr = this.data;
        int i = this.position;
        return (char) ((bArr[i + 1] & 255) | ((bArr[i] & 255) << 8));
    }

    public int readUnsignedByte() {
        byte[] bArr = this.data;
        int i = this.position;
        this.position = i + 1;
        return bArr[i] & 255;
    }

    public int readUnsignedShort() {
        byte[] bArr = this.data;
        int i = this.position;
        this.position = i + 1;
        i = (bArr[i] & 255) << 8;
        int i2 = this.position;
        this.position = i2 + 1;
        return (bArr[i2] & 255) | i;
    }

    public int readLittleEndianUnsignedShort() {
        byte[] bArr = this.data;
        int i = this.position;
        this.position = i + 1;
        i = bArr[i] & 255;
        int i2 = this.position;
        this.position = i2 + 1;
        return ((bArr[i2] & 255) << 8) | i;
    }

    public short readShort() {
        byte[] bArr = this.data;
        int i = this.position;
        this.position = i + 1;
        i = (bArr[i] & 255) << 8;
        int i2 = this.position;
        this.position = i2 + 1;
        return (short) ((bArr[i2] & 255) | i);
    }

    public short readLittleEndianShort() {
        byte[] bArr = this.data;
        int i = this.position;
        this.position = i + 1;
        i = bArr[i] & 255;
        int i2 = this.position;
        this.position = i2 + 1;
        return (short) (((bArr[i2] & 255) << 8) | i);
    }

    public int readUnsignedInt24() {
        byte[] bArr = this.data;
        int i = this.position;
        this.position = i + 1;
        i = (bArr[i] & 255) << 16;
        int i2 = this.position;
        this.position = i2 + 1;
        i |= (bArr[i2] & 255) << 8;
        i2 = this.position;
        this.position = i2 + 1;
        return (bArr[i2] & 255) | i;
    }

    public int readInt24() {
        byte[] bArr = this.data;
        int i = this.position;
        this.position = i + 1;
        i = ((bArr[i] & 255) << 24) >> 8;
        int i2 = this.position;
        this.position = i2 + 1;
        i |= (bArr[i2] & 255) << 8;
        i2 = this.position;
        this.position = i2 + 1;
        return (bArr[i2] & 255) | i;
    }

    public int readLittleEndianInt24() {
        byte[] bArr = this.data;
        int i = this.position;
        this.position = i + 1;
        i = bArr[i] & 255;
        int i2 = this.position;
        this.position = i2 + 1;
        i |= (bArr[i2] & 255) << 8;
        i2 = this.position;
        this.position = i2 + 1;
        return ((bArr[i2] & 255) << 16) | i;
    }

    public int readLittleEndianUnsignedInt24() {
        byte[] bArr = this.data;
        int i = this.position;
        this.position = i + 1;
        i = bArr[i] & 255;
        int i2 = this.position;
        this.position = i2 + 1;
        i |= (bArr[i2] & 255) << 8;
        i2 = this.position;
        this.position = i2 + 1;
        return ((bArr[i2] & 255) << 16) | i;
    }

    public long readUnsignedInt() {
        byte[] bArr = this.data;
        int i = this.position;
        this.position = i + 1;
        long j = (((long) bArr[i]) & 255) << 24;
        int i2 = this.position;
        this.position = i2 + 1;
        j |= (((long) bArr[i2]) & 255) << 16;
        i2 = this.position;
        this.position = i2 + 1;
        j |= (((long) bArr[i2]) & 255) << 8;
        i2 = this.position;
        this.position = i2 + 1;
        return j | (255 & ((long) bArr[i2]));
    }

    public long readLittleEndianUnsignedInt() {
        byte[] bArr = this.data;
        int i = this.position;
        this.position = i + 1;
        long j = ((long) bArr[i]) & 255;
        int i2 = this.position;
        this.position = i2 + 1;
        j |= (((long) bArr[i2]) & 255) << 8;
        i2 = this.position;
        this.position = i2 + 1;
        j |= (((long) bArr[i2]) & 255) << 16;
        i2 = this.position;
        this.position = i2 + 1;
        return j | ((255 & ((long) bArr[i2])) << 24);
    }

    public int readInt() {
        byte[] bArr = this.data;
        int i = this.position;
        this.position = i + 1;
        i = (bArr[i] & 255) << 24;
        int i2 = this.position;
        this.position = i2 + 1;
        i |= (bArr[i2] & 255) << 16;
        i2 = this.position;
        this.position = i2 + 1;
        i |= (bArr[i2] & 255) << 8;
        i2 = this.position;
        this.position = i2 + 1;
        return (bArr[i2] & 255) | i;
    }

    public int readLittleEndianInt() {
        byte[] bArr = this.data;
        int i = this.position;
        this.position = i + 1;
        i = bArr[i] & 255;
        int i2 = this.position;
        this.position = i2 + 1;
        i |= (bArr[i2] & 255) << 8;
        i2 = this.position;
        this.position = i2 + 1;
        i |= (bArr[i2] & 255) << 16;
        i2 = this.position;
        this.position = i2 + 1;
        return ((bArr[i2] & 255) << 24) | i;
    }

    public long readLong() {
        byte[] bArr = this.data;
        int i = this.position;
        this.position = i + 1;
        long j = (((long) bArr[i]) & 255) << 56;
        int i2 = this.position;
        this.position = i2 + 1;
        j |= (((long) bArr[i2]) & 255) << 48;
        i2 = this.position;
        this.position = i2 + 1;
        j |= (((long) bArr[i2]) & 255) << 40;
        i2 = this.position;
        this.position = i2 + 1;
        j |= (((long) bArr[i2]) & 255) << 32;
        i2 = this.position;
        this.position = i2 + 1;
        j |= (((long) bArr[i2]) & 255) << 24;
        i2 = this.position;
        this.position = i2 + 1;
        j |= (((long) bArr[i2]) & 255) << 16;
        i2 = this.position;
        this.position = i2 + 1;
        j |= (((long) bArr[i2]) & 255) << 8;
        i2 = this.position;
        this.position = i2 + 1;
        return j | (255 & ((long) bArr[i2]));
    }

    public long readLittleEndianLong() {
        byte[] bArr = this.data;
        int i = this.position;
        this.position = i + 1;
        long j = ((long) bArr[i]) & 255;
        int i2 = this.position;
        this.position = i2 + 1;
        j |= (((long) bArr[i2]) & 255) << 8;
        i2 = this.position;
        this.position = i2 + 1;
        j |= (((long) bArr[i2]) & 255) << 16;
        i2 = this.position;
        this.position = i2 + 1;
        j |= (((long) bArr[i2]) & 255) << 24;
        i2 = this.position;
        this.position = i2 + 1;
        j |= (((long) bArr[i2]) & 255) << 32;
        i2 = this.position;
        this.position = i2 + 1;
        j |= (((long) bArr[i2]) & 255) << 40;
        i2 = this.position;
        this.position = i2 + 1;
        j |= (((long) bArr[i2]) & 255) << 48;
        i2 = this.position;
        this.position = i2 + 1;
        return j | ((255 & ((long) bArr[i2])) << 56);
    }

    public int readUnsignedFixedPoint1616() {
        byte[] bArr = this.data;
        int i = this.position;
        this.position = i + 1;
        i = (bArr[i] & 255) << 8;
        int i2 = this.position;
        this.position = i2 + 1;
        int result = (bArr[i2] & 255) | i;
        this.position += 2;
        return result;
    }

    public int readSynchSafeInt() {
        return (((readUnsignedByte() << 21) | (readUnsignedByte() << 14)) | (readUnsignedByte() << 7)) | readUnsignedByte();
    }

    public int readUnsignedIntToInt() {
        int result = readInt();
        if (result >= 0) {
            return result;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Top bit not zero: ");
        stringBuilder.append(result);
        throw new IllegalStateException(stringBuilder.toString());
    }

    public int readLittleEndianUnsignedIntToInt() {
        int result = readLittleEndianInt();
        if (result >= 0) {
            return result;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Top bit not zero: ");
        stringBuilder.append(result);
        throw new IllegalStateException(stringBuilder.toString());
    }

    public long readUnsignedLongToLong() {
        long result = readLong();
        if (result >= 0) {
            return result;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Top bit not zero: ");
        stringBuilder.append(result);
        throw new IllegalStateException(stringBuilder.toString());
    }

    public float readFloat() {
        return Float.intBitsToFloat(readInt());
    }

    public double readDouble() {
        return Double.longBitsToDouble(readLong());
    }

    public String readString(int length) {
        return readString(length, Charset.forName("UTF-8"));
    }

    public String readString(int length, Charset charset) {
        String result = new String(this.data, this.position, length, charset);
        this.position += length;
        return result;
    }

    public String readNullTerminatedString(int length) {
        if (length == 0) {
            return "";
        }
        int stringLength = length;
        int lastIndex = (this.position + length) - 1;
        if (lastIndex < this.limit && this.data[lastIndex] == (byte) 0) {
            stringLength--;
        }
        String result = Util.fromUtf8Bytes(this.data, this.position, stringLength);
        this.position += length;
        return result;
    }

    @Nullable
    public String readNullTerminatedString() {
        if (bytesLeft() == 0) {
            return null;
        }
        int stringLimit = this.position;
        while (stringLimit < this.limit && this.data[stringLimit] != (byte) 0) {
            stringLimit++;
        }
        String string = this.data;
        int i = this.position;
        string = Util.fromUtf8Bytes(string, i, stringLimit - i);
        this.position = stringLimit;
        i = this.position;
        if (i < this.limit) {
            this.position = i + 1;
        }
        return string;
    }

    @Nullable
    public String readLine() {
        if (bytesLeft() == 0) {
            return null;
        }
        byte[] bArr;
        String line;
        int i;
        int i2;
        int lineLimit = this.position;
        while (lineLimit < this.limit && !Util.isLinebreak(this.data[lineLimit])) {
            lineLimit++;
        }
        int i3 = this.position;
        if (lineLimit - i3 >= 3) {
            bArr = this.data;
            if (bArr[i3] == (byte) -17 && bArr[i3 + 1] == ClassDefinitionUtils.OPS_new && bArr[i3 + 2] == (byte) -65) {
                this.position = i3 + 3;
                line = this.data;
                i = this.position;
                line = Util.fromUtf8Bytes(line, i, lineLimit - i);
                this.position = lineLimit;
                i = this.position;
                i2 = this.limit;
                if (i == i2) {
                    return line;
                }
                if (this.data[i] == (byte) 13) {
                    this.position = i + 1;
                    if (this.position == i2) {
                        return line;
                    }
                }
                bArr = this.data;
                i2 = this.position;
                if (bArr[i2] == (byte) 10) {
                    this.position = i2 + 1;
                }
                return line;
            }
        }
        line = this.data;
        i = this.position;
        line = Util.fromUtf8Bytes(line, i, lineLimit - i);
        this.position = lineLimit;
        i = this.position;
        i2 = this.limit;
        if (i == i2) {
            return line;
        }
        if (this.data[i] == (byte) 13) {
            this.position = i + 1;
            if (this.position == i2) {
                return line;
            }
        }
        bArr = this.data;
        i2 = this.position;
        if (bArr[i2] == (byte) 10) {
            this.position = i2 + 1;
        }
        return line;
    }
}
