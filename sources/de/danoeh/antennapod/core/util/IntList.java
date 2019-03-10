package de.danoeh.antennapod.core.util;

import java.util.Arrays;

public final class IntList {
    private int size;
    private int[] values;

    public IntList() {
        this(4);
    }

    private IntList(int initialCapacity) {
        if (initialCapacity >= 0) {
            this.values = new int[initialCapacity];
            this.size = 0;
            return;
        }
        throw new IllegalArgumentException("initial capacity must be 0 or higher");
    }

    public int hashCode() {
        int hashCode = 1;
        for (int i = 0; i < this.size; i++) {
            hashCode = (hashCode * 31) + this.values[i];
        }
        return hashCode;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof IntList)) {
            return false;
        }
        IntList otherList = (IntList) other;
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
        sb.append("IntList{");
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

    public int get(int n) {
        if (n >= this.size) {
            throw new IndexOutOfBoundsException("n >= size()");
        } else if (n >= 0) {
            return this.values[n];
        } else {
            throw new IndexOutOfBoundsException("n < 0");
        }
    }

    public int set(int index, int value) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("n >= size()");
        } else if (index >= 0) {
            int[] iArr = this.values;
            int result = iArr[index];
            iArr[index] = value;
            return result;
        } else {
            throw new IndexOutOfBoundsException("n < 0");
        }
    }

    public void add(int value) {
        growIfNeeded();
        int[] iArr = this.values;
        int i = this.size;
        this.size = i + 1;
        iArr[i] = value;
    }

    public void insert(int n, int value) {
        if (n > this.size) {
            throw new IndexOutOfBoundsException("n > size()");
        } else if (n >= 0) {
            growIfNeeded();
            Object obj = this.values;
            System.arraycopy(obj, n, obj, n + 1, this.size - n);
            this.values[n] = value;
            this.size++;
        } else {
            throw new IndexOutOfBoundsException("n < 0");
        }
    }

    public boolean remove(int value) {
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
            int[] newArray = new int[(((i * 3) / 2) + 10)];
            System.arraycopy(obj, 0, newArray, 0, i);
            this.values = newArray;
        }
    }

    private int indexOf(int value) {
        for (int i = 0; i < this.size; i++) {
            if (this.values[i] == value) {
                return i;
            }
        }
        return -1;
    }

    public void clear() {
        this.values = new int[4];
        this.size = 0;
    }

    public boolean contains(int value) {
        return indexOf(value) >= 0;
    }

    public int[] toArray() {
        return Arrays.copyOf(this.values, this.size);
    }
}
