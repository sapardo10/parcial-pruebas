package okhttp3.internal.ws;

import java.io.IOException;
import java.net.ProtocolException;
import java.util.concurrent.TimeUnit;
import okio.Buffer;
import okio.Buffer$UnsafeCursor;
import okio.BufferedSource;

final class WebSocketReader {
    boolean closed;
    private final Buffer controlFrameBuffer = new Buffer();
    final WebSocketReader$FrameCallback frameCallback;
    long frameLength;
    final boolean isClient;
    boolean isControlFrame;
    boolean isFinalFrame;
    private final Buffer$UnsafeCursor maskCursor;
    private final byte[] maskKey;
    private final Buffer messageFrameBuffer = new Buffer();
    int opcode;
    final BufferedSource source;

    private void readMessage() throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:19:0x006d in {6, 7, 8, 11, 14, 16, 18} preds:[]
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
    L_0x0000:
        r0 = r5.closed;
        if (r0 != 0) goto L_0x0065;
    L_0x0004:
        r0 = r5.frameLength;
        r2 = 0;
        r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r4 <= 0) goto L_0x003a;
    L_0x000c:
        r2 = r5.source;
        r3 = r5.messageFrameBuffer;
        r2.readFully(r3, r0);
        r0 = r5.isClient;
        if (r0 != 0) goto L_0x0039;
    L_0x0017:
        r0 = r5.messageFrameBuffer;
        r1 = r5.maskCursor;
        r0.readAndWriteUnsafe(r1);
        r0 = r5.maskCursor;
        r1 = r5.messageFrameBuffer;
        r1 = r1.size();
        r3 = r5.frameLength;
        r1 = r1 - r3;
        r0.seek(r1);
        r0 = r5.maskCursor;
        r1 = r5.maskKey;
        okhttp3.internal.ws.WebSocketProtocol.toggleMask(r0, r1);
        r0 = r5.maskCursor;
        r0.close();
        goto L_0x003b;
    L_0x0039:
        goto L_0x003b;
    L_0x003b:
        r0 = r5.isFinalFrame;
        if (r0 == 0) goto L_0x0040;
    L_0x003f:
        return;
    L_0x0040:
        r5.readUntilNonControlFrame();
        r0 = r5.opcode;
        if (r0 != 0) goto L_0x0048;
    L_0x0047:
        goto L_0x0000;
    L_0x0048:
        r0 = new java.net.ProtocolException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Expected continuation opcode. Got: ";
        r1.append(r2);
        r2 = r5.opcode;
        r2 = java.lang.Integer.toHexString(r2);
        r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
    L_0x0065:
        r0 = new java.io.IOException;
        r1 = "closed";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.ws.WebSocketReader.readMessage():void");
    }

    WebSocketReader(boolean isClient, BufferedSource source, WebSocketReader$FrameCallback frameCallback) {
        if (source == null) {
            throw new NullPointerException("source == null");
        } else if (frameCallback != null) {
            this.isClient = isClient;
            this.source = source;
            this.frameCallback = frameCallback;
            Buffer$UnsafeCursor buffer$UnsafeCursor = null;
            this.maskKey = isClient ? null : new byte[4];
            if (!isClient) {
                buffer$UnsafeCursor = new Buffer$UnsafeCursor();
            }
            this.maskCursor = buffer$UnsafeCursor;
        } else {
            throw new NullPointerException("frameCallback == null");
        }
    }

    void processNextFrame() throws IOException {
        readHeader();
        if (this.isControlFrame) {
            readControlFrame();
        } else {
            readMessageFrame();
        }
    }

    private void readHeader() throws IOException {
        if (this.closed) {
            throw new IOException("closed");
        }
        long timeoutBefore = this.source.timeout().timeoutNanos();
        this.source.timeout().clearTimeout();
        Object timeout;
        boolean isMasked;
        try {
            int b0 = this.source.readByte() & 255;
            this.isFinalFrame = timeout != null;
            this.isControlFrame = (b0 & 8) != 0;
            if (this.isControlFrame) {
                if (!this.isFinalFrame) {
                    throw new ProtocolException("Control frames must be final.");
                }
            }
            boolean reservedFlag1 = (b0 & 64) != 0;
            boolean reservedFlag2 = (b0 & 32) != 0;
            boolean reservedFlag3 = (b0 & 16) != 0;
            if (reservedFlag1 || reservedFlag2 || reservedFlag3) {
                throw new ProtocolException("Reserved flags are unsupported.");
            }
            int b1 = this.source.readByte() & 255;
            if ((b1 & 128) == 0) {
                isMasked = false;
            }
            boolean z = this.isClient;
            if (isMasked == z) {
                String str;
                if (z) {
                    str = "Server-sent frames must not be masked.";
                } else {
                    str = "Client-sent frames must be masked.";
                }
                throw new ProtocolException(str);
            }
            this.frameLength = (long) (b1 & 127);
            long j = this.frameLength;
            if (j == 126) {
                this.frameLength = ((long) this.source.readShort()) & 65535;
            } else if (j == 127) {
                this.frameLength = this.source.readLong();
                if (this.frameLength < 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Frame length 0x");
                    stringBuilder.append(Long.toHexString(this.frameLength));
                    stringBuilder.append(" > 0x7FFFFFFFFFFFFFFF");
                    throw new ProtocolException(stringBuilder.toString());
                }
            }
            if (this.isControlFrame) {
                if (this.frameLength > 125) {
                    throw new ProtocolException("Control frame must be less than 125B.");
                }
            }
            if (isMasked) {
                this.source.readFully(this.maskKey);
            }
        } finally {
            timeout = this.source.timeout();
            isMasked = TimeUnit.NANOSECONDS;
            timeout.timeout(timeoutBefore, isMasked);
        }
    }

    private void readControlFrame() throws IOException {
        long j = this.frameLength;
        if (j > 0) {
            this.source.readFully(this.controlFrameBuffer, j);
            if (!this.isClient) {
                this.controlFrameBuffer.readAndWriteUnsafe(this.maskCursor);
                this.maskCursor.seek(0);
                WebSocketProtocol.toggleMask(this.maskCursor, this.maskKey);
                this.maskCursor.close();
            }
        }
        switch (this.opcode) {
            case 8:
                int code = 1005;
                String reason = "";
                long bufferSize = this.controlFrameBuffer.size();
                if (bufferSize != 1) {
                    if (bufferSize != 0) {
                        code = this.controlFrameBuffer.readShort();
                        reason = this.controlFrameBuffer.readUtf8();
                        String codeExceptionMessage = WebSocketProtocol.closeCodeExceptionMessage(code);
                        if (codeExceptionMessage != null) {
                            throw new ProtocolException(codeExceptionMessage);
                        }
                    }
                    this.frameCallback.onReadClose(code, reason);
                    this.closed = true;
                    return;
                }
                throw new ProtocolException("Malformed close payload length of 1.");
            case 9:
                this.frameCallback.onReadPing(this.controlFrameBuffer.readByteString());
                return;
            case 10:
                this.frameCallback.onReadPong(this.controlFrameBuffer.readByteString());
                return;
            default:
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unknown control opcode: ");
                stringBuilder.append(Integer.toHexString(this.opcode));
                throw new ProtocolException(stringBuilder.toString());
        }
    }

    private void readMessageFrame() throws IOException {
        int opcode = this.opcode;
        if (opcode != 1) {
            if (opcode != 2) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unknown opcode: ");
                stringBuilder.append(Integer.toHexString(opcode));
                throw new ProtocolException(stringBuilder.toString());
            }
        }
        readMessage();
        if (opcode == 1) {
            this.frameCallback.onReadMessage(this.messageFrameBuffer.readUtf8());
        } else {
            this.frameCallback.onReadMessage(this.messageFrameBuffer.readByteString());
        }
    }

    private void readUntilNonControlFrame() throws IOException {
        while (!this.closed) {
            readHeader();
            if (this.isControlFrame) {
                readControlFrame();
            } else {
                return;
            }
        }
    }
}
