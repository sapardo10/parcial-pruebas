package com.google.android.exoplayer2.upstream;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.android.exoplayer2.upstream.HttpDataSource.HttpDataSourceException;
import com.google.android.exoplayer2.upstream.HttpDataSource.InvalidContentTypeException;
import com.google.android.exoplayer2.upstream.HttpDataSource.InvalidResponseCodeException;
import com.google.android.exoplayer2.upstream.HttpDataSource.RequestProperties;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Predicate;
import com.google.android.exoplayer2.util.Util;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultHttpDataSource extends BaseDataSource implements HttpDataSource {
    private static final Pattern CONTENT_RANGE_HEADER = Pattern.compile("^bytes (\\d+)-(\\d+)/(\\d+)$");
    public static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 8000;
    public static final int DEFAULT_READ_TIMEOUT_MILLIS = 8000;
    private static final int HTTP_STATUS_PERMANENT_REDIRECT = 308;
    private static final int HTTP_STATUS_TEMPORARY_REDIRECT = 307;
    private static final long MAX_BYTES_TO_DRAIN = 2048;
    private static final int MAX_REDIRECTS = 20;
    private static final String TAG = "DefaultHttpDataSource";
    private static final AtomicReference<byte[]> skipBufferReference = new AtomicReference();
    private final boolean allowCrossProtocolRedirects;
    private long bytesRead;
    private long bytesSkipped;
    private long bytesToRead;
    private long bytesToSkip;
    private final int connectTimeoutMillis;
    @Nullable
    private HttpURLConnection connection;
    @Nullable
    private final Predicate<String> contentTypePredicate;
    @Nullable
    private DataSpec dataSpec;
    @Nullable
    private final RequestProperties defaultRequestProperties;
    @Nullable
    private InputStream inputStream;
    private boolean opened;
    private final int readTimeoutMillis;
    private final RequestProperties requestProperties;
    private final String userAgent;

    private java.net.HttpURLConnection makeConnection(com.google.android.exoplayer2.upstream.DataSpec r27) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:35:0x00b7 in {3, 11, 21, 27, 28, 30, 31, 32, 34} preds:[]
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
        r26 = this;
        r0 = r27;
        r2 = new java.net.URL;
        r1 = r0.uri;
        r1 = r1.toString();
        r2.<init>(r1);
        r11 = r0.httpMethod;
        r12 = r0.httpBody;
        r14 = r0.position;
        r9 = r0.length;
        r1 = 1;
        r23 = r0.isFlagSet(r1);
        r13 = r26;
        r3 = r13.allowCrossProtocolRedirects;
        if (r3 != 0) goto L_0x0033;
    L_0x0020:
        r16 = 1;
        r1 = r26;
        r3 = r11;
        r4 = r12;
        r5 = r14;
        r7 = r9;
        r24 = r9;
        r9 = r23;
        r10 = r16;
        r1 = r1.makeConnection(r2, r3, r4, r5, r7, r9, r10);
        return r1;
    L_0x0033:
        r24 = r9;
        r3 = 0;
    L_0x0036:
        r4 = r3 + 1;
        r5 = 20;
        if (r3 > r5) goto L_0x009f;
    L_0x003c:
        r22 = 0;
        r13 = r26;
        r5 = r14;
        r14 = r2;
        r15 = r11;
        r16 = r12;
        r17 = r5;
        r19 = r24;
        r21 = r23;
        r3 = r13.makeConnection(r14, r15, r16, r17, r19, r21, r22);
        r7 = r3.getResponseCode();
        r8 = "Location";
        r8 = r3.getHeaderField(r8);
        r9 = 303; // 0x12f float:4.25E-43 double:1.497E-321;
        r10 = 302; // 0x12e float:4.23E-43 double:1.49E-321;
        r13 = 301; // 0x12d float:4.22E-43 double:1.487E-321;
        r14 = 300; // 0x12c float:4.2E-43 double:1.48E-321;
        if (r11 == r1) goto L_0x0068;
    L_0x0063:
        r15 = 3;
        if (r11 != r15) goto L_0x0067;
    L_0x0066:
        goto L_0x0068;
    L_0x0067:
        goto L_0x0079;
    L_0x0068:
        if (r7 == r14) goto L_0x0092;
    L_0x006a:
        if (r7 == r13) goto L_0x0092;
    L_0x006c:
        if (r7 == r10) goto L_0x0092;
    L_0x006e:
        if (r7 == r9) goto L_0x0092;
    L_0x0070:
        r15 = 307; // 0x133 float:4.3E-43 double:1.517E-321;
        if (r7 == r15) goto L_0x0092;
    L_0x0074:
        r15 = 308; // 0x134 float:4.32E-43 double:1.52E-321;
        if (r7 != r15) goto L_0x0067;
    L_0x0078:
        goto L_0x0092;
    L_0x0079:
        r15 = 2;
        if (r11 != r15) goto L_0x0090;
    L_0x007c:
        if (r7 == r14) goto L_0x0084;
    L_0x007e:
        if (r7 == r13) goto L_0x0084;
    L_0x0080:
        if (r7 == r10) goto L_0x0084;
    L_0x0082:
        if (r7 != r9) goto L_0x0090;
    L_0x0084:
        r3.disconnect();
        r9 = 1;
        r10 = 0;
        r2 = handleRedirect(r2, r8);
        r11 = r9;
        r12 = r10;
        goto L_0x009a;
        return r3;
        r3.disconnect();
        r2 = handleRedirect(r2, r8);
    L_0x009a:
        r13 = r26;
        r3 = r4;
        r14 = r5;
        goto L_0x0036;
    L_0x009f:
        r5 = r14;
        r1 = new java.net.NoRouteToHostException;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r7 = "Too many redirects: ";
        r3.append(r7);
        r3.append(r4);
        r3 = r3.toString();
        r1.<init>(r3);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.upstream.DefaultHttpDataSource.makeConnection(com.google.android.exoplayer2.upstream.DataSpec):java.net.HttpURLConnection");
    }

    public DefaultHttpDataSource(String userAgent, @Nullable Predicate<String> contentTypePredicate) {
        this(userAgent, contentTypePredicate, 8000, 8000);
    }

    public DefaultHttpDataSource(String userAgent, @Nullable Predicate<String> contentTypePredicate, int connectTimeoutMillis, int readTimeoutMillis) {
        this(userAgent, contentTypePredicate, connectTimeoutMillis, readTimeoutMillis, false, null);
    }

    public DefaultHttpDataSource(String userAgent, @Nullable Predicate<String> contentTypePredicate, int connectTimeoutMillis, int readTimeoutMillis, boolean allowCrossProtocolRedirects, @Nullable RequestProperties defaultRequestProperties) {
        super(true);
        this.userAgent = Assertions.checkNotEmpty(userAgent);
        this.contentTypePredicate = contentTypePredicate;
        this.requestProperties = new RequestProperties();
        this.connectTimeoutMillis = connectTimeoutMillis;
        this.readTimeoutMillis = readTimeoutMillis;
        this.allowCrossProtocolRedirects = allowCrossProtocolRedirects;
        this.defaultRequestProperties = defaultRequestProperties;
    }

    @Deprecated
    public DefaultHttpDataSource(String userAgent, @Nullable Predicate<String> contentTypePredicate, @Nullable TransferListener listener) {
        this(userAgent, contentTypePredicate, listener, 8000, 8000);
    }

    @Deprecated
    public DefaultHttpDataSource(String userAgent, @Nullable Predicate<String> contentTypePredicate, @Nullable TransferListener listener, int connectTimeoutMillis, int readTimeoutMillis) {
        this(userAgent, contentTypePredicate, listener, connectTimeoutMillis, readTimeoutMillis, false, null);
    }

    @Deprecated
    public DefaultHttpDataSource(String userAgent, @Nullable Predicate<String> contentTypePredicate, @Nullable TransferListener listener, int connectTimeoutMillis, int readTimeoutMillis, boolean allowCrossProtocolRedirects, @Nullable RequestProperties defaultRequestProperties) {
        this(userAgent, contentTypePredicate, connectTimeoutMillis, readTimeoutMillis, allowCrossProtocolRedirects, defaultRequestProperties);
        if (listener != null) {
            addTransferListener(listener);
        }
    }

    @Nullable
    public Uri getUri() {
        HttpURLConnection httpURLConnection = this.connection;
        return httpURLConnection == null ? null : Uri.parse(httpURLConnection.getURL().toString());
    }

    public Map<String, List<String>> getResponseHeaders() {
        HttpURLConnection httpURLConnection = this.connection;
        return httpURLConnection == null ? Collections.emptyMap() : httpURLConnection.getHeaderFields();
    }

    public void setRequestProperty(String name, String value) {
        Assertions.checkNotNull(name);
        Assertions.checkNotNull(value);
        this.requestProperties.set(name, value);
    }

    public void clearRequestProperty(String name) {
        Assertions.checkNotNull(name);
        this.requestProperties.remove(name);
    }

    public void clearAllRequestProperties() {
        this.requestProperties.clear();
    }

    public long open(DataSpec dataSpec) throws HttpDataSourceException {
        StringBuilder stringBuilder;
        this.dataSpec = dataSpec;
        long j = 0;
        this.bytesRead = 0;
        this.bytesSkipped = 0;
        transferInitializing(dataSpec);
        try {
            this.connection = makeConnection(dataSpec);
            try {
                int responseCode = this.connection.getResponseCode();
                String responseMessage = this.connection.getResponseMessage();
                if (responseCode >= 200) {
                    if (responseCode <= 299) {
                        String contentType = this.connection.getContentType();
                        Predicate predicate = this.contentTypePredicate;
                        if (predicate != null) {
                            if (!predicate.evaluate(contentType)) {
                                closeConnectionQuietly();
                                throw new InvalidContentTypeException(contentType, dataSpec);
                            }
                        }
                        if (responseCode == 200 && dataSpec.position != 0) {
                            j = dataSpec.position;
                        }
                        this.bytesToSkip = j;
                        if (dataSpec.isFlagSet(1)) {
                            this.bytesToRead = dataSpec.length;
                        } else {
                            long j2 = -1;
                            if (dataSpec.length != -1) {
                                this.bytesToRead = dataSpec.length;
                            } else {
                                j = getContentLength(this.connection);
                                if (j != -1) {
                                    j2 = j - this.bytesToSkip;
                                }
                                this.bytesToRead = j2;
                            }
                        }
                        try {
                            this.inputStream = this.connection.getInputStream();
                            this.opened = true;
                            transferStarted(dataSpec);
                            return this.bytesToRead;
                        } catch (IOException e) {
                            closeConnectionQuietly();
                            throw new HttpDataSourceException(e, dataSpec, 1);
                        }
                    }
                }
                Map<String, List<String>> headers = this.connection.getHeaderFields();
                closeConnectionQuietly();
                InvalidResponseCodeException exception = new InvalidResponseCodeException(responseCode, responseMessage, headers, dataSpec);
                if (responseCode == 416) {
                    exception.initCause(new DataSourceException(0));
                }
                throw exception;
            } catch (IOException e2) {
                closeConnectionQuietly();
                stringBuilder = new StringBuilder();
                stringBuilder.append("Unable to connect to ");
                stringBuilder.append(dataSpec.uri.toString());
                throw new HttpDataSourceException(stringBuilder.toString(), e2, dataSpec, 1);
            }
        } catch (IOException e22) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("Unable to connect to ");
            stringBuilder.append(dataSpec.uri.toString());
            throw new HttpDataSourceException(stringBuilder.toString(), e22, dataSpec, 1);
        }
    }

    public int read(byte[] buffer, int offset, int readLength) throws HttpDataSourceException {
        try {
            skipInternal();
            return readInternal(buffer, offset, readLength);
        } catch (IOException e) {
            throw new HttpDataSourceException(e, this.dataSpec, 2);
        }
    }

    public void close() throws HttpDataSourceException {
        try {
            if (this.inputStream != null) {
                maybeTerminateInputStream(this.connection, bytesRemaining());
                this.inputStream.close();
            }
            this.inputStream = null;
            closeConnectionQuietly();
            if (this.opened) {
                this.opened = false;
                transferEnded();
            }
        } catch (IOException e) {
            throw new HttpDataSourceException(e, this.dataSpec, 3);
        } catch (Throwable th) {
            this.inputStream = null;
            closeConnectionQuietly();
            if (this.opened) {
                this.opened = false;
                transferEnded();
            }
        }
    }

    @Nullable
    protected final HttpURLConnection getConnection() {
        return this.connection;
    }

    protected final long bytesSkipped() {
        return this.bytesSkipped;
    }

    protected final long bytesRead() {
        return this.bytesRead;
    }

    protected final long bytesRemaining() {
        long j = this.bytesToRead;
        return j == -1 ? j : j - this.bytesRead;
    }

    private HttpURLConnection makeConnection(URL url, int httpMethod, byte[] httpBody, long position, long length, boolean allowGzip, boolean followRedirects) throws IOException {
        byte[] bArr = httpBody;
        long j = position;
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(this.connectTimeoutMillis);
        connection.setReadTimeout(this.readTimeoutMillis);
        RequestProperties requestProperties = this.defaultRequestProperties;
        if (requestProperties != null) {
            for (Entry<String, String> property : requestProperties.getSnapshot().entrySet()) {
                connection.setRequestProperty((String) property.getKey(), (String) property.getValue());
            }
        }
        for (Entry<String, String> property2 : r0.requestProperties.getSnapshot().entrySet()) {
            connection.setRequestProperty((String) property2.getKey(), (String) property2.getValue());
        }
        if (j == 0) {
            if (length == -1) {
                connection.setRequestProperty("User-Agent", r0.userAgent);
                if (!allowGzip) {
                    connection.setRequestProperty("Accept-Encoding", "identity");
                }
                connection.setInstanceFollowRedirects(followRedirects);
                connection.setDoOutput(bArr == null);
                connection.setRequestMethod(DataSpec.getStringForHttpMethod(httpMethod));
                if (bArr == null) {
                    connection.setFixedLengthStreamingMode(bArr.length);
                    connection.connect();
                    OutputStream os = connection.getOutputStream();
                    os.write(httpBody);
                    os.close();
                } else {
                    connection.connect();
                }
                return connection;
            }
        }
        String rangeRequest = new StringBuilder();
        rangeRequest.append("bytes=");
        rangeRequest.append(j);
        rangeRequest.append("-");
        rangeRequest = rangeRequest.toString();
        if (length != -1) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(rangeRequest);
            stringBuilder.append((j + length) - 1);
            rangeRequest = stringBuilder.toString();
        }
        connection.setRequestProperty("Range", rangeRequest);
        connection.setRequestProperty("User-Agent", r0.userAgent);
        if (!allowGzip) {
            connection.setRequestProperty("Accept-Encoding", "identity");
        }
        connection.setInstanceFollowRedirects(followRedirects);
        if (bArr == null) {
        }
        connection.setDoOutput(bArr == null);
        connection.setRequestMethod(DataSpec.getStringForHttpMethod(httpMethod));
        if (bArr == null) {
            connection.connect();
        } else {
            connection.setFixedLengthStreamingMode(bArr.length);
            connection.connect();
            OutputStream os2 = connection.getOutputStream();
            os2.write(httpBody);
            os2.close();
        }
        return connection;
    }

    private static URL handleRedirect(URL originalUrl, String location) throws IOException {
        if (location != null) {
            URL url = new URL(originalUrl, location);
            String protocol = url.getProtocol();
            if (!"https".equals(protocol)) {
                if (!"http".equals(protocol)) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Unsupported protocol redirect: ");
                    stringBuilder.append(protocol);
                    throw new ProtocolException(stringBuilder.toString());
                }
            }
            return url;
        }
        throw new ProtocolException("Null location redirect");
    }

    private static long getContentLength(HttpURLConnection connection) {
        long contentLength = -1;
        String contentLengthHeader = connection.getHeaderField("Content-Length");
        if (!TextUtils.isEmpty(contentLengthHeader)) {
            try {
                contentLength = Long.parseLong(contentLengthHeader);
            } catch (NumberFormatException e) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unexpected Content-Length [");
                stringBuilder.append(contentLengthHeader);
                stringBuilder.append("]");
                Log.m6e(str, stringBuilder.toString());
            }
        }
        String contentRangeHeader = connection.getHeaderField("Content-Range");
        if (!TextUtils.isEmpty(contentRangeHeader)) {
            Matcher matcher = CONTENT_RANGE_HEADER.matcher(contentRangeHeader);
            if (matcher.find()) {
                try {
                    long contentLengthFromRange = (Long.parseLong(matcher.group(2)) - Long.parseLong(matcher.group(1))) + 1;
                    if (contentLength < 0) {
                        contentLength = contentLengthFromRange;
                    } else if (contentLength != contentLengthFromRange) {
                        String str2 = TAG;
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("Inconsistent headers [");
                        stringBuilder2.append(contentLengthHeader);
                        stringBuilder2.append("] [");
                        stringBuilder2.append(contentRangeHeader);
                        stringBuilder2.append("]");
                        Log.m10w(str2, stringBuilder2.toString());
                        contentLength = Math.max(contentLength, contentLengthFromRange);
                    }
                } catch (NumberFormatException e2) {
                    String str3 = TAG;
                    StringBuilder stringBuilder3 = new StringBuilder();
                    stringBuilder3.append("Unexpected Content-Range [");
                    stringBuilder3.append(contentRangeHeader);
                    stringBuilder3.append("]");
                    Log.m6e(str3, stringBuilder3.toString());
                }
            }
        }
        return contentLength;
    }

    private void skipInternal() throws IOException {
        if (this.bytesSkipped != this.bytesToSkip) {
            byte[] skipBuffer = (byte[]) skipBufferReference.getAndSet(null);
            if (skipBuffer == null) {
                skipBuffer = new byte[4096];
            }
            while (true) {
                long j = this.bytesSkipped;
                long j2 = this.bytesToSkip;
                if (j != j2) {
                    int read = this.inputStream.read(skipBuffer, 0, (int) Math.min(j2 - j, (long) skipBuffer.length));
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedIOException();
                    } else if (read != -1) {
                        this.bytesSkipped += (long) read;
                        bytesTransferred(read);
                    } else {
                        throw new EOFException();
                    }
                }
                skipBufferReference.set(skipBuffer);
                return;
            }
        }
    }

    private int readInternal(byte[] buffer, int offset, int readLength) throws IOException {
        if (readLength == 0) {
            return 0;
        }
        long bytesRemaining = this.bytesToRead;
        if (bytesRemaining != -1) {
            bytesRemaining -= this.bytesRead;
            if (bytesRemaining == 0) {
                return -1;
            }
            readLength = (int) Math.min((long) readLength, bytesRemaining);
        }
        int read = this.inputStream.read(buffer, offset, readLength);
        if (read != -1) {
            this.bytesRead += (long) read;
            bytesTransferred(read);
            return read;
        } else if (this.bytesToRead == -1) {
            return -1;
        } else {
            throw new EOFException();
        }
    }

    private static void maybeTerminateInputStream(HttpURLConnection connection, long bytesRemaining) {
        if (Util.SDK_INT == 19 || Util.SDK_INT == 20) {
            try {
                InputStream inputStream = connection.getInputStream();
                if (bytesRemaining == -1) {
                    if (inputStream.read() == -1) {
                        return;
                    }
                } else if (bytesRemaining <= 2048) {
                    return;
                }
                String className = inputStream.getClass().getName();
                if (!"com.android.okhttp.internal.http.HttpTransport$ChunkedInputStream".equals(className)) {
                    if (!"com.android.okhttp.internal.http.HttpTransport$FixedLengthInputStream".equals(className)) {
                    }
                }
                Method unexpectedEndOfInput = inputStream.getClass().getSuperclass().getDeclaredMethod("unexpectedEndOfInput", new Class[0]);
                unexpectedEndOfInput.setAccessible(true);
                unexpectedEndOfInput.invoke(inputStream, new Object[0]);
            } catch (Exception e) {
            }
        }
    }

    private void closeConnectionQuietly() {
        HttpURLConnection httpURLConnection = this.connection;
        if (httpURLConnection != null) {
            try {
                httpURLConnection.disconnect();
            } catch (Exception e) {
                Log.m7e(TAG, "Unexpected error while disconnecting", e);
            }
            this.connection = null;
        }
    }
}
