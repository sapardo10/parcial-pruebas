package okio;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

final class SegmentedByteString extends ByteString {
    final transient int[] directory;
    final transient byte[][] segments;

    public void write(java.io.OutputStream r8) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:7:0x0027 in {3, 4, 6} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r7 = this;
        if (r8 == 0) goto L_0x001f;
    L_0x0002:
        r0 = 0;
        r1 = 0;
        r2 = r7.segments;
        r2 = r2.length;
    L_0x0007:
        if (r1 >= r2) goto L_0x001e;
    L_0x0009:
        r3 = r7.directory;
        r4 = r2 + r1;
        r4 = r3[r4];
        r3 = r3[r1];
        r5 = r7.segments;
        r5 = r5[r1];
        r6 = r3 - r0;
        r8.write(r5, r4, r6);
        r0 = r3;
        r1 = r1 + 1;
        goto L_0x0007;
    L_0x001e:
        return;
    L_0x001f:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "out == null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.SegmentedByteString.write(java.io.OutputStream):void");
    }

    SegmentedByteString(Buffer buffer, int byteCount) {
        super(null);
        Util.checkOffsetAndCount(buffer.size, 0, (long) byteCount);
        int offset = 0;
        int segmentCount = 0;
        Segment s = buffer.head;
        while (offset < byteCount) {
            if (s.limit != s.pos) {
                offset += s.limit - s.pos;
                segmentCount++;
                s = s.next;
            } else {
                throw new AssertionError("s.limit == s.pos");
            }
        }
        this.segments = new byte[segmentCount][];
        this.directory = new int[(segmentCount * 2)];
        offset = 0;
        segmentCount = 0;
        s = buffer.head;
        while (offset < byteCount) {
            this.segments[segmentCount] = s.data;
            offset += s.limit - s.pos;
            if (offset > byteCount) {
                offset = byteCount;
            }
            int[] iArr = this.directory;
            iArr[segmentCount] = offset;
            iArr[this.segments.length + segmentCount] = s.pos;
            s.shared = true;
            segmentCount++;
            s = s.next;
        }
    }

    public String utf8() {
        return toByteString().utf8();
    }

    public String string(Charset charset) {
        return toByteString().string(charset);
    }

    public String base64() {
        return toByteString().base64();
    }

    public String hex() {
        return toByteString().hex();
    }

    public ByteString toAsciiLowercase() {
        return toByteString().toAsciiLowercase();
    }

    public ByteString toAsciiUppercase() {
        return toByteString().toAsciiUppercase();
    }

    public ByteString md5() {
        return toByteString().md5();
    }

    public ByteString sha1() {
        return toByteString().sha1();
    }

    public ByteString sha256() {
        return toByteString().sha256();
    }

    public ByteString hmacSha1(ByteString key) {
        return toByteString().hmacSha1(key);
    }

    public ByteString hmacSha256(ByteString key) {
        return toByteString().hmacSha256(key);
    }

    public String base64Url() {
        return toByteString().base64Url();
    }

    public ByteString substring(int beginIndex) {
        return toByteString().substring(beginIndex);
    }

    public ByteString substring(int beginIndex, int endIndex) {
        return toByteString().substring(beginIndex, endIndex);
    }

    public byte getByte(int pos) {
        Util.checkOffsetAndCount((long) this.directory[this.segments.length - 1], (long) pos, 1);
        int segment = segment(pos);
        int segmentOffset = segment == 0 ? 0 : this.directory[segment - 1];
        int segmentPos = this.directory;
        byte[][] bArr = this.segments;
        return bArr[segment][(pos - segmentOffset) + segmentPos[bArr.length + segment]];
    }

    private int segment(int pos) {
        int i = Arrays.binarySearch(this.directory, 0, this.segments.length, pos + 1);
        return i >= 0 ? i : i ^ -1;
    }

    public int size() {
        return this.directory[this.segments.length - 1];
    }

    public byte[] toByteArray() {
        int[] iArr = this.directory;
        int segmentCount = this.segments;
        byte[] result = new byte[iArr[segmentCount.length - 1]];
        int segmentOffset = 0;
        segmentCount = segmentCount.length;
        for (int s = 0; s < segmentCount; s++) {
            int nextSegmentOffset = this.directory;
            int segmentPos = nextSegmentOffset[segmentCount + s];
            nextSegmentOffset = nextSegmentOffset[s];
            System.arraycopy(this.segments[s], segmentPos, result, segmentOffset, nextSegmentOffset - segmentOffset);
            segmentOffset = nextSegmentOffset;
        }
        return result;
    }

    public ByteBuffer asByteBuffer() {
        return ByteBuffer.wrap(toByteArray()).asReadOnlyBuffer();
    }

    void write(Buffer buffer) {
        int segmentOffset = 0;
        int segmentCount = this.segments.length;
        for (int s = 0; s < segmentCount; s++) {
            int nextSegmentOffset = this.directory;
            int segmentPos = nextSegmentOffset[segmentCount + s];
            nextSegmentOffset = nextSegmentOffset[s];
            Segment segment = new Segment(this.segments[s], segmentPos, (segmentPos + nextSegmentOffset) - segmentOffset, true, false);
            if (buffer.head == null) {
                segment.prev = segment;
                segment.next = segment;
                buffer.head = segment;
            } else {
                buffer.head.prev.push(segment);
            }
            segmentOffset = nextSegmentOffset;
        }
        buffer.size += (long) segmentOffset;
    }

    public boolean rangeEquals(int offset, ByteString other, int otherOffset, int byteCount) {
        if (offset >= 0) {
            if (offset <= size() - byteCount) {
                int s = segment(offset);
                while (byteCount > 0) {
                    int segmentOffset = s == 0 ? 0 : this.directory[s - 1];
                    int stepSize = Math.min(byteCount, (segmentOffset + (this.directory[s] - segmentOffset)) - offset);
                    int segmentPos = this.directory;
                    byte[][] bArr = this.segments;
                    if (!other.rangeEquals(otherOffset, bArr[s], (offset - segmentOffset) + segmentPos[bArr.length + s], stepSize)) {
                        return false;
                    }
                    offset += stepSize;
                    otherOffset += stepSize;
                    byteCount -= stepSize;
                    s++;
                }
                return true;
            }
        }
        return false;
    }

    public boolean rangeEquals(int offset, byte[] other, int otherOffset, int byteCount) {
        if (offset >= 0 && offset <= size() - byteCount && otherOffset >= 0) {
            if (otherOffset <= other.length - byteCount) {
                int s = segment(offset);
                while (byteCount > 0) {
                    int segmentOffset = s == 0 ? 0 : this.directory[s - 1];
                    int stepSize = Math.min(byteCount, (segmentOffset + (this.directory[s] - segmentOffset)) - offset);
                    int segmentPos = this.directory;
                    byte[][] bArr = this.segments;
                    if (!Util.arrayRangeEquals(bArr[s], (offset - segmentOffset) + segmentPos[bArr.length + s], other, otherOffset, stepSize)) {
                        return false;
                    }
                    offset += stepSize;
                    otherOffset += stepSize;
                    byteCount -= stepSize;
                    s++;
                }
                return true;
            }
        }
        return false;
    }

    public int indexOf(byte[] other, int fromIndex) {
        return toByteString().indexOf(other, fromIndex);
    }

    public int lastIndexOf(byte[] other, int fromIndex) {
        return toByteString().lastIndexOf(other, fromIndex);
    }

    private ByteString toByteString() {
        return new ByteString(toByteArray());
    }

    byte[] internalArray() {
        return toByteArray();
    }

    public boolean equals(Object o) {
        boolean z = true;
        if (o == this) {
            return true;
        }
        if (o instanceof ByteString) {
            if (((ByteString) o).size() == size()) {
                if (rangeEquals(0, (ByteString) o, 0, size())) {
                    return z;
                }
            }
        }
        z = false;
        return z;
    }

    public int hashCode() {
        int result = this.hashCode;
        if (result != 0) {
            return result;
        }
        result = 1;
        int segmentOffset = 0;
        int segmentCount = this.segments.length;
        for (int s = 0; s < segmentCount; s++) {
            byte[] segment = this.segments[s];
            int nextSegmentOffset = this.directory;
            int segmentPos = nextSegmentOffset[segmentCount + s];
            nextSegmentOffset = nextSegmentOffset[s];
            for (int i = segmentPos; i < segmentPos + (nextSegmentOffset - segmentOffset); i++) {
                result = (result * 31) + segment[i];
            }
            segmentOffset = nextSegmentOffset;
        }
        this.hashCode = result;
        return result;
    }

    public String toString() {
        return toByteString().toString();
    }

    private Object writeReplace() {
        return toByteString();
    }
}
