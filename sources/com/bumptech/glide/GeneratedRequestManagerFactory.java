package com.bumptech.glide;

import android.content.Context;
import android.support.annotation.NonNull;
import com.bumptech.glide.manager.Lifecycle;
import com.bumptech.glide.manager.RequestManagerRetriever.RequestManagerFactory;
import com.bumptech.glide.manager.RequestManagerTreeNode;
import de.danoeh.antennapod.core.glide.GlideRequests;

final class GeneratedRequestManagerFactory implements RequestManagerFactory {
    GeneratedRequestManagerFactory() {
    }

    @NonNull
    public RequestManager build(@NonNull Glide glide, @NonNull Lifecycle lifecycle, @NonNull RequestManagerTreeNode treeNode, @NonNull Context context) {
        return new GlideRequests(glide, lifecycle, treeNode, context);
    }
}
