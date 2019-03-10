package org.apache.commons.lang3;

public class CharSetUtils {
    public static String squeeze(String str, String... set) {
        if (!StringUtils.isEmpty(str)) {
            if (!deepEmpty(set)) {
                CharSet chars = CharSet.getInstance(set);
                StringBuilder buffer = new StringBuilder(str.length());
                char[] chrs = str.toCharArray();
                int sz = chrs.length;
                char lastChar = chrs['\u0000'];
                Character inChars = null;
                Character notInChars = null;
                buffer.append(lastChar);
                for (int i = 1; i < sz; i++) {
                    char ch = chrs[i];
                    if (ch == lastChar) {
                        if (inChars == null || ch != inChars.charValue()) {
                            if (notInChars != null) {
                                if (ch != notInChars.charValue()) {
                                }
                            }
                            if (chars.contains(ch)) {
                                inChars = Character.valueOf(ch);
                            } else {
                                notInChars = Character.valueOf(ch);
                            }
                        } else {
                        }
                    }
                    buffer.append(ch);
                    lastChar = ch;
                }
                return buffer.toString();
            }
        }
        return str;
    }

    public static boolean containsAny(String str, String... set) {
        if (!StringUtils.isEmpty(str)) {
            if (!deepEmpty(set)) {
                CharSet chars = CharSet.getInstance(set);
                for (char c : str.toCharArray()) {
                    if (chars.contains(c)) {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    public static int count(String str, String... set) {
        if (!StringUtils.isEmpty(str)) {
            if (!deepEmpty(set)) {
                CharSet chars = CharSet.getInstance(set);
                int count = 0;
                for (char c : str.toCharArray()) {
                    if (chars.contains(c)) {
                        count++;
                    }
                }
                return count;
            }
        }
        return 0;
    }

    public static String keep(String str, String... set) {
        if (str == null) {
            return null;
        }
        if (!str.isEmpty()) {
            if (!deepEmpty(set)) {
                return modify(str, set, true);
            }
        }
        return "";
    }

    public static String delete(String str, String... set) {
        if (!StringUtils.isEmpty(str)) {
            if (!deepEmpty(set)) {
                return modify(str, set, false);
            }
        }
        return str;
    }

    private static String modify(String str, String[] set, boolean expect) {
        CharSet chars = CharSet.getInstance(set);
        StringBuilder buffer = new StringBuilder(str.length());
        for (char chr : str.toCharArray()) {
            if (chars.contains(chr) == expect) {
                buffer.append(chr);
            }
        }
        return buffer.toString();
    }

    private static boolean deepEmpty(String[] strings) {
        if (strings != null) {
            for (String s : strings) {
                if (StringUtils.isNotEmpty(s)) {
                    return false;
                }
            }
        }
        return true;
    }
}
