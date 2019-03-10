package retrofit2;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.Call$Factory;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.ResponseBody;

final class ServiceMethod<R, T> {
    static final String PARAM = "[a-zA-Z][a-zA-Z0-9_-]*";
    static final Pattern PARAM_NAME_REGEX = Pattern.compile(PARAM);
    static final Pattern PARAM_URL_REGEX = Pattern.compile("\\{([a-zA-Z][a-zA-Z0-9_-]*)\\}");
    private final HttpUrl baseUrl;
    private final CallAdapter<R, T> callAdapter;
    private final Call$Factory callFactory;
    private final MediaType contentType;
    private final boolean hasBody;
    private final Headers headers;
    private final String httpMethod;
    private final boolean isFormEncoded;
    private final boolean isMultipart;
    private final ParameterHandler<?>[] parameterHandlers;
    private final String relativeUrl;
    private final Converter<ResponseBody, R> responseConverter;

    okhttp3.Call toCall(@javax.annotation.Nullable java.lang.Object... r11) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:13:0x005d in {2, 3, 8, 10, 12} preds:[]
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
        r10 = this;
        r9 = new retrofit2.RequestBuilder;
        r1 = r10.httpMethod;
        r2 = r10.baseUrl;
        r3 = r10.relativeUrl;
        r4 = r10.headers;
        r5 = r10.contentType;
        r6 = r10.hasBody;
        r7 = r10.isFormEncoded;
        r8 = r10.isMultipart;
        r0 = r9;
        r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8);
        r1 = r10.parameterHandlers;
        if (r11 == 0) goto L_0x001c;
    L_0x001a:
        r2 = r11.length;
        goto L_0x001d;
    L_0x001c:
        r2 = 0;
    L_0x001d:
        r3 = r1.length;
        if (r2 != r3) goto L_0x0038;
    L_0x0020:
        r3 = 0;
    L_0x0021:
        if (r3 >= r2) goto L_0x002d;
    L_0x0023:
        r4 = r1[r3];
        r5 = r11[r3];
        r4.apply(r0, r5);
        r3 = r3 + 1;
        goto L_0x0021;
    L_0x002d:
        r3 = r10.callFactory;
        r4 = r0.build();
        r3 = r3.newCall(r4);
        return r3;
    L_0x0038:
        r3 = new java.lang.IllegalArgumentException;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "Argument count (";
        r4.append(r5);
        r4.append(r2);
        r5 = ") doesn't match expected count (";
        r4.append(r5);
        r5 = r1.length;
        r4.append(r5);
        r5 = ")";
        r4.append(r5);
        r4 = r4.toString();
        r3.<init>(r4);
        throw r3;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: retrofit2.ServiceMethod.toCall(java.lang.Object[]):okhttp3.Call");
    }

    ServiceMethod(ServiceMethod$Builder<R, T> builder) {
        this.callFactory = builder.retrofit.callFactory();
        this.callAdapter = builder.callAdapter;
        this.baseUrl = builder.retrofit.baseUrl();
        this.responseConverter = builder.responseConverter;
        this.httpMethod = builder.httpMethod;
        this.relativeUrl = builder.relativeUrl;
        this.headers = builder.headers;
        this.contentType = builder.contentType;
        this.hasBody = builder.hasBody;
        this.isFormEncoded = builder.isFormEncoded;
        this.isMultipart = builder.isMultipart;
        this.parameterHandlers = builder.parameterHandlers;
    }

    T adapt(Call<R> call) {
        return this.callAdapter.adapt(call);
    }

    R toResponse(ResponseBody body) throws IOException {
        return this.responseConverter.convert(body);
    }

    static Set<String> parsePathParameters(String path) {
        Matcher m = PARAM_URL_REGEX.matcher(path);
        Set<String> patterns = new LinkedHashSet();
        while (m.find()) {
            patterns.add(m.group(1));
        }
        return patterns;
    }

    static Class<?> boxIfPrimitive(Class<?> type) {
        if (Boolean.TYPE == type) {
            return Boolean.class;
        }
        if (Byte.TYPE == type) {
            return Byte.class;
        }
        if (Character.TYPE == type) {
            return Character.class;
        }
        if (Double.TYPE == type) {
            return Double.class;
        }
        if (Float.TYPE == type) {
            return Float.class;
        }
        if (Integer.TYPE == type) {
            return Integer.class;
        }
        if (Long.TYPE == type) {
            return Long.class;
        }
        if (Short.TYPE == type) {
            return Short.class;
        }
        return type;
    }
}
