package org.apache.commons.text.similarity;

public class LevenshteinDistance implements EditDistance<Integer> {
    private static final LevenshteinDistance DEFAULT_INSTANCE = new LevenshteinDistance();
    private final Integer threshold;

    private static int limitedCompare(java.lang.CharSequence r16, java.lang.CharSequence r17, int r18) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:48:0x00cb in {7, 8, 11, 12, 14, 15, 18, 23, 24, 26, 28, 29, 34, 35, 36, 37, 41, 43, 45, 47} preds:[]
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
        r0 = r18;
        if (r16 == 0) goto L_0x00c2;
    L_0x0004:
        if (r17 == 0) goto L_0x00c2;
    L_0x0006:
        if (r0 < 0) goto L_0x00ba;
    L_0x0008:
        r1 = r16.length();
        r2 = r17.length();
        r3 = -1;
        if (r1 != 0) goto L_0x0018;
    L_0x0013:
        if (r2 > r0) goto L_0x0017;
    L_0x0015:
        r3 = r2;
    L_0x0017:
        return r3;
    L_0x0018:
        if (r2 != 0) goto L_0x001f;
    L_0x001a:
        if (r1 > r0) goto L_0x001e;
    L_0x001c:
        r3 = r1;
    L_0x001e:
        return r3;
    L_0x001f:
        if (r1 <= r2) goto L_0x002c;
    L_0x0021:
        r4 = r16;
        r5 = r17;
        r6 = r4;
        r1 = r2;
        r2 = r6.length();
        goto L_0x0030;
    L_0x002c:
        r5 = r16;
        r6 = r17;
    L_0x0030:
        r4 = r1 + 1;
        r4 = new int[r4];
        r7 = r1 + 1;
        r7 = new int[r7];
        r8 = java.lang.Math.min(r1, r0);
        r9 = 1;
        r8 = r8 + r9;
        r10 = 0;
    L_0x003f:
        if (r10 >= r8) goto L_0x0046;
    L_0x0041:
        r4[r10] = r10;
        r10 = r10 + 1;
        goto L_0x003f;
    L_0x0046:
        r10 = r4.length;
        r11 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        java.util.Arrays.fill(r4, r8, r10, r11);
        java.util.Arrays.fill(r7, r11);
        r10 = 1;
    L_0x0051:
        if (r10 > r2) goto L_0x00b1;
    L_0x0053:
        r12 = r10 + -1;
        r12 = r6.charAt(r12);
        r13 = 0;
        r7[r13] = r10;
        r13 = r10 - r0;
        r13 = java.lang.Math.max(r9, r13);
        r14 = r11 - r0;
        if (r10 <= r14) goto L_0x0068;
    L_0x0066:
        r14 = r1;
        goto L_0x006e;
    L_0x0068:
        r14 = r10 + r0;
        r14 = java.lang.Math.min(r1, r14);
    L_0x006e:
        if (r13 <= r14) goto L_0x0071;
    L_0x0070:
        return r3;
    L_0x0071:
        if (r13 <= r9) goto L_0x0078;
    L_0x0073:
        r15 = r13 + -1;
        r7[r15] = r11;
        goto L_0x0079;
    L_0x0079:
        r15 = r13;
    L_0x007a:
        if (r15 > r14) goto L_0x00a7;
    L_0x007c:
        r11 = r15 + -1;
        r11 = r5.charAt(r11);
        if (r11 != r12) goto L_0x008b;
    L_0x0084:
        r11 = r15 + -1;
        r11 = r4[r11];
        r7[r15] = r11;
        goto L_0x00a0;
    L_0x008b:
        r11 = r15 + -1;
        r11 = r7[r11];
        r3 = r4[r15];
        r3 = java.lang.Math.min(r11, r3);
        r11 = r15 + -1;
        r11 = r4[r11];
        r3 = java.lang.Math.min(r3, r11);
        r3 = r3 + r9;
        r7[r15] = r3;
    L_0x00a0:
        r15 = r15 + 1;
        r3 = -1;
        r11 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        goto L_0x007a;
    L_0x00a7:
        r3 = r4;
        r4 = r7;
        r7 = r3;
        r10 = r10 + 1;
        r3 = -1;
        r11 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        goto L_0x0051;
    L_0x00b1:
        r3 = r4[r1];
        if (r3 > r0) goto L_0x00b8;
    L_0x00b5:
        r3 = r4[r1];
        return r3;
    L_0x00b8:
        r3 = -1;
        return r3;
    L_0x00ba:
        r1 = new java.lang.IllegalArgumentException;
        r2 = "Threshold must not be negative";
        r1.<init>(r2);
        throw r1;
        r1 = new java.lang.IllegalArgumentException;
        r2 = "Strings must not be null";
        r1.<init>(r2);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.text.similarity.LevenshteinDistance.limitedCompare(java.lang.CharSequence, java.lang.CharSequence, int):int");
    }

    private static int unlimitedCompare(java.lang.CharSequence r13, java.lang.CharSequence r14) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:27:0x0070 in {4, 6, 8, 9, 12, 19, 20, 21, 22, 24, 26} preds:[]
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
        if (r13 == 0) goto L_0x0067;
    L_0x0002:
        if (r14 == 0) goto L_0x0067;
    L_0x0004:
        r0 = r13.length();
        r1 = r14.length();
        if (r0 != 0) goto L_0x000f;
    L_0x000e:
        return r1;
    L_0x000f:
        if (r1 != 0) goto L_0x0012;
    L_0x0011:
        return r0;
    L_0x0012:
        if (r0 <= r1) goto L_0x001d;
    L_0x0014:
        r2 = r13;
        r13 = r14;
        r14 = r2;
        r0 = r1;
        r1 = r14.length();
        goto L_0x001e;
    L_0x001e:
        r2 = r0 + 1;
        r2 = new int[r2];
        r3 = 0;
    L_0x0023:
        if (r3 > r0) goto L_0x002a;
    L_0x0025:
        r2[r3] = r3;
        r3 = r3 + 1;
        goto L_0x0023;
    L_0x002a:
        r4 = 1;
    L_0x002b:
        if (r4 > r1) goto L_0x0064;
    L_0x002d:
        r5 = 0;
        r6 = r2[r5];
        r7 = r4 + -1;
        r7 = r14.charAt(r7);
        r2[r5] = r4;
        r3 = 1;
    L_0x0039:
        if (r3 > r0) goto L_0x0061;
    L_0x003b:
        r8 = r2[r3];
        r9 = r3 + -1;
        r9 = r13.charAt(r9);
        r10 = 1;
        if (r9 != r7) goto L_0x0048;
    L_0x0046:
        r9 = 0;
        goto L_0x0049;
    L_0x0048:
        r9 = 1;
    L_0x0049:
        r11 = r3 + -1;
        r11 = r2[r11];
        r11 = r11 + r10;
        r12 = r2[r3];
        r12 = r12 + r10;
        r10 = java.lang.Math.min(r11, r12);
        r11 = r6 + r9;
        r10 = java.lang.Math.min(r10, r11);
        r2[r3] = r10;
        r6 = r8;
        r3 = r3 + 1;
        goto L_0x0039;
    L_0x0061:
        r4 = r4 + 1;
        goto L_0x002b;
    L_0x0064:
        r5 = r2[r0];
        return r5;
        r0 = new java.lang.IllegalArgumentException;
        r1 = "Strings must not be null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.text.similarity.LevenshteinDistance.unlimitedCompare(java.lang.CharSequence, java.lang.CharSequence):int");
    }

    public LevenshteinDistance() {
        this(null);
    }

    public LevenshteinDistance(Integer threshold) {
        if (threshold != null) {
            if (threshold.intValue() < 0) {
                throw new IllegalArgumentException("Threshold must not be negative");
            }
        }
        this.threshold = threshold;
    }

    public Integer apply(CharSequence left, CharSequence right) {
        Integer num = this.threshold;
        if (num != null) {
            return Integer.valueOf(limitedCompare(left, right, num.intValue()));
        }
        return Integer.valueOf(unlimitedCompare(left, right));
    }

    public static LevenshteinDistance getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public Integer getThreshold() {
        return this.threshold;
    }
}
