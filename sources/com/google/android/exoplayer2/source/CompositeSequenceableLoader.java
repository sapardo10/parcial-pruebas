package com.google.android.exoplayer2.source;

public class CompositeSequenceableLoader implements SequenceableLoader {
    protected final SequenceableLoader[] loaders;

    public CompositeSequenceableLoader(SequenceableLoader[] loaders) {
        this.loaders = loaders;
    }

    public final long getBufferedPositionUs() {
        long bufferedPositionUs = Long.MAX_VALUE;
        for (SequenceableLoader loader : this.loaders) {
            long loaderBufferedPositionUs = loader.getBufferedPositionUs();
            if (loaderBufferedPositionUs != Long.MIN_VALUE) {
                bufferedPositionUs = Math.min(bufferedPositionUs, loaderBufferedPositionUs);
            }
        }
        if (bufferedPositionUs == Long.MAX_VALUE) {
            return Long.MIN_VALUE;
        }
        return bufferedPositionUs;
    }

    public final long getNextLoadPositionUs() {
        long nextLoadPositionUs = Long.MAX_VALUE;
        for (SequenceableLoader loader : this.loaders) {
            long loaderNextLoadPositionUs = loader.getNextLoadPositionUs();
            if (loaderNextLoadPositionUs != Long.MIN_VALUE) {
                nextLoadPositionUs = Math.min(nextLoadPositionUs, loaderNextLoadPositionUs);
            }
        }
        if (nextLoadPositionUs == Long.MAX_VALUE) {
            return Long.MIN_VALUE;
        }
        return nextLoadPositionUs;
    }

    public final void reevaluateBuffer(long positionUs) {
        for (SequenceableLoader loader : this.loaders) {
            loader.reevaluateBuffer(positionUs);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean continueLoading(long r19) {
        /*
        r18 = this;
        r0 = r19;
        r2 = 0;
    L_0x0003:
        r3 = 0;
        r4 = r18.getNextLoadPositionUs();
        r6 = -9223372036854775808;
        r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r8 != 0) goto L_0x0011;
    L_0x000e:
        r8 = r18;
        goto L_0x0043;
    L_0x0011:
        r8 = r18;
        r9 = r8.loaders;
        r10 = r9.length;
        r11 = 0;
        r12 = r3;
        r3 = 0;
    L_0x0019:
        if (r3 >= r10) goto L_0x003f;
    L_0x001b:
        r13 = r9[r3];
        r14 = r13.getNextLoadPositionUs();
        r16 = (r14 > r6 ? 1 : (r14 == r6 ? 0 : -1));
        if (r16 == 0) goto L_0x002c;
    L_0x0025:
        r16 = (r14 > r0 ? 1 : (r14 == r0 ? 0 : -1));
        if (r16 > 0) goto L_0x002c;
    L_0x0029:
        r16 = 1;
        goto L_0x002e;
    L_0x002c:
        r16 = 0;
    L_0x002e:
        r17 = (r14 > r4 ? 1 : (r14 == r4 ? 0 : -1));
        if (r17 == 0) goto L_0x0036;
    L_0x0032:
        if (r16 == 0) goto L_0x0035;
    L_0x0034:
        goto L_0x0036;
    L_0x0035:
        goto L_0x003c;
    L_0x0036:
        r17 = r13.continueLoading(r0);
        r12 = r12 | r17;
    L_0x003c:
        r3 = r3 + 1;
        goto L_0x0019;
    L_0x003f:
        r2 = r2 | r12;
        if (r12 != 0) goto L_0x0044;
    L_0x0042:
        r3 = r12;
    L_0x0043:
        return r2;
    L_0x0044:
        goto L_0x0003;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.CompositeSequenceableLoader.continueLoading(long):boolean");
    }
}
