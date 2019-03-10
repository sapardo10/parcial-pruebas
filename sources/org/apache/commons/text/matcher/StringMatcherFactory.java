package org.apache.commons.text.matcher;

import kotlin.text.Typography;

public final class StringMatcherFactory {
    private static final CharMatcher COMMA_MATCHER = new CharMatcher(',');
    private static final CharMatcher DOUBLE_QUOTE_MATCHER = new CharMatcher(Typography.quote);
    public static final StringMatcherFactory INSTANCE = new StringMatcherFactory();
    private static final NoMatcher NONE_MATCHER = new NoMatcher();
    private static final CharSetMatcher QUOTE_MATCHER = new CharSetMatcher("'\"".toCharArray());
    private static final CharMatcher SINGLE_QUOTE_MATCHER = new CharMatcher('\'');
    private static final CharMatcher SPACE_MATCHER = new CharMatcher(' ');
    private static final CharSetMatcher SPLIT_MATCHER = new CharSetMatcher(" \t\n\r\f".toCharArray());
    private static final CharMatcher TAB_MATCHER = new CharMatcher('\t');
    private static final TrimMatcher TRIM_MATCHER = new TrimMatcher();

    private StringMatcherFactory() {
    }

    public StringMatcher charMatcher(char ch) {
        return new CharMatcher(ch);
    }

    public StringMatcher charSetMatcher(char... chars) {
        if (chars != null) {
            if (chars.length != 0) {
                if (chars.length == 1) {
                    return new CharMatcher(chars[0]);
                }
                return new CharSetMatcher(chars);
            }
        }
        return NONE_MATCHER;
    }

    public StringMatcher charSetMatcher(String chars) {
        if (chars != null) {
            if (chars.length() != 0) {
                if (chars.length() == 1) {
                    return new CharMatcher(chars.charAt(0));
                }
                return new CharSetMatcher(chars.toCharArray());
            }
        }
        return NONE_MATCHER;
    }

    public StringMatcher commaMatcher() {
        return COMMA_MATCHER;
    }

    public StringMatcher doubleQuoteMatcher() {
        return DOUBLE_QUOTE_MATCHER;
    }

    public StringMatcher noneMatcher() {
        return NONE_MATCHER;
    }

    public StringMatcher quoteMatcher() {
        return QUOTE_MATCHER;
    }

    public StringMatcher singleQuoteMatcher() {
        return SINGLE_QUOTE_MATCHER;
    }

    public StringMatcher spaceMatcher() {
        return SPACE_MATCHER;
    }

    public StringMatcher splitMatcher() {
        return SPLIT_MATCHER;
    }

    public StringMatcher stringMatcher(String str) {
        if (str != null) {
            if (str.length() != 0) {
                return new StringMatcher(str);
            }
        }
        return NONE_MATCHER;
    }

    public StringMatcher tabMatcher() {
        return TAB_MATCHER;
    }

    public StringMatcher trimMatcher() {
        return TRIM_MATCHER;
    }
}
