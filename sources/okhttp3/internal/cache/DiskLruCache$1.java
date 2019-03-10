package okhttp3.internal.cache;

import java.io.IOException;
import okio.Okio;

class DiskLruCache$1 implements Runnable {
    final /* synthetic */ DiskLruCache this$0;

    DiskLruCache$1(DiskLruCache this$0) {
        this.this$0 = this$0;
    }

    public void run() {
        synchronized (this.this$0) {
            if (((!this.this$0.initialized ? 1 : 0) | this.this$0.closed) != 0) {
                return;
            }
            try {
                this.this$0.trimToSize();
            } catch (IOException e) {
                this.this$0.mostRecentTrimFailed = true;
            }
            try {
                if (this.this$0.journalRebuildRequired()) {
                    this.this$0.rebuildJournal();
                    this.this$0.redundantOpCount = 0;
                }
            } catch (IOException e2) {
                this.this$0.mostRecentRebuildFailed = true;
                this.this$0.journalWriter = Okio.buffer(Okio.blackhole());
            }
        }
        return;
    }
}
