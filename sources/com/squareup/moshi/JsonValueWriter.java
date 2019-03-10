package com.squareup.moshi;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import okio.BufferedSource;

final class JsonValueWriter extends JsonWriter {
    @Nullable
    private String deferredName;
    Object[] stack = new Object[32];

    JsonValueWriter() {
        pushScope(6);
    }

    public Object root() {
        int size = this.stackSize;
        if (size <= 1 && (size != 1 || this.scopes[size - 1] == 7)) {
            return this.stack[0];
        }
        throw new IllegalStateException("Incomplete document");
    }

    public JsonWriter beginArray() throws IOException {
        if (this.promoteValueToName) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Array cannot be used as a map key in JSON at path ");
            stringBuilder.append(getPath());
            throw new IllegalStateException(stringBuilder.toString());
        }
        checkStack();
        List<Object> list = new ArrayList();
        add(list);
        this.stack[this.stackSize] = list;
        this.pathIndices[this.stackSize] = 0;
        pushScope(1);
        return this;
    }

    public JsonWriter endArray() throws IOException {
        if (peekScope() == 1) {
            this.stackSize--;
            this.stack[this.stackSize] = null;
            int[] iArr = this.pathIndices;
            int i = this.stackSize - 1;
            iArr[i] = iArr[i] + 1;
            return this;
        }
        throw new IllegalStateException("Nesting problem.");
    }

    public JsonWriter beginObject() throws IOException {
        if (this.promoteValueToName) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Object cannot be used as a map key in JSON at path ");
            stringBuilder.append(getPath());
            throw new IllegalStateException(stringBuilder.toString());
        }
        checkStack();
        Map<String, Object> map = new LinkedHashTreeMap();
        add(map);
        this.stack[this.stackSize] = map;
        pushScope(3);
        return this;
    }

    public JsonWriter endObject() throws IOException {
        if (peekScope() != 3) {
            throw new IllegalStateException("Nesting problem.");
        } else if (this.deferredName == null) {
            this.promoteValueToName = false;
            this.stackSize--;
            this.stack[this.stackSize] = null;
            this.pathNames[this.stackSize] = null;
            int[] iArr = this.pathIndices;
            int i = this.stackSize - 1;
            iArr[i] = iArr[i] + 1;
            return this;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Dangling name: ");
            stringBuilder.append(this.deferredName);
            throw new IllegalStateException(stringBuilder.toString());
        }
    }

    public JsonWriter name(String name) throws IOException {
        if (name == null) {
            throw new NullPointerException("name == null");
        } else if (this.stackSize == 0) {
            throw new IllegalStateException("JsonWriter is closed.");
        } else if (peekScope() == 3 && this.deferredName == null) {
            this.deferredName = name;
            this.pathNames[this.stackSize - 1] = name;
            this.promoteValueToName = false;
            return this;
        } else {
            throw new IllegalStateException("Nesting problem.");
        }
    }

    public JsonWriter value(@Nullable String value) throws IOException {
        if (this.promoteValueToName) {
            return name(value);
        }
        add(value);
        int[] iArr = this.pathIndices;
        int i = this.stackSize - 1;
        iArr[i] = iArr[i] + 1;
        return this;
    }

    public JsonWriter nullValue() throws IOException {
        if (this.promoteValueToName) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("null cannot be used as a map key in JSON at path ");
            stringBuilder.append(getPath());
            throw new IllegalStateException(stringBuilder.toString());
        }
        add(null);
        int[] iArr = this.pathIndices;
        int i = this.stackSize - 1;
        iArr[i] = iArr[i] + 1;
        return this;
    }

    public JsonWriter value(boolean value) throws IOException {
        if (this.promoteValueToName) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Boolean cannot be used as a map key in JSON at path ");
            stringBuilder.append(getPath());
            throw new IllegalStateException(stringBuilder.toString());
        }
        add(Boolean.valueOf(value));
        int[] iArr = this.pathIndices;
        int i = this.stackSize - 1;
        iArr[i] = iArr[i] + 1;
        return this;
    }

    public JsonWriter value(@Nullable Boolean value) throws IOException {
        if (this.promoteValueToName) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Boolean cannot be used as a map key in JSON at path ");
            stringBuilder.append(getPath());
            throw new IllegalStateException(stringBuilder.toString());
        }
        add(value);
        int[] iArr = this.pathIndices;
        int i = this.stackSize - 1;
        iArr[i] = iArr[i] + 1;
        return this;
    }

    public JsonWriter value(double value) throws IOException {
        if (!this.lenient) {
            if (Double.isNaN(value) || value == Double.NEGATIVE_INFINITY || value == Double.POSITIVE_INFINITY) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Numeric values must be finite, but was ");
                stringBuilder.append(value);
                throw new IllegalArgumentException(stringBuilder.toString());
            }
        }
        if (this.promoteValueToName) {
            return name(Double.toString(value));
        }
        add(Double.valueOf(value));
        int[] iArr = this.pathIndices;
        int i = this.stackSize - 1;
        iArr[i] = iArr[i] + 1;
        return this;
    }

    public JsonWriter value(long value) throws IOException {
        if (this.promoteValueToName) {
            return name(Long.toString(value));
        }
        add(Long.valueOf(value));
        int[] iArr = this.pathIndices;
        int i = this.stackSize - 1;
        iArr[i] = iArr[i] + 1;
        return this;
    }

    public JsonWriter value(@Nullable Number value) throws IOException {
        if (!((value instanceof Byte) || (value instanceof Short) || (value instanceof Integer))) {
            if (!(value instanceof Long)) {
                if (!(value instanceof Float)) {
                    if (!(value instanceof Double)) {
                        if (value == null) {
                            return nullValue();
                        }
                        BigDecimal bigDecimalValue;
                        if (value instanceof BigDecimal) {
                            bigDecimalValue = (BigDecimal) value;
                        } else {
                            bigDecimalValue = new BigDecimal(value.toString());
                        }
                        if (this.promoteValueToName) {
                            return name(bigDecimalValue.toString());
                        }
                        add(bigDecimalValue);
                        int[] iArr = this.pathIndices;
                        int i = this.stackSize - 1;
                        iArr[i] = iArr[i] + 1;
                        return this;
                    }
                }
                return value(value.doubleValue());
            }
        }
        return value(value.longValue());
    }

    public JsonWriter value(BufferedSource source) throws IOException {
        if (this.promoteValueToName) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("BufferedSource cannot be used as a map key in JSON at path ");
            stringBuilder.append(getPath());
            throw new IllegalStateException(stringBuilder.toString());
        }
        Object value = JsonReader.of(source).readJsonValue();
        boolean serializeNulls = this.serializeNulls;
        this.serializeNulls = true;
        try {
            add(value);
            int[] iArr = this.pathIndices;
            int i = this.stackSize - 1;
            iArr[i] = iArr[i] + 1;
            return this;
        } finally {
            this.serializeNulls = serializeNulls;
        }
    }

    public void close() throws IOException {
        int size = this.stackSize;
        if (size > 1 || (size == 1 && this.scopes[size - 1] != 7)) {
            throw new IOException("Incomplete document");
        }
        this.stackSize = 0;
    }

    public void flush() throws IOException {
        if (this.stackSize == 0) {
            throw new IllegalStateException("JsonWriter is closed.");
        }
    }

    private JsonValueWriter add(@Nullable Object newTop) {
        int scope = peekScope();
        if (this.stackSize == 1) {
            if (scope == 6) {
                this.scopes[this.stackSize - 1] = 7;
                this.stack[this.stackSize - 1] = newTop;
            } else {
                throw new IllegalStateException("JSON must have only one top-level value.");
            }
        } else if (scope == 3 && this.deferredName != null) {
            if (newTop == null) {
                if (!this.serializeNulls) {
                    this.deferredName = null;
                }
            }
            Object replaced = this.stack[this.stackSize - 1].put(this.deferredName, newTop);
            if (replaced != null) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Map key '");
                stringBuilder.append(this.deferredName);
                stringBuilder.append("' has multiple values at path ");
                stringBuilder.append(getPath());
                stringBuilder.append(": ");
                stringBuilder.append(replaced);
                stringBuilder.append(" and ");
                stringBuilder.append(newTop);
                throw new IllegalArgumentException(stringBuilder.toString());
            }
            this.deferredName = null;
        } else if (scope == 1) {
            this.stack[this.stackSize - 1].add(newTop);
        } else {
            throw new IllegalStateException("Nesting problem.");
        }
        return this;
    }
}
