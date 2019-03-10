package okhttp3.internal.ws;

import android.support.v4.view.PointerIconCompat;
import okio.Buffer$UnsafeCursor;
import okio.ByteString;

public final class WebSocketProtocol {
    static final String ACCEPT_MAGIC = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
    static final int B0_FLAG_FIN = 128;
    static final int B0_FLAG_RSV1 = 64;
    static final int B0_FLAG_RSV2 = 32;
    static final int B0_FLAG_RSV3 = 16;
    static final int B0_MASK_OPCODE = 15;
    static final int B1_FLAG_MASK = 128;
    static final int B1_MASK_LENGTH = 127;
    static final int CLOSE_CLIENT_GOING_AWAY = 1001;
    static final long CLOSE_MESSAGE_MAX = 123;
    static final int CLOSE_NO_STATUS_CODE = 1005;
    static final int OPCODE_BINARY = 2;
    static final int OPCODE_CONTINUATION = 0;
    static final int OPCODE_CONTROL_CLOSE = 8;
    static final int OPCODE_CONTROL_PING = 9;
    static final int OPCODE_CONTROL_PONG = 10;
    static final int OPCODE_FLAG_CONTROL = 8;
    static final int OPCODE_TEXT = 1;
    static final long PAYLOAD_BYTE_MAX = 125;
    static final int PAYLOAD_LONG = 127;
    static final int PAYLOAD_SHORT = 126;
    static final long PAYLOAD_SHORT_MAX = 65535;

    static void toggleMask(Buffer$UnsafeCursor cursor, byte[] key) {
        int keyIndex = 0;
        int keyLength = key.length;
        while (true) {
            byte[] buffer = cursor.data;
            int i = cursor.start;
            int end = cursor.end;
            while (i < end) {
                keyIndex %= keyLength;
                buffer[i] = (byte) (buffer[i] ^ key[keyIndex]);
                i++;
                keyIndex++;
            }
            if (cursor.next() == -1) {
                return;
            }
        }
    }

    static String closeCodeExceptionMessage(int code) {
        StringBuilder stringBuilder;
        if (code >= 1000) {
            if (code < 5000) {
                if (code >= PointerIconCompat.TYPE_WAIT) {
                    if (code > PointerIconCompat.TYPE_CELL) {
                    }
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Code ");
                    stringBuilder.append(code);
                    stringBuilder.append(" is reserved and may not be used.");
                    return stringBuilder.toString();
                }
                if (code < PointerIconCompat.TYPE_NO_DROP || code > 2999) {
                    return null;
                }
                stringBuilder = new StringBuilder();
                stringBuilder.append("Code ");
                stringBuilder.append(code);
                stringBuilder.append(" is reserved and may not be used.");
                return stringBuilder.toString();
            }
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append("Code must be in range [1000,5000): ");
        stringBuilder.append(code);
        return stringBuilder.toString();
    }

    static void validateCloseCode(int code) {
        String message = closeCodeExceptionMessage(code);
        if (message != null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static String acceptHeader(String key) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(key);
        stringBuilder.append(ACCEPT_MAGIC);
        return ByteString.encodeUtf8(stringBuilder.toString()).sha1().base64();
    }

    private WebSocketProtocol() {
        throw new AssertionError("No instances.");
    }
}
