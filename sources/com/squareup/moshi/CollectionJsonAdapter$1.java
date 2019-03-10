package com.squareup.moshi;

import com.squareup.moshi.JsonAdapter.Factory;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

class CollectionJsonAdapter$1 implements Factory {
    CollectionJsonAdapter$1() {
    }

    @Nullable
    public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations, Moshi moshi) {
        Class<?> rawType = Types.getRawType(type);
        if (!annotations.isEmpty()) {
            return null;
        }
        if (rawType != List.class) {
            if (rawType != Collection.class) {
                if (rawType == Set.class) {
                    return CollectionJsonAdapter.newLinkedHashSetAdapter(type, moshi).nullSafe();
                }
                return null;
            }
        }
        return CollectionJsonAdapter.newArrayListAdapter(type, moshi).nullSafe();
    }
}
