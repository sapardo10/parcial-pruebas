package com.squareup.moshi;

import com.squareup.moshi.ClassJsonAdapter.FieldBinding;
import com.squareup.moshi.JsonAdapter.Factory;
import com.squareup.moshi.internal.Util;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.annotation.Nullable;

class ClassJsonAdapter$1 implements Factory {
    ClassJsonAdapter$1() {
    }

    @Nullable
    public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations, Moshi moshi) {
        if (!(type instanceof Class) && !(type instanceof ParameterizedType)) {
            return null;
        }
        Class<?> rawType = Types.getRawType(type);
        if (!rawType.isInterface()) {
            if (!rawType.isEnum()) {
                StringBuilder stringBuilder;
                if (Util.isPlatformType(rawType)) {
                    if (!Types.isAllowedPlatformType(rawType)) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Platform ");
                        stringBuilder.append(Util.typeAnnotatedWithAnnotations(type, annotations));
                        stringBuilder.append(" requires explicit JsonAdapter to be registered");
                        throw new IllegalArgumentException(stringBuilder.toString());
                    }
                }
                if (!annotations.isEmpty()) {
                    return null;
                }
                if (rawType.isAnonymousClass()) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Cannot serialize anonymous class ");
                    stringBuilder.append(rawType.getName());
                    throw new IllegalArgumentException(stringBuilder.toString());
                } else if (rawType.isLocalClass()) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Cannot serialize local class ");
                    stringBuilder.append(rawType.getName());
                    throw new IllegalArgumentException(stringBuilder.toString());
                } else {
                    if (rawType.getEnclosingClass() != null) {
                        if (!Modifier.isStatic(rawType.getModifiers())) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("Cannot serialize non-static nested class ");
                            stringBuilder.append(rawType.getName());
                            throw new IllegalArgumentException(stringBuilder.toString());
                        }
                    }
                    if (Modifier.isAbstract(rawType.getModifiers())) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Cannot serialize abstract class ");
                        stringBuilder.append(rawType.getName());
                        throw new IllegalArgumentException(stringBuilder.toString());
                    }
                    ClassFactory<Object> classFactory = ClassFactory.get(rawType);
                    Map<String, FieldBinding<?>> fields = new TreeMap();
                    for (Type t = type; t != Object.class; t = Types.getGenericSuperclass(t)) {
                        createFieldBindings(moshi, t, fields);
                    }
                    return new ClassJsonAdapter(classFactory, fields).nullSafe();
                }
            }
        }
        return null;
    }

    private void createFieldBindings(Moshi moshi, Type type, Map<String, FieldBinding<?>> fieldBindings) {
        Class<?> rawType;
        Moshi moshi2;
        Type type2;
        Class<?> rawType2 = Types.getRawType(type);
        boolean platformType = Util.isPlatformType(rawType2);
        Field[] declaredFields = rawType2.getDeclaredFields();
        int length = declaredFields.length;
        int i = 0;
        while (i < length) {
            AnnotatedElement field = declaredFields[i];
            if (includeField(platformType, field.getModifiers())) {
                Type fieldType = Util.resolve(type, rawType2, field.getGenericType());
                Set<? extends Annotation> annotations = Util.jsonAnnotations(field);
                String fieldName = field.getName();
                JsonAdapter<Object> adapter = moshi.adapter(fieldType, annotations, fieldName);
                field.setAccessible(true);
                Json jsonAnnotation = (Json) field.getAnnotation(Json.class);
                String name = jsonAnnotation != null ? jsonAnnotation.name() : fieldName;
                FieldBinding<Object> fieldBinding = new FieldBinding(name, field, adapter);
                rawType = rawType2;
                FieldBinding rawType3 = (FieldBinding) fieldBindings.put(name, fieldBinding);
                if (rawType3 != null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Conflicting fields:\n    ");
                    stringBuilder.append(rawType3.field);
                    stringBuilder.append("\n    ");
                    stringBuilder.append(fieldBinding.field);
                    throw new IllegalArgumentException(stringBuilder.toString());
                }
            } else {
                moshi2 = moshi;
                type2 = type;
                rawType = rawType2;
            }
            i++;
            rawType2 = rawType;
        }
        ClassJsonAdapter$1 classJsonAdapter$1 = this;
        moshi2 = moshi;
        type2 = type;
        rawType = rawType2;
    }

    private boolean includeField(boolean platformType, int modifiers) {
        boolean z = false;
        if (!Modifier.isStatic(modifiers)) {
            if (!Modifier.isTransient(modifiers)) {
                if (!(Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers))) {
                    if (platformType) {
                        return z;
                    }
                }
                z = true;
                return z;
            }
        }
        return false;
    }
}
