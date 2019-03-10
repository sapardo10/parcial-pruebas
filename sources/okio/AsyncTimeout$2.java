package okio;

import java.io.IOException;

class AsyncTimeout$2 implements Source {
    final /* synthetic */ AsyncTimeout this$0;
    final /* synthetic */ Source val$source;

    AsyncTimeout$2(AsyncTimeout this$0, Source source) {
        this.this$0 = this$0;
        this.val$source = source;
    }

    public long read(Buffer sink, long byteCount) throws IOException {
        this.this$0.enter();
        try {
            long result = this.val$source.read(sink, byteCount);
            this.this$0.exit(true);
            return result;
        } catch (IOException e) {
            throw this.this$0.exit(e);
        } catch (Throwable th) {
            this.this$0.exit(false);
        }
    }

    public void close() throws IOException {
        try {
            this.val$source.close();
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
        stringBuilder.append("AsyncTimeout.source(");
        stringBuilder.append(this.val$source);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}
