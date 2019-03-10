package org.apache.commons.text.similarity;

public class HammingDistance implements EditDistance<Integer> {
    public java.lang.Integer apply(java.lang.CharSequence r5, java.lang.CharSequence r6) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:18:0x003d in {9, 10, 11, 13, 15, 17} preds:[]
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
        r4 = this;
        if (r5 == 0) goto L_0x0034;
    L_0x0002:
        if (r6 == 0) goto L_0x0034;
    L_0x0004:
        r0 = r5.length();
        r1 = r6.length();
        if (r0 != r1) goto L_0x002c;
    L_0x000e:
        r0 = 0;
        r1 = 0;
    L_0x0010:
        r2 = r5.length();
        if (r1 >= r2) goto L_0x0027;
    L_0x0016:
        r2 = r5.charAt(r1);
        r3 = r6.charAt(r1);
        if (r2 == r3) goto L_0x0023;
    L_0x0020:
        r0 = r0 + 1;
        goto L_0x0024;
    L_0x0024:
        r1 = r1 + 1;
        goto L_0x0010;
    L_0x0027:
        r1 = java.lang.Integer.valueOf(r0);
        return r1;
    L_0x002c:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "Strings must have the same length";
        r0.<init>(r1);
        throw r0;
        r0 = new java.lang.IllegalArgumentException;
        r1 = "Strings must not be null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.text.similarity.HammingDistance.apply(java.lang.CharSequence, java.lang.CharSequence):java.lang.Integer");
    }
}
