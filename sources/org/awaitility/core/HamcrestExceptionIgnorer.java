package org.awaitility.core;

import org.hamcrest.Matcher;

public class HamcrestExceptionIgnorer implements ExceptionIgnorer {
    private final Matcher<? super Throwable> matcher;

    public HamcrestExceptionIgnorer(Matcher<? super Throwable> matcher) {
        if (matcher != null) {
            this.matcher = matcher;
            return;
        }
        throw new IllegalArgumentException("matcher cannot be null");
    }

    public boolean shouldIgnoreException(Throwable exception) {
        return this.matcher.matches(exception);
    }
}
