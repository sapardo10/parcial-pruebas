package com.squareup.moshi;

import com.squareup.moshi.JsonAdapter.Factory;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

abstract class CollectionJsonAdapter<C extends Collection<T>, T> extends JsonAdapter<C> {
    public static final Factory FACTORY = new CollectionJsonAdapter$1();
    private final JsonAdapter<T> elementAdapter;

    /* renamed from: com.squareup.moshi.CollectionJsonAdapter$2 */
    class C11182 extends CollectionJsonAdapter<Collection<T>, T> {
        C11182(JsonAdapter elementAdapter) {
            super(elementAdapter);
        }

        public /* bridge */ /* synthetic */ Object fromJson(JsonReader jsonReader) throws IOException {
            return super.fromJson(jsonReader);
        }

        public /* bridge */ /* synthetic */ void toJson(JsonWriter jsonWriter, Object obj) throws IOException {
            super.toJson(jsonWriter, (Collection) obj);
        }

        Collection<T> newCollection() {
            return new ArrayList();
        }
    }

    /* renamed from: com.squareup.moshi.CollectionJsonAdapter$3 */
    class C11193 extends CollectionJsonAdapter<Set<T>, T> {
        C11193(JsonAdapter elementAdapter) {
            super(elementAdapter);
        }

        public /* bridge */ /* synthetic */ Object fromJson(JsonReader jsonReader) throws IOException {
            return super.fromJson(jsonReader);
        }

        public /* bridge */ /* synthetic */ void toJson(JsonWriter jsonWriter, Object obj) throws IOException {
            super.toJson(jsonWriter, (Set) obj);
        }

        Set<T> newCollection() {
            return new LinkedHashSet();
        }
    }

    abstract C newCollection();

    private CollectionJsonAdapter(JsonAdapter<T> elementAdapter) {
        this.elementAdapter = elementAdapter;
    }

    static <T> JsonAdapter<Collection<T>> newArrayListAdapter(Type type, Moshi moshi) {
        return new C11182(moshi.adapter(Types.collectionElementType(type, Collection.class)));
    }

    static <T> JsonAdapter<Set<T>> newLinkedHashSetAdapter(Type type, Moshi moshi) {
        return new C11193(moshi.adapter(Types.collectionElementType(type, Collection.class)));
    }

    public C fromJson(JsonReader reader) throws IOException {
        C result = newCollection();
        reader.beginArray();
        while (reader.hasNext()) {
            result.add(this.elementAdapter.fromJson(reader));
        }
        reader.endArray();
        return result;
    }

    public void toJson(JsonWriter writer, C value) throws IOException {
        writer.beginArray();
        for (T element : value) {
            this.elementAdapter.toJson(writer, element);
        }
        writer.endArray();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.elementAdapter);
        stringBuilder.append(".collection()");
        return stringBuilder.toString();
    }
}
