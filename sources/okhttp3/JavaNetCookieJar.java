package okhttp3;

import java.io.IOException;
import java.net.CookieHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import okhttp3.internal.Util;
import okhttp3.internal.platform.Platform;

public final class JavaNetCookieJar implements CookieJar {
    private final CookieHandler cookieHandler;

    public JavaNetCookieJar(CookieHandler cookieHandler) {
        this.cookieHandler = cookieHandler;
    }

    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        if (this.cookieHandler != null) {
            List<String> cookieStrings = new ArrayList();
            for (Cookie cookie : cookies) {
                cookieStrings.add(cookie.toString(true));
            }
            try {
                this.cookieHandler.put(url.uri(), Collections.singletonMap("Set-Cookie", cookieStrings));
            } catch (IOException e) {
                Platform platform = Platform.get();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Saving cookies failed for ");
                stringBuilder.append(url.resolve("/..."));
                platform.log(5, stringBuilder.toString(), e);
            }
        }
    }

    public List<Cookie> loadForRequest(HttpUrl url) {
        try {
            List<Cookie> unmodifiableList;
            List<Cookie> cookies = null;
            for (Entry<String, List<String>> entry : this.cookieHandler.get(url.uri(), Collections.emptyMap()).entrySet()) {
                String key = (String) entry.getKey();
                if (!"Cookie".equalsIgnoreCase(key)) {
                    if ("Cookie2".equalsIgnoreCase(key)) {
                    }
                }
                if (!((List) entry.getValue()).isEmpty()) {
                    for (String header : (List) entry.getValue()) {
                        if (cookies == null) {
                            cookies = new ArrayList();
                        }
                        cookies.addAll(decodeHeaderAsJavaNetCookies(url, header));
                    }
                }
            }
            if (cookies != null) {
                unmodifiableList = Collections.unmodifiableList(cookies);
            } else {
                unmodifiableList = Collections.emptyList();
            }
            return unmodifiableList;
        } catch (IOException e) {
            Platform platform = Platform.get();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Loading cookies failed for ");
            stringBuilder.append(url.resolve("/..."));
            platform.log(5, stringBuilder.toString(), e);
            return Collections.emptyList();
        }
    }

    private List<Cookie> decodeHeaderAsJavaNetCookies(HttpUrl url, String header) {
        List<Cookie> result = new ArrayList();
        int pos = 0;
        int limit = header.length();
        while (pos < limit) {
            int pairEnd = Util.delimiterOffset(header, pos, limit, ";,");
            int equalsSign = Util.delimiterOffset(header, pos, pairEnd, 61);
            String name = Util.trimSubstring(header, pos, equalsSign);
            if (!name.startsWith("$")) {
                String value;
                if (equalsSign < pairEnd) {
                    value = Util.trimSubstring(header, equalsSign + 1, pairEnd);
                } else {
                    value = "";
                }
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }
                result.add(new Cookie$Builder().name(name).value(value).domain(url.host()).build());
            }
            pos = pairEnd + 1;
        }
        return result;
    }
}
