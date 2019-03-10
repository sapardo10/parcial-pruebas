package com.squareup.moshi.adapters;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonReader$Token;
import com.squareup.moshi.JsonReader.Options;
import com.squareup.moshi.JsonWriter;
import java.io.IOException;
import java.util.Arrays;
import javax.annotation.Nullable;

public final class EnumJsonAdapter<T extends Enum<T>> extends JsonAdapter<T> {
    final T[] constants;
    final Class<T> enumType;
    @Nullable
    final T fallbackValue;
    final String[] nameStrings;
    final Options options;
    final boolean useFallbackValue;

    EnumJsonAdapter(java.lang.Class<T> r6, @javax.annotation.Nullable T r7, boolean r8) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:16:0x0067 in {7, 8, 9, 12, 15} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
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
        r5.fallbackValue = r7;
        r5.useFallbackValue = r8;
        r0 = r6.getEnumConstants();	 Catch:{ NoSuchFieldException -> 0x004b }
        r0 = (java.lang.Enum[]) r0;	 Catch:{ NoSuchFieldException -> 0x004b }
        r5.constants = r0;	 Catch:{ NoSuchFieldException -> 0x004b }
        r0 = r5.constants;	 Catch:{ NoSuchFieldException -> 0x004b }
        r0 = r0.length;	 Catch:{ NoSuchFieldException -> 0x004b }
        r0 = new java.lang.String[r0];	 Catch:{ NoSuchFieldException -> 0x004b }
        r5.nameStrings = r0;	 Catch:{ NoSuchFieldException -> 0x004b }
        r0 = 0;	 Catch:{ NoSuchFieldException -> 0x004b }
    L_0x0019:
        r1 = r5.constants;	 Catch:{ NoSuchFieldException -> 0x004b }
        r1 = r1.length;	 Catch:{ NoSuchFieldException -> 0x004b }
        if (r0 >= r1) goto L_0x0041;	 Catch:{ NoSuchFieldException -> 0x004b }
    L_0x001e:
        r1 = r5.constants;	 Catch:{ NoSuchFieldException -> 0x004b }
        r1 = r1[r0];	 Catch:{ NoSuchFieldException -> 0x004b }
        r1 = r1.name();	 Catch:{ NoSuchFieldException -> 0x004b }
        r2 = r6.getField(r1);	 Catch:{ NoSuchFieldException -> 0x004b }
        r3 = com.squareup.moshi.Json.class;	 Catch:{ NoSuchFieldException -> 0x004b }
        r2 = r2.getAnnotation(r3);	 Catch:{ NoSuchFieldException -> 0x004b }
        r2 = (com.squareup.moshi.Json) r2;	 Catch:{ NoSuchFieldException -> 0x004b }
        if (r2 == 0) goto L_0x0039;	 Catch:{ NoSuchFieldException -> 0x004b }
    L_0x0034:
        r3 = r2.name();	 Catch:{ NoSuchFieldException -> 0x004b }
        goto L_0x003a;	 Catch:{ NoSuchFieldException -> 0x004b }
    L_0x0039:
        r3 = r1;	 Catch:{ NoSuchFieldException -> 0x004b }
    L_0x003a:
        r4 = r5.nameStrings;	 Catch:{ NoSuchFieldException -> 0x004b }
        r4[r0] = r3;	 Catch:{ NoSuchFieldException -> 0x004b }
        r0 = r0 + 1;	 Catch:{ NoSuchFieldException -> 0x004b }
        goto L_0x0019;	 Catch:{ NoSuchFieldException -> 0x004b }
    L_0x0041:
        r0 = r5.nameStrings;	 Catch:{ NoSuchFieldException -> 0x004b }
        r0 = com.squareup.moshi.JsonReader.Options.of(r0);	 Catch:{ NoSuchFieldException -> 0x004b }
        r5.options = r0;	 Catch:{ NoSuchFieldException -> 0x004b }
        return;
    L_0x004b:
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
        throw new UnsupportedOperationException("Method not decompiled: com.squareup.moshi.adapters.EnumJsonAdapter.<init>(java.lang.Class, java.lang.Enum, boolean):void");
    }

    public static <T extends Enum<T>> EnumJsonAdapter<T> create(Class<T> enumType) {
        return new EnumJsonAdapter(enumType, null, false);
    }

    public EnumJsonAdapter<T> withUnknownFallback(@Nullable T fallbackValue) {
        return new EnumJsonAdapter(this.enumType, fallbackValue, true);
    }

    @Nullable
    public T fromJson(JsonReader reader) throws IOException {
        int index = reader.selectString(this.options);
        if (index != -1) {
            return this.constants[index];
        }
        String path = reader.getPath();
        if (!this.useFallbackValue) {
            String name = reader.nextString();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Expected one of ");
            stringBuilder.append(Arrays.asList(this.nameStrings));
            stringBuilder.append(" but was ");
            stringBuilder.append(name);
            stringBuilder.append(" at path ");
            stringBuilder.append(path);
            throw new JsonDataException(stringBuilder.toString());
        } else if (reader.peek() == JsonReader$Token.STRING) {
            reader.skipValue();
            return this.fallbackValue;
        } else {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Expected a string but was ");
            stringBuilder2.append(reader.peek());
            stringBuilder2.append(" at path ");
            stringBuilder2.append(path);
            throw new JsonDataException(stringBuilder2.toString());
        }
    }

    public void toJson(JsonWriter writer, T value) throws IOException {
        if (value != null) {
            writer.value(this.nameStrings[value.ordinal()]);
            return;
        }
        throw new NullPointerException("value was null! Wrap in .nullSafe() to write nullable values.");
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("EnumJsonAdapter(");
        stringBuilder.append(this.enumType.getName());
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}
