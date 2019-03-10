package retrofit2;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import okhttp3.Headers;
import okhttp3.Headers$Builder;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.ParameterHandler.RawPart;
import retrofit2.ParameterHandler.RelativeUrl;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.OPTIONS;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.QueryName;
import retrofit2.http.Url;

final class ServiceMethod$Builder<T, R> {
    CallAdapter<T, R> callAdapter;
    MediaType contentType;
    boolean gotBody;
    boolean gotField;
    boolean gotPart;
    boolean gotPath;
    boolean gotQuery;
    boolean gotUrl;
    boolean hasBody;
    Headers headers;
    String httpMethod;
    boolean isFormEncoded;
    boolean isMultipart;
    final Method method;
    final Annotation[] methodAnnotations;
    final Annotation[][] parameterAnnotationsArray;
    ParameterHandler<?>[] parameterHandlers;
    final Type[] parameterTypes;
    String relativeUrl;
    Set<String> relativeUrlParamNames;
    Converter<ResponseBody, T> responseConverter;
    Type responseType;
    final Retrofit retrofit;

    private retrofit2.ParameterHandler<?> parseParameter(int r7, java.lang.reflect.Type r8, java.lang.annotation.Annotation[] r9) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:14:0x002b in {4, 6, 7, 9, 11, 13} preds:[]
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
        r0 = 0;
        r1 = r9.length;
        r2 = 0;
        r3 = r0;
        r0 = 0;
    L_0x0005:
        if (r0 >= r1) goto L_0x001f;
    L_0x0007:
        r4 = r9[r0];
        r5 = r6.parseParameterAnnotation(r7, r8, r9, r4);
        if (r5 != 0) goto L_0x0010;
    L_0x000f:
        goto L_0x0013;
    L_0x0010:
        if (r3 != 0) goto L_0x0016;
    L_0x0012:
        r3 = r5;
    L_0x0013:
        r0 = r0 + 1;
        goto L_0x0005;
    L_0x0016:
        r0 = new java.lang.Object[r2];
        r1 = "Multiple Retrofit annotations found, only one allowed.";
        r0 = r6.parameterError(r7, r1, r0);
        throw r0;
    L_0x001f:
        if (r3 == 0) goto L_0x0022;
    L_0x0021:
        return r3;
    L_0x0022:
        r0 = new java.lang.Object[r2];
        r1 = "No Retrofit annotation found.";
        r0 = r6.parameterError(r7, r1, r0);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: retrofit2.ServiceMethod$Builder.parseParameter(int, java.lang.reflect.Type, java.lang.annotation.Annotation[]):retrofit2.ParameterHandler<?>");
    }

    public retrofit2.ServiceMethod build() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:71:0x011b in {6, 15, 17, 19, 20, 28, 30, 32, 37, 39, 48, 50, 55, 57, 62, 64, 66, 68, 70} preds:[]
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
        r0 = r7.createCallAdapter();
        r7.callAdapter = r0;
        r0 = r7.callAdapter;
        r0 = r0.responseType();
        r7.responseType = r0;
        r0 = r7.responseType;
        r1 = retrofit2.Response.class;
        r2 = 0;
        if (r0 == r1) goto L_0x00f3;
    L_0x0015:
        r1 = okhttp3.Response.class;
        if (r0 == r1) goto L_0x00f3;
    L_0x0019:
        r0 = r7.createResponseConverter();
        r7.responseConverter = r0;
        r0 = r7.methodAnnotations;
        r1 = r0.length;
        r3 = 0;
    L_0x0023:
        if (r3 >= r1) goto L_0x002d;
    L_0x0025:
        r4 = r0[r3];
        r7.parseMethodAnnotation(r4);
        r3 = r3 + 1;
        goto L_0x0023;
    L_0x002d:
        r0 = r7.httpMethod;
        if (r0 == 0) goto L_0x00ea;
    L_0x0031:
        r0 = r7.hasBody;
        if (r0 != 0) goto L_0x0050;
    L_0x0035:
        r0 = r7.isMultipart;
        if (r0 != 0) goto L_0x0047;
    L_0x0039:
        r0 = r7.isFormEncoded;
        if (r0 != 0) goto L_0x003e;
    L_0x003d:
        goto L_0x0051;
    L_0x003e:
        r0 = new java.lang.Object[r2];
        r1 = "FormUrlEncoded can only be specified on HTTP methods with request body (e.g., @POST).";
        r0 = r7.methodError(r1, r0);
        throw r0;
    L_0x0047:
        r0 = new java.lang.Object[r2];
        r1 = "Multipart can only be specified on HTTP methods with request body (e.g., @POST).";
        r0 = r7.methodError(r1, r0);
        throw r0;
    L_0x0051:
        r0 = r7.parameterAnnotationsArray;
        r0 = r0.length;
        r1 = new retrofit2.ParameterHandler[r0];
        r7.parameterHandlers = r1;
        r1 = 0;
    L_0x0059:
        r3 = 1;
        if (r1 >= r0) goto L_0x008b;
    L_0x005c:
        r4 = r7.parameterTypes;
        r4 = r4[r1];
        r5 = retrofit2.Utils.hasUnresolvableType(r4);
        if (r5 != 0) goto L_0x0080;
    L_0x0066:
        r3 = r7.parameterAnnotationsArray;
        r3 = r3[r1];
        if (r3 == 0) goto L_0x0077;
    L_0x006c:
        r5 = r7.parameterHandlers;
        r6 = r7.parseParameter(r1, r4, r3);
        r5[r1] = r6;
        r1 = r1 + 1;
        goto L_0x0059;
    L_0x0077:
        r2 = new java.lang.Object[r2];
        r5 = "No Retrofit annotation found.";
        r2 = r7.parameterError(r1, r5, r2);
        throw r2;
    L_0x0080:
        r3 = new java.lang.Object[r3];
        r3[r2] = r4;
        r2 = "Parameter type must not include a type variable or wildcard: %s";
        r2 = r7.parameterError(r1, r2, r3);
        throw r2;
        r1 = r7.relativeUrl;
        if (r1 != 0) goto L_0x00a2;
    L_0x0090:
        r1 = r7.gotUrl;
        if (r1 == 0) goto L_0x0095;
    L_0x0094:
        goto L_0x00a2;
    L_0x0095:
        r1 = new java.lang.Object[r3];
        r3 = r7.httpMethod;
        r1[r2] = r3;
        r2 = "Missing either @%s URL or @Url parameter.";
        r1 = r7.methodError(r2, r1);
        throw r1;
        r1 = r7.isFormEncoded;
        if (r1 != 0) goto L_0x00bd;
    L_0x00a7:
        r1 = r7.isMultipart;
        if (r1 != 0) goto L_0x00bd;
    L_0x00ab:
        r1 = r7.hasBody;
        if (r1 != 0) goto L_0x00bd;
    L_0x00af:
        r1 = r7.gotBody;
        if (r1 != 0) goto L_0x00b4;
    L_0x00b3:
        goto L_0x00bd;
    L_0x00b4:
        r1 = new java.lang.Object[r2];
        r2 = "Non-body HTTP method cannot contain @Body.";
        r1 = r7.methodError(r2, r1);
        throw r1;
        r1 = r7.isFormEncoded;
        if (r1 == 0) goto L_0x00d0;
    L_0x00c2:
        r1 = r7.gotField;
        if (r1 == 0) goto L_0x00c7;
    L_0x00c6:
        goto L_0x00d0;
    L_0x00c7:
        r1 = new java.lang.Object[r2];
        r2 = "Form-encoded method must contain at least one @Field.";
        r1 = r7.methodError(r2, r1);
        throw r1;
        r1 = r7.isMultipart;
        if (r1 == 0) goto L_0x00e3;
    L_0x00d5:
        r1 = r7.gotPart;
        if (r1 == 0) goto L_0x00da;
    L_0x00d9:
        goto L_0x00e3;
    L_0x00da:
        r1 = new java.lang.Object[r2];
        r2 = "Multipart method must contain at least one @Part.";
        r1 = r7.methodError(r2, r1);
        throw r1;
        r1 = new retrofit2.ServiceMethod;
        r1.<init>(r7);
        return r1;
    L_0x00ea:
        r0 = new java.lang.Object[r2];
        r1 = "HTTP method annotation is required (e.g., @GET, @POST, etc.).";
        r0 = r7.methodError(r1, r0);
        throw r0;
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "'";
        r0.append(r1);
        r1 = r7.responseType;
        r1 = retrofit2.Utils.getRawType(r1);
        r1 = r1.getName();
        r0.append(r1);
        r1 = "' is not a valid response body type. Did you mean ResponseBody?";
        r0.append(r1);
        r0 = r0.toString();
        r1 = new java.lang.Object[r2];
        r0 = r7.methodError(r0, r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: retrofit2.ServiceMethod$Builder.build():retrofit2.ServiceMethod");
    }

    ServiceMethod$Builder(Retrofit retrofit, Method method) {
        this.retrofit = retrofit;
        this.method = method;
        this.methodAnnotations = method.getAnnotations();
        this.parameterTypes = method.getGenericParameterTypes();
        this.parameterAnnotationsArray = method.getParameterAnnotations();
    }

    private CallAdapter<T, R> createCallAdapter() {
        Type returnType = this.method.getGenericReturnType();
        int i = 1;
        if (Utils.hasUnresolvableType(returnType)) {
            throw methodError("Method return type must not include a type variable or wildcard: %s", returnType);
        } else if (returnType != Void.TYPE) {
            try {
                i = this.retrofit.callAdapter(returnType, this.method.getAnnotations());
                return i;
            } catch (RuntimeException e) {
                Object[] objArr = new Object[i];
                objArr[0] = returnType;
                throw methodError(e, "Unable to create call adapter for %s", objArr);
            }
        } else {
            throw methodError("Service methods cannot return void.", new Object[0]);
        }
    }

    private void parseMethodAnnotation(Annotation annotation) {
        if (annotation instanceof DELETE) {
            parseHttpMethodAndPath("DELETE", ((DELETE) annotation).value(), false);
        } else if (annotation instanceof GET) {
            parseHttpMethodAndPath("GET", ((GET) annotation).value(), false);
        } else if (annotation instanceof HEAD) {
            parseHttpMethodAndPath("HEAD", ((HEAD) annotation).value(), false);
            if (!Void.class.equals(this.responseType)) {
                throw methodError("HEAD method must use Void as response type.", new Object[0]);
            }
        } else if (annotation instanceof PATCH) {
            parseHttpMethodAndPath("PATCH", ((PATCH) annotation).value(), true);
        } else if (annotation instanceof POST) {
            parseHttpMethodAndPath("POST", ((POST) annotation).value(), true);
        } else if (annotation instanceof PUT) {
            parseHttpMethodAndPath("PUT", ((PUT) annotation).value(), true);
        } else if (annotation instanceof OPTIONS) {
            parseHttpMethodAndPath("OPTIONS", ((OPTIONS) annotation).value(), false);
        } else if (annotation instanceof HTTP) {
            HTTP http = (HTTP) annotation;
            parseHttpMethodAndPath(http.method(), http.path(), http.hasBody());
        } else if (annotation instanceof retrofit2.http.Headers) {
            String[] headersToParse = ((retrofit2.http.Headers) annotation).value();
            if (headersToParse.length != 0) {
                this.headers = parseHeaders(headersToParse);
            } else {
                throw methodError("@Headers annotation is empty.", new Object[0]);
            }
        } else if (annotation instanceof Multipart) {
            if (this.isFormEncoded) {
                throw methodError("Only one encoding annotation is allowed.", new Object[0]);
            } else {
                this.isMultipart = true;
            }
        } else if (!(annotation instanceof FormUrlEncoded)) {
        } else {
            if (this.isMultipart) {
                throw methodError("Only one encoding annotation is allowed.", new Object[0]);
            } else {
                this.isFormEncoded = true;
            }
        }
    }

    private void parseHttpMethodAndPath(String httpMethod, String value, boolean hasBody) {
        if (this.httpMethod == null) {
            this.httpMethod = httpMethod;
            this.hasBody = hasBody;
            if (!value.isEmpty()) {
                int question = value.indexOf(63);
                if (question != -1 && question < value.length() - 1) {
                    if (ServiceMethod.PARAM_URL_REGEX.matcher(value.substring(question + 1)).find()) {
                        throw methodError("URL query string \"%s\" must not have replace block. For dynamic query parameters use @Query.", value.substring(question + 1));
                    }
                }
                this.relativeUrl = value;
                this.relativeUrlParamNames = ServiceMethod.parsePathParameters(value);
                return;
            }
            return;
        }
        throw methodError("Only one HTTP method is allowed. Found: %s and %s.", this.httpMethod, httpMethod);
    }

    private Headers parseHeaders(String[] headers) {
        Headers$Builder builder = new Headers$Builder();
        for (String header : headers) {
            int colon = header.indexOf(58);
            if (colon == -1 || colon == 0 || colon == header.length() - 1) {
                throw methodError("@Headers value must be in the form \"Name: Value\". Found: \"%s\"", header);
            }
            String headerName = header.substring(0, colon);
            String headerValue = header.substring(colon + 1).trim();
            if ("Content-Type".equalsIgnoreCase(headerName)) {
                MediaType type = MediaType.parse(headerValue);
                if (type != null) {
                    this.contentType = type;
                } else {
                    throw methodError("Malformed content type: %s", headerValue);
                }
            }
            builder.add(headerName, headerValue);
        }
        return builder.build();
    }

    private ParameterHandler<?> parseParameterAnnotation(int p, Type type, Annotation[] annotations, Annotation annotation) {
        if (annotation instanceof Url) {
            if (this.gotUrl) {
                throw parameterError(p, "Multiple @Url method annotations found.", new Object[0]);
            } else if (this.gotPath) {
                throw parameterError(p, "@Path parameters may not be used with @Url.", new Object[0]);
            } else if (this.gotQuery) {
                throw parameterError(p, "A @Url parameter must not come after a @Query", new Object[0]);
            } else if (this.relativeUrl == null) {
                this.gotUrl = true;
                if (type != HttpUrl.class && type != String.class && type != URI.class) {
                    if (type instanceof Class) {
                        if ("android.net.Uri".equals(((Class) type).getName())) {
                        }
                    }
                    throw parameterError(p, "@Url must be okhttp3.HttpUrl, String, java.net.URI, or android.net.Uri type.", new Object[0]);
                }
                return new RelativeUrl();
            } else {
                throw parameterError(p, "@Url cannot be used with @%s URL", this.httpMethod);
            }
        } else if (annotation instanceof Path) {
            if (this.gotQuery) {
                throw parameterError(p, "A @Path parameter must not come after a @Query.", new Object[0]);
            } else if (this.gotUrl) {
                throw parameterError(p, "@Path parameters may not be used with @Url.", new Object[0]);
            } else if (this.relativeUrl != null) {
                this.gotPath = true;
                Path path = (Path) annotation;
                name = path.value();
                validatePathName(p, name);
                return new ParameterHandler.Path(name, this.retrofit.stringConverter(type, annotations), path.encoded());
            } else {
                throw parameterError(p, "@Path can only be used with relative url on @%s", this.httpMethod);
            }
        } else if (annotation instanceof Query) {
            Query query = (Query) annotation;
            name = query.value();
            encoded = query.encoded();
            rawParameterType = Utils.getRawType(type);
            this.gotQuery = true;
            if (Iterable.class.isAssignableFrom(rawParameterType)) {
                if (type instanceof ParameterizedType) {
                    return new ParameterHandler.Query(name, this.retrofit.stringConverter(Utils.getParameterUpperBound(0, (ParameterizedType) type), annotations), encoded).iterable();
                }
                r1 = new StringBuilder();
                r1.append(rawParameterType.getSimpleName());
                r1.append(" must include generic type (e.g., ");
                r1.append(rawParameterType.getSimpleName());
                r1.append("<String>)");
                throw parameterError(p, r1.toString(), new Object[0]);
            } else if (!rawParameterType.isArray()) {
                return new ParameterHandler.Query(name, this.retrofit.stringConverter(type, annotations), encoded);
            } else {
                return new ParameterHandler.Query(name, this.retrofit.stringConverter(ServiceMethod.boxIfPrimitive(rawParameterType.getComponentType()), annotations), encoded).array();
            }
        } else if (annotation instanceof QueryName) {
            boolean encoded = ((QueryName) annotation).encoded();
            rawParameterType = Utils.getRawType(type);
            this.gotQuery = true;
            if (Iterable.class.isAssignableFrom(rawParameterType)) {
                if (type instanceof ParameterizedType) {
                    return new ParameterHandler.QueryName(this.retrofit.stringConverter(Utils.getParameterUpperBound(0, (ParameterizedType) type), annotations), encoded).iterable();
                }
                r1 = new StringBuilder();
                r1.append(rawParameterType.getSimpleName());
                r1.append(" must include generic type (e.g., ");
                r1.append(rawParameterType.getSimpleName());
                r1.append("<String>)");
                throw parameterError(p, r1.toString(), new Object[0]);
            } else if (!rawParameterType.isArray()) {
                return new ParameterHandler.QueryName(this.retrofit.stringConverter(type, annotations), encoded);
            } else {
                return new ParameterHandler.QueryName(this.retrofit.stringConverter(ServiceMethod.boxIfPrimitive(rawParameterType.getComponentType()), annotations), encoded).array();
            }
        } else if (annotation instanceof QueryMap) {
            rawParameterType = Utils.getRawType(type);
            if (Map.class.isAssignableFrom(rawParameterType)) {
                mapType = Utils.getSupertype(type, rawParameterType, Map.class);
                if (mapType instanceof ParameterizedType) {
                    parameterizedType = (ParameterizedType) mapType;
                    keyType = Utils.getParameterUpperBound(0, parameterizedType);
                    if (String.class == keyType) {
                        return new ParameterHandler.QueryMap(this.retrofit.stringConverter(Utils.getParameterUpperBound(1, parameterizedType), annotations), ((QueryMap) annotation).encoded());
                    }
                    r1 = new StringBuilder();
                    r1.append("@QueryMap keys must be of type String: ");
                    r1.append(keyType);
                    throw parameterError(p, r1.toString(), new Object[0]);
                }
                throw parameterError(p, "Map must include generic types (e.g., Map<String, String>)", new Object[0]);
            }
            throw parameterError(p, "@QueryMap parameter type must be Map.", new Object[0]);
        } else if (annotation instanceof Header) {
            name = ((Header) annotation).value();
            Class<?> rawParameterType = Utils.getRawType(type);
            if (Iterable.class.isAssignableFrom(rawParameterType)) {
                if (type instanceof ParameterizedType) {
                    return new ParameterHandler.Header(name, this.retrofit.stringConverter(Utils.getParameterUpperBound(0, (ParameterizedType) type), annotations)).iterable();
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(rawParameterType.getSimpleName());
                stringBuilder.append(" must include generic type (e.g., ");
                stringBuilder.append(rawParameterType.getSimpleName());
                stringBuilder.append("<String>)");
                throw parameterError(p, stringBuilder.toString(), new Object[0]);
            } else if (!rawParameterType.isArray()) {
                return new ParameterHandler.Header(name, this.retrofit.stringConverter(type, annotations));
            } else {
                return new ParameterHandler.Header(name, this.retrofit.stringConverter(ServiceMethod.boxIfPrimitive(rawParameterType.getComponentType()), annotations)).array();
            }
        } else if (annotation instanceof HeaderMap) {
            rawParameterType = Utils.getRawType(type);
            if (Map.class.isAssignableFrom(rawParameterType)) {
                mapType = Utils.getSupertype(type, rawParameterType, Map.class);
                if (mapType instanceof ParameterizedType) {
                    parameterizedType = (ParameterizedType) mapType;
                    keyType = Utils.getParameterUpperBound(0, parameterizedType);
                    if (String.class == keyType) {
                        return new ParameterHandler.HeaderMap(this.retrofit.stringConverter(Utils.getParameterUpperBound(1, parameterizedType), annotations));
                    }
                    r1 = new StringBuilder();
                    r1.append("@HeaderMap keys must be of type String: ");
                    r1.append(keyType);
                    throw parameterError(p, r1.toString(), new Object[0]);
                }
                throw parameterError(p, "Map must include generic types (e.g., Map<String, String>)", new Object[0]);
            }
            throw parameterError(p, "@HeaderMap parameter type must be Map.", new Object[0]);
        } else if (annotation instanceof Field) {
            if (this.isFormEncoded) {
                Field field = (Field) annotation;
                name = field.value();
                encoded = field.encoded();
                this.gotField = true;
                Class<?> rawParameterType2 = Utils.getRawType(type);
                if (Iterable.class.isAssignableFrom(rawParameterType2)) {
                    if (type instanceof ParameterizedType) {
                        return new ParameterHandler.Field(name, this.retrofit.stringConverter(Utils.getParameterUpperBound(0, (ParameterizedType) type), annotations), encoded).iterable();
                    }
                    r5 = new StringBuilder();
                    r5.append(rawParameterType2.getSimpleName());
                    r5.append(" must include generic type (e.g., ");
                    r5.append(rawParameterType2.getSimpleName());
                    r5.append("<String>)");
                    throw parameterError(p, r5.toString(), new Object[0]);
                } else if (!rawParameterType2.isArray()) {
                    return new ParameterHandler.Field(name, this.retrofit.stringConverter(type, annotations), encoded);
                } else {
                    return new ParameterHandler.Field(name, this.retrofit.stringConverter(ServiceMethod.boxIfPrimitive(rawParameterType2.getComponentType()), annotations), encoded).array();
                }
            }
            throw parameterError(p, "@Field parameters can only be used with form encoding.", new Object[0]);
        } else if (annotation instanceof FieldMap) {
            if (this.isFormEncoded) {
                rawParameterType = Utils.getRawType(type);
                if (Map.class.isAssignableFrom(rawParameterType)) {
                    mapType = Utils.getSupertype(type, rawParameterType, Map.class);
                    if (mapType instanceof ParameterizedType) {
                        parameterizedType = (ParameterizedType) mapType;
                        keyType = Utils.getParameterUpperBound(0, parameterizedType);
                        if (String.class == keyType) {
                            Converter<?, String> valueConverter = this.retrofit.stringConverter(Utils.getParameterUpperBound(1, parameterizedType), annotations);
                            this.gotField = true;
                            return new ParameterHandler.FieldMap(valueConverter, ((FieldMap) annotation).encoded());
                        }
                        r1 = new StringBuilder();
                        r1.append("@FieldMap keys must be of type String: ");
                        r1.append(keyType);
                        throw parameterError(p, r1.toString(), new Object[0]);
                    }
                    throw parameterError(p, "Map must include generic types (e.g., Map<String, String>)", new Object[0]);
                }
                throw parameterError(p, "@FieldMap parameter type must be Map.", new Object[0]);
            }
            throw parameterError(p, "@FieldMap parameters can only be used with form encoding.", new Object[0]);
        } else if (annotation instanceof Part) {
            if (this.isMultipart) {
                Part part = (Part) annotation;
                this.gotPart = true;
                name = part.value();
                rawParameterType = Utils.getRawType(type);
                if (!name.isEmpty()) {
                    String[] strArr = new String[4];
                    strArr[0] = "Content-Disposition";
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("form-data; name=\"");
                    stringBuilder2.append(name);
                    stringBuilder2.append("\"");
                    strArr[1] = stringBuilder2.toString();
                    strArr[2] = "Content-Transfer-Encoding";
                    strArr[3] = part.encoding();
                    Headers headers = Headers.of(strArr);
                    if (Iterable.class.isAssignableFrom(rawParameterType)) {
                        if (type instanceof ParameterizedType) {
                            Type iterableType = Utils.getParameterUpperBound(0, (ParameterizedType) type);
                            if (!MultipartBody.Part.class.isAssignableFrom(Utils.getRawType(iterableType))) {
                                return new ParameterHandler.Part(headers, this.retrofit.requestBodyConverter(iterableType, annotations, this.methodAnnotations)).iterable();
                            }
                            throw parameterError(p, "@Part parameters using the MultipartBody.Part must not include a part name in the annotation.", new Object[0]);
                        }
                        r5 = new StringBuilder();
                        r5.append(rawParameterType.getSimpleName());
                        r5.append(" must include generic type (e.g., ");
                        r5.append(rawParameterType.getSimpleName());
                        r5.append("<String>)");
                        throw parameterError(p, r5.toString(), new Object[0]);
                    } else if (rawParameterType.isArray()) {
                        rawParameterType = ServiceMethod.boxIfPrimitive(rawParameterType.getComponentType());
                        if (!MultipartBody.Part.class.isAssignableFrom(rawParameterType)) {
                            return new ParameterHandler.Part(headers, this.retrofit.requestBodyConverter(rawParameterType, annotations, this.methodAnnotations)).array();
                        }
                        throw parameterError(p, "@Part parameters using the MultipartBody.Part must not include a part name in the annotation.", new Object[0]);
                    } else if (!MultipartBody.Part.class.isAssignableFrom(rawParameterType)) {
                        return new ParameterHandler.Part(headers, this.retrofit.requestBodyConverter(type, annotations, this.methodAnnotations));
                    } else {
                        throw parameterError(p, "@Part parameters using the MultipartBody.Part must not include a part name in the annotation.", new Object[0]);
                    }
                } else if (Iterable.class.isAssignableFrom(rawParameterType)) {
                    if (!(type instanceof ParameterizedType)) {
                        r1 = new StringBuilder();
                        r1.append(rawParameterType.getSimpleName());
                        r1.append(" must include generic type (e.g., ");
                        r1.append(rawParameterType.getSimpleName());
                        r1.append("<String>)");
                        throw parameterError(p, r1.toString(), new Object[0]);
                    } else if (MultipartBody.Part.class.isAssignableFrom(Utils.getRawType(Utils.getParameterUpperBound(0, (ParameterizedType) type)))) {
                        return RawPart.INSTANCE.iterable();
                    } else {
                        throw parameterError(p, "@Part annotation must supply a name or use MultipartBody.Part parameter type.", new Object[0]);
                    }
                } else if (rawParameterType.isArray()) {
                    if (MultipartBody.Part.class.isAssignableFrom(rawParameterType.getComponentType())) {
                        return RawPart.INSTANCE.array();
                    }
                    throw parameterError(p, "@Part annotation must supply a name or use MultipartBody.Part parameter type.", new Object[0]);
                } else if (MultipartBody.Part.class.isAssignableFrom(rawParameterType)) {
                    return RawPart.INSTANCE;
                } else {
                    throw parameterError(p, "@Part annotation must supply a name or use MultipartBody.Part parameter type.", new Object[0]);
                }
            }
            throw parameterError(p, "@Part parameters can only be used with multipart encoding.", new Object[0]);
        } else if (annotation instanceof PartMap) {
            if (this.isMultipart) {
                this.gotPart = true;
                rawParameterType = Utils.getRawType(type);
                if (Map.class.isAssignableFrom(rawParameterType)) {
                    mapType = Utils.getSupertype(type, rawParameterType, Map.class);
                    if (mapType instanceof ParameterizedType) {
                        parameterizedType = (ParameterizedType) mapType;
                        keyType = Utils.getParameterUpperBound(0, parameterizedType);
                        if (String.class == keyType) {
                            Type valueType = Utils.getParameterUpperBound(1, parameterizedType);
                            if (!MultipartBody.Part.class.isAssignableFrom(Utils.getRawType(valueType))) {
                                return new ParameterHandler.PartMap(this.retrofit.requestBodyConverter(valueType, annotations, this.methodAnnotations), ((PartMap) annotation).encoding());
                            }
                            throw parameterError(p, "@PartMap values cannot be MultipartBody.Part. Use @Part List<Part> or a different value type instead.", new Object[0]);
                        }
                        r1 = new StringBuilder();
                        r1.append("@PartMap keys must be of type String: ");
                        r1.append(keyType);
                        throw parameterError(p, r1.toString(), new Object[0]);
                    }
                    throw parameterError(p, "Map must include generic types (e.g., Map<String, String>)", new Object[0]);
                }
                throw parameterError(p, "@PartMap parameter type must be Map.", new Object[0]);
            }
            throw parameterError(p, "@PartMap parameters can only be used with multipart encoding.", new Object[0]);
        } else if (!(annotation instanceof Body)) {
            return null;
        } else {
            if (this.isFormEncoded || this.isMultipart) {
                throw parameterError(p, "@Body parameters cannot be used with form or multi-part encoding.", new Object[0]);
            } else if (this.gotBody) {
                throw parameterError(p, "Multiple @Body method annotations found.", new Object[0]);
            } else {
                try {
                    Converter<?, RequestBody> converter = this.retrofit.requestBodyConverter(type, annotations, this.methodAnnotations);
                    this.gotBody = true;
                    return new ParameterHandler.Body(converter);
                } catch (RuntimeException e) {
                    throw parameterError(e, p, "Unable to create @Body converter for %s", type);
                }
            }
        }
    }

    private void validatePathName(int p, String name) {
        if (!ServiceMethod.PARAM_NAME_REGEX.matcher(name).matches()) {
            throw parameterError(p, "@Path parameter name must match %s. Found: %s", ServiceMethod.PARAM_URL_REGEX.pattern(), name);
        } else if (!this.relativeUrlParamNames.contains(name)) {
            throw parameterError(p, "URL \"%s\" does not contain \"{%s}\".", this.relativeUrl, name);
        }
    }

    private Converter<ResponseBody, T> createResponseConverter() {
        try {
            return this.retrofit.responseBodyConverter(this.responseType, this.method.getAnnotations());
        } catch (RuntimeException e) {
            throw methodError(e, "Unable to create converter for %s", this.responseType);
        }
    }

    private RuntimeException methodError(String message, Object... args) {
        return methodError(null, message, args);
    }

    private RuntimeException methodError(Throwable cause, String message, Object... args) {
        message = String.format(message, args);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(message);
        stringBuilder.append("\n    for method ");
        stringBuilder.append(this.method.getDeclaringClass().getSimpleName());
        stringBuilder.append(".");
        stringBuilder.append(this.method.getName());
        return new IllegalArgumentException(stringBuilder.toString(), cause);
    }

    private RuntimeException parameterError(Throwable cause, int p, String message, Object... args) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(message);
        stringBuilder.append(" (parameter #");
        stringBuilder.append(p + 1);
        stringBuilder.append(")");
        return methodError(cause, stringBuilder.toString(), args);
    }

    private RuntimeException parameterError(int p, String message, Object... args) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(message);
        stringBuilder.append(" (parameter #");
        stringBuilder.append(p + 1);
        stringBuilder.append(")");
        return methodError(stringBuilder.toString(), args);
    }
}
