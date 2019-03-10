package de.danoeh.antennapod.core.glide;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import com.bumptech.glide.integration.okhttp3.OkHttpStreamFetcher;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.signature.ObjectKey;
import de.danoeh.antennapod.core.service.download.AntennapodHttpClient;
import de.danoeh.antennapod.core.service.download.HttpDownloader;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.util.NetworkUtils;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.Interceptor;
import okhttp3.Interceptor$Chain;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.CharEncoding;

class ApOkHttpUrlLoader implements ModelLoader<String, InputStream> {
    private static final String TAG = ApOkHttpUrlLoader.class.getSimpleName();
    private final OkHttpClient client;

    private static class BasicAuthenticationInterceptor implements Interceptor {
        private BasicAuthenticationInterceptor() {
        }

        public Response intercept(Interceptor$Chain chain) throws IOException {
            Request request = chain.request();
            String url = request.url().toString();
            String authentication = DBReader.getImageAuthentication(url);
            if (TextUtils.isEmpty(authentication)) {
                String access$300 = ApOkHttpUrlLoader.TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("no credentials for '");
                stringBuilder.append(url);
                stringBuilder.append("'");
                Log.d(access$300, stringBuilder.toString());
                return chain.proceed(request);
            }
            String[] auth = authentication.split(":");
            Request newRequest = request.newBuilder().addHeader("Authorization", HttpDownloader.encodeCredentials(auth[0], auth[1], CharEncoding.ISO_8859_1)).build();
            Log.d(ApOkHttpUrlLoader.TAG, "Basic authentication with ISO-8859-1 encoding");
            Response response = chain.proceed(newRequest);
            if (response.isSuccessful() || response.code() != 401) {
                return response;
            }
            String credentials = request.newBuilder().addHeader("Authorization", HttpDownloader.encodeCredentials(auth[0], auth[1], "UTF-8")).build();
            Log.d(ApOkHttpUrlLoader.TAG, "Basic authentication with UTF-8 encoding");
            return chain.proceed(credentials);
        }
    }

    public static class Factory implements ModelLoaderFactory<String, InputStream> {
        private static volatile OkHttpClient internalClient;
        private final OkHttpClient client;

        private static OkHttpClient getInternalClient() {
            if (internalClient == null) {
                synchronized (Factory.class) {
                    if (internalClient == null) {
                        Builder builder = AntennapodHttpClient.newBuilder();
                        builder.interceptors().add(new NetworkAllowanceInterceptor());
                        builder.interceptors().add(new BasicAuthenticationInterceptor());
                        internalClient = builder.build();
                    }
                }
            }
            return internalClient;
        }

        Factory() {
            this(getInternalClient());
        }

        Factory(OkHttpClient client) {
            this.client = client;
        }

        @NonNull
        public ModelLoader<String, InputStream> build(@NonNull MultiModelLoaderFactory multiFactory) {
            return new ApOkHttpUrlLoader(this.client);
        }

        public void teardown() {
        }
    }

    private static class NetworkAllowanceInterceptor implements Interceptor {
        private NetworkAllowanceInterceptor() {
        }

        public Response intercept(Interceptor$Chain chain) throws IOException {
            if (NetworkUtils.isDownloadAllowed()) {
                return chain.proceed(chain.request());
            }
            return null;
        }
    }

    private ApOkHttpUrlLoader(OkHttpClient client) {
        this.client = client;
    }

    @Nullable
    public LoadData<InputStream> buildLoadData(@NonNull String model, int width, int height, @NonNull Options options) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("buildLoadData() called with: model = [");
        stringBuilder.append(model);
        stringBuilder.append("], width = [");
        stringBuilder.append(width);
        stringBuilder.append("], height = [");
        stringBuilder.append(height);
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
        if (TextUtils.isEmpty(model)) {
            return null;
        }
        if (model.startsWith("/")) {
            return new LoadData(new ObjectKey(model), new AudioCoverFetcher(model));
        }
        return new LoadData(new ObjectKey(model), new OkHttpStreamFetcher(this.client, new GlideUrl(model)));
    }

    public boolean handles(@NonNull String s) {
        return true;
    }
}
