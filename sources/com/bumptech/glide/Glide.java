package com.bumptech.glide;

import android.app.Activity;
import android.content.ComponentCallbacks2;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.ResourceEncoder;
import com.bumptech.glide.load.data.InputStreamRewinder;
import com.bumptech.glide.load.engine.Engine;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.engine.cache.MemoryCache;
import com.bumptech.glide.load.engine.prefill.BitmapPreFiller;
import com.bumptech.glide.load.engine.prefill.PreFillType.Builder;
import com.bumptech.glide.load.model.AssetUriLoader;
import com.bumptech.glide.load.model.ByteArrayLoader;
import com.bumptech.glide.load.model.ByteArrayLoader.ByteBufferFactory;
import com.bumptech.glide.load.model.ByteBufferEncoder;
import com.bumptech.glide.load.model.ByteBufferFileLoader;
import com.bumptech.glide.load.model.DataUrlLoader;
import com.bumptech.glide.load.model.FileLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.MediaStoreFileLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.ResourceLoader.AssetFileDescriptorFactory;
import com.bumptech.glide.load.model.ResourceLoader.FileDescriptorFactory;
import com.bumptech.glide.load.model.ResourceLoader.StreamFactory;
import com.bumptech.glide.load.model.ResourceLoader.UriFactory;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.model.StringLoader;
import com.bumptech.glide.load.model.UnitModelLoader.Factory;
import com.bumptech.glide.load.model.UriLoader;
import com.bumptech.glide.load.model.UrlUriLoader;
import com.bumptech.glide.load.model.stream.HttpGlideUrlLoader;
import com.bumptech.glide.load.model.stream.HttpUriLoader;
import com.bumptech.glide.load.model.stream.MediaStoreImageThumbLoader;
import com.bumptech.glide.load.model.stream.MediaStoreVideoThumbLoader;
import com.bumptech.glide.load.model.stream.UrlLoader;
import com.bumptech.glide.load.resource.bitmap.BitmapDrawableDecoder;
import com.bumptech.glide.load.resource.bitmap.BitmapDrawableEncoder;
import com.bumptech.glide.load.resource.bitmap.BitmapEncoder;
import com.bumptech.glide.load.resource.bitmap.ByteBufferBitmapDecoder;
import com.bumptech.glide.load.resource.bitmap.DefaultImageHeaderParser;
import com.bumptech.glide.load.resource.bitmap.Downsampler;
import com.bumptech.glide.load.resource.bitmap.ExifInterfaceImageHeaderParser;
import com.bumptech.glide.load.resource.bitmap.ResourceBitmapDecoder;
import com.bumptech.glide.load.resource.bitmap.StreamBitmapDecoder;
import com.bumptech.glide.load.resource.bitmap.UnitBitmapDecoder;
import com.bumptech.glide.load.resource.bitmap.VideoDecoder;
import com.bumptech.glide.load.resource.bytes.ByteBufferRewinder;
import com.bumptech.glide.load.resource.drawable.ResourceDrawableDecoder;
import com.bumptech.glide.load.resource.drawable.UnitDrawableDecoder;
import com.bumptech.glide.load.resource.file.FileDecoder;
import com.bumptech.glide.load.resource.gif.ByteBufferGifDecoder;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawableEncoder;
import com.bumptech.glide.load.resource.gif.GifFrameResourceDecoder;
import com.bumptech.glide.load.resource.gif.StreamGifDecoder;
import com.bumptech.glide.load.resource.transcode.BitmapBytesTranscoder;
import com.bumptech.glide.load.resource.transcode.BitmapDrawableTranscoder;
import com.bumptech.glide.load.resource.transcode.DrawableBytesTranscoder;
import com.bumptech.glide.load.resource.transcode.GifDrawableBytesTranscoder;
import com.bumptech.glide.manager.ConnectivityMonitorFactory;
import com.bumptech.glide.manager.RequestManagerRetriever;
import com.bumptech.glide.module.GlideModule;
import com.bumptech.glide.module.ManifestParser;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTargetFactory;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Glide implements ComponentCallbacks2 {
    private static final String DEFAULT_DISK_CACHE_DIR = "image_manager_disk_cache";
    private static final String TAG = "Glide";
    private static volatile Glide glide;
    private static volatile boolean isInitializing;
    private final ArrayPool arrayPool;
    private final BitmapPool bitmapPool;
    private final BitmapPreFiller bitmapPreFiller;
    private final ConnectivityMonitorFactory connectivityMonitorFactory;
    private final Engine engine;
    private final GlideContext glideContext;
    private final List<RequestManager> managers = new ArrayList();
    private final MemoryCache memoryCache;
    private MemoryCategory memoryCategory = MemoryCategory.NORMAL;
    private final Registry registry;
    private final RequestManagerRetriever requestManagerRetriever;

    boolean removeFromManagers(@android.support.annotation.NonNull com.bumptech.glide.request.target.Target<?> r5) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:18:0x0026 in {10, 11, 14, 17} preds:[]
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
        r4 = this;
        r0 = r4.managers;
        monitor-enter(r0);
        r1 = r4.managers;	 Catch:{ all -> 0x0023 }
        r1 = r1.iterator();	 Catch:{ all -> 0x0023 }
    L_0x0009:
        r2 = r1.hasNext();	 Catch:{ all -> 0x0023 }
        if (r2 == 0) goto L_0x0020;	 Catch:{ all -> 0x0023 }
    L_0x000f:
        r2 = r1.next();	 Catch:{ all -> 0x0023 }
        r2 = (com.bumptech.glide.RequestManager) r2;	 Catch:{ all -> 0x0023 }
        r3 = r2.untrack(r5);	 Catch:{ all -> 0x0023 }
        if (r3 == 0) goto L_0x001e;	 Catch:{ all -> 0x0023 }
    L_0x001b:
        r1 = 1;	 Catch:{ all -> 0x0023 }
        monitor-exit(r0);	 Catch:{ all -> 0x0023 }
        return r1;	 Catch:{ all -> 0x0023 }
        goto L_0x0009;	 Catch:{ all -> 0x0023 }
    L_0x0020:
        monitor-exit(r0);	 Catch:{ all -> 0x0023 }
        r0 = 0;	 Catch:{ all -> 0x0023 }
        return r0;	 Catch:{ all -> 0x0023 }
    L_0x0023:
        r1 = move-exception;	 Catch:{ all -> 0x0023 }
        monitor-exit(r0);	 Catch:{ all -> 0x0023 }
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.Glide.removeFromManagers(com.bumptech.glide.request.target.Target):boolean");
    }

    @Nullable
    public static File getPhotoCacheDir(@NonNull Context context) {
        return getPhotoCacheDir(context, "image_manager_disk_cache");
    }

    @Nullable
    public static File getPhotoCacheDir(@NonNull Context context, @NonNull String cacheName) {
        File cacheDir = context.getCacheDir();
        if (cacheDir != null) {
            File result = new File(cacheDir, cacheName);
            if (result.mkdirs() || (result.exists() && result.isDirectory())) {
                return result;
            }
            return null;
        }
        if (Log.isLoggable(TAG, 6)) {
            Log.e(TAG, "default disk cache dir is null");
        }
        return null;
    }

    @NonNull
    public static Glide get(@NonNull Context context) {
        if (glide == null) {
            synchronized (Glide.class) {
                if (glide == null) {
                    checkAndInitializeGlide(context);
                }
            }
        }
        return glide;
    }

    private static void checkAndInitializeGlide(@NonNull Context context) {
        if (isInitializing) {
            throw new IllegalStateException("You cannot call Glide.get() in registerComponents(), use the provided Glide instance instead");
        }
        isInitializing = true;
        initializeGlide(context);
        isInitializing = false;
    }

    @VisibleForTesting
    @Deprecated
    public static synchronized void init(Glide glide) {
        synchronized (Glide.class) {
            if (glide != null) {
                tearDown();
            }
            glide = glide;
        }
    }

    @VisibleForTesting
    public static synchronized void init(@NonNull Context context, @NonNull GlideBuilder builder) {
        synchronized (Glide.class) {
            if (glide != null) {
                tearDown();
            }
            initializeGlide(context, builder);
        }
    }

    @VisibleForTesting
    public static synchronized void tearDown() {
        synchronized (Glide.class) {
            if (glide != null) {
                glide.getContext().getApplicationContext().unregisterComponentCallbacks(glide);
                glide.engine.shutdown();
            }
            glide = null;
        }
    }

    private static void initializeGlide(@NonNull Context context) {
        initializeGlide(context, new GlideBuilder());
    }

    private static void initializeGlide(@NonNull Context context, @NonNull GlideBuilder builder) {
        Set<Class<?>> excludedModuleClasses;
        Iterator<GlideModule> iterator;
        GlideModule current;
        Glide glide;
        Context applicationContext = context.getApplicationContext();
        GeneratedAppGlideModule annotationGeneratedModule = getAnnotationGeneratedGlideModules();
        List<GlideModule> manifestModules = Collections.emptyList();
        if (annotationGeneratedModule != null) {
            if (!annotationGeneratedModule.isManifestParsingEnabled()) {
                if (annotationGeneratedModule != null) {
                    if (!annotationGeneratedModule.getExcludedModuleClasses().isEmpty()) {
                        excludedModuleClasses = annotationGeneratedModule.getExcludedModuleClasses();
                        iterator = manifestModules.iterator();
                        while (iterator.hasNext()) {
                            current = (GlideModule) iterator.next();
                            if (!excludedModuleClasses.contains(current.getClass())) {
                                if (Log.isLoggable(TAG, 3)) {
                                    String str = TAG;
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append("AppGlideModule excludes manifest GlideModule: ");
                                    stringBuilder.append(current);
                                    Log.d(str, stringBuilder.toString());
                                }
                                iterator.remove();
                            }
                        }
                    }
                }
                if (Log.isLoggable(TAG, 3)) {
                    for (GlideModule glideModule : manifestModules) {
                        String str2 = TAG;
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("Discovered GlideModule from manifest: ");
                        stringBuilder2.append(glideModule.getClass());
                        Log.d(str2, stringBuilder2.toString());
                    }
                }
                builder.setRequestManagerFactory(annotationGeneratedModule == null ? annotationGeneratedModule.getRequestManagerFactory() : null);
                for (GlideModule module : manifestModules) {
                    module.applyOptions(applicationContext, builder);
                }
                if (annotationGeneratedModule != null) {
                    annotationGeneratedModule.applyOptions(applicationContext, builder);
                }
                glide = builder.build(applicationContext);
                for (GlideModule current2 : manifestModules) {
                    current2.registerComponents(applicationContext, glide, glide.registry);
                }
                if (annotationGeneratedModule != null) {
                    annotationGeneratedModule.registerComponents(applicationContext, glide, glide.registry);
                }
                applicationContext.registerComponentCallbacks(glide);
                glide = glide;
            }
        }
        manifestModules = new ManifestParser(applicationContext).parse();
        if (annotationGeneratedModule != null) {
            if (!annotationGeneratedModule.getExcludedModuleClasses().isEmpty()) {
                excludedModuleClasses = annotationGeneratedModule.getExcludedModuleClasses();
                iterator = manifestModules.iterator();
                while (iterator.hasNext()) {
                    current2 = (GlideModule) iterator.next();
                    if (!excludedModuleClasses.contains(current2.getClass())) {
                        if (Log.isLoggable(TAG, 3)) {
                            String str3 = TAG;
                            StringBuilder stringBuilder3 = new StringBuilder();
                            stringBuilder3.append("AppGlideModule excludes manifest GlideModule: ");
                            stringBuilder3.append(current2);
                            Log.d(str3, stringBuilder3.toString());
                        }
                        iterator.remove();
                    }
                }
            }
        }
        if (Log.isLoggable(TAG, 3)) {
            for (GlideModule glideModule2 : manifestModules) {
                String str22 = TAG;
                StringBuilder stringBuilder22 = new StringBuilder();
                stringBuilder22.append("Discovered GlideModule from manifest: ");
                stringBuilder22.append(glideModule2.getClass());
                Log.d(str22, stringBuilder22.toString());
            }
        }
        if (annotationGeneratedModule == null) {
        }
        builder.setRequestManagerFactory(annotationGeneratedModule == null ? annotationGeneratedModule.getRequestManagerFactory() : null);
        while (r4.hasNext()) {
            module.applyOptions(applicationContext, builder);
        }
        if (annotationGeneratedModule != null) {
            annotationGeneratedModule.applyOptions(applicationContext, builder);
        }
        glide = builder.build(applicationContext);
        while (r5.hasNext()) {
            current2.registerComponents(applicationContext, glide, glide.registry);
        }
        if (annotationGeneratedModule != null) {
            annotationGeneratedModule.registerComponents(applicationContext, glide, glide.registry);
        }
        applicationContext.registerComponentCallbacks(glide);
        glide = glide;
    }

    @Nullable
    private static GeneratedAppGlideModule getAnnotationGeneratedGlideModules() {
        GeneratedAppGlideModule result = null;
        try {
            result = (GeneratedAppGlideModule) Class.forName("com.bumptech.glide.GeneratedAppGlideModuleImpl").getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (ClassNotFoundException e) {
            if (Log.isLoggable(TAG, 5)) {
                Log.w(TAG, "Failed to find GeneratedAppGlideModule. You should include an annotationProcessor compile dependency on com.github.bumptech.glide:compiler in your application and a @GlideModule annotated AppGlideModule implementation or LibraryGlideModules will be silently ignored");
            }
        } catch (InstantiationException e2) {
            throwIncorrectGlideModule(e2);
        } catch (IllegalAccessException e3) {
            throwIncorrectGlideModule(e3);
        } catch (NoSuchMethodException e4) {
            throwIncorrectGlideModule(e4);
        } catch (InvocationTargetException e5) {
            throwIncorrectGlideModule(e5);
        }
        return result;
    }

    private static void throwIncorrectGlideModule(Exception e) {
        throw new IllegalStateException("GeneratedAppGlideModuleImpl is implemented incorrectly. If you've manually implemented this class, remove your implementation. The Annotation processor will generate a correct implementation.", e);
    }

    Glide(@NonNull Context context, @NonNull Engine engine, @NonNull MemoryCache memoryCache, @NonNull BitmapPool bitmapPool, @NonNull ArrayPool arrayPool, @NonNull RequestManagerRetriever requestManagerRetriever, @NonNull ConnectivityMonitorFactory connectivityMonitorFactory, int logLevel, @NonNull RequestOptions defaultRequestOptions, @NonNull Map<Class<?>, TransitionOptions<?, ?>> defaultTransitionOptions) {
        Context context2 = context;
        MemoryCache memoryCache2 = memoryCache;
        BitmapPool bitmapPool2 = bitmapPool;
        ArrayPool arrayPool2 = arrayPool;
        this.engine = engine;
        this.bitmapPool = bitmapPool2;
        this.arrayPool = arrayPool2;
        this.memoryCache = memoryCache2;
        this.requestManagerRetriever = requestManagerRetriever;
        this.connectivityMonitorFactory = connectivityMonitorFactory;
        DecodeFormat decodeFormat = (DecodeFormat) defaultRequestOptions.getOptions().get(Downsampler.DECODE_FORMAT);
        this.bitmapPreFiller = new BitmapPreFiller(memoryCache2, bitmapPool2, decodeFormat);
        Resources resources = context.getResources();
        this.registry = new Registry();
        if (VERSION.SDK_INT >= 27) {
            r0.registry.register(new ExifInterfaceImageHeaderParser());
        }
        r0.registry.register(new DefaultImageHeaderParser());
        Downsampler downsampler = new Downsampler(r0.registry.getImageHeaderParsers(), resources.getDisplayMetrics(), bitmapPool2, arrayPool2);
        ByteBufferGifDecoder byteBufferGifDecoder = new ByteBufferGifDecoder(context2, r0.registry.getImageHeaderParsers(), bitmapPool2, arrayPool2);
        ResourceDecoder<ParcelFileDescriptor, Bitmap> parcelFileDescriptorVideoDecoder = VideoDecoder.parcel(bitmapPool);
        ResourceDecoder byteBufferBitmapDecoder = new ByteBufferBitmapDecoder(downsampler);
        ResourceDecoder streamBitmapDecoder = new StreamBitmapDecoder(downsampler, arrayPool2);
        ResourceDecoder resourceDrawableDecoder = new ResourceDrawableDecoder(context2);
        ModelLoaderFactory resourceLoaderStreamFactory = new StreamFactory(resources);
        DecodeFormat decodeFormat2 = decodeFormat;
        UriFactory resourceLoaderUriFactory = new UriFactory(resources);
        FileDescriptorFactory resourceLoaderFileDescriptorFactory = new FileDescriptorFactory(resources);
        AssetFileDescriptorFactory resourceLoaderAssetFileDescriptorFactory = new AssetFileDescriptorFactory(resources);
        ResourceEncoder bitmapEncoder = new BitmapEncoder(arrayPool2);
        BitmapBytesTranscoder bitmapBytesTranscoder = new BitmapBytesTranscoder();
        GifDrawableBytesTranscoder gifDrawableBytesTranscoder = new GifDrawableBytesTranscoder();
        AssetFileDescriptorFactory resourceLoaderAssetFileDescriptorFactory2 = resourceLoaderAssetFileDescriptorFactory;
        ModelLoaderFactory resourceLoaderFileDescriptorFactory2 = resourceLoaderFileDescriptorFactory;
        ModelLoaderFactory resourceLoaderUriFactory2 = resourceLoaderUriFactory;
        ModelLoaderFactory resourceLoaderAssetFileDescriptorFactory3 = resourceLoaderAssetFileDescriptorFactory2;
        Context context3 = context;
        ContentResolver contentResolver = context.getContentResolver();
        BitmapBytesTranscoder bitmapBytesTranscoder2 = bitmapBytesTranscoder;
        GifDrawableBytesTranscoder gifDrawableBytesTranscoder2 = gifDrawableBytesTranscoder;
        r0.registry.append(ByteBuffer.class, new ByteBufferEncoder()).append(InputStream.class, new StreamEncoder(arrayPool2)).append(Registry.BUCKET_BITMAP, ByteBuffer.class, Bitmap.class, byteBufferBitmapDecoder).append(Registry.BUCKET_BITMAP, InputStream.class, Bitmap.class, streamBitmapDecoder).append(Registry.BUCKET_BITMAP, ParcelFileDescriptor.class, Bitmap.class, parcelFileDescriptorVideoDecoder).append(Registry.BUCKET_BITMAP, AssetFileDescriptor.class, Bitmap.class, VideoDecoder.asset(bitmapPool)).append(Bitmap.class, Bitmap.class, Factory.getInstance()).append(Registry.BUCKET_BITMAP, Bitmap.class, Bitmap.class, new UnitBitmapDecoder()).append(Bitmap.class, bitmapEncoder).append(Registry.BUCKET_BITMAP_DRAWABLE, ByteBuffer.class, BitmapDrawable.class, new BitmapDrawableDecoder(resources, byteBufferBitmapDecoder)).append(Registry.BUCKET_BITMAP_DRAWABLE, InputStream.class, BitmapDrawable.class, new BitmapDrawableDecoder(resources, streamBitmapDecoder)).append(Registry.BUCKET_BITMAP_DRAWABLE, ParcelFileDescriptor.class, BitmapDrawable.class, new BitmapDrawableDecoder(resources, (ResourceDecoder) parcelFileDescriptorVideoDecoder)).append(BitmapDrawable.class, new BitmapDrawableEncoder(bitmapPool2, bitmapEncoder)).append(Registry.BUCKET_GIF, InputStream.class, GifDrawable.class, new StreamGifDecoder(r0.registry.getImageHeaderParsers(), byteBufferGifDecoder, arrayPool2)).append(Registry.BUCKET_GIF, ByteBuffer.class, GifDrawable.class, byteBufferGifDecoder).append(GifDrawable.class, new GifDrawableEncoder()).append(GifDecoder.class, GifDecoder.class, Factory.getInstance()).append(Registry.BUCKET_BITMAP, GifDecoder.class, Bitmap.class, new GifFrameResourceDecoder(bitmapPool2)).append(Uri.class, Drawable.class, resourceDrawableDecoder).append(Uri.class, Bitmap.class, new ResourceBitmapDecoder(resourceDrawableDecoder, bitmapPool2)).register(new ByteBufferRewinder.Factory()).append(File.class, ByteBuffer.class, new ByteBufferFileLoader.Factory()).append(File.class, InputStream.class, new FileLoader.StreamFactory()).append(File.class, File.class, new FileDecoder()).append(File.class, ParcelFileDescriptor.class, new FileLoader.FileDescriptorFactory()).append(File.class, File.class, Factory.getInstance()).register(new InputStreamRewinder.Factory(arrayPool2)).append(Integer.TYPE, InputStream.class, resourceLoaderStreamFactory).append(Integer.TYPE, ParcelFileDescriptor.class, resourceLoaderFileDescriptorFactory2).append(Integer.class, InputStream.class, resourceLoaderStreamFactory).append(Integer.class, ParcelFileDescriptor.class, resourceLoaderFileDescriptorFactory2).append(Integer.class, Uri.class, resourceLoaderUriFactory2).append(Integer.TYPE, AssetFileDescriptor.class, resourceLoaderAssetFileDescriptorFactory3).append(Integer.class, AssetFileDescriptor.class, resourceLoaderAssetFileDescriptorFactory3).append(Integer.TYPE, Uri.class, resourceLoaderUriFactory2).append(String.class, InputStream.class, new DataUrlLoader.StreamFactory()).append(Uri.class, InputStream.class, new DataUrlLoader.StreamFactory()).append(String.class, InputStream.class, new StringLoader.StreamFactory()).append(String.class, ParcelFileDescriptor.class, new StringLoader.FileDescriptorFactory()).append(String.class, AssetFileDescriptor.class, new StringLoader.AssetFileDescriptorFactory()).append(Uri.class, InputStream.class, new HttpUriLoader.Factory()).append(Uri.class, InputStream.class, new AssetUriLoader.StreamFactory(context.getAssets())).append(Uri.class, ParcelFileDescriptor.class, new AssetUriLoader.FileDescriptorFactory(context.getAssets())).append(Uri.class, InputStream.class, new MediaStoreImageThumbLoader.Factory(context3)).append(Uri.class, InputStream.class, new MediaStoreVideoThumbLoader.Factory(context3)).append(Uri.class, InputStream.class, new UriLoader.StreamFactory(contentResolver)).append(Uri.class, ParcelFileDescriptor.class, new UriLoader.FileDescriptorFactory(contentResolver)).append(Uri.class, AssetFileDescriptor.class, new UriLoader.AssetFileDescriptorFactory(contentResolver)).append(Uri.class, InputStream.class, new UrlUriLoader.StreamFactory()).append(URL.class, InputStream.class, new UrlLoader.StreamFactory()).append(Uri.class, File.class, new MediaStoreFileLoader.Factory(context3)).append(GlideUrl.class, InputStream.class, new HttpGlideUrlLoader.Factory()).append(byte[].class, ByteBuffer.class, new ByteBufferFactory()).append(byte[].class, InputStream.class, new ByteArrayLoader.StreamFactory()).append(Uri.class, Uri.class, Factory.getInstance()).append(Drawable.class, Drawable.class, Factory.getInstance()).append(Drawable.class, Drawable.class, new UnitDrawableDecoder()).register(Bitmap.class, BitmapDrawable.class, new BitmapDrawableTranscoder(resources)).register(Bitmap.class, byte[].class, bitmapBytesTranscoder2).register(Drawable.class, byte[].class, new DrawableBytesTranscoder(bitmapPool2, bitmapBytesTranscoder2, gifDrawableBytesTranscoder2)).register(GifDrawable.class, byte[].class, gifDrawableBytesTranscoder2);
        ImageViewTargetFactory imageViewTargetFactory = new ImageViewTargetFactory();
        GlideContext glideContext = r1;
        Registry registry = r0.registry;
        DecodeFormat decodeFormat3 = decodeFormat2;
        DecodeFormat decodeFormat4 = decodeFormat3;
        GlideContext glideContext2 = new GlideContext(context, arrayPool, registry, imageViewTargetFactory, defaultRequestOptions, defaultTransitionOptions, engine, logLevel);
        r0.glideContext = glideContext;
    }

    @NonNull
    public BitmapPool getBitmapPool() {
        return this.bitmapPool;
    }

    @NonNull
    public ArrayPool getArrayPool() {
        return this.arrayPool;
    }

    @NonNull
    public Context getContext() {
        return this.glideContext.getBaseContext();
    }

    ConnectivityMonitorFactory getConnectivityMonitorFactory() {
        return this.connectivityMonitorFactory;
    }

    @NonNull
    GlideContext getGlideContext() {
        return this.glideContext;
    }

    public void preFillBitmapPool(@NonNull Builder... bitmapAttributeBuilders) {
        this.bitmapPreFiller.preFill(bitmapAttributeBuilders);
    }

    public void clearMemory() {
        Util.assertMainThread();
        this.memoryCache.clearMemory();
        this.bitmapPool.clearMemory();
        this.arrayPool.clearMemory();
    }

    public void trimMemory(int level) {
        Util.assertMainThread();
        this.memoryCache.trimMemory(level);
        this.bitmapPool.trimMemory(level);
        this.arrayPool.trimMemory(level);
    }

    public void clearDiskCache() {
        Util.assertBackgroundThread();
        this.engine.clearDiskCache();
    }

    @NonNull
    public RequestManagerRetriever getRequestManagerRetriever() {
        return this.requestManagerRetriever;
    }

    @NonNull
    public MemoryCategory setMemoryCategory(@NonNull MemoryCategory memoryCategory) {
        Util.assertMainThread();
        this.memoryCache.setSizeMultiplier(memoryCategory.getMultiplier());
        this.bitmapPool.setSizeMultiplier(memoryCategory.getMultiplier());
        MemoryCategory oldCategory = this.memoryCategory;
        this.memoryCategory = memoryCategory;
        return oldCategory;
    }

    @NonNull
    private static RequestManagerRetriever getRetriever(@Nullable Context context) {
        Preconditions.checkNotNull(context, "You cannot start a load on a not yet attached View or a Fragment where getActivity() returns null (which usually occurs when getActivity() is called before the Fragment is attached or after the Fragment is destroyed).");
        return get(context).getRequestManagerRetriever();
    }

    @NonNull
    public static RequestManager with(@NonNull Context context) {
        return getRetriever(context).get(context);
    }

    @NonNull
    public static RequestManager with(@NonNull Activity activity) {
        return getRetriever(activity).get(activity);
    }

    @NonNull
    public static RequestManager with(@NonNull FragmentActivity activity) {
        return getRetriever(activity).get(activity);
    }

    @NonNull
    public static RequestManager with(@NonNull Fragment fragment) {
        return getRetriever(fragment.getActivity()).get(fragment);
    }

    @Deprecated
    @NonNull
    public static RequestManager with(@NonNull android.app.Fragment fragment) {
        return getRetriever(fragment.getActivity()).get(fragment);
    }

    @NonNull
    public static RequestManager with(@NonNull View view) {
        return getRetriever(view.getContext()).get(view);
    }

    @NonNull
    public Registry getRegistry() {
        return this.registry;
    }

    void registerRequestManager(RequestManager requestManager) {
        synchronized (this.managers) {
            if (this.managers.contains(requestManager)) {
                throw new IllegalStateException("Cannot register already registered manager");
            }
            this.managers.add(requestManager);
        }
    }

    void unregisterRequestManager(RequestManager requestManager) {
        synchronized (this.managers) {
            if (this.managers.contains(requestManager)) {
                this.managers.remove(requestManager);
            } else {
                throw new IllegalStateException("Cannot unregister not yet registered manager");
            }
        }
    }

    public void onTrimMemory(int level) {
        trimMemory(level);
    }

    public void onConfigurationChanged(Configuration newConfig) {
    }

    public void onLowMemory() {
        clearMemory();
    }
}
