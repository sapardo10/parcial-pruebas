package com.bumptech.glide.load.engine.bitmap_recycle;

import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import com.bumptech.glide.util.Preconditions;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public final class LruArrayPool implements ArrayPool {
    private static final int DEFAULT_SIZE = 4194304;
    @VisibleForTesting
    static final int MAX_OVER_SIZE_MULTIPLE = 8;
    private static final int SINGLE_ARRAY_MAX_SIZE_DIVISOR = 2;
    private final Map<Class<?>, ArrayAdapterInterface<?>> adapters;
    private int currentSize;
    private final GroupedLinkedMap<Key, Object> groupedMap;
    private final KeyPool keyPool;
    private final int maxSize;
    private final Map<Class<?>, NavigableMap<Integer, Integer>> sortedSizes;

    private static final class Key implements Poolable {
        private Class<?> arrayClass;
        private final KeyPool pool;
        int size;

        Key(KeyPool pool) {
            this.pool = pool;
        }

        void init(int length, Class<?> arrayClass) {
            this.size = length;
            this.arrayClass = arrayClass;
        }

        public boolean equals(Object o) {
            boolean z = false;
            if (!(o instanceof Key)) {
                return false;
            }
            Key other = (Key) o;
            if (this.size == other.size && this.arrayClass == other.arrayClass) {
                z = true;
            }
            return z;
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Key{size=");
            stringBuilder.append(this.size);
            stringBuilder.append("array=");
            stringBuilder.append(this.arrayClass);
            stringBuilder.append('}');
            return stringBuilder.toString();
        }

        public void offer() {
            this.pool.offer(this);
        }

        public int hashCode() {
            int result = this.size * 31;
            Class cls = this.arrayClass;
            return result + (cls != null ? cls.hashCode() : 0);
        }
    }

    private static final class KeyPool extends BaseKeyPool<Key> {
        KeyPool() {
        }

        Key get(int size, Class<?> arrayClass) {
            Key result = (Key) get();
            result.init(size, arrayClass);
            return result;
        }

        protected Key create() {
            return new Key(this);
        }
    }

    @VisibleForTesting
    public LruArrayPool() {
        this.groupedMap = new GroupedLinkedMap();
        this.keyPool = new KeyPool();
        this.sortedSizes = new HashMap();
        this.adapters = new HashMap();
        this.maxSize = 4194304;
    }

    public LruArrayPool(int maxSize) {
        this.groupedMap = new GroupedLinkedMap();
        this.keyPool = new KeyPool();
        this.sortedSizes = new HashMap();
        this.adapters = new HashMap();
        this.maxSize = maxSize;
    }

    @Deprecated
    public <T> void put(T array, Class<T> cls) {
        put(array);
    }

    public synchronized <T> void put(T array) {
        Class<T> arrayClass = array.getClass();
        ArrayAdapterInterface<T> arrayAdapter = getAdapterFromType(arrayClass);
        int size = arrayAdapter.getArrayLength(array);
        int arrayBytes = arrayAdapter.getElementSizeInBytes() * size;
        if (isSmallEnoughForReuse(arrayBytes)) {
            Key key = this.keyPool.get(size, arrayClass);
            this.groupedMap.put(key, array);
            NavigableMap<Integer, Integer> sizes = getSizesForAdapter(arrayClass);
            Integer current = (Integer) sizes.get(Integer.valueOf(key.size));
            Integer valueOf = Integer.valueOf(key.size);
            int i = 1;
            if (current != null) {
                i = 1 + current.intValue();
            }
            sizes.put(valueOf, Integer.valueOf(i));
            this.currentSize += arrayBytes;
            evict();
        }
    }

    public synchronized <T> T getExact(int size, Class<T> arrayClass) {
        return getForKey(this.keyPool.get(size, arrayClass), arrayClass);
    }

    public synchronized <T> T get(int size, Class<T> arrayClass) {
        Key key;
        Integer possibleSize = (Integer) getSizesForAdapter(arrayClass).ceilingKey(Integer.valueOf(size));
        if (mayFillRequest(size, possibleSize)) {
            key = this.keyPool.get(possibleSize.intValue(), arrayClass);
        } else {
            key = this.keyPool.get(size, arrayClass);
        }
        return getForKey(key, arrayClass);
    }

    private <T> T getForKey(Key key, Class<T> arrayClass) {
        ArrayAdapterInterface<T> arrayAdapter = getAdapterFromType(arrayClass);
        T result = getArrayForKey(key);
        if (result != null) {
            this.currentSize -= arrayAdapter.getArrayLength(result) * arrayAdapter.getElementSizeInBytes();
            decrementArrayOfSize(arrayAdapter.getArrayLength(result), arrayClass);
        }
        if (result != null) {
            return result;
        }
        if (Log.isLoggable(arrayAdapter.getTag(), 2)) {
            String tag = arrayAdapter.getTag();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Allocated ");
            stringBuilder.append(key.size);
            stringBuilder.append(" bytes");
            Log.v(tag, stringBuilder.toString());
        }
        return arrayAdapter.newArray(key.size);
    }

    @Nullable
    private <T> T getArrayForKey(Key key) {
        return this.groupedMap.get(key);
    }

    private boolean isSmallEnoughForReuse(int byteSize) {
        return byteSize <= this.maxSize / 2;
    }

    private boolean mayFillRequest(int requestedSize, Integer actualSize) {
        if (actualSize != null) {
            if (!isNoMoreThanHalfFull()) {
                if (actualSize.intValue() <= requestedSize * 8) {
                }
            }
            return true;
        }
        return false;
    }

    private boolean isNoMoreThanHalfFull() {
        int i = this.currentSize;
        if (i != 0) {
            if (this.maxSize / i < 2) {
                return false;
            }
        }
        return true;
    }

    public synchronized void clearMemory() {
        evictToSize(0);
    }

    public synchronized void trimMemory(int level) {
        if (level >= 40) {
            clearMemory();
        } else {
            if (level < 20) {
                if (level == 15) {
                }
            }
            evictToSize(this.maxSize / 2);
        }
    }

    private void evict() {
        evictToSize(this.maxSize);
    }

    private void evictToSize(int size) {
        while (this.currentSize > size) {
            Object evicted = this.groupedMap.removeLast();
            Preconditions.checkNotNull(evicted);
            ArrayAdapterInterface<Object> arrayAdapter = getAdapterFromObject(evicted);
            this.currentSize -= arrayAdapter.getArrayLength(evicted) * arrayAdapter.getElementSizeInBytes();
            decrementArrayOfSize(arrayAdapter.getArrayLength(evicted), evicted.getClass());
            if (Log.isLoggable(arrayAdapter.getTag(), 2)) {
                String tag = arrayAdapter.getTag();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("evicted: ");
                stringBuilder.append(arrayAdapter.getArrayLength(evicted));
                Log.v(tag, stringBuilder.toString());
            }
        }
    }

    private void decrementArrayOfSize(int size, Class<?> arrayClass) {
        NavigableMap<Integer, Integer> sizes = getSizesForAdapter(arrayClass);
        Integer current = (Integer) sizes.get(Integer.valueOf(size));
        if (current == null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Tried to decrement empty size, size: ");
            stringBuilder.append(size);
            stringBuilder.append(", this: ");
            stringBuilder.append(this);
            throw new NullPointerException(stringBuilder.toString());
        } else if (current.intValue() == 1) {
            sizes.remove(Integer.valueOf(size));
        } else {
            sizes.put(Integer.valueOf(size), Integer.valueOf(current.intValue() - 1));
        }
    }

    private NavigableMap<Integer, Integer> getSizesForAdapter(Class<?> arrayClass) {
        NavigableMap<Integer, Integer> sizes = (NavigableMap) this.sortedSizes.get(arrayClass);
        if (sizes != null) {
            return sizes;
        }
        TreeMap sizes2 = new TreeMap();
        this.sortedSizes.put(arrayClass, sizes2);
        return sizes2;
    }

    private <T> ArrayAdapterInterface<T> getAdapterFromObject(T object) {
        return getAdapterFromType(object.getClass());
    }

    private <T> ArrayAdapterInterface<T> getAdapterFromType(Class<T> arrayPoolClass) {
        ArrayAdapterInterface<?> adapter = (ArrayAdapterInterface) this.adapters.get(arrayPoolClass);
        if (adapter == null) {
            if (arrayPoolClass.equals(int[].class)) {
                adapter = new IntegerArrayAdapter();
            } else if (arrayPoolClass.equals(byte[].class)) {
                adapter = new ByteArrayAdapter();
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("No array pool found for: ");
                stringBuilder.append(arrayPoolClass.getSimpleName());
                throw new IllegalArgumentException(stringBuilder.toString());
            }
            this.adapters.put(arrayPoolClass, adapter);
        }
        return adapter;
    }

    int getCurrentSize() {
        int currentSize = 0;
        for (Class<?> type : this.sortedSizes.keySet()) {
            for (Integer size : ((NavigableMap) this.sortedSizes.get(type)).keySet()) {
                currentSize += (size.intValue() * ((Integer) ((NavigableMap) this.sortedSizes.get(type)).get(size)).intValue()) * getAdapterFromType(type).getElementSizeInBytes();
            }
        }
        return currentSize;
    }
}
