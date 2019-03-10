package org.awaitility.core;

public class PredicateExceptionIgnorer implements ExceptionIgnorer {
    private final Predicate<? super Throwable> predicate;

    public PredicateExceptionIgnorer(Predicate<? super Throwable> predicate) {
        if (predicate != null) {
            this.predicate = predicate;
            return;
        }
        throw new IllegalArgumentException("predicate cannot be null");
    }

    public boolean shouldIgnoreException(Throwable exception) {
        return this.predicate.matches(exception);
    }
}
