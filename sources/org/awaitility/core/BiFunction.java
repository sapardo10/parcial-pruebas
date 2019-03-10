package org.awaitility.core;

public interface BiFunction<T, U, R> {
    R apply(T t, U u);
}
