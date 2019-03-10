package retrofit2;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import okhttp3.Call$Factory;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.BuiltInConverters.ToStringConverter;
import retrofit2.CallAdapter.Factory;

public final class Retrofit {
    final HttpUrl baseUrl;
    final List<Factory> callAdapterFactories;
    final Call$Factory callFactory;
    @Nullable
    final Executor callbackExecutor;
    final List<Converter$Factory> converterFactories;
    private final Map<Method, ServiceMethod<?, ?>> serviceMethodCache = new ConcurrentHashMap();
    final boolean validateEagerly;

    public static final class Builder {
        private HttpUrl baseUrl;
        private final List<Factory> callAdapterFactories;
        @Nullable
        private Call$Factory callFactory;
        @Nullable
        private Executor callbackExecutor;
        private final List<Converter$Factory> converterFactories;
        private final Platform platform;
        private boolean validateEagerly;

        Builder(Platform platform) {
            this.converterFactories = new ArrayList();
            this.callAdapterFactories = new ArrayList();
            this.platform = platform;
        }

        public Builder() {
            this(Platform.get());
        }

        Builder(Retrofit retrofit) {
            this.converterFactories = new ArrayList();
            this.callAdapterFactories = new ArrayList();
            this.platform = Platform.get();
            this.callFactory = retrofit.callFactory;
            this.baseUrl = retrofit.baseUrl;
            this.converterFactories.addAll(retrofit.converterFactories);
            this.converterFactories.remove(0);
            this.callAdapterFactories.addAll(retrofit.callAdapterFactories);
            List list = this.callAdapterFactories;
            list.remove(list.size() - 1);
            this.callbackExecutor = retrofit.callbackExecutor;
            this.validateEagerly = retrofit.validateEagerly;
        }

        public Builder client(OkHttpClient client) {
            return callFactory((Call$Factory) Utils.checkNotNull(client, "client == null"));
        }

        public Builder callFactory(Call$Factory factory) {
            this.callFactory = (Call$Factory) Utils.checkNotNull(factory, "factory == null");
            return this;
        }

        public Builder baseUrl(String baseUrl) {
            Utils.checkNotNull(baseUrl, "baseUrl == null");
            HttpUrl httpUrl = HttpUrl.parse(baseUrl);
            if (httpUrl != null) {
                return baseUrl(httpUrl);
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Illegal URL: ");
            stringBuilder.append(baseUrl);
            throw new IllegalArgumentException(stringBuilder.toString());
        }

        public Builder baseUrl(HttpUrl baseUrl) {
            Utils.checkNotNull(baseUrl, "baseUrl == null");
            List<String> pathSegments = baseUrl.pathSegments();
            if ("".equals(pathSegments.get(pathSegments.size() - 1))) {
                this.baseUrl = baseUrl;
                return this;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("baseUrl must end in /: ");
            stringBuilder.append(baseUrl);
            throw new IllegalArgumentException(stringBuilder.toString());
        }

        public Builder addConverterFactory(Converter$Factory factory) {
            this.converterFactories.add(Utils.checkNotNull(factory, "factory == null"));
            return this;
        }

        public Builder addCallAdapterFactory(Factory factory) {
            this.callAdapterFactories.add(Utils.checkNotNull(factory, "factory == null"));
            return this;
        }

        public Builder callbackExecutor(Executor executor) {
            this.callbackExecutor = (Executor) Utils.checkNotNull(executor, "executor == null");
            return this;
        }

        public List<Factory> callAdapterFactories() {
            return this.callAdapterFactories;
        }

        public List<Converter$Factory> converterFactories() {
            return this.converterFactories;
        }

        public Builder validateEagerly(boolean validateEagerly) {
            this.validateEagerly = validateEagerly;
            return this;
        }

        public Retrofit build() {
            if (this.baseUrl != null) {
                Executor callbackExecutor;
                Call$Factory callFactory = this.callFactory;
                if (callFactory == null) {
                    callFactory = new OkHttpClient();
                }
                Executor callbackExecutor2 = this.callbackExecutor;
                if (callbackExecutor2 == null) {
                    callbackExecutor = this.platform.defaultCallbackExecutor();
                } else {
                    callbackExecutor = callbackExecutor2;
                }
                ArrayList callAdapterFactories = new ArrayList(this.callAdapterFactories);
                callAdapterFactories.add(this.platform.defaultCallAdapterFactory(callbackExecutor));
                ArrayList converterFactories = new ArrayList(this.converterFactories.size() + 1);
                converterFactories.add(new BuiltInConverters());
                converterFactories.addAll(this.converterFactories);
                return new Retrofit(callFactory, this.baseUrl, Collections.unmodifiableList(converterFactories), Collections.unmodifiableList(callAdapterFactories), callbackExecutor, this.validateEagerly);
            }
            throw new IllegalStateException("Base URL required.");
        }
    }

    public retrofit2.CallAdapter<?, ?> nextCallAdapter(@javax.annotation.Nullable retrofit2.CallAdapter.Factory r6, java.lang.reflect.Type r7, java.lang.annotation.Annotation[] r8) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:18:0x009d in {4, 5, 10, 11, 12, 15, 17} preds:[]
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
        r5 = this;
        r0 = "returnType == null";
        retrofit2.Utils.checkNotNull(r7, r0);
        r0 = "annotations == null";
        retrofit2.Utils.checkNotNull(r8, r0);
        r0 = r5.callAdapterFactories;
        r0 = r0.indexOf(r6);
        r0 = r0 + 1;
        r1 = r0;
        r2 = r5.callAdapterFactories;
        r2 = r2.size();
    L_0x0019:
        if (r1 >= r2) goto L_0x002e;
    L_0x001b:
        r3 = r5.callAdapterFactories;
        r3 = r3.get(r1);
        r3 = (retrofit2.CallAdapter.Factory) r3;
        r3 = r3.get(r7, r8, r5);
        if (r3 == 0) goto L_0x002a;
    L_0x0029:
        return r3;
        r1 = r1 + 1;
        goto L_0x0019;
    L_0x002e:
        r1 = new java.lang.StringBuilder;
        r2 = "Could not locate call adapter for ";
        r1.<init>(r2);
        r1.append(r7);
        r2 = ".\n";
        r1 = r1.append(r2);
        if (r6 == 0) goto L_0x0069;
    L_0x0040:
        r2 = "  Skipped:";
        r1.append(r2);
        r2 = 0;
    L_0x0046:
        if (r2 >= r0) goto L_0x0063;
    L_0x0048:
        r3 = "\n   * ";
        r1.append(r3);
        r3 = r5.callAdapterFactories;
        r3 = r3.get(r2);
        r3 = (retrofit2.CallAdapter.Factory) r3;
        r3 = r3.getClass();
        r3 = r3.getName();
        r1.append(r3);
        r2 = r2 + 1;
        goto L_0x0046;
    L_0x0063:
        r2 = 10;
        r1.append(r2);
        goto L_0x006a;
    L_0x006a:
        r2 = "  Tried:";
        r1.append(r2);
        r2 = r0;
        r3 = r5.callAdapterFactories;
        r3 = r3.size();
    L_0x0076:
        if (r2 >= r3) goto L_0x0093;
    L_0x0078:
        r4 = "\n   * ";
        r1.append(r4);
        r4 = r5.callAdapterFactories;
        r4 = r4.get(r2);
        r4 = (retrofit2.CallAdapter.Factory) r4;
        r4 = r4.getClass();
        r4 = r4.getName();
        r1.append(r4);
        r2 = r2 + 1;
        goto L_0x0076;
    L_0x0093:
        r2 = new java.lang.IllegalArgumentException;
        r3 = r1.toString();
        r2.<init>(r3);
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: retrofit2.Retrofit.nextCallAdapter(retrofit2.CallAdapter$Factory, java.lang.reflect.Type, java.lang.annotation.Annotation[]):retrofit2.CallAdapter<?, ?>");
    }

    public <T> retrofit2.Converter<T, okhttp3.RequestBody> nextRequestBodyConverter(@javax.annotation.Nullable retrofit2.Converter$Factory r6, java.lang.reflect.Type r7, java.lang.annotation.Annotation[] r8, java.lang.annotation.Annotation[] r9) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:18:0x00a3 in {4, 5, 10, 11, 12, 15, 17} preds:[]
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
        r5 = this;
        r0 = "type == null";
        retrofit2.Utils.checkNotNull(r7, r0);
        r0 = "parameterAnnotations == null";
        retrofit2.Utils.checkNotNull(r8, r0);
        r0 = "methodAnnotations == null";
        retrofit2.Utils.checkNotNull(r9, r0);
        r0 = r5.converterFactories;
        r0 = r0.indexOf(r6);
        r0 = r0 + 1;
        r1 = r0;
        r2 = r5.converterFactories;
        r2 = r2.size();
    L_0x001e:
        if (r1 >= r2) goto L_0x0034;
    L_0x0020:
        r3 = r5.converterFactories;
        r3 = r3.get(r1);
        r3 = (retrofit2.Converter$Factory) r3;
        r4 = r3.requestBodyConverter(r7, r8, r9, r5);
        if (r4 == 0) goto L_0x0030;
    L_0x002f:
        return r4;
        r1 = r1 + 1;
        goto L_0x001e;
    L_0x0034:
        r1 = new java.lang.StringBuilder;
        r2 = "Could not locate RequestBody converter for ";
        r1.<init>(r2);
        r1.append(r7);
        r2 = ".\n";
        r1 = r1.append(r2);
        if (r6 == 0) goto L_0x006f;
    L_0x0046:
        r2 = "  Skipped:";
        r1.append(r2);
        r2 = 0;
    L_0x004c:
        if (r2 >= r0) goto L_0x0069;
    L_0x004e:
        r3 = "\n   * ";
        r1.append(r3);
        r3 = r5.converterFactories;
        r3 = r3.get(r2);
        r3 = (retrofit2.Converter$Factory) r3;
        r3 = r3.getClass();
        r3 = r3.getName();
        r1.append(r3);
        r2 = r2 + 1;
        goto L_0x004c;
    L_0x0069:
        r2 = 10;
        r1.append(r2);
        goto L_0x0070;
    L_0x0070:
        r2 = "  Tried:";
        r1.append(r2);
        r2 = r0;
        r3 = r5.converterFactories;
        r3 = r3.size();
    L_0x007c:
        if (r2 >= r3) goto L_0x0099;
    L_0x007e:
        r4 = "\n   * ";
        r1.append(r4);
        r4 = r5.converterFactories;
        r4 = r4.get(r2);
        r4 = (retrofit2.Converter$Factory) r4;
        r4 = r4.getClass();
        r4 = r4.getName();
        r1.append(r4);
        r2 = r2 + 1;
        goto L_0x007c;
    L_0x0099:
        r2 = new java.lang.IllegalArgumentException;
        r3 = r1.toString();
        r2.<init>(r3);
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: retrofit2.Retrofit.nextRequestBodyConverter(retrofit2.Converter$Factory, java.lang.reflect.Type, java.lang.annotation.Annotation[], java.lang.annotation.Annotation[]):retrofit2.Converter<T, okhttp3.RequestBody>");
    }

    public <T> retrofit2.Converter<okhttp3.ResponseBody, T> nextResponseBodyConverter(@javax.annotation.Nullable retrofit2.Converter$Factory r6, java.lang.reflect.Type r7, java.lang.annotation.Annotation[] r8) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:18:0x009d in {4, 5, 10, 11, 12, 15, 17} preds:[]
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
        r5 = this;
        r0 = "type == null";
        retrofit2.Utils.checkNotNull(r7, r0);
        r0 = "annotations == null";
        retrofit2.Utils.checkNotNull(r8, r0);
        r0 = r5.converterFactories;
        r0 = r0.indexOf(r6);
        r0 = r0 + 1;
        r1 = r0;
        r2 = r5.converterFactories;
        r2 = r2.size();
    L_0x0019:
        if (r1 >= r2) goto L_0x002e;
    L_0x001b:
        r3 = r5.converterFactories;
        r3 = r3.get(r1);
        r3 = (retrofit2.Converter$Factory) r3;
        r3 = r3.responseBodyConverter(r7, r8, r5);
        if (r3 == 0) goto L_0x002a;
    L_0x0029:
        return r3;
        r1 = r1 + 1;
        goto L_0x0019;
    L_0x002e:
        r1 = new java.lang.StringBuilder;
        r2 = "Could not locate ResponseBody converter for ";
        r1.<init>(r2);
        r1.append(r7);
        r2 = ".\n";
        r1 = r1.append(r2);
        if (r6 == 0) goto L_0x0069;
    L_0x0040:
        r2 = "  Skipped:";
        r1.append(r2);
        r2 = 0;
    L_0x0046:
        if (r2 >= r0) goto L_0x0063;
    L_0x0048:
        r3 = "\n   * ";
        r1.append(r3);
        r3 = r5.converterFactories;
        r3 = r3.get(r2);
        r3 = (retrofit2.Converter$Factory) r3;
        r3 = r3.getClass();
        r3 = r3.getName();
        r1.append(r3);
        r2 = r2 + 1;
        goto L_0x0046;
    L_0x0063:
        r2 = 10;
        r1.append(r2);
        goto L_0x006a;
    L_0x006a:
        r2 = "  Tried:";
        r1.append(r2);
        r2 = r0;
        r3 = r5.converterFactories;
        r3 = r3.size();
    L_0x0076:
        if (r2 >= r3) goto L_0x0093;
    L_0x0078:
        r4 = "\n   * ";
        r1.append(r4);
        r4 = r5.converterFactories;
        r4 = r4.get(r2);
        r4 = (retrofit2.Converter$Factory) r4;
        r4 = r4.getClass();
        r4 = r4.getName();
        r1.append(r4);
        r2 = r2 + 1;
        goto L_0x0076;
    L_0x0093:
        r2 = new java.lang.IllegalArgumentException;
        r3 = r1.toString();
        r2.<init>(r3);
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: retrofit2.Retrofit.nextResponseBodyConverter(retrofit2.Converter$Factory, java.lang.reflect.Type, java.lang.annotation.Annotation[]):retrofit2.Converter<okhttp3.ResponseBody, T>");
    }

    Retrofit(Call$Factory callFactory, HttpUrl baseUrl, List<Converter$Factory> converterFactories, List<Factory> callAdapterFactories, @Nullable Executor callbackExecutor, boolean validateEagerly) {
        this.callFactory = callFactory;
        this.baseUrl = baseUrl;
        this.converterFactories = converterFactories;
        this.callAdapterFactories = callAdapterFactories;
        this.callbackExecutor = callbackExecutor;
        this.validateEagerly = validateEagerly;
    }

    public <T> T create(final Class<T> service) {
        Utils.validateServiceInterface(service);
        if (this.validateEagerly) {
            eagerlyValidateMethods(service);
        }
        return Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, new InvocationHandler() {
            private final Platform platform = Platform.get();

            public Object invoke(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
                if (method.getDeclaringClass() == Object.class) {
                    return method.invoke(this, args);
                }
                if (this.platform.isDefaultMethod(method)) {
                    return this.platform.invokeDefaultMethod(method, service, proxy, args);
                }
                ServiceMethod<Object, Object> serviceMethod = Retrofit.this.loadServiceMethod(method);
                return serviceMethod.adapt(new OkHttpCall(serviceMethod, args));
            }
        });
    }

    private void eagerlyValidateMethods(Class<?> service) {
        Platform platform = Platform.get();
        for (Method method : service.getDeclaredMethods()) {
            if (!platform.isDefaultMethod(method)) {
                loadServiceMethod(method);
            }
        }
    }

    ServiceMethod<?, ?> loadServiceMethod(Method method) {
        ServiceMethod<?, ?> result = (ServiceMethod) this.serviceMethodCache.get(method);
        if (result != null) {
            return result;
        }
        synchronized (this.serviceMethodCache) {
            result = (ServiceMethod) this.serviceMethodCache.get(method);
            if (result == null) {
                result = new ServiceMethod$Builder(this, method).build();
                this.serviceMethodCache.put(method, result);
            }
        }
        return result;
    }

    public Call$Factory callFactory() {
        return this.callFactory;
    }

    public HttpUrl baseUrl() {
        return this.baseUrl;
    }

    public List<Factory> callAdapterFactories() {
        return this.callAdapterFactories;
    }

    public CallAdapter<?, ?> callAdapter(Type returnType, Annotation[] annotations) {
        return nextCallAdapter(null, returnType, annotations);
    }

    public List<Converter$Factory> converterFactories() {
        return this.converterFactories;
    }

    public <T> Converter<T, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations) {
        return nextRequestBodyConverter(null, type, parameterAnnotations, methodAnnotations);
    }

    public <T> Converter<ResponseBody, T> responseBodyConverter(Type type, Annotation[] annotations) {
        return nextResponseBodyConverter(null, type, annotations);
    }

    public <T> Converter<T, String> stringConverter(Type type, Annotation[] annotations) {
        Utils.checkNotNull(type, "type == null");
        Utils.checkNotNull(annotations, "annotations == null");
        int count = this.converterFactories.size();
        for (int i = 0; i < count; i++) {
            Converter<?, String> converter = ((Converter$Factory) this.converterFactories.get(i)).stringConverter(type, annotations, this);
            if (converter != null) {
                return converter;
            }
        }
        return ToStringConverter.INSTANCE;
    }

    @Nullable
    public Executor callbackExecutor() {
        return this.callbackExecutor;
    }

    public Builder newBuilder() {
        return new Builder(this);
    }
}
