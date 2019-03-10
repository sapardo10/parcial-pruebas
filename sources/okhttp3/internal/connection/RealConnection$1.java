package okhttp3.internal.connection;

import java.io.IOException;
import okhttp3.internal.ws.RealWebSocket$Streams;
import okio.BufferedSink;
import okio.BufferedSource;

class RealConnection$1 extends RealWebSocket$Streams {
    final /* synthetic */ RealConnection this$0;
    final /* synthetic */ StreamAllocation val$streamAllocation;

    RealConnection$1(RealConnection this$0, boolean client, BufferedSource source, BufferedSink sink, StreamAllocation streamAllocation) {
        this.this$0 = this$0;
        this.val$streamAllocation = streamAllocation;
        super(client, source, sink);
    }

    public void close() throws IOException {
        StreamAllocation streamAllocation = this.val$streamAllocation;
        streamAllocation.streamFinished(true, streamAllocation.codec(), -1, null);
    }
}
