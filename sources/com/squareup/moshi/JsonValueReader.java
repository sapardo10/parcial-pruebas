package com.squareup.moshi;

import com.squareup.moshi.JsonReader.Options;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;

final class JsonValueReader extends JsonReader {
    private static final Object JSON_READER_CLOSED = new Object();
    private Object[] stack = new Object[32];

    JsonValueReader(Object root) {
        this.scopes[this.stackSize] = 7;
        Object[] objArr = this.stack;
        int i = this.stackSize;
        this.stackSize = i + 1;
        objArr[i] = root;
    }

    public void beginArray() throws IOException {
        ListIterator<?> iterator = ((List) require(List.class, JsonReader$Token.BEGIN_ARRAY)).listIterator();
        this.stack[this.stackSize - 1] = iterator;
        this.scopes[this.stackSize - 1] = 1;
        this.pathIndices[this.stackSize - 1] = 0;
        if (iterator.hasNext()) {
            push(iterator.next());
        }
    }

    public void endArray() throws IOException {
        ListIterator<?> peeked = (ListIterator) require(ListIterator.class, JsonReader$Token.END_ARRAY);
        if (peeked.hasNext()) {
            throw typeMismatch(peeked, JsonReader$Token.END_ARRAY);
        }
        remove();
    }

    public void beginObject() throws IOException {
        Iterator<?> iterator = ((Map) require(Map.class, JsonReader$Token.BEGIN_OBJECT)).entrySet().iterator();
        this.stack[this.stackSize - 1] = iterator;
        this.scopes[this.stackSize - 1] = 3;
        if (iterator.hasNext()) {
            push(iterator.next());
        }
    }

    public void endObject() throws IOException {
        Iterator<?> peeked = (Iterator) require(Iterator.class, JsonReader$Token.END_OBJECT);
        if ((peeked instanceof ListIterator) || peeked.hasNext()) {
            throw typeMismatch(peeked, JsonReader$Token.END_OBJECT);
        }
        this.pathNames[this.stackSize - 1] = null;
        remove();
    }

    public boolean hasNext() throws IOException {
        boolean z = false;
        if (this.stackSize == 0) {
            return false;
        }
        Object peeked = this.stack[this.stackSize - 1];
        if (peeked instanceof Iterator) {
            if (!((Iterator) peeked).hasNext()) {
                return z;
            }
        }
        z = true;
        return z;
    }

    public JsonReader$Token peek() throws IOException {
        if (this.stackSize == 0) {
            return JsonReader$Token.END_DOCUMENT;
        }
        Object peeked = this.stack[this.stackSize - 1];
        if (peeked instanceof ListIterator) {
            return JsonReader$Token.END_ARRAY;
        }
        if (peeked instanceof Iterator) {
            return JsonReader$Token.END_OBJECT;
        }
        if (peeked instanceof List) {
            return JsonReader$Token.BEGIN_ARRAY;
        }
        if (peeked instanceof Map) {
            return JsonReader$Token.BEGIN_OBJECT;
        }
        if (peeked instanceof Entry) {
            return JsonReader$Token.NAME;
        }
        if (peeked instanceof String) {
            return JsonReader$Token.STRING;
        }
        if (peeked instanceof Boolean) {
            return JsonReader$Token.BOOLEAN;
        }
        if (peeked instanceof Number) {
            return JsonReader$Token.NUMBER;
        }
        if (peeked == null) {
            return JsonReader$Token.NULL;
        }
        if (peeked == JSON_READER_CLOSED) {
            throw new IllegalStateException("JsonReader is closed");
        }
        throw typeMismatch(peeked, "a JSON value");
    }

    public String nextName() throws IOException {
        Entry<?, ?> peeked = (Entry) require(Entry.class, JsonReader$Token.NAME);
        String result = stringKey(peeked);
        this.stack[this.stackSize - 1] = peeked.getValue();
        this.pathNames[this.stackSize - 2] = result;
        return result;
    }

    public int selectName(Options options) throws IOException {
        Entry<?, ?> peeked = (Entry) require(Entry.class, JsonReader$Token.NAME);
        String name = stringKey(peeked);
        int length = options.strings.length;
        for (int i = 0; i < length; i++) {
            if (options.strings[i].equals(name)) {
                this.stack[this.stackSize - 1] = peeked.getValue();
                this.pathNames[this.stackSize - 2] = name;
                return i;
            }
        }
        return -1;
    }

    public void skipName() throws IOException {
        if (this.failOnUnknown) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Cannot skip unexpected ");
            stringBuilder.append(peek());
            stringBuilder.append(" at ");
            stringBuilder.append(getPath());
            throw new JsonDataException(stringBuilder.toString());
        }
        this.stack[this.stackSize - 1] = ((Entry) require(Entry.class, JsonReader$Token.NAME)).getValue();
        this.pathNames[this.stackSize - 2] = "null";
    }

    public String nextString() throws IOException {
        Object peeked = this.stackSize != 0 ? this.stack[this.stackSize - 1] : null;
        if (peeked instanceof String) {
            remove();
            return (String) peeked;
        } else if (peeked instanceof Number) {
            remove();
            return peeked.toString();
        } else if (peeked == JSON_READER_CLOSED) {
            throw new IllegalStateException("JsonReader is closed");
        } else {
            throw typeMismatch(peeked, JsonReader$Token.STRING);
        }
    }

    public int selectString(Options options) throws IOException {
        String peeked = this.stackSize != 0 ? this.stack[this.stackSize - 1] : null;
        if (peeked instanceof String) {
            String peekedString = peeked;
            int length = options.strings.length;
            for (int i = 0; i < length; i++) {
                if (options.strings[i].equals(peekedString)) {
                    remove();
                    return i;
                }
            }
            return -1;
        } else if (peeked != JSON_READER_CLOSED) {
            return -1;
        } else {
            throw new IllegalStateException("JsonReader is closed");
        }
    }

    public boolean nextBoolean() throws IOException {
        Boolean peeked = (Boolean) require(Boolean.class, JsonReader$Token.BOOLEAN);
        remove();
        return peeked.booleanValue();
    }

    @Nullable
    public <T> T nextNull() throws IOException {
        require(Void.class, JsonReader$Token.NULL);
        remove();
        return null;
    }

    public double nextDouble() throws IOException {
        double result;
        Object peeked = require(Object.class, JsonReader$Token.NUMBER);
        if (peeked instanceof Number) {
            result = ((Number) peeked).doubleValue();
        } else if (peeked instanceof String) {
            try {
                result = Double.parseDouble((String) peeked);
            } catch (NumberFormatException e) {
                throw typeMismatch(peeked, JsonReader$Token.NUMBER);
            }
        } else {
            throw typeMismatch(peeked, JsonReader$Token.NUMBER);
        }
        if (!this.lenient) {
            if (Double.isNaN(result) || Double.isInfinite(result)) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("JSON forbids NaN and infinities: ");
                stringBuilder.append(result);
                stringBuilder.append(" at path ");
                stringBuilder.append(getPath());
                throw new JsonEncodingException(stringBuilder.toString());
            }
        }
        remove();
        return result;
    }

    public long nextLong() throws IOException {
        NumberFormatException e;
        Object peeked = require(Object.class, JsonReader$Token.NUMBER);
        if (peeked instanceof Number) {
            e = ((Number) peeked).longValue();
        } else if (peeked instanceof String) {
            try {
                e = Long.parseLong((String) peeked);
            } catch (NumberFormatException e2) {
                try {
                    e = new BigDecimal((String) peeked).longValueExact();
                } catch (NumberFormatException e3) {
                    throw typeMismatch(peeked, JsonReader$Token.NUMBER);
                }
            }
        } else {
            throw typeMismatch(peeked, JsonReader$Token.NUMBER);
        }
        remove();
        return e;
    }

    public int nextInt() throws IOException {
        NumberFormatException e;
        Object peeked = require(Object.class, JsonReader$Token.NUMBER);
        if (peeked instanceof Number) {
            e = ((Number) peeked).intValue();
        } else if (peeked instanceof String) {
            try {
                e = Integer.parseInt((String) peeked);
            } catch (NumberFormatException e2) {
                try {
                    e = new BigDecimal((String) peeked).intValueExact();
                } catch (NumberFormatException e3) {
                    throw typeMismatch(peeked, JsonReader$Token.NUMBER);
                }
            }
        } else {
            throw typeMismatch(peeked, JsonReader$Token.NUMBER);
        }
        remove();
        return e;
    }

    public void skipValue() throws IOException {
        if (this.failOnUnknown) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Cannot skip unexpected ");
            stringBuilder.append(peek());
            stringBuilder.append(" at ");
            stringBuilder.append(getPath());
            throw new JsonDataException(stringBuilder.toString());
        }
        if (this.stackSize > 1) {
            this.pathNames[this.stackSize - 2] = "null";
        }
        if ((this.stackSize != 0 ? this.stack[this.stackSize - 1] : null) instanceof Entry) {
            this.stack[this.stackSize - 1] = this.stack[this.stackSize - 1].getValue();
        } else if (this.stackSize > 0) {
            remove();
        }
    }

    void promoteNameToValue() throws IOException {
        if (hasNext()) {
            push(nextName());
        }
    }

    public void close() throws IOException {
        Arrays.fill(this.stack, 0, this.stackSize, null);
        this.stack[0] = JSON_READER_CLOSED;
        this.scopes[0] = 8;
        this.stackSize = 1;
    }

    private void push(Object newTop) {
        Object[] objArr;
        if (this.stackSize == this.stack.length) {
            if (this.stackSize != 256) {
                this.scopes = Arrays.copyOf(this.scopes, this.scopes.length * 2);
                this.pathNames = (String[]) Arrays.copyOf(this.pathNames, this.pathNames.length * 2);
                this.pathIndices = Arrays.copyOf(this.pathIndices, this.pathIndices.length * 2);
                objArr = this.stack;
                this.stack = Arrays.copyOf(objArr, objArr.length * 2);
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Nesting too deep at ");
                stringBuilder.append(getPath());
                throw new JsonDataException(stringBuilder.toString());
            }
        }
        objArr = this.stack;
        int i = this.stackSize;
        this.stackSize = i + 1;
        objArr[i] = newTop;
    }

    @Nullable
    private <T> T require(Class<T> type, JsonReader$Token expected) throws IOException {
        Object peeked = this.stackSize != 0 ? this.stack[this.stackSize - 1] : null;
        if (type.isInstance(peeked)) {
            return type.cast(peeked);
        }
        if (peeked == null && expected == JsonReader$Token.NULL) {
            return null;
        }
        if (peeked == JSON_READER_CLOSED) {
            throw new IllegalStateException("JsonReader is closed");
        }
        throw typeMismatch(peeked, expected);
    }

    private String stringKey(Entry<?, ?> entry) {
        Object name = entry.getKey();
        if (name instanceof String) {
            return (String) name;
        }
        throw typeMismatch(name, JsonReader$Token.NAME);
    }

    private void remove() {
        this.stackSize--;
        this.stack[this.stackSize] = null;
        this.scopes[this.stackSize] = 0;
        if (this.stackSize > 0) {
            int[] iArr = this.pathIndices;
            int i = this.stackSize - 1;
            iArr[i] = iArr[i] + 1;
            Object parent = this.stack[this.stackSize - 1];
            if ((parent instanceof Iterator) && ((Iterator) parent).hasNext()) {
                push(((Iterator) parent).next());
            }
        }
    }
}
