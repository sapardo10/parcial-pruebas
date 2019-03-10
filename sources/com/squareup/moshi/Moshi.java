package com.squareup.moshi;

import com.squareup.moshi.JsonAdapter.Factory;
import com.squareup.moshi.internal.Util;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

public final class Moshi {
    static final List<Factory> BUILT_IN_FACTORIES = new ArrayList(5);
    private final Map<Object, JsonAdapter<?>> adapterCache = new LinkedHashMap();
    private final List<Factory> factories;
    private final ThreadLocal<List<DeferredAdapter<?>>> reentrantCalls = new ThreadLocal();

    public static final class Builder {
        final List<Factory> factories = new ArrayList();

        public <T> Builder add(final Type type, final JsonAdapter<T> jsonAdapter) {
            if (type == null) {
                throw new IllegalArgumentException("type == null");
            } else if (jsonAdapter != null) {
                return add(new Factory() {
                    @Nullable
                    public JsonAdapter<?> create(Type targetType, Set<? extends Annotation> annotations, Moshi moshi) {
                        return (annotations.isEmpty() && Util.typesMatch(type, targetType)) ? jsonAdapter : null;
                    }
                });
            } else {
                throw new IllegalArgumentException("jsonAdapter == null");
            }
        }

        public <T> Builder add(final Type type, final Class<? extends Annotation> annotation, final JsonAdapter<T> jsonAdapter) {
            if (type == null) {
                throw new IllegalArgumentException("type == null");
            } else if (annotation == null) {
                throw new IllegalArgumentException("annotation == null");
            } else if (jsonAdapter == null) {
                throw new IllegalArgumentException("jsonAdapter == null");
            } else if (!annotation.isAnnotationPresent(JsonQualifier.class)) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(annotation);
                stringBuilder.append(" does not have @JsonQualifier");
                throw new IllegalArgumentException(stringBuilder.toString());
            } else if (annotation.getDeclaredMethods().length <= 0) {
                return add(new Factory() {
                    @Nullable
                    public JsonAdapter<?> create(Type targetType, Set<? extends Annotation> annotations, Moshi moshi) {
                        if (Util.typesMatch(type, targetType)) {
                            if (annotations.size() == 1) {
                                if (Util.isAnnotationPresent(annotations, annotation)) {
                                    return jsonAdapter;
                                }
                            }
                        }
                        return null;
                    }
                });
            } else {
                throw new IllegalArgumentException("Use JsonAdapter.Factory for annotations with elements");
            }
        }

        public Builder add(Factory factory) {
            if (factory != null) {
                this.factories.add(factory);
                return this;
            }
            throw new IllegalArgumentException("factory == null");
        }

        public Builder add(Object adapter) {
            if (adapter != null) {
                return add(AdapterMethodsFactory.get(adapter));
            }
            throw new IllegalArgumentException("adapter == null");
        }

        Builder addAll(List<Factory> factories) {
            this.factories.addAll(factories);
            return this;
        }

        @CheckReturnValue
        public Moshi build() {
            return new Moshi(this);
        }
    }

    private static final class DeferredAdapter<T> extends JsonAdapter<T> {
        @Nullable
        Object cacheKey;
        @Nullable
        private JsonAdapter<T> delegate;
        @Nullable
        final String fieldName;
        final Type type;

        DeferredAdapter(Type type, @Nullable String fieldName, Object cacheKey) {
            this.type = type;
            this.fieldName = fieldName;
            this.cacheKey = cacheKey;
        }

        void ready(JsonAdapter<T> delegate) {
            this.delegate = delegate;
            this.cacheKey = null;
        }

        public T fromJson(JsonReader reader) throws IOException {
            JsonAdapter jsonAdapter = this.delegate;
            if (jsonAdapter != null) {
                return jsonAdapter.fromJson(reader);
            }
            throw new IllegalStateException("Type adapter isn't ready");
        }

        public void toJson(JsonWriter writer, T value) throws IOException {
            JsonAdapter jsonAdapter = this.delegate;
            if (jsonAdapter != null) {
                jsonAdapter.toJson(writer, (Object) value);
                return;
            }
            throw new IllegalStateException("Type adapter isn't ready");
        }
    }

    @javax.annotation.CheckReturnValue
    public <T> com.squareup.moshi.JsonAdapter<T> adapter(java.lang.reflect.Type r10, java.util.Set<? extends java.lang.annotation.Annotation> r11, @javax.annotation.Nullable java.lang.String r12) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:70:0x00e1 in {8, 16, 17, 18, 19, 35, 36, 37, 42, 43, 45, 46, 48, 54, 55, 57, 59, 60, 61, 65, 67, 69} preds:[]
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
        r9 = this;
        if (r10 == 0) goto L_0x00d9;
    L_0x0002:
        if (r11 == 0) goto L_0x00d1;
    L_0x0004:
        r0 = com.squareup.moshi.internal.Util.canonicalize(r10);
        r1 = r9.cacheKey(r0, r11);
        r2 = r9.adapterCache;
        monitor-enter(r2);
        r10 = r9.adapterCache;	 Catch:{ all -> 0x00ce }
        r10 = r10.get(r1);	 Catch:{ all -> 0x00ce }
        r10 = (com.squareup.moshi.JsonAdapter) r10;	 Catch:{ all -> 0x00ce }
        if (r10 == 0) goto L_0x001b;	 Catch:{ all -> 0x00ce }
    L_0x0019:
        monitor-exit(r2);	 Catch:{ all -> 0x00ce }
        return r10;	 Catch:{ all -> 0x00ce }
    L_0x001b:
        monitor-exit(r2);	 Catch:{ all -> 0x00ce }
        r10 = r9.reentrantCalls;
        r10 = r10.get();
        r10 = (java.util.List) r10;
        if (r10 == 0) goto L_0x0041;
    L_0x0026:
        r2 = 0;
        r3 = r10.size();
    L_0x002b:
        if (r2 >= r3) goto L_0x0040;
    L_0x002d:
        r4 = r10.get(r2);
        r4 = (com.squareup.moshi.Moshi.DeferredAdapter) r4;
        r5 = r4.cacheKey;
        r5 = r5.equals(r1);
        if (r5 == 0) goto L_0x003c;
    L_0x003b:
        return r4;
        r2 = r2 + 1;
        goto L_0x002b;
    L_0x0040:
        goto L_0x004c;
    L_0x0041:
        r2 = new java.util.ArrayList;
        r2.<init>();
        r10 = r2;
        r2 = r9.reentrantCalls;
        r2.set(r10);
    L_0x004c:
        r2 = new com.squareup.moshi.Moshi$DeferredAdapter;
        r2.<init>(r0, r12, r1);
        r10.add(r2);
        r3 = r10.size();
        r3 = r3 + -1;
        r4 = 0;
        r5 = r9.factories;	 Catch:{ IllegalArgumentException -> 0x00b8 }
        r5 = r5.size();	 Catch:{ IllegalArgumentException -> 0x00b8 }
    L_0x0061:
        if (r4 >= r5) goto L_0x0092;	 Catch:{ IllegalArgumentException -> 0x00b8 }
    L_0x0063:
        r6 = r9.factories;	 Catch:{ IllegalArgumentException -> 0x00b8 }
        r6 = r6.get(r4);	 Catch:{ IllegalArgumentException -> 0x00b8 }
        r6 = (com.squareup.moshi.JsonAdapter.Factory) r6;	 Catch:{ IllegalArgumentException -> 0x00b8 }
        r6 = r6.create(r0, r11, r9);	 Catch:{ IllegalArgumentException -> 0x00b8 }
        if (r6 == 0) goto L_0x008e;	 Catch:{ IllegalArgumentException -> 0x00b8 }
    L_0x0071:
        r2.ready(r6);	 Catch:{ IllegalArgumentException -> 0x00b8 }
        r7 = r9.adapterCache;	 Catch:{ IllegalArgumentException -> 0x00b8 }
        monitor-enter(r7);	 Catch:{ IllegalArgumentException -> 0x00b8 }
        r8 = r9.adapterCache;	 Catch:{ all -> 0x008b }
        r8.put(r1, r6);	 Catch:{ all -> 0x008b }
        monitor-exit(r7);	 Catch:{ all -> 0x008b }
        r10.remove(r3);	 Catch:{ IllegalArgumentException -> 0x00b8 }
        if (r3 != 0) goto L_0x0089;
    L_0x0083:
        r7 = r9.reentrantCalls;
        r7.remove();
        goto L_0x008a;
    L_0x008a:
        return r6;
    L_0x008b:
        r8 = move-exception;
        monitor-exit(r7);	 Catch:{ all -> 0x008b }
        throw r8;	 Catch:{ IllegalArgumentException -> 0x00b8 }
        r4 = r4 + 1;
        goto L_0x0061;
    L_0x0092:
        if (r3 != 0) goto L_0x009a;
    L_0x0094:
        r4 = r9.reentrantCalls;
        r4.remove();
        goto L_0x009b;
    L_0x009b:
        r4 = new java.lang.IllegalArgumentException;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "No JsonAdapter for ";
        r5.append(r6);
        r6 = com.squareup.moshi.internal.Util.typeAnnotatedWithAnnotations(r0, r11);
        r5.append(r6);
        r5 = r5.toString();
        r4.<init>(r5);
        throw r4;
    L_0x00b6:
        r4 = move-exception;
        goto L_0x00c4;
    L_0x00b8:
        r4 = move-exception;
        if (r3 != 0) goto L_0x00c1;
    L_0x00bb:
        r5 = errorWithFields(r10, r4);	 Catch:{ all -> 0x00b6 }
        r4 = r5;	 Catch:{ all -> 0x00b6 }
        goto L_0x00c2;	 Catch:{ all -> 0x00b6 }
        throw r4;	 Catch:{ all -> 0x00b6 }
    L_0x00c4:
        if (r3 != 0) goto L_0x00cc;
    L_0x00c6:
        r5 = r9.reentrantCalls;
        r5.remove();
        goto L_0x00cd;
    L_0x00cd:
        throw r4;
    L_0x00ce:
        r10 = move-exception;
        monitor-exit(r2);	 Catch:{ all -> 0x00ce }
        throw r10;
    L_0x00d1:
        r0 = new java.lang.NullPointerException;
        r1 = "annotations == null";
        r0.<init>(r1);
        throw r0;
    L_0x00d9:
        r0 = new java.lang.NullPointerException;
        r1 = "type == null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.squareup.moshi.Moshi.adapter(java.lang.reflect.Type, java.util.Set, java.lang.String):com.squareup.moshi.JsonAdapter<T>");
    }

    @javax.annotation.CheckReturnValue
    public <T> com.squareup.moshi.JsonAdapter<T> nextAdapter(com.squareup.moshi.JsonAdapter.Factory r5, java.lang.reflect.Type r6, java.util.Set<? extends java.lang.annotation.Annotation> r7) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:15:0x0065 in {7, 8, 10, 12, 14} preds:[]
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
        r4 = this;
        if (r7 == 0) goto L_0x005d;
    L_0x0002:
        r6 = com.squareup.moshi.internal.Util.canonicalize(r6);
        r0 = r4.factories;
        r0 = r0.indexOf(r5);
        r1 = -1;
        if (r0 == r1) goto L_0x0046;
    L_0x000f:
        r1 = r0 + 1;
        r2 = r4.factories;
        r2 = r2.size();
    L_0x0017:
        if (r1 >= r2) goto L_0x002b;
    L_0x0019:
        r3 = r4.factories;
        r3 = r3.get(r1);
        r3 = (com.squareup.moshi.JsonAdapter.Factory) r3;
        r3 = r3.create(r6, r7, r4);
        if (r3 == 0) goto L_0x0028;
    L_0x0027:
        return r3;
    L_0x0028:
        r1 = r1 + 1;
        goto L_0x0017;
    L_0x002b:
        r1 = new java.lang.IllegalArgumentException;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "No next JsonAdapter for ";
        r2.append(r3);
        r3 = com.squareup.moshi.internal.Util.typeAnnotatedWithAnnotations(r6, r7);
        r2.append(r3);
        r2 = r2.toString();
        r1.<init>(r2);
        throw r1;
    L_0x0046:
        r1 = new java.lang.IllegalArgumentException;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "Unable to skip past unknown factory ";
        r2.append(r3);
        r2.append(r5);
        r2 = r2.toString();
        r1.<init>(r2);
        throw r1;
    L_0x005d:
        r0 = new java.lang.NullPointerException;
        r1 = "annotations == null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.squareup.moshi.Moshi.nextAdapter(com.squareup.moshi.JsonAdapter$Factory, java.lang.reflect.Type, java.util.Set):com.squareup.moshi.JsonAdapter<T>");
    }

    static {
        BUILT_IN_FACTORIES.add(StandardJsonAdapters.FACTORY);
        BUILT_IN_FACTORIES.add(CollectionJsonAdapter.FACTORY);
        BUILT_IN_FACTORIES.add(MapJsonAdapter.FACTORY);
        BUILT_IN_FACTORIES.add(ArrayJsonAdapter.FACTORY);
        BUILT_IN_FACTORIES.add(ClassJsonAdapter.FACTORY);
    }

    Moshi(Builder builder) {
        List<Factory> factories = new ArrayList(builder.factories.size() + BUILT_IN_FACTORIES.size());
        factories.addAll(builder.factories);
        factories.addAll(BUILT_IN_FACTORIES);
        this.factories = Collections.unmodifiableList(factories);
    }

    @CheckReturnValue
    public <T> JsonAdapter<T> adapter(Type type) {
        return adapter(type, Util.NO_ANNOTATIONS);
    }

    @CheckReturnValue
    public <T> JsonAdapter<T> adapter(Class<T> type) {
        return adapter((Type) type, Util.NO_ANNOTATIONS);
    }

    @CheckReturnValue
    public <T> JsonAdapter<T> adapter(Type type, Class<? extends Annotation> annotationType) {
        if (annotationType != null) {
            return adapter(type, Collections.singleton(Types.createJsonQualifierImplementation(annotationType)));
        }
        throw new NullPointerException("annotationType == null");
    }

    @CheckReturnValue
    public <T> JsonAdapter<T> adapter(Type type, Class<? extends Annotation>... annotationTypes) {
        int i = 0;
        if (annotationTypes.length == 1) {
            return adapter(type, annotationTypes[0]);
        }
        Set<Annotation> annotations = new LinkedHashSet(annotationTypes.length);
        int length = annotationTypes.length;
        while (i < length) {
            annotations.add(Types.createJsonQualifierImplementation(annotationTypes[i]));
            i++;
        }
        return adapter(type, Collections.unmodifiableSet(annotations));
    }

    @CheckReturnValue
    public <T> JsonAdapter<T> adapter(Type type, Set<? extends Annotation> annotations) {
        return adapter(type, annotations, null);
    }

    @CheckReturnValue
    public Builder newBuilder() {
        return new Builder().addAll(this.factories.subList(0, this.factories.size() - BUILT_IN_FACTORIES.size()));
    }

    private Object cacheKey(Type type, Set<? extends Annotation> annotations) {
        if (annotations.isEmpty()) {
            return type;
        }
        return Arrays.asList(new Object[]{type, annotations});
    }

    static IllegalArgumentException errorWithFields(List<DeferredAdapter<?>> typesAndFieldNames, IllegalArgumentException e) {
        int size = typesAndFieldNames.size();
        if (size == 1 && ((DeferredAdapter) typesAndFieldNames.get(0)).fieldName == null) {
            return e;
        }
        StringBuilder errorMessageBuilder = new StringBuilder(e.getMessage());
        for (int i = size - 1; i >= 0; i--) {
            DeferredAdapter<?> deferredAdapter = (DeferredAdapter) typesAndFieldNames.get(i);
            errorMessageBuilder.append("\nfor ");
            errorMessageBuilder.append(deferredAdapter.type);
            if (deferredAdapter.fieldName != null) {
                errorMessageBuilder.append(' ');
                errorMessageBuilder.append(deferredAdapter.fieldName);
            }
        }
        return new IllegalArgumentException(errorMessageBuilder.toString(), e);
    }
}
