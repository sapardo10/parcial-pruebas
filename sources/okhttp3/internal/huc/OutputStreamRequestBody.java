package okhttp3.internal.huc;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Timeout;

abstract class OutputStreamRequestBody extends RequestBody {
    boolean closed;
    private long expectedContentLength;
    private OutputStream outputStream;
    private Timeout timeout;

    OutputStreamRequestBody() {
    }

    protected void initOutputStream(final BufferedSink sink, final long expectedContentLength) {
        this.timeout = sink.timeout();
        this.expectedContentLength = expectedContentLength;
        this.outputStream = new OutputStream() {
            private long bytesReceived;

            public void write(int b) throws IOException {
                write(new byte[]{(byte) b}, 0, 1);
            }

            public void write(byte[] source, int offset, int byteCount) throws IOException {
                if (OutputStreamRequestBody.this.closed) {
                    throw new IOException("closed");
                }
                long j = expectedContentLength;
                if (j != -1) {
                    if (this.bytesReceived + ((long) byteCount) > j) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("expected ");
                        stringBuilder.append(expectedContentLength);
                        stringBuilder.append(" bytes but received ");
                        stringBuilder.append(this.bytesReceived);
                        stringBuilder.append(byteCount);
                        throw new ProtocolException(stringBuilder.toString());
                    }
                }
                this.bytesReceived += (long) byteCount;
                try {
                    sink.write(source, offset, byteCount);
                } catch (InterruptedIOException e) {
                    throw new SocketTimeoutException(e.getMessage());
                }
            }

            public void flush() throws IOException {
                if (!OutputStreamRequestBody.this.closed) {
                    sink.flush();
                }
            }

            public void close() throws IOException {
                OutputStreamRequestBody.this.closed = true;
                long j = expectedContentLength;
                if (j != -1) {
                    if (this.bytesReceived < j) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("expected ");
                        stringBuilder.append(expectedContentLength);
                        stringBuilder.append(" bytes but received ");
                        stringBuilder.append(this.bytesReceived);
                        throw new ProtocolException(stringBuilder.toString());
                    }
                }
                sink.close();
            }
        };
    }

    public final OutputStream outputStream() {
        return this.outputStream;
    }

    public final Timeout timeout() {
        return this.timeout;
    }

    public final boolean isClosed() {
        return this.closed;
    }

    public long contentLength() throws IOException {
        return this.expectedContentLength;
    }

    public final MediaType contentType() {
        return null;
    }

    public Request prepareToSendRequest(Request request) throws IOException {
        return request;
    }
}
