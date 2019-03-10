package com.squareup.moshi.adapters;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonReader$Token;
import com.squareup.moshi.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

final class RuntimeJsonAdapterFactory$RuntimeJsonAdapter extends JsonAdapter<Object> {
    final String labelKey;
    final Map<String, JsonAdapter<Object>> labelToAdapter;
    final JsonAdapter<Object> objectJsonAdapter;
    final Map<Type, String> typeToLabel;

    RuntimeJsonAdapterFactory$RuntimeJsonAdapter(String labelKey, Map<String, JsonAdapter<Object>> labelToAdapter, Map<Type, String> typeToLabel, JsonAdapter<Object> objectJsonAdapter) {
        this.labelKey = labelKey;
        this.labelToAdapter = labelToAdapter;
        this.typeToLabel = typeToLabel;
        this.objectJsonAdapter = objectJsonAdapter;
    }

    public Object fromJson(JsonReader reader) throws IOException {
        JsonReader$Token peekedToken = reader.peek();
        if (peekedToken == JsonReader$Token.BEGIN_OBJECT) {
            Map<String, Object> jsonValue = reader.readJsonValue();
            Object label = jsonValue.get(this.labelKey);
            StringBuilder stringBuilder;
            if (label == null) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("Missing label for ");
                stringBuilder.append(this.labelKey);
                throw new JsonDataException(stringBuilder.toString());
            } else if (label instanceof String) {
                JsonAdapter<Object> adapter = (JsonAdapter) this.labelToAdapter.get(label);
                if (adapter != null) {
                    return adapter.fromJsonValue(jsonValue);
                }
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Expected one of ");
                stringBuilder2.append(this.labelToAdapter.keySet());
                stringBuilder2.append(" for key '");
                stringBuilder2.append(this.labelKey);
                stringBuilder2.append("' but found '");
                stringBuilder2.append(label);
                stringBuilder2.append("'. Register a subtype for this label.");
                throw new JsonDataException(stringBuilder2.toString());
            } else {
                stringBuilder = new StringBuilder();
                stringBuilder.append("Label for '");
                stringBuilder.append(this.labelKey);
                stringBuilder.append("' must be a string but was ");
                stringBuilder.append(label);
                stringBuilder.append(", a ");
                stringBuilder.append(label.getClass());
                throw new JsonDataException(stringBuilder.toString());
            }
        }
        StringBuilder stringBuilder3 = new StringBuilder();
        stringBuilder3.append("Expected BEGIN_OBJECT but was ");
        stringBuilder3.append(peekedToken);
        stringBuilder3.append(" at path ");
        stringBuilder3.append(reader.getPath());
        throw new JsonDataException(stringBuilder3.toString());
    }

    public void toJson(JsonWriter writer, Object value) throws IOException {
        String label = (String) this.typeToLabel.get(value.getClass());
        if (label != null) {
            Map<String, Object> jsonValue = (Map) ((JsonAdapter) this.labelToAdapter.get(label)).toJsonValue(value);
            Map<String, Object> valueWithLabel = new LinkedHashMap(jsonValue.size() + 1);
            valueWithLabel.put(this.labelKey, label);
            valueWithLabel.putAll(jsonValue);
            this.objectJsonAdapter.toJson(writer, valueWithLabel);
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Expected one of ");
        stringBuilder.append(this.typeToLabel.keySet());
        stringBuilder.append(" but found ");
        stringBuilder.append(value);
        stringBuilder.append(", a ");
        stringBuilder.append(value.getClass());
        stringBuilder.append(". Register this subtype.");
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("RuntimeJsonAdapter(");
        stringBuilder.append(this.labelKey);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}
