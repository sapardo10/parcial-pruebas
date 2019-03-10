package org.apache.commons.text.similarity;

import java.lang.reflect.Array;

public class LongestCommonSubsequence implements SimilarityScore<Integer> {
    public java.lang.CharSequence longestCommonSubsequence(java.lang.CharSequence r9, java.lang.CharSequence r10) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:15:0x0073 in {6, 9, 10, 12, 14} preds:[]
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
        r8 = this;
        if (r9 == 0) goto L_0x006a;
    L_0x0002:
        if (r10 == 0) goto L_0x006a;
    L_0x0004:
        r0 = new java.lang.StringBuilder;
        r1 = r9.length();
        r2 = r10.length();
        r1 = java.lang.Math.max(r1, r2);
        r0.<init>(r1);
        r1 = r8.longestCommonSubstringLengthArray(r9, r10);
        r2 = r9.length();
        r2 = r2 + -1;
        r3 = r10.length();
        r3 = r3 + -1;
        r4 = r9.length();
        r4 = r1[r4];
        r5 = r10.length();
        r4 = r4[r5];
        r4 = r4 + -1;
    L_0x0033:
        if (r4 < 0) goto L_0x0061;
    L_0x0035:
        r5 = r9.charAt(r2);
        r6 = r10.charAt(r3);
        if (r5 != r6) goto L_0x004d;
    L_0x003f:
        r5 = r9.charAt(r2);
        r0.append(r5);
        r2 = r2 + -1;
        r3 = r3 + -1;
        r4 = r4 + -1;
        goto L_0x0033;
    L_0x004d:
        r5 = r2 + 1;
        r5 = r1[r5];
        r5 = r5[r3];
        r6 = r1[r2];
        r7 = r3 + 1;
        r6 = r6[r7];
        if (r5 >= r6) goto L_0x005e;
    L_0x005b:
        r2 = r2 + -1;
        goto L_0x0033;
    L_0x005e:
        r3 = r3 + -1;
        goto L_0x0033;
    L_0x0061:
        r5 = r0.reverse();
        r5 = r5.toString();
        return r5;
        r0 = new java.lang.IllegalArgumentException;
        r1 = "Inputs must not be null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.text.similarity.LongestCommonSubsequence.longestCommonSubsequence(java.lang.CharSequence, java.lang.CharSequence):java.lang.CharSequence");
    }

    public Integer apply(CharSequence left, CharSequence right) {
        if (left != null && right != null) {
            return Integer.valueOf(longestCommonSubsequence(left, right).length());
        }
        throw new IllegalArgumentException("Inputs must not be null");
    }

    @Deprecated
    public CharSequence logestCommonSubsequence(CharSequence left, CharSequence right) {
        return longestCommonSubsequence(left, right);
    }

    public int[][] longestCommonSubstringLengthArray(CharSequence left, CharSequence right) {
        int[][] lcsLengthArray = (int[][]) Array.newInstance(int.class, new int[]{left.length() + 1, right.length() + 1});
        for (int i = 0; i < left.length(); i++) {
            for (int j = 0; j < right.length(); j++) {
                if (i == 0) {
                    lcsLengthArray[i][j] = 0;
                }
                if (j == 0) {
                    lcsLengthArray[i][j] = 0;
                }
                if (left.charAt(i) == right.charAt(j)) {
                    lcsLengthArray[i + 1][j + 1] = lcsLengthArray[i][j] + 1;
                } else {
                    lcsLengthArray[i + 1][j + 1] = Math.max(lcsLengthArray[i + 1][j], lcsLengthArray[i][j + 1]);
                }
            }
        }
        return lcsLengthArray;
    }
}
