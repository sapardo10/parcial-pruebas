package com.bumptech.glide.module;

import android.content.Context;

@Deprecated
public final class ManifestParser {
    private static final String GLIDE_MODULE_VALUE = "GlideModule";
    private static final String TAG = "ManifestParser";
    private final Context context;

    private static com.bumptech.glide.module.GlideModule parseModule(java.lang.String r5) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:24:0x0051 in {5, 6, 8, 10, 12, 14, 18, 20, 23} preds:[]
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
        r0 = java.lang.Class.forName(r5);	 Catch:{ ClassNotFoundException -> 0x0048 }
        r1 = 0;
        r2 = 0;
        r3 = new java.lang.Class[r2];	 Catch:{ InstantiationException -> 0x0024, IllegalAccessException -> 0x001f, NoSuchMethodException -> 0x001a, InvocationTargetException -> 0x0015 }
        r3 = r0.getDeclaredConstructor(r3);	 Catch:{ InstantiationException -> 0x0024, IllegalAccessException -> 0x001f, NoSuchMethodException -> 0x001a, InvocationTargetException -> 0x0015 }
        r2 = new java.lang.Object[r2];	 Catch:{ InstantiationException -> 0x0024, IllegalAccessException -> 0x001f, NoSuchMethodException -> 0x001a, InvocationTargetException -> 0x0015 }
        r2 = r3.newInstance(r2);	 Catch:{ InstantiationException -> 0x0024, IllegalAccessException -> 0x001f, NoSuchMethodException -> 0x001a, InvocationTargetException -> 0x0015 }
        r1 = r2;
    L_0x0014:
        goto L_0x0029;
    L_0x0015:
        r2 = move-exception;
        throwInstantiateGlideModuleException(r0, r2);
        goto L_0x0029;
    L_0x001a:
        r2 = move-exception;
        throwInstantiateGlideModuleException(r0, r2);
        goto L_0x0014;
    L_0x001f:
        r2 = move-exception;
        throwInstantiateGlideModuleException(r0, r2);
        goto L_0x0014;
    L_0x0024:
        r2 = move-exception;
        throwInstantiateGlideModuleException(r0, r2);
        goto L_0x0014;
    L_0x0029:
        r2 = r1 instanceof com.bumptech.glide.module.GlideModule;
        if (r2 == 0) goto L_0x0031;
    L_0x002d:
        r2 = r1;
        r2 = (com.bumptech.glide.module.GlideModule) r2;
        return r2;
    L_0x0031:
        r2 = new java.lang.RuntimeException;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "Expected instanceof GlideModule, but found: ";
        r3.append(r4);
        r3.append(r1);
        r3 = r3.toString();
        r2.<init>(r3);
        throw r2;
    L_0x0048:
        r0 = move-exception;
        r1 = new java.lang.IllegalArgumentException;
        r2 = "Unable to find GlideModule implementation";
        r1.<init>(r2, r0);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.module.ManifestParser.parseModule(java.lang.String):com.bumptech.glide.module.GlideModule");
    }

    public java.util.List<com.bumptech.glide.module.GlideModule> parse() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:37:0x00cc in {2, 3, 10, 11, 12, 15, 16, 25, 26, 27, 28, 31, 32, 33, 36} preds:[]
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
        r8 = this;
        r0 = "ManifestParser";
        r1 = 3;
        r0 = android.util.Log.isLoggable(r0, r1);
        if (r0 == 0) goto L_0x0011;
    L_0x0009:
        r0 = "ManifestParser";
        r2 = "Loading Glide modules";
        android.util.Log.d(r0, r2);
        goto L_0x0012;
    L_0x0012:
        r0 = new java.util.ArrayList;
        r0.<init>();
        r2 = r8.context;	 Catch:{ NameNotFoundException -> 0x00c3 }
        r2 = r2.getPackageManager();	 Catch:{ NameNotFoundException -> 0x00c3 }
        r3 = r8.context;	 Catch:{ NameNotFoundException -> 0x00c3 }
        r3 = r3.getPackageName();	 Catch:{ NameNotFoundException -> 0x00c3 }
        r4 = 128; // 0x80 float:1.794E-43 double:6.32E-322;	 Catch:{ NameNotFoundException -> 0x00c3 }
        r2 = r2.getApplicationInfo(r3, r4);	 Catch:{ NameNotFoundException -> 0x00c3 }
        r3 = r2.metaData;	 Catch:{ NameNotFoundException -> 0x00c3 }
        if (r3 != 0) goto L_0x003f;	 Catch:{ NameNotFoundException -> 0x00c3 }
    L_0x002d:
        r3 = "ManifestParser";	 Catch:{ NameNotFoundException -> 0x00c3 }
        r1 = android.util.Log.isLoggable(r3, r1);	 Catch:{ NameNotFoundException -> 0x00c3 }
        if (r1 == 0) goto L_0x003d;	 Catch:{ NameNotFoundException -> 0x00c3 }
    L_0x0035:
        r1 = "ManifestParser";	 Catch:{ NameNotFoundException -> 0x00c3 }
        r3 = "Got null app info metadata";	 Catch:{ NameNotFoundException -> 0x00c3 }
        android.util.Log.d(r1, r3);	 Catch:{ NameNotFoundException -> 0x00c3 }
        goto L_0x003e;	 Catch:{ NameNotFoundException -> 0x00c3 }
    L_0x003e:
        return r0;	 Catch:{ NameNotFoundException -> 0x00c3 }
    L_0x003f:
        r3 = "ManifestParser";	 Catch:{ NameNotFoundException -> 0x00c3 }
        r4 = 2;	 Catch:{ NameNotFoundException -> 0x00c3 }
        r3 = android.util.Log.isLoggable(r3, r4);	 Catch:{ NameNotFoundException -> 0x00c3 }
        if (r3 == 0) goto L_0x0061;	 Catch:{ NameNotFoundException -> 0x00c3 }
    L_0x0048:
        r3 = "ManifestParser";	 Catch:{ NameNotFoundException -> 0x00c3 }
        r4 = new java.lang.StringBuilder;	 Catch:{ NameNotFoundException -> 0x00c3 }
        r4.<init>();	 Catch:{ NameNotFoundException -> 0x00c3 }
        r5 = "Got app info metadata: ";	 Catch:{ NameNotFoundException -> 0x00c3 }
        r4.append(r5);	 Catch:{ NameNotFoundException -> 0x00c3 }
        r5 = r2.metaData;	 Catch:{ NameNotFoundException -> 0x00c3 }
        r4.append(r5);	 Catch:{ NameNotFoundException -> 0x00c3 }
        r4 = r4.toString();	 Catch:{ NameNotFoundException -> 0x00c3 }
        android.util.Log.v(r3, r4);	 Catch:{ NameNotFoundException -> 0x00c3 }
        goto L_0x0062;	 Catch:{ NameNotFoundException -> 0x00c3 }
    L_0x0062:
        r3 = r2.metaData;	 Catch:{ NameNotFoundException -> 0x00c3 }
        r3 = r3.keySet();	 Catch:{ NameNotFoundException -> 0x00c3 }
        r3 = r3.iterator();	 Catch:{ NameNotFoundException -> 0x00c3 }
    L_0x006c:
        r4 = r3.hasNext();	 Catch:{ NameNotFoundException -> 0x00c3 }
        if (r4 == 0) goto L_0x00af;	 Catch:{ NameNotFoundException -> 0x00c3 }
    L_0x0072:
        r4 = r3.next();	 Catch:{ NameNotFoundException -> 0x00c3 }
        r4 = (java.lang.String) r4;	 Catch:{ NameNotFoundException -> 0x00c3 }
        r5 = "GlideModule";	 Catch:{ NameNotFoundException -> 0x00c3 }
        r6 = r2.metaData;	 Catch:{ NameNotFoundException -> 0x00c3 }
        r6 = r6.get(r4);	 Catch:{ NameNotFoundException -> 0x00c3 }
        r5 = r5.equals(r6);	 Catch:{ NameNotFoundException -> 0x00c3 }
        if (r5 == 0) goto L_0x00ad;	 Catch:{ NameNotFoundException -> 0x00c3 }
    L_0x0086:
        r5 = parseModule(r4);	 Catch:{ NameNotFoundException -> 0x00c3 }
        r0.add(r5);	 Catch:{ NameNotFoundException -> 0x00c3 }
        r5 = "ManifestParser";	 Catch:{ NameNotFoundException -> 0x00c3 }
        r5 = android.util.Log.isLoggable(r5, r1);	 Catch:{ NameNotFoundException -> 0x00c3 }
        if (r5 == 0) goto L_0x00ac;	 Catch:{ NameNotFoundException -> 0x00c3 }
    L_0x0095:
        r5 = "ManifestParser";	 Catch:{ NameNotFoundException -> 0x00c3 }
        r6 = new java.lang.StringBuilder;	 Catch:{ NameNotFoundException -> 0x00c3 }
        r6.<init>();	 Catch:{ NameNotFoundException -> 0x00c3 }
        r7 = "Loaded Glide module: ";	 Catch:{ NameNotFoundException -> 0x00c3 }
        r6.append(r7);	 Catch:{ NameNotFoundException -> 0x00c3 }
        r6.append(r4);	 Catch:{ NameNotFoundException -> 0x00c3 }
        r6 = r6.toString();	 Catch:{ NameNotFoundException -> 0x00c3 }
        android.util.Log.d(r5, r6);	 Catch:{ NameNotFoundException -> 0x00c3 }
        goto L_0x00ae;
    L_0x00ac:
        goto L_0x00ae;
    L_0x00ae:
        goto L_0x006c;
        r2 = "ManifestParser";
        r1 = android.util.Log.isLoggable(r2, r1);
        if (r1 == 0) goto L_0x00c1;
    L_0x00b9:
        r1 = "ManifestParser";
        r2 = "Finished loading Glide modules";
        android.util.Log.d(r1, r2);
        goto L_0x00c2;
    L_0x00c2:
        return r0;
    L_0x00c3:
        r1 = move-exception;
        r2 = new java.lang.RuntimeException;
        r3 = "Unable to find metadata to parse GlideModules";
        r2.<init>(r3, r1);
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.module.ManifestParser.parse():java.util.List<com.bumptech.glide.module.GlideModule>");
    }

    public ManifestParser(Context context) {
        this.context = context;
    }

    private static void throwInstantiateGlideModuleException(Class<?> clazz, Exception e) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unable to instantiate GlideModule implementation for ");
        stringBuilder.append(clazz);
        throw new RuntimeException(stringBuilder.toString(), e);
    }
}
