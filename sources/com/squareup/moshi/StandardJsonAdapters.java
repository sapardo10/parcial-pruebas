package com.squareup.moshi;

import com.squareup.moshi.JsonAdapter.Factory;
import com.squareup.moshi.JsonReader.Options;
import com.squareup.moshi.internal.Util;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.text.Typography;

final class StandardJsonAdapters {
    static final JsonAdapter<Boolean> BOOLEAN_JSON_ADAPTER = new C09962();
    static final JsonAdapter<Byte> BYTE_JSON_ADAPTER = new C09973();
    static final JsonAdapter<Character> CHARACTER_JSON_ADAPTER = new C09984();
    static final JsonAdapter<Double> DOUBLE_JSON_ADAPTER = new C09995();
    private static final String ERROR_FORMAT = "Expected %s but was %s at path %s";
    public static final Factory FACTORY = new C09951();
    static final JsonAdapter<Float> FLOAT_JSON_ADAPTER = new C10006();
    static final JsonAdapter<Integer> INTEGER_JSON_ADAPTER = new C10017();
    static final JsonAdapter<Long> LONG_JSON_ADAPTER = new C10028();
    static final JsonAdapter<Short> SHORT_JSON_ADAPTER = new C10039();
    static final JsonAdapter<String> STRING_JSON_ADAPTER = new JsonAdapter<String>() {
        public String fromJson(JsonReader reader) throws IOException {
            return reader.nextString();
        }

        public void toJson(JsonWriter writer, String value) throws IOException {
            writer.value(value);
        }

        public String toString() {
            return "JsonAdapter(String)";
        }
    };

    /* renamed from: com.squareup.moshi.StandardJsonAdapters$1 */
    class C09951 implements Factory {
        C09951() {
        }

        public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations, Moshi moshi) {
            if (!annotations.isEmpty()) {
                return null;
            }
            if (type == Boolean.TYPE) {
                return StandardJsonAdapters.BOOLEAN_JSON_ADAPTER;
            }
            if (type == Byte.TYPE) {
                return StandardJsonAdapters.BYTE_JSON_ADAPTER;
            }
            if (type == Character.TYPE) {
                return StandardJsonAdapters.CHARACTER_JSON_ADAPTER;
            }
            if (type == Double.TYPE) {
                return StandardJsonAdapters.DOUBLE_JSON_ADAPTER;
            }
            if (type == Float.TYPE) {
                return StandardJsonAdapters.FLOAT_JSON_ADAPTER;
            }
            if (type == Integer.TYPE) {
                return StandardJsonAdapters.INTEGER_JSON_ADAPTER;
            }
            if (type == Long.TYPE) {
                return StandardJsonAdapters.LONG_JSON_ADAPTER;
            }
            if (type == Short.TYPE) {
                return StandardJsonAdapters.SHORT_JSON_ADAPTER;
            }
            if (type == Boolean.class) {
                return StandardJsonAdapters.BOOLEAN_JSON_ADAPTER.nullSafe();
            }
            if (type == Byte.class) {
                return StandardJsonAdapters.BYTE_JSON_ADAPTER.nullSafe();
            }
            if (type == Character.class) {
                return StandardJsonAdapters.CHARACTER_JSON_ADAPTER.nullSafe();
            }
            if (type == Double.class) {
                return StandardJsonAdapters.DOUBLE_JSON_ADAPTER.nullSafe();
            }
            if (type == Float.class) {
                return StandardJsonAdapters.FLOAT_JSON_ADAPTER.nullSafe();
            }
            if (type == Integer.class) {
                return StandardJsonAdapters.INTEGER_JSON_ADAPTER.nullSafe();
            }
            if (type == Long.class) {
                return StandardJsonAdapters.LONG_JSON_ADAPTER.nullSafe();
            }
            if (type == Short.class) {
                return StandardJsonAdapters.SHORT_JSON_ADAPTER.nullSafe();
            }
            if (type == String.class) {
                return StandardJsonAdapters.STRING_JSON_ADAPTER.nullSafe();
            }
            if (type == Object.class) {
                return new ObjectJsonAdapter(moshi).nullSafe();
            }
            Class<?> rawType = Types.getRawType(type);
            JsonClass jsonClass = (JsonClass) rawType.getAnnotation(JsonClass.class);
            if (jsonClass != null && jsonClass.generateAdapter()) {
                return StandardJsonAdapters.generatedAdapter(moshi, type, rawType).nullSafe();
            }
            if (rawType.isEnum()) {
                return new EnumJsonAdapter(rawType).nullSafe();
            }
            return null;
        }
    }

    /* renamed from: com.squareup.moshi.StandardJsonAdapters$2 */
    class C09962 extends JsonAdapter<Boolean> {
        C09962() {
        }

        public Boolean fromJson(JsonReader reader) throws IOException {
            return Boolean.valueOf(reader.nextBoolean());
        }

        public void toJson(JsonWriter writer, Boolean value) throws IOException {
            writer.value(value.booleanValue());
        }

        public String toString() {
            return "JsonAdapter(Boolean)";
        }
    }

    /* renamed from: com.squareup.moshi.StandardJsonAdapters$3 */
    class C09973 extends JsonAdapter<Byte> {
        C09973() {
        }

        public Byte fromJson(JsonReader reader) throws IOException {
            return Byte.valueOf((byte) StandardJsonAdapters.rangeCheckNextInt(reader, "a byte", -128, 255));
        }

        public void toJson(JsonWriter writer, Byte value) throws IOException {
            writer.value((long) (value.intValue() & 255));
        }

        public String toString() {
            return "JsonAdapter(Byte)";
        }
    }

    /* renamed from: com.squareup.moshi.StandardJsonAdapters$4 */
    class C09984 extends JsonAdapter<Character> {
        C09984() {
        }

        public Character fromJson(JsonReader reader) throws IOException {
            String value = reader.nextString();
            if (value.length() <= 1) {
                return Character.valueOf(value.charAt(0));
            }
            Object[] objArr = new Object[3];
            objArr[0] = "a char";
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(Typography.quote);
            stringBuilder.append(value);
            stringBuilder.append(Typography.quote);
            objArr[1] = stringBuilder.toString();
            objArr[2] = reader.getPath();
            throw new JsonDataException(String.format(StandardJsonAdapters.ERROR_FORMAT, objArr));
        }

        public void toJson(JsonWriter writer, Character value) throws IOException {
            writer.value(value.toString());
        }

        public String toString() {
            return "JsonAdapter(Character)";
        }
    }

    /* renamed from: com.squareup.moshi.StandardJsonAdapters$5 */
    class C09995 extends JsonAdapter<Double> {
        C09995() {
        }

        public Double fromJson(JsonReader reader) throws IOException {
            return Double.valueOf(reader.nextDouble());
        }

        public void toJson(JsonWriter writer, Double value) throws IOException {
            writer.value(value.doubleValue());
        }

        public String toString() {
            return "JsonAdapter(Double)";
        }
    }

    /* renamed from: com.squareup.moshi.StandardJsonAdapters$6 */
    class C10006 extends JsonAdapter<Float> {
        C10006() {
        }

        public Float fromJson(JsonReader reader) throws IOException {
            float value = (float) reader.nextDouble();
            if (!reader.isLenient()) {
                if (Float.isInfinite(value)) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("JSON forbids NaN and infinities: ");
                    stringBuilder.append(value);
                    stringBuilder.append(" at path ");
                    stringBuilder.append(reader.getPath());
                    throw new JsonDataException(stringBuilder.toString());
                }
            }
            return Float.valueOf(value);
        }

        public void toJson(JsonWriter writer, Float value) throws IOException {
            if (value != null) {
                writer.value(value);
                return;
            }
            throw new NullPointerException();
        }

        public String toString() {
            return "JsonAdapter(Float)";
        }
    }

    /* renamed from: com.squareup.moshi.StandardJsonAdapters$7 */
    class C10017 extends JsonAdapter<Integer> {
        C10017() {
        }

        public Integer fromJson(JsonReader reader) throws IOException {
            return Integer.valueOf(reader.nextInt());
        }

        public void toJson(JsonWriter writer, Integer value) throws IOException {
            writer.value((long) value.intValue());
        }

        public String toString() {
            return "JsonAdapter(Integer)";
        }
    }

    /* renamed from: com.squareup.moshi.StandardJsonAdapters$8 */
    class C10028 extends JsonAdapter<Long> {
        C10028() {
        }

        public Long fromJson(JsonReader reader) throws IOException {
            return Long.valueOf(reader.nextLong());
        }

        public void toJson(JsonWriter writer, Long value) throws IOException {
            writer.value(value.longValue());
        }

        public String toString() {
            return "JsonAdapter(Long)";
        }
    }

    /* renamed from: com.squareup.moshi.StandardJsonAdapters$9 */
    class C10039 extends JsonAdapter<Short> {
        C10039() {
        }

        public Short fromJson(JsonReader reader) throws IOException {
            return Short.valueOf((short) StandardJsonAdapters.rangeCheckNextInt(reader, "a short", -32768, 32767));
        }

        public void toJson(JsonWriter writer, Short value) throws IOException {
            writer.value((long) value.intValue());
        }

        public String toString() {
            return "JsonAdapter(Short)";
        }
    }

    static final class EnumJsonAdapter<T extends Enum<T>> extends JsonAdapter<T> {
        private final T[] constants;
        private final Class<T> enumType;
        private final String[] nameStrings;
        private final Options options;

        EnumJsonAdapter(java.lang.Class<T> r6) {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:16:0x0066 in {7, 8, 9, 12, 15} preds:[]
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
            r5 = this;
            r5.<init>();
            r5.enumType = r6;
            r0 = r6.getEnumConstants();	 Catch:{ NoSuchFieldException -> 0x004a }
            r0 = (java.lang.Enum[]) r0;	 Catch:{ NoSuchFieldException -> 0x004a }
            r5.constants = r0;	 Catch:{ NoSuchFieldException -> 0x004a }
            r0 = r5.constants;	 Catch:{ NoSuchFieldException -> 0x004a }
            r0 = r0.length;	 Catch:{ NoSuchFieldException -> 0x004a }
            r0 = new java.lang.String[r0];	 Catch:{ NoSuchFieldException -> 0x004a }
            r5.nameStrings = r0;	 Catch:{ NoSuchFieldException -> 0x004a }
            r0 = 0;	 Catch:{ NoSuchFieldException -> 0x004a }
        L_0x0015:
            r1 = r5.constants;	 Catch:{ NoSuchFieldException -> 0x004a }
            r1 = r1.length;	 Catch:{ NoSuchFieldException -> 0x004a }
            if (r0 >= r1) goto L_0x0040;	 Catch:{ NoSuchFieldException -> 0x004a }
        L_0x001a:
            r1 = r5.constants;	 Catch:{ NoSuchFieldException -> 0x004a }
            r1 = r1[r0];	 Catch:{ NoSuchFieldException -> 0x004a }
            r2 = r1.name();	 Catch:{ NoSuchFieldException -> 0x004a }
            r2 = r6.getField(r2);	 Catch:{ NoSuchFieldException -> 0x004a }
            r3 = com.squareup.moshi.Json.class;	 Catch:{ NoSuchFieldException -> 0x004a }
            r2 = r2.getAnnotation(r3);	 Catch:{ NoSuchFieldException -> 0x004a }
            r2 = (com.squareup.moshi.Json) r2;	 Catch:{ NoSuchFieldException -> 0x004a }
            if (r2 == 0) goto L_0x0035;	 Catch:{ NoSuchFieldException -> 0x004a }
        L_0x0030:
            r3 = r2.name();	 Catch:{ NoSuchFieldException -> 0x004a }
            goto L_0x0039;	 Catch:{ NoSuchFieldException -> 0x004a }
        L_0x0035:
            r3 = r1.name();	 Catch:{ NoSuchFieldException -> 0x004a }
        L_0x0039:
            r4 = r5.nameStrings;	 Catch:{ NoSuchFieldException -> 0x004a }
            r4[r0] = r3;	 Catch:{ NoSuchFieldException -> 0x004a }
            r0 = r0 + 1;	 Catch:{ NoSuchFieldException -> 0x004a }
            goto L_0x0015;	 Catch:{ NoSuchFieldException -> 0x004a }
        L_0x0040:
            r0 = r5.nameStrings;	 Catch:{ NoSuchFieldException -> 0x004a }
            r0 = com.squareup.moshi.JsonReader.Options.of(r0);	 Catch:{ NoSuchFieldException -> 0x004a }
            r5.options = r0;	 Catch:{ NoSuchFieldException -> 0x004a }
            return;
        L_0x004a:
            r0 = move-exception;
            r1 = new java.lang.AssertionError;
            r2 = new java.lang.StringBuilder;
            r2.<init>();
            r3 = "Missing field in ";
            r2.append(r3);
            r3 = r6.getName();
            r2.append(r3);
            r2 = r2.toString();
            r1.<init>(r2, r0);
            throw r1;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.squareup.moshi.StandardJsonAdapters.EnumJsonAdapter.<init>(java.lang.Class):void");
        }

        public T fromJson(JsonReader reader) throws IOException {
            int index = reader.selectString(this.options);
            if (index != -1) {
                return this.constants[index];
            }
            String path = reader.getPath();
            String name = reader.nextString();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Expected one of ");
            stringBuilder.append(Arrays.asList(this.nameStrings));
            stringBuilder.append(" but was ");
            stringBuilder.append(name);
            stringBuilder.append(" at path ");
            stringBuilder.append(path);
            throw new JsonDataException(stringBuilder.toString());
        }

        public void toJson(JsonWriter writer, T value) throws IOException {
            writer.value(this.nameStrings[value.ordinal()]);
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("JsonAdapter(");
            stringBuilder.append(this.enumType.getName());
            stringBuilder.append(")");
            return stringBuilder.toString();
        }
    }

    static final class ObjectJsonAdapter extends JsonAdapter<Object> {
        private final JsonAdapter<Boolean> booleanAdapter;
        private final JsonAdapter<Double> doubleAdapter;
        private final JsonAdapter<List> listJsonAdapter;
        private final JsonAdapter<Map> mapAdapter;
        private final Moshi moshi;
        private final JsonAdapter<String> stringAdapter;

        ObjectJsonAdapter(Moshi moshi) {
            this.moshi = moshi;
            this.listJsonAdapter = moshi.adapter(List.class);
            this.mapAdapter = moshi.adapter(Map.class);
            this.stringAdapter = moshi.adapter(String.class);
            this.doubleAdapter = moshi.adapter(Double.class);
            this.booleanAdapter = moshi.adapter(Boolean.class);
        }

        public Object fromJson(JsonReader reader) throws IOException {
            switch (reader.peek()) {
                case BEGIN_ARRAY:
                    return this.listJsonAdapter.fromJson(reader);
                case BEGIN_OBJECT:
                    return this.mapAdapter.fromJson(reader);
                case STRING:
                    return this.stringAdapter.fromJson(reader);
                case NUMBER:
                    return this.doubleAdapter.fromJson(reader);
                case BOOLEAN:
                    return this.booleanAdapter.fromJson(reader);
                case NULL:
                    return reader.nextNull();
                default:
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Expected a value but was ");
                    stringBuilder.append(reader.peek());
                    stringBuilder.append(" at path ");
                    stringBuilder.append(reader.getPath());
                    throw new IllegalStateException(stringBuilder.toString());
            }
        }

        public void toJson(JsonWriter writer, Object value) throws IOException {
            Class<?> valueClass = value.getClass();
            if (valueClass == Object.class) {
                writer.beginObject();
                writer.endObject();
                return;
            }
            this.moshi.adapter(toJsonType(valueClass), Util.NO_ANNOTATIONS).toJson(writer, value);
        }

        private Class<?> toJsonType(Class<?> valueClass) {
            if (Map.class.isAssignableFrom(valueClass)) {
                return Map.class;
            }
            if (Collection.class.isAssignableFrom(valueClass)) {
                return Collection.class;
            }
            return valueClass;
        }

        public String toString() {
            return "JsonAdapter(Object)";
        }
    }

    private StandardJsonAdapters() {
    }

    static int rangeCheckNextInt(JsonReader reader, String typeMessage, int min, int max) throws IOException {
        int value = reader.nextInt();
        if (value >= min && value <= max) {
            return value;
        }
        throw new JsonDataException(String.format(ERROR_FORMAT, new Object[]{typeMessage, Integer.valueOf(value), reader.getPath()}));
    }

    static JsonAdapter<?> generatedAdapter(Moshi moshi, Type type, Class<?> rawType) {
        StringBuilder stringBuilder;
        String adapterClassName = new StringBuilder();
        adapterClassName.append(rawType.getName().replace("$", "_"));
        adapterClassName.append("JsonAdapter");
        try {
            Class<? extends JsonAdapter<?>> adapterClass = Class.forName(adapterClassName.toString(), true, rawType.getClassLoader());
            if (type instanceof ParameterizedType) {
                Constructor<? extends JsonAdapter<?>> constructor = adapterClass.getDeclaredConstructor(new Class[]{Moshi.class, Type[].class});
                constructor.setAccessible(true);
                return (JsonAdapter) constructor.newInstance(new Object[]{moshi, ((ParameterizedType) type).getActualTypeArguments()});
            }
            Constructor<? extends JsonAdapter<?>> constructor2 = adapterClass.getDeclaredConstructor(new Class[]{Moshi.class});
            constructor2.setAccessible(true);
            return (JsonAdapter) constructor2.newInstance(new Object[]{moshi});
        } catch (ClassNotFoundException e) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("Failed to find the generated JsonAdapter class for ");
            stringBuilder.append(rawType);
            throw new RuntimeException(stringBuilder.toString(), e);
        } catch (NoSuchMethodException e2) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("Failed to find the generated JsonAdapter constructor for ");
            stringBuilder.append(rawType);
            throw new RuntimeException(stringBuilder.toString(), e2);
        } catch (IllegalAccessException e3) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("Failed to access the generated JsonAdapter for ");
            stringBuilder.append(rawType);
            throw new RuntimeException(stringBuilder.toString(), e3);
        } catch (InstantiationException e4) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("Failed to instantiate the generated JsonAdapter for ");
            stringBuilder.append(rawType);
            throw new RuntimeException(stringBuilder.toString(), e4);
        } catch (InvocationTargetException e5) {
            throw Util.rethrowCause(e5);
        }
    }
}
