package de.danoeh.antennapod.core.glide;

import android.media.MediaMetadataRetriever;
import android.support.annotation.NonNull;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.DataFetcher.DataCallback;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

class AudioCoverFetcher implements DataFetcher<InputStream> {
    private static final String TAG = "AudioCoverFetcher";
    private final String path;

    public AudioCoverFetcher(String path) {
        this.path = path;
    }

    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super InputStream> callback) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(this.path);
            byte[] picture = retriever.getEmbeddedPicture();
            if (picture != null) {
                callback.onDataReady(new ByteArrayInputStream(picture));
                return;
            }
            retriever.release();
            callback.onLoadFailed(new IOException("Loading embedded cover did not work"));
        } finally {
            retriever.release();
        }
    }

    public void cleanup() {
    }

    public void cancel() {
    }

    @NonNull
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }

    @NonNull
    public DataSource getDataSource() {
        return DataSource.LOCAL;
    }
}
