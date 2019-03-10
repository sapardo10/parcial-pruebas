package okhttp3.internal.http2;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import okio.Source;
import okio.Timeout;

final class Http2Reader implements Closeable {
    static final Logger logger = Logger.getLogger(Http2.class.getName());
    private final boolean client;
    private final ContinuationSource continuation = new ContinuationSource(this.source);
    final Reader hpackReader = new Reader(4096, this.continuation);
    private final BufferedSource source;

    interface Handler {
        void ackSettings();

        void alternateService(int i, String str, ByteString byteString, String str2, int i2, long j);

        void data(boolean z, int i, BufferedSource bufferedSource, int i2) throws IOException;

        void goAway(int i, ErrorCode errorCode, ByteString byteString);

        void headers(boolean z, int i, int i2, List<Header> list);

        void ping(boolean z, int i, int i2);

        void priority(int i, int i2, int i3, boolean z);

        void pushPromise(int i, int i2, List<Header> list) throws IOException;

        void rstStream(int i, ErrorCode errorCode);

        void settings(boolean z, Settings settings);

        void windowUpdate(int i, long j);
    }

    static final class ContinuationSource implements Source {
        byte flags;
        int left;
        int length;
        short padding;
        private final BufferedSource source;
        int streamId;

        ContinuationSource(BufferedSource source) {
            this.source = source;
        }

        public long read(Buffer sink, long byteCount) throws IOException {
            int i;
            while (true) {
                i = this.left;
                if (i != 0) {
                    break;
                }
                this.source.skip((long) this.padding);
                this.padding = (short) 0;
                if ((this.flags & 4) != 0) {
                    return -1;
                }
                readContinuationHeader();
            }
            long read = this.source.read(sink, Math.min(byteCount, (long) i));
            if (read == -1) {
                return -1;
            }
            this.left = (int) (((long) this.left) - read);
            return read;
        }

        public Timeout timeout() {
            return this.source.timeout();
        }

        public void close() throws IOException {
        }

        private void readContinuationHeader() throws IOException {
            int previousStreamId = this.streamId;
            int readMedium = Http2Reader.readMedium(this.source);
            this.left = readMedium;
            this.length = readMedium;
            byte type = (byte) (this.source.readByte() & 255);
            this.flags = (byte) (this.source.readByte() & 255);
            if (Http2Reader.logger.isLoggable(Level.FINE)) {
                Http2Reader.logger.fine(Http2.frameLog(true, this.streamId, this.length, type, this.flags));
            }
            this.streamId = this.source.readInt() & Integer.MAX_VALUE;
            if (type != (byte) 9) {
                throw Http2.ioException("%s != TYPE_CONTINUATION", Byte.valueOf(type));
            } else if (this.streamId != previousStreamId) {
                throw Http2.ioException("TYPE_CONTINUATION streamId changed", new Object[0]);
            }
        }
    }

    private void readSettings(okhttp3.internal.http2.Http2Reader.Handler r8, int r9, byte r10, int r11) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:44:0x0092 in {6, 8, 15, 16, 21, 23, 26, 28, 29, 32, 34, 35, 36, 37, 39, 41, 43} preds:[]
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
        r7 = this;
        r0 = 0;
        if (r11 != 0) goto L_0x0089;
    L_0x0003:
        r1 = r10 & 1;
        if (r1 == 0) goto L_0x0016;
    L_0x0007:
        if (r9 != 0) goto L_0x000d;
    L_0x0009:
        r8.ackSettings();
        return;
    L_0x000d:
        r0 = new java.lang.Object[r0];
        r1 = "FRAME_SIZE_ERROR ack frame should be empty!";
        r0 = okhttp3.internal.http2.Http2.ioException(r1, r0);
        throw r0;
    L_0x0016:
        r1 = r9 % 6;
        r2 = 1;
        if (r1 != 0) goto L_0x007a;
    L_0x001b:
        r1 = new okhttp3.internal.http2.Settings;
        r1.<init>();
        r3 = 0;
    L_0x0021:
        if (r3 >= r9) goto L_0x0076;
    L_0x0023:
        r4 = r7.source;
        r4 = r4.readShort();
        r5 = 65535; // 0xffff float:9.1834E-41 double:3.23786E-319;
        r4 = r4 & r5;
        r5 = r7.source;
        r5 = r5.readInt();
        switch(r4) {
            case 1: goto L_0x006f;
            case 2: goto L_0x0060;
            case 3: goto L_0x005e;
            case 4: goto L_0x0051;
            case 5: goto L_0x0038;
            case 6: goto L_0x0037;
            default: goto L_0x0036;
        };
    L_0x0036:
        goto L_0x0070;
    L_0x0037:
        goto L_0x0070;
    L_0x0038:
        r6 = 16384; // 0x4000 float:2.2959E-41 double:8.0948E-320;
        if (r5 < r6) goto L_0x0042;
    L_0x003c:
        r6 = 16777215; // 0xffffff float:2.3509886E-38 double:8.2890456E-317;
        if (r5 > r6) goto L_0x0042;
    L_0x0041:
        goto L_0x0070;
    L_0x0042:
        r2 = new java.lang.Object[r2];
        r6 = java.lang.Integer.valueOf(r5);
        r2[r0] = r6;
        r0 = "PROTOCOL_ERROR SETTINGS_MAX_FRAME_SIZE: %s";
        r0 = okhttp3.internal.http2.Http2.ioException(r0, r2);
        throw r0;
    L_0x0051:
        r4 = 7;
        if (r5 < 0) goto L_0x0055;
    L_0x0054:
        goto L_0x0070;
    L_0x0055:
        r0 = new java.lang.Object[r0];
        r2 = "PROTOCOL_ERROR SETTINGS_INITIAL_WINDOW_SIZE > 2^31 - 1";
        r0 = okhttp3.internal.http2.Http2.ioException(r2, r0);
        throw r0;
    L_0x005e:
        r4 = 4;
        goto L_0x0070;
    L_0x0060:
        if (r5 == 0) goto L_0x006e;
    L_0x0062:
        if (r5 != r2) goto L_0x0065;
    L_0x0064:
        goto L_0x006e;
    L_0x0065:
        r0 = new java.lang.Object[r0];
        r2 = "PROTOCOL_ERROR SETTINGS_ENABLE_PUSH != 0 or 1";
        r0 = okhttp3.internal.http2.Http2.ioException(r2, r0);
        throw r0;
    L_0x006e:
        goto L_0x0070;
    L_0x0070:
        r1.set(r4, r5);
        r3 = r3 + 6;
        goto L_0x0021;
    L_0x0076:
        r8.settings(r0, r1);
        return;
    L_0x007a:
        r1 = new java.lang.Object[r2];
        r2 = java.lang.Integer.valueOf(r9);
        r1[r0] = r2;
        r0 = "TYPE_SETTINGS length %% 6 != 0: %s";
        r0 = okhttp3.internal.http2.Http2.ioException(r0, r1);
        throw r0;
    L_0x0089:
        r0 = new java.lang.Object[r0];
        r1 = "TYPE_SETTINGS streamId != 0";
        r0 = okhttp3.internal.http2.Http2.ioException(r1, r0);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http2.Http2Reader.readSettings(okhttp3.internal.http2.Http2Reader$Handler, int, byte, int):void");
    }

    Http2Reader(BufferedSource source, boolean client) {
        this.source = source;
        this.client = client;
    }

    public void readConnectionPreface(Handler handler) throws IOException {
        if (!this.client) {
            ByteString connectionPreface = this.source.readByteString((long) Http2.CONNECTION_PREFACE.size());
            if (logger.isLoggable(Level.FINE)) {
                logger.fine(Util.format("<< CONNECTION %s", new Object[]{connectionPreface.hex()}));
            }
            if (!Http2.CONNECTION_PREFACE.equals(connectionPreface)) {
                throw Http2.ioException("Expected a connection header but was %s", connectionPreface.utf8());
            }
        } else if (!nextFrame(true, handler)) {
            throw Http2.ioException("Required SETTINGS preface not received", new Object[0]);
        }
    }

    public boolean nextFrame(boolean requireSettings, Handler handler) throws IOException {
        try {
            this.source.require(9);
            int length = readMedium(this.source);
            if (length < 0 || length > 16384) {
                throw Http2.ioException("FRAME_SIZE_ERROR: %s", Integer.valueOf(length));
            }
            byte type = (byte) (this.source.readByte() & 255);
            if (requireSettings) {
                if (type != (byte) 4) {
                    throw Http2.ioException("Expected a SETTINGS frame but was %s", Byte.valueOf(type));
                }
            }
            byte flags = (byte) (this.source.readByte() & 255);
            int streamId = this.source.readInt() & Integer.MAX_VALUE;
            if (logger.isLoggable(Level.FINE)) {
                logger.fine(Http2.frameLog(true, streamId, length, type, flags));
            }
            switch (type) {
                case (byte) 0:
                    readData(handler, length, flags, streamId);
                    break;
                case (byte) 1:
                    readHeaders(handler, length, flags, streamId);
                    break;
                case (byte) 2:
                    readPriority(handler, length, flags, streamId);
                    break;
                case (byte) 3:
                    readRstStream(handler, length, flags, streamId);
                    break;
                case (byte) 4:
                    readSettings(handler, length, flags, streamId);
                    break;
                case (byte) 5:
                    readPushPromise(handler, length, flags, streamId);
                    break;
                case (byte) 6:
                    readPing(handler, length, flags, streamId);
                    break;
                case (byte) 7:
                    readGoAway(handler, length, flags, streamId);
                    break;
                case (byte) 8:
                    readWindowUpdate(handler, length, flags, streamId);
                    break;
                default:
                    this.source.skip((long) length);
                    break;
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void readHeaders(Handler handler, int length, byte flags, int streamId) throws IOException {
        short padding = (short) 0;
        if (streamId != 0) {
            boolean endStream = (flags & 1) != 0;
            if ((flags & 8) != 0) {
                padding = (short) (this.source.readByte() & 255);
            }
            if ((flags & 32) != 0) {
                readPriority(handler, streamId);
                length -= 5;
            }
            handler.headers(endStream, streamId, -1, readHeaderBlock(lengthWithoutPadding(length, flags, padding), padding, flags, streamId));
            return;
        }
        throw Http2.ioException("PROTOCOL_ERROR: TYPE_HEADERS streamId == 0", new Object[0]);
    }

    private List<Header> readHeaderBlock(int length, short padding, byte flags, int streamId) throws IOException {
        ContinuationSource continuationSource = this.continuation;
        continuationSource.left = length;
        continuationSource.length = length;
        continuationSource.padding = padding;
        continuationSource.flags = flags;
        continuationSource.streamId = streamId;
        this.hpackReader.readHeaders();
        return this.hpackReader.getAndResetHeaderList();
    }

    private void readData(Handler handler, int length, byte flags, int streamId) throws IOException {
        short padding = (short) 0;
        if (streamId != 0) {
            boolean gzipped = true;
            boolean inFinished = (flags & 1) != 0;
            if ((flags & 32) == 0) {
                gzipped = false;
            }
            if (gzipped) {
                throw Http2.ioException("PROTOCOL_ERROR: FLAG_COMPRESSED without SETTINGS_COMPRESS_DATA", new Object[0]);
            }
            if ((flags & 8) != 0) {
                padding = (short) (this.source.readByte() & 255);
            }
            handler.data(inFinished, streamId, this.source, lengthWithoutPadding(length, flags, padding));
            this.source.skip((long) padding);
            return;
        }
        throw Http2.ioException("PROTOCOL_ERROR: TYPE_DATA streamId == 0", new Object[0]);
    }

    private void readPriority(Handler handler, int length, byte flags, int streamId) throws IOException {
        if (length != 5) {
            throw Http2.ioException("TYPE_PRIORITY length: %d != 5", Integer.valueOf(length));
        } else if (streamId != 0) {
            readPriority(handler, streamId);
        } else {
            throw Http2.ioException("TYPE_PRIORITY streamId == 0", new Object[0]);
        }
    }

    private void readPriority(Handler handler, int streamId) throws IOException {
        int w1 = this.source.readInt();
        handler.priority(streamId, Integer.MAX_VALUE & w1, (this.source.readByte() & 255) + 1, (Integer.MIN_VALUE & w1) != 0);
    }

    private void readRstStream(Handler handler, int length, byte flags, int streamId) throws IOException {
        if (length != 4) {
            throw Http2.ioException("TYPE_RST_STREAM length: %d != 4", Integer.valueOf(length));
        } else if (streamId != 0) {
            ErrorCode errorCode = ErrorCode.fromHttp2(this.source.readInt());
            if (errorCode != null) {
                handler.rstStream(streamId, errorCode);
            } else {
                throw Http2.ioException("TYPE_RST_STREAM unexpected error code: %d", Integer.valueOf(errorCodeInt));
            }
        } else {
            throw Http2.ioException("TYPE_RST_STREAM streamId == 0", new Object[0]);
        }
    }

    private void readPushPromise(Handler handler, int length, byte flags, int streamId) throws IOException {
        short padding = (short) 0;
        if (streamId != 0) {
            if ((flags & 8) != 0) {
                padding = (short) (this.source.readByte() & 255);
            }
            handler.pushPromise(streamId, this.source.readInt() & Integer.MAX_VALUE, readHeaderBlock(lengthWithoutPadding(length - 4, flags, padding), padding, flags, streamId));
            return;
        }
        throw Http2.ioException("PROTOCOL_ERROR: TYPE_PUSH_PROMISE streamId == 0", new Object[0]);
    }

    private void readPing(Handler handler, int length, byte flags, int streamId) throws IOException {
        boolean ack = false;
        if (length != 8) {
            throw Http2.ioException("TYPE_PING length != 8: %s", Integer.valueOf(length));
        } else if (streamId == 0) {
            int payload1 = this.source.readInt();
            int payload2 = this.source.readInt();
            if ((flags & 1) != 0) {
                ack = true;
            }
            handler.ping(ack, payload1, payload2);
        } else {
            throw Http2.ioException("TYPE_PING streamId != 0", new Object[0]);
        }
    }

    private void readGoAway(Handler handler, int length, byte flags, int streamId) throws IOException {
        if (length < 8) {
            throw Http2.ioException("TYPE_GOAWAY length < 8: %s", Integer.valueOf(length));
        } else if (streamId == 0) {
            int lastStreamId = this.source.readInt();
            int opaqueDataLength = length - 8;
            ErrorCode errorCode = ErrorCode.fromHttp2(this.source.readInt());
            if (errorCode != null) {
                ByteString debugData = ByteString.EMPTY;
                if (opaqueDataLength > 0) {
                    debugData = this.source.readByteString((long) opaqueDataLength);
                }
                handler.goAway(lastStreamId, errorCode, debugData);
                return;
            }
            throw Http2.ioException("TYPE_GOAWAY unexpected error code: %d", Integer.valueOf(errorCodeInt));
        } else {
            throw Http2.ioException("TYPE_GOAWAY streamId != 0", new Object[0]);
        }
    }

    private void readWindowUpdate(Handler handler, int length, byte flags, int streamId) throws IOException {
        if (length == 4) {
            long increment = ((long) this.source.readInt()) & 2147483647L;
            if (increment != 0) {
                handler.windowUpdate(streamId, increment);
                return;
            } else {
                throw Http2.ioException("windowSizeIncrement was 0", Long.valueOf(increment));
            }
        }
        throw Http2.ioException("TYPE_WINDOW_UPDATE length !=4: %s", Integer.valueOf(length));
    }

    public void close() throws IOException {
        this.source.close();
    }

    static int readMedium(BufferedSource source) throws IOException {
        return (((source.readByte() & 255) << 16) | ((source.readByte() & 255) << 8)) | (source.readByte() & 255);
    }

    static int lengthWithoutPadding(int length, byte flags, short padding) throws IOException {
        if ((flags & 8) != 0) {
            short length2 = length - 1;
        }
        if (padding <= length2) {
            return (short) (length2 - padding);
        }
        throw Http2.ioException("PROTOCOL_ERROR padding %s > remaining length %s", Short.valueOf(padding), Integer.valueOf(length2));
    }
}
