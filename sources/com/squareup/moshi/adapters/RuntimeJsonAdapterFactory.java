package com.squareup.moshi.adapters;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonAdapter.Factory;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.CheckReturnValue;

final class RuntimeJsonAdapterFactory<T> implements Factory {
    final Class<T> baseType;
    final String labelKey;
    final Map<String, Type> labelToType = new LinkedHashMap();

    @CheckReturnValue
    public static <T> RuntimeJsonAdapterFactory<T> of(Class<T> baseType, String labelKey) {
        if (baseType == null) {
            throw new NullPointerException("baseType == null");
        } else if (labelKey == null) {
            throw new NullPointerException("labelKey == null");
        } else if (baseType != Object.class) {
            return new RuntimeJsonAdapterFactory(baseType, labelKey);
        } else {
            throw new IllegalArgumentException("The base type must not be Object. Consider using a marker interface.");
        }
    }

    RuntimeJsonAdapterFactory(Class<T> baseType, String labelKey) {
        this.baseType = baseType;
        this.labelKey = labelKey;
    }

    public RuntimeJsonAdapterFactory<T> registerSubtype(Class<? extends T> subtype, String label) {
        if (subtype == null) {
            throw new NullPointerException("subtype == null");
        } else if (label == null) {
            throw new NullPointerException("label == null");
        } else if (this.labelToType.containsKey(label) || this.labelToType.containsValue(subtype)) {
            throw new IllegalArgumentException("Subtypes and labels must be unique.");
        } else {
            this.labelToType.put(label, subtype);
            return this;
        }
    }

    public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations, Moshi moshi) {
        if (Types.getRawType(type) == this.baseType) {
            if (annotations.isEmpty()) {
                int size = this.labelToType.size();
                Map<String, JsonAdapter<Object>> labelToAdapter = new LinkedHashMap(size);
                Map<Type, String> typeToLabel = new LinkedHashMap(size);
                for (Entry<String, Type> entry : this.labelToType.entrySet()) {
                    String label = (String) entry.getKey();
                    Type typeValue = (Type) entry.getValue();
                    typeToLabel.put(typeValue, label);
                    labelToAdapter.put(label, moshi.adapter(typeValue));
                }
                return new RuntimeJsonAdapterFactory$RuntimeJsonAdapter(this.labelKey, labelToAdapter, typeToLabel, moshi.adapter(Object.class)).nullSafe();
            }
        }
        return null;
    }
}
