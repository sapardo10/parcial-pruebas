package org.apache.commons.text;

import java.util.Arrays;
import kotlin.text.Typography;

@Deprecated
public abstract class StrMatcher {
    private static final StrMatcher COMMA_MATCHER = new CharMatcher(',');
    private static final StrMatcher DOUBLE_QUOTE_MATCHER = new CharMatcher(Typography.quote);
    private static final StrMatcher NONE_MATCHER = new NoMatcher();
    private static final StrMatcher QUOTE_MATCHER = new CharSetMatcher("'\"".toCharArray());
    private static final StrMatcher SINGLE_QUOTE_MATCHER = new CharMatcher('\'');
    private static final StrMatcher SPACE_MATCHER = new CharMatcher(' ');
    private static final StrMatcher SPLIT_MATCHER = new CharSetMatcher(" \t\n\r\f".toCharArray());
    private static final StrMatcher TAB_MATCHER = new CharMatcher('\t');
    private static final StrMatcher TRIM_MATCHER = new TrimMatcher();

    static final class CharMatcher extends StrMatcher {
        private final char ch;

        CharMatcher(char ch) {
            this.ch = ch;
        }

        public int isMatch(char[] buffer, int pos, int bufferStart, int bufferEnd) {
            return this.ch == buffer[pos] ? 1 : 0;
        }
    }

    static final class CharSetMatcher extends StrMatcher {
        private final char[] chars;

        CharSetMatcher(char[] chars) {
            this.chars = (char[]) chars.clone();
            Arrays.sort(this.chars);
        }

        public int isMatch(char[] buffer, int pos, int bufferStart, int bufferEnd) {
            return Arrays.binarySearch(this.chars, buffer[pos]) >= 0 ? 1 : 0;
        }
    }

    static final class NoMatcher extends StrMatcher {
        NoMatcher() {
        }

        public int isMatch(char[] buffer, int pos, int bufferStart, int bufferEnd) {
            return 0;
        }
    }

    static final class StringMatcher extends StrMatcher {
        private final char[] chars;

        StringMatcher(String str) {
            this.chars = str.toCharArray();
        }

        public int isMatch(char[] buffer, int pos, int bufferStart, int bufferEnd) {
            int len = this.chars.length;
            if (pos + len > bufferEnd) {
                return 0;
            }
            int i = 0;
            while (true) {
                char[] cArr = this.chars;
                if (i >= cArr.length) {
                    return len;
                }
                if (cArr[i] != buffer[pos]) {
                    return 0;
                }
                i++;
                pos++;
            }
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(super.toString());
            stringBuilder.append(' ');
            stringBuilder.append(Arrays.toString(this.chars));
            return stringBuilder.toString();
        }
    }

    static final class TrimMatcher extends StrMatcher {
        TrimMatcher() {
        }

        public int isMatch(char[] buffer, int pos, int bufferStart, int bufferEnd) {
            return buffer[pos] <= ' ' ? 1 : 0;
        }
    }

    public abstract int isMatch(char[] cArr, int i, int i2, int i3);

    public static StrMatcher commaMatcher() {
        return COMMA_MATCHER;
    }

    public static StrMatcher tabMatcher() {
        return TAB_MATCHER;
    }

    public static StrMatcher spaceMatcher() {
        return SPACE_MATCHER;
    }

    public static StrMatcher splitMatcher() {
        return SPLIT_MATCHER;
    }

    public static StrMatcher trimMatcher() {
        return TRIM_MATCHER;
    }

    public static StrMatcher singleQuoteMatcher() {
        return SINGLE_QUOTE_MATCHER;
    }

    public static StrMatcher doubleQuoteMatcher() {
        return DOUBLE_QUOTE_MATCHER;
    }

    public static StrMatcher quoteMatcher() {
        return QUOTE_MATCHER;
    }

    public static StrMatcher noneMatcher() {
        return NONE_MATCHER;
    }

    public static StrMatcher charMatcher(char ch) {
        return new CharMatcher(ch);
    }

    public static StrMatcher charSetMatcher(char... chars) {
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

    public static StrMatcher charSetMatcher(String chars) {
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

    public static StrMatcher stringMatcher(String str) {
        if (str != null) {
            if (str.length() != 0) {
                return new StringMatcher(str);
            }
        }
        return NONE_MATCHER;
    }

    protected StrMatcher() {
    }

    public int isMatch(char[] buffer, int pos) {
        return isMatch(buffer, pos, 0, buffer.length);
    }
}
