package okhttp3;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public final class CertificatePinner$Builder {
    private final List<CertificatePinner$Pin> pins = new ArrayList();

    public okhttp3.CertificatePinner$Builder add(java.lang.String r6, java.lang.String... r7) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:7:0x001e in {3, 4, 6} preds:[]
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
        r5 = this;
        if (r6 == 0) goto L_0x0016;
    L_0x0002:
        r0 = r7.length;
        r1 = 0;
    L_0x0004:
        if (r1 >= r0) goto L_0x0015;
    L_0x0006:
        r2 = r7[r1];
        r3 = r5.pins;
        r4 = new okhttp3.CertificatePinner$Pin;
        r4.<init>(r6, r2);
        r3.add(r4);
        r1 = r1 + 1;
        goto L_0x0004;
    L_0x0015:
        return r5;
    L_0x0016:
        r0 = new java.lang.NullPointerException;
        r1 = "pattern == null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.CertificatePinner$Builder.add(java.lang.String, java.lang.String[]):okhttp3.CertificatePinner$Builder");
    }

    public CertificatePinner build() {
        return new CertificatePinner(new LinkedHashSet(this.pins), null);
    }
}
