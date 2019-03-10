package com.squareup.moshi;

import com.squareup.moshi.JsonAdapter.Factory;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;
import javax.annotation.Nullable;

class ArrayJsonAdapter$1 implements Factory {
    ArrayJsonAdapter$1() {
    }

    @Nullable
    public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations, Moshi moshi) {
        Type elementType = Types.arrayComponentType(type);
        if (elementType != null && annotations.isEmpty()) {
            return new ArrayJsonAdapter(Types.getRawType(elementType), moshi.adapter(elementType)).nullSafe();
        }
        return null;
    }
}
