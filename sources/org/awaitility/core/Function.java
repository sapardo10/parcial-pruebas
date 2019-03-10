package org.awaitility.core;

public interface Function<T, R> {
    R apply(T t);
}
