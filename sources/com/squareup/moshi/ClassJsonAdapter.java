package com.squareup.moshi;

import com.squareup.moshi.JsonAdapter.Factory;
import com.squareup.moshi.JsonReader.Options;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

final class ClassJsonAdapter<T> extends JsonAdapter<T> {
    public static final Factory FACTORY = new ClassJsonAdapter$1();
    private final ClassFactory<T> classFactory;
    private final FieldBinding<?>[] fieldsArray;
    private final Options options;

    static class FieldBinding<T> {
        final JsonAdapter<T> adapter;
        final Field field;
        final String name;

        FieldBinding(String name, Field field, JsonAdapter<T> adapter) {
            this.name = name;
            this.field = field;
            this.adapter = adapter;
        }

        void read(JsonReader reader, Object value) throws IOException, IllegalAccessException {
            this.field.set(value, this.adapter.fromJson(reader));
        }

        void write(JsonWriter writer, Object value) throws IllegalAccessException, IOException {
            this.adapter.toJson(writer, this.field.get(value));
        }
    }

    public T fromJson(com.squareup.moshi.JsonReader r4) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:25:0x0047 in {9, 10, 12, 15, 18, 21, 24} preds:[]
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
        r3 = this;
        r0 = r3.classFactory;	 Catch:{ InstantiationException -> 0x0040, InvocationTargetException -> 0x003a, IllegalAccessException -> 0x0033 }
        r0 = r0.newInstance();	 Catch:{ InstantiationException -> 0x0040, InvocationTargetException -> 0x003a, IllegalAccessException -> 0x0033 }
        r4.beginObject();	 Catch:{ IllegalAccessException -> 0x002c }
    L_0x000a:
        r1 = r4.hasNext();	 Catch:{ IllegalAccessException -> 0x002c }
        if (r1 == 0) goto L_0x0028;	 Catch:{ IllegalAccessException -> 0x002c }
    L_0x0010:
        r1 = r3.options;	 Catch:{ IllegalAccessException -> 0x002c }
        r1 = r4.selectName(r1);	 Catch:{ IllegalAccessException -> 0x002c }
        r2 = -1;	 Catch:{ IllegalAccessException -> 0x002c }
        if (r1 != r2) goto L_0x0020;	 Catch:{ IllegalAccessException -> 0x002c }
    L_0x0019:
        r4.skipName();	 Catch:{ IllegalAccessException -> 0x002c }
        r4.skipValue();	 Catch:{ IllegalAccessException -> 0x002c }
        goto L_0x000a;	 Catch:{ IllegalAccessException -> 0x002c }
    L_0x0020:
        r2 = r3.fieldsArray;	 Catch:{ IllegalAccessException -> 0x002c }
        r2 = r2[r1];	 Catch:{ IllegalAccessException -> 0x002c }
        r2.read(r4, r0);	 Catch:{ IllegalAccessException -> 0x002c }
        goto L_0x000a;	 Catch:{ IllegalAccessException -> 0x002c }
    L_0x0028:
        r4.endObject();	 Catch:{ IllegalAccessException -> 0x002c }
        return r0;
    L_0x002c:
        r1 = move-exception;
        r2 = new java.lang.AssertionError;
        r2.<init>();
        throw r2;
    L_0x0033:
        r0 = move-exception;
        r1 = new java.lang.AssertionError;
        r1.<init>();
        throw r1;
    L_0x003a:
        r0 = move-exception;
        r1 = com.squareup.moshi.internal.Util.rethrowCause(r0);
        throw r1;
    L_0x0040:
        r0 = move-exception;
        r1 = new java.lang.RuntimeException;
        r1.<init>(r0);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.squareup.moshi.ClassJsonAdapter.fromJson(com.squareup.moshi.JsonReader):T");
    }

    public void toJson(com.squareup.moshi.JsonWriter r6, T r7) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:10:0x0022 in {3, 6, 9} preds:[]
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
        r6.beginObject();	 Catch:{ IllegalAccessException -> 0x001b }
        r0 = r5.fieldsArray;	 Catch:{ IllegalAccessException -> 0x001b }
        r1 = r0.length;	 Catch:{ IllegalAccessException -> 0x001b }
        r2 = 0;	 Catch:{ IllegalAccessException -> 0x001b }
    L_0x0007:
        if (r2 >= r1) goto L_0x0016;	 Catch:{ IllegalAccessException -> 0x001b }
    L_0x0009:
        r3 = r0[r2];	 Catch:{ IllegalAccessException -> 0x001b }
        r4 = r3.name;	 Catch:{ IllegalAccessException -> 0x001b }
        r6.name(r4);	 Catch:{ IllegalAccessException -> 0x001b }
        r3.write(r6, r7);	 Catch:{ IllegalAccessException -> 0x001b }
        r2 = r2 + 1;	 Catch:{ IllegalAccessException -> 0x001b }
        goto L_0x0007;	 Catch:{ IllegalAccessException -> 0x001b }
    L_0x0016:
        r6.endObject();	 Catch:{ IllegalAccessException -> 0x001b }
        return;
    L_0x001b:
        r0 = move-exception;
        r1 = new java.lang.AssertionError;
        r1.<init>();
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.squareup.moshi.ClassJsonAdapter.toJson(com.squareup.moshi.JsonWriter, java.lang.Object):void");
    }

    ClassJsonAdapter(ClassFactory<T> classFactory, Map<String, FieldBinding<?>> fieldsMap) {
        this.classFactory = classFactory;
        this.fieldsArray = (FieldBinding[]) fieldsMap.values().toArray(new FieldBinding[fieldsMap.size()]);
        this.options = Options.of((String[]) fieldsMap.keySet().toArray(new String[fieldsMap.size()]));
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("JsonAdapter(");
        stringBuilder.append(this.classFactory);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}
