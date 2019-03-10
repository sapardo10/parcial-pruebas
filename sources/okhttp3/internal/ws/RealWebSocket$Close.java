package okhttp3.internal.ws;

import okio.ByteString;

final class RealWebSocket$Close {
    final long cancelAfterCloseMillis;
    final int code;
    final ByteString reason;

    RealWebSocket$Close(int code, ByteString reason, long cancelAfterCloseMillis) {
        this.code = code;
        this.reason = reason;
        this.cancelAfterCloseMillis = cancelAfterCloseMillis;
    }
}
