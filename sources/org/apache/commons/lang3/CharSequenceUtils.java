package org.apache.commons.lang3;

import org.apache.commons.text.RandomStringGenerator.Builder;

public class CharSequenceUtils {
    private static final int NOT_FOUND = -1;

    public static CharSequence subSequence(CharSequence cs, int start) {
        return cs == null ? null : cs.subSequence(start, cs.length());
    }

    static int indexOf(CharSequence cs, int searchChar, int start) {
        if (cs instanceof String) {
            return ((String) cs).indexOf(searchChar, start);
        }
        int sz = cs.length();
        if (start < 0) {
            start = 0;
        }
        if (searchChar < 65536) {
            for (int i = start; i < sz; i++) {
                if (cs.charAt(i) == searchChar) {
                    return i;
                }
            }
        }
        if (searchChar <= Builder.DEFAULT_MAXIMUM_CODE_POINT) {
            char[] chars = Character.toChars(searchChar);
            for (int i2 = start; i2 < sz - 1; i2++) {
                char high = cs.charAt(i2);
                char low = cs.charAt(i2 + 1);
                if (high == chars[0] && low == chars[1]) {
                    return i2;
                }
            }
        }
        return -1;
    }

    static int indexOf(CharSequence cs, CharSequence searchChar, int start) {
        return cs.toString().indexOf(searchChar.toString(), start);
    }

    static int lastIndexOf(CharSequence cs, int searchChar, int start) {
        if (cs instanceof String) {
            return ((String) cs).lastIndexOf(searchChar, start);
        }
        int sz = cs.length();
        if (start < 0) {
            return -1;
        }
        if (start >= sz) {
            start = sz - 1;
        }
        if (searchChar < 65536) {
            for (int i = start; i >= 0; i--) {
                if (cs.charAt(i) == searchChar) {
                    return i;
                }
            }
        }
        if (searchChar <= Builder.DEFAULT_MAXIMUM_CODE_POINT) {
            char[] chars = Character.toChars(searchChar);
            if (start == sz - 1) {
                return -1;
            }
            for (int i2 = start; i2 >= 0; i2--) {
                char high = cs.charAt(i2);
                char low = cs.charAt(i2 + 1);
                if (chars[0] == high && chars[1] == low) {
                    return i2;
                }
            }
        }
        return -1;
    }

    static int lastIndexOf(CharSequence cs, CharSequence searchChar, int start) {
        return cs.toString().lastIndexOf(searchChar.toString(), start);
    }

    static char[] toCharArray(CharSequence cs) {
        if (cs instanceof String) {
            return ((String) cs).toCharArray();
        }
        int sz = cs.length();
        char[] array = new char[cs.length()];
        for (int i = 0; i < sz; i++) {
            array[i] = cs.charAt(i);
        }
        return array;
    }

    static boolean regionMatches(CharSequence cs, boolean ignoreCase, int thisStart, CharSequence substring, int start, int length) {
        if ((cs instanceof String) && (substring instanceof String)) {
            return ((String) cs).regionMatches(ignoreCase, thisStart, (String) substring, start, length);
        }
        char c1 = thisStart;
        char c2 = start;
        int tmpLen = length;
        int srcLen = cs.length() - thisStart;
        int otherLen = substring.length() - start;
        if (thisStart >= 0 && start >= 0) {
            if (length >= 0) {
                if (srcLen >= length) {
                    if (otherLen >= length) {
                        while (true) {
                            int tmpLen2 = tmpLen - 1;
                            if (tmpLen <= 0) {
                                return true;
                            }
                            char index1 = c1 + 1;
                            c1 = cs.charAt(c1);
                            char index2 = c2 + 1;
                            c2 = substring.charAt(c2);
                            if (c1 != c2) {
                                if (!ignoreCase) {
                                    return false;
                                }
                                if (Character.toUpperCase(c1) != Character.toUpperCase(c2) && Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
                                    return false;
                                }
                            }
                            c1 = index1;
                            tmpLen = tmpLen2;
                            c2 = index2;
                        }
                    }
                }
                return false;
            }
        }
        return false;
    }
}
