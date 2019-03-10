package org.apache.commons.text.similarity;

import java.util.Locale;

public class FuzzyScore {
    private final Locale locale;

    public java.lang.Integer fuzzyScore(java.lang.CharSequence r11, java.lang.CharSequence r12) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:23:0x005a in {13, 14, 15, 16, 17, 18, 20, 22} preds:[]
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
        r10 = this;
        if (r11 == 0) goto L_0x0051;
    L_0x0002:
        if (r12 == 0) goto L_0x0051;
    L_0x0004:
        r0 = r11.toString();
        r1 = r10.locale;
        r0 = r0.toLowerCase(r1);
        r1 = r12.toString();
        r2 = r10.locale;
        r1 = r1.toLowerCase(r2);
        r2 = 0;
        r3 = 0;
        r4 = -2147483648; // 0xffffffff80000000 float:-0.0 double:NaN;
        r5 = 0;
    L_0x001d:
        r6 = r1.length();
        if (r5 >= r6) goto L_0x004c;
    L_0x0023:
        r6 = r1.charAt(r5);
        r7 = 0;
        r8 = r0.length();
        if (r3 >= r8) goto L_0x0048;
    L_0x002f:
        if (r7 != 0) goto L_0x0048;
    L_0x0031:
        r8 = r0.charAt(r3);
        if (r6 != r8) goto L_0x0044;
    L_0x0037:
        r2 = r2 + 1;
        r9 = r4 + 1;
        if (r9 != r3) goto L_0x0040;
    L_0x003d:
        r2 = r2 + 2;
        goto L_0x0041;
    L_0x0041:
        r4 = r3;
        r7 = 1;
        goto L_0x0045;
    L_0x0045:
        r3 = r3 + 1;
        goto L_0x0028;
        r5 = r5 + 1;
        goto L_0x001d;
    L_0x004c:
        r5 = java.lang.Integer.valueOf(r2);
        return r5;
        r0 = new java.lang.IllegalArgumentException;
        r1 = "Strings must not be null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.text.similarity.FuzzyScore.fuzzyScore(java.lang.CharSequence, java.lang.CharSequence):java.lang.Integer");
    }

    public FuzzyScore(Locale locale) {
        if (locale != null) {
            this.locale = locale;
            return;
        }
        throw new IllegalArgumentException("Locale must not be null");
    }

    public Locale getLocale() {
        return this.locale;
    }
}
