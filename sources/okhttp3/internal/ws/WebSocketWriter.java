package okhttp3.internal.ws;

import android.support.v4.media.session.PlaybackStateCompat;
import java.io.IOException;
import java.util.Random;
import okio.Buffer;
import okio.Buffer$UnsafeCursor;
import okio.BufferedSink;
import okio.ByteString;
import okio.Sink;
import okio.Timeout;

final class WebSocketWriter {
    boolean activeWriter;
    final Buffer buffer = new Buffer();
    final FrameSink frameSink = new FrameSink();
    final boolean isClient;
    private final Buffer$UnsafeCursor maskCursor;
    private final byte[] maskKey;
    final Random random;
    final BufferedSink sink;
    final Buffer sinkBuffer;
    boolean writerClosed;

    final class FrameSink implements Sink {
        boolean closed;
        long contentLength;
        int formatOpcode;
        boolean isFirstFrame;

        FrameSink() {
        }

        public void write(Buffer source, long byteCount) throws IOException {
            if (this.closed) {
                throw new IOException("closed");
            }
            boolean deferWrite;
            long emitCount;
            WebSocketWriter.this.buffer.write(source, byteCount);
            if (this.isFirstFrame && this.contentLength != -1) {
                if (WebSocketWriter.this.buffer.size() > this.contentLength - PlaybackStateCompat.ACTION_PLAY_FROM_URI) {
                    deferWrite = true;
                    emitCount = WebSocketWriter.this.buffer.completeSegmentByteCount();
                    if (emitCount <= 0 && !deferWrite) {
                        WebSocketWriter.this.writeMessageFrame(this.formatOpcode, emitCount, this.isFirstFrame, false);
                        this.isFirstFrame = false;
                        return;
                    }
                }
            }
            deferWrite = false;
            emitCount = WebSocketWriter.this.buffer.completeSegmentByteCount();
            if (emitCount <= 0) {
            }
        }

        public void flush() throws IOException {
            if (this.closed) {
                throw new IOException("closed");
            }
            WebSocketWriter webSocketWriter = WebSocketWriter.this;
            webSocketWriter.writeMessageFrame(this.formatOpcode, webSocketWriter.buffer.size(), this.isFirstFrame, false);
            this.isFirstFrame = false;
        }

        public Timeout timeout() {
            return WebSocketWriter.this.sink.timeout();
        }

        public void close() throws IOException {
            if (this.closed) {
                throw new IOException("closed");
            }
            WebSocketWriter webSocketWriter = WebSocketWriter.this;
            webSocketWriter.writeMessageFrame(this.formatOpcode, webSocketWriter.buffer.size(), this.isFirstFrame, true);
            this.closed = true;
            WebSocketWriter.this.activeWriter = false;
        }
    }

    WebSocketWriter(boolean isClient, BufferedSink sink, Random random) {
        if (sink == null) {
            throw new NullPointerException("sink == null");
        } else if (random != null) {
            this.isClient = isClient;
            this.sink = sink;
            this.sinkBuffer = sink.buffer();
            this.random = random;
            Buffer$UnsafeCursor buffer$UnsafeCursor = null;
            this.maskKey = isClient ? new byte[4] : null;
            if (isClient) {
                buffer$UnsafeCursor = new Buffer$UnsafeCursor();
            }
            this.maskCursor = buffer$UnsafeCursor;
        } else {
            throw new NullPointerException("random == null");
        }
    }

    void writePing(ByteString payload) throws IOException {
        writeControlFrame(9, payload);
    }

    void writePong(ByteString payload) throws IOException {
        writeControlFrame(10, payload);
    }

    void writeClose(int code, ByteString reason) throws IOException {
        ByteString payload = ByteString.EMPTY;
        if (code == 0) {
            if (reason == null) {
                writeControlFrame(8, payload);
            }
        }
        if (code != 0) {
            WebSocketProtocol.validateCloseCode(code);
        }
        Buffer buffer = new Buffer();
        buffer.writeShort(code);
        if (reason != null) {
            buffer.write(reason);
        }
        payload = buffer.readByteString();
        try {
            writeControlFrame(8, payload);
        } finally {
            this.writerClosed = true;
        }
    }

    private void writeControlFrame(int opcode, ByteString payload) throws IOException {
        if (this.writerClosed) {
            throw new IOException("closed");
        }
        int length = payload.size();
        if (((long) length) <= 125) {
            this.sinkBuffer.writeByte(opcode | 128);
            int b1 = length;
            if (this.isClient) {
                this.sinkBuffer.writeByte(b1 | 128);
                this.random.nextBytes(this.maskKey);
                this.sinkBuffer.write(this.maskKey);
                if (length > 0) {
                    long payloadStart = this.sinkBuffer.size();
                    this.sinkBuffer.write(payload);
                    this.sinkBuffer.readAndWriteUnsafe(this.maskCursor);
                    this.maskCursor.seek(payloadStart);
                    WebSocketProtocol.toggleMask(this.maskCursor, this.maskKey);
                    this.maskCursor.close();
                }
            } else {
                this.sinkBuffer.writeByte(b1);
                this.sinkBuffer.write(payload);
            }
            this.sink.flush();
            return;
        }
        throw new IllegalArgumentException("Payload size must be less than or equal to 125");
    }

    Sink newMessageSink(int formatOpcode, long contentLength) {
        if (this.activeWriter) {
            throw new IllegalStateException("Another message writer is active. Did you call close()?");
        }
        this.activeWriter = true;
        Sink sink = this.frameSink;
        sink.formatOpcode = formatOpcode;
        sink.contentLength = contentLength;
        sink.isFirstFrame = true;
        sink.closed = false;
        return sink;
    }

    void writeMessageFrame(int formatOpcode, long byteCount, boolean isFirstFrame, boolean isFinal) throws IOException {
        if (this.writerClosed) {
            throw new IOException("closed");
        }
        int b0 = isFirstFrame ? formatOpcode : 0;
        if (isFinal) {
            b0 |= 128;
        }
        this.sinkBuffer.writeByte(b0);
        int b1 = 0;
        if (this.isClient) {
            b1 = 0 | 128;
        }
        if (byteCount <= 125) {
            this.sinkBuffer.writeByte(b1 | ((int) byteCount));
        } else if (byteCount <= 65535) {
            this.sinkBuffer.writeByte(b1 | 126);
            this.sinkBuffer.writeShort((int) byteCount);
        } else {
            this.sinkBuffer.writeByte(b1 | 127);
            this.sinkBuffer.writeLong(byteCount);
        }
        if (this.isClient) {
            this.random.nextBytes(this.maskKey);
            this.sinkBuffer.write(this.maskKey);
            if (byteCount > 0) {
                long bufferStart = this.sinkBuffer.size();
                this.sinkBuffer.write(this.buffer, byteCount);
                this.sinkBuffer.readAndWriteUnsafe(this.maskCursor);
                this.maskCursor.seek(bufferStart);
                WebSocketProtocol.toggleMask(this.maskCursor, this.maskKey);
                this.maskCursor.close();
            }
        } else {
            this.sinkBuffer.write(this.buffer, byteCount);
        }
        this.sink.emit();
    }
}
