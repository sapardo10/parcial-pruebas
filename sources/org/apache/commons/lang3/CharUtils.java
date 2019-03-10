package org.apache.commons.lang3;

public class CharUtils {
    private static final String[] CHAR_STRING_ARRAY = new String[128];
    public static final char CR = '\r';
    private static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    public static final char LF = '\n';
    public static final char NUL = '\u0000';

    static {
        char c = '\u0000';
        while (true) {
            String[] strArr = CHAR_STRING_ARRAY;
            if (c < strArr.length) {
                strArr[c] = String.valueOf(c);
                c = (char) (c + 1);
            } else {
                return;
            }
        }
    }

    @Deprecated
    public static Character toCharacterObject(char ch) {
        return Character.valueOf(ch);
    }

    public static Character toCharacterObject(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        return Character.valueOf(str.charAt(0));
    }

    public static char toChar(Character ch) {
        Validate.isTrue(ch != null, "The Character must not be null", new Object[0]);
        return ch.charValue();
    }

    public static char toChar(Character ch, char defaultValue) {
        if (ch == null) {
            return defaultValue;
        }
        return ch.charValue();
    }

    public static char toChar(String str) {
        Validate.isTrue(StringUtils.isNotEmpty(str), "The String must not be empty", new Object[0]);
        return str.charAt(0);
    }

    public static char toChar(String str, char defaultValue) {
        if (StringUtils.isEmpty(str)) {
            return defaultValue;
        }
        return str.charAt(0);
    }

    public static int toIntValue(char ch) {
        if (isAsciiNumeric(ch)) {
            return ch - 48;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("The character ");
        stringBuilder.append(ch);
        stringBuilder.append(" is not in the range '0' - '9'");
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public static int toIntValue(char ch, int defaultValue) {
        if (isAsciiNumeric(ch)) {
            return ch - 48;
        }
        return defaultValue;
    }

    public static int toIntValue(Character ch) {
        Validate.isTrue(ch != null, "The character must not be null", new Object[0]);
        return toIntValue(ch.charValue());
    }

    public static int toIntValue(Character ch, int defaultValue) {
        if (ch == null) {
            return defaultValue;
        }
        return toIntValue(ch.charValue(), defaultValue);
    }

    public static String toString(char ch) {
        if (ch < '') {
            return CHAR_STRING_ARRAY[ch];
        }
        return new String(new char[]{ch});
    }

    public static String toString(Character ch) {
        if (ch == null) {
            return null;
        }
        return toString(ch.charValue());
    }

    public static String unicodeEscaped(char ch) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\\u");
        stringBuilder.append(HEX_DIGITS[(ch >> 12) & 15]);
        stringBuilder.append(HEX_DIGITS[(ch >> 8) & 15]);
        stringBuilder.append(HEX_DIGITS[(ch >> 4) & 15]);
        stringBuilder.append(HEX_DIGITS[ch & 15]);
        return stringBuilder.toString();
    }

    public static String unicodeEscaped(Character ch) {
        if (ch == null) {
            return null;
        }
        return unicodeEscaped(ch.charValue());
    }

    public static boolean isAscii(char ch) {
        return ch < '';
    }

    public static boolean isAsciiPrintable(char ch) {
        return ch >= ' ' && ch < '';
    }

    public static boolean isAsciiControl(char ch) {
        if (ch >= ' ') {
            if (ch != '') {
                return false;
            }
        }
        return true;
    }

    public static boolean isAsciiAlpha(char ch) {
        if (!isAsciiAlphaUpper(ch)) {
            if (!isAsciiAlphaLower(ch)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAsciiAlphaUpper(char ch) {
        return ch >= 'A' && ch <= 'Z';
    }

    public static boolean isAsciiAlphaLower(char ch) {
        return ch >= 'a' && ch <= 'z';
    }

    public static boolean isAsciiNumeric(char ch) {
        return ch >= '0' && ch <= '9';
    }

    public static boolean isAsciiAlphanumeric(char ch) {
        if (!isAsciiAlpha(ch)) {
            if (!isAsciiNumeric(ch)) {
                return false;
            }
        }
        return true;
    }

    public static int compare(char x, char y) {
        return x - y;
    }
}
