package okhttp3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import okhttp3.internal.Util;
import okhttp3.internal.http.HttpDate;
import okhttp3.internal.publicsuffix.PublicSuffixDatabase;
import org.apache.commons.io.IOUtils;

public final class Cookie {
    private static final Pattern DAY_OF_MONTH_PATTERN = Pattern.compile("(\\d{1,2})[^\\d]*");
    private static final Pattern MONTH_PATTERN = Pattern.compile("(?i)(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec).*");
    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d{1,2}):(\\d{1,2}):(\\d{1,2})[^\\d]*");
    private static final Pattern YEAR_PATTERN = Pattern.compile("(\\d{2,4})[^\\d]*");
    private final String domain;
    private final long expiresAt;
    private final boolean hostOnly;
    private final boolean httpOnly;
    private final String name;
    private final String path;
    private final boolean persistent;
    private final boolean secure;
    private final String value;

    private static long parseExpires(java.lang.String r13, int r14, int r15) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:62:0x0133 in {7, 12, 17, 22, 23, 24, 29, 33, 49, 51, 53, 55, 57, 59, 61} preds:[]
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
        r0 = 0;
        r14 = dateCharacterOffset(r13, r14, r15, r0);
        r1 = -1;
        r2 = -1;
        r3 = -1;
        r4 = -1;
        r5 = -1;
        r6 = -1;
        r7 = TIME_PATTERN;
        r7 = r7.matcher(r13);
    L_0x0011:
        r8 = 2;
        r9 = -1;
        r10 = 1;
        if (r14 >= r15) goto L_0x00a7;
    L_0x0016:
        r11 = r14 + 1;
        r11 = dateCharacterOffset(r13, r11, r15, r10);
        r7.region(r14, r11);
        if (r1 != r9) goto L_0x0047;
    L_0x0021:
        r12 = TIME_PATTERN;
        r12 = r7.usePattern(r12);
        r12 = r12.matches();
        if (r12 == 0) goto L_0x0047;
    L_0x002d:
        r9 = r7.group(r10);
        r1 = java.lang.Integer.parseInt(r9);
        r8 = r7.group(r8);
        r2 = java.lang.Integer.parseInt(r8);
        r8 = 3;
        r8 = r7.group(r8);
        r3 = java.lang.Integer.parseInt(r8);
        goto L_0x009f;
        if (r4 != r9) goto L_0x005f;
    L_0x004a:
        r8 = DAY_OF_MONTH_PATTERN;
        r8 = r7.usePattern(r8);
        r8 = r8.matches();
        if (r8 == 0) goto L_0x005f;
    L_0x0056:
        r8 = r7.group(r10);
        r4 = java.lang.Integer.parseInt(r8);
        goto L_0x009f;
        if (r5 != r9) goto L_0x0086;
    L_0x0062:
        r8 = MONTH_PATTERN;
        r8 = r7.usePattern(r8);
        r8 = r8.matches();
        if (r8 == 0) goto L_0x0086;
    L_0x006e:
        r8 = r7.group(r10);
        r9 = java.util.Locale.US;
        r8 = r8.toLowerCase(r9);
        r9 = MONTH_PATTERN;
        r9 = r9.pattern();
        r9 = r9.indexOf(r8);
        r9 = r9 / 4;
        r5 = r9;
        goto L_0x009f;
        if (r6 != r9) goto L_0x009e;
    L_0x0089:
        r8 = YEAR_PATTERN;
        r8 = r7.usePattern(r8);
        r8 = r8.matches();
        if (r8 == 0) goto L_0x009e;
    L_0x0095:
        r8 = r7.group(r10);
        r6 = java.lang.Integer.parseInt(r8);
        goto L_0x009f;
    L_0x009f:
        r8 = r11 + 1;
        r14 = dateCharacterOffset(r13, r8, r15, r0);
        goto L_0x0011;
    L_0x00a7:
        r11 = 70;
        if (r6 < r11) goto L_0x00b2;
    L_0x00ab:
        r11 = 99;
        if (r6 > r11) goto L_0x00b2;
    L_0x00af:
        r6 = r6 + 1900;
    L_0x00b2:
        if (r6 < 0) goto L_0x00bb;
    L_0x00b4:
        r11 = 69;
        if (r6 > r11) goto L_0x00bb;
    L_0x00b8:
        r6 = r6 + 2000;
    L_0x00bb:
        r11 = 1601; // 0x641 float:2.243E-42 double:7.91E-321;
        if (r6 < r11) goto L_0x012c;
    L_0x00bf:
        if (r5 == r9) goto L_0x0125;
    L_0x00c1:
        if (r4 < r10) goto L_0x011d;
    L_0x00c3:
        r9 = 31;
        if (r4 > r9) goto L_0x011d;
    L_0x00c7:
        if (r1 < 0) goto L_0x0115;
    L_0x00c9:
        r9 = 23;
        if (r1 > r9) goto L_0x0115;
    L_0x00cd:
        if (r2 < 0) goto L_0x010d;
    L_0x00cf:
        r9 = 59;
        if (r2 > r9) goto L_0x010d;
    L_0x00d3:
        if (r3 < 0) goto L_0x0106;
    L_0x00d5:
        if (r3 > r9) goto L_0x0106;
    L_0x00d7:
        r9 = new java.util.GregorianCalendar;
        r11 = okhttp3.internal.Util.UTC;
        r9.<init>(r11);
        r9.setLenient(r0);
        r9.set(r10, r6);
        r10 = r5 + -1;
        r9.set(r8, r10);
        r8 = 5;
        r9.set(r8, r4);
        r8 = 11;
        r9.set(r8, r1);
        r8 = 12;
        r9.set(r8, r2);
        r8 = 13;
        r9.set(r8, r3);
        r8 = 14;
        r9.set(r8, r0);
        r10 = r9.getTimeInMillis();
        return r10;
        r0 = new java.lang.IllegalArgumentException;
        r0.<init>();
        throw r0;
        r0 = new java.lang.IllegalArgumentException;
        r0.<init>();
        throw r0;
        r0 = new java.lang.IllegalArgumentException;
        r0.<init>();
        throw r0;
        r0 = new java.lang.IllegalArgumentException;
        r0.<init>();
        throw r0;
        r0 = new java.lang.IllegalArgumentException;
        r0.<init>();
        throw r0;
        r0 = new java.lang.IllegalArgumentException;
        r0.<init>();
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.Cookie.parseExpires(java.lang.String, int, int):long");
    }

    private Cookie(String name, String value, long expiresAt, String domain, String path, boolean secure, boolean httpOnly, boolean hostOnly, boolean persistent) {
        this.name = name;
        this.value = value;
        this.expiresAt = expiresAt;
        this.domain = domain;
        this.path = path;
        this.secure = secure;
        this.httpOnly = httpOnly;
        this.hostOnly = hostOnly;
        this.persistent = persistent;
    }

    Cookie(Cookie$Builder builder) {
        if (builder.name == null) {
            throw new NullPointerException("builder.name == null");
        } else if (builder.value == null) {
            throw new NullPointerException("builder.value == null");
        } else if (builder.domain != null) {
            this.name = builder.name;
            this.value = builder.value;
            this.expiresAt = builder.expiresAt;
            this.domain = builder.domain;
            this.path = builder.path;
            this.secure = builder.secure;
            this.httpOnly = builder.httpOnly;
            this.persistent = builder.persistent;
            this.hostOnly = builder.hostOnly;
        } else {
            throw new NullPointerException("builder.domain == null");
        }
    }

    public String name() {
        return this.name;
    }

    public String value() {
        return this.value;
    }

    public boolean persistent() {
        return this.persistent;
    }

    public long expiresAt() {
        return this.expiresAt;
    }

    public boolean hostOnly() {
        return this.hostOnly;
    }

    public String domain() {
        return this.domain;
    }

    public String path() {
        return this.path;
    }

    public boolean httpOnly() {
        return this.httpOnly;
    }

    public boolean secure() {
        return this.secure;
    }

    public boolean matches(HttpUrl url) {
        boolean domainMatch;
        if (this.hostOnly) {
            domainMatch = url.host().equals(this.domain);
        } else {
            domainMatch = domainMatch(url.host(), this.domain);
        }
        if (!domainMatch || !pathMatch(url, this.path)) {
            return false;
        }
        if (!this.secure || url.isHttps()) {
            return true;
        }
        return false;
    }

    private static boolean domainMatch(String urlHost, String domain) {
        if (urlHost.equals(domain)) {
            return true;
        }
        if (urlHost.endsWith(domain)) {
            if (urlHost.charAt((urlHost.length() - domain.length()) - 1) == '.') {
                if (!Util.verifyAsIpAddress(urlHost)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean pathMatch(HttpUrl url, String path) {
        String urlPath = url.encodedPath();
        if (urlPath.equals(path)) {
            return true;
        }
        if (urlPath.startsWith(path)) {
            if (path.endsWith("/") || urlPath.charAt(path.length()) == IOUtils.DIR_SEPARATOR_UNIX) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    public static Cookie parse(HttpUrl url, String setCookie) {
        return parse(System.currentTimeMillis(), url, setCookie);
    }

    @Nullable
    static Cookie parse(long currentTimeMillis, HttpUrl url, String setCookie) {
        String str = setCookie;
        int limit = setCookie.length();
        char c = ';';
        int cookiePairEnd = Util.delimiterOffset(str, 0, limit, ';');
        char c2 = '=';
        int pairEqualsSign = Util.delimiterOffset(str, 0, cookiePairEnd, '=');
        if (pairEqualsSign == cookiePairEnd) {
            return null;
        }
        String cookieName = Util.trimSubstring(str, 0, pairEqualsSign);
        if (!cookieName.isEmpty()) {
            if (Util.indexOfControlOrNonAscii(cookieName) == -1) {
                String cookieValue = Util.trimSubstring(str, pairEqualsSign + 1, cookiePairEnd);
                if (Util.indexOfControlOrNonAscii(cookieValue) != -1) {
                    return null;
                }
                String attributeValue;
                long expiresAt;
                String domain;
                int pos;
                long expiresAt2 = HttpDate.MAX_DATE;
                String domain2 = null;
                String path = null;
                long deltaSeconds = -1;
                boolean secureOnly = false;
                boolean httpOnly = false;
                boolean hostOnly = true;
                boolean persistent = false;
                int pos2 = cookiePairEnd + 1;
                while (pos2 < limit) {
                    int attributePairEnd = Util.delimiterOffset(str, pos2, limit, c);
                    int attributeEqualsSign = Util.delimiterOffset(str, pos2, attributePairEnd, c2);
                    String attributeName = Util.trimSubstring(str, pos2, attributeEqualsSign);
                    if (attributeEqualsSign < attributePairEnd) {
                        attributeValue = Util.trimSubstring(str, attributeEqualsSign + 1, attributePairEnd);
                    } else {
                        attributeValue = "";
                    }
                    if (attributeName.equalsIgnoreCase("expires")) {
                        try {
                            expiresAt2 = parseExpires(attributeValue, 0, attributeValue.length());
                            persistent = true;
                        } catch (IllegalArgumentException e) {
                        }
                    } else if (attributeName.equalsIgnoreCase("max-age")) {
                        try {
                            deltaSeconds = parseMaxAge(attributeValue);
                            persistent = true;
                        } catch (NumberFormatException e2) {
                        }
                    } else if (attributeName.equalsIgnoreCase("domain")) {
                        try {
                            hostOnly = false;
                            domain2 = parseDomain(attributeValue);
                        } catch (IllegalArgumentException e3) {
                        }
                    } else if (attributeName.equalsIgnoreCase("path")) {
                        path = attributeValue;
                    } else if (attributeName.equalsIgnoreCase("secure")) {
                        secureOnly = true;
                    } else if (attributeName.equalsIgnoreCase("httponly")) {
                        httpOnly = true;
                    }
                    pos2 = attributePairEnd + 1;
                    c = ';';
                    c2 = '=';
                }
                if (deltaSeconds == Long.MIN_VALUE) {
                    expiresAt = Long.MIN_VALUE;
                } else if (deltaSeconds != -1) {
                    long deltaMilliseconds;
                    if (deltaSeconds <= 9223372036854775L) {
                        deltaMilliseconds = 1000 * deltaSeconds;
                    } else {
                        deltaMilliseconds = Long.MAX_VALUE;
                    }
                    expiresAt2 = currentTimeMillis + deltaMilliseconds;
                    if (expiresAt2 >= currentTimeMillis) {
                        if (expiresAt2 <= HttpDate.MAX_DATE) {
                            expiresAt = expiresAt2;
                        }
                    }
                    expiresAt = HttpDate.MAX_DATE;
                } else {
                    expiresAt = expiresAt2;
                }
                attributeValue = url.host();
                if (domain2 == null) {
                    domain = attributeValue;
                } else if (!domainMatch(attributeValue, domain2)) {
                    return null;
                } else {
                    domain = domain2;
                }
                if (attributeValue.length() != domain.length()) {
                    if (PublicSuffixDatabase.get().getEffectiveTldPlusOne(domain) == null) {
                        return null;
                    }
                }
                if (path != null) {
                    if (path.startsWith("/")) {
                        pos = path;
                        return new Cookie(cookieName, cookieValue, expiresAt, domain, pos, secureOnly, httpOnly, hostOnly, persistent);
                    }
                }
                String encodedPath = url.encodedPath();
                int lastSlash = encodedPath.lastIndexOf(47);
                pos = lastSlash != 0 ? encodedPath.substring(0, lastSlash) : "/";
                return new Cookie(cookieName, cookieValue, expiresAt, domain, pos, secureOnly, httpOnly, hostOnly, persistent);
            }
        }
        return null;
    }

    private static int dateCharacterOffset(String input, int pos, int limit, boolean invert) {
        for (int i = pos; i < limit; i++) {
            boolean dateCharacter;
            int c = input.charAt(i);
            if ((c >= 32 || c == 9) && c < 127 && ((c < 48 || c > 57) && ((c < 97 || c > 122) && (c < 65 || c > 90)))) {
                if (c != 58) {
                    dateCharacter = false;
                    if (dateCharacter == (invert ^ 1)) {
                        return i;
                    }
                }
            }
            dateCharacter = true;
            if (dateCharacter == (invert ^ 1)) {
                return i;
            }
        }
        return limit;
    }

    private static long parseMaxAge(String s) {
        long j = Long.MIN_VALUE;
        try {
            long parsed = Long.parseLong(s);
            if (parsed > 0) {
                j = parsed;
            }
            return j;
        } catch (NumberFormatException e) {
            if (s.matches("-?\\d+")) {
                if (!s.startsWith("-")) {
                    j = Long.MAX_VALUE;
                }
                return j;
            }
            throw e;
        }
    }

    private static String parseDomain(String s) {
        if (s.endsWith(".")) {
            throw new IllegalArgumentException();
        }
        if (s.startsWith(".")) {
            s = s.substring(1);
        }
        String canonicalDomain = Util.canonicalizeHost(s);
        if (canonicalDomain != null) {
            return canonicalDomain;
        }
        throw new IllegalArgumentException();
    }

    public static List<Cookie> parseAll(HttpUrl url, Headers headers) {
        List<String> cookieStrings = headers.values("Set-Cookie");
        List<Cookie> cookies = null;
        int size = cookieStrings.size();
        for (int i = 0; i < size; i++) {
            Cookie cookie = parse(url, (String) cookieStrings.get(i));
            if (cookie != null) {
                if (cookies == null) {
                    cookies = new ArrayList();
                }
                cookies.add(cookie);
            }
        }
        if (cookies != null) {
            return Collections.unmodifiableList(cookies);
        }
        return Collections.emptyList();
    }

    public String toString() {
        return toString(false);
    }

    String toString(boolean forObsoleteRfc2965) {
        StringBuilder result = new StringBuilder();
        result.append(this.name);
        result.append('=');
        result.append(this.value);
        if (this.persistent) {
            if (this.expiresAt == Long.MIN_VALUE) {
                result.append("; max-age=0");
            } else {
                result.append("; expires=");
                result.append(HttpDate.format(new Date(this.expiresAt)));
            }
        }
        if (!this.hostOnly) {
            result.append("; domain=");
            if (forObsoleteRfc2965) {
                result.append(".");
            }
            result.append(this.domain);
        }
        result.append("; path=");
        result.append(this.path);
        if (this.secure) {
            result.append("; secure");
        }
        if (this.httpOnly) {
            result.append("; httponly");
        }
        return result.toString();
    }

    public boolean equals(@Nullable Object other) {
        boolean z = false;
        if (!(other instanceof Cookie)) {
            return false;
        }
        Cookie that = (Cookie) other;
        if (that.name.equals(this.name)) {
            if (that.value.equals(this.value)) {
                if (that.domain.equals(this.domain)) {
                    if (that.path.equals(this.path) && that.expiresAt == this.expiresAt && that.secure == this.secure && that.httpOnly == this.httpOnly && that.persistent == this.persistent && that.hostOnly == this.hostOnly) {
                        z = true;
                        return z;
                    }
                }
            }
        }
        return z;
    }

    public int hashCode() {
        int hashCode = ((((((((17 * 31) + this.name.hashCode()) * 31) + this.value.hashCode()) * 31) + this.domain.hashCode()) * 31) + this.path.hashCode()) * 31;
        long j = this.expiresAt;
        return ((((((((hashCode + ((int) (j ^ (j >>> 32)))) * 31) + (this.secure ^ 1)) * 31) + (this.httpOnly ^ 1)) * 31) + (this.persistent ^ 1)) * 31) + (this.hostOnly ^ 1);
    }
}
