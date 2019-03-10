package com.squareup.moshi;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;

public abstract class JsonAdapter<T> {

    public interface Factory {
        @CheckReturnValue
        @Nullable
        JsonAdapter<?> create(Type type, Set<? extends Annotation> set, Moshi moshi);
    }

    @CheckReturnValue
    @Nullable
    public abstract T fromJson(JsonReader jsonReader) throws IOException;

    public abstract void toJson(JsonWriter jsonWriter, @Nullable T t) throws IOException;

    @CheckReturnValue
    @Nullable
    public final T fromJson(BufferedSource source) throws IOException {
        return fromJson(JsonReader.of(source));
    }

    @CheckReturnValue
    @Nullable
    public final T fromJson(String string) throws IOException {
        JsonReader reader = JsonReader.of(new Buffer().writeUtf8(string));
        T result = fromJson(reader);
        if (!isLenient()) {
            if (reader.peek() != JsonReader$Token.END_DOCUMENT) {
                throw new JsonDataException("JSON document was not fully consumed.");
            }
        }
        return result;
    }

    public final void toJson(BufferedSink sink, @Nullable T value) throws IOException {
        toJson(JsonWriter.of(sink), (Object) value);
    }

    @CheckReturnValue
    public final String toJson(@Nullable T value) {
        BufferedSink buffer = new Buffer();
        try {
            toJson(buffer, (Object) value);
            return buffer.readUtf8();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    @CheckReturnValue
    @Nullable
    public final Object toJsonValue(@Nullable T value) {
        JsonWriter writer = new JsonValueWriter();
        try {
            toJson(writer, (Object) value);
            return writer.root();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    @CheckReturnValue
    @Nullable
    public final T fromJsonValue(@Nullable Object value) {
        try {
            return fromJson(new JsonValueReader(value));
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    @CheckReturnValue
    public final JsonAdapter<T> serializeNulls() {
        final JsonAdapter<T> delegate = this;
        return new JsonAdapter<T>() {
            @Nullable
            public T fromJson(JsonReader reader) throws IOException {
                return delegate.fromJson(reader);
            }

            public void toJson(JsonWriter writer, @Nullable T value) throws IOException {
                boolean serializeNulls = writer.getSerializeNulls();
                writer.setSerializeNulls(true);
                try {
                    delegate.toJson(writer, (Object) value);
                } finally {
                    writer.setSerializeNulls(serializeNulls);
                }
            }

            boolean isLenient() {
                return delegate.isLenient();
            }

            public String toString() {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(delegate);
                stringBuilder.append(".serializeNulls()");
                return stringBuilder.toString();
            }
        };
    }

    @CheckReturnValue
    public final JsonAdapter<T> nullSafe() {
        final JsonAdapter<T> delegate = this;
        return new JsonAdapter<T>() {
            @Nullable
            public T fromJson(JsonReader reader) throws IOException {
                if (reader.peek() == JsonReader$Token.NULL) {
                    return reader.nextNull();
                }
                return delegate.fromJson(reader);
            }

            public void toJson(JsonWriter writer, @Nullable T value) throws IOException {
                if (value == null) {
                    writer.nullValue();
                } else {
                    delegate.toJson(writer, (Object) value);
                }
            }

            boolean isLenient() {
                return delegate.isLenient();
            }

            public String toString() {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(delegate);
                stringBuilder.append(".nullSafe()");
                return stringBuilder.toString();
            }
        };
    }

    @CheckReturnValue
    public final JsonAdapter<T> nonNull() {
        final JsonAdapter<T> delegate = this;
        return new JsonAdapter<T>() {
            @Nullable
            public T fromJson(JsonReader reader) throws IOException {
                if (reader.peek() != JsonReader$Token.NULL) {
                    return delegate.fromJson(reader);
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unexpected null at ");
                stringBuilder.append(reader.getPath());
                throw new JsonDataException(stringBuilder.toString());
            }

            public void toJson(JsonWriter writer, @Nullable T value) throws IOException {
                if (value != null) {
                    delegate.toJson(writer, (Object) value);
                    return;
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unexpected null at ");
                stringBuilder.append(writer.getPath());
                throw new JsonDataException(stringBuilder.toString());
            }

            boolean isLenient() {
                return delegate.isLenient();
            }

            public String toString() {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(delegate);
                stringBuilder.append(".nonNull()");
                return stringBuilder.toString();
            }
        };
    }

    @CheckReturnValue
    public final JsonAdapter<T> lenient() {
        final JsonAdapter<T> delegate = this;
        return new JsonAdapter<T>() {
            @Nullable
            public T fromJson(JsonReader reader) throws IOException {
                boolean lenient = reader.isLenient();
                reader.setLenient(true);
                try {
                    T fromJson = delegate.fromJson(reader);
                    return fromJson;
                } finally {
                    reader.setLenient(lenient);
                }
            }

            public void toJson(JsonWriter writer, @Nullable T value) throws IOException {
                boolean lenient = writer.isLenient();
                writer.setLenient(true);
                try {
                    delegate.toJson(writer, (Object) value);
                } finally {
                    writer.setLenient(lenient);
                }
            }

            boolean isLenient() {
                return true;
            }

            public String toString() {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(delegate);
                stringBuilder.append(".lenient()");
                return stringBuilder.toString();
            }
        };
    }

    @CheckReturnValue
    public final JsonAdapter<T> failOnUnknown() {
        final JsonAdapter<T> delegate = this;
        return new JsonAdapter<T>() {
            @Nullable
            public T fromJson(JsonReader reader) throws IOException {
                boolean skipForbidden = reader.failOnUnknown();
                reader.setFailOnUnknown(true);
                try {
                    T fromJson = delegate.fromJson(reader);
                    return fromJson;
                } finally {
                    reader.setFailOnUnknown(skipForbidden);
                }
            }

            public void toJson(JsonWriter writer, @Nullable T value) throws IOException {
                delegate.toJson(writer, (Object) value);
            }

            boolean isLenient() {
                return delegate.isLenient();
            }

            public String toString() {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(delegate);
                stringBuilder.append(".failOnUnknown()");
                return stringBuilder.toString();
            }
        };
    }

    @CheckReturnValue
    public JsonAdapter<T> indent(final String indent) {
        if (indent != null) {
            final JsonAdapter<T> delegate = this;
            return new JsonAdapter<T>() {
                @Nullable
                public T fromJson(JsonReader reader) throws IOException {
                    return delegate.fromJson(reader);
                }

                public void toJson(JsonWriter writer, @Nullable T value) throws IOException {
                    String originalIndent = writer.getIndent();
                    writer.setIndent(indent);
                    try {
                        delegate.toJson(writer, (Object) value);
                    } finally {
                        writer.setIndent(originalIndent);
                    }
                }

                boolean isLenient() {
                    return delegate.isLenient();
                }

                public String toString() {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(delegate);
                    stringBuilder.append(".indent(\"");
                    stringBuilder.append(indent);
                    stringBuilder.append("\")");
                    return stringBuilder.toString();
                }
            };
        }
        throw new NullPointerException("indent == null");
    }

    boolean isLenient() {
        return false;
    }
}
