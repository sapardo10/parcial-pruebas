package okhttp3;

import java.io.Closeable;
import java.io.File;
import java.io.Flushable;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;
import okhttp3.Response.Builder;
import okhttp3.internal.Util;
import okhttp3.internal.cache.CacheRequest;
import okhttp3.internal.cache.CacheStrategy;
import okhttp3.internal.cache.DiskLruCache;
import okhttp3.internal.cache.DiskLruCache$Editor;
import okhttp3.internal.cache.DiskLruCache.Snapshot;
import okhttp3.internal.cache.InternalCache;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.http.HttpMethod;
import okhttp3.internal.http.StatusLine;
import okhttp3.internal.io.FileSystem;
import okhttp3.internal.platform.Platform;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ByteString;
import okio.Okio;

public final class Cache implements Closeable, Flushable {
    private static final int ENTRY_BODY = 1;
    private static final int ENTRY_COUNT = 2;
    private static final int ENTRY_METADATA = 0;
    private static final int VERSION = 201105;
    final DiskLruCache cache;
    private int hitCount;
    final InternalCache internalCache;
    private int networkCount;
    private int requestCount;
    int writeAbortCount;
    int writeSuccessCount;

    /* renamed from: okhttp3.Cache$2 */
    class C00032 implements Iterator<String> {
        boolean canRemove;
        final Iterator<Snapshot> delegate = Cache.this.cache.snapshots();
        @Nullable
        String nextUrl;

        C00032() throws IOException {
        }

        public boolean hasNext() {
            if (this.nextUrl != null) {
                return true;
            }
            this.canRemove = false;
            while (this.delegate.hasNext()) {
                Snapshot snapshot = (Snapshot) this.delegate.next();
                try {
                    this.nextUrl = Okio.buffer(snapshot.getSource(0)).readUtf8LineStrict();
                    snapshot.close();
                    return true;
                } catch (IOException e) {
                    snapshot.close();
                } catch (Throwable th) {
                    snapshot.close();
                    throw th;
                }
            }
            return false;
        }

        public String next() {
            if (hasNext()) {
                String result = this.nextUrl;
                this.nextUrl = null;
                this.canRemove = true;
                return result;
            }
            throw new NoSuchElementException();
        }

        public void remove() {
            if (this.canRemove) {
                this.delegate.remove();
                return;
            }
            throw new IllegalStateException("remove() before next()");
        }
    }

    private static final class Entry {
        private static final String RECEIVED_MILLIS;
        private static final String SENT_MILLIS;
        private final int code;
        @Nullable
        private final Handshake handshake;
        private final String message;
        private final Protocol protocol;
        private final long receivedResponseMillis;
        private final String requestMethod;
        private final Headers responseHeaders;
        private final long sentRequestMillis;
        private final String url;
        private final Headers varyHeaders;

        Entry(okio.Source r17) throws java.io.IOException {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:34:0x0121 in {4, 7, 10, 11, 14, 15, 22, 23, 24, 26, 28, 30, 33} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r16 = this;
            r1 = r16;
            r16.<init>();
            r0 = okio.Okio.buffer(r17);	 Catch:{ all -> 0x011b }
            r2 = r0.readUtf8LineStrict();	 Catch:{ all -> 0x011b }
            r1.url = r2;	 Catch:{ all -> 0x011b }
            r2 = r0.readUtf8LineStrict();	 Catch:{ all -> 0x011b }
            r1.requestMethod = r2;	 Catch:{ all -> 0x011b }
            r2 = new okhttp3.Headers$Builder;	 Catch:{ all -> 0x011b }
            r2.<init>();	 Catch:{ all -> 0x011b }
            r3 = okhttp3.Cache.readInt(r0);	 Catch:{ all -> 0x011b }
            r4 = 0;	 Catch:{ all -> 0x011b }
        L_0x001f:
            if (r4 >= r3) goto L_0x002b;	 Catch:{ all -> 0x011b }
        L_0x0021:
            r5 = r0.readUtf8LineStrict();	 Catch:{ all -> 0x011b }
            r2.addLenient(r5);	 Catch:{ all -> 0x011b }
            r4 = r4 + 1;	 Catch:{ all -> 0x011b }
            goto L_0x001f;	 Catch:{ all -> 0x011b }
        L_0x002b:
            r4 = r2.build();	 Catch:{ all -> 0x011b }
            r1.varyHeaders = r4;	 Catch:{ all -> 0x011b }
            r4 = r0.readUtf8LineStrict();	 Catch:{ all -> 0x011b }
            r4 = okhttp3.internal.http.StatusLine.parse(r4);	 Catch:{ all -> 0x011b }
            r5 = r4.protocol;	 Catch:{ all -> 0x011b }
            r1.protocol = r5;	 Catch:{ all -> 0x011b }
            r5 = r4.code;	 Catch:{ all -> 0x011b }
            r1.code = r5;	 Catch:{ all -> 0x011b }
            r5 = r4.message;	 Catch:{ all -> 0x011b }
            r1.message = r5;	 Catch:{ all -> 0x011b }
            r5 = new okhttp3.Headers$Builder;	 Catch:{ all -> 0x011b }
            r5.<init>();	 Catch:{ all -> 0x011b }
            r6 = okhttp3.Cache.readInt(r0);	 Catch:{ all -> 0x011b }
            r7 = 0;	 Catch:{ all -> 0x011b }
        L_0x004f:
            if (r7 >= r6) goto L_0x005b;	 Catch:{ all -> 0x011b }
        L_0x0051:
            r8 = r0.readUtf8LineStrict();	 Catch:{ all -> 0x011b }
            r5.addLenient(r8);	 Catch:{ all -> 0x011b }
            r7 = r7 + 1;	 Catch:{ all -> 0x011b }
            goto L_0x004f;	 Catch:{ all -> 0x011b }
        L_0x005b:
            r7 = SENT_MILLIS;	 Catch:{ all -> 0x011b }
            r7 = r5.get(r7);	 Catch:{ all -> 0x011b }
            r8 = RECEIVED_MILLIS;	 Catch:{ all -> 0x011b }
            r8 = r5.get(r8);	 Catch:{ all -> 0x011b }
            r9 = SENT_MILLIS;	 Catch:{ all -> 0x011b }
            r5.removeAll(r9);	 Catch:{ all -> 0x011b }
            r9 = RECEIVED_MILLIS;	 Catch:{ all -> 0x011b }
            r5.removeAll(r9);	 Catch:{ all -> 0x011b }
            r9 = 0;	 Catch:{ all -> 0x011b }
            if (r7 == 0) goto L_0x007a;	 Catch:{ all -> 0x011b }
        L_0x0075:
            r11 = java.lang.Long.parseLong(r7);	 Catch:{ all -> 0x011b }
            goto L_0x007c;	 Catch:{ all -> 0x011b }
            r11 = r9;	 Catch:{ all -> 0x011b }
            r1.sentRequestMillis = r11;	 Catch:{ all -> 0x011b }
            if (r8 == 0) goto L_0x0089;	 Catch:{ all -> 0x011b }
            r9 = java.lang.Long.parseLong(r8);	 Catch:{ all -> 0x011b }
            goto L_0x008b;	 Catch:{ all -> 0x011b }
            r1.receivedResponseMillis = r9;	 Catch:{ all -> 0x011b }
            r9 = r5.build();	 Catch:{ all -> 0x011b }
            r1.responseHeaders = r9;	 Catch:{ all -> 0x011b }
            r9 = r16.isHttps();	 Catch:{ all -> 0x011b }
            if (r9 == 0) goto L_0x010f;	 Catch:{ all -> 0x011b }
            r9 = r0.readUtf8LineStrict();	 Catch:{ all -> 0x011b }
            r10 = r9.length();	 Catch:{ all -> 0x011b }
            if (r10 > 0) goto L_0x00e8;	 Catch:{ all -> 0x011b }
            r10 = r0.readUtf8LineStrict();	 Catch:{ all -> 0x011b }
            r11 = okhttp3.CipherSuite.forJavaName(r10);	 Catch:{ all -> 0x011b }
            r12 = r1.readCertificateList(r0);	 Catch:{ all -> 0x011b }
            r13 = r1.readCertificateList(r0);	 Catch:{ all -> 0x011b }
            r14 = r0.exhausted();	 Catch:{ all -> 0x011b }
            if (r14 != 0) goto L_0x00d8;	 Catch:{ all -> 0x011b }
            r14 = r0.readUtf8LineStrict();	 Catch:{ all -> 0x011b }
            r14 = okhttp3.TlsVersion.forJavaName(r14);	 Catch:{ all -> 0x011b }
            goto L_0x00dc;	 Catch:{ all -> 0x011b }
            r14 = okhttp3.TlsVersion.SSL_3_0;	 Catch:{ all -> 0x011b }
            r15 = okhttp3.Handshake.get(r14, r11, r12, r13);	 Catch:{ all -> 0x011b }
            r1.handshake = r15;	 Catch:{ all -> 0x011b }
            goto L_0x0114;	 Catch:{ all -> 0x011b }
            r10 = new java.io.IOException;	 Catch:{ all -> 0x011b }
            r11 = new java.lang.StringBuilder;	 Catch:{ all -> 0x011b }
            r11.<init>();	 Catch:{ all -> 0x011b }
            r12 = "expected \"\" but was \"";	 Catch:{ all -> 0x011b }
            r11.append(r12);	 Catch:{ all -> 0x011b }
            r11.append(r9);	 Catch:{ all -> 0x011b }
            r12 = "\"";	 Catch:{ all -> 0x011b }
            r11.append(r12);	 Catch:{ all -> 0x011b }
            r11 = r11.toString();	 Catch:{ all -> 0x011b }
            r10.<init>(r11);	 Catch:{ all -> 0x011b }
            throw r10;	 Catch:{ all -> 0x011b }
            r9 = 0;	 Catch:{ all -> 0x011b }
            r1.handshake = r9;	 Catch:{ all -> 0x011b }
            r17.close();
            return;
        L_0x011b:
            r0 = move-exception;
            r17.close();
            throw r0;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.Cache.Entry.<init>(okio.Source):void");
        }

        private java.util.List<java.security.cert.Certificate> readCertificateList(okio.BufferedSource r8) throws java.io.IOException {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:13:0x0045 in {3, 8, 9, 12} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r7 = this;
            r0 = okhttp3.Cache.readInt(r8);
            r1 = -1;
            if (r0 != r1) goto L_0x000c;
        L_0x0007:
            r1 = java.util.Collections.emptyList();
            return r1;
        L_0x000c:
            r1 = "X.509";	 Catch:{ CertificateException -> 0x003a }
            r1 = java.security.cert.CertificateFactory.getInstance(r1);	 Catch:{ CertificateException -> 0x003a }
            r2 = new java.util.ArrayList;	 Catch:{ CertificateException -> 0x003a }
            r2.<init>(r0);	 Catch:{ CertificateException -> 0x003a }
            r3 = 0;	 Catch:{ CertificateException -> 0x003a }
        L_0x0018:
            if (r3 >= r0) goto L_0x0039;	 Catch:{ CertificateException -> 0x003a }
        L_0x001a:
            r4 = r8.readUtf8LineStrict();	 Catch:{ CertificateException -> 0x003a }
            r5 = new okio.Buffer;	 Catch:{ CertificateException -> 0x003a }
            r5.<init>();	 Catch:{ CertificateException -> 0x003a }
            r6 = okio.ByteString.decodeBase64(r4);	 Catch:{ CertificateException -> 0x003a }
            r5.write(r6);	 Catch:{ CertificateException -> 0x003a }
            r6 = r5.inputStream();	 Catch:{ CertificateException -> 0x003a }
            r6 = r1.generateCertificate(r6);	 Catch:{ CertificateException -> 0x003a }
            r2.add(r6);	 Catch:{ CertificateException -> 0x003a }
            r3 = r3 + 1;
            goto L_0x0018;
        L_0x0039:
            return r2;
        L_0x003a:
            r1 = move-exception;
            r2 = new java.io.IOException;
            r3 = r1.getMessage();
            r2.<init>(r3);
            throw r2;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.Cache.Entry.readCertificateList(okio.BufferedSource):java.util.List<java.security.cert.Certificate>");
        }

        private void writeCertList(okio.BufferedSink r7, java.util.List<java.security.cert.Certificate> r8) throws java.io.IOException {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:10:0x003f in {4, 6, 9} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r6 = this;
            r0 = r8.size();	 Catch:{ CertificateEncodingException -> 0x0034 }
            r0 = (long) r0;	 Catch:{ CertificateEncodingException -> 0x0034 }
            r0 = r7.writeDecimalLong(r0);	 Catch:{ CertificateEncodingException -> 0x0034 }
            r1 = 10;	 Catch:{ CertificateEncodingException -> 0x0034 }
            r0.writeByte(r1);	 Catch:{ CertificateEncodingException -> 0x0034 }
            r0 = 0;	 Catch:{ CertificateEncodingException -> 0x0034 }
            r2 = r8.size();	 Catch:{ CertificateEncodingException -> 0x0034 }
        L_0x0013:
            if (r0 >= r2) goto L_0x0032;	 Catch:{ CertificateEncodingException -> 0x0034 }
        L_0x0015:
            r3 = r8.get(r0);	 Catch:{ CertificateEncodingException -> 0x0034 }
            r3 = (java.security.cert.Certificate) r3;	 Catch:{ CertificateEncodingException -> 0x0034 }
            r3 = r3.getEncoded();	 Catch:{ CertificateEncodingException -> 0x0034 }
            r4 = okio.ByteString.of(r3);	 Catch:{ CertificateEncodingException -> 0x0034 }
            r4 = r4.base64();	 Catch:{ CertificateEncodingException -> 0x0034 }
            r5 = r7.writeUtf8(r4);	 Catch:{ CertificateEncodingException -> 0x0034 }
            r5.writeByte(r1);	 Catch:{ CertificateEncodingException -> 0x0034 }
            r0 = r0 + 1;
            goto L_0x0013;
            return;
        L_0x0034:
            r0 = move-exception;
            r1 = new java.io.IOException;
            r2 = r0.getMessage();
            r1.<init>(r2);
            throw r1;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.Cache.Entry.writeCertList(okio.BufferedSink, java.util.List):void");
        }

        static {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(Platform.get().getPrefix());
            stringBuilder.append("-Sent-Millis");
            SENT_MILLIS = stringBuilder.toString();
            stringBuilder = new StringBuilder();
            stringBuilder.append(Platform.get().getPrefix());
            stringBuilder.append("-Received-Millis");
            RECEIVED_MILLIS = stringBuilder.toString();
        }

        Entry(Response response) {
            this.url = response.request().url().toString();
            this.varyHeaders = HttpHeaders.varyHeaders(response);
            this.requestMethod = response.request().method();
            this.protocol = response.protocol();
            this.code = response.code();
            this.message = response.message();
            this.responseHeaders = response.headers();
            this.handshake = response.handshake();
            this.sentRequestMillis = response.sentRequestAtMillis();
            this.receivedResponseMillis = response.receivedResponseAtMillis();
        }

        public void writeTo(DiskLruCache$Editor editor) throws IOException {
            int i;
            BufferedSink sink = Okio.buffer(editor.newSink(0));
            sink.writeUtf8(this.url).writeByte(10);
            sink.writeUtf8(this.requestMethod).writeByte(10);
            sink.writeDecimalLong((long) this.varyHeaders.size()).writeByte(10);
            int size = this.varyHeaders.size();
            for (i = 0; i < size; i++) {
                sink.writeUtf8(this.varyHeaders.name(i)).writeUtf8(": ").writeUtf8(this.varyHeaders.value(i)).writeByte(10);
            }
            sink.writeUtf8(new StatusLine(this.protocol, this.code, this.message).toString()).writeByte(10);
            sink.writeDecimalLong((long) (this.responseHeaders.size() + 2)).writeByte(10);
            size = this.responseHeaders.size();
            for (i = 0; i < size; i++) {
                sink.writeUtf8(this.responseHeaders.name(i)).writeUtf8(": ").writeUtf8(this.responseHeaders.value(i)).writeByte(10);
            }
            sink.writeUtf8(SENT_MILLIS).writeUtf8(": ").writeDecimalLong(this.sentRequestMillis).writeByte(10);
            sink.writeUtf8(RECEIVED_MILLIS).writeUtf8(": ").writeDecimalLong(this.receivedResponseMillis).writeByte(10);
            if (isHttps()) {
                sink.writeByte(10);
                sink.writeUtf8(this.handshake.cipherSuite().javaName()).writeByte(10);
                writeCertList(sink, this.handshake.peerCertificates());
                writeCertList(sink, this.handshake.localCertificates());
                sink.writeUtf8(this.handshake.tlsVersion().javaName()).writeByte(10);
            }
            sink.close();
        }

        private boolean isHttps() {
            return this.url.startsWith("https://");
        }

        public boolean matches(Request request, Response response) {
            if (this.url.equals(request.url().toString())) {
                if (this.requestMethod.equals(request.method())) {
                    if (HttpHeaders.varyMatches(response, this.varyHeaders, request)) {
                        return true;
                    }
                }
            }
            return false;
        }

        public Response response(Snapshot snapshot) {
            String contentType = this.responseHeaders.get("Content-Type");
            String contentLength = this.responseHeaders.get("Content-Length");
            return new Builder().request(new Request.Builder().url(this.url).method(this.requestMethod, null).headers(this.varyHeaders).build()).protocol(this.protocol).code(this.code).message(this.message).headers(this.responseHeaders).body(new CacheResponseBody(snapshot, contentType, contentLength)).handshake(this.handshake).sentRequestAtMillis(this.sentRequestMillis).receivedResponseAtMillis(this.receivedResponseMillis).build();
        }
    }

    private static class CacheResponseBody extends ResponseBody {
        private final BufferedSource bodySource;
        @Nullable
        private final String contentLength;
        @Nullable
        private final String contentType;
        final Snapshot snapshot;

        CacheResponseBody(Snapshot snapshot, String contentType, String contentLength) {
            this.snapshot = snapshot;
            this.contentType = contentType;
            this.contentLength = contentLength;
            this.bodySource = Okio.buffer(new Cache$CacheResponseBody$1(this, snapshot.getSource(1), snapshot));
        }

        public MediaType contentType() {
            String str = this.contentType;
            return str != null ? MediaType.parse(str) : null;
        }

        public long contentLength() {
            long j = -1;
            try {
                if (this.contentLength != null) {
                    j = Long.parseLong(this.contentLength);
                }
                return j;
            } catch (NumberFormatException e) {
                return -1;
            }
        }

        public BufferedSource source() {
            return this.bodySource;
        }
    }

    public Cache(File directory, long maxSize) {
        this(directory, maxSize, FileSystem.SYSTEM);
    }

    Cache(File directory, long maxSize, FileSystem fileSystem) {
        this.internalCache = new Cache$1(this);
        this.cache = DiskLruCache.create(fileSystem, directory, VERSION, 2, maxSize);
    }

    public static String key(HttpUrl url) {
        return ByteString.encodeUtf8(url.toString()).md5().hex();
    }

    @Nullable
    Response get(Request request) {
        try {
            Closeable snapshot = this.cache.get(key(request.url()));
            if (snapshot == null) {
                return null;
            }
            try {
                Entry entry = new Entry(snapshot.getSource(0));
                Response response = entry.response(snapshot);
                if (entry.matches(request, response)) {
                    return response;
                }
                Util.closeQuietly(response.body());
                return null;
            } catch (IOException e) {
                Util.closeQuietly(snapshot);
                return null;
            }
        } catch (IOException e2) {
            return null;
        }
    }

    @Nullable
    CacheRequest put(Response response) {
        String requestMethod = response.request().method();
        if (HttpMethod.invalidatesCache(response.request().method())) {
            try {
                remove(response.request());
            } catch (IOException e) {
            }
            return null;
        } else if (!requestMethod.equals("GET") || HttpHeaders.hasVaryAll(response)) {
            return null;
        } else {
            Entry entry = new Entry(response);
            try {
                DiskLruCache$Editor editor = this.cache.edit(key(response.request().url()));
                if (editor == null) {
                    return null;
                }
                entry.writeTo(editor);
                return new Cache$CacheRequestImpl(this, editor);
            } catch (IOException e2) {
                abortQuietly(null);
                return null;
            }
        }
    }

    void remove(Request request) throws IOException {
        this.cache.remove(key(request.url()));
    }

    void update(Response cached, Response network) {
        Entry entry = new Entry(network);
        try {
            DiskLruCache$Editor editor = ((CacheResponseBody) cached.body()).snapshot.edit();
            if (editor != null) {
                entry.writeTo(editor);
                editor.commit();
            }
        } catch (IOException e) {
            abortQuietly(null);
        }
    }

    private void abortQuietly(@Nullable DiskLruCache$Editor editor) {
        if (editor != null) {
            try {
                editor.abort();
            } catch (IOException e) {
            }
        }
    }

    public void initialize() throws IOException {
        this.cache.initialize();
    }

    public void delete() throws IOException {
        this.cache.delete();
    }

    public void evictAll() throws IOException {
        this.cache.evictAll();
    }

    public Iterator<String> urls() throws IOException {
        return new C00032();
    }

    public synchronized int writeAbortCount() {
        return this.writeAbortCount;
    }

    public synchronized int writeSuccessCount() {
        return this.writeSuccessCount;
    }

    public long size() throws IOException {
        return this.cache.size();
    }

    public long maxSize() {
        return this.cache.getMaxSize();
    }

    public void flush() throws IOException {
        this.cache.flush();
    }

    public void close() throws IOException {
        this.cache.close();
    }

    public File directory() {
        return this.cache.getDirectory();
    }

    public boolean isClosed() {
        return this.cache.isClosed();
    }

    synchronized void trackResponse(CacheStrategy cacheStrategy) {
        this.requestCount++;
        if (cacheStrategy.networkRequest != null) {
            this.networkCount++;
        } else if (cacheStrategy.cacheResponse != null) {
            this.hitCount++;
        }
    }

    synchronized void trackConditionalCacheHit() {
        this.hitCount++;
    }

    public synchronized int networkCount() {
        return this.networkCount;
    }

    public synchronized int hitCount() {
        return this.hitCount;
    }

    public synchronized int requestCount() {
        return this.requestCount;
    }

    static int readInt(BufferedSource source) throws IOException {
        try {
            long result = source.readDecimalLong();
            String line = source.readUtf8LineStrict();
            if (result >= 0 && result <= 2147483647L && line.isEmpty()) {
                return (int) result;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("expected an int but was \"");
            stringBuilder.append(result);
            stringBuilder.append(line);
            stringBuilder.append("\"");
            throw new IOException(stringBuilder.toString());
        } catch (NumberFormatException e) {
            throw new IOException(e.getMessage());
        }
    }
}
