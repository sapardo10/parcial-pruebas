package com.squareup.moshi;

import com.squareup.moshi.JsonAdapter.Factory;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

class MapJsonAdapter$1 implements Factory {
    MapJsonAdapter$1() {
    }

    @Nullable
    public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations, Moshi moshi) {
        if (!annotations.isEmpty()) {
            return null;
        }
        Class<?> rawType = Types.getRawType(type);
        if (rawType != Map.class) {
            return null;
        }
        Type[] keyAndValue = Types.mapKeyAndValueTypes(type, rawType);
        return new MapJsonAdapter(moshi, keyAndValue[0], keyAndValue[1]).nullSafe();
    }
}
