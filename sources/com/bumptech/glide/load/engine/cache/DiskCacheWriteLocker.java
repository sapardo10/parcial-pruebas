package com.bumptech.glide.load.engine.cache;

import com.bumptech.glide.util.Preconditions;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

final class DiskCacheWriteLocker {
    private final Map<String, WriteLock> locks = new HashMap();
    private final WriteLockPool writeLockPool = new WriteLockPool();

    private static class WriteLock {
        int interestedThreads;
        final Lock lock = new ReentrantLock();

        WriteLock() {
        }
    }

    private static class WriteLockPool {
        private static final int MAX_POOL_SIZE = 10;
        private final Queue<WriteLock> pool = new ArrayDeque();

        WriteLockPool() {
        }

        WriteLock obtain() {
            synchronized (this.pool) {
                WriteLock result = (WriteLock) this.pool.poll();
            }
            if (result == null) {
                return new WriteLock();
            }
            return result;
        }

        void offer(WriteLock writeLock) {
            synchronized (this.pool) {
                if (this.pool.size() < 10) {
                    this.pool.offer(writeLock);
                }
            }
        }
    }

    DiskCacheWriteLocker() {
    }

    void acquire(String safeKey) {
        WriteLock writeLock;
        synchronized (this) {
            writeLock = (WriteLock) this.locks.get(safeKey);
            if (writeLock == null) {
                writeLock = this.writeLockPool.obtain();
                this.locks.put(safeKey, writeLock);
            }
            writeLock.interestedThreads++;
        }
        writeLock.lock.lock();
    }

    void release(String safeKey) {
        WriteLock writeLock;
        synchronized (this) {
            writeLock = (WriteLock) Preconditions.checkNotNull(this.locks.get(safeKey));
            if (writeLock.interestedThreads >= 1) {
                writeLock.interestedThreads--;
                if (writeLock.interestedThreads == 0) {
                    WriteLock removed = (WriteLock) this.locks.remove(safeKey);
                    if (removed.equals(writeLock)) {
                        this.writeLockPool.offer(removed);
                    } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Removed the wrong lock, expected to remove: ");
                        stringBuilder.append(writeLock);
                        stringBuilder.append(", but actually removed: ");
                        stringBuilder.append(removed);
                        stringBuilder.append(", safeKey: ");
                        stringBuilder.append(safeKey);
                        throw new IllegalStateException(stringBuilder.toString());
                    }
                }
            } else {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Cannot release a lock that is not held, safeKey: ");
                stringBuilder2.append(safeKey);
                stringBuilder2.append(", interestedThreads: ");
                stringBuilder2.append(writeLock.interestedThreads);
                throw new IllegalStateException(stringBuilder2.toString());
            }
        }
        writeLock.lock.unlock();
    }
}
