package okhttp3.internal.cache;

import java.io.IOException;
import okio.Sink;

class DiskLruCache$2 extends FaultHidingSink {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    final /* synthetic */ DiskLruCache this$0;

    static {
        Class cls = DiskLruCache.class;
    }

    DiskLruCache$2(DiskLruCache this$0, Sink delegate) {
        this.this$0 = this$0;
        super(delegate);
    }

    protected void onException(IOException e) {
        this.this$0.hasJournalErrors = true;
    }
}
