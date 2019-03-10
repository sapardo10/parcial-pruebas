package org.jsoup.helper;

import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import kotlin.text.Typography;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.UncheckedIOException;
import org.jsoup.internal.ConstrainableInputStream;
import org.jsoup.internal.Normalizer;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.parser.TokenQueue;

public class HttpConnection implements Connection {
    public static final String CONTENT_ENCODING = "Content-Encoding";
    private static final String CONTENT_TYPE = "Content-Type";
    public static final String DEFAULT_UA = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36";
    private static final String DefaultUploadType = "application/octet-stream";
    private static final String FORM_URL_ENCODED = "application/x-www-form-urlencoded";
    private static final int HTTP_TEMP_REDIR = 307;
    private static final String MULTIPART_FORM_DATA = "multipart/form-data";
    private static final String USER_AGENT = "User-Agent";
    private org.jsoup.Connection.Request req = new Request();
    private org.jsoup.Connection.Response res = new Response();

    private static abstract class Base<T extends org.jsoup.Connection.Base> implements org.jsoup.Connection.Base<T> {
        Map<String, String> cookies;
        Map<String, List<String>> headers;
        Method method;
        URL url;

        private Base() {
            this.headers = new LinkedHashMap();
            this.cookies = new LinkedHashMap();
        }

        public URL url() {
            return this.url;
        }

        public T url(URL url) {
            Validate.notNull(url, "URL must not be null");
            this.url = url;
            return this;
        }

        public Method method() {
            return this.method;
        }

        public T method(Method method) {
            Validate.notNull(method, "Method must not be null");
            this.method = method;
            return this;
        }

        public String header(String name) {
            Validate.notNull(name, "Header name must not be null");
            Collection vals = getHeadersCaseInsensitive(name);
            if (vals.size() > 0) {
                return StringUtil.join(vals, ", ");
            }
            return null;
        }

        public T addHeader(String name, String value) {
            Validate.notEmpty(name);
            value = value == null ? "" : value;
            List<String> values = headers(name);
            if (values.isEmpty()) {
                values = new ArrayList();
                this.headers.put(name, values);
            }
            values.add(fixHeaderEncoding(value));
            return this;
        }

        public List<String> headers(String name) {
            Validate.notEmpty(name);
            return getHeadersCaseInsensitive(name);
        }

        private static String fixHeaderEncoding(String val) {
            try {
                byte[] bytes = val.getBytes(CharEncoding.ISO_8859_1);
                if (looksLikeUtf8(bytes)) {
                    return new String(bytes, "UTF-8");
                }
                return val;
            } catch (UnsupportedEncodingException e) {
                return val;
            }
        }

        private static boolean looksLikeUtf8(byte[] input) {
            int j;
            int o;
            int end;
            int i = 0;
            if (input.length >= 3 && (input[0] & 255) == 239) {
                if ((((input[1] & 255) == 187 ? 1 : 0) & ((input[2] & 255) == 191 ? 1 : 0)) != 0) {
                    i = 3;
                    j = input.length;
                    while (i < j) {
                        o = input[i];
                        if ((o & 128) == 0) {
                            if ((o & 224) == PsExtractor.AUDIO_STREAM) {
                                end = i + 1;
                            } else if ((o & PsExtractor.VIDEO_STREAM_MASK) == 224) {
                                end = i + 2;
                            } else if ((o & 248) == PsExtractor.VIDEO_STREAM_MASK) {
                                return false;
                            } else {
                                end = i + 3;
                            }
                            while (i < end) {
                                i++;
                                if ((input[i] & PsExtractor.AUDIO_STREAM) != 128) {
                                    return false;
                                }
                            }
                        }
                        i++;
                    }
                    return true;
                }
            }
            j = input.length;
            while (i < j) {
                o = input[i];
                if ((o & 128) == 0) {
                    if ((o & 224) == PsExtractor.AUDIO_STREAM) {
                        end = i + 1;
                    } else if ((o & PsExtractor.VIDEO_STREAM_MASK) == 224) {
                        end = i + 2;
                    } else if ((o & 248) == PsExtractor.VIDEO_STREAM_MASK) {
                        return false;
                    } else {
                        end = i + 3;
                    }
                    while (i < end) {
                        i++;
                        if ((input[i] & PsExtractor.AUDIO_STREAM) != 128) {
                            return false;
                        }
                    }
                }
                i++;
            }
            return true;
        }

        public T header(String name, String value) {
            Validate.notEmpty(name, "Header name must not be empty");
            removeHeader(name);
            addHeader(name, value);
            return this;
        }

        public boolean hasHeader(String name) {
            Validate.notEmpty(name, "Header name must not be empty");
            return getHeadersCaseInsensitive(name).size() != 0;
        }

        public boolean hasHeaderWithValue(String name, String value) {
            Validate.notEmpty(name);
            Validate.notEmpty(value);
            for (String candidate : headers(name)) {
                if (value.equalsIgnoreCase(candidate)) {
                    return true;
                }
            }
            return false;
        }

        public T removeHeader(String name) {
            Validate.notEmpty(name, "Header name must not be empty");
            Entry<String, List<String>> entry = scanHeaders(name);
            if (entry != null) {
                this.headers.remove(entry.getKey());
            }
            return this;
        }

        public Map<String, String> headers() {
            LinkedHashMap<String, String> map = new LinkedHashMap(this.headers.size());
            for (Entry<String, List<String>> entry : this.headers.entrySet()) {
                String header = (String) entry.getKey();
                List<String> values = (List) entry.getValue();
                if (values.size() > 0) {
                    map.put(header, values.get(0));
                }
            }
            return map;
        }

        public Map<String, List<String>> multiHeaders() {
            return this.headers;
        }

        private List<String> getHeadersCaseInsensitive(String name) {
            Validate.notNull(name);
            for (Entry<String, List<String>> entry : this.headers.entrySet()) {
                if (name.equalsIgnoreCase((String) entry.getKey())) {
                    return (List) entry.getValue();
                }
            }
            return Collections.emptyList();
        }

        private Entry<String, List<String>> scanHeaders(String name) {
            String lc = Normalizer.lowerCase(name);
            for (Entry<String, List<String>> entry : this.headers.entrySet()) {
                if (Normalizer.lowerCase((String) entry.getKey()).equals(lc)) {
                    return entry;
                }
            }
            return null;
        }

        public String cookie(String name) {
            Validate.notEmpty(name, "Cookie name must not be empty");
            return (String) this.cookies.get(name);
        }

        public T cookie(String name, String value) {
            Validate.notEmpty(name, "Cookie name must not be empty");
            Validate.notNull(value, "Cookie value must not be null");
            this.cookies.put(name, value);
            return this;
        }

        public boolean hasCookie(String name) {
            Validate.notEmpty(name, "Cookie name must not be empty");
            return this.cookies.containsKey(name);
        }

        public T removeCookie(String name) {
            Validate.notEmpty(name, "Cookie name must not be empty");
            this.cookies.remove(name);
            return this;
        }

        public Map<String, String> cookies() {
            return this.cookies;
        }
    }

    public static class KeyVal implements org.jsoup.Connection.KeyVal {
        private String contentType;
        private String key;
        private InputStream stream;
        private String value;

        public static KeyVal create(String key, String value) {
            return new KeyVal().key(key).value(value);
        }

        public static KeyVal create(String key, String filename, InputStream stream) {
            return new KeyVal().key(key).value(filename).inputStream(stream);
        }

        private KeyVal() {
        }

        public KeyVal key(String key) {
            Validate.notEmpty(key, "Data key must not be empty");
            this.key = key;
            return this;
        }

        public String key() {
            return this.key;
        }

        public KeyVal value(String value) {
            Validate.notNull(value, "Data value must not be null");
            this.value = value;
            return this;
        }

        public String value() {
            return this.value;
        }

        public KeyVal inputStream(InputStream inputStream) {
            Validate.notNull(this.value, "Data input stream must not be null");
            this.stream = inputStream;
            return this;
        }

        public InputStream inputStream() {
            return this.stream;
        }

        public boolean hasInputStream() {
            return this.stream != null;
        }

        public org.jsoup.Connection.KeyVal contentType(String contentType) {
            Validate.notEmpty(contentType);
            this.contentType = contentType;
            return this;
        }

        public String contentType() {
            return this.contentType;
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(this.key);
            stringBuilder.append("=");
            stringBuilder.append(this.value);
            return stringBuilder.toString();
        }
    }

    public static class Request extends Base<org.jsoup.Connection.Request> implements org.jsoup.Connection.Request {
        private String body;
        private Collection<org.jsoup.Connection.KeyVal> data;
        private boolean followRedirects;
        private boolean ignoreContentType;
        private boolean ignoreHttpErrors;
        private int maxBodySizeBytes;
        private Parser parser;
        private boolean parserDefined;
        private String postDataCharset;
        private Proxy proxy;
        private int timeoutMilliseconds;
        private boolean validateTSLCertificates;

        public /* bridge */ /* synthetic */ String cookie(String str) {
            return super.cookie(str);
        }

        public /* bridge */ /* synthetic */ Map cookies() {
            return super.cookies();
        }

        public /* bridge */ /* synthetic */ boolean hasCookie(String str) {
            return super.hasCookie(str);
        }

        public /* bridge */ /* synthetic */ boolean hasHeader(String str) {
            return super.hasHeader(str);
        }

        public /* bridge */ /* synthetic */ boolean hasHeaderWithValue(String str, String str2) {
            return super.hasHeaderWithValue(str, str2);
        }

        public /* bridge */ /* synthetic */ String header(String str) {
            return super.header(str);
        }

        public /* bridge */ /* synthetic */ List headers(String str) {
            return super.headers(str);
        }

        public /* bridge */ /* synthetic */ Map headers() {
            return super.headers();
        }

        public /* bridge */ /* synthetic */ Method method() {
            return super.method();
        }

        public /* bridge */ /* synthetic */ Map multiHeaders() {
            return super.multiHeaders();
        }

        public /* bridge */ /* synthetic */ URL url() {
            return super.url();
        }

        Request() {
            super();
            this.body = null;
            this.ignoreHttpErrors = false;
            this.ignoreContentType = false;
            this.parserDefined = false;
            this.validateTSLCertificates = true;
            this.postDataCharset = "UTF-8";
            this.timeoutMilliseconds = 30000;
            this.maxBodySizeBytes = 1048576;
            this.followRedirects = true;
            this.data = new ArrayList();
            this.method = Method.GET;
            addHeader("Accept-Encoding", "gzip");
            addHeader(HttpConnection.USER_AGENT, HttpConnection.DEFAULT_UA);
            this.parser = Parser.htmlParser();
        }

        public Proxy proxy() {
            return this.proxy;
        }

        public Request proxy(Proxy proxy) {
            this.proxy = proxy;
            return this;
        }

        public Request proxy(String host, int port) {
            this.proxy = new Proxy(Type.HTTP, InetSocketAddress.createUnresolved(host, port));
            return this;
        }

        public int timeout() {
            return this.timeoutMilliseconds;
        }

        public Request timeout(int millis) {
            Validate.isTrue(millis >= 0, "Timeout milliseconds must be 0 (infinite) or greater");
            this.timeoutMilliseconds = millis;
            return this;
        }

        public int maxBodySize() {
            return this.maxBodySizeBytes;
        }

        public org.jsoup.Connection.Request maxBodySize(int bytes) {
            Validate.isTrue(bytes >= 0, "maxSize must be 0 (unlimited) or larger");
            this.maxBodySizeBytes = bytes;
            return this;
        }

        public boolean followRedirects() {
            return this.followRedirects;
        }

        public org.jsoup.Connection.Request followRedirects(boolean followRedirects) {
            this.followRedirects = followRedirects;
            return this;
        }

        public boolean ignoreHttpErrors() {
            return this.ignoreHttpErrors;
        }

        public boolean validateTLSCertificates() {
            return this.validateTSLCertificates;
        }

        public void validateTLSCertificates(boolean value) {
            this.validateTSLCertificates = value;
        }

        public org.jsoup.Connection.Request ignoreHttpErrors(boolean ignoreHttpErrors) {
            this.ignoreHttpErrors = ignoreHttpErrors;
            return this;
        }

        public boolean ignoreContentType() {
            return this.ignoreContentType;
        }

        public org.jsoup.Connection.Request ignoreContentType(boolean ignoreContentType) {
            this.ignoreContentType = ignoreContentType;
            return this;
        }

        public Request data(org.jsoup.Connection.KeyVal keyval) {
            Validate.notNull(keyval, "Key val must not be null");
            this.data.add(keyval);
            return this;
        }

        public Collection<org.jsoup.Connection.KeyVal> data() {
            return this.data;
        }

        public org.jsoup.Connection.Request requestBody(String body) {
            this.body = body;
            return this;
        }

        public String requestBody() {
            return this.body;
        }

        public Request parser(Parser parser) {
            this.parser = parser;
            this.parserDefined = true;
            return this;
        }

        public Parser parser() {
            return this.parser;
        }

        public org.jsoup.Connection.Request postDataCharset(String charset) {
            Validate.notNull(charset, "Charset must not be null");
            if (Charset.isSupported(charset)) {
                this.postDataCharset = charset;
                return this;
            }
            throw new IllegalCharsetNameException(charset);
        }

        public String postDataCharset() {
            return this.postDataCharset;
        }
    }

    public static class Response extends Base<org.jsoup.Connection.Response> implements org.jsoup.Connection.Response {
        private static final String LOCATION = "Location";
        private static final int MAX_REDIRECTS = 20;
        private static SSLSocketFactory sslSocketFactory;
        private static final Pattern xmlContentTypeRxp = Pattern.compile("(application|text)/\\w*\\+?xml.*");
        private InputStream bodyStream;
        private ByteBuffer byteData;
        private String charset;
        private String contentType;
        private boolean executed = false;
        private boolean inputStreamRead = false;
        private int numRedirects = 0;
        private org.jsoup.Connection.Request req;
        private int statusCode;
        private String statusMessage;

        /* renamed from: org.jsoup.helper.HttpConnection$Response$1 */
        class C11791 implements HostnameVerifier {
            C11791() {
            }

            public boolean verify(String urlHostName, SSLSession session) {
                return true;
            }
        }

        /* renamed from: org.jsoup.helper.HttpConnection$Response$2 */
        class C11802 implements X509TrustManager {
            C11802() {
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        }

        static org.jsoup.helper.HttpConnection.Response execute(org.jsoup.Connection.Request r17, org.jsoup.helper.HttpConnection.Response r18) throws java.io.IOException {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:108:0x0209 in {4, 6, 9, 10, 13, 14, 18, 19, 22, 23, 24, 29, 30, 37, 38, 45, 46, 50, 52, 57, 61, 70, 72, 73, 74, 75, 83, 84, 85, 92, 93, 96, 97, 98, 99, 101, 104, 107} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r1 = r17;
            r2 = r18;
            r0 = "Request must not be null";
            org.jsoup.helper.Validate.notNull(r1, r0);
            r0 = r17.url();
            r3 = r0.getProtocol();
            r0 = "http";
            r0 = r3.equals(r0);
            if (r0 != 0) goto L_0x002a;
        L_0x0019:
            r0 = "https";
            r0 = r3.equals(r0);
            if (r0 == 0) goto L_0x0022;
        L_0x0021:
            goto L_0x002a;
        L_0x0022:
            r0 = new java.net.MalformedURLException;
            r4 = "Only http & https protocols supported";
            r0.<init>(r4);
            throw r0;
            r0 = r17.method();
            r4 = r0.hasBody();
            r0 = r17.requestBody();
            r5 = 1;
            if (r0 == 0) goto L_0x003c;
        L_0x003a:
            r0 = 1;
            goto L_0x003d;
        L_0x003c:
            r0 = 0;
        L_0x003d:
            r6 = r0;
            if (r4 != 0) goto L_0x0059;
        L_0x0040:
            r0 = new java.lang.StringBuilder;
            r0.<init>();
            r7 = "Cannot set a request body for HTTP method ";
            r0.append(r7);
            r7 = r17.method();
            r0.append(r7);
            r0 = r0.toString();
            org.jsoup.helper.Validate.isFalse(r6, r0);
            goto L_0x005a;
        L_0x005a:
            r0 = 0;
            r7 = r17.data();
            r7 = r7.size();
            if (r7 <= 0) goto L_0x006d;
        L_0x0065:
            if (r4 == 0) goto L_0x0069;
        L_0x0067:
            if (r6 == 0) goto L_0x006d;
        L_0x0069:
            serialiseRequestUrl(r17);
            goto L_0x0077;
            if (r4 == 0) goto L_0x0076;
        L_0x0070:
            r0 = setOutputContentType(r17);
            r7 = r0;
            goto L_0x0078;
        L_0x0077:
            r7 = r0;
        L_0x0078:
            r8 = java.lang.System.nanoTime();
            r10 = createConnection(r17);
            r10.connect();	 Catch:{ IOException -> 0x0204 }
            r0 = r10.getDoOutput();	 Catch:{ IOException -> 0x0204 }
            if (r0 == 0) goto L_0x0091;	 Catch:{ IOException -> 0x0204 }
        L_0x0089:
            r0 = r10.getOutputStream();	 Catch:{ IOException -> 0x0204 }
            writePost(r1, r0, r7);	 Catch:{ IOException -> 0x0204 }
            goto L_0x0092;	 Catch:{ IOException -> 0x0204 }
        L_0x0092:
            r0 = r10.getResponseCode();	 Catch:{ IOException -> 0x0204 }
            r11 = new org.jsoup.helper.HttpConnection$Response;	 Catch:{ IOException -> 0x0204 }
            r11.<init>(r2);	 Catch:{ IOException -> 0x0204 }
            r11.setupFromConnection(r10, r2);	 Catch:{ IOException -> 0x0204 }
            r11.req = r1;	 Catch:{ IOException -> 0x0204 }
            r12 = "Location";	 Catch:{ IOException -> 0x0204 }
            r12 = r11.hasHeader(r12);	 Catch:{ IOException -> 0x0204 }
            r13 = 0;	 Catch:{ IOException -> 0x0204 }
            if (r12 == 0) goto L_0x0127;	 Catch:{ IOException -> 0x0204 }
        L_0x00a9:
            r12 = r17.followRedirects();	 Catch:{ IOException -> 0x0204 }
            if (r12 == 0) goto L_0x0127;	 Catch:{ IOException -> 0x0204 }
        L_0x00af:
            r5 = 307; // 0x133 float:4.3E-43 double:1.517E-321;	 Catch:{ IOException -> 0x0204 }
            if (r0 == r5) goto L_0x00c8;	 Catch:{ IOException -> 0x0204 }
        L_0x00b3:
            r5 = org.jsoup.Connection.Method.GET;	 Catch:{ IOException -> 0x0204 }
            r1.method(r5);	 Catch:{ IOException -> 0x0204 }
            r5 = r17.data();	 Catch:{ IOException -> 0x0204 }
            r5.clear();	 Catch:{ IOException -> 0x0204 }
            r1.requestBody(r13);	 Catch:{ IOException -> 0x0204 }
            r5 = "Content-Type";	 Catch:{ IOException -> 0x0204 }
            r1.removeHeader(r5);	 Catch:{ IOException -> 0x0204 }
            goto L_0x00c9;	 Catch:{ IOException -> 0x0204 }
        L_0x00c9:
            r5 = "Location";	 Catch:{ IOException -> 0x0204 }
            r5 = r11.header(r5);	 Catch:{ IOException -> 0x0204 }
            if (r5 == 0) goto L_0x00e8;	 Catch:{ IOException -> 0x0204 }
        L_0x00d1:
            r12 = "http:/";	 Catch:{ IOException -> 0x0204 }
            r12 = r5.startsWith(r12);	 Catch:{ IOException -> 0x0204 }
            if (r12 == 0) goto L_0x00e8;	 Catch:{ IOException -> 0x0204 }
        L_0x00d9:
            r12 = 6;	 Catch:{ IOException -> 0x0204 }
            r13 = r5.charAt(r12);	 Catch:{ IOException -> 0x0204 }
            r14 = 47;	 Catch:{ IOException -> 0x0204 }
            if (r13 == r14) goto L_0x00e8;	 Catch:{ IOException -> 0x0204 }
        L_0x00e2:
            r12 = r5.substring(r12);	 Catch:{ IOException -> 0x0204 }
            r5 = r12;	 Catch:{ IOException -> 0x0204 }
            goto L_0x00e9;	 Catch:{ IOException -> 0x0204 }
        L_0x00e9:
            r12 = r17.url();	 Catch:{ IOException -> 0x0204 }
            r12 = org.jsoup.helper.StringUtil.resolve(r12, r5);	 Catch:{ IOException -> 0x0204 }
            r13 = org.jsoup.helper.HttpConnection.encodeUrl(r12);	 Catch:{ IOException -> 0x0204 }
            r1.url(r13);	 Catch:{ IOException -> 0x0204 }
            r13 = r11.cookies;	 Catch:{ IOException -> 0x0204 }
            r13 = r13.entrySet();	 Catch:{ IOException -> 0x0204 }
            r13 = r13.iterator();	 Catch:{ IOException -> 0x0204 }
        L_0x0102:
            r14 = r13.hasNext();	 Catch:{ IOException -> 0x0204 }
            if (r14 == 0) goto L_0x0122;	 Catch:{ IOException -> 0x0204 }
        L_0x0108:
            r14 = r13.next();	 Catch:{ IOException -> 0x0204 }
            r14 = (java.util.Map.Entry) r14;	 Catch:{ IOException -> 0x0204 }
            r15 = r14.getKey();	 Catch:{ IOException -> 0x0204 }
            r15 = (java.lang.String) r15;	 Catch:{ IOException -> 0x0204 }
            r16 = r14.getValue();	 Catch:{ IOException -> 0x0204 }
            r2 = r16;	 Catch:{ IOException -> 0x0204 }
            r2 = (java.lang.String) r2;	 Catch:{ IOException -> 0x0204 }
            r1.cookie(r15, r2);	 Catch:{ IOException -> 0x0204 }
            r2 = r18;	 Catch:{ IOException -> 0x0204 }
            goto L_0x0102;	 Catch:{ IOException -> 0x0204 }
        L_0x0122:
            r2 = execute(r1, r11);	 Catch:{ IOException -> 0x0204 }
            return r2;	 Catch:{ IOException -> 0x0204 }
            r2 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;	 Catch:{ IOException -> 0x0204 }
            if (r0 < r2) goto L_0x0132;	 Catch:{ IOException -> 0x0204 }
        L_0x012c:
            r2 = 400; // 0x190 float:5.6E-43 double:1.976E-321;	 Catch:{ IOException -> 0x0204 }
            if (r0 < r2) goto L_0x0131;	 Catch:{ IOException -> 0x0204 }
        L_0x0130:
            goto L_0x0132;	 Catch:{ IOException -> 0x0204 }
        L_0x0131:
            goto L_0x0139;	 Catch:{ IOException -> 0x0204 }
        L_0x0132:
            r2 = r17.ignoreHttpErrors();	 Catch:{ IOException -> 0x0204 }
            if (r2 == 0) goto L_0x01f4;	 Catch:{ IOException -> 0x0204 }
        L_0x0138:
            goto L_0x0131;	 Catch:{ IOException -> 0x0204 }
        L_0x0139:
            r2 = r11.contentType();	 Catch:{ IOException -> 0x0204 }
            if (r2 == 0) goto L_0x016c;	 Catch:{ IOException -> 0x0204 }
        L_0x013f:
            r12 = r17.ignoreContentType();	 Catch:{ IOException -> 0x0204 }
            if (r12 != 0) goto L_0x016b;	 Catch:{ IOException -> 0x0204 }
        L_0x0145:
            r12 = "text/";	 Catch:{ IOException -> 0x0204 }
            r12 = r2.startsWith(r12);	 Catch:{ IOException -> 0x0204 }
            if (r12 != 0) goto L_0x016a;	 Catch:{ IOException -> 0x0204 }
        L_0x014d:
            r12 = xmlContentTypeRxp;	 Catch:{ IOException -> 0x0204 }
            r12 = r12.matcher(r2);	 Catch:{ IOException -> 0x0204 }
            r12 = r12.matches();	 Catch:{ IOException -> 0x0204 }
            if (r12 == 0) goto L_0x015a;	 Catch:{ IOException -> 0x0204 }
        L_0x0159:
            goto L_0x016d;	 Catch:{ IOException -> 0x0204 }
        L_0x015a:
            r5 = new org.jsoup.UnsupportedMimeTypeException;	 Catch:{ IOException -> 0x0204 }
            r12 = "Unhandled content type. Must be text/*, application/xml, or application/xhtml+xml";	 Catch:{ IOException -> 0x0204 }
            r13 = r17.url();	 Catch:{ IOException -> 0x0204 }
            r13 = r13.toString();	 Catch:{ IOException -> 0x0204 }
            r5.<init>(r12, r2, r13);	 Catch:{ IOException -> 0x0204 }
            throw r5;	 Catch:{ IOException -> 0x0204 }
        L_0x016a:
            goto L_0x016d;	 Catch:{ IOException -> 0x0204 }
        L_0x016b:
            goto L_0x016d;	 Catch:{ IOException -> 0x0204 }
        L_0x016d:
            if (r2 == 0) goto L_0x0191;	 Catch:{ IOException -> 0x0204 }
        L_0x016f:
            r12 = xmlContentTypeRxp;	 Catch:{ IOException -> 0x0204 }
            r12 = r12.matcher(r2);	 Catch:{ IOException -> 0x0204 }
            r12 = r12.matches();	 Catch:{ IOException -> 0x0204 }
            if (r12 == 0) goto L_0x0191;	 Catch:{ IOException -> 0x0204 }
        L_0x017b:
            r12 = r1 instanceof org.jsoup.helper.HttpConnection.Request;	 Catch:{ IOException -> 0x0204 }
            if (r12 == 0) goto L_0x0190;	 Catch:{ IOException -> 0x0204 }
        L_0x017f:
            r12 = r1;	 Catch:{ IOException -> 0x0204 }
            r12 = (org.jsoup.helper.HttpConnection.Request) r12;	 Catch:{ IOException -> 0x0204 }
            r12 = r12.parserDefined;	 Catch:{ IOException -> 0x0204 }
            if (r12 != 0) goto L_0x0190;	 Catch:{ IOException -> 0x0204 }
        L_0x0188:
            r12 = org.jsoup.parser.Parser.xmlParser();	 Catch:{ IOException -> 0x0204 }
            r1.parser(r12);	 Catch:{ IOException -> 0x0204 }
            goto L_0x0192;	 Catch:{ IOException -> 0x0204 }
        L_0x0190:
            goto L_0x0192;	 Catch:{ IOException -> 0x0204 }
        L_0x0192:
            r12 = r11.contentType;	 Catch:{ IOException -> 0x0204 }
            r12 = org.jsoup.helper.DataUtil.getCharsetFromContentType(r12);	 Catch:{ IOException -> 0x0204 }
            r11.charset = r12;	 Catch:{ IOException -> 0x0204 }
            r12 = r10.getContentLength();	 Catch:{ IOException -> 0x0204 }
            if (r12 == 0) goto L_0x01e9;	 Catch:{ IOException -> 0x0204 }
        L_0x01a0:
            r12 = r17.method();	 Catch:{ IOException -> 0x0204 }
            r14 = org.jsoup.Connection.Method.HEAD;	 Catch:{ IOException -> 0x0204 }
            if (r12 == r14) goto L_0x01e9;	 Catch:{ IOException -> 0x0204 }
        L_0x01a8:
            r11.bodyStream = r13;	 Catch:{ IOException -> 0x0204 }
            r12 = r10.getErrorStream();	 Catch:{ IOException -> 0x0204 }
            if (r12 == 0) goto L_0x01b5;	 Catch:{ IOException -> 0x0204 }
        L_0x01b0:
            r12 = r10.getErrorStream();	 Catch:{ IOException -> 0x0204 }
            goto L_0x01b9;	 Catch:{ IOException -> 0x0204 }
        L_0x01b5:
            r12 = r10.getInputStream();	 Catch:{ IOException -> 0x0204 }
        L_0x01b9:
            r11.bodyStream = r12;	 Catch:{ IOException -> 0x0204 }
            r12 = "Content-Encoding";	 Catch:{ IOException -> 0x0204 }
            r13 = "gzip";	 Catch:{ IOException -> 0x0204 }
            r12 = r11.hasHeaderWithValue(r12, r13);	 Catch:{ IOException -> 0x0204 }
            if (r12 == 0) goto L_0x01cf;	 Catch:{ IOException -> 0x0204 }
        L_0x01c5:
            r12 = new java.util.zip.GZIPInputStream;	 Catch:{ IOException -> 0x0204 }
            r13 = r11.bodyStream;	 Catch:{ IOException -> 0x0204 }
            r12.<init>(r13);	 Catch:{ IOException -> 0x0204 }
            r11.bodyStream = r12;	 Catch:{ IOException -> 0x0204 }
            goto L_0x01d0;	 Catch:{ IOException -> 0x0204 }
        L_0x01d0:
            r12 = r11.bodyStream;	 Catch:{ IOException -> 0x0204 }
            r13 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;	 Catch:{ IOException -> 0x0204 }
            r14 = r17.maxBodySize();	 Catch:{ IOException -> 0x0204 }
            r12 = org.jsoup.internal.ConstrainableInputStream.wrap(r12, r13, r14);	 Catch:{ IOException -> 0x0204 }
            r13 = r17.timeout();	 Catch:{ IOException -> 0x0204 }
            r13 = (long) r13;	 Catch:{ IOException -> 0x0204 }
            r12 = r12.timeout(r8, r13);	 Catch:{ IOException -> 0x0204 }
            r11.bodyStream = r12;	 Catch:{ IOException -> 0x0204 }
            goto L_0x01f0;	 Catch:{ IOException -> 0x0204 }
            r12 = org.jsoup.helper.DataUtil.emptyByteBuffer();	 Catch:{ IOException -> 0x0204 }
            r11.byteData = r12;	 Catch:{ IOException -> 0x0204 }
            r11.executed = r5;
            return r11;
        L_0x01f4:
            r2 = new org.jsoup.HttpStatusException;	 Catch:{ IOException -> 0x0204 }
            r5 = "HTTP error fetching URL";	 Catch:{ IOException -> 0x0204 }
            r12 = r17.url();	 Catch:{ IOException -> 0x0204 }
            r12 = r12.toString();	 Catch:{ IOException -> 0x0204 }
            r2.<init>(r5, r0, r12);	 Catch:{ IOException -> 0x0204 }
            throw r2;	 Catch:{ IOException -> 0x0204 }
        L_0x0204:
            r0 = move-exception;
            r10.disconnect();
            throw r0;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.jsoup.helper.HttpConnection.Response.execute(org.jsoup.Connection$Request, org.jsoup.helper.HttpConnection$Response):org.jsoup.helper.HttpConnection$Response");
        }

        public /* bridge */ /* synthetic */ String cookie(String str) {
            return super.cookie(str);
        }

        public /* bridge */ /* synthetic */ Map cookies() {
            return super.cookies();
        }

        public /* bridge */ /* synthetic */ boolean hasCookie(String str) {
            return super.hasCookie(str);
        }

        public /* bridge */ /* synthetic */ boolean hasHeader(String str) {
            return super.hasHeader(str);
        }

        public /* bridge */ /* synthetic */ boolean hasHeaderWithValue(String str, String str2) {
            return super.hasHeaderWithValue(str, str2);
        }

        public /* bridge */ /* synthetic */ String header(String str) {
            return super.header(str);
        }

        public /* bridge */ /* synthetic */ List headers(String str) {
            return super.headers(str);
        }

        public /* bridge */ /* synthetic */ Map headers() {
            return super.headers();
        }

        public /* bridge */ /* synthetic */ Method method() {
            return super.method();
        }

        public /* bridge */ /* synthetic */ Map multiHeaders() {
            return super.multiHeaders();
        }

        public /* bridge */ /* synthetic */ URL url() {
            return super.url();
        }

        Response() {
            super();
        }

        private Response(Response previousResponse) throws IOException {
            super();
            if (previousResponse != null) {
                this.numRedirects = previousResponse.numRedirects + 1;
                if (this.numRedirects >= 20) {
                    throw new IOException(String.format("Too many redirects occurred trying to load URL %s", new Object[]{previousResponse.url()}));
                }
            }
        }

        static Response execute(org.jsoup.Connection.Request req) throws IOException {
            return execute(req, null);
        }

        public int statusCode() {
            return this.statusCode;
        }

        public String statusMessage() {
            return this.statusMessage;
        }

        public String charset() {
            return this.charset;
        }

        public Response charset(String charset) {
            this.charset = charset;
            return this;
        }

        public String contentType() {
            return this.contentType;
        }

        public Document parse() throws IOException {
            Validate.isTrue(this.executed, "Request must be executed (with .execute(), .get(), or .post() before parsing response");
            ByteBuffer byteBuffer = this.byteData;
            if (byteBuffer != null) {
                this.bodyStream = new ByteArrayInputStream(byteBuffer.array());
                this.inputStreamRead = false;
            }
            Validate.isFalse(this.inputStreamRead, "Input stream already read and parsed, cannot re-read.");
            Document doc = DataUtil.parseInputStream(this.bodyStream, this.charset, this.url.toExternalForm(), this.req.parser());
            this.charset = doc.outputSettings().charset().name();
            this.inputStreamRead = true;
            safeClose();
            return doc;
        }

        private void prepareByteData() {
            Validate.isTrue(this.executed, "Request must be executed (with .execute(), .get(), or .post() before getting response body");
            if (this.byteData == null) {
                Validate.isFalse(this.inputStreamRead, "Request has already been read (with .parse())");
                try {
                    this.byteData = DataUtil.readToByteBuffer(this.bodyStream, this.req.maxBodySize());
                    this.inputStreamRead = true;
                    safeClose();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                } catch (Throwable th) {
                    this.inputStreamRead = true;
                    safeClose();
                }
            }
        }

        public String body() {
            prepareByteData();
            String str = this.charset;
            if (str == null) {
                str = Charset.forName("UTF-8").decode(this.byteData).toString();
            } else {
                str = Charset.forName(str).decode(this.byteData).toString();
            }
            this.byteData.rewind();
            return str;
        }

        public byte[] bodyAsBytes() {
            prepareByteData();
            return this.byteData.array();
        }

        public org.jsoup.Connection.Response bufferUp() {
            prepareByteData();
            return this;
        }

        public BufferedInputStream bodyStream() {
            Validate.isTrue(this.executed, "Request must be executed (with .execute(), .get(), or .post() before getting response body");
            Validate.isFalse(this.inputStreamRead, "Request has already been read");
            this.inputStreamRead = true;
            return ConstrainableInputStream.wrap(this.bodyStream, 32768, this.req.maxBodySize());
        }

        private static HttpURLConnection createConnection(org.jsoup.Connection.Request req) throws IOException {
            HttpURLConnection conn;
            if (req.proxy() == null) {
                conn = req.url().openConnection();
            } else {
                conn = req.url().openConnection(req.proxy());
            }
            conn = conn;
            conn.setRequestMethod(req.method().name());
            conn.setInstanceFollowRedirects(false);
            conn.setConnectTimeout(req.timeout());
            conn.setReadTimeout(req.timeout() / 2);
            if (conn instanceof HttpsURLConnection) {
                if (!req.validateTLSCertificates()) {
                    initUnSecureTSL();
                    ((HttpsURLConnection) conn).setSSLSocketFactory(sslSocketFactory);
                    ((HttpsURLConnection) conn).setHostnameVerifier(getInsecureVerifier());
                }
            }
            if (req.method().hasBody()) {
                conn.setDoOutput(true);
            }
            if (req.cookies().size() > 0) {
                conn.addRequestProperty("Cookie", getRequestCookieString(req));
            }
            for (Entry<String, List<String>> header : req.multiHeaders().entrySet()) {
                for (String value : (List) header.getValue()) {
                    conn.addRequestProperty((String) header.getKey(), value);
                }
            }
            return conn;
        }

        private void safeClose() {
            InputStream inputStream = this.bodyStream;
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                } catch (Throwable th) {
                    this.bodyStream = null;
                }
                this.bodyStream = null;
            }
        }

        private static HostnameVerifier getInsecureVerifier() {
            return new C11791();
        }

        private static synchronized void initUnSecureTSL() throws IOException {
            synchronized (Response.class) {
                if (sslSocketFactory == null) {
                    TrustManager[] trustAllCerts = new TrustManager[]{new C11802()};
                    try {
                        SSLContext sslContext = SSLContext.getInstance("SSL");
                        sslContext.init(null, trustAllCerts, new SecureRandom());
                        sslSocketFactory = sslContext.getSocketFactory();
                    } catch (NoSuchAlgorithmException e) {
                        throw new IOException("Can't create unsecure trust manager");
                    }
                }
            }
        }

        private void setupFromConnection(HttpURLConnection conn, org.jsoup.Connection.Response previousResponse) throws IOException {
            this.method = Method.valueOf(conn.getRequestMethod());
            this.url = conn.getURL();
            this.statusCode = conn.getResponseCode();
            this.statusMessage = conn.getResponseMessage();
            this.contentType = conn.getContentType();
            processResponseHeaders(createHeaderMap(conn));
            if (previousResponse != null) {
                for (Entry<String, String> prevCookie : previousResponse.cookies().entrySet()) {
                    if (!hasCookie((String) prevCookie.getKey())) {
                        cookie((String) prevCookie.getKey(), (String) prevCookie.getValue());
                    }
                }
            }
        }

        private static LinkedHashMap<String, List<String>> createHeaderMap(HttpURLConnection conn) {
            LinkedHashMap<String, List<String>> headers = new LinkedHashMap();
            int i = 0;
            while (true) {
                String key = conn.getHeaderFieldKey(i);
                String val = conn.getHeaderField(i);
                if (key == null && val == null) {
                    return headers;
                }
                i++;
                if (key != null) {
                    if (val != null) {
                        if (headers.containsKey(key)) {
                            ((List) headers.get(key)).add(val);
                        } else {
                            ArrayList<String> vals = new ArrayList();
                            vals.add(val);
                            headers.put(key, vals);
                        }
                    }
                }
            }
        }

        void processResponseHeaders(Map<String, List<String>> resHeaders) {
            for (Entry<String, List<String>> entry : resHeaders.entrySet()) {
                String name = (String) entry.getKey();
                if (name != null) {
                    List<String> values = (List) entry.getValue();
                    if (name.equalsIgnoreCase("Set-Cookie")) {
                        for (String value : values) {
                            if (value != null) {
                                TokenQueue cd = new TokenQueue(value);
                                String cookieName = cd.chompTo("=").trim();
                                String cookieVal = cd.consumeTo(";").trim();
                                if (cookieName.length() > 0) {
                                    cookie(cookieName, cookieVal);
                                }
                            }
                        }
                    }
                    for (String value2 : values) {
                        addHeader(name, value2);
                    }
                }
            }
        }

        private static String setOutputContentType(org.jsoup.Connection.Request req) {
            if (req.hasHeader(HttpConnection.CONTENT_TYPE)) {
                return null;
            }
            if (HttpConnection.needsMultipart(req)) {
                String bound = DataUtil.mimeBoundary();
                String str = HttpConnection.CONTENT_TYPE;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("multipart/form-data; boundary=");
                stringBuilder.append(bound);
                req.header(str, stringBuilder.toString());
                return bound;
            }
            str = HttpConnection.CONTENT_TYPE;
            stringBuilder = new StringBuilder();
            stringBuilder.append("application/x-www-form-urlencoded; charset=");
            stringBuilder.append(req.postDataCharset());
            req.header(str, stringBuilder.toString());
            return null;
        }

        private static void writePost(org.jsoup.Connection.Request req, OutputStream outputStream, String bound) throws IOException {
            Collection<org.jsoup.Connection.KeyVal> data = req.data();
            BufferedWriter w = new BufferedWriter(new OutputStreamWriter(outputStream, req.postDataCharset()));
            if (bound != null) {
                for (org.jsoup.Connection.KeyVal keyVal : data) {
                    w.write("--");
                    w.write(bound);
                    w.write(IOUtils.LINE_SEPARATOR_WINDOWS);
                    w.write("Content-Disposition: form-data; name=\"");
                    w.write(HttpConnection.encodeMimeName(keyVal.key()));
                    w.write("\"");
                    if (keyVal.hasInputStream()) {
                        w.write("; filename=\"");
                        w.write(HttpConnection.encodeMimeName(keyVal.value()));
                        w.write("\"\r\nContent-Type: ");
                        w.write(keyVal.contentType() != null ? keyVal.contentType() : HttpConnection.DefaultUploadType);
                        w.write("\r\n\r\n");
                        w.flush();
                        DataUtil.crossStreams(keyVal.inputStream(), outputStream);
                        outputStream.flush();
                    } else {
                        w.write("\r\n\r\n");
                        w.write(keyVal.value());
                    }
                    w.write(IOUtils.LINE_SEPARATOR_WINDOWS);
                }
                w.write("--");
                w.write(bound);
                w.write("--");
            } else if (req.requestBody() != null) {
                w.write(req.requestBody());
            } else {
                boolean first = true;
                for (org.jsoup.Connection.KeyVal keyVal2 : data) {
                    if (first) {
                        first = false;
                    } else {
                        w.append(Typography.amp);
                    }
                    w.write(URLEncoder.encode(keyVal2.key(), req.postDataCharset()));
                    w.write(61);
                    w.write(URLEncoder.encode(keyVal2.value(), req.postDataCharset()));
                }
            }
            w.close();
        }

        private static String getRequestCookieString(org.jsoup.Connection.Request req) {
            StringBuilder sb = StringUtil.stringBuilder();
            boolean first = true;
            for (Entry<String, String> cookie : req.cookies().entrySet()) {
                if (first) {
                    first = false;
                } else {
                    sb.append("; ");
                }
                sb.append((String) cookie.getKey());
                sb.append('=');
                sb.append((String) cookie.getValue());
            }
            return sb.toString();
        }

        private static void serialiseRequestUrl(org.jsoup.Connection.Request req) throws IOException {
            URL in = req.url();
            StringBuilder url = StringUtil.stringBuilder();
            boolean first = true;
            url.append(in.getProtocol());
            url.append("://");
            url.append(in.getAuthority());
            url.append(in.getPath());
            url.append("?");
            if (in.getQuery() != null) {
                url.append(in.getQuery());
                first = false;
            }
            for (org.jsoup.Connection.KeyVal keyVal : req.data()) {
                Validate.isFalse(keyVal.hasInputStream(), "InputStream data not supported in URL query string.");
                if (first) {
                    first = false;
                } else {
                    url.append(Typography.amp);
                }
                url.append(URLEncoder.encode(keyVal.key(), "UTF-8"));
                url.append('=');
                url.append(URLEncoder.encode(keyVal.value(), "UTF-8"));
            }
            req.url(new URL(url.toString()));
            req.data().clear();
        }
    }

    public static Connection connect(String url) {
        Connection con = new HttpConnection();
        con.url(url);
        return con;
    }

    public static Connection connect(URL url) {
        Connection con = new HttpConnection();
        con.url(url);
        return con;
    }

    private static String encodeUrl(String url) {
        try {
            return encodeUrl(new URL(url)).toExternalForm();
        } catch (Exception e) {
            return url;
        }
    }

    static URL encodeUrl(URL u) {
        try {
            return new URL(new URI(u.toExternalForm().replaceAll(StringUtils.SPACE, "%20")).toASCIIString());
        } catch (Exception e) {
            return u;
        }
    }

    private static String encodeMimeName(String val) {
        if (val == null) {
            return null;
        }
        return val.replaceAll("\"", "%22");
    }

    private HttpConnection() {
    }

    public Connection url(URL url) {
        this.req.url(url);
        return this;
    }

    public Connection url(String url) {
        Validate.notEmpty(url, "Must supply a valid URL");
        try {
            this.req.url(new URL(encodeUrl(url)));
            return this;
        } catch (MalformedURLException e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Malformed URL: ");
            stringBuilder.append(url);
            throw new IllegalArgumentException(stringBuilder.toString(), e);
        }
    }

    public Connection proxy(Proxy proxy) {
        this.req.proxy(proxy);
        return this;
    }

    public Connection proxy(String host, int port) {
        this.req.proxy(host, port);
        return this;
    }

    public Connection userAgent(String userAgent) {
        Validate.notNull(userAgent, "User agent must not be null");
        this.req.header(USER_AGENT, userAgent);
        return this;
    }

    public Connection timeout(int millis) {
        this.req.timeout(millis);
        return this;
    }

    public Connection maxBodySize(int bytes) {
        this.req.maxBodySize(bytes);
        return this;
    }

    public Connection followRedirects(boolean followRedirects) {
        this.req.followRedirects(followRedirects);
        return this;
    }

    public Connection referrer(String referrer) {
        Validate.notNull(referrer, "Referrer must not be null");
        this.req.header("Referer", referrer);
        return this;
    }

    public Connection method(Method method) {
        this.req.method(method);
        return this;
    }

    public Connection ignoreHttpErrors(boolean ignoreHttpErrors) {
        this.req.ignoreHttpErrors(ignoreHttpErrors);
        return this;
    }

    public Connection ignoreContentType(boolean ignoreContentType) {
        this.req.ignoreContentType(ignoreContentType);
        return this;
    }

    public Connection validateTLSCertificates(boolean value) {
        this.req.validateTLSCertificates(value);
        return this;
    }

    public Connection data(String key, String value) {
        this.req.data(KeyVal.create(key, value));
        return this;
    }

    public Connection data(String key, String filename, InputStream inputStream) {
        this.req.data(KeyVal.create(key, filename, inputStream));
        return this;
    }

    public Connection data(String key, String filename, InputStream inputStream, String contentType) {
        this.req.data(KeyVal.create(key, filename, inputStream).contentType(contentType));
        return this;
    }

    public Connection data(Map<String, String> data) {
        Validate.notNull(data, "Data map must not be null");
        for (Entry<String, String> entry : data.entrySet()) {
            this.req.data(KeyVal.create((String) entry.getKey(), (String) entry.getValue()));
        }
        return this;
    }

    public Connection data(String... keyvals) {
        Validate.notNull(keyvals, "Data key value pairs must not be null");
        Validate.isTrue(keyvals.length % 2 == 0, "Must supply an even number of key value pairs");
        for (int i = 0; i < keyvals.length; i += 2) {
            String key = keyvals[i];
            String value = keyvals[i + 1];
            Validate.notEmpty(key, "Data key must not be empty");
            Validate.notNull(value, "Data value must not be null");
            this.req.data(KeyVal.create(key, value));
        }
        return this;
    }

    public Connection data(Collection<org.jsoup.Connection.KeyVal> data) {
        Validate.notNull(data, "Data collection must not be null");
        for (org.jsoup.Connection.KeyVal entry : data) {
            this.req.data(entry);
        }
        return this;
    }

    public org.jsoup.Connection.KeyVal data(String key) {
        Validate.notEmpty(key, "Data key must not be empty");
        for (org.jsoup.Connection.KeyVal keyVal : request().data()) {
            if (keyVal.key().equals(key)) {
                return keyVal;
            }
        }
        return null;
    }

    public Connection requestBody(String body) {
        this.req.requestBody(body);
        return this;
    }

    public Connection header(String name, String value) {
        this.req.header(name, value);
        return this;
    }

    public Connection headers(Map<String, String> headers) {
        Validate.notNull(headers, "Header map must not be null");
        for (Entry<String, String> entry : headers.entrySet()) {
            this.req.header((String) entry.getKey(), (String) entry.getValue());
        }
        return this;
    }

    public Connection cookie(String name, String value) {
        this.req.cookie(name, value);
        return this;
    }

    public Connection cookies(Map<String, String> cookies) {
        Validate.notNull(cookies, "Cookie map must not be null");
        for (Entry<String, String> entry : cookies.entrySet()) {
            this.req.cookie((String) entry.getKey(), (String) entry.getValue());
        }
        return this;
    }

    public Connection parser(Parser parser) {
        this.req.parser(parser);
        return this;
    }

    public Document get() throws IOException {
        this.req.method(Method.GET);
        execute();
        return this.res.parse();
    }

    public Document post() throws IOException {
        this.req.method(Method.POST);
        execute();
        return this.res.parse();
    }

    public org.jsoup.Connection.Response execute() throws IOException {
        this.res = Response.execute(this.req);
        return this.res;
    }

    public org.jsoup.Connection.Request request() {
        return this.req;
    }

    public Connection request(org.jsoup.Connection.Request request) {
        this.req = request;
        return this;
    }

    public org.jsoup.Connection.Response response() {
        return this.res;
    }

    public Connection response(org.jsoup.Connection.Response response) {
        this.res = response;
        return this;
    }

    public Connection postDataCharset(String charset) {
        this.req.postDataCharset(charset);
        return this;
    }

    private static boolean needsMultipart(org.jsoup.Connection.Request req) {
        for (org.jsoup.Connection.KeyVal keyVal : req.data()) {
            if (keyVal.hasInputStream()) {
                return true;
            }
        }
        return false;
    }
}
