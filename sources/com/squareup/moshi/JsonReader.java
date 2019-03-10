package com.squareup.moshi;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import okio.BufferedSource;

public abstract class JsonReader implements Closeable {
    boolean failOnUnknown;
    boolean lenient;
    int[] pathIndices = new int[32];
    String[] pathNames = new String[32];
    int[] scopes = new int[32];
    int stackSize = 0;

    public static final class Options {
        final okio.Options doubleQuoteSuffix;
        final String[] strings;

        @javax.annotation.CheckReturnValue
        public static com.squareup.moshi.JsonReader.Options of(java.lang.String... r5) {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:10:0x0034 in {4, 6, 9} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r0 = r5.length;	 Catch:{ IOException -> 0x002d }
            r0 = new okio.ByteString[r0];	 Catch:{ IOException -> 0x002d }
            r1 = new okio.Buffer;	 Catch:{ IOException -> 0x002d }
            r1.<init>();	 Catch:{ IOException -> 0x002d }
            r2 = 0;	 Catch:{ IOException -> 0x002d }
        L_0x0009:
            r3 = r5.length;	 Catch:{ IOException -> 0x002d }
            if (r2 >= r3) goto L_0x001d;	 Catch:{ IOException -> 0x002d }
        L_0x000c:
            r3 = r5[r2];	 Catch:{ IOException -> 0x002d }
            com.squareup.moshi.JsonUtf8Writer.string(r1, r3);	 Catch:{ IOException -> 0x002d }
            r1.readByte();	 Catch:{ IOException -> 0x002d }
            r3 = r1.readByteString();	 Catch:{ IOException -> 0x002d }
            r0[r2] = r3;	 Catch:{ IOException -> 0x002d }
            r2 = r2 + 1;	 Catch:{ IOException -> 0x002d }
            goto L_0x0009;	 Catch:{ IOException -> 0x002d }
        L_0x001d:
            r2 = new com.squareup.moshi.JsonReader$Options;	 Catch:{ IOException -> 0x002d }
            r3 = r5.clone();	 Catch:{ IOException -> 0x002d }
            r3 = (java.lang.String[]) r3;	 Catch:{ IOException -> 0x002d }
            r4 = okio.Options.of(r0);	 Catch:{ IOException -> 0x002d }
            r2.<init>(r3, r4);	 Catch:{ IOException -> 0x002d }
            return r2;
        L_0x002d:
            r0 = move-exception;
            r1 = new java.lang.AssertionError;
            r1.<init>(r0);
            throw r1;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.squareup.moshi.JsonReader.Options.of(java.lang.String[]):com.squareup.moshi.JsonReader$Options");
        }

        private Options(String[] strings, okio.Options doubleQuoteSuffix) {
            this.strings = strings;
            this.doubleQuoteSuffix = doubleQuoteSuffix;
        }
    }

    public abstract void beginArray() throws IOException;

    public abstract void beginObject() throws IOException;

    public abstract void endArray() throws IOException;

    public abstract void endObject() throws IOException;

    @CheckReturnValue
    public abstract boolean hasNext() throws IOException;

    public abstract boolean nextBoolean() throws IOException;

    public abstract double nextDouble() throws IOException;

    public abstract int nextInt() throws IOException;

    public abstract long nextLong() throws IOException;

    @CheckReturnValue
    public abstract String nextName() throws IOException;

    @Nullable
    public abstract <T> T nextNull() throws IOException;

    public abstract String nextString() throws IOException;

    @CheckReturnValue
    public abstract JsonReader$Token peek() throws IOException;

    abstract void promoteNameToValue() throws IOException;

    @CheckReturnValue
    public abstract int selectName(Options options) throws IOException;

    @CheckReturnValue
    public abstract int selectString(Options options) throws IOException;

    public abstract void skipName() throws IOException;

    public abstract void skipValue() throws IOException;

    @CheckReturnValue
    public static JsonReader of(BufferedSource source) {
        return new JsonUtf8Reader(source);
    }

    JsonReader() {
    }

    final void pushScope(int newTop) {
        int[] iArr;
        int i = this.stackSize;
        int[] iArr2 = this.scopes;
        if (i == iArr2.length) {
            if (i != 256) {
                this.scopes = Arrays.copyOf(iArr2, iArr2.length * 2);
                String[] strArr = this.pathNames;
                this.pathNames = (String[]) Arrays.copyOf(strArr, strArr.length * 2);
                iArr = this.pathIndices;
                this.pathIndices = Arrays.copyOf(iArr, iArr.length * 2);
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Nesting too deep at ");
                stringBuilder.append(getPath());
                throw new JsonDataException(stringBuilder.toString());
            }
        }
        iArr = this.scopes;
        int i2 = this.stackSize;
        this.stackSize = i2 + 1;
        iArr[i2] = newTop;
    }

    final JsonEncodingException syntaxError(String message) throws JsonEncodingException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(message);
        stringBuilder.append(" at path ");
        stringBuilder.append(getPath());
        throw new JsonEncodingException(stringBuilder.toString());
    }

    final JsonDataException typeMismatch(@Nullable Object value, Object expected) {
        if (value == null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Expected ");
            stringBuilder.append(expected);
            stringBuilder.append(" but was null at path ");
            stringBuilder.append(getPath());
            return new JsonDataException(stringBuilder.toString());
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append("Expected ");
        stringBuilder.append(expected);
        stringBuilder.append(" but was ");
        stringBuilder.append(value);
        stringBuilder.append(", a ");
        stringBuilder.append(value.getClass().getName());
        stringBuilder.append(", at path ");
        stringBuilder.append(getPath());
        return new JsonDataException(stringBuilder.toString());
    }

    public final void setLenient(boolean lenient) {
        this.lenient = lenient;
    }

    @CheckReturnValue
    public final boolean isLenient() {
        return this.lenient;
    }

    public final void setFailOnUnknown(boolean failOnUnknown) {
        this.failOnUnknown = failOnUnknown;
    }

    @CheckReturnValue
    public final boolean failOnUnknown() {
        return this.failOnUnknown;
    }

    @Nullable
    public final Object readJsonValue() throws IOException {
        switch (JsonReader$1.$SwitchMap$com$squareup$moshi$JsonReader$Token[peek().ordinal()]) {
            case 1:
                List<Object> list = new ArrayList();
                beginArray();
                while (hasNext()) {
                    list.add(readJsonValue());
                }
                endArray();
                return list;
            case 2:
                Map<String, Object> map = new LinkedHashTreeMap();
                beginObject();
                while (hasNext()) {
                    String name = nextName();
                    Object value = readJsonValue();
                    Object replaced = map.put(name, value);
                    if (replaced != null) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Map key '");
                        stringBuilder.append(name);
                        stringBuilder.append("' has multiple values at path ");
                        stringBuilder.append(getPath());
                        stringBuilder.append(": ");
                        stringBuilder.append(replaced);
                        stringBuilder.append(" and ");
                        stringBuilder.append(value);
                        throw new JsonDataException(stringBuilder.toString());
                    }
                }
                endObject();
                return map;
            case 3:
                return nextString();
            case 4:
                return Double.valueOf(nextDouble());
            case 5:
                return Boolean.valueOf(nextBoolean());
            case 6:
                return nextNull();
            default:
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Expected a value but was ");
                stringBuilder2.append(peek());
                stringBuilder2.append(" at path ");
                stringBuilder2.append(getPath());
                throw new IllegalStateException(stringBuilder2.toString());
        }
    }

    @CheckReturnValue
    public final String getPath() {
        return JsonScope.getPath(this.stackSize, this.scopes, this.pathNames, this.pathIndices);
    }
}
