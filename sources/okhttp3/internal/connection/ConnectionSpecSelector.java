package okhttp3.internal.connection;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ProtocolException;
import java.security.cert.CertificateException;
import java.util.List;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLProtocolException;
import javax.net.ssl.SSLSocket;
import okhttp3.ConnectionSpec;

public final class ConnectionSpecSelector {
    private final List<ConnectionSpec> connectionSpecs;
    private boolean isFallback;
    private boolean isFallbackPossible;
    private int nextModeIndex = 0;

    public okhttp3.ConnectionSpec configureSecureSocket(javax.net.ssl.SSLSocket r6) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:11:0x0066 in {4, 5, 8, 10} preds:[]
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
        r0 = 0;
        r1 = r5.nextModeIndex;
        r2 = r5.connectionSpecs;
        r2 = r2.size();
    L_0x0009:
        if (r1 >= r2) goto L_0x0023;
    L_0x000b:
        r3 = r5.connectionSpecs;
        r3 = r3.get(r1);
        r3 = (okhttp3.ConnectionSpec) r3;
        r4 = r3.isCompatible(r6);
        if (r4 == 0) goto L_0x001f;
    L_0x0019:
        r0 = r3;
        r4 = r1 + 1;
        r5.nextModeIndex = r4;
        goto L_0x0023;
        r1 = r1 + 1;
        goto L_0x0009;
    L_0x0023:
        if (r0 == 0) goto L_0x0033;
    L_0x0025:
        r1 = r5.isFallbackPossible(r6);
        r5.isFallbackPossible = r1;
        r1 = okhttp3.internal.Internal.instance;
        r2 = r5.isFallback;
        r1.apply(r0, r6, r2);
        return r0;
    L_0x0033:
        r1 = new java.net.UnknownServiceException;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "Unable to find acceptable protocols. isFallback=";
        r2.append(r3);
        r3 = r5.isFallback;
        r2.append(r3);
        r3 = ", modes=";
        r2.append(r3);
        r3 = r5.connectionSpecs;
        r2.append(r3);
        r3 = ", supported protocols=";
        r2.append(r3);
        r3 = r6.getEnabledProtocols();
        r3 = java.util.Arrays.toString(r3);
        r2.append(r3);
        r2 = r2.toString();
        r1.<init>(r2);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.connection.ConnectionSpecSelector.configureSecureSocket(javax.net.ssl.SSLSocket):okhttp3.ConnectionSpec");
    }

    public ConnectionSpecSelector(List<ConnectionSpec> connectionSpecs) {
        this.connectionSpecs = connectionSpecs;
    }

    public boolean connectionFailed(IOException e) {
        boolean z = true;
        this.isFallback = true;
        if (!this.isFallbackPossible || (e instanceof ProtocolException) || (e instanceof InterruptedIOException)) {
            return false;
        }
        if (e instanceof SSLHandshakeException) {
            if (e.getCause() instanceof CertificateException) {
                return false;
            }
        }
        if (e instanceof SSLPeerUnverifiedException) {
            return false;
        }
        if (!(e instanceof SSLHandshakeException)) {
            if (!(e instanceof SSLProtocolException)) {
                z = false;
            }
        }
        return z;
    }

    private boolean isFallbackPossible(SSLSocket socket) {
        for (int i = this.nextModeIndex; i < this.connectionSpecs.size(); i++) {
            if (((ConnectionSpec) this.connectionSpecs.get(i)).isCompatible(socket)) {
                return true;
            }
        }
        return false;
    }
}
