package android.support.v7.util;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.util.ThreadUtil.BackgroundCallback;
import android.support.v7.util.ThreadUtil.MainThreadCallback;
import android.support.v7.util.TileList.Tile;
import android.util.Log;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

class MessageThreadUtil<T> implements ThreadUtil<T> {

    static class MessageQueue {
        private SyncQueueItem mRoot;

        synchronized void removeMessages(int r5) {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:24:0x003f in {6, 14, 15, 16, 17, 18, 20, 23} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r4 = this;
            monitor-enter(r4);
        L_0x0001:
            r0 = r4.mRoot;	 Catch:{ all -> 0x003c }
            if (r0 == 0) goto L_0x0019;	 Catch:{ all -> 0x003c }
        L_0x0005:
            r0 = r4.mRoot;	 Catch:{ all -> 0x003c }
            r0 = r0.what;	 Catch:{ all -> 0x003c }
            if (r0 != r5) goto L_0x0019;	 Catch:{ all -> 0x003c }
        L_0x000b:
            r0 = r4.mRoot;	 Catch:{ all -> 0x003c }
            r1 = r4.mRoot;	 Catch:{ all -> 0x003c }
            r1 = r1.next;	 Catch:{ all -> 0x003c }
            r4.mRoot = r1;	 Catch:{ all -> 0x003c }
            r0.recycle();	 Catch:{ all -> 0x003c }
            goto L_0x0001;	 Catch:{ all -> 0x003c }
            r0 = r4.mRoot;	 Catch:{ all -> 0x003c }
            if (r0 == 0) goto L_0x0039;	 Catch:{ all -> 0x003c }
        L_0x001e:
            r0 = r4.mRoot;	 Catch:{ all -> 0x003c }
            r1 = r0.next;	 Catch:{ all -> 0x003c }
        L_0x0024:
            if (r1 == 0) goto L_0x0038;	 Catch:{ all -> 0x003c }
        L_0x0026:
            r2 = r1.next;	 Catch:{ all -> 0x003c }
            r3 = r1.what;	 Catch:{ all -> 0x003c }
            if (r3 != r5) goto L_0x0035;	 Catch:{ all -> 0x003c }
        L_0x002e:
            r0.next = r2;	 Catch:{ all -> 0x003c }
            r1.recycle();	 Catch:{ all -> 0x003c }
            goto L_0x0036;
        L_0x0035:
            r0 = r1;
        L_0x0036:
            r1 = r2;
            goto L_0x0024;
        L_0x0038:
            goto L_0x003a;
        L_0x003a:
            monitor-exit(r4);
            return;
        L_0x003c:
            r5 = move-exception;
            monitor-exit(r4);
            throw r5;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.v7.util.MessageThreadUtil.MessageQueue.removeMessages(int):void");
        }

        synchronized void sendMessage(android.support.v7.util.MessageThreadUtil.SyncQueueItem r3) {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:18:0x001f in {6, 11, 14, 17} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r2 = this;
            monitor-enter(r2);
            r0 = r2.mRoot;	 Catch:{ all -> 0x001c }
            if (r0 != 0) goto L_0x0009;	 Catch:{ all -> 0x001c }
        L_0x0005:
            r2.mRoot = r3;	 Catch:{ all -> 0x001c }
            monitor-exit(r2);
            return;
        L_0x0009:
            r0 = r2.mRoot;	 Catch:{ all -> 0x001c }
        L_0x000b:
            r1 = r0.next;	 Catch:{ all -> 0x001c }
            if (r1 == 0) goto L_0x0017;	 Catch:{ all -> 0x001c }
        L_0x0011:
            r1 = r0.next;	 Catch:{ all -> 0x001c }
            r0 = r1;	 Catch:{ all -> 0x001c }
            goto L_0x000b;	 Catch:{ all -> 0x001c }
        L_0x0017:
            r0.next = r3;	 Catch:{ all -> 0x001c }
            monitor-exit(r2);
            return;
        L_0x001c:
            r3 = move-exception;
            monitor-exit(r2);
            throw r3;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.v7.util.MessageThreadUtil.MessageQueue.sendMessage(android.support.v7.util.MessageThreadUtil$SyncQueueItem):void");
        }

        MessageQueue() {
        }

        synchronized SyncQueueItem next() {
            if (this.mRoot == null) {
                return null;
            }
            SyncQueueItem next = this.mRoot;
            this.mRoot = this.mRoot.next;
            return next;
        }

        synchronized void sendMessageAtFrontOfQueue(SyncQueueItem item) {
            item.next = this.mRoot;
            this.mRoot = item;
        }
    }

    static class SyncQueueItem {
        private static SyncQueueItem sPool;
        private static final Object sPoolLock = new Object();
        public int arg1;
        public int arg2;
        public int arg3;
        public int arg4;
        public int arg5;
        public Object data;
        private SyncQueueItem next;
        public int what;

        SyncQueueItem() {
        }

        void recycle() {
            this.next = null;
            this.arg5 = 0;
            this.arg4 = 0;
            this.arg3 = 0;
            this.arg2 = 0;
            this.arg1 = 0;
            this.what = 0;
            this.data = null;
            synchronized (sPoolLock) {
                if (sPool != null) {
                    this.next = sPool;
                }
                sPool = this;
            }
        }

        static SyncQueueItem obtainMessage(int what, int arg1, int arg2, int arg3, int arg4, int arg5, Object data) {
            SyncQueueItem item;
            synchronized (sPoolLock) {
                if (sPool == null) {
                    item = new SyncQueueItem();
                } else {
                    item = sPool;
                    sPool = sPool.next;
                    item.next = null;
                }
                item.what = what;
                item.arg1 = arg1;
                item.arg2 = arg2;
                item.arg3 = arg3;
                item.arg4 = arg4;
                item.arg5 = arg5;
                item.data = data;
            }
            return item;
        }

        static SyncQueueItem obtainMessage(int what, int arg1, int arg2) {
            return obtainMessage(what, arg1, arg2, 0, 0, 0, null);
        }

        static SyncQueueItem obtainMessage(int what, int arg1, Object data) {
            return obtainMessage(what, arg1, 0, 0, 0, 0, data);
        }
    }

    MessageThreadUtil() {
    }

    public MainThreadCallback<T> getMainThreadProxy(final MainThreadCallback<T> callback) {
        return new MainThreadCallback<T>() {
            static final int ADD_TILE = 2;
            static final int REMOVE_TILE = 3;
            static final int UPDATE_ITEM_COUNT = 1;
            private final Handler mMainThreadHandler = new Handler(Looper.getMainLooper());
            private Runnable mMainThreadRunnable = new C03261();
            final MessageQueue mQueue = new MessageQueue();

            /* renamed from: android.support.v7.util.MessageThreadUtil$1$1 */
            class C03261 implements Runnable {
                C03261() {
                }

                public void run() {
                    SyncQueueItem msg = C08741.this.mQueue.next();
                    while (msg != null) {
                        switch (msg.what) {
                            case 1:
                                callback.updateItemCount(msg.arg1, msg.arg2);
                                break;
                            case 2:
                                callback.addTile(msg.arg1, (Tile) msg.data);
                                break;
                            case 3:
                                callback.removeTile(msg.arg1, msg.arg2);
                                break;
                            default:
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("Unsupported message, what=");
                                stringBuilder.append(msg.what);
                                Log.e("ThreadUtil", stringBuilder.toString());
                                break;
                        }
                        msg = C08741.this.mQueue.next();
                    }
                }
            }

            public void updateItemCount(int generation, int itemCount) {
                sendMessage(SyncQueueItem.obtainMessage(1, generation, itemCount));
            }

            public void addTile(int generation, Tile<T> tile) {
                sendMessage(SyncQueueItem.obtainMessage(2, generation, (Object) tile));
            }

            public void removeTile(int generation, int position) {
                sendMessage(SyncQueueItem.obtainMessage(3, generation, position));
            }

            private void sendMessage(SyncQueueItem msg) {
                this.mQueue.sendMessage(msg);
                this.mMainThreadHandler.post(this.mMainThreadRunnable);
            }
        };
    }

    public BackgroundCallback<T> getBackgroundProxy(final BackgroundCallback<T> callback) {
        return new BackgroundCallback<T>() {
            static final int LOAD_TILE = 3;
            static final int RECYCLE_TILE = 4;
            static final int REFRESH = 1;
            static final int UPDATE_RANGE = 2;
            private Runnable mBackgroundRunnable = new C03271();
            AtomicBoolean mBackgroundRunning = new AtomicBoolean(false);
            private final Executor mExecutor = AsyncTask.THREAD_POOL_EXECUTOR;
            final MessageQueue mQueue = new MessageQueue();

            /* renamed from: android.support.v7.util.MessageThreadUtil$2$1 */
            class C03271 implements Runnable {
                C03271() {
                }

                public void run() {
                    while (true) {
                        SyncQueueItem msg = C08752.this.mQueue.next();
                        if (msg != null) {
                            switch (msg.what) {
                                case 1:
                                    C08752.this.mQueue.removeMessages(1);
                                    callback.refresh(msg.arg1);
                                    break;
                                case 2:
                                    C08752.this.mQueue.removeMessages(2);
                                    C08752.this.mQueue.removeMessages(3);
                                    callback.updateRange(msg.arg1, msg.arg2, msg.arg3, msg.arg4, msg.arg5);
                                    break;
                                case 3:
                                    callback.loadTile(msg.arg1, msg.arg2);
                                    break;
                                case 4:
                                    callback.recycleTile((Tile) msg.data);
                                    break;
                                default:
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append("Unsupported message, what=");
                                    stringBuilder.append(msg.what);
                                    Log.e("ThreadUtil", stringBuilder.toString());
                                    break;
                            }
                        }
                        C08752.this.mBackgroundRunning.set(false);
                        return;
                    }
                }
            }

            public void refresh(int generation) {
                sendMessageAtFrontOfQueue(SyncQueueItem.obtainMessage(1, generation, null));
            }

            public void updateRange(int rangeStart, int rangeEnd, int extRangeStart, int extRangeEnd, int scrollHint) {
                sendMessageAtFrontOfQueue(SyncQueueItem.obtainMessage(2, rangeStart, rangeEnd, extRangeStart, extRangeEnd, scrollHint, null));
            }

            public void loadTile(int position, int scrollHint) {
                sendMessage(SyncQueueItem.obtainMessage(3, position, scrollHint));
            }

            public void recycleTile(Tile<T> tile) {
                sendMessage(SyncQueueItem.obtainMessage(4, 0, (Object) tile));
            }

            private void sendMessage(SyncQueueItem msg) {
                this.mQueue.sendMessage(msg);
                maybeExecuteBackgroundRunnable();
            }

            private void sendMessageAtFrontOfQueue(SyncQueueItem msg) {
                this.mQueue.sendMessageAtFrontOfQueue(msg);
                maybeExecuteBackgroundRunnable();
            }

            private void maybeExecuteBackgroundRunnable() {
                if (this.mBackgroundRunning.compareAndSet(false, true)) {
                    this.mExecutor.execute(this.mBackgroundRunnable);
                }
            }
        };
    }
}
