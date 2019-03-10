package org.apache.commons.text;

import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class CaseUtils {
    public static String toCamelCase(String str, boolean capitalizeFirstLetter, char... delimiters) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        str = str.toLowerCase();
        int strLen = str.length();
        int[] newCodePoints = new int[strLen];
        int outOffset = 0;
        Set<Integer> delimiterSet = generateDelimiterSet(delimiters);
        boolean capitalizeNext = false;
        if (capitalizeFirstLetter) {
            capitalizeNext = true;
        }
        int index = 0;
        while (index < strLen) {
            int codePoint = str.codePointAt(index);
            if (delimiterSet.contains(Integer.valueOf(codePoint))) {
                capitalizeNext = true;
                if (outOffset == 0) {
                    capitalizeNext = false;
                }
                index += Character.charCount(codePoint);
            } else {
                int outOffset2;
                if (!capitalizeNext) {
                    if (outOffset != 0 || !capitalizeFirstLetter) {
                        outOffset2 = outOffset + 1;
                        newCodePoints[outOffset] = codePoint;
                        index += Character.charCount(codePoint);
                        outOffset = outOffset2;
                    }
                }
                outOffset2 = Character.toTitleCase(codePoint);
                int outOffset3 = outOffset + 1;
                newCodePoints[outOffset] = outOffset2;
                index += Character.charCount(outOffset2);
                capitalizeNext = false;
                outOffset = outOffset3;
            }
        }
        if (outOffset != 0) {
            return new String(newCodePoints, 0, outOffset);
        }
        return str;
    }

    private static Set<Integer> generateDelimiterSet(char[] delimiters) {
        Set<Integer> delimiterHashSet = new HashSet();
        delimiterHashSet.add(Integer.valueOf(Character.codePointAt(new char[]{' '}, 0)));
        if (delimiters != null) {
            if (delimiters.length != 0) {
                for (int index = 0; index < delimiters.length; index++) {
                    delimiterHashSet.add(Integer.valueOf(Character.codePointAt(delimiters, index)));
                }
                return delimiterHashSet;
            }
        }
        return delimiterHashSet;
    }
}
