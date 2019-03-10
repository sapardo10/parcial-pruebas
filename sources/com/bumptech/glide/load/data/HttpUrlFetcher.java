package com.bumptech.glide.load.data;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Log;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher.DataCallback;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.util.ContentLengthInputStream;
import com.bumptech.glide.util.LogTime;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUrlFetcher implements DataFetcher<InputStream> {
    @VisibleForTesting
    static final HttpUrlConnectionFactory DEFAULT_CONNECTION_FACTORY = new DefaultHttpUrlConnectionFactory();
    private static final int INVALID_STATUS_CODE = -1;
    private static final int MAXIMUM_REDIRECTS = 5;
    private static final String TAG = "HttpUrlFetcher";
    private final HttpUrlConnectionFactory connectionFactory;
    private final GlideUrl glideUrl;
    private volatile boolean isCancelled;
    private InputStream stream;
    private final int timeout;
    private HttpURLConnection urlConnection;

    interface HttpUrlConnectionFactory {
        HttpURLConnection build(URL url) throws IOException;
    }

    private static class DefaultHttpUrlConnectionFactory implements HttpUrlConnectionFactory {
        DefaultHttpUrlConnectionFactory() {
        }

        public HttpURLConnection build(URL url) throws IOException {
            return (HttpURLConnection) url.openConnection();
        }
    }

    private java.io.InputStream loadDataWithRedirects(java.net.URL r6, int r7, java.net.URL r8, java.util.Map<java.lang.String, java.lang.String> r9) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:40:0x00db in {6, 8, 10, 11, 15, 19, 23, 29, 31, 35, 37, 39} preds:[]
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
        r0 = 5;
        if (r7 >= r0) goto L_0x00d3;
    L_0x0003:
        if (r8 == 0) goto L_0x001e;
    L_0x0005:
        r0 = r6.toURI();	 Catch:{ URISyntaxException -> 0x001c }
        r1 = r8.toURI();	 Catch:{ URISyntaxException -> 0x001c }
        r0 = r0.equals(r1);	 Catch:{ URISyntaxException -> 0x001c }
        if (r0 != 0) goto L_0x0014;	 Catch:{ URISyntaxException -> 0x001c }
    L_0x0013:
        goto L_0x001e;	 Catch:{ URISyntaxException -> 0x001c }
    L_0x0014:
        r0 = new com.bumptech.glide.load.HttpException;	 Catch:{ URISyntaxException -> 0x001c }
        r1 = "In re-direct loop";	 Catch:{ URISyntaxException -> 0x001c }
        r0.<init>(r1);	 Catch:{ URISyntaxException -> 0x001c }
        throw r0;	 Catch:{ URISyntaxException -> 0x001c }
    L_0x001c:
        r0 = move-exception;
        goto L_0x0020;
    L_0x0020:
        r0 = r5.connectionFactory;
        r0 = r0.build(r6);
        r5.urlConnection = r0;
        r0 = r9.entrySet();
        r0 = r0.iterator();
    L_0x0030:
        r1 = r0.hasNext();
        if (r1 == 0) goto L_0x004e;
    L_0x0036:
        r1 = r0.next();
        r1 = (java.util.Map.Entry) r1;
        r2 = r5.urlConnection;
        r3 = r1.getKey();
        r3 = (java.lang.String) r3;
        r4 = r1.getValue();
        r4 = (java.lang.String) r4;
        r2.addRequestProperty(r3, r4);
        goto L_0x0030;
    L_0x004e:
        r0 = r5.urlConnection;
        r1 = r5.timeout;
        r0.setConnectTimeout(r1);
        r0 = r5.urlConnection;
        r1 = r5.timeout;
        r0.setReadTimeout(r1);
        r0 = r5.urlConnection;
        r1 = 0;
        r0.setUseCaches(r1);
        r0 = r5.urlConnection;
        r2 = 1;
        r0.setDoInput(r2);
        r0 = r5.urlConnection;
        r0.setInstanceFollowRedirects(r1);
        r0 = r5.urlConnection;
        r0.connect();
        r0 = r5.urlConnection;
        r0 = r0.getInputStream();
        r5.stream = r0;
        r0 = r5.isCancelled;
        if (r0 == 0) goto L_0x0080;
    L_0x007e:
        r0 = 0;
        return r0;
    L_0x0080:
        r0 = r5.urlConnection;
        r0 = r0.getResponseCode();
        r1 = isHttpOk(r0);
        if (r1 == 0) goto L_0x0093;
    L_0x008c:
        r1 = r5.urlConnection;
        r1 = r5.getStreamForSuccessfulRequest(r1);
        return r1;
    L_0x0093:
        r1 = isHttpRedirect(r0);
        if (r1 == 0) goto L_0x00be;
    L_0x0099:
        r1 = r5.urlConnection;
        r2 = "Location";
        r1 = r1.getHeaderField(r2);
        r2 = android.text.TextUtils.isEmpty(r1);
        if (r2 != 0) goto L_0x00b6;
    L_0x00a7:
        r2 = new java.net.URL;
        r2.<init>(r6, r1);
        r5.cleanup();
        r3 = r7 + 1;
        r3 = r5.loadDataWithRedirects(r2, r3, r6, r9);
        return r3;
    L_0x00b6:
        r2 = new com.bumptech.glide.load.HttpException;
        r3 = "Received empty or null redirect url";
        r2.<init>(r3);
        throw r2;
    L_0x00be:
        r1 = -1;
        if (r0 != r1) goto L_0x00c7;
    L_0x00c1:
        r1 = new com.bumptech.glide.load.HttpException;
        r1.<init>(r0);
        throw r1;
    L_0x00c7:
        r1 = new com.bumptech.glide.load.HttpException;
        r2 = r5.urlConnection;
        r2 = r2.getResponseMessage();
        r1.<init>(r2, r0);
        throw r1;
    L_0x00d3:
        r0 = new com.bumptech.glide.load.HttpException;
        r1 = "Too many (> 5) redirects!";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.load.data.HttpUrlFetcher.loadDataWithRedirects(java.net.URL, int, java.net.URL, java.util.Map):java.io.InputStream");
    }

    public HttpUrlFetcher(GlideUrl glideUrl, int timeout) {
        this(glideUrl, timeout, DEFAULT_CONNECTION_FACTORY);
    }

    @VisibleForTesting
    HttpUrlFetcher(GlideUrl glideUrl, int timeout, HttpUrlConnectionFactory connectionFactory) {
        this.glideUrl = glideUrl;
        this.timeout = timeout;
        this.connectionFactory = connectionFactory;
    }

    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super InputStream> callback) {
        long startTime = LogTime.getLogTime();
        String str;
        StringBuilder stringBuilder;
        try {
            callback.onDataReady(loadDataWithRedirects(this.glideUrl.toURL(), 0, null, this.glideUrl.getHeaders()));
            if (Log.isLoggable(TAG, 2)) {
                str = TAG;
                stringBuilder = new StringBuilder();
                stringBuilder.append("Finished http url fetcher fetch in ");
                stringBuilder.append(LogTime.getElapsedMillis(startTime));
                Log.v(str, stringBuilder.toString());
            }
        } catch (IOException e) {
            if (Log.isLoggable(TAG, 3)) {
                Log.d(TAG, "Failed to load data for url", e);
            }
            callback.onLoadFailed(e);
            if (Log.isLoggable(TAG, 2)) {
                str = TAG;
                stringBuilder = new StringBuilder();
            }
        } catch (Throwable th) {
            if (Log.isLoggable(TAG, 2)) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Finished http url fetcher fetch in ");
                stringBuilder2.append(LogTime.getElapsedMillis(startTime));
                Log.v(TAG, stringBuilder2.toString());
            }
        }
    }

    private static boolean isHttpOk(int statusCode) {
        return statusCode / 100 == 2;
    }

    private static boolean isHttpRedirect(int statusCode) {
        return statusCode / 100 == 3;
    }

    private InputStream getStreamForSuccessfulRequest(HttpURLConnection urlConnection) throws IOException {
        if (TextUtils.isEmpty(urlConnection.getContentEncoding())) {
            this.stream = ContentLengthInputStream.obtain(urlConnection.getInputStream(), (long) urlConnection.getContentLength());
        } else {
            if (Log.isLoggable(TAG, 3)) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Got non empty content encoding: ");
                stringBuilder.append(urlConnection.getContentEncoding());
                Log.d(str, stringBuilder.toString());
            }
            this.stream = urlConnection.getInputStream();
        }
        return this.stream;
    }

    public void cleanup() {
        InputStream inputStream = this.stream;
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
            }
        }
        HttpURLConnection httpURLConnection = this.urlConnection;
        if (httpURLConnection != null) {
            httpURLConnection.disconnect();
        }
        this.urlConnection = null;
    }

    public void cancel() {
        this.isCancelled = true;
    }

    @NonNull
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }

    @NonNull
    public DataSource getDataSource() {
        return DataSource.REMOTE;
    }
}
