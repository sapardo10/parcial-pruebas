package de.danoeh.antennapod.core.glide;

import android.content.Context;
import android.support.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import de.danoeh.antennapod.core.glide.ApOkHttpUrlLoader.Factory;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import java.io.InputStream;

public class ApGlideModule extends AppGlideModule {
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        builder.setDefaultRequestOptions(new RequestOptions().format(DecodeFormat.PREFER_ARGB_8888));
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, (long) UserPreferences.getImageCacheSize()));
    }

    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        registry.append(String.class, InputStream.class, new Factory());
    }
}
