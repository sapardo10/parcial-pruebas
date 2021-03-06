package org.jsoup.helper;

import java.util.ArrayList;
import java.util.Collection;

public abstract class ChangeNotifyingArrayList<E> extends ArrayList<E> {
    public abstract void onContentsChanged();

    public ChangeNotifyingArrayList(int initialCapacity) {
        super(initialCapacity);
    }

    public E set(int index, E element) {
        onContentsChanged();
        return super.set(index, element);
    }

    public boolean add(E e) {
        onContentsChanged();
        return super.add(e);
    }

    public void add(int index, E element) {
        onContentsChanged();
        super.add(index, element);
    }

    public E remove(int index) {
        onContentsChanged();
        return super.remove(index);
    }

    public boolean remove(Object o) {
        onContentsChanged();
        return super.remove(o);
    }

    public void clear() {
        onContentsChanged();
        super.clear();
    }

    public boolean addAll(Collection<? extends E> c) {
        onContentsChanged();
        return super.addAll(c);
    }

    public boolean addAll(int index, Collection<? extends E> c) {
        onContentsChanged();
        return super.addAll(index, c);
    }

    protected void removeRange(int fromIndex, int toIndex) {
        onContentsChanged();
        super.removeRange(fromIndex, toIndex);
    }

    public boolean removeAll(Collection<?> c) {
        onContentsChanged();
        return super.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        onContentsChanged();
        return super.retainAll(c);
    }
}
