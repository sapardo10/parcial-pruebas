package com.bumptech.glide.load.engine.bitmap_recycle;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.support.annotation.VisibleForTesting;
import com.bumptech.glide.util.Util;

class AttributeStrategy implements LruPoolStrategy {
    private final GroupedLinkedMap<Key, Bitmap> groupedMap = new GroupedLinkedMap();
    private final KeyPool keyPool = new KeyPool();

    @VisibleForTesting
    static class Key implements Poolable {
        private Config config;
        private int height;
        private final KeyPool pool;
        private int width;

        public Key(KeyPool pool) {
            this.pool = pool;
        }

        public void init(int width, int height, Config config) {
            this.width = width;
            this.height = height;
            this.config = config;
        }

        public boolean equals(Object o) {
            boolean z = false;
            if (!(o instanceof Key)) {
                return false;
            }
            Key other = (Key) o;
            if (this.width == other.width && this.height == other.height && this.config == other.config) {
                z = true;
            }
            return z;
        }

        public int hashCode() {
            int i = ((this.width * 31) + this.height) * 31;
            Config config = this.config;
            return i + (config != null ? config.hashCode() : 0);
        }

        public String toString() {
            return AttributeStrategy.getBitmapString(this.width, this.height, this.config);
        }

        public void offer() {
            this.pool.offer(this);
        }
    }

    @VisibleForTesting
    static class KeyPool extends BaseKeyPool<Key> {
        KeyPool() {
        }

        Key get(int width, int height, Config config) {
            Key result = (Key) get();
            result.init(width, height, config);
            return result;
        }

        protected Key create() {
            return new Key(this);
        }
    }

    AttributeStrategy() {
    }

    public void put(Bitmap bitmap) {
        this.groupedMap.put(this.keyPool.get(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig()), bitmap);
    }

    public Bitmap get(int width, int height, Config config) {
        return (Bitmap) this.groupedMap.get(this.keyPool.get(width, height, config));
    }

    public Bitmap removeLast() {
        return (Bitmap) this.groupedMap.removeLast();
    }

    public String logBitmap(Bitmap bitmap) {
        return getBitmapString(bitmap);
    }

    public String logBitmap(int width, int height, Config config) {
        return getBitmapString(width, height, config);
    }

    public int getSize(Bitmap bitmap) {
        return Util.getBitmapByteSize(bitmap);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("AttributeStrategy:\n  ");
        stringBuilder.append(this.groupedMap);
        return stringBuilder.toString();
    }

    private static String getBitmapString(Bitmap bitmap) {
        return getBitmapString(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
    }

    static String getBitmapString(int width, int height, Config config) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        stringBuilder.append(width);
        stringBuilder.append("x");
        stringBuilder.append(height);
        stringBuilder.append("], ");
        stringBuilder.append(config);
        return stringBuilder.toString();
    }
}
