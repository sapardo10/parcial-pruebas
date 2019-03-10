package okhttp3.internal.http;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.Challenge;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.Headers;
import okhttp3.Headers$Builder;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.Util;

public final class HttpHeaders {
    private static final Pattern PARAMETER = Pattern.compile(" +([^ \"=]*)=(:?\"([^\"]*)\"|([^ \"=]*)) *(:?,|$)");
    private static final String QUOTED_STRING = "\"([^\"]*)\"";
    private static final String TOKEN = "([^ \"=]*)";

    private HttpHeaders() {
    }

    public static long contentLength(Response response) {
        return contentLength(response.headers());
    }

    public static long contentLength(Headers headers) {
        return stringToLong(headers.get("Content-Length"));
    }

    private static long stringToLong(String s) {
        long j = -1;
        if (s == null) {
            return -1;
        }
        try {
            j = Long.parseLong(s);
            return j;
        } catch (NumberFormatException e) {
            return j;
        }
    }

    public static boolean varyMatches(Response cachedResponse, Headers cachedRequest, Request newRequest) {
        for (String field : varyFields(cachedResponse)) {
            if (!Util.equal(cachedRequest.values(field), newRequest.headers(field))) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasVaryAll(Response response) {
        return hasVaryAll(response.headers());
    }

    public static boolean hasVaryAll(Headers responseHeaders) {
        return varyFields(responseHeaders).contains("*");
    }

    private static Set<String> varyFields(Response response) {
        return varyFields(response.headers());
    }

    public static Set<String> varyFields(Headers responseHeaders) {
        Set<String> result = Collections.emptySet();
        int size = responseHeaders.size();
        for (int i = 0; i < size; i++) {
            if ("Vary".equalsIgnoreCase(responseHeaders.name(i))) {
                String value = responseHeaders.value(i);
                if (result.isEmpty()) {
                    result = new TreeSet(String.CASE_INSENSITIVE_ORDER);
                }
                for (String varyField : value.split(",")) {
                    result.add(varyField.trim());
                }
            }
        }
        return result;
    }

    public static Headers varyHeaders(Response response) {
        return varyHeaders(response.networkResponse().request().headers(), response.headers());
    }

    public static Headers varyHeaders(Headers requestHeaders, Headers responseHeaders) {
        Set<String> varyFields = varyFields(responseHeaders);
        if (varyFields.isEmpty()) {
            return new Headers$Builder().build();
        }
        Headers$Builder result = new Headers$Builder();
        int size = requestHeaders.size();
        for (int i = 0; i < size; i++) {
            String fieldName = requestHeaders.name(i);
            if (varyFields.contains(fieldName)) {
                result.add(fieldName, requestHeaders.value(i));
            }
        }
        return result.build();
    }

    public static List<Challenge> parseChallenges(Headers responseHeaders, String challengeHeader) {
        List<Challenge> challenges = new ArrayList();
        List<String> authenticationHeaders = responseHeaders.values(challengeHeader);
        Iterator it = authenticationHeaders.iterator();
        while (it.hasNext()) {
            String header = (String) it.next();
            int index = header.indexOf(32);
            if (index != -1) {
                List<String> authenticationHeaders2;
                Iterator it2;
                String scheme = header.substring(0, index);
                Matcher matcher = PARAMETER.matcher(header);
                String realm = null;
                String charset = null;
                int i = index;
                while (matcher.find(i)) {
                    authenticationHeaders2 = authenticationHeaders;
                    if (header.regionMatches(true, matcher.start(1), "realm", 0, 5)) {
                        realm = matcher.group(3);
                        it2 = it;
                    } else {
                        it2 = it;
                        if (header.regionMatches(true, matcher.start(1), "charset", 0, 7) != null) {
                            charset = matcher.group(3);
                        }
                    }
                    if (realm != null && charset != null) {
                        break;
                    }
                    i = matcher.end();
                    it = it2;
                    authenticationHeaders = authenticationHeaders2;
                }
                authenticationHeaders2 = authenticationHeaders;
                it2 = it;
                if (realm == null) {
                    it = it2;
                    authenticationHeaders = authenticationHeaders2;
                } else {
                    authenticationHeaders = new Challenge(scheme, realm);
                    if (charset != null) {
                        if (charset.equalsIgnoreCase("UTF-8")) {
                            authenticationHeaders = authenticationHeaders.withCharset(Util.UTF_8);
                        } else {
                            it = it2;
                            authenticationHeaders = authenticationHeaders2;
                        }
                    }
                    challenges.add(authenticationHeaders);
                    it = it2;
                    authenticationHeaders = authenticationHeaders2;
                }
            }
        }
        return challenges;
    }

    public static void receiveHeaders(CookieJar cookieJar, HttpUrl url, Headers headers) {
        if (cookieJar != CookieJar.NO_COOKIES) {
            List<Cookie> cookies = Cookie.parseAll(url, headers);
            if (!cookies.isEmpty()) {
                cookieJar.saveFromResponse(url, cookies);
            }
        }
    }

    public static boolean hasBody(Response response) {
        if (response.request().method().equals("HEAD")) {
            return false;
        }
        int responseCode = response.code();
        if ((responseCode < 100 || responseCode >= 200) && responseCode != 204 && responseCode != 304) {
            return true;
        }
        if (contentLength(response) == -1) {
            if (!"chunked".equalsIgnoreCase(response.header("Transfer-Encoding"))) {
                return false;
            }
        }
        return true;
    }

    public static int skipUntil(String input, int pos, String characters) {
        while (pos < input.length()) {
            if (characters.indexOf(input.charAt(pos)) != -1) {
                break;
            }
            pos++;
        }
        return pos;
    }

    public static int skipWhitespace(String input, int pos) {
        while (pos < input.length()) {
            char c = input.charAt(pos);
            if (c != ' ' && c != '\t') {
                break;
            }
            pos++;
        }
        return pos;
    }

    public static int parseSeconds(String value, int defaultValue) {
        try {
            long seconds = Long.parseLong(value);
            if (seconds > 2147483647L) {
                return Integer.MAX_VALUE;
            }
            if (seconds < 0) {
                return 0;
            }
            return (int) seconds;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
