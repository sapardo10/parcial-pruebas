package okhttp3.internal.tls;

import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;

public final class BasicCertificateChainCleaner extends CertificateChainCleaner {
    private static final int MAX_SIGNERS = 9;
    private final TrustRootIndex trustRootIndex;

    public java.util.List<java.security.cert.Certificate> clean(java.util.List<java.security.cert.Certificate> r10, java.lang.String r11) throws javax.net.ssl.SSLPeerUnverifiedException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:31:0x009a in {9, 10, 11, 14, 15, 21, 22, 23, 26, 28, 30} preds:[]
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
        r9 = this;
        r0 = new java.util.ArrayDeque;
        r0.<init>(r10);
        r1 = new java.util.ArrayList;
        r1.<init>();
        r2 = r0.removeFirst();
        r1.add(r2);
        r2 = 0;
        r3 = 0;
    L_0x0013:
        r4 = 9;
        if (r3 >= r4) goto L_0x0082;
    L_0x0017:
        r4 = r1.size();
        r5 = 1;
        r4 = r4 - r5;
        r4 = r1.get(r4);
        r4 = (java.security.cert.X509Certificate) r4;
        r6 = r9.trustRootIndex;
        r6 = r6.findByIssuerAndSignature(r4);
        if (r6 == 0) goto L_0x0045;
    L_0x002b:
        r7 = r1.size();
        if (r7 > r5) goto L_0x0039;
    L_0x0031:
        r5 = r4.equals(r6);
        if (r5 != 0) goto L_0x0038;
    L_0x0037:
        goto L_0x0039;
    L_0x0038:
        goto L_0x003c;
    L_0x0039:
        r1.add(r6);
    L_0x003c:
        r5 = r9.verifySignature(r6, r6);
        if (r5 == 0) goto L_0x0043;
    L_0x0042:
        return r1;
    L_0x0043:
        r2 = 1;
        goto L_0x0062;
    L_0x0045:
        r5 = r0.iterator();
    L_0x0049:
        r7 = r5.hasNext();
        if (r7 == 0) goto L_0x0067;
    L_0x004f:
        r7 = r5.next();
        r7 = (java.security.cert.X509Certificate) r7;
        r8 = r9.verifySignature(r4, r7);
        if (r8 == 0) goto L_0x0065;
    L_0x005b:
        r5.remove();
        r1.add(r7);
    L_0x0062:
        r3 = r3 + 1;
        goto L_0x0013;
        goto L_0x0049;
        if (r2 == 0) goto L_0x006b;
    L_0x006a:
        return r1;
    L_0x006b:
        r5 = new javax.net.ssl.SSLPeerUnverifiedException;
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r8 = "Failed to find a trusted cert that signed ";
        r7.append(r8);
        r7.append(r4);
        r7 = r7.toString();
        r5.<init>(r7);
        throw r5;
        r3 = new javax.net.ssl.SSLPeerUnverifiedException;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "Certificate chain too long: ";
        r4.append(r5);
        r4.append(r1);
        r4 = r4.toString();
        r3.<init>(r4);
        throw r3;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.tls.BasicCertificateChainCleaner.clean(java.util.List, java.lang.String):java.util.List<java.security.cert.Certificate>");
    }

    public BasicCertificateChainCleaner(TrustRootIndex trustRootIndex) {
        this.trustRootIndex = trustRootIndex;
    }

    private boolean verifySignature(X509Certificate toVerify, X509Certificate signingCert) {
        if (!toVerify.getIssuerDN().equals(signingCert.getSubjectDN())) {
            return false;
        }
        try {
            toVerify.verify(signingCert.getPublicKey());
            return true;
        } catch (GeneralSecurityException e) {
            return false;
        }
    }

    public int hashCode() {
        return this.trustRootIndex.hashCode();
    }

    public boolean equals(Object other) {
        boolean z = true;
        if (other == this) {
            return true;
        }
        if (other instanceof BasicCertificateChainCleaner) {
            if (((BasicCertificateChainCleaner) other).trustRootIndex.equals(this.trustRootIndex)) {
                return z;
            }
        }
        z = false;
        return z;
    }
}
