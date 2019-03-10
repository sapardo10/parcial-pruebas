package okhttp3.internal.cache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import okhttp3.internal.cache.DiskLruCache.Snapshot;

class DiskLruCache$3 implements Iterator<Snapshot> {
    final Iterator<DiskLruCache$Entry> delegate = new ArrayList(this.this$0.lruEntries.values()).iterator();
    Snapshot nextSnapshot;
    Snapshot removeSnapshot;
    final /* synthetic */ DiskLruCache this$0;

    public boolean hasNext() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:23:0x0032 in {2, 9, 14, 17, 19, 22} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r5 = this;
        r0 = r5.nextSnapshot;
        r1 = 1;
        if (r0 == 0) goto L_0x0006;
    L_0x0005:
        return r1;
    L_0x0006:
        r0 = r5.this$0;
        monitor-enter(r0);
        r2 = r5.this$0;	 Catch:{ all -> 0x002f }
        r2 = r2.closed;	 Catch:{ all -> 0x002f }
        r3 = 0;	 Catch:{ all -> 0x002f }
        if (r2 == 0) goto L_0x0012;	 Catch:{ all -> 0x002f }
    L_0x0010:
        monitor-exit(r0);	 Catch:{ all -> 0x002f }
        return r3;	 Catch:{ all -> 0x002f }
    L_0x0012:
        r2 = r5.delegate;	 Catch:{ all -> 0x002f }
        r2 = r2.hasNext();	 Catch:{ all -> 0x002f }
        if (r2 == 0) goto L_0x002d;	 Catch:{ all -> 0x002f }
    L_0x001a:
        r2 = r5.delegate;	 Catch:{ all -> 0x002f }
        r2 = r2.next();	 Catch:{ all -> 0x002f }
        r2 = (okhttp3.internal.cache.DiskLruCache$Entry) r2;	 Catch:{ all -> 0x002f }
        r4 = r2.snapshot();	 Catch:{ all -> 0x002f }
        if (r4 != 0) goto L_0x0029;	 Catch:{ all -> 0x002f }
    L_0x0028:
        goto L_0x0012;	 Catch:{ all -> 0x002f }
    L_0x0029:
        r5.nextSnapshot = r4;	 Catch:{ all -> 0x002f }
        monitor-exit(r0);	 Catch:{ all -> 0x002f }
        return r1;	 Catch:{ all -> 0x002f }
    L_0x002d:
        monitor-exit(r0);	 Catch:{ all -> 0x002f }
        return r3;	 Catch:{ all -> 0x002f }
    L_0x002f:
        r1 = move-exception;	 Catch:{ all -> 0x002f }
        monitor-exit(r0);	 Catch:{ all -> 0x002f }
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.DiskLruCache$3.hasNext():boolean");
    }

    DiskLruCache$3(DiskLruCache this$0) {
        this.this$0 = this$0;
    }

    public Snapshot next() {
        if (hasNext()) {
            this.removeSnapshot = this.nextSnapshot;
            this.nextSnapshot = null;
            return this.removeSnapshot;
        }
        throw new NoSuchElementException();
    }

    public void remove() {
        Snapshot snapshot = this.removeSnapshot;
        if (snapshot != null) {
            try {
                this.this$0.remove(Snapshot.access$000(snapshot));
            } catch (IOException e) {
            } catch (Throwable th) {
                this.removeSnapshot = null;
            }
            this.removeSnapshot = null;
            return;
        }
        throw new IllegalStateException("remove() before next()");
    }
}
