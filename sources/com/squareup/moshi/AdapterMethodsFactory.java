package com.squareup.moshi;

import com.squareup.moshi.JsonAdapter.Factory;
import com.squareup.moshi.internal.Util;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

final class AdapterMethodsFactory implements Factory {
    private final List<AdapterMethod> fromAdapters;
    private final List<AdapterMethod> toAdapters;

    static abstract class AdapterMethod {
        final Object adapter;
        final int adaptersOffset;
        final Set<? extends Annotation> annotations;
        final JsonAdapter<?>[] jsonAdapters;
        final Method method;
        final boolean nullable;
        final Type type;

        AdapterMethod(Type type, Set<? extends Annotation> annotations, Object adapter, Method method, int parameterCount, int adaptersOffset, boolean nullable) {
            this.type = Util.canonicalize(type);
            this.annotations = annotations;
            this.adapter = adapter;
            this.method = method;
            this.adaptersOffset = adaptersOffset;
            this.jsonAdapters = new JsonAdapter[(parameterCount - adaptersOffset)];
            this.nullable = nullable;
        }

        public void bind(Moshi moshi, Factory factory) {
            if (this.jsonAdapters.length > 0) {
                Type[] parameterTypes = this.method.getGenericParameterTypes();
                Annotation[][] parameterAnnotations = this.method.getParameterAnnotations();
                int size = parameterTypes.length;
                for (int i = this.adaptersOffset; i < size; i++) {
                    JsonAdapter nextAdapter;
                    Type type = ((ParameterizedType) parameterTypes[i]).getActualTypeArguments()[0];
                    Set jsonAnnotations = Util.jsonAnnotations(parameterAnnotations[i]);
                    JsonAdapter[] jsonAdapterArr = this.jsonAdapters;
                    int i2 = i - this.adaptersOffset;
                    if (Types.equals(this.type, type) && this.annotations.equals(jsonAnnotations)) {
                        nextAdapter = moshi.nextAdapter(factory, type, jsonAnnotations);
                    } else {
                        nextAdapter = moshi.adapter(type, jsonAnnotations);
                    }
                    jsonAdapterArr[i2] = nextAdapter;
                }
            }
        }

        public void toJson(Moshi moshi, JsonWriter writer, @Nullable Object value) throws IOException, InvocationTargetException {
            throw new AssertionError();
        }

        @Nullable
        public Object fromJson(Moshi moshi, JsonReader reader) throws IOException, InvocationTargetException {
            throw new AssertionError();
        }

        @Nullable
        protected Object invoke(@Nullable Object a1) throws InvocationTargetException {
            Object obj = this.jsonAdapters;
            Object[] args = new Object[(obj.length + 1)];
            args[0] = a1;
            System.arraycopy(obj, 0, args, 1, obj.length);
            try {
                return this.method.invoke(this.adapter, args);
            } catch (IllegalAccessException e) {
                throw new AssertionError();
            }
        }

        protected Object invoke(@Nullable Object a1, @Nullable Object a2) throws InvocationTargetException {
            Object obj = this.jsonAdapters;
            Object[] args = new Object[(obj.length + 2)];
            args[0] = a1;
            args[1] = a2;
            System.arraycopy(obj, 0, args, 2, obj.length);
            try {
                return this.method.invoke(this.adapter, args);
            } catch (IllegalAccessException e) {
                throw new AssertionError();
            }
        }
    }

    /* renamed from: com.squareup.moshi.AdapterMethodsFactory$2 */
    class C00062 extends AdapterMethod {
        C00062(Type type, Set annotations, Object adapter, Method method, int parameterCount, int adaptersOffset, boolean nullable) {
            super(type, annotations, adapter, method, parameterCount, adaptersOffset, nullable);
        }

        public void toJson(Moshi moshi, JsonWriter writer, @Nullable Object value) throws IOException, InvocationTargetException {
            invoke(writer, value);
        }
    }

    /* renamed from: com.squareup.moshi.AdapterMethodsFactory$3 */
    class C00073 extends AdapterMethod {
        private JsonAdapter<Object> delegate;
        final /* synthetic */ Type[] val$parameterTypes;
        final /* synthetic */ Set val$qualifierAnnotations;
        final /* synthetic */ Type val$returnType;
        final /* synthetic */ Set val$returnTypeAnnotations;

        C00073(Type type, Set annotations, Object adapter, Method method, int parameterCount, int adaptersOffset, boolean nullable, Type[] typeArr, Type type2, Set set, Set set2) {
            this.val$parameterTypes = typeArr;
            this.val$returnType = type2;
            this.val$qualifierAnnotations = set;
            this.val$returnTypeAnnotations = set2;
            super(type, annotations, adapter, method, parameterCount, adaptersOffset, nullable);
        }

        public void bind(Moshi moshi, Factory factory) {
            JsonAdapter nextAdapter;
            super.bind(moshi, factory);
            if (Types.equals(this.val$parameterTypes[0], this.val$returnType)) {
                if (this.val$qualifierAnnotations.equals(this.val$returnTypeAnnotations)) {
                    nextAdapter = moshi.nextAdapter(factory, this.val$returnType, this.val$returnTypeAnnotations);
                    this.delegate = nextAdapter;
                }
            }
            nextAdapter = moshi.adapter(this.val$returnType, this.val$returnTypeAnnotations);
            this.delegate = nextAdapter;
        }

        public void toJson(Moshi moshi, JsonWriter writer, @Nullable Object value) throws IOException, InvocationTargetException {
            this.delegate.toJson(writer, invoke(value));
        }
    }

    AdapterMethodsFactory(List<AdapterMethod> toAdapters, List<AdapterMethod> fromAdapters) {
        this.toAdapters = toAdapters;
        this.fromAdapters = fromAdapters;
    }

    @Nullable
    public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations, Moshi moshi) {
        AdapterMethod toAdapter = get(this.toAdapters, type, annotations);
        AdapterMethod fromAdapter = get(this.fromAdapters, type, annotations);
        if (toAdapter == null && fromAdapter == null) {
            return null;
        }
        if (toAdapter != null) {
            if (fromAdapter != null) {
                JsonAdapter<Object> delegate = null;
                if (toAdapter != null) {
                    toAdapter.bind(moshi, this);
                }
                if (fromAdapter != null) {
                    fromAdapter.bind(moshi, this);
                }
                final AdapterMethod adapterMethod = toAdapter;
                final JsonAdapter<Object> jsonAdapter = delegate;
                final Moshi moshi2 = moshi;
                final AdapterMethod adapterMethod2 = fromAdapter;
                final Set<? extends Annotation> set = annotations;
                final Type type2 = type;
                return new JsonAdapter<Object>() {
                    public void toJson(JsonWriter writer, @Nullable Object value) throws IOException {
                        AdapterMethod adapterMethod = adapterMethod;
                        if (adapterMethod == null) {
                            jsonAdapter.toJson(writer, value);
                        } else if (adapterMethod.nullable || value != null) {
                            try {
                                adapterMethod.toJson(moshi2, writer, value);
                            } catch (InvocationTargetException e) {
                                Throwable cause = e.getCause();
                                if (cause instanceof IOException) {
                                    throw ((IOException) cause);
                                }
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append(cause);
                                stringBuilder.append(" at ");
                                stringBuilder.append(writer.getPath());
                                throw new JsonDataException(stringBuilder.toString(), cause);
                            }
                        } else {
                            writer.nullValue();
                        }
                    }

                    @Nullable
                    public Object fromJson(JsonReader reader) throws IOException {
                        AdapterMethod adapterMethod = adapterMethod2;
                        if (adapterMethod == null) {
                            return jsonAdapter.fromJson(reader);
                        }
                        if (adapterMethod.nullable || reader.peek() != JsonReader$Token.NULL) {
                            try {
                                return adapterMethod2.fromJson(moshi2, reader);
                            } catch (InvocationTargetException e) {
                                Throwable cause = e.getCause();
                                if (cause instanceof IOException) {
                                    throw ((IOException) cause);
                                }
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append(cause);
                                stringBuilder.append(" at ");
                                stringBuilder.append(reader.getPath());
                                throw new JsonDataException(stringBuilder.toString(), cause);
                            }
                        }
                        reader.nextNull();
                        return null;
                    }

                    public String toString() {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("JsonAdapter");
                        stringBuilder.append(set);
                        stringBuilder.append("(");
                        stringBuilder.append(type2);
                        stringBuilder.append(")");
                        return stringBuilder.toString();
                    }
                };
            }
        }
        try {
            delegate = moshi.nextAdapter(this, type, annotations);
            if (toAdapter != null) {
                toAdapter.bind(moshi, this);
            }
            if (fromAdapter != null) {
                fromAdapter.bind(moshi, this);
            }
            final AdapterMethod adapterMethod3 = toAdapter;
            final JsonAdapter<Object> jsonAdapter2 = delegate;
            final Moshi moshi22 = moshi;
            final AdapterMethod adapterMethod22 = fromAdapter;
            final Set<? extends Annotation> set2 = annotations;
            final Type type22 = type;
            return /* anonymous class already generated */;
        } catch (IllegalArgumentException e) {
            String missingAnnotation = toAdapter == null ? "@ToJson" : "@FromJson";
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("No ");
            stringBuilder.append(missingAnnotation);
            stringBuilder.append(" adapter for ");
            stringBuilder.append(Util.typeAnnotatedWithAnnotations(type, annotations));
            throw new IllegalArgumentException(stringBuilder.toString(), e);
        }
    }

    public static AdapterMethodsFactory get(Object adapter) {
        List<AdapterMethod> toAdapters = new ArrayList();
        List<AdapterMethod> fromAdapters = new ArrayList();
        for (Class<?> c = adapter.getClass(); c != Object.class; c = c.getSuperclass()) {
            for (Method m : c.getDeclaredMethods()) {
                AdapterMethod toAdapter;
                AdapterMethod conflicting;
                if (m.isAnnotationPresent(ToJson.class)) {
                    toAdapter = toAdapter(adapter, m);
                    conflicting = get(toAdapters, toAdapter.type, toAdapter.annotations);
                    if (conflicting == null) {
                        toAdapters.add(toAdapter);
                    } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Conflicting @ToJson methods:\n    ");
                        stringBuilder.append(conflicting.method);
                        stringBuilder.append("\n    ");
                        stringBuilder.append(toAdapter.method);
                        throw new IllegalArgumentException(stringBuilder.toString());
                    }
                }
                if (m.isAnnotationPresent(FromJson.class)) {
                    toAdapter = fromAdapter(adapter, m);
                    conflicting = get(fromAdapters, toAdapter.type, toAdapter.annotations);
                    if (conflicting == null) {
                        fromAdapters.add(toAdapter);
                    } else {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Conflicting @FromJson methods:\n    ");
                        stringBuilder.append(conflicting.method);
                        stringBuilder.append("\n    ");
                        stringBuilder.append(toAdapter.method);
                        throw new IllegalArgumentException(stringBuilder.toString());
                    }
                }
            }
        }
        if (toAdapters.isEmpty()) {
            if (fromAdapters.isEmpty()) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Expected at least one @ToJson or @FromJson method on ");
                stringBuilder2.append(adapter.getClass().getName());
                throw new IllegalArgumentException(stringBuilder2.toString());
            }
        }
        return new AdapterMethodsFactory(toAdapters, fromAdapters);
    }

    static AdapterMethod toAdapter(Object adapter, Method method) {
        Method method2 = method;
        method2.setAccessible(true);
        Type returnType = method.getGenericReturnType();
        Type[] parameterTypes = method.getGenericParameterTypes();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        if (parameterTypes.length >= 2 && parameterTypes[0] == JsonWriter.class && returnType == Void.TYPE) {
            if (parametersAreJsonAdapters(2, parameterTypes)) {
                return new C00062(parameterTypes[1], Util.jsonAnnotations(parameterAnnotations[1]), adapter, method, parameterTypes.length, 2, true);
            }
        }
        if (parameterTypes.length != 1 || returnType == Void.TYPE) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unexpected signature for ");
            stringBuilder.append(method2);
            stringBuilder.append(".\n@ToJson method signatures may have one of the following structures:\n    <any access modifier> void toJson(JsonWriter writer, T value) throws <any>;\n    <any access modifier> void toJson(JsonWriter writer, T value, JsonAdapter<any> delegate, <any more delegates>) throws <any>;\n    <any access modifier> R toJson(T value) throws <any>;\n");
            throw new IllegalArgumentException(stringBuilder.toString());
        }
        Set<? extends Annotation> returnTypeAnnotations = Util.jsonAnnotations((AnnotatedElement) method);
        Set<? extends Annotation> qualifierAnnotations = Util.jsonAnnotations(parameterAnnotations[0]);
        return new C00073(parameterTypes[0], qualifierAnnotations, adapter, method, parameterTypes.length, 1, Util.hasNullable(parameterAnnotations[0]), parameterTypes, returnType, qualifierAnnotations, returnTypeAnnotations);
    }

    private static boolean parametersAreJsonAdapters(int offset, Type[] parameterTypes) {
        int i = offset;
        int length = parameterTypes.length;
        while (i < length) {
            if (!(parameterTypes[i] instanceof ParameterizedType) || ((ParameterizedType) parameterTypes[i]).getRawType() != JsonAdapter.class) {
                return false;
            }
            i++;
        }
        return true;
    }

    static AdapterMethod fromAdapter(Object adapter, Method method) {
        Method method2 = method;
        method2.setAccessible(true);
        Type returnType = method.getGenericReturnType();
        Set<? extends Annotation> returnTypeAnnotations = Util.jsonAnnotations((AnnotatedElement) method);
        Type[] parameterTypes = method.getGenericParameterTypes();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        if (parameterTypes.length >= 1 && parameterTypes[0] == JsonReader.class && returnType != Void.TYPE) {
            if (parametersAreJsonAdapters(1, parameterTypes)) {
                return new AdapterMethodsFactory$4(returnType, returnTypeAnnotations, adapter, method, parameterTypes.length, 1, true);
            }
        }
        if (parameterTypes.length != 1 || returnType == Void.TYPE) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unexpected signature for ");
            stringBuilder.append(method2);
            stringBuilder.append(".\n@FromJson method signatures may have one of the following structures:\n    <any access modifier> R fromJson(JsonReader jsonReader) throws <any>;\n    <any access modifier> R fromJson(JsonReader jsonReader, JsonAdapter<any> delegate, <any more delegates>) throws <any>;\n    <any access modifier> R fromJson(T value) throws <any>;\n");
            throw new IllegalArgumentException(stringBuilder.toString());
        }
        Set<? extends Annotation> qualifierAnnotations = Util.jsonAnnotations(parameterAnnotations[0]);
        return new AdapterMethodsFactory$5(returnType, returnTypeAnnotations, adapter, method, parameterTypes.length, 1, Util.hasNullable(parameterAnnotations[0]), parameterTypes, returnType, qualifierAnnotations, returnTypeAnnotations);
    }

    @Nullable
    private static AdapterMethod get(List<AdapterMethod> adapterMethods, Type type, Set<? extends Annotation> annotations) {
        int size = adapterMethods.size();
        for (int i = 0; i < size; i++) {
            AdapterMethod adapterMethod = (AdapterMethod) adapterMethods.get(i);
            if (Types.equals(adapterMethod.type, type) && adapterMethod.annotations.equals(annotations)) {
                return adapterMethod;
            }
        }
        return null;
    }
}
