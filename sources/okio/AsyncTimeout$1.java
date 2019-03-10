package okio;

import android.support.v4.media.session.PlaybackStateCompat;
import java.io.IOException;

class AsyncTimeout$1 implements Sink {
    final /* synthetic */ AsyncTimeout this$0;
    final /* synthetic */ Sink val$sink;

    AsyncTimeout$1(AsyncTimeout this$0, Sink sink) {
        this.this$0 = this$0;
        this.val$sink = sink;
    }

    public void write(Buffer source, long byteCount) throws IOException {
        Util.checkOffsetAndCount(source.size, 0, byteCount);
        while (byteCount > 0) {
            long toWrite = 0;
            Segment s = source.head;
            while (toWrite < PlaybackStateCompat.ACTION_PREPARE_FROM_SEARCH) {
                toWrite += (long) (s.limit - s.pos);
                if (toWrite >= byteCount) {
                    toWrite = byteCount;
                    break;
                }
                s = s.next;
            }
            this.this$0.enter();
            try {
                this.val$sink.write(source, toWrite);
                byteCount -= toWrite;
                this.this$0.exit(true);
            } catch (IOException e) {
                throw this.this$0.exit(e);
            } catch (Throwable th) {
                this.this$0.exit(false);
            }
        }
    }

    public void flush() throws IOException {
        this.this$0.enter();
        try {
            this.val$sink.flush();
            this.this$0.exit(true);
        } catch (IOException e) {
            throw this.this$0.exit(e);
        } catch (Throwable th) {
            this.this$0.exit(false);
        }
    }

    public void close() throws IOException {
        this.this$0.enter();
        try {
            this.val$sink.close();
            this.this$0.exit(true);
        } catch (IOException e) {
            throw this.this$0.exit(e);
        } catch (Throwable th) {
            this.this$0.exit(false);
        }
    }

    public Timeout timeout() {
        return this.this$0;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("AsyncTimeout.sink(");
        stringBuilder.append(this.val$sink);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}
