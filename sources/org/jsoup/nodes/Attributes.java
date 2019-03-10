package org.jsoup.nodes;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import kotlin.text.Typography;
import org.jsoup.SerializationException;
import org.jsoup.helper.Validate;
import org.jsoup.internal.Normalizer;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.nodes.Document.OutputSettings.Syntax;

public class Attributes implements Iterable<Attribute>, Cloneable {
    private static final String[] Empty = new String[0];
    private static final String EmptyString = "";
    private static final int GrowthFactor = 2;
    private static final int InitialCapacity = 4;
    static final int NotFound = -1;
    protected static final String dataPrefix = "data-";
    String[] keys;
    private int size = 0;
    String[] vals;

    /* renamed from: org.jsoup.nodes.Attributes$1 */
    class C11821 implements Iterator<Attribute> {
        /* renamed from: i */
        int f79i = 0;

        C11821() {
        }

        public boolean hasNext() {
            return this.f79i < Attributes.this.size;
        }

        public Attribute next() {
            Attribute attr = new Attribute(Attributes.this.keys[this.f79i], Attributes.this.vals[this.f79i], Attributes.this);
            this.f79i++;
            return attr;
        }

        public void remove() {
            Attributes attributes = Attributes.this;
            int i = this.f79i - 1;
            this.f79i = i;
            attributes.remove(i);
        }
    }

    private static class Dataset extends AbstractMap<String, String> {
        private final Attributes attributes;

        private class DatasetIterator implements Iterator<Entry<String, String>> {
            private Attribute attr;
            private Iterator<Attribute> attrIter;

            private DatasetIterator() {
                this.attrIter = Dataset.this.attributes.iterator();
            }

            public boolean hasNext() {
                while (this.attrIter.hasNext()) {
                    this.attr = (Attribute) this.attrIter.next();
                    if (this.attr.isDataAttribute()) {
                        return true;
                    }
                }
                return false;
            }

            public Entry<String, String> next() {
                return new Attribute(this.attr.getKey().substring(Attributes.dataPrefix.length()), this.attr.getValue());
            }

            public void remove() {
                Dataset.this.attributes.remove(this.attr.getKey());
            }
        }

        private class EntrySet extends AbstractSet<Entry<String, String>> {
            private EntrySet() {
            }

            public Iterator<Entry<String, String>> iterator() {
                return new DatasetIterator();
            }

            public int size() {
                int count = 0;
                while (new DatasetIterator().hasNext()) {
                    count++;
                }
                return count;
            }
        }

        private Dataset(Attributes attributes) {
            this.attributes = attributes;
        }

        public Set<Entry<String, String>> entrySet() {
            return new EntrySet();
        }

        public String put(String key, String value) {
            String dataKey = Attributes.dataKey(key);
            String oldValue = this.attributes.hasKey(dataKey) ? this.attributes.get(dataKey) : null;
            this.attributes.put(dataKey, value);
            return oldValue;
        }
    }

    public Attributes() {
        String[] strArr = Empty;
        this.keys = strArr;
        this.vals = strArr;
    }

    private void checkCapacity(int minNewSize) {
        Validate.isTrue(minNewSize >= this.size);
        int curSize = this.keys.length;
        if (curSize < minNewSize) {
            int newSize = 4;
            if (curSize >= 4) {
                newSize = this.size * 2;
            }
            if (minNewSize > newSize) {
                newSize = minNewSize;
            }
            this.keys = copyOf(this.keys, newSize);
            this.vals = copyOf(this.vals, newSize);
        }
    }

    private static String[] copyOf(String[] orig, int size) {
        String[] copy = new String[size];
        System.arraycopy(orig, 0, copy, 0, Math.min(orig.length, size));
        return copy;
    }

    int indexOfKey(String key) {
        Validate.notNull(key);
        for (int i = 0; i < this.size; i++) {
            if (key.equals(this.keys[i])) {
                return i;
            }
        }
        return -1;
    }

    private int indexOfKeyIgnoreCase(String key) {
        Validate.notNull(key);
        for (int i = 0; i < this.size; i++) {
            if (key.equalsIgnoreCase(this.keys[i])) {
                return i;
            }
        }
        return -1;
    }

    static String checkNotNull(String val) {
        return val == null ? "" : val;
    }

    public String get(String key) {
        int i = indexOfKey(key);
        return i == -1 ? "" : checkNotNull(this.vals[i]);
    }

    public String getIgnoreCase(String key) {
        int i = indexOfKeyIgnoreCase(key);
        return i == -1 ? "" : checkNotNull(this.vals[i]);
    }

    private void add(String key, String value) {
        checkCapacity(this.size + 1);
        String[] strArr = this.keys;
        int i = this.size;
        strArr[i] = key;
        this.vals[i] = value;
        this.size = i + 1;
    }

    public Attributes put(String key, String value) {
        int i = indexOfKey(key);
        if (i != -1) {
            this.vals[i] = value;
        } else {
            add(key, value);
        }
        return this;
    }

    void putIgnoreCase(String key, String value) {
        int i = indexOfKeyIgnoreCase(key);
        if (i != -1) {
            this.vals[i] = value;
            if (!this.keys[i].equals(key)) {
                this.keys[i] = key;
                return;
            }
            return;
        }
        add(key, value);
    }

    public Attributes put(String key, boolean value) {
        if (value) {
            putIgnoreCase(key, null);
        } else {
            remove(key);
        }
        return this;
    }

    public Attributes put(Attribute attribute) {
        Validate.notNull(attribute);
        put(attribute.getKey(), attribute.getValue());
        attribute.parent = this;
        return this;
    }

    private void remove(int index) {
        Validate.isFalse(index >= this.size);
        int shifted = (this.size - index) - 1;
        if (shifted > 0) {
            Object obj = this.keys;
            System.arraycopy(obj, index + 1, obj, index, shifted);
            obj = this.vals;
            System.arraycopy(obj, index + 1, obj, index, shifted);
        }
        this.size--;
        String[] strArr = this.keys;
        int i = this.size;
        strArr[i] = null;
        this.vals[i] = null;
    }

    public void remove(String key) {
        int i = indexOfKey(key);
        if (i != -1) {
            remove(i);
        }
    }

    public void removeIgnoreCase(String key) {
        int i = indexOfKeyIgnoreCase(key);
        if (i != -1) {
            remove(i);
        }
    }

    public boolean hasKey(String key) {
        return indexOfKey(key) != -1;
    }

    public boolean hasKeyIgnoreCase(String key) {
        return indexOfKeyIgnoreCase(key) != -1;
    }

    public int size() {
        return this.size;
    }

    public void addAll(Attributes incoming) {
        if (incoming.size() != 0) {
            checkCapacity(this.size + incoming.size);
            Iterator it = incoming.iterator();
            while (it.hasNext()) {
                put((Attribute) it.next());
            }
        }
    }

    public Iterator<Attribute> iterator() {
        return new C11821();
    }

    public List<Attribute> asList() {
        ArrayList<Attribute> list = new ArrayList(this.size);
        for (int i = 0; i < this.size; i++) {
            Attribute attr;
            String[] strArr = this.vals;
            if (strArr[i] == null) {
                attr = new BooleanAttribute(this.keys[i]);
            } else {
                attr = new Attribute(this.keys[i], strArr[i], this);
            }
            list.add(attr);
        }
        return Collections.unmodifiableList(list);
    }

    public Map<String, String> dataset() {
        return new Dataset();
    }

    public String html() {
        StringBuilder accum = new StringBuilder();
        try {
            html(accum, new Document("").outputSettings());
            return accum.toString();
        } catch (Throwable e) {
            throw new SerializationException(e);
        }
    }

    final void html(Appendable accum, OutputSettings out) throws IOException {
        int sz = this.size;
        for (int i = 0; i < sz; i++) {
            String key = this.keys[i];
            String val = this.vals[i];
            accum.append(' ').append(key);
            if (out.syntax() == Syntax.html) {
                if (val != null) {
                    if (val.equals(key)) {
                        if (Attribute.isBooleanAttribute(key)) {
                        }
                    }
                }
            }
            accum.append("=\"");
            Entities.escape(accum, val == null ? "" : val, out, true, false, false);
            accum.append(Typography.quote);
        }
    }

    public String toString() {
        return html();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o != null) {
            if (getClass() == o.getClass()) {
                Attributes that = (Attributes) o;
                return (this.size == that.size && Arrays.equals(this.keys, that.keys)) ? Arrays.equals(this.vals, that.vals) : false;
            }
        }
        return false;
    }

    public int hashCode() {
        return (((this.size * 31) + Arrays.hashCode(this.keys)) * 31) + Arrays.hashCode(this.vals);
    }

    public Attributes clone() {
        try {
            Attributes clone = (Attributes) super.clone();
            clone.size = this.size;
            this.keys = copyOf(this.keys, this.size);
            this.vals = copyOf(this.vals, this.size);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public void normalize() {
        for (int i = 0; i < this.size; i++) {
            String[] strArr = this.keys;
            strArr[i] = Normalizer.lowerCase(strArr[i]);
        }
    }

    private static String dataKey(String key) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(dataPrefix);
        stringBuilder.append(key);
        return stringBuilder.toString();
    }
}
