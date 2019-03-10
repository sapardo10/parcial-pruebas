package org.awaitility.core;

public interface Predicate<T> {
    boolean matches(T t);
}
