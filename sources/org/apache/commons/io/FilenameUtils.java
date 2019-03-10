package org.apache.commons.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

public class FilenameUtils {
    public static final char EXTENSION_SEPARATOR = '.';
    public static final String EXTENSION_SEPARATOR_STR = Character.toString('.');
    private static final int NOT_FOUND = -1;
    private static final char OTHER_SEPARATOR;
    private static final char SYSTEM_SEPARATOR = File.separatorChar;
    private static final char UNIX_SEPARATOR = '/';
    private static final char WINDOWS_SEPARATOR = '\\';

    static {
        if (isSystemWindows()) {
            OTHER_SEPARATOR = '/';
        } else {
            OTHER_SEPARATOR = '\\';
        }
    }

    static boolean isSystemWindows() {
        return SYSTEM_SEPARATOR == '\\';
    }

    private static boolean isSeparator(char ch) {
        if (ch != '/') {
            if (ch != '\\') {
                return false;
            }
        }
        return true;
    }

    public static String normalize(String filename) {
        return doNormalize(filename, SYSTEM_SEPARATOR, true);
    }

    public static String normalize(String filename, boolean unixSeparator) {
        return doNormalize(filename, unixSeparator ? '/' : '\\', true);
    }

    public static String normalizeNoEndSeparator(String filename) {
        return doNormalize(filename, SYSTEM_SEPARATOR, false);
    }

    public static String normalizeNoEndSeparator(String filename, boolean unixSeparator) {
        return doNormalize(filename, unixSeparator ? '/' : '\\', false);
    }

    private static String doNormalize(String filename, char separator, boolean keepSeparator) {
        if (filename == null) {
            return null;
        }
        failIfNullBytePresent(filename);
        int length = filename.length();
        if (length == 0) {
            return filename;
        }
        int prefix = getPrefixLength(filename);
        if (prefix < 0) {
            return null;
        }
        int size;
        char[] array = new char[(length + 2)];
        filename.getChars(0, filename.length(), array, 0);
        char otherSeparator = SYSTEM_SEPARATOR;
        if (separator == otherSeparator) {
            otherSeparator = OTHER_SEPARATOR;
        }
        for (int i = 0; i < array.length; i++) {
            if (array[i] == otherSeparator) {
                array[i] = separator;
            }
        }
        boolean lastIsDirectory = true;
        if (array[length - 1] != separator) {
            size = length + 1;
            array[length] = separator;
            lastIsDirectory = false;
            length = size;
        }
        size = prefix + 1;
        while (size < length) {
            if (array[size] == separator && array[size - 1] == separator) {
                System.arraycopy(array, size, array, size - 1, length - size);
                length--;
                size--;
            }
            size++;
        }
        size = prefix + 1;
        while (size < length) {
            if (array[size] == separator && array[size - 1] == '.' && (size == prefix + 1 || array[size - 2] == separator)) {
                if (size == length - 1) {
                    lastIsDirectory = true;
                }
                System.arraycopy(array, size + 1, array, size - 1, length - size);
                length -= 2;
                size--;
            }
            size++;
        }
        size = prefix + 2;
        while (size < length) {
            if (array[size] == separator && array[size - 1] == '.' && array[size - 2] == '.' && (size == prefix + 2 || array[size - 3] == separator)) {
                if (size == prefix + 2) {
                    return null;
                }
                if (size == length - 1) {
                    lastIsDirectory = true;
                }
                for (int j = size - 4; j >= prefix; j--) {
                    if (array[j] == separator) {
                        System.arraycopy(array, size + 1, array, j + 1, length - size);
                        length -= size - j;
                        size = j + 1;
                        break;
                    }
                }
                System.arraycopy(array, size + 1, array, prefix, length - size);
                length -= (size + 1) - prefix;
                size = prefix + 1;
            }
            size++;
        }
        if (length <= 0) {
            return "";
        }
        if (length <= prefix) {
            return new String(array, 0, length);
        }
        if (lastIsDirectory && keepSeparator) {
            return new String(array, 0, length);
        }
        return new String(array, 0, length - 1);
    }

    public static String concat(String basePath, String fullFilenameToAdd) {
        int prefix = getPrefixLength(fullFilenameToAdd);
        if (prefix < 0) {
            return null;
        }
        if (prefix > 0) {
            return normalize(fullFilenameToAdd);
        }
        if (basePath == null) {
            return null;
        }
        int len = basePath.length();
        if (len == 0) {
            return normalize(fullFilenameToAdd);
        }
        if (isSeparator(basePath.charAt(len - 1))) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(basePath);
            stringBuilder.append(fullFilenameToAdd);
            return normalize(stringBuilder.toString());
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append(basePath);
        stringBuilder.append('/');
        stringBuilder.append(fullFilenameToAdd);
        return normalize(stringBuilder.toString());
    }

    public static boolean directoryContains(String canonicalParent, String canonicalChild) throws IOException {
        if (canonicalParent == null) {
            throw new IllegalArgumentException("Directory must not be null");
        } else if (canonicalChild == null || IOCase.SYSTEM.checkEquals(canonicalParent, canonicalChild)) {
            return false;
        } else {
            return IOCase.SYSTEM.checkStartsWith(canonicalChild, canonicalParent);
        }
    }

    public static String separatorsToUnix(String path) {
        if (path != null) {
            if (path.indexOf(92) != -1) {
                return path.replace('\\', '/');
            }
        }
        return path;
    }

    public static String separatorsToWindows(String path) {
        if (path != null) {
            if (path.indexOf(47) != -1) {
                return path.replace('/', '\\');
            }
        }
        return path;
    }

    public static String separatorsToSystem(String path) {
        if (path == null) {
            return null;
        }
        if (isSystemWindows()) {
            return separatorsToWindows(path);
        }
        return separatorsToUnix(path);
    }

    public static int getPrefixLength(String filename) {
        if (filename == null) {
            return -1;
        }
        int len = filename.length();
        if (len == 0) {
            return 0;
        }
        char ch0 = filename.charAt(0);
        if (ch0 == ':') {
            return -1;
        }
        if (len == 1) {
            if (ch0 == '~') {
                return 2;
            }
            return isSeparator(ch0);
        } else if (ch0 == '~') {
            posUnix = filename.indexOf(47, 1);
            int posWin = filename.indexOf(92, 1);
            if (posUnix == -1 && posWin == -1) {
                return len + 1;
            }
            posUnix = posUnix == -1 ? posWin : posUnix;
            return Math.min(posUnix, posWin == -1 ? posUnix : posWin) + 1;
        } else {
            char ch1 = filename.charAt(1);
            if (ch1 == ':') {
                ch0 = Character.toUpperCase(ch0);
                if (ch0 < 'A' || ch0 > 'Z') {
                    return -1;
                }
                if (len != 2) {
                    if (isSeparator(filename.charAt(2))) {
                        return 3;
                    }
                }
                return 2;
            } else if (!isSeparator(ch0) || !isSeparator(ch1)) {
                return isSeparator(ch0);
            } else {
                posUnix = filename.indexOf(47, 2);
                int posWin2 = filename.indexOf(92, 2);
                if (!((posUnix == -1 && posWin2 == -1) || posUnix == 2)) {
                    if (posWin2 != 2) {
                        posUnix = posUnix == -1 ? posWin2 : posUnix;
                        return Math.min(posUnix, posWin2 == -1 ? posUnix : posWin2) + 1;
                    }
                }
                return -1;
            }
        }
    }

    public static int indexOfLastSeparator(String filename) {
        if (filename == null) {
            return -1;
        }
        return Math.max(filename.lastIndexOf(47), filename.lastIndexOf(92));
    }

    public static int indexOfExtension(String filename) {
        int i = -1;
        if (filename == null) {
            return -1;
        }
        int extensionPos = filename.lastIndexOf(46);
        if (indexOfLastSeparator(filename) <= extensionPos) {
            i = extensionPos;
        }
        return i;
    }

    public static String getPrefix(String filename) {
        if (filename == null) {
            return null;
        }
        int len = getPrefixLength(filename);
        if (len < 0) {
            return null;
        }
        if (len > filename.length()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(filename);
            stringBuilder.append('/');
            failIfNullBytePresent(stringBuilder.toString());
            stringBuilder = new StringBuilder();
            stringBuilder.append(filename);
            stringBuilder.append('/');
            return stringBuilder.toString();
        }
        String path = filename.substring(null, len);
        failIfNullBytePresent(path);
        return path;
    }

    public static String getPath(String filename) {
        return doGetPath(filename, 1);
    }

    public static String getPathNoEndSeparator(String filename) {
        return doGetPath(filename, 0);
    }

    private static String doGetPath(String filename, int separatorAdd) {
        if (filename == null) {
            return null;
        }
        int prefix = getPrefixLength(filename);
        if (prefix < 0) {
            return null;
        }
        int index = indexOfLastSeparator(filename);
        int endIndex = index + separatorAdd;
        if (prefix < filename.length() && index >= 0) {
            if (prefix < endIndex) {
                String path = filename.substring(prefix, endIndex);
                failIfNullBytePresent(path);
                return path;
            }
        }
        return "";
    }

    public static String getFullPath(String filename) {
        return doGetFullPath(filename, true);
    }

    public static String getFullPathNoEndSeparator(String filename) {
        return doGetFullPath(filename, false);
    }

    private static String doGetFullPath(String filename, boolean includeSeparator) {
        if (filename == null) {
            return null;
        }
        int prefix = getPrefixLength(filename);
        if (prefix < 0) {
            return null;
        }
        if (prefix < filename.length()) {
            int index = indexOfLastSeparator(filename);
            if (index < 0) {
                return filename.substring(0, prefix);
            }
            int end = index + includeSeparator;
            if (end == 0) {
                end++;
            }
            return filename.substring(0, end);
        } else if (includeSeparator) {
            return getPrefix(filename);
        } else {
            return filename;
        }
    }

    public static String getName(String filename) {
        if (filename == null) {
            return null;
        }
        failIfNullBytePresent(filename);
        return filename.substring(indexOfLastSeparator(filename) + 1);
    }

    private static void failIfNullBytePresent(String path) {
        int len = path.length();
        int i = 0;
        while (i < len) {
            if (path.charAt(i) != '\u0000') {
                i++;
            } else {
                throw new IllegalArgumentException("Null byte present in file/path name. There are no known legitimate use cases for such data, but several injection attacks may use it");
            }
        }
    }

    public static String getBaseName(String filename) {
        return removeExtension(getName(filename));
    }

    public static String getExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int index = indexOfExtension(filename);
        if (index == -1) {
            return "";
        }
        return filename.substring(index + 1);
    }

    public static String removeExtension(String filename) {
        if (filename == null) {
            return null;
        }
        failIfNullBytePresent(filename);
        int index = indexOfExtension(filename);
        if (index == -1) {
            return filename;
        }
        return filename.substring(0, index);
    }

    public static boolean equals(String filename1, String filename2) {
        return equals(filename1, filename2, false, IOCase.SENSITIVE);
    }

    public static boolean equalsOnSystem(String filename1, String filename2) {
        return equals(filename1, filename2, false, IOCase.SYSTEM);
    }

    public static boolean equalsNormalized(String filename1, String filename2) {
        return equals(filename1, filename2, true, IOCase.SENSITIVE);
    }

    public static boolean equalsNormalizedOnSystem(String filename1, String filename2) {
        return equals(filename1, filename2, true, IOCase.SYSTEM);
    }

    public static boolean equals(String filename1, String filename2, boolean normalized, IOCase caseSensitivity) {
        if (filename1 != null) {
            if (filename2 != null) {
                if (normalized) {
                    filename1 = normalize(filename1);
                    filename2 = normalize(filename2);
                    if (filename1 == null || filename2 == null) {
                        throw new NullPointerException("Error normalizing one or both of the file names");
                    }
                }
                if (caseSensitivity == null) {
                    caseSensitivity = IOCase.SENSITIVE;
                }
                return caseSensitivity.checkEquals(filename1, filename2);
            }
        }
        boolean z = filename1 == null && filename2 == null;
        return z;
    }

    public static boolean isExtension(String filename, String extension) {
        boolean z = false;
        if (filename == null) {
            return false;
        }
        failIfNullBytePresent(filename);
        if (extension != null) {
            if (!extension.isEmpty()) {
                return getExtension(filename).equals(extension);
            }
        }
        if (indexOfExtension(filename) == -1) {
            z = true;
        }
        return z;
    }

    public static boolean isExtension(String filename, String[] extensions) {
        boolean z = false;
        if (filename == null) {
            return false;
        }
        failIfNullBytePresent(filename);
        if (extensions != null) {
            if (extensions.length != 0) {
                String fileExt = getExtension(filename);
                for (String extension : extensions) {
                    if (fileExt.equals(extension)) {
                        return true;
                    }
                }
                return false;
            }
        }
        if (indexOfExtension(filename) == -1) {
            z = true;
        }
        return z;
    }

    public static boolean isExtension(String filename, Collection<String> extensions) {
        boolean z = false;
        if (filename == null) {
            return false;
        }
        failIfNullBytePresent(filename);
        if (extensions != null) {
            if (!extensions.isEmpty()) {
                String fileExt = getExtension(filename);
                for (String extension : extensions) {
                    if (fileExt.equals(extension)) {
                        return true;
                    }
                }
                return false;
            }
        }
        if (indexOfExtension(filename) == -1) {
            z = true;
        }
        return z;
    }

    public static boolean wildcardMatch(String filename, String wildcardMatcher) {
        return wildcardMatch(filename, wildcardMatcher, IOCase.SENSITIVE);
    }

    public static boolean wildcardMatchOnSystem(String filename, String wildcardMatcher) {
        return wildcardMatch(filename, wildcardMatcher, IOCase.SYSTEM);
    }

    public static boolean wildcardMatch(String filename, String wildcardMatcher, IOCase caseSensitivity) {
        if (filename == null && wildcardMatcher == null) {
            return true;
        }
        if (filename != null) {
            if (wildcardMatcher != null) {
                IOCase caseSensitivity2;
                if (caseSensitivity == null) {
                    caseSensitivity2 = IOCase.SENSITIVE;
                } else {
                    caseSensitivity2 = caseSensitivity;
                }
                String[] wcs = splitOnTokens(wildcardMatcher);
                boolean anyChars = false;
                int textIdx = 0;
                int wcsIdx = 0;
                Stack<int[]> backtrack = new Stack();
                while (true) {
                    if (backtrack.size() > 0) {
                        int[] array = (int[]) backtrack.pop();
                        wcsIdx = array[0];
                        textIdx = array[1];
                        anyChars = true;
                    }
                    while (wcsIdx < wcs.length) {
                        if (wcs[wcsIdx].equals("?")) {
                            textIdx++;
                            if (textIdx > filename.length()) {
                                break;
                            }
                            anyChars = false;
                        } else if (wcs[wcsIdx].equals("*")) {
                            anyChars = true;
                            if (wcsIdx == wcs.length - 1) {
                                textIdx = filename.length();
                            }
                        } else {
                            if (anyChars) {
                                textIdx = caseSensitivity2.checkIndexOf(filename, textIdx, wcs[wcsIdx]);
                                if (textIdx == -1) {
                                    break;
                                }
                                if (caseSensitivity2.checkIndexOf(filename, textIdx + 1, wcs[wcsIdx]) >= 0) {
                                    backtrack.push(new int[]{wcsIdx, caseSensitivity2.checkIndexOf(filename, textIdx + 1, wcs[wcsIdx])});
                                }
                            } else if (!caseSensitivity2.checkRegionMatches(filename, textIdx, wcs[wcsIdx])) {
                                break;
                            }
                            textIdx += wcs[wcsIdx].length();
                            anyChars = false;
                        }
                        wcsIdx++;
                    }
                    if (wcsIdx == wcs.length && textIdx == filename.length()) {
                        return true;
                    }
                    if (backtrack.size() <= 0) {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    static String[] splitOnTokens(String text) {
        if (text.indexOf(63) == -1 && text.indexOf(42) == -1) {
            return new String[]{text};
        }
        char[] array = text.toCharArray();
        ArrayList<String> list = new ArrayList();
        StringBuilder buffer = new StringBuilder();
        char prevChar = '\u0000';
        for (char ch : array) {
            if (ch != '?') {
                if (ch != '*') {
                    buffer.append(ch);
                    prevChar = ch;
                }
            }
            if (buffer.length() != 0) {
                list.add(buffer.toString());
                buffer.setLength(0);
            }
            if (ch == '?') {
                list.add("?");
            } else if (prevChar != '*') {
                list.add("*");
            }
            prevChar = ch;
        }
        if (buffer.length() != 0) {
            list.add(buffer.toString());
        }
        return (String[]) list.toArray(new String[list.size()]);
    }
}
