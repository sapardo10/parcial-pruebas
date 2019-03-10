package com.squareup.moshi;

import com.squareup.moshi.AdapterMethodsFactory.AdapterMethod;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Set;

class AdapterMethodsFactory$4 extends AdapterMethod {
    AdapterMethodsFactory$4(Type type, Set annotations, Object adapter, Method method, int parameterCount, int adaptersOffset, boolean nullable) {
        super(type, annotations, adapter, method, parameterCount, adaptersOffset, nullable);
    }

    public Object fromJson(Moshi moshi, JsonReader reader) throws IOException, InvocationTargetException {
        return invoke(reader);
    }
}
