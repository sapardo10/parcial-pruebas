package com.bumptech.glide.load.engine;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

final class ActiveResources {
    private static final int MSG_CLEAN_REF = 1;
    @VisibleForTesting
    final Map<Key, ResourceWeakReference> activeEngineResources = new HashMap();
    @Nullable
    private volatile DequeuedResourceCallback cb;
    @Nullable
    private Thread cleanReferenceQueueThread;
    private final boolean isActiveResourceRetentionAllowed;
    private volatile boolean isShutdown;
    private ResourceListener listener;
    private final Handler mainHandler = new Handler(Looper.getMainLooper(), new C05271());
    @Nullable
    private ReferenceQueue<EngineResource<?>> resourceReferenceQueue;

    /* renamed from: com.bumptech.glide.load.engine.ActiveResources$1 */
    class C05271 implements Callback {
        C05271() {
        }

        public boolean handleMessage(Message msg) {
            if (msg.what != 1) {
                return false;
            }
            ActiveResources.this.cleanupActiveReference((ResourceWeakReference) msg.obj);
            return true;
        }
    }

    /* renamed from: com.bumptech.glide.load.engine.ActiveResources$2 */
    class C05282 implements Runnable {
        C05282() {
        }

        public void run() {
            Process.setThreadPriority(10);
            ActiveResources.this.cleanReferenceQueue();
        }
    }

    @VisibleForTesting
    interface DequeuedResourceCallback {
        void onResourceDequeued();
    }

    @VisibleForTesting
    static final class ResourceWeakReference extends WeakReference<EngineResource<?>> {
        final boolean isCacheable;
        final Key key;
        @Nullable
        Resource<?> resource;

        ResourceWeakReference(@NonNull Key key, @NonNull EngineResource<?> referent, @NonNull ReferenceQueue<? super EngineResource<?>> queue, boolean isActiveResourceRetentionAllowed) {
            super(referent, queue);
            this.key = (Key) Preconditions.checkNotNull(key);
            Resource resource = (referent.isCacheable() && isActiveResourceRetentionAllowed) ? (Resource) Preconditions.checkNotNull(referent.getResource()) : null;
            this.resource = resource;
            this.isCacheable = referent.isCacheable();
        }

        void reset() {
            this.resource = null;
            clear();
        }
    }

    ActiveResources(boolean isActiveResourceRetentionAllowed) {
        this.isActiveResourceRetentionAllowed = isActiveResourceRetentionAllowed;
    }

    void setListener(ResourceListener listener) {
        this.listener = listener;
    }

    void activate(Key key, EngineResource<?> resource) {
        ResourceWeakReference removed = (ResourceWeakReference) this.activeEngineResources.put(key, new ResourceWeakReference(key, resource, getReferenceQueue(), this.isActiveResourceRetentionAllowed));
        if (removed != null) {
            removed.reset();
        }
    }

    void deactivate(Key key) {
        ResourceWeakReference removed = (ResourceWeakReference) this.activeEngineResources.remove(key);
        if (removed != null) {
            removed.reset();
        }
    }

    @Nullable
    EngineResource<?> get(Key key) {
        ResourceWeakReference activeRef = (ResourceWeakReference) this.activeEngineResources.get(key);
        if (activeRef == null) {
            return null;
        }
        EngineResource<?> active = (EngineResource) activeRef.get();
        if (active == null) {
            cleanupActiveReference(activeRef);
        }
        return active;
    }

    void cleanupActiveReference(@NonNull ResourceWeakReference ref) {
        Util.assertMainThread();
        this.activeEngineResources.remove(ref.key);
        if (ref.isCacheable) {
            if (ref.resource != null) {
                EngineResource<?> newResource = new EngineResource(ref.resource, true, false);
                newResource.setResourceListener(ref.key, this.listener);
                this.listener.onResourceReleased(ref.key, newResource);
            }
        }
    }

    private ReferenceQueue<EngineResource<?>> getReferenceQueue() {
        if (this.resourceReferenceQueue == null) {
            this.resourceReferenceQueue = new ReferenceQueue();
            this.cleanReferenceQueueThread = new Thread(new C05282(), "glide-active-resources");
            this.cleanReferenceQueueThread.start();
        }
        return this.resourceReferenceQueue;
    }

    void cleanReferenceQueue() {
        while (!this.isShutdown) {
            try {
                this.mainHandler.obtainMessage(1, (ResourceWeakReference) this.resourceReferenceQueue.remove()).sendToTarget();
                DequeuedResourceCallback current = this.cb;
                if (current != null) {
                    current.onResourceDequeued();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @VisibleForTesting
    void setDequeuedResourceCallback(DequeuedResourceCallback cb) {
        this.cb = cb;
    }

    @VisibleForTesting
    void shutdown() {
        this.isShutdown = true;
        Thread thread = this.cleanReferenceQueueThread;
        if (thread != null) {
            thread.interrupt();
            try {
                this.cleanReferenceQueueThread.join(TimeUnit.SECONDS.toMillis(5));
                if (this.cleanReferenceQueueThread.isAlive()) {
                    throw new RuntimeException("Failed to join in time");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
