package org.awaitility.core;

public interface ExceptionIgnorer {
    boolean shouldIgnoreException(Throwable th);
}
