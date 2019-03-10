package okhttp3;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import javax.net.ssl.SSLPeerUnverifiedException;
import okhttp3.internal.Util;
import okhttp3.internal.tls.CertificateChainCleaner;
import okio.ByteString;

public final class CertificatePinner {
    public static final CertificatePinner DEFAULT = new CertificatePinner$Builder().build();
    @Nullable
    private final CertificateChainCleaner certificateChainCleaner;
    private final Set<CertificatePinner$Pin> pins;

    public void check(java.lang.String r13, java.util.List<java.security.cert.Certificate> r14) throws javax.net.ssl.SSLPeerUnverifiedException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:38:0x00f1 in {2, 5, 6, 14, 17, 18, 22, 25, 26, 28, 29, 32, 35, 37} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r12 = this;
        r0 = r12.findMatchingPins(r13);
        r1 = r0.isEmpty();
        if (r1 == 0) goto L_0x000b;
    L_0x000a:
        return;
    L_0x000b:
        r1 = r12.certificateChainCleaner;
        if (r1 == 0) goto L_0x0014;
    L_0x000f:
        r14 = r1.clean(r14, r13);
        goto L_0x0015;
    L_0x0015:
        r1 = 0;
        r2 = r14.size();
    L_0x001a:
        if (r1 >= r2) goto L_0x0086;
    L_0x001c:
        r3 = r14.get(r1);
        r3 = (java.security.cert.X509Certificate) r3;
        r4 = 0;
        r5 = 0;
        r6 = 0;
        r7 = r0.size();
    L_0x0029:
        if (r6 >= r7) goto L_0x0082;
    L_0x002b:
        r8 = r0.get(r6);
        r8 = (okhttp3.CertificatePinner$Pin) r8;
        r9 = r8.hashAlgorithm;
        r10 = "sha256/";
        r9 = r9.equals(r10);
        if (r9 == 0) goto L_0x004c;
    L_0x003b:
        if (r5 != 0) goto L_0x0042;
    L_0x003d:
        r5 = sha256(r3);
    L_0x0042:
        r9 = r8.hash;
        r9 = r9.equals(r5);
        if (r9 == 0) goto L_0x004b;
    L_0x004a:
        return;
    L_0x004b:
        goto L_0x0066;
    L_0x004c:
        r9 = r8.hashAlgorithm;
        r10 = "sha1/";
        r9 = r9.equals(r10);
        if (r9 == 0) goto L_0x0069;
    L_0x0056:
        if (r4 != 0) goto L_0x005d;
    L_0x0058:
        r4 = sha1(r3);
    L_0x005d:
        r9 = r8.hash;
        r9 = r9.equals(r4);
        if (r9 == 0) goto L_0x0066;
    L_0x0065:
        return;
    L_0x0066:
        r6 = r6 + 1;
        goto L_0x0029;
    L_0x0069:
        r9 = new java.lang.AssertionError;
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r11 = "unsupported hashAlgorithm: ";
        r10.append(r11);
        r11 = r8.hashAlgorithm;
        r10.append(r11);
        r10 = r10.toString();
        r9.<init>(r10);
        throw r9;
        r1 = r1 + 1;
        goto L_0x001a;
    L_0x0086:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Certificate pinning failure!";
        r1.append(r2);
        r2 = "\n  Peer certificate chain:";
        r1 = r1.append(r2);
        r2 = 0;
        r3 = r14.size();
    L_0x009b:
        if (r2 >= r3) goto L_0x00c2;
    L_0x009d:
        r4 = r14.get(r2);
        r4 = (java.security.cert.X509Certificate) r4;
        r5 = "\n    ";
        r1.append(r5);
        r5 = pin(r4);
        r1.append(r5);
        r5 = ": ";
        r1.append(r5);
        r5 = r4.getSubjectDN();
        r5 = r5.getName();
        r1.append(r5);
        r2 = r2 + 1;
        goto L_0x009b;
    L_0x00c2:
        r2 = "\n  Pinned certificates for ";
        r1.append(r2);
        r1.append(r13);
        r2 = ":";
        r1.append(r2);
        r2 = 0;
        r3 = r0.size();
    L_0x00d4:
        if (r2 >= r3) goto L_0x00e7;
    L_0x00d6:
        r4 = r0.get(r2);
        r4 = (okhttp3.CertificatePinner$Pin) r4;
        r5 = "\n    ";
        r1.append(r5);
        r1.append(r4);
        r2 = r2 + 1;
        goto L_0x00d4;
    L_0x00e7:
        r2 = new javax.net.ssl.SSLPeerUnverifiedException;
        r3 = r1.toString();
        r2.<init>(r3);
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.CertificatePinner.check(java.lang.String, java.util.List):void");
    }

    CertificatePinner(Set<CertificatePinner$Pin> pins, @Nullable CertificateChainCleaner certificateChainCleaner) {
        this.pins = pins;
        this.certificateChainCleaner = certificateChainCleaner;
    }

    public boolean equals(@Nullable Object other) {
        boolean z = true;
        if (other == this) {
            return true;
        }
        if (other instanceof CertificatePinner) {
            if (Util.equal(this.certificateChainCleaner, ((CertificatePinner) other).certificateChainCleaner)) {
                if (this.pins.equals(((CertificatePinner) other).pins)) {
                    return z;
                }
            }
        }
        z = false;
        return z;
    }

    public int hashCode() {
        CertificateChainCleaner certificateChainCleaner = this.certificateChainCleaner;
        return ((certificateChainCleaner != null ? certificateChainCleaner.hashCode() : 0) * 31) + this.pins.hashCode();
    }

    public void check(String hostname, Certificate... peerCertificates) throws SSLPeerUnverifiedException {
        check(hostname, Arrays.asList(peerCertificates));
    }

    List<CertificatePinner$Pin> findMatchingPins(String hostname) {
        List<CertificatePinner$Pin> result = Collections.emptyList();
        for (CertificatePinner$Pin pin : this.pins) {
            if (pin.matches(hostname)) {
                if (result.isEmpty()) {
                    result = new ArrayList();
                }
                result.add(pin);
            }
        }
        return result;
    }

    CertificatePinner withCertificateChainCleaner(@Nullable CertificateChainCleaner certificateChainCleaner) {
        if (Util.equal(this.certificateChainCleaner, certificateChainCleaner)) {
            return this;
        }
        return new CertificatePinner(this.pins, certificateChainCleaner);
    }

    public static String pin(Certificate certificate) {
        if (certificate instanceof X509Certificate) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("sha256/");
            stringBuilder.append(sha256((X509Certificate) certificate).base64());
            return stringBuilder.toString();
        }
        throw new IllegalArgumentException("Certificate pinning requires X509 certificates");
    }

    static ByteString sha1(X509Certificate x509Certificate) {
        return ByteString.of(x509Certificate.getPublicKey().getEncoded()).sha1();
    }

    static ByteString sha256(X509Certificate x509Certificate) {
        return ByteString.of(x509Certificate.getPublicKey().getEncoded()).sha256();
    }
}
