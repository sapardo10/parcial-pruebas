package org.apache.commons.lang3;

import java.io.Serializable;
import java.util.Comparator;

public final class Range<T> implements Serializable {
    private static final long serialVersionUID = 1;
    private final Comparator<T> comparator;
    private transient int hashCode;
    private final T maximum;
    private final T minimum;
    private transient String toString;

    private enum ComparableComparator implements Comparator {
        INSTANCE;

        public int compare(Object obj1, Object obj2) {
            return ((Comparable) obj1).compareTo(obj2);
        }
    }

    public static <T extends Comparable<T>> Range<T> is(T element) {
        return between(element, element, null);
    }

    public static <T> Range<T> is(T element, Comparator<T> comparator) {
        return between(element, element, comparator);
    }

    public static <T extends Comparable<T>> Range<T> between(T fromInclusive, T toInclusive) {
        return between(fromInclusive, toInclusive, null);
    }

    public static <T> Range<T> between(T fromInclusive, T toInclusive, Comparator<T> comparator) {
        return new Range(fromInclusive, toInclusive, comparator);
    }

    private Range(T element1, T element2, Comparator<T> comp) {
        if (element1 == null || element2 == null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Elements in a range must not be null: element1=");
            stringBuilder.append(element1);
            stringBuilder.append(", element2=");
            stringBuilder.append(element2);
            throw new IllegalArgumentException(stringBuilder.toString());
        }
        if (comp == null) {
            this.comparator = ComparableComparator.INSTANCE;
        } else {
            this.comparator = comp;
        }
        if (this.comparator.compare(element1, element2) < 1) {
            this.minimum = element1;
            this.maximum = element2;
            return;
        }
        this.minimum = element2;
        this.maximum = element1;
    }

    public T getMinimum() {
        return this.minimum;
    }

    public T getMaximum() {
        return this.maximum;
    }

    public Comparator<T> getComparator() {
        return this.comparator;
    }

    public boolean isNaturalOrdering() {
        return this.comparator == ComparableComparator.INSTANCE;
    }

    public boolean contains(T element) {
        boolean z = false;
        if (element == null) {
            return false;
        }
        if (this.comparator.compare(element, this.minimum) > -1 && this.comparator.compare(element, this.maximum) < 1) {
            z = true;
        }
        return z;
    }

    public boolean isAfter(T element) {
        boolean z = false;
        if (element == null) {
            return false;
        }
        if (this.comparator.compare(element, this.minimum) < 0) {
            z = true;
        }
        return z;
    }

    public boolean isStartedBy(T element) {
        boolean z = false;
        if (element == null) {
            return false;
        }
        if (this.comparator.compare(element, this.minimum) == 0) {
            z = true;
        }
        return z;
    }

    public boolean isEndedBy(T element) {
        boolean z = false;
        if (element == null) {
            return false;
        }
        if (this.comparator.compare(element, this.maximum) == 0) {
            z = true;
        }
        return z;
    }

    public boolean isBefore(T element) {
        boolean z = false;
        if (element == null) {
            return false;
        }
        if (this.comparator.compare(element, this.maximum) > 0) {
            z = true;
        }
        return z;
    }

    public int elementCompareTo(T element) {
        Validate.notNull(element, "Element is null", new Object[0]);
        if (isAfter(element)) {
            return -1;
        }
        if (isBefore(element)) {
            return 1;
        }
        return 0;
    }

    public boolean containsRange(Range<T> otherRange) {
        boolean z = false;
        if (otherRange == null) {
            return false;
        }
        if (contains(otherRange.minimum) && contains(otherRange.maximum)) {
            z = true;
        }
        return z;
    }

    public boolean isAfterRange(Range<T> otherRange) {
        if (otherRange == null) {
            return false;
        }
        return isAfter(otherRange.maximum);
    }

    public boolean isOverlappedBy(Range<T> otherRange) {
        boolean z = false;
        if (otherRange == null) {
            return false;
        }
        if (!(otherRange.contains(this.minimum) || otherRange.contains(this.maximum))) {
            if (!contains(otherRange.minimum)) {
                return z;
            }
        }
        z = true;
        return z;
    }

    public boolean isBeforeRange(Range<T> otherRange) {
        if (otherRange == null) {
            return false;
        }
        return isBefore(otherRange.minimum);
    }

    public Range<T> intersectionWith(Range<T> other) {
        if (!isOverlappedBy(other)) {
            throw new IllegalArgumentException(String.format("Cannot calculate intersection with non-overlapping range %s", new Object[]{other}));
        } else if (equals(other)) {
            return this;
        } else {
            return between(getComparator().compare(this.minimum, other.minimum) < 0 ? other.minimum : this.minimum, getComparator().compare(this.maximum, other.maximum) < 0 ? this.maximum : other.maximum, getComparator());
        }
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (obj == this) {
            return true;
        }
        if (obj != null) {
            if (obj.getClass() == getClass()) {
                Range<T> range = (Range) obj;
                if (!this.minimum.equals(range.minimum) || !this.maximum.equals(range.maximum)) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }

    public int hashCode() {
        int result = this.hashCode;
        if (this.hashCode != 0) {
            return result;
        }
        result = (((((17 * 37) + getClass().hashCode()) * 37) + this.minimum.hashCode()) * 37) + this.maximum.hashCode();
        this.hashCode = result;
        return result;
    }

    public String toString() {
        if (this.toString == null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("[");
            stringBuilder.append(this.minimum);
            stringBuilder.append("..");
            stringBuilder.append(this.maximum);
            stringBuilder.append("]");
            this.toString = stringBuilder.toString();
        }
        return this.toString;
    }

    public String toString(String format) {
        return String.format(format, new Object[]{this.minimum, this.maximum, this.comparator});
    }
}
