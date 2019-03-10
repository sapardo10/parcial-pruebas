package com.bumptech.glide.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MultiClassKey {
    private Class<?> first;
    private Class<?> second;
    private Class<?> third;

    public MultiClassKey(@NonNull Class<?> first, @NonNull Class<?> second) {
        set(first, second);
    }

    public MultiClassKey(@NonNull Class<?> first, @NonNull Class<?> second, @Nullable Class<?> third) {
        set(first, second, third);
    }

    public void set(@NonNull Class<?> first, @NonNull Class<?> second) {
        set(first, second, null);
    }

    public void set(@NonNull Class<?> first, @NonNull Class<?> second, @Nullable Class<?> third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("MultiClassKey{first=");
        stringBuilder.append(this.first);
        stringBuilder.append(", second=");
        stringBuilder.append(this.second);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o != null) {
            if (getClass() == o.getClass()) {
                MultiClassKey that = (MultiClassKey) o;
                if (this.first.equals(that.first) && this.second.equals(that.second) && Util.bothNullOrEqual(this.third, that.third)) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public int hashCode() {
        int hashCode = ((this.first.hashCode() * 31) + this.second.hashCode()) * 31;
        Class cls = this.third;
        return hashCode + (cls != null ? cls.hashCode() : 0);
    }
}
