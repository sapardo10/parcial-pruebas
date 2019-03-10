package org.shredzone.flattr4j.connector.impl;

import android.os.Build.VERSION;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpRetryException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Collection;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import kotlin.text.Typography;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.shredzone.flattr4j.connector.Connection;
import org.shredzone.flattr4j.connector.FlattrObject;
import org.shredzone.flattr4j.connector.RateLimit;
import org.shredzone.flattr4j.connector.RequestType;
import org.shredzone.flattr4j.exception.FlattrException;
import org.shredzone.flattr4j.exception.FlattrServiceException;
import org.shredzone.flattr4j.exception.ForbiddenException;
import org.shredzone.flattr4j.exception.MarshalException;
import org.shredzone.flattr4j.exception.NoMoneyException;
import org.shredzone.flattr4j.exception.NotFoundException;
import org.shredzone.flattr4j.exception.RateLimitExceededException;
import org.shredzone.flattr4j.exception.ValidationException;
import org.shredzone.flattr4j.oauth.AccessToken;
import org.shredzone.flattr4j.oauth.ConsumerKey;

public class FlattrConnection implements Connection {
    private static final String BASE64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    private static final Pattern CHARSET = Pattern.compile(".*?charset=\"?(.*?)\"?\\s*(;.*)?", 2);
    private static final String ENCODING = "utf-8";
    private static final Logger LOG = new Logger("flattr4j", FlattrConnection.class.getName());
    private static final int TIMEOUT = 10000;
    private static final String USER_AGENT;
    private String baseUrl;
    private String call;
    private FlattrObject data;
    private StringBuilder formParams;
    private ConsumerKey key;
    private RateLimit limit;
    private StringBuilder queryParams;
    private AccessToken token;
    private RequestType type;

    private java.lang.String readResponse(java.net.HttpURLConnection r8) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:24:0x005a in {4, 5, 8, 9, 13, 16, 17, 18, 21, 22, 23} preds:[]
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
        r7 = this;
        r0 = 0;
        r1 = r8.getErrorStream();	 Catch:{ all -> 0x0051 }
        r0 = r1;	 Catch:{ all -> 0x0051 }
        if (r0 != 0) goto L_0x000e;	 Catch:{ all -> 0x0051 }
    L_0x0008:
        r1 = r8.getInputStream();	 Catch:{ all -> 0x0051 }
        r0 = r1;	 Catch:{ all -> 0x0051 }
        goto L_0x000f;	 Catch:{ all -> 0x0051 }
    L_0x000f:
        r1 = "gzip";	 Catch:{ all -> 0x0051 }
        r2 = r8.getContentEncoding();	 Catch:{ all -> 0x0051 }
        r1 = r1.equals(r2);	 Catch:{ all -> 0x0051 }
        if (r1 == 0) goto L_0x0022;	 Catch:{ all -> 0x0051 }
    L_0x001b:
        r1 = new java.util.zip.GZIPInputStream;	 Catch:{ all -> 0x0051 }
        r1.<init>(r0);	 Catch:{ all -> 0x0051 }
        r0 = r1;	 Catch:{ all -> 0x0051 }
        goto L_0x0023;	 Catch:{ all -> 0x0051 }
    L_0x0023:
        r1 = r8.getContentType();	 Catch:{ all -> 0x0051 }
        r1 = r7.getCharset(r1);	 Catch:{ all -> 0x0051 }
        r2 = new java.io.InputStreamReader;	 Catch:{ all -> 0x0051 }
        r2.<init>(r0, r1);	 Catch:{ all -> 0x0051 }
        r3 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;	 Catch:{ all -> 0x0051 }
        r3 = new char[r3];	 Catch:{ all -> 0x0051 }
        r4 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0051 }
        r4.<init>();	 Catch:{ all -> 0x0051 }
    L_0x0039:
        r5 = r2.read(r3);	 Catch:{ all -> 0x0051 }
        r6 = r5;	 Catch:{ all -> 0x0051 }
        if (r5 < 0) goto L_0x0045;	 Catch:{ all -> 0x0051 }
    L_0x0040:
        r5 = 0;	 Catch:{ all -> 0x0051 }
        r4.append(r3, r5, r6);	 Catch:{ all -> 0x0051 }
        goto L_0x0039;	 Catch:{ all -> 0x0051 }
    L_0x0045:
        r5 = r4.toString();	 Catch:{ all -> 0x0051 }
        if (r0 == 0) goto L_0x004f;
    L_0x004b:
        r0.close();
        goto L_0x0050;
    L_0x0050:
        return r5;
    L_0x0051:
        r1 = move-exception;
        if (r0 == 0) goto L_0x0058;
    L_0x0054:
        r0.close();
        goto L_0x0059;
    L_0x0059:
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.shredzone.flattr4j.connector.impl.FlattrConnection.readResponse(java.net.HttpURLConnection):java.lang.String");
    }

    protected java.lang.String base64(java.lang.String r8) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:26:0x0066 in {6, 7, 10, 11, 17, 18, 19, 20, 22, 25} preds:[]
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
        r7 = this;
        r0 = "utf-8";	 Catch:{ UnsupportedEncodingException -> 0x005d }
        r0 = r8.getBytes(r0);	 Catch:{ UnsupportedEncodingException -> 0x005d }
        r1 = new java.lang.StringBuilder;	 Catch:{ UnsupportedEncodingException -> 0x005d }
        r1.<init>();	 Catch:{ UnsupportedEncodingException -> 0x005d }
        r2 = 0;	 Catch:{ UnsupportedEncodingException -> 0x005d }
    L_0x000c:
        r3 = r0.length;	 Catch:{ UnsupportedEncodingException -> 0x005d }
        if (r2 >= r3) goto L_0x0058;	 Catch:{ UnsupportedEncodingException -> 0x005d }
    L_0x000f:
        r3 = r0[r2];	 Catch:{ UnsupportedEncodingException -> 0x005d }
        r3 = r3 & 255;	 Catch:{ UnsupportedEncodingException -> 0x005d }
        r3 = r3 << 16;	 Catch:{ UnsupportedEncodingException -> 0x005d }
        r4 = r2 + 1;	 Catch:{ UnsupportedEncodingException -> 0x005d }
        r5 = r0.length;	 Catch:{ UnsupportedEncodingException -> 0x005d }
        if (r4 >= r5) goto L_0x0024;	 Catch:{ UnsupportedEncodingException -> 0x005d }
    L_0x001a:
        r4 = r2 + 1;	 Catch:{ UnsupportedEncodingException -> 0x005d }
        r4 = r0[r4];	 Catch:{ UnsupportedEncodingException -> 0x005d }
        r4 = r4 & 255;	 Catch:{ UnsupportedEncodingException -> 0x005d }
        r4 = r4 << 8;	 Catch:{ UnsupportedEncodingException -> 0x005d }
        r3 = r3 | r4;	 Catch:{ UnsupportedEncodingException -> 0x005d }
        goto L_0x0025;	 Catch:{ UnsupportedEncodingException -> 0x005d }
    L_0x0025:
        r4 = r2 + 2;	 Catch:{ UnsupportedEncodingException -> 0x005d }
        r5 = r0.length;	 Catch:{ UnsupportedEncodingException -> 0x005d }
        if (r4 >= r5) goto L_0x0032;	 Catch:{ UnsupportedEncodingException -> 0x005d }
    L_0x002a:
        r4 = r2 + 2;	 Catch:{ UnsupportedEncodingException -> 0x005d }
        r4 = r0[r4];	 Catch:{ UnsupportedEncodingException -> 0x005d }
        r4 = r4 & 255;	 Catch:{ UnsupportedEncodingException -> 0x005d }
        r3 = r3 | r4;	 Catch:{ UnsupportedEncodingException -> 0x005d }
        goto L_0x0033;	 Catch:{ UnsupportedEncodingException -> 0x005d }
    L_0x0033:
        r4 = 0;	 Catch:{ UnsupportedEncodingException -> 0x005d }
    L_0x0034:
        r5 = 4;	 Catch:{ UnsupportedEncodingException -> 0x005d }
        if (r4 >= r5) goto L_0x0055;	 Catch:{ UnsupportedEncodingException -> 0x005d }
    L_0x0037:
        r5 = r2 + r4;	 Catch:{ UnsupportedEncodingException -> 0x005d }
        r6 = r0.length;	 Catch:{ UnsupportedEncodingException -> 0x005d }
        if (r5 > r6) goto L_0x004d;	 Catch:{ UnsupportedEncodingException -> 0x005d }
    L_0x003c:
        r5 = 16515072; // 0xfc0000 float:2.3142545E-38 double:8.1595297E-317;	 Catch:{ UnsupportedEncodingException -> 0x005d }
        r5 = r5 & r3;	 Catch:{ UnsupportedEncodingException -> 0x005d }
        r5 = r5 >> 18;	 Catch:{ UnsupportedEncodingException -> 0x005d }
        r6 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";	 Catch:{ UnsupportedEncodingException -> 0x005d }
        r6 = r6.charAt(r5);	 Catch:{ UnsupportedEncodingException -> 0x005d }
        r1.append(r6);	 Catch:{ UnsupportedEncodingException -> 0x005d }
        r3 = r3 << 6;	 Catch:{ UnsupportedEncodingException -> 0x005d }
        goto L_0x0052;	 Catch:{ UnsupportedEncodingException -> 0x005d }
    L_0x004d:
        r5 = 61;	 Catch:{ UnsupportedEncodingException -> 0x005d }
        r1.append(r5);	 Catch:{ UnsupportedEncodingException -> 0x005d }
    L_0x0052:
        r4 = r4 + 1;	 Catch:{ UnsupportedEncodingException -> 0x005d }
        goto L_0x0034;	 Catch:{ UnsupportedEncodingException -> 0x005d }
    L_0x0055:
        r2 = r2 + 3;	 Catch:{ UnsupportedEncodingException -> 0x005d }
        goto L_0x000c;	 Catch:{ UnsupportedEncodingException -> 0x005d }
    L_0x0058:
        r2 = r1.toString();	 Catch:{ UnsupportedEncodingException -> 0x005d }
        return r2;
    L_0x005d:
        r0 = move-exception;
        r1 = new java.lang.IllegalArgumentException;
        r2 = "utf-8";
        r1.<init>(r2, r0);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.shredzone.flattr4j.connector.impl.FlattrConnection.base64(java.lang.String):java.lang.String");
    }

    public org.shredzone.flattr4j.connector.Connection parameterArray(java.lang.String r7, java.lang.String[] r8) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:15:0x0079 in {6, 7, 8, 11, 14} preds:[]
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
        r6 = this;
        r0 = new java.lang.StringBuilder;	 Catch:{ UnsupportedEncodingException -> 0x0070 }
        r0.<init>();	 Catch:{ UnsupportedEncodingException -> 0x0070 }
        r1 = 0;	 Catch:{ UnsupportedEncodingException -> 0x0070 }
        r2 = r1;	 Catch:{ UnsupportedEncodingException -> 0x0070 }
        r3 = r8.length;	 Catch:{ UnsupportedEncodingException -> 0x0070 }
        if (r2 >= r3) goto L_0x002c;	 Catch:{ UnsupportedEncodingException -> 0x0070 }
        if (r2 <= 0) goto L_0x0018;	 Catch:{ UnsupportedEncodingException -> 0x0070 }
        r3 = 44;	 Catch:{ UnsupportedEncodingException -> 0x0070 }
        r0.append(r3);	 Catch:{ UnsupportedEncodingException -> 0x0070 }
        goto L_0x0019;	 Catch:{ UnsupportedEncodingException -> 0x0070 }
        r3 = r8[r2];	 Catch:{ UnsupportedEncodingException -> 0x0070 }
        r4 = "utf-8";	 Catch:{ UnsupportedEncodingException -> 0x0070 }
        r3 = java.net.URLEncoder.encode(r3, r4);	 Catch:{ UnsupportedEncodingException -> 0x0070 }
        r0.append(r3);	 Catch:{ UnsupportedEncodingException -> 0x0070 }
        r2 = r2 + 1;	 Catch:{ UnsupportedEncodingException -> 0x0070 }
        goto L_0x0008;	 Catch:{ UnsupportedEncodingException -> 0x0070 }
        r2 = r6.call;	 Catch:{ UnsupportedEncodingException -> 0x0070 }
        r3 = new java.lang.StringBuilder;	 Catch:{ UnsupportedEncodingException -> 0x0070 }
        r3.<init>();	 Catch:{ UnsupportedEncodingException -> 0x0070 }
        r4 = ":";	 Catch:{ UnsupportedEncodingException -> 0x0070 }
        r3.append(r4);	 Catch:{ UnsupportedEncodingException -> 0x0070 }
        r3.append(r7);	 Catch:{ UnsupportedEncodingException -> 0x0070 }
        r3 = r3.toString();	 Catch:{ UnsupportedEncodingException -> 0x0070 }
        r4 = r0.toString();	 Catch:{ UnsupportedEncodingException -> 0x0070 }
        r2 = r2.replace(r3, r4);	 Catch:{ UnsupportedEncodingException -> 0x0070 }
        r6.call = r2;	 Catch:{ UnsupportedEncodingException -> 0x0070 }
        r2 = LOG;	 Catch:{ UnsupportedEncodingException -> 0x0070 }
        r3 = "-> param {0} = [{1}]";	 Catch:{ UnsupportedEncodingException -> 0x0070 }
        r4 = 2;	 Catch:{ UnsupportedEncodingException -> 0x0070 }
        r4 = new java.lang.Object[r4];	 Catch:{ UnsupportedEncodingException -> 0x0070 }
        r4[r1] = r7;	 Catch:{ UnsupportedEncodingException -> 0x0070 }
        r1 = r0.toString();	 Catch:{ UnsupportedEncodingException -> 0x0070 }
        r5 = 1;	 Catch:{ UnsupportedEncodingException -> 0x0070 }
        r4[r5] = r1;	 Catch:{ UnsupportedEncodingException -> 0x0070 }
        r2.verbose(r3, r4);	 Catch:{ UnsupportedEncodingException -> 0x0070 }
        return r6;
    L_0x0070:
        r0 = move-exception;
        r1 = new java.lang.IllegalStateException;
        r1.<init>(r0);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.shredzone.flattr4j.connector.impl.FlattrConnection.parameterArray(java.lang.String, java.lang.String[]):org.shredzone.flattr4j.connector.Connection");
    }

    public java.util.Collection<org.shredzone.flattr4j.connector.FlattrObject> result() throws org.shredzone.flattr4j.exception.FlattrException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:85:0x02cd in {4, 5, 8, 9, 12, 15, 16, 19, 22, 23, 30, 33, 34, 39, 40, 43, 44, 47, 48, 51, 52, 53, 61, 62, 65, 66, 68, 70, 72, 75, 78, 81, 84} preds:[]
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
        r18 = this;
        r1 = r18;
        r2 = r1.queryParams;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        if (r2 == 0) goto L_0x001a;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
    L_0x0006:
        r2 = new java.lang.StringBuilder;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r2.<init>();	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r3 = "?";	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r2.append(r3);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r3 = r1.queryParams;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r2.append(r3);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r2 = r2.toString();	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        goto L_0x001c;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
    L_0x001a:
        r2 = "";	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
    L_0x001c:
        r3 = r1.call;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        if (r3 == 0) goto L_0x0041;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
    L_0x0020:
        r3 = new java.net.URI;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r4 = r1.baseUrl;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r3.<init>(r4);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r4 = new java.lang.StringBuilder;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r4.<init>();	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r5 = r1.call;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r4.append(r5);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r4.append(r2);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r4 = r4.toString();	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r3 = r3.resolve(r4);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r3 = r3.toURL();	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        goto L_0x005b;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
    L_0x0041:
        r3 = new java.net.URI;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r4 = new java.lang.StringBuilder;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r4.<init>();	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r5 = r1.baseUrl;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r4.append(r5);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r4.append(r2);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r4 = r4.toString();	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r3.<init>(r4);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r3 = r3.toURL();	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
    L_0x005b:
        r4 = r1.createConnection(r3);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r5 = r1.type;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r5 = r5.name();	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r4.setRequestMethod(r5);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r5 = "Accept";	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r6 = "application/json";	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r4.setRequestProperty(r5, r6);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r5 = "Accept-Charset";	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r6 = "utf-8";	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r4.setRequestProperty(r5, r6);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r5 = "Accept-Encoding";	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r6 = "gzip";	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r4.setRequestProperty(r5, r6);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r5 = r1.token;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        if (r5 == 0) goto L_0x009e;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
    L_0x0081:
        r5 = "Authorization";	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r6 = new java.lang.StringBuilder;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r6.<init>();	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r7 = "Bearer ";	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r6.append(r7);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r7 = r1.token;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r7 = r7.getToken();	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r6.append(r7);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r6 = r6.toString();	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r4.setRequestProperty(r5, r6);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        goto L_0x00de;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
    L_0x009e:
        r5 = r1.key;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        if (r5 == 0) goto L_0x00dd;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
    L_0x00a2:
        r5 = "Authorization";	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r6 = new java.lang.StringBuilder;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r6.<init>();	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r7 = "Basic ";	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r6.append(r7);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r7 = new java.lang.StringBuilder;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r7.<init>();	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r8 = r1.key;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r8 = r8.getKey();	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r7.append(r8);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r8 = 58;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r7.append(r8);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r8 = r1.key;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r8 = r8.getSecret();	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r7.append(r8);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r7 = r7.toString();	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r7 = r1.base64(r7);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r6.append(r7);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r6 = r6.toString();	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r4.setRequestProperty(r5, r6);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        goto L_0x00de;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
    L_0x00de:
        r5 = 0;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r6 = r1.data;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r7 = 1;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        if (r6 == 0) goto L_0x0100;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
    L_0x00e4:
        r6 = r1.data;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r6 = r6.toString();	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r8 = "utf-8";	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r6 = r6.getBytes(r8);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r5 = r6;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r4.setDoOutput(r7);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r6 = "Content-Type";	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r8 = "application/json";	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r4.setRequestProperty(r6, r8);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r6 = r5.length;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r4.setFixedLengthStreamingMode(r6);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        goto L_0x0121;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
    L_0x0100:
        r6 = r1.formParams;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        if (r6 == 0) goto L_0x0120;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
    L_0x0104:
        r6 = r1.formParams;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r6 = r6.toString();	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r8 = "utf-8";	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r6 = r6.getBytes(r8);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r5 = r6;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r4.setDoOutput(r7);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r6 = "Content-Type";	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r8 = "application/x-www-form-urlencoded";	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r4.setRequestProperty(r6, r8);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r6 = r5.length;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r4.setFixedLengthStreamingMode(r6);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        goto L_0x0121;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
    L_0x0121:
        r6 = LOG;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r8 = "Sending Flattr request: {0}";	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r9 = new java.lang.Object[r7];	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r10 = r1.call;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r11 = 0;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r9[r11] = r10;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r6.info(r8, r9);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r4.connect();	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        if (r5 == 0) goto L_0x0145;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
    L_0x0134:
        r6 = r4.getOutputStream();	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r6.write(r5);	 Catch:{ all -> 0x013f }
        r6.close();	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        goto L_0x0146;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
    L_0x013f:
        r0 = move-exception;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r7 = r0;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r6.close();	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        throw r7;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
    L_0x0146:
        r6 = r1.limit;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        if (r6 == 0) goto L_0x01bd;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
    L_0x014a:
        r6 = "X-RateLimit-Remaining";	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r6 = r4.getHeaderField(r6);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r8 = 0;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        if (r6 == 0) goto L_0x0161;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
    L_0x0153:
        r9 = r1.limit;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r12 = java.lang.Long.parseLong(r6);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r10 = java.lang.Long.valueOf(r12);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r9.setRemaining(r10);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        goto L_0x0166;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
    L_0x0161:
        r9 = r1.limit;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r9.setRemaining(r8);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
    L_0x0166:
        r9 = "X-RateLimit-Limit";	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r9 = r4.getHeaderField(r9);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        if (r9 == 0) goto L_0x017c;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
    L_0x016e:
        r10 = r1.limit;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r12 = java.lang.Long.parseLong(r9);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r12 = java.lang.Long.valueOf(r12);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r10.setLimit(r12);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        goto L_0x0181;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
    L_0x017c:
        r10 = r1.limit;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r10.setLimit(r8);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
    L_0x0181:
        r10 = "X-RateLimit-Current";	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r10 = r4.getHeaderField(r10);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        if (r10 == 0) goto L_0x0197;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
    L_0x0189:
        r12 = r1.limit;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r13 = java.lang.Long.parseLong(r10);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r13 = java.lang.Long.valueOf(r13);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r12.setCurrent(r13);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        goto L_0x019c;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
    L_0x0197:
        r12 = r1.limit;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r12.setCurrent(r8);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
    L_0x019c:
        r12 = "X-RateLimit-Reset";	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r12 = r4.getHeaderField(r12);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        if (r12 == 0) goto L_0x01b7;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
    L_0x01a4:
        r8 = r1.limit;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r13 = new java.util.Date;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r14 = java.lang.Long.parseLong(r12);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r16 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r14 = r14 * r16;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r13.<init>(r14);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r8.setReset(r13);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        goto L_0x01be;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
    L_0x01b7:
        r13 = r1.limit;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r13.setReset(r8);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        goto L_0x01be;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
    L_0x01be:
        r6 = r1.assertStatusOk(r4);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        if (r6 == 0) goto L_0x0285;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
    L_0x01c4:
        r6 = new org.json.JSONTokener;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r8 = r1.readResponse(r4);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r6.<init>(r8);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r6 = r6.nextValue();	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r8 = r6 instanceof org.json.JSONArray;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        if (r8 == 0) goto L_0x022f;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
    L_0x01d5:
        r8 = r6;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r8 = (org.json.JSONArray) r8;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r9 = new java.util.ArrayList;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r10 = r8.length();	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r9.<init>(r10);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r10 = r11;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r12 = r8.length();	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        if (r10 >= r12) goto L_0x0211;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r12 = new org.shredzone.flattr4j.connector.FlattrObject;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r13 = r8.getJSONObject(r10);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r12.<init>(r13);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r9.add(r12);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r13 = LOG;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r14 = "<- JSON result: {0}";	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r15 = new java.lang.Object[r7];	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r15[r11] = r12;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r13.verbose(r14, r15);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r10 = r10 + 1;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        goto L_0x01e3;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r10 = LOG;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r12 = "<-   {0} rows";	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r7 = new java.lang.Object[r7];	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r13 = r8.length();	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r13 = java.lang.Integer.valueOf(r13);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r7[r11] = r13;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r10.verbose(r12, r7);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        goto L_0x0259;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r8 = r6 instanceof org.json.JSONObject;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        if (r8 == 0) goto L_0x025b;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r8 = new org.shredzone.flattr4j.connector.FlattrObject;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r9 = r6;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r9 = (org.json.JSONObject) r9;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r8.<init>(r9);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r9 = java.util.Collections.singletonList(r8);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r10 = LOG;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r12 = "<- JSON result: {0}";	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r7 = new java.lang.Object[r7];	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r7[r11] = r8;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r10.verbose(r12, r7);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        goto L_0x028c;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r7 = new org.shredzone.flattr4j.exception.MarshalException;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r8 = new java.lang.StringBuilder;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r8.<init>();	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r9 = "unexpected result type ";	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r8.append(r9);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r9 = r6.getClass();	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r9 = r9.getName();	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r8.append(r9);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r8 = r8.toString();	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r7.<init>(r8);	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        throw r7;	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r6 = java.util.Collections.emptyList();	 Catch:{ URISyntaxException -> 0x02c1, IOException -> 0x02a4, JSONException -> 0x029a, ClassCastException -> 0x028e }
        r9 = r6;
        return r9;
    L_0x028e:
        r0 = move-exception;
        r2 = r0;
        r3 = new org.shredzone.flattr4j.exception.FlattrException;
        r4 = "Unexpected result type";
        r3.<init>(r4, r2);
        throw r3;
    L_0x029a:
        r0 = move-exception;
        r2 = r0;
        r3 = new org.shredzone.flattr4j.exception.MarshalException;
        r3.<init>(r2);
        throw r3;
    L_0x02a4:
        r0 = move-exception;
        r2 = r0;
        r3 = new org.shredzone.flattr4j.exception.FlattrException;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "API access failed: ";
        r4.append(r5);
        r5 = r1.call;
        r4.append(r5);
        r4 = r4.toString();
        r3.<init>(r4, r2);
        throw r3;
    L_0x02c1:
        r0 = move-exception;
        r2 = r0;
        r3 = new java.lang.IllegalStateException;
        r4 = "bad baseUrl";
        r3.<init>(r4, r2);
        throw r3;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.shredzone.flattr4j.connector.impl.FlattrConnection.result():java.util.Collection<org.shredzone.flattr4j.connector.FlattrObject>");
    }

    static {
        StringBuilder agent = new StringBuilder("flattr4j");
        try {
            Properties prop = new Properties();
            prop.load(FlattrConnection.class.getResourceAsStream("/org/shredzone/flattr4j/version.properties"));
            agent.append(IOUtils.DIR_SEPARATOR_UNIX);
            agent.append(prop.getProperty("version"));
        } catch (IOException ex) {
            LOG.verbose("Failed to read version number", ex);
        }
        try {
            String release = VERSION.RELEASE;
            agent.append(" Android/");
            agent.append(release);
        } catch (Throwable th) {
            agent.append(" Java/");
            agent.append(System.getProperty("java.version"));
        }
        USER_AGENT = agent.toString();
    }

    public FlattrConnection(RequestType type) {
        this.type = type;
    }

    public Connection url(String url) {
        this.baseUrl = url;
        LOG.verbose("-> baseUrl {0}", url);
        return this;
    }

    public Connection call(String call) {
        this.call = call;
        LOG.verbose("-> call {0}", call);
        return this;
    }

    public Connection token(AccessToken token) {
        this.token = token;
        return this;
    }

    public Connection key(ConsumerKey key) {
        this.key = key;
        return this;
    }

    public Connection parameter(String name, String value) {
        try {
            String str = this.call;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(":");
            stringBuilder.append(name);
            this.call = str.replace(stringBuilder.toString(), URLEncoder.encode(value, ENCODING));
            LOG.verbose("-> param {0} = {1}", name, value);
            return this;
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public Connection query(String name, String value) {
        if (this.queryParams == null) {
            this.queryParams = new StringBuilder();
        }
        appendParam(this.queryParams, name, value);
        LOG.verbose("-> query {0} = {1}", name, value);
        return this;
    }

    public Connection data(FlattrObject data) {
        if (this.formParams == null) {
            this.data = data;
            LOG.verbose("-> JSON body: {0}", data);
            return this;
        }
        throw new IllegalArgumentException("no data permitted when form is used");
    }

    public Connection form(String name, String value) {
        if (this.data == null) {
            if (this.formParams == null) {
                this.formParams = new StringBuilder();
            }
            appendParam(this.formParams, name, value);
            LOG.verbose("-> form {0} = {1}", name, value);
            return this;
        }
        throw new IllegalArgumentException("no form permitted when data is used");
    }

    public Connection rateLimit(RateLimit limit) {
        this.limit = limit;
        return this;
    }

    public FlattrObject singleResult() throws FlattrException {
        Collection<FlattrObject> result = result();
        if (result.size() == 1) {
            return (FlattrObject) result.iterator().next();
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Expected 1, but got ");
        stringBuilder.append(result.size());
        stringBuilder.append(" result rows");
        throw new MarshalException(stringBuilder.toString());
    }

    private boolean assertStatusOk(HttpURLConnection conn) throws FlattrException {
        String error = null;
        String desc = null;
        String httpStatus = null;
        try {
            int statusCode = conn.getResponseCode();
            if (statusCode != 200) {
                if (statusCode != 201) {
                    if (statusCode == 204) {
                        return false;
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("HTTP ");
                    stringBuilder.append(statusCode);
                    stringBuilder.append(": ");
                    stringBuilder.append(conn.getResponseMessage());
                    httpStatus = stringBuilder.toString();
                    JSONObject errorData = (JSONObject) new JSONTokener(readResponse(conn)).nextValue();
                    LOG.verbose("<- ERROR {0}: {1}", Integer.valueOf(statusCode), errorData);
                    error = errorData.optString("error");
                    desc = errorData.optString("error_description");
                    LOG.error("Flattr ERROR {0}: {1}", error, desc);
                    if (error == null || desc == null) {
                        LOG.error("Flattr {0}", httpStatus);
                        throw new FlattrException(httpStatus);
                    }
                    if (!"flattr_once".equals(error)) {
                        if (!"flattr_owner".equals(error)) {
                            if (!"thing_owner".equals(error)) {
                                if (!"forbidden".equals(error)) {
                                    if (!"insufficient_scope".equals(error)) {
                                        if (!"unauthorized".equals(error)) {
                                            if (!"subscribed".equals(error)) {
                                                if (!"no_means".equals(error)) {
                                                    if (!"no_money".equals(error)) {
                                                        if ("not_found".equals(error)) {
                                                            throw new NotFoundException(error, desc);
                                                        } else if ("rate_limit_exceeded".equals(error)) {
                                                            throw new RateLimitExceededException(error, desc);
                                                        } else {
                                                            if (!"invalid_parameters".equals(error)) {
                                                                if (!"invalid_scope".equals(error)) {
                                                                    if (!"validation".equals(error)) {
                                                                        throw new FlattrServiceException(error, desc);
                                                                    }
                                                                }
                                                            }
                                                            throw new ValidationException(error, desc);
                                                        }
                                                    }
                                                }
                                                throw new NoMoneyException(error, desc);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    throw new ForbiddenException(error, desc);
                }
            }
            return true;
        } catch (HttpRetryException ex) {
            LOG.debug("Could not read error response", ex);
        } catch (IOException ex2) {
            throw new FlattrException("Could not read response", ex2);
        } catch (ClassCastException ex3) {
            LOG.debug("Unexpected JSON type was returned", ex3);
        } catch (JSONException ex4) {
            LOG.debug("No valid error message was returned", ex4);
        }
    }

    protected HttpURLConnection createConnection(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        conn.setUseCaches(false);
        conn.setRequestProperty("User-Agent", USER_AGENT);
        return conn;
    }

    private void appendParam(StringBuilder builder, String key, String value) {
        try {
            if (builder.length() > 0) {
                builder.append(Typography.amp);
            }
            builder.append(URLEncoder.encode(key, ENCODING));
            builder.append('=');
            builder.append(URLEncoder.encode(value, ENCODING));
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }
    }

    protected Charset getCharset(String contentType) {
        Charset charset = Charset.forName(ENCODING);
        if (contentType == null) {
            return charset;
        }
        Matcher m = CHARSET.matcher(contentType);
        if (!m.matches()) {
            return charset;
        }
        int i = 1;
        try {
            i = Charset.forName(m.group(1));
            return i;
        } catch (UnsupportedCharsetException ex) {
            LOG.debug(m.group(i), ex);
            return charset;
        }
    }
}
