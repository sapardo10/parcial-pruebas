package com.squareup.moshi;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.util.Arrays;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import okio.BufferedSink;
import okio.BufferedSource;

public abstract class JsonWriter implements Closeable, Flushable {
    String indent;
    boolean lenient;
    int[] pathIndices = new int[32];
    String[] pathNames = new String[32];
    boolean promoteValueToName;
    int[] scopes = new int[32];
    boolean serializeNulls;
    int stackSize = 0;

    public abstract JsonWriter beginArray() throws IOException;

    public abstract JsonWriter beginObject() throws IOException;

    public abstract JsonWriter endArray() throws IOException;

    public abstract JsonWriter endObject() throws IOException;

    public abstract JsonWriter name(String str) throws IOException;

    public abstract JsonWriter nullValue() throws IOException;

    public abstract JsonWriter value(double d) throws IOException;

    public abstract JsonWriter value(long j) throws IOException;

    public abstract JsonWriter value(@Nullable Boolean bool) throws IOException;

    public abstract JsonWriter value(@Nullable Number number) throws IOException;

    public abstract JsonWriter value(@Nullable String str) throws IOException;

    public abstract JsonWriter value(BufferedSource bufferedSource) throws IOException;

    public abstract JsonWriter value(boolean z) throws IOException;

    @CheckReturnValue
    public static JsonWriter of(BufferedSink sink) {
        return new JsonUtf8Writer(sink);
    }

    JsonWriter() {
    }

    final int peekScope() {
        int i = this.stackSize;
        if (i != 0) {
            return this.scopes[i - 1];
        }
        throw new IllegalStateException("JsonWriter is closed.");
    }

    final boolean checkStack() {
        int i = this.stackSize;
        int[] iArr = this.scopes;
        if (i != iArr.length) {
            return false;
        }
        if (i != 256) {
            this.scopes = Arrays.copyOf(iArr, iArr.length * 2);
            String[] strArr = this.pathNames;
            this.pathNames = (String[]) Arrays.copyOf(strArr, strArr.length * 2);
            int[] iArr2 = this.pathIndices;
            this.pathIndices = Arrays.copyOf(iArr2, iArr2.length * 2);
            if (this instanceof JsonValueWriter) {
                ((JsonValueWriter) this).stack = Arrays.copyOf(((JsonValueWriter) this).stack, ((JsonValueWriter) this).stack.length * 2);
            }
            return true;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Nesting too deep at ");
        stringBuilder.append(getPath());
        stringBuilder.append(": circular reference?");
        throw new JsonDataException(stringBuilder.toString());
    }

    final void pushScope(int newTop) {
        int[] iArr = this.scopes;
        int i = this.stackSize;
        this.stackSize = i + 1;
        iArr[i] = newTop;
    }

    final void replaceTop(int topOfStack) {
        this.scopes[this.stackSize - 1] = topOfStack;
    }

    public void setIndent(String indent) {
        this.indent = !indent.isEmpty() ? indent : null;
    }

    @CheckReturnValue
    public final String getIndent() {
        String str = this.indent;
        return str != null ? str : "";
    }

    public final void setLenient(boolean lenient) {
        this.lenient = lenient;
    }

    @CheckReturnValue
    public final boolean isLenient() {
        return this.lenient;
    }

    public final void setSerializeNulls(boolean serializeNulls) {
        this.serializeNulls = serializeNulls;
    }

    @CheckReturnValue
    public final boolean getSerializeNulls() {
        return this.serializeNulls;
    }

    final void promoteValueToName() throws IOException {
        int context = peekScope();
        if (context != 5) {
            if (context != 3) {
                throw new IllegalStateException("Nesting problem.");
            }
        }
        this.promoteValueToName = true;
    }

    @CheckReturnValue
    public final String getPath() {
        return JsonScope.getPath(this.stackSize, this.scopes, this.pathNames, this.pathIndices);
    }
}
