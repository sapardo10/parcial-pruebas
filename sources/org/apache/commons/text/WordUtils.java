package org.apache.commons.text;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class WordUtils {
    public static String wrap(String str, int wrapLength) {
        return wrap(str, wrapLength, null, false);
    }

    public static String wrap(String str, int wrapLength, String newLineStr, boolean wrapLongWords) {
        return wrap(str, wrapLength, newLineStr, wrapLongWords, StringUtils.SPACE);
    }

    public static String wrap(String str, int wrapLength, String newLineStr, boolean wrapLongWords, String wrapOn) {
        if (str == null) {
            return null;
        }
        if (newLineStr == null) {
            newLineStr = System.lineSeparator();
        }
        if (wrapLength < 1) {
            wrapLength = 1;
        }
        if (StringUtils.isBlank(wrapOn)) {
            wrapOn = StringUtils.SPACE;
        }
        Pattern patternToWrapOn = Pattern.compile(wrapOn);
        int inputLineLength = str.length();
        int offset = 0;
        StringBuilder wrappedLine = new StringBuilder(inputLineLength + 32);
        while (offset < inputLineLength) {
            int spaceToWrapAt = -1;
            Matcher matcher = patternToWrapOn.matcher(str.substring(offset, Math.min((offset + wrapLength) + 1, inputLineLength)));
            if (matcher.find()) {
                if (matcher.start() == 0) {
                    offset += matcher.end();
                } else {
                    spaceToWrapAt = matcher.start() + offset;
                }
            }
            if (inputLineLength - offset <= wrapLength) {
                break;
            }
            while (matcher.find()) {
                spaceToWrapAt = matcher.start() + offset;
            }
            if (spaceToWrapAt >= offset) {
                wrappedLine.append(str, offset, spaceToWrapAt);
                wrappedLine.append(newLineStr);
                offset = spaceToWrapAt + 1;
            } else if (wrapLongWords) {
                wrappedLine.append(str, offset, wrapLength + offset);
                wrappedLine.append(newLineStr);
                offset += wrapLength;
            } else {
                matcher = patternToWrapOn.matcher(str.substring(offset + wrapLength));
                if (matcher.find()) {
                    spaceToWrapAt = (matcher.start() + offset) + wrapLength;
                }
                if (spaceToWrapAt >= 0) {
                    wrappedLine.append(str, offset, spaceToWrapAt);
                    wrappedLine.append(newLineStr);
                    offset = spaceToWrapAt + 1;
                } else {
                    wrappedLine.append(str, offset, str.length());
                    offset = inputLineLength;
                }
            }
        }
        wrappedLine.append(str, offset, str.length());
        return wrappedLine.toString();
    }

    public static String capitalize(String str) {
        return capitalize(str, null);
    }

    public static String capitalize(String str, char... delimiters) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        Set<Integer> delimiterSet = generateDelimiterSet(delimiters);
        int strLen = str.length();
        int[] newCodePoints = new int[strLen];
        int outOffset = 0;
        boolean capitalizeNext = true;
        int index = 0;
        while (index < strLen) {
            int codePoint = str.codePointAt(index);
            int outOffset2;
            if (delimiterSet.contains(Integer.valueOf(codePoint))) {
                capitalizeNext = true;
                outOffset2 = outOffset + 1;
                newCodePoints[outOffset] = codePoint;
                index += Character.charCount(codePoint);
                outOffset = outOffset2;
            } else if (capitalizeNext) {
                outOffset2 = Character.toTitleCase(codePoint);
                int outOffset3 = outOffset + 1;
                newCodePoints[outOffset] = outOffset2;
                index += Character.charCount(outOffset2);
                capitalizeNext = false;
                outOffset = outOffset3;
            } else {
                outOffset2 = outOffset + 1;
                newCodePoints[outOffset] = codePoint;
                index += Character.charCount(codePoint);
                outOffset = outOffset2;
            }
        }
        return new String(newCodePoints, 0, outOffset);
    }

    public static String capitalizeFully(String str) {
        return capitalizeFully(str, null);
    }

    public static String capitalizeFully(String str, char... delimiters) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        return capitalize(str.toLowerCase(), delimiters);
    }

    public static String uncapitalize(String str) {
        return uncapitalize(str, null);
    }

    public static String uncapitalize(String str, char... delimiters) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        Set<Integer> delimiterSet = generateDelimiterSet(delimiters);
        int strLen = str.length();
        int[] newCodePoints = new int[strLen];
        int outOffset = 0;
        boolean uncapitalizeNext = true;
        int index = 0;
        while (index < strLen) {
            int codePoint = str.codePointAt(index);
            int outOffset2;
            if (delimiterSet.contains(Integer.valueOf(codePoint))) {
                uncapitalizeNext = true;
                outOffset2 = outOffset + 1;
                newCodePoints[outOffset] = codePoint;
                index += Character.charCount(codePoint);
                outOffset = outOffset2;
            } else if (uncapitalizeNext) {
                outOffset2 = Character.toLowerCase(codePoint);
                int outOffset3 = outOffset + 1;
                newCodePoints[outOffset] = outOffset2;
                index += Character.charCount(outOffset2);
                uncapitalizeNext = false;
                outOffset = outOffset3;
            } else {
                outOffset2 = outOffset + 1;
                newCodePoints[outOffset] = codePoint;
                index += Character.charCount(codePoint);
                outOffset = outOffset2;
            }
        }
        return new String(newCodePoints, 0, outOffset);
    }

    public static String swapCase(String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        int strLen = str.length();
        int[] newCodePoints = new int[strLen];
        int outOffset = 0;
        boolean whitespace = true;
        int index = 0;
        while (index < strLen) {
            int newCodePoint;
            int outOffset2;
            int oldCodepoint = str.codePointAt(index);
            if (!Character.isUpperCase(oldCodepoint)) {
                if (!Character.isTitleCase(oldCodepoint)) {
                    if (!Character.isLowerCase(oldCodepoint)) {
                        whitespace = Character.isWhitespace(oldCodepoint);
                        newCodePoint = oldCodepoint;
                    } else if (whitespace) {
                        newCodePoint = Character.toTitleCase(oldCodepoint);
                        whitespace = false;
                    } else {
                        newCodePoint = Character.toUpperCase(oldCodepoint);
                    }
                    outOffset2 = outOffset + 1;
                    newCodePoints[outOffset] = newCodePoint;
                    index += Character.charCount(newCodePoint);
                    outOffset = outOffset2;
                }
            }
            newCodePoint = Character.toLowerCase(oldCodepoint);
            whitespace = false;
            outOffset2 = outOffset + 1;
            newCodePoints[outOffset] = newCodePoint;
            index += Character.charCount(newCodePoint);
            outOffset = outOffset2;
        }
        return new String(newCodePoints, 0, outOffset);
    }

    public static String initials(String str) {
        return initials(str, null);
    }

    public static String initials(String str, char... delimiters) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        if (delimiters != null && delimiters.length == 0) {
            return "";
        }
        Set<Integer> delimiterSet = generateDelimiterSet(delimiters);
        int strLen = str.length();
        int[] newCodePoints = new int[((strLen / 2) + 1)];
        int count = 0;
        boolean lastWasGap = true;
        int i = 0;
        while (i < strLen) {
            int codePoint = str.codePointAt(i);
            if (!delimiterSet.contains(Integer.valueOf(codePoint))) {
                if (delimiters != null || !Character.isWhitespace(codePoint)) {
                    if (lastWasGap) {
                        int count2 = count + 1;
                        newCodePoints[count] = codePoint;
                        lastWasGap = false;
                        count = count2;
                    }
                    i += Character.charCount(codePoint);
                }
            }
            lastWasGap = true;
            i += Character.charCount(codePoint);
        }
        return new String(newCodePoints, 0, count);
    }

    public static boolean containsAllWords(CharSequence word, CharSequence... words) {
        if (!StringUtils.isEmpty(word)) {
            if (!ArrayUtils.isEmpty((Object[]) words)) {
                for (CharSequence w : words) {
                    if (StringUtils.isBlank(w)) {
                        return false;
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(".*\\b");
                    stringBuilder.append(w);
                    stringBuilder.append("\\b.*");
                    if (!Pattern.compile(stringBuilder.toString()).matcher(word).matches()) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Deprecated
    public static boolean isDelimiter(char ch, char[] delimiters) {
        if (delimiters == null) {
            return Character.isWhitespace(ch);
        }
        for (char delimiter : delimiters) {
            if (ch == delimiter) {
                return true;
            }
        }
        return false;
    }

    @Deprecated
    public static boolean isDelimiter(int codePoint, char[] delimiters) {
        if (delimiters == null) {
            return Character.isWhitespace(codePoint);
        }
        for (int index = 0; index < delimiters.length; index++) {
            if (Character.codePointAt(delimiters, index) == codePoint) {
                return true;
            }
        }
        return false;
    }

    public static String abbreviate(String str, int lower, int upper, String appendToEnd) {
        boolean z = true;
        Validate.isTrue(upper >= -1, "upper value cannot be less than -1", new Object[0]);
        if (upper < lower) {
            if (upper != -1) {
                z = false;
            }
        }
        Validate.isTrue(z, "upper value is less than lower value", new Object[0]);
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        StringBuilder result;
        int index;
        if (lower > str.length()) {
            lower = str.length();
        }
        if (upper != -1) {
            if (upper <= str.length()) {
                result = new StringBuilder();
                index = StringUtils.indexOf((CharSequence) str, StringUtils.SPACE, lower);
                if (index == -1) {
                    result.append(str, 0, upper);
                    if (upper != str.length()) {
                        result.append(StringUtils.defaultString(appendToEnd));
                    }
                } else if (index <= upper) {
                    result.append(str, 0, upper);
                    result.append(StringUtils.defaultString(appendToEnd));
                } else {
                    result.append(str, 0, index);
                    result.append(StringUtils.defaultString(appendToEnd));
                }
                return result.toString();
            }
        }
        upper = str.length();
        result = new StringBuilder();
        index = StringUtils.indexOf((CharSequence) str, StringUtils.SPACE, lower);
        if (index == -1) {
            result.append(str, 0, upper);
            if (upper != str.length()) {
                result.append(StringUtils.defaultString(appendToEnd));
            }
        } else if (index <= upper) {
            result.append(str, 0, index);
            result.append(StringUtils.defaultString(appendToEnd));
        } else {
            result.append(str, 0, upper);
            result.append(StringUtils.defaultString(appendToEnd));
        }
        return result.toString();
    }

    private static Set<Integer> generateDelimiterSet(char[] delimiters) {
        Set<Integer> delimiterHashSet = new HashSet();
        if (delimiters != null) {
            if (delimiters.length != 0) {
                for (int index = 0; index < delimiters.length; index++) {
                    delimiterHashSet.add(Integer.valueOf(Character.codePointAt(delimiters, index)));
                }
                return delimiterHashSet;
            }
        }
        if (delimiters == null) {
            delimiterHashSet.add(Integer.valueOf(Character.codePointAt(new char[]{' '}, 0)));
        }
        return delimiterHashSet;
    }
}
