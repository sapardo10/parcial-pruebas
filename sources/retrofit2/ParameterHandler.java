package retrofit2;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Map;
import javax.annotation.Nullable;
import okhttp3.Headers;
import okhttp3.RequestBody;

abstract class ParameterHandler<T> {

    /* renamed from: retrofit2.ParameterHandler$1 */
    class C00201 extends ParameterHandler<Iterable<T>> {
        C00201() {
        }

        void apply(RequestBuilder builder, @Nullable Iterable<T> values) throws IOException {
            if (values != null) {
                for (T value : values) {
                    ParameterHandler.this.apply(builder, value);
                }
            }
        }
    }

    /* renamed from: retrofit2.ParameterHandler$2 */
    class C00212 extends ParameterHandler<Object> {
        C00212() {
        }

        void apply(RequestBuilder builder, @Nullable Object values) throws IOException {
            if (values != null) {
                int size = Array.getLength(values);
                for (int i = 0; i < size; i++) {
                    ParameterHandler.this.apply(builder, Array.get(values, i));
                }
            }
        }
    }

    static final class Body<T> extends ParameterHandler<T> {
        private final Converter<T, RequestBody> converter;

        Body(Converter<T, RequestBody> converter) {
            this.converter = converter;
        }

        void apply(RequestBuilder builder, @Nullable T value) {
            if (value != null) {
                try {
                    builder.setBody((RequestBody) this.converter.convert(value));
                    return;
                } catch (IOException e) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Unable to convert ");
                    stringBuilder.append(value);
                    stringBuilder.append(" to RequestBody");
                    throw new RuntimeException(stringBuilder.toString(), e);
                }
            }
            throw new IllegalArgumentException("Body parameter value must not be null.");
        }
    }

    static final class Field<T> extends ParameterHandler<T> {
        private final boolean encoded;
        private final String name;
        private final Converter<T, String> valueConverter;

        Field(String name, Converter<T, String> valueConverter, boolean encoded) {
            this.name = (String) Utils.checkNotNull(name, "name == null");
            this.valueConverter = valueConverter;
            this.encoded = encoded;
        }

        void apply(RequestBuilder builder, @Nullable T value) throws IOException {
            if (value != null) {
                String fieldValue = (String) this.valueConverter.convert(value);
                if (fieldValue != null) {
                    builder.addFormField(this.name, fieldValue, this.encoded);
                }
            }
        }
    }

    static final class FieldMap<T> extends ParameterHandler<Map<String, T>> {
        private final boolean encoded;
        private final Converter<T, String> valueConverter;

        void apply(retrofit2.RequestBuilder r8, @javax.annotation.Nullable java.util.Map<java.lang.String, T> r9) throws java.io.IOException {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:20:0x0097 in {10, 12, 14, 16, 17, 19} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r7 = this;
            if (r9 == 0) goto L_0x008f;
        L_0x0002:
            r0 = r9.entrySet();
            r0 = r0.iterator();
        L_0x000a:
            r1 = r0.hasNext();
            if (r1 == 0) goto L_0x008e;
        L_0x0010:
            r1 = r0.next();
            r1 = (java.util.Map.Entry) r1;
            r2 = r1.getKey();
            r2 = (java.lang.String) r2;
            if (r2 == 0) goto L_0x0086;
        L_0x001e:
            r3 = r1.getValue();
            if (r3 == 0) goto L_0x006a;
        L_0x0024:
            r4 = r7.valueConverter;
            r4 = r4.convert(r3);
            r4 = (java.lang.String) r4;
            if (r4 == 0) goto L_0x0034;
        L_0x002e:
            r5 = r7.encoded;
            r8.addFormField(r2, r4, r5);
            goto L_0x000a;
        L_0x0034:
            r0 = new java.lang.IllegalArgumentException;
            r5 = new java.lang.StringBuilder;
            r5.<init>();
            r6 = "Field map value '";
            r5.append(r6);
            r5.append(r3);
            r6 = "' converted to null by ";
            r5.append(r6);
            r6 = r7.valueConverter;
            r6 = r6.getClass();
            r6 = r6.getName();
            r5.append(r6);
            r6 = " for key '";
            r5.append(r6);
            r5.append(r2);
            r6 = "'.";
            r5.append(r6);
            r5 = r5.toString();
            r0.<init>(r5);
            throw r0;
        L_0x006a:
            r0 = new java.lang.IllegalArgumentException;
            r4 = new java.lang.StringBuilder;
            r4.<init>();
            r5 = "Field map contained null value for key '";
            r4.append(r5);
            r4.append(r2);
            r5 = "'.";
            r4.append(r5);
            r4 = r4.toString();
            r0.<init>(r4);
            throw r0;
        L_0x0086:
            r0 = new java.lang.IllegalArgumentException;
            r3 = "Field map contained null key.";
            r0.<init>(r3);
            throw r0;
        L_0x008e:
            return;
        L_0x008f:
            r0 = new java.lang.IllegalArgumentException;
            r1 = "Field map was null.";
            r0.<init>(r1);
            throw r0;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: retrofit2.ParameterHandler.FieldMap.apply(retrofit2.RequestBuilder, java.util.Map):void");
        }

        FieldMap(Converter<T, String> valueConverter, boolean encoded) {
            this.valueConverter = valueConverter;
            this.encoded = encoded;
        }
    }

    static final class Header<T> extends ParameterHandler<T> {
        private final String name;
        private final Converter<T, String> valueConverter;

        Header(String name, Converter<T, String> valueConverter) {
            this.name = (String) Utils.checkNotNull(name, "name == null");
            this.valueConverter = valueConverter;
        }

        void apply(RequestBuilder builder, @Nullable T value) throws IOException {
            if (value != null) {
                String headerValue = (String) this.valueConverter.convert(value);
                if (headerValue != null) {
                    builder.addHeader(this.name, headerValue);
                }
            }
        }
    }

    static final class HeaderMap<T> extends ParameterHandler<Map<String, T>> {
        private final Converter<T, String> valueConverter;

        void apply(retrofit2.RequestBuilder r7, @javax.annotation.Nullable java.util.Map<java.lang.String, T> r8) throws java.io.IOException {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:16:0x005d in {8, 10, 12, 13, 15} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r6 = this;
            if (r8 == 0) goto L_0x0055;
        L_0x0002:
            r0 = r8.entrySet();
            r0 = r0.iterator();
        L_0x000a:
            r1 = r0.hasNext();
            if (r1 == 0) goto L_0x0054;
        L_0x0010:
            r1 = r0.next();
            r1 = (java.util.Map.Entry) r1;
            r2 = r1.getKey();
            r2 = (java.lang.String) r2;
            if (r2 == 0) goto L_0x004c;
        L_0x001e:
            r3 = r1.getValue();
            if (r3 == 0) goto L_0x0030;
        L_0x0024:
            r4 = r6.valueConverter;
            r4 = r4.convert(r3);
            r4 = (java.lang.String) r4;
            r7.addHeader(r2, r4);
            goto L_0x000a;
        L_0x0030:
            r0 = new java.lang.IllegalArgumentException;
            r4 = new java.lang.StringBuilder;
            r4.<init>();
            r5 = "Header map contained null value for key '";
            r4.append(r5);
            r4.append(r2);
            r5 = "'.";
            r4.append(r5);
            r4 = r4.toString();
            r0.<init>(r4);
            throw r0;
        L_0x004c:
            r0 = new java.lang.IllegalArgumentException;
            r3 = "Header map contained null key.";
            r0.<init>(r3);
            throw r0;
        L_0x0054:
            return;
        L_0x0055:
            r0 = new java.lang.IllegalArgumentException;
            r1 = "Header map was null.";
            r0.<init>(r1);
            throw r0;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: retrofit2.ParameterHandler.HeaderMap.apply(retrofit2.RequestBuilder, java.util.Map):void");
        }

        HeaderMap(Converter<T, String> valueConverter) {
            this.valueConverter = valueConverter;
        }
    }

    static final class Part<T> extends ParameterHandler<T> {
        private final Converter<T, RequestBody> converter;
        private final Headers headers;

        Part(Headers headers, Converter<T, RequestBody> converter) {
            this.headers = headers;
            this.converter = converter;
        }

        void apply(RequestBuilder builder, @Nullable T value) {
            if (value != null) {
                try {
                    builder.addPart(this.headers, (RequestBody) this.converter.convert(value));
                } catch (IOException e) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Unable to convert ");
                    stringBuilder.append(value);
                    stringBuilder.append(" to RequestBody");
                    throw new RuntimeException(stringBuilder.toString(), e);
                }
            }
        }
    }

    static final class PartMap<T> extends ParameterHandler<Map<String, T>> {
        private final String transferEncoding;
        private final Converter<T, RequestBody> valueConverter;

        void apply(retrofit2.RequestBuilder r9, @javax.annotation.Nullable java.util.Map<java.lang.String, T> r10) throws java.io.IOException {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:16:0x008c in {8, 10, 12, 13, 15} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r8 = this;
            if (r10 == 0) goto L_0x0084;
        L_0x0002:
            r0 = r10.entrySet();
            r0 = r0.iterator();
        L_0x000a:
            r1 = r0.hasNext();
            if (r1 == 0) goto L_0x0083;
        L_0x0010:
            r1 = r0.next();
            r1 = (java.util.Map.Entry) r1;
            r2 = r1.getKey();
            r2 = (java.lang.String) r2;
            if (r2 == 0) goto L_0x007b;
        L_0x001e:
            r3 = r1.getValue();
            if (r3 == 0) goto L_0x005f;
        L_0x0024:
            r4 = 4;
            r4 = new java.lang.String[r4];
            r5 = 0;
            r6 = "Content-Disposition";
            r4[r5] = r6;
            r5 = 1;
            r6 = new java.lang.StringBuilder;
            r6.<init>();
            r7 = "form-data; name=\"";
            r6.append(r7);
            r6.append(r2);
            r7 = "\"";
            r6.append(r7);
            r6 = r6.toString();
            r4[r5] = r6;
            r5 = 2;
            r6 = "Content-Transfer-Encoding";
            r4[r5] = r6;
            r5 = 3;
            r6 = r8.transferEncoding;
            r4[r5] = r6;
            r4 = okhttp3.Headers.of(r4);
            r5 = r8.valueConverter;
            r5 = r5.convert(r3);
            r5 = (okhttp3.RequestBody) r5;
            r9.addPart(r4, r5);
            goto L_0x000a;
        L_0x005f:
            r0 = new java.lang.IllegalArgumentException;
            r4 = new java.lang.StringBuilder;
            r4.<init>();
            r5 = "Part map contained null value for key '";
            r4.append(r5);
            r4.append(r2);
            r5 = "'.";
            r4.append(r5);
            r4 = r4.toString();
            r0.<init>(r4);
            throw r0;
        L_0x007b:
            r0 = new java.lang.IllegalArgumentException;
            r3 = "Part map contained null key.";
            r0.<init>(r3);
            throw r0;
        L_0x0083:
            return;
        L_0x0084:
            r0 = new java.lang.IllegalArgumentException;
            r1 = "Part map was null.";
            r0.<init>(r1);
            throw r0;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: retrofit2.ParameterHandler.PartMap.apply(retrofit2.RequestBuilder, java.util.Map):void");
        }

        PartMap(Converter<T, RequestBody> valueConverter, String transferEncoding) {
            this.valueConverter = valueConverter;
            this.transferEncoding = transferEncoding;
        }
    }

    static final class Path<T> extends ParameterHandler<T> {
        private final boolean encoded;
        private final String name;
        private final Converter<T, String> valueConverter;

        Path(String name, Converter<T, String> valueConverter, boolean encoded) {
            this.name = (String) Utils.checkNotNull(name, "name == null");
            this.valueConverter = valueConverter;
            this.encoded = encoded;
        }

        void apply(RequestBuilder builder, @Nullable T value) throws IOException {
            if (value != null) {
                builder.addPathParam(this.name, (String) this.valueConverter.convert(value), this.encoded);
                return;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Path parameter \"");
            stringBuilder.append(this.name);
            stringBuilder.append("\" value must not be null.");
            throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    static final class Query<T> extends ParameterHandler<T> {
        private final boolean encoded;
        private final String name;
        private final Converter<T, String> valueConverter;

        Query(String name, Converter<T, String> valueConverter, boolean encoded) {
            this.name = (String) Utils.checkNotNull(name, "name == null");
            this.valueConverter = valueConverter;
            this.encoded = encoded;
        }

        void apply(RequestBuilder builder, @Nullable T value) throws IOException {
            if (value != null) {
                String queryValue = (String) this.valueConverter.convert(value);
                if (queryValue != null) {
                    builder.addQueryParam(this.name, queryValue, this.encoded);
                }
            }
        }
    }

    static final class QueryMap<T> extends ParameterHandler<Map<String, T>> {
        private final boolean encoded;
        private final Converter<T, String> valueConverter;

        void apply(retrofit2.RequestBuilder r8, @javax.annotation.Nullable java.util.Map<java.lang.String, T> r9) throws java.io.IOException {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:20:0x0097 in {10, 12, 14, 16, 17, 19} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r7 = this;
            if (r9 == 0) goto L_0x008f;
        L_0x0002:
            r0 = r9.entrySet();
            r0 = r0.iterator();
        L_0x000a:
            r1 = r0.hasNext();
            if (r1 == 0) goto L_0x008e;
        L_0x0010:
            r1 = r0.next();
            r1 = (java.util.Map.Entry) r1;
            r2 = r1.getKey();
            r2 = (java.lang.String) r2;
            if (r2 == 0) goto L_0x0086;
        L_0x001e:
            r3 = r1.getValue();
            if (r3 == 0) goto L_0x006a;
        L_0x0024:
            r4 = r7.valueConverter;
            r4 = r4.convert(r3);
            r4 = (java.lang.String) r4;
            if (r4 == 0) goto L_0x0034;
        L_0x002e:
            r5 = r7.encoded;
            r8.addQueryParam(r2, r4, r5);
            goto L_0x000a;
        L_0x0034:
            r0 = new java.lang.IllegalArgumentException;
            r5 = new java.lang.StringBuilder;
            r5.<init>();
            r6 = "Query map value '";
            r5.append(r6);
            r5.append(r3);
            r6 = "' converted to null by ";
            r5.append(r6);
            r6 = r7.valueConverter;
            r6 = r6.getClass();
            r6 = r6.getName();
            r5.append(r6);
            r6 = " for key '";
            r5.append(r6);
            r5.append(r2);
            r6 = "'.";
            r5.append(r6);
            r5 = r5.toString();
            r0.<init>(r5);
            throw r0;
        L_0x006a:
            r0 = new java.lang.IllegalArgumentException;
            r4 = new java.lang.StringBuilder;
            r4.<init>();
            r5 = "Query map contained null value for key '";
            r4.append(r5);
            r4.append(r2);
            r5 = "'.";
            r4.append(r5);
            r4 = r4.toString();
            r0.<init>(r4);
            throw r0;
        L_0x0086:
            r0 = new java.lang.IllegalArgumentException;
            r3 = "Query map contained null key.";
            r0.<init>(r3);
            throw r0;
        L_0x008e:
            return;
        L_0x008f:
            r0 = new java.lang.IllegalArgumentException;
            r1 = "Query map was null.";
            r0.<init>(r1);
            throw r0;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: retrofit2.ParameterHandler.QueryMap.apply(retrofit2.RequestBuilder, java.util.Map):void");
        }

        QueryMap(Converter<T, String> valueConverter, boolean encoded) {
            this.valueConverter = valueConverter;
            this.encoded = encoded;
        }
    }

    static final class QueryName<T> extends ParameterHandler<T> {
        private final boolean encoded;
        private final Converter<T, String> nameConverter;

        QueryName(Converter<T, String> nameConverter, boolean encoded) {
            this.nameConverter = nameConverter;
            this.encoded = encoded;
        }

        void apply(RequestBuilder builder, @Nullable T value) throws IOException {
            if (value != null) {
                builder.addQueryParam((String) this.nameConverter.convert(value), null, this.encoded);
            }
        }
    }

    static final class RawPart extends ParameterHandler<okhttp3.MultipartBody.Part> {
        static final RawPart INSTANCE = new RawPart();

        private RawPart() {
        }

        void apply(RequestBuilder builder, @Nullable okhttp3.MultipartBody.Part value) {
            if (value != null) {
                builder.addPart(value);
            }
        }
    }

    static final class RelativeUrl extends ParameterHandler<Object> {
        RelativeUrl() {
        }

        void apply(RequestBuilder builder, @Nullable Object value) {
            Utils.checkNotNull(value, "@Url parameter is null.");
            builder.setRelativeUrl(value);
        }
    }

    abstract void apply(RequestBuilder requestBuilder, @Nullable T t) throws IOException;

    ParameterHandler() {
    }

    final ParameterHandler<Iterable<T>> iterable() {
        return new C00201();
    }

    final ParameterHandler<Object> array() {
        return new C00212();
    }
}
