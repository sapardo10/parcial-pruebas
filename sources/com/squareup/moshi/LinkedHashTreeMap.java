package com.squareup.moshi;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

final class LinkedHashTreeMap<K, V> extends AbstractMap<K, V> implements Serializable {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final Comparator<Comparable> NATURAL_ORDER = new C06891();
    Comparator<? super K> comparator;
    private EntrySet entrySet;
    final Node<K, V> header;
    private KeySet keySet;
    int modCount;
    int size;
    Node<K, V>[] table;
    int threshold;

    /* renamed from: com.squareup.moshi.LinkedHashTreeMap$1 */
    class C06891 implements Comparator<Comparable> {
        C06891() {
        }

        public int compare(Comparable a, Comparable b) {
            return a.compareTo(b);
        }
    }

    static final class AvlBuilder<K, V> {
        private int leavesSkipped;
        private int leavesToSkip;
        private int size;
        private Node<K, V> stack;

        AvlBuilder() {
        }

        void reset(int targetSize) {
            this.leavesToSkip = ((Integer.highestOneBit(targetSize) * 2) - 1) - targetSize;
            this.size = 0;
            this.leavesSkipped = 0;
            this.stack = null;
        }

        void add(Node<K, V> node) {
            int i;
            Node<K, V> right;
            Node<K, V> center;
            Node<K, V> left;
            node.right = null;
            node.parent = null;
            node.left = null;
            node.height = 1;
            int i2 = this.leavesToSkip;
            if (i2 > 0) {
                i = this.size;
                if ((i & 1) == 0) {
                    this.size = i + 1;
                    this.leavesToSkip = i2 - 1;
                    this.leavesSkipped++;
                    node.parent = this.stack;
                    this.stack = node;
                    this.size++;
                    i2 = this.leavesToSkip;
                    if (i2 > 0) {
                        i = this.size;
                        if ((i & 1) == 0) {
                            this.size = i + 1;
                            this.leavesToSkip = i2 - 1;
                            this.leavesSkipped++;
                            for (i2 = 4; (this.size & (i2 - 1)) == i2 - 1; i2 *= 2) {
                                i = this.leavesSkipped;
                                if (i != 0) {
                                    right = this.stack;
                                    center = right.parent;
                                    left = center.parent;
                                    center.parent = left.parent;
                                    this.stack = center;
                                    center.left = left;
                                    center.right = right;
                                    center.height = right.height + 1;
                                    left.parent = center;
                                    right.parent = center;
                                } else if (i != 1) {
                                    right = this.stack;
                                    left = right.parent;
                                    this.stack = left;
                                    left.right = right;
                                    left.height = right.height + 1;
                                    right.parent = left;
                                    this.leavesSkipped = 0;
                                } else if (i == 2) {
                                    this.leavesSkipped = 0;
                                }
                            }
                        }
                    }
                    for (i2 = 4; (this.size & (i2 - 1)) == i2 - 1; i2 *= 2) {
                        i = this.leavesSkipped;
                        if (i != 0) {
                            right = this.stack;
                            center = right.parent;
                            left = center.parent;
                            center.parent = left.parent;
                            this.stack = center;
                            center.left = left;
                            center.right = right;
                            center.height = right.height + 1;
                            left.parent = center;
                            right.parent = center;
                        } else if (i != 1) {
                            right = this.stack;
                            left = right.parent;
                            this.stack = left;
                            left.right = right;
                            left.height = right.height + 1;
                            right.parent = left;
                            this.leavesSkipped = 0;
                        } else if (i == 2) {
                            this.leavesSkipped = 0;
                        }
                    }
                }
            }
            node.parent = this.stack;
            this.stack = node;
            this.size++;
            i2 = this.leavesToSkip;
            if (i2 > 0) {
                i = this.size;
                if ((i & 1) == 0) {
                    this.size = i + 1;
                    this.leavesToSkip = i2 - 1;
                    this.leavesSkipped++;
                    for (i2 = 4; (this.size & (i2 - 1)) == i2 - 1; i2 *= 2) {
                        i = this.leavesSkipped;
                        if (i != 0) {
                            right = this.stack;
                            center = right.parent;
                            left = center.parent;
                            center.parent = left.parent;
                            this.stack = center;
                            center.left = left;
                            center.right = right;
                            center.height = right.height + 1;
                            left.parent = center;
                            right.parent = center;
                        } else if (i != 1) {
                            right = this.stack;
                            left = right.parent;
                            this.stack = left;
                            left.right = right;
                            left.height = right.height + 1;
                            right.parent = left;
                            this.leavesSkipped = 0;
                        } else if (i == 2) {
                            this.leavesSkipped = 0;
                        }
                    }
                }
            }
            for (i2 = 4; (this.size & (i2 - 1)) == i2 - 1; i2 *= 2) {
                i = this.leavesSkipped;
                if (i != 0) {
                    right = this.stack;
                    center = right.parent;
                    left = center.parent;
                    center.parent = left.parent;
                    this.stack = center;
                    center.left = left;
                    center.right = right;
                    center.height = right.height + 1;
                    left.parent = center;
                    right.parent = center;
                } else if (i != 1) {
                    right = this.stack;
                    left = right.parent;
                    this.stack = left;
                    left.right = right;
                    left.height = right.height + 1;
                    right.parent = left;
                    this.leavesSkipped = 0;
                } else if (i == 2) {
                    this.leavesSkipped = 0;
                }
            }
        }

        Node<K, V> root() {
            Node<K, V> stackTop = this.stack;
            if (stackTop.parent == null) {
                return stackTop;
            }
            throw new IllegalStateException();
        }
    }

    static class AvlIterator<K, V> {
        private Node<K, V> stackTop;

        AvlIterator() {
        }

        void reset(Node<K, V> root) {
            Node<K, V> stackTop = null;
            for (Node<K, V> n = root; n != null; n = n.left) {
                n.parent = stackTop;
                stackTop = n;
            }
            this.stackTop = stackTop;
        }

        public Node<K, V> next() {
            Node<K, V> stackTop = this.stackTop;
            if (stackTop == null) {
                return null;
            }
            Node<K, V> result = stackTop;
            stackTop = result.parent;
            result.parent = null;
            for (Node<K, V> n = result.right; n != null; n = n.left) {
                n.parent = stackTop;
                stackTop = n;
            }
            this.stackTop = stackTop;
            return result;
        }
    }

    final class EntrySet extends AbstractSet<Entry<K, V>> {

        /* renamed from: com.squareup.moshi.LinkedHashTreeMap$EntrySet$1 */
        class C09931 extends LinkedTreeMapIterator<Entry<K, V>> {
            C09931() {
                super();
            }

            public Entry<K, V> next() {
                return nextNode();
            }
        }

        EntrySet() {
        }

        public int size() {
            return LinkedHashTreeMap.this.size;
        }

        public Iterator<Entry<K, V>> iterator() {
            return new C09931();
        }

        public boolean contains(Object o) {
            return (o instanceof Entry) && LinkedHashTreeMap.this.findByEntry((Entry) o) != null;
        }

        public boolean remove(Object o) {
            if (!(o instanceof Entry)) {
                return false;
            }
            Node<K, V> node = LinkedHashTreeMap.this.findByEntry((Entry) o);
            if (node == null) {
                return false;
            }
            LinkedHashTreeMap.this.removeInternal(node, true);
            return true;
        }

        public void clear() {
            LinkedHashTreeMap.this.clear();
        }
    }

    final class KeySet extends AbstractSet<K> {

        /* renamed from: com.squareup.moshi.LinkedHashTreeMap$KeySet$1 */
        class C09941 extends LinkedTreeMapIterator<K> {
            C09941() {
                super();
            }

            public K next() {
                return nextNode().key;
            }
        }

        KeySet() {
        }

        public int size() {
            return LinkedHashTreeMap.this.size;
        }

        public Iterator<K> iterator() {
            return new C09941();
        }

        public boolean contains(Object o) {
            return LinkedHashTreeMap.this.containsKey(o);
        }

        public boolean remove(Object key) {
            return LinkedHashTreeMap.this.removeInternalByKey(key) != null;
        }

        public void clear() {
            LinkedHashTreeMap.this.clear();
        }
    }

    abstract class LinkedTreeMapIterator<T> implements Iterator<T> {
        int expectedModCount = LinkedHashTreeMap.this.modCount;
        Node<K, V> lastReturned = null;
        Node<K, V> next = LinkedHashTreeMap.this.header.next;

        LinkedTreeMapIterator() {
        }

        public final boolean hasNext() {
            return this.next != LinkedHashTreeMap.this.header;
        }

        final Node<K, V> nextNode() {
            Node<K, V> e = this.next;
            if (e == LinkedHashTreeMap.this.header) {
                throw new NoSuchElementException();
            } else if (LinkedHashTreeMap.this.modCount == this.expectedModCount) {
                this.next = e.next;
                this.lastReturned = e;
                return e;
            } else {
                throw new ConcurrentModificationException();
            }
        }

        public final void remove() {
            Node node = this.lastReturned;
            if (node != null) {
                LinkedHashTreeMap.this.removeInternal(node, true);
                this.lastReturned = null;
                this.expectedModCount = LinkedHashTreeMap.this.modCount;
                return;
            }
            throw new IllegalStateException();
        }
    }

    static final class Node<K, V> implements Entry<K, V> {
        final int hash;
        int height;
        final K key;
        Node<K, V> left;
        Node<K, V> next;
        Node<K, V> parent;
        Node<K, V> prev;
        Node<K, V> right;
        V value;

        Node() {
            this.key = null;
            this.hash = -1;
            this.prev = this;
            this.next = this;
        }

        Node(Node<K, V> parent, K key, int hash, Node<K, V> next, Node<K, V> prev) {
            this.parent = parent;
            this.key = key;
            this.hash = hash;
            this.height = 1;
            this.next = next;
            this.prev = prev;
            prev.next = this;
            next.prev = this;
        }

        public K getKey() {
            return this.key;
        }

        public V getValue() {
            return this.value;
        }

        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean equals(java.lang.Object r5) {
            /*
            r4 = this;
            r0 = r5 instanceof java.util.Map.Entry;
            r1 = 0;
            if (r0 == 0) goto L_0x0038;
        L_0x0005:
            r0 = r5;
            r0 = (java.util.Map.Entry) r0;
            r2 = r4.key;
            if (r2 != 0) goto L_0x0013;
        L_0x000c:
            r2 = r0.getKey();
            if (r2 != 0) goto L_0x0035;
        L_0x0012:
            goto L_0x001d;
        L_0x0013:
            r3 = r0.getKey();
            r2 = r2.equals(r3);
            if (r2 == 0) goto L_0x0035;
        L_0x001d:
            r2 = r4.value;
            if (r2 != 0) goto L_0x0028;
        L_0x0021:
            r2 = r0.getValue();
            if (r2 != 0) goto L_0x0034;
        L_0x0027:
            goto L_0x0032;
        L_0x0028:
            r3 = r0.getValue();
            r2 = r2.equals(r3);
            if (r2 == 0) goto L_0x0034;
        L_0x0032:
            r1 = 1;
            goto L_0x0037;
        L_0x0034:
            goto L_0x0036;
        L_0x0037:
            return r1;
        L_0x0038:
            return r1;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.squareup.moshi.LinkedHashTreeMap.Node.equals(java.lang.Object):boolean");
        }

        public int hashCode() {
            Object obj = this.key;
            int i = 0;
            int hashCode = obj == null ? 0 : obj.hashCode();
            Object obj2 = this.value;
            if (obj2 != null) {
                i = obj2.hashCode();
            }
            return hashCode ^ i;
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(this.key);
            stringBuilder.append("=");
            stringBuilder.append(this.value);
            return stringBuilder.toString();
        }

        public Node<K, V> first() {
            Node<K, V> node;
            Node<K, V> child = this.left;
            while (child != null) {
                node = child;
                child = node.left;
            }
            return node;
        }

        public Node<K, V> last() {
            Node<K, V> node;
            Node<K, V> child = this.right;
            while (child != null) {
                node = child;
                child = node.right;
            }
            return node;
        }
    }

    LinkedHashTreeMap() {
        this(null);
    }

    LinkedHashTreeMap(Comparator<? super K> comparator) {
        Comparator comparator2;
        this.size = 0;
        this.modCount = 0;
        if (comparator != null) {
            comparator2 = comparator;
        } else {
            comparator2 = NATURAL_ORDER;
        }
        this.comparator = comparator2;
        this.header = new Node();
        this.table = new Node[16];
        Node[] nodeArr = this.table;
        this.threshold = (nodeArr.length / 2) + (nodeArr.length / 4);
    }

    public int size() {
        return this.size;
    }

    public V get(Object key) {
        Node<K, V> node = findByObject(key);
        return node != null ? node.value : null;
    }

    public boolean containsKey(Object key) {
        return findByObject(key) != null;
    }

    public V put(K key, V value) {
        if (key != null) {
            Node<K, V> created = find(key, true);
            V result = created.value;
            created.value = value;
            return result;
        }
        throw new NullPointerException("key == null");
    }

    public void clear() {
        Arrays.fill(this.table, null);
        this.size = 0;
        this.modCount++;
        Node<K, V> header = this.header;
        Node<K, V> e = header.next;
        while (e != header) {
            Node<K, V> next = e.next;
            e.prev = null;
            e.next = null;
            e = next;
        }
        header.prev = header;
        header.next = header;
    }

    public V remove(Object key) {
        Node<K, V> node = removeInternalByKey(key);
        return node != null ? node.value : null;
    }

    Node<K, V> find(K key, boolean create) {
        int comparison;
        Node<K, V> nearest;
        int comparison2;
        K k = key;
        Comparator<? super K> comparator = this.comparator;
        Node<K, V>[] table = this.table;
        int hash = secondaryHash(key.hashCode());
        int index = hash & (table.length - 1);
        Node<K, V> nearest2 = table[index];
        if (nearest2 != null) {
            Comparable<Object> comparableKey;
            if (comparator == NATURAL_ORDER) {
                comparableKey = (Comparable) k;
            } else {
                comparableKey = null;
            }
            while (true) {
                int compareTo;
                if (comparableKey != null) {
                    compareTo = comparableKey.compareTo(nearest2.key);
                } else {
                    compareTo = comparator.compare(k, nearest2.key);
                }
                comparison = compareTo;
                if (comparison == 0) {
                    return nearest2;
                }
                Node<K, V> child = comparison < 0 ? nearest2.left : nearest2.right;
                if (child == null) {
                    break;
                }
                nearest2 = child;
            }
            nearest = nearest2;
            comparison2 = comparison;
        } else {
            nearest = nearest2;
            comparison2 = 0;
        }
        if (!create) {
            return null;
        }
        Node<K, V> header = r0.header;
        if (nearest == null) {
            if (comparator == NATURAL_ORDER) {
                if (!(k instanceof Comparable)) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(key.getClass().getName());
                    stringBuilder.append(" is not Comparable");
                    throw new ClassCastException(stringBuilder.toString());
                }
            }
            nearest2 = new Node(nearest, key, hash, header, header.prev);
            table[index] = nearest2;
        } else {
            nearest2 = new Node(nearest, key, hash, header, header.prev);
            if (comparison2 < 0) {
                nearest.left = nearest2;
            } else {
                nearest.right = nearest2;
            }
            rebalance(nearest, true);
        }
        comparison = r0.size;
        r0.size = comparison + 1;
        if (comparison > r0.threshold) {
            doubleCapacity();
        }
        r0.modCount++;
        return nearest2;
    }

    Node<K, V> findByObject(Object key) {
        Node<K, V> node = null;
        if (key != null) {
            try {
                node = find(key, false);
            } catch (ClassCastException e) {
                return node;
            }
        }
        return node;
    }

    Node<K, V> findByEntry(Entry<?, ?> entry) {
        Node<K, V> mine = findByObject(entry.getKey());
        boolean valuesEqual = mine != null && equal(mine.value, entry.getValue());
        return valuesEqual ? mine : null;
    }

    private boolean equal(Object a, Object b) {
        if (a != b) {
            if (a == null || !a.equals(b)) {
                return false;
            }
        }
        return true;
    }

    private static int secondaryHash(int h) {
        h ^= (h >>> 20) ^ (h >>> 12);
        return ((h >>> 7) ^ h) ^ (h >>> 4);
    }

    void removeInternal(Node<K, V> node, boolean unlink) {
        if (unlink) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
            node.prev = null;
            node.next = null;
        }
        Node<K, V> left = node.left;
        Node<K, V> right = node.right;
        Node<K, V> originalParent = node.parent;
        if (left == null || right == null) {
            if (left != null) {
                replaceInParent(node, left);
                node.left = null;
            } else if (right != null) {
                replaceInParent(node, right);
                node.right = null;
            } else {
                replaceInParent(node, null);
            }
            rebalance(originalParent, false);
            this.size--;
            this.modCount++;
            return;
        }
        Node<K, V> adjacent = left.height > right.height ? left.last() : right.first();
        removeInternal(adjacent, false);
        int leftHeight = 0;
        left = node.left;
        if (left != null) {
            leftHeight = left.height;
            adjacent.left = left;
            left.parent = adjacent;
            node.left = null;
        }
        int rightHeight = 0;
        right = node.right;
        if (right != null) {
            rightHeight = right.height;
            adjacent.right = right;
            right.parent = adjacent;
            node.right = null;
        }
        adjacent.height = Math.max(leftHeight, rightHeight) + 1;
        replaceInParent(node, adjacent);
    }

    Node<K, V> removeInternalByKey(Object key) {
        Node<K, V> node = findByObject(key);
        if (node != null) {
            removeInternal(node, true);
        }
        return node;
    }

    private void replaceInParent(Node<K, V> node, Node<K, V> replacement) {
        Node<K, V> parent = node.parent;
        node.parent = null;
        if (replacement != null) {
            replacement.parent = parent;
        }
        if (parent == null) {
            int index = node.hash;
            Node[] nodeArr = this.table;
            nodeArr[index & (nodeArr.length - 1)] = replacement;
        } else if (parent.left == node) {
            parent.left = replacement;
        } else {
            parent.right = replacement;
        }
    }

    private void rebalance(Node<K, V> unbalanced, boolean insert) {
        for (Node<K, V> node = unbalanced; node != null; node = node.parent) {
            Node<K, V> left = node.left;
            Node<K, V> right = node.right;
            int leftLeftHeight = 0;
            int leftHeight = left != null ? left.height : 0;
            int rightHeight = right != null ? right.height : 0;
            int delta = leftHeight - rightHeight;
            Node<K, V> rightLeft;
            int rightDelta;
            if (delta == -2) {
                rightLeft = right.left;
                Node<K, V> rightRight = right.right;
                int rightRightHeight = rightRight != null ? rightRight.height : 0;
                if (rightLeft != null) {
                    leftLeftHeight = rightLeft.height;
                }
                rightDelta = leftLeftHeight - rightRightHeight;
                if (rightDelta != -1) {
                    if (rightDelta != 0 || insert) {
                        rotateRight(right);
                        rotateLeft(node);
                        if (insert) {
                            return;
                        }
                    }
                }
                rotateLeft(node);
                if (insert) {
                    return;
                }
            } else if (delta == 2) {
                rightLeft = left.left;
                Node<K, V> leftRight = left.right;
                rightDelta = leftRight != null ? leftRight.height : 0;
                if (rightLeft != null) {
                    leftLeftHeight = rightLeft.height;
                }
                int leftDelta = leftLeftHeight - rightDelta;
                if (leftDelta != 1) {
                    if (leftDelta != 0 || insert) {
                        rotateLeft(left);
                        rotateRight(node);
                        if (insert) {
                            return;
                        }
                    }
                }
                rotateRight(node);
                if (insert) {
                    return;
                }
            } else if (delta == 0) {
                node.height = leftHeight + 1;
                if (insert) {
                    return;
                }
            } else {
                node.height = Math.max(leftHeight, rightHeight) + 1;
                if (!insert) {
                    return;
                }
            }
        }
    }

    private void rotateLeft(Node<K, V> root) {
        Node<K, V> left = root.left;
        Node<K, V> pivot = root.right;
        Node<K, V> pivotLeft = pivot.left;
        Node<K, V> pivotRight = pivot.right;
        root.right = pivotLeft;
        if (pivotLeft != null) {
            pivotLeft.parent = root;
        }
        replaceInParent(root, pivot);
        pivot.left = root;
        root.parent = pivot;
        int i = 0;
        root.height = Math.max(left != null ? left.height : 0, pivotLeft != null ? pivotLeft.height : 0) + 1;
        int i2 = root.height;
        if (pivotRight != null) {
            i = pivotRight.height;
        }
        pivot.height = Math.max(i2, i) + 1;
    }

    private void rotateRight(Node<K, V> root) {
        Node<K, V> pivot = root.left;
        Node<K, V> right = root.right;
        Node<K, V> pivotLeft = pivot.left;
        Node<K, V> pivotRight = pivot.right;
        root.left = pivotRight;
        if (pivotRight != null) {
            pivotRight.parent = root;
        }
        replaceInParent(root, pivot);
        pivot.right = root;
        root.parent = pivot;
        int i = 0;
        root.height = Math.max(right != null ? right.height : 0, pivotRight != null ? pivotRight.height : 0) + 1;
        int i2 = root.height;
        if (pivotLeft != null) {
            i = pivotLeft.height;
        }
        pivot.height = Math.max(i2, i) + 1;
    }

    public Set<Entry<K, V>> entrySet() {
        EntrySet result = this.entrySet;
        if (result != null) {
            return result;
        }
        Set entrySet = new EntrySet();
        this.entrySet = entrySet;
        return entrySet;
    }

    public Set<K> keySet() {
        KeySet result = this.keySet;
        if (result != null) {
            return result;
        }
        Set keySet = new KeySet();
        this.keySet = keySet;
        return keySet;
    }

    private void doubleCapacity() {
        this.table = doubleCapacity(this.table);
        Node[] nodeArr = this.table;
        this.threshold = (nodeArr.length / 2) + (nodeArr.length / 4);
    }

    static <K, V> Node<K, V>[] doubleCapacity(Node<K, V>[] oldTable) {
        int oldCapacity = oldTable.length;
        Node<K, V>[] newTable = new Node[(oldCapacity * 2)];
        AvlIterator<K, V> iterator = new AvlIterator();
        AvlBuilder<K, V> leftBuilder = new AvlBuilder();
        AvlBuilder<K, V> rightBuilder = new AvlBuilder();
        for (int i = 0; i < oldCapacity; i++) {
            Node<K, V> root = oldTable[i];
            if (root != null) {
                Node<K, V> next;
                Node<K, V> node;
                iterator.reset(root);
                int leftSize = 0;
                int rightSize = 0;
                while (true) {
                    next = iterator.next();
                    node = next;
                    if (next == null) {
                        break;
                    } else if ((node.hash & oldCapacity) == 0) {
                        leftSize++;
                    } else {
                        rightSize++;
                    }
                }
                leftBuilder.reset(leftSize);
                rightBuilder.reset(rightSize);
                iterator.reset(root);
                while (true) {
                    next = iterator.next();
                    node = next;
                    if (next == null) {
                        break;
                    } else if ((node.hash & oldCapacity) == 0) {
                        leftBuilder.add(node);
                    } else {
                        rightBuilder.add(node);
                    }
                }
                Node node2 = null;
                newTable[i] = leftSize > 0 ? leftBuilder.root() : null;
                int i2 = i + oldCapacity;
                if (rightSize > 0) {
                    node2 = rightBuilder.root();
                }
                newTable[i2] = node2;
            }
        }
        return newTable;
    }

    private Object writeReplace() throws ObjectStreamException {
        return new LinkedHashMap(this);
    }
}
