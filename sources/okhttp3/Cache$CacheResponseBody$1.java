package okhttp3;

import java.io.IOException;
import okhttp3.Cache.CacheResponseBody;
import okhttp3.internal.cache.DiskLruCache.Snapshot;
import okio.ForwardingSource;
import okio.Source;

class Cache$CacheResponseBody$1 extends ForwardingSource {
    final /* synthetic */ CacheResponseBody this$0;
    final /* synthetic */ Snapshot val$snapshot;

    Cache$CacheResponseBody$1(CacheResponseBody this$0, Source x0, Snapshot snapshot) {
        this.this$0 = this$0;
        this.val$snapshot = snapshot;
        super(x0);
    }

    public void close() throws IOException {
        this.val$snapshot.close();
        super.close();
    }
}
