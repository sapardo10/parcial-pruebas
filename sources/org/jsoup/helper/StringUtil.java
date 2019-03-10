package org.jsoup.helper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import org.apache.commons.lang3.StringUtils;

public final class StringUtil {
    private static final int MaxCachedBuilderSize = 8192;
    static final String[] padding = new String[]{"", StringUtils.SPACE, "  ", "   ", "    ", "     ", "      ", "       ", "        ", "         ", "          ", "           ", "            ", "             ", "              ", "               ", "                ", "                 ", "                  ", "                   ", "                    "};
    private static final ThreadLocal<StringBuilder> stringLocal = new C11811();

    /* renamed from: org.jsoup.helper.StringUtil$1 */
    class C11811 extends ThreadLocal<StringBuilder> {
        C11811() {
        }

        protected StringBuilder initialValue() {
            return new StringBuilder(8192);
        }
    }

    public static java.lang.String padding(int r3) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:12:0x0023 in {4, 7, 9, 11} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        if (r3 < 0) goto L_0x001b;
    L_0x0002:
        r0 = padding;
        r1 = r0.length;
        if (r3 >= r1) goto L_0x000a;
    L_0x0007:
        r0 = r0[r3];
        return r0;
    L_0x000a:
        r0 = new char[r3];
        r1 = 0;
    L_0x000d:
        if (r1 >= r3) goto L_0x0016;
    L_0x000f:
        r2 = 32;
        r0[r1] = r2;
        r1 = r1 + 1;
        goto L_0x000d;
    L_0x0016:
        r1 = java.lang.String.valueOf(r0);
        return r1;
    L_0x001b:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "width must be > 0";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jsoup.helper.StringUtil.padding(int):java.lang.String");
    }

    public static String join(Collection strings, String sep) {
        return join(strings.iterator(), sep);
    }

    public static String join(Iterator strings, String sep) {
        if (!strings.hasNext()) {
            return "";
        }
        String start = strings.next().toString();
        if (!strings.hasNext()) {
            return start;
        }
        StringBuilder sb = new StringBuilder(64).append(start);
        while (strings.hasNext()) {
            sb.append(sep);
            sb.append(strings.next());
        }
        return sb.toString();
    }

    public static String join(String[] strings, String sep) {
        return join(Arrays.asList(strings), sep);
    }

    public static boolean isBlank(String string) {
        if (string != null) {
            if (string.length() != 0) {
                int l = string.length();
                for (int i = 0; i < l; i++) {
                    if (!isWhitespace(string.codePointAt(i))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return true;
    }

    public static boolean isNumeric(String string) {
        if (string != null) {
            if (string.length() != 0) {
                int l = string.length();
                for (int i = 0; i < l; i++) {
                    if (!Character.isDigit(string.codePointAt(i))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public static boolean isWhitespace(int c) {
        if (!(c == 32 || c == 9 || c == 10 || c == 12)) {
            if (c != 13) {
                return false;
            }
        }
        return true;
    }

    public static boolean isActuallyWhitespace(int c) {
        if (!(c == 32 || c == 9 || c == 10 || c == 12 || c == 13)) {
            if (c != 160) {
                return false;
            }
        }
        return true;
    }

    public static boolean isInvisibleChar(int c) {
        return Character.getType(c) == 16 && (c == 8203 || c == 8204 || c == 8205 || c == 173);
    }

    public static String normaliseWhitespace(String string) {
        StringBuilder sb = stringBuilder();
        appendNormalisedWhitespace(sb, string, false);
        return sb.toString();
    }

    public static void appendNormalisedWhitespace(StringBuilder accum, String string, boolean stripLeading) {
        boolean lastWasWhite = false;
        boolean reachedNonWhite = false;
        int len = string.length();
        int i = 0;
        while (i < len) {
            int c = string.codePointAt(i);
            if (isActuallyWhitespace(c)) {
                if (stripLeading) {
                    if (reachedNonWhite) {
                    }
                }
                if (!lastWasWhite) {
                    accum.append(' ');
                    lastWasWhite = true;
                }
            } else if (!isInvisibleChar(c)) {
                accum.appendCodePoint(c);
                lastWasWhite = false;
                reachedNonWhite = true;
            }
            i += Character.charCount(c);
        }
    }

    public static boolean in(String needle, String... haystack) {
        for (String equals : haystack) {
            if (equals.equals(needle)) {
                return true;
            }
        }
        return false;
    }

    public static boolean inSorted(String needle, String[] haystack) {
        return Arrays.binarySearch(haystack, needle) >= 0;
    }

    public static URL resolve(URL base, String relUrl) throws MalformedURLException {
        if (relUrl.startsWith("?")) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(base.getPath());
            stringBuilder.append(relUrl);
            relUrl = stringBuilder.toString();
        }
        if (relUrl.indexOf(46) == 0 && base.getFile().indexOf(47) != 0) {
            String protocol = base.getProtocol();
            String host = base.getHost();
            int port = base.getPort();
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("/");
            stringBuilder2.append(base.getFile());
            base = new URL(protocol, host, port, stringBuilder2.toString());
        }
        return new URL(base, relUrl);
    }

    public static String resolve(String baseUrl, String relUrl) {
        try {
            try {
                return resolve(new URL(baseUrl), relUrl).toExternalForm();
            } catch (MalformedURLException e) {
                return "";
            }
        } catch (MalformedURLException e2) {
            return new URL(relUrl).toExternalForm();
        }
    }

    public static StringBuilder stringBuilder() {
        StringBuilder sb = (StringBuilder) stringLocal.get();
        if (sb.length() > 8192) {
            sb = new StringBuilder(8192);
            stringLocal.set(sb);
            return sb;
        }
        sb.delete(0, sb.length());
        return sb;
    }
}
