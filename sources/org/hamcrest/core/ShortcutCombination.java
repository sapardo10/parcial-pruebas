package org.hamcrest.core;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

abstract class ShortcutCombination<T> extends BaseMatcher<T> {
    private final Iterable<Matcher<? super T>> matchers;

    public abstract void describeTo(Description description);

    public abstract boolean matches(Object obj);

    public ShortcutCombination(Iterable<Matcher<? super T>> matchers) {
        this.matchers = matchers;
    }

    protected boolean matches(Object o, boolean shortcut) {
        for (Matcher<? super T> matcher : this.matchers) {
            if (matcher.matches(o) == shortcut) {
                return shortcut;
            }
        }
        return shortcut ^ 1;
    }

    public void describeTo(Description description, String operator) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(StringUtils.SPACE);
        stringBuilder.append(operator);
        stringBuilder.append(StringUtils.SPACE);
        description.appendList("(", stringBuilder.toString(), ")", this.matchers);
    }
}
