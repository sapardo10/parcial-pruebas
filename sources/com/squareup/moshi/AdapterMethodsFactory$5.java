package com.squareup.moshi;

import com.squareup.moshi.AdapterMethodsFactory.AdapterMethod;
import com.squareup.moshi.JsonAdapter.Factory;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Set;

class AdapterMethodsFactory$5 extends AdapterMethod {
    JsonAdapter<Object> delegate;
    final /* synthetic */ Type[] val$parameterTypes;
    final /* synthetic */ Set val$qualifierAnnotations;
    final /* synthetic */ Type val$returnType;
    final /* synthetic */ Set val$returnTypeAnnotations;

    AdapterMethodsFactory$5(Type type, Set annotations, Object adapter, Method method, int parameterCount, int adaptersOffset, boolean nullable, Type[] typeArr, Type type2, Set set, Set set2) {
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
                nextAdapter = moshi.nextAdapter(factory, this.val$parameterTypes[0], this.val$qualifierAnnotations);
                this.delegate = nextAdapter;
            }
        }
        nextAdapter = moshi.adapter(this.val$parameterTypes[0], this.val$qualifierAnnotations);
        this.delegate = nextAdapter;
    }

    public Object fromJson(Moshi moshi, JsonReader reader) throws IOException, InvocationTargetException {
        return invoke(this.delegate.fromJson(reader));
    }
}
