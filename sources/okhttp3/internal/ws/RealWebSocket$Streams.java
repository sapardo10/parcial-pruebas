package okhttp3.internal.ws;

import java.io.Closeable;
import okio.BufferedSink;
import okio.BufferedSource;

public abstract class RealWebSocket$Streams implements Closeable {
    public final boolean client;
    public final BufferedSink sink;
    public final BufferedSource source;

    public RealWebSocket$Streams(boolean client, BufferedSource source, BufferedSink sink) {
        this.client = client;
        this.source = source;
        this.sink = sink;
    }
}
