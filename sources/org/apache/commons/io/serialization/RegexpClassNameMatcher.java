package org.apache.commons.io.serialization;

import java.util.regex.Pattern;

final class RegexpClassNameMatcher implements ClassNameMatcher {
    private final Pattern pattern;

    public RegexpClassNameMatcher(String regex) {
        this(Pattern.compile(regex));
    }

    public RegexpClassNameMatcher(Pattern pattern) {
        if (pattern != null) {
            this.pattern = pattern;
            return;
        }
        throw new IllegalArgumentException("Null pattern");
    }

    public boolean matches(String className) {
        return this.pattern.matcher(className).matches();
    }
}
