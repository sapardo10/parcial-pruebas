package okhttp3;

import android.support.v4.internal.view.SupportMenu;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import kotlin.text.Typography;
import okhttp3.internal.Util;
import okhttp3.internal.publicsuffix.PublicSuffixDatabase;
import okio.Buffer;
import org.apache.commons.io.IOUtils;

public final class HttpUrl {
    static final String FORM_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#&!$(),~";
    static final String FRAGMENT_ENCODE_SET = "";
    static final String FRAGMENT_ENCODE_SET_URI = " \"#<>\\^`{|}";
    private static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    static final String PASSWORD_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#";
    static final String PATH_SEGMENT_ENCODE_SET = " \"<>^`{}|/\\?#";
    static final String PATH_SEGMENT_ENCODE_SET_URI = "[]";
    static final String QUERY_COMPONENT_ENCODE_SET = " !\"#$&'(),/:;<=>?@[]\\^`{|}~";
    static final String QUERY_COMPONENT_ENCODE_SET_URI = "\\^`{|}";
    static final String QUERY_COMPONENT_REENCODE_SET = " \"'<>#&=";
    static final String QUERY_ENCODE_SET = " \"'<>#";
    static final String USERNAME_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#";
    @Nullable
    private final String fragment;
    final String host;
    private final String password;
    private final List<String> pathSegments;
    final int port;
    @Nullable
    private final List<String> queryNamesAndValues;
    final String scheme;
    private final String url;
    private final String username;

    public static final class Builder {
        @Nullable
        String encodedFragment;
        String encodedPassword = "";
        final List<String> encodedPathSegments = new ArrayList();
        @Nullable
        List<String> encodedQueryNamesAndValues;
        String encodedUsername = "";
        @Nullable
        String host;
        int port = -1;
        @Nullable
        String scheme;

        public Builder() {
            this.encodedPathSegments.add("");
        }

        public Builder scheme(String scheme) {
            if (scheme != null) {
                if (scheme.equalsIgnoreCase("http")) {
                    this.scheme = "http";
                } else if (scheme.equalsIgnoreCase("https")) {
                    this.scheme = "https";
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("unexpected scheme: ");
                    stringBuilder.append(scheme);
                    throw new IllegalArgumentException(stringBuilder.toString());
                }
                return this;
            }
            throw new NullPointerException("scheme == null");
        }

        public Builder username(String username) {
            if (username != null) {
                this.encodedUsername = HttpUrl.canonicalize(username, " \"':;<=>@[]^`{}|/\\?#", false, false, false, true);
                return this;
            }
            throw new NullPointerException("username == null");
        }

        public Builder encodedUsername(String encodedUsername) {
            if (encodedUsername != null) {
                this.encodedUsername = HttpUrl.canonicalize(encodedUsername, " \"':;<=>@[]^`{}|/\\?#", true, false, false, true);
                return this;
            }
            throw new NullPointerException("encodedUsername == null");
        }

        public Builder password(String password) {
            if (password != null) {
                this.encodedPassword = HttpUrl.canonicalize(password, " \"':;<=>@[]^`{}|/\\?#", false, false, false, true);
                return this;
            }
            throw new NullPointerException("password == null");
        }

        public Builder encodedPassword(String encodedPassword) {
            if (encodedPassword != null) {
                this.encodedPassword = HttpUrl.canonicalize(encodedPassword, " \"':;<=>@[]^`{}|/\\?#", true, false, false, true);
                return this;
            }
            throw new NullPointerException("encodedPassword == null");
        }

        public Builder host(String host) {
            if (host != null) {
                String encoded = canonicalizeHost(host, null, host.length());
                if (encoded != null) {
                    this.host = encoded;
                    return this;
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("unexpected host: ");
                stringBuilder.append(host);
                throw new IllegalArgumentException(stringBuilder.toString());
            }
            throw new NullPointerException("host == null");
        }

        public Builder port(int port) {
            if (port <= 0 || port > SupportMenu.USER_MASK) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("unexpected port: ");
                stringBuilder.append(port);
                throw new IllegalArgumentException(stringBuilder.toString());
            }
            this.port = port;
            return this;
        }

        int effectivePort() {
            int i = this.port;
            return i != -1 ? i : HttpUrl.defaultPort(this.scheme);
        }

        public Builder addPathSegment(String pathSegment) {
            if (pathSegment != null) {
                push(pathSegment, 0, pathSegment.length(), false, false);
                return this;
            }
            throw new NullPointerException("pathSegment == null");
        }

        public Builder addPathSegments(String pathSegments) {
            if (pathSegments != null) {
                return addPathSegments(pathSegments, false);
            }
            throw new NullPointerException("pathSegments == null");
        }

        public Builder addEncodedPathSegment(String encodedPathSegment) {
            if (encodedPathSegment != null) {
                push(encodedPathSegment, 0, encodedPathSegment.length(), false, true);
                return this;
            }
            throw new NullPointerException("encodedPathSegment == null");
        }

        public Builder addEncodedPathSegments(String encodedPathSegments) {
            if (encodedPathSegments != null) {
                return addPathSegments(encodedPathSegments, true);
            }
            throw new NullPointerException("encodedPathSegments == null");
        }

        private Builder addPathSegments(String pathSegments, boolean alreadyEncoded) {
            int offset = 0;
            while (true) {
                int segmentEnd = Util.delimiterOffset(pathSegments, offset, pathSegments.length(), "/\\");
                push(pathSegments, offset, segmentEnd, segmentEnd < pathSegments.length(), alreadyEncoded);
                offset = segmentEnd + 1;
                if (offset > pathSegments.length()) {
                    return this;
                }
            }
        }

        public Builder setPathSegment(int index, String pathSegment) {
            if (pathSegment != null) {
                String canonicalPathSegment = HttpUrl.canonicalize(pathSegment, 0, pathSegment.length(), HttpUrl.PATH_SEGMENT_ENCODE_SET, false, false, false, true, null);
                if (isDot(canonicalPathSegment) || isDotDot(canonicalPathSegment)) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("unexpected path segment: ");
                    stringBuilder.append(pathSegment);
                    throw new IllegalArgumentException(stringBuilder.toString());
                }
                this.encodedPathSegments.set(index, canonicalPathSegment);
                return this;
            }
            throw new NullPointerException("pathSegment == null");
        }

        public Builder setEncodedPathSegment(int index, String encodedPathSegment) {
            if (encodedPathSegment != null) {
                String canonicalPathSegment = HttpUrl.canonicalize(encodedPathSegment, 0, encodedPathSegment.length(), HttpUrl.PATH_SEGMENT_ENCODE_SET, true, false, false, true, null);
                this.encodedPathSegments.set(index, canonicalPathSegment);
                if (!isDot(canonicalPathSegment) && !isDotDot(canonicalPathSegment)) {
                    return this;
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("unexpected path segment: ");
                stringBuilder.append(encodedPathSegment);
                throw new IllegalArgumentException(stringBuilder.toString());
            }
            throw new NullPointerException("encodedPathSegment == null");
        }

        public Builder removePathSegment(int index) {
            this.encodedPathSegments.remove(index);
            if (this.encodedPathSegments.isEmpty()) {
                this.encodedPathSegments.add("");
            }
            return this;
        }

        public Builder encodedPath(String encodedPath) {
            if (encodedPath == null) {
                throw new NullPointerException("encodedPath == null");
            } else if (encodedPath.startsWith("/")) {
                resolvePath(encodedPath, 0, encodedPath.length());
                return this;
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("unexpected encodedPath: ");
                stringBuilder.append(encodedPath);
                throw new IllegalArgumentException(stringBuilder.toString());
            }
        }

        public Builder query(@Nullable String query) {
            List queryStringToNamesAndValues;
            if (query != null) {
                queryStringToNamesAndValues = HttpUrl.queryStringToNamesAndValues(HttpUrl.canonicalize(query, HttpUrl.QUERY_ENCODE_SET, false, false, true, true));
            } else {
                queryStringToNamesAndValues = null;
            }
            this.encodedQueryNamesAndValues = queryStringToNamesAndValues;
            return this;
        }

        public Builder encodedQuery(@Nullable String encodedQuery) {
            List queryStringToNamesAndValues;
            if (encodedQuery != null) {
                queryStringToNamesAndValues = HttpUrl.queryStringToNamesAndValues(HttpUrl.canonicalize(encodedQuery, HttpUrl.QUERY_ENCODE_SET, true, false, true, true));
            } else {
                queryStringToNamesAndValues = null;
            }
            this.encodedQueryNamesAndValues = queryStringToNamesAndValues;
            return this;
        }

        public Builder addQueryParameter(String name, @Nullable String value) {
            if (name != null) {
                Object canonicalize;
                if (this.encodedQueryNamesAndValues == null) {
                    this.encodedQueryNamesAndValues = new ArrayList();
                }
                this.encodedQueryNamesAndValues.add(HttpUrl.canonicalize(name, HttpUrl.QUERY_COMPONENT_ENCODE_SET, false, false, true, true));
                List list = this.encodedQueryNamesAndValues;
                if (value != null) {
                    canonicalize = HttpUrl.canonicalize(value, HttpUrl.QUERY_COMPONENT_ENCODE_SET, false, false, true, true);
                } else {
                    canonicalize = null;
                }
                list.add(canonicalize);
                return this;
            }
            throw new NullPointerException("name == null");
        }

        public Builder addEncodedQueryParameter(String encodedName, @Nullable String encodedValue) {
            if (encodedName != null) {
                Object canonicalize;
                if (this.encodedQueryNamesAndValues == null) {
                    this.encodedQueryNamesAndValues = new ArrayList();
                }
                this.encodedQueryNamesAndValues.add(HttpUrl.canonicalize(encodedName, HttpUrl.QUERY_COMPONENT_REENCODE_SET, true, false, true, true));
                List list = this.encodedQueryNamesAndValues;
                if (encodedValue != null) {
                    canonicalize = HttpUrl.canonicalize(encodedValue, HttpUrl.QUERY_COMPONENT_REENCODE_SET, true, false, true, true);
                } else {
                    canonicalize = null;
                }
                list.add(canonicalize);
                return this;
            }
            throw new NullPointerException("encodedName == null");
        }

        public Builder setQueryParameter(String name, @Nullable String value) {
            removeAllQueryParameters(name);
            addQueryParameter(name, value);
            return this;
        }

        public Builder setEncodedQueryParameter(String encodedName, @Nullable String encodedValue) {
            removeAllEncodedQueryParameters(encodedName);
            addEncodedQueryParameter(encodedName, encodedValue);
            return this;
        }

        public Builder removeAllQueryParameters(String name) {
            if (name == null) {
                throw new NullPointerException("name == null");
            } else if (this.encodedQueryNamesAndValues == null) {
                return this;
            } else {
                removeAllCanonicalQueryParameters(HttpUrl.canonicalize(name, HttpUrl.QUERY_COMPONENT_ENCODE_SET, false, false, true, true));
                return this;
            }
        }

        public Builder removeAllEncodedQueryParameters(String encodedName) {
            if (encodedName == null) {
                throw new NullPointerException("encodedName == null");
            } else if (this.encodedQueryNamesAndValues == null) {
                return this;
            } else {
                removeAllCanonicalQueryParameters(HttpUrl.canonicalize(encodedName, HttpUrl.QUERY_COMPONENT_REENCODE_SET, true, false, true, true));
                return this;
            }
        }

        private void removeAllCanonicalQueryParameters(String canonicalName) {
            for (int i = this.encodedQueryNamesAndValues.size() - 2; i >= 0; i -= 2) {
                if (canonicalName.equals(this.encodedQueryNamesAndValues.get(i))) {
                    this.encodedQueryNamesAndValues.remove(i + 1);
                    this.encodedQueryNamesAndValues.remove(i);
                    if (this.encodedQueryNamesAndValues.isEmpty()) {
                        this.encodedQueryNamesAndValues = null;
                        return;
                    }
                }
            }
        }

        public Builder fragment(@Nullable String fragment) {
            String canonicalize;
            if (fragment != null) {
                canonicalize = HttpUrl.canonicalize(fragment, "", false, false, false, false);
            } else {
                canonicalize = null;
            }
            this.encodedFragment = canonicalize;
            return this;
        }

        public Builder encodedFragment(@Nullable String encodedFragment) {
            String canonicalize;
            if (encodedFragment != null) {
                canonicalize = HttpUrl.canonicalize(encodedFragment, "", true, false, false, false);
            } else {
                canonicalize = null;
            }
            this.encodedFragment = canonicalize;
            return this;
        }

        Builder reencodeForUri() {
            int i;
            String component;
            int size = this.encodedPathSegments.size();
            for (i = 0; i < size; i++) {
                this.encodedPathSegments.set(i, HttpUrl.canonicalize((String) this.encodedPathSegments.get(i), HttpUrl.PATH_SEGMENT_ENCODE_SET_URI, true, true, false, true));
            }
            i = this.encodedQueryNamesAndValues;
            if (i != 0) {
                i = i.size();
                for (size = 0; size < i; size++) {
                    component = (String) this.encodedQueryNamesAndValues.get(size);
                    if (component != null) {
                        this.encodedQueryNamesAndValues.set(size, HttpUrl.canonicalize(component, HttpUrl.QUERY_COMPONENT_ENCODE_SET_URI, true, true, true, true));
                    }
                }
            }
            component = this.encodedFragment;
            if (component != null) {
                this.encodedFragment = HttpUrl.canonicalize(component, HttpUrl.FRAGMENT_ENCODE_SET_URI, true, true, false, false);
            }
            return this;
        }

        public HttpUrl build() {
            if (this.scheme == null) {
                throw new IllegalStateException("scheme == null");
            } else if (this.host != null) {
                return new HttpUrl(this);
            } else {
                throw new IllegalStateException("host == null");
            }
        }

        public String toString() {
            int effectivePort;
            StringBuilder result = new StringBuilder();
            result.append(this.scheme);
            result.append("://");
            if (this.encodedUsername.isEmpty()) {
                if (this.encodedPassword.isEmpty()) {
                    if (this.host.indexOf(58) == -1) {
                        result.append('[');
                        result.append(this.host);
                        result.append(']');
                    } else {
                        result.append(this.host);
                    }
                    effectivePort = effectivePort();
                    if (effectivePort != HttpUrl.defaultPort(this.scheme)) {
                        result.append(':');
                        result.append(effectivePort);
                    }
                    HttpUrl.pathSegmentsToString(result, this.encodedPathSegments);
                    if (this.encodedQueryNamesAndValues != null) {
                        result.append('?');
                        HttpUrl.namesAndValuesToQueryString(result, this.encodedQueryNamesAndValues);
                    }
                    if (this.encodedFragment != null) {
                        result.append('#');
                        result.append(this.encodedFragment);
                    }
                    return result.toString();
                }
            }
            result.append(this.encodedUsername);
            if (!this.encodedPassword.isEmpty()) {
                result.append(':');
                result.append(this.encodedPassword);
            }
            result.append('@');
            if (this.host.indexOf(58) == -1) {
                result.append(this.host);
            } else {
                result.append('[');
                result.append(this.host);
                result.append(']');
            }
            effectivePort = effectivePort();
            if (effectivePort != HttpUrl.defaultPort(this.scheme)) {
                result.append(':');
                result.append(effectivePort);
            }
            HttpUrl.pathSegmentsToString(result, this.encodedPathSegments);
            if (this.encodedQueryNamesAndValues != null) {
                result.append('?');
                HttpUrl.namesAndValuesToQueryString(result, this.encodedQueryNamesAndValues);
            }
            if (this.encodedFragment != null) {
                result.append('#');
                result.append(this.encodedFragment);
            }
            return result.toString();
        }

        HttpUrl$Builder$ParseResult parse(@Nullable HttpUrl base, String input) {
            boolean z;
            boolean z2;
            char c;
            int pathDelimiterOffset;
            int pos;
            Builder builder = this;
            HttpUrl httpUrl = base;
            String str = input;
            int pos2 = Util.skipLeadingAsciiWhitespace(str, 0, input.length());
            int limit = Util.skipTrailingAsciiWhitespace(str, pos2, input.length());
            char c2 = '￿';
            if (schemeDelimiterOffset(str, pos2, limit) != -1) {
                if (input.regionMatches(true, pos2, "https:", 0, 6)) {
                    builder.scheme = "https";
                    pos2 += "https:".length();
                } else {
                    if (!input.regionMatches(true, pos2, "http:", 0, 5)) {
                        return HttpUrl$Builder$ParseResult.UNSUPPORTED_SCHEME;
                    }
                    builder.scheme = "http";
                    pos2 += "http:".length();
                }
            } else if (httpUrl == null) {
                return HttpUrl$Builder$ParseResult.MISSING_SCHEME;
            } else {
                builder.scheme = httpUrl.scheme;
            }
            int slashCount = slashCount(str, pos2, limit);
            char c3 = '#';
            if (slashCount < 2 && httpUrl != null) {
                if (httpUrl.scheme.equals(builder.scheme)) {
                    builder.encodedUsername = base.encodedUsername();
                    builder.encodedPassword = base.encodedPassword();
                    builder.host = httpUrl.host;
                    builder.port = httpUrl.port;
                    builder.encodedPathSegments.clear();
                    builder.encodedPathSegments.addAll(base.encodedPathSegments());
                    if (pos2 != limit) {
                        if (str.charAt(pos2) != '#') {
                            z = false;
                            z2 = false;
                            c = '#';
                            pathDelimiterOffset = Util.delimiterOffset(str, pos2, limit, "?#");
                            resolvePath(str, pos2, pathDelimiterOffset);
                            pos = pathDelimiterOffset;
                            if (pos < limit || str.charAt(pos) != '?') {
                                pos = pos;
                            } else {
                                int queryDelimiterOffset = Util.delimiterOffset(str, pos, limit, c);
                                builder.encodedQueryNamesAndValues = HttpUrl.queryStringToNamesAndValues(HttpUrl.canonicalize(input, pos + 1, queryDelimiterOffset, HttpUrl.QUERY_ENCODE_SET, true, false, true, true, null));
                                pos = queryDelimiterOffset;
                            }
                            if (pos < limit || str.charAt(pos) != c) {
                            } else {
                                int i = pos;
                                builder.encodedFragment = HttpUrl.canonicalize(input, pos + 1, limit, "", true, false, false, false, null);
                            }
                            return HttpUrl$Builder$ParseResult.SUCCESS;
                        }
                    }
                    encodedQuery(base.encodedQuery());
                    z = false;
                    z2 = false;
                    c = '#';
                    pathDelimiterOffset = Util.delimiterOffset(str, pos2, limit, "?#");
                    resolvePath(str, pos2, pathDelimiterOffset);
                    pos = pathDelimiterOffset;
                    if (pos < limit) {
                    }
                    pos = pos;
                    if (pos < limit) {
                    }
                    return HttpUrl$Builder$ParseResult.SUCCESS;
                }
            }
            z = false;
            z2 = false;
            int pos3 = pos2 + slashCount;
            while (true) {
                char charAt;
                pos2 = Util.delimiterOffset(str, pos3, limit, "@/\\?#");
                if (pos2 != limit) {
                    charAt = str.charAt(pos2);
                } else {
                    charAt = '￿';
                }
                char c4 = charAt;
                if (!(c4 == c2 || c4 == r10 || c4 == '/' || c4 == '\\')) {
                    switch (c4) {
                        case '?':
                            break;
                        case '@':
                            int componentDelimiterOffset;
                            if (z2) {
                                componentDelimiterOffset = pos2;
                                int pos4 = pos3;
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append(builder.encodedPassword);
                                stringBuilder.append("%40");
                                stringBuilder.append(HttpUrl.canonicalize(input, pos4, componentDelimiterOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, false, true, null));
                                builder.encodedPassword = stringBuilder.toString();
                            } else {
                                String stringBuilder2;
                                int passwordColonOffset = Util.delimiterOffset(str, pos3, pos2, ':');
                                pathDelimiterOffset = passwordColonOffset;
                                componentDelimiterOffset = pos2;
                                String canonicalUsername = HttpUrl.canonicalize(input, pos3, passwordColonOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, false, true, null);
                                if (z) {
                                    StringBuilder stringBuilder3 = new StringBuilder();
                                    stringBuilder3.append(builder.encodedUsername);
                                    stringBuilder3.append("%40");
                                    stringBuilder3.append(canonicalUsername);
                                    stringBuilder2 = stringBuilder3.toString();
                                } else {
                                    stringBuilder2 = canonicalUsername;
                                }
                                builder.encodedUsername = stringBuilder2;
                                pos3 = componentDelimiterOffset;
                                if (pathDelimiterOffset != pos3) {
                                    z2 = true;
                                    componentDelimiterOffset = pos3;
                                    builder.encodedPassword = HttpUrl.canonicalize(input, pathDelimiterOffset + 1, pos3, " \"':;<=>@[]^`{}|/\\?#", true, false, false, 1, null);
                                } else {
                                    componentDelimiterOffset = pos3;
                                    String str2 = canonicalUsername;
                                }
                                z = true;
                            }
                            pos3 = componentDelimiterOffset + 1;
                            continue;
                        default:
                            continue;
                    }
                }
                char c5 = c4;
                int componentDelimiterOffset2 = pos2;
                c = '#';
                pos2 = pos3;
                int portColonOffset = portColonOffset(str, pos2, componentDelimiterOffset2);
                if (portColonOffset + 1 < componentDelimiterOffset2) {
                    builder.host = canonicalizeHost(str, pos2, portColonOffset);
                    builder.port = parsePort(str, portColonOffset + 1, componentDelimiterOffset2);
                    if (builder.port == -1) {
                        return HttpUrl$Builder$ParseResult.INVALID_PORT;
                    }
                } else {
                    builder.host = canonicalizeHost(str, pos2, portColonOffset);
                    builder.port = HttpUrl.defaultPort(builder.scheme);
                }
                if (builder.host == null) {
                    return HttpUrl$Builder$ParseResult.INVALID_HOST;
                }
                pos2 = componentDelimiterOffset2;
                pathDelimiterOffset = Util.delimiterOffset(str, pos2, limit, "?#");
                resolvePath(str, pos2, pathDelimiterOffset);
                pos = pathDelimiterOffset;
                if (pos < limit) {
                }
                pos = pos;
                if (pos < limit) {
                }
                return HttpUrl$Builder$ParseResult.SUCCESS;
                httpUrl = base;
                c3 = '#';
                c2 = '￿';
            }
        }

        private void resolvePath(String input, int pos, int limit) {
            if (pos != limit) {
                int i;
                int pathSegmentDelimiterOffset;
                boolean segmentHasTrailingSlash;
                char c = input.charAt(pos);
                if (c != IOUtils.DIR_SEPARATOR_UNIX) {
                    if (c != IOUtils.DIR_SEPARATOR_WINDOWS) {
                        List list = this.encodedPathSegments;
                        list.set(list.size() - 1, "");
                        i = pos;
                        while (i < limit) {
                            pathSegmentDelimiterOffset = Util.delimiterOffset(input, i, limit, "/\\");
                            segmentHasTrailingSlash = pathSegmentDelimiterOffset >= limit;
                            push(input, i, pathSegmentDelimiterOffset, segmentHasTrailingSlash, true);
                            i = pathSegmentDelimiterOffset;
                            if (segmentHasTrailingSlash) {
                                i++;
                            }
                        }
                    }
                }
                this.encodedPathSegments.clear();
                this.encodedPathSegments.add("");
                pos++;
                i = pos;
                while (i < limit) {
                    pathSegmentDelimiterOffset = Util.delimiterOffset(input, i, limit, "/\\");
                    if (pathSegmentDelimiterOffset >= limit) {
                    }
                    segmentHasTrailingSlash = pathSegmentDelimiterOffset >= limit;
                    push(input, i, pathSegmentDelimiterOffset, segmentHasTrailingSlash, true);
                    i = pathSegmentDelimiterOffset;
                    if (segmentHasTrailingSlash) {
                        i++;
                    }
                }
            }
        }

        private void push(String input, int pos, int limit, boolean addTrailingSlash, boolean alreadyEncoded) {
            String segment = HttpUrl.canonicalize(input, pos, limit, HttpUrl.PATH_SEGMENT_ENCODE_SET, alreadyEncoded, false, false, true, null);
            if (!isDot(segment)) {
                if (isDotDot(segment)) {
                    pop();
                    return;
                }
                List list = this.encodedPathSegments;
                if (((String) list.get(list.size() - 1)).isEmpty()) {
                    list = this.encodedPathSegments;
                    list.set(list.size() - 1, segment);
                } else {
                    this.encodedPathSegments.add(segment);
                }
                if (addTrailingSlash) {
                    this.encodedPathSegments.add("");
                }
            }
        }

        private boolean isDot(String input) {
            if (!input.equals(".")) {
                if (!input.equalsIgnoreCase("%2e")) {
                    return false;
                }
            }
            return true;
        }

        private boolean isDotDot(String input) {
            if (!input.equals("..")) {
                if (!input.equalsIgnoreCase("%2e.")) {
                    if (!input.equalsIgnoreCase(".%2e")) {
                        if (!input.equalsIgnoreCase("%2e%2e")) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }

        private void pop() {
            List list = this.encodedPathSegments;
            if (!((String) list.remove(list.size() - 1)).isEmpty() || this.encodedPathSegments.isEmpty()) {
                this.encodedPathSegments.add("");
                return;
            }
            List list2 = this.encodedPathSegments;
            list2.set(list2.size() - 1, "");
        }

        private static int schemeDelimiterOffset(String input, int pos, int limit) {
            if (limit - pos < 2) {
                return -1;
            }
            int i;
            char c;
            char c0 = input.charAt(pos);
            if (c0 >= 'a') {
                if (c0 > 'z') {
                }
                for (i = pos + 1; i < limit; i++) {
                    c = input.charAt(i);
                    if (c >= 'a') {
                        if (c > 'z') {
                        }
                    }
                    if ((c < 'A' || c > 'Z') && !((c >= '0' && c <= '9') || c == '+' || c == '-')) {
                        if (c == '.') {
                        } else if (c != ':') {
                            return i;
                        } else {
                            return -1;
                        }
                    }
                }
                return -1;
            }
            if (c0 >= 'A') {
                if (c0 > 'Z') {
                }
                while (i < limit) {
                    c = input.charAt(i);
                    if (c >= 'a') {
                        if (c > 'z') {
                        }
                    }
                    if (c == '.') {
                    } else if (c != ':') {
                        return -1;
                    } else {
                        return i;
                    }
                }
                return -1;
            }
            return -1;
        }

        private static int slashCount(String input, int pos, int limit) {
            int slashCount = 0;
            while (pos < limit) {
                char c = input.charAt(pos);
                if (c != IOUtils.DIR_SEPARATOR_WINDOWS) {
                    if (c != IOUtils.DIR_SEPARATOR_UNIX) {
                        break;
                    }
                }
                slashCount++;
                pos++;
            }
            return slashCount;
        }

        private static int portColonOffset(String input, int pos, int limit) {
            int i = pos;
            while (i < limit) {
                char charAt = input.charAt(i);
                if (charAt == ':') {
                    return i;
                }
                if (charAt == '[') {
                    while (true) {
                        i++;
                        if (i >= limit) {
                            break;
                        } else if (input.charAt(i) == ']') {
                            break;
                        }
                    }
                }
                i++;
            }
            return limit;
        }

        private static String canonicalizeHost(String input, int pos, int limit) {
            return Util.canonicalizeHost(HttpUrl.percentDecode(input, pos, limit, null));
        }

        private static int parsePort(String input, int pos, int limit) {
            try {
                int i = Integer.parseInt(HttpUrl.canonicalize(input, pos, limit, "", false, false, false, true, null));
                if (i <= 0 || i > SupportMenu.USER_MASK) {
                    return -1;
                }
                return i;
            } catch (NumberFormatException e) {
                return -1;
            }
        }
    }

    HttpUrl(Builder builder) {
        List percentDecode;
        this.scheme = builder.scheme;
        this.username = percentDecode(builder.encodedUsername, false);
        this.password = percentDecode(builder.encodedPassword, false);
        this.host = builder.host;
        this.port = builder.effectivePort();
        this.pathSegments = percentDecode(builder.encodedPathSegments, false);
        String str = null;
        if (builder.encodedQueryNamesAndValues != null) {
            percentDecode = percentDecode(builder.encodedQueryNamesAndValues, true);
        } else {
            percentDecode = null;
        }
        this.queryNamesAndValues = percentDecode;
        if (builder.encodedFragment != null) {
            str = percentDecode(builder.encodedFragment, false);
        }
        this.fragment = str;
        this.url = builder.toString();
    }

    public URL url() {
        try {
            return new URL(this.url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public URI uri() {
        String uri = newBuilder().reencodeForUri().toString();
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            try {
                return URI.create(uri.replaceAll("[\\u0000-\\u001F\\u007F-\\u009F\\p{javaWhitespace}]", ""));
            } catch (Exception e2) {
                throw new RuntimeException(e);
            }
        }
    }

    public String scheme() {
        return this.scheme;
    }

    public boolean isHttps() {
        return this.scheme.equals("https");
    }

    public String encodedUsername() {
        if (this.username.isEmpty()) {
            return "";
        }
        int usernameStart = this.scheme.length() + 3;
        String usernameEnd = this.url;
        return this.url.substring(usernameStart, Util.delimiterOffset(usernameEnd, usernameStart, usernameEnd.length(), ":@"));
    }

    public String username() {
        return this.username;
    }

    public String encodedPassword() {
        if (this.password.isEmpty()) {
            return "";
        }
        return this.url.substring(this.url.indexOf(58, this.scheme.length() + 3) + 1, this.url.indexOf(64));
    }

    public String password() {
        return this.password;
    }

    public String host() {
        return this.host;
    }

    public int port() {
        return this.port;
    }

    public static int defaultPort(String scheme) {
        if (scheme.equals("http")) {
            return 80;
        }
        if (scheme.equals("https")) {
            return 443;
        }
        return -1;
    }

    public int pathSize() {
        return this.pathSegments.size();
    }

    public String encodedPath() {
        int pathStart = this.url.indexOf(47, this.scheme.length() + 3);
        String pathEnd = this.url;
        return this.url.substring(pathStart, Util.delimiterOffset(pathEnd, pathStart, pathEnd.length(), "?#"));
    }

    static void pathSegmentsToString(StringBuilder out, List<String> pathSegments) {
        int size = pathSegments.size();
        for (int i = 0; i < size; i++) {
            out.append(IOUtils.DIR_SEPARATOR_UNIX);
            out.append((String) pathSegments.get(i));
        }
    }

    public List<String> encodedPathSegments() {
        int pathStart = this.url.indexOf(47, this.scheme.length() + 3);
        String pathEnd = this.url;
        int pathEnd2 = Util.delimiterOffset(pathEnd, pathStart, pathEnd.length(), "?#");
        List<String> result = new ArrayList();
        int i = pathStart;
        while (i < pathEnd2) {
            i++;
            int segmentEnd = Util.delimiterOffset(this.url, i, pathEnd2, (char) IOUtils.DIR_SEPARATOR_UNIX);
            result.add(this.url.substring(i, segmentEnd));
            i = segmentEnd;
        }
        return result;
    }

    public List<String> pathSegments() {
        return this.pathSegments;
    }

    @Nullable
    public String encodedQuery() {
        if (this.queryNamesAndValues == null) {
            return null;
        }
        int queryStart = this.url.indexOf(63) + 1;
        String queryEnd = this.url;
        return this.url.substring(queryStart, Util.delimiterOffset(queryEnd, queryStart, queryEnd.length(), '#'));
    }

    static void namesAndValuesToQueryString(StringBuilder out, List<String> namesAndValues) {
        int size = namesAndValues.size();
        for (int i = 0; i < size; i += 2) {
            String name = (String) namesAndValues.get(i);
            String value = (String) namesAndValues.get(i + 1);
            if (i > 0) {
                out.append(Typography.amp);
            }
            out.append(name);
            if (value != null) {
                out.append('=');
                out.append(value);
            }
        }
    }

    static List<String> queryStringToNamesAndValues(String encodedQuery) {
        List<String> result = new ArrayList();
        int pos = 0;
        while (pos <= encodedQuery.length()) {
            int ampersandOffset = encodedQuery.indexOf(38, pos);
            if (ampersandOffset == -1) {
                ampersandOffset = encodedQuery.length();
            }
            int equalsOffset = encodedQuery.indexOf(61, pos);
            if (equalsOffset != -1) {
                if (equalsOffset <= ampersandOffset) {
                    result.add(encodedQuery.substring(pos, equalsOffset));
                    result.add(encodedQuery.substring(equalsOffset + 1, ampersandOffset));
                    pos = ampersandOffset + 1;
                }
            }
            result.add(encodedQuery.substring(pos, ampersandOffset));
            result.add(null);
            pos = ampersandOffset + 1;
        }
        return result;
    }

    @Nullable
    public String query() {
        if (this.queryNamesAndValues == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        namesAndValuesToQueryString(result, this.queryNamesAndValues);
        return result.toString();
    }

    public int querySize() {
        List list = this.queryNamesAndValues;
        return list != null ? list.size() / 2 : 0;
    }

    @Nullable
    public String queryParameter(String name) {
        int size = this.queryNamesAndValues;
        if (size == 0) {
            return null;
        }
        size = size.size();
        for (int i = 0; i < size; i += 2) {
            if (name.equals(this.queryNamesAndValues.get(i))) {
                return (String) this.queryNamesAndValues.get(i + 1);
            }
        }
        return null;
    }

    public Set<String> queryParameterNames() {
        if (this.queryNamesAndValues == null) {
            return Collections.emptySet();
        }
        Set<String> result = new LinkedHashSet();
        int size = this.queryNamesAndValues.size();
        for (int i = 0; i < size; i += 2) {
            result.add(this.queryNamesAndValues.get(i));
        }
        return Collections.unmodifiableSet(result);
    }

    public List<String> queryParameterValues(String name) {
        if (this.queryNamesAndValues == null) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList();
        int size = this.queryNamesAndValues.size();
        for (int i = 0; i < size; i += 2) {
            if (name.equals(this.queryNamesAndValues.get(i))) {
                result.add(this.queryNamesAndValues.get(i + 1));
            }
        }
        return Collections.unmodifiableList(result);
    }

    public String queryParameterName(int index) {
        List list = this.queryNamesAndValues;
        if (list != null) {
            return (String) list.get(index * 2);
        }
        throw new IndexOutOfBoundsException();
    }

    public String queryParameterValue(int index) {
        List list = this.queryNamesAndValues;
        if (list != null) {
            return (String) list.get((index * 2) + 1);
        }
        throw new IndexOutOfBoundsException();
    }

    @Nullable
    public String encodedFragment() {
        if (this.fragment == null) {
            return null;
        }
        return this.url.substring(this.url.indexOf(35) + 1);
    }

    @Nullable
    public String fragment() {
        return this.fragment;
    }

    public String redact() {
        return newBuilder("/...").username("").password("").build().toString();
    }

    @Nullable
    public HttpUrl resolve(String link) {
        Builder builder = newBuilder(link);
        return builder != null ? builder.build() : null;
    }

    public Builder newBuilder() {
        Builder result = new Builder();
        result.scheme = this.scheme;
        result.encodedUsername = encodedUsername();
        result.encodedPassword = encodedPassword();
        result.host = this.host;
        result.port = this.port != defaultPort(this.scheme) ? this.port : -1;
        result.encodedPathSegments.clear();
        result.encodedPathSegments.addAll(encodedPathSegments());
        result.encodedQuery(encodedQuery());
        result.encodedFragment = encodedFragment();
        return result;
    }

    @Nullable
    public Builder newBuilder(String link) {
        Builder builder = new Builder();
        return builder.parse(this, link) == HttpUrl$Builder$ParseResult.SUCCESS ? builder : null;
    }

    @Nullable
    public static HttpUrl parse(String url) {
        Builder builder = new Builder();
        if (builder.parse(null, url) == HttpUrl$Builder$ParseResult.SUCCESS) {
            return builder.build();
        }
        return null;
    }

    @Nullable
    public static HttpUrl get(URL url) {
        return parse(url.toString());
    }

    static HttpUrl getChecked(String url) throws MalformedURLException, UnknownHostException {
        Builder builder = new Builder();
        HttpUrl$Builder$ParseResult result = builder.parse(null, url);
        StringBuilder stringBuilder;
        switch (HttpUrl$1.$SwitchMap$okhttp3$HttpUrl$Builder$ParseResult[result.ordinal()]) {
            case 1:
                return builder.build();
            case 2:
                stringBuilder = new StringBuilder();
                stringBuilder.append("Invalid host: ");
                stringBuilder.append(url);
                throw new UnknownHostException(stringBuilder.toString());
            default:
                stringBuilder = new StringBuilder();
                stringBuilder.append("Invalid URL: ");
                stringBuilder.append(result);
                stringBuilder.append(" for ");
                stringBuilder.append(url);
                throw new MalformedURLException(stringBuilder.toString());
        }
    }

    @Nullable
    public static HttpUrl get(URI uri) {
        return parse(uri.toString());
    }

    public boolean equals(@Nullable Object other) {
        return (other instanceof HttpUrl) && ((HttpUrl) other).url.equals(this.url);
    }

    public int hashCode() {
        return this.url.hashCode();
    }

    public String toString() {
        return this.url;
    }

    @Nullable
    public String topPrivateDomain() {
        if (Util.verifyAsIpAddress(this.host)) {
            return null;
        }
        return PublicSuffixDatabase.get().getEffectiveTldPlusOne(this.host);
    }

    static String percentDecode(String encoded, boolean plusIsSpace) {
        return percentDecode(encoded, 0, encoded.length(), plusIsSpace);
    }

    private List<String> percentDecode(List<String> list, boolean plusIsSpace) {
        int size = list.size();
        List<String> result = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            String s = (String) list.get(i);
            result.add(s != null ? percentDecode(s, plusIsSpace) : null);
        }
        return Collections.unmodifiableList(result);
    }

    static String percentDecode(String encoded, int pos, int limit, boolean plusIsSpace) {
        int i = pos;
        while (i < limit) {
            char c = encoded.charAt(i);
            if (c != '%') {
                if (c != '+' || !plusIsSpace) {
                    i++;
                }
            }
            Buffer out = new Buffer();
            out.writeUtf8(encoded, pos, i);
            percentDecode(out, encoded, i, limit, plusIsSpace);
            return out.readUtf8();
        }
        return encoded.substring(pos, limit);
    }

    static void percentDecode(Buffer out, String encoded, int pos, int limit, boolean plusIsSpace) {
        int i = pos;
        while (i < limit) {
            int codePoint = encoded.codePointAt(i);
            if (codePoint == 37 && i + 2 < limit) {
                int d1 = Util.decodeHexDigit(encoded.charAt(i + 1));
                int d2 = Util.decodeHexDigit(encoded.charAt(i + 2));
                if (d1 != -1 && d2 != -1) {
                    out.writeByte((d1 << 4) + d2);
                    i += 2;
                    i += Character.charCount(codePoint);
                }
            } else if (codePoint == 43 && plusIsSpace) {
                out.writeByte(32);
                i += Character.charCount(codePoint);
            }
            out.writeUtf8CodePoint(codePoint);
            i += Character.charCount(codePoint);
        }
    }

    static boolean percentEncoded(String encoded, int pos, int limit) {
        if (pos + 2 < limit) {
            if (encoded.charAt(pos) == '%') {
                if (Util.decodeHexDigit(encoded.charAt(pos + 1)) != -1) {
                    if (Util.decodeHexDigit(encoded.charAt(pos + 2)) != -1) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    static String canonicalize(String input, int pos, int limit, String encodeSet, boolean alreadyEncoded, boolean strict, boolean plusIsSpace, boolean asciiOnly, Charset charset) {
        String str;
        String str2 = input;
        int i = limit;
        int i2 = pos;
        while (i2 < i) {
            int codePoint = str2.codePointAt(i2);
            if (codePoint < 32 || codePoint == 127) {
                str = encodeSet;
            } else {
                if (codePoint >= 128) {
                    if (asciiOnly) {
                        str = encodeSet;
                    }
                }
                if (encodeSet.indexOf(codePoint) == -1) {
                    if (codePoint == 37) {
                        if (alreadyEncoded) {
                            if (strict) {
                                if (percentEncoded(str2, i2, i)) {
                                    if (codePoint == 43 || !plusIsSpace) {
                                        i2 += Character.charCount(codePoint);
                                    }
                                }
                            }
                        }
                    }
                    if (codePoint == 43) {
                    }
                    i2 += Character.charCount(codePoint);
                }
            }
            Buffer buffer = new Buffer();
            Buffer out = buffer;
            out.writeUtf8(str2, pos, i2);
            canonicalize(buffer, input, i2, limit, encodeSet, alreadyEncoded, strict, plusIsSpace, asciiOnly, charset);
            return out.readUtf8();
        }
        str = encodeSet;
        return input.substring(pos, limit);
    }

    static void canonicalize(Buffer out, String input, int pos, int limit, String encodeSet, boolean alreadyEncoded, boolean strict, boolean plusIsSpace, boolean asciiOnly, Charset charset) {
        String str;
        Buffer buffer = out;
        String str2 = input;
        int i = limit;
        Charset charset2 = charset;
        Buffer encodedCharBuffer = null;
        int i2 = pos;
        while (i2 < i) {
            int codePoint = input.codePointAt(i2);
            if (alreadyEncoded && (codePoint == 9 || codePoint == 10 || codePoint == 12 || codePoint == 13)) {
                str = encodeSet;
            } else if (codePoint == 43 && plusIsSpace) {
                out.writeUtf8(alreadyEncoded ? "+" : "%2B");
                str = encodeSet;
            } else {
                int b;
                if (codePoint < 32 || codePoint == 127) {
                    str = encodeSet;
                } else {
                    if (codePoint >= 128) {
                        if (asciiOnly) {
                            str = encodeSet;
                        }
                    }
                    if (encodeSet.indexOf(codePoint) == -1) {
                        if (codePoint == 37) {
                            if (alreadyEncoded) {
                                if (strict) {
                                    if (percentEncoded(input, i2, limit)) {
                                        out.writeUtf8CodePoint(codePoint);
                                    }
                                }
                            }
                        }
                        out.writeUtf8CodePoint(codePoint);
                    }
                }
                if (encodedCharBuffer == null) {
                    encodedCharBuffer = new Buffer();
                }
                if (charset2 != null) {
                    if (!charset2.equals(Util.UTF_8)) {
                        encodedCharBuffer.writeString(input, i2, Character.charCount(codePoint) + i2, charset2);
                        while (!encodedCharBuffer.exhausted()) {
                            b = encodedCharBuffer.readByte() & 255;
                            out.writeByte(37);
                            out.writeByte(HEX_DIGITS[(b >> 4) & 15]);
                            out.writeByte(HEX_DIGITS[b & 15]);
                        }
                    }
                }
                encodedCharBuffer.writeUtf8CodePoint(codePoint);
                while (!encodedCharBuffer.exhausted()) {
                    b = encodedCharBuffer.readByte() & 255;
                    out.writeByte(37);
                    out.writeByte(HEX_DIGITS[(b >> 4) & 15]);
                    out.writeByte(HEX_DIGITS[b & 15]);
                }
            }
            i2 += Character.charCount(codePoint);
        }
        str = encodeSet;
    }

    static String canonicalize(String input, String encodeSet, boolean alreadyEncoded, boolean strict, boolean plusIsSpace, boolean asciiOnly, Charset charset) {
        return canonicalize(input, 0, input.length(), encodeSet, alreadyEncoded, strict, plusIsSpace, asciiOnly, charset);
    }

    static String canonicalize(String input, String encodeSet, boolean alreadyEncoded, boolean strict, boolean plusIsSpace, boolean asciiOnly) {
        return canonicalize(input, 0, input.length(), encodeSet, alreadyEncoded, strict, plusIsSpace, asciiOnly, null);
    }
}
