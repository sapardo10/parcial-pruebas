package com.squareup.moshi;

import java.io.IOException;
import javax.annotation.Nullable;
import okio.BufferedSink;
import okio.BufferedSource;

final class JsonUtf8Writer extends JsonWriter {
    private static final String[] REPLACEMENT_CHARS = new String[128];
    private String deferredName;
    private String separator = ":";
    private final BufferedSink sink;

    static {
        for (int i = 0; i <= 31; i++) {
            REPLACEMENT_CHARS[i] = String.format("\\u%04x", new Object[]{Integer.valueOf(i)});
        }
        String[] strArr = REPLACEMENT_CHARS;
        strArr[34] = "\\\"";
        strArr[92] = "\\\\";
        strArr[9] = "\\t";
        strArr[8] = "\\b";
        strArr[10] = "\\n";
        strArr[13] = "\\r";
        strArr[12] = "\\f";
    }

    JsonUtf8Writer(BufferedSink sink) {
        if (sink != null) {
            this.sink = sink;
            pushScope(6);
            return;
        }
        throw new NullPointerException("sink == null");
    }

    public void setIndent(String indent) {
        super.setIndent(indent);
        this.separator = !indent.isEmpty() ? ": " : ":";
    }

    public JsonWriter beginArray() throws IOException {
        if (this.promoteValueToName) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Array cannot be used as a map key in JSON at path ");
            stringBuilder.append(getPath());
            throw new IllegalStateException(stringBuilder.toString());
        }
        writeDeferredName();
        return open(1, "[");
    }

    public JsonWriter endArray() throws IOException {
        return close(1, 2, "]");
    }

    public JsonWriter beginObject() throws IOException {
        if (this.promoteValueToName) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Object cannot be used as a map key in JSON at path ");
            stringBuilder.append(getPath());
            throw new IllegalStateException(stringBuilder.toString());
        }
        writeDeferredName();
        return open(3, "{");
    }

    public JsonWriter endObject() throws IOException {
        this.promoteValueToName = false;
        return close(3, 5, "}");
    }

    private JsonWriter open(int empty, String openBracket) throws IOException {
        beforeValue();
        checkStack();
        pushScope(empty);
        this.pathIndices[this.stackSize - 1] = 0;
        this.sink.writeUtf8(openBracket);
        return this;
    }

    private JsonWriter close(int empty, int nonempty, String closeBracket) throws IOException {
        int context = peekScope();
        if (context != nonempty) {
            if (context != empty) {
                throw new IllegalStateException("Nesting problem.");
            }
        }
        if (this.deferredName == null) {
            this.stackSize--;
            this.pathNames[this.stackSize] = null;
            int[] iArr = this.pathIndices;
            int i = this.stackSize - 1;
            iArr[i] = iArr[i] + 1;
            if (context == nonempty) {
                newline();
            }
            this.sink.writeUtf8(closeBracket);
            return this;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Dangling name: ");
        stringBuilder.append(this.deferredName);
        throw new IllegalStateException(stringBuilder.toString());
    }

    public JsonWriter name(String name) throws IOException {
        if (name == null) {
            throw new NullPointerException("name == null");
        } else if (this.stackSize != 0) {
            int context = peekScope();
            if ((context == 3 || context == 5) && this.deferredName == null) {
                this.deferredName = name;
                this.pathNames[this.stackSize - 1] = name;
                this.promoteValueToName = false;
                return this;
            }
            throw new IllegalStateException("Nesting problem.");
        } else {
            throw new IllegalStateException("JsonWriter is closed.");
        }
    }

    private void writeDeferredName() throws IOException {
        if (this.deferredName != null) {
            beforeName();
            string(this.sink, this.deferredName);
            this.deferredName = null;
        }
    }

    public JsonWriter value(String value) throws IOException {
        if (value == null) {
            return nullValue();
        }
        if (this.promoteValueToName) {
            return name(value);
        }
        writeDeferredName();
        beforeValue();
        string(this.sink, value);
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
        if (this.deferredName != null) {
            if (this.serializeNulls) {
                writeDeferredName();
            } else {
                this.deferredName = null;
                return this;
            }
        }
        beforeValue();
        this.sink.writeUtf8("null");
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
        writeDeferredName();
        beforeValue();
        this.sink.writeUtf8(value ? "true" : "false");
        int[] iArr = this.pathIndices;
        int i = this.stackSize - 1;
        iArr[i] = iArr[i] + 1;
        return this;
    }

    public JsonWriter value(Boolean value) throws IOException {
        if (value == null) {
            return nullValue();
        }
        return value(value.booleanValue());
    }

    public JsonWriter value(double value) throws IOException {
        if (!this.lenient) {
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Numeric values must be finite, but was ");
                stringBuilder.append(value);
                throw new IllegalArgumentException(stringBuilder.toString());
            }
        }
        if (this.promoteValueToName) {
            return name(Double.toString(value));
        }
        writeDeferredName();
        beforeValue();
        this.sink.writeUtf8(Double.toString(value));
        int[] iArr = this.pathIndices;
        int i = this.stackSize - 1;
        iArr[i] = iArr[i] + 1;
        return this;
    }

    public JsonWriter value(long value) throws IOException {
        if (this.promoteValueToName) {
            return name(Long.toString(value));
        }
        writeDeferredName();
        beforeValue();
        this.sink.writeUtf8(Long.toString(value));
        int[] iArr = this.pathIndices;
        int i = this.stackSize - 1;
        iArr[i] = iArr[i] + 1;
        return this;
    }

    public JsonWriter value(@Nullable Number value) throws IOException {
        if (value == null) {
            return nullValue();
        }
        String string = value.toString();
        if (!this.lenient) {
            if (string.equals("-Infinity") || string.equals("Infinity") || string.equals("NaN")) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Numeric values must be finite, but was ");
                stringBuilder.append(value);
                throw new IllegalArgumentException(stringBuilder.toString());
            }
        }
        if (this.promoteValueToName) {
            return name(string);
        }
        writeDeferredName();
        beforeValue();
        this.sink.writeUtf8(string);
        int[] iArr = this.pathIndices;
        int i = this.stackSize - 1;
        iArr[i] = iArr[i] + 1;
        return this;
    }

    public JsonWriter value(BufferedSource source) throws IOException {
        if (this.promoteValueToName) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("BufferedSource cannot be used as a map key in JSON at path ");
            stringBuilder.append(getPath());
            throw new IllegalStateException(stringBuilder.toString());
        }
        writeDeferredName();
        beforeValue();
        this.sink.writeAll(source);
        int[] iArr = this.pathIndices;
        int i = this.stackSize - 1;
        iArr[i] = iArr[i] + 1;
        return this;
    }

    public void flush() throws IOException {
        if (this.stackSize != 0) {
            this.sink.flush();
            return;
        }
        throw new IllegalStateException("JsonWriter is closed.");
    }

    public void close() throws IOException {
        this.sink.close();
        int size = this.stackSize;
        if (size > 1 || (size == 1 && this.scopes[size - 1] != 7)) {
            throw new IOException("Incomplete document");
        }
        this.stackSize = 0;
    }

    static void string(BufferedSink sink, String value) throws IOException {
        String[] replacements = REPLACEMENT_CHARS;
        sink.writeByte(34);
        int last = 0;
        int length = value.length();
        for (int i = 0; i < length; i++) {
            String replacement;
            char c = value.charAt(i);
            if (c < '') {
                replacement = replacements[c];
                if (replacement == null) {
                }
            } else if (c == ' ') {
                replacement = "\\u2028";
            } else if (c == ' ') {
                replacement = "\\u2029";
            } else {
            }
            if (last < i) {
                sink.writeUtf8(value, last, i);
            }
            sink.writeUtf8(replacement);
            last = i + 1;
        }
        if (last < length) {
            sink.writeUtf8(value, last, length);
        }
        sink.writeByte(34);
    }

    private void newline() throws IOException {
        if (this.indent != null) {
            this.sink.writeByte(10);
            int size = this.stackSize;
            for (int i = 1; i < size; i++) {
                this.sink.writeUtf8(this.indent);
            }
        }
    }

    private void beforeName() throws IOException {
        int context = peekScope();
        if (context == 5) {
            this.sink.writeByte(44);
        } else if (context != 3) {
            throw new IllegalStateException("Nesting problem.");
        }
        newline();
        replaceTop(4);
    }

    private void beforeValue() throws IOException {
        switch (peekScope()) {
            case 1:
                replaceTop(2);
                newline();
                return;
            case 2:
                this.sink.writeByte(44);
                newline();
                return;
            case 4:
                this.sink.writeUtf8(this.separator);
                replaceTop(5);
                return;
            case 6:
                break;
            case 7:
                if (this.lenient) {
                    break;
                }
                throw new IllegalStateException("JSON must have only one top-level value.");
            default:
                throw new IllegalStateException("Nesting problem.");
        }
        replaceTop(7);
    }
}
