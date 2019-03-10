package de.danoeh.antennapod.core.util;

import java.util.Arrays;

public final class LongList {
    private int size;
    private long[] values;

    public LongList() {
        this(4);
    }

    public LongList(int initialCapacity) {
        if (initialCapacity >= 0) {
            this.values = new long[initialCapacity];
            this.size = 0;
            return;
        }
        throw new IllegalArgumentException("initial capacity must be 0 or higher");
    }

    public static LongList of(long... values) {
        int i = 0;
        if (values != null) {
            if (values.length != 0) {
                LongList result = new LongList(values.length);
                int length = values.length;
                while (i < length) {
                    result.add(values[i]);
                    i++;
                }
                return result;
            }
        }
        return new LongList(0);
    }

    public int hashCode() {
        int hashCode = 1;
        for (int i = 0; i < this.size; i++) {
            long value = this.values[i];
            hashCode = (hashCode * 31) + ((int) ((value >>> 32) ^ value));
        }
        return hashCode;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof LongList)) {
            return false;
        }
        LongList otherList = (LongList) other;
        if (this.size != otherList.size) {
            return false;
        }
        for (int i = 0; i < this.size; i++) {
            if (this.values[i] != otherList.values[i]) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder((this.size * 5) + 10);
        sb.append("LongList{");
        for (int i = 0; i < this.size; i++) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append(this.values[i]);
        }
        sb.append("}");
        return sb.toString();
    }

    public int size() {
        return this.size;
    }

    public long get(int n) {
        if (n >= this.size) {
            throw new IndexOutOfBoundsException("n >= size()");
        } else if (n >= 0) {
            return this.values[n];
        } else {
            throw new IndexOutOfBoundsException("n < 0");
        }
    }

    public long set(int index, long value) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("n >= size()");
        } else if (index >= 0) {
            long[] jArr = this.values;
            long result = jArr[index];
            jArr[index] = value;
            return result;
        } else {
            throw new IndexOutOfBoundsException("n < 0");
        }
    }

    public void add(long value) {
        growIfNeeded();
        long[] jArr = this.values;
        int i = this.size;
        this.size = i + 1;
        jArr[i] = value;
    }

    public void insert(int n, int value) {
        if (n > this.size) {
            throw new IndexOutOfBoundsException("n > size()");
        } else if (n >= 0) {
            growIfNeeded();
            Object obj = this.values;
            System.arraycopy(obj, n, obj, n + 1, this.size - n);
            this.values[n] = (long) value;
            this.size++;
        } else {
            throw new IndexOutOfBoundsException("n < 0");
        }
    }

    public boolean remove(long value) {
        int i = 0;
        while (true) {
            int i2 = this.size;
            if (i >= i2) {
                return false;
            }
            Object obj = this.values;
            if (obj[i] == value) {
                this.size = i2 - 1;
                System.arraycopy(obj, i + 1, obj, i, this.size - i);
                return true;
            }
            i++;
        }
    }

    public void removeAll(long[] values) {
        for (long value : values) {
            remove(value);
        }
    }

    public void removeAll(LongList list) {
        for (long value : list.values) {
            remove(value);
        }
    }

    public void removeIndex(int index) {
        int i = this.size;
        if (index >= i) {
            throw new IndexOutOfBoundsException("n >= size()");
        } else if (index >= 0) {
            this.size = i - 1;
            Object obj = this.values;
            System.arraycopy(obj, index + 1, obj, index, this.size - index);
        } else {
            throw new IndexOutOfBoundsException("n < 0");
        }
    }

    private void growIfNeeded() {
        int i = this.size;
        Object obj = this.values;
        if (i == obj.length) {
            long[] newArray = new long[(((i * 3) / 2) + 10)];
            System.arraycopy(obj, 0, newArray, 0, i);
            this.values = newArray;
        }
    }

    public int indexOf(long value) {
        for (int i = 0; i < this.size; i++) {
            if (this.values[i] == value) {
                return i;
            }
        }
        return -1;
    }

    public void clear() {
        this.values = new long[4];
        this.size = 0;
    }

    public boolean contains(long value) {
        return indexOf(value) >= 0;
    }

    public long[] toArray() {
        return Arrays.copyOf(this.values, this.size);
    }
}
