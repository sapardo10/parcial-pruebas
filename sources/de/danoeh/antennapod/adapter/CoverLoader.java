package de.danoeh.antennapod.adapter;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.core.glide.ApGlideSettings;
import java.lang.ref.WeakReference;

public class CoverLoader {
    private MainActivity activity;
    private int errorResource = -1;
    private String fallbackUri;
    private ImageView imgvCover;
    private TextView txtvPlaceholder;
    private String uri;

    class CoverTarget extends CustomViewTarget<ImageView, Drawable> {
        private final WeakReference<ImageView> cover;
        private final WeakReference<TextView> placeholder;

        public CoverTarget(TextView txtvPlaceholder, ImageView imgvCover) {
            super(imgvCover);
            this.placeholder = new WeakReference(txtvPlaceholder);
            this.cover = new WeakReference(imgvCover);
        }

        public void onLoadFailed(Drawable errorDrawable) {
        }

        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
            TextView txtvPlaceholder = (TextView) this.placeholder.get();
            if (txtvPlaceholder != null) {
                txtvPlaceholder.setVisibility(4);
            }
            ((ImageView) this.cover.get()).setImageDrawable(resource);
        }

        protected void onResourceCleared(@Nullable Drawable placeholder) {
            ((ImageView) this.cover.get()).setImageDrawable(placeholder);
        }
    }

    public CoverLoader(MainActivity activity) {
        this.activity = activity;
    }

    public CoverLoader withUri(String uri) {
        this.uri = uri;
        return this;
    }

    public CoverLoader withFallbackUri(String uri) {
        this.fallbackUri = uri;
        return this;
    }

    public CoverLoader withCoverView(ImageView coverView) {
        this.imgvCover = coverView;
        return this;
    }

    public CoverLoader withError(int errorResource) {
        this.errorResource = errorResource;
        return this;
    }

    public CoverLoader withPlaceholderView(TextView placeholderView) {
        this.txtvPlaceholder = placeholderView;
        return this;
    }

    public void load() {
        RequestOptions options = new RequestOptions().diskCacheStrategy(ApGlideSettings.AP_DISK_CACHE_STRATEGY).fitCenter().dontAnimate();
        int i = this.errorResource;
        if (i != -1) {
            options = options.error(i);
        }
        RequestBuilder builder = Glide.with(this.activity).load(this.uri).apply(options);
        if (this.fallbackUri != null && this.txtvPlaceholder != null && this.imgvCover != null) {
            builder = builder.error(Glide.with(this.activity).load(this.fallbackUri).apply(options));
        }
        builder.into(new CoverTarget(this.txtvPlaceholder, this.imgvCover));
    }
}
