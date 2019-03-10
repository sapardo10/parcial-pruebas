package com.bumptech.glide.load.model.stream;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;
import com.bumptech.glide.load.model.ModelCache;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class BaseGlideUrlLoader<Model> implements ModelLoader<Model, InputStream> {
    private final ModelLoader<GlideUrl, InputStream> concreteLoader;
    @Nullable
    private final ModelCache<Model, GlideUrl> modelCache;

    protected abstract String getUrl(Model model, int i, int i2, Options options);

    protected BaseGlideUrlLoader(ModelLoader<GlideUrl, InputStream> concreteLoader) {
        this(concreteLoader, null);
    }

    protected BaseGlideUrlLoader(ModelLoader<GlideUrl, InputStream> concreteLoader, @Nullable ModelCache<Model, GlideUrl> modelCache) {
        this.concreteLoader = concreteLoader;
        this.modelCache = modelCache;
    }

    @Nullable
    public LoadData<InputStream> buildLoadData(@NonNull Model model, int width, int height, @NonNull Options options) {
        GlideUrl result = null;
        ModelCache modelCache = this.modelCache;
        if (modelCache != null) {
            result = (GlideUrl) modelCache.get(model, width, height);
        }
        if (result == null) {
            String stringURL = getUrl(model, width, height, options);
            if (TextUtils.isEmpty(stringURL)) {
                return null;
            }
            result = new GlideUrl(stringURL, getHeaders(model, width, height, options));
            ModelCache modelCache2 = this.modelCache;
            if (modelCache2 != null) {
                modelCache2.put(model, width, height, result);
            }
        }
        List<String> alternateUrls = getAlternateUrls(model, width, height, options);
        LoadData<InputStream> concreteLoaderData = this.concreteLoader.buildLoadData(result, width, height, options);
        if (concreteLoaderData != null) {
            if (!alternateUrls.isEmpty()) {
                return new LoadData(concreteLoaderData.sourceKey, getAlternateKeys(alternateUrls), concreteLoaderData.fetcher);
            }
        }
        return concreteLoaderData;
    }

    private static List<Key> getAlternateKeys(Collection<String> alternateUrls) {
        List<Key> result = new ArrayList(alternateUrls.size());
        for (String alternate : alternateUrls) {
            result.add(new GlideUrl(alternate));
        }
        return result;
    }

    protected List<String> getAlternateUrls(Model model, int width, int height, Options options) {
        return Collections.emptyList();
    }

    @Nullable
    protected Headers getHeaders(Model model, int width, int height, Options options) {
        return Headers.DEFAULT;
    }
}
