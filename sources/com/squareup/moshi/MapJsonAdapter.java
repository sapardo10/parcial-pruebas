package com.squareup.moshi;

import com.squareup.moshi.JsonAdapter.Factory;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;

final class MapJsonAdapter<K, V> extends JsonAdapter<Map<K, V>> {
    public static final Factory FACTORY = new MapJsonAdapter$1();
    private final JsonAdapter<K> keyAdapter;
    private final JsonAdapter<V> valueAdapter;

    MapJsonAdapter(Moshi moshi, Type keyType, Type valueType) {
        this.keyAdapter = moshi.adapter(keyType);
        this.valueAdapter = moshi.adapter(valueType);
    }

    public void toJson(JsonWriter writer, Map<K, V> map) throws IOException {
        writer.beginObject();
        for (Entry<K, V> entry : map.entrySet()) {
            if (entry.getKey() != null) {
                writer.promoteValueToName();
                this.keyAdapter.toJson(writer, entry.getKey());
                this.valueAdapter.toJson(writer, entry.getValue());
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Map key is null at ");
                stringBuilder.append(writer.getPath());
                throw new JsonDataException(stringBuilder.toString());
            }
        }
        writer.endObject();
    }

    public Map<K, V> fromJson(JsonReader reader) throws IOException {
        LinkedHashTreeMap<K, V> result = new LinkedHashTreeMap();
        reader.beginObject();
        while (reader.hasNext()) {
            reader.promoteNameToValue();
            K name = this.keyAdapter.fromJson(reader);
            V value = this.valueAdapter.fromJson(reader);
            V replaced = result.put(name, value);
            if (replaced != null) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Map key '");
                stringBuilder.append(name);
                stringBuilder.append("' has multiple values at path ");
                stringBuilder.append(reader.getPath());
                stringBuilder.append(": ");
                stringBuilder.append(replaced);
                stringBuilder.append(" and ");
                stringBuilder.append(value);
                throw new JsonDataException(stringBuilder.toString());
            }
        }
        reader.endObject();
        return result;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("JsonAdapter(");
        stringBuilder.append(this.keyAdapter);
        stringBuilder.append("=");
        stringBuilder.append(this.valueAdapter);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}
