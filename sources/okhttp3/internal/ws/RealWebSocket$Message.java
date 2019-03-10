package okhttp3.internal.ws;

import okio.ByteString;

final class RealWebSocket$Message {
    final ByteString data;
    final int formatOpcode;

    RealWebSocket$Message(int formatOpcode, ByteString data) {
        this.formatOpcode = formatOpcode;
        this.data = data;
    }
}
