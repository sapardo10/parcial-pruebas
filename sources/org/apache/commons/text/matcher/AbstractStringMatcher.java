package org.apache.commons.text.matcher;

import java.util.Arrays;

abstract class AbstractStringMatcher implements StringMatcher {

    static final class CharMatcher extends AbstractStringMatcher {
        private final char ch;

        CharMatcher(char ch) {
            this.ch = ch;
        }

        public int isMatch(char[] buffer, int pos, int bufferStart, int bufferEnd) {
            return this.ch == buffer[pos] ? 1 : 0;
        }
    }

    static final class CharSetMatcher extends AbstractStringMatcher {
        private final char[] chars;

        CharSetMatcher(char[] chars) {
            this.chars = (char[]) chars.clone();
            Arrays.sort(this.chars);
        }

        public int isMatch(char[] buffer, int pos, int bufferStart, int bufferEnd) {
            return Arrays.binarySearch(this.chars, buffer[pos]) >= 0 ? 1 : 0;
        }
    }

    static final class NoMatcher extends AbstractStringMatcher {
        NoMatcher() {
        }

        public int isMatch(char[] buffer, int pos, int bufferStart, int bufferEnd) {
            return 0;
        }
    }

    static final class StringMatcher extends AbstractStringMatcher {
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

    static final class TrimMatcher extends AbstractStringMatcher {
        private static final int SPACE_INT = 32;

        TrimMatcher() {
        }

        public int isMatch(char[] buffer, int pos, int bufferStart, int bufferEnd) {
            return buffer[pos] <= ' ' ? 1 : 0;
        }
    }

    protected AbstractStringMatcher() {
    }

    public int isMatch(char[] buffer, int pos) {
        return isMatch(buffer, pos, 0, buffer.length);
    }
}
