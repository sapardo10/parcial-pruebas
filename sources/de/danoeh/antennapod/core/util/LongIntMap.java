package de.danoeh.antennapod.core.util;

import java.util.Arrays;

public class LongIntMap {
    private long[] keys;
    private int size;
    private int[] values;

    public LongIntMap() {
        this(10);
    }

    public LongIntMap(int initialCapacity) {
        if (initialCapacity >= 0) {
            this.keys = new long[initialCapacity];
            this.values = new int[initialCapacity];
            this.size = 0;
            return;
        }
        throw new IllegalArgumentException("initial capacity must be 0 or higher");
    }

    private void growIfNeeded() {
        int i = this.size;
        Object obj = this.keys;
        if (i == obj.length) {
            long[] newKeysArray = new long[(((i * 3) / 2) + 10)];
            int[] newValuesArray = new int[(((i * 3) / 2) + 10)];
            System.arraycopy(obj, 0, newKeysArray, 0, i);
            System.arraycopy(this.values, 0, newValuesArray, 0, this.size);
            this.keys = newKeysArray;
            this.values = newValuesArray;
        }
    }

    public int get(long key) {
        return get(key, 0);
    }

    public int get(long key, int valueIfKeyNotFound) {
        int index = indexOfKey(key);
        if (index >= 0) {
            return this.values[index];
        }
        return valueIfKeyNotFound;
    }

    public boolean delete(long key) {
        int index = indexOfKey(key);
        if (index < 0) {
            return false;
        }
        removeAt(index);
        return true;
    }

    private void removeAt(int index) {
        Object obj = this.keys;
        System.arraycopy(obj, index + 1, obj, index, this.size - (index + 1));
        obj = this.values;
        System.arraycopy(obj, index + 1, obj, index, this.size - (index + 1));
        this.size--;
    }

    public void put(long key, int value) {
        int index = indexOfKey(key);
        if (index >= 0) {
            this.values[index] = value;
            return;
        }
        growIfNeeded();
        long[] jArr = this.keys;
        int i = this.size;
        jArr[i] = key;
        this.values[i] = value;
        this.size = i + 1;
    }

    public int size() {
        return this.size;
    }

    private long keyAt(int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("n >= size()");
        } else if (index >= 0) {
            return this.keys[index];
        } else {
            throw new IndexOutOfBoundsException("n < 0");
        }
    }

    private int valueAt(int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("n >= size()");
        } else if (index >= 0) {
            return this.values[index];
        } else {
            throw new IndexOutOfBoundsException("n < 0");
        }
    }

    public int indexOfKey(long key) {
        for (int i = 0; i < this.size; i++) {
            if (this.keys[i] == key) {
                return i;
            }
        }
        return -1;
    }

    public int indexOfValue(long value) {
        for (int i = 0; i < this.size; i++) {
            if (((long) this.values[i]) == value) {
                return i;
            }
        }
        return -1;
    }

    public void clear() {
        this.keys = new long[10];
        this.values = new int[10];
        this.size = 0;
    }

    public int[] values() {
        return Arrays.copyOf(this.values, this.size);
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof LongIntMap)) {
            return false;
        }
        LongIntMap otherMap = (LongIntMap) other;
        if (this.size != otherMap.size) {
            return false;
        }
        int i = 0;
        while (i < this.size) {
            if (this.keys[i] == otherMap.keys[i]) {
                if (this.values[i] == otherMap.values[i]) {
                    i++;
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        for (int i = 0; i < this.size; i++) {
            long value = (long) this.values[i];
            hashCode = (hashCode * 31) + ((int) ((value >>> 32) ^ value));
        }
        return hashCode;
    }

    public String toString() {
        if (size() <= 0) {
            return "LongLongMap{}";
        }
        StringBuilder buffer = new StringBuilder(this.size * 28);
        buffer.append("LongLongMap{");
        for (int i = 0; i < this.size; i++) {
            if (i > 0) {
                buffer.append(", ");
            }
            buffer.append(keyAt(i));
            buffer.append('=');
            buffer.append((long) valueAt(i));
        }
        buffer.append('}');
        return buffer.toString();
    }
}
