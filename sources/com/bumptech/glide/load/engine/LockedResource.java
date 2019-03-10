package com.bumptech.glide.load.engine;

import android.support.annotation.NonNull;
import android.support.v4.util.Pools.Pool;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.pool.FactoryPools;
import com.bumptech.glide.util.pool.FactoryPools.Factory;
import com.bumptech.glide.util.pool.FactoryPools.Poolable;
import com.bumptech.glide.util.pool.StateVerifier;

final class LockedResource<Z> implements Resource<Z>, Poolable {
    private static final Pool<LockedResource<?>> POOL = FactoryPools.threadSafe(20, new C09491());
    private boolean isLocked;
    private boolean isRecycled;
    private final StateVerifier stateVerifier = StateVerifier.newInstance();
    private Resource<Z> toWrap;

    /* renamed from: com.bumptech.glide.load.engine.LockedResource$1 */
    class C09491 implements Factory<LockedResource<?>> {
        C09491() {
        }

        public LockedResource<?> create() {
            return new LockedResource();
        }
    }

    @NonNull
    static <Z> LockedResource<Z> obtain(Resource<Z> resource) {
        LockedResource<Z> result = (LockedResource) Preconditions.checkNotNull((LockedResource) POOL.acquire());
        result.init(resource);
        return result;
    }

    LockedResource() {
    }

    private void init(Resource<Z> toWrap) {
        this.isRecycled = false;
        this.isLocked = true;
        this.toWrap = toWrap;
    }

    private void release() {
        this.toWrap = null;
        POOL.release(this);
    }

    synchronized void unlock() {
        this.stateVerifier.throwIfRecycled();
        if (this.isLocked) {
            this.isLocked = false;
            if (this.isRecycled) {
                recycle();
            }
        } else {
            throw new IllegalStateException("Already unlocked");
        }
    }

    @NonNull
    public Class<Z> getResourceClass() {
        return this.toWrap.getResourceClass();
    }

    @NonNull
    public Z get() {
        return this.toWrap.get();
    }

    public int getSize() {
        return this.toWrap.getSize();
    }

    public synchronized void recycle() {
        this.stateVerifier.throwIfRecycled();
        this.isRecycled = true;
        if (!this.isLocked) {
            this.toWrap.recycle();
            release();
        }
    }

    @NonNull
    public StateVerifier getVerifier() {
        return this.stateVerifier;
    }
}
