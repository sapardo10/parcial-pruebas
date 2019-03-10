package okhttp3.internal.http2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import okhttp3.Headers;
import okhttp3.Interceptor$Chain;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Response.Builder;
import okhttp3.ResponseBody;
import okhttp3.internal.Internal;
import okhttp3.internal.Util;
import okhttp3.internal.connection.StreamAllocation;
import okhttp3.internal.http.HttpCodec;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.http.RealResponseBody;
import okhttp3.internal.http.RequestLine;
import okio.Buffer;
import okio.ByteString;
import okio.ForwardingSource;
import okio.Okio;
import okio.Sink;
import okio.Source;

public final class Http2Codec implements HttpCodec {
    private static final ByteString CONNECTION = ByteString.encodeUtf8("connection");
    private static final ByteString ENCODING = ByteString.encodeUtf8("encoding");
    private static final ByteString HOST = ByteString.encodeUtf8("host");
    private static final List<ByteString> HTTP_2_SKIPPED_REQUEST_HEADERS = Util.immutableList(new ByteString[]{CONNECTION, HOST, KEEP_ALIVE, PROXY_CONNECTION, TE, TRANSFER_ENCODING, ENCODING, UPGRADE, Header.TARGET_METHOD, Header.TARGET_PATH, Header.TARGET_SCHEME, Header.TARGET_AUTHORITY});
    private static final List<ByteString> HTTP_2_SKIPPED_RESPONSE_HEADERS = Util.immutableList(new ByteString[]{CONNECTION, HOST, KEEP_ALIVE, PROXY_CONNECTION, TE, TRANSFER_ENCODING, ENCODING, UPGRADE});
    private static final ByteString KEEP_ALIVE = ByteString.encodeUtf8("keep-alive");
    private static final ByteString PROXY_CONNECTION = ByteString.encodeUtf8("proxy-connection");
    private static final ByteString TE = ByteString.encodeUtf8("te");
    private static final ByteString TRANSFER_ENCODING = ByteString.encodeUtf8("transfer-encoding");
    private static final ByteString UPGRADE = ByteString.encodeUtf8("upgrade");
    private final Interceptor$Chain chain;
    private final OkHttpClient client;
    private final Http2Connection connection;
    private Http2Stream stream;
    final StreamAllocation streamAllocation;

    class StreamFinishingSource extends ForwardingSource {
        long bytesRead = 0;
        boolean completed = false;

        StreamFinishingSource(Source delegate) {
            super(delegate);
        }

        public long read(Buffer sink, long byteCount) throws IOException {
            try {
                long read = delegate().read(sink, byteCount);
                if (read > 0) {
                    this.bytesRead += read;
                }
                return read;
            } catch (IOException e) {
                endOfInput(e);
                throw e;
            }
        }

        public void close() throws IOException {
            super.close();
            endOfInput(null);
        }

        private void endOfInput(IOException e) {
            if (!this.completed) {
                this.completed = true;
                Http2Codec.this.streamAllocation.streamFinished(false, Http2Codec.this, this.bytesRead, e);
            }
        }
    }

    public static okhttp3.Response.Builder readHttp2HeadersList(java.util.List<okhttp3.internal.http2.Header> r9) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:22:0x008c in {7, 8, 11, 14, 15, 16, 19, 21} preds:[]
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
        r0 = 0;
        r1 = new okhttp3.Headers$Builder;
        r1.<init>();
        r2 = 0;
        r3 = r9.size();
    L_0x000b:
        if (r2 >= r3) goto L_0x0062;
    L_0x000d:
        r4 = r9.get(r2);
        r4 = (okhttp3.internal.http2.Header) r4;
        if (r4 != 0) goto L_0x0026;
    L_0x0015:
        if (r0 == 0) goto L_0x0025;
    L_0x0017:
        r5 = r0.code;
        r6 = 100;
        if (r5 != r6) goto L_0x0025;
    L_0x001d:
        r0 = 0;
        r5 = new okhttp3.Headers$Builder;
        r5.<init>();
        r1 = r5;
        goto L_0x005f;
    L_0x0025:
        goto L_0x005f;
    L_0x0026:
        r5 = r4.name;
        r6 = r4.value;
        r6 = r6.utf8();
        r7 = okhttp3.internal.http2.Header.RESPONSE_STATUS;
        r7 = r5.equals(r7);
        if (r7 == 0) goto L_0x004c;
    L_0x0036:
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r8 = "HTTP/1.1 ";
        r7.append(r8);
        r7.append(r6);
        r7 = r7.toString();
        r0 = okhttp3.internal.http.StatusLine.parse(r7);
        goto L_0x005f;
    L_0x004c:
        r7 = HTTP_2_SKIPPED_RESPONSE_HEADERS;
        r7 = r7.contains(r5);
        if (r7 != 0) goto L_0x005e;
    L_0x0054:
        r7 = okhttp3.internal.Internal.instance;
        r8 = r5.utf8();
        r7.addLenient(r1, r8, r6);
        goto L_0x005f;
    L_0x005f:
        r2 = r2 + 1;
        goto L_0x000b;
    L_0x0062:
        if (r0 == 0) goto L_0x0084;
    L_0x0064:
        r2 = new okhttp3.Response$Builder;
        r2.<init>();
        r3 = okhttp3.Protocol.HTTP_2;
        r2 = r2.protocol(r3);
        r3 = r0.code;
        r2 = r2.code(r3);
        r3 = r0.message;
        r2 = r2.message(r3);
        r3 = r1.build();
        r2 = r2.headers(r3);
        return r2;
    L_0x0084:
        r2 = new java.net.ProtocolException;
        r3 = "Expected ':status' header not present";
        r2.<init>(r3);
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http2.Http2Codec.readHttp2HeadersList(java.util.List):okhttp3.Response$Builder");
    }

    public Http2Codec(OkHttpClient client, Interceptor$Chain chain, StreamAllocation streamAllocation, Http2Connection connection) {
        this.client = client;
        this.chain = chain;
        this.streamAllocation = streamAllocation;
        this.connection = connection;
    }

    public Sink createRequestBody(Request request, long contentLength) {
        return this.stream.getSink();
    }

    public void writeRequestHeaders(Request request) throws IOException {
        if (this.stream == null) {
            this.stream = this.connection.newStream(http2HeadersList(request), request.body() != null);
            this.stream.readTimeout().timeout((long) this.chain.readTimeoutMillis(), TimeUnit.MILLISECONDS);
            this.stream.writeTimeout().timeout((long) this.chain.writeTimeoutMillis(), TimeUnit.MILLISECONDS);
        }
    }

    public void flushRequest() throws IOException {
        this.connection.flush();
    }

    public void finishRequest() throws IOException {
        this.stream.getSink().close();
    }

    public Builder readResponseHeaders(boolean expectContinue) throws IOException {
        Builder responseBuilder = readHttp2HeadersList(this.stream.takeResponseHeaders());
        if (expectContinue && Internal.instance.code(responseBuilder) == 100) {
            return null;
        }
        return responseBuilder;
    }

    public static List<Header> http2HeadersList(Request request) {
        Headers headers = request.headers();
        List<Header> result = new ArrayList(headers.size() + 4);
        result.add(new Header(Header.TARGET_METHOD, request.method()));
        result.add(new Header(Header.TARGET_PATH, RequestLine.requestPath(request.url())));
        String host = request.header("Host");
        if (host != null) {
            result.add(new Header(Header.TARGET_AUTHORITY, host));
        }
        result.add(new Header(Header.TARGET_SCHEME, request.url().scheme()));
        int size = headers.size();
        for (int i = 0; i < size; i++) {
            ByteString name = ByteString.encodeUtf8(headers.name(i).toLowerCase(Locale.US));
            if (!HTTP_2_SKIPPED_REQUEST_HEADERS.contains(name)) {
                result.add(new Header(name, headers.value(i)));
            }
        }
        return result;
    }

    public ResponseBody openResponseBody(Response response) throws IOException {
        this.streamAllocation.eventListener.responseBodyStart(this.streamAllocation.call);
        return new RealResponseBody(response.header("Content-Type"), HttpHeaders.contentLength(response), Okio.buffer(new StreamFinishingSource(this.stream.getSource())));
    }

    public void cancel() {
        Http2Stream http2Stream = this.stream;
        if (http2Stream != null) {
            http2Stream.closeLater(ErrorCode.CANCEL);
        }
    }
}
