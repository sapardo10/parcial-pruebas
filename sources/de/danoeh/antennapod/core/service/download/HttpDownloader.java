package de.danoeh.antennapod.core.service.download;

import android.text.TextUtils;
import android.util.Log;
import de.danoeh.antennapod.core.util.DownloadError;
import de.danoeh.antennapod.core.util.URIUtil;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import okhttp3.Interceptor;
import okhttp3.Interceptor$Chain;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;
import okio.ByteString;
import org.apache.commons.lang3.CharEncoding;

public class HttpDownloader extends Downloader {
    private static final int BUFFER_SIZE = 8192;
    private static final String TAG = "HttpDownloader";

    private static class BasicAuthorizationInterceptor implements Interceptor {
        private final DownloadRequest downloadRequest;

        public BasicAuthorizationInterceptor(DownloadRequest downloadRequest) {
            this.downloadRequest = downloadRequest;
        }

        public Response intercept(Interceptor$Chain chain) throws IOException {
            Request request = chain.request();
            String userInfo = URIUtil.getURIFromRequestUrl(this.downloadRequest.getSource()).getUserInfo();
            Response response = chain.proceed(request);
            if (response.code() != 401) {
                return response;
            }
            String[] parts;
            Builder newRequest = request.newBuilder();
            Log.d(HttpDownloader.TAG, "Authorization failed, re-trying with ISO-8859-1 encoded credentials");
            if (userInfo != null) {
                String[] parts2 = userInfo.split(":");
                if (parts2.length == 2) {
                    newRequest.header("Authorization", HttpDownloader.encodeCredentials(parts2[0], parts2[1], CharEncoding.ISO_8859_1));
                }
            } else if (!(TextUtils.isEmpty(this.downloadRequest.getUsername()) || this.downloadRequest.getPassword() == null)) {
                newRequest.header("Authorization", HttpDownloader.encodeCredentials(this.downloadRequest.getUsername(), this.downloadRequest.getPassword(), CharEncoding.ISO_8859_1));
                response = chain.proceed(newRequest.build());
                if (response.code() != 401) {
                    return response;
                }
                Log.d(HttpDownloader.TAG, "Authorization failed, re-trying with UTF-8 encoded credentials");
                if (userInfo == null) {
                    parts = userInfo.split(":");
                    if (parts.length == 2) {
                        newRequest.header("Authorization", HttpDownloader.encodeCredentials(parts[0], parts[1], "UTF-8"));
                    }
                } else if (!(TextUtils.isEmpty(this.downloadRequest.getUsername()) || this.downloadRequest.getPassword() == null)) {
                    newRequest.header("Authorization", HttpDownloader.encodeCredentials(this.downloadRequest.getUsername(), this.downloadRequest.getPassword(), "UTF-8"));
                    return chain.proceed(newRequest.build());
                }
                return chain.proceed(newRequest.build());
            }
            response = chain.proceed(newRequest.build());
            if (response.code() != 401) {
                return response;
            }
            Log.d(HttpDownloader.TAG, "Authorization failed, re-trying with UTF-8 encoded credentials");
            if (userInfo == null) {
                newRequest.header("Authorization", HttpDownloader.encodeCredentials(this.downloadRequest.getUsername(), this.downloadRequest.getPassword(), "UTF-8"));
                return chain.proceed(newRequest.build());
            }
            parts = userInfo.split(":");
            if (parts.length == 2) {
                newRequest.header("Authorization", HttpDownloader.encodeCredentials(parts[0], parts[1], "UTF-8"));
            }
            return chain.proceed(newRequest.build());
        }
    }

    protected void download() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:354:0x072f in {4, 12, 14, 16, 18, 20, 22, 24, 25, 37, 38, 39, 40, 42, 44, 46, 48, 50, 52, 53, 59, 61, 62, 65, 67, 69, 71, 73, 75, 82, 88, 89, 98, 105, 112, 123, 125, 126, 139, 140, 142, 144, 146, 148, 150, 152, 153, 158, 160, 162, 164, 166, 168, 170, 171, 179, 180, 181, 183, 189, 190, 201, 220, 222, 223, 225, 227, 230, 238, 239, 240, 248, 253, 254, 255, 256, 258, 260, 262, 264, 266, 268, 269, 273, 276, 277, 280, 282, 284, 286, 288, 290, 292, 294, 296, 298, 300, 302, 304, 307, 309, 311, 313, 315, 317, 319, 321, 323, 325, 327, 329, 331, 333, 335, 337, 338, 340, 341, 343, 344, 346, 347, 348, 349, 351, 353} preds:[]
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
        r29 = this;
        r1 = r29;
        r2 = new java.io.File;
        r3 = r1.request;
        r3 = r3.getDestination();
        r2.<init>(r3);
        r3 = r2.exists();
        r4 = r1.request;
        r4 = r4.isDeleteOnFailure();
        if (r4 == 0) goto L_0x0026;
    L_0x0019:
        if (r3 == 0) goto L_0x0026;
    L_0x001b:
        r4 = "HttpDownloader";
        r5 = "File already exists";
        android.util.Log.w(r4, r5);
        r29.onSuccess();
        return;
        r4 = de.danoeh.antennapod.core.service.download.AntennapodHttpClient.newBuilder();
        r5 = r4.interceptors();
        r6 = new de.danoeh.antennapod.core.service.download.HttpDownloader$BasicAuthorizationInterceptor;
        r7 = r1.request;
        r6.<init>(r7);
        r5.add(r6);
        r5 = r4.build();
        r6 = 0;
        r7 = 0;
        r8 = r1.request;	 Catch:{ IllegalArgumentException -> 0x0704, SocketTimeoutException -> 0x06f1, UnknownHostException -> 0x06de, IOException -> 0x06cb, NullPointerException -> 0x06b6, all -> 0x06ac }
        r8 = r8.getSource();	 Catch:{ IllegalArgumentException -> 0x0704, SocketTimeoutException -> 0x06f1, UnknownHostException -> 0x06de, IOException -> 0x06cb, NullPointerException -> 0x06b6, all -> 0x06ac }
        r8 = de.danoeh.antennapod.core.util.URIUtil.getURIFromRequestUrl(r8);	 Catch:{ IllegalArgumentException -> 0x0704, SocketTimeoutException -> 0x06f1, UnknownHostException -> 0x06de, IOException -> 0x06cb, NullPointerException -> 0x06b6, all -> 0x06ac }
        r9 = new okhttp3.Request$Builder;	 Catch:{ IllegalArgumentException -> 0x0704, SocketTimeoutException -> 0x06f1, UnknownHostException -> 0x06de, IOException -> 0x06cb, NullPointerException -> 0x06b6, all -> 0x06ac }
        r9.<init>();	 Catch:{ IllegalArgumentException -> 0x0704, SocketTimeoutException -> 0x06f1, UnknownHostException -> 0x06de, IOException -> 0x06cb, NullPointerException -> 0x06b6, all -> 0x06ac }
        r10 = r8.toURL();	 Catch:{ IllegalArgumentException -> 0x0704, SocketTimeoutException -> 0x06f1, UnknownHostException -> 0x06de, IOException -> 0x06cb, NullPointerException -> 0x06b6, all -> 0x06ac }
        r9 = r9.url(r10);	 Catch:{ IllegalArgumentException -> 0x0704, SocketTimeoutException -> 0x06f1, UnknownHostException -> 0x06de, IOException -> 0x06cb, NullPointerException -> 0x06b6, all -> 0x06ac }
        r10 = "User-Agent";	 Catch:{ IllegalArgumentException -> 0x0704, SocketTimeoutException -> 0x06f1, UnknownHostException -> 0x06de, IOException -> 0x06cb, NullPointerException -> 0x06b6, all -> 0x06ac }
        r11 = de.danoeh.antennapod.core.ClientConfig.USER_AGENT;	 Catch:{ IllegalArgumentException -> 0x0704, SocketTimeoutException -> 0x06f1, UnknownHostException -> 0x06de, IOException -> 0x06cb, NullPointerException -> 0x06b6, all -> 0x06ac }
        r9 = r9.header(r10, r11);	 Catch:{ IllegalArgumentException -> 0x0704, SocketTimeoutException -> 0x06f1, UnknownHostException -> 0x06de, IOException -> 0x06cb, NullPointerException -> 0x06b6, all -> 0x06ac }
        r10 = r1.request;	 Catch:{ IllegalArgumentException -> 0x0704, SocketTimeoutException -> 0x06f1, UnknownHostException -> 0x06de, IOException -> 0x06cb, NullPointerException -> 0x06b6, all -> 0x06ac }
        r10 = r10.getFeedfileType();	 Catch:{ IllegalArgumentException -> 0x0704, SocketTimeoutException -> 0x06f1, UnknownHostException -> 0x06de, IOException -> 0x06cb, NullPointerException -> 0x06b6, all -> 0x06ac }
        r11 = 2;
        if (r10 != r11) goto L_0x00a8;
    L_0x0067:
        r10 = "HttpDownloader";	 Catch:{ IllegalArgumentException -> 0x00a0, SocketTimeoutException -> 0x0098, UnknownHostException -> 0x0090, IOException -> 0x0088, NullPointerException -> 0x0080, all -> 0x0076 }
        r12 = "addHeader(\"Accept-Encoding\", \"identity\")";	 Catch:{ IllegalArgumentException -> 0x00a0, SocketTimeoutException -> 0x0098, UnknownHostException -> 0x0090, IOException -> 0x0088, NullPointerException -> 0x0080, all -> 0x0076 }
        android.util.Log.d(r10, r12);	 Catch:{ IllegalArgumentException -> 0x00a0, SocketTimeoutException -> 0x0098, UnknownHostException -> 0x0090, IOException -> 0x0088, NullPointerException -> 0x0080, all -> 0x0076 }
        r10 = "Accept-Encoding";	 Catch:{ IllegalArgumentException -> 0x00a0, SocketTimeoutException -> 0x0098, UnknownHostException -> 0x0090, IOException -> 0x0088, NullPointerException -> 0x0080, all -> 0x0076 }
        r12 = "identity";	 Catch:{ IllegalArgumentException -> 0x00a0, SocketTimeoutException -> 0x0098, UnknownHostException -> 0x0090, IOException -> 0x0088, NullPointerException -> 0x0080, all -> 0x0076 }
        r9.addHeader(r10, r12);	 Catch:{ IllegalArgumentException -> 0x00a0, SocketTimeoutException -> 0x0098, UnknownHostException -> 0x0090, IOException -> 0x0088, NullPointerException -> 0x0080, all -> 0x0076 }
        goto L_0x00a9;
    L_0x0076:
        r0 = move-exception;
        r26 = r2;
        r17 = r4;
        r20 = r5;
        r2 = r0;
        goto L_0x0725;
    L_0x0080:
        r0 = move-exception;
        r26 = r2;
        r17 = r4;
        r2 = r0;
        goto L_0x06bc;
    L_0x0088:
        r0 = move-exception;
        r26 = r2;
        r17 = r4;
        r2 = r0;
        goto L_0x06d1;
    L_0x0090:
        r0 = move-exception;
        r26 = r2;
        r17 = r4;
        r2 = r0;
        goto L_0x06e4;
    L_0x0098:
        r0 = move-exception;
        r26 = r2;
        r17 = r4;
        r2 = r0;
        goto L_0x06f7;
    L_0x00a0:
        r0 = move-exception;
        r26 = r2;
        r17 = r4;
        r2 = r0;
        goto L_0x070a;
    L_0x00a9:
        r10 = r1.request;	 Catch:{ IllegalArgumentException -> 0x0704, SocketTimeoutException -> 0x06f1, UnknownHostException -> 0x06de, IOException -> 0x06cb, NullPointerException -> 0x06b6, all -> 0x06ac }
        r10 = r10.getLastModified();	 Catch:{ IllegalArgumentException -> 0x0704, SocketTimeoutException -> 0x06f1, UnknownHostException -> 0x06de, IOException -> 0x06cb, NullPointerException -> 0x06b6, all -> 0x06ac }
        r10 = android.text.TextUtils.isEmpty(r10);	 Catch:{ IllegalArgumentException -> 0x0704, SocketTimeoutException -> 0x06f1, UnknownHostException -> 0x06de, IOException -> 0x06cb, NullPointerException -> 0x06b6, all -> 0x06ac }
        if (r10 != 0) goto L_0x014c;
    L_0x00b5:
        r10 = r1.request;	 Catch:{ IllegalArgumentException -> 0x0144, SocketTimeoutException -> 0x013c, UnknownHostException -> 0x0134, IOException -> 0x012c, NullPointerException -> 0x0124, all -> 0x011a }
        r10 = r10.getLastModified();	 Catch:{ IllegalArgumentException -> 0x0144, SocketTimeoutException -> 0x013c, UnknownHostException -> 0x0134, IOException -> 0x012c, NullPointerException -> 0x0124, all -> 0x011a }
        r12 = de.danoeh.antennapod.core.util.DateUtils.parse(r10);	 Catch:{ IllegalArgumentException -> 0x0144, SocketTimeoutException -> 0x013c, UnknownHostException -> 0x0134, IOException -> 0x012c, NullPointerException -> 0x0124, all -> 0x011a }
        if (r12 == 0) goto L_0x00f7;	 Catch:{ IllegalArgumentException -> 0x0144, SocketTimeoutException -> 0x013c, UnknownHostException -> 0x0134, IOException -> 0x012c, NullPointerException -> 0x0124, all -> 0x011a }
    L_0x00c1:
        r13 = java.lang.System.currentTimeMillis();	 Catch:{ IllegalArgumentException -> 0x0144, SocketTimeoutException -> 0x013c, UnknownHostException -> 0x0134, IOException -> 0x012c, NullPointerException -> 0x0124, all -> 0x011a }
        r15 = 259200000; // 0xf731400 float:1.1984677E-29 double:1.280618154E-315;	 Catch:{ IllegalArgumentException -> 0x0144, SocketTimeoutException -> 0x013c, UnknownHostException -> 0x0134, IOException -> 0x012c, NullPointerException -> 0x0124, all -> 0x011a }
        r13 = r13 - r15;	 Catch:{ IllegalArgumentException -> 0x0144, SocketTimeoutException -> 0x013c, UnknownHostException -> 0x0134, IOException -> 0x012c, NullPointerException -> 0x0124, all -> 0x011a }
        r15 = r12.getTime();	 Catch:{ IllegalArgumentException -> 0x0144, SocketTimeoutException -> 0x013c, UnknownHostException -> 0x0134, IOException -> 0x012c, NullPointerException -> 0x0124, all -> 0x011a }
        r17 = (r15 > r13 ? 1 : (r15 == r13 ? 0 : -1));	 Catch:{ IllegalArgumentException -> 0x0144, SocketTimeoutException -> 0x013c, UnknownHostException -> 0x0134, IOException -> 0x012c, NullPointerException -> 0x0124, all -> 0x011a }
        if (r17 <= 0) goto L_0x00f4;	 Catch:{ IllegalArgumentException -> 0x0144, SocketTimeoutException -> 0x013c, UnknownHostException -> 0x0134, IOException -> 0x012c, NullPointerException -> 0x0124, all -> 0x011a }
    L_0x00d1:
        r15 = "HttpDownloader";	 Catch:{ IllegalArgumentException -> 0x0144, SocketTimeoutException -> 0x013c, UnknownHostException -> 0x0134, IOException -> 0x012c, NullPointerException -> 0x0124, all -> 0x011a }
        r11 = new java.lang.StringBuilder;	 Catch:{ IllegalArgumentException -> 0x0144, SocketTimeoutException -> 0x013c, UnknownHostException -> 0x0134, IOException -> 0x012c, NullPointerException -> 0x0124, all -> 0x011a }
        r11.<init>();	 Catch:{ IllegalArgumentException -> 0x0144, SocketTimeoutException -> 0x013c, UnknownHostException -> 0x0134, IOException -> 0x012c, NullPointerException -> 0x0124, all -> 0x011a }
        r17 = r4;
        r4 = "addHeader(\"If-Modified-Since\", \"";	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r11.append(r4);	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r11.append(r10);	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r4 = "\")";	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r11.append(r4);	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r4 = r11.toString();	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        android.util.Log.d(r15, r4);	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r4 = "If-Modified-Since";	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r9.addHeader(r4, r10);	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        goto L_0x00f6;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
    L_0x00f4:
        r17 = r4;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
    L_0x00f6:
        goto L_0x014e;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
    L_0x00f7:
        r17 = r4;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r4 = "HttpDownloader";	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r11 = new java.lang.StringBuilder;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r11.<init>();	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r13 = "addHeader(\"If-None-Match\", \"";	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r11.append(r13);	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r11.append(r10);	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r13 = "\")";	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r11.append(r13);	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r11 = r11.toString();	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        android.util.Log.d(r4, r11);	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r4 = "If-None-Match";	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r9.addHeader(r4, r10);	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        goto L_0x014e;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
    L_0x011a:
        r0 = move-exception;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r17 = r4;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r26 = r2;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r20 = r5;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r2 = r0;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        goto L_0x0725;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
    L_0x0124:
        r0 = move-exception;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r17 = r4;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r26 = r2;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r2 = r0;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        goto L_0x06bc;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
    L_0x012c:
        r0 = move-exception;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r17 = r4;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r26 = r2;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r2 = r0;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        goto L_0x06d1;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
    L_0x0134:
        r0 = move-exception;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r17 = r4;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r26 = r2;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r2 = r0;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        goto L_0x06e4;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
    L_0x013c:
        r0 = move-exception;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r17 = r4;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r26 = r2;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r2 = r0;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        goto L_0x06f7;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
    L_0x0144:
        r0 = move-exception;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r17 = r4;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r26 = r2;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r2 = r0;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        goto L_0x070a;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
    L_0x014c:
        r17 = r4;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
    L_0x014e:
        r10 = 0;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        if (r3 == 0) goto L_0x01a7;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
    L_0x0152:
        r12 = r2.length();	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r4 = (r12 > r10 ? 1 : (r12 == r10 ? 0 : -1));	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        if (r4 <= 0) goto L_0x01a7;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
    L_0x015a:
        r4 = r1.request;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r12 = r2.length();	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r4.setSoFar(r12);	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r4 = "Range";	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r12 = new java.lang.StringBuilder;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r12.<init>();	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r13 = "bytes=";	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r12.append(r13);	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r13 = r1.request;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r13 = r13.getSoFar();	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r12.append(r13);	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r13 = "-";	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r12.append(r13);	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r12 = r12.toString();	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r9.addHeader(r4, r12);	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r4 = "HttpDownloader";	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r12 = new java.lang.StringBuilder;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r12.<init>();	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r13 = "Adding range header: ";	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r12.append(r13);	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r13 = r1.request;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r13 = r13.getSoFar();	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r12.append(r13);	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r12 = r12.toString();	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        android.util.Log.d(r4, r12);	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        goto L_0x01a8;
    L_0x01a1:
        r0 = move-exception;
        r26 = r2;
        r2 = r0;
        goto L_0x06d1;
    L_0x01a8:
        r4 = r9.build();	 Catch:{ IOException -> 0x01d5, IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r4 = r5.newCall(r4);	 Catch:{ IOException -> 0x01d5, IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r4 = r4.execute();	 Catch:{ IOException -> 0x01d5, IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, NullPointerException -> 0x01bd, all -> 0x01b5 }
        goto L_0x020c;
    L_0x01b5:
        r0 = move-exception;
        r26 = r2;
        r20 = r5;
        r2 = r0;
        goto L_0x0725;
    L_0x01bd:
        r0 = move-exception;
        r26 = r2;
        r2 = r0;
        goto L_0x06bc;
    L_0x01c3:
        r0 = move-exception;
        r26 = r2;
        r2 = r0;
        goto L_0x06e4;
    L_0x01c9:
        r0 = move-exception;
        r26 = r2;
        r2 = r0;
        goto L_0x06f7;
    L_0x01cf:
        r0 = move-exception;
        r26 = r2;
        r2 = r0;
        goto L_0x070a;
    L_0x01d5:
        r0 = move-exception;
        r4 = r0;
        r12 = "HttpDownloader";	 Catch:{ IllegalArgumentException -> 0x06a6, SocketTimeoutException -> 0x06a1, UnknownHostException -> 0x069c, IOException -> 0x0697, NullPointerException -> 0x0692, all -> 0x068a }
        r13 = r4.toString();	 Catch:{ IllegalArgumentException -> 0x06a6, SocketTimeoutException -> 0x06a1, UnknownHostException -> 0x069c, IOException -> 0x0697, NullPointerException -> 0x0692, all -> 0x068a }
        android.util.Log.e(r12, r13);	 Catch:{ IllegalArgumentException -> 0x06a6, SocketTimeoutException -> 0x06a1, UnknownHostException -> 0x069c, IOException -> 0x0697, NullPointerException -> 0x0692, all -> 0x068a }
        r12 = r4.getMessage();	 Catch:{ IllegalArgumentException -> 0x06a6, SocketTimeoutException -> 0x06a1, UnknownHostException -> 0x069c, IOException -> 0x0697, NullPointerException -> 0x0692, all -> 0x068a }
        r13 = "PROTOCOL_ERROR";	 Catch:{ IllegalArgumentException -> 0x06a6, SocketTimeoutException -> 0x06a1, UnknownHostException -> 0x069c, IOException -> 0x0697, NullPointerException -> 0x0692, all -> 0x068a }
        r12 = r12.contains(r13);	 Catch:{ IllegalArgumentException -> 0x06a6, SocketTimeoutException -> 0x06a1, UnknownHostException -> 0x069c, IOException -> 0x0697, NullPointerException -> 0x0692, all -> 0x068a }
        if (r12 == 0) goto L_0x0670;	 Catch:{ IllegalArgumentException -> 0x06a6, SocketTimeoutException -> 0x06a1, UnknownHostException -> 0x069c, IOException -> 0x0697, NullPointerException -> 0x0692, all -> 0x068a }
    L_0x01ec:
        r12 = r5.newBuilder();	 Catch:{ IllegalArgumentException -> 0x06a6, SocketTimeoutException -> 0x06a1, UnknownHostException -> 0x069c, IOException -> 0x0697, NullPointerException -> 0x0692, all -> 0x068a }
        r13 = okhttp3.Protocol.HTTP_1_1;	 Catch:{ IllegalArgumentException -> 0x06a6, SocketTimeoutException -> 0x06a1, UnknownHostException -> 0x069c, IOException -> 0x0697, NullPointerException -> 0x0692, all -> 0x068a }
        r13 = java.util.Collections.singletonList(r13);	 Catch:{ IllegalArgumentException -> 0x06a6, SocketTimeoutException -> 0x06a1, UnknownHostException -> 0x069c, IOException -> 0x0697, NullPointerException -> 0x0692, all -> 0x068a }
        r12 = r12.protocols(r13);	 Catch:{ IllegalArgumentException -> 0x06a6, SocketTimeoutException -> 0x06a1, UnknownHostException -> 0x069c, IOException -> 0x0697, NullPointerException -> 0x0692, all -> 0x068a }
        r12 = r12.build();	 Catch:{ IllegalArgumentException -> 0x06a6, SocketTimeoutException -> 0x06a1, UnknownHostException -> 0x069c, IOException -> 0x0697, NullPointerException -> 0x0692, all -> 0x068a }
        r5 = r12;	 Catch:{ IllegalArgumentException -> 0x06a6, SocketTimeoutException -> 0x06a1, UnknownHostException -> 0x069c, IOException -> 0x0697, NullPointerException -> 0x0692, all -> 0x068a }
        r12 = r9.build();	 Catch:{ IllegalArgumentException -> 0x06a6, SocketTimeoutException -> 0x06a1, UnknownHostException -> 0x069c, IOException -> 0x0697, NullPointerException -> 0x0692, all -> 0x068a }
        r12 = r5.newCall(r12);	 Catch:{ IllegalArgumentException -> 0x06a6, SocketTimeoutException -> 0x06a1, UnknownHostException -> 0x069c, IOException -> 0x0697, NullPointerException -> 0x0692, all -> 0x068a }
        r12 = r12.execute();	 Catch:{ IllegalArgumentException -> 0x06a6, SocketTimeoutException -> 0x06a1, UnknownHostException -> 0x069c, IOException -> 0x0697, NullPointerException -> 0x0692, all -> 0x068a }
        r4 = r12;
    L_0x020c:
        r12 = r4.body();	 Catch:{ IllegalArgumentException -> 0x0668, SocketTimeoutException -> 0x0660, UnknownHostException -> 0x0658, IOException -> 0x0650, NullPointerException -> 0x0648, all -> 0x0640 }
        r7 = r12;	 Catch:{ IllegalArgumentException -> 0x0668, SocketTimeoutException -> 0x0660, UnknownHostException -> 0x0658, IOException -> 0x0650, NullPointerException -> 0x0648, all -> 0x0640 }
        r12 = "Content-Encoding";	 Catch:{ IllegalArgumentException -> 0x0668, SocketTimeoutException -> 0x0660, UnknownHostException -> 0x0658, IOException -> 0x0650, NullPointerException -> 0x0648, all -> 0x0640 }
        r12 = r4.header(r12);	 Catch:{ IllegalArgumentException -> 0x0668, SocketTimeoutException -> 0x0660, UnknownHostException -> 0x0658, IOException -> 0x0650, NullPointerException -> 0x0648, all -> 0x0640 }
        r13 = 0;	 Catch:{ IllegalArgumentException -> 0x0668, SocketTimeoutException -> 0x0660, UnknownHostException -> 0x0658, IOException -> 0x0650, NullPointerException -> 0x0648, all -> 0x0640 }
        r14 = android.text.TextUtils.isEmpty(r12);	 Catch:{ IllegalArgumentException -> 0x0668, SocketTimeoutException -> 0x0660, UnknownHostException -> 0x0658, IOException -> 0x0650, NullPointerException -> 0x0648, all -> 0x0640 }
        if (r14 != 0) goto L_0x022a;
    L_0x021e:
        r14 = r12.toLowerCase();	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r15 = "gzip";	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r14 = android.text.TextUtils.equals(r14, r15);	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r13 = r14;
        goto L_0x022b;
    L_0x022b:
        r14 = "HttpDownloader";	 Catch:{ IllegalArgumentException -> 0x0668, SocketTimeoutException -> 0x0660, UnknownHostException -> 0x0658, IOException -> 0x0650, NullPointerException -> 0x0648, all -> 0x0640 }
        r15 = new java.lang.StringBuilder;	 Catch:{ IllegalArgumentException -> 0x0668, SocketTimeoutException -> 0x0660, UnknownHostException -> 0x0658, IOException -> 0x0650, NullPointerException -> 0x0648, all -> 0x0640 }
        r15.<init>();	 Catch:{ IllegalArgumentException -> 0x0668, SocketTimeoutException -> 0x0660, UnknownHostException -> 0x0658, IOException -> 0x0650, NullPointerException -> 0x0648, all -> 0x0640 }
        r10 = "Response code is ";	 Catch:{ IllegalArgumentException -> 0x0668, SocketTimeoutException -> 0x0660, UnknownHostException -> 0x0658, IOException -> 0x0650, NullPointerException -> 0x0648, all -> 0x0640 }
        r15.append(r10);	 Catch:{ IllegalArgumentException -> 0x0668, SocketTimeoutException -> 0x0660, UnknownHostException -> 0x0658, IOException -> 0x0650, NullPointerException -> 0x0648, all -> 0x0640 }
        r10 = r4.code();	 Catch:{ IllegalArgumentException -> 0x0668, SocketTimeoutException -> 0x0660, UnknownHostException -> 0x0658, IOException -> 0x0650, NullPointerException -> 0x0648, all -> 0x0640 }
        r15.append(r10);	 Catch:{ IllegalArgumentException -> 0x0668, SocketTimeoutException -> 0x0660, UnknownHostException -> 0x0658, IOException -> 0x0650, NullPointerException -> 0x0648, all -> 0x0640 }
        r10 = r15.toString();	 Catch:{ IllegalArgumentException -> 0x0668, SocketTimeoutException -> 0x0660, UnknownHostException -> 0x0658, IOException -> 0x0650, NullPointerException -> 0x0648, all -> 0x0640 }
        android.util.Log.d(r14, r10);	 Catch:{ IllegalArgumentException -> 0x0668, SocketTimeoutException -> 0x0660, UnknownHostException -> 0x0658, IOException -> 0x0650, NullPointerException -> 0x0648, all -> 0x0640 }
        r10 = r4.isSuccessful();	 Catch:{ IllegalArgumentException -> 0x0668, SocketTimeoutException -> 0x0660, UnknownHostException -> 0x0658, IOException -> 0x0650, NullPointerException -> 0x0648, all -> 0x0640 }
        if (r10 != 0) goto L_0x0281;
    L_0x024b:
        r10 = r4.code();	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r11 = 304; // 0x130 float:4.26E-43 double:1.5E-321;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        if (r10 != r11) goto L_0x0281;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
    L_0x0253:
        r10 = "HttpDownloader";	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r11 = new java.lang.StringBuilder;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r11.<init>();	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r14 = "Feed '";	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r11.append(r14);	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r14 = r1.request;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r14 = r14.getSource();	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r11.append(r14);	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r14 = "' not modified since last update, Download canceled";	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r11.append(r14);	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r11 = r11.toString();	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        android.util.Log.d(r10, r11);	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r29.onCancelled();	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        org.apache.commons.io.IOUtils.closeQuietly(r6);
        de.danoeh.antennapod.core.service.download.AntennapodHttpClient.cleanup();
        org.apache.commons.io.IOUtils.closeQuietly(r7);
        return;
        r10 = r4.isSuccessful();	 Catch:{ IllegalArgumentException -> 0x0668, SocketTimeoutException -> 0x0660, UnknownHostException -> 0x0658, IOException -> 0x0650, NullPointerException -> 0x0648, all -> 0x0640 }
        if (r10 == 0) goto L_0x05d9;	 Catch:{ IllegalArgumentException -> 0x0668, SocketTimeoutException -> 0x0660, UnknownHostException -> 0x0658, IOException -> 0x0650, NullPointerException -> 0x0648, all -> 0x0640 }
    L_0x0288:
        r10 = r4.body();	 Catch:{ IllegalArgumentException -> 0x0668, SocketTimeoutException -> 0x0660, UnknownHostException -> 0x0658, IOException -> 0x0650, NullPointerException -> 0x0648, all -> 0x0640 }
        if (r10 != 0) goto L_0x0298;	 Catch:{ IllegalArgumentException -> 0x0668, SocketTimeoutException -> 0x0660, UnknownHostException -> 0x0658, IOException -> 0x0650, NullPointerException -> 0x0648, all -> 0x0640 }
    L_0x028e:
        r26 = r2;	 Catch:{ IllegalArgumentException -> 0x0668, SocketTimeoutException -> 0x0660, UnknownHostException -> 0x0658, IOException -> 0x0650, NullPointerException -> 0x0648, all -> 0x0640 }
        r20 = r5;	 Catch:{ IllegalArgumentException -> 0x0668, SocketTimeoutException -> 0x0660, UnknownHostException -> 0x0658, IOException -> 0x0650, NullPointerException -> 0x0648, all -> 0x0640 }
        r21 = r8;	 Catch:{ IllegalArgumentException -> 0x0668, SocketTimeoutException -> 0x0660, UnknownHostException -> 0x0658, IOException -> 0x0650, NullPointerException -> 0x0648, all -> 0x0640 }
        r22 = r9;	 Catch:{ IllegalArgumentException -> 0x0668, SocketTimeoutException -> 0x0660, UnknownHostException -> 0x0658, IOException -> 0x0650, NullPointerException -> 0x0648, all -> 0x0640 }
        goto L_0x05e1;	 Catch:{ IllegalArgumentException -> 0x0668, SocketTimeoutException -> 0x0660, UnknownHostException -> 0x0658, IOException -> 0x0650, NullPointerException -> 0x0648, all -> 0x0640 }
    L_0x0298:
        r10 = de.danoeh.antennapod.core.util.StorageUtils.storageAvailable();	 Catch:{ IllegalArgumentException -> 0x0668, SocketTimeoutException -> 0x0660, UnknownHostException -> 0x0658, IOException -> 0x0650, NullPointerException -> 0x0648, all -> 0x0640 }
        r11 = 0;
        if (r10 != 0) goto L_0x02ae;
    L_0x029f:
        r10 = de.danoeh.antennapod.core.util.DownloadError.ERROR_DEVICE_NOT_FOUND;	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        r1.onFail(r10, r11);	 Catch:{ IllegalArgumentException -> 0x01cf, SocketTimeoutException -> 0x01c9, UnknownHostException -> 0x01c3, IOException -> 0x01a1, NullPointerException -> 0x01bd, all -> 0x01b5 }
        org.apache.commons.io.IOUtils.closeQuietly(r6);
        de.danoeh.antennapod.core.service.download.AntennapodHttpClient.cleanup();
        org.apache.commons.io.IOUtils.closeQuietly(r7);
        return;
    L_0x02ae:
        r10 = r1.request;	 Catch:{ IllegalArgumentException -> 0x0668, SocketTimeoutException -> 0x0660, UnknownHostException -> 0x0658, IOException -> 0x0650, NullPointerException -> 0x0648, all -> 0x0640 }
        r10 = r10.getFeedfileType();	 Catch:{ IllegalArgumentException -> 0x0668, SocketTimeoutException -> 0x0660, UnknownHostException -> 0x0658, IOException -> 0x0650, NullPointerException -> 0x0648, all -> 0x0640 }
        r14 = 2;
        if (r10 != r14) goto L_0x0350;
    L_0x02b7:
        r10 = -1;
        r14 = "Content-Length";	 Catch:{ IllegalArgumentException -> 0x0348, SocketTimeoutException -> 0x0340, UnknownHostException -> 0x0338, IOException -> 0x0330, NullPointerException -> 0x0328, all -> 0x0320 }
        r14 = r4.header(r14);	 Catch:{ IllegalArgumentException -> 0x0348, SocketTimeoutException -> 0x0340, UnknownHostException -> 0x0338, IOException -> 0x0330, NullPointerException -> 0x0328, all -> 0x0320 }
        if (r14 == 0) goto L_0x02c8;
    L_0x02c0:
        r15 = java.lang.Integer.parseInt(r14);	 Catch:{ NumberFormatException -> 0x02c6 }
        r10 = r15;
        goto L_0x02c9;
    L_0x02c6:
        r0 = move-exception;
        goto L_0x02c9;
    L_0x02c9:
        r15 = "HttpDownloader";	 Catch:{ IllegalArgumentException -> 0x0348, SocketTimeoutException -> 0x0340, UnknownHostException -> 0x0338, IOException -> 0x0330, NullPointerException -> 0x0328, all -> 0x0320 }
        r11 = new java.lang.StringBuilder;	 Catch:{ IllegalArgumentException -> 0x0348, SocketTimeoutException -> 0x0340, UnknownHostException -> 0x0338, IOException -> 0x0330, NullPointerException -> 0x0328, all -> 0x0320 }
        r11.<init>();	 Catch:{ IllegalArgumentException -> 0x0348, SocketTimeoutException -> 0x0340, UnknownHostException -> 0x0338, IOException -> 0x0330, NullPointerException -> 0x0328, all -> 0x0320 }
        r20 = r5;
        r5 = "content length: ";	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r11.append(r5);	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r11.append(r10);	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r5 = r11.toString();	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        android.util.Log.d(r15, r5);	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r5 = "Content-Type";	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r5 = r4.header(r5);	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r11 = "HttpDownloader";	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r15 = new java.lang.StringBuilder;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r15.<init>();	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r21 = r8;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r8 = "content type: ";	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r15.append(r8);	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r15.append(r5);	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r8 = r15.toString();	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        android.util.Log.d(r11, r8);	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        if (r5 == 0) goto L_0x031f;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
    L_0x0301:
        r8 = "text/";	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r8 = r5.startsWith(r8);	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        if (r8 == 0) goto L_0x031f;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
    L_0x030a:
        r8 = 102400; // 0x19000 float:1.43493E-40 double:5.05923E-319;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        if (r10 >= r8) goto L_0x031f;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
    L_0x030f:
        r8 = de.danoeh.antennapod.core.util.DownloadError.ERROR_FILE_TYPE;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r11 = 0;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r1.onFail(r8, r11);	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        org.apache.commons.io.IOUtils.closeQuietly(r6);
        de.danoeh.antennapod.core.service.download.AntennapodHttpClient.cleanup();
        org.apache.commons.io.IOUtils.closeQuietly(r7);
        return;
    L_0x031f:
        goto L_0x0354;
    L_0x0320:
        r0 = move-exception;
        r20 = r5;
        r26 = r2;
        r2 = r0;
        goto L_0x0725;
    L_0x0328:
        r0 = move-exception;
        r20 = r5;
        r26 = r2;
        r2 = r0;
        goto L_0x06bc;
    L_0x0330:
        r0 = move-exception;
        r20 = r5;
        r26 = r2;
        r2 = r0;
        goto L_0x06d1;
    L_0x0338:
        r0 = move-exception;
        r20 = r5;
        r26 = r2;
        r2 = r0;
        goto L_0x06e4;
    L_0x0340:
        r0 = move-exception;
        r20 = r5;
        r26 = r2;
        r2 = r0;
        goto L_0x06f7;
    L_0x0348:
        r0 = move-exception;
        r20 = r5;
        r26 = r2;
        r2 = r0;
        goto L_0x070a;
    L_0x0350:
        r20 = r5;
        r21 = r8;
    L_0x0354:
        r5 = new java.io.BufferedInputStream;	 Catch:{ IllegalArgumentException -> 0x05d1, SocketTimeoutException -> 0x05c9, UnknownHostException -> 0x05c1, IOException -> 0x05b9, NullPointerException -> 0x05b1, all -> 0x05ab }
        r8 = r7.byteStream();	 Catch:{ IllegalArgumentException -> 0x05d1, SocketTimeoutException -> 0x05c9, UnknownHostException -> 0x05c1, IOException -> 0x05b9, NullPointerException -> 0x05b1, all -> 0x05ab }
        r5.<init>(r8);	 Catch:{ IllegalArgumentException -> 0x05d1, SocketTimeoutException -> 0x05c9, UnknownHostException -> 0x05c1, IOException -> 0x05b9, NullPointerException -> 0x05b1, all -> 0x05ab }
        if (r3 == 0) goto L_0x0394;
    L_0x035f:
        r8 = "Content-Range";	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r11 = r4.header(r8);	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        goto L_0x0395;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
    L_0x0366:
        r0 = move-exception;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r26 = r2;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r2 = r0;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        goto L_0x0725;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
    L_0x036c:
        r0 = move-exception;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r26 = r2;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r5 = r20;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r2 = r0;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        goto L_0x06bc;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
    L_0x0374:
        r0 = move-exception;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r26 = r2;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r5 = r20;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r2 = r0;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        goto L_0x06d1;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
    L_0x037c:
        r0 = move-exception;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r26 = r2;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r5 = r20;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r2 = r0;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        goto L_0x06e4;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
    L_0x0384:
        r0 = move-exception;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r26 = r2;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r5 = r20;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r2 = r0;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        goto L_0x06f7;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
    L_0x038c:
        r0 = move-exception;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r26 = r2;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r5 = r20;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r2 = r0;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        goto L_0x070a;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
    L_0x0394:
        r11 = 0;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
    L_0x0395:
        r8 = r11;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        if (r3 == 0) goto L_0x03f7;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
    L_0x0398:
        r10 = r4.code();	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r11 = 206; // 0xce float:2.89E-43 double:1.02E-321;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        if (r10 != r11) goto L_0x03f7;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
    L_0x03a0:
        r10 = android.text.TextUtils.isEmpty(r8);	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        if (r10 != 0) goto L_0x03f2;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
    L_0x03a6:
        r10 = "bytes ";	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r10 = r10.length();	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r11 = "-";	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r11 = r8.indexOf(r11);	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r10 = r8.substring(r10, r11);	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r11 = r1.request;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r14 = java.lang.Long.parseLong(r10);	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r11.setSoFar(r14);	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r11 = "HttpDownloader";	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r14 = new java.lang.StringBuilder;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r14.<init>();	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r15 = "Starting download at position ";	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r14.append(r15);	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r15 = r1.request;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r23 = r8;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r22 = r9;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r8 = r15.getSoFar();	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r14.append(r8);	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r8 = r14.toString();	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        android.util.Log.d(r11, r8);	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r8 = new java.io.RandomAccessFile;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r9 = "rw";	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r8.<init>(r2, r9);	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r6 = r8;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r8 = r1.request;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r8 = r8.getSoFar();	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r6.seek(r8);	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        goto L_0x040a;
    L_0x03f2:
        r23 = r8;
        r22 = r9;
        goto L_0x03fb;
    L_0x03f7:
        r23 = r8;
        r22 = r9;
    L_0x03fb:
        r2.delete();	 Catch:{ IllegalArgumentException -> 0x05d1, SocketTimeoutException -> 0x05c9, UnknownHostException -> 0x05c1, IOException -> 0x05b9, NullPointerException -> 0x05b1, all -> 0x05ab }
        r2.createNewFile();	 Catch:{ IllegalArgumentException -> 0x05d1, SocketTimeoutException -> 0x05c9, UnknownHostException -> 0x05c1, IOException -> 0x05b9, NullPointerException -> 0x05b1, all -> 0x05ab }
        r8 = new java.io.RandomAccessFile;	 Catch:{ IllegalArgumentException -> 0x05d1, SocketTimeoutException -> 0x05c9, UnknownHostException -> 0x05c1, IOException -> 0x05b9, NullPointerException -> 0x05b1, all -> 0x05ab }
        r9 = "rw";	 Catch:{ IllegalArgumentException -> 0x05d1, SocketTimeoutException -> 0x05c9, UnknownHostException -> 0x05c1, IOException -> 0x05b9, NullPointerException -> 0x05b1, all -> 0x05ab }
        r8.<init>(r2, r9);	 Catch:{ IllegalArgumentException -> 0x05d1, SocketTimeoutException -> 0x05c9, UnknownHostException -> 0x05c1, IOException -> 0x05b9, NullPointerException -> 0x05b1, all -> 0x05ab }
        r6 = r8;	 Catch:{ IllegalArgumentException -> 0x05d1, SocketTimeoutException -> 0x05c9, UnknownHostException -> 0x05c1, IOException -> 0x05b9, NullPointerException -> 0x05b1, all -> 0x05ab }
    L_0x040a:
        r8 = 8192; // 0x2000 float:1.14794E-41 double:4.0474E-320;	 Catch:{ IllegalArgumentException -> 0x05d1, SocketTimeoutException -> 0x05c9, UnknownHostException -> 0x05c1, IOException -> 0x05b9, NullPointerException -> 0x05b1, all -> 0x05ab }
        r8 = new byte[r8];	 Catch:{ IllegalArgumentException -> 0x05d1, SocketTimeoutException -> 0x05c9, UnknownHostException -> 0x05c1, IOException -> 0x05b9, NullPointerException -> 0x05b1, all -> 0x05ab }
        r9 = 0;	 Catch:{ IllegalArgumentException -> 0x05d1, SocketTimeoutException -> 0x05c9, UnknownHostException -> 0x05c1, IOException -> 0x05b9, NullPointerException -> 0x05b1, all -> 0x05ab }
        r10 = r1.request;	 Catch:{ IllegalArgumentException -> 0x05d1, SocketTimeoutException -> 0x05c9, UnknownHostException -> 0x05c1, IOException -> 0x05b9, NullPointerException -> 0x05b1, all -> 0x05ab }
        r11 = de.danoeh.antennapod.core.C0734R.string.download_running;	 Catch:{ IllegalArgumentException -> 0x05d1, SocketTimeoutException -> 0x05c9, UnknownHostException -> 0x05c1, IOException -> 0x05b9, NullPointerException -> 0x05b1, all -> 0x05ab }
        r10.setStatusMsg(r11);	 Catch:{ IllegalArgumentException -> 0x05d1, SocketTimeoutException -> 0x05c9, UnknownHostException -> 0x05c1, IOException -> 0x05b9, NullPointerException -> 0x05b1, all -> 0x05ab }
        r10 = "HttpDownloader";	 Catch:{ IllegalArgumentException -> 0x05d1, SocketTimeoutException -> 0x05c9, UnknownHostException -> 0x05c1, IOException -> 0x05b9, NullPointerException -> 0x05b1, all -> 0x05ab }
        r11 = "Getting size of download";	 Catch:{ IllegalArgumentException -> 0x05d1, SocketTimeoutException -> 0x05c9, UnknownHostException -> 0x05c1, IOException -> 0x05b9, NullPointerException -> 0x05b1, all -> 0x05ab }
        android.util.Log.d(r10, r11);	 Catch:{ IllegalArgumentException -> 0x05d1, SocketTimeoutException -> 0x05c9, UnknownHostException -> 0x05c1, IOException -> 0x05b9, NullPointerException -> 0x05b1, all -> 0x05ab }
        r10 = r1.request;	 Catch:{ IllegalArgumentException -> 0x05d1, SocketTimeoutException -> 0x05c9, UnknownHostException -> 0x05c1, IOException -> 0x05b9, NullPointerException -> 0x05b1, all -> 0x05ab }
        r14 = r7.contentLength();	 Catch:{ IllegalArgumentException -> 0x05d1, SocketTimeoutException -> 0x05c9, UnknownHostException -> 0x05c1, IOException -> 0x05b9, NullPointerException -> 0x05b1, all -> 0x05ab }
        r11 = r1.request;	 Catch:{ IllegalArgumentException -> 0x05d1, SocketTimeoutException -> 0x05c9, UnknownHostException -> 0x05c1, IOException -> 0x05b9, NullPointerException -> 0x05b1, all -> 0x05ab }
        r24 = r11.getSoFar();	 Catch:{ IllegalArgumentException -> 0x05d1, SocketTimeoutException -> 0x05c9, UnknownHostException -> 0x05c1, IOException -> 0x05b9, NullPointerException -> 0x05b1, all -> 0x05ab }
        r14 = r14 + r24;	 Catch:{ IllegalArgumentException -> 0x05d1, SocketTimeoutException -> 0x05c9, UnknownHostException -> 0x05c1, IOException -> 0x05b9, NullPointerException -> 0x05b1, all -> 0x05ab }
        r10.setSize(r14);	 Catch:{ IllegalArgumentException -> 0x05d1, SocketTimeoutException -> 0x05c9, UnknownHostException -> 0x05c1, IOException -> 0x05b9, NullPointerException -> 0x05b1, all -> 0x05ab }
        r10 = "HttpDownloader";	 Catch:{ IllegalArgumentException -> 0x05d1, SocketTimeoutException -> 0x05c9, UnknownHostException -> 0x05c1, IOException -> 0x05b9, NullPointerException -> 0x05b1, all -> 0x05ab }
        r11 = new java.lang.StringBuilder;	 Catch:{ IllegalArgumentException -> 0x05d1, SocketTimeoutException -> 0x05c9, UnknownHostException -> 0x05c1, IOException -> 0x05b9, NullPointerException -> 0x05b1, all -> 0x05ab }
        r11.<init>();	 Catch:{ IllegalArgumentException -> 0x05d1, SocketTimeoutException -> 0x05c9, UnknownHostException -> 0x05c1, IOException -> 0x05b9, NullPointerException -> 0x05b1, all -> 0x05ab }
        r14 = "Size is ";	 Catch:{ IllegalArgumentException -> 0x05d1, SocketTimeoutException -> 0x05c9, UnknownHostException -> 0x05c1, IOException -> 0x05b9, NullPointerException -> 0x05b1, all -> 0x05ab }
        r11.append(r14);	 Catch:{ IllegalArgumentException -> 0x05d1, SocketTimeoutException -> 0x05c9, UnknownHostException -> 0x05c1, IOException -> 0x05b9, NullPointerException -> 0x05b1, all -> 0x05ab }
        r14 = r1.request;	 Catch:{ IllegalArgumentException -> 0x05d1, SocketTimeoutException -> 0x05c9, UnknownHostException -> 0x05c1, IOException -> 0x05b9, NullPointerException -> 0x05b1, all -> 0x05ab }
        r14 = r14.getSize();	 Catch:{ IllegalArgumentException -> 0x05d1, SocketTimeoutException -> 0x05c9, UnknownHostException -> 0x05c1, IOException -> 0x05b9, NullPointerException -> 0x05b1, all -> 0x05ab }
        r11.append(r14);	 Catch:{ IllegalArgumentException -> 0x05d1, SocketTimeoutException -> 0x05c9, UnknownHostException -> 0x05c1, IOException -> 0x05b9, NullPointerException -> 0x05b1, all -> 0x05ab }
        r11 = r11.toString();	 Catch:{ IllegalArgumentException -> 0x05d1, SocketTimeoutException -> 0x05c9, UnknownHostException -> 0x05c1, IOException -> 0x05b9, NullPointerException -> 0x05b1, all -> 0x05ab }
        android.util.Log.d(r10, r11);	 Catch:{ IllegalArgumentException -> 0x05d1, SocketTimeoutException -> 0x05c9, UnknownHostException -> 0x05c1, IOException -> 0x05b9, NullPointerException -> 0x05b1, all -> 0x05ab }
        r10 = r1.request;	 Catch:{ IllegalArgumentException -> 0x05d1, SocketTimeoutException -> 0x05c9, UnknownHostException -> 0x05c1, IOException -> 0x05b9, NullPointerException -> 0x05b1, all -> 0x05ab }
        r10 = r10.getSize();	 Catch:{ IllegalArgumentException -> 0x05d1, SocketTimeoutException -> 0x05c9, UnknownHostException -> 0x05c1, IOException -> 0x05b9, NullPointerException -> 0x05b1, all -> 0x05ab }
        r14 = -1;
        r18 = 0;
        r24 = (r10 > r18 ? 1 : (r10 == r18 ? 0 : -1));
        if (r24 >= 0) goto L_0x045e;
    L_0x0458:
        r10 = r1.request;	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        r10.setSize(r14);	 Catch:{ IllegalArgumentException -> 0x038c, SocketTimeoutException -> 0x0384, UnknownHostException -> 0x037c, IOException -> 0x0374, NullPointerException -> 0x036c, all -> 0x0366 }
        goto L_0x045f;
    L_0x045f:
        r10 = de.danoeh.antennapod.core.util.StorageUtils.getFreeSpaceAvailable();	 Catch:{ IllegalArgumentException -> 0x05d1, SocketTimeoutException -> 0x05c9, UnknownHostException -> 0x05c1, IOException -> 0x05b9, NullPointerException -> 0x05b1, all -> 0x05ab }
        r14 = "HttpDownloader";	 Catch:{ IllegalArgumentException -> 0x05d1, SocketTimeoutException -> 0x05c9, UnknownHostException -> 0x05c1, IOException -> 0x05b9, NullPointerException -> 0x05b1, all -> 0x05ab }
        r15 = new java.lang.StringBuilder;	 Catch:{ IllegalArgumentException -> 0x05d1, SocketTimeoutException -> 0x05c9, UnknownHostException -> 0x05c1, IOException -> 0x05b9, NullPointerException -> 0x05b1, all -> 0x05ab }
        r15.<init>();	 Catch:{ IllegalArgumentException -> 0x05d1, SocketTimeoutException -> 0x05c9, UnknownHostException -> 0x05c1, IOException -> 0x05b9, NullPointerException -> 0x05b1, all -> 0x05ab }
        r26 = r2;
        r2 = "Free space is ";	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r15.append(r2);	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r15.append(r10);	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r2 = r15.toString();	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        android.util.Log.d(r14, r2);	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r2 = r1.request;	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r14 = r2.getSize();	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r24 = -1;	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r2 = (r14 > r24 ? 1 : (r14 == r24 ? 0 : -1));	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        if (r2 == 0) goto L_0x04a1;	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
    L_0x0487:
        r2 = r1.request;	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r14 = r2.getSize();	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r2 = (r14 > r10 ? 1 : (r14 == r10 ? 0 : -1));	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        if (r2 <= 0) goto L_0x04a1;	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
    L_0x0491:
        r2 = de.danoeh.antennapod.core.util.DownloadError.ERROR_NOT_ENOUGH_SPACE;	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r14 = 0;	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r1.onFail(r2, r14);	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        org.apache.commons.io.IOUtils.closeQuietly(r6);
        de.danoeh.antennapod.core.service.download.AntennapodHttpClient.cleanup();
        org.apache.commons.io.IOUtils.closeQuietly(r7);
        return;
        r2 = "HttpDownloader";	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r14 = "Starting download";	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        android.util.Log.d(r2, r14);	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
    L_0x04a9:
        r2 = r1.cancelled;	 Catch:{ IOException -> 0x04f0, IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, NullPointerException -> 0x0622, all -> 0x061e }
        if (r2 != 0) goto L_0x04ed;	 Catch:{ IOException -> 0x04f0, IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, NullPointerException -> 0x0622, all -> 0x061e }
    L_0x04ad:
        r2 = r5.read(r8);	 Catch:{ IOException -> 0x04f0, IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, NullPointerException -> 0x0622, all -> 0x061e }
        r9 = r2;	 Catch:{ IOException -> 0x04f0, IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, NullPointerException -> 0x0622, all -> 0x061e }
        r14 = -1;	 Catch:{ IOException -> 0x04f0, IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, NullPointerException -> 0x0622, all -> 0x061e }
        if (r2 == r14) goto L_0x04ed;	 Catch:{ IOException -> 0x04f0, IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, NullPointerException -> 0x0622, all -> 0x061e }
    L_0x04b5:
        r2 = 0;	 Catch:{ IOException -> 0x04f0, IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, NullPointerException -> 0x0622, all -> 0x061e }
        r6.write(r8, r2, r9);	 Catch:{ IOException -> 0x04f0, IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, NullPointerException -> 0x0622, all -> 0x061e }
        r2 = r1.request;	 Catch:{ IOException -> 0x04f0, IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, NullPointerException -> 0x0622, all -> 0x061e }
        r14 = r1.request;	 Catch:{ IOException -> 0x04f0, IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, NullPointerException -> 0x0622, all -> 0x061e }
        r14 = r14.getSoFar();	 Catch:{ IOException -> 0x04f0, IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, NullPointerException -> 0x0622, all -> 0x061e }
        r27 = r10;
        r10 = (long) r9;
        r14 = r14 + r10;
        r2.setSoFar(r14);	 Catch:{ IOException -> 0x04ea, IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, NullPointerException -> 0x0622, all -> 0x061e }
        r10 = 4636737291354636288; // 0x4059000000000000 float:0.0 double:100.0;	 Catch:{ IOException -> 0x04ea, IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, NullPointerException -> 0x0622, all -> 0x061e }
        r2 = r1.request;	 Catch:{ IOException -> 0x04ea, IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, NullPointerException -> 0x0622, all -> 0x061e }
        r14 = r2.getSoFar();	 Catch:{ IOException -> 0x04ea, IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, NullPointerException -> 0x0622, all -> 0x061e }
        r14 = (double) r14;
        java.lang.Double.isNaN(r14);
        r14 = r14 * r10;
        r2 = r1.request;	 Catch:{ IOException -> 0x04ea, IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, NullPointerException -> 0x0622, all -> 0x061e }
        r10 = r2.getSize();	 Catch:{ IOException -> 0x04ea, IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, NullPointerException -> 0x0622, all -> 0x061e }
        r10 = (double) r10;
        java.lang.Double.isNaN(r10);
        r14 = r14 / r10;
        r2 = (int) r14;
        r10 = r1.request;	 Catch:{ IOException -> 0x04ea, IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, NullPointerException -> 0x0622, all -> 0x061e }
        r10.setProgressPercent(r2);	 Catch:{ IOException -> 0x04ea, IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, NullPointerException -> 0x0622, all -> 0x061e }
        r10 = r27;
        goto L_0x04a9;
    L_0x04ea:
        r0 = move-exception;
        r2 = r0;
        goto L_0x04f4;
    L_0x04ed:
        r27 = r10;
        goto L_0x04fd;
    L_0x04f0:
        r0 = move-exception;
        r27 = r10;
        r2 = r0;
    L_0x04f4:
        r10 = "HttpDownloader";	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r11 = android.util.Log.getStackTraceString(r2);	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        android.util.Log.e(r10, r11);	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
    L_0x04fd:
        r2 = r1.cancelled;	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        if (r2 == 0) goto L_0x0506;	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
    L_0x0501:
        r29.onCancelled();	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        goto L_0x059e;	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
    L_0x0506:
        if (r13 != 0) goto L_0x0559;	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
    L_0x0508:
        r2 = r1.request;	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r10 = r2.getSize();	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r14 = -1;	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r2 = (r10 > r14 ? 1 : (r10 == r14 ? 0 : -1));	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        if (r2 == 0) goto L_0x0559;	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
    L_0x0514:
        r2 = r1.request;	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r10 = r2.getSoFar();	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r2 = r1.request;	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r14 = r2.getSize();	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r2 = (r10 > r14 ? 1 : (r10 == r14 ? 0 : -1));	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        if (r2 == 0) goto L_0x0558;	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
    L_0x0524:
        r2 = de.danoeh.antennapod.core.util.DownloadError.ERROR_IO_ERROR;	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r10 = new java.lang.StringBuilder;	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r10.<init>();	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r11 = "Download completed but size: ";	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r10.append(r11);	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r11 = r1.request;	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r14 = r11.getSoFar();	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r10.append(r14);	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r11 = " does not equal expected size ";	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r10.append(r11);	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r11 = r1.request;	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r14 = r11.getSize();	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r10.append(r14);	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r10 = r10.toString();	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r1.onFail(r2, r10);	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        org.apache.commons.io.IOUtils.closeQuietly(r6);
        de.danoeh.antennapod.core.service.download.AntennapodHttpClient.cleanup();
        org.apache.commons.io.IOUtils.closeQuietly(r7);
        return;
    L_0x0558:
        goto L_0x055a;
    L_0x055a:
        r2 = r1.request;	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r10 = r2.getSize();	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r14 = 0;	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r2 = (r10 > r14 ? 1 : (r10 == r14 ? 0 : -1));	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        if (r2 <= 0) goto L_0x0581;	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
    L_0x0566:
        r2 = r1.request;	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r10 = r2.getSoFar();	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r2 = (r10 > r14 ? 1 : (r10 == r14 ? 0 : -1));	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        if (r2 != 0) goto L_0x0581;	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
    L_0x0570:
        r2 = de.danoeh.antennapod.core.util.DownloadError.ERROR_IO_ERROR;	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r10 = "Download completed, but nothing was read";	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r1.onFail(r2, r10);	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        org.apache.commons.io.IOUtils.closeQuietly(r6);
        de.danoeh.antennapod.core.service.download.AntennapodHttpClient.cleanup();
        org.apache.commons.io.IOUtils.closeQuietly(r7);
        return;
        r2 = "Last-Modified";	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r2 = r4.header(r2);	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        if (r2 == 0) goto L_0x0590;	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
    L_0x058a:
        r10 = r1.request;	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r10.setLastModified(r2);	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        goto L_0x059b;	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
    L_0x0590:
        r10 = r1.request;	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r11 = "ETag";	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r11 = r4.header(r11);	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r10.setLastModified(r11);	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
    L_0x059b:
        r29.onSuccess();	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
    L_0x059e:
        org.apache.commons.io.IOUtils.closeQuietly(r6);
        de.danoeh.antennapod.core.service.download.AntennapodHttpClient.cleanup();
        org.apache.commons.io.IOUtils.closeQuietly(r7);
        r5 = r20;
        goto L_0x0720;
    L_0x05ab:
        r0 = move-exception;
        r26 = r2;
        r2 = r0;
        goto L_0x0725;
    L_0x05b1:
        r0 = move-exception;
        r26 = r2;
        r2 = r0;
        r5 = r20;
        goto L_0x06bc;
    L_0x05b9:
        r0 = move-exception;
        r26 = r2;
        r2 = r0;
        r5 = r20;
        goto L_0x06d1;
    L_0x05c1:
        r0 = move-exception;
        r26 = r2;
        r2 = r0;
        r5 = r20;
        goto L_0x06e4;
    L_0x05c9:
        r0 = move-exception;
        r26 = r2;
        r2 = r0;
        r5 = r20;
        goto L_0x06f7;
    L_0x05d1:
        r0 = move-exception;
        r26 = r2;
        r2 = r0;
        r5 = r20;
        goto L_0x070a;
    L_0x05d9:
        r26 = r2;
        r20 = r5;
        r21 = r8;
        r22 = r9;
    L_0x05e1:
        r2 = r4.code();	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r5 = 401; // 0x191 float:5.62E-43 double:1.98E-321;	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        if (r2 != r5) goto L_0x05f4;	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
    L_0x05e9:
        r2 = de.danoeh.antennapod.core.util.DownloadError.ERROR_UNAUTHORIZED;	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r5 = r4.code();	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r5 = java.lang.String.valueOf(r5);	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        goto L_0x0611;	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
    L_0x05f4:
        r2 = r4.code();	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r5 = 403; // 0x193 float:5.65E-43 double:1.99E-321;	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        if (r2 != r5) goto L_0x0607;	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
    L_0x05fc:
        r2 = de.danoeh.antennapod.core.util.DownloadError.ERROR_FORBIDDEN;	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r5 = r4.code();	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r5 = java.lang.String.valueOf(r5);	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        goto L_0x0611;	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
    L_0x0607:
        r2 = de.danoeh.antennapod.core.util.DownloadError.ERROR_HTTP_DATA_ERROR;	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r5 = r4.code();	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        r5 = java.lang.String.valueOf(r5);	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
    L_0x0611:
        r1.onFail(r2, r5);	 Catch:{ IllegalArgumentException -> 0x063a, SocketTimeoutException -> 0x0634, UnknownHostException -> 0x062e, IOException -> 0x0628, NullPointerException -> 0x0622, all -> 0x061e }
        org.apache.commons.io.IOUtils.closeQuietly(r6);
        de.danoeh.antennapod.core.service.download.AntennapodHttpClient.cleanup();
        org.apache.commons.io.IOUtils.closeQuietly(r7);
        return;
    L_0x061e:
        r0 = move-exception;
        r2 = r0;
        goto L_0x0725;
    L_0x0622:
        r0 = move-exception;
        r2 = r0;
        r5 = r20;
        goto L_0x06bc;
    L_0x0628:
        r0 = move-exception;
        r2 = r0;
        r5 = r20;
        goto L_0x06d1;
    L_0x062e:
        r0 = move-exception;
        r2 = r0;
        r5 = r20;
        goto L_0x06e4;
    L_0x0634:
        r0 = move-exception;
        r2 = r0;
        r5 = r20;
        goto L_0x06f7;
    L_0x063a:
        r0 = move-exception;
        r2 = r0;
        r5 = r20;
        goto L_0x070a;
    L_0x0640:
        r0 = move-exception;
        r26 = r2;
        r20 = r5;
        r2 = r0;
        goto L_0x0725;
    L_0x0648:
        r0 = move-exception;
        r26 = r2;
        r20 = r5;
        r2 = r0;
        goto L_0x06bc;
    L_0x0650:
        r0 = move-exception;
        r26 = r2;
        r20 = r5;
        r2 = r0;
        goto L_0x06d1;
    L_0x0658:
        r0 = move-exception;
        r26 = r2;
        r20 = r5;
        r2 = r0;
        goto L_0x06e4;
    L_0x0660:
        r0 = move-exception;
        r26 = r2;
        r20 = r5;
        r2 = r0;
        goto L_0x06f7;
    L_0x0668:
        r0 = move-exception;
        r26 = r2;
        r20 = r5;
        r2 = r0;
        goto L_0x070a;
    L_0x0670:
        r26 = r2;
        r21 = r8;
        r22 = r9;
        throw r4;	 Catch:{ IllegalArgumentException -> 0x0686, SocketTimeoutException -> 0x0682, UnknownHostException -> 0x067e, IOException -> 0x067a, NullPointerException -> 0x0677 }
    L_0x0677:
        r0 = move-exception;
        r2 = r0;
        goto L_0x06bc;
    L_0x067a:
        r0 = move-exception;
        r2 = r0;
        goto L_0x06d1;
    L_0x067e:
        r0 = move-exception;
        r2 = r0;
        goto L_0x06e4;
    L_0x0682:
        r0 = move-exception;
        r2 = r0;
        goto L_0x06f7;
    L_0x0686:
        r0 = move-exception;
        r2 = r0;
        goto L_0x070a;
    L_0x068a:
        r0 = move-exception;
        r26 = r2;
        r2 = r0;
        r20 = r5;
        goto L_0x0725;
    L_0x0692:
        r0 = move-exception;
        r26 = r2;
        r2 = r0;
        goto L_0x06bc;
    L_0x0697:
        r0 = move-exception;
        r26 = r2;
        r2 = r0;
        goto L_0x06d1;
    L_0x069c:
        r0 = move-exception;
        r26 = r2;
        r2 = r0;
        goto L_0x06e4;
    L_0x06a1:
        r0 = move-exception;
        r26 = r2;
        r2 = r0;
        goto L_0x06f7;
    L_0x06a6:
        r0 = move-exception;
        r26 = r2;
        r2 = r0;
        goto L_0x070a;
    L_0x06ac:
        r0 = move-exception;
        r26 = r2;
        r17 = r4;
        r2 = r0;
        r20 = r5;
        goto L_0x0725;
    L_0x06b6:
        r0 = move-exception;
        r26 = r2;
        r17 = r4;
        r2 = r0;
    L_0x06bc:
        r2.printStackTrace();	 Catch:{ all -> 0x0721 }
        r4 = de.danoeh.antennapod.core.util.DownloadError.ERROR_CONNECTION_ERROR;	 Catch:{ all -> 0x0721 }
        r8 = r1.request;	 Catch:{ all -> 0x0721 }
        r8 = r8.getSource();	 Catch:{ all -> 0x0721 }
        r1.onFail(r4, r8);	 Catch:{ all -> 0x0721 }
        goto L_0x0716;	 Catch:{ all -> 0x0721 }
    L_0x06cb:
        r0 = move-exception;	 Catch:{ all -> 0x0721 }
        r26 = r2;	 Catch:{ all -> 0x0721 }
        r17 = r4;	 Catch:{ all -> 0x0721 }
        r2 = r0;	 Catch:{ all -> 0x0721 }
    L_0x06d1:
        r2.printStackTrace();	 Catch:{ all -> 0x0721 }
        r4 = de.danoeh.antennapod.core.util.DownloadError.ERROR_IO_ERROR;	 Catch:{ all -> 0x0721 }
        r8 = r2.getMessage();	 Catch:{ all -> 0x0721 }
        r1.onFail(r4, r8);	 Catch:{ all -> 0x0721 }
        goto L_0x0716;	 Catch:{ all -> 0x0721 }
    L_0x06de:
        r0 = move-exception;	 Catch:{ all -> 0x0721 }
        r26 = r2;	 Catch:{ all -> 0x0721 }
        r17 = r4;	 Catch:{ all -> 0x0721 }
        r2 = r0;	 Catch:{ all -> 0x0721 }
    L_0x06e4:
        r2.printStackTrace();	 Catch:{ all -> 0x0721 }
        r4 = de.danoeh.antennapod.core.util.DownloadError.ERROR_UNKNOWN_HOST;	 Catch:{ all -> 0x0721 }
        r8 = r2.getMessage();	 Catch:{ all -> 0x0721 }
        r1.onFail(r4, r8);	 Catch:{ all -> 0x0721 }
        goto L_0x0716;	 Catch:{ all -> 0x0721 }
    L_0x06f1:
        r0 = move-exception;	 Catch:{ all -> 0x0721 }
        r26 = r2;	 Catch:{ all -> 0x0721 }
        r17 = r4;	 Catch:{ all -> 0x0721 }
        r2 = r0;	 Catch:{ all -> 0x0721 }
    L_0x06f7:
        r2.printStackTrace();	 Catch:{ all -> 0x0721 }
        r4 = de.danoeh.antennapod.core.util.DownloadError.ERROR_CONNECTION_ERROR;	 Catch:{ all -> 0x0721 }
        r8 = r2.getMessage();	 Catch:{ all -> 0x0721 }
        r1.onFail(r4, r8);	 Catch:{ all -> 0x0721 }
        goto L_0x0716;	 Catch:{ all -> 0x0721 }
    L_0x0704:
        r0 = move-exception;	 Catch:{ all -> 0x0721 }
        r26 = r2;	 Catch:{ all -> 0x0721 }
        r17 = r4;	 Catch:{ all -> 0x0721 }
        r2 = r0;	 Catch:{ all -> 0x0721 }
    L_0x070a:
        r2.printStackTrace();	 Catch:{ all -> 0x0721 }
        r4 = de.danoeh.antennapod.core.util.DownloadError.ERROR_MALFORMED_URL;	 Catch:{ all -> 0x0721 }
        r8 = r2.getMessage();	 Catch:{ all -> 0x0721 }
        r1.onFail(r4, r8);	 Catch:{ all -> 0x0721 }
    L_0x0716:
        org.apache.commons.io.IOUtils.closeQuietly(r6);
        de.danoeh.antennapod.core.service.download.AntennapodHttpClient.cleanup();
        org.apache.commons.io.IOUtils.closeQuietly(r7);
    L_0x0720:
        return;
    L_0x0721:
        r0 = move-exception;
        r2 = r0;
        r20 = r5;
    L_0x0725:
        org.apache.commons.io.IOUtils.closeQuietly(r6);
        de.danoeh.antennapod.core.service.download.AntennapodHttpClient.cleanup();
        org.apache.commons.io.IOUtils.closeQuietly(r7);
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.core.service.download.HttpDownloader.download():void");
    }

    public HttpDownloader(DownloadRequest request) {
        super(request);
    }

    private void onSuccess() {
        Log.d(TAG, "Download was successful");
        this.result.setSuccessful();
    }

    private void onFail(DownloadError reason, String reasonDetailed) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onFail() called with: reason = [");
        stringBuilder.append(reason);
        stringBuilder.append("], reasonDetailed = [");
        stringBuilder.append(reasonDetailed);
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
        this.result.setFailed(reason, reasonDetailed);
        if (this.request.isDeleteOnFailure()) {
            cleanup();
        }
    }

    private void onCancelled() {
        Log.d(TAG, "Download was cancelled");
        this.result.setCancelled();
        cleanup();
    }

    private void cleanup() {
        if (this.request.getDestination() != null) {
            File dest = new File(this.request.getDestination());
            if (dest.exists()) {
                boolean rc = dest.delete();
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Deleted file ");
                stringBuilder.append(dest.getName());
                stringBuilder.append("; Result: ");
                stringBuilder.append(rc);
                Log.d(str, stringBuilder.toString());
                return;
            }
            Log.d(TAG, "cleanup() didn't delete file: does not exist.");
        }
    }

    public static String encodeCredentials(String username, String password, String charset) {
        try {
            String credentials = new StringBuilder();
            credentials.append(username);
            credentials.append(":");
            credentials.append(password);
            String encoded = ByteString.of(credentials.toString().getBytes(charset)).base64();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Basic ");
            stringBuilder.append(encoded);
            return stringBuilder.toString();
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }
}
