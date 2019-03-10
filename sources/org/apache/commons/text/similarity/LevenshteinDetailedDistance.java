package org.apache.commons.text.similarity;

public class LevenshteinDetailedDistance implements EditDistance<LevenshteinResults> {
    private static final LevenshteinDetailedDistance DEFAULT_INSTANCE = new LevenshteinDetailedDistance();
    private final Integer threshold;

    private static org.apache.commons.text.similarity.LevenshteinResults limitedCompare(java.lang.CharSequence r20, java.lang.CharSequence r21, int r22) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:59:0x0194 in {7, 8, 9, 12, 13, 14, 17, 18, 21, 24, 27, 32, 33, 36, 39, 40, 45, 46, 47, 48, 52, 54, 56, 58} preds:[]
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
        r0 = r22;
        if (r20 == 0) goto L_0x018b;
    L_0x0004:
        if (r21 == 0) goto L_0x018b;
    L_0x0006:
        if (r0 < 0) goto L_0x0183;
    L_0x0008:
        r1 = r20.length();
        r2 = r21.length();
        r3 = -1;
        r4 = 0;
        if (r1 != 0) goto L_0x0043;
    L_0x0014:
        if (r2 > r0) goto L_0x002c;
    L_0x0016:
        r3 = new org.apache.commons.text.similarity.LevenshteinResults;
        r5 = java.lang.Integer.valueOf(r2);
        r6 = java.lang.Integer.valueOf(r2);
        r7 = java.lang.Integer.valueOf(r4);
        r4 = java.lang.Integer.valueOf(r4);
        r3.<init>(r5, r6, r7, r4);
        goto L_0x0042;
    L_0x002c:
        r5 = new org.apache.commons.text.similarity.LevenshteinResults;
        r3 = java.lang.Integer.valueOf(r3);
        r6 = java.lang.Integer.valueOf(r4);
        r7 = java.lang.Integer.valueOf(r4);
        r4 = java.lang.Integer.valueOf(r4);
        r5.<init>(r3, r6, r7, r4);
        r3 = r5;
    L_0x0042:
        return r3;
    L_0x0043:
        if (r2 != 0) goto L_0x0074;
    L_0x0045:
        if (r1 > r0) goto L_0x005d;
    L_0x0047:
        r3 = new org.apache.commons.text.similarity.LevenshteinResults;
        r5 = java.lang.Integer.valueOf(r1);
        r6 = java.lang.Integer.valueOf(r4);
        r7 = java.lang.Integer.valueOf(r1);
        r4 = java.lang.Integer.valueOf(r4);
        r3.<init>(r5, r6, r7, r4);
        goto L_0x0073;
    L_0x005d:
        r5 = new org.apache.commons.text.similarity.LevenshteinResults;
        r3 = java.lang.Integer.valueOf(r3);
        r6 = java.lang.Integer.valueOf(r4);
        r7 = java.lang.Integer.valueOf(r4);
        r4 = java.lang.Integer.valueOf(r4);
        r5.<init>(r3, r6, r7, r4);
        r3 = r5;
    L_0x0073:
        return r3;
    L_0x0074:
        r5 = 0;
        if (r1 <= r2) goto L_0x0083;
    L_0x0077:
        r6 = r20;
        r7 = r21;
        r8 = r6;
        r1 = r2;
        r2 = r8.length();
        r5 = 1;
        goto L_0x0087;
    L_0x0083:
        r7 = r20;
        r8 = r21;
    L_0x0087:
        r6 = r1 + 1;
        r6 = new int[r6];
        r9 = r1 + 1;
        r9 = new int[r9];
        r10 = r2 + 1;
        r11 = r1 + 1;
        r10 = new int[]{r10, r11};
        r11 = int.class;
        r10 = java.lang.reflect.Array.newInstance(r11, r10);
        r10 = (int[][]) r10;
        r11 = 0;
    L_0x00a0:
        if (r11 > r1) goto L_0x00a9;
    L_0x00a2:
        r12 = r10[r4];
        r12[r11] = r11;
        r11 = r11 + 1;
        goto L_0x00a0;
    L_0x00a9:
        r11 = 0;
    L_0x00aa:
        if (r11 > r2) goto L_0x00b3;
    L_0x00ac:
        r12 = r10[r11];
        r12[r4] = r11;
        r11 = r11 + 1;
        goto L_0x00aa;
    L_0x00b3:
        r11 = java.lang.Math.min(r1, r0);
        r12 = 1;
        r11 = r11 + r12;
        r13 = 0;
    L_0x00ba:
        if (r13 >= r11) goto L_0x00c1;
    L_0x00bc:
        r6[r13] = r13;
        r13 = r13 + 1;
        goto L_0x00ba;
    L_0x00c1:
        r13 = r6.length;
        r14 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        java.util.Arrays.fill(r6, r11, r13, r14);
        java.util.Arrays.fill(r9, r14);
        r13 = 1;
    L_0x00cc:
        if (r13 > r2) goto L_0x0159;
    L_0x00ce:
        r15 = r13 + -1;
        r15 = r8.charAt(r15);
        r9[r4] = r13;
        r4 = r13 - r0;
        r4 = java.lang.Math.max(r12, r4);
        r12 = r14 - r0;
        if (r13 <= r12) goto L_0x00e2;
    L_0x00e0:
        r12 = r1;
        goto L_0x00e8;
    L_0x00e2:
        r12 = r13 + r0;
        r12 = java.lang.Math.min(r1, r12);
    L_0x00e8:
        if (r4 <= r12) goto L_0x0108;
    L_0x00ea:
        r14 = new org.apache.commons.text.similarity.LevenshteinResults;
        r3 = java.lang.Integer.valueOf(r3);
        r21 = r2;
        r16 = 0;
        r2 = java.lang.Integer.valueOf(r16);
        r17 = r11;
        r11 = java.lang.Integer.valueOf(r16);
        r18 = r5;
        r5 = java.lang.Integer.valueOf(r16);
        r14.<init>(r3, r2, r11, r5);
        return r14;
    L_0x0108:
        r21 = r2;
        r18 = r5;
        r17 = r11;
        r2 = 1;
        if (r4 <= r2) goto L_0x0116;
    L_0x0111:
        r2 = r4 + -1;
        r9[r2] = r14;
        goto L_0x0117;
    L_0x0117:
        r2 = r4;
    L_0x0118:
        if (r2 > r12) goto L_0x0149;
    L_0x011a:
        r5 = r2 + -1;
        r5 = r7.charAt(r5);
        if (r5 != r15) goto L_0x012a;
    L_0x0122:
        r5 = r2 + -1;
        r5 = r6[r5];
        r9[r2] = r5;
        r11 = 1;
        goto L_0x0140;
    L_0x012a:
        r5 = r2 + -1;
        r5 = r9[r5];
        r11 = r6[r2];
        r5 = java.lang.Math.min(r5, r11);
        r11 = r2 + -1;
        r11 = r6[r11];
        r5 = java.lang.Math.min(r5, r11);
        r11 = 1;
        r5 = r5 + r11;
        r9[r2] = r5;
    L_0x0140:
        r5 = r10[r13];
        r19 = r9[r2];
        r5[r2] = r19;
        r2 = r2 + 1;
        goto L_0x0118;
    L_0x0149:
        r11 = 1;
        r2 = r6;
        r6 = r9;
        r9 = r2;
        r13 = r13 + 1;
        r2 = r21;
        r11 = r17;
        r5 = r18;
        r4 = 0;
        r12 = 1;
        goto L_0x00cc;
    L_0x0159:
        r21 = r2;
        r18 = r5;
        r17 = r11;
        r2 = r6[r1];
        if (r2 > r0) goto L_0x016a;
    L_0x0163:
        r5 = r18;
        r2 = findDetailedResults(r7, r8, r10, r5);
        return r2;
    L_0x016a:
        r5 = r18;
        r2 = new org.apache.commons.text.similarity.LevenshteinResults;
        r3 = java.lang.Integer.valueOf(r3);
        r4 = 0;
        r11 = java.lang.Integer.valueOf(r4);
        r12 = java.lang.Integer.valueOf(r4);
        r4 = java.lang.Integer.valueOf(r4);
        r2.<init>(r3, r11, r12, r4);
        return r2;
    L_0x0183:
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.text.similarity.LevenshteinDetailedDistance.limitedCompare(java.lang.CharSequence, java.lang.CharSequence, int):org.apache.commons.text.similarity.LevenshteinResults");
    }

    private static org.apache.commons.text.similarity.LevenshteinResults unlimitedCompare(java.lang.CharSequence r14, java.lang.CharSequence r15) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:36:0x00cd in {5, 8, 11, 12, 15, 18, 21, 28, 29, 30, 31, 33, 35} preds:[]
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
        if (r14 == 0) goto L_0x00c4;
    L_0x0002:
        if (r15 == 0) goto L_0x00c4;
    L_0x0004:
        r0 = r14.length();
        r1 = r15.length();
        r2 = 0;
        if (r0 != 0) goto L_0x0025;
    L_0x000f:
        r3 = new org.apache.commons.text.similarity.LevenshteinResults;
        r4 = java.lang.Integer.valueOf(r1);
        r5 = java.lang.Integer.valueOf(r1);
        r6 = java.lang.Integer.valueOf(r2);
        r2 = java.lang.Integer.valueOf(r2);
        r3.<init>(r4, r5, r6, r2);
        return r3;
    L_0x0025:
        if (r1 != 0) goto L_0x003d;
    L_0x0027:
        r3 = new org.apache.commons.text.similarity.LevenshteinResults;
        r4 = java.lang.Integer.valueOf(r0);
        r5 = java.lang.Integer.valueOf(r2);
        r6 = java.lang.Integer.valueOf(r0);
        r2 = java.lang.Integer.valueOf(r2);
        r3.<init>(r4, r5, r6, r2);
        return r3;
    L_0x003d:
        r3 = 0;
        if (r0 <= r1) goto L_0x004a;
    L_0x0040:
        r4 = r14;
        r14 = r15;
        r15 = r4;
        r0 = r1;
        r1 = r15.length();
        r3 = 1;
        goto L_0x004b;
    L_0x004b:
        r4 = r0 + 1;
        r4 = new int[r4];
        r5 = r0 + 1;
        r5 = new int[r5];
        r6 = r1 + 1;
        r7 = r0 + 1;
        r6 = new int[]{r6, r7};
        r7 = int.class;
        r6 = java.lang.reflect.Array.newInstance(r7, r6);
        r6 = (int[][]) r6;
        r7 = 0;
    L_0x0064:
        if (r7 > r0) goto L_0x006d;
    L_0x0066:
        r8 = r6[r2];
        r8[r7] = r7;
        r7 = r7 + 1;
        goto L_0x0064;
    L_0x006d:
        r7 = 0;
    L_0x006e:
        if (r7 > r1) goto L_0x0077;
    L_0x0070:
        r8 = r6[r7];
        r8[r2] = r7;
        r7 = r7 + 1;
        goto L_0x006e;
    L_0x0077:
        r7 = 0;
    L_0x0078:
        if (r7 > r0) goto L_0x007f;
    L_0x007a:
        r4[r7] = r7;
        r7 = r7 + 1;
        goto L_0x0078;
    L_0x007f:
        r8 = 1;
    L_0x0080:
        if (r8 > r1) goto L_0x00bf;
    L_0x0082:
        r9 = r8 + -1;
        r9 = r15.charAt(r9);
        r5[r2] = r8;
        r7 = 1;
    L_0x008b:
        if (r7 > r0) goto L_0x00b9;
    L_0x008d:
        r10 = r7 + -1;
        r10 = r14.charAt(r10);
        r11 = 1;
        if (r10 != r9) goto L_0x0098;
    L_0x0096:
        r10 = 0;
        goto L_0x0099;
    L_0x0098:
        r10 = 1;
    L_0x0099:
        r12 = r7 + -1;
        r12 = r5[r12];
        r12 = r12 + r11;
        r13 = r4[r7];
        r13 = r13 + r11;
        r11 = java.lang.Math.min(r12, r13);
        r12 = r7 + -1;
        r12 = r4[r12];
        r12 = r12 + r10;
        r11 = java.lang.Math.min(r11, r12);
        r5[r7] = r11;
        r11 = r6[r8];
        r12 = r5[r7];
        r11[r7] = r12;
        r7 = r7 + 1;
        goto L_0x008b;
    L_0x00b9:
        r10 = r4;
        r4 = r5;
        r5 = r10;
        r8 = r8 + 1;
        goto L_0x0080;
    L_0x00bf:
        r2 = findDetailedResults(r14, r15, r6, r3);
        return r2;
        r0 = new java.lang.IllegalArgumentException;
        r1 = "Strings must not be null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.text.similarity.LevenshteinDetailedDistance.unlimitedCompare(java.lang.CharSequence, java.lang.CharSequence):org.apache.commons.text.similarity.LevenshteinResults");
    }

    public LevenshteinDetailedDistance() {
        this(null);
    }

    public LevenshteinDetailedDistance(Integer threshold) {
        if (threshold != null) {
            if (threshold.intValue() < 0) {
                throw new IllegalArgumentException("Threshold must not be negative");
            }
        }
        this.threshold = threshold;
    }

    public LevenshteinResults apply(CharSequence left, CharSequence right) {
        Integer num = this.threshold;
        if (num != null) {
            return limitedCompare(left, right, num.intValue());
        }
        return unlimitedCompare(left, right);
    }

    public static LevenshteinDetailedDistance getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public Integer getThreshold() {
        return this.threshold;
    }

    private static LevenshteinResults findDetailedResults(CharSequence left, CharSequence right, int[][] matrix, boolean swapped) {
        CharSequence charSequence;
        CharSequence charSequence2;
        int delCount = 0;
        int addCount = 0;
        int subCount = 0;
        int rowIndex = right.length();
        int columnIndex = left.length();
        while (rowIndex >= 0 && columnIndex >= 0) {
            int dataAtLeft;
            int dataAtTop;
            if (columnIndex == 0) {
                dataAtLeft = -1;
            } else {
                dataAtLeft = matrix[rowIndex][columnIndex - 1];
            }
            if (rowIndex == 0) {
                dataAtTop = -1;
            } else {
                dataAtTop = matrix[rowIndex - 1][columnIndex];
            }
            int dataAtDiagonal;
            if (rowIndex <= 0 || columnIndex <= 0) {
                dataAtDiagonal = -1;
            } else {
                dataAtDiagonal = matrix[rowIndex - 1][columnIndex - 1];
            }
            if (dataAtLeft == -1 && dataAtTop == -1 && dataAtDiagonal == -1) {
                charSequence = left;
                charSequence2 = right;
                break;
            }
            int data = matrix[rowIndex][columnIndex];
            if (columnIndex <= 0 || rowIndex <= 0) {
                charSequence = left;
                charSequence2 = right;
            } else {
                if (left.charAt(columnIndex - 1) == right.charAt(rowIndex - 1)) {
                    columnIndex--;
                    rowIndex--;
                }
            }
            boolean deleted = false;
            boolean added = false;
            if ((data - 1 == dataAtLeft && data <= dataAtDiagonal && data <= dataAtTop) || (dataAtDiagonal == -1 && dataAtTop == -1)) {
                columnIndex--;
                if (swapped) {
                    addCount++;
                    added = true;
                } else {
                    delCount++;
                    deleted = true;
                }
            } else if ((data - 1 == dataAtTop && data <= dataAtDiagonal && data <= dataAtLeft) || (dataAtDiagonal == -1 && dataAtLeft == -1)) {
                rowIndex--;
                if (swapped) {
                    delCount++;
                    deleted = true;
                } else {
                    addCount++;
                    added = true;
                }
            }
            if (!added && !deleted) {
                subCount++;
                columnIndex--;
                rowIndex--;
            }
        }
        charSequence = left;
        charSequence2 = right;
        return new LevenshteinResults(Integer.valueOf((addCount + delCount) + subCount), Integer.valueOf(addCount), Integer.valueOf(delCount), Integer.valueOf(subCount));
    }
}
