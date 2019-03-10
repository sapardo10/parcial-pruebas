package org.apache.commons.lang3;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;
import kotlin.text.Typography;

public class StringUtils {
    public static final String CR = "\r";
    public static final String EMPTY = "";
    public static final int INDEX_NOT_FOUND = -1;
    public static final String LF = "\n";
    private static final int PAD_LIMIT = 8192;
    public static final String SPACE = " ";

    @java.lang.Deprecated
    public static int getFuzzyDistance(java.lang.CharSequence r10, java.lang.CharSequence r11, java.util.Locale r12) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:25:0x005a in {14, 15, 16, 17, 18, 19, 20, 22, 24} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        if (r10 == 0) goto L_0x0051;
    L_0x0002:
        if (r11 == 0) goto L_0x0051;
    L_0x0004:
        if (r12 == 0) goto L_0x0049;
    L_0x0006:
        r0 = r10.toString();
        r0 = r0.toLowerCase(r12);
        r1 = r11.toString();
        r1 = r1.toLowerCase(r12);
        r2 = 0;
        r3 = 0;
        r4 = -2147483648; // 0xffffffff80000000 float:-0.0 double:NaN;
        r5 = 0;
    L_0x001b:
        r6 = r1.length();
        if (r5 >= r6) goto L_0x0048;
    L_0x0021:
        r6 = r1.charAt(r5);
        r7 = 0;
    L_0x0026:
        r8 = r0.length();
        if (r3 >= r8) goto L_0x0045;
    L_0x002c:
        if (r7 != 0) goto L_0x0045;
    L_0x002e:
        r8 = r0.charAt(r3);
        if (r6 != r8) goto L_0x0041;
    L_0x0034:
        r2 = r2 + 1;
        r9 = r4 + 1;
        if (r9 != r3) goto L_0x003d;
    L_0x003a:
        r2 = r2 + 2;
        goto L_0x003e;
    L_0x003e:
        r4 = r3;
        r7 = 1;
        goto L_0x0042;
    L_0x0042:
        r3 = r3 + 1;
        goto L_0x0026;
    L_0x0045:
        r5 = r5 + 1;
        goto L_0x001b;
    L_0x0048:
        return r2;
    L_0x0049:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "Locale must not be null";
        r0.<init>(r1);
        throw r0;
        r0 = new java.lang.IllegalArgumentException;
        r1 = "Strings must not be null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.StringUtils.getFuzzyDistance(java.lang.CharSequence, java.lang.CharSequence, java.util.Locale):int");
    }

    @java.lang.Deprecated
    public static int getLevenshteinDistance(java.lang.CharSequence r13, java.lang.CharSequence r14) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:27:0x0070 in {4, 6, 8, 9, 12, 19, 20, 21, 22, 24, 26} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        if (r13 == 0) goto L_0x0067;
    L_0x0002:
        if (r14 == 0) goto L_0x0067;
    L_0x0004:
        r0 = r13.length();
        r1 = r14.length();
        if (r0 != 0) goto L_0x000f;
    L_0x000e:
        return r1;
    L_0x000f:
        if (r1 != 0) goto L_0x0012;
    L_0x0011:
        return r0;
    L_0x0012:
        if (r0 <= r1) goto L_0x001d;
    L_0x0014:
        r2 = r13;
        r13 = r14;
        r14 = r2;
        r0 = r1;
        r1 = r14.length();
        goto L_0x001e;
    L_0x001e:
        r2 = r0 + 1;
        r2 = new int[r2];
        r3 = 0;
    L_0x0023:
        if (r3 > r0) goto L_0x002a;
    L_0x0025:
        r2[r3] = r3;
        r3 = r3 + 1;
        goto L_0x0023;
    L_0x002a:
        r4 = 1;
    L_0x002b:
        if (r4 > r1) goto L_0x0064;
    L_0x002d:
        r5 = 0;
        r6 = r2[r5];
        r7 = r4 + -1;
        r7 = r14.charAt(r7);
        r2[r5] = r4;
        r3 = 1;
    L_0x0039:
        if (r3 > r0) goto L_0x0061;
    L_0x003b:
        r8 = r2[r3];
        r9 = r3 + -1;
        r9 = r13.charAt(r9);
        r10 = 1;
        if (r9 != r7) goto L_0x0048;
    L_0x0046:
        r9 = 0;
        goto L_0x0049;
    L_0x0048:
        r9 = 1;
    L_0x0049:
        r11 = r3 + -1;
        r11 = r2[r11];
        r11 = r11 + r10;
        r12 = r2[r3];
        r12 = r12 + r10;
        r10 = java.lang.Math.min(r11, r12);
        r11 = r6 + r9;
        r10 = java.lang.Math.min(r10, r11);
        r2[r3] = r10;
        r6 = r8;
        r3 = r3 + 1;
        goto L_0x0039;
    L_0x0061:
        r4 = r4 + 1;
        goto L_0x002b;
    L_0x0064:
        r5 = r2[r0];
        return r5;
        r0 = new java.lang.IllegalArgumentException;
        r1 = "Strings must not be null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.StringUtils.getLevenshteinDistance(java.lang.CharSequence, java.lang.CharSequence):int");
    }

    @java.lang.Deprecated
    public static int getLevenshteinDistance(java.lang.CharSequence r16, java.lang.CharSequence r17, int r18) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:51:0x00d4 in {7, 8, 11, 12, 15, 17, 18, 21, 26, 27, 29, 31, 32, 37, 38, 39, 40, 44, 46, 48, 50} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r0 = r18;
        if (r16 == 0) goto L_0x00cb;
    L_0x0004:
        if (r17 == 0) goto L_0x00cb;
    L_0x0006:
        if (r0 < 0) goto L_0x00c3;
    L_0x0008:
        r1 = r16.length();
        r2 = r17.length();
        r3 = -1;
        if (r1 != 0) goto L_0x0018;
    L_0x0013:
        if (r2 > r0) goto L_0x0017;
    L_0x0015:
        r3 = r2;
    L_0x0017:
        return r3;
    L_0x0018:
        if (r2 != 0) goto L_0x001f;
    L_0x001a:
        if (r1 > r0) goto L_0x001e;
    L_0x001c:
        r3 = r1;
    L_0x001e:
        return r3;
    L_0x001f:
        r4 = r1 - r2;
        r4 = java.lang.Math.abs(r4);
        if (r4 <= r0) goto L_0x0028;
    L_0x0027:
        return r3;
    L_0x0028:
        if (r1 <= r2) goto L_0x0035;
    L_0x002a:
        r4 = r16;
        r5 = r17;
        r6 = r4;
        r1 = r2;
        r2 = r6.length();
        goto L_0x0039;
    L_0x0035:
        r5 = r16;
        r6 = r17;
    L_0x0039:
        r4 = r1 + 1;
        r4 = new int[r4];
        r7 = r1 + 1;
        r7 = new int[r7];
        r8 = java.lang.Math.min(r1, r0);
        r9 = 1;
        r8 = r8 + r9;
        r10 = 0;
    L_0x0048:
        if (r10 >= r8) goto L_0x004f;
    L_0x004a:
        r4[r10] = r10;
        r10 = r10 + 1;
        goto L_0x0048;
    L_0x004f:
        r10 = r4.length;
        r11 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        java.util.Arrays.fill(r4, r8, r10, r11);
        java.util.Arrays.fill(r7, r11);
        r10 = 1;
    L_0x005a:
        if (r10 > r2) goto L_0x00ba;
    L_0x005c:
        r12 = r10 + -1;
        r12 = r6.charAt(r12);
        r13 = 0;
        r7[r13] = r10;
        r13 = r10 - r0;
        r13 = java.lang.Math.max(r9, r13);
        r14 = r11 - r0;
        if (r10 <= r14) goto L_0x0071;
    L_0x006f:
        r14 = r1;
        goto L_0x0077;
    L_0x0071:
        r14 = r10 + r0;
        r14 = java.lang.Math.min(r1, r14);
    L_0x0077:
        if (r13 <= r14) goto L_0x007a;
    L_0x0079:
        return r3;
    L_0x007a:
        if (r13 <= r9) goto L_0x0081;
    L_0x007c:
        r15 = r13 + -1;
        r7[r15] = r11;
        goto L_0x0082;
    L_0x0082:
        r15 = r13;
    L_0x0083:
        if (r15 > r14) goto L_0x00b0;
    L_0x0085:
        r11 = r15 + -1;
        r11 = r5.charAt(r11);
        if (r11 != r12) goto L_0x0094;
    L_0x008d:
        r11 = r15 + -1;
        r11 = r4[r11];
        r7[r15] = r11;
        goto L_0x00a9;
    L_0x0094:
        r11 = r15 + -1;
        r11 = r7[r11];
        r3 = r4[r15];
        r3 = java.lang.Math.min(r11, r3);
        r11 = r15 + -1;
        r11 = r4[r11];
        r3 = java.lang.Math.min(r3, r11);
        r3 = r3 + r9;
        r7[r15] = r3;
    L_0x00a9:
        r15 = r15 + 1;
        r3 = -1;
        r11 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        goto L_0x0083;
    L_0x00b0:
        r3 = r4;
        r4 = r7;
        r7 = r3;
        r10 = r10 + 1;
        r3 = -1;
        r11 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        goto L_0x005a;
    L_0x00ba:
        r3 = r4[r1];
        if (r3 > r0) goto L_0x00c1;
    L_0x00be:
        r3 = r4[r1];
        return r3;
    L_0x00c1:
        r3 = -1;
        return r3;
    L_0x00c3:
        r1 = new java.lang.IllegalArgumentException;
        r2 = "Threshold must not be negative";
        r1.<init>(r2);
        throw r1;
        r1 = new java.lang.IllegalArgumentException;
        r2 = "Strings must not be null";
        r1.<init>(r2);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.StringUtils.getLevenshteinDistance(java.lang.CharSequence, java.lang.CharSequence, int):int");
    }

    public static java.lang.String joinWith(java.lang.String r5, java.lang.Object... r6) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:13:0x0041 in {6, 7, 8, 10, 12} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        if (r6 == 0) goto L_0x0039;
    L_0x0002:
        r0 = "";
        r0 = defaultString(r5, r0);
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = java.util.Arrays.asList(r6);
        r2 = r2.iterator();
    L_0x0015:
        r3 = r2.hasNext();
        if (r3 == 0) goto L_0x0034;
    L_0x001b:
        r3 = r2.next();
        r4 = "";
        r3 = java.util.Objects.toString(r3, r4);
        r1.append(r3);
        r4 = r2.hasNext();
        if (r4 == 0) goto L_0x0032;
    L_0x002e:
        r1.append(r0);
        goto L_0x0033;
    L_0x0033:
        goto L_0x0015;
    L_0x0034:
        r3 = r1.toString();
        return r3;
    L_0x0039:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "Object varargs must not be null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.StringUtils.joinWith(java.lang.String, java.lang.Object[]):java.lang.String");
    }

    public static boolean isEmpty(CharSequence cs) {
        if (cs != null) {
            if (cs.length() != 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotEmpty(CharSequence cs) {
        return isEmpty(cs) ^ 1;
    }

    public static boolean isAnyEmpty(CharSequence... css) {
        if (ArrayUtils.isEmpty((Object[]) css)) {
            return false;
        }
        for (CharSequence cs : css) {
            if (isEmpty(cs)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNoneEmpty(CharSequence... css) {
        return isAnyEmpty(css) ^ 1;
    }

    public static boolean isAllEmpty(CharSequence... css) {
        if (ArrayUtils.isEmpty((Object[]) css)) {
            return true;
        }
        for (CharSequence cs : css) {
            if (isNotEmpty(cs)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isBlank(CharSequence cs) {
        if (cs != null) {
            int length = cs.length();
            int strLen = length;
            if (length != 0) {
                for (length = 0; length < strLen; length++) {
                    if (!Character.isWhitespace(cs.charAt(length))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return true;
    }

    public static boolean isNotBlank(CharSequence cs) {
        return isBlank(cs) ^ 1;
    }

    public static boolean isAnyBlank(CharSequence... css) {
        if (ArrayUtils.isEmpty((Object[]) css)) {
            return false;
        }
        for (CharSequence cs : css) {
            if (isBlank(cs)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNoneBlank(CharSequence... css) {
        return isAnyBlank(css) ^ 1;
    }

    public static boolean isAllBlank(CharSequence... css) {
        if (ArrayUtils.isEmpty((Object[]) css)) {
            return true;
        }
        for (CharSequence cs : css) {
            if (isNotBlank(cs)) {
                return false;
            }
        }
        return true;
    }

    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    public static String trimToNull(String str) {
        String ts = trim(str);
        return isEmpty(ts) ? null : ts;
    }

    public static String trimToEmpty(String str) {
        return str == null ? "" : str.trim();
    }

    public static String truncate(String str, int maxWidth) {
        return truncate(str, 0, maxWidth);
    }

    public static String truncate(String str, int offset, int maxWidth) {
        if (offset < 0) {
            throw new IllegalArgumentException("offset cannot be negative");
        } else if (maxWidth < 0) {
            throw new IllegalArgumentException("maxWith cannot be negative");
        } else if (str == null) {
            return null;
        } else {
            if (offset > str.length()) {
                return "";
            }
            if (str.length() <= maxWidth) {
                return str.substring(offset);
            }
            return str.substring(offset, offset + maxWidth > str.length() ? str.length() : offset + maxWidth);
        }
    }

    public static String strip(String str) {
        return strip(str, null);
    }

    public static String stripToNull(String str) {
        String str2 = null;
        if (str == null) {
            return null;
        }
        str = strip(str, null);
        if (!str.isEmpty()) {
            str2 = str;
        }
        return str2;
    }

    public static String stripToEmpty(String str) {
        return str == null ? "" : strip(str, null);
    }

    public static String strip(String str, String stripChars) {
        if (isEmpty(str)) {
            return str;
        }
        return stripEnd(stripStart(str, stripChars), stripChars);
    }

    public static String stripStart(String str, String stripChars) {
        if (str != null) {
            int length = str.length();
            int strLen = length;
            if (length != 0) {
                length = 0;
                if (stripChars == null) {
                    while (length != strLen && Character.isWhitespace(str.charAt(length))) {
                        length++;
                    }
                } else if (stripChars.isEmpty()) {
                    return str;
                } else {
                    while (length != strLen && stripChars.indexOf(str.charAt(length)) != -1) {
                        length++;
                    }
                }
                return str.substring(length);
            }
        }
        return str;
    }

    public static String stripEnd(String str, String stripChars) {
        if (str != null) {
            int length = str.length();
            int end = length;
            if (length != 0) {
                if (stripChars == null) {
                    while (end != 0 && Character.isWhitespace(str.charAt(end - 1))) {
                        end--;
                    }
                } else if (stripChars.isEmpty()) {
                    return str;
                } else {
                    while (end != 0 && stripChars.indexOf(str.charAt(end - 1)) != -1) {
                        end--;
                    }
                }
                return str.substring(0, end);
            }
        }
        return str;
    }

    public static String[] stripAll(String... strs) {
        return stripAll(strs, null);
    }

    public static String[] stripAll(String[] strs, String stripChars) {
        if (strs != null) {
            int length = strs.length;
            int strsLen = length;
            if (length != 0) {
                String[] newArr = new String[strsLen];
                for (int i = 0; i < strsLen; i++) {
                    newArr[i] = strip(strs[i], stripChars);
                }
                return newArr;
            }
        }
        return strs;
    }

    public static String stripAccents(String input) {
        if (input == null) {
            return null;
        }
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        StringBuilder decomposed = new StringBuilder(Normalizer.normalize(input, Form.NFD));
        convertRemainingAccentCharacters(decomposed);
        return pattern.matcher(decomposed).replaceAll("");
    }

    private static void convertRemainingAccentCharacters(StringBuilder decomposed) {
        for (int i = 0; i < decomposed.length(); i++) {
            if (decomposed.charAt(i) == 'Ł') {
                decomposed.deleteCharAt(i);
                decomposed.insert(i, 'L');
            } else if (decomposed.charAt(i) == 'ł') {
                decomposed.deleteCharAt(i);
                decomposed.insert(i, 'l');
            }
        }
    }

    public static boolean equals(CharSequence cs1, CharSequence cs2) {
        if (cs1 == cs2) {
            return true;
        }
        if (cs1 != null) {
            if (cs2 != null) {
                if (cs1.length() != cs2.length()) {
                    return false;
                }
                if ((cs1 instanceof String) && (cs2 instanceof String)) {
                    return cs1.equals(cs2);
                }
                return CharSequenceUtils.regionMatches(cs1, false, 0, cs2, 0, cs1.length());
            }
        }
        return false;
    }

    public static boolean equalsIgnoreCase(CharSequence str1, CharSequence str2) {
        boolean z = true;
        if (str1 != null) {
            if (str2 != null) {
                if (str1 == str2) {
                    return true;
                }
                if (str1.length() != str2.length()) {
                    return false;
                }
                return CharSequenceUtils.regionMatches(str1, true, 0, str2, 0, str1.length());
            }
        }
        if (str1 != str2) {
            z = false;
        }
        return z;
    }

    public static int compare(String str1, String str2) {
        return compare(str1, str2, true);
    }

    public static int compare(String str1, String str2, boolean nullIsLess) {
        if (str1 == str2) {
            return 0;
        }
        int i = -1;
        if (str1 == null) {
            if (!nullIsLess) {
                i = 1;
            }
            return i;
        } else if (str2 != null) {
            return str1.compareTo(str2);
        } else {
            if (nullIsLess) {
                i = 1;
            }
            return i;
        }
    }

    public static int compareIgnoreCase(String str1, String str2) {
        return compareIgnoreCase(str1, str2, true);
    }

    public static int compareIgnoreCase(String str1, String str2, boolean nullIsLess) {
        if (str1 == str2) {
            return 0;
        }
        int i = -1;
        if (str1 == null) {
            if (!nullIsLess) {
                i = 1;
            }
            return i;
        } else if (str2 != null) {
            return str1.compareToIgnoreCase(str2);
        } else {
            if (nullIsLess) {
                i = 1;
            }
            return i;
        }
    }

    public static boolean equalsAny(CharSequence string, CharSequence... searchStrings) {
        if (ArrayUtils.isNotEmpty((Object[]) searchStrings)) {
            for (CharSequence next : searchStrings) {
                if (equals(string, next)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean equalsAnyIgnoreCase(CharSequence string, CharSequence... searchStrings) {
        if (ArrayUtils.isNotEmpty((Object[]) searchStrings)) {
            for (CharSequence next : searchStrings) {
                if (equalsIgnoreCase(string, next)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int indexOf(CharSequence seq, int searchChar) {
        if (isEmpty(seq)) {
            return -1;
        }
        return CharSequenceUtils.indexOf(seq, searchChar, 0);
    }

    public static int indexOf(CharSequence seq, int searchChar, int startPos) {
        if (isEmpty(seq)) {
            return -1;
        }
        return CharSequenceUtils.indexOf(seq, searchChar, startPos);
    }

    public static int indexOf(CharSequence seq, CharSequence searchSeq) {
        if (seq != null) {
            if (searchSeq != null) {
                return CharSequenceUtils.indexOf(seq, searchSeq, 0);
            }
        }
        return -1;
    }

    public static int indexOf(CharSequence seq, CharSequence searchSeq, int startPos) {
        if (seq != null) {
            if (searchSeq != null) {
                return CharSequenceUtils.indexOf(seq, searchSeq, startPos);
            }
        }
        return -1;
    }

    public static int ordinalIndexOf(CharSequence str, CharSequence searchStr, int ordinal) {
        return ordinalIndexOf(str, searchStr, ordinal, false);
    }

    private static int ordinalIndexOf(CharSequence str, CharSequence searchStr, int ordinal, boolean lastIndex) {
        int index = -1;
        if (!(str == null || searchStr == null)) {
            if (ordinal > 0) {
                if (searchStr.length() == 0) {
                    return lastIndex ? str.length() : 0;
                }
                int found = 0;
                if (lastIndex) {
                    index = str.length();
                }
                while (true) {
                    if (lastIndex) {
                        index = CharSequenceUtils.lastIndexOf(str, searchStr, index - 1);
                    } else {
                        index = CharSequenceUtils.indexOf(str, searchStr, index + 1);
                    }
                    if (index < 0) {
                        return index;
                    }
                    found++;
                    if (found >= ordinal) {
                        return index;
                    }
                }
            }
        }
        return -1;
    }

    public static int indexOfIgnoreCase(CharSequence str, CharSequence searchStr) {
        return indexOfIgnoreCase(str, searchStr, 0);
    }

    public static int indexOfIgnoreCase(CharSequence str, CharSequence searchStr, int startPos) {
        if (str != null) {
            if (searchStr != null) {
                if (startPos < 0) {
                    startPos = 0;
                }
                int endLimit = (str.length() - searchStr.length()) + 1;
                if (startPos > endLimit) {
                    return -1;
                }
                if (searchStr.length() == 0) {
                    return startPos;
                }
                for (int i = startPos; i < endLimit; i++) {
                    if (CharSequenceUtils.regionMatches(str, true, i, searchStr, 0, searchStr.length())) {
                        return i;
                    }
                }
                return -1;
            }
        }
        return -1;
    }

    public static int lastIndexOf(CharSequence seq, int searchChar) {
        if (isEmpty(seq)) {
            return -1;
        }
        return CharSequenceUtils.lastIndexOf(seq, searchChar, seq.length());
    }

    public static int lastIndexOf(CharSequence seq, int searchChar, int startPos) {
        if (isEmpty(seq)) {
            return -1;
        }
        return CharSequenceUtils.lastIndexOf(seq, searchChar, startPos);
    }

    public static int lastIndexOf(CharSequence seq, CharSequence searchSeq) {
        if (seq != null) {
            if (searchSeq != null) {
                return CharSequenceUtils.lastIndexOf(seq, searchSeq, seq.length());
            }
        }
        return -1;
    }

    public static int lastOrdinalIndexOf(CharSequence str, CharSequence searchStr, int ordinal) {
        return ordinalIndexOf(str, searchStr, ordinal, true);
    }

    public static int lastIndexOf(CharSequence seq, CharSequence searchSeq, int startPos) {
        if (seq != null) {
            if (searchSeq != null) {
                return CharSequenceUtils.lastIndexOf(seq, searchSeq, startPos);
            }
        }
        return -1;
    }

    public static int lastIndexOfIgnoreCase(CharSequence str, CharSequence searchStr) {
        if (str != null) {
            if (searchStr != null) {
                return lastIndexOfIgnoreCase(str, searchStr, str.length());
            }
        }
        return -1;
    }

    public static int lastIndexOfIgnoreCase(CharSequence str, CharSequence searchStr, int startPos) {
        if (str != null) {
            if (searchStr != null) {
                if (startPos > str.length() - searchStr.length()) {
                    startPos = str.length() - searchStr.length();
                }
                if (startPos < 0) {
                    return -1;
                }
                if (searchStr.length() == 0) {
                    return startPos;
                }
                for (int i = startPos; i >= 0; i--) {
                    if (CharSequenceUtils.regionMatches(str, true, i, searchStr, 0, searchStr.length())) {
                        return i;
                    }
                }
                return -1;
            }
        }
        return -1;
    }

    public static boolean contains(CharSequence seq, int searchChar) {
        boolean z = false;
        if (isEmpty(seq)) {
            return false;
        }
        if (CharSequenceUtils.indexOf(seq, searchChar, 0) >= 0) {
            z = true;
        }
        return z;
    }

    public static boolean contains(CharSequence seq, CharSequence searchSeq) {
        boolean z = false;
        if (seq != null) {
            if (searchSeq != null) {
                if (CharSequenceUtils.indexOf(seq, searchSeq, 0) >= 0) {
                    z = true;
                }
                return z;
            }
        }
        return false;
    }

    public static boolean containsIgnoreCase(CharSequence str, CharSequence searchStr) {
        if (str != null) {
            if (searchStr != null) {
                int len = searchStr.length();
                int max = str.length() - len;
                for (int i = 0; i <= max; i++) {
                    if (CharSequenceUtils.regionMatches(str, true, i, searchStr, 0, len)) {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    public static boolean containsWhitespace(CharSequence seq) {
        if (isEmpty(seq)) {
            return false;
        }
        int strLen = seq.length();
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(seq.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static int indexOfAny(CharSequence cs, char... searchChars) {
        if (!isEmpty(cs)) {
            if (!ArrayUtils.isEmpty(searchChars)) {
                int csLen = cs.length();
                int csLast = csLen - 1;
                int searchLen = searchChars.length;
                int searchLast = searchLen - 1;
                for (int i = 0; i < csLen; i++) {
                    char ch = cs.charAt(i);
                    int j = 0;
                    while (j < searchLen) {
                        if (searchChars[j] == ch) {
                            if (i >= csLast || j >= searchLast || !Character.isHighSurrogate(ch)) {
                                return i;
                            }
                            if (searchChars[j + 1] == cs.charAt(i + 1)) {
                                return i;
                            }
                        }
                        j++;
                    }
                }
                return -1;
            }
        }
        return -1;
    }

    public static int indexOfAny(CharSequence cs, String searchChars) {
        if (!isEmpty(cs)) {
            if (!isEmpty(searchChars)) {
                return indexOfAny(cs, searchChars.toCharArray());
            }
        }
        return -1;
    }

    public static boolean containsAny(CharSequence cs, char... searchChars) {
        if (!isEmpty(cs)) {
            if (!ArrayUtils.isEmpty(searchChars)) {
                int csLength = cs.length();
                int searchLength = searchChars.length;
                int csLast = csLength - 1;
                int searchLast = searchLength - 1;
                int i = 0;
                while (i < csLength) {
                    char ch = cs.charAt(i);
                    int j = 0;
                    while (j < searchLength) {
                        if (searchChars[j] == ch) {
                            if (!Character.isHighSurrogate(ch) || j == searchLast) {
                                return true;
                            }
                            if (i < csLast && searchChars[j + 1] == cs.charAt(i + 1)) {
                                return true;
                            }
                        }
                        j++;
                    }
                    i++;
                }
                return false;
            }
        }
        return false;
    }

    public static boolean containsAny(CharSequence cs, CharSequence searchChars) {
        if (searchChars == null) {
            return false;
        }
        return containsAny(cs, CharSequenceUtils.toCharArray(searchChars));
    }

    public static boolean containsAny(CharSequence cs, CharSequence... searchCharSequences) {
        if (!isEmpty(cs)) {
            if (!ArrayUtils.isEmpty((Object[]) searchCharSequences)) {
                for (CharSequence searchCharSequence : searchCharSequences) {
                    if (contains(cs, searchCharSequence)) {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    public static int indexOfAnyBut(CharSequence cs, char... searchChars) {
        if (!isEmpty(cs)) {
            if (!ArrayUtils.isEmpty(searchChars)) {
                int csLen = cs.length();
                int csLast = csLen - 1;
                int searchLen = searchChars.length;
                int searchLast = searchLen - 1;
                int i = 0;
                while (i < csLen) {
                    char ch = cs.charAt(i);
                    int j = 0;
                    while (j < searchLen) {
                        if (searchChars[j] == ch) {
                            if (i < csLast && j < searchLast && Character.isHighSurrogate(ch)) {
                                if (searchChars[j + 1] == cs.charAt(i + 1)) {
                                }
                            }
                            i++;
                        }
                        j++;
                    }
                    return i;
                }
                return -1;
            }
        }
        return -1;
    }

    public static int indexOfAnyBut(CharSequence seq, CharSequence searchChars) {
        if (!isEmpty(seq)) {
            if (!isEmpty(searchChars)) {
                int strLen = seq.length();
                for (int i = 0; i < strLen; i++) {
                    int ch = seq.charAt(i);
                    boolean chFound = CharSequenceUtils.indexOf(searchChars, ch, 0) >= 0;
                    if (i + 1 < strLen && Character.isHighSurrogate(ch)) {
                        int ch2 = seq.charAt(i + 1);
                        if (chFound && CharSequenceUtils.indexOf(searchChars, ch2, 0) < 0) {
                            return i;
                        }
                    } else if (!chFound) {
                        return i;
                    }
                }
                return -1;
            }
        }
        return -1;
    }

    public static boolean containsOnly(CharSequence cs, char... valid) {
        boolean z = false;
        if (valid != null) {
            if (cs != null) {
                if (cs.length() == 0) {
                    return true;
                }
                if (valid.length == 0) {
                    return false;
                }
                if (indexOfAnyBut(cs, valid) == -1) {
                    z = true;
                }
                return z;
            }
        }
        return false;
    }

    public static boolean containsOnly(CharSequence cs, String validChars) {
        if (cs != null) {
            if (validChars != null) {
                return containsOnly(cs, validChars.toCharArray());
            }
        }
        return false;
    }

    public static boolean containsNone(CharSequence cs, char... searchChars) {
        if (cs != null) {
            if (searchChars != null) {
                int csLen = cs.length();
                int csLast = csLen - 1;
                int searchLen = searchChars.length;
                int searchLast = searchLen - 1;
                int i = 0;
                while (i < csLen) {
                    char ch = cs.charAt(i);
                    int j = 0;
                    while (j < searchLen) {
                        if (searchChars[j] == ch) {
                            if (!Character.isHighSurrogate(ch) || j == searchLast) {
                                return false;
                            }
                            if (i < csLast && searchChars[j + 1] == cs.charAt(i + 1)) {
                                return false;
                            }
                        }
                        j++;
                    }
                    i++;
                }
                return true;
            }
        }
        return true;
    }

    public static boolean containsNone(CharSequence cs, String invalidChars) {
        if (cs != null) {
            if (invalidChars != null) {
                return containsNone(cs, invalidChars.toCharArray());
            }
        }
        return true;
    }

    public static int indexOfAny(CharSequence str, CharSequence... searchStrs) {
        int i = -1;
        if (str != null) {
            if (searchStrs != null) {
                int ret = Integer.MAX_VALUE;
                for (CharSequence search : searchStrs) {
                    if (search != null) {
                        int tmp = CharSequenceUtils.indexOf(str, search, 0);
                        if (tmp != -1) {
                            if (tmp < ret) {
                                ret = tmp;
                            }
                        }
                    }
                }
                if (ret != Integer.MAX_VALUE) {
                    i = ret;
                }
                return i;
            }
        }
        return -1;
    }

    public static int lastIndexOfAny(CharSequence str, CharSequence... searchStrs) {
        if (str != null) {
            if (searchStrs != null) {
                int ret = -1;
                for (CharSequence search : searchStrs) {
                    if (search != null) {
                        int tmp = CharSequenceUtils.lastIndexOf(str, search, str.length());
                        if (tmp > ret) {
                            ret = tmp;
                        }
                    }
                }
                return ret;
            }
        }
        return -1;
    }

    public static String substring(String str, int start) {
        if (str == null) {
            return null;
        }
        if (start < 0) {
            start += str.length();
        }
        if (start < 0) {
            start = 0;
        }
        if (start > str.length()) {
            return "";
        }
        return str.substring(start);
    }

    public static String substring(String str, int start, int end) {
        if (str == null) {
            return null;
        }
        if (end < 0) {
            end += str.length();
        }
        if (start < 0) {
            start += str.length();
        }
        if (end > str.length()) {
            end = str.length();
        }
        if (start > end) {
            return "";
        }
        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }
        return str.substring(start, end);
    }

    public static String left(String str, int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return "";
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(0, len);
    }

    public static String right(String str, int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return "";
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(str.length() - len);
    }

    public static String mid(String str, int pos, int len) {
        if (str == null) {
            return null;
        }
        if (len >= 0) {
            if (pos <= str.length()) {
                if (pos < 0) {
                    pos = 0;
                }
                if (str.length() <= pos + len) {
                    return str.substring(pos);
                }
                return str.substring(pos, pos + len);
            }
        }
        return "";
    }

    public static String substringBefore(String str, String separator) {
        if (!isEmpty(str)) {
            if (separator != null) {
                if (separator.isEmpty()) {
                    return "";
                }
                int pos = str.indexOf(separator);
                if (pos == -1) {
                    return str;
                }
                return str.substring(0, pos);
            }
        }
        return str;
    }

    public static String substringAfter(String str, String separator) {
        if (isEmpty(str)) {
            return str;
        }
        if (separator == null) {
            return "";
        }
        int pos = str.indexOf(separator);
        if (pos == -1) {
            return "";
        }
        return str.substring(separator.length() + pos);
    }

    public static String substringBeforeLast(String str, String separator) {
        if (!isEmpty(str)) {
            if (!isEmpty(separator)) {
                int pos = str.lastIndexOf(separator);
                if (pos == -1) {
                    return str;
                }
                return str.substring(0, pos);
            }
        }
        return str;
    }

    public static String substringAfterLast(String str, String separator) {
        if (isEmpty(str)) {
            return str;
        }
        if (isEmpty(separator)) {
            return "";
        }
        int pos = str.lastIndexOf(separator);
        if (pos != -1) {
            if (pos != str.length() - separator.length()) {
                return str.substring(separator.length() + pos);
            }
        }
        return "";
    }

    public static String substringBetween(String str, String tag) {
        return substringBetween(str, tag, tag);
    }

    public static String substringBetween(String str, String open, String close) {
        if (!(str == null || open == null)) {
            if (close != null) {
                int start = str.indexOf(open);
                if (start != -1) {
                    int end = str.indexOf(close, open.length() + start);
                    if (end != -1) {
                        return str.substring(open.length() + start, end);
                    }
                }
                return null;
            }
        }
        return null;
    }

    public static String[] substringsBetween(String str, String open, String close) {
        if (!(str == null || isEmpty(open))) {
            if (!isEmpty(close)) {
                int strLen = str.length();
                if (strLen == 0) {
                    return ArrayUtils.EMPTY_STRING_ARRAY;
                }
                int closeLen = close.length();
                int openLen = open.length();
                List<String> list = new ArrayList();
                int pos = 0;
                while (pos < strLen - closeLen) {
                    int start = str.indexOf(open, pos);
                    if (start < 0) {
                        break;
                    }
                    start += openLen;
                    int end = str.indexOf(close, start);
                    if (end < 0) {
                        break;
                    }
                    list.add(str.substring(start, end));
                    pos = end + closeLen;
                }
                return list.isEmpty() ? null : (String[]) list.toArray(new String[list.size()]);
            }
        }
        return null;
    }

    public static String[] split(String str) {
        return split(str, null, -1);
    }

    public static String[] split(String str, char separatorChar) {
        return splitWorker(str, separatorChar, false);
    }

    public static String[] split(String str, String separatorChars) {
        return splitWorker(str, separatorChars, -1, false);
    }

    public static String[] split(String str, String separatorChars, int max) {
        return splitWorker(str, separatorChars, max, false);
    }

    public static String[] splitByWholeSeparator(String str, String separator) {
        return splitByWholeSeparatorWorker(str, separator, -1, false);
    }

    public static String[] splitByWholeSeparator(String str, String separator, int max) {
        return splitByWholeSeparatorWorker(str, separator, max, false);
    }

    public static String[] splitByWholeSeparatorPreserveAllTokens(String str, String separator) {
        return splitByWholeSeparatorWorker(str, separator, -1, true);
    }

    public static String[] splitByWholeSeparatorPreserveAllTokens(String str, String separator, int max) {
        return splitByWholeSeparatorWorker(str, separator, max, true);
    }

    private static String[] splitByWholeSeparatorWorker(String str, String separator, int max, boolean preserveAllTokens) {
        if (str == null) {
            return null;
        }
        int len = str.length();
        if (len == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        if (separator != null) {
            if (!"".equals(separator)) {
                int separatorLength = separator.length();
                ArrayList<String> substrings = new ArrayList();
                int numberOfSubstrings = 0;
                int beg = 0;
                int end = 0;
                while (end < len) {
                    end = str.indexOf(separator, beg);
                    if (end <= -1) {
                        substrings.add(str.substring(beg));
                        end = len;
                    } else if (end > beg) {
                        numberOfSubstrings++;
                        if (numberOfSubstrings == max) {
                            end = len;
                            substrings.add(str.substring(beg));
                        } else {
                            substrings.add(str.substring(beg, end));
                            beg = end + separatorLength;
                        }
                    } else {
                        if (preserveAllTokens) {
                            numberOfSubstrings++;
                            if (numberOfSubstrings == max) {
                                end = len;
                                substrings.add(str.substring(beg));
                            } else {
                                substrings.add("");
                            }
                        }
                        beg = end + separatorLength;
                    }
                }
                return (String[]) substrings.toArray(new String[substrings.size()]);
            }
        }
        return splitWorker(str, null, max, preserveAllTokens);
    }

    public static String[] splitPreserveAllTokens(String str) {
        return splitWorker(str, null, -1, true);
    }

    public static String[] splitPreserveAllTokens(String str, char separatorChar) {
        return splitWorker(str, separatorChar, true);
    }

    private static String[] splitWorker(String str, char separatorChar, boolean preserveAllTokens) {
        if (str == null) {
            return null;
        }
        int len = str.length();
        if (len == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        List<String> list = new ArrayList();
        int i = 0;
        int start = 0;
        boolean match = false;
        boolean lastMatch = false;
        while (i < len) {
            if (str.charAt(i) == separatorChar) {
                if (!match) {
                    if (!preserveAllTokens) {
                        i++;
                        start = i;
                    }
                }
                list.add(str.substring(start, i));
                match = false;
                lastMatch = true;
                i++;
                start = i;
            } else {
                lastMatch = false;
                match = true;
                i++;
            }
        }
        if (!match) {
            if (!preserveAllTokens || !lastMatch) {
                return (String[]) list.toArray(new String[list.size()]);
            }
        }
        list.add(str.substring(start, i));
        return (String[]) list.toArray(new String[list.size()]);
    }

    public static String[] splitPreserveAllTokens(String str, String separatorChars) {
        return splitWorker(str, separatorChars, -1, true);
    }

    public static String[] splitPreserveAllTokens(String str, String separatorChars, int max) {
        return splitWorker(str, separatorChars, max, true);
    }

    private static String[] splitWorker(String str, String separatorChars, int max, boolean preserveAllTokens) {
        if (str == null) {
            return null;
        }
        int len = str.length();
        if (len == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        List<String> list = new ArrayList();
        int i = 1;
        int i2 = 0;
        int start = 0;
        boolean match = false;
        boolean lastMatch = false;
        int sizePlus1;
        if (separatorChars == null) {
            while (i2 < len) {
                if (Character.isWhitespace(str.charAt(i2))) {
                    if (!match) {
                        if (!preserveAllTokens) {
                            i2++;
                            start = i2;
                        }
                    }
                    lastMatch = true;
                    sizePlus1 = i + 1;
                    if (i == max) {
                        lastMatch = false;
                        i2 = len;
                    }
                    list.add(str.substring(start, i2));
                    match = false;
                    i = sizePlus1;
                    i2++;
                    start = i2;
                } else {
                    lastMatch = false;
                    match = true;
                    i2++;
                }
            }
        } else if (separatorChars.length() == 1) {
            char sep = separatorChars.charAt('\u0000');
            while (i2 < len) {
                if (str.charAt(i2) == sep) {
                    if (!match) {
                        if (!preserveAllTokens) {
                            i2++;
                            start = i2;
                        }
                    }
                    lastMatch = true;
                    int sizePlus12 = i + 1;
                    if (i == max) {
                        lastMatch = false;
                        i2 = len;
                    }
                    list.add(str.substring(start, i2));
                    match = false;
                    i = sizePlus12;
                    i2++;
                    start = i2;
                } else {
                    lastMatch = false;
                    match = true;
                    i2++;
                }
            }
        } else {
            while (i2 < len) {
                if (separatorChars.indexOf(str.charAt(i2)) >= 0) {
                    if (!match) {
                        if (!preserveAllTokens) {
                            i2++;
                            start = i2;
                        }
                    }
                    lastMatch = true;
                    sizePlus1 = i + 1;
                    if (i == max) {
                        lastMatch = false;
                        i2 = len;
                    }
                    list.add(str.substring(start, i2));
                    match = false;
                    i = sizePlus1;
                    i2++;
                    start = i2;
                } else {
                    lastMatch = false;
                    match = true;
                    i2++;
                }
            }
        }
        if (!match) {
            if (!preserveAllTokens || !lastMatch) {
                return (String[]) list.toArray(new String[list.size()]);
            }
        }
        list.add(str.substring(start, i2));
        return (String[]) list.toArray(new String[list.size()]);
    }

    public static String[] splitByCharacterType(String str) {
        return splitByCharacterType(str, false);
    }

    public static String[] splitByCharacterTypeCamelCase(String str) {
        return splitByCharacterType(str, true);
    }

    private static String[] splitByCharacterType(String str, boolean camelCase) {
        if (str == null) {
            return null;
        }
        if (str.isEmpty()) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        char[] c = str.toCharArray();
        List<String> list = new ArrayList();
        int tokenStart = 0;
        int currentType = Character.getType(c[0]);
        for (int pos = 0 + 1; pos < c.length; pos++) {
            int type = Character.getType(c[pos]);
            if (type != currentType) {
                if (camelCase && type == 2 && currentType == 1) {
                    int newTokenStart = pos - 1;
                    if (newTokenStart != tokenStart) {
                        list.add(new String(c, tokenStart, newTokenStart - tokenStart));
                        tokenStart = newTokenStart;
                    }
                } else {
                    list.add(new String(c, tokenStart, pos - tokenStart));
                    tokenStart = pos;
                }
                currentType = type;
            }
        }
        list.add(new String(c, tokenStart, c.length - tokenStart));
        return (String[]) list.toArray(new String[list.size()]);
    }

    @SafeVarargs
    public static <T> String join(T... elements) {
        return join((Object[]) elements, null);
    }

    public static String join(Object[] array, char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    public static String join(long[] array, char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    public static String join(int[] array, char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    public static String join(short[] array, char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    public static String join(byte[] array, char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    public static String join(char[] array, char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    public static String join(float[] array, char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    public static String join(double[] array, char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    public static String join(Object[] array, char separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return "";
        }
        StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }

    public static String join(long[] array, char separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return "";
        }
        StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            buf.append(array[i]);
        }
        return buf.toString();
    }

    public static String join(int[] array, char separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return "";
        }
        StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            buf.append(array[i]);
        }
        return buf.toString();
    }

    public static String join(byte[] array, char separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return "";
        }
        StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            buf.append(array[i]);
        }
        return buf.toString();
    }

    public static String join(short[] array, char separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return "";
        }
        StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            buf.append(array[i]);
        }
        return buf.toString();
    }

    public static String join(char[] array, char separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return "";
        }
        StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            buf.append(array[i]);
        }
        return buf.toString();
    }

    public static String join(double[] array, char separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return "";
        }
        StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            buf.append(array[i]);
        }
        return buf.toString();
    }

    public static String join(float[] array, char separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return "";
        }
        StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            buf.append(array[i]);
        }
        return buf.toString();
    }

    public static String join(Object[] array, String separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    public static String join(Object[] array, String separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        if (separator == null) {
            separator = "";
        }
        int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return "";
        }
        StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }

    public static String join(Iterator<?> iterator, char separator) {
        if (iterator == null) {
            return null;
        }
        if (!iterator.hasNext()) {
            return "";
        }
        Object first = iterator.next();
        if (!iterator.hasNext()) {
            return Objects.toString(first, "");
        }
        StringBuilder buf = new StringBuilder(256);
        if (first != null) {
            buf.append(first);
        }
        while (iterator.hasNext()) {
            buf.append(separator);
            Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
        }
        return buf.toString();
    }

    public static String join(Iterator<?> iterator, String separator) {
        if (iterator == null) {
            return null;
        }
        if (!iterator.hasNext()) {
            return "";
        }
        Object first = iterator.next();
        if (!iterator.hasNext()) {
            return Objects.toString(first, "");
        }
        StringBuilder buf = new StringBuilder(256);
        if (first != null) {
            buf.append(first);
        }
        while (iterator.hasNext()) {
            if (separator != null) {
                buf.append(separator);
            }
            Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
        }
        return buf.toString();
    }

    public static String join(Iterable<?> iterable, char separator) {
        if (iterable == null) {
            return null;
        }
        return join(iterable.iterator(), separator);
    }

    public static String join(Iterable<?> iterable, String separator) {
        if (iterable == null) {
            return null;
        }
        return join(iterable.iterator(), separator);
    }

    public static String deleteWhitespace(String str) {
        if (isEmpty(str)) {
            return str;
        }
        int sz = str.length();
        char[] chs = new char[sz];
        int count = 0;
        for (int i = 0; i < sz; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                int count2 = count + 1;
                chs[count] = str.charAt(i);
                count = count2;
            }
        }
        if (count == sz) {
            return str;
        }
        return new String(chs, 0, count);
    }

    public static String removeStart(String str, String remove) {
        if (!isEmpty(str)) {
            if (!isEmpty(remove)) {
                if (str.startsWith(remove)) {
                    return str.substring(remove.length());
                }
                return str;
            }
        }
        return str;
    }

    public static String removeStartIgnoreCase(String str, String remove) {
        if (!isEmpty(str)) {
            if (!isEmpty(remove)) {
                if (startsWithIgnoreCase(str, remove)) {
                    return str.substring(remove.length());
                }
                return str;
            }
        }
        return str;
    }

    public static String removeEnd(String str, String remove) {
        if (!isEmpty(str)) {
            if (!isEmpty(remove)) {
                if (str.endsWith(remove)) {
                    return str.substring(0, str.length() - remove.length());
                }
                return str;
            }
        }
        return str;
    }

    public static String removeEndIgnoreCase(String str, String remove) {
        if (!isEmpty(str)) {
            if (!isEmpty(remove)) {
                if (endsWithIgnoreCase(str, remove)) {
                    return str.substring(0, str.length() - remove.length());
                }
                return str;
            }
        }
        return str;
    }

    public static String remove(String str, String remove) {
        if (!isEmpty(str)) {
            if (!isEmpty(remove)) {
                return replace(str, remove, "", -1);
            }
        }
        return str;
    }

    public static String removeIgnoreCase(String str, String remove) {
        if (!isEmpty(str)) {
            if (!isEmpty(remove)) {
                return replaceIgnoreCase(str, remove, "", -1);
            }
        }
        return str;
    }

    public static String remove(String str, char remove) {
        if (!isEmpty(str)) {
            if (str.indexOf(remove) != -1) {
                char[] chars = str.toCharArray();
                int pos = 0;
                for (int i = 0; i < chars.length; i++) {
                    if (chars[i] != remove) {
                        int pos2 = pos + 1;
                        chars[pos] = chars[i];
                        pos = pos2;
                    }
                }
                return new String(chars, 0, pos);
            }
        }
        return str;
    }

    public static String removeAll(String text, String regex) {
        return replaceAll(text, regex, "");
    }

    public static String removeFirst(String text, String regex) {
        return replaceFirst(text, regex, "");
    }

    public static String replaceOnce(String text, String searchString, String replacement) {
        return replace(text, searchString, replacement, 1);
    }

    public static String replaceOnceIgnoreCase(String text, String searchString, String replacement) {
        return replaceIgnoreCase(text, searchString, replacement, 1);
    }

    public static String replacePattern(String source, String regex, String replacement) {
        if (!(source == null || regex == null)) {
            if (replacement != null) {
                return Pattern.compile(regex, 32).matcher(source).replaceAll(replacement);
            }
        }
        return source;
    }

    public static String removePattern(String source, String regex) {
        return replacePattern(source, regex, "");
    }

    public static String replaceAll(String text, String regex, String replacement) {
        if (!(text == null || regex == null)) {
            if (replacement != null) {
                return text.replaceAll(regex, replacement);
            }
        }
        return text;
    }

    public static String replaceFirst(String text, String regex, String replacement) {
        if (!(text == null || regex == null)) {
            if (replacement != null) {
                return text.replaceFirst(regex, replacement);
            }
        }
        return text;
    }

    public static String replace(String text, String searchString, String replacement) {
        return replace(text, searchString, replacement, -1);
    }

    public static String replaceIgnoreCase(String text, String searchString, String replacement) {
        return replaceIgnoreCase(text, searchString, replacement, -1);
    }

    public static String replace(String text, String searchString, String replacement, int max) {
        return replace(text, searchString, replacement, max, false);
    }

    private static String replace(String text, String searchString, String replacement, int max, boolean ignoreCase) {
        if (!(isEmpty(text) || isEmpty(searchString) || replacement == null)) {
            if (max != 0) {
                String searchText = text;
                if (ignoreCase) {
                    searchText = text.toLowerCase();
                    searchString = searchString.toLowerCase();
                }
                int start = 0;
                int end = searchText.indexOf(searchString, 0);
                if (end == -1) {
                    return text;
                }
                int replLength = searchString.length();
                int increase = replacement.length() - replLength;
                increase = increase < 0 ? 0 : increase;
                int i = 64;
                if (max < 0) {
                    i = 16;
                } else if (max <= 64) {
                    i = max;
                }
                StringBuilder buf = new StringBuilder(text.length() + (increase * i));
                while (end != -1) {
                    buf.append(text, start, end);
                    buf.append(replacement);
                    start = end + replLength;
                    max--;
                    if (max == 0) {
                        break;
                    }
                    end = searchText.indexOf(searchString, start);
                }
                buf.append(text, start, text.length());
                return buf.toString();
            }
        }
        return text;
    }

    public static String replaceIgnoreCase(String text, String searchString, String replacement, int max) {
        return replace(text, searchString, replacement, max, true);
    }

    public static String replaceEach(String text, String[] searchList, String[] replacementList) {
        return replaceEach(text, searchList, replacementList, false, 0);
    }

    public static String replaceEachRepeatedly(String text, String[] searchList, String[] replacementList) {
        return replaceEach(text, searchList, replacementList, true, searchList == null ? 0 : searchList.length);
    }

    private static String replaceEach(String text, String[] searchList, String[] replacementList, boolean repeat, int timeToLive) {
        String str = text;
        String[] strArr = searchList;
        String[] strArr2 = replacementList;
        boolean z = repeat;
        if (!(str == null || text.isEmpty() || strArr == null || strArr.length == 0 || strArr2 == null)) {
            if (strArr2.length != 0) {
                if (timeToLive >= 0) {
                    int searchLength = strArr.length;
                    int replacementLength = strArr2.length;
                    if (searchLength == replacementLength) {
                        int tempIndex;
                        boolean[] noMoreMatchesForReplIndex = new boolean[searchLength];
                        int textIndex = -1;
                        int replaceIndex = -1;
                        int i = 0;
                        while (i < searchLength) {
                            if (!noMoreMatchesForReplIndex[i] && strArr[i] != null && !strArr[i].isEmpty()) {
                                if (strArr2[i] != null) {
                                    tempIndex = str.indexOf(strArr[i]);
                                    if (tempIndex == -1) {
                                        noMoreMatchesForReplIndex[i] = true;
                                    } else {
                                        if (textIndex != -1) {
                                            if (tempIndex < textIndex) {
                                            }
                                        }
                                        textIndex = tempIndex;
                                        replaceIndex = i;
                                    }
                                }
                            }
                            i++;
                        }
                        if (textIndex == -1) {
                            return str;
                        }
                        int greater;
                        int i2;
                        i = 0;
                        int increase = 0;
                        for (int i3 = 0; i3 < strArr.length; i3++) {
                            if (strArr[i3] != null) {
                                if (strArr2[i3] != null) {
                                    greater = strArr2[i3].length() - strArr[i3].length();
                                    if (greater > 0) {
                                        increase += greater * 3;
                                    }
                                }
                            }
                        }
                        StringBuilder buf = new StringBuilder(text.length() + Math.min(increase, text.length() / 5));
                        while (textIndex != -1) {
                            for (greater = i; greater < textIndex; greater++) {
                                buf.append(str.charAt(greater));
                            }
                            buf.append(strArr2[replaceIndex]);
                            i = textIndex + strArr[replaceIndex].length();
                            textIndex = -1;
                            replaceIndex = -1;
                            i2 = 0;
                            while (i2 < searchLength) {
                                if (!noMoreMatchesForReplIndex[i2] && strArr[i2] != null && !strArr[i2].isEmpty()) {
                                    if (strArr2[i2] != null) {
                                        tempIndex = str.indexOf(strArr[i2], i);
                                        if (tempIndex == -1) {
                                            noMoreMatchesForReplIndex[i2] = true;
                                        } else {
                                            if (textIndex != -1) {
                                                if (tempIndex < textIndex) {
                                                }
                                            }
                                            textIndex = tempIndex;
                                            replaceIndex = i2;
                                        }
                                    }
                                }
                                i2++;
                            }
                        }
                        int textLength = text.length();
                        for (i2 = i; i2 < textLength; i2++) {
                            buf.append(str.charAt(i2));
                        }
                        String result = buf.toString();
                        if (z) {
                            return replaceEach(result, strArr, strArr2, z, timeToLive - 1);
                        }
                        return result;
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Search and Replace array lengths don't match: ");
                    stringBuilder.append(searchLength);
                    stringBuilder.append(" vs ");
                    stringBuilder.append(replacementLength);
                    throw new IllegalArgumentException(stringBuilder.toString());
                }
                throw new IllegalStateException("Aborting to protect against StackOverflowError - output of one loop is the input of another");
            }
        }
        return str;
    }

    public static String replaceChars(String str, char searchChar, char replaceChar) {
        if (str == null) {
            return null;
        }
        return str.replace(searchChar, replaceChar);
    }

    public static String replaceChars(String str, String searchChars, String replaceChars) {
        if (!isEmpty(str)) {
            if (!isEmpty(searchChars)) {
                if (replaceChars == null) {
                    replaceChars = "";
                }
                boolean modified = false;
                int replaceCharsLength = replaceChars.length();
                int strLength = str.length();
                StringBuilder buf = new StringBuilder(strLength);
                for (int i = 0; i < strLength; i++) {
                    char ch = str.charAt(i);
                    int index = searchChars.indexOf(ch);
                    if (index >= 0) {
                        modified = true;
                        if (index < replaceCharsLength) {
                            buf.append(replaceChars.charAt(index));
                        }
                    } else {
                        buf.append(ch);
                    }
                }
                if (modified) {
                    return buf.toString();
                }
                return str;
            }
        }
        return str;
    }

    public static String overlay(String str, String overlay, int start, int end) {
        if (str == null) {
            return null;
        }
        if (overlay == null) {
            overlay = "";
        }
        int len = str.length();
        if (start < 0) {
            start = 0;
        }
        if (start > len) {
            start = len;
        }
        if (end < 0) {
            end = 0;
        }
        if (end > len) {
            end = len;
        }
        if (start > end) {
            int temp = start;
            start = end;
            end = temp;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(str.substring(0, start));
        stringBuilder.append(overlay);
        stringBuilder.append(str.substring(end));
        return stringBuilder.toString();
    }

    public static String chomp(String str) {
        if (isEmpty(str)) {
            return str;
        }
        if (str.length() == 1) {
            char ch = str.charAt(0);
            if (ch != CharUtils.CR) {
                if (ch != '\n') {
                    return str;
                }
            }
            return "";
        }
        int lastIdx = str.length() - 1;
        char last = str.charAt(lastIdx);
        if (last == '\n') {
            if (str.charAt(lastIdx - 1) == CharUtils.CR) {
                lastIdx--;
            }
        } else if (last != CharUtils.CR) {
            lastIdx++;
        }
        return str.substring(0, lastIdx);
    }

    @Deprecated
    public static String chomp(String str, String separator) {
        return removeEnd(str, separator);
    }

    public static String chop(String str) {
        if (str == null) {
            return null;
        }
        int strLen = str.length();
        if (strLen < 2) {
            return "";
        }
        int lastIdx = strLen - 1;
        String ret = str.substring(0, lastIdx);
        if (str.charAt(lastIdx) == '\n' && ret.charAt(lastIdx - 1) == CharUtils.CR) {
            return ret.substring(0, lastIdx - 1);
        }
        return ret;
    }

    public static String repeat(String str, int repeat) {
        if (str == null) {
            return null;
        }
        if (repeat <= 0) {
            return "";
        }
        int inputLength = str.length();
        if (repeat != 1) {
            if (inputLength != 0) {
                if (inputLength == 1 && repeat <= 8192) {
                    return repeat(str.charAt(0), repeat);
                }
                int outputLength = inputLength * repeat;
                switch (inputLength) {
                    case 1:
                        return repeat(str.charAt(0), repeat);
                    case 2:
                        char ch0 = str.charAt(0);
                        char ch1 = str.charAt(1);
                        char[] output2 = new char[outputLength];
                        for (int i = (repeat * 2) - 2; i >= 0; i = (i - 1) - 1) {
                            output2[i] = ch0;
                            output2[i + 1] = ch1;
                        }
                        return new String(output2);
                    default:
                        StringBuilder buf = new StringBuilder(outputLength);
                        for (int i2 = 0; i2 < repeat; i2++) {
                            buf.append(str);
                        }
                        return buf.toString();
                }
            }
        }
        return str;
    }

    public static String repeat(String str, String separator, int repeat) {
        if (str != null) {
            if (separator != null) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append(separator);
                return removeEnd(repeat(stringBuilder.toString(), repeat), separator);
            }
        }
        return repeat(str, repeat);
    }

    public static String repeat(char ch, int repeat) {
        if (repeat <= 0) {
            return "";
        }
        char[] buf = new char[repeat];
        for (int i = repeat - 1; i >= 0; i--) {
            buf[i] = ch;
        }
        return new String(buf);
    }

    public static String rightPad(String str, int size) {
        return rightPad(str, size, ' ');
    }

    public static String rightPad(String str, int size, char padChar) {
        if (str == null) {
            return null;
        }
        int pads = size - str.length();
        if (pads <= 0) {
            return str;
        }
        if (pads > 8192) {
            return rightPad(str, size, String.valueOf(padChar));
        }
        return str.concat(repeat(padChar, pads));
    }

    public static String rightPad(String str, int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (isEmpty(padStr)) {
            padStr = SPACE;
        }
        int padLen = padStr.length();
        int pads = size - str.length();
        if (pads <= 0) {
            return str;
        }
        if (padLen == 1 && pads <= 8192) {
            return rightPad(str, size, padStr.charAt(0));
        }
        if (pads == padLen) {
            return str.concat(padStr);
        }
        if (pads < padLen) {
            return str.concat(padStr.substring(0, pads));
        }
        char[] padding = new char[pads];
        char[] padChars = padStr.toCharArray();
        for (int i = 0; i < pads; i++) {
            padding[i] = padChars[i % padLen];
        }
        return str.concat(new String(padding));
    }

    public static String leftPad(String str, int size) {
        return leftPad(str, size, ' ');
    }

    public static String leftPad(String str, int size, char padChar) {
        if (str == null) {
            return null;
        }
        int pads = size - str.length();
        if (pads <= 0) {
            return str;
        }
        if (pads > 8192) {
            return leftPad(str, size, String.valueOf(padChar));
        }
        return repeat(padChar, pads).concat(str);
    }

    public static String leftPad(String str, int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (isEmpty(padStr)) {
            padStr = SPACE;
        }
        int padLen = padStr.length();
        int pads = size - str.length();
        if (pads <= 0) {
            return str;
        }
        if (padLen == 1 && pads <= 8192) {
            return leftPad(str, size, padStr.charAt(0));
        }
        if (pads == padLen) {
            return padStr.concat(str);
        }
        if (pads < padLen) {
            return padStr.substring(0, pads).concat(str);
        }
        char[] padding = new char[pads];
        char[] padChars = padStr.toCharArray();
        for (int i = 0; i < pads; i++) {
            padding[i] = padChars[i % padLen];
        }
        return new String(padding).concat(str);
    }

    public static int length(CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }

    public static String center(String str, int size) {
        return center(str, size, ' ');
    }

    public static String center(String str, int size, char padChar) {
        if (str != null) {
            if (size > 0) {
                int strLen = str.length();
                int pads = size - strLen;
                if (pads <= 0) {
                    return str;
                }
                return rightPad(leftPad(str, (pads / 2) + strLen, padChar), size, padChar);
            }
        }
        return str;
    }

    public static String center(String str, int size, String padStr) {
        if (str != null) {
            if (size > 0) {
                if (isEmpty(padStr)) {
                    padStr = SPACE;
                }
                int strLen = str.length();
                int pads = size - strLen;
                if (pads <= 0) {
                    return str;
                }
                return rightPad(leftPad(str, (pads / 2) + strLen, padStr), size, padStr);
            }
        }
        return str;
    }

    public static String upperCase(String str) {
        if (str == null) {
            return null;
        }
        return str.toUpperCase();
    }

    public static String upperCase(String str, Locale locale) {
        if (str == null) {
            return null;
        }
        return str.toUpperCase(locale);
    }

    public static String lowerCase(String str) {
        if (str == null) {
            return null;
        }
        return str.toLowerCase();
    }

    public static String lowerCase(String str, Locale locale) {
        if (str == null) {
            return null;
        }
        return str.toLowerCase(locale);
    }

    public static String capitalize(String str) {
        if (str != null) {
            int length = str.length();
            int strLen = length;
            if (length != 0) {
                int firstCodepoint = str.codePointAt(0);
                int newCodePoint = Character.toTitleCase(firstCodepoint);
                if (firstCodepoint == newCodePoint) {
                    return str;
                }
                int[] newCodePoints = new int[strLen];
                int outOffset = 0 + 1;
                newCodePoints[0] = newCodePoint;
                int inOffset = Character.charCount(firstCodepoint);
                while (inOffset < strLen) {
                    int codepoint = str.codePointAt(inOffset);
                    int outOffset2 = outOffset + 1;
                    newCodePoints[outOffset] = codepoint;
                    inOffset += Character.charCount(codepoint);
                    outOffset = outOffset2;
                }
                return new String(newCodePoints, 0, outOffset);
            }
        }
        return str;
    }

    public static String uncapitalize(String str) {
        if (str != null) {
            int length = str.length();
            int strLen = length;
            if (length != 0) {
                int firstCodepoint = str.codePointAt(0);
                int newCodePoint = Character.toLowerCase(firstCodepoint);
                if (firstCodepoint == newCodePoint) {
                    return str;
                }
                int[] newCodePoints = new int[strLen];
                int outOffset = 0 + 1;
                newCodePoints[0] = newCodePoint;
                int inOffset = Character.charCount(firstCodepoint);
                while (inOffset < strLen) {
                    int codepoint = str.codePointAt(inOffset);
                    int outOffset2 = outOffset + 1;
                    newCodePoints[outOffset] = codepoint;
                    inOffset += Character.charCount(codepoint);
                    outOffset = outOffset2;
                }
                return new String(newCodePoints, 0, outOffset);
            }
        }
        return str;
    }

    public static String swapCase(String str) {
        if (isEmpty(str)) {
            return str;
        }
        int strLen = str.length();
        int[] newCodePoints = new int[strLen];
        int outOffset = 0;
        int i = 0;
        while (i < strLen) {
            int newCodePoint;
            int oldCodepoint = str.codePointAt(i);
            if (Character.isUpperCase(oldCodepoint)) {
                newCodePoint = Character.toLowerCase(oldCodepoint);
            } else if (Character.isTitleCase(oldCodepoint)) {
                newCodePoint = Character.toLowerCase(oldCodepoint);
            } else if (Character.isLowerCase(oldCodepoint)) {
                newCodePoint = Character.toUpperCase(oldCodepoint);
            } else {
                newCodePoint = oldCodepoint;
            }
            int outOffset2 = outOffset + 1;
            newCodePoints[outOffset] = newCodePoint;
            i += Character.charCount(newCodePoint);
            outOffset = outOffset2;
        }
        return new String(newCodePoints, 0, outOffset);
    }

    public static int countMatches(CharSequence str, CharSequence sub) {
        if (!isEmpty(str)) {
            if (!isEmpty(sub)) {
                int count = 0;
                int idx = 0;
                while (true) {
                    int indexOf = CharSequenceUtils.indexOf(str, sub, idx);
                    idx = indexOf;
                    if (indexOf == -1) {
                        return count;
                    }
                    count++;
                    idx += sub.length();
                }
            }
        }
        return 0;
    }

    public static int countMatches(CharSequence str, char ch) {
        if (isEmpty(str)) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (ch == str.charAt(i)) {
                count++;
            }
        }
        return count;
    }

    public static boolean isAlpha(CharSequence cs) {
        if (isEmpty(cs)) {
            return false;
        }
        int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isLetter(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAlphaSpace(CharSequence cs) {
        if (cs == null) {
            return false;
        }
        int sz = cs.length();
        int i = 0;
        while (i < sz) {
            if (!Character.isLetter(cs.charAt(i)) && cs.charAt(i) != ' ') {
                return false;
            }
            i++;
        }
        return true;
    }

    public static boolean isAlphanumeric(CharSequence cs) {
        if (isEmpty(cs)) {
            return false;
        }
        int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isLetterOrDigit(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAlphanumericSpace(CharSequence cs) {
        if (cs == null) {
            return false;
        }
        int sz = cs.length();
        int i = 0;
        while (i < sz) {
            if (!Character.isLetterOrDigit(cs.charAt(i)) && cs.charAt(i) != ' ') {
                return false;
            }
            i++;
        }
        return true;
    }

    public static boolean isAsciiPrintable(CharSequence cs) {
        if (cs == null) {
            return false;
        }
        int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!CharUtils.isAsciiPrintable(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNumeric(CharSequence cs) {
        if (isEmpty(cs)) {
            return false;
        }
        int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNumericSpace(CharSequence cs) {
        if (cs == null) {
            return false;
        }
        int sz = cs.length();
        int i = 0;
        while (i < sz) {
            if (!Character.isDigit(cs.charAt(i)) && cs.charAt(i) != ' ') {
                return false;
            }
            i++;
        }
        return true;
    }

    public static String getDigits(String str) {
        if (isEmpty(str)) {
            return str;
        }
        int sz = str.length();
        StringBuilder strDigits = new StringBuilder(sz);
        for (int i = 0; i < sz; i++) {
            char tempChar = str.charAt(i);
            if (Character.isDigit(tempChar)) {
                strDigits.append(tempChar);
            }
        }
        return strDigits.toString();
    }

    public static boolean isWhitespace(CharSequence cs) {
        if (cs == null) {
            return false;
        }
        int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAllLowerCase(CharSequence cs) {
        if (cs != null) {
            if (!isEmpty(cs)) {
                int sz = cs.length();
                for (int i = 0; i < sz; i++) {
                    if (!Character.isLowerCase(cs.charAt(i))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public static boolean isAllUpperCase(CharSequence cs) {
        if (cs != null) {
            if (!isEmpty(cs)) {
                int sz = cs.length();
                for (int i = 0; i < sz; i++) {
                    if (!Character.isUpperCase(cs.charAt(i))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public static boolean isMixedCase(CharSequence cs) {
        boolean z = false;
        if (!isEmpty(cs)) {
            if (cs.length() != 1) {
                boolean containsUppercase = false;
                boolean containsLowercase = false;
                int sz = cs.length();
                for (int i = 0; i < sz; i++) {
                    if (containsUppercase && containsLowercase) {
                        return true;
                    }
                    if (Character.isUpperCase(cs.charAt(i))) {
                        containsUppercase = true;
                    } else if (Character.isLowerCase(cs.charAt(i))) {
                        containsLowercase = true;
                    }
                }
                if (containsUppercase && containsLowercase) {
                    z = true;
                }
                return z;
            }
        }
        return false;
    }

    public static String defaultString(String str) {
        return str == null ? "" : str;
    }

    public static String defaultString(String str, String defaultStr) {
        return str == null ? defaultStr : str;
    }

    public static <T extends CharSequence> T defaultIfBlank(T str, T defaultStr) {
        return isBlank(str) ? defaultStr : str;
    }

    public static <T extends CharSequence> T defaultIfEmpty(T str, T defaultStr) {
        return isEmpty(str) ? defaultStr : str;
    }

    public static String rotate(String str, int shift) {
        if (str == null) {
            return null;
        }
        int strLen = str.length();
        if (!(shift == 0 || strLen == 0)) {
            if (shift % strLen != 0) {
                StringBuilder builder = new StringBuilder(strLen);
                int offset = -(shift % strLen);
                builder.append(substring(str, offset));
                builder.append(substring(str, 0, offset));
                return builder.toString();
            }
        }
        return str;
    }

    public static String reverse(String str) {
        if (str == null) {
            return null;
        }
        return new StringBuilder(str).reverse().toString();
    }

    public static String reverseDelimited(String str, char separatorChar) {
        if (str == null) {
            return null;
        }
        Object[] strs = split(str, separatorChar);
        ArrayUtils.reverse(strs);
        return join(strs, separatorChar);
    }

    public static String abbreviate(String str, int maxWidth) {
        String defaultAbbrevMarker = "...";
        return abbreviate(str, "...", 0, maxWidth);
    }

    public static String abbreviate(String str, int offset, int maxWidth) {
        String defaultAbbrevMarker = "...";
        return abbreviate(str, "...", offset, maxWidth);
    }

    public static String abbreviate(String str, String abbrevMarker, int maxWidth) {
        return abbreviate(str, abbrevMarker, 0, maxWidth);
    }

    public static String abbreviate(String str, String abbrevMarker, int offset, int maxWidth) {
        if (!isEmpty(str)) {
            if (!isEmpty(abbrevMarker)) {
                int abbrevMarkerLength = abbrevMarker.length();
                int minAbbrevWidthOffset = (abbrevMarkerLength + abbrevMarkerLength) + 1;
                if (maxWidth < abbrevMarkerLength + 1) {
                    throw new IllegalArgumentException(String.format("Minimum abbreviation width is %d", new Object[]{Integer.valueOf(abbrevMarkerLength + 1)}));
                } else if (str.length() <= maxWidth) {
                    return str;
                } else {
                    if (offset > str.length()) {
                        offset = str.length();
                    }
                    if (str.length() - offset < maxWidth - abbrevMarkerLength) {
                        offset = str.length() - (maxWidth - abbrevMarkerLength);
                    }
                    StringBuilder stringBuilder;
                    if (offset <= abbrevMarkerLength + 1) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(str.substring(0, maxWidth - abbrevMarkerLength));
                        stringBuilder.append(abbrevMarker);
                        return stringBuilder.toString();
                    } else if (maxWidth < minAbbrevWidthOffset) {
                        throw new IllegalArgumentException(String.format("Minimum abbreviation width with offset is %d", new Object[]{Integer.valueOf(minAbbrevWidthOffset)}));
                    } else if ((offset + maxWidth) - abbrevMarkerLength < str.length()) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(abbrevMarker);
                        stringBuilder.append(abbreviate(str.substring(offset), abbrevMarker, maxWidth - abbrevMarkerLength));
                        return stringBuilder.toString();
                    } else {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(abbrevMarker);
                        stringBuilder.append(str.substring(str.length() - (maxWidth - abbrevMarkerLength)));
                        return stringBuilder.toString();
                    }
                }
            }
        }
        return str;
    }

    public static String abbreviateMiddle(String str, String middle, int length) {
        if (!isEmpty(str)) {
            if (!isEmpty(middle)) {
                if (length < str.length()) {
                    if (length >= middle.length() + 2) {
                        int targetSting = length - middle.length();
                        int startOffset = (targetSting / 2) + (targetSting % 2);
                        int endOffset = str.length() - (targetSting / 2);
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(str.substring(0, startOffset));
                        stringBuilder.append(middle);
                        stringBuilder.append(str.substring(endOffset));
                        return stringBuilder.toString();
                    }
                }
                return str;
            }
        }
        return str;
    }

    public static String difference(String str1, String str2) {
        if (str1 == null) {
            return str2;
        }
        if (str2 == null) {
            return str1;
        }
        int at = indexOfDifference(str1, str2);
        if (at == -1) {
            return "";
        }
        return str2.substring(at);
    }

    public static int indexOfDifference(CharSequence cs1, CharSequence cs2) {
        if (cs1 == cs2) {
            return -1;
        }
        if (cs1 != null) {
            if (cs2 != null) {
                int i = 0;
                while (i < cs1.length() && i < cs2.length()) {
                    if (cs1.charAt(i) != cs2.charAt(i)) {
                        break;
                    }
                    i++;
                }
                if (i >= cs2.length()) {
                    if (i >= cs1.length()) {
                        return -1;
                    }
                }
                return i;
            }
        }
        return 0;
    }

    public static int indexOfDifference(CharSequence... css) {
        if (css != null) {
            if (css.length > 1) {
                int i$;
                int len$;
                boolean anyStringNull = false;
                boolean allStringsNull = true;
                int arrayLen = css.length;
                int shortestStrLen = Integer.MAX_VALUE;
                int longestStrLen = 0;
                for (CharSequence cs : css) {
                    if (cs == null) {
                        anyStringNull = true;
                        shortestStrLen = 0;
                    } else {
                        allStringsNull = false;
                        shortestStrLen = Math.min(cs.length(), shortestStrLen);
                        longestStrLen = Math.max(cs.length(), longestStrLen);
                    }
                }
                if (!allStringsNull) {
                    if (longestStrLen != 0 || anyStringNull) {
                        if (shortestStrLen == 0) {
                            return 0;
                        }
                        len$ = -1;
                        for (i$ = 0; i$ < shortestStrLen; i$++) {
                            char comparisonChar = css[0].charAt(i$);
                            for (int arrayPos = 1; arrayPos < arrayLen; arrayPos++) {
                                if (css[arrayPos].charAt(i$) != comparisonChar) {
                                    len$ = i$;
                                    break;
                                }
                            }
                            if (len$ != -1) {
                                break;
                            }
                        }
                        if (len$ != -1 || shortestStrLen == longestStrLen) {
                            return len$;
                        }
                        return shortestStrLen;
                    }
                }
                return -1;
            }
        }
        return -1;
    }

    public static String getCommonPrefix(String... strs) {
        if (strs != null) {
            if (strs.length != 0) {
                int smallestIndexOfDiff = indexOfDifference(strs);
                if (smallestIndexOfDiff == -1) {
                    if (strs[0] == null) {
                        return "";
                    }
                    return strs[0];
                } else if (smallestIndexOfDiff == 0) {
                    return "";
                } else {
                    return strs[0].substring(0, smallestIndexOfDiff);
                }
            }
        }
        return "";
    }

    @Deprecated
    public static double getJaroWinklerDistance(CharSequence first, CharSequence second) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }
        int[] mtp = matches(first, second);
        double m = (double) mtp[0];
        if (m == 0.0d) {
            return 0.0d;
        }
        double length = (double) first.length();
        Double.isNaN(m);
        Double.isNaN(length);
        length = m / length;
        double length2 = (double) second.length();
        Double.isNaN(m);
        Double.isNaN(length2);
        length += m / length2;
        length2 = (double) mtp[1];
        Double.isNaN(m);
        Double.isNaN(length2);
        length2 = m - length2;
        Double.isNaN(m);
        length = (length + (length2 / m)) / 3.0d;
        if (length < 0.7d) {
            length2 = length;
        } else {
            double d = (double) mtp[3];
            Double.isNaN(d);
            length2 = Math.min(0.1d, 1.0d / d);
            d = (double) mtp[2];
            Double.isNaN(d);
            length2 = ((length2 * d) * (1.0d - length)) + length;
        }
        double round = (double) Math.round(length2 * 100.0d);
        Double.isNaN(round);
        return round / 100.0d;
    }

    private static int[] matches(CharSequence first, CharSequence second) {
        CharSequence max;
        CharSequence min;
        int xi;
        int xn;
        int i;
        if (first.length() > second.length()) {
            max = first;
            min = second;
        } else {
            max = second;
            min = first;
        }
        int range = Math.max((max.length() / 2) - 1, 0);
        int[] matchIndexes = new int[min.length()];
        Arrays.fill(matchIndexes, -1);
        boolean[] matchFlags = new boolean[max.length()];
        int matches = 0;
        for (int mi = 0; mi < min.length(); mi++) {
            char c1 = min.charAt(mi);
            xi = Math.max(mi - range, 0);
            xn = Math.min((mi + range) + 1, max.length());
            while (xi < xn) {
                if (!matchFlags[xi] && c1 == max.charAt(xi)) {
                    matchIndexes[mi] = xi;
                    matchFlags[xi] = true;
                    matches++;
                    break;
                }
                xi++;
            }
        }
        char[] ms1 = new char[matches];
        char[] ms2 = new char[matches];
        xn = 0;
        for (xi = 0; xi < min.length(); xi++) {
            if (matchIndexes[xi] != -1) {
                ms1[xn] = min.charAt(xi);
                xn++;
            }
        }
        xi = 0;
        for (i = 0; i < max.length(); i++) {
            if (matchFlags[i]) {
                ms2[xi] = max.charAt(i);
                xi++;
            }
        }
        i = 0;
        for (xi = 0; xi < ms1.length; xi++) {
            if (ms1[xi] != ms2[xi]) {
                i++;
            }
        }
        xi = 0;
        for (xn = 0; xn < min.length(); xn++) {
            if (first.charAt(xn) != second.charAt(xn)) {
                break;
            }
            xi++;
        }
        CharSequence charSequence = first;
        CharSequence charSequence2 = second;
        return new int[]{matches, i / 2, xi, max.length()};
    }

    public static boolean startsWith(CharSequence str, CharSequence prefix) {
        return startsWith(str, prefix, false);
    }

    public static boolean startsWithIgnoreCase(CharSequence str, CharSequence prefix) {
        return startsWith(str, prefix, true);
    }

    private static boolean startsWith(CharSequence str, CharSequence prefix, boolean ignoreCase) {
        boolean z = false;
        if (str != null) {
            if (prefix != null) {
                if (prefix.length() > str.length()) {
                    return false;
                }
                return CharSequenceUtils.regionMatches(str, ignoreCase, 0, prefix, 0, prefix.length());
            }
        }
        if (str == null && prefix == null) {
            z = true;
        }
        return z;
    }

    public static boolean startsWithAny(CharSequence sequence, CharSequence... searchStrings) {
        if (!isEmpty(sequence)) {
            if (!ArrayUtils.isEmpty((Object[]) searchStrings)) {
                for (CharSequence searchString : searchStrings) {
                    if (startsWith(sequence, searchString)) {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    public static boolean endsWith(CharSequence str, CharSequence suffix) {
        return endsWith(str, suffix, false);
    }

    public static boolean endsWithIgnoreCase(CharSequence str, CharSequence suffix) {
        return endsWith(str, suffix, true);
    }

    private static boolean endsWith(CharSequence str, CharSequence suffix, boolean ignoreCase) {
        boolean z = false;
        if (str != null) {
            if (suffix != null) {
                if (suffix.length() > str.length()) {
                    return false;
                }
                return CharSequenceUtils.regionMatches(str, ignoreCase, str.length() - suffix.length(), suffix, 0, suffix.length());
            }
        }
        if (str == null && suffix == null) {
            z = true;
        }
        return z;
    }

    public static String normalizeSpace(String str) {
        if (isEmpty(str)) {
            return str;
        }
        int size = str.length();
        char[] newChars = new char[size];
        int count = 0;
        int whitespacesCount = 0;
        boolean startWhitespaces = true;
        for (int i = 0; i < size; i++) {
            char actualChar = str.charAt(i);
            if (Character.isWhitespace(actualChar)) {
                if (whitespacesCount == 0 && !startWhitespaces) {
                    int count2 = count + 1;
                    newChars[count] = SPACE.charAt(0);
                    count = count2;
                }
                whitespacesCount++;
            } else {
                startWhitespaces = false;
                int count3 = count + 1;
                newChars[count] = actualChar == Typography.nbsp ? ' ' : actualChar;
                whitespacesCount = 0;
                count = count3;
            }
        }
        if (startWhitespaces) {
            return "";
        }
        return new String(newChars, 0, count - (whitespacesCount > 0 ? 1 : 0)).trim();
    }

    public static boolean endsWithAny(CharSequence sequence, CharSequence... searchStrings) {
        if (!isEmpty(sequence)) {
            if (!ArrayUtils.isEmpty((Object[]) searchStrings)) {
                for (CharSequence searchString : searchStrings) {
                    if (endsWith(sequence, searchString)) {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    private static String appendIfMissing(String str, CharSequence suffix, boolean ignoreCase, CharSequence... suffixes) {
        if (!(str == null || isEmpty(suffix))) {
            if (!endsWith(str, suffix, ignoreCase)) {
                if (suffixes != null && suffixes.length > 0) {
                    for (CharSequence s : suffixes) {
                        if (endsWith(str, s, ignoreCase)) {
                            return str;
                        }
                    }
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append(suffix.toString());
                return stringBuilder.toString();
            }
        }
        return str;
    }

    public static String appendIfMissing(String str, CharSequence suffix, CharSequence... suffixes) {
        return appendIfMissing(str, suffix, false, suffixes);
    }

    public static String appendIfMissingIgnoreCase(String str, CharSequence suffix, CharSequence... suffixes) {
        return appendIfMissing(str, suffix, true, suffixes);
    }

    private static String prependIfMissing(String str, CharSequence prefix, boolean ignoreCase, CharSequence... prefixes) {
        if (!(str == null || isEmpty(prefix))) {
            if (!startsWith(str, prefix, ignoreCase)) {
                if (prefixes != null && prefixes.length > 0) {
                    for (CharSequence p : prefixes) {
                        if (startsWith(str, p, ignoreCase)) {
                            return str;
                        }
                    }
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(prefix.toString());
                stringBuilder.append(str);
                return stringBuilder.toString();
            }
        }
        return str;
    }

    public static String prependIfMissing(String str, CharSequence prefix, CharSequence... prefixes) {
        return prependIfMissing(str, prefix, false, prefixes);
    }

    public static String prependIfMissingIgnoreCase(String str, CharSequence prefix, CharSequence... prefixes) {
        return prependIfMissing(str, prefix, true, prefixes);
    }

    @Deprecated
    public static String toString(byte[] bytes, String charsetName) throws UnsupportedEncodingException {
        return charsetName != null ? new String(bytes, charsetName) : new String(bytes, Charset.defaultCharset());
    }

    public static String toEncodedString(byte[] bytes, Charset charset) {
        return new String(bytes, charset != null ? charset : Charset.defaultCharset());
    }

    public static String wrap(String str, char wrapWith) {
        if (!isEmpty(str)) {
            if (wrapWith != '\u0000') {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(wrapWith);
                stringBuilder.append(str);
                stringBuilder.append(wrapWith);
                return stringBuilder.toString();
            }
        }
        return str;
    }

    public static String wrap(String str, String wrapWith) {
        if (!isEmpty(str)) {
            if (!isEmpty(wrapWith)) {
                return wrapWith.concat(str).concat(wrapWith);
            }
        }
        return str;
    }

    public static String wrapIfMissing(String str, char wrapWith) {
        if (!isEmpty(str)) {
            if (wrapWith != '\u0000') {
                StringBuilder builder = new StringBuilder(str.length() + 2);
                if (str.charAt(0) != wrapWith) {
                    builder.append(wrapWith);
                }
                builder.append(str);
                if (str.charAt(str.length() - 1) != wrapWith) {
                    builder.append(wrapWith);
                }
                return builder.toString();
            }
        }
        return str;
    }

    public static String wrapIfMissing(String str, String wrapWith) {
        if (!isEmpty(str)) {
            if (!isEmpty(wrapWith)) {
                StringBuilder builder = new StringBuilder((str.length() + wrapWith.length()) + wrapWith.length());
                if (!str.startsWith(wrapWith)) {
                    builder.append(wrapWith);
                }
                builder.append(str);
                if (!str.endsWith(wrapWith)) {
                    builder.append(wrapWith);
                }
                return builder.toString();
            }
        }
        return str;
    }

    public static String unwrap(String str, String wrapToken) {
        if (!isEmpty(str)) {
            if (!isEmpty(wrapToken)) {
                if (startsWith(str, wrapToken) && endsWith(str, wrapToken)) {
                    int startIndex = str.indexOf(wrapToken);
                    int endIndex = str.lastIndexOf(wrapToken);
                    int wrapLength = wrapToken.length();
                    if (startIndex != -1 && endIndex != -1) {
                        return str.substring(startIndex + wrapLength, endIndex);
                    }
                }
                return str;
            }
        }
        return str;
    }

    public static String unwrap(String str, char wrapChar) {
        if (!isEmpty(str)) {
            if (wrapChar != '\u0000') {
                if (str.charAt(0) == wrapChar && str.charAt(str.length() - 1) == wrapChar) {
                    int endIndex = str.length() - 1;
                    if (endIndex != -1) {
                        return str.substring(1, endIndex);
                    }
                }
                return str;
            }
        }
        return str;
    }

    public static int[] toCodePoints(CharSequence str) {
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return ArrayUtils.EMPTY_INT_ARRAY;
        }
        String s = str.toString();
        int[] result = new int[s.codePointCount(0, s.length())];
        int index = 0;
        for (int i = 0; i < result.length; i++) {
            result[i] = s.codePointAt(index);
            index += Character.charCount(result[i]);
        }
        return result;
    }
}
