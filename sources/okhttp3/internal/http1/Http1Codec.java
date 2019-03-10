package okhttp3.internal.http1;

import android.support.v4.media.session.PlaybackStateCompat;
import java.io.EOFException;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.concurrent.TimeUnit;
import okhttp3.Headers;
import okhttp3.Headers$Builder;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Response.Builder;
import okhttp3.ResponseBody;
import okhttp3.internal.Internal;
import okhttp3.internal.Util;
import okhttp3.internal.connection.RealConnection;
import okhttp3.internal.connection.StreamAllocation;
import okhttp3.internal.http.HttpCodec;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.http.RealResponseBody;
import okhttp3.internal.http.RequestLine;
import okhttp3.internal.http.StatusLine;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ForwardingTimeout;
import okio.Okio;
import okio.Sink;
import okio.Source;
import okio.Timeout;
import org.apache.commons.io.IOUtils;

public final class Http1Codec implements HttpCodec {
    private static final int HEADER_LIMIT = 262144;
    private static final int STATE_CLOSED = 6;
    private static final int STATE_IDLE = 0;
    private static final int STATE_OPEN_REQUEST_BODY = 1;
    private static final int STATE_OPEN_RESPONSE_BODY = 4;
    private static final int STATE_READING_RESPONSE_BODY = 5;
    private static final int STATE_READ_RESPONSE_HEADERS = 3;
    private static final int STATE_WRITING_REQUEST_BODY = 2;
    final OkHttpClient client;
    private long headerLimit = PlaybackStateCompat.ACTION_SET_REPEAT_MODE;
    final BufferedSink sink;
    final BufferedSource source;
    int state = 0;
    final StreamAllocation streamAllocation;

    private abstract class AbstractSource implements Source {
        protected long bytesRead;
        protected boolean closed;
        protected final ForwardingTimeout timeout;

        private AbstractSource() {
            this.timeout = new ForwardingTimeout(Http1Codec.this.source.timeout());
            this.bytesRead = 0;
        }

        public Timeout timeout() {
            return this.timeout;
        }

        public long read(Buffer sink, long byteCount) throws IOException {
            try {
                long read = Http1Codec.this.source.read(sink, byteCount);
                if (read > 0) {
                    this.bytesRead += read;
                }
                return read;
            } catch (IOException e) {
                endOfInput(false, e);
                throw e;
            }
        }

        protected final void endOfInput(boolean reuseConnection, IOException e) throws IOException {
            if (Http1Codec.this.state != 6) {
                if (Http1Codec.this.state == 5) {
                    Http1Codec.this.detachTimeout(this.timeout);
                    Http1Codec http1Codec = Http1Codec.this;
                    http1Codec.state = 6;
                    if (http1Codec.streamAllocation != null) {
                        Http1Codec.this.streamAllocation.streamFinished(reuseConnection ^ 1, Http1Codec.this, this.bytesRead, e);
                    }
                    return;
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("state: ");
                stringBuilder.append(Http1Codec.this.state);
                throw new IllegalStateException(stringBuilder.toString());
            }
        }
    }

    private final class ChunkedSink implements Sink {
        private boolean closed;
        private final ForwardingTimeout timeout = new ForwardingTimeout(Http1Codec.this.sink.timeout());

        ChunkedSink() {
        }

        public Timeout timeout() {
            return this.timeout;
        }

        public void write(Buffer source, long byteCount) throws IOException {
            if (this.closed) {
                throw new IllegalStateException("closed");
            } else if (byteCount != 0) {
                Http1Codec.this.sink.writeHexadecimalUnsignedLong(byteCount);
                Http1Codec.this.sink.writeUtf8(IOUtils.LINE_SEPARATOR_WINDOWS);
                Http1Codec.this.sink.write(source, byteCount);
                Http1Codec.this.sink.writeUtf8(IOUtils.LINE_SEPARATOR_WINDOWS);
            }
        }

        public synchronized void flush() throws IOException {
            if (!this.closed) {
                Http1Codec.this.sink.flush();
            }
        }

        public synchronized void close() throws IOException {
            if (!this.closed) {
                this.closed = true;
                Http1Codec.this.sink.writeUtf8("0\r\n\r\n");
                Http1Codec.this.detachTimeout(this.timeout);
                Http1Codec.this.state = 3;
            }
        }
    }

    private final class FixedLengthSink implements Sink {
        private long bytesRemaining;
        private boolean closed;
        private final ForwardingTimeout timeout = new ForwardingTimeout(Http1Codec.this.sink.timeout());

        FixedLengthSink(long bytesRemaining) {
            this.bytesRemaining = bytesRemaining;
        }

        public Timeout timeout() {
            return this.timeout;
        }

        public void write(Buffer source, long byteCount) throws IOException {
            if (this.closed) {
                throw new IllegalStateException("closed");
            }
            Util.checkOffsetAndCount(source.size(), 0, byteCount);
            if (byteCount <= this.bytesRemaining) {
                Http1Codec.this.sink.write(source, byteCount);
                this.bytesRemaining -= byteCount;
                return;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("expected ");
            stringBuilder.append(this.bytesRemaining);
            stringBuilder.append(" bytes but received ");
            stringBuilder.append(byteCount);
            throw new ProtocolException(stringBuilder.toString());
        }

        public void flush() throws IOException {
            if (!this.closed) {
                Http1Codec.this.sink.flush();
            }
        }

        public void close() throws IOException {
            if (!this.closed) {
                this.closed = true;
                if (this.bytesRemaining <= 0) {
                    Http1Codec.this.detachTimeout(this.timeout);
                    Http1Codec.this.state = 3;
                    return;
                }
                throw new ProtocolException("unexpected end of stream");
            }
        }
    }

    private class ChunkedSource extends AbstractSource {
        private static final long NO_CHUNK_YET = -1;
        private long bytesRemainingInChunk = -1;
        private boolean hasMoreChunks = true;
        private final HttpUrl url;

        ChunkedSource(HttpUrl url) {
            super();
            this.url = url;
        }

        public long read(Buffer sink, long byteCount) throws IOException {
            if (byteCount < 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("byteCount < 0: ");
                stringBuilder.append(byteCount);
                throw new IllegalArgumentException(stringBuilder.toString());
            } else if (this.closed) {
                throw new IllegalStateException("closed");
            } else if (!this.hasMoreChunks) {
                return -1;
            } else {
                long read;
                ProtocolException e;
                long j = this.bytesRemainingInChunk;
                if (j != 0) {
                    if (j != -1) {
                        read = super.read(sink, Math.min(byteCount, this.bytesRemainingInChunk));
                        if (read == -1) {
                            this.bytesRemainingInChunk -= read;
                            return read;
                        }
                        e = new ProtocolException("unexpected end of stream");
                        endOfInput(false, e);
                        throw e;
                    }
                }
                readChunkSize();
                if (!this.hasMoreChunks) {
                    return -1;
                }
                read = super.read(sink, Math.min(byteCount, this.bytesRemainingInChunk));
                if (read == -1) {
                    e = new ProtocolException("unexpected end of stream");
                    endOfInput(false, e);
                    throw e;
                }
                this.bytesRemainingInChunk -= read;
                return read;
            }
        }

        private void readChunkSize() throws IOException {
            if (this.bytesRemainingInChunk != -1) {
                Http1Codec.this.source.readUtf8LineStrict();
            }
            try {
                this.bytesRemainingInChunk = Http1Codec.this.source.readHexadecimalUnsignedLong();
                String extensions = Http1Codec.this.source.readUtf8LineStrict().trim();
                if (this.bytesRemainingInChunk < 0 || !(extensions.isEmpty() || extensions.startsWith(";"))) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("expected chunk size and optional extensions but was \"");
                    stringBuilder.append(this.bytesRemainingInChunk);
                    stringBuilder.append(extensions);
                    stringBuilder.append("\"");
                    throw new ProtocolException(stringBuilder.toString());
                } else if (this.bytesRemainingInChunk == 0) {
                    this.hasMoreChunks = false;
                    HttpHeaders.receiveHeaders(Http1Codec.this.client.cookieJar(), this.url, Http1Codec.this.readHeaders());
                    endOfInput(true, null);
                }
            } catch (NumberFormatException e) {
                throw new ProtocolException(e.getMessage());
            }
        }

        public void close() throws IOException {
            if (!this.closed) {
                if (this.hasMoreChunks && !Util.discard(this, 100, TimeUnit.MILLISECONDS)) {
                    endOfInput(false, null);
                }
                this.closed = true;
            }
        }
    }

    private class FixedLengthSource extends AbstractSource {
        private long bytesRemaining;

        FixedLengthSource(long length) throws IOException {
            super();
            this.bytesRemaining = length;
            if (this.bytesRemaining == 0) {
                endOfInput(true, null);
            }
        }

        public long read(Buffer sink, long byteCount) throws IOException {
            if (byteCount < 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("byteCount < 0: ");
                stringBuilder.append(byteCount);
                throw new IllegalArgumentException(stringBuilder.toString());
            } else if (this.closed) {
                throw new IllegalStateException("closed");
            } else {
                long j = this.bytesRemaining;
                if (j == 0) {
                    return -1;
                }
                j = super.read(sink, Math.min(j, byteCount));
                if (j != -1) {
                    this.bytesRemaining -= j;
                    if (this.bytesRemaining == 0) {
                        endOfInput(true, null);
                    }
                    return j;
                }
                ProtocolException e = new ProtocolException("unexpected end of stream");
                endOfInput(false, e);
                throw e;
            }
        }

        public void close() throws IOException {
            if (!this.closed) {
                if (this.bytesRemaining != 0 && !Util.discard(this, 100, TimeUnit.MILLISECONDS)) {
                    endOfInput(false, null);
                }
                this.closed = true;
            }
        }
    }

    private class UnknownLengthSource extends AbstractSource {
        private boolean inputExhausted;

        UnknownLengthSource() {
            super();
        }

        public long read(Buffer sink, long byteCount) throws IOException {
            if (byteCount < 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("byteCount < 0: ");
                stringBuilder.append(byteCount);
                throw new IllegalArgumentException(stringBuilder.toString());
            } else if (this.closed) {
                throw new IllegalStateException("closed");
            } else if (this.inputExhausted) {
                return -1;
            } else {
                long read = super.read(sink, byteCount);
                if (read != -1) {
                    return read;
                }
                this.inputExhausted = true;
                endOfInput(true, null);
                return -1;
            }
        }

        public void close() throws IOException {
            if (!this.closed) {
                if (!this.inputExhausted) {
                    endOfInput(false, null);
                }
                this.closed = true;
            }
        }
    }

    public void writeRequest(okhttp3.Headers r5, java.lang.String r6) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:9:0x005a in {4, 6, 8} preds:[]
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
        r4 = this;
        r0 = r4.state;
        if (r0 != 0) goto L_0x0041;
    L_0x0004:
        r0 = r4.sink;
        r0 = r0.writeUtf8(r6);
        r1 = "\r\n";
        r0.writeUtf8(r1);
        r0 = 0;
        r1 = r5.size();
    L_0x0014:
        if (r0 >= r1) goto L_0x0036;
    L_0x0016:
        r2 = r4.sink;
        r3 = r5.name(r0);
        r2 = r2.writeUtf8(r3);
        r3 = ": ";
        r2 = r2.writeUtf8(r3);
        r3 = r5.value(r0);
        r2 = r2.writeUtf8(r3);
        r3 = "\r\n";
        r2.writeUtf8(r3);
        r0 = r0 + 1;
        goto L_0x0014;
    L_0x0036:
        r0 = r4.sink;
        r1 = "\r\n";
        r0.writeUtf8(r1);
        r0 = 1;
        r4.state = r0;
        return;
    L_0x0041:
        r0 = new java.lang.IllegalStateException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "state: ";
        r1.append(r2);
        r2 = r4.state;
        r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http1.Http1Codec.writeRequest(okhttp3.Headers, java.lang.String):void");
    }

    public Http1Codec(OkHttpClient client, StreamAllocation streamAllocation, BufferedSource source, BufferedSink sink) {
        this.client = client;
        this.streamAllocation = streamAllocation;
        this.source = source;
        this.sink = sink;
    }

    public Sink createRequestBody(Request request, long contentLength) {
        if ("chunked".equalsIgnoreCase(request.header("Transfer-Encoding"))) {
            return newChunkedSink();
        }
        if (contentLength != -1) {
            return newFixedLengthSink(contentLength);
        }
        throw new IllegalStateException("Cannot stream a request body without chunked encoding or a known content length!");
    }

    public void cancel() {
        RealConnection connection = this.streamAllocation.connection();
        if (connection != null) {
            connection.cancel();
        }
    }

    public void writeRequestHeaders(Request request) throws IOException {
        writeRequest(request.headers(), RequestLine.get(request, this.streamAllocation.connection().route().proxy().type()));
    }

    public ResponseBody openResponseBody(Response response) throws IOException {
        this.streamAllocation.eventListener.responseBodyStart(this.streamAllocation.call);
        String contentType = response.header("Content-Type");
        if (!HttpHeaders.hasBody(response)) {
            return new RealResponseBody(contentType, 0, Okio.buffer(newFixedLengthSource(0)));
        }
        if ("chunked".equalsIgnoreCase(response.header("Transfer-Encoding"))) {
            return new RealResponseBody(contentType, -1, Okio.buffer(newChunkedSource(response.request().url())));
        }
        long contentLength = HttpHeaders.contentLength(response);
        if (contentLength != -1) {
            return new RealResponseBody(contentType, contentLength, Okio.buffer(newFixedLengthSource(contentLength)));
        }
        return new RealResponseBody(contentType, -1, Okio.buffer(newUnknownLengthSource()));
    }

    public boolean isClosed() {
        return this.state == 6;
    }

    public void flushRequest() throws IOException {
        this.sink.flush();
    }

    public void finishRequest() throws IOException {
        this.sink.flush();
    }

    public Builder readResponseHeaders(boolean expectContinue) throws IOException {
        int i = this.state;
        if (i != 1) {
            if (i != 3) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("state: ");
                stringBuilder.append(this.state);
                throw new IllegalStateException(stringBuilder.toString());
            }
        }
        try {
            StatusLine statusLine = StatusLine.parse(readHeaderLine());
            Builder responseBuilder = new Builder().protocol(statusLine.protocol).code(statusLine.code).message(statusLine.message).headers(readHeaders());
            if (expectContinue && statusLine.code == 100) {
                return null;
            }
            if (statusLine.code == 100) {
                this.state = 3;
                return responseBuilder;
            }
            this.state = 4;
            return responseBuilder;
        } catch (EOFException e) {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("unexpected end of stream on ");
            stringBuilder2.append(this.streamAllocation);
            IOException exception = new IOException(stringBuilder2.toString());
            exception.initCause(e);
            throw exception;
        }
    }

    private String readHeaderLine() throws IOException {
        String line = this.source.readUtf8LineStrict(this.headerLimit);
        this.headerLimit -= (long) line.length();
        return line;
    }

    public Headers readHeaders() throws IOException {
        Headers$Builder headers = new Headers$Builder();
        while (true) {
            String readHeaderLine = readHeaderLine();
            String line = readHeaderLine;
            if (readHeaderLine.length() == 0) {
                return headers.build();
            }
            Internal.instance.addLenient(headers, line);
        }
    }

    public Sink newChunkedSink() {
        if (this.state == 1) {
            this.state = 2;
            return new ChunkedSink();
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("state: ");
        stringBuilder.append(this.state);
        throw new IllegalStateException(stringBuilder.toString());
    }

    public Sink newFixedLengthSink(long contentLength) {
        if (this.state == 1) {
            this.state = 2;
            return new FixedLengthSink(contentLength);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("state: ");
        stringBuilder.append(this.state);
        throw new IllegalStateException(stringBuilder.toString());
    }

    public Source newFixedLengthSource(long length) throws IOException {
        if (this.state == 4) {
            this.state = 5;
            return new FixedLengthSource(length);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("state: ");
        stringBuilder.append(this.state);
        throw new IllegalStateException(stringBuilder.toString());
    }

    public Source newChunkedSource(HttpUrl url) throws IOException {
        if (this.state == 4) {
            this.state = 5;
            return new ChunkedSource(url);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("state: ");
        stringBuilder.append(this.state);
        throw new IllegalStateException(stringBuilder.toString());
    }

    public Source newUnknownLengthSource() throws IOException {
        if (this.state == 4) {
            StreamAllocation streamAllocation = this.streamAllocation;
            if (streamAllocation != null) {
                this.state = 5;
                streamAllocation.noNewStreams();
                return new UnknownLengthSource();
            }
            throw new IllegalStateException("streamAllocation == null");
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("state: ");
        stringBuilder.append(this.state);
        throw new IllegalStateException(stringBuilder.toString());
    }

    void detachTimeout(ForwardingTimeout timeout) {
        Timeout oldDelegate = timeout.delegate();
        timeout.setDelegate(Timeout.NONE);
        oldDelegate.clearDeadline();
        oldDelegate.clearTimeout();
    }
}
