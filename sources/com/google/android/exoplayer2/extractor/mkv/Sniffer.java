package com.google.android.exoplayer2.extractor.mkv;

import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.io.IOException;

final class Sniffer {
    private static final int ID_EBML = 440786851;
    private static final int SEARCH_LENGTH = 1024;
    private int peekLength;
    private final ParsableByteArray scratch = new ParsableByteArray(8);

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean sniff(com.google.android.exoplayer2.extractor.ExtractorInput r25) throws java.io.IOException, java.lang.InterruptedException {
        /*
        r24 = this;
        r0 = r24;
        r1 = r25;
        r2 = r25.getLength();
        r4 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
        r6 = -1;
        r8 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1));
        if (r8 == 0) goto L_0x0017;
    L_0x0010:
        r8 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r8 <= 0) goto L_0x0015;
    L_0x0014:
        goto L_0x0017;
    L_0x0015:
        r4 = r2;
    L_0x0017:
        r4 = (int) r4;
        r5 = r0.scratch;
        r5 = r5.data;
        r8 = 4;
        r9 = 0;
        r1.peekFully(r5, r9, r8);
        r5 = r0.scratch;
        r10 = r5.readUnsignedInt();
        r0.peekLength = r8;
    L_0x0029:
        r12 = 440786851; // 0x1a45dfa3 float:4.0919297E-23 double:2.1777764E-315;
        r5 = 1;
        r8 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1));
        if (r8 == 0) goto L_0x0053;
    L_0x0031:
        r8 = r0.peekLength;
        r8 = r8 + r5;
        r0.peekLength = r8;
        if (r8 != r4) goto L_0x0039;
    L_0x0038:
        return r9;
    L_0x0039:
        r8 = r0.scratch;
        r8 = r8.data;
        r1.peekFully(r8, r9, r5);
        r5 = 8;
        r12 = r10 << r5;
        r14 = -256; // 0xffffffffffffff00 float:NaN double:NaN;
        r10 = r12 & r14;
        r5 = r0.scratch;
        r5 = r5.data;
        r5 = r5[r9];
        r5 = r5 & 255;
        r12 = (long) r5;
        r10 = r10 | r12;
        goto L_0x0029;
    L_0x0053:
        r12 = r24.readUint(r25);
        r8 = r0.peekLength;
        r14 = (long) r8;
        r16 = -9223372036854775808;
        r8 = (r12 > r16 ? 1 : (r12 == r16 ? 0 : -1));
        if (r8 == 0) goto L_0x00b1;
    L_0x0060:
        r8 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1));
        if (r8 == 0) goto L_0x006b;
    L_0x0064:
        r6 = r14 + r12;
        r8 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1));
        if (r8 < 0) goto L_0x006b;
    L_0x006a:
        goto L_0x00b1;
    L_0x006b:
        r6 = r0.peekLength;
        r7 = (long) r6;
        r18 = r14 + r12;
        r20 = (r7 > r18 ? 1 : (r7 == r18 ? 0 : -1));
        if (r20 >= 0) goto L_0x00a4;
    L_0x0074:
        r6 = r24.readUint(r25);
        r8 = (r6 > r16 ? 1 : (r6 == r16 ? 0 : -1));
        if (r8 != 0) goto L_0x007d;
    L_0x007c:
        return r9;
    L_0x007d:
        r18 = r6;
        r5 = r24.readUint(r25);
        r20 = 0;
        r7 = (r5 > r20 ? 1 : (r5 == r20 ? 0 : -1));
        if (r7 < 0) goto L_0x00a2;
    L_0x0089:
        r22 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        r7 = (r5 > r22 ? 1 : (r5 == r22 ? 0 : -1));
        if (r7 <= 0) goto L_0x0091;
    L_0x0090:
        goto L_0x00a2;
    L_0x0091:
        r7 = (r5 > r20 ? 1 : (r5 == r20 ? 0 : -1));
        if (r7 == 0) goto L_0x009f;
    L_0x0095:
        r7 = (int) r5;
        r1.advancePeekPosition(r7);
        r8 = r0.peekLength;
        r8 = r8 + r7;
        r0.peekLength = r8;
        goto L_0x00a0;
    L_0x00a0:
        r5 = 1;
        goto L_0x006b;
        return r9;
    L_0x00a4:
        r5 = (long) r6;
        r7 = r14 + r12;
        r16 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));
        if (r16 != 0) goto L_0x00ae;
    L_0x00ab:
        r20 = 1;
        goto L_0x00b0;
    L_0x00ae:
        r20 = 0;
    L_0x00b0:
        return r20;
        return r9;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.mkv.Sniffer.sniff(com.google.android.exoplayer2.extractor.ExtractorInput):boolean");
    }

    private long readUint(ExtractorInput input) throws IOException, InterruptedException {
        input.peekFully(this.scratch.data, 0, 1);
        int value = this.scratch.data[0] & 255;
        if (value == 0) {
            return Long.MIN_VALUE;
        }
        int mask = 128;
        int length = 0;
        while ((value & mask) == 0) {
            mask >>= 1;
            length++;
        }
        value &= mask ^ -1;
        input.peekFully(this.scratch.data, 1, length);
        for (int i = 0; i < length; i++) {
            value = (value << 8) + (this.scratch.data[i + 1] & 255);
        }
        this.peekLength += length + 1;
        return (long) value;
    }
}
