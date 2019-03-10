package com.squareup.moshi;

import com.squareup.moshi.JsonAdapter.Factory;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

final class ArrayJsonAdapter extends JsonAdapter<Object> {
    public static final Factory FACTORY = new ArrayJsonAdapter$1();
    private final JsonAdapter<Object> elementAdapter;
    private final Class<?> elementClass;

    ArrayJsonAdapter(Class<?> elementClass, JsonAdapter<Object> elementAdapter) {
        this.elementClass = elementClass;
        this.elementAdapter = elementAdapter;
    }

    public Object fromJson(JsonReader reader) throws IOException {
        List<Object> list = new ArrayList();
        reader.beginArray();
        while (reader.hasNext()) {
            list.add(this.elementAdapter.fromJson(reader));
        }
        reader.endArray();
        Object array = Array.newInstance(this.elementClass, list.size());
        for (int i = 0; i < list.size(); i++) {
            Array.set(array, i, list.get(i));
        }
        return array;
    }

    public void toJson(JsonWriter writer, Object value) throws IOException {
        writer.beginArray();
        int size = Array.getLength(value);
        for (int i = 0; i < size; i++) {
            this.elementAdapter.toJson(writer, Array.get(value, i));
        }
        writer.endArray();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.elementAdapter);
        stringBuilder.append(".array()");
        return stringBuilder.toString();
    }
}
