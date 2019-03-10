package com.bumptech.glide.util.pool;

import android.support.annotation.NonNull;
import android.support.v4.util.Pools.Pool;
import android.support.v4.util.Pools.SimplePool;
import android.support.v4.util.Pools.SynchronizedPool;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public final class FactoryPools {
    private static final int DEFAULT_POOL_SIZE = 20;
    private static final Resetter<Object> EMPTY_RESETTER = new C09711();
    private static final String TAG = "FactoryPools";

    public interface Factory<T> {
        T create();
    }

    public interface Poolable {
        @NonNull
        StateVerifier getVerifier();
    }

    public interface Resetter<T> {
        void reset(@NonNull T t);
    }

    /* renamed from: com.bumptech.glide.util.pool.FactoryPools$1 */
    class C09711 implements Resetter<Object> {
        C09711() {
        }

        public void reset(@NonNull Object object) {
        }
    }

    /* renamed from: com.bumptech.glide.util.pool.FactoryPools$2 */
    class C09722 implements Factory<List<T>> {
        C09722() {
        }

        @NonNull
        public List<T> create() {
            return new ArrayList();
        }
    }

    /* renamed from: com.bumptech.glide.util.pool.FactoryPools$3 */
    class C09733 implements Resetter<List<T>> {
        C09733() {
        }

        public void reset(@NonNull List<T> object) {
            object.clear();
        }
    }

    private static final class FactoryPool<T> implements Pool<T> {
        private final Factory<T> factory;
        private final Pool<T> pool;
        private final Resetter<T> resetter;

        FactoryPool(@NonNull Pool<T> pool, @NonNull Factory<T> factory, @NonNull Resetter<T> resetter) {
            this.pool = pool;
            this.factory = factory;
            this.resetter = resetter;
        }

        public T acquire() {
            T result = this.pool.acquire();
            if (result == null) {
                result = this.factory.create();
                if (Log.isLoggable(FactoryPools.TAG, 2)) {
                    String str = FactoryPools.TAG;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Created new ");
                    stringBuilder.append(result.getClass());
                    Log.v(str, stringBuilder.toString());
                }
            }
            if (result instanceof Poolable) {
                ((Poolable) result).getVerifier().setRecycled(false);
            }
            return result;
        }

        public boolean release(@NonNull T instance) {
            if (instance instanceof Poolable) {
                ((Poolable) instance).getVerifier().setRecycled(true);
            }
            this.resetter.reset(instance);
            return this.pool.release(instance);
        }
    }

    private FactoryPools() {
    }

    @NonNull
    public static <T extends Poolable> Pool<T> simple(int size, @NonNull Factory<T> factory) {
        return build(new SimplePool(size), factory);
    }

    @NonNull
    public static <T extends Poolable> Pool<T> threadSafe(int size, @NonNull Factory<T> factory) {
        return build(new SynchronizedPool(size), factory);
    }

    @NonNull
    public static <T> Pool<List<T>> threadSafeList() {
        return threadSafeList(20);
    }

    @NonNull
    public static <T> Pool<List<T>> threadSafeList(int size) {
        return build(new SynchronizedPool(size), new C09722(), new C09733());
    }

    @NonNull
    private static <T extends Poolable> Pool<T> build(@NonNull Pool<T> pool, @NonNull Factory<T> factory) {
        return build(pool, factory, emptyResetter());
    }

    @NonNull
    private static <T> Pool<T> build(@NonNull Pool<T> pool, @NonNull Factory<T> factory, @NonNull Resetter<T> resetter) {
        return new FactoryPool(pool, factory, resetter);
    }

    @NonNull
    private static <T> Resetter<T> emptyResetter() {
        return EMPTY_RESETTER;
    }
}
