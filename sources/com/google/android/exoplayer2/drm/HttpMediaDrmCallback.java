package com.google.android.exoplayer2.drm;

import android.annotation.TargetApi;
import android.text.TextUtils;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.drm.ExoMediaDrm.KeyRequest;
import com.google.android.exoplayer2.drm.ExoMediaDrm.ProvisionRequest;
import com.google.android.exoplayer2.upstream.HttpDataSource.Factory;
import com.google.android.exoplayer2.upstream.HttpDataSource.InvalidResponseCodeException;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@TargetApi(18)
public final class HttpMediaDrmCallback implements MediaDrmCallback {
    private static final int MAX_MANUAL_REDIRECTS = 5;
    private final Factory dataSourceFactory;
    private final String defaultLicenseUrl;
    private final boolean forceDefaultLicenseUrl;
    private final Map<String, String> keyRequestProperties;

    private static byte[] executePost(com.google.android.exoplayer2.upstream.HttpDataSource.Factory r15, java.lang.String r16, byte[] r17, java.util.Map<java.lang.String, java.lang.String> r18) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:46:0x008a in {5, 6, 7, 13, 15, 24, 25, 28, 29, 30, 34, 36, 37, 40, 43, 45} preds:[]
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
        r1 = r15.createDataSource();
        if (r18 == 0) goto L_0x002b;
    L_0x0006:
        r0 = r18.entrySet();
        r0 = r0.iterator();
    L_0x000e:
        r2 = r0.hasNext();
        if (r2 == 0) goto L_0x002a;
    L_0x0014:
        r2 = r0.next();
        r2 = (java.util.Map.Entry) r2;
        r3 = r2.getKey();
        r3 = (java.lang.String) r3;
        r4 = r2.getValue();
        r4 = (java.lang.String) r4;
        r1.setRequestProperty(r3, r4);
        goto L_0x000e;
    L_0x002a:
        goto L_0x002c;
    L_0x002c:
        r0 = 0;
        r2 = r16;
        r3 = r0;
    L_0x0030:
        r0 = new com.google.android.exoplayer2.upstream.DataSpec;
        r5 = android.net.Uri.parse(r2);
        r7 = 0;
        r9 = 0;
        r11 = -1;
        r13 = 0;
        r14 = 1;
        r4 = r0;
        r6 = r17;
        r4.<init>(r5, r6, r7, r9, r11, r13, r14);
        r0 = new com.google.android.exoplayer2.upstream.DataSourceInputStream;
        r0.<init>(r1, r4);
        r5 = r0;
        r0 = com.google.android.exoplayer2.util.Util.toByteArray(r5);	 Catch:{ InvalidResponseCodeException -> 0x0054 }
        com.google.android.exoplayer2.util.Util.closeQuietly(r5);
        return r0;
    L_0x0052:
        r0 = move-exception;
        goto L_0x0086;
    L_0x0054:
        r0 = move-exception;
        r6 = r0;
        r0 = r6;
        r6 = r0.responseCode;	 Catch:{ all -> 0x0052 }
        r7 = 307; // 0x133 float:4.3E-43 double:1.517E-321;	 Catch:{ all -> 0x0052 }
        if (r6 == r7) goto L_0x0065;	 Catch:{ all -> 0x0052 }
    L_0x005d:
        r6 = r0.responseCode;	 Catch:{ all -> 0x0052 }
        r7 = 308; // 0x134 float:4.32E-43 double:1.52E-321;
        if (r6 != r7) goto L_0x0064;
    L_0x0063:
        goto L_0x0065;
    L_0x0064:
        goto L_0x006d;
    L_0x0065:
        r6 = r3 + 1;
        r7 = 5;
        if (r3 >= r7) goto L_0x006c;
    L_0x006a:
        r3 = 1;
        goto L_0x0070;
    L_0x006c:
        r3 = r6;
    L_0x006d:
        r6 = 0;
        r6 = r3;
        r3 = 0;
    L_0x0070:
        if (r3 == 0) goto L_0x007a;
    L_0x0072:
        r7 = getRedirectUrl(r0);	 Catch:{ all -> 0x0077 }
        goto L_0x007b;
    L_0x0077:
        r0 = move-exception;
        r3 = r6;
        goto L_0x0086;
    L_0x007a:
        r7 = 0;
    L_0x007b:
        r2 = r7;
        if (r2 == 0) goto L_0x0084;
    L_0x007e:
        com.google.android.exoplayer2.util.Util.closeQuietly(r5);
        r3 = r6;
        goto L_0x0030;
        throw r0;	 Catch:{ all -> 0x0077 }
    L_0x0086:
        com.google.android.exoplayer2.util.Util.closeQuietly(r5);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.drm.HttpMediaDrmCallback.executePost(com.google.android.exoplayer2.upstream.HttpDataSource$Factory, java.lang.String, byte[], java.util.Map):byte[]");
    }

    public HttpMediaDrmCallback(String defaultLicenseUrl, Factory dataSourceFactory) {
        this(defaultLicenseUrl, false, dataSourceFactory);
    }

    public HttpMediaDrmCallback(String defaultLicenseUrl, boolean forceDefaultLicenseUrl, Factory dataSourceFactory) {
        this.dataSourceFactory = dataSourceFactory;
        this.defaultLicenseUrl = defaultLicenseUrl;
        this.forceDefaultLicenseUrl = forceDefaultLicenseUrl;
        this.keyRequestProperties = new HashMap();
    }

    public void setKeyRequestProperty(String name, String value) {
        Assertions.checkNotNull(name);
        Assertions.checkNotNull(value);
        synchronized (this.keyRequestProperties) {
            this.keyRequestProperties.put(name, value);
        }
    }

    public void clearKeyRequestProperty(String name) {
        Assertions.checkNotNull(name);
        synchronized (this.keyRequestProperties) {
            this.keyRequestProperties.remove(name);
        }
    }

    public void clearAllKeyRequestProperties() {
        synchronized (this.keyRequestProperties) {
            this.keyRequestProperties.clear();
        }
    }

    public byte[] executeProvisionRequest(UUID uuid, ProvisionRequest request) throws IOException {
        String url = new StringBuilder();
        url.append(request.getDefaultUrl());
        url.append("&signedRequest=");
        url.append(Util.fromUtf8Bytes(request.getData()));
        return executePost(this.dataSourceFactory, url.toString(), Util.EMPTY_BYTE_ARRAY, null);
    }

    public byte[] executeKeyRequest(UUID uuid, KeyRequest request) throws Exception {
        Map<String, String> requestProperties;
        String contentType;
        String url = request.getLicenseServerUrl();
        if (!this.forceDefaultLicenseUrl) {
            if (!TextUtils.isEmpty(url)) {
                requestProperties = new HashMap();
                contentType = C0555C.PLAYREADY_UUID.equals(uuid) ? "text/xml" : C0555C.CLEARKEY_UUID.equals(uuid) ? "application/json" : "application/octet-stream";
                requestProperties.put("Content-Type", contentType);
                if (C0555C.PLAYREADY_UUID.equals(uuid)) {
                    requestProperties.put("SOAPAction", "http://schemas.microsoft.com/DRM/2007/03/protocols/AcquireLicense");
                }
                synchronized (this.keyRequestProperties) {
                    requestProperties.putAll(this.keyRequestProperties);
                }
                return executePost(this.dataSourceFactory, url, request.getData(), requestProperties);
            }
        }
        url = this.defaultLicenseUrl;
        requestProperties = new HashMap();
        if (C0555C.PLAYREADY_UUID.equals(uuid)) {
        }
        requestProperties.put("Content-Type", contentType);
        if (C0555C.PLAYREADY_UUID.equals(uuid)) {
            requestProperties.put("SOAPAction", "http://schemas.microsoft.com/DRM/2007/03/protocols/AcquireLicense");
        }
        synchronized (this.keyRequestProperties) {
            requestProperties.putAll(this.keyRequestProperties);
        }
        return executePost(this.dataSourceFactory, url, request.getData(), requestProperties);
    }

    private static String getRedirectUrl(InvalidResponseCodeException exception) {
        Map<String, List<String>> headerFields = exception.headerFields;
        if (headerFields != null) {
            List<String> locationHeaders = (List) headerFields.get("Location");
            if (locationHeaders != null && !locationHeaders.isEmpty()) {
                return (String) locationHeaders.get(0);
            }
        }
        return null;
    }
}
